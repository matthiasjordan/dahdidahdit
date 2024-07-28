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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

/**
 * Generates words randomly chosen from a static, predefined list of words.
 */
public class RandomWordTextGenerator extends AbstractWordTextGenerator implements TextGenerator {

    private final List<String> text;

    private final Random random = new Random();


    /**
     * Creates an empty {@link RandomWordTextGenerator}.
     *
     * @param stopwords the stopwords
     *
     * @see #add(String)
     */
    public RandomWordTextGenerator(Stopwords stopwords) {
        this(stopwords, null, true);
    }


    /**
     * Creates a new {@link RandomWordTextGenerator}.
     *
     * @param stopwords the stopwords
     * @param text      the text, which will be split at white space. Or null.
     */
    public RandomWordTextGenerator(Stopwords stopwords, String text) {
        this(stopwords, text, true);
    }


    public RandomWordTextGenerator(Stopwords stopwords, String text, boolean repeat) {
        super(stopwords, repeat);
        List<String> t = new ArrayList<>();
        if (text != null) {
            t.addAll(Arrays.asList(text.split("\\s+")));
        }
        this.text = t;
    }


    /**
     * Adds a word to the random pool of the generator.
     * @param word the word to add
     */
    public void add(String word) {
        text.add(word);
    }


    /**
     * Returns the number of words in the generator.
     * @return the number of words
     */
    public int size() {
        return text.size();
    }


    /**
     * Returns the internal list for direct manipulation.
     *
     * @return the internal list
     */
    protected List<String> getList() {
        return text;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_mode_static;
    }


    @Override
    protected MorseCode.CharacterList generateNextWord() {
        int pos = random.nextInt(text.size());
        return new MorseCode.MutableCharacterList(text.get(pos));
    }

    public static RandomWordTextGenerator createSuffixGenerator() {
        return new RandomWordTextGenerator(null, "/p /a /am /mm", true);
    }

}
