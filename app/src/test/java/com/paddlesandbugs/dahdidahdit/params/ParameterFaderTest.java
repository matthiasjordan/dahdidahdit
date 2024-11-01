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

package com.paddlesandbugs.dahdidahdit.params;

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParams;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;

public class ParameterFaderTest {

    private static final ParameterFader.FadeStep kochStep = new ParameterFader.FadeStep(Field.KOCH_LEVEL, 1);
    private static final ParameterFader.FadeStep wpmStep = new ParameterFader.FadeStep(Field.WPM, 1);
    private static final ParameterFader.FadeStep effwpmStep = new ParameterFader.FadeStep(Field.EFF_WPM, 1);
    private static final ParameterFader.FadeStep wpmStepm2 = new ParameterFader.FadeStep(Field.WPM, -2);

    private static final Context context = TestingUtils.createContextMock();

    @Test
    public void testFade0() {
        ParameterFader sut = new ParameterFader();
        ParameterFader.Config config = new ParameterFader.Config();
        GeneralFadedParameters from = new CopyTrainerParams(context).current();
        GeneralFadedParameters to = new CopyTrainerParams(context).current();

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
    }


    @Test
    public void testFade1a() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.KOCH_LEVEL, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio1);
        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setKochLevel(1);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setKochLevel(5);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(kochStep, kochStep, kochStep, kochStep), res);
    }


    @Test
    public void testFade1aDown() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.KOCH_LEVEL, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio1);
        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setKochLevel(5);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setKochLevel(1);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(kochStep, kochStep, kochStep, kochStep), res);
    }


    @Test
    public void testFade1b() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.WPM, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio1);
        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setWPM(12);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setWPM(14);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(wpmStep, wpmStep), res);
    }


    /**
     * WPM and Koch at level 1 with equal weight.
     * <p>
     * Only WPM differs
     */
    @Test
    public void testFade2pu_1diff() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.WPM, 1);
        ParameterFader.Prio faderPrio2 = new ParameterFader.Prio(Field.KOCH_LEVEL, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio1);
        stage1.add(faderPrio2);

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setWPM(12);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setWPM(14);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(wpmStep, wpmStep), res);
    }


    /**
     * WPM and Koch at level 1 with equal weight.
     * <p>
     * WPM and Koch differ
     */
    @Test
    public void testFade2pu_2diff() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.WPM, 1);
        ParameterFader.Prio faderPrio2 = new ParameterFader.Prio(Field.KOCH_LEVEL, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio1);
        stage1.add(faderPrio2);

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setWPM(12);
        from.setKochLevel(1);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setWPM(14);
        to.setKochLevel(5);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals("Koch steps", 4, count(res, Field.KOCH_LEVEL));
        Assert.assertEquals("WPM steps", 2, count(res, Field.WPM));
    }


    /**
     * WPM and Koch at level 1 with equal weight.
     * <p>
     * WPM and Koch differ
     */
    @Test
    public void testFade2su_2diff() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.WPM, 1);
        ParameterFader.Prio faderPrio2 = new ParameterFader.Prio(Field.KOCH_LEVEL, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio2);

        ParameterFader.Stage stage2 = new ParameterFader.Stage();
        stage2.add(faderPrio1);

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);
        config.add(stage2);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setWPM(12);
        from.setKochLevel(1);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setWPM(14);
        to.setKochLevel(5);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(kochStep, kochStep, kochStep, kochStep, wpmStep, wpmStep), res);
    }


    /**
     * Koch level first, then WPM.
     * <p>
     * Koch level and WPM differ.
     * <p>
     * There is no invariant so we expect Koch level to be faded first, then WPM.
     */
    @Test
    public void testFadeInvariantMissing() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.WPM, 1);
        ParameterFader.Prio faderPrio2 = new ParameterFader.Prio(Field.KOCH_LEVEL, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio2);

        ParameterFader.Stage stage2 = new ParameterFader.Stage();
        stage2.add(faderPrio1);

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);
        config.add(stage2);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setWPM(12);
        from.setEffWPM(12);
        from.setKochLevel(12);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setWPM(14);
        to.setEffWPM(14);
        to.setKochLevel(14);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(kochStep, kochStep, wpmStep, wpmStep), res);
    }


    /**
     * Koch level first, then WPM.
     * <p>
     * Koch level and WPM differ.
     * <p>
     * There is no invariant so we expect Koch level to be faded first, then WPM.
     */
    @Test
    public void testFadeInvariantKW1() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.WPM, 1);
        ParameterFader.Prio faderPrio2 = new ParameterFader.Prio(Field.KOCH_LEVEL, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio2);

        ParameterFader.Stage stage2 = new ParameterFader.Stage();
        stage2.add(faderPrio1);

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);
        config.add(stage2);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setWPM(12);
        from.setEffWPM(12);
        from.setKochLevel(12);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setWPM(14);
        to.setEffWPM(14);
        to.setKochLevel(14);

        config.add(new ParameterFader.Invariant() {
            @Override
            public boolean apply(ParameterMap map) {
                return map.get(Field.WPM) >= map.get(Field.KOCH_LEVEL);
            }
        });

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(wpmStep, kochStep, wpmStep, kochStep), res);
    }


    /**
     * WPM first, then Effective WPM.
     * <p>
     * EffWPM and WPM differ.
     * <p>
     * There is an internal invariant so we expect EffWPM to be faded only after WPM advanced first.
     * <p>
     * Since WPM is faded first anyway, this should result in first fading WPM, then Effective WPM.
     */
    @Test
    public void testFadeInternalInvariant2() {
        ParameterFader sut = new ParameterFader();

        ParameterFader.Prio faderPrio1 = new ParameterFader.Prio(Field.WPM, 1);
        ParameterFader.Prio faderPrio2 = new ParameterFader.Prio(Field.EFF_WPM, 1);

        ParameterFader.Stage stage1 = new ParameterFader.Stage();
        stage1.add(faderPrio1);

        ParameterFader.Stage stage2 = new ParameterFader.Stage();
        stage2.add(faderPrio2);

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(stage1);
        config.add(stage2);

        CopyTrainerParamsFaded from = new CopyTrainerParamsFaded(context, "");
        from.setWPM(12);
        from.setEffWPM(12);
        from.setKochLevel(1);

        CopyTrainerParamsFaded to = new CopyTrainerParamsFaded(context, "");
        to.setWPM(14);
        to.setEffWPM(14);
        to.setKochLevel(5);

        List<ParameterFader.FadeStep> res = sut.fade(config, from.toMap(), to.toMap());

        System.out.println(res);
        Assert.assertEquals(Arrays.asList(wpmStep, wpmStep, effwpmStep, effwpmStep), res);
    }


    private int count(List<ParameterFader.FadeStep> steps, Field field) {
        int res = 0;
        for (ParameterFader.FadeStep step : steps) {
            if (step.field == field) {
                res += 1;
            }
        }
        return res;
    }


    @Test
    public void testFadeSequenceAsString0() {
        ParameterFader.FadeSequence sut = new ParameterFader.FadeSequence();
        Assert.assertEquals("", sut.asString());
    }


    @Test
    public void testFadeSequenceAsString1() {
        ParameterFader.FadeSequence sut = new ParameterFader.FadeSequence(kochStep, kochStep, wpmStepm2);
        Assert.assertEquals("k 1;k 1;w -2", sut.asString());
    }


    @Test
    public void testFadeSequenceFromString0() {
        ParameterFader.FadeSequence expected = new ParameterFader.FadeSequence();
        ParameterFader.FadeSequence res = ParameterFader.FadeSequence.fromString("");
        Assert.assertEquals(expected, res);
    }


    @Test
    public void testFadeSequenceFromString1() {
        ParameterFader.FadeSequence expected = new ParameterFader.FadeSequence(kochStep, kochStep, wpmStepm2);
        ParameterFader.FadeSequence res = ParameterFader.FadeSequence.fromString("k 1;k 1;w -2");
        Assert.assertEquals(expected, res);
    }

}

