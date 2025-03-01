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
import android.view.View;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.Tooltip;

public class OnScreenPaddle {

    private final ButtonSensor leftSensor;

    private final ButtonSensor rightSensor;


    public OnScreenPaddle(Activity context, Keyer keyer) {
        leftSensor = new ButtonSensor(context, R.id.buttonLeft, keyer, PaddleKeyer.KEY_LEFT);
        rightSensor = new ButtonSensor(context, R.id.buttonRight, keyer, PaddleKeyer.KEY_RIGHT);

        View v = context.findViewById(R.id.paddleButtons);
        new Tooltip(context).iff("onscreen_paddles_1").above(v).center().text(R.string.onscreen_paddles_tooltip).show();
    }


    public void setActive(boolean active) {
        leftSensor.setActive(active);
        rightSensor.setActive(active);
    }
}
