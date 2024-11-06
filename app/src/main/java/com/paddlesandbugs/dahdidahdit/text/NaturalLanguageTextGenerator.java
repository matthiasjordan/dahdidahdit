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
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Generates text whose statistical properties are similar to that of the content of a given English text resource.
 */
public class NaturalLanguageTextGenerator extends AbstractTextGenerator {

    public static final int MIN_WORD_LENGTH = 2;

    public static final int WORDLIST_RESOURCE_ID = R.raw.wordlist;

    private static final Random random = new Random();

    private final Distribution.Compiled<String> distribution;

    private final Queue<MorseCode.CharacterData> characterQueue = new ArrayDeque<>();

    private int currentWordLength = 0;


    /**
     * Creates a {@link TextGenerator} that uses a distribution of 1-grams.
     *
     * @param context the {@link Context}
     */
    public NaturalLanguageTextGenerator(Context context) {
        this(context, null);
    }

    /**
     * Creates a {@link TextGenerator} that uses a distribution of 1-grams.
     *
     * @param context      the {@link Context}
     * @param permittedSet the set of characters to allow to be emitted
     */
    public NaturalLanguageTextGenerator(Context context, Set<MorseCode.CharacterData> permittedSet) {
        this(context, 1, permittedSet);
    }


    /**
     * Creates a {@link TextGenerator} that uses a distribution of n-grams.
     *
     * @param context the {@link Context}
     */
    public NaturalLanguageTextGenerator(Context context, int n) {
        this(context, n, null);
    }

    /**
     * Creates a {@link TextGenerator} that uses a distribution of n-grams.
     *
     * @param context      the {@link Context}
     * @param n            the n in n-gram
     * @param permittedSet the set of characters to allow to be emitted
     */
    public NaturalLanguageTextGenerator(Context context, int n, Set<MorseCode.CharacterData> permittedSet) {
        this(context, n, permittedSet, 5);
    }

    /**
     * Creates a {@link TextGenerator} that uses a distribution of n-grams.
     *
     * @param context         the {@link Context}
     * @param n               the n in n-gram
     * @param permittedSet    the set of characters to allow to be emitted
     * @param moreCharPercent the probability of additional characters (specials, numbers). 0 means none at all.
     */
    public NaturalLanguageTextGenerator(Context context, int n, Set<MorseCode.CharacterData> permittedSet, int moreCharPercent) {
        this.distribution = generate(context, n, permittedSet, moreCharPercent).compile();
        this.currentWordLength = getWordLength();
    }


    /**
     * Generates a distribution of n-grams.
     *
     * @param context               app context
     * @param n                     the "n" in n-gram
     * @param permittedSet          the permitted set of characters. If null, all characters are permitted.
     * @param moreCharactersPercent probability of additional characters (specials, numbers). 0 means none at all.
     * @return the distribution of n-grams
     */
    static Distribution<String> generate(Context context, int n, Set<MorseCode.CharacterData> permittedSet, int moreCharactersPercent) {
        Map<String, AtomicReference<Float>> frequencies = new HashMap<>();

        addFromWordList(context, n, permittedSet, frequencies);
        if (moreCharactersPercent > 0) {
            addMoreCharacters(frequencies, moreCharactersPercent);
        }

        final Distribution<String> dist = new Distribution<>(frequencies.keySet());
        for (Map.Entry<String, AtomicReference<Float>> entry : frequencies.entrySet()) {
            dist.setWeight(entry.getKey(), entry.getValue().get());
        }

        return dist;
    }

    private static void addMoreCharacters(Map<String, AtomicReference<Float>> frequencies, int weightInPercent) {
        final Set<MorseCode.CharacterData> specials = MorseCode.getInstance().specials;
        final Set<MorseCode.CharacterData> numbers = MorseCode.getInstance().numbers;
        Float previousTotalWeight = frequencies.values().stream().collect(Collectors.reducing(0.0f, AtomicReference::get, (a, b) -> a + b));
        // Idea: characters of the additional classes should make up weightIntPercent% of the total.
        int numAdditionalCharacters = specials.size() + numbers.size();
        float addCharsWeight = previousTotalWeight * weightInPercent / (100 - weightInPercent);
        float weightOfEachAdditionalCharacter = addCharsWeight / numAdditionalCharacters;

        for (MorseCode.CharacterData character : specials) {
            frequencies.put(character.toString(), new AtomicReference<>(weightOfEachAdditionalCharacter));
        }

        for (MorseCode.CharacterData character : numbers) {
            frequencies.put(character.toString(), new AtomicReference<>(weightOfEachAdditionalCharacter));
        }
    }

    private static void addFromWordList(Context context, int n, Set<MorseCode.CharacterData> permittedSet, Map<String, AtomicReference<Float>> frequencies) {
        try (InputStream is = context.getResources().openRawResource(WORDLIST_RESOURCE_ID); //
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8); //
             BufferedReader br = new BufferedReader(isr);) {

            List<Character> chars = new ArrayList<>();
            int charRead;
            while ((charRead = br.read()) != -1) {
                char c = (char) charRead;
                String cStr = String.valueOf(c);

                if (cStr.isBlank()) {
                    // Token ended before n-gram was finished but we only want n-grams.
                    chars.clear();
                    continue;
                }

                if ((permittedSet != null) && !permittedSet.contains(MorseCode.getInstance().get(cStr))) {
                    continue;
                }

                chars.add(c);

                if (chars.size() == n) {
                    String str = chars.stream().map(String::valueOf).collect(Collectors.joining());
                    AtomicReference<Float> freq = frequencies.get(str);
                    if (freq == null) {
                        freq = new AtomicReference<>(0.0f);
                    }
                    float current = freq.get();
                    current += 1;
                    freq.set(current);
                    frequencies.put(str, freq);
                    chars.remove(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getTextID() {
        return 0;
    }


    @Override
    public boolean hasNext() {
        refreshQueue();
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
            characterQueue.clear();
            return new TextPart(MorseCode.WORDBREAK);
        }

        MorseCode.CharacterData c = nextChar();
        currentWordLength -= 1;

        return new TextPart(c);
    }


    private MorseCode.CharacterData nextChar() {
        refreshQueue();
        return characterQueue.poll();
    }


    private void refreshQueue() {
        int attempts = 1000;
        while (characterQueue.isEmpty() && (attempts-- >= 0)) {
            String str = distribution.next();
            String[] chars = str.split("");
            for (String c : chars) {
                if (!c.isEmpty() && !c.isBlank()) {
                    final MorseCode.CharacterData characterData = MorseCode.getInstance().get(c);
                    if (characterData != null) {
                        characterQueue.add(characterData);
                    }
                }
            }
        }

        if (characterQueue.isEmpty()) {
            close();
        }
    }


    private int getWordLength() {
        int minWordLength = MIN_WORD_LENGTH;
        int i = random.nextInt(getMaxWordLength() + 1 - minWordLength);
        return minWordLength + i;
    }

}
