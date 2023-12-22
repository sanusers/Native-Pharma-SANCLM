package saneforce.sanclm.activity.approvals.leave;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityLeaveBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class LeaveApprovalActivity extends AppCompatActivity {
    public static String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode;
    ActivityLeaveBinding leaveBinding;
    ArrayList<LeaveModelList> leaveModelLists = new ArrayList<>();
    LeaveApprovalAdapter leaveApprovalAdapter;
    JSONObject jsonLeave = new JSONObject();
    ApiInterface api_interface;
    LoginResponse loginResponse;
    SQLite sqLite;
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leaveBinding = ActivityLeaveBinding.inflate(getLayoutInflater());
        setContentView(leaveBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());
        // progressDialog = CommonUtilsMethods.createProgressDialog(LeaveApprovalActivity.this);
        getRequiredData();
        CallApiLeave();

        leaveBinding.ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LeaveApprovalActivity.this, ApprovalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        leaveBinding.searchLeave.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void CallApiLeave() {
        progressDialog = CommonUtilsMethods.createProgressDialog(LeaveApprovalActivity.this);
        try {
            jsonLeave.put("tableName", "getlvlapproval");
            jsonLeave.put("sfcode", SfCode);
            jsonLeave.put("division_code", DivCode);
            jsonLeave.put("Rsf", SfCode);
            jsonLeave.put("sf_type", SfType);
            jsonLeave.put("Designation", Designation);
            jsonLeave.put("state_code", SubDivisionCode);
            Log.v("json_get_lvl_list", jsonLeave.toString());
        } catch (Exception ignored) {

        }


        Call<JsonArray> callGetLeaveApproval;
        callGetLeaveApproval = api_interface.getLeaveApprovalList(jsonLeave.toString());
        callGetLeaveApproval.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                assert response.body() != null;
                Log.v("jjj", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            leaveModelLists.add(new LeaveModelList(json.getString("LvID"), json.getString("Sf_Code"), json.getString("SFName"), json.getString("FDate"), json.getString("TDate"), json.getString("Reason"), json.getString("Address"), json.getString("LType"), json.getString("LAvail"), json.getString("No_of_Days")));
                        }
                        leaveApprovalAdapter = new LeaveApprovalAdapter(LeaveApprovalActivity.this, leaveModelLists);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        leaveBinding.rvLeaveList.setLayoutManager(mLayoutManager);
                        leaveBinding.rvLeaveList.setAdapter(leaveApprovalAdapter);
                    } catch (Exception e) {
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LeaveApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LeaveApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
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
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(LeaveApprovalActivity.this);
    }

    private void filter(String text) {
        ArrayList<LeaveModelList> filteredNames = new ArrayList<>();
        for (LeaveModelList s : leaveModelLists) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getLeave_type().contains(text.toLowerCase()) || s.getReason().contains(text.toLowerCase()) || s.getLeave_id().contains(text.toLowerCase()) || s.getAddr().contains(text.toLowerCase()) || s.getNo_of_days().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        leaveApprovalAdapter.filterList(filteredNames);
    }
}