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

package com.paddlesandbugs.dahdidahdit.learnqcodes;

import org.junit.Assert;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class LearnModelTest {

    @Test
    public void test1a() {
        Fact f = new Fact(1, new MorseCode.MutableCharacterList("<as>"), "wait");
        DataPoint d = new DataPoint(0, DataPoint.Score.EFFORTLESS);

        LearnModel.adjust(f, d);

        Assert.assertEquals("rep", 1, f.repNo);
        Assert.assertEquals("easy", 2.6f, f.easiness, 0.05f);
        Assert.assertEquals("interval", 132000, f.intervalMs);
        Assert.assertEquals("showdate", 132000, f.nextShowDateMs);
    }


    @Test
    public void test1b() {
        Fact f = new Fact(1, new MorseCode.MutableCharacterList("<as>"), "wait");
        DataPoint d = new DataPoint(0, DataPoint.Score.WITH_EFFORT);

        LearnModel.adjust(f, d);

        Assert.assertEquals("rep", 1, f.repNo);
        Assert.assertEquals("easy", 2.5f, f.easiness, 0.05f);
        Assert.assertEquals("interval", 120000, f.intervalMs);
        Assert.assertEquals("showdate", 120000, f.nextShowDateMs);
    }

    @Test
    public void test1c() {
        Fact f = new Fact(1, new MorseCode.MutableCharacterList("<as>"), "wait");
        DataPoint d = new DataPoint(0, DataPoint.Score.NOT_AT_ALL);

        LearnModel.adjust(f, d);

        Assert.assertEquals("rep", 1, f.repNo);
        Assert.assertEquals("easy", 2.5f, f.easiness, 0.05f);
        Assert.assertEquals("interval", 120000, f.intervalMs);
        Assert.assertEquals("showdate", 120000, f.nextShowDateMs);
    }


    @Test
    public void test2a() {
        Fact f = new Fact(1, new MorseCode.MutableCharacterList("<as>"), "wait");

        LearnModel.adjust(f, new DataPoint(0, DataPoint.Score.EFFORTLESS));
        LearnModel.adjust(f, new DataPoint(130000, DataPoint.Score.EFFORTLESS));

        Assert.assertEquals("rep", 2, f.repNo);
        Assert.assertEquals("easy", 2.7f, f.easiness, 0.05f);
        Assert.assertEquals("interval", 660000, f.intervalMs);
        Assert.assertEquals("showdate", 130000 + 660000, f.nextShowDateMs);
    }

    @Test
    public void test2b() {
        Fact f = new Fact(1, new MorseCode.MutableCharacterList("<as>"), "wait");

        LearnModel.adjust(f, new DataPoint(0, DataPoint.Score.EFFORTLESS));
        LearnModel.adjust(f, new DataPoint(130000, DataPoint.Score.WITH_EFFORT));

        Assert.assertEquals("rep", 2, f.repNo);
        Assert.assertEquals("easy", 2.6f, f.easiness, 0.05f);
        Assert.assertEquals("interval", 600000, f.intervalMs);
        Assert.assertEquals("showdate", 130000 + 600000, f.nextShowDateMs);
    }

    @Test
    public void test2c() {
        Fact f = new Fact(1, new MorseCode.MutableCharacterList("<as>"), "wait");

        LearnModel.adjust(f, new DataPoint(0, DataPoint.Score.EFFORTLESS));
        LearnModel.adjust(f, new DataPoint(130000, DataPoint.Score.NOT_AT_ALL));

        Assert.assertEquals("rep", 1, f.repNo);
        Assert.assertEquals("easy", 2.6f, f.easiness, 0.05f);
        Assert.assertEquals("interval", 120000, f.intervalMs);
        Assert.assertEquals("showdate", 130000 + 120000, f.nextShowDateMs);
    }


}
