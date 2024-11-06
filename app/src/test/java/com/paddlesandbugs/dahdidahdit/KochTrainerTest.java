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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.KochSequence;

public class KochTrainerTest {

    private static final MorseCode.CharacterList koch0 = new MorseCode.MutableCharacterList("km");
    private static final MorseCode.CharacterList koch1 = new MorseCode.MutableCharacterList("u");
    private static final MorseCode.CharacterList koch2 = new MorseCode.MutableCharacterList("r");
    private static final MorseCode.CharacterList koch3 = new MorseCode.MutableCharacterList("e");
    private static final MorseCode.CharacterList koch4 = new MorseCode.MutableCharacterList("s");


    @Test
    public void testGetChar() {
        final KochSequence sut = new KochSequence();
        Assert.assertEquals(koch0, sut.getChar(0));
        Assert.assertEquals(koch1, sut.getChar(1));
        Assert.assertEquals(koch2, sut.getChar(2));
    }


    @Test
    public void testGetChars() {
        final KochSequence sut = new KochSequence();

        Assert.assertEquals(Arrays.asList(koch0, koch1), new CopyTrainer(null, sut, null).getChars(1));
        Assert.assertEquals(Arrays.asList(koch0, koch1, koch2, koch3, koch4), new CopyTrainer(null, sut, null).getChars(4));
    }
}
