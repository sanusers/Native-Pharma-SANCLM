package saneforce.sanclm.activity.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.DownloaderClass;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.databinding.ActivitySettingsBinding;
import saneforce.sanclm.utility.ImageStorage;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    ApiInterface apiInterface;
    DownloaderClass downloaderClass = new DownloaderClass();
    AsyncInterface asyncInterface;
    PackageManager packageManager;
    PackageInfo packageInfo;
    String deviceId = "", url = "", licenseKey ="",divisionCode = "",baseWebUrl="", phpPathUrl ="",reportsUrl="", slidesUrl ="",logoUrl="";
    int hitCount = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        deviceId = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        binding.tvDeviceId.setText(deviceId);
        SharedPref.saveDeviceId(getApplicationContext(), deviceId);

        String[] languages = {"BURMESE","ENGLISH", "FRENCH","MANDARIN", "PORTUGUESE", "SPANISH", "VIETNAMESE" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drop_down_spinner_layout, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLanguage.setAdapter(adapter);
        binding.spinnerLanguage.setSelection(1);

        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                Log.e("test",textView.getText().toString());
                String selectedLanguage = "";
                switch (textView.getText().toString().toUpperCase()) {
                    case "ENGLISH" : {
                        selectedLanguage = "en";
                        break;
                    }
                    case "BURMESE" : {
                        selectedLanguage = "my";
                        break;
                    }
                    case "FRENCH" : {
                        selectedLanguage = "fr";
                        break;
                    }
                    case "MANDARIN" : {
                        selectedLanguage = "zh";
                        break;
                    }
                    case "PORTUGUESE" : {
                        selectedLanguage = "pt";
                        break;
                    }
                    case "SPANISH" : {
                        selectedLanguage = "es";
                        break;
                    }
                    case "VIETNAMESE" : {
                        selectedLanguage = "vi";
                        break;
                    }

                }
                SharedPref.saveSelectedLanguage(SettingsActivity.this,selectedLanguage);
            }

            @Override
            public void onNothingSelected (AdapterView<?> adapterView) {

            }
        });
        binding.btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                UtilityClass.hideKeyboard(SettingsActivity.this);
                url = binding.etWebUrl.getText().toString().trim().replaceAll("\\s","");
                licenseKey = binding.etLicenseKey.getText().toString().trim();
                deviceId = binding.tvDeviceId.getText().toString();

                if (url.isEmpty()){
                    binding.etWebUrl.requestFocus();
                    Toast.makeText(SettingsActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                } else if (licenseKey.isEmpty()) {
                    binding.etLicenseKey.requestFocus();
                    Toast.makeText(SettingsActivity.this, "Enter License Key", Toast.LENGTH_SHORT).show();
                } else{

                      SharedPref.Loginsite(getApplicationContext(),url);
                    if (UtilityClass.isNetworkAvailable(getApplicationContext())){
                        if (checkURL(url)){
                            configuration("https://" + url + "/apps/");
                        }else{
                            Toast.makeText(SettingsActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void selectLanguage(){

        final String[] Language = {"ENGLISH", "FRENCH", "PORTUGUESE", "BURMESE", "VIETNAMESE", "MANDARIN", "SPANISH"};
        ArrayList<String> langList = new ArrayList<>();
        Collections.addAll(langList, Language);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater =this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_listview, null);
        alertDialog.setView(dialogView);
        TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
        ListView listView = dialogView.findViewById(R.id.listView);

        headerTxt.setText("Select a language");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, langList);
        listView.setAdapter(adapter);
        AlertDialog dialog = alertDialog.create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View view, int position, long l) {
                String selectedLang = listView.getItemAtPosition(position).toString();
                Log.e("test","selected language : " + selectedLang);
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private static boolean checkURL(CharSequence input) {
        boolean validUrl = false;
        Pattern pattern = Patterns.WEB_URL;
        validUrl = pattern.matcher(input).matches();
        return validUrl;
    }

    public void configuration(String enteredUrl){
        binding.configurationPB.setVisibility(View.VISIBLE);
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), enteredUrl);

        try {
            Call<JsonArray> call = apiInterface.configuration("ConfigiOS.json");
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse (@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                    binding.configurationPB.setVisibility(View.GONE);
                    if (response.isSuccessful()){
                        Log.e("test","success : "+ response.body().toString());
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response.body().toString());
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
                                    SharedPref.setTagImageUrl(getApplicationContext(), "http://" + binding.etWebUrl.getText().toString().trim() + "/");
                                    String[] splitUrl = logoUrl.split("/");
                                    SharedPref.saveUrls(getApplicationContext(), enteredUrl, licenseKey, baseWebUrl, phpPathUrl, reportsUrl, slidesUrl, logoUrl, true);
                                    SharedPref.setCallApiUrl(SettingsActivity.this, baseWebUrl + phpPathUrl.replaceAll("\\?.*", "/"));
                                    downloadImage(baseWebUrl + logoUrl, splitUrl[splitUrl.length -1], enteredUrl);
                                    licenseKeyValid = true;
                                    break;
                                }
                            }

                            if (!licenseKeyValid){
                                Toast.makeText(SettingsActivity.this, "Invalid license key", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        SharedPref.Loginsite(getApplicationContext(),url);
                    }else{
                        Toast.makeText(SettingsActivity.this, "Invalid web url", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure (@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                    Log.e("test","failed : " + t.toString());
                    hitCount++;
                    if (hitCount < 2){
                        configuration("http://" + url + "/apps/");
                    }else{
                        binding.configurationPB.setVisibility(View.GONE);
                        Toast.makeText(SettingsActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                        Log.e("test","hit count is : " + hitCount);
                        hitCount =0;
                    }

                }
            });
        } catch (Exception exception){
            throw new RuntimeException(exception);
        }

    }

    public void downloadImage(String url,String imageName,String enteredUrl){
        packageManager = this.getPackageManager();
        String packageName = this.getPackageName();
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String fileDirectory = packageInfo.applicationInfo.dataDir;
        Log.e("test","filepath name : " + fileDirectory + "/" + imageName);

        asyncInterface = new AsyncInterface() {
            @Override
            public void taskCompleted (boolean status) {
                downloaderClass.cancel(true);
                navigate();
            }
        };

        if(!ImageStorage.checkIfImageExists(fileDirectory, imageName)) {
            Log.e("test","image not exists");
            downloaderClass = (DownloaderClass) new DownloaderClass(url, fileDirectory, imageName, asyncInterface).execute();
        }else{
            Log.e("test","image exists");
            navigate();
        }

    }

    public void navigate(){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SettingsActivity.this, "Configured Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.putExtra(Constants.NAVIGATE_FROM, "Setting");
        startActivity(intent);
        finish();
    }


}