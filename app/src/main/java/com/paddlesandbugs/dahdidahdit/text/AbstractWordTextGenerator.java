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

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

import java.util.Collections;
import java.util.Set;

/**
 * Generates words based on a method that returns words one by one.
 */
public abstract class AbstractWordTextGenerator extends AbstractTextGenerator implements TextGenerator {

    /**
     * How many times is {@link AbstractWordTextGenerator#generateNextWord()} called to generate a word that satisfies the requirements before the
     * text generator calls it a day.
     */
    private static final int MAX_ATTEMPTS_TO_FIND_WORD = 60;

    private final boolean repeat;
    private StaticTextGenerator staticTg;
    private final TextGenerator empty = new EmptyTextGenerator();
    private final Stopwords stopwords;

    private Set<MorseCode.CharacterData> allowed = null;


    public AbstractWordTextGenerator(Stopwords stopwords, boolean repeat) {
        this.stopwords = stopwords;
        this.repeat = repeat;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_mode_static;
    }


    @Override
    public boolean hasNext() {
        return getTG().hasNext();
    }


    @Override
    public TextPart next() {
        return getTG().next();
    }


    private TextGenerator getTG() {
        if ((staticTg == null) || (repeat && !staticTg.hasNext())) {
            // StaticTextGenerator is not there or empty but we need more text

            if (!isClosed()) {
                // We need text and this generator is not closed
                // So we generate a word, feed it into the StaticTextGenerator and use it to generate characters.
                MorseCode.CharacterList word = getNextFittingWord();
                if (word == null) {
                    // No word can be produced that satisfies all requirements.
                    return empty;
                }

                if (staticTg != null) {
                    MorseCode.CharacterList tmp = new MorseCode.MutableCharacterList();
                    tmp.add(MorseCode.WORDBREAK);
                    tmp.append(word);
                    word = tmp;
                }

                staticTg = new StaticTextGenerator(word, false);
            } else {
                // There is a request for more text but this generator is closed.
                return empty;
            }
        }

        return staticTg;
    }


    protected Set<MorseCode.CharacterData> getAllowed() {
        return allowed;
    }


    public void setAllowed(Set<MorseCode.CharacterData> allowedChars) {
        this.allowed = (allowedChars == null) ? null : Collections.unmodifiableSet(allowedChars);
    }


    protected boolean isAllowed(MorseCode.CharacterList chars) {
        if (allowed == null) {
            return true;
        }

        for (MorseCode.CharacterData c : chars) {
            if (!allowed.contains(c)) {
                return false;
            }
        }

        return true;
    }


    /**
     * @return the next word, or null, if no next word exists.
     */
    private MorseCode.CharacterList getNextFittingWord() {
        int attempts = 0;
        MorseCode.CharacterList shortest = null;
        MorseCode.CharacterList word = null;
        do {
            attempts += 1;
            MorseCode.CharacterList testWord = generateNextWord();
            if (testWord == null) {
                return null;
            }

            if (containsNull(testWord)) {
                continue;
            }

            boolean lengthOkay = lengthOkay(testWord);
            boolean filterOkay = allCharsAllowed(testWord);
            boolean stopwordsOkay = stopWordsOkay(testWord);

            if (filterOkay && stopwordsOkay) {
                if (lengthOkay) {
                    word = testWord;
                    break;
                } else {
                    if ((shortest == null) || (testWord.countChars() < shortest.countChars())) {
                        shortest = testWord;
                    }
                }
            }

        } while (attempts < MAX_ATTEMPTS_TO_FIND_WORD);

        return word;
    }


    private boolean containsNull(MorseCode.CharacterList testWord) {
        for (MorseCode.CharacterData characterData : testWord) {
            if (characterData == null) {
                return true;
            }
        }

        return false;
    }


    private boolean wordOkay(MorseCode.CharacterList word) {
        return lengthOkay(word) && allCharsAllowed(word) && stopWordsOkay(word);
    }


    private boolean stopWordsOkay(MorseCode.CharacterList testWord) {
        return (stopwords == null) || !stopwords.contains(testWord.asString());
    }


    private boolean lengthOkay(MorseCode.CharacterList word) {
        return word.countChars() <= getMaxWordLength();
    }


    protected boolean allCharsAllowed(MorseCode.CharacterList word) {
        if (allowed == null) {
            return true;
        }

        for (MorseCode.CharacterData c : word) {
            if (!allowed.contains(c)) {
                return false;
            }
        }

        return true;
    }


    /**
     * @return a generated word as a candidate for the next word, or null, if no next candidate could be found
     */
    protected abstract MorseCode.CharacterList generateNextWord();
}
