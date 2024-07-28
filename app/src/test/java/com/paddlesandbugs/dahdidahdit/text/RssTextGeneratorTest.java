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
