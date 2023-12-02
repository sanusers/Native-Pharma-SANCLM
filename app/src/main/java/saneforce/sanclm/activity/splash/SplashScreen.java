package saneforce.sanclm.activity.splash;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.setting.SettingsActivity;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class SplashScreen extends AppCompatActivity {

    SQLite sqLite;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run () {
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

            }
        },2000);
    }
}