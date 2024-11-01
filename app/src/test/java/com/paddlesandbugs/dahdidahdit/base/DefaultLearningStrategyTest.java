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

package com.paddlesandbugs.dahdidahdit.base;

import static org.mockito.Mockito.anyString;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerLearningStrategy;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParams;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;

public class DefaultLearningStrategyTest {

    private static final Context context = TestingUtils.createContextMock();


    @Test
    public void test3() {
        SharedPreferences.Editor ed = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(ed.putString(anyString(), anyString())).thenReturn(ed);
        SharedPreferences sp = Mockito.mock(SharedPreferences.class);
        Mockito.when(sp.getString(anyString(), anyString())).thenReturn("", "W -1;W -1;W -1;W -1", "W -1;W -1;W -1;W -1", "W -1;W -1;W -1;W -1", "W -1;W -1;W -1");
        Mockito.when(sp.edit()).thenReturn(ed);

        CopyTrainerParams pf = createParams();

        DefaultLearningStrategy sut = new CopyTrainerLearningStrategy(context, sp) {
            @Override
            protected GeneralParameters getParameters() {
                return pf;
            }
        };

        sut.applyTempLearningEaseAdjustments(pf.current());

        // Nothing really happened
        Assert.assertEquals(16, pf.current().getEffWPM());

        // Progress!
        sut.handleLearningProgress(pf.current(), new ParameterFader.FadeStep(Field.KOCH_LEVEL, 1));

        Mockito.verify(ed).putString("copytrainer_temp_learning_ease", "W -1;W -1;W -1;W -1");

        // Next session
        CopyTrainerParamsFaded pf1 = createParamsFaded();
        sut.applyTempLearningEaseAdjustments(pf1);
        Assert.assertEquals(16 - LearningEase.FARNSWORTH_REDUCE_AMOUNT, pf1.getEffWPM());

        CopyTrainerParamsFaded pf2 = createParamsFaded();
        sut.applyTempLearningEaseAdjustments(pf2);
        Assert.assertEquals(16 - LearningEase.FARNSWORTH_REDUCE_AMOUNT, pf2.getEffWPM());

        sut.reduceTempLearningEaseAdjustments();

        Mockito.verify(ed).putString("copytrainer_temp_learning_ease", "W -1;W -1;W -1");

        CopyTrainerParamsFaded pf3 = createParamsFaded();
        sut.applyTempLearningEaseAdjustments(pf3);
        Assert.assertEquals(16 - LearningEase.FARNSWORTH_REDUCE_AMOUNT + 1, pf3.getEffWPM());

        CopyTrainerParamsFaded pf4 = createParamsFaded();
        sut.applyTempLearningEaseAdjustments(pf4);
        Assert.assertEquals(16 - LearningEase.FARNSWORTH_REDUCE_AMOUNT + 1, pf4.getEffWPM());

    }


    public CopyTrainerParamsFaded createParamsFaded() {
        CopyTrainerParamsFaded pf = new CopyTrainerParamsFaded(context, "");
        pf.setWPM(16); // We need this only for the internal invariant of the fader config
        pf.setEffWPM(16);
        return pf;
    }


    public CopyTrainerParams createParams() {
        CopyTrainerParams p = new CopyTrainerParams(context);
        p.current().setWPM(16); // We need this only for the internal invariant of the fader config
        p.current().setEffWPM(16);
        return p;
    }
}
