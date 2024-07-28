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

package com.paddlesandbugs.dahdidahdit.learnqcodes;

public class DataPoint {

    public enum Score {
        NOT_AT_ALL(2),
        WITH_EFFORT(1),
        EFFORTLESS(0);

        private final int difficulty;


        Score(int level) {
            this.difficulty = level;
        }


        public int getDifficulty() {
            return difficulty;
        }
    }

    public final long timestampMs;
    public final Score score;


    public DataPoint(long timestampMs, Score score) {
        this.timestampMs = timestampMs;
        this.score = score;
    }
}
