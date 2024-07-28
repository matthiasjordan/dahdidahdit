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

package com.paddlesandbugs.dahdidahdit.learnqcodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;

public class LearnQCodesTrainer {

    private LearnQCodesTrainer() {
    }


    public static void init(Context context) {
        createFactProvider(context).get(1);
    }


    /**
     * Supplies the currently used {@link LearningStrategy}.
     *
     * @param context the context
     *
     * @return the {@link LearningStrategy}
     */
    public static QCodesLearningStrategy get(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return new QCodesLearningStrategy(context, createFactProvider(context), prefs);
    }


    public static DBFactProvider createFactProvider(Context context) {
        return DBFactProvider.create(context);
    }


    public static class StaticFactProvider implements QCodesLearningStrategy.FactProvider {

        private static final List<Fact> facts = createFacts();


        @Override
        public Fact get(int id) {
            return facts.get(0);
        }


        @Override
        public List<Fact> getFacts() {
            return facts;
        }


        private static List<Fact> createFacts() {
            List<Fact> facts = new ArrayList<>();
            facts.add(new Fact(1, new MorseCode.MutableCharacterList("qth"), "Ort"));
            return facts;
        }


        @Override
        public Fact nextOnHand() {
            return getFacts().get(0);
        }


        @Override
        public long getNextShowDate() {
            return nextOnHand().nextShowDateMs;
        }


        @Override
        public void store(Fact fact) {

        }


    }

    /**
     * Keeps facts in a database.
     */
    public static class DBFactProvider implements QCodesLearningStrategy.FactProvider {


        private final FactDatabase db;


        private DBFactProvider(Context context) {
            this.db = FactDatabase.get(context);
        }


        public static DBFactProvider create(Context context) {
            DBFactProvider d = new DBFactProvider(context);
            return d;
        }


        @Override
        public Fact get(int id) {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<List<Fact>> f = ex.submit(new Callable<List<Fact>>() {

                @Override
                public List<Fact> call() throws Exception {
                    return db.factDao().loadAllByIds(new int[]{id});
                }
            });

            try {
                final List<Fact> facts = f.get();
                if ((facts == null) || facts.isEmpty()) {
                    return null;
                }

                return facts.get(0);
            } catch (ExecutionException | InterruptedException e) {
                return null;
            }
        }


        @Override
        public List<Fact> getFacts() {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<List<Fact>> f = ex.submit(new Callable<List<Fact>>() {

                @Override
                public List<Fact> call() throws Exception {
                    return db.factDao().getAll();
                }
            });

            try {
                return f.get();
            } catch (ExecutionException | InterruptedException e) {
                return Collections.emptyList();
            }
        }


        @Override
        public Fact nextOnHand() {
            final List<Fact> facts = getFacts();

            if (facts.isEmpty()) {
                return null;
            }

            long now = System.currentTimeMillis();
            Iterator<Fact> it = facts.iterator();
            while (it.hasNext()) {
                Fact f = it.next();
                final boolean notYetDue = f.nextShowDateMs > now;
                final boolean notOnHand = !f.isOnHand();
                if (notYetDue || notOnHand) {
                    Log.d("QCodeTr", "removed " + f.id);
                    it.remove();
                }
            }

            if (facts.isEmpty()) {
                return null;
            }

            Collections.sort(facts, ((o1, o2) -> Long.compare(o1.nextShowDateMs, o2.nextShowDateMs)));
            return facts.get(0);
        }


        @Override
        public long getNextShowDate() {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<Long> f = ex.submit(new Callable<Long>() {

                @Override
                public Long call() throws Exception {
                    return db.factDao().getNextReviewTime();
                }
            });

            try {
                return f.get();
            } catch (ExecutionException | InterruptedException e) {
                return 0;
            }
        }


        @Override
        public void store(Fact fact) {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<Void> f = ex.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    db.factDao().update(fact);
                    return null;
                }
            });

            try {
                f.get();
            } catch (ExecutionException | InterruptedException e) {
                return;
            }
        }


    }

}
