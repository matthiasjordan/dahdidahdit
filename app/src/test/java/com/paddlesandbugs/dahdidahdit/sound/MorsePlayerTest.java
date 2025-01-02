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

package com.paddlesandbugs.dahdidahdit.sound;

import org.junit.Assert;
import org.junit.Test;

public class MorsePlayerTest {

    @Test
    public void testMix0() {
        short[] sample = new short[5];
        sample[0] = 0;
        sample[1] = 100;
        sample[2] = Short.MAX_VALUE / 10;
        sample[3] = Short.MAX_VALUE / 2;
        sample[4] = Short.MAX_VALUE;
        SampleGenerator vol = new StaticSampleGenerator(Short.MAX_VALUE);

        MorsePlayer.mix(sample, vol);

        Assert.assertEquals(0, sample[0]);
        Assert.assertEquals(100, sample[1]);
        Assert.assertEquals(Short.MAX_VALUE / 10, sample[2]);
        Assert.assertEquals(Short.MAX_VALUE / 2, sample[3]);
        Assert.assertEquals(Short.MAX_VALUE, sample[4]);
    }


    @Test
    public void testMix1() {
        short[] sample = new short[5];
        sample[0] = 0;
        sample[1] = 100;
        sample[2] = Short.MAX_VALUE / 10;
        sample[3] = Short.MAX_VALUE / 2;
        sample[4] = Short.MAX_VALUE;
        SampleGenerator vol = new StaticSampleGenerator((short) (Short.MAX_VALUE / 2));

        MorsePlayer.mix(sample, vol);

        Assert.assertEquals(0, sample[0]);
        Assert.assertEquals(49, sample[1]);
        Assert.assertEquals(Short.MAX_VALUE / 10 / 2 - 1, sample[2]);
        Assert.assertEquals(Short.MAX_VALUE / 2 / 2, sample[3]);
        Assert.assertEquals(Short.MAX_VALUE / 2, sample[4]);
    }


    @Test
    public void testMix2() {
        short[] sample = new short[5];
        sample[0] = 0;
        sample[1] = 100;
        sample[2] = Short.MAX_VALUE / 10;
        sample[3] = Short.MAX_VALUE / 2;
        sample[4] = Short.MAX_VALUE;
        SampleGenerator vol = new StaticSampleGenerator((short) 0);

        MorsePlayer.mix(sample, vol);

        Assert.assertEquals(0, sample[0]);
        Assert.assertEquals(0, sample[1]);
        Assert.assertEquals(0, sample[2]);
        Assert.assertEquals(0, sample[3]);
        Assert.assertEquals(0, sample[4]);
    }

}
