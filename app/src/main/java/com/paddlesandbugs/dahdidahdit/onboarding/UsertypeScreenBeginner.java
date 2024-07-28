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

package com.paddlesandbugs.dahdidahdit.onboarding;

import android.content.Context;
import android.view.View;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.params.Field;

class UsertypeScreenBeginner implements UsertypeScreen {
    private final Context context;
    private final OnboardingActivity.Values values;


    public UsertypeScreenBeginner(Context context, OnboardingActivity.Values values) {
        this.context = context;
        this.values = values;
    }


    @Override
    public View view() {
        return null;
    }


    @Override
    public OnboardingActivity.Values values() {
        values.wpm = context.getResources().getInteger(R.integer.default_value_wpm_general);
        values.wpmEff = context.getResources().getInteger(R.integer.default_value_effwpm_general);
        values.kochLevel = Field.KOCH_LEVEL.defaultValue;
        values.frequency = context.getResources().getString(R.string.default_value_frequency_general);
        return values;
    }


    @Override
    public void onVisible() {
        values();
    }
}
