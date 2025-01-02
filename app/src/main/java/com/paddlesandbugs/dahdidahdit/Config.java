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

package com.paddlesandbugs.dahdidahdit;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Settings that don't change too often.
 */
public class Config {

    public int freqDit = 600;
    public int freqDah = 600;
    public boolean showUppercase = false;
    public boolean wrapWithVvvKaAr = false;
    public boolean isPaddles = true;



    public void update(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        freqDit = getFrequency(sharedPreferences, "freq_dit", 600);

        boolean useDifferentDah = sharedPreferences.getBoolean("dah_frequency_differs", false);
        if (useDifferentDah) {
            freqDah = getFrequency(sharedPreferences, "freq_dah", 600);
        } else {
            freqDah = freqDit;
        }

        showUppercase = sharedPreferences.getBoolean("show_morse_text_uppercase", false);
        wrapWithVvvKaAr = sharedPreferences.getBoolean("wrap_morse_text_with_vvvkaar", false);

        isPaddles = sharedPreferences.getString("morse_key_type", "paddles").equals("paddles");
    }


    public void persist(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefs = sharedPreferences.edit();

        prefs.putString("freq_dit", freqDit + " Hz");
        prefs.putString("freq_dah", freqDah + " Hz");
        prefs.putBoolean("dah_frequency_differs", freqDah != freqDit);
        prefs.putBoolean("show_morse_text_uppercase", showUppercase);
        prefs.putBoolean("wrap_morse_text_with_vvvkaar", wrapWithVvvKaAr);

        prefs.apply();
    }


    private int getFrequency(SharedPreferences prefs, String key, int defaultValue) {
        String freqStr;
        try {
            freqStr = prefs.getString(key, null);
        } catch (Exception e) {
            return prefs.getInt(key, defaultValue);
        }
        if (freqStr == null) {
            return defaultValue;
        }

        return parseFrequency(freqStr, defaultValue);
    }


    public static int parseFrequency(String freqStr, int defaultValue) {
        String[] parts = freqStr.split(" ");
        if (parts.length != 2) {
            return defaultValue;
        }

        int freq = defaultValue;
        try {
            freq = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return defaultValue;
        }

        return freq;
    }
}
