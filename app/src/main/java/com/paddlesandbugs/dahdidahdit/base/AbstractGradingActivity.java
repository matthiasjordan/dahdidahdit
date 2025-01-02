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
import android.view.View;
import android.widget.Button;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.widget.Widgets;

public abstract class AbstractGradingActivity extends AbstractShowTextActivity {


    public static void callMe(Context context, String morseText) {
        Intent intent = new Intent(context, AbstractGradingActivity.class);
        intent.putExtra(TRAINING_TEXT_KEY, morseText);
        context.startActivity(intent);
    }


    @Override
    protected void postCreate(CharSequence plainText) {
        Widgets.notifyPracticed(this);

        int textLen = MorseCode.countRawChars(plainText);

        GradingStrategy.ErrorBounds bounds = getGradingStrategy().getBounds(textLen);
        int lowBelowCount = bounds.lowBelow;
        int mediumBelowCount = bounds.mediumBelow;

        Button lowButton;
        Button mediumButton;
        Button highButton;

        lowButton = (Button) findViewById(R.id.buttonRight);
        mediumButton = (Button) findViewById(R.id.buttonShow);
        highButton = (Button) findViewById(R.id.buttonWrong);

        if (lowButton != null) {
            if (0 == lowBelowCount) {
                lowButton.setText("0");
            } else {
                lowButton.setText(getString(R.string.lowButtonText, 0, lowBelowCount));
            }
        }
        if (mediumButton != null) {
            final int i = lowBelowCount + 1;
            if (i == mediumBelowCount) {
                mediumButton.setText(Integer.toString(i));
            } else if (i > mediumBelowCount) {
                mediumButton.setVisibility(View.INVISIBLE);
            } else {
                mediumButton.setText(getString(R.string.mediumButtonText, i, mediumBelowCount));
            }
        }
        if (highButton != null) {
            final int i = mediumBelowCount + 1;
            if (i == textLen) {
                highButton.setText(Integer.toString(i));
            } else {
                highButton.setText(getString(R.string.highButtonText, i, textLen));
            }
        }

        new Tooltip(this).above(mediumButton).text(R.string.tooltip_grading_countbuttons).iff("countButton").show();
    }


    protected abstract GradingStrategy getGradingStrategy();


    public void onButtonLow(View view) {
        getGradingStrategy().onButtonPress(LearningProgress.Mistake.LOW);
    }


    public void onButtonMedium(View view) {
        getGradingStrategy().onButtonPress(LearningProgress.Mistake.MEDIUM);
    }


    public void onButtonHigh(View view) {
        getGradingStrategy().onButtonPress(LearningProgress.Mistake.HIGH);
    }

}