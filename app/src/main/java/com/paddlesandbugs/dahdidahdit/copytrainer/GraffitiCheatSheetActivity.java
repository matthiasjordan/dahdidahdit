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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.databinding.ActivityGraffitiCheatSheetBinding;

public class GraffitiCheatSheetActivity extends AppCompatActivity {

    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz0123456789-.,=?".toCharArray();


    public static void callMe(Context context) {
        Intent intent = new Intent(context, GraffitiCheatSheetActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.graffiti_cheatsheet_title);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = Math.round(displayMetrics.widthPixels / displayMetrics.density);

        final int imageWidth = 100;
        final int imagePadding = 10;
        final int betweenPadding = 10;
        final int imagesPerRow = (width - betweenPadding) / ((imagePadding * 2 + imageWidth) + betweenPadding);

        ActivityGraffitiCheatSheetBinding binding = ActivityGraffitiCheatSheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TableLayout table = findViewById(R.id.graffiti_scroll_view);

        TableRow tr = null;

        final MorseCode instance = MorseCode.getInstance();

        for (int i = 0; (i < LETTERS.length); i++) {
            char letter = LETTERS[i];
            MorseCode.CharacterData character = instance.get(Character.toString(letter));
            final int image = character.getImage();
            if (image == 0) {
                continue;
            }

            CardView cv = new CardView(this);
            cv.setCardElevation(10.0f);
            cv.setUseCompatPadding(true);

            String label = character.makeDisplayString();
            TextView tv = new TextView(this);
            tv.setText(label);
            tv.setPadding(0, 10, 0, 0);
            tv.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

            ImageView iv = new ImageView(this);
            int color = Utils.getThemeColor(this, R.attr.colorPrimaryVariant);
            int colorRGB = getResources().getColor(color, getTheme());
            Drawable steno = Utils.getDrawable(this, image, colorRGB);
            iv.setPadding(0, 0, 0, 10);
            iv.setImageDrawable(steno);
            iv.setMaxHeight(imageWidth);
            iv.setMaxWidth(imageWidth);

            cv.addView(iv);
            cv.addView(tv);

            if (i % imagesPerRow == 0) {
                tr = new TableRow(this);
                table.addView(tr);
            }

            tr.addView(cv);
        }

    }


}