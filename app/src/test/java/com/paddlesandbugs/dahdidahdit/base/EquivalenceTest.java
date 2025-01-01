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

package com.paddlesandbugs.dahdidahdit.base;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

public class EquivalenceTest {

    @Test
    public void testNX() {
        Equivalence<String> sut = new Equivalence<>();

        sut.put("hallo", "hello");
        sut.put("morgen", "morning");
        sut.put("morgen", "asa");

        Assert.assertEquals(Sets.newSet(), sut.get("nx"));

    }


    @Test
    public void test1() {
        Equivalence<String> sut = new Equivalence<>();

        sut.put("hallo", "hello");

        Assert.assertEquals(Sets.newSet("hallo", "hello"), sut.get("hallo"));
        Assert.assertEquals(Sets.newSet("hallo", "hello"), sut.get("hello"));
    }


    @Test
    public void test2() {
        Equivalence<String> sut = new Equivalence<>();

        sut.put("morgen", "morning");
        sut.put("morgen", "asa");

        Assert.assertEquals(Sets.newSet("morgen", "morning", "asa"), sut.get("morgen"));
        Assert.assertEquals(Sets.newSet("morgen", "morning", "asa"), sut.get("morning"));
        Assert.assertEquals(Sets.newSet("morgen", "morning", "asa"), sut.get("asa"));
    }

    @Test
    public void testJoin() {
        Equivalence<String> sut = new Equivalence<>();

        sut.put("a", "b");
        sut.put("a", "c");
        sut.put("c", "d");

        Assert.assertEquals(Sets.newSet("a", "b", "c", "d"), sut.get("b"));


        sut.put("1", "2");
        sut.put("2", "3");
        sut.put("3", "4");

        Assert.assertEquals(Sets.newSet("1", "2", "3", "4"), sut.get("2"));

        sut.put("b", "2");

        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("1"));
        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("2"));
        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("3"));
        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("4"));
        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("a"));
        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("b"));
        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("c"));
        Assert.assertEquals(Sets.newSet("1", "2", "3", "4", "a", "b", "c", "d"), sut.get("d"));
        Assert.assertEquals(Sets.newSet(), sut.get("e"));

    }


}
