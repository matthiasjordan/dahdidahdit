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

import java.util.Random;

import com.paddlesandbugs.dahdidahdit.MorseCode;

/**
 * TextGenerator that generates words from randomly selected delegate {@link TextGenerator}s.
 */
public class CompoundTextGenerator implements TextGenerator {

    private final int textId;
    private final TextGenerator[] generators;

    private StaticTextGenerator staticTextGenerator;
    private final Random random = new Random();


    /**
     * Creates a new generator.
     *
     * @param textId the ID of the text resource to use to describe the generator
     * @param generators the delegate generators
     */
    public CompoundTextGenerator(int textId, TextGenerator... generators) {
        this.textId = textId;
        this.generators = generators;
    }


    @Override
    public int getTextID() {
        return textId;
    }


    @Override
    public void close() {
        for (TextGenerator generator : generators) {
            generator.close();
        }
    }


    @Override
    public void setWordLengthMax(int maxWordLength) {
        for (TextGenerator generator : generators) {
            generator.setWordLengthMax(maxWordLength);
        }
    }


    private TextGenerator getGenerator() {
        if ((staticTextGenerator == null) || !staticTextGenerator.hasNext()) {

            MorseCode.CharacterList word = new MorseCode.MutableCharacterList();

            if (staticTextGenerator != null) {
                word.add(MorseCode.WORDBREAK);
            }

            TextGenerator tg = chooseGenerator();
            if (!tg.hasNext()) {
                // We only have empty generators left. Time to wrap it up.
                return tg;
            }

            while (tg.hasNext()) {
                TextPart tp = tg.next();
                final MorseCode.CharacterData aChar = tp.getChar();
                if (!aChar.is(MorseCode.INTERNAL)) {
                    word.add(aChar);
                }
                if (aChar.equals(MorseCode.WORDBREAK)) {
                    break;
                }
            }


            staticTextGenerator = new StaticTextGenerator(word, false);
        }

        return staticTextGenerator;
    }


    private TextGenerator chooseGenerator() {
        int attempts = 20;
        int current = random.nextInt(generators.length);

        TextGenerator generator;
        do {
            generator = generators[current];
        } while ((!generator.hasNext() && (attempts-- > 0)));

        // Maximal 20 attempts later we have generator that might be finished. Let's see.

        if (!generator.hasNext()) {
            for (TextGenerator gen : generators) {
                generator = gen;
                if (generator.hasNext()) {
                    // Got one!
                    break;
                }
            }
        }

        return generator;
    }


    @Override
    public boolean hasNext() {
        return getGenerator().hasNext();
    }


    @Override
    public TextPart next() {
        return getGenerator().next();
    }
}
