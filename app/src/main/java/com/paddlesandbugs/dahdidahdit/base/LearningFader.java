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

package com.paddlesandbugs.dahdidahdit.base;

import android.content.SharedPreferences;
import android.util.Log;

import com.paddlesandbugs.dahdidahdit.params.FadedParameters;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;

public class LearningFader {

    private static final String LOG_TAG = "LearningFader";

    private final SharedPreferences prefs;
    private final String key;


    public LearningFader(SharedPreferences prefs, String key) {
        this.prefs = prefs;
        this.key = key;
    }


    public void update(GeneralParameters p, ParameterFader.Config faderConfig) {
        ParameterFader fader = new ParameterFader();

        FadedParameters pc = p.current();
        FadedParameters pt = p.to();
        Log.i(LOG_TAG, "Using fader config " + faderConfig);
        ParameterFader.FadeSequence sequence = fader.fade(faderConfig, pc.toMap(), pt.toMap());
        persistFaderSequence(sequence);
    }


    private void persistFaderSequence(ParameterFader.FadeSequence sequence) {
        String faderStepsStr = sequence.asString();
        prefs.edit().putString(key, faderStepsStr).apply();
        Log.i(LOG_TAG, "fader steps written to " + key + ": " + faderStepsStr);
    }


    private ParameterFader.FadeSequence readFaderSequence() {
        String faderStepsStr = prefs.getString(key, "");
        Log.i(LOG_TAG, "fader steps read from " + key + ": " + faderStepsStr);
        ParameterFader.FadeSequence fs = ParameterFader.FadeSequence.fromString(faderStepsStr);
        return fs;
    }


    public ParameterFader.FadeStep nextFaderStep() {

        ParameterFader.FadeSequence fs = readFaderSequence();
        if (fs.isEmpty()) {
            return null;
        }

        ParameterFader.FadeStep step = fs.remove(0);

        persistFaderSequence(fs);

        Log.i(LOG_TAG, "returning step " + step);
        return step;
    }


    /**
     * Pushes a step to the {@link LearningFader}.
     * <p>
     * This is the inverse of {@link #nextFaderStep()}.
     *
     * @param step the step to push to the top of the stack
     *
     * @see #nextFaderStep()
     */
    public void push(ParameterFader.FadeStep step) {
        ParameterFader.FadeSequence fs = readFaderSequence();
        fs.add(0, step);
        persistFaderSequence(fs);
    }


    public void clear() {
        ParameterFader.FadeSequence sequence = new ParameterFader.FadeSequence();
        persistFaderSequence(sequence);
    }
}
