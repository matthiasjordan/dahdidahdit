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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedActivity;

@Keep
public class SelfdefinedFragment extends AbstractFragmentCallingFragment {


    private static final String RECEIVED_FILE_NAME = SelfdefinedActivity.RECEIVED_FILE_NAME;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_selfdefined_main, rootKey);

        SettingsActivity.addWordListChangeListener(this, "selfdefined_text_generator", "selfdefined_text_first_n");

        ListPreference providerSelection = findPreference("selfdefined_rss_provider");
        if (providerSelection != null) {
            ListPreference feedSelection = findPreference("selfdefined_rss_feed");
            final RSSProviderChangeListener onProviderChangeListener = new RSSProviderChangeListener(getContext(), providerSelection, feedSelection);
            providerSelection.setOnPreferenceChangeListener(onProviderChangeListener);
            onProviderChangeListener.onPreferenceChange(providerSelection, providerSelection.getValue());
        }

        Preference pref = findPreference("selfdefined_text_chooser");
        if (pref != null) {
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("text/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
                    startActivityForResult(intent, 1);
                    return true;
                }
            });

            String fileHead = new ReceivedFile(getContext(), SelfdefinedActivity.RECEIVED_FILE_NAME).head();
            if (fileHead.length() != 0) {
                pref.setSummary(fileHead);
            }
        }
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
    protected int getTitleId() {
        return R.string.selfdefined_title;
    }
}
