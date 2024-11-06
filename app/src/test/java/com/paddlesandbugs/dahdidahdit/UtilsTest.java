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

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.paddlesandbugs.dahdidahdit.text.Stopwords;

public class UtilsTest {

    @Test
    public void testStream() throws IOException {
        Context context = TestingUtils.createContextMock();
        Stopwords stopwords = Mockito.mock(Stopwords.class);

        Stream<String> str = Utils.toStream(context, R.raw.headcopy_words_manual);

        List<String> lines = new ArrayList<>();
        str.forEach(lines::add);
        Assert.assertEquals(Arrays.asList("hallo1","foo1","bar1","baz1","radio1","test1"), lines);
    }

}
