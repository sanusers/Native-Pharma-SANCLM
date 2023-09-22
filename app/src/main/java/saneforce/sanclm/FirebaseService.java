package saneforce.sanclm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

import saneforce.sanclm.Storage.SharedPref;


public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onCreate () {
        super.onCreate();
    }

    @Override
    public void onNewToken (@NonNull String s) {
        super.onNewToken(s);
        Log.e("fcm_token", "onNewToken method : " + s);
        SharedPref.saveFcmToken(getApplicationContext(), s);
    }
}
