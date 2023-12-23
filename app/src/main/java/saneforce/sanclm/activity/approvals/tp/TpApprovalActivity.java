package saneforce.sanclm.activity.approvals.tp;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanclm.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.activity.approvals.tp.adapter.TpApprovalAdapter;
import saneforce.sanclm.activity.approvals.tp.adapter.TpApprovalDetailedAdapter;
import saneforce.sanclm.activity.approvals.tp.pojo.TpDetailedModel;
import saneforce.sanclm.activity.approvals.tp.pojo.TpModelList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityTpApprovalBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class TpApprovalActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public static String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode, SelectedSfCode, SelectedMonthYear, SelectedMonth, SelectedYear, SelectedDay = "";
    @SuppressLint("StaticFieldLeak")
    public static ActivityTpApprovalBinding tpApprovalBinding;
    public static int SelectedPosition;
    ArrayList<TpDetailedModel> tpDetailedModelsList = new ArrayList<>();
    TpApprovalDetailedAdapter tpApprovalDetailedAdapter;
    LoginResponse loginResponse;
    SQLite sqLite;
    Dialog dialogReject;
    ApiInterface api_interface;
    ArrayList<TpModelList> tpModelLists = new ArrayList<>();
    TpApprovalAdapter tpApprovalAdapter;
    JSONObject jsonTp = new JSONObject();
    ProgressDialog progressDialog = null;
    int totalPlannedDays = 0, totalWeekOffDays = 0, totalHolidays = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tpApprovalBinding = ActivityTpApprovalBinding.inflate(getLayoutInflater());
        setContentView(tpApprovalBinding.getRoot());
        sqLite = new SQLite(this);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getRequiredData();
        CallTpListApi();
        tpApprovalBinding.ivBack.setOnClickListener(view -> {
            SelectedSfCode = "";
            SelectedMonth = "";
            SelectedYear = "";
            SelectedDay = "";
            Intent intent = new Intent(TpApprovalActivity.this, ApprovalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        tpApprovalBinding.btnApproved.setOnClickListener(v -> tpApproval());
        tpApprovalBinding.btnReject.setOnClickListener(v -> DisplayDialog());


        tpApprovalBinding.searchTp.addTextChangedListener(new TextWatcher() {
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

    private void tpApproval() {

        progressDialog = CommonUtilsMethods.createProgressDialog(getApplicationContext());
        try {
            jsonTp = new JSONObject();
            jsonTp.put("tableName", "savetpapproval");
            jsonTp.put("sfcode", SfCode);
            jsonTp.put("Month", SelectedMonth);
            jsonTp.put("Year", SelectedYear);
            jsonTp.put("division_code", DivCode);
            jsonTp.put("Rsf", SelectedSfCode);
            jsonTp.put("sf_type", SfType);
            jsonTp.put("Designation", Designation);
            jsonTp.put("state_code", StateCode);
            jsonTp.put("subdivision_code", SubDivisionCode);

            Log.v("json_tp_Approved", jsonTp.toString());
        } catch (Exception ignored) {

        }

        Call<JsonObject> callApproveTp;
        callApproveTp = api_interface.saveApprovedRejectTp(jsonTp.toString());

        callApproveTp.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(TpApprovalActivity.this, "Approved Successfully", Toast.LENGTH_SHORT).show();
                            removeSelectedData();
                            ApprovalsActivity.TpCount--;
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DisplayDialog() {
        dialogReject = new Dialog(TpApprovalActivity.this);
        dialogReject.setContentView(R.layout.popup_reject);
        Objects.requireNonNull(dialogReject.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReject.setCancelable(false);

        ImageView iv_close = dialogReject.findViewById(R.id.img_close);
        EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
        Button btn_cancel = dialogReject.findViewById(R.id.btn_cancel);
        Button btn_reject = dialogReject.findViewById(R.id.btn_reject);
        ed_reason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_reason)});
        btn_cancel.setOnClickListener(view1 -> {
            ed_reason.setText("");
            dialogReject.dismiss();
        });

        iv_close.setOnClickListener(view12 -> {
            ed_reason.setText("");
            dialogReject.dismiss();
        });

        btn_reject.setOnClickListener(view13 -> {
            if (!TextUtils.isEmpty(ed_reason.getText().toString())) {
                tpReject(ed_reason.getText().toString());
            } else {
                Toast.makeText(TpApprovalActivity.this, getResources().getText(R.string.toast_enter_reason_for_reject), Toast.LENGTH_SHORT).show();
            }
        });
        dialogReject.show();
    }


    private void tpReject(String reason) {
        progressDialog = CommonUtilsMethods.createProgressDialog(getApplicationContext());
        try {
            jsonTp = new JSONObject();
            jsonTp.put("tableName", "savetpreject");
            jsonTp.put("sfcode", SfCode);
            jsonTp.put("Month", SelectedMonth);
            jsonTp.put("Year", SelectedYear);
            jsonTp.put("reason", reason);
            jsonTp.put("division_code", DivCode);
            jsonTp.put("Rsf", SelectedSfCode);
            jsonTp.put("sf_type", SfType);
            jsonTp.put("Designation", Designation);
            jsonTp.put("state_code", StateCode);
            jsonTp.put("subdivision_code", SubDivisionCode);

            Log.v("json_tp_Reject", jsonTp.toString());
        } catch (Exception ignored) {

        }
        Call<JsonObject> callRejectTp;
        callRejectTp = api_interface.saveApprovedRejectTp(jsonTp.toString());
        callRejectTp.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(TpApprovalActivity.this, "Rejected Successfully", Toast.LENGTH_SHORT).show();
                            dialogReject.dismiss();
                            removeSelectedData();
                            ApprovalsActivity.TpCount--;
                        }
                    } catch (Exception e) {
                        dialogReject.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    dialogReject.dismiss();
                    Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                dialogReject.dismiss();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeSelectedData() {
        tpApprovalAdapter.removeAt(SelectedPosition);
        tpApprovalAdapter.notifyDataSetChanged();
        for (int i = 0; i < tpModelLists.size(); i++) {
            if (tpModelLists.get(i).getCode().equalsIgnoreCase(SelectedSfCode) && tpModelLists.get(i).getMn().equalsIgnoreCase(SelectedMonth) && tpModelLists.get(i).getYear().equalsIgnoreCase(SelectedSfCode)) {
                tpModelLists.remove(i);
                break;
            }
        }
        tpApprovalBinding.constraintTpListContent.setVisibility(View.GONE);
        SelectedSfCode = "";
        SelectedMonth = "";
        SelectedYear = "";
        SelectedDay = "";
    }

    private void CallTpListApi() {
        progressDialog = CommonUtilsMethods.createProgressDialog(TpApprovalActivity.this);
        try {
            jsonTp.put("tableName", "gettpapproval");
            jsonTp.put("sfcode", SfCode);
            jsonTp.put("division_code", DivCode);
            jsonTp.put("Rsf", SfCode);
            jsonTp.put("sf_type", SfType);
            jsonTp.put("Designation", Designation);
            jsonTp.put("state_code", SubDivisionCode);
            Log.v("json_getTpList", jsonTp.toString());
        } catch (Exception ignored) {

        }
        Call<JsonArray> callGetTPApproval;
        callGetTPApproval = api_interface.getTpApprovalList(jsonTp.toString());
        callGetTPApproval.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                assert response.body() != null;
                Log.v("jjj", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                tpModelLists.add(new TpModelList(json.getString("Sf_Code"), json.getString("SFName"), json.getString("Mnth"), json.getString("Yr"), json.getString("Mn")));
                            }
                            tpApprovalAdapter = new TpApprovalAdapter(TpApprovalActivity.this, tpModelLists, TpApprovalActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tpApprovalBinding.rvTpList.setLayoutManager(mLayoutManager);
                            tpApprovalBinding.rvTpList.setAdapter(tpApprovalAdapter);
                        } else {
                            Toast.makeText(TpApprovalActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        ArrayList<TpModelList> filteredNames = new ArrayList<>();
        for (TpModelList s : tpModelLists) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        tpApprovalAdapter.filterList(filteredNames);
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
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(TpApprovalActivity.this);
    }

    @Override
    public void onClick(DCRApprovalList dcrApprovalList, int pos) {
    }

    @Override
    public void onClickDcrDetail(DcrDetailModelList dcrDetailModelList) {

    }

    @Override
    public void onItemClick(TpModelList tpModelLists, int pos) {
        progressDialog = CommonUtilsMethods.createProgressDialog(TpApprovalActivity.this);
        tpApprovalBinding.tvName.setText(tpModelLists.getName());
        tpApprovalBinding.tvTpPlannedFor.setText(String.format("%s %s", tpModelLists.getMonth(), tpModelLists.getYear()));
        tpApprovalBinding.constraintTpListContent.setVisibility(View.VISIBLE);
        SelectedSfCode = tpModelLists.getCode();
        SelectedMonthYear = String.format("%s %s", tpModelLists.getMonth(), tpModelLists.getYear());
        SelectedMonth = tpModelLists.getMn();
        SelectedYear = tpModelLists.getYear();
        SelectedPosition = pos;
        GetDetailsApi();
    }

    private void GetDetailsApi() {
        try {
            jsonTp = new JSONObject();
            jsonTp.put("tableName", "gettpdetail");
            jsonTp.put("sfcode", SelectedSfCode);
            jsonTp.put("Month", SelectedMonth);
            jsonTp.put("Year", SelectedYear);
            jsonTp.put("division_code", DivCode);
            jsonTp.put("Rsf", SelectedSfCode);
            jsonTp.put("sf_type", SfType);
            jsonTp.put("Designation", Designation);
            jsonTp.put("state_code", SubDivisionCode);
            Log.v("json_getTpDetailedList", jsonTp.toString());
        } catch (Exception ignored) {

        }

        Call<JsonArray> callGetTPADetailedList;
        callGetTPADetailedList = api_interface.getTpDetailedList(jsonTp.toString());

        callGetTPADetailedList.enqueue(new Callback<JsonArray>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                tpDetailedModelsList.add(new TpDetailedModel(json.getString("dayno"), json.getString("Change_Status"), json.getString("Rejection_Reason"), json.getString("WTCode"), json.getString("WTCode2"), json.getString("WTCode3"), json.getString("WTName"), json.getString("WTName2"), json.getString("WTName3"), json.getString("ClusterCode"), json.getString("ClusterCode2"), json.getString("ClusterCode3"), json.getString("ClusterName"), json.getString("ClusterName2"), json.getString("ClusterName3"), json.getString("FWFlg"), json.getString("FWFlg2"), json.getString("FWFlg3"), json.getString("DayRemarks"), json.getString("DayRemarks2"), json.getString("DayRemarks3"), json.getString("Dr_Name"), json.getString("Dr_two_name"), json.getString("Dr_three_name"), json.getString("Chem_Name"), json.getString("Chem_two_name"), json.getString("Chem_three_name"), json.getString("Stockist_Name"), json.getString("Stockist_two_name"), json.getString("Stockist_three_name"), json.getString("JWNames"), json.getString("JWNames2"), json.getString("JWNames3")));
                                if (json.getString("FWFlg").equalsIgnoreCase("W")) {
                                    totalWeekOffDays++;
                                } else if (json.getString("FWFlg").equalsIgnoreCase("H")) {
                                    totalHolidays++;
                                } else {
                                    totalPlannedDays++;
                                }
                            }
                            tpApprovalBinding.tvTotalPlannedDays.setText(String.valueOf(totalPlannedDays));
                            tpApprovalBinding.tvWeekOffDays.setText(totalWeekOffDays + " / " + totalHolidays);
                            tpApprovalDetailedAdapter = new TpApprovalDetailedAdapter(TpApprovalActivity.this, tpDetailedModelsList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tpApprovalBinding.rvTpContentList.setLayoutManager(mLayoutManager);
                            tpApprovalBinding.rvTpContentList.setAdapter(tpApprovalDetailedAdapter);
                        } else {
                            Toast.makeText(TpApprovalActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.v("tpDetailedList", "---" + e);
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}