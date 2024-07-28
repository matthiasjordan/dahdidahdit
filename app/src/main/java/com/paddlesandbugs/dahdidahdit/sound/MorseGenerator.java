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

package com.paddlesandbugs.dahdidahdit.sound;

import java.util.Arrays;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

public interface MorseGenerator {


    static class Part {
        /**
         * A buffer with PCM samples.
         */
        public short[] sample;

        /**
         * The corresponding text.
         */
        public MorseCode.CharacterList text;

        /**
         * Is this supposed to be printed?
         */
        public boolean isPrinted = true;


        @Override
        public String toString() {
            return "Part{text=\"" + text + "\"" + " sample=" + Arrays.toString(sample) + '}';
        }
    }


    /**
     * Generates a part
     *
     * @return the next part
     */
    Part generate();


    /**
     * Closes the underlying {@link TextGenerator}, making the {@link MorseGenerator} cease operation at the next sensible point in time.
     */
    void close();
}
