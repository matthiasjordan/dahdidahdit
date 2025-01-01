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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class CompoundReader extends Reader {

    private final Queue<Reader> delegates;

    public CompoundReader(Reader... readers) {
        this.delegates = new ArrayDeque<>(readers.length);
        delegates.addAll(Arrays.asList(readers));
    }


    @Override
    public int read() throws IOException {
        int res;
        do {
            Reader reader = delegates.peek();
            if (reader == null) {
                return -1;
            }

            res = reader.read();

            if (res != -1) {
                return res;
            }

            delegates.poll();
        }
        while (!delegates.isEmpty());

        return res;
    }

    @Override
    public int read(char[] chars, int pos, int len) throws IOException {
        final int max = Math.min(pos + len, chars.length);
        int i;
        for (i = pos; (i < max); i++) {
            int c = read();
            if (c == -1) {
                break;
            }
            chars[i] = (char) c;
        }

        return i - pos;
    }

    @Override
    public void close() throws IOException {
        for (Reader delegate : delegates) {
            delegate.close();
        }
    }
}
