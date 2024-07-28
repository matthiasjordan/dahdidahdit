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
