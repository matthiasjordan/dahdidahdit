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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WordIterator implements Iterator<String> {

    private final List<String> words;
    private final Iterator<String> it;


    public WordIterator(String text) {
        words = Arrays.asList(text.split("\\s+"));
        it = words.iterator();
    }


    @Override
    public boolean hasNext() {
        return it.hasNext();
    }


    @Override
    public String next() {
        return it.next();
    }
}
