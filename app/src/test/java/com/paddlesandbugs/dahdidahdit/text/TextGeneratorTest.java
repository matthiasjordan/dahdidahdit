/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2024 Matthias Jordan <matthias@paddlesandbugs.com>

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TextGeneratorTest {

    @Test
    public void testFetchNextWord() {
        TextGenerator g = new RandomWordTextGenerator(null, "abc   def ghi  jkl   ");

        Set<String> fetchedWords = new HashSet<>();

        for (int i = 0; (i < 1000); i++) {
            String word = TextGenerator.fetchWord(g);
            fetchedWords.add(word);
        }

        Assert.assertEquals(new HashSet<>(Arrays.asList("abc", "def", "ghi", "jkl")), fetchedWords);
    }


    @Test
    public void testFetchNextWordEnd() {
        TextGenerator g = new RandomWordTextGenerator(null, "abc   def ghi  jkl   ");

        Set<String> fetchedWords = new HashSet<>();
        int i;
        for (i = 0; (i < 1000); i++) {
            String word = TextGenerator.fetchWord(g);
            if (word != null) {
                fetchedWords.add(word);
            } else {
                break;
            }

            if (i == 3) {
                g.close();
            }
        }

        // Close on 3,
        // fetch last word on 4
        // generator closed on 5
        Assert.assertEquals(5, i);
    }

}
