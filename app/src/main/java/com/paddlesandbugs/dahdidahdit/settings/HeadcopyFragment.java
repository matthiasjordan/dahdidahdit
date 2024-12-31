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

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.SeekBarPreference;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyLearningStrategy;

@Keep
public class HeadcopyFragment extends AbstractFragmentCallingFragment {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_headcopy_main, rootKey);

        SettingsUtils.setAutoupdatingSummaryProvider(findPreference("headcopy_stage"),
                new Preference.SummaryProvider() {
                    private final HeadcopyLearningStrategy strategy = new HeadcopyLearningStrategy(getContext());

                    @Override
                    public CharSequence provideSummary(@NonNull Preference preference) {
                        final int stageNo = ((SeekBarPreference) preference).getValue();
                        final String summary = strategy.getSummaryForStage(stageNo);
                        return summary;
                    }
                });

    }


    @Override
    protected int getTitleId() {
        return R.string.headcopy_title;
    }
}
