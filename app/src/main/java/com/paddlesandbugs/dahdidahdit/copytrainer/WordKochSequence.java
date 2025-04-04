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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import com.paddlesandbugs.dahdidahdit.MorseCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements the letter learning sequence of Word Koch.
 * <p>
 * See the <a href="https://gitlab.com/4ham/koch-method-real-words/-/blob/master/README.md">Word Koch Gitlab repo</a> for details.
 */
public class WordKochSequence implements LearningSequence {

    public static final String PREFS_KEY_INFIX = "wordkoch";

    private static final List<MorseCode.CharacterList> KOCH_SEQUENCE = createKochList();

    private static final int KOCH_SEQUENCE_MAX = KOCH_SEQUENCE.size() - 1;


    private static List<MorseCode.CharacterList> createKochList() {
        final MorseCode morse = MorseCode.getInstance();

        List<MorseCode.CharacterList> list = new ArrayList<>();

        add(morse, list, "a", "p", "s");


        for (char c : "1ml2eb3yhg4wc5ud6tn7kio8rf9vx0jzq/".toCharArray()) {
            add(morse, list, String.valueOf(c));
        }

        return Collections.unmodifiableList(list);
    }


    private static void add(MorseCode morse, List<MorseCode.CharacterList> list, String... ss) {
        MorseCode.CharacterList l = new MorseCode.MutableCharacterList();
        for (String s : ss) {
            l.add(morse.get(s));
        }
        list.add(new MorseCode.UnmodifiableCharacterList(l));
    }


    @Override
    public MorseCode.CharacterList getChar(int lvl) {
        lvl = Math.min(lvl, KOCH_SEQUENCE_MAX);
        return new MorseCode.MutableCharacterList(KOCH_SEQUENCE.get(lvl));
    }


    @Override
    public int getMax() {
        return KOCH_SEQUENCE_MAX;
    }


    @Override
    public String getPrefsKeyInfix() {
        return PREFS_KEY_INFIX;
    }

}
