package com.paddlesandbugs.dahdidahdit.text;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class TextTestUtils {

    public static final int MAX_CHARS_OUTPUT = 80;


    /**
     * Gathers unique words from the string, splitting at space characters.
     *
     * @param str the input string
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
     * @param generator the generator
     * @param charsToPull  how many characters to pull
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

}
