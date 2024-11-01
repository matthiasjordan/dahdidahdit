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

import android.content.Context;
import android.content.res.Resources;

import com.paddlesandbugs.dahdidahdit.MorseCode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests {@link QSOTextGenerator}.
 */
public class QSOTextGeneratorTest extends AbstractTextGeneratorTest {

    private static final int RUNS = 100;

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
    public void testAPI() {
        for (int i = 0; (i < RUNS); i++) {
            QSOTextGenerator sut = new QSOTextGenerator(context);

            MorseCode.CharacterList actual = TextTestUtils.read(sut, 400);

            String actualStr = actual.asString();
            System.out.println("qso: " + actualStr);

            Assert.assertFalse("%", actualStr.contains("%"));
            Assert.assertTrue(actualStr.length() > 30);
        }
    }


    @Test
    public void testInternal() {
        Pattern p = Pattern.compile(".*\\bname ([a-z]++)\\b.* name \\1\\b.*");

        for (int i = 0; (i < RUNS); i++) {
            QSOTextGenerator sut = new QSOTextGenerator(context);

            String actualStr = sut.createRandomQSO();
            System.out.println("qso: " + actualStr);

            Assert.assertFalse("%", actualStr.contains("%"));
            Assert.assertTrue(actualStr.length() > 30);
            Assert.assertFalse(p.matcher(actualStr).matches()); // DX and local have different names
        }
    }


}
