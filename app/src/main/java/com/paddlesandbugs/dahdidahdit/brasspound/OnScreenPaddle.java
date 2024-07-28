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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.Tooltip;

public class OnScreenPaddle {

    private final Button left;

    private final Button right;


    public OnScreenPaddle(Activity context, Keyer keyer) {
        View v = context.findViewById(R.id.paddleButtons);
        left = v.findViewById(R.id.buttonLeft);
        right = v.findViewById(R.id.buttonRight);

        connect(keyer);

        new Tooltip(context).iff("onscreen_paddles_1").above(v).center().text(R.string.onscreen_paddles_tooltip).show();

        increaseButtonHeight(left);
        increaseButtonHeight(right);
    }


    private void increaseButtonHeight(Button button) {
        button.post(() -> {
            int height = button.getHeight();
            final int newHeight = (int) Math.floor((float) height * 1.2f);
            Log.i("ONSCRPAD", "Setting button height from " + height + " to " + newHeight);
            button.setHeight(newHeight);
        });
    }


    private void connect(Keyer keyer) {
        new ButtonSensor(left, keyer, PaddleKeyer.KEY_LEFT);
        new ButtonSensor(right, keyer, PaddleKeyer.KEY_RIGHT);
    }
}
