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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

/**
 * Generates random strings that might be mistaken for a callsign.
 */
public class CallsignGenerator extends AbstractWordTextGenerator implements TextGenerator {

    private static final Random random = new Random();

    private static final MorseCode code = MorseCode.getInstance();

    private final Distribution.Compiled<MorseCode.CharacterData> letters;
    private final Distribution.Compiled<MorseCode.CharacterData> numbers;


    public CallsignGenerator(Stopwords stopwords) {
        this(stopwords, null);
    }


    public CallsignGenerator(Stopwords stopwords, Set<MorseCode.CharacterData> allowed) throws IllegalArgumentException {
        super(stopwords, true);

        letters = new Distribution<>(set(code.letters, allowed)).compile();
        numbers = new Distribution<>(set(code.numbers, allowed)).compile();
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

}
