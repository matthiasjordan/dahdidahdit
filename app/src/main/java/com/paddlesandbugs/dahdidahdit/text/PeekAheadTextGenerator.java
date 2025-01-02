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

import java.util.LinkedList;
import java.util.List;

public class PeekAheadTextGenerator implements TextGenerator {

    /**
     * Keeps {@link com.paddlesandbugs.dahdidahdit.text.TextGenerator.TextPart} objects that have been read from the delegate but not yet returned to the
     * client.
     */
    private final List<TextPart> queue = new LinkedList<>();

    /**
     * The source of the {@link com.paddlesandbugs.dahdidahdit.text.TextGenerator.TextPart} objects we deal with.
     */
    private final TextGenerator delegate;




    public PeekAheadTextGenerator(TextGenerator delegate) {
        this.delegate = delegate;
    }


    @Override
    public int getTextID() {
        return delegate.getTextID();
    }


    @Override
    public void close() {
        delegate.close();
    }


    @Override
    public void setWordLengthMax(int maxWordLength) {
        delegate.setWordLengthMax(maxWordLength);
    }


    @Override
    public boolean hasNext() {
        return (!queue.isEmpty() || delegate.hasNext());
    }


    @Override
    public TextPart next() {
        if (!queue.isEmpty()) {
            return queue.remove(0);
        }

        return delegate.next();
    }


    public TextPart peek(int lookAhead) {
        final int needeParts = lookAhead + 1;
        while ((needeParts > queue.size()) && delegate.hasNext()) {
            queue.add(delegate.next());
        }

        return queue.get(lookAhead);
    }

}
