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

import android.content.Context;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

public class AprilFoolsGenerator extends AbstractTextGenerator implements TextGenerator {

    static final String msg = "the wow signal was me = ha = hpi april fools day ";

    private final int textID;

    private final TextGenerator tg;


    public AprilFoolsGenerator(Context context) {
        MorseCode.CharacterList chars = new MorseCode.MutableCharacterList();

        RandomTextGenerator rtg = RandomTextGenerator.createKochTextGenerator(MainActivity.getCopyTrainer(context), 20);

        for (int i = 0; (i < 10); i++) {
            chars.add(rtg.next().getChar());
        }

        MorseCode.CharacterList s = new MorseCode.MutableCharacterList(msg);
        chars.append(s);

        for (int i = 0; (i < 30); i++) {
            if (rtg.hasNext()) {
                chars.add(rtg.next().getChar());
            }
        }

        tg = new StaticTextGenerator(chars, false);
        this.textID = rtg.getTextID();
    }


    @Override
    public int getTextID() {
        return textID;
    }


    @Override
    public void close() {
        tg.close();
    }


    @Override
    public boolean hasNext() {
        return tg.hasNext();
    }


    @Override
    public TextPart next() {
        return tg.next();
    }
}
