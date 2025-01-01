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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

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
        final Context context = getContext();

        if (context == null) {
            return;
        }

        CharSequence[] charList = MainActivity.getCopyTrainer(context).getAllCharLabels();
        CharSequence[] intList = getIntList(charList.length);

        String prefsKey = Field.KOCH_LEVEL.getPrefsKey(context, getPrefsKeyPrefix(), getInfix());
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
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(prefsKey, (String) newValue).apply();
                Log.i(SettingsActivity.LOG_TAG, "WOULD CHANGE PREF " + prefsKey + " to " + newValue);
                lp.setValue((String) newValue);
                return false;
            }
        });

        PreferenceScreen screen = getPreferenceScreen();
        lp.setValue(PreferenceManager.getDefaultSharedPreferences(context).getString(prefsKey, "0"));
        screen.addPreference(lp);
        setPreferenceScreen(screen);
    }


    protected abstract String getKochDefaultValue();


    protected void setDistributionSummary(String infix) {
        final String key = "copytrainer_" + infix + "_distribution";
        final Preference preference = findPreference(key);
        if (preference == null) {
            return;
        }

        SettingsUtils.setAutoupdatingSummaryProvider(((SeekBarPreference) preference), new Preference.SummaryProvider<SeekBarPreference>() {
            @Override
            public CharSequence provideSummary(@NonNull SeekBarPreference pref) {
                if (pref != preference) {
                    return "";
                }
                int value = pref.getValue();
                if ((value < 0) || (10 < value)) {
                    return "";
                }

                final int percent = value * 10;
                final String message;
                switch (value) {
                    case 0: {
                        message = getResources().getString(R.string.prefs_summary_distribution_0);
                        break;
                    }
                    case 10: {
                        message = getResources().getString(R.string.prefs_summary_distribution_10);
                        break;
                    }
                    default: {
                        message = getResources().getString(R.string.prefs_summary_distribution_x, percent, 100 - percent);
                        break;
                    }
                }

                return message;
            }
        });
    }

}
