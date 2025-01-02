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

package com.paddlesandbugs.dahdidahdit.headcopy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractGradingActivity;
import com.paddlesandbugs.dahdidahdit.base.GradingStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningProgress;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;

public class GradingActivity extends AbstractGradingActivity {


    public static void callMe(Context context, String morseText) {
        Intent intent = new Intent(context, GradingActivity.class);
        intent.putExtra(TRAINING_TEXT_KEY, morseText);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.exit_checkbox).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonRight).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonWrong).setVisibility(View.INVISIBLE);
        findViewById(R.id.textSent).setVisibility(View.INVISIBLE);
    }


    @Override
    protected void postCreate(CharSequence plainText) {
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_headcopy_grading;
    }


    @Override
    protected int createTitleID() {
        return R.string.headcopy_title;
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_HEADCOPY;
    }


    @Override
    protected GradingStrategy getGradingStrategy() {
        return HeadcopyTrainer.get(this);
    }


    public void onButtonShow(View view) {
        findViewById(R.id.buttonShow).setVisibility(View.GONE);
        findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
        findViewById(R.id.exit_checkbox).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonWrong).setVisibility(View.VISIBLE);
        findViewById(R.id.textSent).setVisibility(View.VISIBLE);
    }


    public void onButtonRight(View view) {
        getGradingStrategy().onButtonPress(LearningProgress.Mistake.LOW);
        forward();
    }


    public void onButtonWrong(View view) {
        getGradingStrategy().onButtonPress(LearningProgress.Mistake.HIGH);
        forward();
    }


    private void forward() {
        boolean exit = ((CheckBox) findViewById(R.id.exit_checkbox)).isChecked();
        HeadcopyActivity.callMe(this, !exit);
        finish();
    }


}