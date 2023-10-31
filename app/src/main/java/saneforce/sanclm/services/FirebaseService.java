package saneforce.sanclm.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.storage.SharedPref;


public class FirebaseService extends FirebaseMessagingService {

    LocalBroadcastManager broadcastManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    Random random;
    String imageUrl = "", title = "", body = "";
    int notificationId = 0;


    @Override
    public void onCreate () {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        random = new Random();
    }

    @Override
    public void onNewToken (@NonNull String s) {
        super.onNewToken(s);
        Log.e("fcm_token", "onNewToken method : " + s);
        SharedPref.saveFcmToken(getApplicationContext(), s);
    }

    @Override
    public void onMessageReceived (@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("test","remote msg notification : " + remoteMessage.getNotification().getBody());
        Log.e("test","remote msg data :" + remoteMessage.getData().get("title"));
        imageUrl = String.valueOf(remoteMessage.getNotification().getImageUrl());
        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
        notificationId = random.nextInt(1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationForGraterOreo(remoteMessage);
        } else {
            createNotificationForLesserOreo(remoteMessage);
        }

    }

    public boolean isAppInForeground () {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
    }

    public void createNotificationForGraterOreo(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, HomeDashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, imageUrl, pendingIntent);
        int i = 0;
        oreoNotification.getManager().notify(i, builder.build());
    }

    public void createNotificationForLesserOreo(RemoteMessage remoteMessage) {
        Intent resultIntent = new Intent(getApplicationContext(), HomeDashBoard.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 , resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.san_clm_logo)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(10)
                    .setContentTitle(title)
                    .setContentText(body)
//                    .setStyle(bigPictureStyle)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(1, notificationBuilder.build());
    }
}
