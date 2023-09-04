package saneforce.sanclm.activity.mastersync;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.databinding.ActivityMasterSyncBinding;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;


public class MasterSyncActivity extends AppCompatActivity {

    ActivityMasterSyncBinding binding;
    SQLite sqLite;
    LoginResponse loginResponse;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMasterSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        Cursor cursor = sqLite.getLoginData();
        loginResponse = new LoginResponse();

        String loginData = "";
        if (cursor.moveToNext()){
            loginData = cursor.getString(0);
        }
        cursor.close();
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        loginResponse = new Gson().fromJson(loginData, type);
        Log.e("test","login data from sqlite : " + new Gson().toJson(loginResponse));
        binding.hqName.setText(loginResponse.getHQName());


    }



}