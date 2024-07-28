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
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {


    public static final Charset CHARSET = StandardCharsets.UTF_8;


    /**
     * Reads the input stream and returns its content as a string, using UTF-8 encoding.
     *
     * @param is the input stream
     *
     * @return the resulting string
     */
    public static String toString(InputStream is) {
        StringBuilder b = new StringBuilder();
        try (InputStreamReader r = new InputStreamReader(is, CHARSET);) {
            int c;
            while ((c = r.read()) != -1) {
                b.append((char) c);
            }
        } catch (IOException e) {
            Log.e("Utils", "Exception", e);
        }

        return b.toString();
    }


    /**
     * Reads the input stream and returns its content as a string, using UTF-8 encoding.
     *
     * @param is the input stream
     *
     * @return the resulting string
     */
    public static String toString(InputStream is, int max) {
        StringBuilder b = new StringBuilder();
        try (InputStreamReader r = new InputStreamReader(is, CHARSET);) {
            int c;
            while ((max-- > 0) && ((c = r.read()) != -1)) {
                b.append((char) c);
            }
        } catch (IOException e) {
            Log.e("Utils", "Exception", e);
        }

        return b.toString();
    }


    /**
     * Reads the raw resource whose ID is given and returns its content as a string, using {@link #toString(InputStream)}.
     *
     * @param context the context
     * @param rsrcId  the ID of the raw resource
     *
     * @return the content of that resource as a string
     */
    public static String toString(Context context, int rsrcId) {
        try (InputStream in_s = context.getResources().openRawResource(rsrcId);) {
            return Utils.toString(in_s);
        } catch (IOException e) {
            return "";
        }
    }


    /**
     * Reads the raw resource whose ID is given and returns its content as a list of strings, each of which corresponds to a line in the text file.
     *
     * @param context the context
     * @param rsrcId  the ID of the raw resource
     * @param count   how many lines to return
     *
     * @return the content of that resource as a string
     */
    public static List<String> toString(Context context, int rsrcId, int count) {
        List<String> lines = new ArrayList<>();

        try (InputStream in_s = context.getResources().openRawResource(rsrcId);//
             InputStreamReader isr = new InputStreamReader(in_s); //
             BufferedReader br = new BufferedReader(isr);) {
            String line;
            while ((lines.size() < count) && ((line = br.readLine()) != null)) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
        }
        return lines;
    }


    /**
     * Reads the raw resource whose ID is given and returns its content as a stream of strings, each of which corresponds to a line in the text file.
     *
     * @param context the context
     * @param rsrcId  the ID of the raw resource
     *
     * @return the content of that resource as a stream of strings
     */
    public static Stream<String> toStream(Context context, int rsrcId) throws IOException {
            InputStream in_s = context.getResources().openRawResource(rsrcId);
            InputStreamReader isr = new InputStreamReader(in_s);
            BufferedReader br = new BufferedReader(isr);

            Iterator<String> it = new Iterator<String>() {

                private String lastRead;


                @Override
                public boolean hasNext() {
                    if (lastRead != null) {
                        return true;
                    }
                    try {
                        lastRead = br.readLine();
                    } catch (IOException ex) {
                        lastRead = null;
                    }
                    return (lastRead != null);
                }


                @Override
                public String next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    String n = lastRead;
                    lastRead = null;
                    return n.trim();
                }
            };

            Stream<String> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), false).onClose(() -> {
                try {
                    br.close();
                    isr.close();
                    in_s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return stream;

    }


    /**
     * Returns the color ID for the theme color.
     *
     * @param context the Context
     * @param col     the theme's color name
     *
     * @return the resolved color ID
     */
    public static int getThemeColor(Context context, int col) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(col, typedValue, true);
        return typedValue.resourceId;
    }


    /**
     * Measure the height of a view.
     *
     * @param m    the display metrics
     * @param view the view
     *
     * @return the height
     */
    public static int getMeasuredHeight(DisplayMetrics m, View view) {
        int availableWidth = m.widthPixels;

        int widthSpec = View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        view.measure(widthSpec, heightSpec);
        int measuredHeight = view.getMeasuredHeight();
        Log.i("Tooltip", "Measured size " + measuredHeight);
        return measuredHeight;
    }


    public static String repeat(String str, int count) {
        return repeat(str, " ", count);
    }


    public static String repeat(String str, String inter, int count) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; (i < count); i++) {
            if (s.length() != 0) {
                s.append(inter);
            }
            s.append(str);
        }
        return s.toString();
    }


    private static final long msPerDay = TimeUnit.DAYS.toMillis(1);


    public static long getDayNum(long ts1) {
        return ts1 / msPerDay;
    }


    public static long getStartOfDayMs(long ts1) {
        return getDayNum(ts1) * msPerDay;
    }


    public static boolean isDifferentDay(long ts1, long ts2) {
        return differenceInDays(ts1, ts2) != 0;
    }


    public static int differenceInDays(long ts1, long ts2) {
        long ts1DayNo = getDayNum(ts1);
        long ts2DayNo = getDayNum(ts2);
        return differenceInDaysFromDayNo(ts1DayNo, ts2DayNo);
    }


    private static int differenceInDaysFromDayNo(long ts1DayNo, long ts2DayNo) {
        final long diffDays = ts1DayNo - ts2DayNo;
        long diffDaysCappedToInt = Math.min((long) Integer.MAX_VALUE, diffDays);
        return Math.abs((int) diffDaysCappedToInt);
    }


    public static Drawable getDrawable(Context context, int bulletRsrc, int color) {
        Drawable mDrawable = ContextCompat.getDrawable(context, bulletRsrc);
        Drawable mWrappedDrawable = mDrawable.mutate();
        mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);
        DrawableCompat.setTint(mWrappedDrawable, color);
        DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);
        return mWrappedDrawable;
    }


    public static boolean isEmpty(String s) {
        return (s == null) || s.isEmpty();
    }

}
