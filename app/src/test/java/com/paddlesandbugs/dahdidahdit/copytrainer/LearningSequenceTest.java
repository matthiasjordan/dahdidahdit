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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

public class LearningSequenceTest {

    private static final Set<MorseCode.CharacterData> COMPLETE = new MorseCode.MutableCharacterList("abcdefghijklmnopqrstuvwxyz0123456789").asSet();

    @Test
    public void test() {
        Context context = TestingUtils.createContextMock("nx");
        for (Function<Context, CopyTrainer> provider : MainActivity.getNameToCopyTrainerProviders().values()) {
            CopyTrainer ct = provider.apply(context);
            LearningSequence sequence = ct.getSequence();
            testCompleteness(sequence);
        }
    }


    private void testCompleteness(LearningSequence sequence) {
        Set<MorseCode.CharacterData> found = new HashSet<>();
        for (int i = 0; (i <= sequence.getMax()); i += 1) {
            MorseCode.CharacterList c = sequence.getChar(i);
            found.addAll(c.asSet());
        }

        Set<MorseCode.CharacterData> rest = new HashSet<>(COMPLETE);
        rest.removeAll(found);
        Assert.assertTrue("Sequence " + sequence.getClass().getSimpleName() + " left over: " + rest, found.containsAll(COMPLETE));
    }
}
