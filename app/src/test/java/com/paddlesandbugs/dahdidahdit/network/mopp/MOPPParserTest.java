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

import static com.paddlesandbugs.dahdidahdit.TestingUtils.toBytes;

import org.junit.Assert;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.TestingUtils;

public class MOPPParserTest {

    @Test
    public void testToMOPP1() {
        Packet packet = new Packet(new MorseCode.MutableCharacterList("qrl?"), 16);
        byte[] res = MOPPParser.toMOPP(packet);
        final String x = TestingUtils.toHexString(res);
        System.out.println(x);
        Assert.assertEquals("5b429864651697", x);
    }

    @Test
    public void testToMOPP2() {
        Packet packet = new Packet(new MorseCode.MutableCharacterList("dl4mat"), 16);
        byte[] res = MOPPParser.toMOPP(packet);
        final String x = TestingUtils.toHexString(res);
        System.out.println(x);
        Assert.assertEquals("5b425194558a18b0", x);
    }


    @Test
    public void testToMOPPMultipleWords() {
        Packet packet = new Packet(new MorseCode.MutableCharacterList("cq mt de de1bug"), 18);
        byte[] res = MOPPParser.toMOPP(packet);
        final String x = TestingUtils.toHexString(res);
        System.out.println(x);
        Assert.assertEquals("5b4a64a60a209442511aa25458a7", x);
    }


    @Test
    public void testFromMOPP1() {
        int[] data = new int[]{0x6f, 0x3a, 0x98, 0x64, 0x65, 0x16, 0x97};
        byte[] bytes = toBytes(data);

        Packet res = MOPPParser.fromMOPP(bytes);
        Assert.assertEquals("qrl?", res.getCharacters().asString());
        Assert.assertEquals(14, res.getWpm());
    }


    @Test
    public void testFromMOPP2() {
        int[] data = new int[]{0x6d, 0x42, 0x6c};
        byte[] bytes = toBytes(data);

        Packet res = MOPPParser.fromMOPP(bytes);
        Assert.assertEquals("k", res.getCharacters().asString());
        Assert.assertEquals(16, res.getWpm());

    }


    @Test
    public void testFromMOPP3() {
        int[] data = new int[]{0x4d, 0x42, 0x51};
        byte[] bytes = toBytes(data);

        Packet res = MOPPParser.fromMOPP(bytes);
        Assert.assertEquals("de", res.getCharacters().asString());
        Assert.assertEquals(16, res.getWpm());
    }


    @Test
    public void testFromMOPP4() {
        int[] data = new int[]{0x5b, 0x42, 0x51, 0x94, 0x55, 0x8a, 0x18, 0xb0};
        byte[] bytes = toBytes(data);

        Packet res = MOPPParser.fromMOPP(bytes);
        Assert.assertEquals("dl4mat", res.getCharacters().asString());
        Assert.assertEquals(16, res.getWpm());
    }


    @Test
    public void testRoundTrip() {
        final MorseCode.MutableCharacterList input = new MorseCode.MutableCharacterList("t");
        byte[] mopp = MOPPParser.toMOPP(new Packet(input, 16));
        System.out.println(TestingUtils.toHexString(mopp));
        Packet res = MOPPParser.fromMOPP(mopp);
        System.out.println(res);
        Assert.assertEquals("t", res.getCharacters().asString());
        Assert.assertEquals(16, res.getWpm());
    }


    /**
     * This is actually expected. MOPP only transfers single words. So multiple words need splitting up into multiple MOPP messages.
     */
    @Test
    public void testRoundTripMultipleWords() {
        final MorseCode.MutableCharacterList input = new MorseCode.MutableCharacterList("cq mt de de1bug");
        byte[] mopp = MOPPParser.toMOPP(new Packet(input, 16));
        System.out.println(TestingUtils.toHexString(mopp));
        Packet res = MOPPParser.fromMOPP(mopp);
        System.out.println(res);
        Assert.assertEquals("cqmtdede1bug", res.getCharacters().asString());
        Assert.assertEquals(16, res.getWpm());
    }


}
