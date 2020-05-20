package com.androidserverside.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import static android.content.Context.ALARM_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {
    AlarmManager alarmManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onReceive(final Context context, final Intent intent) {
        final int ReaptingTime = intent.getIntExtra("NotificationFrequency", 1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ReaptingTime * 1000 * 20, pendingIntent);
        Intent serviceIntent = new Intent(context, NotificationService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startService(serviceIntent);
        }
    }
}