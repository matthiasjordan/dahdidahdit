/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2025 Matthias Jordan <matthias@paddlesandbugs.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
****************************************************************************/

package com.paddlesandbugs.dahdidahdit.brasspound;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.Arrays;

import com.paddlesandbugs.dahdidahdit.Const;
import com.paddlesandbugs.dahdidahdit.settings.ReceivedFile;
import com.paddlesandbugs.dahdidahdit.sound.IntSoundGenerator;
import com.paddlesandbugs.dahdidahdit.sound.SoundGenerator;

public class AudioHelper {

    private static int sampleRate = Const.SAMPLES_PER_S;
    private static int bufSize = 1000;


    public static void start(Context context, int freq) {
        createEngine();

        /*
         * retrieve fast audio path sample rate and buf size; if we have it, we pass to native
         * side to create a player with fast audio enabled [ fast audio == low latency audio ];
         * IF we do not have a fast audio path, we pass 0 for sampleRate, which will force native
         * side to pick up the 8Khz sample rate.
         */
        AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        String nativeParam = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        sampleRate = Integer.parseInt(nativeParam);
        nativeParam = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        bufSize = Integer.parseInt(nativeParam);

        Log.i("AudioH", "sampleRate " + sampleRate + " bufsize " + bufSize);

        AudioHelper.createBufferQueueAudioPlayer(sampleRate, bufSize);

        final SoundGenerator.Sample fullSample = new IntSoundGenerator(sampleRate, true).genToneW(freq, 1.0f, 1);
        final short[] attackSample = fullSample.getAttack();
        final short[] sustainSample = fullSample.getSustain();
        final short[] releaseSample = fullSample.getRelease();
        short[] silenceSample = new short[100];
        Arrays.fill(silenceSample, (short) 0);
        setSamples(attackSample, attackSample.length, //
                sustainSample, sustainSample.length, //
                releaseSample, releaseSample.length, //
                silenceSample, silenceSample.length, //
                sampleRate);

        ReceivedFile f = new ReceivedFile(context, "sample-full.txt");
        f.store(fullSample.getFull());
        ReceivedFile fa = new ReceivedFile(context, "sample-attack.txt");
        fa.store(fullSample.getAttack());
        ReceivedFile fs = new ReceivedFile(context, "sample-sustain.txt");
        fs.store(fullSample.getSustain());
        ReceivedFile fr = new ReceivedFile(context, "sample-release.txt");
        fr.store(fullSample.getRelease());

        startLoop();
    }


    /**
     * Native methods, implemented in jni folder
     */
    private static native void createEngine();


    private static native void createBufferQueueAudioPlayer(int sampleRate, int samplesPerBuf);


    private static native void setSamples(short[] attackSamples, int attackCount, short[] samples, int count, short[] releaseSamples, int releaseCount, short[] silenceSamples, int silenceCount, int sampleRate);


    private static native void startLoop();


    public static native boolean playSamples();


    public static native void stopPlaying();


    public static native void shutdown();


    static {
        System.loadLibrary("native-audio-jni");
    }

}
