package com.paddlesandbugs.dahdidahdit.params;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;
import com.paddlesandbugs.dahdidahdit.params.ParameterMap;

public class ParameterMapTest {

    ParameterMap sut;


    @Before
    public void setup() {
        ParameterMap sut = new ParameterMap();
        sut.put(Field.KOCH_LEVEL, 4);
        sut.put(Field.WPM, 16);

        this.sut = sut;
    }


    @Test
    public void testApply1a() {
        ParameterFader.FadeStep step = new ParameterFader.FadeStep(Field.KOCH_LEVEL, 1);

        sut.apply(step);
        Assert.assertEquals("Koch", new Integer(5), sut.get(Field.KOCH_LEVEL));
        Assert.assertEquals("WPM", new Integer(16), sut.get(Field.WPM));
        Assert.assertEquals("EffWPM", (Integer) 0, sut.get(Field.EFF_WPM));
    }


    @Test
    public void testApply1b() {
        ParameterFader.FadeStep step = new ParameterFader.FadeStep(Field.WPM, 1);

        sut.apply(step);
        Assert.assertEquals("Koch", new Integer(4), sut.get(Field.KOCH_LEVEL));
        Assert.assertEquals("WPM", new Integer(17), sut.get(Field.WPM));
        Assert.assertEquals("EffWPM", (Integer) 0, sut.get(Field.EFF_WPM));
    }


    @Test
    public void testRevert1() {
        ParameterFader.FadeStep step = new ParameterFader.FadeStep(Field.KOCH_LEVEL, 1);

        sut.apply(step);
        Assert.assertEquals("Koch", new Integer(5), sut.get(Field.KOCH_LEVEL));
        Assert.assertEquals("WPM", new Integer(16), sut.get(Field.WPM));
        Assert.assertEquals("EffWPM", (Integer) 0, sut.get(Field.EFF_WPM));

        sut.revert(step);

        Assert.assertEquals("Koch", new Integer(4), sut.get(Field.KOCH_LEVEL));
        Assert.assertEquals("WPM", new Integer(16), sut.get(Field.WPM));
        Assert.assertEquals("EffWPM", (Integer) 0, sut.get(Field.EFF_WPM));
    }


    @Test
    public void testRevert2a() {
        ParameterFader.FadeStep step = new ParameterFader.FadeStep(Field.KOCH_LEVEL, 1);

        sut.apply(step);
        sut.revert(step);

        Assert.assertEquals("Koch", new Integer(4), sut.get(Field.KOCH_LEVEL));
        Assert.assertEquals("WPM", new Integer(16), sut.get(Field.WPM));
        Assert.assertEquals("EffWPM", (Integer) 0, sut.get(Field.EFF_WPM));
    }


    @Test
    public void testRevert2b() {
        ParameterFader.FadeStep step = new ParameterFader.FadeStep(Field.WPM, 1);

        sut.apply(step);
        sut.revert(step);

        Assert.assertEquals("Koch", new Integer(4), sut.get(Field.KOCH_LEVEL));
        Assert.assertEquals("WPM", new Integer(16), sut.get(Field.WPM));
        Assert.assertEquals("EffWPM", (Integer) 0, sut.get(Field.EFF_WPM));
    }
}
