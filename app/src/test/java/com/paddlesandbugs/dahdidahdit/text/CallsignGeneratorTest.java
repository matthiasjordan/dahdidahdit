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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.res.Resources;

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
            public ByteArrayInputStream answer(InvocationOnMock invocation) throws Throwable {
                return new ByteArrayInputStream("ab\nba-bc\n".getBytes());
            }
        });
    }

    @Test
    public void testGenerate1() {
        CallsignGenerator sut = new CallsignGenerator(context, stopwords);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 3);
        System.out.println(res);

        assertTrue(res.size() >= 3);
        assertTrue(res.size() <= 6);
    }

    @Test
    public void testGenerate2() {
        CallsignGenerator sut = new CallsignGenerator(context, stopwords);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 7);
        System.out.println(res);

        assertTrue(res.size() >= 7);
        assertTrue(res.size() <= 13);
    }

    @Test
    public void testGenerateAllowed1() {
        Set<MorseCode.CharacterData> allowed = MorseCode.asSet("abc12");
        CallsignGenerator sut = new CallsignGenerator(context, stopwords, allowed);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 100);

        Set<MorseCode.CharacterData> found = new HashSet<>();
        for (MorseCode.CharacterData c : res) {
            found.add(c);
        }

        found.remove(MorseCode.WORDBREAK);

        Assert.assertEquals(allowed, found);
    }

    @Test
    public void testDistribution() {
        Distribution<String> d = CallsignGenerator.generatePrefixDistribution(context);
        System.out.println(d);

    }
}