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

import java.util.Collections;
import java.util.Iterator;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

/**
 * Contains a static, predefined text that it returns piece by piece.
 */
public class StaticTextGenerator extends AbstractTextGenerator implements TextGenerator {

    private final MorseCode.CharacterList res;
    private Iterator<TextPart> it;

    private final boolean repeat;

    private boolean isPrinted = true;


    public StaticTextGenerator() {
        this(new MorseCode.MutableCharacterList("cq dx"), false);
    }


    public StaticTextGenerator(String text) {
        this(text, false);
    }


    public StaticTextGenerator(String text, boolean repeat) {
        this(new MorseCode.MutableCharacterList((text == null) ? "" : text.replaceAll("(\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029])+", "\r")), repeat);
    }


    public StaticTextGenerator(MorseCode.CharacterList text, boolean repeat) {
        this.res = text;
        this.repeat = repeat;
        if (repeat) {
            text.add(MorseCode.WORDBREAK);
        }
    }


    public static StaticTextGenerator createUnprinted(String text) {
        StaticTextGenerator res = new StaticTextGenerator(text, false);
        res.isPrinted = false;
        return res;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_mode_static;
    }


    @Override
    public boolean hasNext() {
        return getIt().hasNext();
    }


    @Override
    public TextPart next() {
        return getIt().next();
    }


    private Iterator<TextPart> getIt() {
        if (!isClosed()) {
            if ((it == null) || (repeat && !it.hasNext())) {
                it = new Iterator<TextPart>() {
                    private final Iterator<MorseCode.CharacterData> charIt = res.iterator();


                    @Override
                    public boolean hasNext() {
                        return charIt.hasNext();
                    }


                    @Override
                    public TextPart next() {
                        MorseCode.CharacterData d = charIt.next();
                        final TextPart textPart = new TextPart(d);
                        textPart.setIsPrinted(isPrinted);
                        return textPart;
                    }
                };
            }
        } else {
            return Collections.emptyIterator();
        }

        return it;
    }
}
