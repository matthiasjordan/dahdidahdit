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
