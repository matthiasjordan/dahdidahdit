package com.paddlesandbugs.dahdidahdit.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;

import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RandomTextGeneratorTest extends AbstractTextGeneratorTest {
    @Test
    public void testGenerate() {
        RandomTextGenerator sut = RandomTextGenerator.createUniformRandomTextGenerator();

        MorseCode.CharacterList res = TextTestUtils.read(sut, 10);
        System.out.println(res);

        assertTrue(res.size() <= 20);
    }


    @Test
    public void testGenerate1() {
        Set<MorseCode.CharacterData> cs = new HashSet<>();
        cs.add(MorseCode.getInstance().get("a"));
        cs.add(MorseCode.getInstance().get("b"));
        cs.add(MorseCode.getInstance().get("c"));

        Distribution<MorseCode.CharacterData> dist = RandomTextGenerator.createUniformDistribution(cs);
        RandomTextGenerator sut = new RandomTextGenerator(dist.compile());

        MorseCode.CharacterList res = TextTestUtils.read(sut, 200);
        System.out.println(res);

        assertTrue(res.size() <= 200);
        Map<MorseCode.CharacterData, Double> counts = TextTestUtils.count(res);
        assertTrue(counts.size() <= 4);
        final Set<MorseCode.CharacterData> keyset = counts.keySet();

        keyset.remove(MorseCode.getInstance().get("a"));
        keyset.remove(MorseCode.getInstance().get("b"));
        keyset.remove(MorseCode.getInstance().get("c"));
        keyset.remove(MorseCode.getInstance().get(" "));

        assertTrue(keyset.isEmpty());
    }


    @Test
    public void testWordLength() {
        testWordLength(5);
        testWordLength(7);
        testWordLength(10);
    }


    private void testWordLength(int maxWordLength) {
        RandomTextGenerator sut = RandomTextGenerator.createUniformRandomTextGenerator();
        sut.setWordLengthMax(maxWordLength);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 500);
        System.out.println(res);

        int minSeen = Integer.MAX_VALUE;
        int maxSeen = 0;

        int charsInWord = 0;
        for (MorseCode.CharacterData charD : res) {
            if (charD != MorseCode.WORDBREAK) {
                charsInWord += 1;
            } else {
                if (charsInWord < minSeen) {
                    minSeen = charsInWord;
                }

                if (charsInWord > maxSeen) {
                    maxSeen = charsInWord;
                }

                charsInWord = 0;
            }
        }

        assertEquals("min", RandomTextGenerator.MIN_WORD_LENGTH, minSeen);
        assertEquals("max", maxWordLength, maxSeen);
    }

}