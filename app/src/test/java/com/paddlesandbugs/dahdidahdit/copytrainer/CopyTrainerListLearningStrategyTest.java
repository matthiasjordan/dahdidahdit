package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;

import org.junit.Ignore;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextTestUtils;

public class CopyTrainerListLearningStrategyTest {
    @Ignore
    @Test
    public void test() {
        Context context = TestingUtils.createContextMock("wordkoch");
        CopyTrainerListLearningStrategy sut = new CopyTrainerListLearningStrategy(context);

        TextGenerator textGen = sut.createTextGenerator();

        String res = TextTestUtils.pullString(textGen, 1000);
        System.out.println(res);
    }
}
