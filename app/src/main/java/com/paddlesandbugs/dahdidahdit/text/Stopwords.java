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
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import com.paddlesandbugs.dahdidahdit.R;

public class Stopwords {

    private final Set<String> stopwords = new TreeSet<>();


    public void createList(Context context) {
        try (InputStream in_s = context.getResources().openRawResource(R.raw.stopwords); //
             InputStreamReader isr = new InputStreamReader(in_s); //
             BufferedReader br = new BufferedReader(isr);) {
            String line = null;
            while ((line = br.readLine()) != null) {
                final String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    stopwords.add(trimmed);
                }
            }
        } catch (IOException e) {
        }
        Log.i("Stopwords", "Initialized with " + stopwords.size() + " words");
    }


    public boolean contains(String word) {
        return stopwords.contains(word);
    }
}
