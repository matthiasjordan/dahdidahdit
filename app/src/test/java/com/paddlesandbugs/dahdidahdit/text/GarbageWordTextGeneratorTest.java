package com.paddlesandbugs.dahdidahdit.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GarbageWordTextGeneratorTest extends AbstractTextGeneratorTest {
    @Test
    public void testGenerate() {
        GarbageWordGenerator sut = new GarbageWordGenerator(new Stopwords(), false);
        sut.setWordLengthMax(3);

        MorseCode.CharacterList res = read(sut, 10);
        System.out.println(res);

        assertTrue(res.size() <= 20);
    }

    private Map<MorseCode.CharacterData, Integer> count(MorseCode.CharacterList res) {
        Map<MorseCode.CharacterData, Integer> map = new HashMap<>();

        for (MorseCode.CharacterData c : res) {
            Integer count = map.get(c);
            if (count == null) {
                count = 0;
            }
            count += 1;
            map.put(c, count);
        }

        return map;
    }

}