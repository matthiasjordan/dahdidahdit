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

import android.database.Cursor;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class RssTextGeneratorTest {

    @Test
    public void test() {
        Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(cursor.getColumnIndex("id")).thenReturn(0);
        Mockito.when(cursor.getColumnIndex("title")).thenReturn(1);
        Mockito.when(cursor.getColumnIndex("text")).thenReturn(2);

        Mockito.when(cursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(cursor.getInt(0)).thenReturn(1).thenReturn(2).thenReturn(3);
        Mockito.when(cursor.getString(1)).thenReturn("title 1").thenReturn("title 2").thenReturn("title 3");
        Mockito.when(cursor.getString(2)).thenReturn("text 1").thenReturn("text 2").thenReturn("text 3");


        RssTextGenerator sut = new RssTextGenerator(cursor, null);

        StringBuilder b = new StringBuilder();
        while (sut.hasNext()) {
            final TextGenerator.TextPart next = sut.next();
            b.append(next.getChar());
        }

        Assert.assertEquals("title 1 = text 1 = title 2 = text 2 = title 3 = text 3", b.toString());

    }
}
