package saneforce.santrip.activity.login;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

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

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.activity.setting.AsyncInterface;
import saneforce.santrip.activity.setting.SettingsActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityLoginBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.DownloaderClass;
import saneforce.santrip.utility.ImageStorage;


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
    CommonUtilsMethods commonUtilsMethods;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        UtilityClass.setLanguage(LoginActivity.this);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sqLite = new SQLite(getApplicationContext());
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        sqLite.getWritableDatabase();
        FirebaseApp.initializeApp(LoginActivity.this);
        fcmToken = SharedPref.getFcmToken(getApplicationContext());

        uiInitialisation();
        binding.versionNoTxt.setText("Version "+getResources().getString(R.string.app_version));

        if (fcmToken.isEmpty()) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(LoginActivity.this, s -> {
                fcmToken = s;
                Log.d("test", "new FCM token in login : " + fcmToken);
                SharedPref.saveFcmToken(getApplicationContext(), s);
            });
        }

        binding.eyeImage.setOnClickListener(view -> {
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
        });

        binding.loginBtn.setOnClickListener(view -> {
            UtilityClass.hideKeyboard(LoginActivity.this);
            userId = binding.userId.getText().toString().trim().replaceAll("\\s","");
            String password = binding.password.getText().toString().trim().replaceAll("\\s","");

            if (userId.isEmpty()){
                binding.userId.requestFocus();
                commonUtilsMethods.ShowToast(context, context.getString(R.string.enter_username), 100);
            } else if (password.isEmpty()) {
                binding.password.requestFocus();
                commonUtilsMethods.ShowToast(context, context.getString(R.string.enter_password), 100);
            }else{
                if (UtilityClass.isNetworkAvailable(LoginActivity.this)){
                    login(userId,password);
                }else{
                    commonUtilsMethods.ShowToast(context, context.getString(R.string.no_network), 100);
                }
            }
        });

        binding.clearData.setOnClickListener(view -> {
            sqLite.deleteAllTable();
            SharedPref.clearSP(LoginActivity.this);
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
                new DownloaderClass(url, fileDirectory, imageName, status -> {
                    if(ImageStorage.checkIfImageExists(fileDirectory, imageName )) {
                        File file = ImageStorage.getImage(fileDirectory + "/images/", imageName );
                        String path = Objects.requireNonNull(file).getAbsolutePath();
                        Bitmap b = BitmapFactory.decodeFile(path);
                        binding.logoImg.setImageBitmap(b);
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
                                commonUtilsMethods.ShowToast(context, responseObject.getString("msg") , 100);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            if(SharedPref.getAutomassyncFromSP(LoginActivity.this)){
                Intent intent = new Intent(LoginActivity.this, HomeDashBoard.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(LoginActivity.this, MasterSyncActivity.class);
                intent.putExtra(Constants.NAVIGATE_FROM, "Login");
                startActivity(intent);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
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