package saneforce.sanzen.activity.approvals.dcr;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanzen.activity.approvals.ApprovalsActivity.DcrCount;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.AdapterModel;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanzen.activity.approvals.dcr.adapter.AdapterCusMainList;
import saneforce.sanzen.activity.approvals.dcr.adapter.AdapterDcrApprovalList;
import saneforce.sanzen.activity.approvals.dcr.adapter.AdapterSelectionList;
import saneforce.sanzen.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanzen.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanzen.activity.approvals.tp.pojo.TpModelList;
import saneforce.sanzen.activity.call.pojo.input.SaveCallInputList;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;

import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DcrApprovalActivity extends AppCompatActivity implements OnItemClickListenerApproval {

    public static String SelectedTransCode, SelectedSfCode, SelectedActivityDate;
    public static int SelectedPosition;
    @SuppressLint("StaticFieldLeak")
    public static saneforce.sanzen.databinding.ActivityDcrCallApprovalBinding dcrCallApprovalBinding;
    public static ArrayList<DcrDetailModelList> dcrDetailedList = new ArrayList<>();
    public static ArrayList<SaveCallProductList> SaveProductList = new ArrayList<>();
    public static ArrayList<SaveCallInputList> saveInputList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static AdapterCusMainList adapterCusMainList;
    public static ArrayList<AdapterModel> adapterModels = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static AdapterSelectionList adapterSelectionList;
    static int countAll = 0, countDr = 0, countChem = 0, countStk = 0, countUnDr = 0;
    static StringBuilder ClusterNames = new StringBuilder();
    public JSONObject jsonDcrContentList = new JSONObject();
    public ProgressDialog progressDialog = null;
    public ApiInterface api_interface;

    JSONObject jsonDcrList = new JSONObject();
    JSONObject jsonAccept = new JSONObject();
    JSONObject jsonReject = new JSONObject();
    ArrayList<DCRApprovalList> dcrApprovalLists = new ArrayList<>();
    AdapterDcrApprovalList adapterDcrApprovalList;
    Dialog dialogReject;
    StringBuilder productPromoted = new StringBuilder();
    CommonUtilsMethods commonUtilsMethods;

    private static void SetupAdapter(Context context) {
        adapterModels.add(new AdapterModel("All", String.valueOf(countAll)));
        if (SharedPref.getDrNeed(context).equalsIgnoreCase("0"))
            adapterModels.add(new AdapterModel(SharedPref.getDrCap(context), String.valueOf(countDr)));
        if (SharedPref.getChmNeed(context).equalsIgnoreCase("0"))
            adapterModels.add(new AdapterModel(SharedPref.getChmCap(context), String.valueOf(countChem)));
        if (SharedPref.getStkNeed(context).equalsIgnoreCase("0"))
            adapterModels.add(new AdapterModel(SharedPref.getStkCap(context), String.valueOf(countStk)));
        if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0"))
            adapterModels.add(new AdapterModel(SharedPref.getUNLcap(context), String.valueOf(countUnDr)));
        if (SharedPref.getCipNeed(context).equalsIgnoreCase("0"))
            adapterModels.add(new AdapterModel(SharedPref.getCipCaption(context), "0"));
        if (SharedPref.getHospNeed(context).equalsIgnoreCase("0"))
            adapterModels.add(new AdapterModel(SharedPref.getHospCaption(context), "0"));

        adapterCusMainList = new AdapterCusMainList(context, dcrDetailedList, SaveProductList, saveInputList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        dcrCallApprovalBinding.rvDcrContentList.setLayoutManager(mLayoutManager);
        dcrCallApprovalBinding.rvDcrContentList.setAdapter(adapterCusMainList);

        adapterSelectionList = new AdapterSelectionList(context, adapterModels, dcrDetailedList, adapterCusMainList, SharedPref.getDrCap(context), SharedPref.getChmCap(context), SharedPref.getStkCap(context), SharedPref.getUNLcap(context));
        dcrCallApprovalBinding.rvSelectionList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        dcrCallApprovalBinding.rvSelectionList.setAdapter(adapterSelectionList);
        if (!ClusterNames.toString().isEmpty()) {
            dcrCallApprovalBinding.tvCluster1.setText(removeLastChar(ClusterNames.toString()));
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
            jsonDcrContentList = CommonUtilsMethods.CommonObjectParameter(DcrApprovalActivity.this);
            jsonDcrContentList.put("tableName", "getvwdcrone");
            jsonDcrContentList.put("Trans_SlNo", SelectedTransCode);
            jsonDcrContentList.put("sfcode", SelectedSfCode);
            jsonDcrContentList.put("division_code", SharedPref.getDivisionCode(this));
            jsonDcrContentList.put("Rsf", SharedPref.getHqCode(this));
            Log.v("json_get_full_dcr_list", jsonDcrContentList.toString());

        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetDetailedList = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonDcrContentList.toString());

        callGetDetailedList.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
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
                        String PrdPromoted;
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

                            if (json.has("promoted_product"))
                                productPromoted = getList(json.getString("promoted_product"));

                            dcrDetailedList.add(new DcrDetailModelList(dcrCallApprovalBinding.tvName.getText().toString(), json.getString("Trans_Detail_Name"), json.getString("Trans_Detail_Info_Code"), json.getString("Type"), json.getString("Trans_Detail_Info_Type"), json.getString("SDP_Name"), json.getString("pob"), json.getString("remarks"), json.getString("jointwrk"), json.getString("Call_Feedback"), json.getString("visitTime"), json.getString("ModTime"), json.getString("Trans_SlNo"), json.getString("Trans_Detail_Slno")));

                            //Extract Product Values
                            String PrdName, PrdSamQty, PrdRxQty;
                            if (!json.getString("products").isEmpty()) {
                                String[] StrArray = json.getString("products").split(",");
                                for (String value : StrArray) {
                                    if (!value.equalsIgnoreCase("  )")) {
                                        PrdName = value.substring(0, value.indexOf('(')).trim();

                                        PrdSamQty = value.substring(value.indexOf("(") + 1);
                                        PrdSamQty = PrdSamQty.substring(0, PrdSamQty.indexOf(")"));

                                        PrdRxQty = value.substring(value.indexOf(")") + 1).trim();
                                        if (PrdRxQty.contains("(")) {
                                            PrdRxQty = PrdRxQty.substring(PrdRxQty.indexOf("(") + 1);
                                            PrdRxQty = PrdRxQty.substring(0, PrdRxQty.indexOf(")"));
                                        } else {
                                            PrdRxQty = "0";
                                        }
                                        if (productPromoted.toString().contains(PrdName)) {
                                            SaveProductList.add(new SaveCallProductList(json.getString("Trans_Detail_Name"), PrdName, PrdSamQty, PrdRxQty, "0", "Yes"));
                                        } else {
                                            SaveProductList.add(new SaveCallProductList(json.getString("Trans_Detail_Name"), PrdName, PrdSamQty, PrdRxQty, "0", "No"));
                                        }
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
                                        InpQty = value.substring(value.indexOf("(") + 1);
                                        InpQty = InpQty.substring(0, InpQty.indexOf(")"));
                                        saveInputList.add(new SaveCallInputList(json.getString("Trans_Detail_Name"), InpName, InpQty));
                                    }
                                }
                            }
                        }
                        progressDialog.dismiss();
                        SetupAdapter(DcrApprovalActivity.this);
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        SetupAdapter(DcrApprovalActivity.this);
                        Log.v("extract", "--error--" + e);
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
                    SetupAdapter(DcrApprovalActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
                SetupAdapter(context);
            }
        });
    }

    private StringBuilder getList(String s) {
        String[] clstarrrayqty = s.split("#");
        StringBuilder ss1 = new StringBuilder();
        for (String value : clstarrrayqty) {
            ss1.append(value.substring(value.lastIndexOf("$") + 1)).append(",");
        }
        return new StringBuilder(ss1.substring(0, ss1.length() - 1));
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            dcrCallApprovalBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(HomeDashBoard.selectedDate != null) {
            outState.putString("date", HomeDashBoard.selectedDate.toString());
        }
        outState.putBoolean("isSaved", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrCallApprovalBinding = saneforce.sanzen.databinding.ActivityDcrCallApprovalBinding.inflate(getLayoutInflater());
        setContentView(dcrCallApprovalBinding.getRoot());
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        if(savedInstanceState != null && savedInstanceState.getBoolean("isSaved")) {
            if(savedInstanceState.getString("date") != null) {
                HomeDashBoard.selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
            }
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                CommonAlertBox.permissionChangeAlert(this);
            }
        }

        dcrCallApprovalBinding.tagCluster1.setText(SharedPref.getClusterCap(DcrApprovalActivity.this));

        if (UtilityClass.isNetworkAvailable(this)) {
            CallDcrListApi();
        } else {
            commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.no_network));
        }
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
            Intent intent = new Intent(DcrApprovalActivity.this, ApprovalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        dcrCallApprovalBinding.btnApproved.setOnClickListener(view -> {
            if (UtilityClass.isNetworkAvailable(this)) {
                CallApprovalApi();
            } else {
                commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.no_network));
            }
        });


        dcrCallApprovalBinding.btnReject.setOnClickListener(view -> {
            dialogReject = new Dialog(DcrApprovalActivity.this);
            dialogReject.setContentView(R.layout.popup_reject);
            Objects.requireNonNull(dialogReject.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogReject.setCancelable(false);

            ImageView iv_close = dialogReject.findViewById(R.id.img_close);
            EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
            Button btn_cancel = dialogReject.findViewById(R.id.btn_cancel);
            Button btn_reject = dialogReject.findViewById(R.id.btn_reject);

            ed_reason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_reason,250)});

            btn_cancel.setOnClickListener(view1 -> {
                ed_reason.setText("");
                dialogReject.dismiss();
                UtilityClass.hideKeyboard(DcrApprovalActivity.this);
            });

            iv_close.setOnClickListener(view12 -> {
                ed_reason.setText("");
                dialogReject.dismiss();
                UtilityClass.hideKeyboard(DcrApprovalActivity.this);
            });

            btn_reject.setOnClickListener(view14 -> {
                if (UtilityClass.isNetworkAvailable(this)) {
                    if (!TextUtils.isEmpty(ed_reason.getText().toString())) {
                        rejectApproval(ed_reason.getText().toString());
                    } else {
                        commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_enter_reason_for_reject));
                    }
                } else {
                    commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.no_network));
                }

            });
            dialogReject.show();
        });

    }

    private void rejectApproval(String toString) {
        progressDialog = CommonUtilsMethods.createProgressDialog(DcrApprovalActivity.this);
        try {
            jsonReject = CommonUtilsMethods.CommonObjectParameter(DcrApprovalActivity.this);
            jsonReject.put("tableName", "dcrreject");
            jsonReject.put("date", SelectedActivityDate);
            jsonReject.put("reason", toString);
            jsonReject.put("sfcode", SelectedSfCode);
            jsonReject.put("division_code", SharedPref.getDivisionCode(this));
            jsonReject.put("Rsf", SharedPref.getHqCode(this));
            Log.v("json_send_approval", jsonReject.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");
        Call<JsonElement> callDcrReject = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonReject.toString());
        callDcrReject.enqueue(new Callback<JsonElement>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {

                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.rejected_successfully));
                            dialogReject.dismiss();
                            removeSelectedData();
                            DcrCount--;
                        }
                    } catch (Exception e) {
                        dialogReject.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
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
        progressDialog = CommonUtilsMethods.createProgressDialog(DcrApprovalActivity.this);
        try {
            jsonAccept = CommonUtilsMethods.CommonObjectParameter(DcrApprovalActivity.this);
            jsonAccept.put("tableName", "dcrapproval");
            jsonAccept.put("date", SelectedActivityDate);
            jsonAccept.put("sfcode", SelectedSfCode);
            jsonAccept.put("division_code", SharedPref.getDivisionCode(this));
            jsonAccept.put("Rsf", SharedPref.getHqCode(this));
            Log.v("json_send_approval", jsonAccept.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");
        Call<JsonElement> callDcrApproval = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonAccept.toString());
        callDcrApproval.enqueue(new Callback<JsonElement>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.approved_successfully));
                            removeSelectedData();
                            DcrCount--;
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
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
            jsonDcrList = CommonUtilsMethods.CommonObjectParameter(DcrApprovalActivity.this);
            jsonDcrList.put("tableName", "getvwdcr");
            jsonDcrList.put("sfcode", SharedPref.getSfCode(this));
            jsonDcrList.put("division_code", SharedPref.getDivisionCode(this));
            jsonDcrList.put("Rsf", SharedPref.getHqCode(this));
            Log.v("json_getDcr_list", jsonDcrList.toString());
        } catch (Exception ignored) {

        }


        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetDcrList = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonDcrList.toString());
        callGetDcrList.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            dcrApprovalLists.add(new DCRApprovalList(json.getString("Trans_SlNo"), json.getString("Sf_Name"), json.getString("Activity_Date"), json.getString("Plan_Name"), json.getString("WorkType_Name"), json.getString("Sf_Code"), json.getString("FieldWork_Indicator"), json.getString("Submission_Date"), json.getString("Hlfday"), json.getString("Remarks"), json.getString("Additional_Temp_Details")));
                        }
                        adapterDcrApprovalList = new AdapterDcrApprovalList(DcrApprovalActivity.this, DcrApprovalActivity.this, dcrApprovalLists, DcrApprovalActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        dcrCallApprovalBinding.rvDcrList.setLayoutManager(mLayoutManager);
                        dcrCallApprovalBinding.rvDcrList.setAdapter(adapterDcrApprovalList);
                    } catch (Exception ignored) {

                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(DcrApprovalActivity.this, getString(R.string.toast_response_failed));
            }
        });
    }


    @Override
    public void onClick(DCRApprovalList dcrApprovalList, int pos) {

        dcrCallApprovalBinding.tvName.setText(dcrApprovalList.getSf_name());
        if (dcrApprovalList.getAdditional_Temp_Details().equalsIgnoreCase(""))
            dcrCallApprovalBinding.tvWorktype1.setText(dcrApprovalList.getWorkType_name());
        else
            dcrCallApprovalBinding.tvWorktype1.setText(dcrApprovalList.getWorkType_name() + " / " + dcrApprovalList.getAdditional_Temp_Details());

        dcrCallApprovalBinding.tvRemark1.setText(dcrApprovalList.getRemarks());
        dcrCallApprovalBinding.tvActivityDate.setText(dcrApprovalList.getActivity_date());
        dcrCallApprovalBinding.tvSubmittedDate.setText(dcrApprovalList.getSubmission_date_sub());
        SelectedTransCode = dcrApprovalList.getTrans_slNo();
        SelectedSfCode = dcrApprovalList.getSfCode();
        SelectedActivityDate = dcrApprovalList.getActivity_date();
        SelectedPosition = pos;

        getDcrContentList();
        if (dcrApprovalList.getFieldWork_indicator().equalsIgnoreCase("F")) {
            dcrCallApprovalBinding.rvSelectionList.setVisibility(View.VISIBLE);
        } else {
            dcrCallApprovalBinding.rvSelectionList.setVisibility(View.GONE);
        }
        dcrCallApprovalBinding.constraintDcrListContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickDcrDetail(DcrDetailModelList dcrDetailModelList) {

    }

    @Override
    public void onItemClick(TpModelList tpModelLists, int pos) {

    }

}