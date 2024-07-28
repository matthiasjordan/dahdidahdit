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

package com.paddlesandbugs.dahdidahdit.brasspound;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clustering for tone timings.
 */
public class Cluster {

    /**
     * Calculates the centroids of the timings.
     *
     * @param tonesO the timings
     *
     * @return a two-element array, with the first element being the dit timing and the second element being the dah timing
     */
    public static long[] calc(List<Long> tonesO) {

        if (tonesO.size() <= 1) {
            return new long[0];
        }

        long max = Collections.max(tonesO);
        long min = Collections.min(tonesO);
        long range = max - min;

        C[] cluster = new C[2];
        cluster[0] = new C(min + (range * 25 / 100));
        cluster[1] = new C(min + (range * 75 / 100));

        assignToClusters(tonesO, cluster);

        cluster[0].recalcCentroid();
        cluster[0].clear();
        cluster[1].recalcCentroid();
        cluster[1].clear();

        assignToClusters(tonesO, cluster);

        cluster[0].recalcCentroid();
        cluster[1].recalcCentroid();

        if (cluster[0].centroid == cluster[1].centroid) {
            return new long[] {cluster[0].centroid};
        }

        long[] res = new long[2];
        res[0] = cluster[0].centroid;
        res[1] = cluster[1].centroid;
        return res;
    }


    private static void assignToClusters(List<Long> tonesO, C[] cluster) {
        for (long tone : tonesO) {
            long diff0 = distance(tone, cluster[0]);
            long diff1 = distance(tone, cluster[1]);

            C target;
            if (diff0 < diff1) {
                target = cluster[0];
            } else {
                target = cluster[1];
            }

            target.data.add(tone);
        }
    }


    private static long distance(long p, C cluster) {
        return Math.abs(p - cluster.centroid);
    }


    private static class C {
        private long centroid;
        private final Set<Long> data = new HashSet<>();


        private C(long centroid) {
            this.centroid = centroid;
        }


        public void recalcCentroid() {
            if (data.size() == 0) {
                return;
            }

            long sum = 0;
            for (long d : data) {
                sum += d;
            }
            centroid = sum / data.size();
        }


        public void clear() {
            data.clear();
        }
    }
}
