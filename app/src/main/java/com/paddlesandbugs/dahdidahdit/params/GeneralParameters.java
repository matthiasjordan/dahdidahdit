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

package com.paddlesandbugs.dahdidahdit.params;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Settings that are not to be adjusted automatically by the trainer.
 */
public abstract class GeneralParameters implements Parameters {

    private static final String LOG_TAG = "GeneralParameters";

    public static final int START_PAUSE_S = 3;
    public static final int SESSION_S = 60;

    private int sessionS;

    private int startPauseS;

    private final GeneralFadedParameters current;

    private final GeneralFadedParameters to;

    private final Context context;

    public GeneralParameters(Context context) {
        sessionS = SESSION_S;
        startPauseS = START_PAUSE_S;
        this.context = context;

        current = createFaded(context, "current");
        to = createFaded(context, "to");
    }


    protected abstract GeneralFadedParameters createFaded(Context context, String name);


    protected abstract String getSettingsPrefix();

    protected Context getContext() {
        return current().getContext();
    }

    @Override
    public GeneralFadedParameters current() {
        return current;
    }


    @Override
    public GeneralFadedParameters to() {
        return to;
    }


    public int getSessionS() {
        return sessionS;
    }


    public int getStartPauseS() {
        return startPauseS;
    }


    @Override
    public void update(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sessionS = getIntFromList(sharedPreferences, getSettingsPrefix() + "_session_duration_S", SESSION_S);
        startPauseS = sharedPreferences.getInt(getSettingsPrefix() + "_session_start_pause_duration_S", START_PAUSE_S);

        current.update(context);
        to.update(context);
    }


    protected Integer getIntFromList(SharedPreferences sharedPreferences, String key, int defaultValue) {
        String sessionSStr = sharedPreferences.getString(key, Integer.toString(defaultValue));
        return Integer.valueOf(sessionSStr);
    }

}

