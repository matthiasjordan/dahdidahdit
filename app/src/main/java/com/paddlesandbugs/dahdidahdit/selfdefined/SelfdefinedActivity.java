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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractTrainerActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;

public class SelfdefinedActivity extends AbstractTrainerActivity {


    public static final String RECEIVED_FILE_NAME = "receivedFile";

    private static final String LOG_TAG = SelfdefinedActivity.class.getSimpleName();


    public static void callMe(Context context) {
        if (!SelfdefinedIntro.callMe(context)) {
            Intent intent = new Intent(context, SelfdefinedActivity.class);
            context.startActivity(intent);
        }
    }


    @Override
    protected String getHelpPageName() {
        return getString(R.string.helpurl_customtraining);
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_SELFDEFINED;
    }


    @Override
    protected void onCreateCallback() {
        MainActivity.setActivity(this, MainActivity.SELFDEFINED);
    }


    @Override
    protected SelfdefinedParams createParams() {
        SelfdefinedParams p = new SelfdefinedParams(this);
        p.update(this);
        return p;
    }


    @Override
    protected LearningStrategy getLearningStrategy() {
        return SelfdefinedTrainer.get(this);
    }


    @Override
    protected int createTitleID() {
        return R.string.selfdefined_title;
    }


    @Override
    protected void callGrading(String text, Object misc, Activity a) {
        GradingActivity.callMe(a, text);
    }

}