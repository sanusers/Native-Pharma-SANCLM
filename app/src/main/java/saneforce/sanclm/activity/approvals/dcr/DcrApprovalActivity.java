package saneforce.sanclm.activity.approvals.dcr;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanclm.activity.approvals.ApprovalsActivity.DcrCount;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.AdapterModel;
import saneforce.sanclm.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanclm.activity.approvals.dcr.adapter.AdapterCusMainList;
import saneforce.sanclm.activity.approvals.dcr.adapter.AdapterDcrApprovalList;
import saneforce.sanclm.activity.approvals.dcr.adapter.AdapterSelectionList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.activity.approvals.tp.TpModelList;
import saneforce.sanclm.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.ActivityDcrCallApprovalBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.response.SetupResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class DcrApprovalActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public static String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode, SelectedTransCode, SelectedSfCode, SelectedActivityDate;
    public static int SelectedPosition;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrCallApprovalBinding dcrCallApprovalBinding;
    public static ArrayList<DcrDetailModelList> dcrDetailedList = new ArrayList<>();
    public static ArrayList<SaveCallProductList> SaveProductList = new ArrayList<>();
    public static ArrayList<SaveCallInputList> saveInputList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static AdapterCusMainList adapterCusMainList;
    public static ArrayList<AdapterModel> adapterModels = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static AdapterSelectionList adapterSelectionList;
    public static String DrCaption, ChemistCaption, CipCaption, StockistCaption, UnDrCaption;
    static int countAll = 0, countDr = 0, countChem = 0, countStk = 0, countUnDr = 0;
    static StringBuilder ClusterNames = new StringBuilder();
    public JSONObject jsonDcrContentList = new JSONObject();
    public ProgressDialog progressDialog = null;
    public ApiInterface api_interface;
    LoginResponse loginResponse;
    SetupResponse setUpResponse;
    SQLite sqLite;
    JSONObject jsonDcrList = new JSONObject();
    JSONObject jsonAccept = new JSONObject();
    JSONObject jsonReject = new JSONObject();
    ArrayList<DCRApprovalList> dcrApprovalLists = new ArrayList<>();
    AdapterDcrApprovalList adapterDcrApprovalList;
    Dialog dialogReject;

    private static void SetupAdapter(Context context) {
        adapterModels.add(new AdapterModel("All", String.valueOf(countAll)));
        adapterModels.add(new AdapterModel(DrCaption, String.valueOf(countDr)));
        adapterModels.add(new AdapterModel(ChemistCaption, String.valueOf(countChem)));
        adapterModels.add(new AdapterModel(StockistCaption, String.valueOf(countStk)));
        adapterModels.add(new AdapterModel(UnDrCaption, String.valueOf(countUnDr)));
        //  adapterModels.add(new AdapterModel("CIP", "0"));
        adapterModels.add(new AdapterModel("Hospital", "0"));

        adapterCusMainList = new AdapterCusMainList(context, dcrDetailedList, SaveProductList, saveInputList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        dcrCallApprovalBinding.rvDcrContentList.setLayoutManager(mLayoutManager);
        dcrCallApprovalBinding.rvDcrContentList.setAdapter(adapterCusMainList);

        adapterSelectionList = new AdapterSelectionList(context, adapterModels, dcrDetailedList, adapterCusMainList, DrCaption, ChemistCaption, StockistCaption, UnDrCaption);
        dcrCallApprovalBinding.rvSelectionList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        dcrCallApprovalBinding.rvSelectionList.setAdapter(adapterSelectionList);

        if (!ClusterNames.toString().isEmpty()) {
            dcrCallApprovalBinding.tvCluster1.setText(removeLastChar(ClusterNames.toString()));
        } else {
            dcrCallApprovalBinding.tvCluster1.setText(context.getResources().getString(R.string.no_cluster));
        }
    }

    public static String removeLastChar(String s) {
        return s.substring(0, s.length() - 1);
    }

    public void getDcrContentList() {
        dcrDetailedList.clear();
        SaveProductList.clear();
        saveInputList.clear();
        adapterModels.clear();
        ClusterNames = new StringBuilder();
        progressDialog = CommonUtilsMethods.createProgressDialog(DcrApprovalActivity.this);

        try {
            jsonDcrContentList.put("tableName", "getvwdcrone");
            jsonDcrContentList.put("Trans_SlNo", SelectedTransCode);
            jsonDcrContentList.put("sfcode", SelectedSfCode);
            jsonDcrContentList.put("division_code", DivCode);
            jsonDcrContentList.put("Rsf", TodayPlanSfCode);
            jsonDcrContentList.put("sf_type", SfType);
            jsonDcrContentList.put("Designation", Designation);
            jsonDcrContentList.put("state_code", StateCode);
            jsonDcrContentList.put("subdivision_code", SubDivisionCode);
            Log.v("json_get_full_dcr_list", jsonDcrContentList.toString());

        } catch (Exception ignored) {

        }

        Call<JsonArray> callGetDetailedList;
        callGetDetailedList = api_interface.getDcrDetailedList(jsonDcrContentList.toString());

        callGetDetailedList.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    try {
                        assert response.body() != null;
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        countAll = 0;
                        countDr = 0;
                        countChem = 0;
                        countStk = 0;
                        countUnDr = 0;
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
                                countUnDr++;
                            }

                            if (!ClusterNames.toString().contains(json.getString("SDP_Name") + ",")) {
                                ClusterNames.append(json.getString("SDP_Name")).append(",");
                            }

                            dcrDetailedList.add(new DcrDetailModelList(dcrCallApprovalBinding.tvName.getText().toString(), json.getString("Trans_Detail_Name"), json.getString("Trans_Detail_Info_Code"), json.getString("Type"), json.getString("Trans_Detail_Info_Type"), json.getString("SDP_Name"), json.getString("pob"), json.getString("remarks"), json.getString("jointwrk"), json.getString("Call_Feedback"), json.getString("visitTime"), json.getString("ModTime")));

                            //Extract Product Values
                            String PrdName, PrdSamQty, PrdRxQty;
                            if (!json.getString("products").isEmpty()) {
                                String[] StrArray = json.getString("products").split(",");
                                for (String value : StrArray) {
                                    if (!value.equalsIgnoreCase("  )")) {
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
                                        SaveProductList.add(new SaveCallProductList(json.getString("Trans_Detail_Name"), PrdName, PrdSamQty, PrdRxQty, "0", "Yes"));
                                    }
                                }
                            }

                            //Extract Input Values
                            String InpName, InpQty;
                            if (!json.getString("gifts").isEmpty()) {
                                String[] StrArray = json.getString("gifts").split(",");
                                for (String value : StrArray) {
                                    if (!value.equalsIgnoreCase("  )")) {
                                        InpName = value.substring(0, value.indexOf('(')).trim();
                                        Log.v("extract", "-inp--1--" + InpName);
                                        InpQty = value.substring(value.indexOf("(") + 1);
                                        InpQty = InpQty.substring(0, InpQty.indexOf(")"));
                                        Log.v("extract", "-inp--2--" + InpQty);
                                        saveInputList.add(new SaveCallInputList(json.getString("Trans_Detail_Name"), InpName, InpQty));
                                    }
                                }
                            }
                        }
                        progressDialog.dismiss();
                        SetupAdapter(context);
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        SetupAdapter(context);
                        Log.v("extract", "--error--" + e);
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, context.getResources().getText(R.string.toast_response_failed), Toast.LENGTH_SHORT).show();
                    SetupAdapter(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                Toast.makeText(context, context.getResources().getText(R.string.toast_response_failed), Toast.LENGTH_SHORT).show();
                SetupAdapter(context);
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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

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
             getOnBackPressedDispatcher().onBackPressed();
          //  startActivity(new Intent(DcrApprovalActivity.this, ApprovalsActivity.class));
        });

        dcrCallApprovalBinding.btnApproved.setOnClickListener(view -> CallApprovalApi());

        dcrCallApprovalBinding.btnReject.setOnClickListener(view -> {
            dialogReject = new Dialog(DcrApprovalActivity.this);
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
                    Toast.makeText(DcrApprovalActivity.this, context.getResources().getText(R.string.toast_enter_reason_for_reject), Toast.LENGTH_SHORT).show();
                }
            });
            dialogReject.show();
        });

    }

    private void rejectApproval(String toString) {
            progressDialog = CommonUtilsMethods.createProgressDialog(DcrApprovalActivity.this);
        try {
            jsonReject.put("tableName", "dcrreject");
            jsonReject.put("date", SelectedActivityDate);
            jsonReject.put("reason", toString);
            jsonReject.put("sfcode", SelectedSfCode);
            jsonReject.put("division_code", DivCode);
            jsonReject.put("Rsf", TodayPlanSfCode);
            jsonReject.put("sf_type", SfType);
            jsonReject.put("Designation", Designation);
            jsonReject.put("state_code", StateCode);
            jsonReject.put("subdivision_code", SubDivisionCode);
            Log.v("json_send_approval", jsonReject.toString());
        } catch (Exception ignored) {

        }
        Call<JsonObject> callDcrReject;
        callDcrReject = api_interface.sendDCRReject(jsonReject.toString());

        callDcrReject.enqueue(new Callback<JsonObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(DcrApprovalActivity.this, "Rejected Successfully", Toast.LENGTH_SHORT).show();
                            dialogReject.dismiss();
                            removeSelectedData();
                            DcrCount--;
                        }
                    } catch (Exception e) {
                        dialogReject.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(DcrApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DcrApprovalActivity.this, getResources().getText(R.string.toast_response_failed), Toast.LENGTH_SHORT).show();
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
            progressDialog = CommonUtilsMethods.createProgressDialog(DcrApprovalActivity.this);
        try {
            jsonAccept.put("tableName", "dcrapproval");
            jsonAccept.put("date", SelectedActivityDate);
            jsonAccept.put("sfcode", SelectedSfCode);
            jsonAccept.put("division_code", DivCode);
            jsonAccept.put("Rsf", TodayPlanSfCode);
            jsonAccept.put("sf_type", SfType);
            jsonAccept.put("Designation", Designation);
            jsonAccept.put("state_code", StateCode);
            jsonAccept.put("subdivision_code", SubDivisionCode);
            Log.v("json_send_approval", jsonAccept.toString());
        } catch (Exception ignored) {

        }

        Call<JsonObject> callDcrApproval;
        callDcrApproval = api_interface.sendDCRApproval(jsonAccept.toString());
        callDcrApproval.enqueue(new Callback<JsonObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(DcrApprovalActivity.this, "Approved Successfully", Toast.LENGTH_SHORT).show();
                            removeSelectedData();
                            DcrCount--;
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(DcrApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DcrApprovalActivity.this, getResources().getText(R.string.toast_response_failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void filter(String text) {
        ArrayList<DCRApprovalList> filteredNames = new ArrayList<>();
        for (DCRApprovalList s : dcrApprovalLists) {
            if (s.getSf_name().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterDcrApprovalList.filterList(filteredNames);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void CallDcrListApi() {
            progressDialog = CommonUtilsMethods.createProgressDialog(DcrApprovalActivity.this);
        try {
            jsonDcrList.put("tableName", "getvwdcr");
            jsonDcrList.put("sfcode", SfCode);
            jsonDcrList.put("division_code", DivCode);
            jsonDcrList.put("Rsf", TodayPlanSfCode);
            jsonDcrList.put("sf_type", SfType);
            jsonDcrList.put("Designation", Designation);
            jsonDcrList.put("state_code", StateCode);
            jsonDcrList.put("subdivision_code", SubDivisionCode);
            Log.v("json_getDcr_list", jsonDcrList.toString());
        } catch (Exception ignored) {

        }

        Call<JsonArray> callGetDcrList;
        callGetDcrList = api_interface.getDcrApprovalList(jsonDcrList.toString());

        callGetDcrList.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            dcrApprovalLists.add(new DCRApprovalList(json.getString("Trans_SlNo"), json.getString("Sf_Name"), json.getString("Activity_Date"), json.getString("Plan_Name"), json.getString("WorkType_Name"), json.getString("Sf_Code"), json.getString("FieldWork_Indicator"), json.getString("Submission_Date"), json.getString("Hlfday")));
                        }
                        adapterDcrApprovalList = new AdapterDcrApprovalList(DcrApprovalActivity.this, DcrApprovalActivity.this, dcrApprovalLists, DcrApprovalActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        dcrCallApprovalBinding.rvDcrList.setLayoutManager(mLayoutManager);
                        dcrCallApprovalBinding.rvDcrList.setAdapter(adapterDcrApprovalList);
                    } catch (Exception ignored) {

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(DcrApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DcrApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
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
            TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(DcrApprovalActivity.this);
            JSONArray jsonArray;
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);

                setUpResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setUpResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
                DrCaption = setUpResponse.getCaptionDr();
                ChemistCaption = setUpResponse.getCaptionChemist();
                StockistCaption = setUpResponse.getCaptionStockist();
                UnDrCaption = setUpResponse.getCaptionUndr();
                if (setupData.has("cip_need")) {
                    CipCaption = setUpResponse.getCaptionCip();
                }
            }
        } catch (Exception ignored) {

        }
    }


    @Override
    public void onClick(DCRApprovalList dcrApprovalList, int pos) {
        dcrCallApprovalBinding.tvName.setText(dcrApprovalList.getSf_name());
        dcrCallApprovalBinding.tvWt.setText(dcrApprovalList.getWorkType_name());
        dcrCallApprovalBinding.tvRemark1.setText(dcrApprovalList.getRemarks());
        dcrCallApprovalBinding.tvActivityDate.setText(dcrApprovalList.getActivity_date());
        dcrCallApprovalBinding.tvSubmittedDate.setText(dcrApprovalList.getSubmission_date_sub());
        SelectedTransCode = dcrApprovalList.getTrans_slNo();
        SelectedSfCode = dcrApprovalList.getSfCode();
        SelectedActivityDate = dcrApprovalList.getActivity_date();
        SelectedPosition = pos;
        getDcrContentList();
        dcrCallApprovalBinding.constraintDcrListContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickDcrDetail(DcrDetailModelList dcrDetailModelList) {

    }

    @Override
    public void onItemClick(TpModelList tpModelLists) {

    }
}