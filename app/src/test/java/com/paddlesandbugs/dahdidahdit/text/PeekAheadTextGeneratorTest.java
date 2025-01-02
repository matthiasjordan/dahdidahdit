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
