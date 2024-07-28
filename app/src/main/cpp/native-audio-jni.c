/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/* This is a JNI example where we use native methods to play sounds
 * using OpenSL ES. See the corresponding Java source file located at:
 *
 *   src/com/example/nativeaudio/NativeAudio/NativeAudio.java
 */

#include <stdlib.h>
#include <assert.h>
#include <jni.h>
#include <string.h>
#include <pthread.h>


// for __android_log_print(ANDROID_LOG_INFO, "YourApp", "formatted message");
// #include <android/log.h>

// for native audio
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

// for native asset manager
#include <sys/types.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>


// engine interfaces
static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;

// output mix interfaces
static SLObjectItf outputMixObject = NULL;
static SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;

// buffer queue player interfaces
static SLObjectItf bqPlayerObject = NULL;
static SLPlayItf bqPlayerPlay;
static SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;
static SLEffectSendItf bqPlayerEffectSend;
static SLMuteSoloItf bqPlayerMuteSolo;
static SLVolumeItf bqPlayerVolume;
static SLmilliHertz bqPlayerSampleRate = 0;

// aux effect on the output mix, used by the buffer queue player
static const SLEnvironmentalReverbSettings reverbSettings =
        SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;


// pointer and size of the next player buffer to enqueue, and number of remaining buffers
static short *attackBuffer;
static unsigned attackSize;
static short *sustainBuffer;
static unsigned sustainSize;
static short *releaseBuffer;
static unsigned releaseSize;
static short *silenceBuffer;
static unsigned silenceSize;

static const int IS_NOT_PLAYING_AT_ALL = -1;
static const int IS_PLAYING_SILENCE = 0;
static const int IS_PLAYING_ATTACK = 1;
static const int IS_PLAYING_SUSTAIN = 2;
static const int IS_PLAYING_RELEASE = 3;


static int isPlaying = IS_PLAYING_SILENCE;

// (called automatically on load)
__attribute__((constructor)) static void onDlOpen(void) {
}

void releaseResampleBuf(void) {
    if (0 == bqPlayerSampleRate) {
        /*
         * we are not using fast path, so we were not creating buffers, nothing to do
         */
        return;
    }

    free(attackBuffer);
    attackBuffer = NULL;

    free(sustainBuffer);
    sustainBuffer = NULL;

    free(releaseBuffer);
    releaseBuffer = NULL;

    free(silenceBuffer);
    silenceBuffer = NULL;
}


short *
createResampledBufFromSample(short *src, int32_t srcSampleCount, uint32_t srcRate, unsigned *size) {

    short *workBuf;
    int upSampleRate;

    if (0 == bqPlayerSampleRate) {
        return NULL;
    }
    if (bqPlayerSampleRate % srcRate) {
        /*
         * simple up-sampling, must be divisible
         */
        return NULL;
    }
    upSampleRate = bqPlayerSampleRate / srcRate;


    short *resampleBuf = (short *) malloc((srcSampleCount * upSampleRate) << 1);
    if (resampleBuf == NULL) {
        return resampleBuf;
    }
    workBuf = resampleBuf;
    for (int sample = 0; sample < srcSampleCount; sample++) {
        for (int dup = 0; dup < upSampleRate; dup++) {
            *workBuf++ = src[sample];
        }
    }

    *size = (srcSampleCount * upSampleRate) << 1;     // sample format is 16 bit
    return resampleBuf;
}


short *
createSamples(JNIEnv *env, jshortArray samples, jint count, jint sampleRate, unsigned *outSize) {
    short *buffer = malloc(count * sizeof(short));
    (*env)->GetShortArrayRegion(env, samples, 0, count, buffer);
    short *outBuffer = createResampledBufFromSample(buffer, count, sampleRate * 1000, outSize);
    free(buffer);
    return outBuffer;
}


JNIEXPORT void JNICALL
Java_com_paddlesandbugs_dahdidahdit_brasspound_AudioHelper_setSamples(JNIEnv *env, jclass clazz,
                                                           jshortArray attackSamples, jint attackCount,
                                                           jshortArray sustainSamples, jint sustainCount,
                                                           jshortArray releaseSamples, jint releaseCount,
                                                           jshortArray silenceSamples, jint silenceCount,
                                                           jint sampleRate) {

    attackBuffer = createSamples(env, attackSamples, attackCount, sampleRate, &attackSize);
    sustainBuffer = createSamples(env, sustainSamples, sustainCount, sampleRate, &sustainSize);
    releaseBuffer = createSamples(env, releaseSamples, releaseCount, sampleRate, &releaseSize);

    silenceBuffer = createSamples(env, silenceSamples, silenceCount, sampleRate, &silenceSize);
}


JNIEXPORT void JNICALL
Java_com_paddlesandbugs_dahdidahdit_brasspound_AudioHelper_startLoop(JNIEnv *env, jclass clazz) {
    isPlaying = IS_PLAYING_SILENCE;
    SLresult result;
    result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, silenceBuffer, silenceSize);
    if (SL_RESULT_SUCCESS != result) {
        isPlaying = IS_NOT_PLAYING_AT_ALL;
    }
}


JNIEXPORT jboolean JNICALL
Java_com_paddlesandbugs_dahdidahdit_brasspound_AudioHelper_playSamples(JNIEnv *env, jclass clazz) {
    isPlaying = IS_PLAYING_ATTACK;
    return JNI_TRUE;
}


JNIEXPORT void JNICALL
Java_com_paddlesandbugs_dahdidahdit_brasspound_AudioHelper_stopPlaying(JNIEnv *env, jclass clazz) {
    isPlaying = IS_PLAYING_RELEASE;
}


// this callback handler is called every time a buffer finishes playing
void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    assert(bq == bqPlayerBufferQueue);
    assert(NULL == context);
    // for streaming playback, replace this test by logic to find and fill the next buffer
    if (isPlaying == IS_PLAYING_ATTACK) {
        isPlaying = IS_PLAYING_SUSTAIN;
        SLresult result;
        result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, attackBuffer, attackSize);
        if (SL_RESULT_SUCCESS != result) {
            isPlaying = IS_NOT_PLAYING_AT_ALL;
        }
        (void) result;
    } else if (isPlaying == IS_PLAYING_SUSTAIN) {
        SLresult result;
        // enqueue another buffer
        result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, sustainBuffer, sustainSize);
        // the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
        // which for this code example would indicate a programming error
        if (SL_RESULT_SUCCESS != result) {
            isPlaying = IS_NOT_PLAYING_AT_ALL;
        }
        (void) result;
    } else if (isPlaying == IS_PLAYING_RELEASE) {
        isPlaying = IS_PLAYING_SILENCE;
        SLresult result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, releaseBuffer,
                                                          releaseSize);
        if (SL_RESULT_SUCCESS != result) {
            isPlaying = IS_NOT_PLAYING_AT_ALL;
        }
        (void) result;
    } else if (isPlaying == IS_PLAYING_SILENCE) {
        SLresult result;
        result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, silenceBuffer, silenceSize);
        if (SL_RESULT_SUCCESS != result) {
            isPlaying = IS_NOT_PLAYING_AT_ALL;
        }
        (void) result;
    }
}




// create the engine and output mix objects
JNIEXPORT void JNICALL
Java_com_paddlesandbugs_dahdidahdit_brasspound_AudioHelper_createEngine(JNIEnv *env, jclass clazz) {
    SLresult result;

    // create engine
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // realize the engine
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // get the engine interface, which is needed in order to create other objects
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // create output mix, with environmental reverb specified as a non-required interface
    const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean req[1] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // realize the output mix
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // get the environmental reverb interface
    // this could fail if the environmental reverb effect is not available,
    // either because the feature is not present, excessive CPU load, or
    // the required MODIFY_AUDIO_SETTINGS permission was not requested and granted
    result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
                                              &outputMixEnvironmentalReverb);
    if (SL_RESULT_SUCCESS == result) {
        result = (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(
                outputMixEnvironmentalReverb, &reverbSettings);
        (void) result;
    }
    // ignore unsuccessful result codes for environmental reverb, as it is optional for this example

}

///////////////////////////////////////////////////////////
// create buffer queue audio player
JNIEXPORT void JNICALL
Java_com_paddlesandbugs_dahdidahdit_brasspound_AudioHelper_createBufferQueueAudioPlayer(JNIEnv *env,
                                                                             jclass clazz,
                                                                             jint sampleRate,
                                                                             jint bufSize) {
    SLresult result;
    if (sampleRate >= 0 && bufSize >= 0) {
        bqPlayerSampleRate = sampleRate * 1000;
        /*
         * device native buffer size is another factor to minimize audio latency, not used in this
         * sample: we only play one giant buffer here
         */
    }

    // configure audio source
    SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8,
                                   SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
                                   SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
    /*
     * Enable Fast Audio when possible:  once we set the same rate to be the native, fast audio path
     * will be triggered
     */
    if (bqPlayerSampleRate) {
        format_pcm.samplesPerSec = bqPlayerSampleRate;       //sample rate in mili second
    }
    SLDataSource audioSrc = {&loc_bufq, &format_pcm};

    // configure audio sink
    SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&loc_outmix, NULL};

    /*
     * create audio player:
     *     fast audio does not support when SL_IID_EFFECTSEND is required, skip it
     *     for fast audio case
     */
    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME, SL_IID_EFFECTSEND,
            /*SL_IID_MUTESOLO,*/};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,
            /*SL_BOOLEAN_TRUE,*/ };

    result = (*engineEngine)->CreateAudioPlayer(engineEngine, &bqPlayerObject, &audioSrc, &audioSnk,
                                                bqPlayerSampleRate ? 2 : 3, ids, req);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // realize the player
    result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // get the play interface
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // get the buffer queue interface
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
                                             &bqPlayerBufferQueue);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // register callback on the buffer queue
    result = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, NULL);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // get the effect send interface
    bqPlayerEffectSend = NULL;
    if (0 == bqPlayerSampleRate) {
        result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_EFFECTSEND,
                                                 &bqPlayerEffectSend);
        assert(SL_RESULT_SUCCESS == result);
        (void) result;
    }

#if 0   // mute/solo is not supported for sources that are known to be mono, as this is
    // get the mute/solo interface
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_MUTESOLO, &bqPlayerMuteSolo);
    assert(SL_RESULT_SUCCESS == result);
    (void)result;
#endif

    // get the volume interface
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_VOLUME, &bqPlayerVolume);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;

    // set the player's state to playing
    result = (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
    assert(SL_RESULT_SUCCESS == result);
    (void) result;
}



// shut down the native audio system
JNIEXPORT void JNICALL
Java_com_paddlesandbugs_dahdidahdit_brasspound_AudioHelper_shutdown(JNIEnv *env, jclass clazz) {

    isPlaying = IS_NOT_PLAYING_AT_ALL;

    // destroy buffer queue audio player object, and invalidate all associated interfaces
    if (bqPlayerObject != NULL) {
        (*bqPlayerObject)->Destroy(bqPlayerObject);
        bqPlayerObject = NULL;
        bqPlayerPlay = NULL;
        bqPlayerBufferQueue = NULL;
        bqPlayerEffectSend = NULL;
        bqPlayerMuteSolo = NULL;
        bqPlayerVolume = NULL;
    }

    // destroy output mix object, and invalidate all associated interfaces
    if (outputMixObject != NULL) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = NULL;
        outputMixEnvironmentalReverb = NULL;
    }

    // destroy engine object, and invalidate all associated interfaces
    if (engineObject != NULL) {
        (*engineObject)->Destroy(engineObject);
        engineObject = NULL;
        engineEngine = NULL;
    }

    releaseResampleBuf();
}
