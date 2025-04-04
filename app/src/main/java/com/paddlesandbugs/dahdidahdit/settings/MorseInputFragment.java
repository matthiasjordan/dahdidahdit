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

import androidx.annotation.Keep;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.paddlesandbugs.dahdidahdit.R;

@Keep
public class MorseInputFragment extends AbstractFragmentCallingFragment {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_morse_input, rootKey);

        final Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof String) {
                    String value = (String) newValue;
                    final boolean isPaddles = "paddles".equals(value);
                    findPreference("paddle_polarity").setVisible(isPaddles);
                    findPreference("paddle_mode").setVisible(isPaddles);
                }
                return true;
            }
        };

        final ListPreference morseKeyTypePref = findPreference("morse_key_type");
        morseKeyTypePref.setOnPreferenceChangeListener(listener);
        morseKeyTypePref.callChangeListener(morseKeyTypePref.getValue());
    }


    @Override
    protected int getTitleId() {
        return R.string.morse_input;
    }


}