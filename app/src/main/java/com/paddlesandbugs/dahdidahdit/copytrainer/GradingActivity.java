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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;
import android.content.Intent;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractGradingActivity;
import com.paddlesandbugs.dahdidahdit.base.GradingStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;

public class GradingActivity extends AbstractGradingActivity {


    public static void callMe(Context context, String morseText) {
        Intent intent = new Intent(context, GradingActivity.class);
        intent.putExtra(TRAINING_TEXT_KEY, morseText);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_copy_grading;
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_COPYTRAINER;
    }


    @Override
    protected int createTitleID() {
        return R.string.copytrainer_title;
    }



    @Override
    protected GradingStrategy getGradingStrategy() {
        return MainActivity.getCopyTrainer(this).get();
    }
}