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

package com.paddlesandbugs.dahdidahdit.learnqcodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.List;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.LearningProgress;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyParams;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;

public class QCodesLearningStrategy implements LearningStrategy {

    private static final int MAX_CARDS_PER_DAY = 5;
    private static final int ADDITIONAL_CARDS_COUNT = 3;

    private static final String PREF_PREFIX = "qcodecards.";
    private static final String CARDS_TODAY = PREF_PREFIX + "cardsToday";
    private static final String TIMESTAMP = PREF_PREFIX + "timestamp";
    private static final String NEW_CARDS_ALLOWED = PREF_PREFIX + "newCardsAllowed";
    public static final String LOGTAG = "QCLS";

    private final FactProvider factProvider;

    private final SharedPreferences prefs;


    public interface FactProvider {
        /**
         * @param id the ID of the fact to return
         *
         * @return the fact with the given ID
         */
        Fact get(int id);

        /**
         * @return all the facts in the whole provider
         */
        List<Fact> getFacts();

        /**
         * @return the next card on hand
         */
        Fact nextOnHand();

        /**
         * @return the timestamp of the card to show next
         */
        long getNextShowDate();

        /**
         * Stores the given fact
         *
         * @param fact the fact to store
         */
        void store(Fact fact);
    }

    private final Context context;


    public QCodesLearningStrategy(Context context, FactProvider fp, SharedPreferences prefs) {
        this.context = context;
        this.factProvider = fp;
        this.prefs = prefs;
    }


    public void onButtonPress(Fact fact, LearningProgress.Mistake level) {
        DataPoint.Score score = levelToScore(level);
        DataPoint d = new DataPoint(getNow(), score);
        LearnModel.adjust(fact, d);
        factProvider.store(fact);
    }


    private DataPoint.Score levelToScore(LearningProgress.Mistake level) {
        DataPoint.Score score;
        switch (level) {
            case LOW: {
                score = DataPoint.Score.EFFORTLESS;
                break;
            }
            case MEDIUM: {
                score = DataPoint.Score.WITH_EFFORT;
                break;
            }
            case HIGH:
            default: {
                score = DataPoint.Score.NOT_AT_ALL;
                break;
            }
        }
        return score;
    }


    @Override
    public void onSettingsChanged(String key) {

    }


    @Override
    public SessionConfig getSessionConfig() {

        updateNewCardsStats();

        Fact f = factProvider.nextOnHand();

        if (f == null) {
            return null;
        }

        final SessionConfig sessionConfig = getSessionConfig(f);
        sessionConfig.misc = f;

        return sessionConfig;
    }


    SessionConfig getSessionConfig(Fact f) {
        Config gc = new Config();
        gc.update(context);

        HeadcopyParams p = new HeadcopyParams(context);
        p.update(context);

        MorsePlayer.Config c = new MorsePlayer.Config().from(gc).from(context, p);
        c.textGenerator = new StaticTextGenerator(f.code, false);
        final SessionConfig sessionConfig = new SessionConfig(c);
        return sessionConfig;
    }


    public void addCards(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int newCardsAllowed = prefs.getInt(NEW_CARDS_ALLOWED, MAX_CARDS_PER_DAY);
        long now = System.currentTimeMillis();
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(NEW_CARDS_ALLOWED, newCardsAllowed + ADDITIONAL_CARDS_COUNT);
        ed.putLong(TIMESTAMP, now);
        ed.apply();

        addCards(ADDITIONAL_CARDS_COUNT);
    }


    private void addCards(int count) {
        List<Fact> facts = factProvider.getFacts();

        for (Fact fact : facts) {
            if (count <= 0) {
                break;
            }

            if (!fact.isOnHand()) {
                fact.putOnHand();
                factProvider.store(fact);
                count -= 1;
            }

        }
    }


    private void updateNewCardsStats() {
        long timestamp = getTimestamp();
        long now = getNow();

        if (Utils.isDifferentDay(now, timestamp)) {
            Log.i(LOGTAG, "A new day began.");
            handleNewDay(now);
        }
    }


    long getNow() {
        return System.currentTimeMillis();
    }


    long getTimestamp() {
        return prefs.getLong(TIMESTAMP, 0);
    }


    void handleNewDay(long now) {
        Log.i(LOGTAG, "New day: setting TIMESTAMP to now, adding cards to hand");
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(NEW_CARDS_ALLOWED, MAX_CARDS_PER_DAY);
        ed.putInt(CARDS_TODAY, 0);
        ed.putLong(TIMESTAMP, now);
        ed.apply();

        addCards(ADDITIONAL_CARDS_COUNT);
    }


}
