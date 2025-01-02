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

import java.util.HashMap;
import java.util.List;

public class ParameterMap extends HashMap<Field, Integer> {

    public ParameterMap diff(ParameterMap other, List<Field> fields) {
        ParameterMap res = new ParameterMap();
        for (Field field : fields) {
            Integer tv = get(field);
            Integer ov = other.get(field);
            res.put(field, (tv - ov));
        }
        return res;
    }


    public Integer get(Field key) {
        final Integer val = super.get(key);
        if (val == null) {
            return 0;
        }
        return val;
    }


    /**
     * Applies the fader step.
     *
     * @param step the step to apply
     *
     * @return true if applied. Else false.
     */
    public boolean apply(ParameterFader.FadeStep step) {
        if (step == null) {
            return false;
        }

        return doApply(step, 1);
    }


    private boolean doApply(ParameterFader.FadeStep step, int factor) {
        boolean applied = false;
        Integer value = get(step.field);
        if (value != null) {
            value += factor * step.stepSize;
            put(step.field, value);
            applied = true;
        }
        return applied;
    }


    /**
     * Reverts the given step.
     *
     * @param step the step to revert
     */
    public void revert(ParameterFader.FadeStep step) {
        doApply(step, -1);
    }
}
