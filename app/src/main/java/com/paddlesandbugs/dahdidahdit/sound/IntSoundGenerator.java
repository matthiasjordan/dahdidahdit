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

public class IntSoundGenerator implements SoundGenerator {

    private static final int rampMs = 10;

    private final int sampleRate;

    private final boolean isRampAdditional;


    public IntSoundGenerator(int sampleRate) {
        this(sampleRate, false);
    }


    public IntSoundGenerator(int sampleRate, boolean isRampAdditional) {
        this.sampleRate = sampleRate;
        this.isRampAdditional = isRampAdditional;
    }


    @Override
    public Sample genTone(float dbFreq, float vol, int durationMs) {
//        Log.i("ISG", "f: " + dbFreq + " v: " + vol + " d: " + durationMs);
        durationMs = Math.max(0, durationMs);

        final int sampleTimeMs = isRampAdditional ? (rampMs + durationMs + rampMs) : durationMs;
        final int numSamples = SoundGenerator.numSamples(sampleTimeMs, sampleRate);
        final short[] sample = new short[numSamples];
        float twoPi = (float) 2 * (float) Math.PI;
        float f = (float) (twoPi / sampleRate * dbFreq);
        float a = 0;
        int rampSamples = SoundGenerator.numSamples(rampMs, sampleRate);
        float rampVol = 0f;
        float rampUp = 1f / (float) rampSamples;

        for (int i = 0; i < numSamples; ++i) {
            if (i < rampSamples) {
                // Attack
                rampVol += rampUp;
            } else if (i > (numSamples - rampSamples)) {
                // Release
                rampVol -= rampUp;
            } else if (i > rampSamples) {
                // Sustain
                rampVol = 1.0f;
            }
            float s = rampVol * vol * (float) Math.sin(a);
            sample[i] = (short) (s * Short.MAX_VALUE);
            a = a + f % twoPi;

            f = getF(i, f);
        }

        final Sample s = new Sample(sample, rampSamples, numSamples - (2 * rampSamples), rampSamples);
        return s;
    }


    public Sample genToneW(float dbFreq, float vol, int waveCount) {
        Log.i("ISG", "f: " + dbFreq + " v: " + vol + " wc: " + waveCount);
        waveCount = Math.max(0, waveCount);

        final int rampWaves = SoundGenerator.waveCount(dbFreq, rampMs);
        final int waves = isRampAdditional ? (rampWaves + waveCount + rampWaves) : waveCount;

        final int rampSamples = SoundGenerator.sampleCount(dbFreq, rampWaves, sampleRate);
        final int numSamples = SoundGenerator.sampleCount(dbFreq, waves, sampleRate);

        final short[] sample = new short[numSamples];
        float twoPi = (float) 2 * (float) Math.PI;
        float f = (float) (twoPi / sampleRate * dbFreq);
        float a = 0;
        float rampVol = 0f;
        float rampUp = 1f / (float) rampSamples;

        for (int i = 0; i < numSamples; ++i) {
            if (i < rampSamples) {
                // Attack
                rampVol += rampUp;
            } else if (i > (numSamples - rampSamples)) {
                // Release
                rampVol -= rampUp;
            } else if (i > rampSamples) {
                // Sustain
                rampVol = 1.0f;
            }
            float s = rampVol * vol * (float) Math.sin(a);
            sample[i] = (short) (s * Short.MAX_VALUE);
            a = a + f % twoPi;

            f = getF(i, f);
        }

        final Sample s = new Sample(sample, rampSamples, numSamples - (2 * rampSamples), rampSamples);
        return s;
    }


    protected float getF(int i, float f) {
        return f;
    }


}
