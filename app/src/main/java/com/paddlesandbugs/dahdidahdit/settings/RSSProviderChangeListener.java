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

package com.paddlesandbugs.dahdidahdit.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.text.RssTextGenerator;

@Keep
class RSSProviderChangeListener implements Preference.OnPreferenceChangeListener {
    private final ListPreference providerSelection;
    private final ListPreference feedSelection;

    private final Context context;


    public RSSProviderChangeListener(Context context, ListPreference providerSelection, ListPreference feedSelection) {
        this.context = context;
        this.providerSelection = providerSelection;
        this.feedSelection = feedSelection;
    }


    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newProvider) {
        boolean ok = false;
        if (feedSelection != null) {
            final String newProviderStr = (String) newProvider;
            boolean isProviderActive = !Utils.isEmpty(newProviderStr);
            if (isProviderActive) {
                new RSSUpdateRunnable(providerSelection, feedSelection, newProviderStr).run();
                ok = true;
            }
        }
        return ok;
    }


    @Keep
    private static class RSSUpdateRunnable implements Runnable {


        private final ListPreference providerSelection;
        private final ListPreference feedSelection;
        private final String newProvider;


        public RSSUpdateRunnable(ListPreference providerSelection, ListPreference feedSelection, String newProvider) {
            this.providerSelection = providerSelection;
            this.feedSelection = feedSelection;
            this.newProvider = newProvider;
        }


        @Override
        public void run() {
            List<RssTextGenerator.Feed> feeds = RssTextGenerator.listFeeds(providerSelection.getContext(), newProvider);
            CharSequence[] entries = transformEntries(feeds);
            CharSequence[] values = transformEntryValues(feeds);
            final boolean noEntries = entries.length == 0;
            feedSelection.setEntryValues(values);
            feedSelection.setEntries(entries);
            feedSelection.setSelectable(!noEntries);
            if (noEntries) {
                feedSelection.setValue(null);
            } else {
                String feedId = PreferenceManager.getDefaultSharedPreferences(providerSelection.getContext()).getString("selfdefined_rss_feed", "");
                feedSelection.setValue(feedId);
            }
        }


        private CharSequence[] transformEntries(List<RssTextGenerator.Feed> feeds) {
            ArrayList<CharSequence> list = new ArrayList<>();
            for (RssTextGenerator.Feed feed : feeds) {
                list.add(feed.name);
            }
            return list.toArray(new CharSequence[list.size()]);
        }


        private CharSequence[] transformEntryValues(List<RssTextGenerator.Feed> feeds) {
            ArrayList<CharSequence> list = new ArrayList<>();
            for (RssTextGenerator.Feed feed : feeds) {
                list.add(feed.id);
            }
            return list.toArray(new CharSequence[list.size()]);
        }

    }
}
