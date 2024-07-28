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

package com.paddlesandbugs.dahdidahdit.settings;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.onboarding.OnboardingActivity;
import com.paddlesandbugs.dahdidahdit.onboarding.OnboardingUtils;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;

@Keep
abstract class MorseDemoPlayer {

    private final Context context;


    MorseDemoPlayer(Context context) {
        this.context = context;
    }


    public void addHook(PreferenceFragmentCompat context, OnboardingActivity.Values v, String prefsKey) {
        Preference.OnPreferenceChangeListener freqChangeHandler = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                try {
                    handle(newValue, v);
                    playMorseDemo(v);
                } catch (NumberFormatException e) {

                }
                return true;
            }
        };

        final Preference pref = context.findPreference(prefsKey);
        if (pref != null) {
            ChainedOnPreferenceChangeListener.add(pref, freqChangeHandler);
        }
    }


    public void addHook(PreferenceFragmentCompat context, GeneralFadedParameters fp, String prefsKey) {
        OnboardingActivity.Values v = new OnboardingActivity.Values(context.getContext(), fp);
        addHook(context, v, prefsKey);
    }


    protected abstract void handle(Object newValue, OnboardingActivity.Values v);


    private void playMorseDemo(OnboardingActivity.Values v) {
        Log.i(SettingsActivity.LOG_TAG, "play morse demo " + v);
        boolean oldPlay = OnboardingUtils.playSound;
        OnboardingUtils.playSound = true;
        OnboardingUtils.playSound(context, v);
        OnboardingUtils.playSound = oldPlay;
    }


    @Keep
    private static class ChainedOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

        private final List<Preference.OnPreferenceChangeListener> delegates;


        ChainedOnPreferenceChangeListener(Preference.OnPreferenceChangeListener... delegate) {
            this.delegates = new ArrayList<>();
            this.delegates.addAll(Arrays.asList(delegate));
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean res = true;
            for (Preference.OnPreferenceChangeListener delegate : delegates) {
                res = res && delegate.onPreferenceChange(preference, newValue);
            }
            return res;
        }


        public static void add(Preference pref, Preference.OnPreferenceChangeListener listener) {
            Preference.OnPreferenceChangeListener oldListener = pref.getOnPreferenceChangeListener();
            if (oldListener != null) {
                listener = new ChainedOnPreferenceChangeListener(oldListener, listener);
            }

            pref.setOnPreferenceChangeListener(listener);
        }

    }
}
