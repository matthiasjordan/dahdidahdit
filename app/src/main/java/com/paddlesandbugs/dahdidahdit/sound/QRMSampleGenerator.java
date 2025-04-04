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

import java.util.Random;

public class QRMSampleGenerator implements SampleGenerator {

    private static final short minValue = Short.MIN_VALUE;
    private static final int bound = Short.MAX_VALUE - minValue;

    private final Random random = new Random();
    private final float vol;


    public QRMSampleGenerator(float vol) {
        this.vol = vol;
    }

    @Override
    public short generate() {
        final int rnd = random.nextInt(bound);
        final int centered = rnd + minValue;
        final float scaled = ((float) centered) * vol;
        return (short) scaled;
    }
}
