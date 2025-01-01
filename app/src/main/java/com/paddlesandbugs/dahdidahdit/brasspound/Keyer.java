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

package com.paddlesandbugs.dahdidahdit.brasspound;

/**
 * A CW keyer.
 * <p>
 *     The keyer has a number of (virtual) keys that can be pressed and released. The keys are identified by a number.
 */
public interface Keyer {


    /**
     * How the keyer keys another system.
     */
    interface KeyListener {

        /**
         * Called when the keyer starts keying (sending CW).
         */
        void keyDown();

        /**
         * Called when the keyer stops keying (sending CW).
         */
        void keyUp();
    }


    /**
     * Registers a {@link KeyListener}
     *
     * @param l the listener
     */
    void register(KeyListener l);

    /**
     * To be called when a key is pressed.
     *
     * @param keyCode the keyCode of the key
     */
    void keyDown(int keyCode);

    /**
     * To be called when a key is released.
     *
     * @param keyCode the keyCode of the key
     */
    void keyUp(int keyCode);
}
