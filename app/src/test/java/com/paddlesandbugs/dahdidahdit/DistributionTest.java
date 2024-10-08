package com.paddlesandbugs.dahdidahdit;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paddlesandbugs.dahdidahdit.Distribution.Compiled;

public class DistributionTest {

    private static final int RUNS = 10000000;

    private Compiled.CompiledItem<Integer> gen(int i, int min, int max) {
        return new Compiled.CompiledItem<>(i, min, max);
    }


    @Test
    public void testCompiledDistribution_10_1() {
        Compiled<Integer> sut = new Compiled<>(Arrays.asList(gen(0, 0, 10), gen(1, 10, 11)));

        // Execute

        Map<Integer, Double> itemsDrawn = runMonteCarlo(sut);

        // Check

        // Ratio should be about 1:10
        double ratio = itemsDrawn.get(0) / itemsDrawn.get(1);
        System.out.println(itemsDrawn + " -> " + ratio);
        Assert.assertEquals(ratio, 10.0d, 1.0d);
    }


    @Test
    public void testCompiledDistribution_10_1_5() {

        Compiled<Integer> sut = new Compiled<>(Arrays.asList(gen(0, 0, 10), gen(1, 10, 11), gen(2, 11, 16)));

        // Execute

        Map<Integer, Double> itemsDrawn = runMonteCarlo(sut);

        // Check

        System.out.println(itemsDrawn);

        // Ratio should be about 10:1:5
        double ratio01 = itemsDrawn.get(0) / itemsDrawn.get(1);
        double ratio12 = itemsDrawn.get(1) / itemsDrawn.get(2);
        double ratio02 = itemsDrawn.get(0) / itemsDrawn.get(2);

        Assert.assertEquals(ratio01, 10.0d, 1.0d);
        Assert.assertEquals(ratio12, 0.2d, 1.0d);
        Assert.assertEquals(ratio02, 2.0d, 1.0d);
    }


    public static <T> Map<T, Double> runMonteCarlo(Compiled<T> sut) {
        Map<T, Double> res = new HashMap<>();
        for (int i = 0; (i < RUNS); i++) {
            T event = sut.next();

            Double old = res.get(event);
            if (old == null) {
                old = new Double(0);
            }
            old += 1;
            res.put(event, old);
        }
        return res;
    }



    @Test(expected = IllegalArgumentException.class)
    public void testDistributionUniform0() {
        runUniformTest(0);
    }


    @Test
    public void testDistributionUniform1() {
        runUniformTest(1);
    }


    @Test
    public void testDistributionUniform2() {
        runUniformTest(2);
    }


    @Test
    public void testDistributionUniform10() {
        runUniformTest(10);
    }


    @Test
    public void testDistributionUniform1000() {
        runUniformTest(1000);
    }


    private void runUniformTest(int count) {
        Distribution<Integer> sut = dist(count);
        runTest(sut);
    }


    private void runTest(Distribution<Integer> sut) {
        Map<Integer, Double> res = runMonteCarlo(sut.compile());
        int count = sut.size();

        System.out.println("Monte Carlo result: " + res);
        final int hitsPerBin = RUNS / count;
        int delta = hitsPerBin * 5 / 100;
        for (int i = 0; (i < count); i++) {
            Assert.assertEquals(hitsPerBin, res.get(i), delta);
        }
    }


    private Distribution<Integer> dist(int count) {
        ArrayList<Integer> ll = new ArrayList<>();
        for (int i = 0; (i < count); i++) {
            ll.add(i);
        }

        return new Distribution<>(ll);
    }


    @Test
    public void testSetWeight1() {
        Distribution<Integer> sut = dist(5);
        sut.setWeight(1, 2.0f);
        Map<Integer, Double> res = runMonteCarlo(sut.compile());
        System.out.println(res);

        double delta = RUNS/1000;
        Assert.assertEquals(res.get(0) * 2, res.get(1), delta);
        Assert.assertEquals(res.get(2) * 2, res.get(1), delta);
        Assert.assertEquals(res.get(3) * 2, res.get(1), delta);
        Assert.assertEquals(res.get(4) * 2, res.get(1), delta);
    }


    @Test
    public void testSetWeight2() {
        Distribution<Integer> sut = dist(5);
        sut.setWeight(3, 3.0f);
        sut.setWeight(4, 2.0f);
        final Compiled<Integer> compiled = sut.compile();
        System.out.println("compiled: " + compiled);
        Map<Integer, Double> res = runMonteCarlo(compiled);
        System.out.println(res);

        final int hitsPerBin = RUNS / 5;
        int delta = hitsPerBin * 3 / 100;
        Assert.assertEquals(res.get(0) * 2, res.get(4), delta);
        Assert.assertEquals(res.get(0) * 3, res.get(3), delta);
        Assert.assertEquals(res.get(1) * 2, res.get(4), delta);
        Assert.assertEquals(res.get(1) * 3, res.get(3), delta);
        Assert.assertEquals(res.get(4) * 1.5f, res.get(3), delta);
    }


    @Test
    public void testMultWeight() {
        Distribution<Integer> sut = dist(5);
        sut.setWeight(1, 2.0f);
        sut.multWeight(1, 6.0f); // Makes weight of 2 * 6

        sut.multWeight(2, 2.0f); // Makes weight of 1 * 2

        sut.multWeight(2222, 2.0f); // Makes nothing because there is no such value in the distribution

        Map<Integer, Double> res = runMonteCarlo(sut.compile());
        System.out.println(res);

        final int delta = 6 * RUNS/1000;
        Assert.assertEquals(res.get(0) * 2 * 6, res.get(1), delta);
        Assert.assertEquals(res.get(2) * 1 * 6, res.get(1), delta);
        Assert.assertEquals(res.get(3) * 2 * 6, res.get(1), delta);
        Assert.assertEquals(res.get(4) * 2 * 6, res.get(1), delta);
    }


    @Test
    public void testDefaultConstructor() {
        Distribution<Integer> sut = new Distribution<>();
        sut.setWeight(0, 1.0f);
        sut.setWeight(1, 1.0f);
        sut.setWeight(2, 1.0f);
        sut.setWeight(3, 3.0f);
        sut.setWeight(4, 2.0f);
        final Compiled<Integer> compiled = sut.compile();
        System.out.println("compiled: " + compiled);
        Map<Integer, Double> res = runMonteCarlo(compiled);
        System.out.println(res);

        final int hitsPerBin = RUNS / 5;
        int delta = hitsPerBin * 3 / 100;
        Assert.assertEquals(res.get(0) * 2, res.get(4), delta);
        Assert.assertEquals(res.get(0) * 3, res.get(3), delta);
        Assert.assertEquals(res.get(1) * 2, res.get(4), delta);
        Assert.assertEquals(res.get(1) * 3, res.get(3), delta);
        Assert.assertEquals(res.get(4) * 1.5f, res.get(3), delta);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testChain1_1() {
        Distribution<Integer> sut1 = dist(5);
        Distribution<Integer> sut2 = dist(6);

        sut2.chain(sut1);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testChain1_2() {
        Distribution<Integer> sut1 = dist(5);
        Distribution<Integer> sut2 = dist(6);

        sut1.chain(sut2);
    }


    @Test
    public void testChain1() {
        Distribution<Integer> sut1 = dist(5);
        sut1.setWeight(2, 2.0f);

        Distribution<Integer> sut2 = dist(5);
        sut2.setWeight(3, 3.0f);

        Distribution<Integer> sut3 = sut1.chain(sut2);

        Compiled<Integer> compiled = sut3.compile();

        System.out.println(compiled);

        Map<Integer, Double> res = runMonteCarlo(compiled);

        System.out.println(res);

        final int hitsPerBin = RUNS / (5 + 2 + 3 - 2);
        int delta = hitsPerBin * 3 / 100;
        Assert.assertEquals(hitsPerBin, res.get(0), delta);
        Assert.assertEquals(hitsPerBin, res.get(1), delta);
        Assert.assertEquals(hitsPerBin * 2, res.get(2), delta);
        Assert.assertEquals(hitsPerBin * 3, res.get(3), delta);
        Assert.assertEquals(hitsPerBin, res.get(4), delta);
    }


    @Test
    public void testChain2() {
        Distribution<Integer> sut1 = dist(5);
        sut1.setWeight(2, 2.0f);

        Distribution<Integer> sut2 = dist(5);
        sut2.setWeight(2, 3.0f);

        Distribution<Integer> sut3 = sut1.chain(sut2);

        Compiled<Integer> compiled = sut3.compile();

        System.out.println(compiled);

        Map<Integer, Double> res = runMonteCarlo(compiled);

        System.out.println(res);

        final int hitsPerBin = RUNS / (5 + (2 * 3) - 1);
        int delta = hitsPerBin * 3 / 100;
        Assert.assertEquals(hitsPerBin, res.get(0), delta);
        Assert.assertEquals(hitsPerBin, res.get(1), delta);
        Assert.assertEquals(hitsPerBin * 2 * 3, res.get(2), delta);
        Assert.assertEquals(hitsPerBin, res.get(3), delta);
        Assert.assertEquals(hitsPerBin, res.get(4), delta);
    }


    @Test
    public void testRoundRobin0() {
        Distribution.RoundRobin<Integer> sut = new Distribution.RoundRobin<>();
        sut.add(1);
        sut.add(2);
        sut.add(3);

        List<Integer> res = new ArrayList<>();
        for (int i = 0; (i < 10); i++) {
            res.add(sut.next());
        }

        Assert.assertEquals(Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3, 1), res);
    }


    @Test
    public void testRoundRobin1() {
        Distribution<Integer> dist = new Distribution<>();
        dist.setWeight(11, 1.0f);
        dist.setWeight(12, 1.0f);
        final Compiled<Integer> compiled = dist.compile();

        Distribution.RoundRobin<Integer> sut = new Distribution.RoundRobin<>();
        sut.add(1);
        sut.add(2);
        sut.add(compiled);

        List<Integer> res = new ArrayList<>();
        for (int i = 0; (i < 10); i++) {
            res.add(sut.next());
        }

        Assert.assertEquals((Integer) 1, res.get(0));
        Assert.assertEquals((Integer) 2, res.get(1));
        Assert.assertTrue(compiled.events().contains(res.get(2)));
        Assert.assertEquals((Integer) 1, res.get(3));
        Assert.assertEquals((Integer) 2, res.get(4));
        Assert.assertTrue(compiled.events().contains(res.get(5)));
        Assert.assertEquals((Integer) 1, res.get(6));
        Assert.assertEquals((Integer) 2, res.get(7));
        Assert.assertTrue(compiled.events().contains(res.get(8)));
        Assert.assertEquals((Integer) 1, res.get(9));
    }


    @Test
    public void testRoundRobinOfRoundRobins() {

        Distribution.RoundRobin<Integer> sut1 = new Distribution.RoundRobin<>();
        sut1.add(11);
        sut1.add(12);

        Distribution.RoundRobin<Integer> sut2 = new Distribution.RoundRobin<>();
        sut2.add(21);
        sut2.add(22);

        Distribution.RoundRobin<Integer> sut = new Distribution.RoundRobin<>();
        sut.add(sut1);
        sut.add(sut2);
        sut.add(1);

        List<Integer> res = new ArrayList<>();
        for (int i = 0; (i < 10); i++) {
            res.add(sut.next());
        }

        Assert.assertEquals((Integer) 11, res.get(0));
        Assert.assertEquals((Integer) 21, res.get(1));
        Assert.assertEquals((Integer) 1, res.get(2));
        Assert.assertEquals((Integer) 12, res.get(3));
        Assert.assertEquals((Integer) 22, res.get(4));
        Assert.assertEquals((Integer) 1, res.get(5));
        Assert.assertEquals((Integer) 11, res.get(6));
        Assert.assertEquals((Integer) 21, res.get(7));
        Assert.assertEquals((Integer) 1, res.get(8));
        Assert.assertEquals((Integer) 12, res.get(9));
    }

}
