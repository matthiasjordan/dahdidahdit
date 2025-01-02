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

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class LearningValue {

    private final String key;

    private final int defaultValue;

    private final SharedPreferences preferences;

    private final Integer min;

    private final Integer max;

    private OnChangeListener listener;

    public LearningValue(Context context, String key, int defaultValue) {
        this(context, key, null, defaultValue, null);
    }


    public LearningValue(Context context, String key, Integer min, int defaultValue, Integer max) {
        this.key = key;
        this.defaultValue = defaultValue;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.min = min;
        this.max = max;
    }


    public void setOnChangeListener(OnChangeListener listener) {
        this.listener = listener;
    }


    public int update(int delta) {
        int oldValue = get();
        int newValue = oldValue + delta;
        if (min != null) {
            newValue = Math.max(min, newValue);
        }
        if (max != null) {
            newValue = Math.min(max, newValue);
        }
        set(preferences, oldValue, newValue);
        return newValue;
    }


    public void set(int newValue) {
        set(preferences, -1, newValue);
    }


    public int get() {
        return preferences.getInt(key, defaultValue);
    }


    public void reset() {
        set(preferences, -1, 0);
    }


    private void set(SharedPreferences sp, int oldValue, int newValue) {
        sp.edit().putInt(key, newValue).apply();

        if (listener != null) {
            listener.handle(oldValue, newValue);
        }
    }


    public interface OnChangeListener {
        void handle(int oldValue, int newValue);
    }
}
