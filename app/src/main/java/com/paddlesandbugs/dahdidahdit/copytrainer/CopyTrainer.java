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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.DefaultLearningStrategy;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;

public class CopyTrainer {

    private final Context context;
    private final LearningSequence sequence;
    private final Function<Context, DefaultLearningStrategy> learnStratSupplier;


    public CopyTrainer(Context context, LearningSequence kochSequence, Function<Context, DefaultLearningStrategy> learnStratSupplier) {
        this.context = context;
        this.sequence = kochSequence;
        this.learnStratSupplier = learnStratSupplier;
    }


    public LearningSequence getSequence() {
        return sequence;
    }


    /**
     * Gets all characters up to and including the given level.
     *
     * @param lvl the level
     *
     * @return the characters
     */
    public List<MorseCode.CharacterList> getChars(int lvl) {
        List<MorseCode.CharacterList> list = new ArrayList<>();
        for (int i = 0; (i <= lvl); i++) {
            list.add(sequence.getChar(i));
        }
        return list;
    }


    /**
     * Gets all characters up to and including the given level.
     *
     * @param lvl the level
     *
     * @return the characters
     */
    public MorseCode.CharacterList getCharsFlat(int lvl) {
        MorseCode.CharacterList list = new MorseCode.MutableCharacterList();
        for (int i = 0; (i <= lvl); i++) {
            final MorseCode.CharacterList aChar = sequence.getChar(i);
            for (MorseCode.CharacterData cd : aChar) {
                list.add(cd);
            }
        }
        return list;
    }


    private String key(LearningSequence sequence) {
        return "copytrainer_next_" + sequence.getPrefsKeyInfix();
    }


    public void prepareRerouteNextCharLearning(MorseCode.CharacterList nextChars) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key(sequence), nextChars.asString()).apply();
    }


    public MorseCode.CharacterList getRerouteNextCharLearning() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String str = sharedPreferences.getString(key(sequence), null);
        if ((str != null) && (!str.isEmpty())) {
            return new MorseCode.MutableCharacterList(str);
        } else {
            return new MorseCode.MutableCharacterList();
        }
    }


    public void resetRerouteNextCharLearning() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key(sequence)).apply();
    }


    public CharSequence[] getAllCharLabels() {
        List<MorseCode.CharacterList> cl = getChars(sequence.getMax());
        List<CharSequence> s = new ArrayList<>(cl.size());
        for (MorseCode.CharacterList c : cl) {
            StringBuilder label = new StringBuilder();
            for (MorseCode.CharacterData cd : c) {
                if (label.length() != 0) {
                    label.append(", ");
                }
                label.append(cd.getPlain().toUpperCase());
            }
            s.add(label.toString());
        }
        return s.toArray(new CharSequence[0]);
    }


    /**
     * Supplies the currently used {@link LearningStrategy}.
     **
     * @return the {@link LearningStrategy}
     */
    public DefaultLearningStrategy get() {
        return learnStratSupplier.apply(context);
    }


    /**
     * This takes care of routing the user through the learning part of the copy trainer: {@link LearnNewCharActivity}, {@link FindTheCharActivity}
     * and {@link FindTheKMActivity}.
     * <p>
     * Entry points to this activity subsystem are:
     *     <ul>
     *         <li>{@link CopyTrainerIntro#launchMain()}</li>
     *         <li>{@link CopyTrainerLearningStrategy#routeToProgress(MorseCode.CharacterData)}</li>
     *         <li>{@link CopyTrainerActivity#relearnLastChar()}</li>
     *     </ul>
     * </p>
     *
     * @param activity the activity
     * @param clazz   who's calling?
     */
    public boolean rerouteLearning(Activity activity, Class<? extends Activity> clazz) {
        MorseCode.CharacterList nextChars = getRerouteNextCharLearning();
        if (nextChars.size() != 0) {
            LearnNewCharActivity.callMe(activity, nextChars);
            activity.finish();
            return true;
        }

        CopyTrainerParamsFaded pf = new CopyTrainerParamsFaded(context, "current");
        pf.update(activity);
        int kochLevel = pf.getKochLevel();

        if (clazz == CopyTrainerActivity.class) {
        } else if (clazz == LearnNewCharActivity.class) {
            if (kochLevel == 0) {
                FindTheKMActivity.callMe(activity);
                activity.finish();
                return true;
            } else {
                FindTheCharActivity.callMe(activity, sequence.getChar(kochLevel).pop());
                activity.finish();
                return true;
            }
        } else if (clazz == FindTheCharActivity.class || clazz == FindTheKMActivity.class) {
            CopyTrainerActivity.callMe(activity);
            activity.finish();
            return true;
        }

        return false;
    }


}
