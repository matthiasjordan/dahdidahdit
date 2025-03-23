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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;

/**
 * Abstract super class for paddle and straight key inputs.
 */
public abstract class AbstractMorseInput {

    public static final String PREFS_KEY_KEY_CODE_LEFT = "key_code_left";

    public static final String PREFS_KEY_KEY_CODE_RIGHT = "key_code_right";

    private final Activity context;

    private final LearningValue wpm;

    private final TextView wpmView;

    private HardwarePaddle hwPaddle;

    private OnScreenPaddle osPaddle;

    private View morseVisual;



    public AbstractMorseInput(Activity context, LearningValue wpm) {
        this.context = context;
        this.wpm = wpm;
        this.wpmView = context.findViewById(R.id.paddleWpm);
    }


    /**
     * The inputs are only processed if the {@link AbstractMorseInput} is set to active.
     *
     * @param active true, if active. Else false.
     */
    public void setActive(boolean active) {
        osPaddle.setActive(active);
        hwPaddle.setActive(active);
    }


    protected Context getContext() {
        return context;
    }


    private void updateWpmView() {
        context.runOnUiThread(() -> {
            final int wpmi = wpm.get();
            if (wpmView != null) {
                wpmView.setText(Integer.toString(wpmi));
            }
            setSpeed(wpmi);
        });
    }


    abstract protected void setSpeed(int wpmi);


    public void setMorseVisual(View view) {
        this.morseVisual = view;
    }


    @NonNull
    protected abstract Keyer createKeyer();

    @NonNull
    protected abstract Decoder createDecoder();


    public void init(Decoder.CharListener charListener) {

        final Keyer keyer = initializeKeyer();

        final Decoder decoder = createDecoder();
        decoder.register(charListener);

        keyer.register(decoder);

        context.findViewById(R.id.buttonMinus).setOnClickListener(new SpeedButtonClickListener(context, keyer, decoder, wpm, -1));
        context.findViewById(R.id.buttonPlus).setOnClickListener(new SpeedButtonClickListener(context, keyer, decoder, wpm, +1));

        setSpeed(wpm.get());

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final int keyCodeLeft = prefs.getInt(PREFS_KEY_KEY_CODE_LEFT, KeyEvent.KEYCODE_A);
        final int keyCodeRight = prefs.getInt(PREFS_KEY_KEY_CODE_RIGHT, KeyEvent.KEYCODE_B);

        hwPaddle = new HardwarePaddle(keyer, keyCodeLeft, keyCodeRight);
        osPaddle = new OnScreenPaddle(context, keyer);

        wpm.setOnChangeListener(new LearningValue.OnChangeListener() {
            @Override
            public void handle(int oldValue, int newValue) {
                updateWpmView();
            }
        });

        updateWpmView();
    }


    @NonNull
    private Keyer initializeKeyer() {
        Keyer keyer = createKeyer();
        keyer.register(new Keyer.KeyListener() {


            @Override
            public void keyDown() {


                AudioHelper.playSamples();
                if (morseVisual != null) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            morseVisual.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }


            @Override
            public void keyUp() {

                AudioHelper.stopPlaying();
                if (morseVisual != null) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            morseVisual.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        return keyer;
    }


    public void handleKey(KeyEvent event) {
        this.hwPaddle.handleKey(event);
    }


    private static class SpeedButtonClickListener implements View.OnClickListener {

        private final Keyer keyer;

        private final Decoder decoder;

        private final Context context;

        private final LearningValue wpm;

        private final int delta;


        public SpeedButtonClickListener(Activity context, Keyer keyer, Decoder decoder, LearningValue wpm, int delta) {
            this.context = context;
            this.keyer = keyer;
            this.decoder = decoder;
            this.wpm = wpm;
            this.delta = delta;
        }


        @Override
        public void onClick(View v) {
            int wpmi = wpm.update(delta);
            Log.i("STA", "wpm now " + wpmi);
        }


    }

}
