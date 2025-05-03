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

import android.view.KeyEvent;
import android.view.MotionEvent;

public class HardwareKeySensor {

    private final int keyCode;
    private final Keyer keyer;
    private final int key;
    public final boolean isMouse;


    public HardwareKeySensor(int keyCode, Keyer keyer, int key, boolean isMouse) {
        this.keyCode = keyCode;
        this.keyer = keyer;
        this.key = key;
        this.isMouse = isMouse;
    }


    public void dispatchKeyEvent(KeyEvent e) {
        if (isMouse) {
            return;
        }

        if (e.getKeyCode() == keyCode) {
            if (e.getRepeatCount() == 0) {

                final int action = e.getAction();

                if (action == KeyEvent.ACTION_DOWN) {
                    keyer.keyDown(key);
                } else if (action == KeyEvent.ACTION_UP) {
                    keyer.keyUp(key);
                }
            }
        }
    }

    public void dispatchTouchEvent(MotionEvent e) {
        if (!isMouse) {
            return;
        }

        if ((e.getButtonState() & keyCode) > 0) {
            keyer.keyDown(key);
        }
        else {
            keyer.keyUp(key);
        }
    }
}
