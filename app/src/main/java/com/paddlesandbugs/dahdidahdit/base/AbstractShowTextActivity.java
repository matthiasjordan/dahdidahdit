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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.R;

public abstract class AbstractShowTextActivity extends AbstractNavigationActivity {

    public static final String TRAINING_TEXT_KEY = "TEXT";


    public static void callMe(Context context, String morseText) {
        Intent intent = new Intent(context, AbstractShowTextActivity.class);
        intent.putExtra(TRAINING_TEXT_KEY, morseText);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Config config = new Config();
        config.update(this);

        CharSequence plainText = getIntent().getExtras().getCharSequence(TRAINING_TEXT_KEY);
        String morseText = plainText.toString();
        morseText = morseText.replaceAll("  +", " ");
        if (config.showUppercase) {
            morseText = morseText.toUpperCase();
        }

        Log.i("Grading", "Setting text " + morseText);
        TextView sent = findViewById(R.id.textSent);
        sent.setText(morseText);

        postCreate(plainText);
    }


    protected void postCreate(CharSequence morseText) {

    }


}