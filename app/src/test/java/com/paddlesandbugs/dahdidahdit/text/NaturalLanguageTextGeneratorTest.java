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

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class NaturalLanguageTextGeneratorTest extends AbstractTextGeneratorTest {


    public static final String HALLO_HELL = "hallo\nhell\n";


    /**
     * Test distribution with 1-grams.
     */
    @Test
    public void testDistribution1grams() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(fakeRawResource(HALLO_HELL));
        when(context.getResources()).thenReturn(resources);

        Distribution<String> sut = NaturalLanguageTextGenerator.generate(context, 1, null, 0);

        Map<String, Double> map = TextTestUtils.runMonteCarlo(sut.compile());

        Assert.assertEquals(4.0d, map.get("l") / map.get("e"), 1.0d);
        Assert.assertEquals(1.0d, map.get("a") / map.get("e"), 1.0d);
        Assert.assertEquals(1.0d, map.get("o") / map.get("e"), 1.0d);
        Assert.assertEquals(2.0d, map.get("h") / map.get("e"), 1.0d);
    }


    /**
     * Test distribution with 1-grams and additional characters for 5%.
     */
    @Test
    public void testDistribution1gramsWithAdditionals5() {
        runAdditionalsTest(5);
    }

    /**
     * Test distribution with 1-grams and additional characters for 1%.
     */
    @Test
    public void testDistribution1gramsWithAdditionals1() {
        runAdditionalsTest(1);
    }

    /**
     * Test distribution with 1-grams and additional characters for 1%.
     */
    @Test
    public void testDistribution1gramsWithAdditionals50() {
        runAdditionalsTest(50);
    }

    private static void runAdditionalsTest(int additionalPercentage) {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(fakeRawResource(HALLO_HELL));
        when(context.getResources()).thenReturn(resources);

        final Distribution<String> sut = NaturalLanguageTextGenerator.generate(context, 1, null, additionalPercentage);

        final Map<String, Double> map = TextTestUtils.runMonteCarlo(sut.compile());
        final HashSet<String> actualSet = new HashSet<>();
        actualSet.addAll(map.keySet());

        final Set<String> mainSet = Set.of("h", "a", "e", "l", "o");

        final Set<String> additionalSet = new HashSet<>();
        MorseCode.getInstance().numbers.stream().map(MorseCode.CharacterData::toString).forEach(additionalSet::add);
        MorseCode.getInstance().specials.stream().map(MorseCode.CharacterData::toString).forEach(additionalSet::add);

        final Set<String> expectedSet = new HashSet<>();
        expectedSet.addAll(mainSet);
        expectedSet.addAll(additionalSet);

        // Make sure all expected characters are there.
        Assert.assertEquals(expectedSet, actualSet);

        // Make sure individual character frequencies are okay.
        Assert.assertEquals(4.0d, map.get("l") / map.get("e"), 1.0d);
        Assert.assertEquals(1.0d, map.get("a") / map.get("e"), 1.0d);
        Assert.assertEquals(1.0d, map.get("o") / map.get("e"), 1.0d);
        Assert.assertEquals(2.0d, map.get("h") / map.get("e"), 1.0d);

        Assert.assertEquals(1.0d, map.get(".") / map.get("@"), 1.0d);
        Assert.assertEquals(1.0d, map.get("/") / map.get("@"), 1.0d);
        Assert.assertEquals(1.0d, map.get("=") / map.get("@"), 1.0d);

        AtomicReference<Double> mainFreq = new AtomicReference<>(0.0d);
        AtomicReference<Double> additionalFreq = new AtomicReference<>(0.0d);
        map.entrySet().forEach(e -> {
            String key = e.getKey();
            if (mainSet.contains(key)) {
                mainFreq.updateAndGet(v -> new Double((double) (v + e.getValue())));
            }
            else if (additionalSet.contains(key)) {
                additionalFreq.updateAndGet(v -> new Double((double) (v + e.getValue())));
            }
            else {
                throw new IllegalStateException("Found unexpected key "+ key + " that is neither main nor additional.");
            }
        });

        final double totalFreq = mainFreq.get() + additionalFreq.get();

        // Make sure that character frequencies over subsets (main, additional) are as specified.
        Assert.assertEquals((100.0d - (double) additionalPercentage) / 100.0d, mainFreq.get() / totalFreq, 0.05d);
        Assert.assertEquals((double) additionalPercentage /100.0d, additionalFreq.get() / totalFreq, 0.05d);
    }




    private void addToSet(Set<String> expectedSet, MorseCode.CharacterData cd) {
        final String str = cd.toString();
        expectedSet.add(str);
    }


    /**
     * Test distribution with 2-grams.
     */
    @Test
    public void testDistribution2grams() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(fakeRawResource(HALLO_HELL));
        when(context.getResources()).thenReturn(resources);

        Distribution<String> sut = NaturalLanguageTextGenerator.generate(context, 2, null, 0);

        Map<String, Double> map = TextTestUtils.runMonteCarlo(sut.compile());

        Assert.assertEquals(2.0d, map.get("ll") / map.get("lo"), 1.0d);
        Assert.assertEquals(1.0d, map.get("lo") / map.get("el"), 0.50d);
        Assert.assertEquals(1.0d, map.get("lo") / map.get("ha"), 0.50d);
        Assert.assertEquals(1.0d, map.get("lo") / map.get("he"), 0.50d);
        Assert.assertEquals(1.0d, map.get("lo") / map.get("al"), 0.50d);
    }


    /**
     * Test generator with 1-grams.
     */
    @Test
    public void testGenerator1() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(fakeRawResource(HALLO_HELL));
        when(context.getResources()).thenReturn(resources);

        NaturalLanguageTextGenerator sut = new NaturalLanguageTextGenerator(context, null);

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        Assert.assertEquals(4.0d, map.get('l') / map.get('e'), 1.0d);
        Assert.assertEquals(1.0d, map.get('a') / map.get('e'), 1.0d);
        Assert.assertEquals(1.0d, map.get('o') / map.get('e'), 1.0d);
        Assert.assertEquals(2.0d, map.get('h') / map.get('e'), 1.0d);
        Assert.assertEquals(3.0d, map.get(' ') / map.get('e'), 1.0d);
    }


    @Test
    public void testGenerator2_withPermittedSet() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(fakeRawResource(HALLO_HELL));
        when(context.getResources()).thenReturn(resources);

        Set<MorseCode.CharacterData> testSet = MorseCode.asSet("hal"); // Only h, a, l should be in the output.
        NaturalLanguageTextGenerator sut = new NaturalLanguageTextGenerator(context, 1, testSet, 0);

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        Assert.assertEquals(Set.of('h', 'a', 'l', ' '), map.keySet());
        Assert.assertEquals(4.0d, map.get('l') / map.get('a'), 1.5d);
        Assert.assertEquals(1.0d, map.get('a') / map.get('a'), 1.0d);
        Assert.assertEquals(2.0d, map.get('h') / map.get('a'), 1.0d);
        Assert.assertEquals(2.0d, map.get(' ') / map.get('a'), 1.0d);
    }

    @Test
    public void testGenerator2_withPermittedSet_withAdditionals() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(fakeRawResource(HALLO_HELL));
        when(context.getResources()).thenReturn(resources);

        Set<MorseCode.CharacterData> testSet = MorseCode.asSet("hal"); // Only h, a, l should be in the output.
        NaturalLanguageTextGenerator sut = new NaturalLanguageTextGenerator(context, 1, testSet, 20);

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);

        Assert.assertEquals(Set.of('h', 'a', 'l', ' '), map.keySet());
        Assert.assertEquals(4.0d, map.get('l') / map.get('a'), 1.5d);
        Assert.assertEquals(1.0d, map.get('a') / map.get('a'), 1.0d);
        Assert.assertEquals(2.0d, map.get('h') / map.get('a'), 1.0d);
        Assert.assertEquals(2.0d, map.get(' ') / map.get('a'), 1.0d);
    }

    @Test
    public void testGeneratorEmpty() {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).thenReturn(fakeRawResource("&ééà"));
        when(context.getResources()).thenReturn(resources);

        NaturalLanguageTextGenerator sut = new NaturalLanguageTextGenerator(context, 3, null, 0);

        Map<Character, Double> map = TextTestUtils.runMonteCarlo(sut);
        Assert.assertEquals(0, map.size());
    }

}
