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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.DefaultLearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;
import com.paddlesandbugs.dahdidahdit.params.ParameterMap;

/**
 * Default {@link LearningStrategy}.
 */
public class CopyTrainerLearningStrategy extends DefaultLearningStrategy implements LearningStrategy {


    public CopyTrainerLearningStrategy(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context));
    }


    public CopyTrainerLearningStrategy(Context context, SharedPreferences prefs) {
        super(context, prefs);
    }



    @Override
    protected String getPrefsPrefix() {
        return "copytrainer_";
    }


    @Override
    protected GeneralParameters getParameters() {
        GeneralParameters p = new CopyTrainerParams(getContext());
        p.update(getContext());
        return p;
    }

    @NonNull
    @Override
    protected MorseCode.CharacterList getCharForLevel(int lvl) {
        return MainActivity.getCopyTrainer(getContext()).getSequence().getChar(lvl);
    }



    @Override
    protected void routeToStart() {
        CopyTrainerActivity.callMe(getContext());
    }


    @Override
    protected void routeToProgress(MorseCode.CharacterList kochChar) {
        LearnNewCharActivity.callMe(getContext(), kochChar);
    }


    @Override
    protected ParameterFader.Config getFaderConfig() {
        ParameterFader.Stage kochStage = ParameterFader.Stage.single(Field.KOCH_LEVEL, 10);
        ParameterFader.Stage farnsworthStage = ParameterFader.Stage.single(Field.EFF_WPM, 10);

        ParameterFader.Stage wpmStage = new ParameterFader.Stage();
        wpmStage.add(new ParameterFader.Prio(Field.WPM, 1));
        wpmStage.add(new ParameterFader.Prio(Field.WORD_LENGTH_MAX, 2));

        ParameterFader.Stage qrXstate = new ParameterFader.Stage();
        qrXstate.add(new ParameterFader.Prio(Field.QSB, 1));
        qrXstate.add(new ParameterFader.Prio(Field.QRM, 1));
        qrXstate.add(new ParameterFader.Prio(Field.QRN, 1));

        ParameterFader.Config config = new ParameterFader.Config();
        config.add(kochStage);
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


}
