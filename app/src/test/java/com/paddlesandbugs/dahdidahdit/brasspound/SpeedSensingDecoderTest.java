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

import org.junit.Test;
import org.mockito.Mockito;

import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;

public class SpeedSensingDecoderTest {

    private long now;


    @Test
    public void test12() {
        StaticDecoder d = Mockito.mock(StaticDecoder.class);
        SpeedSensingDecoder sut = new SpeedSensingDecoder(d) {
            @Override
            long now() {
                return now;
            }
        };

        final int wpm = 12;
        MorseTiming timing = MorseTiming.get(wpm, wpm);

        now = 10000;

        sut.keyDown();
        now += timing.ditD;     // .
        sut.keyUp();
        now += timing.signBreakD;
        sut.keyDown();
        now += timing.ditD;       // .
        sut.keyUp();
        now += timing.signBreakD;
        sut.keyDown();
        now += timing.ditD;       // .
        sut.keyUp();
        now += timing.signBreakD;
        sut.keyDown();
        now += timing.dahD;       // -
        sut.keyUp();

        System.out.println(sut.getTimings());

        Mockito.verify(d, Mockito.times(1)).setSpeed(wpm);
    }


    @Test
    public void test16() {
        StaticDecoder d = Mockito.mock(StaticDecoder.class);
        SpeedSensingDecoder sut = new SpeedSensingDecoder(d) {
            @Override
            long now() {
                return now;
            }
        };

        final int wpm = 16;
        MorseTiming timing = MorseTiming.get(wpm, wpm);

        now = 10000;

        sut.keyDown();
        now += timing.ditD;     // .
        sut.keyUp();
        now += timing.signBreakD;
        sut.keyDown();
        now += timing.ditD;       // .
        sut.keyUp();
        now += timing.signBreakD;
        sut.keyDown();
        now += timing.ditD;       // .
        sut.keyUp();
        now += timing.signBreakD;
        sut.keyDown();
        now += timing.dahD;       // -
        sut.keyUp();

        System.out.println(sut.getTimings());

        Mockito.verify(d, Mockito.times(1)).setSpeed(wpm);
    }

}
