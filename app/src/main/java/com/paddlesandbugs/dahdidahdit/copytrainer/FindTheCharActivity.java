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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;
import com.paddlesandbugs.dahdidahdit.sound.InstantMorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayerI;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

public class FindTheCharActivity extends AbstractNavigationActivity {
    private static final String LOG_TAG = "FindTheCharActivity";

    public static final String NEXT_CHAR_KEY = "NEXT_CHAR";
    private static final String ISPLAYING_CHAR_KEY = "isplayingchar";

    public static final int SMILEY_FACE = 1;
    public static final int FROWNY_FACE = 0;
    public static final int SHOW_SMILEY_MS = 500;
    public static final int PRE_PLAY_SETTLE_PAUSE_MS = 0;

    protected MorseCode.CharacterData newChar;

    private TextView charSampleTextView;

    protected Button yesButton;

    protected Button noButton;

    private MorseCode.CharacterData charPlaying;
    private MorseCode.CharacterData previousCharPlayed;

    private TextGenerator rtg;

    private MorsePlayer.Config config;
    private InstantMorsePlayer player;

    private boolean firstCharSet = false;
    private boolean firstCharPlayed = false;


    public static void callMe(Context context, MorseCode.CharacterData kochChar) {
        Intent i = new Intent(context, FindTheCharActivity.class);
        i.putExtra(FindTheCharActivity.NEXT_CHAR_KEY, kochChar.getPlain());
        context.startActivity(i);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_find_new_char;
    }


    @Override
    protected int createTitleID() {
        return R.string.copytrainer_find_the_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        charSampleTextView = findViewById(R.id.nextCharSampleText);

        if (savedInstanceState != null) {
            String cstr = savedInstanceState.getString(ISPLAYING_CHAR_KEY);
            if (cstr != null) {
                MorseCode.CharacterData charD = MorseCode.getInstance().get(cstr);
                setNextChar(charD);
            }
            Log.i(LOG_TAG, "instancestateloaded");
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (charPlaying != null) {
            outState.putString(ISPLAYING_CHAR_KEY, charPlaying.getPlain());
        }
        Log.i(LOG_TAG, "instancestatesaved");
    }


    @Override
    protected void onResume() {
        super.onResume();

        setNewCharFromIntent();
        showNewChar();

        CopyTrainerParamsFaded pf = new CopyTrainerParamsFaded(this, "current");
        pf.update(this);
        final int kochLevel = pf.getKochLevel();

        final MorsePlayer.Config playerConfig = MainActivity.getCopyTrainer(this).get().getSessionConfig().morsePlayerConfig;

        playerConfig.textGenerator = new TextGeneratorFactory(this, pf, new TextGeneratorFactory.DistributionFunction() {
            @Override
            public Distribution<MorseCode.CharacterData> applyWeights(Distribution<MorseCode.CharacterData> dist) {
                MorseCode.CharacterList lastChars = MainActivity.getCopyTrainer(FindTheCharActivity.this).getSequence().getChar(kochLevel);
                if (kochLevel >= 2) {
                    float weight = (float) kochLevel - 1.0f;
                    // Higher weight for last character
                    dist.setWeight(lastChars, weight);
                    // Higher weights for similar characters
                    applySimilarityWeights(dist, lastChars, weight);
                }
                return dist;
            }


            private void applySimilarityWeights(Distribution<MorseCode.CharacterData> dist, MorseCode.CharacterList lastChars, float weight) {
                for (MorseCode.CharacterData c : lastChars) {
                    Set<MorseCode.CharacterData> similars = MorseCode.getInstance().getSimilar(c);
                    for (MorseCode.CharacterData similar : similars) {
                        if (!similar.equals(c)) {
                            dist.multWeight(similar, weight);
                        }
                    }
                }
            }
        }).create();

        rtg = playerConfig.textGenerator;

        config = playerConfig;
        config.setStartPauseMs(this, 0);
        config.sessionS = 1;

        prepareNextChar();
    }


    @Override
    protected void onPause() {
        super.onPause();
        player.close();
    }


    private void showNewChar() {
        final String text = this.newChar.makeDisplayString();
        charSampleTextView.setText(text);
        charSampleTextView.setVisibility(View.VISIBLE);
        findViewById(R.id.imageStar).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageFail).setVisibility(View.INVISIBLE);
    }


    protected void setNewCharFromIntent() {
        String newChar = getIntent().getExtras().getString(NEXT_CHAR_KEY);
        this.newChar = MorseCode.getInstance().get(newChar);
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


    public void onNext(View view) {
        MainActivity.getCopyTrainer(this).rerouteLearning(this, getClass());
    }


    public void onYesButton(View view) {
        disableButtons();
        if (firstCharPlayed) {
            if (correctlyRecognized()) {
                handleTruePositive();
            } else {
                handleFalsePositive();
            }
            playNextCharLater();
        } else {
            // First button press
            playNextChar();
        }
    }


    private void updateButtons() {
        setYesButtonText();
    }


    protected void setYesButtonText() {
        yesButton.setText(R.string.there_it_is);
    }


    public void onNoButton(View view) {
        disableButtons();
        if (firstCharPlayed) {
            if (correctlyRecognized()) {
                handleFalseNegative();
            } else {
                handleTrueNegative();
            }
        }

        playNextCharLater();
    }


    private boolean correctlyRecognized() {
        return newChar == previousCharPlayed;
    }


    private void disableButtons() {
        yesButton.setEnabled(false);
        noButton.setEnabled(false);
    }


    private void playNextCharLater() {
        charSampleTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                playNextChar();
            }
        }, SHOW_SMILEY_MS + PRE_PLAY_SETTLE_PAUSE_MS);
    }


    private void handleTruePositive() {
        flashText(SMILEY_FACE);
    }


    private void handleFalsePositive() {
        flashText(FROWNY_FACE);
    }


    private void handleTrueNegative() {
        flashText(SMILEY_FACE);
    }


    private void handleFalseNegative() {
        flashText(FROWNY_FACE);
    }


    private void flashText(int fp) {
        charSampleTextView.setVisibility(View.INVISIBLE);
        if (fp == SMILEY_FACE) {
            findViewById(R.id.imageStar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.imageFail).setVisibility(View.VISIBLE);
        }

        charSampleTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNewChar();
            }
        }, SHOW_SMILEY_MS);
    }


    private void playNextChar() {
        firstCharPlayed = true;
        player.play();
    }


    private void prepareNextChar() {
        MorseCode.CharacterData charD = null;

        while (rtg.hasNext() && ((charD = rtg.next().getChar()) == MorseCode.WORDBREAK)) {
            // Nothing
        }

        setNextChar(charD);

        MorseCode.CharacterList cl = new MorseCode.MutableCharacterList(Collections.singletonList(charD));
        config.textGenerator = new StaticTextGenerator(cl, false);
        InstantMorsePlayer p = new InstantMorsePlayer(config);
        p.setFinishedCallback(new InstantMorsePlayer.FinishedCallback() {
            @Override
            public void finished(String text) {
                Log.i(LOG_TAG, "re-enabling buttons");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prepareNextChar();
                        yesButton.setEnabled(true);
                        noButton.setEnabled(true);
                    }
                });
            }
        });
        player = p;
    }


    private void setNextChar(MorseCode.CharacterData nextChar) {
        Log.i(LOG_TAG, "Sending char \"" + charPlaying + "\"");

        if (firstCharSet) {
            updateButtons();
        }
        this.previousCharPlayed = charPlaying;
        this.charPlaying = nextChar;
        firstCharSet = true;
    }

}