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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class NetworkConfig {


    @PrimaryKey(autoGenerate = true)
    public final Long id;

    @ColumnInfo(name = "title")
    public final String title;

    @ColumnInfo(name = "protocol")
    public final String protocol;

    @ColumnInfo(name = "address")
    public final String address;

    @ColumnInfo(name = "jsonProperties")
    public final String jsonProperties;


    public NetworkConfig(Long id, String title, String protocol, String address, String jsonProperties) {
        this.id = id;
        this.title = title;
        this.protocol = protocol;
        this.address = address;
        this.jsonProperties = jsonProperties;
    }


    @Ignore
    public NetworkConfig(String title, String protocol, String address, String jsonProperties) {
        this(null, title, protocol, address, jsonProperties);
    }


    @NonNull
    @Override
    public String toString() {
        return "{NetConf " + id + ": " + title + " - address: " + address + " - props: " + jsonProperties + "}";
    }


}
