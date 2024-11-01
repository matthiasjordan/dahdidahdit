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

        MorseCode.CharacterList res = TextTestUtils.read(sut, 10);
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