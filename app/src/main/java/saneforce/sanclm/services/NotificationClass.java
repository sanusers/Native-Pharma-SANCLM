package saneforce.sanclm.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import saneforce.sanclm.R;

public class NotificationClass extends ContextWrapper {

    private static final String CHANNEL_ID = "10001";
    private static final String CHANNEL_NAME = "SANCLM Notification";
    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    String imageUrl = "", title = "", body = "";
    PendingIntent pendingIntent;
    int notificationId = 0;
    Uri soundUri;

    public NotificationClass (Context base, String title, String body, String imageUrl, PendingIntent pendingIntent) {
        super(base);
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.pendingIntent = pendingIntent;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationId = new Random().nextInt(10000);
        createNotificationChannel();

    }

    private void createNotificationChannel () {
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("SANCLM Notification Description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.setSound(soundUri, audioAttributes);
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
        }

    }


    public void createNotification () {

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.san_clm_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.san_clm_logo))
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setSound(soundUri);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            getImageFromUrl(imageUrl);
//            Bitmap bitmap = getBitmapFromURL(imageUrl);
//            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
//            bigPictureStyle.setBigContentTitle(title);
//            bigPictureStyle.setSummaryText(Html.fromHtml(body).toString());
//
//            bigPictureStyle.bigPicture(bitmap);
//            notificationBuilder.setStyle(bigPictureStyle);
//            notificationManager.notify(notificationId, notificationBuilder.build());
        } else {
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

    }

    private Bitmap getBitmapFromURL (String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getImageFromUrl (String url) {
        final Bitmap[] bitmap = new Bitmap[1];

        Glide.with(this).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady (@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                bitmap[0] = resource;

                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
//                bigPictureStyle.setBigContentTitle(title);
//                bigPictureStyle.setSummaryText(Html.fromHtml(body).toString());
                bigPictureStyle.bigPicture(bitmap[0]);
                notificationBuilder.setStyle(bigPictureStyle);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }

            @Override
            public void onLoadCleared (@Nullable Drawable placeholder) {

            }
        });

    }


}
