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
