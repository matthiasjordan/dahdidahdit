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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;

public abstract class MorseInput {
    private final Activity context;

    private final LearningValue wpm;

    private final TextView wpmView;

    private HardwarePaddle hwPaddle;

    private OnScreenPaddle osPaddle;

    private Keyer keyer;

    private Decoder decoder;

    private View morseVisual;


    public MorseInput(Activity context, LearningValue wpm) {
        this.context = context;
        this.wpm = wpm;
        this.wpmView = context.findViewById(R.id.paddleWpm);
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

        keyer = createKeyer();
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

        decoder = createDecoder();
        decoder.register(charListener);

        keyer.register(decoder);

        context.findViewById(R.id.buttonMinus).setOnClickListener(new SpeedButtonClickListener(context, keyer, decoder, wpm, -1));
        context.findViewById(R.id.buttonPlus).setOnClickListener(new SpeedButtonClickListener(context, keyer, decoder, wpm, +1));

        setSpeed(wpm.get());

        hwPaddle = new HardwarePaddle(keyer);
        osPaddle = new OnScreenPaddle(context, keyer);

        wpm.setOnChangeListener(new LearningValue.OnChangeListener() {
            @Override
            public void handle(int oldValue, int newValue) {
                updateWpmView();
            }
        });

        updateWpmView();
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
