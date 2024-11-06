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

import org.junit.Assert;
import org.junit.Test;

public class SoundGeneratorTest {


    @Test
    public void testWaveCount() {
        Assert.assertEquals(500, SoundGenerator.waveCount(500.0f, 1000));
        Assert.assertEquals(1000, SoundGenerator.waveCount(1000.0f, 1000));
        Assert.assertEquals(100, SoundGenerator.waveCount(1000.0f, 100));
        Assert.assertEquals(55, SoundGenerator.waveCount(550.0f, 100));
        Assert.assertEquals(6, SoundGenerator.waveCount(550.0f, 10));
        Assert.assertEquals(53, SoundGenerator.waveCount(525.0f, 100));
        Assert.assertEquals(5, SoundGenerator.waveCount(525.0f, 10));
    }


    @Test
    public void testSampleCount() {
        Assert.assertEquals(44100, SoundGenerator.sampleCount(500, 500, 44100));
        Assert.assertEquals(88200, SoundGenerator.sampleCount(500, 1000, 44100));
        Assert.assertEquals(8820, SoundGenerator.sampleCount(500, 100, 44100));
        Assert.assertEquals(882, SoundGenerator.sampleCount(500, 10, 44100));

        Assert.assertEquals(48000, SoundGenerator.sampleCount(500, 500, 48000));
        Assert.assertEquals(96000, SoundGenerator.sampleCount(500, 1000, 48000));
        Assert.assertEquals(9600, SoundGenerator.sampleCount(500, 100, 48000));
        Assert.assertEquals(960, SoundGenerator.sampleCount(500, 10, 48000));
    }


    @Test
    public void testNumSamples() {
        Assert.assertEquals(2646, SoundGenerator.numSamples(60, 44100));
        Assert.assertEquals(2646, SoundGenerator.numSamples(60, 44100));
        Assert.assertEquals(2880, SoundGenerator.numSamples(60, 48000));
    }

}
