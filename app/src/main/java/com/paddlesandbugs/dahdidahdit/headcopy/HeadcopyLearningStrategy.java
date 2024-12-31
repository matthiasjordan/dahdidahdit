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

package com.paddlesandbugs.dahdidahdit.headcopy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.GradingStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningEase;
import com.paddlesandbugs.dahdidahdit.base.LearningFader;
import com.paddlesandbugs.dahdidahdit.base.LearningProgress;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;
import com.paddlesandbugs.dahdidahdit.params.ParameterMap;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayerI;
import com.paddlesandbugs.dahdidahdit.text.AbstractWordTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.CountedWordsTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.GarbageWordGenerator;
import com.paddlesandbugs.dahdidahdit.text.ListRandomWordTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.RandomSyllableGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

import java.util.List;
import java.util.Set;

/**
 * Idea:
 * <p>
 * Each step begins with lower Farnsworth speed. Progress when WPM == EffWPM and low error rate (e.g. < 10 in 100)
 * <ul>
 * <li>syllables length 2</li>
 * <li>syllables length 3</li>
 * <li>syllables length 4</li>
 * </ul>
 * <p>
 * Words are played with syllable breaks. Each step begins with long break, break length decreases to 0.
 * Progress when syllable break == 0 and low error rate.
 * <ul>
 * <li>words length 5</li>
 * <li>words length 6</li>
 * <li>words length 7</li>
 * </ul>
 * <p>
 * Word pairs played with long word breaks.
 * <ul>
 * <li>pairs of words of maximal 3 chars each</li>
 * <li>pairs of words of maximal 4 chars each</li>
 * <li>pairs of words of maximal 5 chars each</li>
 * </ul>
 * <p>
 * This stage model is equivalent to the Koch level in copytrainer. Settings fading works equivalently: first we fade the stage then we fade the rest.
 */
public class HeadcopyLearningStrategy implements LearningStrategy, GradingStrategy {


    /**
     * How many errors in the last MAX_SAVED_STEPS sessions before progress.
     */
    public static final int PROGRESS_MAX_ERRORS = 5;
    /**
     * How many sessions have to be done w/o ease before progress to next stage.
     */
    public static final int MIN_SESSIONS_WO_EASE_BEFORE_PROGRESS = 20;
    /**
     * How many errors in the last MAX_RECENT_STEPS sessions before ease is reduced.
     */
    public static final int EASE_MAX_ERRORS = 2;
    /**
     * Settings key used to store the current stage.
     */
    static final String STAGE_KEY = "headcopy_stage";
    /**
     * Log tag.
     */
    private static final String LOG_TAG = "HEAD_LEARN";
    /**
     * Settings key used to store the number of sessions spent on the current stage.
     */
    private static final String SESSION_KEY = "headcopy_session_count";
    /**
     * Settings key used to store the history of learning results (low, medium, high).
     */
    private static final String LEARNING_KEY = "headcopy_learning_progress";
    /**
     * Settings key used to store the fader steps for the last stage.
     */
    private static final String FADERSTEPS_KEY = "headcopy_fader_steady_stage";
    /**
     * How many session results to keep.
     */
    private static final int MAX_SAVED_STEPS = 50;
    /**
     * How many most recent sessions to examine when determining whether to decrease ease.
     */
    private static final int MAX_RECENT_STEPS = 10;
    /**
     * Initial inter-syllable break in milliseconds.
     */
    private static final int DEFAULT_SYLLABLE_BREAK_MS = 1000;
    /**
     * Reduce inter-syllable break by this amount in milliseconds.
     */
    private static final int SYLLABLE_BREAK_REDUCTION_MS = -300;


    private final Context context;
    private final SharedPreferences prefs;
    private final LearningProgress learningProgress;
    private final LearningEase learningEase;
    private final LearningValue sessionCount;
    private final LearningValue stageNo;


    public HeadcopyLearningStrategy(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.learningProgress = new LearningProgress(prefs, LEARNING_KEY, MAX_SAVED_STEPS, MAX_RECENT_STEPS);
        this.learningEase = new LearningEase(context, "headcopy_learning_ease");
        this.sessionCount = new LearningValue(context, SESSION_KEY, 0);
        this.stageNo = new LearningValue(context, STAGE_KEY, 0);
    }


    @Override
    public void onSettingsChanged(String key) {
        HeadcopyParams p = getHeadcopyParams();
        new LearningFader(prefs, FADERSTEPS_KEY).update(p, getFaderConfig());
        handleSettingsChanged(key);
    }


    private ParameterFader.Config getFaderConfig() {
        ParameterFader.Stage farnsworthStage = ParameterFader.Stage.single(Field.EFF_WPM, 10);
        ParameterFader.Stage wpmStage = ParameterFader.Stage.single(Field.WPM, 10);

        ParameterFader.Stage qrXstate = new ParameterFader.Stage();
        qrXstate.add(new ParameterFader.Prio(Field.QSB, 1));
        qrXstate.add(new ParameterFader.Prio(Field.QRM, 1));
        qrXstate.add(new ParameterFader.Prio(Field.QRN, 1));

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(farnsworthStage);
        config.add(wpmStage);
        config.add(qrXstate);

        config.add(new ParameterFader.Invariant() {
            @Override
            public boolean apply(ParameterMap map) {
                return map.get(Field.WPM) >= map.get(Field.EFF_WPM);
            }
        });

        return config;
    }


    private void handleSettingsChanged(String key) {
        learningEase.flush();
        learningProgress.resetLearningProgress();

        if (STAGE_KEY.equals(key)) {
            initializeStage();
        }
    }


    @Override
    public final SessionConfig getSessionConfig() {
        Config gc = getConfig();
        HeadcopyParams p = getHeadcopyParams();

        MorsePlayer.Config config = getStage().createConfig(gc, p);
        config.chirp = p.isChirp();

        return new SessionConfig(config);
    }

    @NonNull
    private HeadcopyParams getHeadcopyParams() {
        HeadcopyParams p = new HeadcopyParams(context);
        p.update(context);
        return p;
    }

    @NonNull
    private Config getConfig() {
        Config gc = new Config();
        gc.update(context);
        return gc;
    }


    private void initializeStage() {
        sessionCount.reset();
        learningProgress.resetLearningProgress();
        getStage().init();
        Log.i(LOG_TAG, "Initialized stage");
    }

    private boolean isRestrictedCharSet(HeadcopyParams p) {
        final CopyTrainer copyTrainer = MainActivity.getCopyTrainer(context);
        final int kochLevel = getKochLevel();
        final int maxLevel = copyTrainer.getSequence().getMax();
        final boolean restrictedCharSet = (kochLevel < maxLevel) && p.isOnlyCopyTrainerChars();
        return restrictedCharSet;
    }


    private Stage getStage() {
        final int stageNo = this.stageNo.get();
        return getStage(stageNo);
    }

    @NonNull
    private AbstractStage getStage(int stageNo) {
        final HeadcopyParams p = getHeadcopyParams();
        final int maximumSyllableStage = 2;
        final int effectiveStageNo = isRestrictedCharSet(p) ? Math.min(stageNo, maximumSyllableStage) : stageNo;
        Log.i(LOG_TAG, "We are in stage " + stageNo + " and effectively in stage " + effectiveStageNo);

        switch (effectiveStageNo) {
            case 0:
                return new SyllableStage(context, 2);
            case 1:
                return new SyllableStage(context, 3);
            case 2:
                return new SyllableStage(context, 4);
            case 3:
                return new WordStage(context, 5);
            case 4:
                return new WordStage(context, 6);
            case 5:
                return new WordStage(context, 7);
            case 6:
                return new WordpairStage(context, 4);
            case 7:
                return new WordpairStage(context, 5);
            case 8:
                return new WordpairStage(context, 6);
            case 9:
                return new WordpairStage(context, 7);
            case 10:
                return new WordpairStage(context, 8);
            case 11:
                return new WordpairStage(context, 9);
            default:
                return new WordpairStage(context, 10);
        }
    }

    public String getSummaryForStage(int stageNo) {
        final Stage stage = getStage(stageNo);
        final String summary = stage.getSummary();
        return summary;
    }

    private void handle(LearningProgress.Mistake mistake) {
        Stage stage = getStage();
        stage.handle(mistake);
    }


    @Override
    public void onButtonPress(LearningProgress.Mistake level) {
        handle(level);
    }


    @Override
    public ErrorBounds getBounds(int textLen) {
        return null;
    }

    private void handleCopyTrainerCharacterFilter(HeadcopyParams p, AbstractWordTextGenerator tg) {
        if (p.isOnlyCopyTrainerChars()) {
            int kochLevel = getKochLevel();
            List<MorseCode.CharacterList> kochChars = MainActivity.getCopyTrainer(context).getChars(kochLevel);
            final Set<MorseCode.CharacterData> kochCharsSet = MorseCode.asSet(kochChars);
            tg.setAllowed(kochCharsSet);
        }
    }

    private int getKochLevel() {
        CopyTrainerParamsFaded cpf = new CopyTrainerParamsFaded(context, "current");
        cpf.update(context);
        int kochLevel = cpf.getKochLevel();
        return kochLevel;
    }


    /**
     * A general stage.
     */
    private abstract class AbstractStage implements Stage {

        protected final int wordLength;
        private final Context context;


        AbstractStage(Context context, int wordLength) {
            this.context = context;
            this.wordLength = wordLength;
        }


        @Override
        public MorsePlayer.Config createConfig(Config gc, HeadcopyParams p) {
            // Apply learning adjustments
            GeneralFadedParameters pf = p.current();

            // Assemble final temp config for playing
            final MorsePlayerI.Config config = new MorsePlayer.Config().from(pf).from(gc);

            final boolean restrictedCharSet = isRestrictedCharSet(p);
            final TextGenerator textGenerator;
            if (restrictedCharSet) {
                AbstractWordTextGenerator tg = new GarbageWordGenerator(MainActivity.stopwords, false);
                tg.setWordLengthMax(wordLength);
                handleCopyTrainerCharacterFilter(p, tg);
                textGenerator = tg;
            } else {
                textGenerator = createTextGenerator(p);
            }

            config.textGenerator = textGenerator;
            return config;
        }


        @NonNull
        protected abstract TextGenerator createTextGenerator(HeadcopyParams p);


        @Override
        public void handle(LearningProgress.Mistake mistake) {
            learningProgress.updateLearningProgress(mistake);
            int sessions = sessionCount.update(1);

            LearningProgress.MistakeMap map = learningProgress.countMistakes();
            LearningProgress.MistakeMap recents = learningProgress.countRecentMistakes();

            handle(map, recents);
        }


        protected void handle(LearningProgress.MistakeMap map, LearningProgress.MistakeMap recents) {
            int allSessions = LearningProgress.countSessions(map);
            int allCount = countErrors(map);
            int recentSessions = LearningProgress.countSessions(recents);
            int recentCount = countErrors(recents);
            int sessionsSinceEaseReduction = sessionCount.get();
            Log.i(LOG_TAG, "Sessions since ease reduction: " + sessionsSinceEaseReduction);
            final boolean easeOff = isEaseOff();
            boolean enoughUneasedSessions = easeOff && (sessionsSinceEaseReduction >= MIN_SESSIONS_WO_EASE_BEFORE_PROGRESS);
            if (enoughUneasedSessions && (allSessions >= MAX_SAVED_STEPS) && (allCount <= PROGRESS_MAX_ERRORS)) {
                Log.i(LOG_TAG, "Nice all count - progress learning");
                progressToNextStage();
            } else if (!easeOff && (recentSessions >= MAX_RECENT_STEPS) && (recentCount <= EASE_MAX_ERRORS)) {
                Log.i(LOG_TAG, "Nice recent count - reduce learning ease");
                reduceLearningEase();
            }
        }


        protected abstract boolean isEaseOff();


        protected int countErrors(LearningProgress.MistakeMap map) {
            return map.get(LearningProgress.Mistake.HIGH);
        }


        protected void progressToNextStage() {
            int stage = stageNo.update(1);
            initializeStage();
        }


        protected void reduceLearningEase() {
            learningProgress.markRecents();
            sessionCount.reset();
        }


    }

    /**
     * The stage where the user learns to copy syllables.
     */
    private class SyllableStage extends AbstractStage {


        private SyllableStage(Context context, int wordLength) {
            super(context, wordLength);
            Log.i(LOG_TAG, "SyllableStage " + wordLength);
        }


        @Override
        public void init() {
            HeadcopyParams p = getHeadcopyParams();
            learningEase.recreate(p.current());
        }


        @Override
        public String getSummary() {
            return context.getResources().getString(R.string.headcopy_stage_summary_syllable, wordLength);
        }


        @Override
        protected AbstractWordTextGenerator createTextGenerator(HeadcopyParams p) {
            final RandomSyllableGenerator tg = new RandomSyllableGenerator(MainActivity.stopwords, false);
            tg.setWordLengthMax(wordLength);
            handleCopyTrainerCharacterFilter(p, tg);

            return tg;
        }


        @Override
        protected void progressToNextStage() {
            super.progressToNextStage();
        }


        @Override
        protected void reduceLearningEase() {
            super.reduceLearningEase();
            learningEase.reduce();
        }


        protected boolean isEaseOff() {
            return learningEase.isEmpty();
        }

    }

    /**
     * The stage where the user learns to copy whole words.
     */
    private class WordStage extends AbstractStage {


        private final LearningValue syllPause = new LearningValue(context, "headcopy_syllable_pause", DEFAULT_SYLLABLE_BREAK_MS);


        private WordStage(Context context, int wordLength) {
            super(context, wordLength);
            Log.i(LOG_TAG, "WordStage " + wordLength);
        }


        @Override
        public void init() {
            learningEase.flush();
        }

        @Override
        public String getSummary() {
            return context.getResources().getString(R.string.headcopy_stage_summary_word, wordLength);
        }


        @Override
        public MorsePlayer.Config createConfig(Config gc, HeadcopyParams p) {
            final MorsePlayer.Config config = super.createConfig(gc, p);

            config.syllablePauseMs = syllPause.get();
            return config;
        }

        @NonNull
        @Override
        protected TextGenerator createTextGenerator(HeadcopyParams p) {
            ListRandomWordTextGenerator tg = new ListRandomWordTextGenerator(context, MainActivity.stopwords);
            tg.setWordLengthMax(wordLength);
            handleCopyTrainerCharacterFilter(p, tg);
            return new CountedWordsTextGenerator(tg, 1);
        }


        @Override
        public void handle(LearningProgress.Mistake mistake) {
            super.handle(mistake);
        }


        @Override
        protected void progressToNextStage() {
            super.progressToNextStage();
            syllPause.set(DEFAULT_SYLLABLE_BREAK_MS);
        }


        @Override
        protected void reduceLearningEase() {
            super.reduceLearningEase();
            Log.i(LOG_TAG, "Nice recent count - take back ease");
            syllPause.update(-1 * Math.abs(SYLLABLE_BREAK_REDUCTION_MS));
        }


        protected boolean isEaseOff() {
            return syllPause.get() == 0;
        }

    }

    /**
     * The stage where the user learns to copy pairs of words and progresses her faded parameters.
     */
    private class WordpairStage extends AbstractStage {


        private WordpairStage(Context context, int wordLength) {
            super(context, wordLength);
            Log.i(LOG_TAG, "SteadyStage " + wordLength);
        }


        @Override
        public void init() {
            learningEase.flush();
        }


        @Override
        public String getSummary() {
            return context.getResources().getString(R.string.headcopy_stage_summary_wordpair, wordLength);
        }


        @NonNull
        @Override
        protected TextGenerator createTextGenerator(HeadcopyParams p) {
            ListRandomWordTextGenerator tg = new ListRandomWordTextGenerator(context, MainActivity.stopwords);
            tg.setWordLengthMax(wordLength);
            return new CountedWordsTextGenerator(tg, 2);
        }


        @Override
        protected void progressToNextStage() {
            super.progressToNextStage();
            HeadcopyParams p = getHeadcopyParams();

            final LearningFader learningFader = new LearningFader(prefs, FADERSTEPS_KEY);
            ParameterFader.FadeStep step = learningFader.nextFaderStep();
            if (step != null) {
                step.apply(p.current());
                p.current().persist(context);
                Log.i(LOG_TAG, "Learning progressed " + step);
            }
        }


        protected boolean isEaseOff() {
            return true;
        }

    }

    /**
     * How stages work.
     */
    private interface Stage {
        /**
         * Called when stage is first entered.
         */
        void init();

        MorsePlayer.Config createConfig(Config gc, HeadcopyParams p);

        void handle(LearningProgress.Mistake mistake);

        String getSummary();
    }

}
