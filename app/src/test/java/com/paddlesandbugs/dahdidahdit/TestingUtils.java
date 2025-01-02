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

package com.paddlesandbugs.dahdidahdit;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestingUtils {

    public static Context createContextMock() {
        return createContextMock("koch");
    }


    public static Activity createActivityMock() {
        return createActivityMock("koch");
    }


    /**
     * Creates a {@link Context} mock.
     *
     * @param sequence the name of the learning sequence that the preferences are supposed to return for "learning_strategy".
     * @return
     */
    public static Context createContextMock(String sequence) {
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.headcopy_words_manual)).thenReturn(new ByteArrayInputStream("hallo1\nfoo1\nbar1\nbaz1\nradio1\ntest1".getBytes(StandardCharsets.UTF_8)));
        when(resources.openRawResource(R.raw.headcopy_words_2000)).thenReturn(new ByteArrayInputStream("hallo2\nfoo2\nbar2\nbaz2\nradio2\ntest2".getBytes(StandardCharsets.UTF_8)));

        when(resources.getString(R.string.action_mopp_morsetennis_intro)).thenReturn("intro");
        SharedPreferences prefs = mock(SharedPreferences.class);
        when(prefs.getString(eq("learning_strategy"), anyString())).thenReturn(sequence);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);

        when(context.getResources()).thenReturn(resources);
        return context;
    }


    public static Activity createActivityMock(String sequence) {
        Activity context = mock(Activity.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.headcopy_words_manual)).thenReturn(new ByteArrayInputStream("hallo1\nfoo1\nbar1\nbaz1\nradio1\ntest1".getBytes(StandardCharsets.UTF_8)));
        when(resources.openRawResource(R.raw.headcopy_words_2000)).thenReturn(new ByteArrayInputStream("hallo2\nfoo2\nbar2\nbaz2\nradio2\ntest2".getBytes(StandardCharsets.UTF_8)));

        when(resources.getString(R.string.action_mopp_morsetennis_intro)).thenReturn("intro");
        SharedPreferences prefs = mock(SharedPreferences.class);
        when(prefs.getString(eq("learning_strategy"), anyString())).thenReturn(sequence);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);

        when(context.getResources()).thenReturn(resources);
        return context;
    }


    @NonNull
    public static String toHexString(byte[] res) {
        StringBuilder s = new StringBuilder();
        for (byte b : res) {
            s.append(String.format("%02x", b));
        }
        final String f = s.toString();
        return f;
    }


    @NonNull
    public static byte[] toBytes(int[] data) {
        byte[] bytes = new byte[data.length];
        for (int i = 0; (i < data.length); i++) {
            bytes[i] = (byte) data[i];
        }
        return bytes;
    }


    /**
     * Simple fake for a raw resource.
     *
     * @param content the content to make the fake resource to contain
     * @return the faked resource stream
     */
    @NonNull
    public static ByteArrayInputStream fakeRawResource(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Fake that returns a fresh stream each time the resource is opened.
     *
     * @param content the content to make the fake resource to contain
     * @return an {@link Answer} containing the faked resource stream
     */
    @NonNull
    public static Answer<ByteArrayInputStream> fakeRawResourceMulti(final String content) {
        return new Answer<>() {

            @Override
            public ByteArrayInputStream answer(InvocationOnMock invocation) {
                return new ByteArrayInputStream(content.getBytes());
            }
        };
    }


    /**
     * Fake that returns a fresh stream each time the resource is opened.
     *
     * @param fileName the file name of the content to make the fake resource to contain, relative to the root of the JAR/APK files (i.g. if you want R.raw.foo, you pass "/raw/foo").
     * @return an {@link Answer} containing the faked resource stream
     */
    @NonNull
    public static Answer<InputStream> fakeRawFileResourceMulti(final String fileName) {
        return new Answer<>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) {
                return getClass().getResourceAsStream(fileName);
            }
        };
    }


}
