package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class CompoundReaderTest {

    @Test
    public void test0() {
        Reader sut = new CompoundReader();
        Assert.assertEquals("", flush(sut));
    }

    @Test
    public void test1() {
        Reader sut = new CompoundReader(new StringReader("hello"));
        Assert.assertEquals("hello", flush(sut));
    }

    @Test
    public void test3() {
        Reader sut = new CompoundReader(new StringReader("hello"),new StringReader(" "),new StringReader("world"));
        Assert.assertEquals("hello world", flush(sut));
    }

    @Test
    public void testReadBuffer1a() throws IOException {
        Reader sut = new CompoundReader(new StringReader("hello"),new StringReader(" "),new StringReader("world"));
        char[] buffer = "123456789".toCharArray();
        int numRead = sut.read(buffer, 1, 7);
        Assert.assertEquals(7, numRead);
        Assert.assertEquals("1hello w9", new String(buffer));
    }

    @Test
    public void testReadBuffer1b() throws IOException {
        Reader sut = new CompoundReader(new StringReader("hello"),new StringReader(" "),new StringReader("world"));
        char[] buffer = "123456789".toCharArray();
        int numRead = sut.read(buffer, 1, 15);
        Assert.assertEquals(8, numRead);
        Assert.assertEquals("1hello wo", new String(buffer));
    }

    @Test
    public void testReadBuffer1c() throws IOException {
        Reader sut = new CompoundReader(new StringReader("hello"),new StringReader(" "),new StringReader("world"));
        char[] buffer = "123456789".toCharArray();
        int numRead = sut.read(buffer, 2, 1);
        Assert.assertEquals(1, numRead);
        Assert.assertEquals("12h456789", new String(buffer));
    }

    @Test
    public void testReadBuffer2() throws IOException {
        Reader sut = new CompoundReader(new StringReader("hello"),new StringReader(" "),new StringReader("world"));
        char[] buffer = "123456789".toCharArray();
        int numRead = sut.read(buffer);
        Assert.assertEquals(9, numRead);
        Assert.assertEquals("hello wor", new String(buffer));
    }


    private String flush(Reader reader) {
        StringBuilder out = new StringBuilder();

        try {
            int c;
            while ((c = reader.read()) != -1) {
                out.append((char) c);
            }
        } catch (IOException e) {
            Assert.fail("Exception + " + e.getMessage());
        }


        return out.toString();
    }
}
