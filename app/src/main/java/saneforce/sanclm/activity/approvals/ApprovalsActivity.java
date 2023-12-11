package saneforce.sanclm.activity.approvals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityApprovalsBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class ApprovalsActivity extends AppCompatActivity {
    public static int DcrCount = 0, TpCount = 0, LeaveCount = 0, DeviationCount = 0;
    ActivityApprovalsBinding approvalsBinding;
    JSONObject jsonGetCount = new JSONObject();
    ApiInterface api_interface;
    LoginResponse loginResponse;
    SQLite sqLite;
    String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode;
    ArrayList<AdapterModel> list_approvals = new ArrayList<>();
    AdapterApprovals adapterApprovals;
    ProgressDialog progressDialog = null;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        approvalsBinding = ActivityApprovalsBinding.inflate(getLayoutInflater());
        setContentView(approvalsBinding.getRoot());

        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getRequiredData();
        if (SharedPref.getApprovalsCounts(ApprovalsActivity.this).equalsIgnoreCase("false")) {
            CallListCountAPI();
        } else {
            AssignCountValues();
        }

        approvalsBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(ApprovalsActivity.this, HomeDashBoard.class)));
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
            Log.v("json_get_full_dcr_list", jsonGetCount.toString());

        } catch (Exception ignored) {

        }

        Call<JsonObject> callGetCountApprovals;
        callGetCountApprovals = api_interface.getListCountApprovals(jsonGetCount.toString());

        callGetCountApprovals.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                assert response.body() != null;
                Log.v("counts", "-0-" + response.body());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        SharedPref.setApprovalsCounts(ApprovalsActivity.this, "true");
                        JSONObject jsonObject1 = new JSONObject(response.body().toString());
                        JSONArray jsonArray = jsonObject1.getJSONArray("apprCount");

                        JSONObject jsonDcr = jsonArray.getJSONObject(0);
                        DcrCount = jsonDcr.getInt("dcrappr_count");
                        JSONObject jsonTp = jsonArray.getJSONObject(1);
                        TpCount = jsonTp.getInt("tpappr_count");
                        JSONObject jsonLeave = jsonArray.getJSONObject(2);
                        LeaveCount = jsonLeave.getInt("leaveappr_count");
                        JSONObject jsonDeviation = jsonArray.getJSONObject(3);
                        DeviationCount = jsonDeviation.getInt("devappr_count");
                        AssignCountValues();
                    } catch (Exception e) {
                        Log.v("counts", "-error-" + e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void AssignCountValues() {
        Log.v("counts", "---" + DcrCount + "---" + TpCount + "---" + LeaveCount + "---" + DeviationCount);
        list_approvals.add(new AdapterModel(getResources().getString(R.string.leave_approvals), String.valueOf(LeaveCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.tp_approvals), String.valueOf(TpCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.dcr_approvals), String.valueOf(DcrCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.tp_deviation), String.valueOf(DeviationCount)));
        list_approvals.add(new AdapterModel(getResources().getString(R.string.geo_tagging), "0"));

        adapterApprovals = new AdapterApprovals(ApprovalsActivity.this, list_approvals);
        approvalsBinding.rvApprovalList.setItemAnimator(new DefaultItemAnimator());
        approvalsBinding.rvApprovalList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4, GridLayoutManager.VERTICAL, false));
        approvalsBinding.rvApprovalList.setAdapter(adapterApprovals);
    }

    private void getRequiredData() {

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(ApprovalsActivity.this);
    }
}