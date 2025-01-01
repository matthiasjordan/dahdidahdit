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

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import com.paddlesandbugs.dahdidahdit.TestingUtils;

public class ListRandomWordTextGeneratorTest extends AbstractTextGeneratorTest {

    public static final int CHARS_TO_PULL = 20000;

    private Context context;
    private Stopwords stopwords;


    @Before
    public void setup() {
        context = TestingUtils.createContextMock();
        stopwords = Mockito.mock(Stopwords.class);
    }



    @Test
    public void testGenerateFromResources1() {
        ListRandomWordTextGenerator sut = new ListRandomWordTextGenerator(context, stopwords, 3);
        sut.setWordLengthMax(6);

        String res = TextTestUtils.pullString(sut, CHARS_TO_PULL);
        Set<String> uniqueWords = TextTestUtils.uniqueWords(res);

        Assert.assertEquals(3, uniqueWords.size());
        uniqueWords.forEach(word -> Assert.assertTrue(word.endsWith("1")));
    }


    @Test
    public void testGenerateFromResources2() {
        ListRandomWordTextGenerator sut = new ListRandomWordTextGenerator(context, stopwords, 7);
        sut.setWordLengthMax(6);

        String res = TextTestUtils.pullString(sut, CHARS_TO_PULL);
        Set<String> uniqueWords = TextTestUtils.uniqueWords(res);

        Assert.assertEquals(7, uniqueWords.size());
        AtomicBoolean found1 = new AtomicBoolean();
        AtomicBoolean found2 = new AtomicBoolean();
        uniqueWords.forEach(word -> {
            if (word.endsWith("1")) {
                found1.set(true);
            }
            if (word.endsWith("2")) {
                found2.set(true);
            }

        });
        Assert.assertTrue(found1.get());
        Assert.assertTrue(found2.get());
    }


    @Test
    public void testGenerateFromStream() {
        ListRandomWordTextGenerator sut = new ListRandomWordTextGenerator(context, stopwords, Stream.of("a", "b", "c", "d", "e"));

        String res = TextTestUtils.pullString(sut, CHARS_TO_PULL);
        Set<String> uniqueWords = TextTestUtils.uniqueWords(res);

        // Test that all words are used
        Assert.assertEquals(5, uniqueWords.size());
        Assert.assertTrue(uniqueWords.contains("a"));
        Assert.assertTrue(uniqueWords.contains("b"));
        Assert.assertTrue(uniqueWords.contains("c"));
        Assert.assertTrue(uniqueWords.contains("d"));
        Assert.assertTrue(uniqueWords.contains("e"));

        // Test that words occur in sequences other than that in the input stream
        Assert.assertTrue(res.contains("a c"));
        Assert.assertTrue(res.contains("a d"));
        Assert.assertTrue(res.contains("b a"));
        Assert.assertTrue(res.contains("d a"));
        Assert.assertTrue(res.contains("a b"));
    }


    @Test
    public void testGeneratorFromStreamWithMax1() {
        ListRandomWordTextGenerator sut = new ListRandomWordTextGenerator(context, stopwords, Stream.of("a", "b", "c", "d", "e"), 2, 5);

        String res = TextTestUtils.pullString(sut, CHARS_TO_PULL);
        Set<String> uniqueWords = TextTestUtils.uniqueWords(res);

        // Test that all of the 2 words are used
        Assert.assertEquals(2, uniqueWords.size());
    }


    @Test
    public void testGeneratorFromStreamWithMax1a() {

        Set<Set<String>> sets = new HashSet<>();
        for (int i = 0; (i < 150); i += 1) {
            ListRandomWordTextGenerator sut = new ListRandomWordTextGenerator(context, stopwords, Stream.of("a", "b", "c", "d", "e"), 2, 5);
            final String str = TextTestUtils.pullString(sut, 100);
            Set<String> uniqueWords = TextTestUtils.uniqueWords(str);
            sets.add(uniqueWords);
        }
        // Test that we get different sets out of this when we run the generator multiple times.
        Assert.assertNotEquals(1, sets.size());

        Set<String> charsSeen = new HashSet<>();
        for (Set<String> set : sets) {
            set.stream().forEach(c -> charsSeen.add(c));
        }

        Assert.assertEquals(5, charsSeen.size());
    }

}