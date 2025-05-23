package saneforce.sanzen.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class FirebaseService extends FirebaseMessagingService {
    LocalBroadcastManager broadcastManager;
    NotificationManager notificationManager;
    Random random;
    String imageUrl = "", title = "", body = "", time = "";
    int notificationId = 0;
    public static int badgeCount = 0;
    private NotificationDataDao notificationDataDao;

    @Override
    public void onCreate () {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        random = new Random();
        RoomDB roomDB = RoomDB.getDatabase(this);
        notificationDataDao = roomDB.notificationDataDao();
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
//        System.out.println("message--->"+ remoteMessage.getNotification().getBody());
        if(SharedPref.getSettingState(getApplicationContext())) {
            if(SharedPref.getLoginState(getApplicationContext())) {
                if(remoteMessage.getNotification() != null) {
                    imageUrl = String.valueOf(remoteMessage.getNotification().getImageUrl());
                    title = remoteMessage.getNotification().getTitle();
                    body = remoteMessage.getNotification().getBody();
                    time = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_2);
                    try {
                        notificationDataDao.saveNotification(new NotificationDataTable(title, body, time));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notificationId = random.nextInt(1000);
                    createNotification();
                }
            }
        }
    }

    public void createNotification(){
        Intent intent = new Intent(this, NotificationClickReceiver.class);
        intent.setAction("saneforce.sanzen.NOTIFICATION_CLICK");
//        Intent intent = new Intent(this, HomeDashBoard.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationClass notificationClass = new NotificationClass(this,title, body, imageUrl, time, pendingIntent);
        notificationClass.createNotification();
    }


}
