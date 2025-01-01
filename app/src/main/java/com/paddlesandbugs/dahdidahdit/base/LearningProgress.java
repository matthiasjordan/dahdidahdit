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

package com.paddlesandbugs.dahdidahdit.base;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.EnumMap;
import java.util.Objects;

public class LearningProgress {


    /**
     * Describes mistakes
     */
    public enum Mistake {
        /**
         * Low number of mistakes.
         */
        LOW('L'), //
        /**
         * Medium number of mistakes.
         */
        MEDIUM('M'), //
        /**
         * High number of mistakes.
         */
        HIGH('H'); //

        /**
         * The character used to encode this {@link Mistake} value in a learning settings string.
         */
        private final char c;


        Mistake(char c) {
            this.c = c;
        }


        public char toChar() {
            return c;
        }


        public static Mistake from(char c) {
            for (Mistake m : values()) {
                if (m.c == c) {
                    return m;
                }
            }
            return null;
        }
    }


    public static class MistakeMap {

        private final EnumMap<Mistake, Integer> map = new EnumMap<>(Mistake.class);


        public MistakeMap() {
            for (Mistake m : Mistake.values()) {
                map.put(m, 0);
            }
        }


        public void put(Mistake m, int i) {
            map.put(m, i);
        }


        public int get(Mistake m) {
            Integer v = map.get(m);
            if (v == null) {
                return 0;
            }
            return v;
        }


        @Override
        public String toString() {
            return map.toString();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MistakeMap that = (MistakeMap) o;
            return Objects.equals(map, that.map);
        }


        @Override
        public int hashCode() {
            return Objects.hash(map);
        }
    }


    private final SharedPreferences prefs;
    private final String key;
    private final int savedSteps;
    private final int recentSteps;


    public LearningProgress(SharedPreferences prefs, String key, int maxSavedSteps, int recentSteps) {
        this.prefs = prefs;
        this.key = key;
        this.savedSteps = maxSavedSteps;
        this.recentSteps = recentSteps;
    }


    protected String readHistory() {
        final String history = prefs.getString(key, "");
        Log.i("Progress", history);
        return history;
    }


    protected void writeHistory(String history) {
        prefs.edit().putString(key, history).apply();
    }


    /**
     * Updates the preferences setting that stores the history mistakes.
     *
     * @param mistake the most recent {@link Mistake} type
     *
     * @return the updated mistakes string
     */
    public MistakeMap update(Mistake mistake) {
        updateLearningProgress(mistake);
        return countMistakes();
    }


    /**
     * Updates the learning progress history and returns the full history, beginning with the oldest item.
     *
     * @param mistake the most recent kind of mistakes
     *
     * @return the full history
     */
    public String updateLearningProgress(Mistake mistake) {
        final char c = mistake.toChar();
        return updateLearningProgress(c);
    }


    private String updateLearningProgress(char c) {
        String current = readHistory();
        current = current + c;
        MistakeMap map = countMistakes(current, false);
        if (countSessions(map) > savedSteps) {
            current = current.substring(1);
        }

        writeHistory(current);

        return current;
    }


    public static int countSessions(MistakeMap map) {
        return map.get(LearningProgress.Mistake.LOW) + map.get(LearningProgress.Mistake.MEDIUM) + map.get(LearningProgress.Mistake.HIGH);
    }


    /**
     * Clears the learning progress.
     */
    public void resetLearningProgress() {
        writeHistory("");
    }


    /**
     * Assembles a statistic about the historic mistakes.
     *
     * @return the map with the statistics
     */
    public MistakeMap countMistakes() {
        return countMistakes(false);
    }


    /**
     * Assembles a statistic about the most recent historic mistakes.
     *
     * @return the map with the statistics
     */
    public MistakeMap countRecentMistakes() {
        return countMistakes(true);
    }


    /**
     * Assembles a statistic about the history mistakes.
     *
     * @param recent process only the last characters up to a marker
     *
     * @return the map with the statistics
     */
    public MistakeMap countMistakes(boolean recent) {
        String str = readHistory();
        return countMistakes(str, recent);
    }


    private MistakeMap countMistakes(String str, boolean recent) {
        MistakeMap map = new MistakeMap();

        if (str == null) {
            return map;
        }

        int mistakes = 0;
        char[] strc = str.toCharArray();
        for (int i = strc.length - 1; (i >= 0); i--) {
            if (recent && (mistakes >= recentSteps)) {
                // only recents and we found the marker character
                break;
            }

            char c = strc[i];
            Mistake m = Mistake.from(c);
            if (m != null) {
                mistakes += 1;
                int v = map.get(m);
                v += 1;
                map.put(m, v);
            } else if (recent) {
                // only recents and we found the marker character
                break;
            }
        }

        return map;
    }


    public void markRecents() {
        String current = readHistory();
        if (current.length() != 0) {
            current += " ";
            writeHistory(current);
        }
    }


}
