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

package com.paddlesandbugs.dahdidahdit.text;

import android.content.Context;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedParams;

/**
 * Generates text from the "custom text" preference of the user.
 */
public class CustomTextGenerator extends StaticTextGenerator {

    private CustomTextGenerator(String text) {
        super(text);
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_mode_frompreferences;
    }


    public static CustomTextGenerator create(Context context) {
        SelfdefinedParams p = new SelfdefinedParams(context);
        p.update(context);
        final String text = p.getText();
        return new CustomTextGenerator(text);
    }
}
