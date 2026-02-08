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

package com.paddlesandbugs.dahdidahdit.selfdefined;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.TextGeneratorFactory;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.text.CallsignGenerator;
import com.paddlesandbugs.dahdidahdit.text.CustomTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.ListRandomWordTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.LoadedTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.QCodeTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.QSOTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.RandomTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.RssTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.text.VvvKaArDecorator;

import java.util.Set;

public class NullLearningStrategy implements LearningStrategy {

    private final Context context;


    public NullLearningStrategy(Context context) {
        this.context = context;
    }


    @Override
    public void onSettingsChanged(String key) {
        // Nothing
    }


    @Override
    public final SessionConfig getSessionConfig() {
        Config gc = new Config();
        gc.update(context);

        SelfdefinedParams p = new SelfdefinedParams(context);
        p.update(context);

        MorsePlayer.Config config = createConfig(gc, p);
        config.chirp = p.isChirp();
        config.qlf = p.getQLF();
        return new SessionConfig(config);
    }


    private MorsePlayer.Config createConfig(Config gc, SelfdefinedParams p) {
        // Apply learning adjustments
        TextGenerator tg = getGenerator(context, p);

        // Assemble final temp config for playing
        final MorsePlayer.Config config = new MorsePlayer.Config().from(context, p).from(gc);

        config.textGenerator = tg;
        return config;
    }


    static TextGenerator getGenerator(Context context, SelfdefinedParams p) {
        TextGenerator tg;

        switch (p.getTextGenerator()) {
            case "callsigns": {
                tg = new CallsignGenerator(context, MainActivity.stopwords);
                break;
            }
            case "frompreferences": {
                tg = CustomTextGenerator.create(context);
                break;
            }
            case "loaded": {
                tg = LoadedTextGenerator.create(context, SelfdefinedActivity.RECEIVED_FILE_NAME);
                break;
            }
            case "qcodes": {
                tg = new QCodeTextGenerator(context);
                break;
            }
            case "2000words": {
                int count = p.getWordCount();
                tg = new ListRandomWordTextGenerator(context, MainActivity.stopwords, count);
                break;
            }
            case "qsos": {
                tg = new QSOTextGenerator(context);
                break;
            }
            case "rss": {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String provAuth = prefs.getString("selfdefined_rss_provider", "");
                String feedId = prefs.getString("selfdefined_rss_feed", "");
                tg = new RssTextGenerator(context, provAuth, feedId, MainActivity.stopwords);
                break;
            }
            case "randomfrompreferences": {
                final String text = p.getLetters();
                tg = RandomTextGenerator.createWeightedRandomTextGenerator(text);
                break;
            }
            default: {
                final MorseCode.CharacterList characters = MorseCode.getInstance().getCharacters();
                Set<MorseCode.CharacterData> set = MorseCode.asSet(characters);
                TextGeneratorFactory.DistributionFunction df = new TextGeneratorFactory.DistributionFunction() {
                    @Override
                    public Distribution<MorseCode.CharacterData> applyWeights(Distribution<MorseCode.CharacterData> dist) {
                        return dist;
                    }
                };
                tg = new TextGeneratorFactory(context, p.current(), df).create();
                int maxWordLength = p.current().get(Field.WORD_LENGTH_MAX);
                tg.setWordLengthMax(maxWordLength);
            }
        }

        Config gc = new Config();
        gc.update(context);
        if (gc.wrapWithVvvKaAr) {
            tg = new VvvKaArDecorator(tg);
        }
        return tg;
    }


    private static StaticTextGenerator createStaticTG(String text) {
        return new StaticTextGenerator(text);
    }


}
