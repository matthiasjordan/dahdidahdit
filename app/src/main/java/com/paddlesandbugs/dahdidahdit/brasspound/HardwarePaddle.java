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

public class HardwarePaddle {

    private final HardwareKeySensor keySensorLeft;

    private final HardwareKeySensor keySensorRight;

    private boolean active = true;

    public HardwarePaddle(Keyer keyer) {
        keySensorLeft = new HardwareKeySensor(KeyEvent.KEYCODE_A, keyer, PaddleKeyer.KEY_LEFT);
        keySensorRight = new HardwareKeySensor(KeyEvent.KEYCODE_B, keyer, PaddleKeyer.KEY_RIGHT);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void handleKey(KeyEvent event) {
        if (active) {
            keySensorLeft.dispatchKeyEvent(event);
            keySensorRight.dispatchKeyEvent(event);
        }
    }
}
