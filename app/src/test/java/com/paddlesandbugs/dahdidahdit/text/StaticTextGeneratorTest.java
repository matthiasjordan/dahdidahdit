package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;

import static org.junit.Assert.assertEquals;

public class StaticTextGeneratorTest extends AbstractTextGeneratorTest {
    @Test
    public void testGenerateOnce1() {
        StaticTextGenerator sut = new StaticTextGenerator("f");

        MorseCode.CharacterList res = read(sut, 10);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("f"), res);
    }


    @Test
    public void testGenerateOnce2() {
        StaticTextGenerator sut = new StaticTextGenerator("foo");

        MorseCode.CharacterList res = read(sut, 2);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("fo"), res);
    }


    @Test
    public void testGenerateOnce3() {
        StaticTextGenerator sut = new StaticTextGenerator("foo");

        MorseCode.CharacterList res = read(sut, 10);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("foo"), res);
    }


    @Test
    public void testGenerateRepeat1a() {
        StaticTextGenerator sut = new StaticTextGenerator("f", true);

        MorseCode.CharacterList res = read(sut, 1);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("f"), res);
    }


    @Test
    public void testGenerateRepeat1b() {
        StaticTextGenerator sut = new StaticTextGenerator("f", true);

        MorseCode.CharacterList res = read(sut, 2);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("f "), res);
    }


    @Test
    public void testGenerateRepeat2() {
        StaticTextGenerator sut = new StaticTextGenerator("foo", true);

        MorseCode.CharacterList res = read(sut, 2);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("fo"), res);
    }


    @Test
    public void testGenerateRepeat3() {
        StaticTextGenerator sut = new StaticTextGenerator("foo", true);

        MorseCode.CharacterList res = read(sut, 10);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("foo foo fo"), res);
    }
}