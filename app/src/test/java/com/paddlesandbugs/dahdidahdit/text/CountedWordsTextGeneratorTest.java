package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

public class CountedWordsTextGeneratorTest {

    @Test
    public void test1() {
        StaticTextGenerator st = new StaticTextGenerator("abc def gh ijklm op");
        CountedWordsTextGenerator sut = new CountedWordsTextGenerator(st, 1);

        String actual = TextTestUtils.pullString(sut, Integer.MAX_VALUE);

        Assert.assertEquals("", "abc", actual);
    }


    @Test
    public void test2() {
        StaticTextGenerator st = new StaticTextGenerator("abc def gh ijklm op");
        CountedWordsTextGenerator sut = new CountedWordsTextGenerator(st, 2);

        String actual = TextTestUtils.pullString(sut, Integer.MAX_VALUE);

        Assert.assertEquals("", "abc def", actual);
    }


    @Test
    public void test3() {
        StaticTextGenerator st = new StaticTextGenerator("abc def gh ijklm op");
        CountedWordsTextGenerator sut = new CountedWordsTextGenerator(st, 3);

        String actual = TextTestUtils.pullString(sut, Integer.MAX_VALUE);

        Assert.assertEquals("", "abc def gh", actual);
    }


    @Test
    public void test4() {
        StaticTextGenerator st = new StaticTextGenerator("abc def gh ijklm op");
        CountedWordsTextGenerator sut = new CountedWordsTextGenerator(st, 4);

        String actual = TextTestUtils.pullString(sut, Integer.MAX_VALUE);

        Assert.assertEquals("", "abc def gh ijklm", actual);
    }


}
