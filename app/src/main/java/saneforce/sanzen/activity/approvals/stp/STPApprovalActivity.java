package saneforce.sanzen.activity.approvals.stp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanzen.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanzen.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanzen.activity.approvals.stp.adapter.STPApprovalAdapter;
import saneforce.sanzen.activity.approvals.stp.adapter.STPApprovalDetailedAdapter;
import saneforce.sanzen.activity.approvals.stp.model.STPDetailedModel;
import saneforce.sanzen.activity.approvals.stp.model.STPModelList;
import saneforce.sanzen.activity.approvals.tp.pojo.TpModelList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.STPDaySorter;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityStpApprovalBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class STPApprovalActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public String SelectedSfCode, stpDrNeed, stpChemNeed, planName, planSName, stpType;
    public ActivityStpApprovalBinding stpApprovalBinding;
    public int SelectedPosition;
    ArrayList<STPDetailedModel> stpDetailedModels = new ArrayList<>();
    STPApprovalDetailedAdapter stpApprovalDetailedAdapter;
    Dialog dialogReject;
    ApiInterface api_interface;
    ArrayList<STPModelList> stpModelLists = new ArrayList<>();
    STPApprovalAdapter stpApprovalAdapter;
    JSONObject jsonSTP = new JSONObject();
    ProgressDialog progressDialog = null;
    int totalPlannedDays = 0;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            stpApprovalBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                                       | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                                       | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                                       | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                                       | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stpApprovalBinding = ActivityStpApprovalBinding.inflate(getLayoutInflater());
        setContentView(stpApprovalBinding.getRoot());

        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        getRequiredData();

        if (stpType.equalsIgnoreCase("1")) {
            stpApprovalBinding.tagTotalPlannedDays.setText(getString(R.string.total_planed_count));
        } else {
            stpApprovalBinding.tagTotalPlannedDays.setText(getString(R.string.total_planed_days));
        }

        CallSTPListApi();
        stpApprovalBinding.ivBack.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent intent = new Intent(STPApprovalActivity.this, ApprovalsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        stpApprovalBinding.btnApproved.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                tpApproval();
            }
        });
        stpApprovalBinding.btnReject.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                DisplayDialog();
            }
        });

        stpApprovalBinding.searchStp.addTextChangedListener(new TextWatcher() {
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

    @Override
    public void onClick(DCRApprovalList dcrApprovalList, int pos) {
    }

    @Override
    public void onClickDcrDetail(DcrDetailModelList dcrDetailModelList) {
    }

    @Override
    public void onItemClick(TpModelList stpModelLists, int pos) {
    }

    @Override
    public void onSTPItemClick(STPModelList stpModelList, int position) {
        UtilityClass.hideKeyboard(this);
        progressDialog = CommonUtilsMethods.createProgressDialog(STPApprovalActivity.this);
        stpApprovalBinding.tvName.setText(stpModelList.getName());
        stpApprovalBinding.constraintStpListContent.setVisibility(View.VISIBLE);
        SelectedSfCode = stpModelList.getCode();
        stpApprovalAdapter.setSelectedSFCode(SelectedSfCode, position);
        SelectedPosition = position;
        GetDetailsApi();
    }

    private void CallSTPListApi() {
        progressDialog = CommonUtilsMethods.createProgressDialog(STPApprovalActivity.this);
        try {
            jsonSTP = CommonUtilsMethods.CommonObjectParameter(STPApprovalActivity.this);
            jsonSTP.put("tableName", "getstpapproval");
            jsonSTP.put("sfcode", SharedPref.getSfCode(this));
            jsonSTP.put("division_code", SharedPref.getDivisionCode(this));
            jsonSTP.put("Rsf", SharedPref.getHqCode(this));
            Log.v("json_getSTPList", jsonSTP.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetTPApproval = api_interface.getJSONElement(SharedPref.getCallApiUrl(STPApprovalActivity.this), mapString, jsonSTP.toString());

        callGetTPApproval.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("jjj", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        stpModelLists.clear();
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            stpApprovalBinding.constraintSelectedDetails.setVisibility(View.VISIBLE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                stpModelLists.add(new STPModelList(json.getString("SFName"), json.getString("Sf_Code"), json.getString("Division_Code")));
                            }
                            stpApprovalAdapter = new STPApprovalAdapter(STPApprovalActivity.this, stpModelLists, STPApprovalActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            stpApprovalBinding.rvStpList.setLayoutManager(mLayoutManager);
                            stpApprovalBinding.rvStpList.setAdapter(stpApprovalAdapter);
                        } else {
                            stpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                            commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_data_found));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    stpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                t.printStackTrace();
                stpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
            }
        });
    }

    private void filter(String text) {
        ArrayList<STPModelList> filteredNames = new ArrayList<>();
        for (STPModelList s : stpModelLists) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        stpApprovalAdapter.filterList(filteredNames);
    }

    private void getRequiredData() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STP_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                planName = jsonObject.getString("Plan_Name");
                planSName = jsonObject.getString("Plan_SName");
            }
            stpDrNeed = SharedPref.getDrNeed(this);
            stpChemNeed = SharedPref.getChmNeed(this);
            stpType = SharedPref.getStpType(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetDetailsApi() {
        try {
            jsonSTP = CommonUtilsMethods.CommonObjectParameter(STPApprovalActivity.this);
            jsonSTP.put("tableName", "getstp_app_details");
            jsonSTP.put("sfcode", SelectedSfCode);
            jsonSTP.put("division_code", SharedPref.getDivisionCode(this));
            jsonSTP.put("Rsf", SelectedSfCode);
            Log.v("json_getSTPDetailedList", jsonSTP.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/stp");
        Call<JsonElement> callGetTPADetailedList = api_interface.getJSONElement(SharedPref.getCallApiUrl(STPApprovalActivity.this), mapString, jsonSTP.toString());

        callGetTPADetailedList.enqueue(new Callback<JsonElement>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    stpDetailedModels.clear();
                    totalPlannedDays = 0;
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        Log.d("STP", "onResponse: " + jsonArray);
                        if (jsonArray.length() > 0) {
                            totalPlannedDays = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                stpDetailedModels.add(new STPDetailedModel(json.getString("Trans_No"), json.getString("sf_code"), json.getString("Division_Code"), json.getString("Day_Plan_Name"), json.getString("Day_Plan_ShortName"), json.getString("Day_Plan_Code"), json.getString("Patch_Code"), json.getString("Patch_Name"), json.getString("Dr_Code"), json.getString("Dr_Name"), json.getString("Chem_Code"), json.getString("Chem_Name"), json.getString("Active_Flag"), json.getString("Created_Date")));
                            }
                            if (!SharedPref.getStpType(STPApprovalActivity.this).equalsIgnoreCase("1")) {
                                STPDaySorter.sortDays(stpDetailedModels, STPDetailedModel::getDayPlanShortName);
                            }
                            stpApprovalBinding.constraintSelectedDetails.setVisibility(View.VISIBLE);
                            stpApprovalBinding.tvTotalPlannedDays.setText(String.valueOf(totalPlannedDays));
                            stpApprovalDetailedAdapter = new STPApprovalDetailedAdapter(STPApprovalActivity.this, stpDetailedModels);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            stpApprovalBinding.rvStpContentList.setLayoutManager(mLayoutManager);
                            stpApprovalBinding.rvStpContentList.setAdapter(stpApprovalDetailedAdapter);
                        } else {
                            commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_data_found));
                        }
                    } catch (Exception e) {
                        Log.v("tpDetailedList", "---" + e);
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
            }
        });
    }

    private void tpApproval() {
        progressDialog = CommonUtilsMethods.createProgressDialog(STPApprovalActivity.this);
        try {
            jsonSTP = CommonUtilsMethods.CommonObjectParameter(STPApprovalActivity.this);
            jsonSTP.put("tableName", "stp_action");
            jsonSTP.put("sfcode", SelectedSfCode);
            jsonSTP.put("approvedby", SharedPref.getSfCode(this));
            jsonSTP.put("approvedbyname", SharedPref.getSfName(this));
            jsonSTP.put("division_code", SharedPref.getDivisionCode(this));
            if (SharedPref.getOneBuild(STPApprovalActivity.this).equalsIgnoreCase("0")) {
                jsonSTP.put("StpFlag", "2");
            } else {
                jsonSTP.put("StpFlag", "0");
            }
            jsonSTP.put("rejectreason", "");
            jsonSTP.put("actionDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
            Log.v("json_stp_Approved", jsonSTP.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/stp");
        Call<JsonElement> callApproveTp = api_interface.getJSONElement(SharedPref.getCallApiUrl(STPApprovalActivity.this), mapString, jsonSTP.toString());
        callApproveTp.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.approved_successfully));
                            removeSelectedData();
                            ApprovalsActivity.STPCount--;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
            }
        });
    }

    private void DisplayDialog() {
        dialogReject = new Dialog(STPApprovalActivity.this);
        dialogReject.setContentView(R.layout.popup_reject);
        Objects.requireNonNull(dialogReject.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReject.setCancelable(false);

        ImageView iv_close = dialogReject.findViewById(R.id.img_close);
        EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
        Button btn_cancel = dialogReject.findViewById(R.id.btn_cancel);
        Button btn_reject = dialogReject.findViewById(R.id.btn_reject);
        ed_reason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_reason, 300)});
        btn_cancel.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ed_reason.setText("");
                dialogReject.dismiss();
            }
        });

        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ed_reason.setText("");
                dialogReject.dismiss();
            }
        });

        btn_reject.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (!TextUtils.isEmpty(ed_reason.getText().toString())) {
                    stpReject(ed_reason.getText().toString());
                } else {
                    commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.toast_enter_reason_for_reject));
                }
            }
        });
        dialogReject.show();
    }

    private void stpReject(String reason) {
        progressDialog = CommonUtilsMethods.createProgressDialog(STPApprovalActivity.this);
        try {
            jsonSTP = CommonUtilsMethods.CommonObjectParameter(STPApprovalActivity.this);
            jsonSTP.put("tableName", "stp_action");
            jsonSTP.put("sfcode", SelectedSfCode);
            jsonSTP.put("approvedby", SharedPref.getSfCode(this));
            jsonSTP.put("approvedbyname", SharedPref.getSfName(this));
            jsonSTP.put("division_code", SharedPref.getDivisionCode(this));
            if (SharedPref.getOneBuild(STPApprovalActivity.this).equalsIgnoreCase("0")) {
                jsonSTP.put("StpFlag", "3");
            } else {
                jsonSTP.put("StpFlag", "1");
            }
            jsonSTP.put("rejectreason", reason);
            jsonSTP.put("actionDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
            Log.v("json_stp_Reject", jsonSTP.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/stp");
        Call<JsonElement> callRejectTp = api_interface.getJSONElement(SharedPref.getCallApiUrl(STPApprovalActivity.this), mapString, jsonSTP.toString());
        callRejectTp.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.rejected_successfully));
                            dialogReject.dismiss();
                            removeSelectedData();
                            ApprovalsActivity.STPCount--;
                        }
                    } catch (Exception e) {
                        dialogReject.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    dialogReject.dismiss();
                    commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(STPApprovalActivity.this, getString(R.string.no_network));
                dialogReject.dismiss();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeSelectedData() {
        stpApprovalAdapter.removeAt(SelectedPosition);
        stpApprovalAdapter.notifyDataSetChanged();
        for (int i = 0; i < stpModelLists.size(); i++) {
            if (stpModelLists.get(i).getCode().equalsIgnoreCase(SelectedSfCode)) {
                stpModelLists.remove(i);
                break;
            }
        }
        stpApprovalBinding.constraintStpListContent.setVisibility(View.GONE);
        SelectedSfCode = "";
    }


}