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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class MorseTimingTest {
    @Test
    public void testGetDitLengthMs() {
        assertEquals(60, MorseTiming.get(20, 20).ditD);
        assertEquals(180, MorseTiming.get(20, 20).charBreakD);
        assertEquals(100, MorseTiming.get(12, 12).ditD);
        assertEquals(300, MorseTiming.get(12, 12).charBreakD);
        assertEquals(92, MorseTiming.get(13, 13).ditD);
        assertEquals(276, MorseTiming.get(13, 13).charBreakD);
        assertEquals(75, MorseTiming.get(16, 16).ditD);
        assertEquals(225, MorseTiming.get(16, 16).charBreakD);
    }


    @Test
    public void testGetEffDitLengthMs() {
        assertEquals(75, MorseTiming.get(16, 10).ditD);
        assertEquals(600, MorseTiming.get(16, 10).charBreakD);
    }

    @Test
    public void testCalcMs() {
        assertEquals(10320, MorseTiming.get(5, 5).calcMs(new MorseCode.MutableCharacterList("paris")));
        final int wordBreakAt5WPM = 7 * 240;
        assertEquals(60_000 - wordBreakAt5WPM, MorseTiming.get(5, 5).calcMs(new MorseCode.MutableCharacterList("paris paris paris paris paris")));
        assertEquals(4300, MorseTiming.get(12, 12).calcMs(new MorseCode.MutableCharacterList("paris")));
    }

}