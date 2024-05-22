package saneforce.sanzen.activity.login;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.activity.setting.SettingsActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityLoginBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.sanzen.roomdatabase.CallsUtil;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataTable;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.DownloaderClass;
import saneforce.sanzen.utility.ImageStorage;
import saneforce.sanzen.utility.LocaleHelper;
import saneforce.sanzen.utility.TimeUtils;


public class LoginActivity extends AppCompatActivity {

   public static ActivityLoginBinding binding;
    ApiInterface apiInterface;
    PackageManager packageManager;
    PackageInfo packageInfo;
    String fcmToken = "";
    String navigateFrom = "";
    String userId = "", userPwd = "";
    LoginViewModel loginViewModel = new LoginViewModel();
    CommonUtilsMethods commonUtilsMethods;
    ArrayAdapter<String> languageAdapter;
    Resources resources;
    String language;
    private int passwordNotVisible = 1;
    RoomDB roomDB;
    MasterDataDao masterDataDao;
    CallTableDao callTableDao;
    private LoginDataDao loginDataDao;
    private CallsUtil callsUtil;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        UtilityClass.setLanguage(LoginActivity.this);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        commonUtilsMethods = new CommonUtilsMethods(getParent());
        FirebaseApp.initializeApp(LoginActivity.this);
        fcmToken = SharedPref.getFcmToken(getApplicationContext());

        callsUtil = new CallsUtil(this);

        roomDB=RoomDB.getDatabase(getApplicationContext());

        masterDataDao=roomDB.masterDataDao();
        callTableDao=roomDB.callTableDao();
        loginDataDao = roomDB.loginDataDao();

        uiInitialisation();
        binding.versionNoTxt.setText(String.format("%s%s", getString(R.string.version), getResources().getString(R.string.app_version)));

        if (fcmToken.isEmpty()) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(LoginActivity.this, s -> {
                fcmToken = s;
                SharedPref.saveFcmToken(getApplicationContext(), s);
            });
        }

        binding.selectLanguage.setOnClickListener(v -> {
            if (binding.languageListView.getVisibility() == View.VISIBLE) {
                binding.dropDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up_arrow_light_grey));
                binding.languageListView.setVisibility(View.GONE);
            } else {
                binding.dropDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.down_arrow_light_grey));
                binding.languageListView.setVisibility(View.VISIBLE);
            }
        });

        binding.eyeImage.setOnClickListener(view -> {
            if (passwordNotVisible == 1) {
                binding.password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.eyeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.eye_hide));
                passwordNotVisible = 0;
            } else {
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.eyeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.eye_visible));
                passwordNotVisible = 1;
            }
            binding.password.setSelection(binding.password.length());
        });

        binding.loginBtn.setOnClickListener(view -> {
            UtilityClass.hideKeyboard(LoginActivity.this);
            userId = binding.userId.getText().toString().trim().replaceAll("\\s", "");
            userPwd = binding.password.getText().toString().trim().replaceAll("\\s", "");

            if (!UtilityClass.isNetworkAvailable(getApplicationContext())) {

                if (userId.isEmpty()) {
                    binding.userId.requestFocus();
                    commonUtilsMethods.showToastMessage(LoginActivity.this, context.getString(R.string.enter_user_id));
                } else if (userPwd.isEmpty()) {
                    binding.password.requestFocus();
                    commonUtilsMethods.showToastMessage(LoginActivity.this, context.getString(R.string.enter_password));
                } else if (!navigateFrom.equalsIgnoreCase("Setting") && SharedPref.getLoginId(LoginActivity.this).equalsIgnoreCase(userId) && (SharedPref.getLoginUserPwd(LoginActivity.this).equalsIgnoreCase(userPwd))) {
                    SharedPref.setSetUpClickedTab(getApplicationContext(), "0");
                    commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.login_successfully));
                    startActivity(new Intent(LoginActivity.this, HomeDashBoard.class));
                } else {
                    commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.mismatch));
                }
            } else {
                if (userId.isEmpty()) {
                    binding.userId.requestFocus();
               commonUtilsMethods.showToastMessage(LoginActivity.this, context.getString(R.string.enter_user_id));
                } else if (userPwd.isEmpty()) {
                    binding.password.requestFocus();
               commonUtilsMethods.showToastMessage(LoginActivity.this, context.getString(R.string.enter_password));
                } else {
                    if (UtilityClass.isNetworkAvailable(LoginActivity.this)) {
                        login(userId, userPwd);
                    } else {
                   commonUtilsMethods.showToastMessage(LoginActivity.this, context.getString(R.string.no_network));
                    }
                }
            }
        });

        binding.clearData.setOnClickListener(view -> {
            if (!callsUtil.isOutBoxDataAvailable()) {
                new AlertDialog.Builder(this).setTitle("Warning!").setIcon(getDrawable(R.drawable.icon_sync_failed)).setMessage("Outbox Data Calls will be deleted, Do you want to Continue?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, (dialog, whichButton) -> DeleteAllFiles()).setNegativeButton(android.R.string.no, null).show();
            } else {
                DeleteAllFiles();
            }
        });
    }

    private void SelectedLanguage(String selectedLanguage) {
        SharedPref.saveSelectedLanguage(LoginActivity.this, selectedLanguage);
        Locale myLocale = new Locale(selectedLanguage);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        context = LocaleHelper.setLocale(getApplicationContext(), selectedLanguage);
        resources = getApplicationContext().getResources();
        binding.tagUserId.setText(getString(R.string.user_id));
        binding.userId.setHint(getString(R.string.enter_user_id));
        binding.tagPassword.setText(getString(R.string.password));
        binding.password.setHint(getString(R.string.enterpassword));
        binding.loginBtn.setText(getString(R.string.login));
        binding.tvclearData.setText(getString(R.string.clear_data));
    }

    private void DeleteAllFiles() {
        roomDB.loginDataDao().deleteAllData();
        roomDB.masterDataDao().deleteAllMasterData();
        roomDB.callTableDao().deleteAllData();
        roomDB.callOfflineDataDao().deleteAllData();
        roomDB.callOfflineECDataDao().deleteAllData();
        roomDB.callOfflineWorkTypeDataDao().deleteAllData();
        roomDB.offlineCheckInOutDataDao().deleteAllData();
        roomDB.dcrDocDataDao().deleteAllData();
        roomDB.presentationDataDao().deleteAllData();
        roomDB.tourPlanOfflineDataDao().deleteAllData();
        roomDB.tourPlanOnlineDataDao().deleteAllData();
        roomDB.callOfflineWorkTypeDataDao().deleteAllData();
        roomDB.slidesDao().deleteAllData();

        SharedPref.clearSP(LoginActivity.this);
        SharedPref.saveLoginState(getApplicationContext(), false);
        SharedPref.saveSettingState(getApplicationContext(), false);
        startActivity(new Intent(LoginActivity.this, SettingsActivity.class));

        if(iscleared()){
            Toast.makeText(LoginActivity.this,"Data Clear Sucessfully",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
        }
    }

    public void uiInitialisation() {
        String logoUrl = SharedPref.getLogoUrl(LoginActivity.this);
        String[] splitLogoUrl = logoUrl.split("/");
        getAndSetLogoImage(splitLogoUrl[splitLogoUrl.length - 1]);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navigateFrom = getIntent().getExtras().getString(Constants.NAVIGATE_FROM);
        }

        assert navigateFrom != null;
        if (navigateFrom.equalsIgnoreCase("Setting")) {
            //  binding.userId.setEnabled(true);
        } else {
            binding.userId.setText(SharedPref.getLoginId(LoginActivity.this));
            binding.password.setText(SharedPref.getLoginUserPwd(LoginActivity.this));
            //  binding.userId.setEnabled(false);
        }

        SetUpLanguage();
    }

    private void SetUpLanguage() {
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        language = SharedPref.getSelectedLanguage(this);

        String[] languages = {"ENGLISH", "BURMESE", "FRENCH", "MANDARIN", "THAILAND", "PORTUGUESE", "SPANISH", "VIETNAMESE"};
        languageAdapter = new ArrayAdapter<>(LoginActivity.this, R.layout.listview_items, languages);
        binding.languageListView.setAdapter(languageAdapter);
        languageAdapter.notifyDataSetChanged();

        if (!language.equalsIgnoreCase("")) {
            String languageData = SharedPref.getSelectedLanguage(getApplicationContext());
            SelectedLanguage(languageData);
            switch (languageData) {
                case "pt":
                    binding.selectLanguage.setText("PORTUGUESE");
                    break;
                case "fr":
                    binding.selectLanguage.setText("FRENCH");
                    break;
                case "my":
                    binding.selectLanguage.setText("BURMESE");
                    break;
                case "vi":
                    binding.selectLanguage.setText("VIETNAMESE");
                    break;
                case "zh":
                    binding.selectLanguage.setText("MANDARIN");
                    break;
                case "es":
                    binding.selectLanguage.setText("SPANISH");
                    break;
                case "th":
                    binding.selectLanguage.setText("THAILAND");
                    break;
                default:
                    binding.selectLanguage.setText("ENGLISH");
                    break;
            }
        } else {
            SelectedLanguage("en");
            binding.selectLanguage.setText("ENGLISH");
        }


        binding.languageListView.setOnItemClickListener((parent, view, position, id) -> {
            TextView textView = (TextView) view;
            binding.selectLanguage.setText(textView.getText().toString());
            //  textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.pink));
            String selectedLanguage = "";
            switch (textView.getText().toString().toUpperCase()) {
                case "ENGLISH": {
                    selectedLanguage = "en";
                    break;
                }
                case "BURMESE": {
                    selectedLanguage = "my";
                    break;
                }
                case "FRENCH": {
                    selectedLanguage = "fr";
                    break;
                }
                case "MANDARIN": {
                    selectedLanguage = "zh";
                    break;
                }
                case "PORTUGUESE": {
                    selectedLanguage = "pt";
                    break;
                }
                case "SPANISH": {
                    selectedLanguage = "es";
                    break;
                }
                case "THAILAND": {
                    selectedLanguage = "th";
                    break;
                }
                case "VIETNAMESE": {
                    selectedLanguage = "vi";
                    break;
                }
            }
            SelectedLanguage(selectedLanguage);
            binding.languageListView.setVisibility(View.GONE);
            binding.dropDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up_arrow_light_grey));
        });
    }

    public void getAndSetLogoImage(String imageName) {
        packageManager = this.getPackageManager();
        String packageName = this.getPackageName();
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String fileDirectory = packageInfo.applicationInfo.dataDir;
        Log.e("test", "filepath name : " + fileDirectory + "/" + imageName);
        if (ImageStorage.checkIfImageExists(fileDirectory, imageName)) {
            File file = ImageStorage.getImage(fileDirectory + "/images/", imageName);
            String path = Objects.requireNonNull(file).getAbsolutePath();
            Bitmap b = BitmapFactory.decodeFile(path);
            binding.logoImg.setImageBitmap(b);
            binding.logoImg.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            String url = SharedPref.getCallApiUrl(getApplicationContext());
            if (!url.equals("")) {
                new DownloaderClass(url, fileDirectory, imageName, status -> {
                    if (ImageStorage.checkIfImageExists(fileDirectory, imageName)) {
                        File file = ImageStorage.getImage(fileDirectory + "/images/", imageName);
                        String path = Objects.requireNonNull(file).getAbsolutePath();
                        Bitmap b = BitmapFactory.decodeFile(path);
                        binding.logoImg.setImageBitmap(b);
                    }
                }).execute();
            }
        }
    }

    public void login(String userId, String password) {
        try {
            binding.loginBtn.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", userId);
            jsonObject.put("password", password);
            jsonObject.put("versionNo",  getResources().getString(R.string.app_version));
            jsonObject.put("mode", Constants.APP_MODE);
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("Tt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_2));
            jsonObject.put("device_id", deviceId);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("AppDeviceRegId", fcmToken);
            jsonObject.put("location", "0.0 : 0.0");
            Log.v("Login", "--json-" + jsonObject);
            loginViewModel.loginProcess(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()), jsonObject.toString()).observe(LoginActivity.this, new Observer<JsonElement>() {
                @Override
                public void onChanged(JsonElement jsonObject) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.loginBtn.setEnabled(true);
                    try {
                        JSONObject responseObject = new JSONObject(jsonObject.toString());
                        if (responseObject.getBoolean("success")) {
                            if (responseObject.getString("Android_Detailing").equals("1")) {
                                Log.v("Android_Detailing", "--json-" + responseObject);
                                commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.login_successfully));
                                process(responseObject);
                            } else {
                                commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.access_denied));
                            }
                        } else {
                            if (responseObject.has("msg")) {
                                commonUtilsMethods.showToastMessage(LoginActivity.this, responseObject.getString("msg"));
                            }
                        }
                    } catch (JSONException e) {
                        Log.v("Login", "--error-" + e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void process(JSONObject jsonObject) {
        try {
            loginDataDao.saveLoginData(new LoginDataTable(jsonObject.toString()));
            SharedPref.InsertLogInData(LoginActivity.this,jsonObject);
            SharedPref.saveLoginId(LoginActivity.this, userId, userPwd);
            SharedPref.saveLoginState(getApplicationContext(), true);
            SharedPref.saveSfType(LoginActivity.this, jsonObject.getString("sf_type"), jsonObject.getString("SF_Code"));
            SharedPref.saveHq(LoginActivity.this, jsonObject.getString("HQName"), jsonObject.getString("SF_Code"));
            SharedPref.saveHqMain(LoginActivity.this, jsonObject.getString("HQName"));

            if (SharedPref.getAutomassyncFromSP(LoginActivity.this)) {
                SharedPref.setSetUpClickedTab(getApplicationContext(), "0");
                Intent intent = new Intent(LoginActivity.this, HomeDashBoard.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, MasterSyncActivity.class);
                intent.putExtra(Constants.NAVIGATE_FROM, "Login");
                startActivity(intent);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.rlHead.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }



    boolean iscleared(){
        File slidesFolder;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            slidesFolder = new File(LoginActivity.this.getExternalFilesDir(null), "Slides");
        } else {
            return false;
        }


        if (slidesFolder.exists()) {
            deleteRecursive(slidesFolder);
        }
        slidesFolder.delete();
        return true;

    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}