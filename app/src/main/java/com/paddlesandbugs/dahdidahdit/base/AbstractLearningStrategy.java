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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

/**
 * Basic implementation of {@link LearningStrategy}.
 * <p>
 * Saves a history of session results and uses the {@link ParameterFader} to automatically increase learning difficulty when the user is "too good"
 * and to decrease the difficulty when the user is "too bad".
 * <p>
 * When that actually is that the user changes levels and what exactly happens and the kind of text generator used, is left to subclasses to define.
 */
public abstract class AbstractLearningStrategy implements LearningStrategy {

    public interface ParameterProvider {
        GeneralParameters get();
    }

    private static final String LOG_TAG = "ct.LearningStrategy";

    /**
     * Settings key that contains the fadersteps needed to progress to the desired target config.
     */
    private static final String FADERSTEPS_KEY = "fadersteps";

    /**
     * Settings key that contains the fadersteps performed in the past so they can be reverted.
     */
    private static final String FADERSTEPS_UNDO_KEY = "fadersteps_undo";

    /**
     * Settings key that contains the number of sessions that we will increase the recently learned character's probability.
     */
    private static final String SESSIONS_SINCE_KOCH_INCREASE_KEY = "sessions_since_increase_koch";

    /**
     * Settings key used to store the history of learning results (low, medium, high).
     */
    private static final String LEARNING_KEY = "learning_progress";

    /**
     * How many historic learning results to store.
     */
    protected static final int MAX_SAVED_STEPS = 10;


    /**
     * Reference to the context.
     */
    private final Context context;


    private final SharedPreferences prefs;

    private final LearningProgress learningProgress;


    /**
     * Creates a {@link LearningStrategy} object.
     *
     * @param context reference to the context
     * @param prefs   preferences
     */
    public AbstractLearningStrategy(Context context, SharedPreferences prefs) {
        this.context = context;
        this.prefs = prefs;
        this.learningProgress = new LearningProgress(prefs, pKey(LEARNING_KEY), MAX_SAVED_STEPS, MAX_SAVED_STEPS);
    }


    /**
     * @return the context
     */
    protected Context getContext() {
        return context;
    }


    /**
     * Creates a prefixed preferences key.
     *
     * @param rest what to append to the prefix
     *
     * @return the key
     *
     * @see #getPrefsPrefix()
     */
    protected String pKey(String rest) {
        return getPrefsPrefix() + rest;
    }


    protected SharedPreferences getPrefs() {
        return prefs;
    }


    /**
     * Updates learning plan as a response to changed settings.
     *
     * @param key settings key changed
     */
    @Override
    public final void onSettingsChanged(String key) {
        new LearningFader(prefs, pKey(FADERSTEPS_KEY)).update(getParameters(), getFaderConfig());
        new LearningFader(prefs, pKey(FADERSTEPS_UNDO_KEY)).clear();
        handleSettingsChanged();
    }


    /**
     * Applies next learning step.
     */
    private void nextStep() {
        ParameterFader.FadeStep step = new LearningFader(prefs, pKey(FADERSTEPS_KEY)).nextFaderStep();
        if (step != null) {
            Log.i(LOG_TAG, "Doing learning step " + step);
            new LearningFader(prefs, pKey(FADERSTEPS_UNDO_KEY)).push(step);
            progressLearning(step);
        } else {
            startNextSession();
        }
    }


    private void stepBack() {
        ParameterFader.FadeStep step = new LearningFader(prefs, pKey(FADERSTEPS_UNDO_KEY)).nextFaderStep();
        if (step != null) {
            Log.i(LOG_TAG, "Undoing learning step " + step);
            new LearningFader(prefs, pKey(FADERSTEPS_KEY)).push(step);
            regressLearning(step);
        } else {
            startNextSession();
        }
    }


    @Override
    public SessionConfig getSessionConfig() {
        Config gc = new Config();
        gc.update(context);

        GeneralParameters p = getParameters();

        SessionConfig sc = new SessionConfig(createConfig(gc, p));
        return sc;
    }


    private MorsePlayer.Config createConfig(Config gc, GeneralParameters p) {
        // Apply learning adjustments
        TextGenerator tg = createTextGenerator();

        GeneralFadedParameters pf = p.current();
        applyTempLearningEaseAdjustments(pf);

        // Assemble final temp config for playing
        final MorsePlayer.Config config = new MorsePlayer.Config().from(getContext(), p).from(gc);
        config.textGenerator = tg;
        return config;
    }


    private String getSessionsSinceKochIncreaseKey() {
        return pKey(SESSIONS_SINCE_KOCH_INCREASE_KEY);
    }


    /**
     * Increases the number of sessions spent on the current Koch level.
     */
    private void increaseKochSessionCounter() {
        int kochSessions = getKochSessions();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getSessionsSinceKochIncreaseKey(), kochSessions + 1).apply();
    }


    /**
     * @return the number of sessions spent on the current Koch level.
     */
    protected final int getKochSessions() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(getSessionsSinceKochIncreaseKey(), 0);
    }


    /**
     * Resets the numner of sessions spent on the current Koch level.
     */
    private void resetKochSessions() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getSessionsSinceKochIncreaseKey(), 0).apply();
    }


    /**
     * Called when the user advances to the next Koch level.
     *
     * @param pf the faded parameters
     */
    private void startNextKochLevel(GeneralFadedParameters pf) {
        resetKochSessions();

        final int lvl = pf.get(Field.KOCH_LEVEL);
        MorseCode.CharacterList kochChars = getCharForLevel(lvl);
        routeToProgress(kochChars);
        ((Activity) context).finish();
    }


    @NonNull
    protected abstract MorseCode.CharacterList getCharForLevel(int lvl);


    protected abstract void routeToProgress(MorseCode.CharacterList kochChars);


    /**
     * The user 1-upped some setting. This method takes care of the increase and the bookkeeping.
     *
     * @param step the step that the user takes to increase her learning
     */
    private void progressLearning(ParameterFader.FadeStep step) {
        GeneralFadedParameters pf = getFadedParameters();
        step.apply(pf);
        pf.persist(context);

        showLevelToast(R.string.toast_text_level_up, pf, step);

        handleLearningProgress(pf, step);

        switch (step.field) {
            case KOCH_LEVEL: {
                startNextKochLevel(pf);
                break;
            }
            default: {
                startNextSession();
            }
        }
    }


    /**
     * The user got demoted on some setting. This method takes care of the increase and the bookkeeping.
     *
     * @param step the step that the user has to revert to decrease her learning
     */
    private void regressLearning(ParameterFader.FadeStep step) {
        GeneralFadedParameters pf = getFadedParameters();
        step.invert().apply(pf);
        pf.persist(context);

        final int textRsrc = R.string.toast_text_level_down;

        showLevelToast(textRsrc, pf, step);

        startNextSession();
    }


    private void showLevelToast(int textRsrc, GeneralFadedParameters pf, ParameterFader.FadeStep step) {
        final String fieldStr = context.getString(step.field.getStringId());
        final int newValue = pf.get(step.field);
        final String toastText = context.getString(textRsrc, fieldStr, newValue);
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
    }


    /**
     * Merely starts the next session without changing anything about the settings.
     */
    private void startNextSession() {
        routeToStart();
        ((Activity) context).finish();
    }


    protected abstract void routeToStart();


    /**
     * Handles a relatively low mistakes count.
     * <p>
     * Updates the learning progress history, counts the mistakes and determines if the user is 1-upped.
     *
     * @param mistake the type of {@link LearningProgress.Mistake} made by the user
     */
    protected void handle(LearningProgress.Mistake mistake) {
        LearningProgress.MistakeMap m = learningProgress.update(mistake);

        Log.i(LOG_TAG, "Handling mistake " + mistake + ". Historic mistakes count: " + m);

        if (isNextLevelWarranted(m)) {
            learningProgress.resetLearningProgress();
            nextStep();
        } else if (isDemotionWarranted(m)) {
            learningProgress.resetLearningProgress();
            stepBack();
        } else {
            startNextSession();
        }

        increaseKochSessionCounter();
    }


    protected abstract GeneralParameters getParameters();


    /**
     * @return the current faded parameters of the user
     */
    protected final GeneralFadedParameters getFadedParameters() {
        return getParameters().current();
    }


    /**
     * @return the current faded parameters of the user
     */
    protected final GeneralFadedParameters getFadedParametersTo() {
        return getParameters().to();
    }


    protected abstract String getPrefsPrefix();


    /**
     * Called when the settings changed for bookkeeping.
     * <p>
     * The implementation in {@link AbstractLearningStrategy#handleSettingsChanged()} does nothing at all, so calling super method is not necessary.
     */
    protected void handleSettingsChanged() {
        // Usually does nothing
    }


    /**
     * @return the fader config for defining how progress is made
     */
    protected abstract ParameterFader.Config getFaderConfig();

    /**
     * Determines if the user advances to the next level.
     *
     * @param m the map containing the number of historic mistakes per {@link LearningProgress.Mistake} type
     *
     * @return true if the user gets 1-upped. Else false.
     */
    protected abstract boolean isNextLevelWarranted(LearningProgress.MistakeMap m);

    /**
     * Determines if the user is demoted to the previous level.
     *
     * @param m the map containing the number of historic mistakes per {@link LearningProgress.Mistake} type
     *
     * @return true if the user gets demoted. Else false.
     */
    protected abstract boolean isDemotionWarranted(LearningProgress.MistakeMap m);


    /**
     * Called when the user levels up.
     * <p>
     * The implementation in {@link AbstractLearningStrategy#handleLearningProgress(GeneralFadedParameters, ParameterFader.FadeStep)} does nothing at
     * all, so calling super method is not necessary.
     *
     * @param pf   the, already leveled, parameters
     * @param step the learning step just made
     */
    protected void handleLearningProgress(GeneralFadedParameters pf, ParameterFader.FadeStep step) {
        // Usually does nothing
    }


    /**
     * Applies some temporary adjustments to the faded parameters that are only valid for the upconing learning session.
     *
     * @param pf the faded parameters
     */
    protected abstract void applyTempLearningEaseAdjustments(GeneralFadedParameters pf);

    /**
     * @return a {@link TextGenerator} to used in the next session
     */
    protected abstract TextGenerator createTextGenerator();


}
