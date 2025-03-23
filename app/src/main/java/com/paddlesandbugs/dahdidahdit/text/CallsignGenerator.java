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

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

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

    /**
     * The callsigns known to be cool.
     */
    static final String[] coolCallsigns = new String[]{
            /* ISS. */
            "RS0ISS",
            "NA1SS",
            "DP0ISS",
            "OR4ISS",
            "IR0ISS",
            /* Organizations. */
            "4U1UN",
            "W1AW",
            /* Individuals. */
            "K4SWL",
            "N4JAW",
    };
    /**
     * The approximate percentage of cool call signs to emit.
     */
    static final int COOL_CALLSIGN_PROBABILITY_PERCENT = 1;
    private static final String LOG_TAG = "CallsignGenerator";
    private static final Random random = new Random();
    private static final MorseCode code = MorseCode.getInstance();
    /**
     * Distribution from which to draw single letters.
     */
    private final Distribution.Compiled<MorseCode.CharacterData> letters;

    /**
     * Distribution from which to draw single numbers.
     */
    private final Distribution.Compiled<MorseCode.CharacterData> numbers;

    /**
     * Distribution to draw callsign prefixes from.
     */
    private final Distribution.Compiled<String> prefixes;

    /**
     * Are cool callsigns allowed to be generated?
     */
    private boolean allowCoolCallsigns = true;

    /**
     * Create a generator that allows all letters and generates a stream of callsigns.
     *
     * @param context   the context
     * @param stopwords the stopwords
     */
    public CallsignGenerator(Context context, Stopwords stopwords) {
        this(context, stopwords, null);
    }


    /**
     * Create a generator that allows only some letters.
     *
     * @param context   the context
     * @param stopwords the stopwords
     * @param allowed   the set of allowed letters
     */
    public CallsignGenerator(Context context, Stopwords stopwords, Set<MorseCode.CharacterData> allowed) throws IllegalArgumentException {
        super(stopwords, true);

        letters = new Distribution<>(set(code.letters, allowed)).compile();
        numbers = new Distribution<>(set(code.numbers, allowed)).compile();
        prefixes = generatePrefixDistribution(context).compile();

        setAllowed(allowed);
    }

    /**
     * Sets if cool callsigns are allowed to be generated.
     * <p>
     * This is mainly intended for test usage.
     *
     * @param allowCoolCallsigns true, if cool callsigns may be generated. Else false.
     */
    public void setAllowCoolCallsigns(boolean allowCoolCallsigns) {
        this.allowCoolCallsigns = allowCoolCallsigns;
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
        int attemptsLeft = 10;

        MorseCode.CharacterList word;
        do {
            attemptsLeft -= 1;

            if (allowCoolCallsigns && random.nextInt(100) < COOL_CALLSIGN_PROBABILITY_PERCENT) {
                // With n% probability, return a cool call sign.
                word = generateCoolCallsign();
            } else {
                word = generateRandomCallsign();
            }
        }
        while ((attemptsLeft > 0) && !isAllowed(word));

        if (!isAllowed(word)) {
            word = generateRandomCallsign();
        }

        return word;
    }

    @NonNull
    static MorseCode.CharacterList generateCoolCallsign() {
        final String callsignStr = coolCallsigns[random.nextInt(coolCallsigns.length)];
        final MorseCode.MutableCharacterList callsignCL = new MorseCode.MutableCharacterList(callsignStr);
        return new MorseCode.UnmodifiableCharacterList(callsignCL);
    }

    @NonNull
    private MorseCode.CharacterList generateRandomCallsign() {
        int suffixLen = 1 + random.nextInt(3);
        final String prefixStr = prefixes.next();
        final MorseCode.CharacterList res = new MorseCode.MutableCharacterList();
        final MorseCode instance = MorseCode.getInstance();

        for (int i = 0; (i < prefixStr.length()); i++) {
            final char currentChar = prefixStr.charAt(i);
            final String currentCharStr = String.valueOf(currentChar);
            final MorseCode.CharacterData currentCharData = instance.get(currentCharStr);
            if (currentCharData != null) {
                res.add(currentCharData);
            } else {
                res.add(instance.get("x"));
            }
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
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                List<String> prefixes = PrefixExploder.explodePrefixes(line.toLowerCase().trim());
                prefixes.forEach(prefix -> distribution.setWeight(prefix, 1.0f));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.d(LOG_TAG, "Generated prefix distribution with " + distribution.size() + " values");

        return distribution;
    }

}
