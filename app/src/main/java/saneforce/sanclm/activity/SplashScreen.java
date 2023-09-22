package saneforce.sanclm.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.setting.SettingsActivity;
import saneforce.sanclm.Storage.SQLite;
import saneforce.sanclm.Storage.SharedPref;


public class SplashScreen extends AppCompatActivity {

    SQLite sqLite;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
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
//               startActivity(new Intent(SplashScreen.this, LoginActivity.class));
//               finish();

            }
        },2000);
    }
}