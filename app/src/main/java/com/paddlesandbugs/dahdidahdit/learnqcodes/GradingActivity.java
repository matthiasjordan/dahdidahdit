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

package com.paddlesandbugs.dahdidahdit.learnqcodes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningProgress;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;
import com.paddlesandbugs.dahdidahdit.widget.Widgets;

public class GradingActivity extends AbstractNavigationActivity {

    private static final String FACT_KEY = "fact";

    private Fact fact;

    public static void callMe(Context context, Fact fact) {
        Intent intent = new Intent(context, GradingActivity.class);
        intent.putExtra(FACT_KEY, fact.id);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Widgets.notifyPracticed(this);

        int factId = getIntent().getExtras().getInt(FACT_KEY);

        QCodesLearningStrategy.FactProvider fp = LearnQCodesTrainer.createFactProvider(this);
        this.fact = fp.get(factId);

        TextView sent = findViewById(R.id.textSent);
        sent.setText(fact.meaning);

        findViewById(R.id.exit_checkbox).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonRight).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonWrong).setVisibility(View.INVISIBLE);
        findViewById(R.id.textSent).setVisibility(View.INVISIBLE);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_headcopy_grading;
    }


    @Override
    protected int createTitleID() {
        return R.string.learnqcodes_title;
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_HEADCOPY;
    }


    protected QCodesLearningStrategy getGradingStrategy() {
        return LearnQCodesTrainer.get(this);
    }


    public void onButtonShow(View view) {
        findViewById(R.id.buttonShow).setVisibility(View.GONE);
        findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
        findViewById(R.id.exit_checkbox).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonWrong).setVisibility(View.VISIBLE);
        findViewById(R.id.textSent).setVisibility(View.VISIBLE);
    }


    public void onButtonRight(View view) {
        getGradingStrategy().onButtonPress(fact, LearningProgress.Mistake.LOW);
        forward();
    }


    public void onButtonWrong(View view) {
        getGradingStrategy().onButtonPress(fact, LearningProgress.Mistake.HIGH);
        forward();
    }


    private void forward() {
        boolean exit = ((CheckBox) findViewById(R.id.exit_checkbox)).isChecked();
        LearnQCodesActivity.callMe(this, !exit);
        finish();
    }


}