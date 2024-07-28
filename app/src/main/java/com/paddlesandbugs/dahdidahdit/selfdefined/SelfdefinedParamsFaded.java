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

package com.paddlesandbugs.dahdidahdit.selfdefined;

import android.content.Context;

import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;

/**
 * Settings that are to be adjusted automatically by the trainer.
 */
public class SelfdefinedParamsFaded extends GeneralFadedParameters {


    public SelfdefinedParamsFaded(Context context, String infix) {
        super(context, infix);
    }


    protected String getPrefsKeyPrefix() {
        return SelfdefinedParams.SETTINGS_PREFIX;
    }


}

