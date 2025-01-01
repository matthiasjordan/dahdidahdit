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

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.SeekBarPreference;

/**
 * Utils for dealing with settings.
 */
public class SettingsUtils {

    /**
     * Attaches the given summary provider to the preference, making sure it is updated during user interaction.
     *
     * @param preference the preference
     * @param provider   the provider to use
     */
    public static void setAutoupdatingSummaryProvider(SeekBarPreference preference, Preference.SummaryProvider<SeekBarPreference> provider) {
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference pref, Object newValue) {
                if (pref != preference) {
                    return false;
                }
                // Seems to be necessary to update the summary.
                preference.setValue((Integer) newValue);
                return true;
            }
        });

        preference.setSummaryProvider(provider);
    }
}
