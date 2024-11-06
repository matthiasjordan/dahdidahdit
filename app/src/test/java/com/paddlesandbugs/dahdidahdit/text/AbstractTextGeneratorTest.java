/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2024 Matthias Jordan <matthias@paddlesandbugs.com>

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

import androidx.annotation.NonNull;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTextGeneratorTest {

    /**
     * Simple fake for a raw resource.
     *
     * @param content the content to make the fake resource to contain
     * @return the faked resource stream
     */
    @NonNull
    protected static ByteArrayInputStream fakeRawResource(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Fake that returns a fresh stream each time the resource is opened.
     *
     * @param content the content to make the fake resource to contain
     * @return an {@link Answer} containing the faked resource stream
     */
    @NonNull
    protected static Answer<ByteArrayInputStream> fakeRawResourceMulti(final String content) {
        return new Answer<>() {

            @Override
            public ByteArrayInputStream answer(InvocationOnMock invocation) {
                return new ByteArrayInputStream(content.getBytes());
            }
        };
    }

    /**
     * Fake that returns a fresh stream each time the resource is opened.
     *
     * @param fileName the file name of the content to make the fake resource to contain, relative to the root of the JAR/APK files (i.g. if you want R.raw.foo, you pass "/raw/foo").
     * @return an {@link Answer} containing the faked resource stream
     */
    @NonNull
    protected static Answer<InputStream> fakeRawFileResourceMulti(final String fileName) {
        return new Answer<>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) {
                return getClass().getResourceAsStream(fileName);
            }
        };
    }

}