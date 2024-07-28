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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractTrainerActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;

public class CopyTrainerActivity extends AbstractTrainerActivity {


    private static final String LOG_TAG = CopyTrainerActivity.class.getSimpleName();


    public static void callMe(Context context) {
        if (!CopyTrainerIntro.callMe(context)) {
            Intent intent = new Intent(context, CopyTrainerActivity.class);
            context.startActivity(intent);
        }
    }


    @Override
    protected void onCreateCallback() {
        MainActivity.setActivity(this, MainActivity.COPYTRAINER);
        MainActivity.getCopyTrainer(this).rerouteLearning(this, getClass());
    }


    protected int getMenuID() {
        return R.menu.menu_copytrainer;
    }


    @Override
    protected String getHelpPageName() {
        return getString(R.string.helpurl_copytraining);
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_COPYTRAINER;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_relearn_char: {
                relearnLastChar();
                return true;
            }
            case R.id.action_show_graffiti_cheatsheet: {
                GraffitiCheatSheetActivity.callMe(this);
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    private void relearnLastChar() {
        GeneralParameters p = createParams();
        int kochLevel = p.current().getKochLevel();
        MorseCode.CharacterList chars = MainActivity.getCopyTrainer(this).getSequence().getChar(kochLevel);
        LearnNewCharActivity.callMe(this, chars);

        finish();
    }


    @Override
    protected GeneralParameters createParams() {
        CopyTrainerParams p = new CopyTrainerParams(this);
        p.update(this);
        return p;
    }


    @Override
    protected LearningStrategy getLearningStrategy() {
        return MainActivity.getCopyTrainer(this).get();
    }


    @Override
    protected int createTitleID() {
        return R.string.copytrainer_title;
    }


    @Override
    protected void callGrading(String text, Object misc, Activity a) {
        GradingActivity.callMe(a, text);
    }

}