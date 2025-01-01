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

package com.paddlesandbugs.dahdidahdit.brasspound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.GradingStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningProgress;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.widget.Widgets;

public class SendingTrainerActivity extends AbstractNavigationActivity {

    public static final String RECEIVED_FILE_NAME = "sendingtrainer";

    /**
     * Factor for scaling (down) the smiley/frowny used for feedback.
     */
    public static final float ICON_SCALE_FACTOR = 0.7f;

    private static final int HOW_MANY_WORDS_TO_COUNT_AS_PRACTICE_DAY = 5;

    private final State stateInitial = new InitialState();

    private final State stateStarting = new StartingState();

    private final State stateSending = new SendingState();

    private final State stateCompare = new CompareState();

    private final StringBuilder tt = new StringBuilder();

    private TextView currentWordTitle;

    private TextView currentWord;

    private TextView copiedWordTitle;

    private TextView copiedWord;

    private ImageView imageYay;

    private ImageView imageNay;

    private MorseInput morseInput;

    private State state;

    private String currentWordText = "cq";

    private String currentCopiedWord = "";

    private TextGenerator generator;

    private GradingStrategy gradingStrategy;

    private int wordsKeyed = 0;


    public static void callMe(Context context) {
        if (!SendingTrainerIntro.callMe(context)) {
            Intent intent = new Intent(context, SendingTrainerActivity.class);
            context.startActivity(intent);
        }
    }


    @Override
    protected String getHelpPageName() {
        return getString(R.string.helpurl_sendingtraining);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_sending_trainer;
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_SENDINGTRAINER;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        takeKeyEvents(true);
        setDefaultKeyMode(DEFAULT_KEYS_DISABLE);
        MainActivity.setActivity(this, MainActivity.SENDINGTRAINER);

        currentWordTitle = findViewById(R.id.current_word_title);
        currentWord = findViewById(R.id.current_word);
        copiedWordTitle = findViewById(R.id.copied_word_title);
        copiedWord = findViewById(R.id.copied_word);

        imageYay = findViewById(R.id.imageStar);
        scale(imageYay);
        imageNay = findViewById(R.id.imageFail);
        scale(imageNay);
    }


    private void scale(ImageView i) {
        i.setScaleX(ICON_SCALE_FACTOR);
        i.setScaleY(ICON_SCALE_FACTOR);
    }


    @Override
    protected void onResume() {
        super.onResume();

        SendingLearningStrategy strategy = new SendingLearningStrategy(this);
        gradingStrategy = (GradingStrategy) strategy;
        LearningValue wpm = strategy.getWpm();

        generator = strategy.getSessionConfig().morsePlayerConfig.textGenerator;

        Config c = new Config();
        c.update(this);

        int freq = c.freqDit;
        Log.i("STA", "freq " + freq);
        AudioHelper.start(this, freq);

        enter(stateInitial);

        if (c.isPaddles) {
            morseInput = new PaddleMorseInput(this, wpm);
        } else {
            morseInput = new StraightMorseInput(this, wpm);
        }
        morseInput.init(new Decoder.CharListener() {


            @Override
            public void decoded(MorseCode.CharacterData c) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String plain;
                        if (MorseCode.WORDBREAK.equals(c)) {
                            final String word = tt.toString();
                            clearTextBuffer();
                            state.onWordCompleted(word);
                        } else {
                            if (c == null) {
                                plain = "*";
                            } else {
                                plain = c.getPlain();
                            }
                            tt.append(plain);
                        }
                    }
                });
            }
        });
    }


    private void clearTextBuffer() {
        tt.delete(0, tt.length());
    }


    @Override
    protected void onPause() {
        AudioHelper.stopPlaying();
        AudioHelper.shutdown();
        Log.i("STA", "onPause()");
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        Log.i("STA", "onDestroy()");
        super.onDestroy();
    }


    @Override
    protected int createTitleID() {
        return R.string.sendingtrainer_title;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        morseInput.handleKey(event);
        return super.dispatchKeyEvent(event);
    }


    private void enter(State newState) {
        if (state != null) {
            state.leave();
        }
        state = newState;
        state.enter();
    }


    private interface State {
        void enter();

        void onWordCompleted(String word);

        void leave();
    }


    private class InitialState implements State {

        @Override
        public void enter() {
            currentWordTitle.setText(R.string.sendingtrainer_start_prompt);
            currentWordTitle.setVisibility(View.VISIBLE);
            currentWord.setVisibility(View.INVISIBLE);
            copiedWordTitle.setVisibility(View.INVISIBLE);
            copiedWord.setVisibility(View.INVISIBLE);
            imageYay.setVisibility(View.INVISIBLE);
            imageNay.setVisibility(View.INVISIBLE);
        }


        @Override
        public void onWordCompleted(String word) {
            SendingTrainerActivity.this.enter(stateStarting);
        }


        @Override
        public void leave() {

        }
    }

    private class StartingState implements State {

        @Override
        public void enter() {
            currentWordText = TextGenerator.fetchWord(generator);

            if (currentWordText == null) {
                SendingTrainerActivity.this.enter(stateInitial);
                return;
            }

            currentWordTitle.setText(R.string.sendingtrainer_word_prompt);
            currentWordTitle.setVisibility(View.VISIBLE);
            currentWord.setText(currentWordText);
            currentWord.setVisibility(View.VISIBLE);

            currentWord.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SendingTrainerActivity.this.enter(stateSending);
                }
            }, 2000);
        }


        @Override
        public void onWordCompleted(String word) {
        }


        @Override
        public void leave() {
        }
    }

    private class SendingState implements State {

        @Override
        public void enter() {
            currentWordTitle.setVisibility(View.INVISIBLE);
            currentWord.setVisibility(View.INVISIBLE);
            clearTextBuffer();
        }


        @Override
        public void onWordCompleted(String word) {
            currentCopiedWord = word;
            SendingTrainerActivity.this.enter(stateCompare);
        }


        @Override
        public void leave() {

        }
    }


    private class CompareState implements State {

        @Override
        public void enter() {
            wordsKeyed += 1;
            if (wordsKeyed == HOW_MANY_WORDS_TO_COUNT_AS_PRACTICE_DAY) {
                Widgets.notifyPracticed(SendingTrainerActivity.this);
            }

            currentWordTitle.setText(R.string.sendingtrainer_current_word_label);
            currentWordTitle.setVisibility(View.VISIBLE);
            currentWord.setVisibility(View.VISIBLE);

            copiedWordTitle.setVisibility(View.VISIBLE);
            copiedWord.setVisibility(View.VISIBLE);
            copiedWord.setText(currentCopiedWord);

            if (currentWordText.equals(currentCopiedWord)) {
                imageYay.setVisibility(View.VISIBLE);
                gradingStrategy.onButtonPress(LearningProgress.Mistake.LOW);
            } else {
                imageNay.setVisibility(View.VISIBLE);
                gradingStrategy.onButtonPress(LearningProgress.Mistake.HIGH);
            }

            currentWord.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SendingTrainerActivity.this.enter(stateStarting);
                }
            }, 2000);
        }


        @Override
        public void onWordCompleted(String word) {
        }


        @Override
        public void leave() {
            copiedWordTitle.setVisibility(View.INVISIBLE);
            copiedWord.setVisibility(View.INVISIBLE);
            imageYay.setVisibility(View.INVISIBLE);
            imageNay.setVisibility(View.INVISIBLE);
        }
    }

}
