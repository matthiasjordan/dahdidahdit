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
