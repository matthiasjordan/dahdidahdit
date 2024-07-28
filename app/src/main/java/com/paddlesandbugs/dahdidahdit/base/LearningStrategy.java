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

package com.paddlesandbugs.dahdidahdit.base;

import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;

/**
 * Defines how learning progress works.
 */
public interface LearningStrategy {

    class SessionConfig {

        /**
         * The config for the {@link MorsePlayer}. This is never null.
         */
        public MorsePlayer.Config morsePlayerConfig;

        /**
         * Miscellaneous stuff needed in a session. Might be null.
         */
        public Object misc;


        public SessionConfig(MorsePlayer.Config mpc) {
            if (mpc == null) {
                throw new NullPointerException("MorsePlayer.Config in SessionConfig null");
            }
            this.morsePlayerConfig = mpc;
        }
    }

    /**
     * Called when the settings are changed so the strategy can update its internal state.
     *
     * @param key the key of the settings item that was changed
     */
    void onSettingsChanged(String key);

    /**
     * @return a configuration for the {@link MorsePlayer} that takes learning progress into account. If null, no further playing is possible.
     */
    SessionConfig getSessionConfig();


}
