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

package com.paddlesandbugs.dahdidahdit.params;

import android.content.Context;

/**
 * Settings that are to be adjusted automatically by the trainer.
 */
public abstract class GeneralFadedParameters extends AbstractFadedParameters {


    public GeneralFadedParameters(Context context, String infix) {
        super(context);
        add(Field.KOCH_LEVEL, infix);
        add(Field.WPM, infix);
        add(Field.EFF_WPM, infix);
        add(Field.QSB, infix);
        add(Field.QRM, infix);
        add(Field.QRN, infix);
        add(Field.WORD_LENGTH_MAX, infix);
        add(Field.DISTRIBUTION, infix);
    }


    public int getKochLevel() {
        return get(Field.KOCH_LEVEL);
    }


    public void setKochLevel(int kochLevel) {
        set(Field.KOCH_LEVEL, kochLevel);
    }


    public int getWpm() {
        return get(Field.WPM);
    }


    public void setWPM(int wpm) {
        set(Field.WPM, wpm);
    }


    public int getEffWPM() {
        int eff_wpm = get(Field.EFF_WPM);
        return (eff_wpm == -1) ? getWpm() : eff_wpm;
    }


    public void setEffWPM(int effWPM) {
        set(Field.EFF_WPM, effWPM);
    }


    public int getQSB() {
        return get(Field.QSB);
    }


    public int getQRM() {
        return get(Field.QRM);
    }


    public int getQRN() {
        return get(Field.QRN);
    }


    public int getWordLengthMax() {
        return get(Field.WORD_LENGTH_MAX);
    }


    public int getDistribution() {
        return get(Field.DISTRIBUTION);
    }
    
}

