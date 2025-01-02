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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.AbstractLearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.base.Tooltip;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;
import com.paddlesandbugs.dahdidahdit.sound.InstantMorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayerI;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;

public class LearnNewCharActivity extends AbstractNavigationActivity {
    private static final String LOG_TAG = "LearnNewCharActivity";

    public static final String NEXT_CHAR_KEY = "NEXT_CHAR";
    private static final int STENO_ID = 42;

    private MorseCode.CharacterList nextChars;
    private MorseCode.CharacterData playChar;
    private InstantMorsePlayer player;


    public static void callMe(Context context, MorseCode.CharacterList kochChars) {
        MainActivity.getCopyTrainer(context).prepareRerouteNextCharLearning(kochChars);
        Intent i = new Intent(context, LearnNewCharActivity.class);
        context.startActivity(i);
    }


    public static void callMe(Context context, int kochLevel) {
        MorseCode.CharacterList chars = MainActivity.getCopyTrainer(context).getSequence().getChar(kochLevel);
        callMe(context, chars);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_learn_new_char;
    }


    @Override
    protected int createTitleID() {
        return R.string.copytrainer_learn_new_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }


    @SuppressLint("ResourceType")
    @Override
    protected void onResume() {
        super.onResume();

        nextChars = MainActivity.getCopyTrainer(this).getRerouteNextCharLearning();
        if (nextChars.size() == 0) {
            CopyTrainerActivity.callMe(this);
            finish();
            return;
        }
        playChar = nextChars.pop();
        final String text = playChar.makeDisplayString();
        TextView t = findViewById(R.id.nextCharSampleText);
        t.setText(text);

        CopyTrainerParams params = new CopyTrainerParams(this);
        params.update(this);

        Tooltip tooltip = new Tooltip(this).text(R.string.tooltip_learn_playButton).below(findViewById(R.id.playButton)).iff("learnNewCharPlay");
        tooltip.show();

        if (params.isShowSteno()) {
            addSteno(playChar, t.getLineHeight(), tooltip);
        }

        createMorsePlayer();
    }


    @Override
    protected void onPause() {
        super.onPause();
        player.close();
    }


    private void addSteno(MorseCode.CharacterData playChar, int lh, Tooltip tooltip) {
        if (playChar.getImage() == 0) {
            // No image to display
            return;
        }

        LinearLayout row = findViewById(R.id.nextCharSampleRow);
        if (row.findViewById(STENO_ID) != null) {
            // Steno image has already been added
            return;
        }

        TextView playButton = findViewById(R.id.nextCharSampleText);
        int col = playButton.getCurrentTextColor();

        ImageView im = new ImageView(this);
        {
            im.setId(STENO_ID);
            LinearLayout.LayoutParams imLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            im.setLayoutParams(imLayout);
            im.setScaleType(ImageView.ScaleType.FIT_CENTER);
            im.setCropToPadding(false);
            final int image = playChar.getImage();

            Drawable d = Utils.getDrawable(this, image, col);
            im.setImageDrawable(d);
        }

        LinearLayout borderView = new LinearLayout(this);
        {
            LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            blp.gravity = Gravity.CENTER_VERTICAL;
            borderView.setLayoutParams(blp);
            //            borderView.setBackgroundResource(R.drawable.thinborder);
        }

        LinearLayout paddingView2 = new LinearLayout(this);
        {
            LinearLayout.LayoutParams pvp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            pvp2.gravity = Gravity.CENTER_VERTICAL;
            paddingView2.setLayoutParams(pvp2);
            int pad2 = lh / 4;
            paddingView2.setPadding(0, pad2, 0, pad2);
        }

        borderView.addView(im);
        paddingView2.addView(borderView);
        row.addView(paddingView2);

        new Tooltip(this).text(R.string.tooltip_learn_steno).below(findViewById(STENO_ID)).iff("learnNewCharSteno").center().after(tooltip);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                SettingsActivity.callMe(this, SettingsActivity.SETTINGS_PART_COPYTRAINER);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    private class ParameterProvider implements AbstractLearningStrategy.ParameterProvider {
        @Override
        public GeneralParameters get() {
            CopyTrainerParams p = new CopyTrainerParams(LearnNewCharActivity.this);
            p.update(LearnNewCharActivity.this);
            return p;
        }
    }


    public void onStartPlay(View view) {

        view.setEnabled(false);
        player.play();
    }


    private void createMorsePlayer() {
        final ImageView view = findViewById(R.id.playButton);

        MorsePlayer.Config mpc = MainActivity.getCopyTrainer(this).get().getSessionConfig().morsePlayerConfig;
        mpc.textGenerator = new StaticTextGenerator(playChar + " ");
        mpc.setStartPauseMs(this, 0);

        player = new InstantMorsePlayer(mpc);
        player.setFinishedCallback(new InstantMorsePlayer.FinishedCallback() {
            @Override
            public void finished(String text) {
                Log.i(LOG_TAG, "finished()");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createMorsePlayer();
                        view.setEnabled(true);
                    }
                });
            }
        });
        player.setFireFinishedOnStop(false);
    }


    public void onNext(View view) {
        MainActivity.getCopyTrainer(this).prepareRerouteNextCharLearning(nextChars);
        MainActivity.getCopyTrainer(this).rerouteLearning(this, getClass());
    }


}