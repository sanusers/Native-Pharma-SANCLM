package saneforce.sanzen.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import saneforce.sanzen.application.AppActivityTracker;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class FirebaseService extends FirebaseMessagingService {
    LocalBroadcastManager broadcastManager;
    NotificationManager notificationManager;
    Random random;
    String imageUrl = "", title = "", body = "", time = "", type = "", hqCode = "";
    //    int notificationId = 0;
    long id = 0;
    private NotificationDataDao notificationDataDao;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        random = new Random();
        RoomDB roomDB = RoomDB.getDatabase(this);
        notificationDataDao = roomDB.notificationDataDao();
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("fcm_token", "onNewToken method : " + s);
        SharedPref.saveFcmToken(getApplicationContext(), s);
    }

    @Override
    public void handleIntent(Intent intent) {
        try {
            if (intent.getExtras() != null) {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("FirebaseService");
                for (String key : intent.getExtras().keySet()) {
                    if (intent.getExtras().get(key) != null) {
                        builder.addData(key, intent.getExtras().get(key).toString());
                    }
                }
                onMessageReceived(builder.build());
            } else {
                super.handleIntent(intent);
            }
        } catch (Exception e) {
            super.handleIntent(intent);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        System.out.println("message--->"+ remoteMessage.getNotification().getBody());

        if(SharedPref.getSettingState(getApplicationContext())) {
            if(SharedPref.getLoginState(getApplicationContext())) {
                if(remoteMessage.getNotification() != null) {
                    try {
                        imageUrl = String.valueOf(remoteMessage.getNotification().getImageUrl());
                        title = remoteMessage.getNotification().getTitle();
                        body = remoteMessage.getNotification().getBody();
                        time = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_2);
//                    notificationId = random.nextInt(1000);
                        Log.d("Firebase notification", "onMessageReceived: " + title + " -> " + body + " \n- " + imageUrl);

                        hqCode = SharedPref.getHqCode(this);
                        if (hqCode == null || hqCode.isEmpty()) {
                            hqCode = SharedPref.getSfCode(this);
                        }
                        if (body.contains("$")) {
                            try {
                                id = notificationDataDao.saveNotification(new NotificationDataTable(title, body, time, 1, 1));
                                if (body.contains("-MR")) {
                                    type = body.substring(body.lastIndexOf("$") + 1, body.lastIndexOf("-MR"));
                                    hqCode = body.substring(body.lastIndexOf("-MR") + 1);
                                } else {
                                    type = body.substring(body.lastIndexOf("$") + 1);
                                }
                                body = body.substring(0, body.lastIndexOf("$"));
                                showNotificationDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                notificationDataDao.saveNotification(new NotificationDataTable(title, body, time));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        createNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (!remoteMessage.getData().isEmpty()) {
                    try {
                        title = remoteMessage.getData().get("title");
                        body = remoteMessage.getData().get("message");
                        imageUrl = remoteMessage.getData().get("imageUrl");
                        time = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_2);
                        Log.d("Firebase notification", "onMessageReceived: " + title + " -> " + body + " \n- " + imageUrl);
                        hqCode = SharedPref.getHqCode(this);
                        if (hqCode == null || hqCode.isEmpty()) {
                            hqCode = SharedPref.getSfCode(this);
                        }
                        if (body.contains("$")) {
                            try {
                                Log.i("Notification", "onMessageReceived: onSave -> " + title + " -> " + body);
                                id = notificationDataDao.saveNotification(new NotificationDataTable(title, body, time, 1, 1));
                                Log.i("Notification", "onMessageReceived: postSave -> " + title + " -> " + body + " -> " + id);
                                if (body.contains("-MR")) {
                                    type = body.substring(body.lastIndexOf("$") + 1, body.lastIndexOf("-MR"));
                                    hqCode = body.substring(body.lastIndexOf("-MR") + 1);
                                } else {
                                    type = body.substring(body.lastIndexOf("$") + 1);
                                }
                                body = body.substring(0, body.lastIndexOf("$"));
                                showNotificationDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                notificationDataDao.saveNotification(new NotificationDataTable(title, body, time));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        createNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createNotification() {
        Intent intent = new Intent(this, NotificationClickReceiver.class);
        intent.setAction("saneforce.sanzen.NOTIFICATION_CLICK");
//        Intent intent = new Intent(this, HomeDashBoard.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationClass notificationClass = new NotificationClass(this, title, body, imageUrl, time, pendingIntent);
        notificationClass.createNotification();
    }

    private void showNotificationDialog() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            Activity currentActivity = AppActivityTracker.getInstance().getCurrentActivity();
            if(currentActivity != null) {
                currentActivity.runOnUiThread(() -> {
                    NotificationDialog.showDialog(currentActivity, title, body, type, hqCode, id);
                });
            }
        });

    }

}
