package saneforce.sanclm.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


import saneforce.sanclm.R;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.setting.SettingsActivity;


public class SplashScreen extends AppCompatActivity {



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run () {
                startActivity(new Intent(SplashScreen.this, SettingsActivity.class));
                finish();
            }
        },2000);
    }
}