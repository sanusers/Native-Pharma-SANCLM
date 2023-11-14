package saneforce.sanclm.activity.approvals.dcr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.AdapterModel;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.activity.approvals.dcr.adapter.AdapterCustMainList;
import saneforce.sanclm.activity.approvals.dcr.adapter.AdapterDcrApprovalList;
import saneforce.sanclm.activity.approvals.dcr.adapter.AdapterSelectionList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityDcrCallApprovalBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class DcrCallApprovalActivity extends AppCompatActivity {
    public static String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode, TodayplanSfCode, SelectedTransCode, SelectedSfCode, SelectedActivityDate;
    public static int SelectedPosition;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrCallApprovalBinding dcrCallApprovalBinding;
    public static JSONObject jsonDcrContentList = new JSONObject();
    public static ProgressDialog progressDialog = null;
    public static ApiInterface api_interface;
    public static ArrayList<DcrDetailModelList> dcrDetailedList = new ArrayList<>();
    public static ArrayList<SaveCallProductList> SaveProductList = new ArrayList<>();
    public static ArrayList<SaveCallInputList> saveInputList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static AdapterCustMainList adapterCustMainList;
    public static ArrayList<AdapterModel> adapterModels = new ArrayList<>();
    public static AdapterSelectionList adapterSelectionList;
    LoginResponse loginResponse;
    SQLite sqLite;
    JSONObject jsonDcrList = new JSONObject();
    JSONObject jsonAccept = new JSONObject();
    JSONObject jsonReject = new JSONObject();
    ArrayList<DCRApprovalList> dcrApprovalLists = new ArrayList<>();
    ArrayList<DCRApprovalList> dcrApprovalListsdummy = new ArrayList<>();
    AdapterDcrApprovalList adapterDcrApprovalList;
    Dialog dialogReject;

    public static void getDcrContentList(Context context) {
        dcrDetailedList.clear();
        SaveProductList.clear();
        saveInputList.clear();
        adapterModels.clear();
        try {
            jsonDcrContentList.put("tableName", "getvwdcrone");
            jsonDcrContentList.put("Trans_SlNo", SelectedTransCode);
            jsonDcrContentList.put("sfcode", SelectedSfCode);
            jsonDcrContentList.put("division_code", DivCode);
            jsonDcrContentList.put("Rsf", TodayplanSfCode);
            jsonDcrContentList.put("sf_type", SfType);
            jsonDcrContentList.put("Designation", Designation);
            jsonDcrContentList.put("state_code", StateCode);
            jsonDcrContentList.put("subdivision_code", SubDivisionCode);
            Log.v("json_get_full_dcr_list", jsonDcrContentList.toString());

        } catch (Exception e) {

        }

        Call<JsonArray> callGetDetailedList = null;
        callGetDetailedList = api_interface.getDcrDetailedList(jsonDcrContentList.toString());

        callGetDetailedList.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        int countAll = 0, countDr = 0, countChem = 0, countStk = 0, countUndr = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            countAll++;
                            if (json.getString("Type").equalsIgnoreCase("DOCTOR")) {
                                countDr++;
                            }
                            if (json.getString("Type").equalsIgnoreCase("CHEMIST")) {
                                countChem++;
                            }
                            if (json.getString("Type").equalsIgnoreCase("STOCKIST")) {
                                countStk++;
                            }
                            if (json.getString("Type").equalsIgnoreCase("ULDOCTOR")) {
                                countUndr++;
                            }

                            dcrDetailedList.add(new DcrDetailModelList(dcrCallApprovalBinding.tvName.getText().toString(), json.getString("Trans_Detail_Name"), json.getString("Type"), json.getString("Trans_Detail_Info_Type"), json.getString("SDP_Name"), json.getString("pob"), json.getString("remarks"), json.getString("jointwrk"), json.getString("Call_Feedback")));

                            //Extract Product Values
                            String PrdName = "", PrdSamQty = "0", PrdRxQty = "0";
                            if (!json.getString("products").isEmpty()) {
                                String[] clstarrrayqty = json.getString("products").split(",");
                               // Log.v("extract", "--0--" + clstarrrayqty[0]);
                                for (String value : clstarrrayqty) {
                                    PrdName = value.substring(0, value.indexOf('(')).trim();
                                    Log.v("extract", "--1--" + PrdName);

                                    PrdSamQty = value.substring(value.indexOf("(") + 1);
                                    PrdSamQty = PrdSamQty.substring(0, PrdSamQty.indexOf(")"));

                                    Log.v("extract", "--2--" + PrdSamQty);

                                    PrdRxQty = value.substring(value.indexOf(")") + 1).trim();
                                    if (PrdRxQty.contains("(")) {
                                        PrdRxQty = PrdRxQty.substring(PrdRxQty.indexOf("(") + 1);
                                        PrdRxQty = PrdRxQty.substring(0, PrdRxQty.indexOf(")"));
                                    } else {
                                        PrdRxQty = "0";
                                    }
                                    Log.v("extract", "--3--" + PrdRxQty);
                                }

                                SaveProductList.add(new SaveCallProductList(json.getString("Trans_Detail_Name"), PrdName, PrdSamQty, PrdRxQty, "0", "Yes"));
                            }

                            //Extract Input Values
                            String InpName = "", InpQty = "0";
                            if (!json.getString("gifts").isEmpty()) {
                                String[] clstarrrayInp = json.getString("gifts").split(",");
                              //  Log.v("extract", "-inp--0--" + clstarrrayInp[0]);
                                for (String value : clstarrrayInp) {
                                    InpName = value.substring(0, value.indexOf('(')).trim();
                                    Log.v("extract", "-inp--1--" + InpName);
                                    InpQty = value.substring(value.indexOf("(") + 1);
                                    InpQty = InpQty.substring(0, InpQty.indexOf(")"));
                                    Log.v("extract", "-inp--2--" + InpQty);
                                }
                                saveInputList.add(new SaveCallInputList(json.getString("Trans_Detail_Name"), InpName, InpQty));
                            }
                              /*  Products = json.getString("products");
                                Log.v("products", "--0--" + Products);

                                PrdName = Products.substring(0, Products.indexOf('(')).trim();
                                Log.v("products", "--1--" + PrdName);

                                PrdSamQty = Products.substring(Products.indexOf("(") + 1);
                                PrdSamQty = PrdSamQty.substring(0, PrdSamQty.indexOf(")"));

                                Log.v("products", "--2--" + PrdSamQty);

                                PrdRxQty = Products.substring(Products.indexOf(")") + 1).trim();
                                if (PrdRxQty.contains("(")) {
                                    PrdRxQty = PrdRxQty.substring(PrdRxQty.indexOf("(") + 1);
                                    PrdRxQty = PrdRxQty.substring(0, PrdRxQty.indexOf(")"));
                                } else {
                                    PrdRxQty = "0";
                                }
                                Log.v("products", "--3--" + PrdRxQty);*/

                        }
                        adapterModels.add(new AdapterModel("All", String.valueOf(countAll)));
                        adapterModels.add(new AdapterModel("Doctor", String.valueOf(countDr)));
                        adapterModels.add(new AdapterModel("Chemist", String.valueOf(countChem)));
                        adapterModels.add(new AdapterModel("Stockiest", String.valueOf(countStk)));
                        adapterModels.add(new AdapterModel("Unlisted Dr", String.valueOf(countUndr)));
                        //  adapterModels.add(new AdapterModel("CIP", "0"));
                        adapterModels.add(new AdapterModel("Hospital", "0"));

                        adapterCustMainList = new AdapterCustMainList(context, dcrDetailedList, SaveProductList, saveInputList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                        dcrCallApprovalBinding.rvDcrContentList.setLayoutManager(mLayoutManager);
                        dcrCallApprovalBinding.rvDcrContentList.setAdapter(adapterCustMainList);

                        adapterSelectionList = new AdapterSelectionList(context, adapterModels, dcrDetailedList, adapterCustMainList);
                        dcrCallApprovalBinding.rvSelectionList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                        dcrCallApprovalBinding.rvSelectionList.setAdapter(adapterSelectionList);
                    } catch (Exception e) {
                        Log.v("extract", "--eror--" + e);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrCallApprovalBinding = ActivityDcrCallApprovalBinding.inflate(getLayoutInflater());
        setContentView(dcrCallApprovalBinding.getRoot());
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());
        progressDialog = CommonUtilsMethods.createProgressDialog(DcrCallApprovalActivity.this);
        getRequiredData();
        CallDcrListApi();

        dcrCallApprovalBinding.searchDcr.addTextChangedListener(new TextWatcher() {
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

        dcrCallApprovalBinding.ivBack.setOnClickListener(view -> {
            SelectedSfCode = "";
            SelectedActivityDate = "";
            startActivity(new Intent(DcrCallApprovalActivity.this, ApprovalsActivity.class));
        });

        dcrCallApprovalBinding.btnApproved.setOnClickListener(view -> CallApprovalApi());

        dcrCallApprovalBinding.btnReject.setOnClickListener(view -> {
            dialogReject = new Dialog(DcrCallApprovalActivity.this);
            dialogReject.setContentView(R.layout.popup_leave_reject);
            dialogReject.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogReject.setCancelable(false);

            ImageView iv_close = dialogReject.findViewById(R.id.img_close);
            EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
            Button btn_cancel = dialogReject.findViewById(R.id.btn_cancel);
            Button btn_reject = dialogReject.findViewById(R.id.btn_reject);

            btn_cancel.setOnClickListener(view1 -> {
                ed_reason.setText("");
                dialogReject.dismiss();
            });

            iv_close.setOnClickListener(view12 -> {
                ed_reason.setText("");
                dialogReject.dismiss();
            });

            btn_reject.setOnClickListener(view14 -> {
                if (!TextUtils.isEmpty(ed_reason.getText().toString())) {
                    rejectApproval(ed_reason.getText().toString());
                } else {
                    Toast.makeText(DcrCallApprovalActivity.this, "Enter reason for reject", Toast.LENGTH_SHORT).show();
                }
            });
            dialogReject.show();
        });

    }

    private void rejectApproval(String toString) {
        if (progressDialog == null) {
            progressDialog = CommonUtilsMethods.createProgressDialog(DcrCallApprovalActivity.this);
            progressDialog.show();
        } else {
            progressDialog.show();
        }
        try {
            jsonReject.put("tableName", "dcrreject");
            jsonReject.put("date", SelectedActivityDate);
            jsonReject.put("reason", toString);
            jsonReject.put("sfcode", SelectedSfCode);
            jsonReject.put("division_code", DivCode);
            jsonReject.put("Rsf", TodayplanSfCode);
            jsonReject.put("sf_type", SfType);
            jsonReject.put("Designation", Designation);
            jsonReject.put("state_code", StateCode);
            jsonReject.put("subdivision_code", SubDivisionCode);
            Log.v("json_send_approval", jsonReject.toString());
        } catch (Exception e) {

        }
        Call<JsonObject> callDcrReject = null;
        callDcrReject = api_interface.sendDCRReject(jsonReject.toString());

        callDcrReject.enqueue(new Callback<JsonObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(DcrCallApprovalActivity.this, "Rejected Successfully", Toast.LENGTH_SHORT).show();
                            dialogReject.dismiss();
                            removeSelectedData();
                        }
                    } catch (Exception e) {
                        dialogReject.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(DcrCallApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DcrCallApprovalActivity.this, getResources().getText(R.string.toast_response_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeSelectedData() {
        adapterDcrApprovalList.removeAt(SelectedPosition);
        adapterDcrApprovalList.notifyDataSetChanged();
        for (int i = 0; i < dcrApprovalLists.size(); i++) {
            if (dcrApprovalLists.get(i).getActivity_date().equalsIgnoreCase(SelectedActivityDate) && dcrApprovalLists.get(i).getSfCode().equalsIgnoreCase(SelectedSfCode)) {
                dcrApprovalLists.remove(i);
                break;
            }
        }
        dcrCallApprovalBinding.constraintDcrListContent.setVisibility(View.GONE);
        SelectedSfCode = "";
        SelectedActivityDate = "";
    }

    private void CallApprovalApi() {
        Log.v("pos", "---" + SelectedPosition);
        if (progressDialog == null) {
            progressDialog = CommonUtilsMethods.createProgressDialog(DcrCallApprovalActivity.this);
            progressDialog.show();
        } else {
            progressDialog.show();
        }
        try {
            jsonAccept.put("tableName", "dcrapproval");
            jsonAccept.put("date", SelectedActivityDate);
            jsonAccept.put("sfcode", SelectedSfCode);
            jsonAccept.put("division_code", DivCode);
            jsonAccept.put("Rsf", TodayplanSfCode);
            jsonAccept.put("sf_type", SfType);
            jsonAccept.put("Designation", Designation);
            jsonAccept.put("state_code", StateCode);
            jsonAccept.put("subdivision_code", SubDivisionCode);
            Log.v("json_send_approval", jsonAccept.toString());
        } catch (Exception e) {

        }

        Call<JsonObject> callDcrApproval = null;
        callDcrApproval = api_interface.sendDCRApproval(jsonAccept.toString());

        callDcrApproval.enqueue(new Callback<JsonObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(DcrCallApprovalActivity.this, "Approved Successfully", Toast.LENGTH_SHORT).show();
                            removeSelectedData();
                        }
                    } catch (Exception e) {

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(DcrCallApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DcrCallApprovalActivity.this, getResources().getText(R.string.toast_response_failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void filter(String text) {
        ArrayList<DCRApprovalList> filterdNames = new ArrayList<>();
        for (DCRApprovalList s : dcrApprovalLists) {
            if (s.getSf_name().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        adapterDcrApprovalList.filterList(filterdNames);
    }

    @Override
    public void onBackPressed() {

    }

    private void CallDcrListApi() {
        if (progressDialog == null) {
            progressDialog = CommonUtilsMethods.createProgressDialog(DcrCallApprovalActivity.this);
            progressDialog.show();
        } else {
            progressDialog.show();
        }
        try {
            jsonDcrList.put("tableName", "getvwdcr");
            jsonDcrList.put("sfcode", SfCode);
            jsonDcrList.put("division_code", DivCode);
            jsonDcrList.put("Rsf", TodayplanSfCode);
            jsonDcrList.put("sf_type", SfType);
            jsonDcrList.put("Designation", Designation);
            jsonDcrList.put("state_code", StateCode);
            jsonDcrList.put("subdivision_code", SubDivisionCode);
            Log.v("json_getdcr_list", jsonDcrList.toString());
        } catch (Exception e) {

        }

        Call<JsonArray> callGetDcrList = null;
        callGetDcrList = api_interface.getDcrApprovalList(jsonDcrList.toString());

        callGetDcrList.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            dcrApprovalLists.add(new DCRApprovalList(json.getString("Trans_SlNo"), json.getString("Sf_Name"), json.getString("Activity_Date"), json.getString("Plan_Name"), json.getString("WorkType_Name"), json.getString("Sf_Code"), json.getString("FieldWork_Indicator")));
                        }
                        adapterDcrApprovalList = new AdapterDcrApprovalList(DcrCallApprovalActivity.this, DcrCallApprovalActivity.this, dcrApprovalLists, dcrApprovalListsdummy);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        dcrCallApprovalBinding.rvDcrList.setLayoutManager(mLayoutManager);
                        dcrCallApprovalBinding.rvDcrList.setAdapter(adapterDcrApprovalList);
                    } catch (Exception e) {

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(DcrCallApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DcrCallApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
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
        TodayplanSfCode = SharedPref.getTodayDayPlanSfCode(DcrCallApprovalActivity.this);
    }
}