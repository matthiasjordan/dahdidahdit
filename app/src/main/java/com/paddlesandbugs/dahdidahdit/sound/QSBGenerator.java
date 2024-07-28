/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2024 Matthias Jordan <matthias@paddlesandbugs.com>

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

import java.util.Random;

import com.paddlesandbugs.dahdidahdit.Const;

public class QSBGenerator implements SampleGenerator {

    private static final short minValue = Short.MIN_VALUE;
    private static final int bound = Short.MAX_VALUE - minValue;

    private static final int rampMs = 1000;
    private static final int rampSamples = rampMs * (Const.SAMPLES_PER_S / 1000);
    private static final int minEffectWidth = (3 * rampSamples);
    private final short rampUp;
    private final int rampEverySamples;
    private short rampVol = 0;


    private final Random random = new Random();
    private final short volMin;
    private final short volMax;
    private final int everyNSamples;
    private final int widthSamplesMax;

    private int effectInNSamples;
    private int effectWidth;
    private int effectI = 0;


    public QSBGenerator(float volMin, float volMax, int widthSecsMax, int everyNSecs) {
        this.everyNSamples = everyNSecs * Const.SAMPLES_PER_S;
        this.widthSamplesMax = widthSecsMax * Const.SAMPLES_PER_S;
        effectInNSamples = (2 * Const.SAMPLES_PER_S) + random.nextInt(everyNSamples);

        this.volMax = SampleGenerator.scale(Short.MAX_VALUE, volMax);
        this.volMin = SampleGenerator.scale(Short.MAX_VALUE, volMin);

        float rampSlope = (this.volMax - this.volMin) / (float) rampSamples;
        if (rampSlope >= 1) {
            // Steep, more than 1 rampDiff per sample
            rampUp = (short) (rampSlope);
            rampEverySamples = 1;
        } else {
            // Shallow, fewer than 1 rampdiff per sample
            rampUp = 1;
            rampEverySamples = (int) (1.0f / rampSlope);
        }

        rampVol = this.volMax;

        Log.i("QSB", "Min:" + this.volMin +" max:" + this.volMax + " slope: " + rampSlope + " ramp step: " + rampUp + " every: " + rampEverySamples);
    }


    @Override
    public short generate() {
        if (effectInNSamples-- <= 0) {
            effectInNSamples = Integer.MAX_VALUE;
            effectWidth = Math.max(minEffectWidth, random.nextInt(widthSamplesMax));
            effectI = 0;
            rampVol = volMax;
            Log.i("QSB", "Effect playing: " + rampSamples + "+" + (effectWidth - (2 * rampSamples)) + "+" + rampSamples);
        }

        short sample;
        if (effectWidth != 0) {
            final boolean isRampStep = (effectI % rampEverySamples) == 0;

            if (isRampStep && (effectI < rampSamples) && (rampVol > volMin)) {
                rampVol -= rampUp;
            } else if (isRampStep && (effectI > (effectWidth - rampSamples)) && (rampVol < volMax)) {
                rampVol += rampUp;
            } else  {
                rampVol = volMin;
            }

            effectI += 1;
            if (effectI >= effectWidth) {
                effectInNSamples = random.nextInt(everyNSamples);
                effectWidth = 0;
                rampVol = volMax;
                Log.i("QSB", "Effect end");
            }
            sample = rampVol;
        } else {
            sample = volMax;
        }

        return sample;
    }
}
