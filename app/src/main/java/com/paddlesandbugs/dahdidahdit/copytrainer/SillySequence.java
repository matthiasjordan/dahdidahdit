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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class SillySequence implements LearningSequence {

    private static final String PREFS_KEY_INFIX = "silly";

    private static final List<MorseCode.CharacterList> KOCH_SEQUENCE = createKochList();

    private static final int KOCH_SEQUENCE_MAX = KOCH_SEQUENCE.size() - 1;


    private static List<MorseCode.CharacterList> createKochList() {
        final MorseCode morse = MorseCode.getInstance();

        //
        // If this is changed, make sure the setting copytraining_to_koch_level is updated to default to the new maximum.
        //

        List<MorseCode.CharacterList> list = new ArrayList<>();

        add(morse, list, "a", "b", "c");

        for (char c : "urenmktlwyvg5q92h38b".toCharArray()) {
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
