package com.paddlesandbugs.dahdidahdit.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class VvvKaArDecoratorTest extends AbstractTextGeneratorTest {

    @Test
    public void test0a() {
        TextGenerator delegate = new StaticTextGenerator(null);
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = read(sut, 100);
        MorseCode.CharacterList resPrinted = readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka>  <ar>"), res);
    }


    @Test
    public void test0b() {
        TextGenerator delegate = new StaticTextGenerator("");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = read(sut, 100);
        MorseCode.CharacterList resPrinted = readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka>  <ar>"), res);
    }


    @Test
    public void test() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = read(sut, 100);
        MorseCode.CharacterList resPrinted = readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka> hallo <ar>"), res);
    }


    @Test
    public void testPrinted() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("hallo"), res);
    }


    @Test
    public void testClose1() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = read(sut, 6);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka> h <ar>"), res);
    }


    @Test
    public void testClose2() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = read(sut, 9);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka> hall <ar>"), res);
    }

}
