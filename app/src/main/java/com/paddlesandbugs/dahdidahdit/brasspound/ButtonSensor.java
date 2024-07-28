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

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ButtonSensor {

    private final Button button;
    private final Keyer keyer;
    private final int key;


    public ButtonSensor(Button button, Keyer keyer, int key) {
        this.button = button;
        button.setOnTouchListener(new ButtonOnTouchListener());
        this.keyer = keyer;
        this.key = key;
    }


    private class ButtonOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (keyer != null) {
                    keyer.keyDown(key);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (keyer != null) {
                    keyer.keyUp(key);
                }
            }

            return false;
        }
    }
}