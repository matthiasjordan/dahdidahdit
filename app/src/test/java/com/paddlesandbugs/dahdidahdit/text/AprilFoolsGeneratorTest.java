package com.paddlesandbugs.dahdidahdit.text;

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.TestingUtils;

public class AprilFoolsGeneratorTest extends AbstractTextGeneratorTest {

    private final Context CONTEXT = TestingUtils.createContextMock();

    @Test
    public void test() {
        AprilFoolsGenerator sut = new AprilFoolsGenerator(CONTEXT);

        MorseCode.CharacterList s = read(sut, 100);
        final String res = s.asString();
        System.out.println(res);

        Assert.assertTrue(res.indexOf(AprilFoolsGenerator.msg) == 10);
    }


    @Test
    public void testId() {
        Assert.assertEquals(R.string.text_generator_mode_random, new AprilFoolsGenerator(CONTEXT).getTextID());
    }
}