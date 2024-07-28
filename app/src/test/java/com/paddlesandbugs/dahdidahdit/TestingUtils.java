package com.paddlesandbugs.dahdidahdit;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
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
        Activity  context = mock(Activity.class);
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



}
