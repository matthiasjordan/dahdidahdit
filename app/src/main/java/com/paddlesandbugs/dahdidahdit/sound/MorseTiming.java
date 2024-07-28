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

package com.paddlesandbugs.dahdidahdit.sound;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class MorseTiming {
    public final int ditDurationMs;
    public final int ditD;
    public final int dahD;
    public final int signBreakD;
    public final int charBreakD;
    public final int wordBreakD;


    /**
     * Creates a MorseTiming instance based on dit and pause durations in milliseconds.
     * @param ditMs
     * @param pauseDitMs
     */
    public MorseTiming(int ditMs, int pauseDitMs) {
        ditDurationMs = ditMs;
        ditD = 1 * ditDurationMs;
        dahD = 3 * ditD;
        signBreakD = ditD;
        charBreakD = 3 * pauseDitMs;
        wordBreakD = 6 * pauseDitMs + ditD;
    }


    /**
     * Creates a MorseTiming instance based on speed and effective speed in words per minute.
     * @param wpm
     * @param effWpm
     * @return the initialized instance
     */
    public static MorseTiming get(int wpm, int effWpm) {
        int ditMs = calcDitLengthMs(wpm);
        int pauseDitMs = calcEffDitLengthMs(wpm, effWpm);
        return new MorseTiming(ditMs, pauseDitMs);
    }


    private static int calcEffDitLengthMs(int wpm, int effWPM) {
        final int dit = 3;
        return (((60 * 1000 / effWPM) - (50 - (4 * dit) - (2 * dit)) * calcDitLengthMs(wpm))) / (4 * dit + 2 * dit);
    }


    private static int calcDitLengthMs(int wpm) {
        return 60 * (1000 / 50) / wpm;
    }

    public static int calcWpm(int ditMs) {
        return 60 * (1000 / 50) / ditMs;
    }


    public int calcMs(MorseCode.CharacterList chars) {
        int totalMs = 0;

        MorseCode.ExplodedCharacterList exploded = new MorseCode.ExplodedCharacterList(chars);

        for (MorseCode.CharacterData charD : exploded) {
            if (charD == MorseCode.WORDBREAK) {
                totalMs += wordBreakD;
            }
            else if (charD == MorseCode.CHARBREAK) {
                totalMs += charBreakD;
            }
            else {
                // This is a "printable" character.
                int charMs = 0;
                for (char sign : charD.getCw().toCharArray()) {
                    if (charMs != 0) {
                        charMs += signBreakD;
                    }
                    switch (sign) {
                        case '.':
                            charMs += ditD;
                            break;
                        case '-':
                            charMs += dahD;
                            break;
                    }
                }
                totalMs += charMs;
            }
        }

        return totalMs;
    }
}
