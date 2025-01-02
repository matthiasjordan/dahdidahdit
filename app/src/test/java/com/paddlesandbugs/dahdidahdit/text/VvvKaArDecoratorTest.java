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

package com.paddlesandbugs.dahdidahdit.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class VvvKaArDecoratorTest extends AbstractTextGeneratorTest {

    @Test
    public void test0a() {
        TextGenerator delegate = new StaticTextGenerator(null);
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 100);
        MorseCode.CharacterList resPrinted = TextTestUtils.readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka>  <ar>"), res);
    }


    @Test
    public void test0b() {
        TextGenerator delegate = new StaticTextGenerator("");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 100);
        MorseCode.CharacterList resPrinted = TextTestUtils.readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka>  <ar>"), res);
    }


    @Test
    public void test() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 100);
        MorseCode.CharacterList resPrinted = TextTestUtils.readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka> hallo <ar>"), res);
    }


    @Test
    public void testPrinted() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = TextTestUtils.readPrinted(sut, 100);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("hallo"), res);
    }


    @Test
    public void testClose1() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 6);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka> h <ar>"), res);
    }


    @Test
    public void testClose2() {
        TextGenerator delegate = new StaticTextGenerator("hallo");
        VvvKaArDecorator sut = new VvvKaArDecorator(delegate);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 9);
        System.out.println(res);

        assertEquals(new MorseCode.MutableCharacterList("vvv<ka> hall <ar>"), res);
    }

}
