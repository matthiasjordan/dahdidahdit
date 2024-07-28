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

package com.paddlesandbugs.dahdidahdit.copytrainer;

import android.content.Context;
import android.content.Intent;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

public class FindTheKMActivity extends FindTheCharActivity {

    private  MorseCode.CharacterList level0Chars = null;


    private MorseCode.CharacterList getLevel0Chars() {
        if (level0Chars == null) {
            level0Chars = MainActivity.getCopyTrainer(this).getSequence().getChar(0);
        }
        return level0Chars;
    }


    public static void callMe(Context context) {
        Intent i = new Intent(context, FindTheKMActivity.class);
        context.startActivity(i);
    }


    @Override
    protected void setNewCharFromIntent() {
        final MorseCode.CharacterList characterData = getLevel0Chars();
        this.newChar = characterData.get(0);
        noButton.setText(characterData.get(1).getPlain());
    }


    @Override
    protected void setYesButtonText() {
        yesButton.setText(getLevel0Chars().get(0).getPlain());
    }

}