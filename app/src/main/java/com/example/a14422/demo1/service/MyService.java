package com.example.a14422.demo1.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.a14422.demo1.activity.MainActivity;
import com.example.a14422.demo1.R;

public class MyService extends Service {

    private DownloadBinder mBinder = new DownloadBinder();

    private String CHANNEL_ONE_ID = "com.primedu.cn";
    private String CHANNEL_ONE_NAME = "Channel One";
    private NotificationChannel notificationChannel = null;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyService", "onCreated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("MyService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MyService", "onDestroy");
    }

    public class DownloadBinder extends Binder {
        public void sayHello() {
            Log.e("DownloadBinder", "Hello World!");
        }

        public void startForeNotification(String title) {
            startForeground(1, getNotification(title));
        }

        public NotificationManager getNotificationManager() {
            return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        public Notification getNotification(String title) {
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(MyService.this, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this);
            builder.setSmallIcon(R.mipmap.hk)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.hk))
                    .setContentIntent(pi)
                    .setContentTitle(title)
                    .setContentText("Hello World");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
            }
            return builder.build();
        }
    }
}
