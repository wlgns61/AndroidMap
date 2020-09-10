package com.example.androidproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;

public class BootReceiver extends BroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    public void onReceive(Context context, Intent intent) {

        //스마트폰이 재부팅 되었다면

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("com.example.androidproject.BootReceiver")) {
            alarmIntent = PendingIntent.getBroadcast(context,1, new Intent(context,AlarmReceiver.class),0);
            alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            alarmMgr.setInexactRepeating(AlarmManager.RTC,
                    SystemClock.elapsedRealtime() + 1000 ,AlarmManager.INTERVAL_HOUR, alarmIntent);

        }
    }
}