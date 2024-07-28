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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.brasspound.BrassPoundActivity;
import com.paddlesandbugs.dahdidahdit.brasspound.SendingTrainerActivity;
import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainerActivity;
import com.paddlesandbugs.dahdidahdit.headcopy.HeadcopyActivity;
import com.paddlesandbugs.dahdidahdit.learnqcodes.LearnQCodesActivity;
import com.paddlesandbugs.dahdidahdit.network.NetworkListActivity;
import com.paddlesandbugs.dahdidahdit.selfdefined.SelfdefinedActivity;
import com.paddlesandbugs.dahdidahdit.settings.SettingsActivity;

public abstract class AbstractNavigationActivity extends AppCompatActivity {


    private static final String LOG_TAG = AbstractNavigationActivity.class.getSimpleName();

    private static final String HELP_URI_PATTERN = "https://paddlesandbugs.com/dahdidahdit-manual/$name";

    private final NavigationListener navigationListener = new NavigationListener();


    protected abstract int getLayoutID();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(getMenuID(), menu);

        if (getHelpPageName() != null) {
            menu.add(Menu.NONE, R.id.menu_item_help, Menu.CATEGORY_SECONDARY | 99, R.string.action_help);
        }

        return true;
    }


    protected int getMenuID() {
        return R.menu.menu_main;
    }


    protected String getHelpPageName() {
        return null;
    }


    /**
     * Which part of the settings should be displayed.
     *
     * @return the settings part string.
     *
     * @see SettingsActivity#SETTINGS_PART_COPYTRAINER and similar
     */
    protected String getSettingsPart() {
        return SettingsActivity.SETTINGS_PART_ROOT;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                String settingsPart = getSettingsPart();
                SettingsActivity.callMe(this, settingsPart);
                return true;
            }
            case R.id.menu_item_help: {
                String helpPageName = getHelpPageName();
                if (helpPageName != null) {
                    String uri = HELP_URI_PATTERN.replace("$name", helpPageName);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(browserIntent);
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        initializeNavigationDrawer();

        ReleaseNotes.showIf(this);
    }


    private void initializeNavigationDrawer() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.app_name, R.string.app_version_name);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView1 != null) {
            navigationView1.setNavigationItemSelectedListener(navigationListener);
        }
        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view_bottom);
        if (navigationView2 != null) {
            navigationView2.setNavigationItemSelectedListener(navigationListener);
        }

    }


    protected abstract int createTitleID();


    private Context getContext() {
        return this;
    }


    @Override
    public boolean onSupportNavigateUp() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
            return false;
        } else {
            onBackPressed();
            return false;
        }
    }


    /**
     * Handles the Back button: closes the nav drawer.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(createTitleID());
    }


    private class NavigationListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            final Context context = getContext();

            final int itemId = item.getItemId();
            switch (itemId) {
                case R.id.nav_copytrainer: {
                    CopyTrainerActivity.callMe(context);
                    ((Activity) context).finish();
                    break;
                }
                case R.id.nav_selfdefined: {
                    SelfdefinedActivity.callMe(context);
                    ((Activity) context).finish();
                    break;
                }
                case R.id.nav_headcopy: {
                    HeadcopyActivity.callMe(context, false);
                    ((Activity) context).finish();
                    break;
                }
                case R.id.nav_settings: {
                    SettingsActivity.callMe((Activity) context);
                    break;
                }
                case R.id.nav_learnqcodes: {
                    LearnQCodesActivity.callMe(context, false);
                    ((Activity) context).finish();
                    break;
                }
                case R.id.nav_brasspounder: {
                    BrassPoundActivity.callMe(context);
                    ((Activity) context).finish();
                    break;
                }
                case R.id.nav_network_list: {
                    NetworkListActivity.callMe(context);
                    ((Activity) context).finish();
                    break;
                }
                case R.id.nav_sending_trainer: {
                    SendingTrainerActivity.callMe(context);
                    ((Activity) context).finish();
                    break;
                }
            }
            return false;
        }
    }


}