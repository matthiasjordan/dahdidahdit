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
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;

/**
 * Keeps flash cards with facts (e.g. Q codes and prosigns).
 */
@Database(entities = {Fact.class}, version = 1, exportSchema = false)
public abstract class FactDatabase extends RoomDatabase {
    public abstract FactDao factDao();


    private static FactDatabase INSTANCE;


    /**
     * @param context the context
     *
     * @return the instance of the database
     */
    public static FactDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (FactDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, FactDatabase.class, "qcodes1") //
                            .fallbackToDestructiveMigration().addCallback(new RoomDatabase.Callback() {

                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db1) {
                                    new InsertCardsTask(context).execute();
                                }
                            }).build();
                }
            }
        }

        return INSTANCE;
    }


    private static class InsertCardsTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        private static final String SPLIT_REGEX = "\\|";

        private static final int MAX_FIELDS = 3;

        private InsertCardsTask(Context context) {
            this.context = context;
        }


        @Override
        protected Void doInBackground(final Void... params) {
            try {
                final Fact[] data = getData();
                final FactDao dao = INSTANCE.factDao();
                dao.insertAll(data);
            } catch (Throwable e) {
                Log.e("QQQ", "foo", e);
            }
            return null;
        }



        private Fact[] getData() {
            List<Fact> facts = new ArrayList<>();
            try (InputStream in_s = context.getResources().openRawResource(R.raw.qcodecards); //
                 Reader br = new InputStreamReader(in_s); //
                 BufferedReader brr = new BufferedReader(br);) {

                int id = 0;
                String line;
                while ((line = brr.readLine()) != null) {
                    String[] parts = line.split(SPLIT_REGEX);
                    if (parts.length > MAX_FIELDS) {
                        continue;
                    }

                    if (parts.length >= 2) {
                        final MorseCode.MutableCharacterList morse = new MorseCode.MutableCharacterList(parts[0]);
                        final Fact fact = new Fact(id++, morse, parts[1]);
                        facts.add(fact);
                    }

                    if (parts.length == MAX_FIELDS) {
                        final MorseCode.MutableCharacterList morse = new MorseCode.MutableCharacterList(parts[0] + "?");
                        final Fact fact = new Fact(id++, morse, parts[2]);
                        facts.add(fact);
                    }
                }
            } catch (IOException e) {

            }

            return facts.toArray(new Fact[0]);
        }
    }

}
