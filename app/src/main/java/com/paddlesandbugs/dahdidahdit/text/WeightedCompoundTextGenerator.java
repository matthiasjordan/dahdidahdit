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

package com.paddlesandbugs.dahdidahdit.text;

import java.util.Random;

/**
 * {@link TextGenerator} that generates words from a delegate {@link TextGenerator} chosen from two delegates by weighted randomness.
 */
public class WeightedCompoundTextGenerator implements TextGenerator {

    private static final int GENERATOR_COUNT = 2;

    private final int textId;

    private final TextGenerator[] generators = new TextGenerator[GENERATOR_COUNT];

    private final int[] hits = new int[GENERATOR_COUNT];

    private final Random random = new Random();

    private double weight = 0.5d;


    /**
     * Creates a new generator.
     *
     * @param textId     the ID of the text resource to use to describe the generator
     * @param generatorA the one delegate generator
     * @param generatorB the other delegate generator
     */
    public WeightedCompoundTextGenerator(int textId, TextGenerator generatorA, TextGenerator generatorB) {
        this.textId = textId;
        this.generators[0] = generatorA;
        this.generators[1] = generatorB;
    }


    /**
     * Sets the weight based on the given value as a point between the minimum and the maximum values given.
     *
     * @param min   the minimum possible value
     * @param max   the maximum possible value
     * @param value the actual value
     */
    public void setWeight(int min, int max, int value) {
        int v = Math.max(min, Math.min(value, max)); // Make sure value is in legal range
        int range = max - min;
        int rvalue = value - min;
        double weight = (double) rvalue / (double) range;
        setWeight(weight);
    }


    /**
     * Returns the weight.
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }


    /**
     * Sets the weight.
     * <p>
     * 0.0 chooses generator A all the time, 1.0 chooses generator B all the time, a value in the middle does middle-ground.
     * <p> The value will be coerced into the interval between 0.0 and 1.0.
     *
     * @param weight the weight
     */
    public void setWeight(double weight) {
        this.weight = Math.max(0.0d, Math.min(weight, 1.0d));
        System.out.println("Set weight to " + this.weight);
    }


    @Override
    public int getTextID() {
        return textId;
    }


    @Override
    public void close() {
        for (TextGenerator generator : generators) {
            generator.close();
        }
    }


    @Override
    public void setWordLengthMax(int maxWordLength) {
        for (TextGenerator generator : generators) {
            generator.setWordLengthMax(maxWordLength);
        }
    }


    private TextGenerator choose(int i) {
        hits[i] += 1;
        return generators[i];
    }


    private TextGenerator getGenerator() {
        if (weight <= 0.01d) {
            return choose(0);
        }
        if (weight >= 0.99d) {
            return choose(1);
        }

        double r = random.nextDouble();

        if (r >= weight) {
            return choose(0);
        } else {
            return choose(1);
        }
    }


    @Override
    public boolean hasNext() {
        return getGenerator().hasNext();
    }


    @Override
    public TextPart next() {
        return getGenerator().next();
    }


    /**
     * Returns the number of hits for each of the two {@link TextGenerator}s.
     *
     * @return the numbers of hits
     */
    public int[] getHits() {
        return hits;
    }

}
