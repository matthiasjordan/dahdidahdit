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
import java.util.List;

public class PrefixExploder {
    public static final String RANGE_CHAR = "-";

    static String findNextPrefix(String current) {
        char[] chars = current.toCharArray();
        int currentIndex = chars.length - 1;
        boolean finished = false;

        do {
            if (chars[currentIndex] == 'z') {
                chars[currentIndex] = 'a';
                currentIndex -= 1;
            } else if (chars[currentIndex] == 'Z') {
                chars[currentIndex] = 'A';
                currentIndex -= 1;
            } else if (chars[currentIndex] == '9') {
                chars[currentIndex] = '0';
                currentIndex -= 1;
            } else {
                chars[currentIndex] += 1;
                finished = true;
            }

            if (currentIndex < 0) {
                return null;
            }
        } while (!finished);

        return new String(chars);
    }

    public static List<String> explodePrefixes(String spec) {
        if (!spec.contains(RANGE_CHAR)) {
            return List.of(spec);
        }

        String[] parts = spec.split(RANGE_CHAR);
        if (parts.length != 2) {
            return List.of(parts[0]);
        }

        ArrayList<String> res = new ArrayList<>();
        final String last = parts[1];
        String current = parts[0];
        do {
            res.add(current);
            current = findNextPrefix(current);
        }
        while ((current != null) && !current.equals(last));
        res.add(last);

        return res;
    }
}
