package saneforce.sanzen.activity.splash;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.PrivacyPolicyActvity.PrivacyPolicyActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.activity.setting.SettingsActivity;
import saneforce.sanzen.databinding.ActivitySplashScreenBinding;
import saneforce.sanzen.storage.SharedPref;


@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

  ActivitySplashScreenBinding binding ;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        new Handler().postDelayed(() -> {
            if (SharedPref.getSettingState(getApplicationContext())){
                if (SharedPref.getLoginState(getApplicationContext())){
                    Intent  intent= new Intent(SplashScreen.this, HomeDashBoard.class);
                    overridePendingTransition (0, 0);
                    startActivity(intent);
                    finish();
                }else{

                if(SharedPref.getPolicy(getApplicationContext())){
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                    }else {
                    startActivity(new Intent(SplashScreen.this, PrivacyPolicyActivity.class));
                    finish();
                }

                }
            }else{
                startActivity(new Intent(SplashScreen.this, SettingsActivity.class));
                finish();
            }

        },3000);
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