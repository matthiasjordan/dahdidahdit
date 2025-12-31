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

import java.util.HashMap;
import java.util.Map;

public class TextUtils {

    private TextUtils() {
        // Nothing.
    }


    public static Map<MorseCode.CharacterData, Integer> count(String text) {
        final MorseCode.MutableCharacterList charList = new MorseCode.MutableCharacterList(text);
        return count(charList);
    }


    public static Map<MorseCode.CharacterData, Integer> count(MorseCode.CharacterList text) {
        Map<MorseCode.CharacterData, Integer> map = new HashMap<>();

        for (MorseCode.CharacterData c : text) {
            Integer count = map.get(c);
            if (count == null) {
                count = 0;
            }
            count += 1;
            map.put(c, count);
        }

        return map;
    }

}
