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

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;
import com.paddlesandbugs.dahdidahdit.params.ParameterMap;

public class LearningEase {

    /**
     * How many WPMs to reduce Farnsworth timing.
     */
    static final int FARNSWORTH_REDUCE_AMOUNT = 4;
    /**
     * The minimum Farnsworth WPM to leave after reduction.
     */
    static final int FARNSWORTH_MIN = 6;


    private final String key;

    private final SharedPreferences prefs;


    public LearningEase(SharedPreferences prefs, String key) {
        this.prefs = prefs;
        this.key = key;
    }


    public LearningEase(Context context, String key) {
        this(PreferenceManager.getDefaultSharedPreferences(context), key);
    }


    private void persistTempLearningEaseSequence(String seqStr) {
        prefs.edit().putString(key, seqStr).apply();
    }


    private String getTempLearningEaseStr() {
        return prefs.getString(key, "");
    }


    private ParameterFader.FadeSequence getTempLearningEaseSequence() {
        String seqStr = getTempLearningEaseStr();
        if (!seqStr.isEmpty()) {
            ParameterFader.FadeSequence seq = ParameterFader.FadeSequence.fromString(seqStr);
            return seq;
        }
        return null;
    }


    private String getFadeSequenceStr(GeneralFadedParameters pf, ParameterFader.Config config) {
        final int effWPM = pf.getWpm();

        ParameterMap from = new ParameterMap();
        from.put(Field.EFF_WPM, effWPM);

        ParameterMap to = new ParameterMap();
        to.put(Field.EFF_WPM, Math.max(FARNSWORTH_MIN, (effWPM - FARNSWORTH_REDUCE_AMOUNT)));

        ParameterFader.FadeSequence seq = new ParameterFader().fade(config, from, to);

        return seq.asString();
    }


    private ParameterFader.Config getTempLearningEaseFaderConfig() {
        ParameterFader.Stage farnsworthStage = ParameterFader.Stage.single(Field.EFF_WPM, 10);

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(farnsworthStage);

        return config;
    }


    /**
     * Creates a new learning ease queue.
     *
     * @param pf the parameters to ease
     */
    public void recreate(GeneralFadedParameters pf) {
        ParameterFader.Config config = getTempLearningEaseFaderConfig();
        final String seqStr = getFadeSequenceStr(pf, config);
        persistTempLearningEaseSequence(seqStr);
    }


    /**
     * Applies the queue to the given config.
     *
     * @param pf the config to fade
     */
    public void apply(GeneralFadedParameters pf) {
        ParameterFader.FadeSequence seq = getTempLearningEaseSequence();
        if (seq != null) {
            for (ParameterFader.FadeStep step : seq) {
                step.apply(pf);
            }
        }
    }


    /**
     * @return true, if no further steps are in the queue
     */
    public boolean isEmpty() {
        return getTempLearningEaseStr().isEmpty();
    }


    /**
     * Pops the first step off the queue.
     */
    public void reduce() {
        ParameterFader.FadeSequence seq = getTempLearningEaseSequence();
        if (seq != null) {
            seq.remove(0);
            persistTempLearningEaseSequence(seq.asString());
        }
    }


    /**
     * Purges all steps from the queue.
     */
    public void flush() {
        persistTempLearningEaseSequence("");
    }

}
