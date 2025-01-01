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

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextTestUtils {

    public static final int MAX_CHARS_OUTPUT = 100;

    private static final int RUNS = 10000000;

    private static String previousCharacters;

    public static String getPreviousCharacters() {
        return previousCharacters;
    }

    /**
     * Gathers unique words from the string, splitting at space characters.
     *
     * @param str the input string
     *
     * @return a set with the words in that string
     */
    @NonNull
    public static Set<String> uniqueWords(String str) {
        String[] words = str.split(" +");
        Set<String> uniqueWords = new HashSet<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                uniqueWords.add(word.trim());
            }
        }
        final String uniqStr = uniqueWords.toString();
        System.out.println(uniqStr.substring(0, Math.min(uniqStr.length(), MAX_CHARS_OUTPUT)));
        return uniqueWords;
    }


    /**
     * Pulls a string of the given length from the generator.
     *
     * @param generator   the generator
     * @param charsToPull how many characters to pull
     *
     * @return the string
     */
    @NonNull
    public static String pullString(TextGenerator generator, int charsToPull) {
        StringBuilder b = new StringBuilder();

        int i = 0;
        while (generator.hasNext()) {
            MorseCode.CharacterData c = generator.next().getChar();
            if ((i++ >= charsToPull) && c.equals(MorseCode.WORDBREAK)) {
                break;
            }
            b.append(c);
        }

        String res = b.toString();
        System.out.println("Chars pulled: " + res.substring(0, Math.min(res.length(), MAX_CHARS_OUTPUT)));
        return res;
    }


    /**
     * Pulls a string of the given length from the distribution.
     *
     * @param distribution the distribution
     * @param charsToPull  how many characters to pull
     *
     * @return the string
     */
    @NonNull
    public static String pullString(Distribution<String> distribution, int charsToPull) {
        Distribution.Compiled<String> compiled = distribution.compile();
        StringBuilder b = new StringBuilder();

        for (int i = 0; (i < charsToPull); i++) {
            String c = compiled.next();
            b.append(c);
        }

        String res = b.toString();
        System.out.println("Chars pulled: " + res.substring(0, Math.min(res.length(), MAX_CHARS_OUTPUT)));
        return res;
    }


    public static MorseCode.CharacterList read(TextGenerator gen, int count) {
        MorseCode.CharacterList res = new MorseCode.MutableCharacterList();
        int i = 0;
        while (gen.hasNext()) {
            res.add(gen.next().getChar());

            if (++i == count) {
                gen.close();
            }
        }
        return res;
    }


    public static List<String> read(Distribution<String> distribution, int ngramsToPull) {
        Distribution.Compiled<String> compiled = distribution.compile();
        ArrayList<String> res = new ArrayList<>();

        for (int i = 0; (i < ngramsToPull); i++) {
            String c = compiled.next();
            res.add(c);
        }
        return res;
    }


    public static MorseCode.CharacterList readPrinted(TextGenerator gen, int count) {
        MorseCode.CharacterList res = new MorseCode.MutableCharacterList();
        int i = 0;
        while (gen.hasNext()) {
            final TextGenerator.TextPart textPart = gen.next();
            if (textPart.isPrinted()) {
                res.add(textPart.getChar());
            }

            if (++i == count) {
                gen.close();
            }
        }
        return res;
    }


    public static Map<MorseCode.CharacterData, Double> count(MorseCode.CharacterList res) {
        Map<MorseCode.CharacterData, Double> map = new HashMap<>();

        for (MorseCode.CharacterData c : res) {
            Double count = map.get(c);
            if (count == null) {
                count = 0.0d;
            }
            count += 1;
            map.put(c, count);
        }

        return map;
    }


    public static <T> Map<T, Double> count(Collection<T> res) {
        Map<T, Double> map = new HashMap<>();

        for (T c : res) {
            Double count = map.get(c);
            if (count == null) {
                count = 0.0d;
            }
            count += 1;
            map.put(c, count);
        }

        return map;
    }


    public static <T> Map<T, Double> runMonteCarlo(Distribution.Compiled<T> sut) {
        return runMonteCarlo(sut, RUNS);
    }


    public static <T> Map<T, Double> runMonteCarlo(Distribution.Compiled<T> sut, int runs) {
        StringBuilder str = new StringBuilder();

        Map<T, Double> res = new HashMap<>();
        for (int i = 0; (i < runs); i++) {
            T event = sut.next();

            str.append(event.toString());

            Double old = res.get(event);
            if (old == null) {
                old = new Double(0);
            }
            old += 1;
            res.put(event, old);
        }

        previousCharacters = str.toString();
        return res;
    }


    public static <T> Map<Character, Double> runMonteCarlo(TextGenerator sut) {
        return runMonteCarlo(sut, RUNS);
    }


    public static <T> Map<Character, Double> runMonteCarlo(TextGenerator sut, int runs) {
        String res = TextTestUtils.pullString(sut, 100000);

        final List<Character> chars = new ArrayList<>();
        for (char c : res.toCharArray()) {
            chars.add(c);
        }

        previousCharacters = res;
        return TextTestUtils.count(chars);
    }


}
