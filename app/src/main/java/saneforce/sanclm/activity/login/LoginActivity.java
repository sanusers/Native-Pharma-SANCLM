package saneforce.sanclm.activity.login;

import android.annotation.SuppressLint;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.masterSync.MasterSyncActivity;
import saneforce.sanclm.activity.setting.AsyncInterface;
import saneforce.sanclm.activity.setting.SettingsActivity;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.ActivityLoginBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.DownloaderClass;
import saneforce.sanclm.utility.ImageStorage;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ApiInterface apiInterface;
    PackageManager packageManager;
    PackageInfo packageInfo;
    String fcmToken = "";
    SQLite sqLite;
    String navigateFrom = "";
    String userId = "";
    LoginViewModel loginViewModel = new LoginViewModel();
    private int passwordNotVisible = 1;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        UtilityClass.setLanguage(LoginActivity.this);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();
        FirebaseApp.initializeApp(LoginActivity.this);
        fcmToken = SharedPref.getFcmToken(getApplicationContext());

        uiInitialisation();

        if (fcmToken.isEmpty()) {
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
            @SuppressLint("UseCompatLoadingForDrawables")
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
                userId = binding.userId.getText().toString().trim().replaceAll("\\s","");
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
                        Toast.makeText(LoginActivity.this, "No Internet connectivity!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sqLite.deleteAllTable();
                File apkStorage = new File(LoginActivity.this.getExternalFilesDir(null) + "/Slides/");
                if (apkStorage.exists() && apkStorage.isDirectory()) {
                    File[] files = apkStorage.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            file.delete();
                        }
                    }
                }

                SharedPref.saveLoginState(getApplicationContext(), false);
                SharedPref.saveSettingState(getApplicationContext(), false);
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            }
        });

    }

    public void uiInitialisation(){

        String logoUrl = SharedPref.getLogoUrl(LoginActivity.this);
        String[] splitLogoUrl = logoUrl.split("/");
        getAndSetLogoImage(splitLogoUrl[splitLogoUrl.length - 1]);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            navigateFrom = getIntent().getExtras().getString(Constants.NAVIGATE_FROM);
        }

        if (navigateFrom.equalsIgnoreCase("Setting")){
            binding.userId.setEnabled(true);
        }else{
            binding.userId.setText(SharedPref.getLoginId(LoginActivity.this));
            binding.userId.setEnabled(false);
        }

    }

    public void getAndSetLogoImage (String imageName){
        packageManager = this.getPackageManager();
        String packageName = this.getPackageName();
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String fileDirectory = packageInfo.applicationInfo.dataDir;
        Log.e("test","filepath name : " + fileDirectory + "/" + imageName);
        if(ImageStorage.checkIfImageExists(fileDirectory, imageName )) {
            File file = ImageStorage.getImage(fileDirectory + "/images/", imageName );
            String path = Objects.requireNonNull(file).getAbsolutePath();
            Bitmap b = BitmapFactory.decodeFile(path);
            binding.logoImg.setImageBitmap(b);
            binding.logoImg.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            String url = SharedPref.getCallApiUrl(getApplicationContext());
            if (!url.equals("")){
                new DownloaderClass(url, fileDirectory, imageName, new AsyncInterface() {
                    @Override
                    public void taskCompleted (boolean status) {
                        if(ImageStorage.checkIfImageExists(fileDirectory, imageName )) {
                            File file = ImageStorage.getImage(fileDirectory + "/images/", imageName );
                            String path = Objects.requireNonNull(file).getAbsolutePath();
                            Bitmap b = BitmapFactory.decodeFile(path);
                            binding.logoImg.setImageBitmap(b);
                        }
                    }
                }).execute();
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

            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
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

            Log.e("test","login master obj : " + jsonObject);
            loginViewModel.loginProcess(getApplicationContext(),baseUrl+replacedUrl,jsonObject.toString()).observe(LoginActivity.this, new Observer<JsonObject>() {
                @Override
                public void onChanged (JsonObject jsonObject) {
                    binding.progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject responseObject = new JSONObject(jsonObject.toString());
                        if (responseObject.getBoolean("success")){
                            Log.e("test","login response : " + jsonObject);
                            process(responseObject);
                        }else{
                            if (responseObject.has("msg")){
                                Toast.makeText(LoginActivity.this,responseObject.getString("msg") , Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    public void process(JSONObject jsonObject) {
        try {
            sqLite.saveLoginData(jsonObject.toString());
            openOrCreateDatabase(SQLite.DATA_BASE_NAME, MODE_PRIVATE, null);
            SharedPref.saveLoginId(LoginActivity.this,userId);
            SharedPref.saveLoginState(getApplicationContext(), true);
            SharedPref.saveSfType(LoginActivity.this, jsonObject.getString("sf_type"), jsonObject.getString("SF_Code"));
            SharedPref.saveHq(LoginActivity.this, jsonObject.getString("HQName"), jsonObject.getString("SF_Code"));

            Intent intent = new Intent(LoginActivity.this, MasterSyncActivity.class);
            intent.putExtra(Constants.NAVIGATE_FROM, "Login");
            startActivity(intent);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
    }
}