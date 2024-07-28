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

package com.paddlesandbugs.dahdidahdit.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.base.LearningEase;

public class ParameterFader {


    public static final int MAX_FADER_STEPS = 100;

    public static class FadeStep {
        public final Field field;
        public final int stepSize;


        public FadeStep(Field field, int stepSize) {
            if (field == null) {
                throw new NullPointerException();
            }
            this.field = field;
            this.stepSize = stepSize;
        }


        public void apply(FadedParameters p) {
            ParameterMap map = p.toMap();
            if (map.apply(this)) {
                p.fromMap(map);
            }
        }


        @Override
        public String toString() {
            return "FadeStep{" + field + ((stepSize > 0) ? "+" : "") + stepSize + '}';
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FadeStep fadeStep = (FadeStep) o;
            return field == fadeStep.field;
        }


        @Override
        public int hashCode() {
            return Objects.hash(field);
        }


        public FadeStep invert() {
            return new FadeStep(field, stepSize * (-1));
        }
    }

    public static class Prio {
        public final Field field;
        public final int weight;


        public Prio(Field field, int weight) {
            this.field = field;
            this.weight = weight;
        }


        @Override
        public String toString() {
            return "{" + field + " " + weight + '}';
        }
    }

    public static class Stage extends ArrayList<Prio> {
        public Stage() {
            super();
        }


        public Stage(Collection<Prio> items) {
            super(items);
        }


        public static Stage from(Prio... prios) {
            return new Stage(Arrays.asList(prios));
        }


        public static Stage single(Field f, int weight) {
            return from(new Prio(f, weight));
        }
    }


    public interface Invariant {
        boolean apply(ParameterMap map);
    }

    public static class Config implements Iterable<Stage> {
        private final List<Stage> stages = new ArrayList<>();
        private final List<Invariant> invariants = new ArrayList<>();


        public Config() {
        }


        @Override
        public Iterator<Stage> iterator() {
            return stages.iterator();
        }


        public void add(Stage stage) {
            stages.add(stage);
        }


        public Stage get(int n) {
            return stages.get(n);
        }


        public void add(Invariant invariant) {
            invariants.add(invariant);
        }


        public boolean invariantsHold(ParameterMap map) {
            for (Invariant invariant : invariants) {
                if (!invariant.apply(map)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class FadeSequence extends LinkedList<FadeStep> {

        public static final String STEP_SEP = ";";
        public static final String PART_SEP = " ";


        public FadeSequence() {
            super();
        }


        public FadeSequence(FadeStep... steps) {
            super(Arrays.asList(steps));
        }


        public String asString() {
            StringBuilder b = new StringBuilder();
            for (FadeStep step : this) {
                if (b.length() != 0) {
                    b.append(STEP_SEP);
                }
                b.append(step.field.seq()).append(PART_SEP).append(step.stepSize);
            }
            return b.toString();
        }


        public static FadeSequence fromString(String str) {
            FadeSequence res = new FadeSequence();
            String[] parts = str.split(Pattern.quote(STEP_SEP));

            if (parts.length == 0) {
                return res;
            }

            for (String part : parts) {
                String[] ppps = part.split(PART_SEP);
                if (ppps.length != 2) {
                    return new FadeSequence();
                }
                Field field = Field.fromSeq(ppps[0].charAt(0));
                int stepSize = Integer.parseInt(ppps[1]);
                FadeStep step = new FadeStep(field, stepSize);
                res.add(step);
            }
            return res;
        }
    }

    private final Random random = new Random();


    private List<Field> explode(Stage prios) {
        List<Field> exploded = new ArrayList<>();
        for (Prio prio : prios) {
            int weight = prio.weight;
            Field field = prio.field;
            fill(exploded, field, weight);
        }
        return exploded;
    }


    private void fill(List<Field> list, Field field, int weight) {
        for (int i = 0; (i < weight); i++) {
            list.add(field);
        }
    }


    private Field choose(List<Field> exploded) {
        int i = random.nextInt(exploded.size());
        return exploded.get(i);
    }


    public FadeSequence fade(Config config, ParameterMap curMap, ParameterMap tarMap) {

        int stepCount = 0;
        FadeSequence stepList = new FadeSequence();

        boolean emergency = false;

        List<Field> later = new ArrayList<>();

        for (int stageNo = 0; (stageNo < config.stages.size()); stageNo++) {
            Stage stage = config.get(stageNo);
            Distribution.Compiled<Field> fieldsToFade = getFieldDistribution(stage, Collections.emptyList());

            List<Field> stageFields = fieldsToFade.events();
            stageFields.addAll(later);

            ParameterMap stageDiff = tarMap.diff(curMap, stageFields);

            Distribution.RoundRobin<Field> rr = new Distribution.RoundRobin<>();
            rr.add(fieldsToFade);
            if (!later.isEmpty()) {
                rr.add(later.get(0));
            }

            emergency = false;
            while (!emergency && (stageDiff.size() != 0)) {

                if (++stepCount == MAX_FADER_STEPS) {
                    stageNo = config.stages.size();
                    break;
                }

                Field fieldToFade = rr.next();
                Integer fieldDiff = stageDiff.get(fieldToFade);

                if (fieldDiff != null) {
                    if (fieldDiff != 0) {
                        // Room for fading
                        final int stepSize = (fieldDiff > 0 ? 1 : -1);
                        final int newValue = fieldDiff - stepSize;
                        final FadeStep fadeStep = new FadeStep(fieldToFade, stepSize);

                        curMap.apply(fadeStep);
                        if (config.invariantsHold(curMap)) {
                            stepList.add(fadeStep);
                            stageDiff.put(fieldToFade, newValue);
                        } else {
                            curMap.revert(fadeStep);
                            if (stageFields.size() == 1) {
                                // We don't expect any other field to be selected, so try a different stage.
                                emergency = true;
                                later.add(stageFields.get(0));
                            }
                        }
                    } else {
                        stageDiff.remove(fieldToFade); // Faded to max
                    }
                }
            }
        }

        return stepList;
    }


    public Distribution.Compiled<Field> getFieldDistribution(Stage stage, List<Prio> postponed) {
        Distribution<Field> fieldDist = new Distribution<>();

        for (Prio fp : stage) {
            fieldDist.setWeight(fp.field, fp.weight);
        }

        for (Prio fp : postponed) {
            fieldDist.setWeight(fp.field, fp.weight);
        }

        return fieldDist.compile();
    }


    public Set<Field> getCommonFields(ParameterMap aMap, ParameterMap bMap) {
        Set<Field> common = new HashSet<>();
        common.addAll(aMap.keySet());
        common.retainAll(bMap.keySet());
        return common;
    }

}
