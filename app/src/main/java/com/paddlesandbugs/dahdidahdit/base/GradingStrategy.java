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

public interface GradingStrategy {

    class ErrorBounds {
        /**
         * The count up to which the mistakes are considered a low number.
         */
        public int lowBelow;

        /**
         * The count up to which the mistakes are considered a medium number.
         */
        public int mediumBelow;
    }

    /**
     * Get button count bounds.
     *
     * @param textLen the number of characters in the text.
     *
     * @return the number of errors that separates the buttons
     */
    ErrorBounds getBounds(int textLen);

    void onButtonPress(LearningProgress.Mistake level);
}
