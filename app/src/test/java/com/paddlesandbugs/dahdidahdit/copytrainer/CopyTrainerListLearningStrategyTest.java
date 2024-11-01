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

import org.junit.Ignore;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextTestUtils;

public class CopyTrainerListLearningStrategyTest {
    @Ignore
    @Test
    public void test() {
        Context context = TestingUtils.createContextMock("wordkoch");
        CopyTrainerListLearningStrategy sut = new CopyTrainerListLearningStrategy(context);

        TextGenerator textGen = sut.createTextGenerator();

        String res = TextTestUtils.pullString(textGen, 1000);
        System.out.println(res);
    }
}
