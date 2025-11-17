package saneforce.sanzen.activity.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.notification.NotificationViewModel;
import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.activity.setting.SettingsActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityLoginBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataTable;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;
import saneforce.sanzen.roomdatabase.OutboxUtil;
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
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private CallTableDao callTableDao;
    private LoginDataDao loginDataDao;
    private OutboxUtil outboxUtil;
    private NotificationDataDao notificationDataDao;
    String appAccess = "";
    private CountDownTimer countDownTimer;
    private boolean isTimerStarted = false;
    private long remainingTime = 0;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        UtilityClass.setLanguage(LoginActivity.this);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        FirebaseApp.initializeApp(LoginActivity.this);
        fcmToken = SharedPref.getFcmToken(getApplicationContext());

        outboxUtil = new OutboxUtil(this);

        roomDB = RoomDB.getDatabase(getApplicationContext());

        masterDataDao = roomDB.masterDataDao();
        callTableDao = roomDB.callTableDao();
        loginDataDao = roomDB.loginDataDao();
        notificationDataDao = roomDB.notificationDataDao();

        uiInitialisation();
        binding.versionNoTxt.setText(String.format("%s%s", getString(R.string.version), getResources().getString(R.string.app_version)));

        int loginFailedCount = SharedPref.getLoginFailedCount(LoginActivity.this);
        if (loginFailedCount == 5) {
            isTimerStarted = true;
            binding.password.setEnabled(false);
            binding.userId.setEnabled(false);
            binding.loginBtn.setEnabled(false);
            binding.clearData.setEnabled(false);
            binding.rlRejReason.setVisibility(View.VISIBLE);
            binding.rejectedReason.setText("Please try again after 5 minutes!");
            remainingTime = TimeUtils.timeDifferenceInMillis(SharedPref.getLoginFailedDateTime(LoginActivity.this), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
            startTimer();
        }

        notificationDataDao.getNotificationBySyncStatus(5).forEach(data -> {
            binding.logoutReasonLayout.setVisibility(View.VISIBLE);
            String reason = data.getMessage().substring(0, data.getMessage().lastIndexOf("$"));
            binding.logoutReasonTxt.setText(reason);
        });

        if (fcmToken.isEmpty()) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(LoginActivity.this, s -> {
                fcmToken = s;
                Log.i("FCM", "onCreate: " + s);
                SharedPref.saveFcmToken(getApplicationContext(), s);
            });
        }

        binding.selectLanguage.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (binding.languageListView.getVisibility() == View.VISIBLE) {
                    binding.dropDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up_arrow_light_grey));
                    binding.languageListView.setVisibility(View.GONE);
                } else {
                    binding.dropDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.down_arrow_light_grey));
                    binding.languageListView.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.eyeImage.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
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
            }
        });

        binding.loginBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                UtilityClass.hideKeyboard(LoginActivity.this);
                //      CommonAlertBox.CheckLocationStatus(LoginActivity.this);
                userId = binding.userId.getText().toString().trim().replaceAll("\\s", "");
                userPwd = binding.password.getText().toString().trim().replaceAll("\\s", "");

                if (!UtilityClass.isNetworkAvailable(getApplicationContext())) {
                    if (userId.isEmpty()) {
                        binding.userId.requestFocus();
                        CommonUtilsMethods.showToastMessage(LoginActivity.this, LoginActivity.this.getString(R.string.enter_user_id));
                    } else if (userPwd.isEmpty()) {
                        binding.password.requestFocus();
                        CommonUtilsMethods.showToastMessage(LoginActivity.this, LoginActivity.this.getString(R.string.enter_password));
                    } else if (SharedPref.getLoginId(LoginActivity.this).equalsIgnoreCase("")) {
                        CommonUtilsMethods.showToastMessage(LoginActivity.this, LoginActivity.this.getString(R.string.no_network));
                    } else if (!navigateFrom.equalsIgnoreCase("Setting") && SharedPref.getLoginId(LoginActivity.this).equalsIgnoreCase(userId) && (SharedPref.getLoginUserPwd(LoginActivity.this).equalsIgnoreCase(userPwd))) {
                        SharedPref.setSetUpClickedTab(getApplicationContext(), 0);
                        notificationDataDao.getNotificationBySyncStatus(5).forEach(data -> {
                            notificationDataDao.changeNotificationSyncStatus(data.getId(), 0);
                            notificationDataDao.changeNotificationReadStatus(data.getId(), 1);
                        });
                        startActivity(new Intent(LoginActivity.this, HomeDashBoard.class));
//                    commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.login_successfully));
                        Toast.makeText(LoginActivity.this, getString(R.string.login_successfully), Toast.LENGTH_LONG).show();
                    } else {
                        loginFailed();
                        commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.mismatch));
                    }
                } else {
                    if (userId.isEmpty()) {
                        binding.userId.requestFocus();
                        commonUtilsMethods.showToastMessage(LoginActivity.this, LoginActivity.this.getString(R.string.enter_user_id));
                    } else if (userPwd.isEmpty()) {
                        binding.password.requestFocus();
                        commonUtilsMethods.showToastMessage(LoginActivity.this, LoginActivity.this.getString(R.string.enter_password));
                    } else {
                        if (UtilityClass.isNetworkAvailable(LoginActivity.this)) {
                            login(userId, userPwd);
                        } else {
                            commonUtilsMethods.showToastMessage(LoginActivity.this, LoginActivity.this.getString(R.string.no_network));
                        }
                    }
                }
            }
        });

        binding.clearData.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (outboxUtil.isOutBoxDataAvailable()) {
                    new AlertDialog.Builder(LoginActivity.this).setTitle("Warning!").setIcon(getDrawable(R.drawable.icon_sync_failed)).setMessage("Outbox Data Calls will be deleted, Do you want to Continue?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, (dialog, whichButton) -> DeleteAllFiles()).setNegativeButton(android.R.string.no, null).show();
                } else {
                    DeleteAllFiles();
                }
            }
        });

        if ((SharedPref.getSrtNd(this).equalsIgnoreCase("0")
                || SharedPref.getCustSrtNd(LoginActivity.this).equalsIgnoreCase("0")
                || SharedPref.getChmSrtNd(LoginActivity.this).equalsIgnoreCase("0")
                || SharedPref.getUnlistSrtNd(LoginActivity.this).equalsIgnoreCase("0"))
                && SharedPref.getCheckInSkipDate(this).equalsIgnoreCase(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_5, LocalDate.now().toString()))) {
            Dialog loginConfirmation = new Dialog(this);
            loginConfirmation.setContentView(R.layout.popup_remarks);
            Objects.requireNonNull(loginConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loginConfirmation.setCancelable(false);
            ImageView iv_close = loginConfirmation.findViewById(R.id.img_close);
            EditText ed_remarks = loginConfirmation.findViewById(R.id.ed_remark);
            TextView heading = loginConfirmation.findViewById(R.id.tv_head);
            TextView content = loginConfirmation.findViewById(R.id.content);
            Button btn_clear = loginConfirmation.findViewById(R.id.btn_clear);
            Button btn_save = loginConfirmation.findViewById(R.id.btn_save);
            heading.setText(this.getString(R.string.alert));
            btn_save.setText(this.getString(R.string.ok));
            btn_clear.setText(this.getString(R.string.no));
            content.setText("Check-In/Out is enabled and Date has been changed. So logged out. Kindly start over after login");
            content.setVisibility(View.VISIBLE);
            btn_clear.setVisibility(View.INVISIBLE);
            ed_remarks.setVisibility(View.INVISIBLE);
            iv_close.setVisibility(View.GONE);
            btn_save.setOnClickListener(view -> {
                SharedPref.setDayCheckInData(LoginActivity.this, "");
                SharedPref.setCheckInSkipDate(LoginActivity.this, "");
                loginConfirmation.dismiss();
            });
            loginConfirmation.show();
        }
    }

    private void loginFailed() {
        int loginFailedCount = SharedPref.getLoginFailedCount(LoginActivity.this);
        loginFailedCount++;
        SharedPref.setLoginFailedCount(LoginActivity.this, loginFailedCount, TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_1));
        if (loginFailedCount == 5) {
            binding.password.setEnabled(false);
            binding.userId.setEnabled(false);
            binding.loginBtn.setEnabled(false);
            binding.clearData.setEnabled(false);
            binding.rlRejReason.setVisibility(View.VISIBLE);
            binding.rejectedReason.setText("Please try again after 5 minutes!");
            isTimerStarted = true;
            String time = "00:05:00", loginTimer = SharedPref.getLoginTimer(LoginActivity.this);
            if (!loginTimer.isEmpty()) {
                try {
                    time = String.format("00:%02d:00", Integer.parseInt(loginTimer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            remainingTime = TimeUtils.getMilliSeconds(TimeUtils.FORMAT_32, time);
            startTimer();
        }
    }

    private void startTimer() {
        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            countDownTimer = new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String timeLeftFormatted = TimeUtils.getMillisToFormattedTime(millisUntilFinished, TimeUtils.FORMAT_40);
                    binding.rejectedReason.setText("Please try again after " + timeLeftFormatted + " minutes!");
                    remainingTime = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    binding.password.setEnabled(true);
                    if (SharedPref.getLoginId(LoginActivity.this).isEmpty()) {
                        binding.userId.setEnabled(true);
                    }
                    binding.loginBtn.setEnabled(true);
                    binding.clearData.setEnabled(true);
                    binding.rlRejReason.setVisibility(View.GONE);
                    SharedPref.setLoginFailedCount(LoginActivity.this, 0, TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_1));
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTimerStarted) {
            try {
                SharedPref.setLoginRemainingTime(LoginActivity.this, remainingTime);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void SelectedLanguage(String selectedLanguage) {
        SharedPref.saveSelectedLanguage(LoginActivity.this, selectedLanguage);
        Locale myLocale = new Locale(selectedLanguage);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Context context = LocaleHelper.setLocale(getApplicationContext(), selectedLanguage);
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
        roomDB.offlineDaySubmitDao().deleteAllData();
        roomDB.stpOfflineDataDao().deleteAllData();
        roomDB.quizOfflineDataDao().deleteAllData();
        roomDB.quizAssertsDao().deleteAllData();
        //      roomDB.slidesDao().deleteAllData();

        SharedPref.clearSP(LoginActivity.this);
        SharedPref.saveLoginState(getApplicationContext(), false);
        SharedPref.saveSettingState(getApplicationContext(), false);
        startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
        commonUtilsMethods.showToastMessage(LoginActivity.this, LoginActivity.this.getString(R.string.data_cleared_successfully));

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
            //  binding.password.setText(SharedPref.getLoginUserPwd(LoginActivity.this));
            binding.userId.setEnabled(false);
            if (binding.userId.getText().toString().isEmpty()) {
                binding.userId.setEnabled(true);
            }
        }

        SetUpLanguage();
    }

    private void SetUpLanguage() {
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        language = SharedPref.getSelectedLanguage(this);

        String[] languages = {"ENGLISH", "BURMESE", "FRENCH", "MANDARIN", "THAILAND", "PORTUGUESE", "SPANISH", "VIETNAMESE", "ARABIC"};
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
                case "ar":
                    binding.selectLanguage.setText("ARABIC");
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
                case "ARABIC": {
                    selectedLanguage = "ar";
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
            jsonObject.put("versionNo", getString(R.string.app_version));
            jsonObject.put("mode", Constants.APP_MODE);
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("device_id", deviceId);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("AppName", getString(R.string.str_app_name));
            jsonObject.put("language", SharedPref.getSelectedLanguage(this));
            jsonObject.put("AppDeviceRegId", fcmToken);
            jsonObject.put("Tt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_2));
            jsonObject.put("key", SharedPref.getLicenseKey(this));
            jsonObject.put("Configurl", SharedPref.getBaseWebUrl(this));


            jsonObject.put("location", "0.0 : 0.0");
            Log.v("Login", "--json-" + jsonObject);
            loginViewModel.loginProcess(this, SharedPref.getCallApiUrl(getApplicationContext()), jsonObject.toString()).observe(LoginActivity.this, new Observer<JsonElement>() {
                @Override
                public void onChanged(JsonElement jsonObject) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.loginBtn.setEnabled(true);
                    try {
                        JSONObject responseObject = new JSONObject(jsonObject.toString());
                        if (responseObject.getBoolean("success")) {
//                            if (responseObject.getString("Android_Detailing").equals("1")) {
                            Log.v("Android_Detailing", "--json-" + responseObject);
                            appAccess = responseObject.getString("sanzen_edet");
//                                System.out.println("appAccess--->"+appAccess);
                            if (appAccess.equals("1")) {


                                process(responseObject);
                                Toast.makeText(LoginActivity.this, getString(R.string.login_successfully), Toast.LENGTH_LONG).show();
//                                    commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.login_successfully));
                            } else {
                                CommonUtilsMethods.accessDialogBox(LoginActivity.this);
                            }
//                            } else {
//                                commonUtilsMethods.showToastMessage(LoginActivity.this, getString(R.string.access_denied));
//                            }
                        } else {
                            loginFailed();
                            if (responseObject.has("msg")) {
                                commonUtilsMethods.showToastMessage(LoginActivity.this, responseObject.getString("msg"));
                            }
                        }
                    } catch (JSONException e) {
                        loginFailed();
                        Log.v("Login", "--error-" + e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void
    process(JSONObject jsonObject) {
        try {
            loginDataDao.saveLoginData(new LoginDataTable(jsonObject.toString()));
            notificationDataDao.getNotificationBySyncStatus(5).forEach(data -> {
                notificationDataDao.changeNotificationSyncStatus(data.getId(), 0);
                notificationDataDao.changeNotificationReadStatus(data.getId(), 1);
            });
            SharedPref.InsertLogInData(LoginActivity.this, jsonObject);
            SharedPref.saveKeys(LoginActivity.this, jsonObject.optString("zakey"), jsonObject.optString("zskey"));
            SharedPref.saveLoginId(LoginActivity.this, userId, userPwd);
            SharedPref.saveLoginState(getApplicationContext(), true);
            SharedPref.saveSfType(LoginActivity.this, jsonObject.getString("sf_type"), jsonObject.getString("SF_Code"));
            //   SharedPref.saveHq(LoginActivity.this, jsonObject.getString("HQName"), jsonObject.getString("SF_Code"));
            SharedPref.saveHqMain(LoginActivity.this, jsonObject.getString("HQName"));

            if (SharedPref.getAutomassyncFromSP(LoginActivity.this)) {
                SharedPref.setSetUpClickedTab(getApplicationContext(), 0);
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
        timeZoneVerification();
        super.onResume();

        int loginFailedCount = SharedPref.getLoginFailedCount(LoginActivity.this);
        if (loginFailedCount == 5) {
            isTimerStarted = true;
            binding.password.setEnabled(false);
            binding.userId.setEnabled(false);
            binding.loginBtn.setEnabled(false);
            binding.clearData.setEnabled(false);
            binding.rlRejReason.setVisibility(View.VISIBLE);
            binding.rejectedReason.setText("Please try again after 5 minutes!");
            remainingTime = TimeUtils.timeDifferenceInMillis(SharedPref.getLoginFailedDateTime(LoginActivity.this), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));

            String time = "00:05:00", loginTimer = SharedPref.getLoginTimer(LoginActivity.this);
            if (!loginTimer.isEmpty()) {
                try {
                    time = String.format("00:%02d:00", Integer.parseInt(loginTimer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            long mins = TimeUtils.getMilliSeconds(TimeUtils.FORMAT_32, time);
            if (remainingTime > mins) {
                remainingTime = 0;
            } else {
                remainingTime = mins - remainingTime;
            }
            startTimer();
        }

    }

    private void timeZoneVerification() {
        boolean isAutoTimeZoneEnabled = commonUtilsMethods.isAutoTimeEnabled(LoginActivity.this) && commonUtilsMethods.isTimeZoneAutomatic(LoginActivity.this);
        if (!isAutoTimeZoneEnabled) {
            CommonUtilsMethods.showCustomDialog(this);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.rlHead.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


//    boolean iscleared(){
//        File slidesFolder;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            slidesFolder = new File(LoginActivity.this.getExternalFilesDir(null), "Slides");
//        } else {
//            return false;
//        }
//
//        if (slidesFolder.exists()) {
//            deleteRecursive(slidesFolder);
//        }
//        slidesFolder.delete();
//
//        File thumbnailStorage = new File(getApplicationContext().getExternalFilesDir(null), "/Thumbnails/");
//        if (thumbnailStorage.exists() && thumbnailStorage.isDirectory()) {
//            File[] files = thumbnailStorage.listFiles();
//            for (File file : files) {
//                if (file.isFile()) {
//                    file.delete();
//                }
//            }
//        }
//
//
//        return true;
//
//    }
//
//    private void deleteRecursive(File fileOrDirectory) {
//        if (fileOrDirectory.isDirectory()) {
//            for (File child : fileOrDirectory.listFiles()) {
//                deleteRecursive(child);
//            }
//        }
//        fileOrDirectory.delete();
//    }
}