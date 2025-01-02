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

package com.paddlesandbugs.dahdidahdit;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Distribution<T> {

    public interface Drawable<T> {
        T next();
    }

    private static class BaseItem<T> {
        private final T event;
        private float weight;


        BaseItem(T event, float weight) {
            this.event = event;
            this.weight = weight;
        }


        @Override
        public String toString() {
            return "B{" + event + "=" + weight + '}';
        }
    }


    public static class Compiled<T> implements Drawable<T> {


        public static class CompiledItem<T> {
            private final T event;
            private final int weightStart;
            private final int weightEnd;


            public CompiledItem(T event, int start, int end) {
                this.event = event;
                this.weightStart = start;
                this.weightEnd = end;
            }


            @Override
            public String toString() {
                return "{" + event + "=" + (weightEnd - weightStart) + " [" + weightStart + ", " + weightEnd + "[}";
            }
        }

        private final List<CompiledItem<T>> items = new ArrayList<>();
        private final List<T> events = new ArrayList<>();

        private final int maxNum;

        private final Random random = new Random();


        public Compiled(Collection<CompiledItem<T>> items) {
            this.items.addAll(items);
            int max = 0;
            for (CompiledItem<T> item : items) {
                events.add(item.event);
                if (item.weightEnd > max) {
                    max = item.weightEnd;
                }
            }
            this.maxNum = max;

            if (this.maxNum <= 0) {
                throw new IllegalArgumentException("Empty distribution");
            }
        }


        public T next() {
            int i = random.nextInt(maxNum);
            for (CompiledItem<T> item : items) {
                if ((item.weightStart <= i) && (i < item.weightEnd)) {
                    return item.event;
                }
            }
            return null;
        }


        public int size() {
            return items.size();
        }


        public List<T> events() {
            return events;
        }


        @Override
        public String toString() {
            return "CompiledDistribution{" + "items=" + items + '}';
        }
    }


    public static class RoundRobin<T> implements Drawable<T> {

        private final List<Object> items = new ArrayList<>();
        private int i = 0;


        public void add(T event) {
            items.add(event);
        }


        public void add(Drawable<T> dist) {
            items.add(dist);
        }


        public T next() {
            T res = null;
            Object next = items.get(i);
            if (next instanceof Drawable) {
                res = ((Drawable<T>) next).next();
            } else {
                res = (T) next;
            }

            i += 1;
            if (i >= items.size()) {
                i = 0;
            }
            return res;
        }
    }

    private static final float BASE_WEIGHT = 1.0f;
    private final Map<T, BaseItem<T>> eventToBase;


    private Distribution<T> chained;


    public Distribution(T... events) {
        this(Arrays.asList(events));
    }


    /**
     * Creates a uniform distribution.
     *
     * @param events the events that make the event space, each of which initially is assigned a uniform weight
     */
    public Distribution(Collection<T> events) {
        HashMap<T, BaseItem<T>> eventToBase = new HashMap<>();
        for (T event : events) {
            eventToBase.put(event, new BaseItem<>(event, BASE_WEIGHT));
        }
        this.eventToBase = eventToBase;
    }


    public Distribution() {
        this(Collections.emptyList());
    }


    public Distribution<T> chain(Distribution<T> other) {
        compareMaps(other.eventToBase, this.eventToBase);
        compareMaps(this.eventToBase, other.eventToBase);

        this.chained = other;
        return this;
    }


    private void compareMaps(Map<T, BaseItem<T>> otherETB, Map<T, BaseItem<T>> thisETB) {
        for (BaseItem<T> event : otherETB.values()) {
            BaseItem<T> lEvent = thisETB.get(event.event);
            if (lEvent == null) {
                throw new IllegalArgumentException("Incompatible distributions");
            }
        }
    }


    private float condense() {
        if (chained != null) {
            chained.condense();
        }

        float max = Float.MIN_VALUE;
        float sum = 0;
        for (BaseItem<T> event : eventToBase.values()) {
            if (chained != null) {
                BaseItem<T> chainedEvent = chained.eventToBase.get(event.event);
                if (chainedEvent != null) {
                    event.weight *= chainedEvent.weight;
                }
            }

            if (event.weight > max) {
                max = event.weight;
            }
            sum += event.weight;
        }

        chained = null;

        return sum;
    }


    public Compiled<T> compile() {
        float sum = condense();
        float factor = (int) Math.floor((float) Integer.MAX_VALUE / sum / 2);

        ArrayList<Compiled.CompiledItem<T>> compI = new ArrayList<>(size());
        int end = 0;
        for (BaseItem<T> event : eventToBase.values()) {
            int width = (int) Math.floor(event.weight * factor);
            int start = end;
            end = start + width;
            Compiled.CompiledItem<T> item = new Compiled.CompiledItem<>(event.event, start, end);
            compI.add(item);
        }

        return new Compiled<>(compI);
    }


    /**
     * Sets the weight.
     *
     * @param event  the event whose weight to set
     * @param weight the weight. 1.0f means "normal".
     */
    public void setWeight(T event, float weight) {
        if (weight == 0.0f) {
            // Must be an error
            Log.e("Distribution", "setWeight() called with weight 0");
            return;
        }
        BaseItem<T> base = new BaseItem<>(event, weight * BASE_WEIGHT);
        eventToBase.put(event, base);
    }


    public void setWeight(Iterable<T> events, float weight) {
        for (T event : events) {
            setWeight(event, weight);
        }
    }


    /**
     * Multiplies a given weight with a factor.
     *
     * @param event the event whose weight to modify
     * @param multiplier the factor to multiply with
     */
    public void multWeight(T event, float multiplier) {
        BaseItem<T> base = eventToBase.get(event);
        if (base != null) {
            base.weight *= multiplier;
        }
    }


    public void add(T event) {
        setWeight(event, 1.0f);
    }


    public int size() {
        return eventToBase.size();
    }


    @Override
    public String toString() {
        return "Distribution{" + "items=" + eventToBase.values() + '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distribution<?> that = (Distribution<?>) o;
        return Objects.equals(eventToBase, that.eventToBase) && Objects.equals(chained, that.chained);
    }


    @Override
    public int hashCode() {
        return Objects.hash(eventToBase, chained);
    }
}
