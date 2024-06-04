package saneforce.sanzen.activity.reports;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityReportsBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;

public class ReportsActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static ActivityReportsBinding binding;
    ReportsAdapter reportsAdapter;
    ApiInterface apiInterface;

    CommonUtilsMethods commonUtilsMethods;
    ProgressDialog progressDialog;
    String url;


    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        populateAdapter();
        binding.backArrow.setOnClickListener(view -> onBackPressed());

    }

    public void populateAdapter() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Day Report");
       /* arrayList.add("Monthly Report");
        arrayList.add("Day Check In Report");
        arrayList.add("Customer Check In Report");
        arrayList.add("Visit Monitor");*/
        arrayList.add("Dash Board");

        reportsAdapter = new ReportsAdapter(arrayList, ReportsActivity.this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ReportsActivity.this, 4);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(reportsAdapter);
    }

    public void getData(String report, String date) {
        if (UtilityClass.isNetworkAvailable(this)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("sf_type", SharedPref.getSfType(this));
                        jsonObject.put("divisionCode", SharedPref.getDivisionCode(this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
                        jsonObject.put("Designation", SharedPref.getDesig(this));
                        jsonObject.put("state_code", SharedPref.getStateCode(this));
                        jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));
                        jsonObject.put("rptDt", date);
                        jsonObject.put("versionNo",  getString(R.string.app_version));
                        jsonObject.put("mod", Constants.APP_MODE);
                        jsonObject.put("Device_version", Build.VERSION.RELEASE);
                        jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
                        jsonObject.put("AppName", getString(R.string.str_app_name));
                        jsonObject.put("language", SharedPref.getSelectedLanguage(this));
                        if (report.equalsIgnoreCase("DAY REPORT")) {
                            jsonObject.put("tableName", "getdayrpt_edet");

                        }

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                progressDialog.dismiss();
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        JsonElement jsonElement = response.body();
                                        JSONArray jsonArray = new JSONArray();
                                        if (jsonElement.isJsonArray()) {
                                            jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                            navigate(jsonArray, report, date);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                progressDialog.dismiss();

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(ReportsActivity.this, "Network UnAvailable");
                }

            });
            networkStatusTask.execute();
        } else {
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.no_network));
        }
    }

    public void navigate(JSONArray jsonArray, String report, String date) {
        Intent intent = new Intent(ReportsActivity.this, ReportFragContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", jsonArray.toString());
        bundle.putString("date", date);
        bundle.putString("fragment", report);
        intent.putExtra("reportBundle", bundle);
        startActivity(intent);
    }
}