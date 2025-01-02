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

package com.paddlesandbugs.dahdidahdit.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.paddlesandbugs.dahdidahdit.onboarding.OnboardingActivity;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;

@Keep
public abstract class AbstractFadedFragment extends PreferenceFragmentCompat {


    protected abstract int getSettingsID();

    protected abstract String getInfix();

    protected abstract String getPrefsKeyPrefix();


    protected final String key(String suffix) {
        final String s = getPrefsKeyPrefix() + "_" + getInfix() + "_" + suffix;
        Log.i(SettingsActivity.LOG_TAG, "Key: " + s);
        return s;
    }


    protected abstract GeneralFadedParameters getFadedParameters();


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(getSettingsID(), rootKey);

        setEffWPMMax();

        (new MorseDemoPlayer(getContext()) {
            @Override
            protected void handle(Object newValue, OnboardingActivity.Values v) {
                final SeekBarPreference effWPMP = findPreference(key("effwpm"));
                v.setWpm((Integer) newValue);
                v.setEffWpm((Integer) effWPMP.getValue());
            }
        }).addHook(this, getFadedParameters(), key("wpm"));

        (new MorseDemoPlayer(getContext()) {
            @Override
            protected void handle(Object newValue, OnboardingActivity.Values v) {
                final SeekBarPreference wpmP = findPreference(key("wpm"));
                v.setWpm((Integer) wpmP.getValue());
                v.setEffWpm((Integer) newValue);
            }
        }).addHook(this, getFadedParameters(), key("effwpm"));

    }


    protected CharSequence[] getIntList(int length) {
        CharSequence[] res = new CharSequence[length];
        for (int i = 0; (i < length); i++) {
            res[i] = Integer.toString(i);
        }
        return res;
    }


    private void setEffWPMMax() {
        /*                WPMeff.cur <= WPM.cur             */
        final SeekBarPreference wpmP = findPreference(key("wpm"));
        final SeekBarPreference effWPMP = findPreference(key("effwpm"));

        if (wpmP != null) {
            wpmP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (effWPMP == null) {
                        return true;
                    }

                    if (effWPMP.getValue() <= (Integer) newValue) {
                        // Everything fine
                        return true;
                    } else {
                        effWPMP.setValue((Integer) newValue);
                        return true;
                    }
                }
            });
        }

        if (effWPMP != null) {
            effWPMP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (wpmP == null) {
                        return true;
                    }

                    if ((Integer) newValue <= wpmP.getValue()) {
                        // Everything fine
                        return true;
                    } else {
                        wpmP.setValue((Integer) newValue);
                        return true;
                    }
                }
            });
        }
    }


}
