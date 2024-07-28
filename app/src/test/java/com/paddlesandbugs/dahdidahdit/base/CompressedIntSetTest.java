package com.paddlesandbugs.dahdidahdit.base;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class CompressedIntSetTest {

    @Test
    public void test() {
        CompressedIntSet sut = new CompressedIntSet();

        sut.add(1);

        Assert.assertEquals("[(1, 1)]", sut.toString());
        sut.add(2);
        Assert.assertEquals("[(1, 2)]", sut.toString());
        sut.add(3);
        Assert.assertEquals("[(1, 3)]", sut.toString());

        sut.add(5);
        Assert.assertEquals("[(1, 3), (5, 5)]", sut.toString());

        sut.add(7);
        Assert.assertEquals("[(1, 3), (5, 5), (7, 7)]", sut.toString());

        sut.add(6);
        Assert.assertEquals("[(1, 3), (5, 7)]", sut.toString());
        sut.add(4);
        Assert.assertEquals("[(1, 7)]", sut.toString());
    }


    @Test
    public void testContains() {
        CompressedIntSet sut = new CompressedIntSet();

        sut.add(1);
        sut.add(2);
        sut.add(3);
        sut.add(5);
        Assert.assertTrue(sut.contains(1));
        Assert.assertTrue(sut.contains(2));
        Assert.assertTrue(sut.contains(3));
        Assert.assertFalse(sut.contains(4));
        Assert.assertTrue(sut.contains(5));
        Assert.assertFalse(sut.contains(6));
        Assert.assertFalse(sut.contains(7));
    }

    @Test
    public void testClear() {
        CompressedIntSet sut = new CompressedIntSet();

        sut.add(1);
        sut.add(2);
        sut.add(3);
        sut.add(5);
        sut.clear();

        Assert.assertFalse(sut.contains(1));
        Assert.assertFalse(sut.contains(2));
        Assert.assertFalse(sut.contains(3));
        Assert.assertFalse(sut.contains(4));
        Assert.assertFalse(sut.contains(5));
        Assert.assertFalse(sut.contains(6));
        Assert.assertFalse(sut.contains(7));
    }


    @Test
    public void testAsString() {
        CompressedIntSet sut = new CompressedIntSet();

        Assert.assertEquals("", sut.asString());

        sut.add(1);
        sut.add(2);
        sut.add(3);
        sut.add(5);

        Assert.assertEquals("1;3|5", sut.asString());

        sut.add(4);

        Assert.assertEquals("1;5", sut.asString());
    }

    @Test
    public void testFromString() {
        Assert.assertEquals("[]", CompressedIntSet.fromString("").toString());
        Assert.assertEquals("[(1, 5), (7, 7), (9, 109)]", CompressedIntSet.fromString("1;5|7|9;109").toString());
    }

    @Test
    public void randomizedSerializationTest() {
        Random r = new Random();

        for (int i = 0; (i < 1000); i++) {
            CompressedIntSet sut = new CompressedIntSet();
            Set<Integer> control = new HashSet<>();
            int max = Integer.MIN_VALUE;

            int count = r.nextInt(1000) + 1;
            int spread = r.nextInt(10) + 1;
            for (int j = 0; (j < count); j++) {
                int num = r.nextInt(count * spread);
                sut.add(num);
                control.add(num);

                if (num > max) {
                    max = num;
                }
            }

            String str = sut.asString();
            CompressedIntSet sut2 = CompressedIntSet.fromString(str);

            for (int controlI : control) {
                Assert.assertTrue(sut.contains(controlI));
                Assert.assertTrue(sut2.contains(controlI));
            }

            for (int rr = 0; (rr < max); rr++) {
                int rand = r.nextInt(max);
                Assert.assertEquals(control.contains(rand), sut.contains(rand));
                Assert.assertEquals(control.contains(rand), sut2.contains(rand));
            }

        }
    }
}
