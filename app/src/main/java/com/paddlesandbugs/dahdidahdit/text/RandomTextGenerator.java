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

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Generates random characters.
 */
public class RandomTextGenerator extends AbstractTextGenerator implements TextGenerator {

    public static final int MIN_WORD_LENGTH = 2;

    private static final Random random = new Random();

    private final Distribution.Compiled<MorseCode.CharacterData> characterDistribution;

    private final int minWordLength = MIN_WORD_LENGTH;

    private int currentWordLength = 0;


    public RandomTextGenerator(Distribution.Compiled<MorseCode.CharacterData> charDist) {
        this.characterDistribution = charDist;
        this.currentWordLength = getWordLength();
    }


    public static RandomTextGenerator createUniformRandomTextGenerator() {
        Distribution<MorseCode.CharacterData> d = createUniformDistribution(MorseCode.getInstance().letters, MorseCode.getInstance().numbers);
        return new RandomTextGenerator(d.compile());
    }


    @SafeVarargs
    public static Distribution<MorseCode.CharacterData> createUniformDistribution(Set<MorseCode.CharacterData>... sets) {
        Set<MorseCode.CharacterData> events = new TreeSet<>();
        for (Set<MorseCode.CharacterData> set : sets) {
            events.addAll(set);
        }
        return new Distribution<>(events);
    }




    public static Distribution<MorseCode.CharacterData> createKochTextDistribution(CopyTrainer trainer, int kochLevel) {
        final MorseCode.CharacterList chars = trainer.getCharsFlat(kochLevel);
        Set<MorseCode.CharacterData> charSet = MorseCode.asSet(chars);
        return new Distribution<>(charSet);
    }


    public static RandomTextGenerator createKochTextGenerator(CopyTrainer trainer, int kochLevel) {
        Distribution<MorseCode.CharacterData> dist = createKochTextDistribution(trainer, kochLevel);
        return new RandomTextGenerator(dist.compile());
    }


    private MorseCode.CharacterData[] genChars(Set<MorseCode.CharacterData>... sets) {
        List<MorseCode.CharacterData> chars = new ArrayList<>();
        for (Set<MorseCode.CharacterData> set : sets) {
            chars.addAll(set);
        }

        MorseCode.CharacterData[] res = new MorseCode.CharacterData[chars.size()];
        for (int i = 0; (i < res.length); i++) {
            res[i] = chars.get(i);
        }
        return res;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_mode_random;
    }


    @Override
    public boolean hasNext() {
        return !isClosed();
    }


    @Override
    public TextPart next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (currentWordLength == 0) {
            // Next word
            currentWordLength = getWordLength();
            return new TextPart(MorseCode.WORDBREAK);
        }

        MorseCode.CharacterData c = characterDistribution.next();
        currentWordLength -= 1;

        return new TextPart(c);
    }


    private int getWordLength() {
        int i = random.nextInt(getMaxWordLength() + 1 - minWordLength);
        return minWordLength + i;
    }
}
