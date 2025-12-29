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


package com.paddlesandbugs.dahdidahdit.base;

import static android.content.Context.UI_MODE_SERVICE;

import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class NightMode {

    public static void setNightMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String modeStr = prefs.getString("darkmode", "system");
        setNightMode(context, modeStr);
    }


    public static void setNightMode(Context context, String modeStr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            int mode = toUiModeManagerMode(modeStr);
            ((UiModeManager) context.getSystemService(UI_MODE_SERVICE)).setApplicationNightMode(mode);
        } else {
            int mode = toAppCompDelegateMode(modeStr);
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }


    private static int toUiModeManagerMode(String modeStr) {
        int mode;
        switch (modeStr) {
            case "bright": {
                mode = UiModeManager.MODE_NIGHT_NO;
                break;
            }
            case "dark": {
                mode = UiModeManager.MODE_NIGHT_YES;
                break;
            }
            case "system":
            default: {
                mode = UiModeManager.MODE_NIGHT_AUTO;
                break;
            }
        }

        return mode;
    }


    private static int toAppCompDelegateMode(String modeStr) {
        int mode;
        switch (modeStr) {
            case "bright": {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            }
            case "dark": {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            }
            case "system":
            default: {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
            }
        }

        return mode;
    }


}
