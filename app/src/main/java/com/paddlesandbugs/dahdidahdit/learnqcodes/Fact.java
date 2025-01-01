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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.paddlesandbugs.dahdidahdit.MorseCode;

@Entity
@TypeConverters({Fact.CharListConverters.class})
public class Fact {

    private static final long MINIMAL_ACTIVE_TIMESTAMP = 1;

    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "code")
    public final MorseCode.CharacterList code;

    @ColumnInfo(name = "meaning")
    public final String meaning;

    @ColumnInfo(name = "easiness")
    public float easiness = LearnModel.INITIAL_EASINESS;

    @ColumnInfo(name = "intervalMs")
    public long intervalMs = 0;

    @ColumnInfo(name = "repNo")
    public int repNo = 0;

    @ColumnInfo(name = "nextShowDateMs")
    public long nextShowDateMs;


    public Fact(int id, MorseCode.CharacterList code, String meaning) {
        this.id = id;
        this.code = code;
        this.meaning = meaning;
    }


    public boolean isOnHand() {
        return nextShowDateMs != 0;
    }


    public void putOnHand() {
        nextShowDateMs = MINIMAL_ACTIVE_TIMESTAMP;
    }


    @NonNull
    @Override
    public String toString() {
        return "{Fact " + id + ": " + code + " nsd:" + nextShowDateMs + " rep:" + repNo + " int:" + intervalMs + " ea:" + easiness + "}";
    }


    public static class CharListConverters {
        @TypeConverter
        public static MorseCode.CharacterList characterListFromString(String value) {
            return value == null ? null : new MorseCode.MutableCharacterList(value);
        }


        @TypeConverter
        public static String characterListToString(MorseCode.CharacterList code) {
            return code == null ? null : code.asString();
        }
    }

}
