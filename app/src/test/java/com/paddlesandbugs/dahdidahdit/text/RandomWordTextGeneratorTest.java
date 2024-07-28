package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class RandomWordTextGeneratorTest extends AbstractTextGeneratorTest {

    @Test
    public void testFilter1() {
        RandomWordTextGenerator sut = new RandomWordTextGenerator(null, "abc def ghi");

        Set<MorseCode.CharacterData> allowed = new HashSet<>();
        allowed.add(MorseCode.getInstance().get("a"));
        allowed.add(MorseCode.getInstance().get("b"));
        allowed.add(MorseCode.getInstance().get("c"));
        sut.setAllowed(allowed);

        StringBuilder b = new StringBuilder();

        int i = 0;
        while (sut.hasNext() && (i++ < 20000)) {
            MorseCode.CharacterData c = sut.next().getChar();
            b.append(c);
        }

        String res = b.toString();

        System.out.println(res);

        // Hypothesis: we find all the words in the list
        Assert.assertTrue(res.contains("abc"));
        Assert.assertFalse(res.contains("def"));
        Assert.assertFalse(res.contains("ghi"));
    }

    @Test
    public void testFilter2() {
        RandomWordTextGenerator sut = new RandomWordTextGenerator(null, "abc def ghi");

        Set<MorseCode.CharacterData> allowed = new HashSet<>();
        allowed.add(MorseCode.getInstance().get("a"));
        allowed.add(MorseCode.getInstance().get("b"));
        allowed.add(MorseCode.getInstance().get("c"));

        allowed.add(MorseCode.getInstance().get("d"));
        allowed.add(MorseCode.getInstance().get("e"));
        allowed.add(MorseCode.getInstance().get("f"));
        sut.setAllowed(allowed);

        StringBuilder b = new StringBuilder();

        int i = 0;
        while (sut.hasNext() && (i++ < 20000)) {
            MorseCode.CharacterData c = sut.next().getChar();
            b.append(c);
        }

        String res = b.toString();

        System.out.println(res);

        // Hypothesis: we find all the words in the list
        Assert.assertTrue(res.contains("abc"));
        Assert.assertTrue(res.contains("def"));
        Assert.assertFalse(res.contains("ghi"));
    }


    @Test
    public void testFilter3() {
        RandomWordTextGenerator sut = new RandomWordTextGenerator(null, "abc def ghi");

        Set<MorseCode.CharacterData> allowed = new HashSet<>();
        allowed.add(MorseCode.getInstance().get("c"));

        allowed.add(MorseCode.getInstance().get("d"));
        allowed.add(MorseCode.getInstance().get("e"));
        sut.setAllowed(allowed);

        StringBuilder b = new StringBuilder();

        Assert.assertFalse(sut.hasNext());

        int i = 0;
        while (sut.hasNext() && (i++ < 20000)) {
            MorseCode.CharacterData c = sut.next().getChar();
            b.append(c);
        }

        String res = b.toString();

        System.out.println(res);

        // Hypothesis: we find all the words in the list
        Assert.assertFalse(res.contains("abc"));
        Assert.assertFalse(res.contains("def"));
        Assert.assertFalse(res.contains("ghi"));
    }

    @Test
    public void testGenerateOnce1() {
        RandomWordTextGenerator sut = new RandomWordTextGenerator(null, "abc def ghi");

        StringBuilder b = new StringBuilder();

        int i = 0;
        while (sut.hasNext() && (i++ < 20000)) {
            MorseCode.CharacterData c = sut.next().getChar();
            b.append(c);
        }

        String res = b.toString();

        // Hypothesis: we find all the words in the list
        Assert.assertTrue(res.contains("abc"));
        Assert.assertTrue(res.contains("def"));
        Assert.assertTrue(res.contains("ghi"));

        // Hypothesis: we find the words in an order that is not in the input
        Assert.assertTrue(res.contains("abc ghi"));
        Assert.assertTrue(res.contains("def abc"));
        Assert.assertTrue(res.contains("ghi abc"));
        Assert.assertTrue(res.contains("ghi def"));
    }


    @Test
    public void textClose() {
        RandomWordTextGenerator sut = new RandomWordTextGenerator(null, "abcde fghij klmno");

        StringBuilder b = new StringBuilder();

        /*
         * First retrieve some characters from the generator.
         */
        int i = 0;
        while (sut.hasNext() && (i++ < 50)) {
            MorseCode.CharacterData c = sut.next().getChar();
            b.append(c);
        }

        /*
         * Now close the generator. We sincerely hope that the generator will stop generating letters after a few letters.
         */
        sut.close();

        /*
         * Now try to retrieve letters until the generator stops or we have retrieved enough letters to be sure the generator won't stop at all.
         */
        while (sut.hasNext() && (i++ < 400)) {
            MorseCode.CharacterData c = sut.next().getChar();
            b.append(c);
        }

        final String s = b.toString();
        System.out.println(s.length());
        Assert.assertTrue("Aborted", s.length() < 350);
        Assert.assertTrue("Ends with full word", s.endsWith(" abcde") || s.endsWith(" fghij") || s.endsWith(" klmno"));

    }
}