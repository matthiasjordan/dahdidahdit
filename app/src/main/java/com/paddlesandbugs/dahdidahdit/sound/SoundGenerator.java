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

import android.util.Log;

import java.util.Arrays;

/**
 * Generates a tone.
 */
public interface SoundGenerator {

    class Sample {
        private final short[] sample;
        private final int attackNum;
        private final int sustainNum;
        private final int releaseNum;


        /**
         * Creates a sample object.
         *
         * @param sample     the samples
         * @param attackNum  the number of samples that make the attack part
         * @param sustainNum the number of samples that make the sustain part
         * @param releaseNum the number of samples that make the release part
         */
        public Sample(short[] sample, int attackNum, int sustainNum, int releaseNum) {
//            Log.i("SGc", "s: " + sample.length + " a: " + attackNum + " s:" + sustainNum + " r:" + releaseNum);
            this.sample = sample;
            this.attackNum = attackNum;
            this.sustainNum = sustainNum;
            this.releaseNum = releaseNum;
        }


        public short[] getFull() {
            return sample;
        }


        public short[] getAttack() {
            return Arrays.copyOfRange(sample, 0, attackNum);
        }


        public short[] getSustain() {
            Log.i("SG", "s: " + sample.length + " a: " + attackNum + " r:" + releaseNum);
            return Arrays.copyOfRange(sample, attackNum, sample.length - releaseNum);
        }


        public short[] getRelease() {
            return Arrays.copyOfRange(sample, sample.length - releaseNum, sample.length);
        }


    }

    /**
     * Generates a tone.
     *
     * @param dbFreq    the frequency in Hz
     * @param vol       the volume where 1.0f is "normal", greater than 1 is louder (with clipping) and less than 1 is lower
     * @param waveCount the duration in number of full waves
     *
     * @return the samples
     */
    Sample genTone(float dbFreq, float vol, int waveCount);


    /**
     * Calculates the number of waves of the given frequency that fit into the given time.
     *
     * @param freq   the frequency
     * @param timeMs the time in milliseconds
     *
     * @return the number of full waves needed to approximatly span the given time
     */
    static int waveCount(float freq, int timeMs) {
        return Math.round(freq * (float) timeMs / 1000.0f);
    }

    /**
     * Calculates the number of samples needed to sample a sound of the given frequency for the number of full waves.
     *
     * @param freq       the frequency
     * @param waveCount  the number of full waves
     * @param sampleRate the sample rate
     *
     * @return the number of samples needed
     */
    static int sampleCount(float freq, int waveCount, int sampleRate) {
        return Math.round((float) sampleRate * (float) waveCount / freq);
    }

    /**
     * Calculates the number of samples for the given time.
     *
     * @param sampleTimeMs the time to sample
     * @param sampleRate   the sample rate
     *
     * @return the number of samples
     */
    static int numSamples(int sampleTimeMs, int sampleRate) {
        final long tmp = (long) sampleTimeMs;
        return (int) Math.min((tmp * sampleRate / 1000L), Integer.MAX_VALUE);
    }

}
