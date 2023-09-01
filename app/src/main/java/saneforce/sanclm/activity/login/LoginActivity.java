package saneforce.sanclm.activity.login;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.common.Constants;
import saneforce.sanclm.common.UtilityClass;
import saneforce.sanclm.databinding.ActivityLoginBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ApiInterface apiInterface;
    String fcmToken = "";
    SQLite sqlite;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sqlite = new SQLite(getApplicationContext());
        FirebaseApp.initializeApp(LoginActivity.this);
        fcmToken = SharedPref.getFcmToken(getApplicationContext());

        if (fcmToken.isEmpty()){
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    fcmToken = s;
                    Log.d("test", "new FCM token in login : " + fcmToken);
                    SharedPref.saveFcmToken(getApplicationContext(), s);
                }
            });

        }

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                UtilityClass.hideKeyboard(LoginActivity.this);
                String userId = binding.userId.getText().toString().trim().replaceAll("\\s","");
                String password = binding.password.getText().toString().trim().replaceAll("\\s","");

                if (userId.isEmpty()){
                    binding.userId.requestFocus();
                    Toast.makeText(LoginActivity.this, "Enter userId", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    binding.password.requestFocus();
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }else{
                    if (UtilityClass.isNetworkAvailable(LoginActivity.this)){
                        login(userId,password);
                    }else{
                        Toast.makeText(LoginActivity.this, "Internet is not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
               // sqlite.deleteAllTable();
                Cursor cursor = sqlite.getLoginData();
                String data = "";
                LoginResponse loginResponse;
                if (cursor.moveToFirst()) {
                    data = cursor.getString(cursor.getColumnIndex("login_data"));

                }
                cursor.close();
                Type type = new TypeToken<LoginResponse>() {
                }.getType();
                loginResponse = new Gson().fromJson(data, type);
                Log.e("test","login data from sqlite : " + new Gson().toJson(loginResponse));

            }
        });

    }


    public void login(String userId,String password)  {

        try {
            binding.progressBar.setVisibility(View.VISIBLE);
            String baseUrl = SharedPref.getBaseUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            Log.e("test","login url : "  + baseUrl + pathUrl);
            Log.e("test","base url from sharePref  : " + SharedPref.getBaseUrl(getApplicationContext()));
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), "http://crm.saneforce.in/iOSServer/db_api.php/");

            String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", userId);
            jsonObject.put("password", password);
            jsonObject.put("versionNo", Constants.APP_VERSION);
            jsonObject.put("mode", Constants.APP_MODE);
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("device_id", deviceId);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("AppDeviceRegId", fcmToken);
            jsonObject.put("location", "0.0 : 0.0");

            Log.e("test","login obj : " + jsonObject);
            Call<JsonObject> call = apiInterface.Login(jsonObject.toString());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse (@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    binding.progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject2 = new JSONObject(response.body().toString());
                            if (jsonObject2.getBoolean("success")){
                                String stringResponse = response.body().toString();
                                LoginResponse loginResponse = new LoginResponse();
                                loginResponse = new Gson().fromJson(stringResponse,LoginResponse.class);

                                Log.e("test","login : " + new Gson().toJson(loginResponse));
                                sqlite.saveLoginData(stringResponse);
                            }else{
                                if (jsonObject2.has("msg")){
                                    Toast.makeText(LoginActivity.this,jsonObject2.getString("msg") , Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }

                @Override
                public void onFailure (@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("test","failed : " + t);
                    binding.progressBar.setVisibility(View.GONE);

                }
            });


        }catch (Exception e){
            e.printStackTrace();

        }



    }
}