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

package com.paddlesandbugs.dahdidahdit.text;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.TestingUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class WeightedCompoundTextGeneratorTest extends AbstractTextGeneratorTest {

    @Test
    public void testDefault() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        System.out.println(map);
        Assert.assertEquals(1.0d, map.get('a') / map.get('b'), 0.5d);
    }

    @Test
    public void test05() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(0.5d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        System.out.println(map);
        Assert.assertEquals(1.0d, map.get('a') / map.get('b'), 0.5d);
    }

    @Test
    public void test0() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(0.0d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut, 10000);

        System.out.println(map);
        Assert.assertEquals(50000.0d, map.get('a'), 2.0d);
        Assert.assertEquals(null, map.get('b'));
        Assert.assertEquals(50000.0d, map.get(' '), 2.5d);
    }

    @Test
    public void test01() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(0.1d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        System.out.println(map);
        Assert.assertEquals(10.0d, map.get('a') / map.get('b'), 2.0d);
    }



    @Test
    public void test02() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(0.2d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        System.out.println(map);
        Assert.assertEquals(4.0d, map.get('a') / map.get('b'), 1.2d);
    }

    @Test
    public void test08() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(0.8d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        System.out.println(map);
        Assert.assertEquals(0.2, map.get('a') / map.get('b'), 0.5d);
    }

    @Test
    public void test09() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(0.9d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        System.out.println(map);
        Assert.assertEquals(0.10d, map.get('a') / map.get('b'), 0.1d);
    }

    @Test
    public void test10() {
        StaticTextGenerator delegateA = new StaticTextGenerator(new MorseCode.MutableCharacterList("a"), true);
        StaticTextGenerator delegateB = new StaticTextGenerator(new MorseCode.MutableCharacterList("b"), true);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(1.0d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut, 10000);

        System.out.println(map);
        Assert.assertEquals(null, map.get('a'));
        Assert.assertEquals(50000.0d, map.get('b'), 2.0d);
        Assert.assertEquals(50000.0d, map.get(' '), 2.0d);
    }


    @Test
    public void testNatualLanguage() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(TestingUtils.fakeRawResource("aaaaabcdddeeeeeeefggghiiiijkkkklmnnnnnnnoooopqrrrssssttttuuuuuvwxyz"));
        when(context.getResources()).thenReturn(resources);

        TextGenerator delegateA = RandomTextGenerator.createUniformRandomTextGenerator();
        TextGenerator delegateB = new NaturalLanguageTextGenerator(context);
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, delegateA, delegateB);
        sut.setWeight(0.80d);

        System.out.println(TextTestUtils.pullString(delegateA, 20));
        System.out.println(TextTestUtils.pullString(delegateB, 20));
        System.out.println(TextTestUtils.pullString(sut, 20));

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        System.out.println(map);
        System.out.println("A: " + sut.getHits()[0] + " B: " + sut.getHits()[1]);
        Assert.assertTrue(sut.getHits()[0] * 3 < sut.getHits()[1]);
    }


    @Test
    public void setWeightTest1() {
        WeightedCompoundTextGenerator sut = new WeightedCompoundTextGenerator(0, null, null);

        sut.setWeight(1, 10, 1);
        Assert.assertEquals(0.0d, sut.getWeight(), 0.0d);

        sut.setWeight(1, 10, 5);
        Assert.assertEquals(0.44d, sut.getWeight(), 0.01d);

        sut.setWeight(1, 10, 9);
        Assert.assertEquals(0.88d, sut.getWeight(), 0.01d);

        sut.setWeight(1, 10, 10);
        Assert.assertEquals(1.0d, sut.getWeight(), 0.0d);
    }

}
