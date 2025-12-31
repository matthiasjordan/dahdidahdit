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


    @Test
    public void testGenerateWeighted1() {
        TextGenerator sut = RandomTextGenerator.createWeightedRandomTextGenerator("abcde");

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);
        System.out.println(map);
        assertEquals(1.0d, map.get('a') / map.get('b'), 0.2d);
        assertEquals(1.0d, map.get('a') / map.get('c'), 0.2d);
        assertEquals(1.0d, map.get('a') / map.get('d'), 0.2d);
        assertEquals(1.0d, map.get('a') / map.get('e'), 0.2d);

        assertEquals(6, map.size());
    }


    @Test
    public void testGenerateWeighted2() {
        TextGenerator sut = RandomTextGenerator.createWeightedRandomTextGenerator("abcccde");

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);
        System.out.println(map);
        assertEquals(1.0d, map.get('a') / map.get('b'), 0.2d);
        assertEquals(0.3d, map.get('a') / map.get('c'), 0.1d);
        assertEquals(1.0d, map.get('a') / map.get('d'), 0.2d);
        assertEquals(1.0d, map.get('a') / map.get('e'), 0.2d);

        assertEquals(6, map.size());
    }


    @Test
    public void testGenerateWeightedRandomized() {
        TextGenerator sut = RandomTextGenerator.createWeightedRandomTextGenerator("abcccde");
        String res = TextTestUtils.pullString(sut, 5000);
        assertTrue(res.contains("ce")); // Cannot occur if pulling in a non-randomized fashion.
    }

    @Test
    public void testGenerateWeightedEmpty1() {
        TextGenerator sut = RandomTextGenerator.createWeightedRandomTextGenerator("  ");
        assertTrue(sut instanceof StaticTextGenerator);
    }
    @Test
    public void testGenerateWeightedEmpty2() {
        TextGenerator sut = RandomTextGenerator.createWeightedRandomTextGenerator("");
        assertTrue(sut instanceof StaticTextGenerator);
    }
    @Test
    public void testGenerateWeightedEmpty3() {
        TextGenerator sut = RandomTextGenerator.createWeightedRandomTextGenerator(null);
        assertTrue(sut instanceof StaticTextGenerator);
    }


}