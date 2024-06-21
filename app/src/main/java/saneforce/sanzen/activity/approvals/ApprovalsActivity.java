package saneforce.sanzen.activity.approvals;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityApprovalsBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;


import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ApprovalsActivity extends AppCompatActivity {
    public static int DcrCount = 0, TpCount = 0, LeaveCount = 0, DeviationCount = 0, GeoTagCount = 0;
    ActivityApprovalsBinding approvalsBinding;
    JSONObject jsonGetCount = new JSONObject();
    ApiInterface api_interface;

    ArrayList<AdapterModel> list_approvals = new ArrayList<>();
    AdapterApprovals adapterApprovals;
    ProgressDialog progressDialog = null;
    CommonUtilsMethods commonUtilsMethods;

    @Override
    protected void onResume() {
        super.onResume();
    }


    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            approvalsBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("date", HomeDashBoard.selectedDate.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        approvalsBinding = ActivityApprovalsBinding.inflate(getLayoutInflater());
        setContentView(approvalsBinding.getRoot());

        if(savedInstanceState != null) {
            HomeDashBoard.selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                CommonAlertBox.permissionChangeAlert(this);
            }
        }

        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        if (SharedPref.getApprovalsCounts(ApprovalsActivity.this).equalsIgnoreCase("false")) {
            CallListCountAPI();
        } else {
            AssignCountValues();
        }


        approvalsBinding.ivBack.setOnClickListener(view -> {
            if(!SharedPref.getDesig(ApprovalsActivity.this).equalsIgnoreCase("MR")&& SharedPref.getApprMandatoryNeed(ApprovalsActivity.this).equalsIgnoreCase("0")) {
                 SharedPref.setApprovalSKIPDate(ApprovalsActivity.this, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
            }
            getOnBackPressedDispatcher().onBackPressed();
            });

        // approvalsBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(ApprovalsActivity.this, HomeDashBoard.class)));
    }

    private void CallListCountAPI() {
        progressDialog = CommonUtilsMethods.createProgressDialog(ApprovalsActivity.this);

        try {
            jsonGetCount.put("tableName", "getapprovalcheck");
            jsonGetCount.put("sfcode", SharedPref.getSfCode(this));
            jsonGetCount.put("division_code", SharedPref.getDivisionCode(this));
            jsonGetCount.put("Rsf", SharedPref.getHqCode(this));
            jsonGetCount.put("sf_type", SharedPref.getSfType(this));
            jsonGetCount.put("Designation", SharedPref.getDesig(this));
            jsonGetCount.put("state_code", SharedPref.getStateCode(this));
            jsonGetCount.put("subdivision_code", SharedPref.getSubdivisionCode(this));
            jsonGetCount.put("Tp_need", SharedPref.getTpNeed(this));
            jsonGetCount.put("geotag_need", SharedPref.getGeotagNeed(this));
            jsonGetCount.put("TPdev_need", SharedPref.getTpdcrMgrappr(this));
            jsonGetCount.put("versionNo", getString(R.string.app_version));
            jsonGetCount.put("mod", Constants.APP_MODE);
            jsonGetCount.put("Device_version", Build.VERSION.RELEASE);
            jsonGetCount.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonGetCount.put("AppName", getString(R.string.str_app_name));
            jsonGetCount.put("language", SharedPref.getSelectedLanguage(this));

            Log.v("json_get_full_dcr_list", jsonGetCount.toString());

        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetCountApprovals = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonGetCount.toString());
        callGetCountApprovals.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("counts", "-0-" + response.body());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        SharedPref.setApprovalsCounts(ApprovalsActivity.this, "true");
                        JSONObject jsonObject1 = new JSONObject(response.body().toString());
                        JSONArray jsonArray = jsonObject1.getJSONArray("apprCount");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonCounts = jsonArray.getJSONObject(i);
                            if (jsonCounts.has("dcrappr_count"))
                                DcrCount = jsonCounts.getInt("dcrappr_count");
                            if (jsonCounts.has("tpappr_count"))
                                TpCount = jsonCounts.getInt("tpappr_count");
                            if (jsonCounts.has("leaveappr_count"))
                                LeaveCount = jsonCounts.getInt("leaveappr_count");
                            if (jsonCounts.has("devappr_count"))
                                DeviationCount = jsonCounts.getInt("devappr_count");
                            if (jsonCounts.has("geotag_count"))
                                GeoTagCount = jsonCounts.getInt("geotag_count");
                        }
                        AssignCountValues();
                    } catch (Exception e) {
                        commonUtilsMethods.showToastMessage(ApprovalsActivity.this,getString(R.string.something_wrong));
                        Log.v("counts", "-error-" + e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                commonUtilsMethods.showToastMessage(getApplicationContext(),getString(R.string.toast_response_failed));
                progressDialog.dismiss();
            }
        });
    }

    private void AssignCountValues() {
        Log.v("counts", "---" + DcrCount + "---" + TpCount + "---" + LeaveCount + "---" + DeviationCount + "---" + GeoTagCount);
        list_approvals.add(new AdapterModel(getResources().getString(R.string.leave_approvals), String.valueOf(LeaveCount)));
        if (SharedPref.getTpNeed(context).equalsIgnoreCase("0")) {
            list_approvals.add(new AdapterModel(getResources().getString(R.string.tp_approvals), String.valueOf(TpCount)));
        }
        list_approvals.add(new AdapterModel(getResources().getString(R.string.dcr_approvals), String.valueOf(DcrCount)));
        if (SharedPref.getTpdcrMgrappr(context).equalsIgnoreCase("0")) {
            list_approvals.add(new AdapterModel(getResources().getString(R.string.tp_deviation), String.valueOf(DeviationCount)));
        }
        if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
            list_approvals.add(new AdapterModel(getResources().getString(R.string.geo_tagging), String.valueOf(GeoTagCount)));
        }

        adapterApprovals = new AdapterApprovals(ApprovalsActivity.this, list_approvals);
        approvalsBinding.rvApprovalList.setItemAnimator(new DefaultItemAnimator());
        approvalsBinding.rvApprovalList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4, GridLayoutManager.VERTICAL, false));
        approvalsBinding.rvApprovalList.setAdapter(adapterApprovals);
    }

}