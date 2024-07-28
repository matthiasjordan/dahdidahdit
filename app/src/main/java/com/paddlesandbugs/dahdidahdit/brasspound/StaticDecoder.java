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

import java.util.Timer;
import java.util.TimerTask;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;

/**
 * A Morse {@link Decoder} that works well if the character speed is known.
 */
public class StaticDecoder implements Decoder {

    /**
     * Multiple of a sign that the sign might be larger or smaller and is still not recognized as such. Max is sqrt(3) = 1.7.
     */
    private static final double JITTER = 1.3;
    /**
     * Fraction as 1/JITTER_FRACTION of a sign that the sign might be larger or smaller. Minimum is 1.4 (from max JITTER of sqrt(3) = 1.7).
     */
    private static final long JITTER_FRACTION = (long) (1.0d / (JITTER - 1.0d));

    private CharListener listener;

    private MorseTiming timing;

    private long epsilonDitMs;
    private long epsilonDahMs;

    private volatile long lastDownMs;
    private volatile long lastUpMs;

    private volatile int wpm;


    private final StringBuilder b = new StringBuilder();


    public StaticDecoder() {
        setSpeed(10);
    }


    public void setSpeed(int wpm) {
        this.wpm = wpm;
        timing = MorseTiming.get(wpm, wpm);
        epsilonDitMs = timing.ditD / JITTER_FRACTION;
        epsilonDahMs = timing.dahD / JITTER_FRACTION;
    }


    public int getWpm() {
        return wpm;
    }


    @Override
    public void register(CharListener l) {
        this.listener = l;
    }


    @Override
    public void keyDown() {
        lastDownMs = System.currentTimeMillis();
    }


    private boolean isTimingOk(long dMs, int m, long eMs) {
        return (dMs > (m - eMs)) && (dMs < (m + eMs));
    }


    @Override
    public void keyUp() {
        lastUpMs = System.currentTimeMillis();
        long dMs = lastUpMs - lastDownMs;

        if (isTimingOk(dMs, timing.ditD, epsilonDitMs)) {
            b.append('.');
        } else if (isTimingOk(dMs, timing.dahD, epsilonDahMs)) {
            b.append('-');
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                long pauseMs = System.currentTimeMillis() - lastUpMs;
                boolean isPause = (lastUpMs > lastDownMs);
                if (isPause && (pauseMs >= timing.charBreakD)) {
                    // Pause between signs is longer than a character break, so this character is done
                    MorseCode.CharacterData cc = MorseCode.getInstance().morseToText(b.toString());
                    if (listener != null) {
                        listener.decoded(cc);
                    }
                    b.delete(0, b.length());

                    // Now we check if this is maybe also the last character of a word
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            long pauseMs = System.currentTimeMillis() - lastUpMs;
                            boolean isPause = (lastUpMs > lastDownMs);
                            if (isPause && (pauseMs > timing.wordBreakD)) {
                                MorseCode.CharacterData cc = MorseCode.WORDBREAK;
                                if (listener != null) {
                                    listener.decoded(cc);
                                }
                                b.delete(0, b.length());
                            }
                        }
                    }, timing.wordBreakD);

                }
            }
        }, timing.charBreakD);
    }
}
