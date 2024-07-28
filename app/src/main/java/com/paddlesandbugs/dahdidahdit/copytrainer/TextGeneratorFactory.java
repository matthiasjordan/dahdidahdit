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

import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.params.FadedParameters;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.Parameters;
import com.paddlesandbugs.dahdidahdit.text.AprilFoolsGenerator;
import com.paddlesandbugs.dahdidahdit.text.RandomTextGenerator;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

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
                return new AprilFoolsGenerator(context);
            }
        }

        Distribution<MorseCode.CharacterData> dist = getCharacterDataDistribution();
        return new RandomTextGenerator(dist.compile());
    }


    Distribution<MorseCode.CharacterData> getCharacterDataDistribution() {
        int kochLevel = pf.get(Field.KOCH_LEVEL);
        Distribution<MorseCode.CharacterData> dist = RandomTextGenerator.createKochTextDistribution(MainActivity.getCopyTrainer(context), kochLevel);
        dist = distributionFunction.applyWeights(dist);
        Log.d(LOG_TAG, "getCharDataDist(" + kochLevel + ", ): " + dist);
        return dist;
    }

}
