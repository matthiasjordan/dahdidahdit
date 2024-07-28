package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class PeekAheadTextGeneratorTest {

    @Test
    public void test0() {
        StaticTextGenerator st = new StaticTextGenerator("abc def");
        PeekAheadTextGenerator sut = new PeekAheadTextGenerator(st);

        Assert.assertEquals("", MorseCode.getInstance().get("a"), sut.next().getChar());

        Assert.assertEquals("", MorseCode.getInstance().get("b"), sut.peek(0).getChar());
        Assert.assertEquals("", MorseCode.getInstance().get("c"), sut.peek(1).getChar());
        Assert.assertEquals("", MorseCode.getInstance().get("f"), sut.peek(5).getChar());

        Assert.assertEquals("", MorseCode.getInstance().get("b"), sut.next().getChar());
        String rest = TextTestUtils.pullString(sut, 5);
        Assert.assertEquals("", "c def", rest);

    }


    @Test
    public void test1() {
        StaticTextGenerator st = new StaticTextGenerator("abc def");
        PeekAheadTextGenerator sut = new PeekAheadTextGenerator(st);


        Assert.assertEquals("", MorseCode.getInstance().get("f"), sut.peek(6).getChar());
        Assert.assertEquals("", MorseCode.getInstance().get("b"), sut.peek(1).getChar());
        Assert.assertEquals("", MorseCode.getInstance().get("c"), sut.peek(2).getChar());

        Assert.assertEquals("", MorseCode.getInstance().get("a"), sut.next().getChar());
        Assert.assertEquals("", MorseCode.getInstance().get("b"), sut.next().getChar());
        String rest = TextTestUtils.pullString(sut, 5);
        Assert.assertEquals("", "c def", rest);

    }

}
