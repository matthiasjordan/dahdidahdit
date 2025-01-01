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

public class CompoundTextGeneratorTest {

    @Test
    public void testAllNonRepeating() {
        final String text1 = "abc def ghi";
        final String text2 = "123 456 789";
        StaticTextGenerator gen1 = new StaticTextGenerator(text1);
        StaticTextGenerator gen2 = new StaticTextGenerator(text2);
        CompoundTextGenerator sut = new CompoundTextGenerator(0, gen1, gen2);

        String str = TextTestUtils.pullString(sut, 10000);

        Assert.assertTrue(str.contains("abc"));
        Assert.assertTrue(str.contains("123"));
        Assert.assertEquals(text1.length() + 1 + text2.length(), str.length());
    }

    @Test
    public void testAllNonRepeatingDifferentLength() {
        final String text1 = "abc def ghi abc def ghi abc def ghi abc def ghi abc def ghi abc def ghi abc def ghi";
        final String text2 = "123 456 789";
        StaticTextGenerator gen1 = new StaticTextGenerator(text1);
        StaticTextGenerator gen2 = new StaticTextGenerator(text2);
        CompoundTextGenerator sut = new CompoundTextGenerator(0, gen1, gen2);

        String str = TextTestUtils.pullString(sut, 10000);

        Assert.assertTrue(str.contains("abc"));
        Assert.assertTrue(str.contains("123"));
        Assert.assertEquals(text1.length() + 1 + text2.length(), str.length());
    }


    @Test
    public void testOneRepeating() {

        boolean text1Found = false;
        boolean text2Found = false;
        for (int i = 0; (i < 30); i++) {
            StaticTextGenerator gen1 = new StaticTextGenerator("abc def ghi");
            StaticTextGenerator gen2 = new StaticTextGenerator("123 456 789", true);
            CompoundTextGenerator sut = new CompoundTextGenerator(0, gen1, gen2);

            String str = TextTestUtils.pullString(sut, 100);

            Assert.assertTrue(str.contains("abc"));
            Assert.assertTrue(str.contains("123"));

            if (str.contains("abc 123")) {
                text1Found = true;
            }
            if (str.contains("def 123")) {
                text2Found = true;
            }
        }

        Assert.assertTrue(text1Found);
        Assert.assertTrue(text2Found);
    }


    @Test
    public void testTwoRepeating() {

        StaticTextGenerator gen1 = new StaticTextGenerator("abc def ghi", true);
        StaticTextGenerator gen2 = new StaticTextGenerator("123 456 789", true);
        CompoundTextGenerator sut = new CompoundTextGenerator(0, gen1, gen2);

        String str = TextTestUtils.pullString(sut, 10000);

        Assert.assertTrue(str.contains("abc"));
        Assert.assertTrue(str.contains("123"));
        Assert.assertTrue(str.contains("abc 123"));
        Assert.assertTrue(str.contains("abc 456"));
    }

}
