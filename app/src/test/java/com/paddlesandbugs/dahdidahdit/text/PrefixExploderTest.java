package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class PrefixExploderTest {

    @Test
    public void test1() {
        Assert.assertEquals("aab", PrefixExploder.findNextPrefix("aaa"));
        Assert.assertEquals("aac", PrefixExploder.findNextPrefix("aab"));
        Assert.assertEquals("abb", PrefixExploder.findNextPrefix("aba"));

        // Rollover
        Assert.assertEquals("aba", PrefixExploder.findNextPrefix("aaz"));

        // Two rollovers
        Assert.assertEquals("baa", PrefixExploder.findNextPrefix("azz"));

        // End of line
        Assert.assertEquals(null, PrefixExploder.findNextPrefix("zzz"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("aa2", PrefixExploder.findNextPrefix("aa1"));
        Assert.assertEquals("aa3", PrefixExploder.findNextPrefix("aa2"));
        Assert.assertEquals("a1b", PrefixExploder.findNextPrefix("a1a"));

        // Rollover
        Assert.assertEquals("a2a", PrefixExploder.findNextPrefix("a1z"));
        Assert.assertEquals("ab0", PrefixExploder.findNextPrefix("aa9"));

        // Two rollovers
        Assert.assertEquals("2aa", PrefixExploder.findNextPrefix("1zz"));
    }


    @Test
    public void testExlode1() {
        Assert.assertEquals(List.of("aa", "ab", "ac", "ad"), PrefixExploder.explodePrefixes("aa-ad"));
        Assert.assertEquals(List.of("ax", "ay", "az", "ba", "bb"), PrefixExploder.explodePrefixes("ax-bb"));
        Assert.assertEquals(List.of("4a", "4b", "4c"), PrefixExploder.explodePrefixes("4a-4c"));
        Assert.assertEquals(List.of("y2", "y3", "y4"), PrefixExploder.explodePrefixes("y2-y4"));
        Assert.assertEquals(List.of("y8", "y9", "z0", "z1", "z2"), PrefixExploder.explodePrefixes("y8-z2"));
    }
}