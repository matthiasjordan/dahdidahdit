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

package com.paddlesandbugs.dahdidahdit.text;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.CompressedIntSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RssTextGenerator extends AbstractWordTextGenerator {

    private static final String LOG_TAG = "RssTxtGen";

    private static final Map<String, String> authToPerm;


    static {
        final HashMap<String, String> m = new HashMap<>();
        m.put("com.nononsenseapps.feeder.rssprovider", "com.nononsenseapps.feeder.permission.read");
        m.put("com.nononsenseapps.feeder.play.rssprovider", "com.nononsenseapps.feeder.play.permission.read");
        /*
            If editing this, also edit
            AndroidManifest.xml
            settings-arrays.xml
            strings.xml
         */

        authToPerm = Collections.unmodifiableMap(m);
    }


    private Context context;

    private final Cursor cursor;

    private WordIterator words;

    private CompressedIntSet articlesSeen = new CompressedIntSet();

    private String prefsName;

    private int articleNum = 0;


    public RssTextGenerator(Context context, String providerAuthority, String feedId, Stopwords stopwords) {
        this(getArticleCursor(context, providerAuthority, feedId), stopwords);

        this.context = context;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefsName = "articles_seen_" + providerAuthority + "_" + feedId;
        String str = prefs.getString(prefsName, "");
        articlesSeen = CompressedIntSet.fromString(str);
    }


    public RssTextGenerator(Cursor articles, Stopwords stopwords) {
        super(stopwords, true);
        this.cursor = articles;
    }


    @Override
    protected int getMaxWordLength() {
        return Integer.MAX_VALUE;
    }


    @NonNull
    private static String uri(String authority) {
        return "content://" + authority;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_rss;
    }


    private void markArticle(int id) {
        articlesSeen.add(id);

        if (prefsName != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String str = articlesSeen.asString();
            prefs.edit().putString(prefsName, str).apply();
        }
    }


    @Override
    protected MorseCode.CharacterList generateNextWord() {
        WordIterator it = getIt();
        if ((it != null) && it.hasNext()) {
            return new MorseCode.MutableCharacterList(it.next().toLowerCase());
        } else {
            return null;
        }
    }


    private WordIterator getIt() {
        if ((words == null) || !words.hasNext()) {

            words = null;

            while ((words == null) && (cursor != null) && cursor.moveToNext()) {
                int idC = cursor.getColumnIndex("id");
                int titleC = cursor.getColumnIndex("title");
                int textC = cursor.getColumnIndex("text");

                int id = cursor.getInt(idC);
                if (!articlesSeen.contains(id)) {
                    markArticle(id);
                    String title = cursor.getString(titleC).trim();
                    String text = cursor.getString(textC).trim();
                    String sep = (articleNum > 0) ? "= " : "";

                    String out = sep + title + " = " + text;

                    Log.i(LOG_TAG, "Next article: " + out);

                    words = new WordIterator(out);
                    articleNum += 1;
                }
            }

        }
        return words;
    }


    public static class Feed {
        public final String id;
        public final String name;


        public Feed(String id, String name) {
            this.id = id;
            this.name = name;
        }


        @Override
        @NonNull
        public String toString() {
            return "Feed{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
        }
    }


    /**
     * Lists feeds of the given provider.
     *
     * @param context  the context
     * @param provAuth the name of the provider
     * @return the list of feeds, which might be empty, but never null.
     */
    @NonNull
    public static List<Feed> listFeeds(Context context, String provAuth) {
        ArrayList<Feed> list = new ArrayList<>();

        final String perm = getPerm(provAuth);
        if (perm != null) {
            int res = ContextCompat.checkSelfPermission(context, perm);
            if (res != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, R.string.rss_permission_missing, Toast.LENGTH_SHORT).show();
                return list;
            }
        }

        CursorLoader cl = new CursorLoader(context);
        cl.setUri(Uri.withAppendedPath(Uri.parse(uri(provAuth)), "feeds"));

        try (Cursor cursor = cl.loadInBackground();) {

            if (cursor == null) {
                return list;
            }

            int idC = cursor.getColumnIndex("id");
            int nameC = cursor.getColumnIndex("title");
            while (cursor.moveToNext()) {
                String id = cursor.getString(idC);
                String name = cursor.getString(nameC);
                Feed f = new Feed(id, name);
                list.add(f);
            }
        } catch (Exception e) {
            if (e instanceof SecurityException) {
                Toast.makeText(context, R.string.rss_permission_missing, Toast.LENGTH_SHORT).show();
            }

            Log.e(LOG_TAG, "Permission to read RSS feed names denied", e);
        }

        return list;
    }


    /**
     * Returns the permission needed for the given provider
     *
     * @param provAuth the authority of the provider
     * @return the permission
     */
    @Nullable
    public static String getPerm(String provAuth) {
        return authToPerm.get(provAuth);
    }


    private static class Article {
        public final String id;
        public final String title;
        public final String text;


        private Article(String id, String title, String text) {
            this.id = id;
            this.title = title;
            this.text = text;
        }


        @Override
        @NonNull
        public String toString() {
            return "Article{" + id + " title='" + title + '\'' + ", text='" + text + '\'' + '}';
        }


    }


    private static Cursor getArticleCursor(Context context, String providerAuthority, String feedId) {
        CursorLoader cl = new CursorLoader(context);
        cl.setUri(Uri.withAppendedPath(Uri.parse(uri(providerAuthority)), "articles/" + feedId));
        try {
            return cl.loadInBackground();
        } catch (SecurityException e) {
            return null;
        }
    }


    private static List<Article> listItems(Context context, String providerAuthority, String feedId) {
        ArrayList<Article> list = new ArrayList<>();
        CursorLoader cl = new CursorLoader(context);
        cl.setUri(Uri.withAppendedPath(Uri.parse(uri(providerAuthority)), "articles/" + feedId));

        try {
            Cursor cursor = cl.loadInBackground();

            if (cursor == null) {
                return list;
            }

            int idC = cursor.getColumnIndex("id");
            int titleC = cursor.getColumnIndex("title");
            int textC = cursor.getColumnIndex("text");
            while (cursor.moveToNext()) {
                String id = cursor.getString(idC);
                String title = cursor.getString(titleC);
                String text = cursor.getString(textC);
                Article a = new Article(id, title, text);
                list.add(a);
            }
        } catch (Exception e) {
            // Was worth the try
        }
        return list;
    }

}
