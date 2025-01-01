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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.DefaultLearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.params.ParameterFader;
import com.paddlesandbugs.dahdidahdit.params.ParameterMap;
import com.paddlesandbugs.dahdidahdit.text.CallsignGenerator;
import com.paddlesandbugs.dahdidahdit.text.CompoundTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.ListRandomWordTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.RandomWordTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

/**
 * Default {@link LearningStrategy}.
 */
public class CopyTrainerListLearningStrategy extends DefaultLearningStrategy implements LearningStrategy {


    public static final int WORD_LIST_RESOURCE = R.raw.wordlist;


    public CopyTrainerListLearningStrategy(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context));
    }


    public CopyTrainerListLearningStrategy(Context context, SharedPreferences prefs) {
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


    @Override
    protected TextGenerator createTextGenerator() {
        GeneralFadedParameters pf = getFadedParameters();
        int currentKoch = pf.get(Field.KOCH_LEVEL);
        int maxWordLength = pf.get(Field.WORD_LENGTH_MAX);

        final CopyTrainer copyTrainer = MainActivity.getCopyTrainer(getContext());
        MorseCode.CharacterList allowedChars = copyTrainer.getCharsFlat(currentKoch);
        Set<MorseCode.CharacterData> allowed = allowedChars.asSet();

        try {
            final MorseCode instance = MorseCode.getInstance();
            Stream<String> stream = Utils.toStream(getContext(), WORD_LIST_RESOURCE) //
                    .filter(s -> s.length() <= maxWordLength) //
                    .filter(s -> Arrays.stream(s.split("")).allMatch(c -> ("".equals(c)) || allowed.contains(instance.get(c))));

            ArrayList<TextGenerator> gens = new ArrayList<>();
            gens.add(new ListRandomWordTextGenerator(getContext(), MainActivity.stopwords, stream));

            try {
                gens.add(new CallsignGenerator(getContext(), MainActivity.stopwords, allowed));
            } catch (IllegalArgumentException e) {
                // no biggie, there just are no callsigns to build with the given letters
            }

            if (allowed.contains(MorseCode.getInstance().get("/"))) {
                gens.add(RandomWordTextGenerator.createSuffixGenerator());
            }

            return new CompoundTextGenerator(gens.get(0).getTextID(), gens.toArray(new TextGenerator[0]));

        } catch (IOException e) {
            return new StaticTextGenerator("error");
        }
    }


}
