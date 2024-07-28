package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

public class LearningSequenceTest {

    private static final Set<MorseCode.CharacterData> COMPLETE = new MorseCode.MutableCharacterList("abcdefghijklmnopqrstuvwxyz0123456789").asSet();

    @Test
    public void test() {
        Context context = TestingUtils.createContextMock("nx");
        for (Function<Context, CopyTrainer> provider : MainActivity.getNameToCopyTrainerProviders().values()) {
            CopyTrainer ct = provider.apply(context);
            LearningSequence sequence = ct.getSequence();
            testCompleteness(sequence);
        }
    }


    private void testCompleteness(LearningSequence sequence) {
        Set<MorseCode.CharacterData> found = new HashSet<>();
        for (int i = 0; (i <= sequence.getMax()); i += 1) {
            MorseCode.CharacterList c = sequence.getChar(i);
            found.addAll(c.asSet());
        }

        Set<MorseCode.CharacterData> rest = new HashSet<>(COMPLETE);
        rest.removeAll(found);
        Assert.assertTrue("Sequence " + sequence.getClass().getSimpleName() + " left over: " + rest, found.containsAll(COMPLETE));
    }
}
