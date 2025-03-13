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
 * Iambic A: When keying dahdidahdit and releasing both paddles, end current sign and stop.
 */
public class IambicAKeyer extends AbstractPaddleKeyer implements Keyer {


    public IambicAKeyer(Polarity polarity) {
        super(polarity);
    }


    @NonNull
    protected Runnable createKeyerThread() {
        return new KeyerThread();
    }


    protected class KeyerThread implements Runnable {
        private boolean isRunning;


        @Override
        public void run() {
            isRunning = true;

            while (isRunning && (isLeftDown || isRightDown)) {
                try {

                    if (isLeftDown && isRightDown) {
                        if (lastKey == KEY_LEFT) {
                            key(KEY_RIGHT);
                        } else {
                            key(KEY_LEFT);
                        }
                    } else if (isLeftDown) {
                        key(KEY_LEFT);
                    } else if (isRightDown) {
                        key(KEY_RIGHT);
                    }

                } catch (InterruptedException e) {
                    isRunning = false;
                }
            }
        }
    }
}
