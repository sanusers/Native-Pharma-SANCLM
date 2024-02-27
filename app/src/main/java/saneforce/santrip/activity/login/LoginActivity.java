package saneforce.santrip.activity.login;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
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
import saneforce.santrip.utility.LocaleHelper;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ApiInterface apiInterface;
    PackageManager packageManager;
    PackageInfo packageInfo;
    String fcmToken = "";
    SQLite sqLite;
    String navigateFrom = "";
    String userId = "", userPwd = "";
    LoginViewModel loginViewModel = new LoginViewModel();
    CommonUtilsMethods commonUtilsMethods;
    ArrayAdapter<String> languageAdapter;
    Resources resources;
    String language;
    private int passwordNotVisible = 1;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        UtilityClass.setLanguage(LoginActivity.this);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sqLite = new SQLite(getApplicationContext());
        commonUtilsMethods = new CommonUtilsMethods(getParent());
        sqLite.getWritableDatabase();
        FirebaseApp.initializeApp(LoginActivity.this);
        fcmToken = SharedPref.getFcmToken(getApplicationContext());

        uiInitialisation();
        binding.versionNoTxt.setText(String.format("%s%s", getString(R.string.version), Constants.APP_VERSION));

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
                if (!navigateFrom.equalsIgnoreCase("Setting") && SharedPref.getLoginId(LoginActivity.this).equalsIgnoreCase(userId) && (SharedPref.getLoginUserPwd(LoginActivity.this).equalsIgnoreCase(userPwd))) {
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
            if (sqLite.isOutBoxDataAvailable()) {
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
            binding.progressBar.setVisibility(View.VISIBLE);
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

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
            Log.v("Login", "--json-" + jsonObject);
            loginViewModel.loginProcess(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()), jsonObject.toString()).observe(LoginActivity.this, new Observer<JsonElement>() {
                @Override
                public void onChanged(JsonElement jsonObject) {
                    binding.progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject responseObject = new JSONObject(jsonObject.toString());
                        if (responseObject.getBoolean("success")) {
                            if (responseObject.getString("Android_Detailing").equals("1")) {
                                Log.v("Login", "--json-" + responseObject);
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
            sqLite.saveLoginData(jsonObject.toString());
            openOrCreateDatabase(SQLite.DATA_BASE_NAME, MODE_PRIVATE, null);
            SharedPref.saveLoginId(LoginActivity.this, userId, userPwd);
            SharedPref.saveLoginState(getApplicationContext(), true);
            SharedPref.saveSfType(LoginActivity.this, jsonObject.getString("sf_type"), jsonObject.getString("SF_Code"));
            SharedPref.saveHq(LoginActivity.this, jsonObject.getString("HQName"), jsonObject.getString("SF_Code"));
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

    public void ShowToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout_root));

        ImageView image = layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.san_clm_logo);
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void ShowToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();
        try {
            assert view != null;
        } catch (Exception ignored) {

        }
        view.getBackground().setColorFilter(context.getColor(R.color.dark_purple), PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(context.getColor(R.color.white));

        toast.show();
    }

}