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

import java.util.Random;

import com.paddlesandbugs.dahdidahdit.Const;

public class QRNSampleGenerator implements SampleGenerator {

    private static final short minValue = Short.MIN_VALUE;

    private static final float PROB_SPACE = 10000.0f;
    private static final int bound = (int) PROB_SPACE;
    private static final short PULSE = Short.MAX_VALUE;

    private final Random random = new Random();
    private final short pulse;
    private final int pulseEveryNSamples;

    private int pulseInNSamples;

    private final int pulseWidthMax;
    private int pulseWidth;


    public QRNSampleGenerator(float vol, int pulseEveryNSecs, int pulseWidthMax) {
        this.pulse = (short) ((float) PULSE * vol);
        this.pulseEveryNSamples = pulseEveryNSecs * Const.SAMPLES_PER_S;
        this.pulseWidthMax = pulseWidthMax;
        pulseInNSamples = random.nextInt(pulseEveryNSamples);
    }


    @Override
    public short generate() {
        if (this.pulseInNSamples-- <= 0) {
            this.pulseWidth = random.nextInt(pulseWidthMax);
            this.pulseInNSamples = random.nextInt(pulseEveryNSamples);
        }

        if (this.pulseWidth != 0) {
            this.pulseWidth -= 1;
            return pulse;
        } else {
            return 0;
        }
    }
}
