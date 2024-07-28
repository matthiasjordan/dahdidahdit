package com.paddlesandbugs.dahdidahdit.network.mopp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link BitInputStream}.
 */
public class BitInputStreamTest {


    @Test
    public void testGetBit1() {
        BitInputStream sut = new BitInputStream(new byte[]{0x7a});
        int[] res = new int[10];
        for (int i = 0; (i < res.length); i++) {
            res[i] = sut.getBit();
        }

        Assert.assertArrayEquals(new int[]{0, 1, 1, 1, 1, 0, 1, 0, -1, -1}, res);
    }


    @Test
    public void testGetBit2() {
        BitInputStream sut = new BitInputStream(new byte[]{-1});
        int[] res = new int[10];
        for (int i = 0; (i < res.length); i++) {
            res[i] = sut.getBit();
        }

        Assert.assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1, 1, 1, -1, -1}, res);
    }


    @Test
    public void testGetBit3() {
        BitInputStream sut = new BitInputStream(new byte[]{0x7a, -1});
        int[] res = new int[8 + 8 + 2];
        for (int i = 0; (i < res.length); i++) {
            res[i] = sut.getBit();
        }

        Assert.assertArrayEquals(new int[]{//
                0, 1, 1, 1, //
                1, 0, 1, 0, //
                1, 1, 1, 1, //
                1, 1, 1, 1, //
                -1, -1}, res);
    }


    @Test
    public void testGetBits1() {
        BitInputStream sut = new BitInputStream(new byte[]{0x7a, -1});
        int res = sut.getBits(2);
        Assert.assertEquals(0b000001, res);
    }


    @Test
    public void testGetBits2() {
        BitInputStream sut = new BitInputStream(new byte[]{0x7a, -1});
        int res = sut.getBits(3);
        Assert.assertEquals(0b000011, res);
    }


    @Test
    public void testGetBits3() {
        BitInputStream sut = new BitInputStream(new byte[]{0x7a, -1});
        int res = sut.getBits(6);
        Assert.assertEquals(0b011110, res);
    }


}
