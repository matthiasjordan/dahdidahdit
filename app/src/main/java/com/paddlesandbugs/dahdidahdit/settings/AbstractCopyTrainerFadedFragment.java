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

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.Field;

@Keep
public abstract class AbstractCopyTrainerFadedFragment extends AbstractFadedFragment {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        addKochList();
    }


    private void addKochList() {
        CharSequence[] charList = MainActivity.getCopyTrainer(getContext()).getAllCharLabels();
        CharSequence[] intList = getIntList(charList.length);

        String prefsKey = Field.KOCH_LEVEL.getPrefsKey(getContext(), getPrefsKeyPrefix(), getInfix());
        final String listKeyKoch = "copytrainer_" + getInfix() + "_sequence_level";

        Log.i(SettingsActivity.LOG_TAG, "LIST KEY IS NOW " + listKeyKoch);
        Log.i(SettingsActivity.LOG_TAG, "PREFS KEY IS NOW " + prefsKey);
        ListPreference lp = findPreference(listKeyKoch);
        if (lp == null) {
            return;
        }
        lp.setTitle(R.string.koch_level);
        lp.setSingleLineTitle(true);
        lp.setEntries(charList);
        lp.setEntryValues(intList);
        lp.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        lp.setOrder(0);
        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(prefsKey, (String) newValue).apply();
                Log.i(SettingsActivity.LOG_TAG, "WOULD CHANGE PREF " + prefsKey + " to " + newValue);
                lp.setValue((String) newValue);
                return false;
            }
        });

        PreferenceScreen screen = getPreferenceScreen();
        lp.setValue(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(prefsKey, "0"));
        screen.addPreference(lp);
        setPreferenceScreen(screen);
    }


    protected abstract String getKochDefaultValue();


}
