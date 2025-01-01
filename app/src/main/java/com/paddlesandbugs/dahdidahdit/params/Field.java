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

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;

public enum Field {
    KOCH_LEVEL('k', 0, 's', "%s_%s_level", R.string.koch_level), //
    WPM('w', 12, 'i', "%s_%s_wpm", R.string.words_per_minute), //
    EFF_WPM('W', 12, 'i', "%s_%s_effwpm", R.string.eff_words_per_minute), //
    WORD_LENGTH_MAX('L', 5, 'i', "%s_%s_wordlength_max", R.string.wordlength_max), //
    QSB('b', 1, 's', "%s_%s_qsb", R.string.QSB), //
    QRM('m', 1, 's', "%s_%s_qrm", R.string.QRM), //
    QRN('n', 1, 's', "%s_%s_qrn", R.string.QRN), //
    DISTRIBUTION('d', 0, 'i', "%s_%s_distribution", R.string.distribution); //

    public final Integer defaultValue;
    private final char seq;
    private final String prefsKeyTemplate;
    private final char prefsType;
    private final int stringId;


    Field(char seq, Integer defaultValue, char prefsType, String prefsKeyTemplate, int stringId) {
        this.seq = seq;
        this.defaultValue = defaultValue;
        this.prefsType = prefsType;
        this.prefsKeyTemplate = prefsKeyTemplate;
        this.stringId = stringId;
    }


    public static Field fromSeq(char seq) {
        for (Field field : values()) {
            if (field.seq == seq) {
                return field;
            }
        }
        return null;
    }


    public static String getPrefsKey(Field field, Context context, String activity, String infix) {
        return field.getPrefsKey(context, activity, infix);
    }


    public String getPrefsKey(CopyTrainer trainer, String activity, String infix) {
        return getPrefsKey(trainer.getSequence().getPrefsKeyInfix(), activity, infix);
    }


    public String getPrefsKey(Context context, String activity, String infix) {
        return getPrefsKey(MainActivity.getCopyTrainer(context), activity, infix);
    }


    public String getPrefsKey(String sequenceName, String activity, String infix) {
        switch (this) {
            case KOCH_LEVEL:
                return getPrefsKeyInt(sequenceName, activity, infix);
            default:
                return getPrefsKeyInt(activity, infix);

        }
    }


    private String getPrefsKeyInt(String activity, String infix) {
        return String.format(prefsKeyTemplate, activity, infix);
    }


    private String getPrefsKeyInt(CopyTrainer trainer, String activity, String infix) {
        return getPrefsKeyInt(trainer.getSequence().getPrefsKeyInfix(), activity, infix);
    }


    private String getPrefsKeyInt(String sequenceName, String activity, String infix) {
        return getPrefsKeyInt(activity, infix + "_" + sequenceName);
    }


    public char getPrefsType() {
        return prefsType;
    }


    public char seq() {
        return seq;
    }


    public int getStringId() {
        return stringId;
    }
}
