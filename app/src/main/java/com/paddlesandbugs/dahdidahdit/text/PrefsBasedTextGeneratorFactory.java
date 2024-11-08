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

import android.content.Context;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.copytrainer.TextGeneratorFactory;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;

import java.util.Set;

/**
 * Generates {@link TextGenerator} instances based on the given type (taken from preferences).
 */
public class PrefsBasedTextGeneratorFactory {

    private static final Set<String> KOCH_LEVEL_HONORING_GENERATORS = Set.of("random");

    private final String receivedFileName;

    private final int maxWordLength;

    private final int kochLevel;

    private int wordListCount = -1;


    public PrefsBasedTextGeneratorFactory(String receivedFileName, int maxWordLength, int kochLevel) {
        this.receivedFileName = receivedFileName;
        this.maxWordLength = maxWordLength;
        this.kochLevel = kochLevel;
    }

    public void setWordListCount(int wordListCount) {
        this.wordListCount = wordListCount;
    }

    public TextGenerator getGenerator(Context context, String generatorType) {
        final TextGenerator tg;

        switch (generatorType) {
            case "callsigns": {
                tg = new CallsignGenerator(context, MainActivity.stopwords);
                break;
            }
            case "frompreferences": {
                tg = CustomTextGenerator.create(context);
                break;
            }
            case "loaded": {
                tg = LoadedTextGenerator.create(context, receivedFileName);
                break;
            }
            case "qcodes": {
                tg = new QCodeTextGenerator(context);
                break;
            }
            case "2000words": {
                if (wordListCount != -1) {
                    tg = new ListRandomWordTextGenerator(context, MainActivity.stopwords, wordListCount);
                } else {
                    tg = new ListRandomWordTextGenerator(context, MainActivity.stopwords);
                }
                break;
            }
            default: {
                TextGeneratorFactory.DistributionFunction df = new TextGeneratorFactory.DistributionFunction() {
                    @Override
                    public Distribution<MorseCode.CharacterData> applyWeights(Distribution<MorseCode.CharacterData> dist) {
                        return dist;
                    }
                };
                GeneralFadedParameters pf = new CopyTrainerParamsFaded(context, "current");
                pf.setKochLevel(kochLevel);
                tg = new TextGeneratorFactory(context, pf, df).create();
                tg.setWordLengthMax(maxWordLength);
            }
        }
        return tg;
    }

    /**
     * Checks if the generator whose prefs name is given honors the setting that only characters up to the given Koch level are to be used.
     *
     * @param generatorName the prefs name of the generator
     * @return true, if the generator can restrict its output to the given Koch level. Else false.
     */
    public static boolean isHonoringKochLevel(String generatorName) {
        return KOCH_LEVEL_HONORING_GENERATORS.contains(generatorName);
    }


}
