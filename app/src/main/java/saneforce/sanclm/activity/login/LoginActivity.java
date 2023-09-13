package saneforce.sanclm.activity.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.mastersync.MasterSyncActivity;
import saneforce.sanclm.activity.setting.SettingsActivity;
import saneforce.sanclm.utility.DownloaderClass;
import saneforce.sanclm.utility.ImageStorage;
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
    PackageManager packageManager;
    PackageInfo packageInfo;
    String fcmToken = "";
    SQLite sqlite;
    private int passwordNotVisible=1;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sqlite = new SQLite(getApplicationContext());
        sqlite.getWritableDatabase();
        FirebaseApp.initializeApp(LoginActivity.this);
        fcmToken = SharedPref.getFcmToken(getApplicationContext());

        getImageFromLocal("logo");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

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

        binding.eyeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (passwordNotVisible == 1) {
                    binding.password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.eyeImage.setImageDrawable(getResources().getDrawable(R.drawable.eye_hide));
                    passwordNotVisible = 0;
                } else {
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.eyeImage.setImageDrawable(getResources().getDrawable(R.drawable.eye_visible));
                    passwordNotVisible = 1;
                }

                binding.password.setSelection(binding.password.length());
            }
        });

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
                sqlite.deleteAllTable();
                SharedPref.saveLoginState(getApplicationContext(),false);
                SharedPref.saveSettingState(getApplicationContext(),false);
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
//                Cursor cursor = sqlite.getLoginData();
//                String data = "";
//                LoginResponse loginResponse;
//                if (cursor.moveToFirst()) {
//                    data = cursor.getString(0);
//                }
//                cursor.close();
//                Type type = new TypeToken<LoginResponse>() {
//                }.getType();
//                loginResponse = new Gson().fromJson(data, type);
//                Log.e("test","login data from sqlite : " + new Gson().toJson(loginResponse));

            }
        });

    }

    public void getImageFromLocal(String imageName){
        Log.e("test","Login getImageFromLocal");
        packageManager = this.getPackageManager();
        String packageName = this.getPackageName();
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String fileDirectory = packageInfo.applicationInfo.dataDir;
        Log.e("test","filepath name : " + fileDirectory + "/" + imageName);
        if(ImageStorage.checkIfImageExists(fileDirectory, imageName + ".png")) {
            Log.e("test","image exists");
            File file = ImageStorage.getImage(fileDirectory + "/images/", imageName + ".png");
            String path = file.getAbsolutePath();
            Bitmap b = BitmapFactory.decodeFile(path);
          binding.logoImg.setImageBitmap(b);
        } else {
            Log.e("test","image not exists in login");
            String baseWebUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String logoUrl = SharedPref.getLogoUrl(getApplicationContext());
            if (!baseWebUrl.equals("") && !logoUrl.equals("")){
                DownloaderClass downloaderClass = (DownloaderClass) new DownloaderClass(baseWebUrl+logoUrl, fileDirectory, imageName).execute();
            }

        }
    }


    public void login(String userId,String password)  {

        try {
            binding.progressBar.setVisibility(View.VISIBLE);
            String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*","/");
            Log.e("test","login url : "  + baseUrl + replacedUrl);
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl+replacedUrl);

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

            Log.e("test","master obj : " + jsonObject);
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

                                Log.e("test","login response : " + new Gson().toJson(loginResponse));
                                sqlite.saveLoginData(stringResponse);
                                openOrCreateDatabase("san_clm.db", MODE_PRIVATE, null);
                                Intent intent = new Intent(LoginActivity.this,MasterSyncActivity.class);
                                intent.putExtra("Origin","Login");
                                startActivity(intent);
                                SharedPref.saveLoginState(getApplicationContext(),true);
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