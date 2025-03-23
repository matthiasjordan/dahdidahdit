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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.brasspound.BrassPoundActivity;
import com.paddlesandbugs.dahdidahdit.brasspound.SendingTrainerActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerLearningStrategy;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerListLearningStrategy;
import com.paddlesandbugs.dahdidahdit.copytrainer.KochSequence;
import com.paddlesandbugs.dahdidahdit.copytrainer.WordKochSequence;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyActivity;
import com.paddlesandbugs.dahdidahdit.learnqcodes.LearnQCodesActivity;
import com.paddlesandbugs.dahdidahdit.learnqcodes.LearnQCodesTrainer;
import com.paddlesandbugs.dahdidahdit.network.NetworkConfigDatabase;
import com.paddlesandbugs.dahdidahdit.network.NetworkListActivity;
import com.paddlesandbugs.dahdidahdit.onboarding.OnboardingActivity;
import com.paddlesandbugs.dahdidahdit.params.Field;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedActivity;
import com.paddlesandbugs.dahdidahdit.settings.ReceivedFile;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;
import com.paddlesandbugs.dahdidahdit.text.Stopwords;
import com.paddlesandbugs.dahdidahdit.widget.Widgets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MainActivity extends AbstractNavigationActivity {

    public static final String LAST_ACTIVITY_KEY = "last_activity";
    public static final String ONBOARDING = "onboarding";
    public static final String COPYTRAINER = "copytrainer";
    public static final String SELFDEFINED = "selfdefined";
    public static final String HEADCOPY = "headcopy";
    public static final String LEARNQCODES = "learnqcodes";
    public static final String BRASSPOUNDER = "brasspounder";
    public static final String NETWORK_LIST = "networklist";
    public static final String NETWORK_ADD = "networkadd";
    public static final String MOPPCLIENT = "moppclient";
    public static final String SENDINGTRAINER = "sendingtrainer";


    /**
     * The prefs key that stores the name of the learning strategy to use.
     */
    public static final String LEARNING_STRATEGY_PREFS_KEY = "learning_strategy";

    /**
     * The name of the learning strategy to use if the current one fails.
     */
    public static final String STANDARD_LEARNING_STRATEGY_NAME = "dahdidahdit";

    public static final Stopwords stopwords = new Stopwords();
    private static final String LOG_TAG = "MainActivity";


    public static void callMe(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopwords.createList(this);

        VersionTracking.init(this);
        if (VersionTracking.isFirstStartAfterUpgrade()) {
            migrate();
        }

        Widgets.init(this);
        initializePreferences();

        LearnQCodesTrainer.init(this);
        NetworkConfigDatabase.DBNetworkConfigProvider.create(this).get(1);


        /*
         * Route to target activity.
         */

        if (handleIntent()) {
            finish();
            return;
        }

        String lastAct = PreferenceManager.getDefaultSharedPreferences(this).getString(LAST_ACTIVITY_KEY, null);

        if (lastAct == null) {
            OnboardingActivity.callMe(this);
            finish();
        } else {
            switch (lastAct) {
                case COPYTRAINER: {
                    CopyTrainerActivity.callMe(this);
                    finish();
                    break;
                }
                case SELFDEFINED: {
                    SelfdefinedActivity.callMe(this);
                    finish();
                    break;
                }
                case HEADCOPY: {
                    HeadcopyActivity.callMe(this, false);
                    finish();
                    break;
                }
                case LEARNQCODES: {
                    LearnQCodesActivity.callMe(this, false);
                    finish();
                    break;
                }
                case BRASSPOUNDER: {
                    BrassPoundActivity.callMe(this);
                    finish();
                    break;
                }
                case NETWORK_LIST:
                case MOPPCLIENT: {
                    NetworkListActivity.callMe(this);
                    finish();
                    break;
                }
                case SENDINGTRAINER: {
                    SendingTrainerActivity.callMe(this);
                    finish();
                    break;
                }
            }
        }


    }


    private void migrate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains("copytrainer_current_level")) {
            movePrefs("copytrainer_current_level", Field.KOCH_LEVEL.getPrefsKey(this, "copytrainer", "current"));
            movePrefs("copytrainer_to_level", Field.KOCH_LEVEL.getPrefsKey(this, "copytrainer", "to"));
        }
    }


    private void movePrefs(String fromKey, String toKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String tmp = prefs.getString(fromKey, null);
        if (tmp != null) {
            prefs//
                    .edit()//
                    .putString(toKey, tmp)//
                    .remove(fromKey) //
                    .apply();
            Log.i(LOG_TAG, "Migrated prefs string from " + fromKey + " to " + toKey);
        }
    }


    private void initializePreferences() {
        setPreferenceDefaults();

        if (VersionTracking.isVeryFirstStart()) {
            SettingsActivity.updateTrainers(this, null);
        }

        if (VersionTracking.getPreviousVersionCode() <= VersionTracking.VERSION_BEFORE_VERSION_TRACKING) {
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
            for (Function<Context, CopyTrainer> ct : nameToCopyTrainerProviders.values()) {
                final CopyTrainer copyTrainer = ct.apply(this);
                final int maxValue = copyTrainer.getSequence().getMax();

                final String curKey = Field.KOCH_LEVEL.getPrefsKey(copyTrainer, "copytrainer", "current");
                final String toKey = Field.KOCH_LEVEL.getPrefsKey(copyTrainer, "copytrainer", "to");
                cap(p, curKey, toKey, maxValue);
            }
        }
    }


    private void setPreferenceDefaults() {
        final int[] prefs = new int[]{ //
                R.xml.prefs_copytrainer_current, //
                R.xml.prefs_copytrainer_main, //
                R.xml.prefs_copytrainer_to, //
                R.xml.prefs_global, //
                R.xml.prefs_headcopy_current, //
                R.xml.prefs_headcopy_main, //
                R.xml.prefs_headcopy_to, //
                R.xml.prefs_root, //
                R.xml.prefs_selfdefined_current, //
                R.xml.prefs_selfdefined_main, //
                R.xml.prefs_sendingtrainer_main, //
        };

        for (int pref : prefs) {
            PreferenceManager.setDefaultValues(this, pref, true);
        }

        final CopyTrainer copyTrainer = getCopyTrainer(this);
        final String key = Field.KOCH_LEVEL.getPrefsKey(copyTrainer, "copytrainer", "to");
        final String current = PreferenceManager.getDefaultSharedPreferences(this).getString(key, null);
        if (current == null) {
            final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(key, Integer.toString(copyTrainer.getSequence().getMax())).apply();
        }

    }


    private void cap(SharedPreferences p, String prefsKeyCurrent, String prefsKeyTo, int maxValue) {
        cap(p, prefsKeyCurrent, 0, maxValue);
        cap(p, prefsKeyTo, maxValue, maxValue);
    }


    private void cap(SharedPreferences p, String prefsKey, int defaultValue, int maxValue) {
        final int sentinel = -1;
        int val = Integer.parseInt(p.getString(prefsKey, Integer.toString(sentinel)));
        if ((val == sentinel) || (val > maxValue)) {
            // Value is not set or exceeds maximum allowed value
            if (val == sentinel) {
                val = defaultValue;
            }
            if (val > maxValue) {
                val = maxValue;
            }
            final String valStr = Integer.toString(val);
            p.edit().putString(prefsKey, valStr).apply();
            Log.v(LOG_TAG, "Capped " + prefsKey + " to " + valStr);
        }
    }


    private static final Map<String, Function<Context, CopyTrainer>> nameToCopyTrainerProviders = createCopyTrainerMap();


    public static Map<String, Function<Context, CopyTrainer>> getNameToCopyTrainerProviders() {
        return nameToCopyTrainerProviders;
    }


    @NonNull
    private static Map<String, Function<Context, CopyTrainer>> createCopyTrainerMap() {
        final HashMap<String, Function<Context, CopyTrainer>> map = new HashMap<>();
        map.put("wordkoch", c -> new CopyTrainer(c, new WordKochSequence(), CopyTrainerListLearningStrategy::new));
        map.put(STANDARD_LEARNING_STRATEGY_NAME, c -> new CopyTrainer(c, new KochSequence(), CopyTrainerLearningStrategy::new));
        return Collections.unmodifiableMap(map);
    }


    public static CopyTrainer getCopyTrainer(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String learnStratName = prefs.getString(LEARNING_STRATEGY_PREFS_KEY, STANDARD_LEARNING_STRATEGY_NAME);
        final Map<String, Function<Context, CopyTrainer>> hashMap = nameToCopyTrainerProviders;
        Function<Context, CopyTrainer> copyTrainerFunc = hashMap.get(learnStratName);
        if (copyTrainerFunc == null) {
            copyTrainerFunc = hashMap.get(STANDARD_LEARNING_STRATEGY_NAME);
        }
        CopyTrainer trainer = copyTrainerFunc.apply(context);
        return trainer;
    }


    @Override
    protected int createTitleID() {
        return R.string.app_name;
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }


    @Override
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_ROOT;
    }


    /**
     * @return true, if handling intent leads to opening another activity. Else false.
     */
    private boolean handleIntent() {
        Intent i = getIntent();
        if (i != null) {
            Log.i(LOG_TAG, i.toString());
            if ("android.intent.action.SEND".equals(i.getAction())) {

                final String txt = i.getStringExtra(Intent.EXTRA_TEXT);
                //                final String title = i.getStringExtra(Intent.EXTRA_TITLE);
                if (txt != null) {
                    new ReceivedFile(this, SelfdefinedActivity.RECEIVED_FILE_NAME).store(txt);
                    SettingsActivity.callMe(this);
                    return true;
                }
            }
        }

        return false;
    }


    public static void setActivity(Context context, String name) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LAST_ACTIVITY_KEY, name).apply();
    }
}