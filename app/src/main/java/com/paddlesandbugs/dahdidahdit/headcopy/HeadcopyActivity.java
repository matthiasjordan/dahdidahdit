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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractTrainerActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;
import com.paddlesandbugs.dahdidahdit.sound.InstantMorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayerI;

public class HeadcopyActivity extends AbstractTrainerActivity {


    private static final String LOG_TAG = HeadcopyActivity.class.getSimpleName();
    public static final String AUTOPLAY_KEY = "AUTOPLAY";


    public static void callMe(Context context, boolean autoPlay) {
        if (!HeadcopyIntro.callMe(context)) {
            Intent intent = new Intent(context, HeadcopyActivity.class);
            intent.putExtra(AUTOPLAY_KEY, autoPlay);
            context.startActivity(intent);
        }
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_copy_trainer;
    }


    @Override
    protected String getHelpPageName() {
        return getString(R.string.helpurl_headcopying);
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_HEADCOPY;
    }


    @Override
    protected void onCreateCallback() {
        MainActivity.setActivity(this, MainActivity.HEADCOPY);

        findViewById(R.id.imagePause).setVisibility(View.GONE);
        View stop = findViewById(R.id.imageStop);
        ConstraintLayout.LayoutParams stopLayoutParams = (ConstraintLayout.LayoutParams) stop.getLayoutParams();

        ImageView start = findViewById(R.id.imageStart);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) start.getLayoutParams();
        layoutParams.rightMargin = stopLayoutParams.leftMargin;
        start.setLayoutParams(layoutParams);

        findViewById(R.id.textCopyTrainDuration).setVisibility(View.GONE);
    }


    public void onResume() {
        super.onResume();
        if (getIntent().getExtras().getBoolean(AUTOPLAY_KEY)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onStartPlay(null);
                }
            });
        }
    }


    @Override
    protected HeadcopyParams createParams() {
        HeadcopyParams p = new HeadcopyParams(this);
        p.update(this);
        return p;
    }


    @Override
    protected LearningStrategy getLearningStrategy() {
        return HeadcopyTrainer.get(this);
    }


    @Override
    protected int createTitleID() {
        return R.string.headcopy_title;
    }

    @NonNull
    protected MorsePlayerI getPlayer(LearningStrategy.SessionConfig sessionConfig) {
        sessionConfig.morsePlayerConfig.sessionS = Integer.MAX_VALUE;
        sessionConfig.morsePlayerConfig.setStartPauseMs(this, 0);
        return new InstantMorsePlayer(sessionConfig.morsePlayerConfig);
    }



    @Override
    protected void callGrading(String text, Object misc, Activity a) {
        GradingActivity.callMe(a, text);
    }

}