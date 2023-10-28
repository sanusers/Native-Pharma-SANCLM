package saneforce.sanclm.activity.approvals;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.leave.LeaveApprovalAdapter;
import saneforce.sanclm.activity.approvals.leave.LeaveModelList;
import saneforce.sanclm.activity.approvals.tp.TpApprovalAdapter;
import saneforce.sanclm.activity.approvals.tp.TpModelList;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityApprovalsBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class ApprovalsActivity extends AppCompatActivity {
    ActivityApprovalsBinding approvalsBinding;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<TpModelList> tpModelLists = new ArrayList<>();
    ArrayList<LeaveModelList> leaveModelLists = new ArrayList<>();
    TpApprovalAdapter tpApprovalAdapter;
    LeaveApprovalAdapter leaveApprovalAdapter;
    JSONObject jsonTp = new JSONObject();
    JSONObject jsonLeave = new JSONObject();
    ApiInterface api_interface;
    LoginResponse loginResponse;
    SQLite sqLite;
    String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode;

    @Override
    protected void onResume() {
        super.onResume();
        commonUtilsMethods.FullScreencall();
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

        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreencall();
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());

        getRequiredData();

        approvalsBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(ApprovalsActivity.this, HomeDashBoard.class)));

        approvalsBinding.llTpApprovals.setOnClickListener(view -> {
            approvalsBinding.tpArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.greater_than_black));
            approvalsBinding.leaveArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.dcrArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.deviateArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.geoTagArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));

            approvalsBinding.constraintTpList.setVisibility(View.VISIBLE);
            approvalsBinding.rvLeaveList.setVisibility(View.GONE);
            approvalsBinding.constraintDcrList.setVisibility(View.GONE);
            approvalsBinding.rvTagList.setVisibility(View.GONE);

            CallApiTp();
        });

        approvalsBinding.llLeaveApproval.setOnClickListener(view -> {
            approvalsBinding.tpArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.leaveArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.greater_than_black));
            approvalsBinding.dcrArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.deviateArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.geoTagArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));

            approvalsBinding.constraintTpList.setVisibility(View.GONE);
            approvalsBinding.rvLeaveList.setVisibility(View.VISIBLE);
            approvalsBinding.constraintDcrList.setVisibility(View.GONE);
            approvalsBinding.rvTagList.setVisibility(View.GONE);

            CallApiLeave();
        });

        approvalsBinding.llDcrApproval.setOnClickListener(view -> {
            approvalsBinding.tpArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.leaveArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.dcrArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.greater_than_black));
            approvalsBinding.deviateArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.geoTagArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));

            approvalsBinding.constraintTpList.setVisibility(View.GONE);
            approvalsBinding.rvLeaveList.setVisibility(View.GONE);
            approvalsBinding.constraintDcrList.setVisibility(View.VISIBLE);
            approvalsBinding.rvTagList.setVisibility(View.GONE);
        });

        approvalsBinding.llDeviateApproval.setOnClickListener(view -> {
            approvalsBinding.tpArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.leaveArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.dcrArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.deviateArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.greater_than_black));
            approvalsBinding.geoTagArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));

            approvalsBinding.constraintTpList.setVisibility(View.GONE);
            approvalsBinding.rvLeaveList.setVisibility(View.GONE);
            approvalsBinding.constraintDcrList.setVisibility(View.GONE);
            approvalsBinding.rvTagList.setVisibility(View.VISIBLE);
        });

        approvalsBinding.llGeoTagApproval.setOnClickListener(view -> {
            approvalsBinding.tpArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.leaveArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.dcrArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.deviateArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.arrow_down));
            approvalsBinding.geoTagArrow.setImageDrawable(ContextCompat.getDrawable(ApprovalsActivity.this, R.drawable.greater_than_black));

            approvalsBinding.constraintTpList.setVisibility(View.GONE);
            approvalsBinding.rvLeaveList.setVisibility(View.GONE);
            approvalsBinding.constraintDcrList.setVisibility(View.GONE);
            approvalsBinding.rvTagList.setVisibility(View.VISIBLE);
        });
    }

    private void getRequiredData() {

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData(true);

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();
    }

    private void CallApiLeave() {
        try {
            jsonLeave.put("tableName", "getlvlapproval");
            jsonLeave.put("sfcode", SfCode);
            jsonLeave.put("division_code", DivCode);
            jsonLeave.put("Rsf", SfCode);
            jsonLeave.put("sf_type", SfType);
            jsonLeave.put("Designation", Designation);
            jsonLeave.put("state_code", SubDivisionCode);
            Log.v("json_get_lvl_list", jsonLeave.toString());
        } catch (Exception e) {

        }

        Call<JsonArray> callGetLeaveApproval = null;
        callGetLeaveApproval = api_interface.getLeaveApprovalList(jsonLeave.toString());
        callGetLeaveApproval.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.v("jjj", response.body().toString() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            leaveModelLists.add(new LeaveModelList(json.getString("LvID"), json.getString("SFName"), json.getString("FDate"), json.getString("TDate"), json.getString("Reason"), json.getString("Address"), json.getString("LType"), json.getString("LAvail"), json.getString("No_of_Days")));
                        }
                        leaveApprovalAdapter = new LeaveApprovalAdapter(ApprovalsActivity.this, leaveModelLists);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        approvalsBinding.rvLeaveList.setLayoutManager(mLayoutManager);
                        approvalsBinding.rvLeaveList.setAdapter(leaveApprovalAdapter);
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(ApprovalsActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(ApprovalsActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CallApiTp() {
        try {
            jsonTp.put("tableName", "gettpapproval");
            jsonTp.put("sfcode", SfCode);
            jsonTp.put("division_code", DivCode);
            jsonTp.put("Rsf", SfCode);
            jsonTp.put("sf_type", SfType);
            jsonTp.put("Designation", Designation);
            jsonTp.put("state_code", SubDivisionCode);
            Log.v("json_getTpList", jsonTp.toString());
        } catch (Exception e) {

        }
        Call<JsonArray> callGetTPApproval = null;
        callGetTPApproval = api_interface.getTpApprovalList(jsonTp.toString());
        callGetTPApproval.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.v("jjj", response.body().toString() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                tpModelLists.add(new TpModelList(json.getString("Sf_Code"), json.getString("SFName"), json.getString("Mnth"), json.getString("Yr"), json.getString("Mn")));
                            }
                            tpApprovalAdapter = new TpApprovalAdapter(ApprovalsActivity.this, tpModelLists);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            approvalsBinding.rvTourPlanList.setLayoutManager(mLayoutManager);
                            approvalsBinding.rvTourPlanList.setItemAnimator(new DefaultItemAnimator());
                            approvalsBinding.rvTourPlanList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                            approvalsBinding.rvTourPlanList.setAdapter(tpApprovalAdapter);
                        } else {
                            Toast.makeText(ApprovalsActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(ApprovalsActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(ApprovalsActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}