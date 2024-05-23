package saneforce.sanzen.activity.setting;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.PrivacyPolicyActvity.PrivacyPolicyActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivitySettingsBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.DownloaderClass;
import saneforce.sanzen.utility.ImageStorage;
import saneforce.sanzen.utility.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    ApiInterface apiInterface;
    DownloaderClass downloaderClass = new DownloaderClass();
    AsyncInterface asyncInterface;
    PackageManager packageManager;
    PackageInfo packageInfo;
    String deviceId = "", url = "", licenseKey = "", divisionCode = "", baseWebUrl = "", phpPathUrl = "", reportsUrl = "", slidesUrl = "", logoUrl = "";
    int hitCount = 0;
    CommonUtilsMethods commonUtilsMethods;
    Resources resources;
    String language;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        deviceId = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        binding.tvDeviceId.setText(deviceId);
        SharedPref.saveDeviceId(getApplicationContext(), deviceId);
        binding.btnSaveSettings.setEnabled(true);
        SetUpLanguage();
        if (!SharedPref.getSaveUrlSetting(getApplicationContext()).equalsIgnoreCase("")) {
            binding.etWebUrl.setText(SharedPref.getSaveUrlSetting(getApplicationContext()));
            binding.etLicenseKey.setText(SharedPref.getSaveLicenseSetting(getApplicationContext()));
        }

        binding.btnSaveSettings.setOnClickListener(view -> {

            UtilityClass.hideKeyboard(SettingsActivity.this);
            url = binding.etWebUrl.getText().toString().trim().replaceAll("\\s", "");
            licenseKey = binding.etLicenseKey.getText().toString().trim();
            deviceId = binding.tvDeviceId.getText().toString();

            if (url.isEmpty()) {
                binding.etWebUrl.requestFocus();
                commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.enter_url));
            } else if (licenseKey.isEmpty()) {
                binding.etLicenseKey.requestFocus();
                commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.enter_license));
            } else {

                SharedPref.Loginsite(getApplicationContext(), url);
                if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                    if (checkURL(url)) {
                        Log.i("settings", "onCreate: " + url + "\nLink: " + "https://" + url );
                        configuration("https://" + url );
                    } else {
                        commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.invalid_url));
                    }
                } else {
                    commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.no_network));
                }
            }
        });

    }

    private void SetUpLanguage() {
        String[] languages = {"ENGLISH", "BURMESE", "FRENCH", "MANDARIN", "THAILAND", "PORTUGUESE", "SPANISH", "VIETNAMESE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drop_down_spinner_layout, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLanguage.setAdapter(adapter);

        commonUtilsMethods.setUpLanguage(getApplicationContext());
        language = SharedPref.getSelectedLanguage(this);

        if (!language.equalsIgnoreCase("")) {
            String languageData = SharedPref.getSelectedLanguage(getApplicationContext());
            SelectedLanguage(languageData);
            switch (languageData) {
                case "pt":
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "PORTUGUESE");
                    break;
                case "fr":
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "FRENCH");
                    break;
                case "my":
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "BURMESE");
                    break;
                case "vi":
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "VIETNAMESE");
                    break;
                case "zh":
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "MANDARIN");
                    break;
                case "es":
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "SPANISH");
                    break;
                case "th":
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "THAILAND");
                    break;
                default:
                    commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "ENGLISH");
                    break;
            }
        } else {
            SelectedLanguage("en");
            commonUtilsMethods.setSpinnerText(binding.spinnerLanguage, "ENGLISH");
        }

        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
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
                SharedPref.saveSelectedLanguage(SettingsActivity.this, selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void selectLanguage() {
        final String[] Language = {"ENGLISH", "FRENCH", "PORTUGUESE", "BURMESE", "VIETNAMESE", "MANDARIN", "SPANISH"};
        ArrayList<String> langList = new ArrayList<>();
        Collections.addAll(langList, Language);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_listview, null);
        alertDialog.setView(dialogView);
        TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
        ListView listView = dialogView.findViewById(R.id.listView);

        headerTxt.setText(R.string.select_a_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, langList);
        listView.setAdapter(adapter);
        AlertDialog dialog = alertDialog.create();

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedLang = listView.getItemAtPosition(position).toString();
            Log.e("test", "selected language : " + selectedLang);
            SelectedLanguage(selectedLang);
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
        dialog.show();

    }

    private void SelectedLanguage(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        context = LocaleHelper.setLocale(getApplicationContext(), lang);
        resources = getApplicationContext().getResources();
        binding.btnSaveSettings.setText(getString(R.string.str_save_settings));
    }

    private static boolean checkURL(CharSequence input) {
        boolean validUrl = false;
        Pattern pattern = Patterns.WEB_URL;
        validUrl = pattern.matcher(input).matches();
        return validUrl;
    }

    public void configuration(String enteredUrl) {
        binding.btnSaveSettings.setEnabled(false);
        binding.configurationPB.setVisibility(View.VISIBLE);
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), enteredUrl);

        try {
            JSONArray jsonArray = new JSONArray("[{\"key\":\"GLSP1128_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/GL/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"49\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"GLSPDEV1128_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/devdp.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/GL/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"49\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"ALPT1612_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/AP/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"70\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"AURL1018_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/AM/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"71\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"DEMO2904_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/DEM/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"15\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"DEMO2603_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/ZF/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"1\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"DEMO2801_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/Dem/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"15\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"FUT5258_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/FP/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"34\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/FUT5258IC.png\"}},{\"key\":\"BALV2618_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/MS/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"79\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"BALG2618_1\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/BG/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"81\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"GLSP1128\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/GL/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"49\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"GLSPDEV1128\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/devdp.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/GL/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"49\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"ALPT1612\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/AP/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"70\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"AURL1018\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/AM/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"71\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"DEMO2904\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/DEM/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"15\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"DEMO2603\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/ZF/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"1\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"DEMO2801\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/Dem/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"15\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"FUT5258\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/FP/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"34\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/FUT5258IC.png\"}},{\"key\":\"BALV2618\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/MS/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"79\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"BALG2618\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/BG/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"81\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"demo123\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/ZF/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"1\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"test123\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/SC/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"44\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"DEMO\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/ST/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"22\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903IC.png\"}},{\"key\":\"sandemo\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/DP/download/\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"63\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"sandet\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/DP/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"63\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"ED19\",\"config\":{\"weburl\":\"http://edetailing.sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/KS/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"19\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"SLSC2811\",\"config\":{\"weburl\":\"http://sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/SI/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"4\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"MARK2023\",\"config\":{\"weburl\":\"http://sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/DP/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"63\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"parvathi\",\"config\":{\"weburl\":\"http://sanffa.info/\",\"baseurl\":\"iOSServer/db_parvathi.php?axn=\",\"appurl\":\"iOSServer/db_api_parvathi.php?axn=\",\"iosurl\":\"iOSServer/db_api_parvathi.php?axn=\",\"slideurl\":\"Edetailing_files/DP/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"63\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"SKDEMO\",\"config\":{\"weburl\":\"http://sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"slideurl\":\"Edetailing_files/KS/download/\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"19\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}},{\"key\":\"TECH\",\"config\":{\"weburl\":\"http://sanffa.info/\",\"baseurl\":\"iOSServer/db.php?axn=\",\"slideurl\":\"Edetailing_files/DP/download/\",\"iosurl\":\"iOSServer/db_api.php?axn=\",\"appurl\":\"iOSServer/db_api.php?axn=\",\"reportUrl\":\"iOSServer/DetailingReport/\",\"division\":\"63\",\"bgimg\":\"iOSServer/eDetCompImg/DEMO2903BG.png\",\"logoimg\":\"iOSServer/eDetCompImg/DEMO2903ICNew.png\"}}]");
            boolean licenseKeyValid = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if (jsonObj.getString("key").equalsIgnoreCase(licenseKey)) {
                    JSONObject config = new JSONObject(jsonObj.getString("config"));
                    divisionCode = config.getString("division");
                    baseWebUrl = config.getString("weburl");
                    phpPathUrl = config.getString("appurl");
                    reportsUrl = config.getString("reportUrl");
                    slidesUrl = config.getString("slideurl");
                    logoUrl = config.getString("logoimg");

                    String web_url_getText = "http://" + binding.etWebUrl.getText().toString().trim() + "/";
                    String urlData = web_url_getText + phpPathUrl;
                    String UploadUrl = urlData.substring(0, urlData.indexOf('?')) + "/";

                    SharedPref.setTagImageUrl(getApplicationContext(), web_url_getText);
                    SharedPref.setTagApiImageUrl(getApplicationContext(), UploadUrl);

                    String[] splitUrl = logoUrl.split("/");
                    SharedPref.saveUrls(getApplicationContext(), enteredUrl, licenseKey, baseWebUrl, phpPathUrl, reportsUrl, slidesUrl, logoUrl, true);
                    SharedPref.setCallApiUrl(SettingsActivity.this, baseWebUrl + phpPathUrl.replaceAll("\\?.*", "/"));
                    downloadImage(baseWebUrl + logoUrl, splitUrl[splitUrl.length - 1], enteredUrl);
                    licenseKeyValid = true;
                    SharedPref.setSaveUrlSetting(getApplicationContext(), binding.etWebUrl.getText().toString());
                    SharedPref.setSaveLicenseSetting(getApplicationContext(), binding.etLicenseKey.getText().toString());
                    break;
                }
            }

            if (!licenseKeyValid){
                binding.configurationPB.setVisibility(View.GONE);
                binding.btnSaveSettings.setEnabled(true);
                commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.invalid_Lis));
            }
            SharedPref.Loginsite(getApplicationContext(), url);
        } catch (Exception e){
            e.printStackTrace();
        }

        navigate();
//        try {
//            Call<JsonArray> call = apiInterface.configuration("/Apps/ConfigiOS.json");
//            call.enqueue(new Callback<JsonArray>() {
//                @Override
//                public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
//                    Log.d("setting", "onResponse: " + response.isSuccessful() + "\n" + response.body());
//                    Log.d("setting", "onResponse: " + response.message() );
//                    Log.d("setting", "onResponse: " + response.errorBody() );
//                    Log.d("setting", "onResponse: " + response.headers());
//                    if (response.isSuccessful()) {
//                        Log.e("test", "success : " + response.body().toString());
//                        JSONArray jsonArray = null;
//                        try {
//                            jsonArray = new JSONArray(response.body().toString());
//                            boolean licenseKeyValid = false;
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObj = jsonArray.getJSONObject(i);
//                                if (jsonObj.getString("key").equalsIgnoreCase(licenseKey)) {
//                                    JSONObject config = new JSONObject(jsonObj.getString("config"));
//                                    divisionCode = config.getString("division");
//                                    baseWebUrl = config.getString("weburl");
//                                    phpPathUrl = config.getString("appurl");
//                                    reportsUrl = config.getString("reportUrl");
//                                    slidesUrl = config.getString("slideurl");
//                                    logoUrl = config.getString("logoimg");
//
//                                    String web_url_getText = "http://" + binding.etWebUrl.getText().toString().trim() + "/";
//                                    String urlData = web_url_getText + phpPathUrl;
//                                    String UploadUrl = urlData.substring(0, urlData.indexOf('?')) + "/";
//
//                                    SharedPref.setTagImageUrl(getApplicationContext(), web_url_getText);
//                                    SharedPref.setTagApiImageUrl(getApplicationContext(), UploadUrl);
//
//                                    String[] splitUrl = logoUrl.split("/");
//                                    SharedPref.saveUrls(getApplicationContext(), enteredUrl, licenseKey, baseWebUrl, phpPathUrl, reportsUrl, slidesUrl, logoUrl, true);
//                                    SharedPref.setCallApiUrl(SettingsActivity.this, baseWebUrl + phpPathUrl.replaceAll("\\?.*", "/"));
//                                    downloadImage(baseWebUrl + logoUrl, splitUrl[splitUrl.length - 1], enteredUrl);
//                                    licenseKeyValid = true;
//                                    SharedPref.setSaveUrlSetting(getApplicationContext(), binding.etWebUrl.getText().toString());
//                                    SharedPref.setSaveLicenseSetting(getApplicationContext(), binding.etLicenseKey.getText().toString());
//                                    break;
//                                }
//                            }
//
//                            if (!licenseKeyValid){
//                                    binding.configurationPB.setVisibility(View.GONE);
//                                    binding.btnSaveSettings.setEnabled(true);
//                                    commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.invalid_Lis));}
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        SharedPref.Loginsite(getApplicationContext(), url);
//                    } else {
//                        binding.btnSaveSettings.setEnabled(true);
//                        commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.invalid_url));
//                        binding.configurationPB.setVisibility(View.GONE);
//                    }
//
//
//
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
//                    Log.e("test", "failed : " + t.toString());
//                    binding.btnSaveSettings.setEnabled(true);
//                    hitCount++;
//                    if (hitCount < 2) {
//                        configuration("http://" + url + "/apps/");
//                    } else {
//                        binding.configurationPB.setVisibility(View.GONE);
//                        commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.invalid_url));
//                        Log.e("test", "hit count is : " + hitCount);
//                        hitCount = 0;
//                    }
//                }
//            });
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
    }

    public void downloadImage(String url, String imageName, String enteredUrl) {
        packageManager = this.getPackageManager();
        String packageName = this.getPackageName();
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String fileDirectory = packageInfo.applicationInfo.dataDir;
        Log.e("test", "filepath name : " + fileDirectory + "/" + imageName);

        asyncInterface = status -> {
            downloaderClass.cancel(true);
            navigate();
        };

        if (!ImageStorage.checkIfImageExists(fileDirectory, imageName)) {
            Log.e("test", "image not exists");
            downloaderClass = (DownloaderClass) new DownloaderClass(url, fileDirectory, imageName, asyncInterface).execute();
        } else {
            Log.e("test", "image exists");
            navigate();
        }

    }

    public void navigate() {

        runOnUiThread(() -> {binding.configurationPB.setVisibility(View.GONE);binding.btnSaveSettings.setEnabled(false);
            commonUtilsMethods.showToastMessage(SettingsActivity.this, getString(R.string.configure_success));});
        Intent intent = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
        intent.putExtra(Constants.NAVIGATE_FROM, "Setting");
        startActivity(intent);
        finish();
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