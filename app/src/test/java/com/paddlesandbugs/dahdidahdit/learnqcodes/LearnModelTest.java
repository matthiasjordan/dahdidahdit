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
