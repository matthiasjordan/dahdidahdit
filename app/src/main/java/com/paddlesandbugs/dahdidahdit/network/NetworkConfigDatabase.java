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

package com.paddlesandbugs.dahdidahdit.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Keeps network config items.
 */
@Database(entities = {NetworkConfig.class}, version = 1, exportSchema = false)
public abstract class NetworkConfigDatabase extends RoomDatabase {
    public abstract NetworkConfigDao configDao();


    private static NetworkConfigDatabase INSTANCE;


    /**
     * @param context the context
     *
     * @return the instance of the database
     */
    public static NetworkConfigDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (NetworkConfigDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, NetworkConfigDatabase.class, "networkconfigs1") //
                            .fallbackToDestructiveMigration().addCallback(new Callback() {

                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db1) {
                                    new InsertConfigsTask().execute();
                                }
                            }).build();
                }
            }
        }

        return INSTANCE;
    }


    private static class InsertConfigsTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(final Void... params) {
            try {
                final NetworkConfig[] data = getData();
                final NetworkConfigDao dao = INSTANCE.configDao();
                dao.insertAll(data);
            } catch (Throwable e) {
                Log.e("QQQ", "foo", e);
            }
            return null;
        }


        private NetworkConfig[] getData() {
            List<NetworkConfig> configs = new ArrayList<>();

            long id = 1;
            configs.add(new NetworkConfig(id++, "QSOBot", "mopp", "qsobot.online", ""));
            configs.add(new NetworkConfig(id++, "M32 Chat Server", "mopp", "cq.morserino.info", ""));

            return configs.toArray(new NetworkConfig[0]);
        }
    }



    /**
     * Keeps facts in a database.
     */
    public static class DBNetworkConfigProvider {


        private final NetworkConfigDatabase db;


        private DBNetworkConfigProvider(Context context) {
            this.db = NetworkConfigDatabase.get(context);
        }


        public static DBNetworkConfigProvider create(Context context) {
            DBNetworkConfigProvider d = new DBNetworkConfigProvider(context);
            return d;
        }


        public NetworkConfig get(int id) {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<List<NetworkConfig>> f = ex.submit(new Callable<List<NetworkConfig>>() {

                @Override
                public List<NetworkConfig> call() throws Exception {
                    return db.configDao().loadAllByIds(id);
                }
            });

            try {
                final List<NetworkConfig> facts = f.get();
                if ((facts == null) || facts.isEmpty()) {
                    return null;
                }

                return facts.get(0);
            } catch (ExecutionException | InterruptedException e) {
                return null;
            }
        }


        public List<NetworkConfig> getConfigs() {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<List<NetworkConfig>> f = ex.submit(new Callable<List<NetworkConfig>>() {

                @Override
                public List<NetworkConfig> call() throws Exception {
                    return db.configDao().getAll();
                }
            });

            try {
                return f.get();
            } catch (ExecutionException | InterruptedException e) {
                return Collections.emptyList();
            }
        }


        public void store(NetworkConfig networkConfig) {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<Void> f = ex.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    if (networkConfig.id == null) {
                        db.configDao().insert(networkConfig);
                    } else {
                        db.configDao().update(networkConfig);
                    }
                    return null;
                }
            });

            try {
                f.get();
            } catch (ExecutionException | InterruptedException e) {
                return;
            }
        }


        public void delete(long id) {
            final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
            Future<Void> f = ex.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                        db.configDao().delete(new NetworkConfig(id, null, null, null, null));
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
