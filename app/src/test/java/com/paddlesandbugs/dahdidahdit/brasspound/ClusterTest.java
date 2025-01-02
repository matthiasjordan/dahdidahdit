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

package com.paddlesandbugs.dahdidahdit.brasspound;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;

public class ClusterTest {

    @Test
    public void test0() {
        long[] res = Cluster.calc(Arrays.asList(2000L, 5000L));
        Assert.assertArrayEquals(new long[]{2000L, 5000L}, res);
    }


    @Test
    public void test1() {
        long[] res = Cluster.calc(Arrays.asList(2000L, 5000L, 2100L, 2200L, 5300L, 5050L));
        Assert.assertArrayEquals(new long[]{2100L, 5116L}, res);
    }


    @Test
    public void testRandom1() {
        ArrayList<Long> data = new ArrayList<>();
        data.addAll(createTimings(200, 2000, 200));
        data.addAll(createTimings(200, 5000, 200));
        long[] res = Cluster.calc(data);
        check(res[0], 2000, 50);
        check(res[1], 5000, 50);
    }


    @Test
    public void testRandom2() {
        MorseTiming timing = MorseTiming.get(16, 16);
        ArrayList<Long> data = new ArrayList<>();
        data.addAll(createTimings(200, timing.ditD, timing.ditD / 2));
        data.addAll(createTimings(200, timing.dahD, timing.dahD / 3));
        long[] res = Cluster.calc(data);
        check(res[0], timing.ditD, 10);
        check(res[1], timing.dahD, 10);
    }


    private void check(long res, long expected, int e) {
        Assert.assertTrue((res >= (expected - e)) && (res <= (expected + e)));
    }


    List<Long> createTimings(int count, long centroid, int spread) {
        ArrayList<Long> res = new ArrayList<>();

        Random r = new Random();
        final int halfSpread = spread / 2;
        for (int i = 0; (i < count); i++) {
            res.add(centroid + r.nextInt(spread) - halfSpread);
        }

        return res;
    }


}
