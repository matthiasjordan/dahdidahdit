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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

/**
 * Generates random syllables based on syllable patterns.
 */
public class RandomSyllableGenerator extends AbstractWordTextGenerator {

    private Distribution.Compiled<MorseCode.CharacterData> cdist;
    private Distribution.Compiled<MorseCode.CharacterData> vdist;
    private List<MorseCode.CharacterList> prosigns;


    private List<MorseCode.CharacterList> createProsignGenerator(Set<MorseCode.CharacterData> allowed) {
        List<MorseCode.CharacterList> g = new ArrayList<>();

        add(g, new MorseCode.MutableCharacterList("cq"), allowed);

        for (MorseCode.CharacterData prosign : MorseCode.getInstance().prosigns) {
            List<MorseCode.CharacterData> l = new ArrayList<>();
            l.add(prosign);
            add(g, new MorseCode.MutableCharacterList(l), allowed);
        }
        return g;
    }


    private void add(List<MorseCode.CharacterList> g, MorseCode.MutableCharacterList cds, Set<MorseCode.CharacterData> allowed) {
        if (allowed != null) {
            for (MorseCode.CharacterData cd : cds) {
                if (!allowed.contains(cd)) {
                    return;
                }
            }
        }

        g.add(new MorseCode.UnmodifiableCharacterList(cds));
    }


    private final Random random = new Random();
    private boolean isEmpty = false;

    private enum Type {
        /**
         * A prosign or other ham code.
         */
        PROSIGN(1),
        /**
         * Vowel, Consonant.
         */
        AN(2),
        /**
         * Consonant, Vowel.
         */
        NA(2),
        /**
         * Consonant, Vowel, Consonant.
         */
        NAN(3),
        /**
         * Consonant, Vowel, Consonant, Consonant
         */
        NANN(4);

        private final int length;


        Type(int length) {
            this.length = length;
        }


        public int getLength() {
            return length;
        }
    }


    public RandomSyllableGenerator(Stopwords stopwords, boolean repeat) {
        super(stopwords, repeat);
        cdist = makeDist(MorseCode.getInstance().consonants);
        vdist = makeDist(MorseCode.getInstance().vowels);
        prosigns = createProsignGenerator(null);
    }


    private Distribution.Compiled<MorseCode.CharacterData> makeDist(Set<MorseCode.CharacterData> consonants) {
        if (consonants.size() != 0) {
            return RandomTextGenerator.createUniformDistribution(consonants).compile();
        }
        else {
            isEmpty = true;
            return null;
        }
    }


    private void createDistributions(Set<MorseCode.CharacterData> allowed) {
        cdist = makeDist(filter(MorseCode.getInstance().consonants, allowed));
        vdist = makeDist(filter(MorseCode.getInstance().vowels, allowed));
        prosigns = createProsignGenerator(allowed);
    }


    @NonNull
    private Set<MorseCode.CharacterData> filter(Set<MorseCode.CharacterData> baseSet, Set<MorseCode.CharacterData> allowed) {
        Set<MorseCode.CharacterData> set = new HashSet<>(baseSet);
        set.retainAll(allowed);
        return set;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_syllables;
    }


    @Override
    protected MorseCode.CharacterList generateNextWord() {
        if (isEmpty) {
            return null;
        }

        MorseCode.CharacterList b = new MorseCode.MutableCharacterList();
        Type t = getType();
        switch (t) {
            case PROSIGN: {
                final int size = prosigns.size();
                if (size != 0) {
                    int i = random.nextInt(size);
                    return prosigns.get(i);
                }
                // Else fall through to next case - this is not supposed to be fair randomness
            }
            case AN: {
                b.add(vdist.next());
                b.add(cdist.next());
                break;
            }
            case NA: {
                b.add(cdist.next());
                b.add(vdist.next());
                break;
            }
            case NAN: {
                b.add(cdist.next());
                b.add(vdist.next());
                b.add(cdist.next());
                break;
            }
            case NANN: {
                b.add(cdist.next());
                b.add(vdist.next());
                MorseCode.CharacterList suffix = new MorseCode.MutableCharacterList();
                final MorseCode.CharacterData plain = cdist.next();
                final MorseCode mc = MorseCode.getInstance();
                switch (plain.getPlain()) {
                    case "c":
                    case "k": {
                        suffix.add(mc.get("c"));
                        suffix.add(mc.get("k"));
                        break;
                    }
                    case "q": {
                        suffix.add(mc.get("q"));
                        suffix.add(mc.get("u"));
                        break;
                    }
                    case "y": {
                        suffix.add(mc.get("n"));
                        suffix.add(mc.get("n"));
                        break;
                    }
                    default: {
                        suffix.add(plain);
                        suffix.add(plain);
                    }
                }

                if (isAllowed(suffix)) {
                    b.append(suffix);
                } else {
                    MorseCode.CharacterData c = cdist.next();
                    b.add(c);
                    b.add(c);
                }
                break;
            }
        }

        return b;
    }


    private int getSyllableLen() {
        return Math.max(2, getMaxWordLength());
    }


    private Type getType() {
        int maxLen = getSyllableLen();
        Type res;
        do {
            final Type[] values = Type.values();
            int t = random.nextInt(values.length);
            res = values[t];
        } while (res.getLength() > maxLen);
        return res;
    }


    @Override
    public void setAllowed(Set<MorseCode.CharacterData> allowedChars) {
        super.setAllowed(allowedChars);
        createDistributions(allowedChars);
    }
}
