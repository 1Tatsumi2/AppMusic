package com.example.musicapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class ApplicationClass extends Application {
    public static  final String CHANNEL_ID_1="CHANNEL_1";
    public static  final String CHANNEL_ID_2="CHANNEL_2";
    public static  final String ACTION_NEXTS="NEXT";
    public static  final String ACTION_PREV="PREVIOUS";
    public static  final String ACTION_PLAY="PLAY";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel1=new NotificationChannel(CHANNEL_ID_1,"Channel(1)", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel1.setDescription("Channel 1 Description");
        NotificationChannel notificationChannel2=new NotificationChannel(CHANNEL_ID_2,"Channel(2)",NotificationManager.IMPORTANCE_HIGH);
        notificationChannel2.setDescription("Channel 2 Description");
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel1);
        notificationManager.createNotificationChannel(notificationChannel2);
    }
}