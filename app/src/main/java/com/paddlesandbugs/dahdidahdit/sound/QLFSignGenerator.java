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

/**
 * Generates signs with QLF timing.
 */
public class QLFSignGenerator implements SignGenerator {

    private final SoundGenerator sg;
    private final int freqDit;
    private final int freqDah;
    private final int charBreakMs;
    private final int syllableBreakMs;
    private final int signBreakMs;
    private final int ditMs;
    private final int dahMs;
    private final int wordBreakMs;

    private final float epsilonMax;

    private final Random random = new Random();


    public QLFSignGenerator(SoundGenerator sg, float e, int freqDit, int freqDah, MorseTiming timing, int syllableBreakMs) {
        this.sg = sg;
        this.epsilonMax = e;
        this.freqDit = freqDit;
        this.freqDah = freqDah;
        this.ditMs = timing.ditD;
        this.dahMs = timing.dahD;
        this.signBreakMs = timing.signBreakD;
        this.charBreakMs = timing.charBreakD;
        this.wordBreakMs = timing.wordBreakD;
        this.syllableBreakMs = syllableBreakMs;
    }


    @Override
    public short[] genSyllableBreak() {
        return sg.genTone(freqDit, 0.0f, syllableBreakMs).getFull();
    }


    @Override
    public short[] genWordBreak() {
        return sg.genTone(freqDit, 0.0f, timing(wordBreakMs)).getFull();
    }


    @Override
    public short[] genCharBreak() {
        return sg.genTone(freqDit, 0.0f, timing(charBreakMs)).getFull();
    }


    @Override
    public short[] genSignBreak() {
        return sg.genTone(freqDit, 0.0f, timing(signBreakMs)).getFull();
    }


    @Override
    public short[] genDit() {
        return sg.genTone(freqDit, 1.0f, timing(ditMs)).getFull();
    }


    @Override
    public short[] genDah() {
        return sg.genTone(freqDah, 1.0f, timing(dahMs)).getFull();
    }


    private int timing(int standardValue) {
        float factor = (2.0f * random.nextFloat()) - 1.0f;
        float mult = 1.0f + (epsilonMax * factor);
        int timing = (int) (((float) standardValue) * mult);
        return timing;
    }
}
