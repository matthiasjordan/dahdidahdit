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

package com.paddlesandbugs.dahdidahdit.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.concurrent.atomic.AtomicInteger;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyTrainer;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedTrainer;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    public static final String SETTINGS_PART_ROOT = "root";

    public static final String SETTINGS_PART_COPYTRAINER = "copytrainer";

    public static final String SETTINGS_PART_SELFDEFINED = "selfdefined";

    public static final String SETTINGS_PART_HEADCOPY = "headcopy";

    public static final String SETTINGS_PART_SENDINGTRAINER = "sendingtrainer";

    static final String LOG_TAG = "SettingsActivity";

    private static final String TITLE_TAG = "settingsActivityTitle";
    //    private static final int REQUEST_CODE_RSSPROVIDER = 1;

    /**
     * This has to be a field because otherwise the listener gets garbage collected.
     */
    private SharedPreferences.OnSharedPreferenceChangeListener configChangedListener;


    public static void callMe(Context context, String settingsPart) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("part", settingsPart);
        context.startActivity(intent);
    }


    public static void callMe(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("part", SETTINGS_PART_ROOT);
        context.startActivity(intent);
    }


    public static void addWordListChangeListener(PreferenceFragmentCompat context, String listPrefKey, String numPrefKey) {
        final Preference.OnPreferenceChangeListener textGenChangeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Preference textListSeekBar = context.findPreference(numPrefKey);
                if (textListSeekBar != null) {
                    boolean isWordListActive = "2000words".equals(newValue);
                    textListSeekBar.setVisible(isWordListActive);
                }

                ListPreference providerSelection = context.findPreference("selfdefined_rss_provider");
                if (providerSelection != null) {
                    boolean isRssListActive = "rss".equals(newValue);
                    providerSelection.setVisible(isRssListActive);
                    ListPreference feedSelection = context.findPreference("selfdefined_rss_feed");
                    feedSelection.setVisible(isRssListActive);
                }

                return true;
            }
        };

        ListPreference textPref = context.findPreference(listPrefKey);
        textPref.setOnPreferenceChangeListener(textGenChangeListener);
        textPref.callChangeListener(textPref.getValue());
    }

    //    private void checkPerms(String perm) {
    //        if (getBaseContext().checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED) {
    //            // You can use the API that requires the permission.
    //            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
    //            new RSSUpdateRunnable(providerSelection, feedSelection, newProviderStr).run();
    //        } else if (shouldShowRequestPermissionRationale(perm)) {
    //            // In an educational UI, explain to the user why your app requires this
    //            // permission for a specific feature to behave as expected. In this UI,
    //            // include a "cancel" or "no thanks" button that allows the user to
    //            // continue using your app without granting the permission.
    //            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
    //        } else {
    //            // You can directly ask for the permission.
    //            Toast.makeText(this, "Asking for permissions", Toast.LENGTH_SHORT).show();
    //            requestPermissions(new String[]{perm}, REQUEST_CODE_RSSPROVIDER);
    //        }
    //    }

    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //        switch (requestCode) {
    //            case REQUEST_CODE_RSSPROVIDER:
    //                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                    // Permission is granted. Continue the action or workflow in your app.
    //                    new RSSUpdateRunnable(providerSelection, feedSelection, newProviderStr).run();
    //                } else {
    //                    // Explain to the user that the feature is unavailable because
    //                    // the features requires a permission that the user has denied.
    //                    // At the same time, respect the user's decision. Don't link to
    //                    // system settings in an effort to convince the user to change
    //                    // their decision.
    //                    Toast.makeText(this, "No permission, so cannot request articles. Sorry", Toast.LENGTH_SHORT).show();
    //                }
    //                return;
    //        }
    //    }


    /**
     * Updates trainer preferences (e.g. learning strategies) if a faded setting has been changed.
     *
     * @param context the context
     * @param key     the name of the setting changed, or null, if trainers should be updated in any case
     */
    public static void updateTrainers(Context context, String key) {
        if ((key == null) || //
                ((key.contains("_current_") || key.contains("_to_")))) {
            MainActivity.getCopyTrainer(context).get().onSettingsChanged(key);
            SelfdefinedTrainer.get(context).onSettingsChanged(key);
            HeadcopyTrainer.get(context).onSettingsChanged(key);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            final PreferenceFragmentCompat fragment = getSettingsPart();
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, fragment).commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    setTitle(R.string.title_activity_settings);
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.i(LOG_TAG, "ActionBar setup");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private PreferenceFragmentCompat getSettingsPart() {
        final String part = getIntent().getStringExtra("part");
        if (SETTINGS_PART_COPYTRAINER.equals(part)) {
            return new CopyTrainerFragment();
        }
        if (SETTINGS_PART_SELFDEFINED.equals(part)) {
            return new SelfdefinedFragment();
        }
        if (SETTINGS_PART_HEADCOPY.equals(part)) {
            return new HeadcopyFragment();
        }
        if (SETTINGS_PART_SENDINGTRAINER.equals(part)) {
            return new SendingTrainerFragment();
        }
        if (SETTINGS_PART_ROOT.equals(part)) {
            return new HeaderFragment();
        }

        return new HeaderFragment();
    }


    @Override
    protected void onResume() {
        super.onResume();

        final Context context = this;

        configChangedListener = new SettingsChangeListener(context);

        Log.i(LOG_TAG, "onResume()");
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(configChangedListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause()");

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(configChangedListener);
        configChangedListener = null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }


    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        onBackPressed();
        return true;
    }


    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.settings, fragment).addToBackStack(null).commit();
        setTitle(pref.getTitle());
        return true;
    }

    @Keep
    private static class SettingsChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        private final Context context;

        private final AtomicInteger clients = new AtomicInteger();


        public SettingsChangeListener(Context context) {
            this.context = context;
        }


        @Override
        public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Make sure this isn't descending into a listener trigger loop
            if (clients.incrementAndGet() == 1) {
                updateTrainers(context, key);
            }

            clients.decrementAndGet();
        }

    }


}