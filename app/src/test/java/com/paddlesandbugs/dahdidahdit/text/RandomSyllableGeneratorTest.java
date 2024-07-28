package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class RandomSyllableGeneratorTest {

    private static final int COUNT = 100_000;

    @Test
    public void test0() {
        final Set<MorseCode.CharacterData> allowed = MorseCode.asSet("km");

        RandomSyllableGenerator sut = new RandomSyllableGenerator(null, false);
        sut.setAllowed(allowed);

        Assert.assertFalse(sut.hasNext());

        StringBuilder b = new StringBuilder();

        for (int i = 0; (i < COUNT); i++) {
            if (sut.hasNext()) {
                b.append(sut.generateNextWord().asString()).append(" ");
            }
        }

        String generated = b.toString();
        Assert.assertTrue(generated.isEmpty());
        Assert.assertFalse(sut.hasNext());
    }


    @Test
    public void test1() {
        final Set<MorseCode.CharacterData> allowed = MorseCode.asSet("abc");

        RandomSyllableGenerator sut = new RandomSyllableGenerator(null, false);
        sut.setAllowed(allowed);

        StringBuilder b = new StringBuilder();

        for (int i = 0; (i < COUNT); i++) {
            if (sut.hasNext()) {
                b.append(sut.generateNextWord().asString()).append(" ");
            }
        }

        String generated = b.toString();
        System.out.println(generated);

        Assert.assertTrue(generated.length() > COUNT);

        for (MorseCode.CharacterData ch : allowed) {
            generated = generated.replace(ch.getPlain(), "");
        }
        generated = generated.replaceAll(" ", "").trim();
        System.out.println("Cleaned: " + generated);
        Assert.assertTrue(generated.isEmpty());


    }
}
