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

package com.paddlesandbugs.dahdidahdit.selfdefined;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractShowTextActivity;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;
import com.paddlesandbugs.dahdidahdit.widget.Widgets;

public class GradingActivity extends AbstractShowTextActivity {


    public static void callMe(Context context, String morseText) {
        Intent intent = new Intent(context, GradingActivity.class);
        intent.putExtra(TRAINING_TEXT_KEY, morseText);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    @Override
    protected void postCreate(CharSequence plainText) {
        Widgets.notifyPracticed(this);

        Button b = findViewById(R.id.buttonWrong);
        b.setText(R.string.selfdefined_grading_next_button_text);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_selfdefined_grading;
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_SELFDEFINED;
    }


    @Override
    protected int createTitleID() {
        return R.string.selfdefined_title;
    }


    public void onButtonHigh(View view) {
        SelfdefinedActivity.callMe(this);
        finish();
    }
}