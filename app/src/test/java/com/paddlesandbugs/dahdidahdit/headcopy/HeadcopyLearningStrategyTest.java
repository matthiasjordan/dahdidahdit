/***************************************************************************
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
 **************************************************************************/

package com.paddlesandbugs.dahdidahdit.headcopy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextTestUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class HeadcopyLearningStrategyTest {


    @Test
    public void testGenerator_KochLevel0_AllowOnlyKoch() {
        final int kochLevel = 0;
        final boolean allowOnlyKoch = true;

        testGenerator_Koch_Allow(0, kochLevel, allowOnlyKoch, Set.of('k', 'm'));
    }

    @Test
    public void testGenerator_KochLevel0_AllowAll() {
        final int kochLevel = 0;
        final boolean allowOnlyKoch = false;

        testGenerator_Koch_Allow(0, kochLevel, allowOnlyKoch, null);
    }

    @Test
    public void testGenerator_KochLevel1_AllowOnlyKoch() {
        final int kochLevel = 1;
        final boolean allowOnlyKoch = true;

        testGenerator_Koch_Allow(0, kochLevel, allowOnlyKoch, Set.of('k', 'm', 'u'));
    }

    @Test
    public void testGenerator_KochLevel1_AllowAll() {
        final int kochLevel = 1;
        final boolean allowOnlyKoch = false;

        testGenerator_Koch_Allow(0, kochLevel, allowOnlyKoch, null);
    }

    @Test
    public void testGenerator_KochLevel2_AllowOnlyKoch() {
        final int kochLevel = 2;
        final boolean allowOnlyKoch = true;

        testGenerator_Koch_Allow(0, kochLevel, allowOnlyKoch, Set.of('k', 'm', 'u', 'r'));
    }

    @Test
    public void testGenerator_KochLevel2_AllowOnlyKoch_stage1() {
        final int kochLevel = 2;
        final boolean allowOnlyKoch = true;

        testGenerator_Koch_Allow(1, kochLevel, allowOnlyKoch, Set.of('k', 'm', 'u', 'r'));
    }

    @Test
    public void testGenerator_KochLevel2_AllowOnlyKoch_stage2() {
        final int kochLevel = 2;
        final boolean allowOnlyKoch = true;

        testGenerator_Koch_Allow(2, kochLevel, allowOnlyKoch, Set.of('k', 'm', 'u', 'r'));
    }

    @Test
    public void testGenerator_KochLevel2_AllowOnlyKoch_stage12() {
        final int kochLevel = 2;
        final boolean allowOnlyKoch = true;

        testGenerator_Koch_Allow(12, kochLevel, allowOnlyKoch, Set.of('k', 'm', 'u', 'r'));
    }

    @Test
    public void testGenerator_KochLevel2_AllowOnlyKoch_stage19() {
        final int kochLevel = 2;
        final boolean allowOnlyKoch = true;

        for (int stage = 0; (stage < 20); stage++) {
            testGenerator_Koch_Allow(stage, kochLevel, allowOnlyKoch, Set.of('k', 'm', 'u', 'r'));
        }
    }

    @Test
    public void testGenerator_KochLevel3_AllowOnlyKoch_stage19() {
        final int kochLevel = 3;
        final boolean allowOnlyKoch = true;

        for (int stage = 0; (stage < 20); stage++) {
            testGenerator_Koch_Allow(stage, kochLevel, allowOnlyKoch, Set.of('k', 'm', 'u', 'r', 'e'));
        }
    }

    private static void testGenerator_Koch_Allow(int stage, int kochLevel, boolean allowOnlyKoch, Set<Character> allowed) {
        SharedPreferences prefs = mock(SharedPreferences.class);
        when(prefs.getString(any(), any())).thenReturn("10");
        when(prefs.getInt(anyString(), anyInt())).thenReturn(10);
        when(prefs.getInt(eq(HeadcopyLearningStrategy.STAGE_KEY), eq(0))).thenReturn(stage);
        when(prefs.getString(eq("headcopy_current_koch_level"), any())).thenReturn(Integer.toString(kochLevel));
        when(prefs.getString(eq("copytrainer_current_koch_level"), any())).thenReturn(Integer.toString(kochLevel));
        when(prefs.getBoolean(eq("headcopy_allow_only_copytrainer_chars"), anyBoolean())).thenReturn(allowOnlyKoch);

        Context context = TestingUtils.createContextMock();
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);

        HeadcopyLearningStrategy sut = new HeadcopyLearningStrategy(context);

        TextGenerator textGen = sut.getSessionConfig().morsePlayerConfig.textGenerator;
        String text = TextTestUtils.pullString(textGen, 1000);
        Assert.assertNotEquals("At least 1 char is pulled", 0, text.length());
        onlyContains(allowed, text);

    }

    private static void onlyContains(Set<Character> allowed, String text) {
        if (allowed != null) {
            text.chars().forEach(c -> Assert.assertTrue((char) c + " allowed?", allowed.contains((char) c)));
        }
    }

}
