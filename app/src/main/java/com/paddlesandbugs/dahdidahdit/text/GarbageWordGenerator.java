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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates words that consist of random characters.
 */
public class GarbageWordGenerator extends AbstractWordTextGenerator {

    private final Random random = new Random();
    private Distribution.Compiled<MorseCode.CharacterData> d;
    private boolean isEmpty = false;


    public GarbageWordGenerator(Stopwords stopwords, boolean repeat) {
        super(stopwords, repeat);

        createDistribution(null);
    }

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

    private Distribution.Compiled<MorseCode.CharacterData> makeDist(Set<MorseCode.CharacterData> consonants) {
        if (consonants.size() != 0) {
            return RandomTextGenerator.createUniformDistribution(consonants).compile();
        } else {
            isEmpty = true;
            return null;
        }
    }


    private void createDistribution(Set<MorseCode.CharacterData> allowed) {
        Set<MorseCode.CharacterData> set = new HashSet<>();
        set.addAll(MorseCode.getInstance().letters);
        set.addAll(MorseCode.getInstance().numbers);
        if (allowed != null) {
            set.retainAll(allowed);
        }

        d = RandomTextGenerator.createUniformDistribution(set).compile();
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

        final int len = Math.max(2, getMaxWordLength());
        MorseCode.CharacterList b = new MorseCode.MutableCharacterList();
        for (int i = 0; (i < len); i++) {
            b.add(d.next());
        }

        return b;
    }


    @Override
    public void setAllowed(Set<MorseCode.CharacterData> allowedChars) {
        super.setAllowed(allowedChars);
        createDistribution(allowedChars);
    }
}
