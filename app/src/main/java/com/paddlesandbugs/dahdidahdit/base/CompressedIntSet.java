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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class CompressedIntSet {

    private static class Range {
        private int start;
        private int end;


        private Range(int start, int end) {
            this.start = start;
            this.end = end;
        }


        private Range(int value) {
            this.start = value;
            this.end = value;
        }


        public boolean contains(int i) {
            return ((start <= i) && (i <= end));
        }


        public boolean isAdjacentBefore(Range other) {
            return ((end + 1) == other.start);
        }


        public boolean isAdjacentAfter(Range other) {
            return ((other.end + 1) == start);
        }


        public void merge(Range other) {
            start = Math.min(start, other.start);
            end = Math.max(end, other.end);
        }


        @Override
        @NonNull
        public String toString() {
            return "(" + start + ", " + end + ')';
        }
    }

    private final LinkedList<Range> ranges = new LinkedList<>();


    public int size() {
        int count = 0;
        for (Range range : ranges) {
            count += (range.end - range.start + 1);
        }
        return count;
    }


    public boolean isEmpty() {
        return ranges.isEmpty();
    }


    public boolean contains(@Nullable Object o) {
        if (!(o instanceof Integer)) {
            return false;
        }
        int value = (Integer) o;

        for (Range current : ranges) {

            if (current.contains(value)) {
                return true;
            }

            if (current.start > value) {
                break;
            }
        }

        return false;
    }


    public boolean add(Integer integer) {
        Range newRange = new Range(integer);
        ranges.add(newRange);
        pack();
        return false;
    }


    private void pack() {
        if (ranges.size() < 2) {
            return;
        }

        /*
        (1,1) (2,3)
        (2,3) (4,4)
         */
        Collections.sort(ranges, new Comparator<Range>() {
            @Override
            public int compare(Range a, Range b) {
                return Integer.compare(a.start, b.start);
            }
        });

        int currentI = 0;
        Range prev = null;
        final int top = ranges.size() - 1;
        Iterator<Range> it = ranges.iterator();
        while (it.hasNext() && (currentI <= top)) {
            Range current = it.next();
            if (prev != null) {
                if (prev.isAdjacentBefore(current)) {
                    break;
                }
            }

            prev = current;
            currentI += 1;
        }

        if (currentI <= top) {
            merge(currentI);
            if ((ranges.size() >= 2) && (currentI < top)) {
                Range p = ranges.get(currentI - 1);
                Range n = ranges.get(currentI);
                if (p.isAdjacentBefore(n)) {
                    merge(currentI);
                }
            }
        }
    }


    private void merge(int currentI) {
        Range a = ranges.get(currentI - 1);
        Range b = ranges.get(currentI);

        a.merge(b);
        ranges.remove(currentI);
    }


    public void clear() {
        ranges.clear();
    }


    @Override
    public String toString() {
        return Objects.toString(ranges);
    }


    private static final char RECORD_SEP = '|';
    private static final String BOUNDARY_SEP = ";";


    public static CompressedIntSet fromString(String str) {
        CompressedIntSet set = new CompressedIntSet();

        if ((str == null) || str.isEmpty()) {
            return set;
        }

        int len = str.length();
        int start = 0;
        for (int i = 0; (i <= len); i++) {
            if ((i == len) || (str.charAt(i) == RECORD_SEP)) {
                String record = str.substring(start, i);
                start = i + 1;

                String[] parts = record.split(BOUNDARY_SEP);
                Range r;
                if (parts.length == 1) {
                    r = new Range(Integer.parseInt(parts[0]));
                } else {
                    r = new Range(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                }
                set.ranges.add(r);
            }
        }

        return set;
    }


    public String asString() {
        StringBuilder b = new StringBuilder();
        for (Range range : ranges) {
            if (b.length() != 0) {
                b.append(RECORD_SEP);
            }
            if (range.start == range.end) {
                b.append(Integer.toString(range.start));
            } else {
                b.append(Integer.toString(range.start)).append(BOUNDARY_SEP).append(Integer.toString(range.end));
            }
        }

        return b.toString();
    }
}
