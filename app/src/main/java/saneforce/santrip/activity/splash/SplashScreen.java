package saneforce.santrip.activity.splash;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.login.LoginActivity;
import saneforce.santrip.activity.setting.SettingsActivity;
import saneforce.santrip.databinding.ActivityHomeDashBoardBinding;
import saneforce.santrip.databinding.ActivitySplashScreenBinding;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;


@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

  ActivitySplashScreenBinding binding ;

    SQLite sqLite;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        new Handler().postDelayed(() -> {
            if (SharedPref.getSettingState(getApplicationContext())){
                if (SharedPref.getLoginState(getApplicationContext())){
                    startActivity(new Intent(SplashScreen.this, HomeDashBoard.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }else{
                startActivity(new Intent(SplashScreen.this, SettingsActivity.class));
                finish();
            }

        },2000);
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