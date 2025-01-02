/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2025 Matthias Jordan <matthias@paddlesandbugs.com>

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

package com.paddlesandbugs.dahdidahdit.network.mopp;

import static com.paddlesandbugs.dahdidahdit.TestingUtils.toHexString;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link BitOutputStream}.
 */
public class BitOutputStreamTest {

    @Test
    public void testAppend1100() {
        BitOutputStream sut = new BitOutputStream();
        sut.append(4, 0b1100);
        byte[] res = sut.toByteArray();

        check(res, "c0");
    }


    @Test
    public void testAppend11001() {
        BitOutputStream sut = new BitOutputStream();
        sut.append(4, 0b1100);
        sut.append(1);
        byte[] res = sut.toByteArray();

        check(res, "c8");
    }


    @Test
    public void testAppend11000011() {
        BitOutputStream sut = new BitOutputStream();
        sut.append(8, 0b11000011);
        byte[] res = sut.toByteArray();

        check(res, "c3");
    }


    @Test
    public void testAppendBit0() {
        BitOutputStream sut = new BitOutputStream();
        sut.append(0);
        byte[] res = sut.toByteArray();

        check(res, "00");
    }


    @Test
    public void testAppendBit1() {
        BitOutputStream sut = new BitOutputStream();
        sut.append(1);
        byte[] res = sut.toByteArray();

        check(res, "80");
    }


    @Test
    public void testAppendBit10() {
        BitOutputStream sut = new BitOutputStream();
        sut.append(1);
        sut.append(1);
        byte[] res = sut.toByteArray();

        check(res, "c0");
    }


    @Test
    public void testAppendBit101() {
        BitOutputStream sut = new BitOutputStream();
        sut.append(1);
        sut.append(0);
        sut.append(1);
        byte[] res = sut.toByteArray();

        check(res, "a0");
    }


    private void check(byte[] res, String expected) {
        final String f = toHexString(res);
        Assert.assertEquals(expected, f);
    }


}
