//package saneforce.sanzen.activity.reports;
//
//import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
//
//import android.annotation.SuppressLint;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
////import saneforce.sanzen.activity.reports.missedReport.MissedReport;
//import saneforce.sanzen.R;
//import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
//import saneforce.sanzen.activity.reports.dayReport.model.SubMenuModel;
//import saneforce.sanzen.activity.reports.missedReport.MissedReportGraph;
//import saneforce.sanzen.activity.reports.visitMonitor.VisitMonitorActivity;
//import saneforce.sanzen.commonClasses.SafeClickListener;
//
//import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
//import saneforce.sanzen.commonClasses.CommonUtilsMethods;
//import saneforce.sanzen.commonClasses.UtilityClass;
//import saneforce.sanzen.databinding.ActivityReportsBinding;
//import saneforce.sanzen.network.ApiInterface;
//import saneforce.sanzen.network.RetrofitClient;
//import saneforce.sanzen.storage.SharedPref;
//import saneforce.sanzen.utility.NetworkStatusTask;
//import saneforce.sanzen.utility.TimeUtils;
//
//public class ReportsActivity extends AppCompatActivity {
//
//    @SuppressLint("StaticFieldLeak")
//    public static ActivityReportsBinding binding;
//    ReportsAdapter reportsAdapter;
//    ApiInterface apiInterface;
//
//    CommonUtilsMethods commonUtilsMethods;
//    ProgressDialog progressDialog;
//    String url;
//    //    DynamicAdapter adapter;
//    ArrayList<MenuModel> menuList = new ArrayList<>();
//    ArrayList<String> reportTitles = new ArrayList<>();
//
//
//    //To Hide the bottomNavigation When popup
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityReportsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
//        commonUtilsMethods.setUpLanguage(getApplicationContext());
//        if (SharedPref.getDynamicOptionNeed(ReportsActivity.this).equalsIgnoreCase("0")) {
//            loadMenuFromApi();
//        }
//
//        populateAdapter();
//        binding.backArrow.setOnClickListener(new SafeClickListener() {
//            @Override
//            public void onSafeClick(View view) {
//               /* Intent intent = new Intent(ReportsActivity.this, HomeDashBoard.class);
//                startActivity(intent);*/
//                finish();
//
//            }
//        });
//
//    }
//
////    public void populateAdapter() {
////        ArrayList<String> arrayList = new ArrayList<>();
////        arrayList.add("Day Report");
////       /* arrayList.add("Monthly Report");
////        arrayList.add("Day Check In Report");
////        arrayList.add("Customer Check In Report");*/
////        arrayList.add("Visit Monitor");
////        arrayList.add("Missed Report");
////        if (SharedPref.getDashboard(this).equals("0")){
////            arrayList.add("Dash Board");
////        }
//
//    /// /        if (SharedPref.getDynamicOptionNeed(this).equals("0")) {
//    /// /            arrayList.add(SharedPref.getDynamicOptionCaps(context));
//    /// /        }
////        reportsAdapter = new ReportsAdapter(arrayList, ReportsActivity.this);
////        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ReportsActivity.this, 4);
////        binding.recView.setLayoutManager(layoutManager);
////        binding.recView.setAdapter(reportsAdapter);
////    }
//    public void populateAdapter() {
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("Day Report");
//        arrayList.add("Visit Monitor");
//        arrayList.add("Missed Report");
//        if (SharedPref.getDashboard(this).equals("0")) {
//            arrayList.add("Dash Board");
//        }
//
//        reportsAdapter = new ReportsAdapter(arrayList, this, reportName -> {
//            switch (reportName.toUpperCase()) {
//                case "DAY REPORT":
//                    progressDialog = CommonUtilsMethods.createProgressDialog(this);
//                    getData(reportName, TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_4));
//                    break;
//
//                case "VISIT MONITOR":
//                    startActivity(new Intent(this, VisitMonitorActivity.class));
//                    break;
//
//                case "MISSED REPORT":
//                    startActivity(new Intent(this, MissedReportGraph.class));
//                    break;
//
//                case "DASH BOARD":
//                    startActivity(new Intent(this, ReportWebActivity.class));
//                    break;
//
//                default:
//                    if (SharedPref.getDynamicOptionNeed(this).equalsIgnoreCase("0")) {
//                        Intent intent = new Intent(this, DynamicWebActivity.class);
//                        intent.putExtra("title", reportName);
//                        startActivity(intent);
//                    }
//                    break;
//            }
//        });
//
//        binding.recView.setLayoutManager(new GridLayoutManager(this, 4));
//        binding.recView.setAdapter(reportsAdapter);
//    }
//
//    public void getData(String report, String date) {
//        if (UtilityClass.isNetworkAvailable(this)) {
//            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
//                if (status) {
//                    try {
//                        apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));
//                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
//                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
//                        jsonObject.put("divisionCode", SharedPref.getDivisionCode(this));
//                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
//                        jsonObject.put("rptDt", date);
//                        if (report.equalsIgnoreCase("DAY REPORT")) {
//                            jsonObject.put("tableName", "getdayrpt_edet");
//                        }
//
//                        Log.d("Report", "getData: " + jsonObject);
//
//                        Map<String, String> mapString = new HashMap<>();
//                        mapString.put("axn", "get/reports");
//                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
//                        call.enqueue(new Callback<JsonElement>() {
//                            @Override
//                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                                progressDialog.dismiss();
//                                try {
//                                    if (response.isSuccessful() && response.body() != null) {
//                                        JsonElement jsonElement = response.body();
//                                        JSONArray jsonArray = new JSONArray();
//                                        if (jsonElement.isJsonArray()) {
//                                            jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
//                                            navigate(jsonArray, report, date);
//                                        }
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                            @Override
//                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//                                progressDialog.dismiss();
//
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    progressDialog.dismiss();
//                    commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.poor_connection));
//                }
//
//            });
//            networkStatusTask.execute();
//        } else {
//            progressDialog.dismiss();
//            commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.no_network));
//        }
//    }
//
//    public void getDynamicData() {
//        if (UtilityClass.isNetworkAvailable(this)) {
//            Intent intent = new Intent(context, DynamicMenuActivity.class);
//            startActivity(intent);
//            progressDialog.dismiss();
//        } else {
//            progressDialog.dismiss();
//            commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.no_network));
//        }
//    }
//
//    public void navigate(JSONArray jsonArray, String report, String date) {
//        Intent intent = new Intent(ReportsActivity.this, ReportFragContainerActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("data", jsonArray.toString());
//        bundle.putString("date", date);
//        bundle.putString("fragment", report);
//        intent.putExtra("reportBundle", bundle);
//        startActivity(intent);
//    }
//
//    private void loadMenuFromApi() {
//        if (UtilityClass.isNetworkAvailable(this)) {
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("Loading...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//
//            menuList = new ArrayList<>();
//
//            binding.recView.setLayoutManager(new GridLayoutManager(this, 4));
//            binding.recView.setAdapter(reportsAdapter);
//
//            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
//                if (status) {
//                    try {
//                        apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));
//
//                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
//                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
//                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
//                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
//                        jsonObject.put("tableName", "getDynamicReport");
//
//                        Map<String, String> mapString = new HashMap<>();
//                        mapString.put("axn", "get/reports");
//
//                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
//                        call.enqueue(new Callback<JsonElement>() {
//                            @Override
//                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                                progressDialog.dismiss();
//                                try {
//                                    if (response.isSuccessful() && response.body() != null) {
//                                        JsonArray jsonArray = response.body().getAsJsonArray();
//                                        if (jsonArray.size() > 0) {
//                                            for (int i = 0; i < jsonArray.size(); i++) {
//                                                JsonObject menuObject = jsonArray.get(i).getAsJsonObject();
//
//                                                String Menu_Name = menuObject.get("Menu_Name").getAsString();
//                                                String Menu_Icon = SharedPref.getTagImageUrl(ReportsActivity.this)
//                                                        + "/" + menuObject.get("Menu_Icon").getAsString();
//                                                String Menu_Page = SharedPref.getTagImageUrl(ReportsActivity.this)
//                                                        + "/" + menuObject.get("Menu_Page").getAsString() + "?";
//                                                Menu_Page += "sfcode=" + SharedPref.getSfCode(ReportsActivity.this)
//                                                        + "&rSF=" + SharedPref.getHqCode(ReportsActivity.this)
//                                                        + "&div_code=" + SharedPref.getDivisionCode(ReportsActivity.this)
//                                                        + "&cMnth=" + TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_9)
//                                                        + "&cYr=" + TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_12)
//                                                        + "&doc_id=-1&IsDocView=0&cluster_code=-1";
//
//                                                MenuModel menuModel = new MenuModel(Menu_Name, Menu_Icon, Menu_Page);
//                                                menuList.add(menuModel);
//                                            }
//                                            reportsAdapter.notifyDataSetChanged();
//                                        } else {
//                                            commonUtilsMethods.showToastMessage(ReportsActivity.this, "No Record Found");
//                                        }
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//                                progressDialog.dismiss();
//                                commonUtilsMethods.showToastMessage(ReportsActivity.this,
//                                        getString(R.string.poor_connection) + " " + getString(R.string.please_try_again));
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    progressDialog.dismiss();
//                    commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.poor_connection));
//                }
//            });
//            networkStatusTask.execute();
//
//        } else {
//            commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.no_network));
//        }
//    }
//
//}

package saneforce.sanzen.activity.reports;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
import saneforce.sanzen.activity.reports.missedReport.MissedReportGraph;
import saneforce.sanzen.activity.reports.visitMonitor.VisitMonitorActivity;
import saneforce.sanzen.commonClasses.SafeClickListener;

import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityReportsBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class ReportsActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static ActivityReportsBinding binding;
    ReportsAdapter reportsAdapter;
    ApiInterface apiInterface;

    CommonUtilsMethods commonUtilsMethods;
    ProgressDialog progressDialog;
    String url;
    // Removed menuList as we will use a single list for the adapter
    // ArrayList<MenuModel> menuList = new ArrayList<>();

    // This list will now hold ALL report titles (static + dynamic)
    ArrayList<String> reportTitles = new ArrayList<>();

    // Store MenuModel objects separately for dynamic report URL lookup
    ArrayList<MenuModel> dynamicMenuList = new ArrayList<>();


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

        // 1. Initialize static reports and adapter
        populateAdapter();

        // 2. Load dynamic reports if needed (API call)
        if (SharedPref.getDynamicOptionNeed(ReportsActivity.this).equalsIgnoreCase("0")) {
            loadMenuFromApi();
        }

        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                finish();
            }
        });

    }

    public void populateAdapter() {
        // Clear previous entries and add static reports
        reportTitles.clear();
        reportTitles.add("Day Report");
        reportTitles.add("Visit Monitor");
        reportTitles.add("Missed Report");
        if (SharedPref.getDashboard(this).equals("0")) {
            reportTitles.add("Dash Board");
        }

        // Initialize adapter with the combined list
        reportsAdapter = new ReportsAdapter(reportTitles, this, reportName -> {
            switch (reportName.toUpperCase()) {
                case "DAY REPORT":
                    progressDialog = CommonUtilsMethods.createProgressDialog(this);
                    getData(reportName, TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_4));
                    break;

                case "VISIT MONITOR":
                    startActivity(new Intent(this, VisitMonitorActivity.class));
                    break;

                case "MISSED REPORT":
                    startActivity(new Intent(this, MissedReportGraph.class));
                    break;

                case "DASH BOARD":
                    // Assuming ReportWebActivity is for the general dashboard
                    startActivity(new Intent(this, ReportWebActivity.class));
                    break;

                default:
                    // Handle Dynamic Reports
                    if (SharedPref.getDynamicOptionNeed(this).equalsIgnoreCase("0")) {
                        // Find the corresponding MenuModel to get the URL
                        MenuModel dynamicReport = findDynamicReport(reportName);
                        if(dynamicReport != null) {
                            Intent intent = new Intent(this, DynamicWebActivity.class);
                            intent.putExtra("title", reportName);
                            intent.putExtra("url", dynamicReport.getMenu_Sub_Details()); // Pass the generated URL
                            startActivity(intent);
                        } else {
                            commonUtilsMethods.showToastMessage(this, "Dynamic Report URL not found.");
                        }
                    }
                    break;
            }
        });

        binding.recView.setLayoutManager(new GridLayoutManager(this, 4));
        binding.recView.setAdapter(reportsAdapter);
    }

    private MenuModel findDynamicReport(String reportName) {
        for (MenuModel model : dynamicMenuList) {
            if (model.getMenu_Name().equalsIgnoreCase(reportName)) {
                return model;
            }
        }
        return null;
    }

    public void getData(String report, String date) {
        // ... (getData implementation remains unchanged for Day Report API call)
        if (UtilityClass.isNetworkAvailable(this)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
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

    public void navigate(JSONArray jsonArray, String report, String date) {
        Intent intent = new Intent(ReportsActivity.this, ReportFragContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", jsonArray.toString());
        bundle.putString("date", date);
        bundle.putString("fragment", report);
        intent.putExtra("reportBundle", bundle);
        startActivity(intent);
    }

    private void loadMenuFromApi() {
        if (UtilityClass.isNetworkAvailable(this)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading dynamic reports...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // dynamicMenuList.clear(); // Clear list before loading

            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));

                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
                        jsonObject.put("tableName", "getDynamicReport");

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");

                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
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

                                                String Menu_Name = menuObject.get("Menu_Name").getAsString();
                                                String Menu_Icon = SharedPref.getTagImageUrl(ReportsActivity.this)
                                                        + "/" + menuObject.get("Menu_Icon").getAsString();
                                                String Menu_Page = SharedPref.getTagImageUrl(ReportsActivity.this)
                                                        + "/" + menuObject.get("Menu_Page").getAsString() + "?";
                                                Menu_Page += "sfcode=" + SharedPref.getSfCode(ReportsActivity.this)
                                                        + "&rSF=" + SharedPref.getHqCode(ReportsActivity.this)
                                                        + "&div_code=" + SharedPref.getDivisionCode(ReportsActivity.this)
                                                        + "&cMnth=" + TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_9)
                                                        + "&cYr=" + TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_12)
                                                        + "&doc_id=-1&IsDocView=0&cluster_code=-1";

                                                MenuModel menuModel = new MenuModel(Menu_Name, Menu_Icon, Menu_Page);

                                                // 🔑 FIX: Add both to the dynamic list and the adapter's display list
                                                dynamicMenuList.add(menuModel);
                                                reportTitles.add(Menu_Name);
                                            }
                                            // 🔑 FIX: Notify adapter to refresh with new data
                                            reportsAdapter.notifyDataSetChanged();
                                        } else {
                                            commonUtilsMethods.showToastMessage(ReportsActivity.this, "No Dynamic Reports Found");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                progressDialog.dismiss();
                                commonUtilsMethods.showToastMessage(ReportsActivity.this,
                                        getString(R.string.poor_connection) + " " + getString(R.string.please_try_again));
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
            commonUtilsMethods.showToastMessage(ReportsActivity.this, getString(R.string.no_network));
        }
    }

}