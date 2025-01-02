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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicReference;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.IntroScreen;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;

class UsertypeScreenAdvanced implements UsertypeScreen {

    private final Context context;
    private final OnboardingActivity.Values values;


    public UsertypeScreenAdvanced(Context context, OnboardingActivity.Values values) {
        this.context = context;
        this.values = values;
    }


    class WPMMediator {

        public final AtomicReference<OnboardingActivity.Consumer> wpmefff = new AtomicReference<>();
        public final AtomicReference<OnboardingActivity.Consumer> wpmf = new AtomicReference<>();


        public void wpmChanged(int e) {
            values.wpm = e;
            if (values.wpm < values.wpmEff) {
                values.wpmEff = values.wpm;
                if (wpmefff.get() != null) {
                    wpmefff.get().accept(values.wpmEff);
                }
            }
        }


        public void wpmEffChanged(int e) {
            values.wpmEff = e;
            if (values.wpm < values.wpmEff) {
                values.wpm = values.wpmEff;
                if (wpmf.get() != null) {
                    wpmf.get().accept(values.wpm);
                }
            }
        }

    }

    private final WPMMediator wpmmediator = new WPMMediator();


    @Override
    public View view() {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        int colId = Utils.getThemeColor(context, R.attr.colorOnPrimary);

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

        View wpmv = OnboardingUtils.createSeekBar(context, wpmmediator::wpmChanged, values.wpm, wpmmediator.wpmf, values);
        l.addView(wpmv);

        TextView twpmeff = new TextView(context);
        twpmeff.setText(R.string.eff_words_per_minute);
        IntroScreen.Builder.style(twpmeff, IntroScreen.Builder.DEFAULT_FONT_SIZE_SP, colId);
        twpmeff.setPadding(0, 20, 0, 0);
        l.addView(twpmeff);

        View wpmeff = OnboardingUtils.createSeekBar(context, wpmmediator::wpmEffChanged, values.wpmEff, wpmmediator.wpmefff, values);
        l.addView(wpmeff);

        return l;
    }


    @Override
    public OnboardingActivity.Values values() {
        values.wpm = context.getResources().getInteger(R.integer.default_value_wpm_general);
        values.wpmEff = context.getResources().getInteger(R.integer.default_value_effwpm_general);
        values.kochLevel = MainActivity.getCopyTrainer(context).getSequence().getMax();
        values.frequency = context.getResources().getString(R.string.default_value_frequency_general);
        return values;
    }


    @Override
    public void onVisible() {
        values();
        wpmmediator.wpmf.get().accept(values.wpm);
        wpmmediator.wpmefff.get().accept(values.wpmEff);
    }
}
