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

package com.paddlesandbugs.dahdidahdit.selfdefined;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;

/**
 * Settings that are to be adjusted automatically by the trainer.
 */
public class SelfdefinedParams extends GeneralParameters {

    public static final String SETTINGS_PREFIX = "selfdefined";

    private String textGenerator;

    private String text;

    private String letters;

    private boolean chirp;

    private int qlf;

    private int wordCount;


    public SelfdefinedParams(Context context) {
        super(context);
    }


    @Override
    protected String getSettingsPrefix() {
        return SETTINGS_PREFIX;
    }


    @Override
    protected GeneralFadedParameters createFaded(Context context, String name) {
        return new SelfdefinedParamsFaded(context, name);
    }


    public String getTextGenerator() {
        return textGenerator;
    }


    public String getText() {
        return text;
    }


    public String getLetters() {
        return letters;
    }


    public boolean isChirp() {
        return chirp;
    }


    public int getQLF() {
        return qlf;
    }


    public int getWordCount() {
        return wordCount;
    }


    @Override
    public void update(Context context) {
        super.update(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        textGenerator = sharedPreferences.getString(getSettingsPrefix() + "_text_generator", "random");
        text = sharedPreferences.getString(getSettingsPrefix() + "_text", "vvv<ka>");
        letters = sharedPreferences.getString(getSettingsPrefix() + "_letters", "");
        chirp = sharedPreferences.getBoolean(getSettingsPrefix() + "_chirp", false);
        qlf = getIntFromList(sharedPreferences, getSettingsPrefix() + "_qlf2", 1);
        wordCount = sharedPreferences.getInt(getSettingsPrefix() + "_text_first_n", 1);

        current().setKochLevel(MainActivity.getCopyTrainer(context).getSequence().getMax());
    }
}

