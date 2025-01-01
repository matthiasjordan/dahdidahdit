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
import android.widget.ScrollView;
import android.widget.TextView;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

public class BrassPoundActivity extends AbstractNavigationActivity {

    private TextView tt;

    private ScrollView sv;

    private MorseInput morseInput;


    public static void callMe(Context context) {
        Intent intent = new Intent(context, BrassPoundActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected String getHelpPageName() {
        return getString(R.string.helpurl_breasspounder);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_brass_pound;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        takeKeyEvents(true);
        setDefaultKeyMode(DEFAULT_KEYS_DISABLE);
        MainActivity.setActivity(this, MainActivity.BRASSPOUNDER);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i("BPA", "onResume()");

        Config c = new Config();
        c.update(this);

        int freq = c.freqDit;
        Log.i("BPA", "freq " + freq);
        AudioHelper.start(this, freq);

        tt = findViewById(R.id.output);
        sv = findViewById(R.id.scroller);

        int defaultWpm = getResources().getInteger(R.integer.default_value_wpm_sending);
        LearningValue wpm = new LearningValue(this, "brasspounder_current_wpm", 1, defaultWpm, 40);

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
                        if (c == null) {
                            plain = "*";
                        } else {
                            plain = c.getPlain();
                        }
                        tt.append(plain);
                        sv.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }


    @Override
    protected void onPause() {
        Log.i("BPA", "onPause()");
        AudioHelper.stopPlaying();
        AudioHelper.shutdown();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected int createTitleID() {
        return R.string.brasspounder_title;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        morseInput.handleKey(event);
        return super.dispatchKeyEvent(event);
    }


}