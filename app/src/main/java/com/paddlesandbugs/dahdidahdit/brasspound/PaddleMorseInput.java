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

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;

/**
 * {@link MorseInput} implementation for use with on-screen and hardware paddles.
 */
public class PaddleMorseInput extends MorseInput {

    private PaddleKeyer paddleKeyer;

    private StaticDecoder staticDecoder;


    public PaddleMorseInput(Activity context, LearningValue wpm) {
        super(context, wpm);
        Button l = context.findViewById(R.id.buttonLeft);
        l.setText("L");
        context.findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
        context.findViewById(R.id.speedButtons).setVisibility(View.VISIBLE);
    }


    @NonNull
    protected Keyer createKeyer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String polarityStr = prefs.getString("paddle_polarity", "");
        PaddleKeyer.Polarity polarity;
        if ("dit_dah".equals(polarityStr)) {
            polarity = PaddleKeyer.Polarity.DIT_DAH;
        } else {
            polarity = PaddleKeyer.Polarity.DAH_DIT;
        }

        paddleKeyer = new PaddleKeyer(polarity);
        return paddleKeyer;
    }


    @NonNull
    protected StaticDecoder createDecoder() {
        staticDecoder = new StaticDecoder();
        return staticDecoder;
    }


    @Override
    protected void setSpeed(int wpmi) {
        staticDecoder.setSpeed(wpmi);
        paddleKeyer.setSpeed(wpmi, wpmi);
    }

}
