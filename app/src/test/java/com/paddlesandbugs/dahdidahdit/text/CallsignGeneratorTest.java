package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.text.AbstractTextGeneratorTest;
import com.paddlesandbugs.dahdidahdit.text.CallsignGenerator;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

public class CallsignGeneratorTest extends AbstractTextGeneratorTest {

    private static final Stopwords stopwords = new Stopwords();

    @Test
    public void testGenerate1() {
        CallsignGenerator sut = new CallsignGenerator(stopwords);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 3);
        System.out.println(res);

        assertTrue(res.size() >= 3);
        assertTrue(res.size() <= 6);
    }

    @Test
    public void testGenerate2() {
        CallsignGenerator sut = new CallsignGenerator(stopwords);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 7);
        System.out.println(res);

        assertTrue(res.size() >= 7);
        assertTrue(res.size() <= 13);
    }

    @Test
    public void testGenerateAllowed1() {
        Set<MorseCode.CharacterData> allowed = MorseCode.asSet("abc12");
        CallsignGenerator sut = new CallsignGenerator(stopwords, allowed);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 100);

        Set<MorseCode.CharacterData> found = new HashSet<>();
        for (MorseCode.CharacterData c : res) {
            found.add(c);
        }

        found.remove(MorseCode.WORDBREAK);

        Assert.assertEquals(allowed, found);
    }

}