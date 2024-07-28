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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.copytrainer.LearningSequence;
import com.paddlesandbugs.dahdidahdit.copytrainer.TextGeneratorFactory;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.text.VvvKaArDecorator;

/**
 * Default {@link LearningStrategy}.
 * <p>
 * Additionally to the mechanisms in {@link AbstractLearningStrategy}, this class adds temporary reduction of difficulty after increasing a level. It
 * also creates a text generator whose character distribution has an increased weight on the most recently learned character and less so, those
 * before.
 */
public abstract class DefaultLearningStrategy extends AbstractLearningStrategy implements LearningStrategy, GradingStrategy {

    private static final String LOG_TAG = "ct.DefaultLStrat";


    /**
     * The key of the preferences item that keeps the currently active temporary learning ease sequence.
     */
    static final String TEMP_LEARNING_EASE_FADER_KEY = "temp_learning_ease";

    private boolean hasJustLeveledUp = false;

    private final LearningEase learningEase;


    public DefaultLearningStrategy(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context));
    }


    public DefaultLearningStrategy(Context context, SharedPreferences prefs) {
        super(context, prefs);
        learningEase = new LearningEase(prefs, pKey(TEMP_LEARNING_EASE_FADER_KEY));
    }


    @Override
    protected void applyTempLearningEaseAdjustments(GeneralFadedParameters pf) {
        learningEase.apply(pf);
    }


    @Override
    protected void handleLearningProgress(GeneralFadedParameters pf, ParameterFader.FadeStep step) {
        if (step.field.equals(Field.KOCH_LEVEL)) {
            learningEase.recreate(pf);
        }
        hasJustLeveledUp = true;
    }


    @Override
    protected boolean isNextLevelWarranted(LearningProgress.MistakeMap m) {
        final boolean manyLowMistakeSessions = m.get(LearningProgress.Mistake.LOW) >= 3;
        final boolean noHighMistakesSessions = m.get(LearningProgress.Mistake.HIGH) == 0;
        final boolean noTempEase = learningEase.isEmpty();
        return noTempEase && manyLowMistakeSessions && noHighMistakesSessions;
    }


    @Override
    protected boolean isDemotionWarranted(LearningProgress.MistakeMap m) {
        final boolean manyHighMistakesSessions = m.get(LearningProgress.Mistake.HIGH) >= MAX_SAVED_STEPS / 2;
        final boolean noLowMistakeSessions = m.get(LearningProgress.Mistake.LOW) == 0;
        return manyHighMistakesSessions && noLowMistakeSessions;
    }


    @Override
    protected void handleSettingsChanged() {
        learningEase.flush();
    }


    @Override
    protected TextGenerator createTextGenerator() {
        GeneralFadedParameters pf = getFadedParameters();
        int currentKoch = pf.get(Field.KOCH_LEVEL);
        int kochSessions = getKochSessions();
        Log.d(LOG_TAG, "Sessions since Koch 1up: " + kochSessions);

        GeneralFadedParameters pt = getFadedParametersTo();
        int targetKoch = pt.get(Field.KOCH_LEVEL);

        final CopyTrainer copyTrainer = MainActivity.getCopyTrainer(getContext());
        final FrequencyDistributionFunction df = new FrequencyDistributionFunction(copyTrainer, currentKoch, targetKoch, kochSessions);
        TextGenerator tg = new TextGeneratorFactory(getContext(), pf, df).create();
        int maxWordLength = pf.get(Field.WORD_LENGTH_MAX);
        tg.setWordLengthMax(maxWordLength);

        Config gc = new Config();
        gc.update(getContext());
        if (gc.wrapWithVvvKaAr) {
            tg = new VvvKaArDecorator(tg);
        }

        return tg;
    }


    @Override
    public void onButtonPress(LearningProgress.Mistake level) {
        handle(level);
        if (!hasJustLeveledUp) {
            reduceTempLearningEaseAdjustments();
        }
    }


    @Override
    public ErrorBounds getBounds(int textLen) {
        ErrorBounds b = new ErrorBounds();
        b.lowBelow = textLen * 10 / 100;
        b.mediumBelow = textLen * 20 / 100;
        return b;
    }


    void reduceTempLearningEaseAdjustments() {
        learningEase.reduce();
    }


    /**
     * The probability of the last few characters of the Koch progressions is increased to result in a specific frequency of {@link
     * FrequencyDistributionFunction#CHAR_DIST_INITIAL_FREQUENCY}.
     */
    static class FrequencyDistributionFunction implements TextGeneratorFactory.DistributionFunction {

        /**
         * This is the frequency of the most frequent character. A value n means that the most frequent character is supposed to show up every n
         * characters. Make sure CHAR_DIST_INITIAL_FREQUENCY >> 1.
         */
        public static final int CHAR_DIST_INITIAL_FREQUENCY = 5;
        public static final int CHAR_DIST_FREQUENCY_MIN = 10;
        public static final int SESSIONS_STAY_ON_MIN = 5;

        private final int sessionsSinceKoch1Up;
        private final int kochLevel;
        private final int targetKochLevel;
        private final CopyTrainer trainer;


        public FrequencyDistributionFunction(CopyTrainer trainer, int kochLevel, int targetKochLevel, int sessionsSinceKoch1Up) {
            this.kochLevel = kochLevel;
            this.targetKochLevel = targetKochLevel;
            this.sessionsSinceKoch1Up = sessionsSinceKoch1Up;
            this.trainer = trainer;
        }


        /**
         * @return n for every n-th character on average.
         */
        int longTermMaxFactor(int sessionsSinceKoch1Up) {
            //                                    ____------------
            //            ____--------------------
            //    ____----
            //----
            // afterkoch      min factor          koch max

            int longTermMaxFactor = Math.min(sessionsSinceKoch1Up, CHAR_DIST_FREQUENCY_MIN);

            if (kochLevel == targetKochLevel) {
                int sessionsSinceMin = sessionsSinceKoch1Up - CHAR_DIST_FREQUENCY_MIN; // because 1:1
                if (sessionsSinceMin > SESSIONS_STAY_ON_MIN) {
                    longTermMaxFactor += (sessionsSinceMin - SESSIONS_STAY_ON_MIN);
                }
            }

            return CHAR_DIST_INITIAL_FREQUENCY + longTermMaxFactor;
        }


        /**
         * @return the relative weight of the last character
         */
        float startFactor(int otherChars, int sessionsSinceKoch1Up) {
            //----____
            //        ----____
            //                --------------------____
            //                                        ----------
            // afterkoch      min factor          koch max

            final int i = longTermMaxFactor(sessionsSinceKoch1Up);
            return (float) otherChars / (float) (i - 1);
        }


        @Override
        public Distribution<MorseCode.CharacterData> applyWeights(Distribution<MorseCode.CharacterData> dist) {
            int otherChars = dist.size() - 1;
            float factor = startFactor(otherChars, sessionsSinceKoch1Up);
            final List<MorseCode.CharacterList> chars1 = trainer.getChars(kochLevel);
            ArrayList<MorseCode.CharacterList> copy = new ArrayList<>(chars1);
            Collections.reverse(copy);
            for (MorseCode.CharacterList c : copy) {
                if (factor <= 1.0f) {
                    break;
                }

                dist.setWeight(c, factor);
                factor /= 2.0f;
            }

            return dist;
        }
    }


    /**
     * The probability of the last few characters of the Koch progressions is increased by the constant factor {@link
     * ConstantDistributionFunction#CHAR_DIST_INITIAL_WEIGHT_INCREASE}.
     */
    static class ConstantDistributionFunction implements TextGeneratorFactory.DistributionFunction {

        /**
         * This is the initial weight of the most recently learned character.
         */
        private static final int CHAR_DIST_INITIAL_WEIGHT_INCREASE = 10;

        private final CopyTrainerParamsFaded pf;
        private final int sessionsSinceKoch1Up;
        private final CopyTrainer trainer;


        public ConstantDistributionFunction(CopyTrainerParamsFaded pf, CopyTrainer trainer, int sessionsSinceKoch1Up) {
            this.pf = pf;
            this.sessionsSinceKoch1Up = sessionsSinceKoch1Up;
            this.trainer = trainer;
        }


        @Override
        public Distribution<MorseCode.CharacterData> applyWeights(Distribution<MorseCode.CharacterData> dist) {
            float factor = CHAR_DIST_INITIAL_WEIGHT_INCREASE - sessionsSinceKoch1Up;
            final MorseCode.CharacterList chars1 = trainer.getCharsFlat(pf.getKochLevel());
            final MorseCode.CharacterList reverse = chars1.reverse();
            for (MorseCode.CharacterData c : reverse) {
                if (factor <= 1.0f) {
                    break;
                }
                dist.setWeight(c, factor);
                factor /= 2.0f;
            }

            return dist;
        }
    }

}
