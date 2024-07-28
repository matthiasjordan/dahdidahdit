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

package com.paddlesandbugs.dahdidahdit.network;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NetworkConfigDao {
    @Query("SELECT * FROM networkconfig")
    List<NetworkConfig> getAll();

    @Query("SELECT * FROM networkconfig WHERE id = :id")
    List<NetworkConfig> loadAllByIds(int id);


    @Insert
    void insertAll(NetworkConfig... configs);

    @Delete
    void delete(NetworkConfig config);

    @Update
    void update(NetworkConfig... configs);

    //@Query("INSERT INTO networkconfig (protocol, title, address, jsonProperties) VALUES (:config)")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NetworkConfig config);

}
