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

import androidx.annotation.NonNull;

/**
 * Iambic B: When keying dahdidahdit and releasing both paddles:
 * if currently keying dit, append dah; if currently keying dah, append dit.
 */
public class IambicBKeyer extends AbstractPaddleKeyer implements Keyer {


    public IambicBKeyer(Polarity polarity) {
        super(polarity);
    }


    @NonNull
    @Override
    protected Runnable createKeyerThread() {
        return new KeyerThread();
    }


    private class KeyerThread implements Runnable {
        private boolean isRunning;
        private boolean isBothKeysPressed = false;


        @Override
        public void run() {
            isRunning = true;

            while (isRunning && (isLeftDown || isRightDown)) {
                try {

                    if (isLeftDown && isRightDown) {
                        isBothKeysPressed = true;
                        if (lastKey == KEY_LEFT) {
                            key(KEY_RIGHT);
                        } else {
                            key(KEY_LEFT);
                        }
                    } else if (isLeftDown) {
                        isBothKeysPressed = false;
                        key(KEY_LEFT);
                    } else if (isRightDown) {
                        isBothKeysPressed = false;
                        key(KEY_RIGHT);
                    }

                } catch (InterruptedException e) {
                    isRunning = false;
                    Thread.currentThread().interrupt();
                }
            }

            if (isBothKeysPressed && isRunning) {
                try {
                    if (lastKey == KEY_LEFT) {
                        key(KEY_RIGHT);
                    } else {
                        key(KEY_LEFT);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            isRunning = false;
        }
    }
}
