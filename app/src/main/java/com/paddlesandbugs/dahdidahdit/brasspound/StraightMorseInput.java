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
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;

/**
 * {@link MorseInput} implementation for use with an on-screen or a hardware straight key.
 */
public class StraightMorseInput extends MorseInput {


    public StraightMorseInput(Activity context, LearningValue wpm) {
        super(context, wpm);
        Button l = context.findViewById(R.id.buttonLeft);
        l.setText("");
        context.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        context.findViewById(R.id.speedButtons).setVisibility(View.GONE);
    }


    @NonNull
    protected Keyer createKeyer() {
        return new StraightKeyKeyer();
    }


    @NonNull
    protected Decoder createDecoder() {
        return new SpeedSensingDecoder(new StaticDecoder());
    }


    @Override
    protected void setSpeed(int wpmi) {
        // Straight keyer. We don't do this sort of thing!
    }
}
