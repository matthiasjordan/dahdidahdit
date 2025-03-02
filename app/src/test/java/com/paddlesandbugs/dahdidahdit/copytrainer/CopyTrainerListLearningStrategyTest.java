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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextTestUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class CopyTrainerListLearningStrategyTest {

    @Test
    public void testWordKoch() {
        final int max = new WordKochSequence().getMax();
        for (int kochLevel = 0; (kochLevel < max); kochLevel++) {
            testKochLevel(kochLevel);
        }
    }


    private static void testKochLevel(int kochLevel) {
        // Setup
        final Context context = getContext(kochLevel);

        // Execute
        CopyTrainerListLearningStrategy sut = new CopyTrainerListLearningStrategy(context);
        TextGenerator textGen = sut.createTextGenerator();
        MorseCode.CharacterList resList = TextTestUtils.readPrinted(textGen, 1000);

        // Check
        Map<MorseCode.CharacterData, Double> counts = TextTestUtils.count(resList);
        Set<MorseCode.CharacterData> actualChars = counts.keySet();
        actualChars.remove(MorseCode.WORDBREAK);

        MorseCode.CharacterList levelChars = new CopyTrainer(context, new WordKochSequence(), null).getCharsFlat(kochLevel);
        System.out.println(actualChars);

        final Set<MorseCode.CharacterData> expectedChars = levelChars.asSet();
        Assert.assertEquals(expectedChars, actualChars);
    }


    @NonNull
    private static Context getContext(int kochLevel) {
        Context context = TestingUtils.createContextMock("wordkoch");
        SharedPreferences sharedPrefs = context.getSharedPreferences("any", 0);
        when(sharedPrefs.getString("copytrainer_session_duration_S", "60")).thenReturn("60");
        when(sharedPrefs.getInt("copytrainer_session_start_pause_duration_S", 3)).thenReturn(3);
        when(sharedPrefs.getInt(eq("copytrainer_current_wordlength_max"), anyInt())).thenReturn(3);
        when(sharedPrefs.getString("copytrainer_current_wordkoch_level", "0")).thenReturn(Integer.toString(kochLevel));
        when(sharedPrefs.getString("copytrainer_current_qsb", "1")).thenReturn("1");
        when(sharedPrefs.getString("copytrainer_current_qrm", "1")).thenReturn("1");
        when(sharedPrefs.getString("copytrainer_current_qrn", "1")).thenReturn("1");
        when(sharedPrefs.getString("copytrainer_to_wordkoch_level", "0")).thenReturn("2");
        when(sharedPrefs.getString("copytrainer_to_qsb", "1")).thenReturn("1");
        when(sharedPrefs.getString("copytrainer_to_qrm", "1")).thenReturn("1");
        when(sharedPrefs.getString("copytrainer_to_qrn", "1")).thenReturn("1");
        when(context.getSharedPreferences("com.paddlesandbugs.dahdidahdit_preferences", Context.MODE_PRIVATE)).thenReturn(sharedPrefs);

        Resources resources = mock(Resources.class);
        when(resources.openRawResource(R.raw.wordlist)).then(TestingUtils.fakeRawFileResourceMulti("/raw/wordlist"));
        when(resources.openRawResource(R.raw.itu_prefixes)).then(TestingUtils.fakeRawFileResourceMulti("/raw/itu_prefixes"));

        when(context.getResources()).thenReturn(resources);
        return context;
    }
}
