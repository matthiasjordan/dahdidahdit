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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.IntroScreen;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.params.Field;

class UsertypeScreenIntermediate implements UsertypeScreen {

    private final Context context;
    private final OnboardingActivity.Values values;

    private Spinner kochSpinner;


    public UsertypeScreenIntermediate(Context context, OnboardingActivity.Values values) {
        this.context = context;
        this.values = values;
    }


    @Override
    public View view() {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        TextView t = new TextView(context);
        t.setText(R.string.onboarding_intermediate_koch_question);
        int colId = Utils.getThemeColor(context, R.attr.colorOnPrimary);
        IntroScreen.Builder.style(t, IntroScreen.Builder.DEFAULT_FONT_SIZE_SP, colId);
        t.setPadding(0, 10, 0, 0);
        l.addView(t);

        CharSequence[] charList = MainActivity.getCopyTrainer(context).getAllCharLabels();

        ArrayAdapter<CharSequence> aa = new ArrayAdapter<>(context, R.layout.large_spinner_label, charList);
        Spinner s = new Spinner(context);
        int spBgColId = Utils.getThemeColor(context, R.attr.colorSecondary);
        IntroScreen.Builder.style(s, spBgColId);
        s.setAdapter(aa);
        s.setSelection(values.kochLevel);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                values.kochLevel = position;
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        kochSpinner = s;
        l.addView(s);

        return l;
    }


    @Override
    public OnboardingActivity.Values values() {
        values.wpm = context.getResources().getInteger(R.integer.default_value_wpm_general);
        values.wpmEff = context.getResources().getInteger(R.integer.default_value_effwpm_general);
        values.frequency = context.getResources().getString(R.string.default_value_frequency_general);
        values.kochLevel = Field.KOCH_LEVEL.defaultValue;
        return values;
    }


    @Override
    public void onVisible() {
        values();
        kochSpinner.setSelection(values.kochLevel);
    }
}
