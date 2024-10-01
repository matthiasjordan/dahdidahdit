package com.paddlesandbugs.dahdidahdit.base;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.DistributionTest;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.KochSequence;
import com.paddlesandbugs.dahdidahdit.copytrainer.TextGeneratorFactory;
import com.paddlesandbugs.dahdidahdit.text.RandomTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextTestUtils;

public class FrequencyDistributionFunctionTest {


    public static final CopyTrainer TRAINER = new CopyTrainer(null, new KochSequence(), null);


    public TextGeneratorFactory.DistributionFunction getFrequencyDistributionFunction(int koch, int max, int sessions) {
        return new DefaultLearningStrategy.FrequencyDistributionFunction(TRAINER, koch, max, sessions);
    }


    @Test
    public void testLongTermMaxFactor9_40() {
        DefaultLearningStrategy.FrequencyDistributionFunction sut = new DefaultLearningStrategy.FrequencyDistributionFunction(TRAINER, 9, 40, 0);

        Assert.assertEquals("", 5, sut.longTermMaxFactor(0)); // first every 5 chars
        Assert.assertEquals("", 6, sut.longTermMaxFactor(1));
        Assert.assertEquals("", 7, sut.longTermMaxFactor(2));
        Assert.assertEquals("", 8, sut.longTermMaxFactor(3));
        Assert.assertEquals("", 9, sut.longTermMaxFactor(4));
        Assert.assertEquals("", 10, sut.longTermMaxFactor(5));
        Assert.assertEquals("", 11, sut.longTermMaxFactor(6));
        Assert.assertEquals("", 12, sut.longTermMaxFactor(7));
        Assert.assertEquals("", 13, sut.longTermMaxFactor(8));
        Assert.assertEquals("", 14, sut.longTermMaxFactor(9));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(10)); // min - now every 15 chars
        Assert.assertEquals("", 15, sut.longTermMaxFactor(11));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(12));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(13));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(14));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(15)); // 15 chars stays on because Koch not leveled to max
        Assert.assertEquals("", 15, sut.longTermMaxFactor(16));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(17));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(18));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(19));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(20));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(21));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(22));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(23));
    }


    @Test
    public void testLongTermMaxFactor39_40() {
        DefaultLearningStrategy.FrequencyDistributionFunction sut = new DefaultLearningStrategy.FrequencyDistributionFunction(TRAINER, 39, 40, 0);

        Assert.assertEquals("", 5, sut.longTermMaxFactor(0)); // first every 5 chars
        Assert.assertEquals("", 6, sut.longTermMaxFactor(1));
        Assert.assertEquals("", 7, sut.longTermMaxFactor(2));
        Assert.assertEquals("", 8, sut.longTermMaxFactor(3));
        Assert.assertEquals("", 9, sut.longTermMaxFactor(4));
        Assert.assertEquals("", 10, sut.longTermMaxFactor(5));
        Assert.assertEquals("", 11, sut.longTermMaxFactor(6));
        Assert.assertEquals("", 12, sut.longTermMaxFactor(7));
        Assert.assertEquals("", 13, sut.longTermMaxFactor(8));
        Assert.assertEquals("", 14, sut.longTermMaxFactor(9));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(10)); // min - now every 15 chars
        Assert.assertEquals("", 15, sut.longTermMaxFactor(11));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(12));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(13));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(14));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(15)); // 15 chars stays on because Koch not leveled to max
        Assert.assertEquals("", 15, sut.longTermMaxFactor(16));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(17));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(18));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(19));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(20));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(21));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(22));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(23));
    }


    @Test
    public void testLongTermMaxFactor40_40() {
        DefaultLearningStrategy.FrequencyDistributionFunction sut = new DefaultLearningStrategy.FrequencyDistributionFunction(TRAINER, 40, 40, 0);

        Assert.assertEquals("", 5, sut.longTermMaxFactor(0)); // first every 5 chars
        Assert.assertEquals("", 6, sut.longTermMaxFactor(1));
        Assert.assertEquals("", 7, sut.longTermMaxFactor(2));
        Assert.assertEquals("", 8, sut.longTermMaxFactor(3));
        Assert.assertEquals("", 9, sut.longTermMaxFactor(4));
        Assert.assertEquals("", 10, sut.longTermMaxFactor(5));
        Assert.assertEquals("", 11, sut.longTermMaxFactor(6));
        Assert.assertEquals("", 12, sut.longTermMaxFactor(7));
        Assert.assertEquals("", 13, sut.longTermMaxFactor(8));
        Assert.assertEquals("", 14, sut.longTermMaxFactor(9));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(10)); // min - now every 15 chars
        Assert.assertEquals("", 15, sut.longTermMaxFactor(11));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(12));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(13));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(14));
        Assert.assertEquals("", 15, sut.longTermMaxFactor(15)); // last min, now increasing ever on because Koch is leveled to max
        Assert.assertEquals("", 16, sut.longTermMaxFactor(16));
        Assert.assertEquals("", 17, sut.longTermMaxFactor(17));
        Assert.assertEquals("", 18, sut.longTermMaxFactor(18));
        Assert.assertEquals("", 19, sut.longTermMaxFactor(19));
        Assert.assertEquals("", 20, sut.longTermMaxFactor(20));
        Assert.assertEquals("", 21, sut.longTermMaxFactor(21));
        Assert.assertEquals("", 22, sut.longTermMaxFactor(22));
        Assert.assertEquals("", 23, sut.longTermMaxFactor(23));
    }


    @Test
    public void testStartFactor39_40() {
        DefaultLearningStrategy.FrequencyDistributionFunction sut = new DefaultLearningStrategy.FrequencyDistributionFunction(TRAINER, 39, 40, 0);

        Assert.assertEquals("", 2.5, sut.startFactor(10, 0), 0.2d); // 10 other chars, freq every 5 chars, char weight 2.5 is 1/4 of 10
        Assert.assertEquals("", 2, sut.startFactor(10, 1), 0.2d);
        Assert.assertEquals("", 1.66, sut.startFactor(10, 2), 0.2d);
        Assert.assertEquals("", 1.42, sut.startFactor(10, 3), 0.2d);
        Assert.assertEquals("", 1.25, sut.startFactor(10, 4), 0.2d);
        Assert.assertEquals("", 1.11, sut.startFactor(10, 5), 0.2d);
        Assert.assertEquals("", 1, sut.startFactor(10, 6), 0.2d);

        Assert.assertEquals("", 0.7, sut.startFactor(10, 10), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 11), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 12), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 13), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 14), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 15), 0.2d); // 15 chars stays on because Koch not leveled to max
        Assert.assertEquals("", 0.7, sut.startFactor(10, 16), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 17), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 18), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 19), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 20), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 21), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 22), 0.2d);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 23), 0.2d);
    }


    @Test
    public void testStartFactor40_40() {
        DefaultLearningStrategy.FrequencyDistributionFunction sut = new DefaultLearningStrategy.FrequencyDistributionFunction(TRAINER, 40, 40, 0);

        final double delta = 0.05d;
        Assert.assertEquals("", 2.5, sut.startFactor(10, 0), delta); // 10 other chars, freq every 5 chars, char weight 2.5 is 1/4 of 10
        Assert.assertEquals("", 2, sut.startFactor(10, 1), delta);
        Assert.assertEquals("", 1.66, sut.startFactor(10, 2), delta);
        Assert.assertEquals("", 1.42, sut.startFactor(10, 3), delta);
        Assert.assertEquals("", 1.25, sut.startFactor(10, 4), delta);
        Assert.assertEquals("", 1.11, sut.startFactor(10, 5), delta);
        Assert.assertEquals("", 1, sut.startFactor(10, 6), delta);

        Assert.assertEquals("", 0.7, sut.startFactor(10, 10), delta);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 11), delta);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 12), delta);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 13), delta);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 14), delta);
        Assert.assertEquals("", 0.7, sut.startFactor(10, 15), delta); // 15 chars stays on because Koch not leveled to max
        Assert.assertEquals("", 0.7, sut.startFactor(10, 16), delta);
        Assert.assertEquals("", 0.625, sut.startFactor(10, 17), delta);
        Assert.assertEquals("", 0.58, sut.startFactor(10, 18), delta);
        Assert.assertEquals("", 0.55, sut.startFactor(10, 19), delta);
        Assert.assertEquals("", 0.52, sut.startFactor(10, 20), delta);
        Assert.assertEquals("", 0.5, sut.startFactor(10, 21), delta);
        Assert.assertEquals("", 0.47, sut.startFactor(10, 22), delta);
        Assert.assertEquals("", 0.45, sut.startFactor(10, 23), delta);
    }


    @Test
    public void test1a_every_6_with_5_others() {
        final int koch = 4;
        Distribution<MorseCode.CharacterData> base = RandomTextGenerator.createKochTextDistribution(TRAINER, koch);

        TextGeneratorFactory.DistributionFunction df = getFrequencyDistributionFunction(koch, 39, 0);
        base = df.applyWeights(base);

        System.out.println(base);

        Map<MorseCode.CharacterData, Double> res = TextTestUtils.runMonteCarlo(base.compile());
        System.out.println(res);

        final MorseCode sut = MorseCode.getInstance();
        final Double kCount = res.get(sut.get("k"));
        final Double mCount = res.get(sut.get("m"));
        final Double uCount = res.get(sut.get("u"));
        final Double rCount = res.get(sut.get("r"));
        final Double eCount = res.get(sut.get("e"));
        final Double sCount = res.get(sut.get("s"));

        final double delta = 0.2d;
        Assert.assertEquals("sk", 1.25, sCount / kCount, delta);
        Assert.assertEquals("sm", 1.25, sCount / mCount, delta);
        Assert.assertEquals("su", 1.25, sCount / uCount, delta);
        Assert.assertEquals("sr", 1.25, sCount / rCount, delta);
        Assert.assertEquals("se", 1.25, sCount / eCount, delta);
    }


    @Test
    public void test1a_every_6_with_10_others() {
        final int koch = 9;
        Distribution<MorseCode.CharacterData> base = RandomTextGenerator.createKochTextDistribution(TRAINER, koch);

        TextGeneratorFactory.DistributionFunction df = getFrequencyDistributionFunction(koch, 39, 0);
        base = df.applyWeights(base);

        System.out.println(base);

        Map<MorseCode.CharacterData, Double> res = TextTestUtils.runMonteCarlo(base.compile());
        System.out.println(res);

        final MorseCode sut = MorseCode.getInstance();
        final Double kCount = res.get(sut.get("k"));
        final Double mCount = res.get(sut.get("m"));
        final Double uCount = res.get(sut.get("u"));
        final Double rCount = res.get(sut.get("r"));
        final Double eCount = res.get(sut.get("e"));
        final Double sCount = res.get(sut.get("s"));
        final Double nCount = res.get(sut.get("n"));
        final Double aCount = res.get(sut.get("a"));
        final Double pCount = res.get(sut.get("p"));
        final Double tCount = res.get(sut.get("t"));
        final Double lCount = res.get(sut.get("l"));

        final double delta = 0.2d;
        Assert.assertEquals("lk", 2.5, lCount / kCount, delta);
        Assert.assertEquals("lm", 2.5, lCount / mCount, delta);
        Assert.assertEquals("lu", 2.5, lCount / uCount, delta);
        Assert.assertEquals("lr", 2.5, lCount / rCount, delta);
        Assert.assertEquals("le", 2.5, lCount / eCount, delta);
        Assert.assertEquals("ls", 2.5, lCount / sCount, delta);
        Assert.assertEquals("ln", 2.5, lCount / nCount, delta);
        Assert.assertEquals("la", 2.5, lCount / aCount, delta);
        Assert.assertEquals("lp", 2.5, lCount / pCount, delta);
        Assert.assertEquals("lt", 2, lCount / tCount, delta);
    }


    @Test
    public void test1a_every_6_with_15_others() {
        final int koch = 14;
        Distribution<MorseCode.CharacterData> base = RandomTextGenerator.createKochTextDistribution(TRAINER, koch);

        TextGeneratorFactory.DistributionFunction df = getFrequencyDistributionFunction(koch, 39, 0);
        base = df.applyWeights(base);

        System.out.println(base);

        Map<MorseCode.CharacterData, Double> res = TextTestUtils.runMonteCarlo(base.compile());
        System.out.println(res);

        final MorseCode sut = MorseCode.getInstance();
        final Double kCount = res.get(sut.get("k"));
        final Double mCount = res.get(sut.get("m"));
        final Double uCount = res.get(sut.get("u"));
        final Double _Count = res.get(sut.get("."));
        final Double jCount = res.get(sut.get("j"));
        final Double zCount = res.get(sut.get("z"));

        Assert.assertEquals("zk", 3.75, zCount / kCount, 0.5d);
        Assert.assertEquals("zm", 3.75, zCount / mCount, 0.5d);
        Assert.assertEquals("zu", 3.75, zCount / uCount, 0.5d);
        Assert.assertEquals("z.", 3.75, zCount / _Count, 0.5d);
        Assert.assertEquals("zj", 2, zCount / jCount, 0.5d);

        final int freq = DefaultLearningStrategy.FrequencyDistributionFunction.CHAR_DIST_INITIAL_FREQUENCY - 1;
        Assert.assertEquals("", freq, (sum(res) - zCount) / zCount, 0.3d);
    }


    @Test
    public void test1a_every_6_with_20_others() {
        final int koch = 19;
        Distribution<MorseCode.CharacterData> base = RandomTextGenerator.createKochTextDistribution(TRAINER, koch);

        TextGeneratorFactory.DistributionFunction df = getFrequencyDistributionFunction(koch, 39, 1);
        base = df.applyWeights(base);

        System.out.println(base);

        Map<MorseCode.CharacterData, Double> res = TextTestUtils.runMonteCarlo(base.compile());
        System.out.println(res);

        final MorseCode sut = MorseCode.getInstance();
        final Double kCount = res.get(sut.get("k"));
        final Double mCount = res.get(sut.get("m"));
        final Double uCount = res.get(sut.get("u"));
        final Double oCount = res.get(sut.get("o"));
        final Double yCount = res.get(sut.get("y"));
        final Double _Count = res.get(sut.get(","));

        final double delta = 0.1d;
        Assert.assertEquals(",k", 4, _Count / kCount, delta);
        Assert.assertEquals(",m", 4, _Count / mCount, delta);
        Assert.assertEquals(",u", 4, _Count / uCount, delta);
        Assert.assertEquals(",o", 4, _Count / oCount, delta);
        Assert.assertEquals(",y", 2, _Count / yCount, delta);

        final int freq = DefaultLearningStrategy.FrequencyDistributionFunction.CHAR_DIST_INITIAL_FREQUENCY;
        Assert.assertEquals("", freq, (sum(res) - _Count) / _Count, 0.3);
    }


    private double sum(Map<MorseCode.CharacterData, Double> res) {
        double sum = 0;
        for (Double v : res.values()) {
            sum += v;
        }
        return sum;
    }

}
