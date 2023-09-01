package saneforce.sanclm.activity.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.common.UtilityClass;
import saneforce.sanclm.databinding.ActivitySettingsBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SharedPref;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    ApiInterface apiInterface;
    String deviceId = "", url = "", licenseKey ="",divisionCode = "",baseWebUrl="", phpPathUrl ="",reportsUrl="", slidesUrl ="";
    int hitCount = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deviceId = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        binding.deviceIdETxt.setText(deviceId);
        SharedPref.saveDeviceId(getApplicationContext(), deviceId);

        binding.language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                selectLanguage();
            }
        });

        binding.saveSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                UtilityClass.hideKeyboard(SettingsActivity.this);
                url = binding.urlETxt.getText().toString().trim().replaceAll("\\s","");
                licenseKey = binding.licenseETxt.getText().toString().trim();
                deviceId = binding.deviceIdETxt.getText().toString();
                
                if (url.isEmpty()){
                    binding.urlETxt.requestFocus();
                    Toast.makeText(SettingsActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                } else if (licenseKey.isEmpty()) {
                    binding.licenseETxt.requestFocus();
                    Toast.makeText(SettingsActivity.this, "Enter License Key", Toast.LENGTH_SHORT).show();
                } else{
                    Log.e("test","url is  : " + url + " -- device id : " + deviceId);
                    if (UtilityClass.isNetworkAvailable(getApplicationContext())){
                        configuration("https://" + url + "/apps/");
                    }else{
                        Toast.makeText(getApplicationContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
                    }
//                    startActivity(new Intent(SettingsActivity.this, MasterSyncActivity.class));
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

    public void configuration(String enteredUrl){
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), enteredUrl);

        Call<JsonArray> call = apiInterface.Configuration("ConfigiOS.json");
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse (@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                if (response.isSuccessful()){
                    Log.e("test","success : "+ response.body().toString());
                    SharedPref.saveBaseUrl(getApplicationContext(),"https://"+url+"/apps/");
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(response.body().toString());
                        boolean licenseKeyValid = false;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            if (jsonObj.getString("key").equalsIgnoreCase(licenseKey)) {
                                JSONObject Config = new JSONObject(jsonObj.getString("config"));
                                divisionCode = Config.getString("division");
                                baseWebUrl = Config.getString("weburl");
                                phpPathUrl = Config.getString("appurl");
                                reportsUrl = Config.getString("reportUrl");
                                slidesUrl = Config.getString("slideurl");
                                SharedPref.saveUrls(getApplicationContext(),enteredUrl,licenseKey,baseWebUrl,phpPathUrl,reportsUrl,slidesUrl);
                                Toast.makeText(SettingsActivity.this, "Configured Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                licenseKeyValid = true;
                                break;
                            }
                        }
                        
                        if (!licenseKeyValid){
                            Toast.makeText(SettingsActivity.this, "Invalid license", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    Toast.makeText(SettingsActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure (@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                Log.e("test","failed : " + t.toString());
                hitCount++;
                if (hitCount <2){
                    configuration("http://" + url + "/apps/");
                }else{
                    Log.e("test","hit count is : " + hitCount);
                    hitCount =0;
                }

            }
        });

    }
}