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

package com.paddlesandbugs.dahdidahdit.base;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.BuildConfig;

public class VersionTracking {

    private static final String VERSION_KEY = "dahdidahdit_version";
    private static final String PREFS_VERSION_KEY = "prefs_version";
    public static final int VERSION_BEFORE_VERSION_TRACKING = 4;

    private static int previousVersionCode;

    private static boolean isFirstStartAfterUpgrade;


    public static void init(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (isVeryFirstStart(prefs)) {
            previousVersionCode = 0;
        } else {
            previousVersionCode = prefs.getInt(VERSION_KEY, VERSION_BEFORE_VERSION_TRACKING);
        }

        isFirstStartAfterUpgrade = isFirstStartAfterUpgrade(prefs);
    }


    public static int getPreviousVersionCode() {
        return previousVersionCode;
    }

    public static int getCurrentVersionCode() {
        return BuildConfig.VERSION_CODE;
    }


    public static boolean isFirstStartAfterUpgrade() {
        return isFirstStartAfterUpgrade;
    }


    public static boolean isVeryFirstStart() {
        return (previousVersionCode == 0);
    }


    private static boolean isVeryFirstStart(SharedPreferences prefs) {
        boolean installed = prefs.contains(VERSION_KEY) || prefs.contains("freq_dah");
        return !installed;
    }


    private static boolean isFirstStartAfterUpgrade(SharedPreferences prefs) {
        final int thisVersion = getCurrentVersionCode();

        boolean veryFirstStart = isVeryFirstStart(prefs);
        if (veryFirstStart) {
            prefs.edit().putInt(VERSION_KEY, thisVersion).apply();
            return false;
        }

        int previousVersion = prefs.getInt(VERSION_KEY, 0);
        boolean thisIsAnUpgrade = (thisVersion > previousVersion);
        if (thisIsAnUpgrade) {
            prefs.edit().putInt(VERSION_KEY, thisVersion).apply();
        }
        return thisIsAnUpgrade;
    }
}
