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

package com.paddlesandbugs.dahdidahdit.learnqcodes;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.IntroScreen;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyActivity;

public class LearnQCodesIntro extends IntroScreen {

    public static boolean callMe(Context context) {
        final int introVersion = 1;
        LearningValue v = new LearningValue(context, "learnqcodes_intro", 0);
        if (v.get() == introVersion) {
            return false;
        }

        v.set(introVersion);
        Intent intent = new Intent(context, LearnQCodesIntro.class);
        context.startActivity(intent);
        return true;
    }


    @Override
    protected void launchMain() {
        LearnQCodesActivity.callMe(this, false);
        finish();
    }


    @Override
    protected List<Supplier> getViews() {
        List<Supplier> screens = new ArrayList<>();

        screens.add(new Builder(this) //
                .headline(R.string.learnqcodes_intro_1_headline) //
                .text(R.string.learnqcodes_intro_1_text) //
                .supply());

        screens.add(new Builder(this) //
                .headline(R.string.learnqcodes_intro_2_headline) //
                .text(R.string.learnqcodes_intro_2_text) //
                .supply());

        screens.add(new Builder(this) //
                .headline(R.string.learnqcodes_intro_3_headline) //
                .text(R.string.learnqcodes_intro_3_text) //
                .supply());

        return screens;
    }


}
