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

package com.paddlesandbugs.dahdidahdit.text;

import android.content.Context;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.settings.ReceivedFile;

/**
 * Generates text from the "loaded text" preference of the user.
 */
public class LoadedTextGenerator extends StaticTextGenerator {

    private LoadedTextGenerator(String text) {
        super(text);
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_mode_loaded;
    }


    public static LoadedTextGenerator create(Context context, String name) {
        final String text = new ReceivedFile(context, name).read();
        return new LoadedTextGenerator(text);
    }
}
