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

package com.paddlesandbugs.dahdidahdit.base;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.TestingUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReleaseNotesTest {

    @Test
    public void testVersionStringToCode() {
        Assert.assertEquals(0, ReleaseNotes.versionStringToCode("1"));
        Assert.assertEquals(0, ReleaseNotes.versionStringToCode("1.2"));
        Assert.assertEquals(1020300, ReleaseNotes.versionStringToCode("1.2.3"));
        Assert.assertEquals(1130100, ReleaseNotes.versionStringToCode("1.13.1"));
        Assert.assertEquals(0, ReleaseNotes.versionStringToCode("1.2.3.4"));
    }

    @Test
    public void testRsrcToSpannable1() {
        // Setup
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).then(TestingUtils.fakeRawFileResourceMulti("/raw/release_notes_test"));
        when(context.getResources()).thenReturn(resources);

        SpannableStringBuilder actual = mock(SpannableStringBuilder.class);

        // Execution
        ReleaseNotes.rsrcToSpannable(context, R.raw.wordlist, ReleaseNotes.versionStringToCode("1.2.0"), actual);

        // Check
        Mockito.verify(actual).append(eq("2.0.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("version 2.0.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("1.2.1"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("version 1.2.1"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));

        Mockito.verify(actual, times(0)).append(eq("1.2.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("version 1.2.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("1.1.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("version 1.1.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
    }

    @Test
    public void testRsrcToSpannable2() {
        // Setup
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).then(TestingUtils.fakeRawFileResourceMulti("/raw/release_notes_test"));
        when(context.getResources()).thenReturn(resources);

        SpannableStringBuilder actual = mock(SpannableStringBuilder.class);

        // Execution
        ReleaseNotes.rsrcToSpannable(context, R.raw.wordlist, ReleaseNotes.versionStringToCode("1.2.1"), actual);

        // Check
        Mockito.verify(actual).append(eq("2.0.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("version 2.0.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));

        Mockito.verify(actual, times(0)).append(eq("1.2.1"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("version 1.2.1"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("1.2.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("version 1.2.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("1.1.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual, times(0)).append(eq("version 1.1.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
    }

    @Test
    public void testRsrcToSpannable0() {
        // Setup
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).then(TestingUtils.fakeRawFileResourceMulti("/raw/release_notes_test"));
        when(context.getResources()).thenReturn(resources);

        SpannableStringBuilder actual = mock(SpannableStringBuilder.class);

        // Execution
        ReleaseNotes.rsrcToSpannable(context, R.raw.wordlist, ReleaseNotes.versionStringToCode(""), actual);

        // Check
        Mockito.verify(actual).append(eq("2.0.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("version 2.0.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));

        Mockito.verify(actual).append(eq("1.2.1"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("version 1.2.1"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("1.2.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("version 1.2.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("1.1.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        Mockito.verify(actual).append(eq("version 1.1.0"), any(), eq(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
    }


}
