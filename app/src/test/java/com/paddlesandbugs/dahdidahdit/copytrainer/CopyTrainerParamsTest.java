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

import static org.junit.Assert.assertEquals;

import android.content.Context;

import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.sound.MorseGenerator;
import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;
import com.paddlesandbugs.dahdidahdit.sound.TextMorseGenerator;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;

public class CopyTrainerParamsTest {


    private static final Context context = TestingUtils.createContextMock();

    @Test
    public void testCountSamples1() {
        String text = "cq cq cq de dl4mat";

        CopyTrainerParams sut = new CopyTrainerParams(context);

        testThis(text, 12, 194700);
        testThis(text, 13, 179124);
        testThis(text, 16, 146025);
    }


    private void testThis(String c1, int wpm, int expectedCount) {
        CopyTrainerParamsFaded foo = new CopyTrainerParamsFaded(context, "");
        foo.setWPM(wpm);
        foo.setEffWPM(wpm);

        MorseTiming timing = MorseTiming.get(foo.getWpm(), foo.getEffWPM());

        final int freq = 400;
        TextMorseGenerator.Config mgc = new TextMorseGenerator.Config();
        mgc.timing = timing;
        mgc.freqDit = freq;
        mgc.freqDah = freq;
        mgc.qlf = 1;

        mgc.textGen = new StaticTextGenerator(c1);
        MorseGenerator g12a = new TextMorseGenerator(mgc);

        mgc.textGen = new StaticTextGenerator(c1);
        MorseGenerator g12b = new TextMorseGenerator(mgc);

        mgc.textGen = new StaticTextGenerator(c1);
        MorseGenerator g12c = new TextMorseGenerator(mgc);

        assertEquals(expectedCount, calcSamples(g12c));
    }


    public int calcSamples(MorseGenerator mg) {
        int count = 0;
        MorseGenerator.Part part;
        while ((part = mg.generate()) != null) {
            count += part.sample.length;
        }

        return count;
    }

}