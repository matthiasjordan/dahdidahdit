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

import static com.paddlesandbugs.dahdidahdit.brasspound.Cluster.calc;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;

public class SpeedSensingDecoder implements Decoder {

    private final StaticDecoder delegate;

    private final TimingLinkedList timings = new TimingLinkedList();

    private long lastTimestampMs;


    public SpeedSensingDecoder(StaticDecoder decoder) {
        this.delegate = decoder;
        this.lastTimestampMs = now();
    }


    long now() {
        return System.currentTimeMillis();
    }


    @Override
    public void register(CharListener l) {
        delegate.register(l);
    }


    @Override
    public void keyDown() {
        long t = getT();
        timings.add(false, t);
        detectWPM();
        delegate.keyDown();
    }


    @Override
    public void keyUp() {
        long t = getT();
        timings.add(true, t);
        detectWPM();
        delegate.keyUp();
    }


    private void detectWPM() {
        ArrayList<Long> tonesO = new ArrayList<>();
        for (Timing timing : timings) {
            if (timing.keyDown) {
                tonesO.add(timing.timeMs);
            }
        }

        long[] centroids = calc(tonesO);
        final boolean twoClusters = centroids.length == 2;
        if (twoClusters) {
            final long ditMs = centroids[0];
            final long dahMs = centroids[1];
            final boolean clusterDistanceOk = (dahMs > (ditMs * 2)) && (dahMs < (ditMs * 4));
            if (clusterDistanceOk) {
                final long smoothedDitMs = dahMs / 3;
                int wpm = MorseTiming.calcWpm((int) Math.min(Integer.MAX_VALUE, smoothedDitMs));
                Log.i("SSD", "Auto-setting speed to " + wpm);
                delegate.setSpeed(wpm);
            }
        }
    }


    private long getT() {
        long now = now();
        long t = now - lastTimestampMs;
        lastTimestampMs = now;
        return t;
    }


    public List<Timing> getTimings() {
        return timings;
    }


    private static class Timing {
        private boolean keyDown;
        private long timeMs;


        @Override
        public String toString() {
            return "{" + "keyDown=" + keyDown + ", timeMs=" + timeMs + '}';
        }
    }

    private static class TimingLinkedList extends LinkedList<Timing> {
        private final int max = 20;


        @Override
        public boolean add(Timing o) {
            if (size() == max) {
                remove(0);
            }
            return super.add(o);
        }


        public boolean add(boolean keyDown, long timeMs) {
            Timing t = new Timing();
            t.keyDown = keyDown;
            t.timeMs = timeMs;
            return add(t);
        }
    }
}
