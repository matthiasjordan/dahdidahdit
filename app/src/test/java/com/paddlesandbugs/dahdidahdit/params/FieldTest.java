package com.paddlesandbugs.dahdidahdit.params;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.Test;

import java.util.function.Function;

import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.base.DefaultLearningStrategy;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.LearningSequence;

public class FieldTest {


    public static final String COPYTRAINER = "copytrainer";
    public static final String TO = "to";


    @Test
    public void testGetPrefsKeyContextKoch() {
        Context context = TestingUtils.createContextMock("koch");
        assertEquals("copytrainer_to_koch_level", Field.KOCH_LEVEL.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_effwpm", Field.EFF_WPM.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_qrm", Field.QRM.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_qrn", Field.QRN.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_qsb", Field.QSB.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_wordlength_max", Field.WORD_LENGTH_MAX.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_wpm", Field.WPM.getPrefsKey(context, COPYTRAINER, TO));
    }


    @Test
    public void testGetPrefsKeyContextWordKoch() {
        Context context = TestingUtils.createContextMock("wordkoch");
        assertEquals("copytrainer_to_wordkoch_level", Field.KOCH_LEVEL.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_effwpm", Field.EFF_WPM.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_qrm", Field.QRM.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_qrn", Field.QRN.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_qsb", Field.QSB.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_wordlength_max", Field.WORD_LENGTH_MAX.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_wpm", Field.WPM.getPrefsKey(context, COPYTRAINER, TO));
    }


    @Test
    public void testGetPrefsKeyContextDefault() {
        Context context = TestingUtils.createContextMock("thisdoesnotexist");
        assertEquals("copytrainer_to_koch_level", Field.KOCH_LEVEL.getPrefsKey(context, COPYTRAINER, TO));
        assertEquals("copytrainer_to_wpm", Field.WPM.getPrefsKey(context, COPYTRAINER, TO));
    }


    @Test
    public void testGetPrefsKeyString() {
        assertEquals("copytrainer_to_koch2_level", Field.KOCH_LEVEL.getPrefsKey("koch2", COPYTRAINER, TO));
        assertEquals("copytrainer_to_wpm", Field.WPM.getPrefsKey("koch2", COPYTRAINER, TO));
    }


    @Test
    public void testGetPrefsKeyTrainer() {
        Context context = TestingUtils.createContextMock("kochfromprefs");

        LearningSequence sequence = mock(LearningSequence.class);
        when(sequence.getPrefsKeyInfix()).thenReturn("xyz");

        DefaultLearningStrategy strat = mock(DefaultLearningStrategy.class);
        Function<Context, DefaultLearningStrategy> stratSupp = (c) -> strat;

        CopyTrainer trainer = new CopyTrainer(context, sequence, stratSupp);

        assertEquals("copytrainer_to_xyz_level", Field.KOCH_LEVEL.getPrefsKey(trainer, COPYTRAINER, TO));
        assertEquals("copytrainer_to_wpm", Field.WPM.getPrefsKey(trainer, COPYTRAINER, TO));
    }


}
