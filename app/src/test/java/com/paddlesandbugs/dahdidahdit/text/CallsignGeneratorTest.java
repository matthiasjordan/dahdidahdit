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

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.res.Resources;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

public class CallsignGeneratorTest extends AbstractTextGeneratorTest {

    private static final Stopwords stopwords = new Stopwords();

    private Context context;

    @Before
    public void setup() {
        context = Mockito.mock(Context.class);
        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(context.getResources()).thenReturn(resources);

        Mockito.when(resources.openRawResource(Mockito.anyInt())).then(new Answer<ByteArrayInputStream>() {

            @Override
            public ByteArrayInputStream answer(InvocationOnMock invocation) {
                return new ByteArrayInputStream("ab\nba-bc\n1a-1b\n".getBytes());
            }
        });
    }

    @Test
    public void testGenerate1() {
        for (int i = 0; (i < 1000); i++) {
            CallsignGenerator sut = new CallsignGenerator(context, stopwords);
            MorseCode.CharacterList res = TextTestUtils.read(sut, 3);

            assertTrue(res.size() >= 3);
            assertTrue(res.size() <= 6);
        }
    }

    @Test
    public void testGenerate2() {
        for (int i = 0; (i < 1000); i++) {
            CallsignGenerator sut = new CallsignGenerator(context, stopwords);
            MorseCode.CharacterList res = TextTestUtils.read(sut, 7);

            assertTrue(res.size() >= 7);
            assertTrue(res.size() <= 13);
        }
    }

    @Test
    public void testGenerateAllowed1() {
        Set<MorseCode.CharacterData> allowed = MorseCode.asSet("abc12");
        CallsignGenerator sut = new CallsignGenerator(context, stopwords, allowed);
        sut.setAllowCoolCallsigns(false);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 10000);

        Set<MorseCode.CharacterData> found = new HashSet<>();
        for (MorseCode.CharacterData c : res) {
            found.add(c);
        }

        found.remove(MorseCode.WORDBREAK);

        Assert.assertEquals("bogus callsign found " + res.asString(), allowed, found);
    }

    @Test
    public void testDistribution() {
        Distribution<String> d = CallsignGenerator.generatePrefixDistribution(context);
        Distribution.Compiled<String> sut = d.compile();

        Set<String> prefixesSeen = new HashSet<>();

        for (int i = 0; (i < 10000); i++) {
            prefixesSeen.add(sut.next());
        }

        Assert.assertEquals(Set.of("ab", "ba", "bb", "bc", "1a", "1b"), prefixesSeen);
    }

    @Test
    public void testCoolCallsign() {
        for (int i = 0; (i < 10000); i++) {
            MorseCode.CharacterList actual = CallsignGenerator.generateCoolCallsign();
            Assert.assertFalse("is Null", actual == null);
            Assert.assertFalse("is blank", actual.asString().isBlank());
        }
    }


    @Test
    public void testGenerateCoolPercentage() {
        final CallsignGenerator sut = new CallsignGenerator(context, stopwords);
        final String resStr = TextTestUtils.read(sut, 1000000).asString();
        final String[] callsigns = resStr.split(" +");

        final Set<String> coolSet = new HashSet<>();
        for (String coolCallsign : CallsignGenerator.coolCallsigns) {
            coolSet.add(coolCallsign.toLowerCase());
        }

        int callsignsSeen = 0;
        int coolCallsignsSeen = 0;
        for (String actual : callsigns) {
            callsignsSeen += 1;

            if (coolSet.contains(actual)) {
                coolCallsignsSeen += 1;
            }
        }

        final double percentActual = (double) coolCallsignsSeen / (double) callsignsSeen;
        final double expected = (double) CallsignGenerator.COOL_CALLSIGN_PROBABILITY_PERCENT / 100.0d;
        Assert.assertEquals("", expected, percentActual, 0.01d);
    }

}