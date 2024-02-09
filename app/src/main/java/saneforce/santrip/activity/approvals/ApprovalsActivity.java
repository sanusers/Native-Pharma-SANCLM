package saneforce.santrip.activity.approvals;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.tourPlan.TourPlanActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.ActivityApprovalsBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class ApprovalsActivity extends AppCompatActivity {
    public static int DcrCount = 0, TpCount = 0, LeaveCount = 0, DeviationCount = 0, GeoTagCount = 0;
    public static String JwCaption, ClusterCaption, DrCaption, ChemistCaption, StockistCaption, UnDrCaption, CIPCaption, HosCaption, DrNeed, ChemistNeed, CipNeed, StockistNeed, UnDrNeed, HospNeed;
    ActivityApprovalsBinding approvalsBinding;
    JSONObject jsonGetCount = new JSONObject();
    ApiInterface api_interface;
    LoginResponse loginResponse;
    SQLite sqLite;
    String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode, TpDevNeed = "1", GeoTagNeed = "1", TpNeed = "1", TodayPlanSfCode;
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
            approvalsBinding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        approvalsBinding = ActivityApprovalsBinding.inflate(getLayoutInflater());
        setContentView(approvalsBinding.getRoot());

        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getRequiredData();
        if (SharedPref.getApprovalsCounts(ApprovalsActivity.this).equalsIgnoreCase("false")) {
            CallListCountAPI();
        } else {
            AssignCountValues();
        }

        approvalsBinding.ivBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        // approvalsBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(ApprovalsActivity.this, HomeDashBoard.class)));
    }

    private void CallListCountAPI() {
        progressDialog = CommonUtilsMethods.createProgressDialog(ApprovalsActivity.this);

        try {
            jsonGetCount.put("tableName", "getapprovalcheck");
            jsonGetCount.put("sfcode", SfCode);
            jsonGetCount.put("division_code", DivCode);
            jsonGetCount.put("Rsf", TodayPlanSfCode);
            jsonGetCount.put("sf_type", SfType);
            jsonGetCount.put("Designation", Designation);
            jsonGetCount.put("state_code", StateCode);
            jsonGetCount.put("subdivision_code", SubDivisionCode);
            jsonGetCount.put("Tp_need", TpNeed);
            jsonGetCount.put("geotag_need", GeoTagNeed);
            jsonGetCount.put("TPdev_need", TpDevNeed);

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
                        commonUtilsMethods.ShowToast(getApplicationContext(),getString(R.string.something_wrong), 100);
                        Log.v("counts", "-error-" + e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                commonUtilsMethods.ShowToast(getApplicationContext(),getString(R.string.toast_response_failed), 100);
                progressDialog.dismiss();
            }
        });
    }

    private void AssignCountValues() {
        Log.v("counts", "---" + DcrCount + "---" + TpCount + "---" + LeaveCount + "---" + DeviationCount + "---" + GeoTagCount);
        list_approvals.add(new AdapterModel(getResources().getString(R.string.leave_approvals), String.valueOf(LeaveCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.tp_approvals), String.valueOf(TpCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.dcr_approvals), String.valueOf(DcrCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.tp_deviation), String.valueOf(DeviationCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.geo_tagging), String.valueOf(GeoTagCount)));

        adapterApprovals = new AdapterApprovals(ApprovalsActivity.this, list_approvals);
        approvalsBinding.rvApprovalList.setItemAnimator(new DefaultItemAnimator());
        approvalsBinding.rvApprovalList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4, GridLayoutManager.VERTICAL, false));
        approvalsBinding.rvApprovalList.setAdapter(adapterApprovals);
    }

    private void getRequiredData() {
        try {
            loginResponse = new LoginResponse();
            loginResponse = sqLite.getLoginData();

            SfType = loginResponse.getSf_type();
            SfCode = loginResponse.getSF_Code();
            SfName = loginResponse.getSF_Name();
            DivCode = loginResponse.getDivision_Code();
            SubDivisionCode = loginResponse.getSubdivision_code();
            Designation = loginResponse.getDesig();
            StateCode = loginResponse.getState_Code();

            ClusterCaption = loginResponse.getCluster_cap();
            DrCaption = loginResponse.getDrCap();
            ChemistCaption = loginResponse.getChmCap();
            StockistCaption = loginResponse.getStkCap();
            UnDrCaption = loginResponse.getNLCap();
            CIPCaption = loginResponse.getCIP_Caption();
            HosCaption = loginResponse.getHosp_caption();

            CipNeed = loginResponse.getCip_need();
            DrNeed = loginResponse.getDrNeed();
            ChemistNeed = loginResponse.getChmNeed();
            StockistNeed = loginResponse.getStkNeed();
            UnDrNeed = loginResponse.getUNLNeed();
            HospNeed = loginResponse.getHosp_need();

            TpNeed = loginResponse.getTp_need();
            TpDevNeed = loginResponse.getTPDCR_MGRAppr();
            GeoTagNeed = loginResponse.getGeoTagApprovalNeed();

            TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(ApprovalsActivity.this);


        } catch (Exception ignored) {
        }
    }
}