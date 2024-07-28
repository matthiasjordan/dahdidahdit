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
 * Generates signs with perfect timing, caching the samples for better performance.
 */
public class PerfectSignGenerator implements SignGenerator {

    private final SoundGenerator sg;
    private final int freqDit;
    private final int freqDah;
    private final int charBreakMs;
    private final int syllableBreakMs;
    private final int signBreakMs;
    private final int ditMs;
    private final int dahMs;
    private final int wordBreakMs;
    private short[] dit;
    private short[] dah;
    private short[] signBreak;
    private short[] charBreak;
    private short[] syllableBreak;
    private short[] wordBreak;


    public PerfectSignGenerator(SoundGenerator sg, int freqDit, int freqDah, MorseTiming timing, int syllableBreakMs) {
        this.sg = sg;
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
        if (syllableBreak == null) {
            syllableBreak = sg.genTone(freqDit, 0.0f, syllableBreakMs).getFull();
        }
        return syllableBreak;
    }


    @Override
    public short[] genWordBreak() {
        if (wordBreak == null) {
            wordBreak = sg.genTone(freqDit, 0.0f, wordBreakMs).getFull();
        }
        return wordBreak;
    }


    @Override
    public short[] genCharBreak() {
        if (charBreak == null) {
            charBreak = sg.genTone(freqDit, 0.0f, charBreakMs).getFull();
        }
        return charBreak;
    }


    @Override
    public short[] genSignBreak() {
        if (signBreak == null) {
            signBreak = sg.genTone(freqDit, 0.0f, signBreakMs).getFull();
        }
        return signBreak;
    }


    @Override
    public short[] genDit() {
        if (dit == null) {
            dit = sg.genTone(freqDit, 1.0f, ditMs).getFull();
        }
        return dit;
    }


    @Override
    public short[] genDah() {
        if (dah == null) {
            dah = sg.genTone(freqDah, 1.0f, dahMs).getFull();
        }
        return dah;
    }
}
