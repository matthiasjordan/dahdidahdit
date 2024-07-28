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

package com.paddlesandbugs.dahdidahdit;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import com.paddlesandbugs.dahdidahdit.base.Equivalence;

public class MorseCode {

    public static final int LETTER = 0b000001;
    public static final int NUMBER = 0b000010;
    public static final int SPECIAL = 0b000100;
    public static final int PROSIGN = 0b001000;
    public static final int ACCENTED = 0b010000;
    public static final int INTERNAL = 0b100000;
    public static final int CONSONANT = 0b1000000;
    public static final int VOWEL = 0b10000000;


    /**
     * Break between words.
     */
    public static final CharacterData WORDBREAK = new CharacterData(" ", "\n", "<xwb>", R.drawable.ic_smiley, INTERNAL);

    /**
     * Break between characters. (We use "sign" for "." and "-".)
     */
    public static final CharacterData CHARBREAK = new CharacterData("", " ", "<xsb>", R.drawable.ic_smiley, INTERNAL);

    /**
     * Break between syllables.
     */
    public static final CharacterData SYLLABLEBREAK = new CharacterData("", "", "<xsyb>", R.drawable.ic_smiley, INTERNAL);

    private final Map<String, CharacterData> nameToData = new TreeMap<>();
    private final Map<String, CharacterData> morseToData = new HashMap<>();

    public final Set<CharacterData> letters;
    public final Set<CharacterData> numbers;
    public final Set<CharacterData> vowels;
    public final Set<CharacterData> consonants;
    public final Set<CharacterData> prosigns;

    private final Equivalence<CharacterData> similarCharacters = new Equivalence<>();


    private static final MorseCode instance = new MorseCode();


    private MorseCode() {
        add(new CharacterData("a", ".-", null, R.drawable.steno_a, LETTER | VOWEL));  //
        add(new CharacterData("b", "-...", null, R.drawable.steno_b, LETTER | CONSONANT));  //
        add(new CharacterData("c", "-.-.", null, R.drawable.steno_c, LETTER | CONSONANT)); //
        add(new CharacterData("d", "-..", null, R.drawable.steno_d, LETTER | CONSONANT)); //
        add(new CharacterData("e", ".", null, R.drawable.steno_e, LETTER | VOWEL)); //
        add(new CharacterData("f", "..-.", null, R.drawable.steno_f, LETTER | CONSONANT)); //
        add(new CharacterData("g", "--.", null, R.drawable.steno_g, LETTER | CONSONANT));  //
        add(new CharacterData("h", "....", null, R.drawable.steno_h, LETTER | CONSONANT));  //
        add(new CharacterData("i", "..", null, R.drawable.steno_i, LETTER | VOWEL));  //
        add(new CharacterData("j", ".---", null, R.drawable.steno_j, LETTER | CONSONANT));  //
        add(new CharacterData("k", "-.-", null, R.drawable.steno_k, LETTER | CONSONANT));  //
        add(new CharacterData("l", ".-..", null, R.drawable.steno_l, LETTER | CONSONANT));  //
        add(new CharacterData("m", "--", null, R.drawable.steno_m, LETTER | CONSONANT));  //
        add(new CharacterData("n", "-.", null, R.drawable.steno_n, LETTER | CONSONANT));  //
        add(new CharacterData("o", "---", null, R.drawable.steno_o, LETTER | VOWEL));  //
        add(new CharacterData("p", ".--.", null, R.drawable.steno_p, LETTER | CONSONANT));  //
        add(new CharacterData("q", "--.-", null, R.drawable.steno_q, LETTER | CONSONANT));  //
        add(new CharacterData("r", ".-.", null, R.drawable.steno_r, LETTER | CONSONANT));  //
        add(new CharacterData("s", "...", null, R.drawable.steno_s, LETTER | CONSONANT));  //
        add(new CharacterData("t", "-", null, R.drawable.steno_t, LETTER | CONSONANT));  //
        add(new CharacterData("u", "..-", null, R.drawable.steno_u, LETTER | VOWEL));  //
        add(new CharacterData("v", "...-", null, R.drawable.steno_v, LETTER | CONSONANT));  //
        add(new CharacterData("w", ".--", null, R.drawable.steno_w, LETTER | CONSONANT));  //
        add(new CharacterData("x", "-..-", null, R.drawable.steno_x, LETTER | CONSONANT));  //
        add(new CharacterData("y", "-.--", null, R.drawable.steno_y, LETTER | CONSONANT));  //
        add(new CharacterData("z", "--..", null, R.drawable.steno_z, LETTER | CONSONANT));  //
        add(new CharacterData("0", "-----", null, R.drawable.steno_0, NUMBER));  //
        add(new CharacterData("1", ".----", null, R.drawable.steno_1, NUMBER));  //
        add(new CharacterData("2", "..---", null, R.drawable.steno_2, NUMBER));  //
        add(new CharacterData("3", "...--", null, R.drawable.steno_3, NUMBER));  //
        add(new CharacterData("4", "....-", null, R.drawable.steno_4, NUMBER));  //
        add(new CharacterData("5", ".....", null, R.drawable.steno_5, NUMBER));  //
        add(new CharacterData("6", "-....", null, R.drawable.steno_6, NUMBER));  //
        add(new CharacterData("7", "--...", null, R.drawable.steno_7, NUMBER));  //
        add(new CharacterData("8", "---..", null, R.drawable.steno_8, NUMBER));  //
        add(new CharacterData("9", "----.", null, R.drawable.steno_9, NUMBER));  //
        add(new CharacterData(".", ".-.-.-", null, R.drawable.steno_dot, SPECIAL));  //
        add(new CharacterData(",", "--..--", null, R.drawable.steno_comma, SPECIAL));  //
        add(new CharacterData(":", "---...", null, 0, SPECIAL));  //
        add(new CharacterData("-", "-....-", null, R.drawable.steno_dash, SPECIAL));  //
        add(new CharacterData("/", "-..-.", null, R.drawable.steno_slash, SPECIAL));  //
        add(new CharacterData("=", "-...-", null, R.drawable.steno_equal, SPECIAL));  //
        add(new CharacterData("?", "..--..", null, R.drawable.steno_questionmark, SPECIAL));  //
        add(new CharacterData("@", ".--.-.", null, 0, SPECIAL));  //

        add(new CharacterData("<ar>", ".-.-.", "+", 0, PROSIGN));  //   (at the same time <ar> !)
        add(new CharacterData("<as>", ".-...", "<as>", 0, PROSIGN));  //
        add(new CharacterData("<ka>", "-.-.-", "<ka>", 0, PROSIGN));  //
        add(new CharacterData("<kn>", "-.--.", "<kn>", 0, PROSIGN));  //
        add(new CharacterData("<sk>", "...-.-", "<sk>", 0, PROSIGN));   //
        add(new CharacterData("<ve>", "...-.", "<ve>", 0, PROSIGN));  //
        add(new CharacterData("<ch>", "----", "<ch>", 0, PROSIGN));   //
        add(new CharacterData("<sos>", "...---...", "<sos>", 0, PROSIGN)); //
        add(new CharacterData("<err>", "........", "<err>", 0, PROSIGN)); //

        add(new CharacterData("ä", ".-.-", null, 0, ACCENTED));  // ae
        add(new CharacterData("ö", "---.", null, 0, ACCENTED));  // oe
        add(new CharacterData("ü", "..--", null, 0, ACCENTED));  // ue

        add(WORDBREAK);  // wordbreak

        this.letters = Collections.unmodifiableSet(collect(LETTER));
        this.numbers = Collections.unmodifiableSet(collect(NUMBER));
        this.vowels = Collections.unmodifiableSet(collect(VOWEL));
        this.consonants = Collections.unmodifiableSet(collect(CONSONANT));
        this.prosigns = Collections.unmodifiableSet(collect(PROSIGN));

        addSimilar("a", "n");
        addSimilar("b", "d");
        addSimilar("v", "u");
        addSimilar("=", "-");
        addSimilar("h", "5");
        addSimilar("4", "v");
        addSimilar("x", "=");
        addSimilar("q", "y");
        addSimilar("j", "w");
    }


    private void addSimilar(String a, String b) {
        final CharacterData t1 = get(a);
        final CharacterData t2 = get(b);
        if ((t1 != null) && (t2 != null)) {
            similarCharacters.put(t1, t2);
        }
    }


    private Set<CharacterData> collect(int flags) {
        final Set<CharacterData> toSet = new HashSet<>();
        for (CharacterData c : nameToData.values()) {
            if (c.is(flags)) {
                toSet.add(c);
            }
        }
        return toSet;
    }


    @SafeVarargs
    private final void add(CharacterData d, Set<CharacterData>... additionalSets) {
        nameToData.put(String.valueOf(d.getPlain()), d);
        morseToData.put(d.cw, d);

        if (d.prosign != null) {
            nameToData.put(d.prosign, d);
        }

        if (additionalSets != null) {
            for (Set<CharacterData> additionalSet : additionalSets) {
                additionalSet.add(d);
            }
        }
    }


    public static MorseCode getInstance() {
        return instance;
    }


    public CharacterData get(String name) {
        return nameToData.get(name);
    }


    /**
     * Returns similar characters.
     *
     * @param d the original character
     *
     * @return the set of characters similar to the original one
     */
    public Set<CharacterData> getSimilar(CharacterData d) {
        return similarCharacters.get(d);
    }


    /**
     * Converts the given plain text to Morse text in the form ".... .-".
     *
     * @param text the text to convert
     *
     * @return the dits and dahs
     */
    public static String textToMorse(String text) {
        CharacterList data = new MutableCharacterList(text);
        ExplodedCharacterList exploded = new ExplodedCharacterList(data);
        return exploded.asCWString();
    }


    /**
     * Finds the character that belongs to the morse code given in the form "-.-.".
     *
     * @param morse the morse input (a single character)
     *
     * @return the character, or null, if no corresponding character was found
     */
    public CharacterData morseToText(String morse) {
        return morseToData.get(morse);
    }


    public static Set<CharacterData> asSet(String text) {
        Set<CharacterData> set = new HashSet<>();
        for (char c : text.toCharArray()) {
            set.add(getInstance().get(String.valueOf(c)));
        }
        return set;
    }


    public static Set<CharacterData> asSet(CharacterList text) {
        Set<CharacterData> set = new HashSet<>();
        for (CharacterData c : text) {
            set.add(c);
        }
        return set;
    }


    public static Set<CharacterData> asSet(Collection<CharacterList> lists) {
        Set<CharacterData> set = new HashSet<>();
        for (CharacterList list : lists) {
            Set<CharacterData> c = asSet(list);
            set.addAll(c);
        }
        return set;
    }


    public CharacterList getCharacters() {
        CharacterList res = new MutableCharacterList();
        for (CharacterData d : nameToData.values()) {
            res.add(d);
        }
        return res;
    }


    /**
     * Counts the raw Morse characters (w/o white space and counting prosigns as one character) in the string.
     *
     * @param plainText the Morse text given as plain text string (e.g. "cq de foo <kn>")
     *
     * @return the number of actual Morse characters in the string (in the example: 8)
     */
    public static int countRawChars(CharSequence plainText) {
        final String onlyChars = plainText.toString().replaceAll("\\s+", "");
        MorseCode.CharacterList l = new MutableCharacterList(onlyChars);
        return l.size();
    }


    public static class CharacterData implements Comparable<CharacterData> {
        private final String plain; // "a"
        private final String cw; // ".-"
        private final String prosign;
        private final int flags;
        private final int imgRsrc;


        public CharacterData(String plain, String cw, String prosign, int rsrc, int flags) {
            Objects.requireNonNull(plain, "plain must not be null");
            Objects.requireNonNull(cw, "cw must not be null");
            this.plain = plain;
            this.cw = cw;
            this.prosign = prosign;
            this.imgRsrc = rsrc;
            this.flags = flags;
        }


        public String getPlain() {
            return plain;
        }


        public String getCw() {
            return cw;
        }


        public String getProsign() {
            return prosign;
        }


        public boolean is(int flag) {
            return ((this.flags & flag) != 0);
        }


        public String makeDisplayString() {
            final String nextChar = getPlain();
            final String text;
            if (is(MorseCode.LETTER)) {
                text = String.valueOf(nextChar).toUpperCase() + String.valueOf(nextChar).toLowerCase();

            } else {
                text = String.valueOf(nextChar);
            }
            return text;
        }


        @NonNull
        @Override
        public String toString() {
            return plain;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CharacterData that = (CharacterData) o;
            return Objects.equals(plain, that.plain);
        }


        @Override
        public int hashCode() {
            return Objects.hash(plain);
        }


        @Override
        public int compareTo(CharacterData o) {
            return plain.compareTo(o.plain);
        }


        public int getImage() {
            return imgRsrc;
        }
    }

    public interface CharacterList extends Iterable<MorseCode.CharacterData> {

        /**
         * Adds a character.
         *
         * @param d the character to add
         */
        void add(CharacterData d);


        /**
         * Removes the first character from the list.
         *
         * @return the removed character
         */
        CharacterData pop();


        /**
         * The size of the list
         *
         * @return the size
         */
        int size();

        /**
         * Returns the i-th character.
         *
         * @param i the index of the character to return
         *
         * @return the i-th character
         */
        CharacterData get(int i);


        /**
         * Returns a list in reversed order.
         *
         * @return the reversed list.
         */
        CharacterList reverse();

        /**
         * Appends a character list to this one.
         *
         * @param list the list to append.
         */
        void append(CharacterList list);


        /**
         * Returns an iterator over the characters in the list.
         *
         * @return the iterator
         */
        @NonNull
        Iterator<CharacterData> iterator();

        /**
         * Returns the CW representation as a string.
         * <p>
         * E.g. a list "f", "o", "o" would return ".-.. --- ---".
         *
         * @return the CW string
         */
        String asCWString();

        /**
         * Returns the characters in the list as a string.
         * <p>
         * E.g. a list "f", "o", "o" would return "foo".
         *
         * @return the string
         */
        String asString();

        /**
         * Returns the characters in the list as a set.
         *
         * @return the set containing the characters in the list.
         */
        Set<CharacterData> asSet();

        /**
         * Counts the actual characters (i.e. not those marked as {@link MorseCode#INTERNAL}.
         *
         * @return the number of "actual" characters
         */
        int countChars();
    }


    public static abstract class AbstractCharacterList implements CharacterList {
        protected List<CharacterData> data;


        public AbstractCharacterList() {
            this(null);
        }


        public AbstractCharacterList(List<CharacterData> data) {
            this.data = new ArrayList<>();
            if (data != null) {
                this.data.addAll(data);
            }
        }


        @Override
        public void add(CharacterData d) {
            data.add(d);
        }


        @Override
        public CharacterData pop() {
            if (size() != 0) {
                return data.remove(0);
            } else {
                return null;
            }
        }


        @Override
        public int size() {
            return data.size();
        }


        @Override
        public CharacterData get(int i) {
            return data.get(i);
        }


        @Override
        public CharacterList reverse() {
            ArrayList<CharacterData> newList = new ArrayList<>(data.size());
            newList.addAll(data);
            Collections.reverse(newList);
            return create(newList);
        }


        @Override
        public void append(CharacterList s) {
            for (CharacterData c : s) {
                add(c);
            }
        }


        protected abstract CharacterList create(List<CharacterData> data);


        @NonNull
        @Override
        public Iterator<CharacterData> iterator() {
            return data.iterator();
        }


        @NonNull
        @Override
        public String toString() {
            return "AbstractCharacterList{" + "data=" + data + '}';
        }


        public String asCWString() {
            StringBuilder b = new StringBuilder();

            for (CharacterData d : this) {
                b.append(d.cw);
            }
            return b.toString();
        }


        public String asString() {
            StringBuilder b = new StringBuilder();

            for (CharacterData d : this) {
                b.append(d.getPlain());
            }
            return b.toString();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !CharacterList.class.isAssignableFrom(o.getClass())) {
                return false;
            }

            CharacterList that = (CharacterList) o;
            if (size() != that.size()) {
                return false;
            }

            Iterator<CharacterData> it = iterator();
            Iterator<CharacterData> thatIt = that.iterator();
            while (it.hasNext() && thatIt.hasNext()) {
                CharacterData cd = it.next();
                CharacterData thatCd = thatIt.next();
                if (!cd.equals(thatCd)) {
                    return false;
                }
            }

            return true;
        }


        @Override
        public int hashCode() {
            return Objects.hash(data);
        }


        @Override
        public int countChars() {
            int chars = 0;
            for (CharacterData cd : data) {
                if (!cd.is(MorseCode.INTERNAL)) {
                    chars += 1;
                }
            }
            return chars;
        }


        @Override
        @NonNull
        public Set<MorseCode.CharacterData> asSet() {
            Set<MorseCode.CharacterData> set = new HashSet<>();
            for (CharacterData characterData : this) {
                set.add(characterData);
            }
            return set;
        }

    }


    private static class UnmodifiableIterator<T> implements Iterator<T> {
        private final Iterator<T> delegate;


        public UnmodifiableIterator(Iterator<T> delegate) {
            this.delegate = delegate;
        }


        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }


        @Override
        public T next() {
            return delegate.next();
        }


        @Override
        public void remove() {
            //
        }

    }

    /**
     * A list of Morse characters.
     */
    public static class MutableCharacterList extends AbstractCharacterList {

        public MutableCharacterList() {
            super();
        }


        /**
         * Creates a {@link MutableCharacterList from the given list of CharacterData}.
         *
         * @param data the base list
         */
        public MutableCharacterList(List<CharacterData> data) {
            super(data);
        }


        /**
         * Creates a {@link MutableCharacterList} from the given {@link CharacterList}.
         *
         * @param data the base list
         */
        public MutableCharacterList(CharacterList data) {
            super();
            for (CharacterData cd : data) {
                add(cd);
            }
        }


        /**
         * Creates a new {@link MutableCharacterList} object from the given string, taking prosigns into account.
         *
         * @param text the string
         */
        public MutableCharacterList(String text) {
            text = text.toLowerCase();
            List<CharacterData> res = new ArrayList<>(text.length());
            for (int i = 0; (i < text.length()); i++) {
                String name = text.substring(i, i + 1);
                if ("<".equals(name)) {
                    Res r = findProsign(text, i);
                    name = r.proSign;
                    i = r.endPos;
                } else if ("|".equals((name))) {
                    res.add(MorseCode.SYLLABLEBREAK);
                }
                CharacterData data = MorseCode.getInstance().get(name);
                if (data != null) {
                    res.add(data);
                }
            }
            this.data = res;
        }


        private Res findProsign(String text, int startPos) {
            Res res = new Res();
            int endPos = text.indexOf('>', startPos);
            if (endPos == -1) {
                res.proSign = text.substring(startPos, startPos + 1);
                res.endPos = startPos;
            } else {
                res.proSign = text.substring(startPos, endPos + 1);
                res.endPos = endPos;
            }
            return res;
        }


        public CharacterList reverse() {
            return super.reverse();
        }


        @Override
        protected CharacterList create(List<CharacterData> data) {
            return new MutableCharacterList(data);
        }


        public static CharacterList create(CharacterData... charData) {
            return new MutableCharacterList(Arrays.asList(charData));
        }


        public ExplodedCharacterList explode() {
            return new ExplodedCharacterList(this);
        }

        public void clear() {
            data.clear();
        }

        private static class Res {
            String proSign;
            int endPos;
        }
    }

    public static class UnmodifiableCharacterList implements CharacterList {
        private final CharacterList delegate;


        public UnmodifiableCharacterList(CharacterList delegate) {
            this.delegate = delegate;
        }


        @Override
        public void add(CharacterData d) {
            throw new UnsupportedOperationException("Unmodifiable");
        }


        @Override
        public CharacterData pop() {
            throw new UnsupportedOperationException("Unmodifiable");
        }


        @Override
        public int size() {
            return delegate.size();
        }


        @Override
        public CharacterData get(int i) {
            return delegate.get(i);
        }


        @Override
        public CharacterList reverse() {
            return delegate.reverse();
        }


        @Override
        public void append(CharacterList s) {
            throw new UnsupportedOperationException("Unmodifiable");
        }


        @NonNull
        @Override
        public Iterator<CharacterData> iterator() {
            return new UnmodifiableIterator<>(delegate.iterator());
        }


        @Override
        public String asCWString() {
            return delegate.asCWString();
        }


        @Override
        public String asString() {
            return delegate.asString();
        }


        @Override
        public int countChars() {
            return delegate.countChars();
        }


        @Override
        public Set<CharacterData> asSet() {
            return delegate.asSet();
        }


        @NonNull
        @Override
        public String toString() {
            return delegate.toString();
        }
    }


    public static class ExplodedCharacterList extends AbstractCharacterList {

        public ExplodedCharacterList(List<CharacterData> data) {
            super(data);
        }


        public ExplodedCharacterList(CharacterList data) {
            this.data = explode(data);
        }


        @Override
        protected ExplodedCharacterList create(List<CharacterData> data) {
            return new ExplodedCharacterList(data);
        }


        private List<CharacterData> explode(CharacterList data) {
            ArrayList<CharacterData> res = new ArrayList<>();
            CharacterData last = null;
            for (CharacterData d : data) {
                if ((res.size() != 0) && (d != WORDBREAK) && (last != WORDBREAK)) {
                    res.add(CHARBREAK);
                }
                res.add(d);
                last = d;
            }
            return res;
        }

    }

}
