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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.TextAppearanceSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.paddlesandbugs.dahdidahdit.R;

public class ReleaseNotes {

    private static boolean isReleaseNotesShown = false;


    /**
     * Shows release notes if they haven't been shown already.
     *
     * @param context
     */
    private static void show(Context context) {

        if (isReleaseNotesShown) {
            return;
        }

        isReleaseNotesShown = true;

        forceShow(context);
    }


    /**
     * Shows release notes when this is the first start after an upgrade and they haven't been shown, yet.
     *
     * @param context
     */
    public static void showIf(Context context) {
        if (VersionTracking.isFirstStartAfterUpgrade()) {
            show(context);
        }
    }


    /**
     * Shows release notes regardless.
     *
     * @param context
     */
    public static void forceShow(Context context) {
        final int rsrcId = R.raw.release_notes;
        SpannableStringBuilder ssb = rsrcToSpannable(context, rsrcId);

        final String title = context.getString(R.string.release_notes_title);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder//
                .setTitle(title) //
                .setMessage(ssb) //
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private static SpannableStringBuilder rsrcToSpannable(Context context, int rsrcId) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        try (InputStream in_s = context.getResources().openRawResource(rsrcId); //
             BufferedReader sr = new BufferedReader(new InputStreamReader(in_s));) {

            String line;
            while ((line = sr.readLine()) != null) {

                if (ssb.length() != 0) {
                    ssb.append("\n");
                    CharacterStyle sss = new TextAppearanceSpan(context, android.R.style.TextAppearance_Small);
                    ssb.append("\n", sss, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (line.startsWith("=")) {
                    line = line.substring(1).trim();
                    CharacterStyle sss = new TextAppearanceSpan(context, android.R.style.TextAppearance_Large);
                    ssb.append(line, sss, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (line.startsWith("*")) {
                    line = line.substring(1).trim();
                    ssb.append(line, new BulletSpan(10), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    ssb.append(line);
                }

            }
        } catch (IOException e) {
            // Won't happen
        }
        return ssb;
    }

}
