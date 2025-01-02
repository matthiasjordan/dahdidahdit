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

package com.paddlesandbugs.dahdidahdit.onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.IntroScreen;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerParamsFaded;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyParamsFaded;
import com.paddlesandbugs.dahdidahdit.params.FadedParameters;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedParamsFaded;

public class OnboardingActivity extends IntroScreen {

    public static final int TEXT_COLOR = R.attr.colorOnPrimary;

    private enum Usertype {
        BEGINNER(99801), //
        INTERMEDIATE(99802), //
        ADVANCED(99803), //
        PRO(99804);

        private final int id;


        Usertype(int id) {
            this.id = id;
        }


        public int id() {
            return id;
        }


        public static Usertype from(int id) {
            for (Usertype u : values()) {
                if (u.id == id) {
                    return u;
                }
            }
            return null;
        }
    }

    public static class Values {
        int wpm;
        int wpmEff;
        int kochLevel;
        String frequency;


        public Values(Context context) {
            Config c = new Config();
            c.update(context);

            frequency = c.freqDah + " Hz";
        }

        public Values(Context context, GeneralFadedParameters cpf) {
            this(context);
            cpf.update(context);

            wpm = cpf.getWpm();
            wpmEff = cpf.getEffWPM();
            kochLevel = cpf.getKochLevel();
        }


        public void setFreq(String freq) {
            frequency = freq;
        }


        public void setWpm(int newValue) {
            wpm = newValue;
        }


        public void setEffWpm(int newValue) {
            wpmEff = newValue;
        }


        private void load(Bundle inState) {
            kochLevel = inState.getInt("kochLevel", kochLevel);
            wpm = inState.getInt("wpm", wpm);
            wpmEff = inState.getInt("wpmEff", wpmEff);
            frequency = inState.getString("frequency", frequency);
        }


        private void persist(@NonNull Bundle outState) {
            outState.putInt("kochLevel", kochLevel);
            outState.putInt("wpm", wpm);
            outState.putInt("wpmEff", wpmEff);
            outState.putString("frequency", frequency);
        }


        @Override
        public String toString() {
            return "Values{" + "wpm=" + wpm + ", wpmEff=" + wpmEff + ", kochLevel=" + kochLevel + ", frequency='" + frequency + '\'' + '}';
        }


    }

    private Usertype usertype = Usertype.BEGINNER;

    private final Map<Usertype, UsertypeScreen> usertypeToRunnable = new HashMap<>();


    private ProficiencySpecificSupplier.Listener listener = null;

    private Values values;


    private final RadioGroup.OnCheckedChangeListener ccl = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            usertype = Usertype.from(checkedId);
            if ((usertype != null) && (listener != null)) {
                listener.handle(usertype);
            }
        }
    };


    public static void callMe(Context context) {
        Intent intent = new Intent(context, OnboardingActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        values = new Values(this, new CopyTrainerParamsFaded(this, "current"));
        if (savedInstanceState != null) {
            int ut = savedInstanceState.getInt("usertype", -1);
            if (ut != -1) {
                usertype = Usertype.from(ut);
            }

            values.load(savedInstanceState);
        } else {
            values.kochLevel = Field.KOCH_LEVEL.defaultValue;
            values.wpm = getResources().getInteger(R.integer.default_value_wpm_general);
            values.wpmEff = getResources().getInteger(R.integer.default_value_effwpm_general);
            values.frequency = getResources().getString(R.string.default_value_frequency_general);

        }

        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("usertype", usertype.id());

        values.persist(outState);
    }


    @Override
    protected List<Supplier> getViews() {
        ArrayList<Supplier> views = new ArrayList<>();

        Supplier welcome = new IntroScreen.Builder(this).bgCol(R.color.theme_primary) //
                .image(R.drawable.ic_paddle) //
                .size(250, 250) //
                .headline(R.string.onboarding_welcome) //
                .text(R.string.onboarding_welcome_text) //
                .supply();
        views.add(welcome);

        Supplier proficiency = new IntroScreen.Builder(this).bgCol(R.color.theme_primary) //
                .text(R.string.onboarding_proficiency_question) //
                .view(getProficiencyCheckboxes(this)) //
                .supply();
        views.add(proficiency);

        Supplier profSpec = new ProficiencySpecificSupplier(this);
        views.add(profSpec);

        Supplier finished = new IntroScreen.Builder(this).bgCol(R.color.theme_primary) //
                .headline(R.string.onboarding_finished) //
                .text(R.string.onboarding_finished_text) //
                .supply();
        views.add(finished);

        return views;
    }


    private View getProficiencyCheckboxes(Context context) {
        RadioButton rb1 = getUsertypeRadioButton(context, Usertype.BEGINNER, R.string.onboarding_proficiency_notatall);
        RadioButton rb2 = getUsertypeRadioButton(context, Usertype.INTERMEDIATE, R.string.onboarding_proficiency_afewletters);
        RadioButton rb3 = getUsertypeRadioButton(context, Usertype.ADVANCED, R.string.onboarding_proficiency_alllettersslowly);
        RadioButton rb4 = getUsertypeRadioButton(context, Usertype.PRO, R.string.onboarding_proficiency_alllettersfast);

        RadioGroup rg = new RadioGroup(context);
        rg.addView(rb1);
        rg.addView(rb2);
        rg.addView(rb3);
        rg.addView(rb4);

        rg.setOnCheckedChangeListener(ccl);

        return rg;
    }


    private RadioButton getUsertypeRadioButton(Context context, Usertype usertype, int labelTextId) {
        RadioButton rb = new RadioButton(context);
        rb.setId(usertype.id());
        rb.setText(labelTextId);
        rb.setChecked(this.usertype == usertype);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 20;
        rb.setLayoutParams(lp);
        int colId = Utils.getThemeColor(context, TEXT_COLOR);
        Builder.style(rb, Builder.DEFAULT_FONT_SIZE_SP, colId);
        rb.setButtonTintList(getRadioButtonColors(colId));
        return rb;
    }


    private ColorStateList getRadioButtonColors(int colId) {
        int col = getResources().getColor(colId);
        return new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, // checked
                new int[]{android.R.attr.state_enabled} // unchecked
        }, new int[]{col, // checked
                col   // unchecked
        });
    }


    @Override
    protected void launchMain() {
        // Onboarding completed
        MainActivity.setActivity(this, MainActivity.ONBOARDING);

        Log.i("ONBOARDING", values.toString());

        int freq = Config.parseFrequency(values.frequency, 600);
        Config c = new Config();
        c.update(this);
        c.freqDah = freq;
        c.freqDit = freq;
        c.persist(this);

        persistSettings();

        switch (usertype) {
            case BEGINNER:
            case INTERMEDIATE:
            case ADVANCED: {
                CopyTrainerActivity.callMe(this);
                break;
            }
            case PRO: {
                MainActivity.callMe(this);
                break;
            }
        }
        finish();
    }


    private void persistSettings() {
        persistSettings(values, new CopyTrainerParamsFaded(this, "current"), new CopyTrainerParamsFaded(this, "to"));
        persistSettings(values, new HeadcopyParamsFaded(this, "current"), new HeadcopyParamsFaded(this, "to"));
        persistSettings(values, new SelfdefinedParamsFaded(this, "current"), null);
    }


    private void persistSettings(Values values, GeneralFadedParameters pc, GeneralFadedParameters pt) {
        pc.update(this);
        pc.setWPM(values.wpm);
        pc.setEffWPM(values.wpmEff);
        pc.setKochLevel(values.kochLevel);
        pc.persist(this);

        if (pt != null) {
            pt.update(this);
            pt.setWPM(Math.max(values.wpm, pt.getWpm()));
            pt.setEffWPM(Math.max(values.wpmEff, pt.getEffWPM()));
            pt.setKochLevel(Math.max(values.kochLevel, pt.getKochLevel()));
            pt.persist(this);
        }
    }


    private class ProficiencySpecificSupplier implements Supplier {
        private final Context context;

        private View baseBeginner;
        private View baseIntermediate;
        private View baseAdvanced;
        private View basePro;

        private final Listener listener;


        public ProficiencySpecificSupplier(Context context) {
            this.context = context;
            listener = new Listener();
        }


        class Listener {
            public void handle(Usertype u) {
                baseBeginner.setVisibility(View.GONE);
                baseIntermediate.setVisibility(View.GONE);
                baseAdvanced.setVisibility(View.GONE);
                basePro.setVisibility(View.GONE);
                OnboardingUtils.playSound = false;

                values = usertypeToRunnable.get(u).values();

                switch (u) {
                    case BEGINNER: {
                        baseBeginner.setVisibility(View.VISIBLE);
                        break;
                    }
                    case INTERMEDIATE: {
                        baseIntermediate.setVisibility(View.VISIBLE);
                        break;
                    }
                    case ADVANCED: {
                        baseAdvanced.setVisibility(View.VISIBLE);
                        break;
                    }
                    case PRO: {
                        basePro.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }


        @Override
        public void becameVisible() {
            OnboardingUtils.playSound = true;
        }


        @Override
        public View get() {
            OnboardingUtils.playSound = false;

            UsertypeScreen ut0 = new UsertypeScreenBeginner(context, values);
            baseBeginner = new Builder(context).bgCol(R.color.theme_primary) //
                    .headline(R.string.onboarding_beginner) //
                    .text(R.string.onboarding_beginner_text) //
                    .add(new Builder.VisibilityListener() {
                        @Override
                        public void visibilityChanged(int visibility) {
                            ut0.onVisible();
                        }
                    }) //
                    .build();
            baseBeginner.setId(Usertype.BEGINNER.id());
            baseBeginner.setVisibility(View.GONE);
            usertypeToRunnable.put(Usertype.BEGINNER, ut0);

            UsertypeScreen ut1 = new UsertypeScreenIntermediate(context, values);
            baseIntermediate = new Builder(context).bgCol(R.color.theme_primary) //
                    .text(R.string.onboarding_beginner_text) //
                    .view(ut1.view()) //
                    .add(new Builder.VisibilityListener() {
                        @Override
                        public void visibilityChanged(int visibility) {
                            ut1.onVisible();
                        }
                    }) //
                    .build();
            baseIntermediate.setId(Usertype.INTERMEDIATE.id());
            baseIntermediate.setVisibility(View.GONE);
            usertypeToRunnable.put(Usertype.INTERMEDIATE, ut1);

            UsertypeScreen ut2 = new UsertypeScreenAdvanced(context, values);
            baseAdvanced = new Builder(context).bgCol(R.color.theme_primary) //
                    .view(ut2.view()) //
                    .add(new Builder.VisibilityListener() {
                        @Override
                        public void visibilityChanged(int visibility) {
                            ut2.onVisible();
                        }
                    }) //
                    .build();
            baseAdvanced.setId(Usertype.ADVANCED.id());
            baseAdvanced.setVisibility(View.GONE);
            usertypeToRunnable.put(Usertype.ADVANCED, ut2);

            UsertypeScreen ut3 = new UsertypeScreenPro(context, values);
            basePro = new Builder(context).bgCol(R.color.theme_primary) //
                    .view(ut3.view()) //
                    .add(new Builder.VisibilityListener() {
                        @Override
                        public void visibilityChanged(int visibility) {
                            ut3.onVisible();
                        }
                    }) //
                    .build();
            basePro.setId(Usertype.PRO.id());
            basePro.setVisibility(View.GONE);
            usertypeToRunnable.put(Usertype.PRO, ut3);

            RelativeLayout rl = new RelativeLayout(context);
            rl.addView(baseBeginner);
            rl.addView(baseIntermediate);
            rl.addView(baseAdvanced);
            rl.addView(basePro);

            OnboardingActivity.this.listener = this.listener;
            this.listener.handle(usertype);

            return rl;
        }


    }


    interface Consumer {
        void accept(int i);
    }


}
