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

package com.paddlesandbugs.dahdidahdit.brasspound;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.GradingStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningProgress;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParams;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.text.PrefsBasedTextGeneratorFactory;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

public class SendingLearningStrategy implements LearningStrategy, GradingStrategy {

    public static final int RECENT_STEPS = 5;

    private final SharedPreferences prefs;

    private final LearningProgress learningProgress;

    private final LearningValue wpm;

    private final Context context;


    public SendingLearningStrategy(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.learningProgress = new LearningProgress(prefs, "sending_progress", 10, RECENT_STEPS);
        int defaultWpm = context.getResources().getInteger(R.integer.default_value_wpm_sending);
        this.wpm = new LearningValue(context, "sendingtrainer_current_wpm", 1, defaultWpm, 40);
    }


    public LearningValue getWpm() {
        return wpm;
    }


    @Override
    public void onSettingsChanged(String key) {

    }


    @Override
    public SessionConfig getSessionConfig() {
        Config gc = new Config();
        gc.update(context);

        GeneralParameters p = new CopyTrainerParams(context);

        final SessionConfig sc = new SessionConfig(createConfig(gc, p));
        return sc;
    }


    private MorsePlayer.Config createConfig(Config gc, GeneralParameters p) {
        // Apply learning adjustments
        TextGenerator tg = createTextGenerator();

        // Assemble final temp config for playing
        final MorsePlayer.Config config = new MorsePlayer.Config().from(context, p).from(gc);
        config.textGenerator = tg;
        return config;
    }


    private TextGenerator createTextGenerator() {
        final String textGen = prefs.getString("sendingtrainer_text_generator", "");
        final int wordListCount = prefs.getInt("sendingtrainer_text_first_n", -1);
        final boolean useOnlyKoch = prefs.getBoolean("sendingtrainer_only_koch_chars", false);

        final int kochLevel;
        if (useOnlyKoch) {
            kochLevel = getKochLevel();
        }
        else {
            kochLevel = 40;
        }

        final PrefsBasedTextGeneratorFactory f = new PrefsBasedTextGeneratorFactory(SendingTrainerActivity.RECEIVED_FILE_NAME, 4, kochLevel);
        f.setWordListCount(wordListCount);
        final TextGenerator tg = f.getGenerator(context, textGen);
        tg.setWordLengthMax(4);
        return tg;
    }


    private int getKochLevel() {
        CopyTrainerParamsFaded cpf = new CopyTrainerParamsFaded(context, "current");
        cpf.update(context);
        int kochLevel = cpf.getKochLevel();
        return kochLevel;
    }



    @Override
    public ErrorBounds getBounds(int textLen) {
        return null;
    }


    @Override
    public void onButtonPress(LearningProgress.Mistake level) {
        switch (level) {
            case LOW: {
                buttonLow();
                break;
            }
            case MEDIUM: {
                buttonMedium();
                break;
            }
            case HIGH:
            default: {
                buttonHigh();
                break;
            }
        }
    }


    private void showLevelToast(int textRsrc, LearningValue newValue) {
        final String fieldStr = context.getString(Field.WPM.getStringId());
        final String toastText = context.getString(textRsrc, fieldStr, newValue.get());
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
    }


    private void buttonLow() {
        learningProgress.update(LearningProgress.Mistake.LOW);
        if (learningProgress.countRecentMistakes().get(LearningProgress.Mistake.LOW) == RECENT_STEPS) {
            wpm.update(1);
            showLevelToast(R.string.toast_text_level_up, wpm);
            learningProgress.markRecents();
        }
    }


    private void buttonMedium() {
        learningProgress.update(LearningProgress.Mistake.MEDIUM);
    }


    private void buttonHigh() {
        learningProgress.update(LearningProgress.Mistake.HIGH);
        if (learningProgress.countRecentMistakes().get(LearningProgress.Mistake.HIGH) == RECENT_STEPS) {
            wpm.update(-1);
            showLevelToast(R.string.toast_text_level_down, wpm);
            learningProgress.markRecents();
        }
    }
}
