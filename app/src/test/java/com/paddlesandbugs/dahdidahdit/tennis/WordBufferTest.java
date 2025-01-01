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

package com.paddlesandbugs.dahdidahdit.tennis;

import org.junit.Assert;
import org.junit.Test;

public class WordBufferTest {


    @Test
    public void test1() {
        WordBuffer sut = new WordBuffer("cq de dl4mat");

        Assert.assertEquals(true, sut.matches("cq de #"));
        Assert.assertEquals("dl4mat", sut.getMatch());
        Assert.assertEquals(true, !sut.getMatch().equals("dl4ma"));
        Assert.assertEquals(false, sut.matches("cq de foo"));
        Assert.assertEquals(false, sut.matches("cq cq de #"));
        Assert.assertEquals(null, sut.getMatch());
        Assert.assertEquals(false, sut.matches("fo1o de dl4mat"));
    }


    @Test
    public void test2() {
        WordBuffer sut = new WordBuffer("hallo hallo");

        Assert.assertEquals(true, sut.matches("# #"));
        Assert.assertEquals("hallo", sut.getMatch());
        Assert.assertEquals(true, sut.matches("hallo #"));
        Assert.assertEquals("hallo", sut.getMatch());
        Assert.assertEquals(false, sut.matches("foo #"));
        Assert.assertEquals(null, sut.getMatch());
        Assert.assertEquals(false, sut.matches("cq de #"));
        Assert.assertEquals(null, sut.getMatch());
        Assert.assertEquals(false, sut.matches("cq de foo"));
        Assert.assertEquals(false, sut.matches("cq cq de #"));
        Assert.assertEquals(false, sut.matches("fo1o de dl4mat"));
    }


    @Test
    public void test3() {
        WordBuffer sut = new WordBuffer("foo de hallo hallo");

        Assert.assertEquals(true, sut.matches("#1 de #2 #2"));
        Assert.assertEquals("foo", sut.getMatch("1"));
        Assert.assertEquals("hallo", sut.getMatch("2"));

        Assert.assertEquals(true, sut.matches("hallo #1"));
        Assert.assertEquals("hallo", sut.getMatch("1"));
    }


    @Test
    public void test4() {
        WordBuffer sut = new WordBuffer("foo de hallo hallox");

        Assert.assertEquals(false, sut.matches("#1 de #2 #2"));
        Assert.assertEquals(null, sut.getMatch("1"));
        Assert.assertEquals(null, sut.getMatch("2"));

        Assert.assertEquals(true, sut.matches("hallo #1"));
        Assert.assertEquals("hallox", sut.getMatch("1"));
    }

    @Test
    public void test5() {
        WordBuffer sut = new WordBuffer("foo de hallo hallo");

        Assert.assertEquals(true, sut.matches("#us de #dx #dx"));
        Assert.assertEquals("foo", sut.getMatch("us"));
        Assert.assertEquals("hallo", sut.getMatch("dx"));
        Assert.assertEquals("foo de hallo hallo", sut.getFullPatternMatch());


        Assert.assertEquals(true, sut.matches("hallo #1"));
        Assert.assertEquals("hallo", sut.getMatch("1"));
    }


    @Test
    public void testAddWord() {
        WordBuffer sut = new WordBuffer("");

        sut.addWord("hallo");

        Assert.assertEquals("hallo", sut.get());

        sut.addWord("word");

        Assert.assertEquals("hallo word", sut.get());

        sut.addWord("three");

        Assert.assertEquals("hallo word three", sut.get());

    }
}
