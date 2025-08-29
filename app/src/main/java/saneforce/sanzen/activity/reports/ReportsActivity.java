package saneforce.sanzen.activity.reports;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

import saneforce.sanzen.activity.reports.dayReport.adapter.DynamicAdapter;
import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
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
    DynamicAdapter adapter;
    ArrayList<MenuModel> menuModels = new ArrayList<>();


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
        if (SharedPref.getDashboard(this).equals("0")){
            arrayList.add("Dash Board");
        }
        if(SharedPref.getDynamicOptionNeed(this).equals("0")){
            arrayList.add(SharedPref.getDynamicOptionCaps(context));
        }
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

                        JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(this);
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("divisionCode", SharedPref.getDivisionCode(this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
                        jsonObject.put("rptDt", date);
                        if (report.equalsIgnoreCase("DAY REPORT")) {
                            jsonObject.put("tableName", "getdayrpt_edet");

                        }

                        Log.d("Report", "getData: " + jsonObject);

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
                    commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.poor_connection));
                }

            });
            networkStatusTask.execute();
        } else {
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.no_network));
        }
    }

    public void getDynamicData(String report, String date){
        if (UtilityClass.isNetworkAvailable(this)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));

                        JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(this);
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
                        if (report.equalsIgnoreCase(SharedPref.getDynamicOptionCaps(context))) {
                            jsonObject.put("tableName", "getdynamicmenu");

                        }

                        Log.d("Report", "getData: " + jsonObject);

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                progressDialog.dismiss();
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        JsonArray jsonArray = response.body().getAsJsonArray();
                                        if (jsonArray.size() > 0) {
                                            for (int i = 0; i < jsonArray.size(); i++) {
                                                JsonObject menuObject = jsonArray.get(i).getAsJsonObject();
                                                String menu_name = menuObject.get("Menu_Name").getAsString();
                                                String menu_icon = menuObject.get("Menu_Icon").getAsString();
                                                JsonArray menu_sub_details = menuObject.get("Menu_Options").getAsJsonArray();

                                                menu_icon = SharedPref.getTagImageUrl(ReportsActivity.this) + "/" + menu_icon;

                                                MenuModel menuModel = new MenuModel(menu_name, menu_icon, menu_sub_details);
                                                menuModels.add(menuModel);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }else{
//                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_record_found), Toast.LENGTH_LONG).show();
                                        }


                                        JsonElement jsonElement = response.body();
                                        JSONArray jsonArray1 = new JSONArray();
                                        if (jsonElement.isJsonArray()) {
                                            jsonArray1 = new JSONArray(jsonElement.getAsJsonArray().toString());
                                            navigateDyn(jsonArray1, report, date);
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
                    commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.poor_connection));
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

    public void navigateDyn(JSONArray jsonArray, String report, String date) {
        Intent intent = new Intent(ReportsActivity.this, DynamicMenuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", jsonArray.toString());
        bundle.putString("date", date);
      /*  bundle.putString("fragment", report);
        intent.putExtra("reportBundle", bundle);*/
        startActivity(intent);
    }
}