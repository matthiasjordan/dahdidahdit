/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2024 Matthias Jordan <matthias@paddlesandbugs.com>

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

package com.paddlesandbugs.dahdidahdit.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Equivalence<T> {

    private final HashMap<T, HashSet<T>> equivs = new HashMap<>();


    public void put(T t1, T t2) {
        HashSet<T> tSet1 = equivs.get(t1);
        HashSet<T> tSet2 = equivs.get(t2);

        if ((tSet1 != null) && (tSet2 != null)) {
            // We know equivalence sets for both elements, so we join the sets
            tSet1.addAll(tSet2);
            for (T t : tSet2) {
                equivs.put(t, tSet1);
            }
        } else if ((tSet1 == null) && (tSet2 == null)) {
            // We know neither element, so we create a new set
            tSet1 = new HashSet<>();
            tSet1.add(t1);
            tSet1.add(t2);
            equivs.put(t1, tSet1);
            equivs.put(t2, tSet1);
        } else {

            if (tSet1 != null) {
                tSet1.add(t2);
                equivs.put(t2, tSet1);
            }

            if (tSet2 != null) {
                tSet2.add(t1);
                equivs.put(t1, tSet2);
            }

        }

    }


    public Set<T> get(T t) {
        HashSet<T> s = equivs.get(t);
        if (s == null) {
            s = new HashSet<>();
        }
        return Collections.unmodifiableSet(s);
    }
}
