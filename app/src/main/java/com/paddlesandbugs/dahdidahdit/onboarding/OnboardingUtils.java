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

package com.paddlesandbugs.dahdidahdit.onboarding;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicReference;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;

public class OnboardingUtils {

    public static boolean playSound = false;


    public static View createSeekBar(Context context, OnboardingActivity.Consumer consumer, int defaultWPM, AtomicReference<OnboardingActivity.Consumer> wpmf, OnboardingActivity.Values values) {
        LinearLayout l = new LinearLayout(context);
        l.setPadding(0, 10, 0, 0);

        final SeekBar s = new SeekBar(context);
        final LinearLayout.LayoutParams sl = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        s.setLayoutParams(sl);
        sl.weight = 1;

        final OnboardingActivity.Consumer wpmHandler = new OnboardingActivity.Consumer() {
            @Override
            public void accept(int wpm) {
                s.setProgress(calcProgress(wpm));
            }
        };
        wpmHandler.accept(defaultWPM);

        if (wpmf != null) {
            wpmf.set(wpmHandler);
        }

        l.addView(s);

        TextView t = new TextView(context);
        final LinearLayout.LayoutParams tvLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvLayout.gravity = Gravity.END;
        tvLayout.weight = 0;
        t.setLayoutParams(tvLayout);
        final SeekBar.OnSeekBarChangeListener osbcl = new SeekBar.OnSeekBarChangeListener() {

            private boolean interactive = false;


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int wpm = calcWPM(progress);
                if (fromUser && interactive) {
                    consumer.accept(wpm);
                    Log.i("ONBOARDING", "setting to " + wpm);
                }
                t.setText(String.format("%2d", wpm));
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                interactive = true;
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                interactive = false;
                playSound(context, values);
            }
        };

        s.setOnSeekBarChangeListener(osbcl);
        osbcl.onProgressChanged(null, s.getProgress(), true);
        l.addView(t);

        return l;
    }


    private static int calcWPM(int progress) {
        final int min = 10;
        final int max = 40;

        float f = (float) progress / 100.0f;
        int wpm = min + (int) Math.round((float) (max - min) * f);
        return wpm;
    }


    private static int calcProgress(int wpm) {
        final int min = 10;
        final int max = 40;

        float f = (float) (wpm - min) / (float) (max - min);
        return (int) Math.round(100.0f * f);
    }


    public static void playSound(Context context, OnboardingActivity.Values values) {
        if (!playSound) {
            return;
        }

        int freq = Config.parseFrequency(values.frequency, 600);

        GeneralFadedParameters gfp = new GeneralFadedParameters(context, "") {
            @Override
            protected String getPrefsKeyPrefix() {
                return "";
            }
        };

        gfp.setWPM(values.wpm);
        gfp.setEffWPM(values.wpmEff);
        MorsePlayer.Config config = new MorsePlayer.Config().from(gfp);
        config.textGenerator = new StaticTextGenerator("cw");
        config.freqDit = freq;
        config.freqDah = freq;
        config.sessionS = 5;
        config.setStartPauseMs(context, 400);
        MorsePlayer p = new MorsePlayer(config);
        p.play();
    }


}
