package com.paddlesandbugs.dahdidahdit.base;

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.DistributionTest;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.copytrainer.KochSequence;
import com.paddlesandbugs.dahdidahdit.copytrainer.TextGeneratorFactory;
import com.paddlesandbugs.dahdidahdit.text.RandomTextGenerator;

public class ConstantDistributionFunctionTest {

    public static final CopyTrainer TRAINER = new CopyTrainer(null, new KochSequence(), null);

    private static final Context context = TestingUtils.createContextMock();


    public TextGeneratorFactory.DistributionFunction getConstantDistributionFunction(int koch, int sessions) {
        CopyTrainerParamsFaded pf = new CopyTrainerParamsFaded(context, "");
        pf.setKochLevel(koch);
        return new DefaultLearningStrategy.ConstantDistributionFunction(pf, TRAINER, sessions);
    }


    @Test
    public void test1a() {
        Distribution<MorseCode.CharacterData> base = RandomTextGenerator.createKochTextDistribution(TRAINER, 9);

        TextGeneratorFactory.DistributionFunction df = getConstantDistributionFunction(9, 0);
        base = df.applyWeights(base);

        System.out.println(base);

        Map<MorseCode.CharacterData, Double> res = DistributionTest.runMonteCarlo(base.compile());
        System.out.println(res);

        final MorseCode sut = MorseCode.getInstance();
        final Double lCount = res.get(sut.get("l"));
        final Double tCount = res.get(sut.get("t"));
        final Double pCount = res.get(sut.get("p"));
        final Double uCount = res.get(sut.get("u"));
        final Double kCount = res.get(sut.get("k"));

        Assert.assertEquals("lk", 10, lCount / kCount, 1.0d);
        Assert.assertEquals("uk", 1, uCount / kCount, 1.0d);
        Assert.assertEquals("lp", 4, lCount / pCount, 1.0d);
        Assert.assertEquals("tp", 2, tCount / pCount, 1.0d);
        Assert.assertEquals("lt", 2, lCount / tCount, 1.0d);
    }


    @Test
    public void test1b() {
        Distribution<MorseCode.CharacterData> base = RandomTextGenerator.createKochTextDistribution(TRAINER, 9);

        TextGeneratorFactory.DistributionFunction df = getConstantDistributionFunction(9, 1);
        base = df.applyWeights(base);

        System.out.println(base);

        Map<MorseCode.CharacterData, Double> res = DistributionTest.runMonteCarlo(base.compile());
        System.out.println(res);

        final MorseCode sut = MorseCode.getInstance();
        final Double lCount = res.get(sut.get("l"));
        final Double tCount = res.get(sut.get("t"));
        final Double pCount = res.get(sut.get("p"));
        final Double uCount = res.get(sut.get("u"));
        final Double kCount = res.get(sut.get("k"));

        Assert.assertEquals("lk", 9, lCount / kCount, 0.5d);
        Assert.assertEquals("uk", 1, uCount / kCount, 0.5d);
        Assert.assertEquals("lp", 4, lCount / pCount, 0.5d);
        Assert.assertEquals("tp", 2, tCount / pCount, 0.5d);
        Assert.assertEquals("lt", 2, lCount / tCount, 0.5d);
    }


    @Test
    public void test2() {
        Distribution<MorseCode.CharacterData> base = RandomTextGenerator.createKochTextDistribution(TRAINER, 9);

        /*
         * Koch level 3, so we expect characters k, m, u, r with r being the most frequent one.
         */
        TextGeneratorFactory.DistributionFunction df = getConstantDistributionFunction(2, 0);
        df.applyWeights(base);

        System.out.println(base);

        Map<MorseCode.CharacterData, Double> res = DistributionTest.runMonteCarlo(base.compile());
        System.out.println(res);

        final MorseCode sut = MorseCode.getInstance();
        final Double rCount = res.get(sut.get("r"));
        final Double uCount = res.get(sut.get("u"));
        final Double mCount = res.get(sut.get("m"));
        final Double kCount = res.get(sut.get("k"));

        final double delta = 0.5d;
        Assert.assertEquals("mk", 2, mCount / kCount, delta);
        Assert.assertEquals("ru", 2, rCount / uCount, delta);
        Assert.assertEquals("rm", 4, rCount / mCount, delta);
        Assert.assertEquals("rk", 8, rCount / kCount, delta);
    }

}
