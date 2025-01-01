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

package com.paddlesandbugs.dahdidahdit.params;

import android.content.Context;

/**
 * Settings that are to be adjusted automatically by the trainer.
 */
public interface FadedParameters {

    /**
     * Update this object from settings stored in the app.
     *
     * @param context the app context
     */
    void update(Context context);

    /**
     * Persist this object to the app's settings.
     *
     * @param context the app context
     */
    void persist(Context context);

    //int get(Field field);

    /**
     * @return this objects values as a map, suitable for fading
     */
    ParameterMap toMap();

    /**
     * Update this object from the given map
     *
     * @param map the parameter map to take values from
     */
    void fromMap(ParameterMap map);

}

