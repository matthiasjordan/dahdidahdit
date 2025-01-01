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

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class CountedWordsTextGenerator implements TextGenerator {

    private final PeekAheadTextGenerator delegate;


    private int wordsToGo;


    public CountedWordsTextGenerator(TextGenerator delegate, int count) {
        this.delegate = new PeekAheadTextGenerator(delegate);
        this.wordsToGo = count;
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
        return !(wordsToGo == 0);
    }


    @Override
    public TextPart next() {
        final TextPart textPart = delegate.next();
        if (nextCharIsWordbreak()) {
            wordsToGo -= 1;
        }

        return textPart;
    }


    private boolean nextCharIsWordbreak() {
        return delegate.peek(0).getChar().equals(MorseCode.WORDBREAK);
    }

}
