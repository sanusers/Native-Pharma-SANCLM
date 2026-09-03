package saneforce.sanzen.activity.splash;

import static saneforce.sanzen.storage.SharedPref.SP_NAME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import saneforce.sanzen.BuildConfig;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.PrivacyPolicyActvity.PrivacyPolicyActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.activity.setting.SettingsActivity;
import saneforce.sanzen.commonClasses.ContinuousLogCollector;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.databinding.ActivitySplashScreenBinding;
import saneforce.sanzen.storage.SharedPref;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    GPSTrack gpsTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        binding.splashImg.setGifResource(R.drawable.animation_slide);
        setContentView(binding.getRoot());
        gpsTrack = new GPSTrack(this);   // Donot Remove it    Need To  Get Location
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

//        SharedPreferences sharedPreferences;
//        SharedPreferences.Editor editor;
//        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putString(SharedPref.DCR_SEQUENTIAL, "0");
//        editor.apply();
        // Request storage permission if needed (for older Android versions)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // Permission already granted, start logging
                ContinuousLogCollector.startLogging(getApplicationContext());
            }
        } else {
            // On Android 11+, start logging directly to app-specific storage
            ContinuousLogCollector.startLogging(getApplicationContext());
        }

        new Handler().postDelayed(() -> {
            if (SharedPref.getSettingState(getApplicationContext())) {
                if (SharedPref.getLoginState(getApplicationContext())) {
                    int savedVersion = SharedPref.getLastKnownVersion(SplashScreen.this);
                    int currentVersion = BuildConfig.VERSION_CODE;
                    Log.d("Versions", "onCreate: " + savedVersion + " -> " + currentVersion);
                    if (savedVersion == 0 || ((savedVersion != currentVersion) && savedVersion < 79)) {
                        SharedPref.setLogoutReason(SplashScreen.this, "App has been updated, Kindly Re-Login when online.");
                        SharedPref.saveLoginState(SplashScreen.this, false);
                        SharedPref.saveLoginPwd(SplashScreen.this, "");
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    Intent intent = new Intent(SplashScreen.this, HomeDashBoard.class);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    finish();
                } else {
                    if (SharedPref.getPolicy(getApplicationContext())) {
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashScreen.this, PrivacyPolicyActivity.class));
                        finish();
                    }
                }
            } else {
                startActivity(new Intent(SplashScreen.this, SettingsActivity.class));
                finish();
            }
        }, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start logging
                ContinuousLogCollector.startLogging(getApplicationContext());
            } else {
                // Permission denied, you might want to inform the user
                android.util.Log.w("SplashScreen", "Write external storage permission denied.");
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.rlHead.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}