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

package com.paddlesandbugs.dahdidahdit.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class Widgets extends AppWidgetProvider {


    private static final String PRACTICE_LEARN_TIMESTAMP = "practice_learn_timestamp";
    private static final String PRACTICE_LEARN_DAYSINAROW = "practice_learn_daysinarow";
    private static final String PRACTICE_LEARN_MAXDAYSINAROW = "practice_learn_maxdaysinarow";


    public static class PracticeData {

        public long nowPracticedMs;
        public long lastPracticedMs;
        public int daysInARow;
        public int maxDaysInARow;


        PracticeData() {
            nowPracticedMs = 0; // Make the compiler happy
        }


        public PracticeData(Context context) {
            this(PreferenceManager.getDefaultSharedPreferences(context));
        }


        public PracticeData(SharedPreferences prefs) {
            nowPracticedMs = System.currentTimeMillis();
            lastPracticedMs = prefs.getLong(PRACTICE_LEARN_TIMESTAMP, nowPracticedMs);
            daysInARow = prefs.getInt(PRACTICE_LEARN_DAYSINAROW, 0);
            maxDaysInARow = prefs.getInt(PRACTICE_LEARN_MAXDAYSINAROW, 0);
        }


        public int daysSinceLastPracticeDay() {
            return Utils.differenceInDays(lastPracticedMs, nowPracticedMs);
        }


        @Override
        public String toString() {
            return "PracticeData{" + "nowPracticedMs=" + nowPracticedMs + ", lastPracticedMs=" + lastPracticedMs + ", daysInARow=" + daysInARow + ", maxDaysInARow=" + maxDaysInARow + '}';
        }
    }


    public static void init(Context context) {
        DateChangedReceiver.startRecurringAlarm(context);
        Widgets.refresh(context);
    }


    public static void notifyPracticed(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        PracticeData data = new PracticeData(prefs);
        updatePracticed(data);

        SharedPreferences.Editor prefsEd = prefs.edit();
        prefsEd.putInt(PRACTICE_LEARN_MAXDAYSINAROW, data.maxDaysInARow);
        prefsEd.putInt(PRACTICE_LEARN_DAYSINAROW, data.daysInARow);
        prefsEd.putLong(PRACTICE_LEARN_TIMESTAMP, data.nowPracticedMs);
        prefsEd.apply();

        notifyAll(context, data);
    }


    static void updatePracticed(PracticeData data) {
        resetDaysInARow(data);

        if (data.daysInARow == 0) {
            data.daysInARow = 1;
        } else if (data.daysSinceLastPracticeDay() == 1) {
            // User practiced yesterday and today
            data.daysInARow += 1;
        }

        if (data.daysInARow > data.maxDaysInARow) {
            // New maximum achieved
            data.maxDaysInARow = data.daysInARow;
        }

        data.lastPracticedMs = data.nowPracticedMs;
    }


    static void resetDaysInARow(PracticeData data) {
        if (data.daysSinceLastPracticeDay() >= 2) {
            data.daysInARow = 0;
        }
    }


    public static void refresh(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        PracticeData data = new PracticeData(prefs);
        resetDaysInARow(data);

        notifyAll(context, data);
    }


    private static void notifyAll(Context context, PracticeData data) {
        notifyPracticed(context, DaysPracticedWidget1x1.class, R.layout.days_practiced_widget_1x1, data);
        notifyPracticed(context, DaysPracticedWidget2x2.class, R.layout.days_practiced_widget_2x2, data);
    }


    private static void notifyPracticed(Context context, Class<? extends AppWidgetProvider> clazz, int layoutId, PracticeData data) {
        final ComponentName componentName = new ComponentName(context, clazz);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(componentName);
        for (int id : ids) {
            updateAppWidget(context, appWidgetManager, id, layoutId, data);
        }
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int layoutId, PracticeData data) {

        final boolean notYetPracticedToday = data.daysSinceLastPracticeDay() != 0;

        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
        final String text = Integer.toString(data.daysInARow) + (notYetPracticedToday ? "!" : "");
        views.setTextViewText(R.id.appwidget_daysinarow_value, text);

        views.setTextViewText(R.id.appwidget_maxdaysinarow_value, Integer.toString(data.maxDaysInARow));

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.appwidget_container, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        //appWidgetManager.updateAppWidget(new ComponentName(context, DaysPracticedWidget.class), views);
    }

}