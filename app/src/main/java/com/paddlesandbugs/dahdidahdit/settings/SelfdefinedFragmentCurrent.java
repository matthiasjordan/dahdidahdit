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

import androidx.annotation.Keep;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedParams;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedParamsFaded;

@Keep
public class SelfdefinedFragmentCurrent extends AbstractFadedFragment {


    @Override
    protected int getSettingsID() {
        return R.xml.prefs_selfdefined_current;
    }


    @Override
    protected String getPrefsKeyPrefix() {
        return SelfdefinedParams.SETTINGS_PREFIX;
    }


    @Override
    protected String getInfix() {
        return "current";
    }


    @Override
    protected GeneralFadedParameters getFadedParameters() {
        return new SelfdefinedParamsFaded(getContext(), "current");
    }

}
