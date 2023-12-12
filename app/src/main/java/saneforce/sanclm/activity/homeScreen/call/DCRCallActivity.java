package saneforce.sanclm.activity.homeScreen.call;

import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.callAddedJointList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.callCaptureImageLists;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.jwothersBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPASelectCompSide.rcpa_comp_list;
import static saneforce.sanclm.activity.profile.CustomerProfile.isDetailingRequired;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.call.adapter.DCRCallTabLayoutAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.AdditionalCusListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.CheckInputListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.product.CheckProductListAdapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.call.fragments.AddCallSelectInpSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AddCallSelectPrdSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.DetailedFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.FeedbackSelectionSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.InputFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JointworkSelectionSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPASelectCompSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPASelectPrdSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.GPSTrack;
import saneforce.sanclm.databinding.ActivityDcrcallBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.CustomSetupResponse;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.response.SetupResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class DCRCallActivity extends AppCompatActivity {

    public static ArrayList<CustList> CallActivityCustDetails;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrcallBinding dcrCallBinding;
    public static String PobNeed, OverallFeedbackNeed, EventCaptureNeed, JwNeed, SampleValidation, InputValidation, PrdSamNeed, PrdRxNeed, CapSamQty, CapRxQty, RcpaCompetitorAdd;
    public static ArrayList<CallCommonCheckedList> StockSample = new ArrayList<>();
    public static ArrayList<CallCommonCheckedList> StockInput = new ArrayList<>();
    public static String isClickedInput;
    String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode;
    DCRCallTabLayoutAdapter viewPagerAdapter;
    CommonUtilsMethods commonUtilsMethods;
    SQLite sqLite;
    ProgressDialog progressDialog = null;
    SetupResponse setUpResponse;
    CustomSetupResponse customSetupResponse;
    GPSTrack gpsTrack;
    LoginResponse loginResponse;
    JSONObject jsonSaveDcr;
    String GeoChk, capPrd, capInp, RCPANeed, HosNeed, FeedbackMandatory, CurrentDate, MgrRcpaMandatory, EventCapMandatory, JwMandatory, CurrentTime, PrdMandatory, InpMandatory, RcpaMandatory, PobMandatory, RemarkMandatory, SamQtyMandatory, RxQtyMandatory, RCPAWOSample;
    double lat, lng;
    ApiInterface api_interface;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrCallBinding = ActivityDcrcallBinding.inflate(getLayoutInflater());
        setContentView(dcrCallBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        sqLite = new SQLite(this);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

        getRequiredData();

        viewPagerAdapter = new DCRCallTabLayoutAdapter(getSupportFragmentManager());

        if (isDetailingRequired) {
            viewPagerAdapter.add(new DetailedFragment(), "Detailed");
        }

        if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
            viewPagerAdapter.add(new ProductFragment(), capPrd);
            viewPagerAdapter.add(new InputFragment(), capInp);
            if (RCPANeed.equalsIgnoreCase("1")) {
                viewPagerAdapter.add(new RCPAFragment(), "RCPA");
            }
            viewPagerAdapter.add(new AdditionalCallFragment(), "Additional Calls");
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
            viewPagerAdapter.add(new ProductFragment(), capPrd);
            viewPagerAdapter.add(new InputFragment(), capInp);
            if (RCPANeed.equalsIgnoreCase("1")) {
                viewPagerAdapter.add(new RCPAFragment(), "RCPA");
            }
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            viewPagerAdapter.add(new ProductFragment(), capPrd);
            viewPagerAdapter.add(new InputFragment(), capInp);
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
            viewPagerAdapter.add(new ProductFragment(), capPrd);
            viewPagerAdapter.add(new InputFragment(), capInp);
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("5")) {
            viewPagerAdapter.add(new ProductFragment(), "Product");
            viewPagerAdapter.add(new InputFragment(), "Input");
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        }

        dcrCallBinding.viewPager.setAdapter(viewPagerAdapter);
        dcrCallBinding.tabLayout.setupWithViewPager(dcrCallBinding.viewPager);
        dcrCallBinding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        dcrCallBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Objects.requireNonNull(tab.getText()).toString().equalsIgnoreCase(capInp)) {
                    isClickedInput = "Input";
                } else if (tab.getText().toString().equalsIgnoreCase("Additional Calls")) {
                    isClickedInput = "Additional Call";
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dcrCallBinding.tabLayout.getWindowToken(), 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        AddProductsData();
        AddInputData();
        AddAdditionalCallData();
        AddRCPAData();
        AddJWData();

        dcrCallBinding.tagCustName.setText(CallActivityCustDetails.get(0).getName());

        dcrCallBinding.ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
            startActivity(intent);
        });

        dcrCallBinding.btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
            startActivity(intent);
        });

        dcrCallBinding.btnFinalSubmit.setOnClickListener(view -> {
            progressDialog = CommonUtilsMethods.createProgressDialog(DCRCallActivity.this);
            if (CheckRequiredFunctions() && CheckCurrentLoc()) {
                CreateJsonFileCall();
                CallSaveDcrAPI(jsonSaveDcr.toString());
            } else {
                progressDialog.dismiss();
            }
        });
    }

    public boolean CheckRequiredFunctions() {
        switch (CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (SfType.equalsIgnoreCase("1")) {
                    if (PrdMandatory.equalsIgnoreCase("1")) {
                        if (CheckProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter the " + capPrd, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    if (PrdSamNeed.equalsIgnoreCase("1") && SamQtyMandatory.equalsIgnoreCase("1")) {
                        if (CheckProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter the " + capPrd, Toast.LENGTH_LONG).show();
                            return false;
                        } else {
                            for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().isEmpty() || CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().equalsIgnoreCase("0")) {
                                    Toast.makeText(getApplicationContext(), "Enter the " + CapSamQty + " Values", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            }
                        }
                    }

                    if (PrdRxNeed.equalsIgnoreCase("1") && RxQtyMandatory.equalsIgnoreCase("1")) {
                        if (CheckProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter the " + capPrd, Toast.LENGTH_LONG).show();
                            return false;
                        } else {
                            for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().isEmpty() || CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().equalsIgnoreCase("0")) {
                                    Toast.makeText(getApplicationContext(), "Enter the " + CapRxQty + " Values", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            }
                        }
                    }

                    if (InpMandatory.equalsIgnoreCase("1")) {
                        if (CheckInputListAdapter.saveCallInputListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Select the " + capInp, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    if (RCPANeed.equalsIgnoreCase("0") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Add RCPA Values", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                } else {
                    if (MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Add RCPA Values", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }

                if (JwMandatory.equalsIgnoreCase("0")) {
                    if (callAddedJointList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "JointWork Needed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                if (PobNeed.equalsIgnoreCase("0") && PobMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(jwothersBinding.edPob.getText()).toString().isEmpty() || jwothersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Add Pob values", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                if (FeedbackMandatory.equalsIgnoreCase("1")) {
                    if (jwothersBinding.tvFeedback.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Add Feedback", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                if (RemarkMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(jwothersBinding.edRemarks.getText()).toString().isEmpty() || jwothersBinding.edRemarks.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Add Remarks", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Event Capture Needed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }


                for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
                    ArrayList<String> dummyChk = new ArrayList<>();
                    for (int j = 0; j < rcpa_comp_list.size(); j++) {
                        if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(j).getPrd_code())) {
                            dummyChk.add(rcpa_comp_list.get(j).getChem_Code());
                        }
                    }

                    if (dummyChk.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Need Competitor for Products", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                for (int i = 0; i < rcpa_comp_list.size(); i++) {
                    if (rcpa_comp_list.get(i).getQty().equalsIgnoreCase("0") || rcpa_comp_list.get(i).getQty().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "RCPA Competitor Need Qty", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                break;
            case "2":
                if (SfType.equalsIgnoreCase("1")) {
                    if (RCPANeed.equalsIgnoreCase("1") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Add RCPA Values", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                } else {
                    if (MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Add RCPA Values", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }

                if (JwMandatory.equalsIgnoreCase("0")) {
                    if (callAddedJointList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "JointWork Needed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                if (PobNeed.equalsIgnoreCase("0") && PobMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(jwothersBinding.edPob.getText()).toString().isEmpty() || jwothersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Add Pob Values", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Event Capture Needed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
                    ArrayList<String> dummyChk = new ArrayList<>();
                    for (int j = 0; j < rcpa_comp_list.size(); j++) {
                        if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(j).getPrd_code())) {
                            dummyChk.add(rcpa_comp_list.get(j).getChem_Code());
                        }
                    }

                    if (dummyChk.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Need Competitor for Products", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                for (int i = 0; i < rcpa_comp_list.size(); i++) {
                    if (rcpa_comp_list.get(i).getQty().equalsIgnoreCase("0") || rcpa_comp_list.get(i).getQty().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "RCPA Competitor Need Qty", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                break;
            case "3":
            case "4":
                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Event Capture Needed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                if (JwMandatory.equalsIgnoreCase("0")) {
                    if (callAddedJointList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "JointWork Needed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                break;
            case "5":
                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Event Capture Needed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    public boolean CheckCurrentLoc() {
        boolean val = false;
        if (GeoChk.equalsIgnoreCase("0")) {
            gpsTrack = new GPSTrack(DCRCallActivity.this);
            try {
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    new android.app.AlertDialog.Builder(DCRCallActivity.this).setTitle("Alert") // GPS not found
                            .setCancelable(false).setMessage("Activate the Gps to proceed further") // Want to enable?
                            .setPositiveButton("Yes", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
                } else {
                    val = true;
                }
            } catch (Exception ignored) {
            }
        } else {
            val = true;
        }
        return val;
    }

    private void CallSaveDcrAPI(String jsonSaveDcr) {
        // progressDialog = CommonUtilsMethods.createProgressDialog(DCRCallActivity.this);
        Log.v("callSave", "---" + jsonSaveDcr);
        JSONObject jsonImage = new JSONObject();
        try {
            jsonImage.put("tableName", "uploadphoto");
            jsonImage.put("sfcode", SfCode);
            jsonImage.put("division_code", DivCode);
            jsonImage.put("Rsf", TodayPlanSfCode);
            jsonImage.put("sf_type", SfType);
            jsonImage.put("Designation", Designation);
            jsonImage.put("state_code", StateCode);
            jsonImage.put("subdivision_code", SubDivisionCode);
        } catch (Exception ignored) {

        }
        if (callCaptureImageLists.size() > 0) {
            for (int i = 0; i < callCaptureImageLists.size(); i++) {
                Log.v("ImgUpload", callCaptureImageLists.get(i).getFilePath());
                MultipartBody.Part img = convertImg("EventImg", callCaptureImageLists.get(i).getFilePath());
                HashMap<String, RequestBody> values = field(jsonImage.toString());
                Call<JsonObject> saveImgDcr = api_interface.saveImgDcr(values, img);
                saveImgDcr.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            try {
                                assert response.body() != null;
                                JSONObject json = new JSONObject(response.body().toString());
                                Log.v("ImgUpload", json.toString());
                                json.getString("success");// Toast.makeText(DCRCallActivity.this, "Picture Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            } catch (Exception ignored) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                    }
                });
            }
        }

        Call<JsonObject> callSaveDcr;
        callSaveDcr = api_interface.saveDcr(jsonSaveDcr);
        callSaveDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                        Log.v("callSave", "---" + jsonSaveRes);
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                            Toast.makeText(DCRCallActivity.this, "Call Successfully Saved", Toast.LENGTH_SHORT).show();
                            UpdateInputStock();
                            UpdateSampleStock();
                        } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                            Toast.makeText(DCRCallActivity.this, "Call Already Exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DCRCallActivity.this, "Call Failed! Try again", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                        startActivity(intent);

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DCRCallActivity.this, "Call Saved! Something Wrong", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                        startActivity(intent);
                        Log.v("callSave", "---" + e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.v("callSave", "---" + t);
                progressDialog.dismiss();
                Toast.makeText(DCRCallActivity.this, "Call Failed! Try again", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                startActivity(intent);
                UpdateInputStock();
            }
        });
    }


    private void UpdateSampleStock() {
        try {
            JSONArray jsonArraySamStk = sqLite.getMasterSyncDataByKey(Constants.STOCK_BALANCE);
            for (int i = 0; i < StockSample.size(); i++) {
                //SampleStockChange
                for (int j = 0; j < jsonArraySamStk.length(); j++) {
                    JSONObject jsonObject = jsonArraySamStk.getJSONObject(j);
                    Log.v("chkSamStk", StockSample.get(i).getCode() + "-----" + jsonObject.getString("Code") + "----" + StockSample.get(i).getCode().equalsIgnoreCase(jsonObject.getString("Code")));
                    if (StockSample.get(i).getCode().equalsIgnoreCase(jsonObject.getString("Code"))) {
                        if (!StockSample.get(i).getCurrentStock().equalsIgnoreCase(jsonObject.getString("Balance_Stock"))) {
                            jsonObject.remove("Balance_Stock");
                            jsonObject.put("Balance_Stock", Integer.parseInt(StockSample.get(i).getCurrentStock()));
                            break;
                        }
                    }
                }
            }
            sqLite.saveMasterSyncData(Constants.STOCK_BALANCE, jsonArraySamStk.toString(), 0);
        } catch (Exception ignored) {
        }
    }

    private void UpdateInputStock() {
        try {
            JSONArray jsonArrayInpStk = sqLite.getMasterSyncDataByKey(Constants.INPUT_BALANCE);
            for (int i = 0; i < StockInput.size(); i++) {
                //InputStockChange
                for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                    JSONObject jsonObject = jsonArrayInpStk.getJSONObject(j);
                    Log.v("chkInpStk", StockInput.get(i).getStockCode() + "-----" + jsonObject.getString("Code") + "----" + StockInput.get(i).getStockCode().equalsIgnoreCase(jsonObject.getString("Code")));
                    if (StockInput.get(i).getStockCode().equalsIgnoreCase(jsonObject.getString("Code"))) {
                        if (!StockInput.get(i).getCurrentStock().equalsIgnoreCase(jsonObject.getString("Balance_Stock"))) {
                            jsonObject.remove("Balance_Stock");
                            jsonObject.put("Balance_Stock", Integer.parseInt(StockInput.get(i).getCurrentStock()));
                            break;
                        }
                    }
                }
            }
            sqLite.saveMasterSyncData(Constants.INPUT_BALANCE, jsonArrayInpStk.toString(), 0);
        } catch (Exception e) {
            Log.v("chkInpStk", "error---" + e);
        }
    }

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(txt, MultipartBody.FORM);
    }

    public MultipartBody.Part convertImg(String tag, String path) {
        Log.d("path", tag + "-" + path);
        MultipartBody.Part yy = null;
        try {
            File file;
            if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(getApplicationContext()).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("path", String.valueOf(yy));
        } catch (Exception ignored) {
        }
        return yy;
    }


    private void CreateJsonFileCall() {
        try {
            CurrentDate = CommonUtilsMethods.getCurrentInstance();
            CurrentTime = CommonUtilsMethods.getCurrentTime();
            gpsTrack = new GPSTrack(this);
            lat = gpsTrack.getLatitude();
            lng = gpsTrack.getLongitude();

            JSONArray jsonArray = new JSONArray();
            jsonSaveDcr = new JSONObject();

            //JointWork
            for (int i = 0; i < JWOthersFragment.callAddedJointList.size(); i++) {
                JSONObject json_joint = new JSONObject();
                json_joint.put("Code", JWOthersFragment.callAddedJointList.get(i).getCode());
                json_joint.put("Name", JWOthersFragment.callAddedJointList.get(i).getName());
                jsonArray.put(json_joint);
            }
            jsonSaveDcr.put("JointWork", jsonArray);

            //Input
            jsonArray = new JSONArray();
            for (int i = 0; i < CheckInputListAdapter.saveCallInputListArrayList.size(); i++) {
                JSONObject json_joint = new JSONObject();
                json_joint.put("Code", CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_code());
                json_joint.put("Name", CheckInputListAdapter.saveCallInputListArrayList.get(i).getInput_name());
                json_joint.put("IQty", CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty());
                jsonArray.put(json_joint);
            }
            jsonSaveDcr.put("Inputs", jsonArray);

            //Products
            jsonArray = new JSONArray();
            for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                JSONObject json_product = new JSONObject();
                json_product.put("Code", CheckProductListAdapter.saveCallProductListArrayList.get(i).getCode());
                json_product.put("Name", CheckProductListAdapter.saveCallProductListArrayList.get(i).getName());
                json_product.put("Group", "0");
                json_product.put("ProdFeedbk", "");
                json_product.put("Rating", "");
                JSONObject json_date = new JSONObject();
                json_date.put("sTm", CurrentDate + " " + CurrentTime);
                json_date.put("eTm", CurrentDate + " " + CurrentTime);
                json_product.put("Timesline", json_date);
                json_product.put("Appver", Constants.APP_VERSION);
                json_product.put("Mod", Constants.APP_MODE);
                json_product.put("SmpQty", CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty());
                json_product.put("RxQty", CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty());
                json_product.put("Type", CallActivityCustDetails.get(0).getType());
                json_product.put("StockistName", "");
                json_product.put("StockistCode", "");
                JSONArray jsonArraySlides = new JSONArray();
                json_product.put("Slides", jsonArraySlides);
                jsonArray.put(json_product);
            }
            jsonSaveDcr.put("Products", jsonArray);

          /*  //Additional Call
            jsonArray = new JSONArray();
            for (int i = 0; i < CallAddCustListAdapter.saveAdditionalCallArrayList.size(); i++) {
                JSONObject json_AdditionalCall = new JSONObject();
                json_AdditionalCall.put("Code", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getCode());
                json_AdditionalCall.put("Name", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getName());
                json_AdditionalCall.put("town_code", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getTown_code());
                json_AdditionalCall.put("town_name", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getTown_name());
                jsonArray.put(json_AdditionalCall);
            }
            jsonSaveDcr.put("AdCuss", jsonArray);*/

            //Additional Call
            jsonArray = new JSONArray();
            for (int i = 0; i < AdditionalCusListAdapter.saveAdditionalCallArrayList.size(); i++) {
                JSONArray jsonArraySample = new JSONArray();
                JSONArray jsonArrayInput = new JSONArray();

                JSONObject json_AdditionalCall = new JSONObject();
                json_AdditionalCall.put("Code", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getCode());
                json_AdditionalCall.put("Name", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getName());
                json_AdditionalCall.put("town_code", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getTown_code());
                json_AdditionalCall.put("town_name", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getTown_name());

                //Additional Call Samples
                for (int j = 0; j < FinalAdditionalCallAdapter.nestedProduct.size(); j++) {
                    JSONObject json_AdditionalInput = new JSONObject();
                    if (FinalAdditionalCallAdapter.nestedProduct.get(j).getCust_code().equalsIgnoreCase(AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getCode())) {
                        if (!FinalAdditionalCallAdapter.nestedProduct.get(j).getPrd_name().equalsIgnoreCase("Select") && !FinalAdditionalCallAdapter.nestedProduct.get(j).getPrd_name().isEmpty()) {
                            json_AdditionalInput.put("Code", FinalAdditionalCallAdapter.nestedProduct.get(j).getPrd_code());
                            json_AdditionalInput.put("Name", FinalAdditionalCallAdapter.nestedProduct.get(j).getPrd_name());
                            json_AdditionalInput.put("SamQty", FinalAdditionalCallAdapter.nestedProduct.get(j).getSample_qty());
                            jsonArraySample.put(json_AdditionalInput);
                        }
                    }
                }

                //Additional Call Inputs
                for (int j = 0; j < FinalAdditionalCallAdapter.nestedInput.size(); j++) {
                    JSONObject json_AdditionalSample = new JSONObject();
                    if (FinalAdditionalCallAdapter.nestedInput.get(j).getCust_code().equalsIgnoreCase(AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getCode())) {
                        if (!FinalAdditionalCallAdapter.nestedInput.get(j).getInput_name().equalsIgnoreCase("Select") && !FinalAdditionalCallAdapter.nestedInput.get(j).getInput_name().isEmpty()) {
                            json_AdditionalSample.put("Code", FinalAdditionalCallAdapter.nestedInput.get(j).getInput_code());
                            json_AdditionalSample.put("Name", FinalAdditionalCallAdapter.nestedInput.get(j).getInput_name());
                            json_AdditionalSample.put("SmpQty", FinalAdditionalCallAdapter.nestedInput.get(j).getInp_qty());
                            jsonArrayInput.put(json_AdditionalSample);
                        }
                    }
                }

                json_AdditionalCall.put("Products", jsonArraySample);
                json_AdditionalCall.put("Inputs", jsonArrayInput);
                jsonArray.put(json_AdditionalCall);
            }
            jsonSaveDcr.put("AdCuss", jsonArray);


            //RCPA
            jsonArray = new JSONArray();
            for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
                JSONObject json_object = new JSONObject();
                JSONArray jsonArrayComp = new JSONArray();

                JSONArray jsonArrayChemist = new JSONArray();
                JSONObject json_che = new JSONObject();
                json_che.put("Name", RCPAFragment.ProductSelectedList.get(i).getChem_names());
                json_che.put("Code", RCPAFragment.ProductSelectedList.get(i).getChe_codes());
                jsonArrayChemist.put(json_che);
                json_object.put("Chemists", jsonArrayChemist);

                json_object.put("OPCode", RCPAFragment.ProductSelectedList.get(i).getPrd_code());
                json_object.put("OPName", RCPAFragment.ProductSelectedList.get(i).getPrd_name());
                json_object.put("OPQty", RCPAFragment.ProductSelectedList.get(i).getQty());
                json_object.put("OPRate", RCPAFragment.ProductSelectedList.get(i).getRate());
                json_object.put("OPValue", RCPAFragment.ProductSelectedList.get(i).getValue());
                json_object.put("OPTotal", RCPAFragment.ProductSelectedList.get(i).getTotalPrdValue());

                for (int j = 0; j < rcpa_comp_list.size(); j++) {
                    JSONObject json_Obj_comp = new JSONObject();
                    if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(j).getPrd_code())) {
                        json_Obj_comp.put("CPQty", rcpa_comp_list.get(j).getQty());
                        json_Obj_comp.put("CPRate", rcpa_comp_list.get(j).getRate());
                        json_Obj_comp.put("CPValue", rcpa_comp_list.get(j).getValue());
                        json_Obj_comp.put("CompCode", rcpa_comp_list.get(j).getComp_company_code());
                        json_Obj_comp.put("CompName", rcpa_comp_list.get(j).getComp_company_name());
                        json_Obj_comp.put("CompPCode", rcpa_comp_list.get(j).getComp_product_code());
                        json_Obj_comp.put("CompPName", rcpa_comp_list.get(j).getComp_product());
                        json_Obj_comp.put("Chemname", rcpa_comp_list.get(j).getChem_names());
                        json_Obj_comp.put("Chemcode", rcpa_comp_list.get(j).getChem_Code());
                        json_Obj_comp.put("CPRemarks", rcpa_comp_list.get(j).getRemarks());
                        jsonArrayComp.put(json_Obj_comp);
                        json_object.put("Competitors", jsonArrayComp);
                    }
                }
                jsonArray.put(json_object);
            }
            jsonSaveDcr.put("RCPAEntry", jsonArray);

            //Customer Details
            jsonSaveDcr.put("CateCode", CallActivityCustDetails.get(0).getCategoryCode());
            jsonSaveDcr.put("CusType", CallActivityCustDetails.get(0).getType());
            jsonSaveDcr.put("CustCode", CallActivityCustDetails.get(0).getCode());
            jsonSaveDcr.put("CustName", CallActivityCustDetails.get(0).getName());
            jsonSaveDcr.put("Entry_location", lat + ":" + lng);
            jsonSaveDcr.put("address", CommonUtilsMethods.gettingAddress(this, lat, lng, false));
            jsonSaveDcr.put("sfcode", SfCode);
            jsonSaveDcr.put("Rsf", TodayPlanSfCode);
            jsonSaveDcr.put("sf_type", SfType);
            jsonSaveDcr.put("Designation", Designation);
            jsonSaveDcr.put("state_code", StateCode);
            jsonSaveDcr.put("subdivision_code", SubDivisionCode);
            jsonSaveDcr.put("division_code", DivCode);
            jsonSaveDcr.put("AppUserSF", TodayPlanSfCode);
            jsonSaveDcr.put("SFName", SfName);
            jsonSaveDcr.put("SpecCode", "2");
            jsonSaveDcr.put("mappedProds", "");
            jsonSaveDcr.put("mode", "0");
            jsonSaveDcr.put("Appver", Constants.APP_VERSION);
            jsonSaveDcr.put("Mod", Constants.APP_MODE);

            JSONArray jsonArrayWt;
            jsonArrayWt = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i = 0; i < jsonArrayWt.length(); i++) {
                JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
                if (workTypeData.getString("FWFlg").equalsIgnoreCase("F")) {
                    jsonSaveDcr.put("WT_code", workTypeData.getString("Code"));
                    jsonSaveDcr.put("WTName", workTypeData.getString("Name"));
                    jsonSaveDcr.put("FWFlg", workTypeData.getString("FWFlg"));
                }
            }
            jsonSaveDcr.put("town_code", CallActivityCustDetails.get(0).getTown_code());
            jsonSaveDcr.put("town_name", CallActivityCustDetails.get(0).getTown_name());
            jsonSaveDcr.put("ModTime", CurrentDate + " " + CurrentTime);
            jsonSaveDcr.put("ReqDt", CurrentDate + " " + CurrentTime);
            jsonSaveDcr.put("vstTime", CurrentDate + " " + CurrentTime);
            jsonSaveDcr.put("Remarks", jwothersBinding.edRemarks.getText());
            jsonSaveDcr.put("amc", "");
            if (!TextUtils.isEmpty(HosNeed)) {
                jsonSaveDcr.put("hospital_code", "");
                jsonSaveDcr.put("hospital_name", "");
            }

            //Validation
            jsonSaveDcr.put("sample_validation", SampleValidation);
            jsonSaveDcr.put("input_validation", InputValidation);

            //EventCapture
            jsonArray = new JSONArray();
            if (callCaptureImageLists.size() > 0) {
                for (int i = 0; i < callCaptureImageLists.size(); i++) {
                    JSONObject json_Eve_cap = new JSONObject();
                    json_Eve_cap.put("EventCapture", "True");
                    json_Eve_cap.put("EventImageName", callCaptureImageLists.get(i).getSystemImgName());
                    json_Eve_cap.put("EventImageNameUser", callCaptureImageLists.get(i).getImg_name());
                    json_Eve_cap.put("EventImageDescription", callCaptureImageLists.get(i).getImg_description());
                    json_Eve_cap.put("filepath", callCaptureImageLists.get(i).getImg_view());
                    jsonArray.put(json_Eve_cap);
                }
                jsonSaveDcr.put("EventCapture", jsonArray);
            }

            //POB
            String pobValue = Objects.requireNonNull(jwothersBinding.edPob.getText()).toString();
            if (PobNeed.equalsIgnoreCase("0") && !pobValue.isEmpty()) {
                jsonSaveDcr.put("DCSUPOB", pobValue);
            } else {
                jsonSaveDcr.put("DCSUPOB", "");
            }

            //FeedBack
            if (OverallFeedbackNeed.equalsIgnoreCase("0")) {
                jsonSaveDcr.put("Drcallfeedbackname", FeedbackSelectionSide.feedbackName);
                jsonSaveDcr.put("Drcallfeedbackcode", FeedbackSelectionSide.feedbackCode);
            } else {
                jsonSaveDcr.put("Drcallfeedbackname", "");
                jsonSaveDcr.put("Drcallfeedbackcode", "");
            }

            Log.v("final_value_call", String.valueOf(jsonSaveDcr));
        } catch (Exception e) {
            Log.v("final_value_call", "---error----" + e);
        }
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
            RcpaCompetitorAdd = loginResponse.getRCPA_competitor_add();
            EventCapMandatory = loginResponse.getCipEvent_Md();


            new JSONArray();
            JSONArray jsonArray;
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                setUpResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setUpResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);

                if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) { //Dr
                    //Caption
                    capPrd = setUpResponse.getCaptionDrPrd();
                    capInp = setUpResponse.getCaptionDrInp();
                    CapSamQty = setUpResponse.getCaptionDrSamQty();
                    CapRxQty = setUpResponse.getCaptionDrRxQty();

                    //Need
                    RCPANeed = setUpResponse.getDrRcpaNeed();
                    PobNeed = setUpResponse.getDrPobNeed();
                    OverallFeedbackNeed = setUpResponse.getDrFeedbackNeed();
                    EventCaptureNeed = setUpResponse.getDrEveCapNeed();
                    JwNeed = setUpResponse.getDrJwNeed();
                    PrdSamNeed = setUpResponse.getDrSamNeed();
                    PrdRxNeed = setUpResponse.getDrRxNeed();

                    //Mandatory
                    PrdMandatory = setUpResponse.getDrPrdMandatory();
                    InpMandatory = setUpResponse.getDrInpMandatory();
                    SamQtyMandatory = setUpResponse.getDrSamQtyMandatory();
                    RxQtyMandatory = setUpResponse.getDrRxQtyMandatory();
                    RcpaMandatory = setUpResponse.getRcpaMandatory();
                    MgrRcpaMandatory = setUpResponse.getMGRDrRcpaMandatory();
                    EventCapMandatory = setUpResponse.getDrEveCapMandatory();
                    PobMandatory = setUpResponse.getDrPobMandatory();
                    FeedbackMandatory = setUpResponse.getDrFeedbackMandatory();
                    JwMandatory = setUpResponse.getDrJwMandatory();
                    RemarkMandatory = setUpResponse.getDrRemarksMan_Need();

                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) { //Chemist
                    //Caption
                    capPrd = setUpResponse.getCaptionChemistPrd();
                    capInp = setUpResponse.getCaptionChemistInp();
                    CapSamQty = setUpResponse.getCaptionChemistSamQty();
                    CapRxQty = setUpResponse.getCaptionChemistRxQty();

                    //Need
                    RCPANeed = setUpResponse.getChemistRcpaNeed();
                    PobNeed = setUpResponse.getChemistPobNeed();
                    EventCaptureNeed = setUpResponse.getChemistEveCapNeed();
                    OverallFeedbackNeed = setUpResponse.getChemistFeedbackNeed();
                    JwNeed = setUpResponse.getChemistJwNeed();
                    PrdSamNeed = setUpResponse.getChemistSamNeed();
                    PrdRxNeed = setUpResponse.getChemistRxNeed();

                    //Mandatory
                    RcpaMandatory = setUpResponse.getRcpaMandatory();
                    MgrRcpaMandatory = setUpResponse.getMGRCheRcpaMandatory();
                    PobMandatory = setUpResponse.getChemistPobMandatory();
                    EventCapMandatory = setUpResponse.getChemistEveCapMandatory();
                    JwMandatory = setUpResponse.getChemistJwMandatory();

                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) { //Stockist
                    //Caption
                    capPrd = setUpResponse.getCaptionStokistPrd();
                    capInp = setUpResponse.getCaptionStokistInp();
                    CapSamQty = "Samples";
                    CapRxQty = setUpResponse.getCaptionStkRxQty();

                    //Need
                    OverallFeedbackNeed = setUpResponse.getStockistFeedbackNeed();
                    EventCaptureNeed = setUpResponse.getStockistEveCapNeed();
                    JwNeed = setUpResponse.getStockistJwNeed();
                    PrdSamNeed = "0";
                    PrdRxNeed = setUpResponse.getStockistRxNeed();

                    //Mandatory
                    EventCapMandatory = setUpResponse.getStockistEveCapMandatory();
                    JwMandatory = setUpResponse.getStockistJwMandatory();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) { //UnlistedDr
                    //Caption
                    capPrd = setUpResponse.getCaptionUndrPrd();
                    capInp = setUpResponse.getCaptionUndrInp();
                    CapSamQty = setUpResponse.getCaptionUndrSamQty();
                    CapRxQty = setUpResponse.getCaptionUndrRxQty();

                    //Need
                    OverallFeedbackNeed = setUpResponse.getUndrFeedbackNeed();
                    EventCaptureNeed = setUpResponse.getUndrEveCapNeed();
                    JwNeed = setUpResponse.getUndrJwNeed();
                    PrdSamNeed = "0";
                    PrdRxNeed = setUpResponse.getUndrRxNeed();

                    //Mandatory
                    EventCapMandatory = setUpResponse.getUndrEveCapMandatory();
                    JwMandatory = setUpResponse.getUndrJwMandatory();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("5")) { //CIP
                    //Caption

                    //Need
                    PobNeed = setUpResponse.getCipPobNeed();
                    OverallFeedbackNeed = setUpResponse.getCipFeedbackNeed();
                    EventCaptureNeed = "0";
                    JwNeed = "0";
                    PrdSamNeed = "0";
                    PrdRxNeed = "0";

                    //Mandatory
                    PobMandatory = setUpResponse.getCipPobMandatory();
                }
                SampleValidation = setUpResponse.getSampleValidation();
                InputValidation = setUpResponse.getInputValidation();
                GeoChk = setUpResponse.getGeoCheck();
            }

            jsonArray = sqLite.getMasterSyncDataByKey(Constants.CUSTOM_SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject CusSetupData = jsonArray.getJSONObject(0);
                customSetupResponse = new CustomSetupResponse();
                Type typeCustomSetup = new TypeToken<CustomSetupResponse>() {
                }.getType();
                customSetupResponse = new Gson().fromJson(String.valueOf(CusSetupData), typeCustomSetup);
                HosNeed = customSetupResponse.getHospNeed();
                if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
                    if (CusSetupData.has("Alba_Nd")) {
                        RCPAWOSample = customSetupResponse.getRCPAWOSample();
                    } else {
                        RCPAWOSample = "1";
                    }
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
                    if (CusSetupData.has("Alba_Nd")) {
                        RCPAWOSample = customSetupResponse.getRCPAWOSample();
                    } else {
                        RCPAWOSample = "1";
                    }
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
                    PobNeed = customSetupResponse.getStockistPobNeed();
                    RCPAWOSample = "1";
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
                    PobNeed = customSetupResponse.getUndrPobNeed();
                    RCPAWOSample = "1";
                }
            }

        } catch (Exception ignored) {

        }
    }

    private void AddJWData() {
        callCaptureImageLists = new ArrayList<>();
        JWOthersFragment.callAddedJointList = new ArrayList<>();
        JointworkSelectionSide.JwList = new ArrayList<>();
    }

    private void AddRCPAData() {
        RCPAFragment.ChemistSelectedList = new ArrayList<>();
        RCPAFragment.ProductSelectedList = new ArrayList<>();
        rcpa_comp_list = new ArrayList<>();
    }

    private void AddAdditionalCallData() {
        AdditionalCallFragment.custListArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.addInputAdditionalCallArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.editedInpList = new ArrayList<>();
        AdditionalCallDetailedSide.addProductAdditionalCallArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.editedPrdList = new ArrayList<>();
        AdditionalCusListAdapter.saveAdditionalCallArrayList = new ArrayList<>();
        FinalAdditionalCallAdapter.nestedProduct = new ArrayList<>();
        FinalAdditionalCallAdapter.nestedInput = new ArrayList<>();

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + TodayPlanSfCode);
            Log.v("length", jsonArray.length() + "---" + Constants.DOCTOR + TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), false));
            }

            int count = AdditionalCallFragment.custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (AdditionalCallFragment.custListArrayList.get(i).getCode().equalsIgnoreCase(AdditionalCallFragment.custListArrayList.get(j).getCode())) {
                        AdditionalCallFragment.custListArrayList.set(i, new CallCommonCheckedList(AdditionalCallFragment.custListArrayList.get(i).getName(), AdditionalCallFragment.custListArrayList.get(i).getCode(), AdditionalCallFragment.custListArrayList.get(i).getTown_name(), AdditionalCallFragment.custListArrayList.get(i).getTown_code(), AdditionalCallFragment.custListArrayList.get(i).isCheckedItem()));
                        AdditionalCallFragment.custListArrayList.remove(j--);
                        count--;
                    } else {
                        AdditionalCallFragment.custListArrayList.set(i, new CallCommonCheckedList(AdditionalCallFragment.custListArrayList.get(i).getName(), AdditionalCallFragment.custListArrayList.get(i).getCode(), AdditionalCallFragment.custListArrayList.get(i).getTown_name(), AdditionalCallFragment.custListArrayList.get(i).getTown_code(), AdditionalCallFragment.custListArrayList.get(i).isCheckedItem()));
                    }
                }
            }

        } catch (Exception ignored) {

        }
    }

    private void AddInputData() {
        InputFragment.checkedInputList = new ArrayList<>();
        CheckInputListAdapter.saveCallInputListArrayList = new ArrayList<>();
        AddCallSelectInpSide.callInputList = new ArrayList<>();
        StockInput.clear();

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.INPUT);
            JSONArray jsonArrayInpStk = sqLite.getMasterSyncDataByKey(Constants.INPUT_BALANCE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject jsonFDate = new JSONObject(jsonObject.getString("EffF"));
                JSONObject jsonTDate = new JSONObject(jsonObject.getString("EffT"));
                Log.v("JsonInput", jsonFDate.getString("date").substring(0, 10) + "---" + jsonTDate.getString("date").substring(0, 10));

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                String todayData = CommonUtilsMethods.getCurrentInstance();

                Date d1 = sdf.parse(jsonFDate.getString("date").substring(0, 10));
                Date d2 = sdf.parse(todayData);
                Date d3 = sdf.parse(jsonTDate.getString("date").substring(0, 10));

                if (d2 != null) {
                    if (d2.compareTo(d1) >= 0) {
                        if (d2.compareTo(d3) <= 0) {
                            InputFragment.checkedInputList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false));
                            AddCallSelectInpSide.callInputList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false));
                            StockInput.add(new CallCommonCheckedList(jsonObject.getString("Code"), "0", "0"));
                        }
                    }
                }
            }

            if (InputValidation.equalsIgnoreCase("1")) {
                for (int i = 0; i < InputFragment.checkedInputList.size(); i++) {
                    for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                        JSONObject jsonObjectInput = jsonArrayInpStk.getJSONObject(j);
                        Log.v("chkInput", InputFragment.checkedInputList.get(i).getCode() + "---" + jsonObjectInput.getString("Code") + "--chk-----" + jsonObjectInput.getString("Code").equalsIgnoreCase(InputFragment.checkedInputList.get(i).getCode()));
                        if (jsonObjectInput.getString("Code").equalsIgnoreCase(InputFragment.checkedInputList.get(i).getCode())) {
                            InputFragment.checkedInputList.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), jsonObjectInput.getString("Balance_Stock"), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            AddCallSelectInpSide.callInputList.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), jsonObjectInput.getString("Balance_Stock"), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            StockInput.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getCode(), jsonObjectInput.getString("Balance_Stock"), jsonObjectInput.getString("Balance_Stock")));
                            break;
                        } else {
                            InputFragment.checkedInputList.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), InputFragment.checkedInputList.get(i).getStock_balance(), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            AddCallSelectInpSide.callInputList.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), InputFragment.checkedInputList.get(i).getStock_balance(), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            StockInput.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getCode(), InputFragment.checkedInputList.get(i).getStock_balance(), InputFragment.checkedInputList.get(i).getStock_balance()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.v("JsonInput", "error--" + e);
        }
    }

    private void AddProductsData() {
        ProductFragment.checkedPrdList = new ArrayList<>();
        CheckProductListAdapter.saveCallProductListArrayList = new ArrayList<>();
        AddCallSelectPrdSide.callSampleList = new ArrayList<>();
        RCPASelectPrdSide.PrdFullList = new ArrayList<>();
        StockSample.clear();
        try {
            int Priority_count = 1;
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
            JSONArray jsonArrayPrdStk = sqLite.getMasterSyncDataByKey(Constants.STOCK_BALANCE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (RCPAWOSample.equalsIgnoreCase("0")) {
                    if (!jsonObject.getString("Product_Mode").equalsIgnoreCase("Sample")) {
                        RCPASelectPrdSide.PrdFullList.add(new SaveCallProductList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("DRate")));
                    }
                } else {
                    RCPASelectPrdSide.PrdFullList.add(new SaveCallProductList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("DRate")));
                }

                if (CallActivityCustDetails.get(0).getPriorityPrdCode().contains(jsonObject.getString("Code"))) {
                    ProductFragment.checkedPrdList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, "P" + Priority_count++, jsonObject.getString("Product_Mode")));
                    StockSample.add(new CallCommonCheckedList(jsonObject.getString("Code"), "0", "0"));
                    if (!jsonObject.getString("Product_Mode").equalsIgnoreCase("Sale")) {
                        AddCallSelectPrdSide.callSampleList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, "P" + Priority_count++, jsonObject.getString("Product_Mode")));
                    }
                } else {
                    ProductFragment.checkedPrdList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, jsonObject.getString("Product_Mode"), jsonObject.getString("Product_Mode")));
                    StockSample.add(new CallCommonCheckedList(jsonObject.getString("Code"), "0", "0"));
                    if (!jsonObject.getString("Product_Mode").equalsIgnoreCase("Sale")) {
                        AddCallSelectPrdSide.callSampleList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, jsonObject.getString("Product_Mode"), jsonObject.getString("Product_Mode")));
                    }
                }
            }

            if (SampleValidation.equalsIgnoreCase("1")) {
                for (int i = 0; i < ProductFragment.checkedPrdList.size(); i++) {

                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
                        if (!ProductFragment.checkedPrdList.get(i).getCategory().equalsIgnoreCase("Sale") && jsonObjectSample.getString("Code").equalsIgnoreCase(ProductFragment.checkedPrdList.get(i).getCode())) {
                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
                            // AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
                            StockSample.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), jsonObjectSample.getString("Balance_Stock")));
                            break;
                        } else {
                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
                            //  AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
                            StockSample.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).getStock_balance()));
                        }
                    }
                }

                for (int i = 0; i < AddCallSelectPrdSide.callSampleList.size(); i++) {
                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
                        if (jsonObjectSample.getString("Code").equalsIgnoreCase(AddCallSelectPrdSide.callSampleList.get(i).getCode())) {
                            AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(AddCallSelectPrdSide.callSampleList.get(i).getName(), AddCallSelectPrdSide.callSampleList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), AddCallSelectPrdSide.callSampleList.get(i).isCheckedItem(), AddCallSelectPrdSide.callSampleList.get(i).getCategory(), AddCallSelectPrdSide.callSampleList.get(i).getCategoryExtra()));
                            break;
                        } else {
                            AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(AddCallSelectPrdSide.callSampleList.get(i).getName(), AddCallSelectPrdSide.callSampleList.get(i).getCode(), AddCallSelectPrdSide.callSampleList.get(i).getStock_balance(), AddCallSelectPrdSide.callSampleList.get(i).isCheckedItem(), AddCallSelectPrdSide.callSampleList.get(i).getCategory(), AddCallSelectPrdSide.callSampleList.get(i).getCategoryExtra()));
                        }
                    }
                }

            }

            Collections.sort(ProductFragment.checkedPrdList, Comparator.comparing(CallCommonCheckedList::getCategory));
            Collections.sort(AddCallSelectPrdSide.callSampleList, Comparator.comparing(CallCommonCheckedList::getCategory));

        } catch (Exception e) {
            Log.v("chkSample", "---error---" + e);
        }
    }
}


   /* Backend Pending:
        1) Add CustStatus for Setup; --> GeoTagApprovalNeed
        2) Add Cust_status for all Doctor,Chemist,Cip,Stockist,UnDr --> cust_status
        3) Add Cust_status for json_object to send the mapped tagged Customer
        4) Add Promoted Products for json_object to send the dcr call Customer
        5) Add Sample and Input Data for Additional_Call_Screen*/



