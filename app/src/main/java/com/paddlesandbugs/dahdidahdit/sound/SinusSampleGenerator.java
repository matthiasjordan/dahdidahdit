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

import com.paddlesandbugs.dahdidahdit.Const;

public class SinusSampleGenerator implements SampleGenerator {

    private static final int sampleRate = Const.SAMPLES_PER_S;
    private static final float twoPi = (float) 2 * (float) Math.PI;

    private final float dbFreq;
    private final float vol;

    private final float f;
    private float a = 0;


    public SinusSampleGenerator(int dbFreq, float vol) {
        this.dbFreq = (float) dbFreq;
        this.vol = vol;
        this.f = (float) (twoPi / sampleRate * dbFreq);
    }


    @Override
    public short generate() {
        float s = vol * (float) Math.sin(a);

        short sample = (short) (s * Short.MAX_VALUE);

        a = a + f % twoPi;

        return sample;
    }

}
