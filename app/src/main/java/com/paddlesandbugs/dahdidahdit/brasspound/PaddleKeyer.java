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

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;

/**
 * The paddle keyer has to keys, one for dit and one for dah.
 * <p>
 * Iambic A: When keying dahdidahdit and releasing both paddles, end current sign and stop. Iambic B: When keying dahdidahdit and releasing both
 * paddles: if currently keying dit, append dah; if currently keying dah, append dit.
 */
public class PaddleKeyer extends AbstractKeyer implements Keyer {


    public enum Polarity {
        /**
         * Left is dit, right is dah.
         */
        DIT_DAH,
        /**
         * Left is dah, right is dit.
         */
        DAH_DIT;
    }

    public static final int KEY_LEFT = 0;
    public static final int KEY_RIGHT = 1;


    private final Executor s = Executors.newSingleThreadExecutor();

    private final KeyerThread keyerThread;


    private volatile MorseTiming timing;

    private volatile boolean isLeftDown = false;
    private volatile boolean isRightDown = false;

    private volatile boolean isLastSignALeft = false;

    private final ReentrantLock lock = new ReentrantLock(true);

    private final int[] toneDurationsMs = new int[2];

    private final Polarity polarity;


    public PaddleKeyer(Polarity polarity) {
        this.polarity = polarity;
        setSpeed(10, 10);
        this.keyerThread = new KeyerThread();
    }


    public void setSpeed(int wpm, int effWpm) {
        timing = MorseTiming.get(wpm, effWpm);
        switch (polarity) {
            case DIT_DAH: {
                this.toneDurationsMs[KEY_LEFT] = timing.ditD;
                this.toneDurationsMs[KEY_RIGHT] = timing.dahD;
                break;
            }
            case DAH_DIT:{
                this.toneDurationsMs[KEY_LEFT] = timing.dahD;
                this.toneDurationsMs[KEY_RIGHT] = timing.ditD;
                break;
            }
        }

    }


    @Override
    public void keyDown(int keyCode) {
        switch (keyCode) {
            case KEY_LEFT: {
                isLeftDown = true;
                break;
            }
            case KEY_RIGHT: {
                isRightDown = true;
                break;
            }
        }

        if (isLeftDown || isRightDown) {
            s.execute(keyerThread);
        }
    }



    private void key(int key) throws InterruptedException {
        startKeying();
        Thread.sleep(toneDurationsMs[key]);
        stopKeying();
        Thread.sleep(timing.signBreakD);
    }


    @Override
    public void keyUp(int keyCode) {
        switch (keyCode) {
            case KEY_LEFT: {
                isLeftDown = false;
                break;
            }
            case KEY_RIGHT: {
                isRightDown = false;
                break;
            }
        }
    }


    private class KeyerThread implements Runnable {
        private boolean isRunning = true;


        @Override
        public void run() {
            while (isRunning && (isLeftDown || isRightDown)) {
                Log.i("PadKey", "running");
                try {

                    if (isLeftDown && isRightDown) {
                        if (isLastSignALeft) {
                            key(KEY_RIGHT);
                            isLastSignALeft = false;
                        } else {
                            key(KEY_LEFT);
                            isLastSignALeft = true;
                        }
                    } else if (isLeftDown) {
                        key(KEY_LEFT);
                        isLastSignALeft = true;
                    } else if (isRightDown) {
                        key(KEY_RIGHT);
                        isLastSignALeft = false;
                    }

                } catch (InterruptedException e) {
                    isRunning = false;
                }
            }
        }
    }
}
