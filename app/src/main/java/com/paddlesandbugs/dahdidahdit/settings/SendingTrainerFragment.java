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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.brasspound.SendingTrainerActivity;
import com.paddlesandbugs.dahdidahdit.onboarding.OnboardingActivity;
import com.paddlesandbugs.dahdidahdit.text.PrefsBasedTextGeneratorFactory;

@Keep
public class SendingTrainerFragment extends PreferenceFragmentCompat {


    private static final String RECEIVED_FILE_NAME = SendingTrainerActivity.RECEIVED_FILE_NAME;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_sendingtrainer_main, rootKey);

        SettingsActivity.addWordListChangeListener(this, "sendingtrainer");

        ListPreference pref = findPreference("sendingtrainer_text_generator");
        if (pref != null) {
            final Preference.OnPreferenceChangeListener currentListener = pref.getOnPreferenceChangeListener();
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    currentListener.onPreferenceChange(preference, newValue);
                    updateKochOnly(newValue);
                    return true;
                }

                private void updateKochOnly(Object value) {
                    Preference p = findPreference("sendingtrainer_only_koch_chars");
                    if ((p != null) && (value instanceof String)) {
                        if (PrefsBasedTextGeneratorFactory.isHonoringKochLevel((String) value)) {
                            p.setEnabled(true);
                        } else {
                            p.setEnabled(false);
                        }
                    }
                }

            });

            pref.callChangeListener(pref.getValue());
        }

        final OnboardingActivity.Values v = new OnboardingActivity.Values(getContext());
        (new MorseDemoPlayer(getContext()) {
            @Override
            protected void handle(Object newValue, OnboardingActivity.Values v) {
                v.setWpm((Integer) newValue);
                v.setEffWpm((Integer) newValue);
            }
        }).addHook(this, v, "sendingtrainer_current_wpm");

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || requestCode != 1) {
            // Exit without doing anything else
            return;
        } else {
            new ReceivedFile(getContext(), RECEIVED_FILE_NAME).handleIncomingDataIntent(data);
            refreshForm();
        }
    }


    private void refreshForm() {
        getPreferenceScreen().removeAll();
        addPreferencesFromResource(R.xml.prefs_root);
    }


    @Override
    public void onResume() {
        super.onResume();
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setTitle(R.string.sendingtrainer_title);
        }
    }
}
