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
