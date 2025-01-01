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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Wraps a {@link TextGenerator} with "vvv&lt;ka&gt;" and "&lt;ar&gt;".
 */
public class VvvKaArDecorator implements TextGenerator {

    private final ArrayList<TextGenerator> generators = new ArrayList<>();

    private final Iterator<TextGenerator> iterator;

    private final StaticTextGenerator prefix = StaticTextGenerator.createUnprinted("vvv<ka> ");
    private final StaticTextGenerator suffix = StaticTextGenerator.createUnprinted(" <ar>");

    private TextGenerator current;
    private TextGenerator delegate;


    /**
     * Creates a decorator for the given {@link TextGenerator}.
     *
     * @param delegate the generator to decorate with prefix and suffix
     */
    public VvvKaArDecorator(TextGenerator delegate) {
        final StaticTextGenerator prefix = StaticTextGenerator.createUnprinted("vvv<ka> ");
        final StaticTextGenerator suffix = StaticTextGenerator.createUnprinted(" <ar>");

        generators.add(prefix);
        generators.add(delegate);
        generators.add(suffix);

        iterator = generators.iterator();

        this.delegate = delegate;
    }


    @Override
    public int getTextID() {
        return delegate.getTextID();
    }


    @Override
    public void close() {
        int i = 0;
        int max = generators.size() - 1;
        for (TextGenerator tg : generators) {
            if (i < max) {
                tg.close();
            }
            i += 1;
        }
    }


    @Override
    public void setWordLengthMax(int maxWordLength) {
        delegate.setWordLengthMax(maxWordLength);
    }


    @Override
    public boolean hasNext() {
        advance();
        return current.hasNext();
    }


    private void advance() {
        if (current == null) {
            if (iterator.hasNext()) {
                current = iterator.next();
            }
        }

        while (!current.hasNext() && iterator.hasNext()) {
            current = iterator.next();
        }
    }


    @Override
    public TextPart next() {
        advance();
        return current.next();

    }
}
