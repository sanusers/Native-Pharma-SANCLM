package saneforce.sanzen.activity.approvals.tp;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import saneforce.sanzen.activity.approvals.tp.adapter.TpApprovalAdapter;
import saneforce.sanzen.activity.approvals.tp.adapter.TpApprovalDetailedAdapter;
import saneforce.sanzen.activity.approvals.tp.pojo.TpDetailedModel;
import saneforce.sanzen.activity.approvals.tp.pojo.TpModelList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityTpApprovalBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;

import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class TpApprovalActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public static String  SelectedSfCode, SelectedMonthYear, SelectedMonth, SelectedYear, SelectedDay = "", TpDrNeed, TpChemNeed, TpClusterNeed, TpJwNeed, TpStockistNeed, TpUnDrNeed, TpCipNeed, TpHospNeed;
    @SuppressLint("StaticFieldLeak")
    public static ActivityTpApprovalBinding tpApprovalBinding;
    public static int SelectedPosition;
    ArrayList<TpDetailedModel> tpDetailedModelsList = new ArrayList<>();
    TpApprovalDetailedAdapter tpApprovalDetailedAdapter;

    Dialog dialogReject;
    ApiInterface api_interface;
    ArrayList<TpModelList> tpModelLists = new ArrayList<>();
    TpApprovalAdapter tpApprovalAdapter;
    JSONObject jsonTp = new JSONObject();
    ProgressDialog progressDialog = null;
    int totalPlannedDays = 0, totalWeekOffDays = 0, totalHolidays = 0;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;


    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            tpApprovalBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tpApprovalBinding = ActivityTpApprovalBinding.inflate(getLayoutInflater());
        setContentView(tpApprovalBinding.getRoot());
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
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
            jsonTp = CommonUtilsMethods.CommonObjectParameter(TpApprovalActivity.this);
            jsonTp.put("tableName", "savetpapproval");
            jsonTp.put("sfcode", SharedPref.getSfCode(this));
            jsonTp.put("Month", SelectedMonth);
            jsonTp.put("Year", SelectedYear);
            jsonTp.put("division_code", SharedPref.getDivisionCode(this));
            jsonTp.put("Rsf", SelectedSfCode);


            Log.v("json_tp_Approved", jsonTp.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/tp");
        Call<JsonElement> callApproveTp = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonTp.toString());
        callApproveTp.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.approved_successfully));
                            removeSelectedData();
                            ApprovalsActivity.TpCount--;
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
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
                commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_enter_reason_for_reject));
            }
        });
        dialogReject.show();
    }


    private void tpReject(String reason) {
        progressDialog = CommonUtilsMethods.createProgressDialog(TpApprovalActivity.this);
        try {
            jsonTp = CommonUtilsMethods.CommonObjectParameter(TpApprovalActivity.this);
            jsonTp.put("tableName", "savetpreject");
            jsonTp.put("sfcode", SharedPref.getSfCode(this));
            jsonTp.put("Month", SelectedMonth);
            jsonTp.put("Year", SelectedYear);
            jsonTp.put("reason", reason);
            jsonTp.put("division_code", SharedPref.getDivisionCode(this));
            jsonTp.put("Rsf", SelectedSfCode);
            Log.v("json_tp_Reject", jsonTp.toString());
        } catch (Exception ignored) {

        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/tp");
        Call<JsonElement> callRejectTp = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonTp.toString());
        callRejectTp.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.rejected_successfully));
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
                    commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
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
            jsonTp=CommonUtilsMethods.CommonObjectParameter(TpApprovalActivity.this);
            jsonTp.put("tableName", "gettpapproval");
            jsonTp.put("sfcode", SharedPref.getSfCode(this));
            jsonTp.put("division_code", SharedPref.getDivisionCode(this));
            jsonTp.put("Rsf", SharedPref.getHqCode(this));
            Log.v("json_getTpList", jsonTp.toString());
        } catch (Exception ignored) {

        }


        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetTPApproval = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonTp.toString());

        callGetTPApproval.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("jjj", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        tpModelLists.clear();
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            tpApprovalBinding.constraintSelectedDetails.setVisibility(View.VISIBLE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                tpModelLists.add(new TpModelList(json.getString("Sf_Code"), json.getString("SFName"), json.getString("Mnth"), json.getString("Yr"), json.getString("Mn")));
                            }
                            tpApprovalAdapter = new TpApprovalAdapter(TpApprovalActivity.this, tpModelLists, TpApprovalActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tpApprovalBinding.rvTpList.setLayoutManager(mLayoutManager);
                            tpApprovalBinding.rvTpList.setAdapter(tpApprovalAdapter);
                        } else {
                            tpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                            commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.no_data_found));
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    tpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                tpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
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
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray(); //Tour Plan setup
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TpClusterNeed = jsonObject.getString("ClusterNeed");
                TpJwNeed = jsonObject.getString("JWNeed");
                TpDrNeed = jsonObject.getString("DrNeed");
                TpChemNeed = jsonObject.getString("ChmNeed");
                TpStockistNeed = jsonObject.getString("StkNeed");
                TpUnDrNeed = jsonObject.getString("UnDrNeed");
                TpCipNeed = jsonObject.getString("Cip_Need");
                TpHospNeed = jsonObject.getString("HospNeed");
            }
        } catch (Exception ignored) {
        }
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
            jsonTp = CommonUtilsMethods.CommonObjectParameter(TpApprovalActivity.this);
            jsonTp.put("tableName", "gettpdetail");
            jsonTp.put("sfcode", SelectedSfCode);
            jsonTp.put("Month", SelectedMonth);
            jsonTp.put("Year", SelectedYear);
            jsonTp.put("division_code", SharedPref.getDivisionCode(this));
            jsonTp.put("Rsf", SelectedSfCode);
            Log.v("json_getTpDetailedList", jsonTp.toString());
        } catch (Exception ignored) {

        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/tp");
        Call<JsonElement> callGetTPADetailedList = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonTp.toString());

        callGetTPADetailedList.enqueue(new Callback<JsonElement>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    tpDetailedModelsList.clear();
                    totalWeekOffDays=0;
                    totalPlannedDays=0;
                    totalHolidays=0;
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                tpDetailedModelsList.add(new TpDetailedModel(json.getString("dayno"), json.getString("Change_Status"), json.getString("Rejection_Reason"), json.getString("WTCode"), json.getString("WTCode2"), json.getString("WTCode3"), json.getString("WTName"), json.getString("WTName2"), json.getString("WTName3"),json.getString("HQCodes"), json.getString("HQCodes2"), json.getString("HQCodes3"),json.getString("HQNames"), json.getString("HQNames2"), json.getString("HQNames3"),   json.getString("ClusterCode"), json.getString("ClusterCode2"), json.getString("ClusterCode3"), json.getString("ClusterName"), json.getString("ClusterName2"), json.getString("ClusterName3"), json.getString("FWFlg"), json.getString("FWFlg2"), json.getString("FWFlg3"), json.getString("DayRemarks"), json.getString("DayRemarks2"), json.getString("DayRemarks3"), json.getString("Dr_Name"), json.getString("Dr_two_name"), json.getString("Dr_three_name"), json.getString("Chem_Name"), json.getString("Chem_two_name"), json.getString("Chem_three_name"), json.getString("Stockist_Name"), json.getString("Stockist_two_name"), json.getString("Stockist_three_name"), json.getString("JWNames"), json.getString("JWNames2"), json.getString("JWNames3")));
                                if (json.getString("FWFlg").equalsIgnoreCase("W")) {
                                    totalWeekOffDays++;
                                } else if (json.getString("FWFlg").equalsIgnoreCase("H")) {
                                    totalHolidays++;
                                } else {
                                    totalPlannedDays++;
                                }
                            }

                            String joiningDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_17, jsonArray.getJSONObject(0).getString("sf_TP_Active_Dt"));
                            String joiningMonth = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, jsonArray.getJSONObject(0).getString("sf_TP_Active_Dt"));
                            String joiningYear = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_26, jsonArray.getJSONObject(0).getString("sf_TP_Active_Dt"));


                            if (joiningMonth.contains(SelectedMonth) && joiningYear.equalsIgnoreCase(SelectedYear)) {
                                tpApprovalBinding.tvJoiningdate.setText(joiningDate);
                                tpApprovalBinding.llJoningdate.setVisibility(View.VISIBLE);
                            } else {
                                tpApprovalBinding.llJoningdate.setVisibility(View.INVISIBLE);
                            }


                            tpApprovalBinding.constraintSelectedDetails.setVisibility(View.VISIBLE);
                            tpApprovalBinding.tvTotalPlannedDays.setText(String.valueOf(totalPlannedDays));
                            tpApprovalBinding.tvWeekOffDays.setText(totalWeekOffDays + " / " + totalHolidays);
                            tpApprovalDetailedAdapter = new TpApprovalDetailedAdapter(TpApprovalActivity.this, tpDetailedModelsList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tpApprovalBinding.rvTpContentList.setLayoutManager(mLayoutManager);
                            tpApprovalBinding.rvTpContentList.setAdapter(tpApprovalDetailedAdapter);
                        } else {
                            commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.no_data_found));
                        }
                    } catch (Exception e) {
                        Log.v("tpDetailedList", "---" + e);
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(TpApprovalActivity.this,getString(R.string.toast_response_failed));
            }
        });
    }
}