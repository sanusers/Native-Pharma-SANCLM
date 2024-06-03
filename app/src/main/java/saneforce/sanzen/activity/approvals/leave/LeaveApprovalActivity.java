package saneforce.sanzen.activity.approvals.leave;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityLeaveBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;

import saneforce.sanzen.storage.SharedPref;

public class LeaveApprovalActivity extends AppCompatActivity {
    ActivityLeaveBinding leaveBinding;
    ArrayList<LeaveModelList> leaveModelLists = new ArrayList<>();
    LeaveApprovalAdapter leaveApprovalAdapter;
    JSONObject jsonLeave = new JSONObject();
    ApiInterface api_interface;

    ProgressDialog progressDialog = null;
    CommonUtilsMethods commonUtilsMethods;


    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            leaveBinding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leaveBinding = ActivityLeaveBinding.inflate(getLayoutInflater());
        setContentView(leaveBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

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
            jsonLeave.put("sfcode", SharedPref.getSfCode(this));
            jsonLeave.put("division_code", SharedPref.getDivisionCode(this));
            jsonLeave.put("Rsf", SharedPref.getHqCode(this));
            jsonLeave.put("sf_type", SharedPref.getSfType(this));
            jsonLeave.put("Designation", SharedPref.getDesig(this));
            jsonLeave.put("state_code", SharedPref.getStateCode(this));
            jsonLeave.put("subdivision_code", SharedPref.getSubdivisionCode(this));
            jsonLeave.put("versionNo", getString(R.string.app_version));
            jsonLeave.put("mod", Constants.APP_MODE);
            jsonLeave.put("Device_version", Build.VERSION.RELEASE);
            jsonLeave.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonLeave.put("AppName", getString(R.string.str_app_name));
            jsonLeave.put("language", SharedPref.getSelectedLanguage(this));
            Log.v("json_get_lvl_list", jsonLeave.toString());
        } catch (Exception ignored) {

        }


        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetLeaveApproval = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonLeave.toString());
        callGetLeaveApproval.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
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
                    commonUtilsMethods.showToastMessage(getApplicationContext(),getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(getApplicationContext(),getString(R.string.toast_response_failed));
            }
        });
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