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

package com.paddlesandbugs.dahdidahdit.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.paddlesandbugs.dahdidahdit.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MorsePlayer implements MorsePlayerI {



    public static final String LOG_TAG = "MorsePlayer";

    public static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    public static final int MODE = AudioTrack.MODE_STREAM;
    private static final int SAMPLE_RATE = Const.SAMPLES_PER_S;
    private static final int EFFECTIVE_SAMPLE_RATE = (int) ((float) SAMPLE_RATE * 1.5);
    private static final int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final short[] end = createEndBuffer();


    private static short[] createEndBuffer() {
        final short[] end = new short[100];
        Arrays.fill(end, 0, end.length, (short) 0);
        return end;
    }


    private PlayerRunnable p;
    private FinishedCallback finishedCallback;
    private Runnable stopCallback;
    private final Config config;
    private final MorseGenerator mg;
    private final int msToPlay;
    private int bufferSize;
    private int sampleRate = SAMPLE_RATE;
    private boolean fireFinishedOnStop = false;


    public MorsePlayer(Config config) {
        this(config, new TextMorseGenerator(createConfig(config)));
    }


    private static TextMorseGenerator.Config createConfig(Config config) {
        TextMorseGenerator.Config mgconf = new TextMorseGenerator.Config();
        mgconf.textGen = config.textGenerator;
        mgconf.timing = config.timing;
        mgconf.freqDit = config.freqDit;
        mgconf.freqDah = config.freqDah;
        mgconf.startPauseMs = config.getStartPauseMs();
        mgconf.endPauseMs = config.getEndPauseMs();
        mgconf.chirp = config.chirp;
        mgconf.qlf = config.qlf;
        mgconf.syllablePauseMs = config.syllablePauseMs;
        return mgconf;
    }


    public MorsePlayer(Config config, MorseGenerator gen) {
        this.config = config;
        this.mg = gen;
        this.msToPlay = config.getStartPauseMs() + (config.sessionS * 1000);
        this.bufferSize = AudioTrack.getMinBufferSize(EFFECTIVE_SAMPLE_RATE, CHANNEL_OUT, ENCODING);
    }


    public void setBuffer(int framesPerBufferInt) {
        this.bufferSize = framesPerBufferInt;
    }


    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }


    public void setFinishedCallback(FinishedCallback finishedCallback) {
        this.finishedCallback = finishedCallback;
    }


    public void setStopCallback(Runnable finishedCallback) {
        this.stopCallback = finishedCallback;
    }


    public Mode getMode() {
        return (p == null) ? Mode.STOPPED : p.mode;
    }


    public void setFireFinishedOnStop(boolean value) {
        this.fireFinishedOnStop = value;
    }


    public void play() {
        if (getMode() == Mode.STOPPED) {
            this.p = new PlayerRunnable();
            new Thread(p).start();
        } else {
            p.play();
        }
    }


    /**
     *
     */
    public void pause() {
        p.pause();
    }


    public void stop() {
        p.stop();
    }


    @Override
    public void close() {
        p.forceClose();
    }



    static void mix(short[] sample, SampleGenerator vol, SampleGenerator... generators) {
        short[] cumulSample = new short[sample.length];
        for (SampleGenerator generator : generators) {
            for (int i = 0; (i < sample.length); i++) {
                short genS = generator.generate();
                cumulSample[i] += genS;
            }
        }

        final int sCount = generators.length + 1;

        for (int i = 0; (i < sample.length); i++) {
            final int i1 = cumulSample[i];
            final short volS = vol.generate();
            final int i2 = (int) (((long) sample[i]));
            float toneDown = (float) volS / (float) (Short.MAX_VALUE);
            int i2td = SampleGenerator.scale(i2, toneDown);

            // mix
            sample[i] = (short) ((i2td + i1) / sCount);
        }
    }


    private class PlayerRunnable implements Runnable {

        private final Semaphore s = new Semaphore(1, true);
        private volatile Mode mode = Mode.PLAYING;


        @Override
        public void run() {
            final SampleGenerator qsb = AtmosphereModel.getQSB(config.qsb);
            List<SampleGenerator> qrX = new ArrayList<>();
            if (config.qrm != 0) {
                final SampleGenerator qrm = AtmosphereModel.getQRM(config.qrm);
                qrX.add(qrm);
            }

            if (config.qrn != 0) {
                final SampleGenerator qrn = AtmosphereModel.getQRN(config.qrn);
                qrX.add(qrn);
            }
            final SampleGenerator[] generators = qrX.toArray(new SampleGenerator[0]);

            int msPlayed = 0;

            StringBuilder textSent = new StringBuilder();

            AudioTrack player = new AudioTrack(STREAM_TYPE, sampleRate, CHANNEL_OUT, ENCODING, bufferSize, MODE);

            player.play();

            TextMorseGenerator.Part part;
            while ((part = mg.generate()) != null) {
                try {
                    s.acquire();
                } catch (InterruptedException e) {
                    // Wayne
                }

                if (mode == Mode.STOPPED) {
                    // Might have changed while waiting to acquire s (have been paused, now stopped)
                    s.release();
                    break;
                }

                if ((part.text != null) && (part.isPrinted)) {
                    textSent.append(part.text.asString());
                }

                mix(part.sample, qsb, generators);

                int i = 0;
                while (i < part.sample.length) {
                    int written = player.write(part.sample, i, (part.sample.length - i));
                    i += written;
                }

                msPlayed += part.sample.length * 1000 / SAMPLE_RATE;
                if (msPlayed > msToPlay) {
                    mg.close();
                }
                s.release();
            }

            suppressEndPulse(player);
            player.release();

            this.mode = Mode.STOPPED;

            if (stopCallback != null) {
                stopCallback.run();
            }

            if (finishedCallback != null) {
                FinishedCallback f = finishedCallback;
                finishedCallback = null;
                f.finished(textSent.toString());
            }
            Log.d(LOG_TAG, "PlayerRunnable leaving");
        }


        /**
         * Play some empty samples with volume 0 to make the KNACK sound at the end disappear that occurs when calling {@link AudioTrack#release()}.
         *
         * @param player the audio track
         */
        private void suppressEndPulse(AudioTrack player) {
            makeSureLastDitIsHeard(player);
            suppressEndClick(player);
        }


        private void suppressEndClick(AudioTrack player) {
            for (int i = 0; (i < 10); i++) {
                player.write(end, 0, end.length);
            }
            player.setVolume(0.0f);
            player.stop();
        }


        private void makeSureLastDitIsHeard(AudioTrack player) {
            long ms = config.getEndPauseMs();
            final long reps = sampleRate * ms / 1000 / end.length;
            Log.i(LOG_TAG, "Pausing " + ms + " ms with " + reps + " reps of " + end.length);
            for (int i = 0; (i < reps); i++) {
                player.write(end, 0, end.length);
            }
        }


        public synchronized void stop() {
            if (!fireFinishedOnStop) {
                finishedCallback = null;
            }

            if (this.mode == Mode.PAUSED) {
                s.release();
            }
            this.mode = Mode.STOPPED;
        }


        public synchronized void pause() {
            if (mode == Mode.PLAYING) {
                mode = Mode.PAUSED;
                try {
                    s.acquire();
                } catch (InterruptedException e) {

                }
            }
        }


        public synchronized void play() {
            if (mode == Mode.PAUSED) {
                mode = Mode.PLAYING;
                s.release();
            }
        }



        private void forceClose() {
            stopCallback = null;
            finishedCallback = null;
            stop();
        }

    }


}
