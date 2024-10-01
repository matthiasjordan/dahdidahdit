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

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParams;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;

@Keep
public class CopyTrainerFragmentTo extends AbstractCopyTrainerFadedFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        setDistributionSummary("to");
    }


    @Override
    protected int getSettingsID() {
        return R.xml.prefs_copytrainer_to;
    }


    @Override
    protected String getPrefsKeyPrefix() {
        return CopyTrainerParams.SETTINGS_PREFIX;
    }


    @Override
    protected String getInfix() {
        return "to";
    }


    @Override
    protected String getKochDefaultValue() {
        return String.valueOf(MainActivity.getCopyTrainer(getContext()).getSequence().getMax());
    }


    @Override
    protected GeneralFadedParameters getFadedParameters() {
        return new CopyTrainerParamsFaded(getContext(), "to");
    }

}
