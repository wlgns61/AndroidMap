package com.example.androidproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "                                            Success!");
        addLocation(context);
    }

    public void addLocation(Context context) {

        Date datetime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm");
        String date = dateFormat.format(datetime);
        String time = timeFormat.format(datetime);
        double latitude;
        double longitude;

        GPStracker GPS = new GPStracker(context);
        latitude = GPS.latitude;
        longitude = GPS.longitude;
        Log.d("GPS","latitude: "+ latitude + "   longitude: " +longitude + "   date:" + date + "   time: " + time);

        ContentValues addValues = new ContentValues();//addValues.put(MyContentProvider._ID, );
        addValues.put(MyContentProvider.DATE, date);
        addValues.put(MyContentProvider.TIME, time);
        addValues.put(MyContentProvider.LATITUDE, latitude);
        addValues.put(MyContentProvider.LONGITUDE, longitude);
        context.getContentResolver().insert(MyContentProvider.CONTENT_URI, addValues);
        String[] projection = {MyContentProvider._ID, MyContentProvider.LATITUDE, MyContentProvider.LONGITUDE,
        MyContentProvider.DATE};
        Cursor c = context.getContentResolver().query(MyContentProvider.CONTENT_URI, projection,
                null, null, null);
        if (c != null) {
            if (c.moveToLast()) {
                Double lati, longi;
                lati = c.getDouble(1);
                longi = c.getDouble(2);
                String dd = c.getString(3);

                Log.d("GPS", "Writed data :" + lati + " " + longi + " date: " + dd);
                c.close();
            }
        }
        createNotification(context,date,time);
    }

    private void createNotification(Context context, String date, String time) {

        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notifManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), channelId);

        Intent notificationIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent
                = PendingIntent.getActivity(context.getApplicationContext()
                , requestID
                , notificationIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle("위치 정보 저장 완료") // required
                .setContentText(date +"  "+ time)  // required
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                .setSound(RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentIntent(pendingIntent);

        notifManager.notify(0, builder.build());
    }
}


