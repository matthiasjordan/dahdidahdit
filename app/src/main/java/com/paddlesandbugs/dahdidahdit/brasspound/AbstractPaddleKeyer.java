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

import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The paddle keyer has two keys, one for dit and one for dah
 * <p>
 * Iambic A: When keying dahdidahdit and releasing both paddles, end current sign and stop. Iambic B: When keying dahdidahdit and releasing both
 * paddles: if currently keying dit, append dah; if currently keying dah, append dit.
 */
public abstract class AbstractPaddleKeyer extends AbstractKeyer implements Keyer {


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

    private final Runnable keyerThread;

    private volatile boolean isKeyerThreadRunning = false;

    private volatile MorseTiming timing;

    protected volatile boolean isLeftDown = false;

    protected volatile boolean isRightDown = false;

    protected volatile int lastKey;

    private final int[] toneDurationsMs = new int[2];

    private final Polarity polarity;


    public AbstractPaddleKeyer(Polarity polarity) {
        this.polarity = polarity;
        setSpeed(10, 10);
        this.keyerThread = new Runnable() {

            private final Runnable innerThread = createKeyerThread();


            @Override
            public void run() {
                isKeyerThreadRunning = true;
                try {
                    innerThread.run();
                } finally {
                    isKeyerThreadRunning = false;
                }
            }
        };
    }


    @NonNull
    protected abstract Runnable createKeyerThread();


    public void setSpeed(int wpm, int effWpm) {
        timing = MorseTiming.get(wpm, effWpm);
        switch (polarity) {
            case DIT_DAH: {
                this.toneDurationsMs[KEY_LEFT] = timing.ditD;
                this.toneDurationsMs[KEY_RIGHT] = timing.dahD;
                break;
            }
            case DAH_DIT: {
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
            if (!isKeyerThreadRunning) {
                s.execute(keyerThread);
            }
        }
    }


    protected void key(int key) throws InterruptedException {
        lastKey = key;
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


}
