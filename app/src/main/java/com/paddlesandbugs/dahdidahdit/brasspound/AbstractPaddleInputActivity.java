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

package com.paddlesandbugs.dahdidahdit.brasspound;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;

/**
 * Abstract super class for activities that do Morse code input using a hardware or on-screen paddle
 * or straight key.
 */
public abstract class AbstractPaddleInputActivity extends AbstractNavigationActivity {

    private MorseInput morseInput;

    private Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        takeKeyEvents(true);
        setDefaultKeyMode(DEFAULT_KEYS_DISABLE);
    }

    protected Config getConfig() {
        return config;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("APIA", "onResume()");

        final Config c = initializeConfig();
        initializeAudio(c);
        initializeMorseKey(c);
    }

    @Override
    protected void onPause() {
        AudioHelper.stopPlaying();
        AudioHelper.shutdown();
        Log.i("APIA", "onPause()");
        super.onPause();
    }

    @NonNull
    private Config initializeConfig() {
        Config c = new Config();
        c.update(this);
        config = c;
        return c;
    }


    protected MorseInput getMorseInput() {
        return morseInput;
    }


    private void initializeAudio(Config c) {
        int freq = c.freqDit;
        Log.i("APIA", "freq " + freq);
        AudioHelper.start(this, freq);
    }

    private void initializeMorseKey(Config c) {
        LearningValue wpm = getInitialWpm();

        if (c.isPaddles) {
            morseInput = new PaddleMorseInput(this, wpm);
        } else {
            morseInput = new StraightMorseInput(this, wpm);
        }

        morseInput.init(getCharListener());
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        morseInput.handleKey(event);
        return super.dispatchKeyEvent(event);
    }

    /**
     * The initial value for WPM.
     *
     * @return the initial value for words per minute
     */
    protected abstract LearningValue getInitialWpm();


    /**
     * The {@link com.paddlesandbugs.dahdidahdit.brasspound.Decoder.CharListener}.
     *
     * @return the {@link com.paddlesandbugs.dahdidahdit.brasspound.Decoder.CharListener}
     */
    protected abstract Decoder.CharListener getCharListener();

}
