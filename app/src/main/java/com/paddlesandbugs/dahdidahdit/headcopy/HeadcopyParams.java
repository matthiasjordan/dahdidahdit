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

package com.paddlesandbugs.dahdidahdit.headcopy;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;

/**
 * Settings that are to be adjusted automatically by the trainer.
 */
public class HeadcopyParams extends GeneralParameters {

    public static final String SETTINGS_PREFIX = "headcopy";

    private String textGenerator;

    private String text;

    private boolean chirp;

    private boolean onlyCopyTrainerChars;

    public HeadcopyParams(Context context) {
        super(context);
    }


    @Override
    protected String getSettingsPrefix() {
        return SETTINGS_PREFIX;
    }


    @Override
    protected GeneralFadedParameters createFaded(Context context, String name) {
        return new HeadcopyParamsFaded(context, name);
    }


    public String getTextGenerator() {
        return textGenerator;
    }


    public String getText() {
        return text;
    }


    public boolean isChirp() {
        return chirp;
    }


    public boolean isOnlyCopyTrainerChars() {
        return onlyCopyTrainerChars;
    }


    @Override
    public int getStartPauseS() {
        return 0;
    }


    @Override
    public int getSessionS() {
        return 100;
    }


    @Override
    public void update(Context context) {
        super.update(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        textGenerator = sharedPreferences.getString(getSettingsPrefix() + "_text_generator", "random");
        text = sharedPreferences.getString(getSettingsPrefix() + "_text", "vvv<ka>");
        chirp = sharedPreferences.getBoolean(getSettingsPrefix() + "_chirp", false);
        onlyCopyTrainerChars = sharedPreferences.getBoolean(getSettingsPrefix() + "_allow_only_copytrainer_chars", false);
    }
}

