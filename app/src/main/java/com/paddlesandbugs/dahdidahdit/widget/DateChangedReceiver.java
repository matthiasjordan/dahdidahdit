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

package com.paddlesandbugs.dahdidahdit.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import com.paddlesandbugs.dahdidahdit.Utils;

/**
 * Broadcast receiver that updates the widget.
 */
public class DateChangedReceiver extends BroadcastReceiver {

    public static final String ACTION_WIDGET_UPDATE = "com.paddlesandbugs.dahdidahdit.UPDATE_WIDGET";


    @Override
    public void onReceive(Context context, Intent intent) {

        if ((intent == null) || (intent.getAction() == null)) {
            return;
        }

        switch (intent.getAction()) {
            case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
            case ACTION_WIDGET_UPDATE: {
                Log.i("UpdateRec", "Update intent received");
                Widgets.refresh(context);
                break;
            }
        }
    }


    public static void startRecurringAlarm(Context context) {
        final Object o = context.getSystemService(Context.ALARM_SERVICE);
        Log.i("UpdateRec", "Update intent about to be scheduled");
        if (o instanceof AlarmManager) {
            AlarmManager alarmManager = (AlarmManager) o;

            Intent i = new Intent(context, DateChangedReceiver.class);
            i.setAction(ACTION_WIDGET_UPDATE);
            i.setFlags(Intent.FLAG_FROM_BACKGROUND);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pi);

            final long now = System.currentTimeMillis();
            final long startOfDayMs = Utils.getStartOfDayMs(now);
            final long startOfAlarmMs = startOfDayMs + TimeUnit.HOURS.toMillis(3);
            final long delta = TimeUnit.DAYS.toMillis(1);

            alarmManager.setInexactRepeating(AlarmManager.RTC, startOfAlarmMs + delta, delta, pi);
            Log.i("UpdateRec", "Update intent scheduled");
        }
    }

}
