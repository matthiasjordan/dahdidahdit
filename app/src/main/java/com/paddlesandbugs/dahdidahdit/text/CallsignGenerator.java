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

import android.content.Context;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates random strings that might be mistaken for a callsign.
 */
public class CallsignGenerator extends AbstractWordTextGenerator implements TextGenerator {

    private static final Random random = new Random();

    private static final MorseCode code = MorseCode.getInstance();

    private final Distribution.Compiled<MorseCode.CharacterData> letters;
    private final Distribution.Compiled<MorseCode.CharacterData> numbers;
    private final Distribution.Compiled<String> prefixes;


    public CallsignGenerator(Context context, Stopwords stopwords) {
        this(context, stopwords, null);
    }


    public CallsignGenerator(Context context, Stopwords stopwords, Set<MorseCode.CharacterData> allowed) throws IllegalArgumentException {
        super(stopwords, true);

        letters = new Distribution<>(set(code.letters, allowed)).compile();
        numbers = new Distribution<>(set(code.numbers, allowed)).compile();
        prefixes = generatePrefixDistribution(context).compile();
    }


    private Set<MorseCode.CharacterData> set(Set<MorseCode.CharacterData> base, Set<MorseCode.CharacterData> allowed) {
        if (allowed == null) {
            return base;
        }

        Set<MorseCode.CharacterData> res = new HashSet<>(base);
        res.retainAll(allowed);
        return res;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_mode_callsigns;
    }


    @Override
    protected MorseCode.CharacterList generateNextWord() {
        int prefixLen = 1 + random.nextInt(2);
        int suffixLen = 1 + random.nextInt(3);

        MorseCode.CharacterList res = new MorseCode.MutableCharacterList();

        for (int i = 0; (i < prefixLen); i++) {
            res.add(letters.next());
        }

        res.add(numbers.next());

        for (int i = 0; (i < suffixLen); i++) {
            res.add(letters.next());
        }

        return res;
    }

    static Distribution<String> generatePrefixDistribution(Context context) {
        Distribution<String> distribution = new Distribution<>();

        try (InputStream is = context.getResources().openRawResource(R.raw.itu_prefixes); //
             InputStreamReader isr = new InputStreamReader(is); //
             BufferedReader br = new BufferedReader(isr);) {

            String line;
            while ((line = br.readLine()) != null) {
                List<String> prefixes = PrefixExploder.explodePrefixes(line.trim());

                prefixes.forEach(prefix -> distribution.setWeight(prefix, 1.0f));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return distribution;
    }

}
