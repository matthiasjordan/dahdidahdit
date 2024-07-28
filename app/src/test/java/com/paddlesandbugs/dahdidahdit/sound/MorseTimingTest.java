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