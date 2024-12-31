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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.text.AprilFoolsGenerator;
import com.paddlesandbugs.dahdidahdit.text.NaturalLanguageTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.RandomTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;
import com.paddlesandbugs.dahdidahdit.text.WeightedCompoundTextGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class TextGeneratorFactory {

    public interface DistributionFunction {

        Distribution<MorseCode.CharacterData> applyWeights(Distribution<MorseCode.CharacterData> dist);

    }

    private static final String LOG_TAG = "TextGenFac";
    public static final String APRILPLAYED = "aprilplayed";

    private final GeneralFadedParameters pf;
    private final Context context;

    private final DistributionFunction distributionFunction;


    public TextGeneratorFactory(Context context, GeneralFadedParameters pf, DistributionFunction df) {
        this.context = context;
        this.pf = pf;
        this.distributionFunction = df;
    }


    public TextGenerator create() {

        Date now = Calendar.getInstance().getTime();
        final String date = new SimpleDateFormat("ddMM").format(now);
        boolean aprilFoolsDay = (date.startsWith("0104"));
        if (aprilFoolsDay) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String playYear = prefs.getString(APRILPLAYED, "");
            String thisYear = new SimpleDateFormat("yyyy").format(now);
            if (!playYear.equals(thisYear)) {
                prefs.edit().putString(APRILPLAYED, thisYear).apply();
                Log.d(LOG_TAG, "Using April Fools Day generator");
                return new AprilFoolsGenerator(context);
            }
        }

        return getWeightedCompoundTextGenerator();
    }


    @NonNull
    private WeightedCompoundTextGenerator getWeightedCompoundTextGenerator() {
        final CopyTrainer trainer = MainActivity.getCopyTrainer(context);

        final int kochLevel = pf.get(Field.KOCH_LEVEL);
        final Set<MorseCode.CharacterData> kochSet = trainer.getCharsFlat(kochLevel).asSet();

        final Distribution<MorseCode.CharacterData> dist = getCharacterDistributionByKochLevel(kochSet);
        final TextGenerator randomTextGenerator = new RandomTextGenerator(dist.compile());
        final int textID = randomTextGenerator.getTextID();

        final TextGenerator naturalLanguageGenerator = new NaturalLanguageTextGenerator(context, 1, kochSet, 2);

        final WeightedCompoundTextGenerator weighted = new WeightedCompoundTextGenerator(textID, randomTextGenerator, naturalLanguageGenerator);
        final int distribution = pf.getDistribution(); // 0 .. 10
        weighted.setWeight(0, 10, distribution);
        Log.d(LOG_TAG, "Using weighted generator with weight " + distribution);

        return weighted;
    }


    private Distribution<MorseCode.CharacterData> getCharacterDistributionByKochLevel(Set<MorseCode.CharacterData> kochSet) {
        final Distribution<MorseCode.CharacterData> dist = new Distribution<>(kochSet);
        return distributionFunction.applyWeights(dist);
    }

}
