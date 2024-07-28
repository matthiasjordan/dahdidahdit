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

/**
 * Generates individual samples when {@link SampleGenerator#generate()} is called.
 */
public interface SampleGenerator {

    /**
     * @return one individual sample
     */
    short generate();

    static short scale(short a, float f) {
        return (short) (((float) a) * f);
    }

    static int scale(int i2, float toneDown) {
        return (int) ((float) i2 * toneDown);
    }


    static String print(short t) {
        final short maxValue = Short.MAX_VALUE;
        float s = (float) t / (float) maxValue;
        final int y0 = 15;

        StringBuilder b = new StringBuilder();
        boolean neg = s < 0;
        int stars = (int) Math.ceil(Math.abs(s) * 10);
        int spaces = neg ? (y0 - stars) : y0;

        for (int i = 0; (i < spaces); i++) {
            b.append(' ');
        }
        for (int i = 0; (i < stars); i++) {
            b.append('*');
        }
        b.append('\n');
        final String graph = b.toString();

        return String.format("%7d - %s", t, graph);
    }
}
