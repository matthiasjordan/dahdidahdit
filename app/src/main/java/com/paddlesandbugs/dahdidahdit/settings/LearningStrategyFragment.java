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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

import java.util.ArrayList;
import java.util.List;

@Keep
public class LearningStrategyFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.prefs_learning_strategy, rootKey);

        makeCheckBoxPrefsMutuallyExclusive();
    }


    private void makeCheckBoxPrefsMutuallyExclusive() {
        List<String> keys = new ArrayList<>();

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        final int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; (i < count); i += 1) {
            String key = preferenceScreen.getPreference(i).getKey();
            keys.add(key);
        }

        for (String key : keys) {
            findPreference(key).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    if (!(preference instanceof  CheckBoxPreference)) {
                        return false;
                    }

                    final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                    if (!checkBoxPreference.isChecked()) {
                        // Clicked box was already checked and is now unchecked.
                        // Check again and bail out to not unclick it.
                        checkBoxPreference.setChecked(true);
                        return true;
                    }

                    final String clickedKey = preference.getKey();
                    final String learnStratName = getName(clickedKey);
                    getPreferenceManager().getSharedPreferences().edit().putString(MainActivity.LEARNING_STRATEGY_PREFS_KEY, learnStratName).apply();
                    Log.i(SettingsActivity.LOG_TAG, "Setting learning strategy to " + learnStratName);

                    for (String key : keys) {
                        if (!key.equals(clickedKey)) {
                            ((CheckBoxPreference) findPreference(key)).setChecked(false);
                        }
                    }
                    return true;
                }


                private String getName(String clickedKey) {
                    int i = clickedKey.lastIndexOf('_');
                    return clickedKey.substring(i + 1);
                }
            });
        }
    }


}
