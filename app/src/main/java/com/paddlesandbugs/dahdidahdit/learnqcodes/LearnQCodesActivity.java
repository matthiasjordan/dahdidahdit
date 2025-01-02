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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Timer;
import java.util.TimerTask;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.AbstractTrainerActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyParams;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;

public class LearnQCodesActivity extends AbstractTrainerActivity {

    public static final String AUTOPLAY_KEY = "AUTOPLAY";

    /**
     * Keeps track if the AUTOPLAY_KEY intent extra has already been honored and the next time we probably come back from Settings and don't have to
     * autoplay again.
     */
    private boolean autoPlayed = false;


    public static void callMe(Context context, boolean autoPlay) {
        if (!LearnQCodesIntro.callMe(context)) {
            Intent intent = new Intent(context, LearnQCodesActivity.class);
            intent.putExtra(AUTOPLAY_KEY, autoPlay);
            context.startActivity(intent);
        }
    }


    @Override
    protected LearningStrategy getLearningStrategy() {
        return LearnQCodesTrainer.get(this);
    }


    @Override
    protected int getMenuID() {
        return R.menu.menu_learnqcodes;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addcards: {
                LearningStrategy strat = getLearningStrategy();
                if (strat instanceof QCodesLearningStrategy) {
                    ((QCodesLearningStrategy) strat).addCards(this);
                    LearnQCodesActivity.callMe(this, false);
                    finish();
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_copy_trainer;
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_HEADCOPY;
    }


    @Override
    protected void onCreateCallback() {
        MainActivity.setActivity(this, MainActivity.LEARNQCODES);

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
                    if (!autoPlayed) {
                        autoPlayed = true;
                        onStartPlay(null);
                    }
                }
            });
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopShowTimeUpdateTimer();
    }


    @Override
    protected HeadcopyParams createParams() {
        HeadcopyParams p = new HeadcopyParams(this);
        p.update(this);
        return p;
    }


    @Override
    protected int createTitleID() {
        return R.string.learnqcodes_title;
    }


    @Override
    protected void onSetSessionEnd() {
        TextView v1 = findViewById(R.id.textCopyTrainHeader);
        v1.setText(R.string.qcode_session_end);

        updateShowTimeLine();
    }


    private Timer showTimeUpdateTimer;


    private boolean updateShowTimeLine() {
        Log.d("QCoAct", "updateShowTimeLine()");
        long nextMs = LearnQCodesTrainer.createFactProvider(this).getNextShowDate();

        long nowMs = System.currentTimeMillis();
        long diffMs = nextMs - nowMs;

        TextView v2 = findViewById(R.id.textCopyTrainerSubtitle);

        if (diffMs > 0) {

            if (!Utils.isDifferentDay(nowMs, nextMs)) {
                double diffS = Math.ceil((double) diffMs / 1000.0d);
                int diffMin = (int) Math.ceil(diffS / 60.0d);

                final String string = getResources().getQuantityString(R.plurals.qcode_seeyousoon, diffMin, diffMin);
                v2.setText(string);
                v2.setVisibility(View.VISIBLE);
                return false;
            }
        }

        v2.setText(R.string.qcode_tomorrow);
        v2.setVisibility(View.VISIBLE);
        return true;

    }


    private void stopShowTimeUpdateTimer() {
        if (showTimeUpdateTimer != null) {
            showTimeUpdateTimer.cancel();
            showTimeUpdateTimer = null;
        }
    }


    @Override
    protected void callGrading(String text, Object misc, Activity a) {
        GradingActivity.callMe(a, (Fact) misc);
    }

}
