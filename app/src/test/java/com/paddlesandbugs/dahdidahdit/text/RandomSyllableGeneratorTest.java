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
