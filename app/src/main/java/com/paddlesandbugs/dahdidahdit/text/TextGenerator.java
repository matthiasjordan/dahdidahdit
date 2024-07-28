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

import androidx.annotation.NonNull;

import java.util.Iterator;

import com.paddlesandbugs.dahdidahdit.MorseCode;

/**
 * Generates text that does only contain actual characters.
 * <p>
 * This is a one-off generator w/o reset.
 */
public interface TextGenerator extends Iterator<TextGenerator.TextPart> {

    class TextPart {

        private final MorseCode.CharacterData delegate;

        private boolean isPrinted = true;


        public TextPart(MorseCode.CharacterData wrapped) {
            this.delegate = wrapped;
        }


        public MorseCode.CharacterData getChar() {
            return delegate;
        }


        public boolean isPrinted() {
            return isPrinted;
        }


        public void setIsPrinted(boolean isPrinted) {
            this.isPrinted = isPrinted;
        }


        @NonNull
        @Override
        public String toString() {
            return (isPrinted) ? delegate.getPlain() : "<" + delegate.getPlain() + ">";
        }
    }

    /**
     * @return the resource ID for the text describing this {@link TextGenerator}
     */
    int getTextID();

    /**
     * Marks the {@link TextGenerator} as closed. This allows implementations to keep sending the rest of the current "thing" (e.g. callsign) and then
     * to cease operation.
     */
    void close();

    /**
     * Sets the maximal length of generated words.
     *
     * @param maxWordLength the maximal number of characters in a word
     */
    void setWordLengthMax(int maxWordLength);

    /**
     * Fetches a full word from the generator - i.e. a string of characters up to, but not including, the next word break.
     *
     * @param g the {@link TextGenerator} to fetch a word from
     * @return the next word, or null, if the generator does not have any more characters to offer
     */
    static String fetchWord(TextGenerator g) {
        StringBuilder b = new StringBuilder();

        boolean isInWord = false;

        while (g.hasNext()) {
            TextPart p = g.next();
            MorseCode.CharacterData d = p.getChar();
            if (MorseCode.WORDBREAK.equals(d)) {
                if (isInWord) {
                    break;
                }
            } else {
                b.append(d.getPlain());
                isInWord = true;
            }
        }

        final String s = b.toString();
        return s.isEmpty() ? null : s;
    }
}
