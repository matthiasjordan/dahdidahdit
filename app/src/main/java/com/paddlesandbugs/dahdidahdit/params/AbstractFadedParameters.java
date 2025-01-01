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
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.EnumMap;
import java.util.Map;

/**
 * Settings that are to be adjusted automatically by the trainer.
 */
public abstract class AbstractFadedParameters implements FadedParameters {


    private static final String LOG_TAG = "AbstractParameters";

    private final Context context;

    private static class F {
        public int value;
        public final String prefsKey;


        public F(int value, String prefsKey) {
            this.value = value;
            this.prefsKey = prefsKey;
        }


        @Override
        public String toString() {
            return "F{" + "value=" + value + ", prefsKey='" + prefsKey + '\'' + '}';
        }
    }

    private final Map<Field, F> fieldToF = new EnumMap<>(Field.class);


    public AbstractFadedParameters(Context context) {
        this.context = context;
    }


    protected void add(Field field, String infix) {
        fieldToF.put(field, new F(field.defaultValue, field.getPrefsKey(context, getPrefsKeyPrefix(), infix)));
    }


    protected abstract String getPrefsKeyPrefix();


    protected void set(Field field, int level) {
        fieldToF.get(field).value = level;
    }


    public int get(Field f) {
        return fieldToF.get(f).value;
    }


    protected static Integer get(ParameterMap map, Field field) {
        Integer value = map.get(field);
        if (value == null) {
            value = field.defaultValue;
        }
        return value;
    }


    @Override
    public void update(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        for (Field field : fieldToF.keySet()) {
            F f = fieldToF.get(field);
            final int value;
            if (field.getPrefsType() == 's') {
                value = parseInt(sharedPreferences, f.prefsKey, field.defaultValue);
            } else {
                value = sharedPreferences.getInt(f.prefsKey, field.defaultValue);
            }
            f.value = value;
        }
    }


    private int parseInt(SharedPreferences p, String key, int defaultValue) {
        final String str = p.getString(key, Integer.toString(defaultValue));
        return Integer.parseInt(str);
    }


    @Override
    public void persist(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Field field : fieldToF.keySet()) {
            F f = fieldToF.get(field);
            if (field.getPrefsType() == 's') {
                editor.putString(f.prefsKey, Integer.toString(f.value));
            } else {
                editor.putInt(f.prefsKey, f.value);
            }
        }

        editor.apply();
    }


    @Override
    public ParameterMap toMap() {
        ParameterMap map = new ParameterMap();

        for (Field field : fieldToF.keySet()) {
            F f = fieldToF.get(field);
            map.put(field, f.value);
        }

        return map;
    }


    @Override
    public void fromMap(ParameterMap map) {
        for (Field field : fieldToF.keySet()) {
            F f = fieldToF.get(field);
            f.value = get(map, field);
        }
    }


    @Override
    public String toString() {
        return "AFP{" + "fieldToF=" + fieldToF + '}';
    }


    protected Context getContext() {
        return context;
    }
}

