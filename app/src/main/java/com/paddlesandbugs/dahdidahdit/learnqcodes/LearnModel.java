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

import java.util.concurrent.TimeUnit;

public class LearnModel {

    private static final int DURATION_FIRST_REP_MINUTES = 2;

    private static final int DURATION_SECOND_LEARN_REP_MINUTES = 10;


    public static final float INITIAL_EASINESS = 2.5f;
    private static final float MIN_EASINESS = 1.3f;


    public static void adjust(Fact f, DataPoint d) {
        adjustShowDate(f, d);

        if (d.score != DataPoint.Score.NOT_AT_ALL) {
            adjustEasiness(f, d);
        }
    }


    private static void adjustShowDate(Fact f, DataPoint d) {
        long now = d.timestampMs;

        if (d.score == DataPoint.Score.NOT_AT_ALL) {
            f.repNo = 1;
        } else {
            f.repNo += 1;
        }

        if (f.repNo == 1) {
            f.intervalMs = TimeUnit.MINUTES.toMillis(DURATION_FIRST_REP_MINUTES);
        } else if (f.repNo == 2) {
            f.intervalMs = TimeUnit.MINUTES.toMillis(DURATION_SECOND_LEARN_REP_MINUTES);
        } else {
            f.intervalMs = (int) Math.ceil((float) f.intervalMs * f.easiness);
        }

        if (d.score == DataPoint.Score.EFFORTLESS) {
            f.intervalMs = (int) Math.ceil((float) f.intervalMs * 1.1f);
        }

        f.nextShowDateMs = now + f.intervalMs;
    }


    private static void adjustEasiness(Fact f, DataPoint d) {
        final int diff = d.score.getDifficulty();
        f.easiness = f.easiness + (0.1f - diff * (0.08f + diff * 0.02f));
        if (f.easiness < MIN_EASINESS) {
            f.easiness = MIN_EASINESS;
        }
    }
}
