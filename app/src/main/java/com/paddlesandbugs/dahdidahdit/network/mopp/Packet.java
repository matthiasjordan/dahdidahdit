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

package com.paddlesandbugs.dahdidahdit.network.mopp;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public class Packet {
    private final MorseCode.CharacterList characters;

    private final int wpm;


    public Packet(MorseCode.CharacterList characters, int wpm) {
        this.characters = new MorseCode.MutableCharacterList(characters);
        this.wpm = wpm;
    }


    public Packet(String text, int wpm) {
        this(new MorseCode.MutableCharacterList(text), wpm);
    }


    public MorseCode.CharacterList getCharacters() {
        return characters;
    }


    public int getWpm() {
        return wpm;
    }
}
