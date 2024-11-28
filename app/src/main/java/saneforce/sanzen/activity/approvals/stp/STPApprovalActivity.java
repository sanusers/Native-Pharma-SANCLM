package saneforce.sanzen.activity.approvals.stp;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import saneforce.sanzen.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanzen.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanzen.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanzen.activity.approvals.stp.adapter.STPApprovalAdapter;
import saneforce.sanzen.activity.approvals.stp.model.STPModelList;
import saneforce.sanzen.activity.approvals.tp.pojo.TpModelList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityStpApprovalBinding;
import saneforce.sanzen.databinding.ActivityTpApprovalBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class STPApprovalActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public String  SelectedSfCode, stpDrNeed, stpChemNeed, planName, planSName;
    public ActivityStpApprovalBinding stpApprovalBinding;
    public int SelectedPosition;
//    ArrayList<TpDetailedModel> tpDetailedModelsList = new ArrayList<>();
//    TpApprovalDetailedAdapter tpApprovalDetailedAdapter;
    Dialog dialogReject;
    ApiInterface api_interface;
    ArrayList<STPModelList> stpModelLists = new ArrayList<>();
    STPApprovalActivity stpApprovalAdapter;
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
            stpApprovalBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                                      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                                      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                                      | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                                      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
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
        CallTpListApi();
        stpApprovalBinding.ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(STPApprovalActivity.this, ApprovalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

//        stpApprovalBinding.btnApproved.setOnClickListener(v -> tpApproval());
//        stpApprovalBinding.btnReject.setOnClickListener(v -> DisplayDialog());


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
    public void onClick(DCRApprovalList dcrApprovalList, int pos) {    }

    @Override
    public void onClickDcrDetail(DcrDetailModelList dcrDetailModelList) {    }

    @Override
    public void onItemClick(TpModelList stpModelLists, int pos) {    }

    @Override
    public void onSTPItemClick(STPModelList stpModelList, int position) {
        
    }
    
    private void CallTpListApi() {
        progressDialog = CommonUtilsMethods.createProgressDialog(STPApprovalActivity.this);
        try {
            jsonTp=CommonUtilsMethods.CommonObjectParameter(STPApprovalActivity.this);
            jsonTp.put("tableName", "getstpapproval");
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
                        stpModelLists.clear();
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            stpApprovalBinding.constraintSelectedDetails.setVisibility(View.VISIBLE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                stpModelLists.add(new STPModelList(json.getString("Sf_Code"), json.getString("SFName"), json.getString("Division_Code")));
                            }
//                            stpApprovalAdapter = new STPApprovalAdapter(STPApprovalActivity.this, stpModelLists, this);
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            stpApprovalBinding.rvTpList.setLayoutManager(mLayoutManager);
//                            stpApprovalBinding.rvTpList.setAdapter(tpApprovalAdapter);
                        } else {
                            stpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                            commonUtilsMethods.showToastMessage(STPApprovalActivity.this,getString(R.string.no_data_found));
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    stpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(STPApprovalActivity.this,getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                stpApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(STPApprovalActivity.this,getString(R.string.no_network));
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
//        stpApprovalAdapter.filterList(filteredNames);
    }

    private void getRequiredData() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STP_SETUP).getMasterSyncDataJsonArray(); //Tour Plan setup
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                planName = jsonObject.getString("Plan_Name");
                planSName = jsonObject.getString("Plan_SName");
            }
            stpDrNeed = SharedPref.getDrNeed(this);
            stpChemNeed = SharedPref.getChmNeed(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetDetailsApi() {
        try {
            jsonTp = CommonUtilsMethods.CommonObjectParameter(STPApprovalActivity.this);
            jsonTp.put("tableName", "getstp_app_details");
            jsonTp.put("sfcode", SelectedSfCode);
            jsonTp.put("division_code", SharedPref.getDivisionCode(this));
            jsonTp.put("Rsf", SelectedSfCode);
            Log.v("json_getSTPDetailedList", jsonTp.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/stp");
        Call<JsonElement> callGetTPADetailedList = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonTp.toString());

        callGetTPADetailedList.enqueue(new Callback<JsonElement>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
//                    tpDetailedModelsList.clear();
                    totalWeekOffDays=0;
                    totalPlannedDays=0;
                    totalHolidays=0;
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
//                                tpDetailedModelsList.add(new TpDetailedModel(json.getString("dayno"), json.getString("Change_Status"), json.getString("Rejection_Reason"), json.getString("WTCode"), json.getString("WTCode2"), json.getString("WTCode3"), json.getString("WTName"), json.getString("WTName2"), json.getString("WTName3"),json.getString("HQCodes"), json.getString("HQCodes2"), json.getString("HQCodes3"),json.getString("HQNames"), json.getString("HQNames2"), json.getString("HQNames3"),   json.getString("ClusterCode"), json.getString("ClusterCode2"), json.getString("ClusterCode3"), json.getString("ClusterName"), json.getString("ClusterName2"), json.getString("ClusterName3"), json.getString("FWFlg"), json.getString("FWFlg2"), json.getString("FWFlg3"), json.getString("DayRemarks"), json.getString("DayRemarks2"), json.getString("DayRemarks3"), json.getString("Dr_Name"), json.getString("Dr_two_name"), json.getString("Dr_three_name"), json.getString("Chem_Name"), json.getString("Chem_two_name"), json.getString("Chem_three_name"), json.getString("Stockist_Name"), json.getString("Stockist_two_name"), json.getString("Stockist_three_name"), json.getString("JWNames"), json.getString("JWNames2"), json.getString("JWNames3")));
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


//                            if (joiningMonth.contains(SelectedMonth) && joiningYear.equalsIgnoreCase(SelectedYear)) {
                                stpApprovalBinding.tvJoiningdate.setText(joiningDate);
                                stpApprovalBinding.llJoningdate.setVisibility(View.VISIBLE);
//                            } else {
//                                stpApprovalBinding.llJoningdate.setVisibility(View.INVISIBLE);
//                            }


                            stpApprovalBinding.constraintSelectedDetails.setVisibility(View.VISIBLE);
                            stpApprovalBinding.tvTotalPlannedDays.setText(String.valueOf(totalPlannedDays));
                            stpApprovalBinding.tvWeekOffDays.setText(totalWeekOffDays + " / " + totalHolidays);
//                            tpApprovalDetailedAdapter = new TpApprovalDetailedAdapter(STPApprovalActivity.this, tpDetailedModelsList);
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            stpApprovalBinding.rvTpContentList.setLayoutManager(mLayoutManager);
//                            stpApprovalBinding.rvTpContentList.setAdapter(tpApprovalDetailedAdapter);
                        } else {
                            commonUtilsMethods.showToastMessage(STPApprovalActivity.this,getString(R.string.no_data_found));
                        }
                    } catch (Exception e) {
                        Log.v("tpDetailedList", "---" + e);
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(STPApprovalActivity.this,getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(STPApprovalActivity.this,getString(R.string.no_network));
            }
        });
    }

}