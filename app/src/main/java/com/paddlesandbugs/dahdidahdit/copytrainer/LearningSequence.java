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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public interface LearningSequence {


    /**
     * Gets character(s) at level lvl.
     * <p>
     * Returns a list because there might be multiple characters at any level - e.g. Koch has 2 characters on level 1.
     * <p>
     * The returned list can be changed at will.
     *
     * @param lvl the level
     *
     * @return the character list
     */
    MorseCode.CharacterList getChar(int lvl);


    /**
     * The maximal level of this sequence.
     * <p>
     * This is the greatest level that {@link #getChar(int)} will return data for
     *
     * @return the maximal level
     */
    int getMax();


    /**
     * The preferences key infix to store information about this sequence.
     * <p>
     * This is mostly used for routing the user to the learn new characters activity and for keeping track the user's progress.
     *
     * @return the preferences key infix
     */
    String getPrefsKeyInfix();
}
