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

package com.paddlesandbugs.dahdidahdit.onboarding;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicReference;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.IntroScreen;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;

class UsertypeScreenPro implements UsertypeScreen {

    private final Context context;
    private final OnboardingActivity.Values values;

    public final AtomicReference<OnboardingActivity.Consumer> wpmf = new AtomicReference<>();


    public UsertypeScreenPro(Context context, OnboardingActivity.Values values) {
        this.context = context;
        this.values = values;
    }


    @Override
    public View view() {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        int colId = Utils.getThemeColor(context, OnboardingActivity.TEXT_COLOR);

        TextView t = new TextView(context);
        t.setText(R.string.onboarding_advanced_text);
        IntroScreen.Builder.style(t, IntroScreen.Builder.DEFAULT_FONT_SIZE_SP, colId);
        t.setPadding(0, 10, 0, 0);
        l.addView(t);

        TextView twpm = new TextView(context);
        twpm.setText(R.string.words_per_minute);
        IntroScreen.Builder.style(twpm, IntroScreen.Builder.DEFAULT_FONT_SIZE_SP, colId);
        twpm.setPadding(0, 20, 0, 0);
        l.addView(twpm);

        View wpmv = OnboardingUtils.createSeekBar(context, e -> {
            values.wpm = e;
            values.wpmEff = e;
        }, values.wpm, wpmf, values);

        l.addView(wpmv);

        TextView ttone = new TextView(context);
        ttone.setText(R.string.onboarding_tone_freq);
        IntroScreen.Builder.style(ttone, IntroScreen.Builder.DEFAULT_FONT_SIZE_SP, colId);
        ttone.setPadding(0, 20, 0, 0);
        l.addView(ttone);

        String[] freqList = context.getResources().getStringArray(R.array.side_tone_frequencies);
        int[] freqList2 = context.getResources().getIntArray(R.array.side_tone_frequencies);

        ArrayAdapter<CharSequence> aa = new ArrayAdapter<>(context, R.layout.large_spinner_label, freqList);
        Spinner s = new Spinner(context);
        int spBgColId = Utils.getThemeColor(context, R.attr.colorSecondary);
        IntroScreen.Builder.style(s, spBgColId);
        s.setAdapter(aa);
        s.setSelection(aa.getPosition(values.frequency));
        l.addView(s);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                values.frequency = freqList[position];
                OnboardingUtils.playSound(context, values);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return l;
    }


    @Override
    public OnboardingActivity.Values values() {
        values.wpm = context.getResources().getInteger(R.integer.default_value_wpm_pro);
        values.wpmEff = values.wpm;
        values.kochLevel = MainActivity.getCopyTrainer(context).getSequence().getMax();
        values.frequency = context.getResources().getString(R.string.default_value_frequency_general);
        return values;
    }


    @Override
    public void onVisible() {
        values();
        wpmf.get().accept(values.wpm);
    }
}
