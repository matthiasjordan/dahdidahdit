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
