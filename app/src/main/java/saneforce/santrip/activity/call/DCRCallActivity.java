package saneforce.santrip.activity.call;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.call.adapter.DCRCallTabLayoutAdapter;
import saneforce.santrip.activity.call.adapter.additionalCalls.AdditionalCusListAdapter;
import saneforce.santrip.activity.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.santrip.activity.call.adapter.input.CheckInputListAdapter;
import saneforce.santrip.activity.call.adapter.product.CheckProductListAdapter;
import saneforce.santrip.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.call.fragments.additionalCall.AddCallSelectInpSide;
import saneforce.santrip.activity.call.fragments.additionalCall.AddCallSelectPrdSide;
import saneforce.santrip.activity.call.fragments.additionalCall.AdditionalCallDetailedSide;
import saneforce.santrip.activity.call.fragments.additionalCall.AdditionalCallFragment;
import saneforce.santrip.activity.call.fragments.detailing.DetailedFragment;
import saneforce.santrip.activity.call.fragments.input.InputFragment;
import saneforce.santrip.activity.call.fragments.jwOthers.FeedbackSelectionSide;
import saneforce.santrip.activity.call.fragments.jwOthers.JWOthersFragment;
import saneforce.santrip.activity.call.fragments.jwOthers.JointWorkSelectionSide;
import saneforce.santrip.activity.call.fragments.product.ProductFragment;
import saneforce.santrip.activity.call.fragments.rcpa.RCPAFragment;
import saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide;
import saneforce.santrip.activity.call.fragments.rcpa.RCPASelectPrdSide;
import saneforce.santrip.activity.call.pojo.CallCaptureImageList;
import saneforce.santrip.activity.call.pojo.CallCommonCheckedList;
import saneforce.santrip.activity.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.santrip.activity.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.santrip.activity.call.pojo.additionalCalls.SaveAdditionalCall;
import saneforce.santrip.activity.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.santrip.activity.call.pojo.input.SaveCallInputList;
import saneforce.santrip.activity.call.pojo.product.SaveCallProductList;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonSharedPreference;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityDcrcallBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class DCRCallActivity extends AppCompatActivity {

    public static ArrayList<CustList> CallActivityCustDetails;
    public static ArrayList<StoreImageTypeUrl> arrayStore;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrcallBinding dcrCallBinding;
    public static String clickedLocalDate, SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, PobNeed, CapPob, OverallFeedbackNeed, EventCaptureNeed, JwNeed, CusCheckInOutNeed, SampleValidation, InputValidation, PrdSamNeed, PrdRxNeed, CapSamQty, CapRxQty, RcpaCompetitorAdd, SamQtyRestriction, SamQtyRestrictValue, InpQtyRestriction, InpQtyRestrictValue, TodayPlanSfCode;
    public static ArrayList<CallCommonCheckedList> StockSample = new ArrayList<>();
    public static ArrayList<CallCommonCheckedList> StockInput = new ArrayList<>();
    public static String isFromActivity;
    public static String isDetailingRequired;
    ArrayList<StoreImageTypeUrl> arr = new ArrayList<>();
    DCRCallTabLayoutAdapter viewPagerAdapter;
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference commonSharedPreference;
    SQLite sqLite;
    ProgressDialog progressDialog = null;
    GPSTrack gpsTrack;
    LoginResponse loginResponse;
    JSONObject jsonSaveDcr, jsonImage;
    String GeoChk, capPrd, capInp, RCPANeed, HosNeed, FeedbackMandatory, CurrentDate, MgrRcpaMandatory, EventCapMandatory, JwMandatory, CurrentTime, PrdMandatory, InpMandatory, RcpaMandatory, PobMandatory, RemarkMandatory, SamQtyMandatory, RxQtyMandatory;
    double lat, lng;
    ApiInterface api_interface;
    String ChemName = "", CheCode = "";
    JSONArray remArray = new JSONArray();
    JSONObject remObj;
    double CompFullValues = 0;
    boolean isCreateJsonSuccess;
    String FwFlag;
    Dialog dialogCheckOut;
    Button btnCheckOut;
    TextView tv_address, tv_dateTime;
    String address, latEdit, lngEdit;


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            dcrCallBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrCallBinding = ActivityDcrcallBinding.inflate(getLayoutInflater());
        setContentView(dcrCallBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonSharedPreference = new CommonSharedPreference(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        sqLite = new SQLite(this);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            isDetailingRequired = extra.getString(Constants.DETAILING_REQUIRED);
            isFromActivity = extra.getString(Constants.DCR_FROM_ACTIVITY);
        }
        dcrCallBinding.tagCustName.setText(CallActivityCustDetails.get(0).getName());
        getRequiredData();
        SetupTabLayout();
        AddProductsData();
        AddInputData();
        AddAdditionalCallData();
        AddRCPAData();
        AddJWData();
        dcrCallBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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

        dcrCallBinding.ivBack.setOnClickListener(view -> {
            if (isFromActivity.equalsIgnoreCase("new")) {
                sqLite.deleteOfflineCalls(CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        dcrCallBinding.btnCancel.setOnClickListener(view -> {
            if (isFromActivity.equalsIgnoreCase("new")) {
                sqLite.deleteOfflineCalls(CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        dcrCallBinding.btnFinalSubmit.setOnClickListener(view -> {
            progressDialog = CommonUtilsMethods.createProgressDialog(this);
            isCreateJsonSuccess = true;
            if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
                if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                    gpsTrack = new GPSTrack(DCRCallActivity.this);
                    double lat = gpsTrack.getLatitude();
                    double lng = gpsTrack.getLongitude();
                    address = CommonUtilsMethods.gettingAddress(this, lat, lng, false);
                    tv_address.setText(address);
                    tv_dateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
                } else {
                    tv_address.setText(context.getString(R.string.no_network));
                }
            }

            if (CheckRequiredFunctions() && CheckCurrentLoc()) {
                CreateJsonFileCall();
                if (isCreateJsonSuccess) {
                    InsertVisitControl();
                    sqLite.saveOfflineCallOut(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CommonUtilsMethods.getCurrentInstance("HH:mm:ss"), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType(), jsonSaveDcr.toString(), Constants.WAITING_FOR_SYNC);
                    if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
                        dialogCheckOut.show();
                    } else {
                        Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                        startActivity(intent);
                    }
                    if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                        CallUploadImage();
                        CallSaveDcrAPI(jsonSaveDcr.toString());
                    } else {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_saved_locally));
                        if (JWOthersFragment.callCaptureImageLists.size() > 0) {
                            for (int i = 0; i < JWOthersFragment.callCaptureImageLists.size(); i++) {
                                sqLite.saveOfflineEC(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), JWOthersFragment.callCaptureImageLists.get(i).getSystemImgName(), JWOthersFragment.callCaptureImageLists.get(i).getFilePath(), jsonImage.toString(), Constants.WAITING_FOR_SYNC, 0);
                            }
                        }
                        UpdateInputStock();
                        UpdateSampleStock();
                        if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
                            dialogCheckOut.show();
                        } else {
                            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                }
            } else {
                progressDialog.dismiss();
            }
        });

        assert isFromActivity != null;
        if (isFromActivity.equalsIgnoreCase("edit_local")) {
            jsonExtractLocal(CallActivityCustDetails.get(0).getJsonArray());
        } else if (isFromActivity.equalsIgnoreCase("edit_online")) {
            jsonExtractOnline(CallActivityCustDetails.get(0).getJsonArray());
        }
    }

    private void SetupTabLayout() {
        viewPagerAdapter = new DCRCallTabLayoutAdapter(getSupportFragmentManager());

        if (isDetailingRequired.equalsIgnoreCase("true")) {
            viewPagerAdapter.add(new DetailedFragment(), "Detailed");
        } else {
            DetailedFragment.callDetailingLists = new ArrayList<>();
        }

        if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
            viewPagerAdapter.add(new ProductFragment(), capPrd);
            viewPagerAdapter.add(new InputFragment(), capInp);
            if (RCPANeed.equalsIgnoreCase("1")) viewPagerAdapter.add(new RCPAFragment(), "RCPA");
            if (isFromActivity.equalsIgnoreCase("new") || isFromActivity.equalsIgnoreCase("edit_local"))
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
    }

    private void DialogCheckOut() {
        dialogCheckOut = new Dialog(this);
        dialogCheckOut.setContentView(R.layout.dialog_cus_checkout);
        dialogCheckOut.setCancelable(false);
        Objects.requireNonNull(dialogCheckOut.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnCheckOut = dialogCheckOut.findViewById(R.id.btn_checkOut);
        tv_address = dialogCheckOut.findViewById(R.id.txt_address);
        tv_dateTime = dialogCheckOut.findViewById(R.id.txt_date_time);

        btnCheckOut.setOnClickListener(v -> {
            dialogCheckOut.dismiss();
            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
            startActivity(intent);
        });
    }

    private void InsertVisitControl() {
        JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.CALL_SYNC);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CustCode", CallActivityCustDetails.get(0).getCode());
            jsonObject.put("CustType", CallActivityCustDetails.get(0).getType());
            jsonObject.put("Dcr_dt", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
            jsonObject.put("month_name", CommonUtilsMethods.getCurrentInstance("MMMM"));
            jsonObject.put("Mnth", CommonUtilsMethods.getCurrentInstance("M"));
            jsonObject.put("Yr", CommonUtilsMethods.getCurrentInstance("yyyy"));
            jsonObject.put("CustName", CallActivityCustDetails.get(0).getName());
            jsonObject.put("town_code", CallActivityCustDetails.get(0).getTown_code());
            jsonObject.put("town_name", CallActivityCustDetails.get(0).getTown_name());
            jsonObject.put("Dcr_flag", "");
            jsonObject.put("SF_Code", SfCode);
            jsonObject.put("Trans_SlNo", "");
            jsonObject.put("FW_Indicator", FwFlag);
            jsonObject.put("AMSLNo", "");
            jsonArray.put(jsonObject);
            sqLite.saveMasterSyncData(Constants.CALL_SYNC, jsonArray.toString(), 0);

            if (AdditionalCusListAdapter.saveAdditionalCallArrayList.size() > 0) {
                for (int i = 0; i < AdditionalCusListAdapter.saveAdditionalCallArrayList.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("CustCode", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getCode());
                    jsonObject.put("CustType", "1");
                    jsonObject.put("Dcr_dt", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                    jsonObject.put("month_name", CommonUtilsMethods.getCurrentInstance("MMMM"));
                    jsonObject.put("Mnth", CommonUtilsMethods.getCurrentInstance("M"));
                    jsonObject.put("Yr", CommonUtilsMethods.getCurrentInstance("yyyy"));
                    jsonObject.put("CustName", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getName());
                    jsonObject.put("town_code", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getTown_code());
                    jsonObject.put("town_name", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getTown_name());
                    jsonObject.put("Dcr_flag", "");
                    jsonObject.put("SF_Code", SfCode);
                    jsonObject.put("Trans_SlNo", "");
                    jsonObject.put("FW_Indicator", FwFlag);
                    jsonObject.put("AMSLNo", "");
                    jsonArray.put(jsonObject);
                    sqLite.saveMasterSyncData(Constants.CALL_SYNC, jsonArray.toString(), 0);
                }
            }

        } catch (Exception ignored) {
        }
    }

    public boolean funStringValidation(String val) {
        return !TextUtils.isEmpty(val);
    }

    private void CallUploadImage() {
        if (JWOthersFragment.callCaptureImageLists.size() > 0) {
            ApiInterface apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getTagApiImageUrl(getApplicationContext()));
            for (int i = 0; i < JWOthersFragment.callCaptureImageLists.size(); i++) {
                if (JWOthersFragment.callCaptureImageLists.get(i).isNewlyAdded()) {
                    Log.v("ImgUpload", JWOthersFragment.callCaptureImageLists.get(i).getFilePath());
                    MultipartBody.Part img = convertImg("EventImg", JWOthersFragment.callCaptureImageLists.get(i).getFilePath());
                    HashMap<String, RequestBody> values = field(jsonImage.toString());
                    Call<JsonObject> saveImgDcr = apiInterface.SaveImg(values, img);
                    int finalI = i;
                    int finalI1 = i;
                    saveImgDcr.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                try {
                                    assert response.body() != null;
                                    JSONObject json = new JSONObject(response.body().toString());
                                    Log.v("ImgUpload", json.toString());
                                    json.getString("success");
                                    DeleteFileCache(JWOthersFragment.callCaptureImageLists.get(finalI).getFilePath());
                                } catch (Exception ignored) {
                                    DeleteFileCache(JWOthersFragment.callCaptureImageLists.get(finalI).getFilePath());
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                            sqLite.saveOfflineEC(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), JWOthersFragment.callCaptureImageLists.get(finalI1).getSystemImgName(), JWOthersFragment.callCaptureImageLists.get(finalI1).getFilePath(), jsonImage.toString(), Constants.WAITING_FOR_SYNC, 1);
                        }
                    });
                }
            }
        }
    }

    private void DeleteFileCache(String filePath) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
    }

    public boolean CheckRequiredFunctions() {
        switch (CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (SfType.equalsIgnoreCase("1")) {
                    if (PrdMandatory.equalsIgnoreCase("1")) {
                        if (CheckProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.enter_the) + capPrd);
                            return false;
                        }
                    }

                    if (PrdSamNeed.equalsIgnoreCase("1") && SamQtyMandatory.equalsIgnoreCase("1")) {
                        if (CheckProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.enter_the) + capPrd);
                            return false;
                        } else {
                            for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().isEmpty() || CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().equalsIgnoreCase("0")) {
                                    commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.enter_the) + CapSamQty + getApplicationContext().getString(R.string.value));
                                    return false;
                                }
                            }
                        }
                    }

                    if (PrdRxNeed.equalsIgnoreCase("1") && RxQtyMandatory.equalsIgnoreCase("1")) {
                        if (CheckProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.enter_the) + capPrd);
                            return false;
                        } else {
                            for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().isEmpty() || CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().equalsIgnoreCase("0")) {
                                    commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.enter_the) + CapRxQty + getApplicationContext().getString(R.string.value));
                                    return false;
                                }
                            }
                        }
                    }

                    if (InpMandatory.equalsIgnoreCase("1")) {
                        if (CheckInputListAdapter.saveCallInputListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.select_the) + capInp);
                            return false;
                        }
                    }

                    if (RCPANeed.equalsIgnoreCase("1") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_rcpa_values));
                            return false;
                        }
                    }

                } else {
                    if (MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_rcpa_values));
                            return false;
                        }
                    }
                }

                if (JwMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callAddedJointList.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.jointwork_need));
                        return false;
                    }
                }

                if (PobNeed.equalsIgnoreCase("0") && PobMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(JWOthersFragment.jwOthersBinding.edPob.getText()).toString().isEmpty() || JWOthersFragment.jwOthersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_pob_values));
                        return false;
                    }
                }

                if (FeedbackMandatory.equalsIgnoreCase("1")) {
                    if (JWOthersFragment.jwOthersBinding.tvFeedback.getText().toString().isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_feedback));
                        return false;
                    }
                }

                if (RemarkMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(JWOthersFragment.jwOthersBinding.edRemarks.getText()).toString().isEmpty() || JWOthersFragment.jwOthersBinding.edRemarks.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_remark));
                        return false;
                    }
                }

                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callCaptureImageLists.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
                        return false;
                    }
                }

                for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
                    ArrayList<String> dummyChk = new ArrayList<>();
                    for (int j = 0; j < RCPASelectCompSide.rcpa_comp_list.size(); j++) {
                        if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getPrd_code())) {
                            dummyChk.add(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code());
                        }
                    }

                    if (dummyChk.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.need_competitors_for_prd));
                        return false;
                    }
                }

                for (int i = 0; i < RCPASelectCompSide.rcpa_comp_list.size(); i++) {
                    if (RCPASelectCompSide.rcpa_comp_list.get(i).getQty().equalsIgnoreCase("0") || RCPASelectCompSide.rcpa_comp_list.get(i).getQty().isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.rcpa_need_qty));
                        return false;
                    }
                }

                break;
            case "2":
                if (SfType.equalsIgnoreCase("1")) {
                    if (RCPANeed.equalsIgnoreCase("1") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_rcpa_values));
                            return false;
                        }
                    }
                } else {
                    if (MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPASelectCompSide.rcpaAddedProdListArrayList.size() == 0) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_rcpa_values));
                            return false;
                        }
                    }
                }

                if (JwMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callAddedJointList.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.jointwork_need));
                        return false;
                    }
                }

                if (PobNeed.equalsIgnoreCase("0") && PobMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(JWOthersFragment.jwOthersBinding.edPob.getText()).toString().isEmpty() || JWOthersFragment.jwOthersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_pob_values));
                        return false;
                    }
                }

                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callCaptureImageLists.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
                        return false;
                    }
                }

                for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
                    ArrayList<String> dummyChk = new ArrayList<>();
                    for (int j = 0; j < RCPASelectCompSide.rcpa_comp_list.size(); j++) {
                        if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getPrd_code())) {
                            dummyChk.add(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code());
                        }
                    }

                    if (dummyChk.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.need_competitors_for_prd));
                        return false;
                    }
                }

                for (int i = 0; i < RCPASelectCompSide.rcpa_comp_list.size(); i++) {
                    if (RCPASelectCompSide.rcpa_comp_list.get(i).getQty().equalsIgnoreCase("0") || RCPASelectCompSide.rcpa_comp_list.get(i).getQty().isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.rcpa_need_qty));
                        return false;
                    }
                }

                break;
            case "3":
            case "4":
                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callCaptureImageLists.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
                        return false;
                    }
                }
                if (JwMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callAddedJointList.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.jointwork_need));
                        return false;
                    }
                }
                break;
            case "5":
                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callCaptureImageLists.size() == 0) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
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
        Log.v("callSave", "---" + jsonSaveDcr);
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/dcr");
        Call<JsonElement> callSaveDcr = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonSaveDcr);
        callSaveDcr.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                        Log.v("callSave", "---" + jsonSaveRes);
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_saved_successfully));
                            UpdateInputStock();
                            UpdateSampleStock();
                        } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_already_exist));
                        } else {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_failed));
                        }

                        sqLite.deleteOfflineCalls(CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                        if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
                            dialogCheckOut.show();
                        } else {
                            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                            startActivity(intent);
                        }
                        progressDialog.dismiss();

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_saved_something_wrong));
                        if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
                            dialogCheckOut.show();
                        } else {
                            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                            startActivity(intent);
                        }
                        Log.v("callSave", "---" + e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                Log.v("callSave", "---" + t);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_failed_saved_locally));
                if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
                    dialogCheckOut.show();
                } else {
                    Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                    startActivity(intent);
                }
                UpdateSampleStock();
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
                    Log.v("chkSamStk", StockSample.get(i).getStockCode() + "-----" + jsonObject.getString("Code") + "----" + StockSample.get(i).getStockCode().equalsIgnoreCase(jsonObject.getString("Code")));
                    if (StockSample.get(i).getStockCode().equalsIgnoreCase(jsonObject.getString("Code"))) {
                        if (!StockSample.get(i).getCurrentStock().equalsIgnoreCase(jsonObject.getString("Balance_Stock"))) {
                            jsonObject.remove("Balance_Stock");
                            jsonObject.put("Balance_Stock", Integer.parseInt(StockSample.get(i).getCurrentStock()));
                            break;
                        }
                    }
                }
            }
            sqLite.saveMasterSyncData(Constants.STOCK_BALANCE, jsonArraySamStk.toString(), 0);
        } catch (Exception e) {
            Log.v("chkSamStk", "error---" + e);
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
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);
        } catch (Exception ignored) {
        }
        return yy;
    }

    public String extractValues(String s, String data) {
        if (TextUtils.isEmpty(s)) return "";

        String[] clstarrrayqty = s.split("#");
        StringBuilder ss1 = new StringBuilder();

        for (String value : clstarrrayqty) {
            if (data.equalsIgnoreCase("sample")) {
                ss1.append(value.substring(value.indexOf("~") + 1));
                ss1 = new StringBuilder(ss1.toString().replace("$0^0", "") + ",");
                int index = ss1.indexOf("$");
                ss1 = new StringBuilder(ss1.substring(0, index) + ",");
            } else if (data.equalsIgnoreCase("Rx")) {
                ss1.append(value.substring(value.indexOf("$") + 1));
                ss1 = new StringBuilder(ss1.toString().replace("$0^0", "") + ",");
            } else if (data.equalsIgnoreCase("input")) {
                ss1.append(value.substring(value.indexOf("~") + 1)).append(",");
            } else if (data.equalsIgnoreCase("names") || data.equalsIgnoreCase("codes")) {
                ss1.append(value.substring(0, value.indexOf("~"))).append(",");
            } else if (data.equalsIgnoreCase("stockistname")) {
                ss1.append(value.substring(0, value.indexOf("^")).substring(value.lastIndexOf("~") + 1)).append(",");
            } else if (data.equalsIgnoreCase("stockistcode")) {
                ss1.append(value.substring(value.indexOf("^") + 1)).append(",");
            }
        }
        // Log.v("jsonExtractOnline", "product_inputs_qty--333--" + ss1);
        String finalValue = "";
        finalValue = ss1.substring(0, ss1.length() - 1);
        if (finalValue.isEmpty()) {
            finalValue = "0";
        }
        Log.v("jsonExtractOnline", "product_inputs_qty--333--" + finalValue);
        return finalValue;
    }

    private void getValues(String names, ArrayList<String> addDatas) {
        String[] separated = names.split(",");
        Collections.addAll(addDatas, separated);
    }

    private StringBuilder filterPromoted(String s) {
        String[] clstarrrayqty = s.split("#");
        StringBuilder ss1 = new StringBuilder();
        for (String value : clstarrrayqty) {
            ss1.append(value.substring(value.lastIndexOf("$") + 1)).append(",");
        }
        return new StringBuilder(ss1.substring(0, ss1.length() - 1));
    }

    private void jsonExtractOnline(String jsonArrayOnline) {
        ArrayList<String> prdNameList = new ArrayList<>();
        ArrayList<String> prdCodeList = new ArrayList<>();
        ArrayList<String> prdSamQtyList = new ArrayList<>();
        ArrayList<String> prdRxQtyList = new ArrayList<>();
        ArrayList<String> prdStkName = new ArrayList<>();
        ArrayList<String> prdStkCode = new ArrayList<>();
        ArrayList<String> input = new ArrayList<>();
        ArrayList<String> inputCode = new ArrayList<>();
        ArrayList<String> inputQty = new ArrayList<>();
        ArrayList<String> StartTimeSlide = new ArrayList<>();
        ArrayList<String> EndTimeSlide = new ArrayList<>();
        StringBuilder productPromoted = new StringBuilder();
        DetailedFragment.callDetailingLists = new ArrayList<>();
        arrayStore = new ArrayList<>();

        try {
            Log.v("jsonExtractOnline", "----" + jsonArrayOnline);
            JSONObject json = new JSONObject(jsonArrayOnline);

            if (!json.getString("DigitalHead").equalsIgnoreCase("[]")) {
                JSONArray jsonPrdSlides = new JSONArray(json.getString("DigitalHead"));
                for (int i = 0; i < jsonPrdSlides.length(); i++) {
                    JSONObject jsSlidesPrds = jsonPrdSlides.getJSONObject(i);

                    String ProductName = "", ProductCode = "", Rating = "", StartTime = "", EndTime = "", Date = "", PrdFeedBack = "", SlideType = "", SlideRemarks = "", SlideName = "", SlidePath = "", SlideRating = "0";
                    if (jsSlidesPrds.getString("GroupID").equalsIgnoreCase("1")) {
                        if (funStringValidation(jsSlidesPrds.getString("Product_Name")))
                            ProductName = jsSlidesPrds.getString("Product_Name");

                        if (funStringValidation(jsSlidesPrds.getString("Product_Code")))
                            ProductCode = jsSlidesPrds.getString("Product_Code");

                        if (funStringValidation(jsSlidesPrds.getString("Rating")))
                            Rating = jsSlidesPrds.getString("Rating");

                        JSONObject jsonStart = jsSlidesPrds.getJSONObject("StartTime");

                        if (funStringValidation(jsSlidesPrds.getString("Feedbk_Status")))
                            PrdFeedBack = jsSlidesPrds.getString("Feedbk_Status");

                        if (funStringValidation(jsonStart.getString("date")))
                            Date = jsonStart.getString("date").substring(0, 11);
                        StartTime = jsonStart.getString("date").substring((jsonStart.getString("date").indexOf(" ")) + 1);

                        JSONObject jsonEnd = jsSlidesPrds.getJSONObject("EndTime");
                        if (funStringValidation(jsonEnd.getString("date")))
                            EndTime = jsonEnd.getString("date").substring((jsonStart.getString("date").indexOf(" ")) + 1);


                        JSONArray jsonArray = jsSlidesPrds.getJSONArray("DigitalDet");
                        Log.v("DcrDetail_extract", "----DigitalSlides--len--" + jsonArray.length());
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonSlide = jsonArray.getJSONObject(j);

                            if (!SlideName.equalsIgnoreCase(jsonSlide.getString("SlideName"))) {
                                StartTimeSlide.clear();
                                EndTimeSlide.clear();

                                if (jsSlidesPrds.getString("GroupID").equalsIgnoreCase("1")) {
                                    if (funStringValidation(jsonSlide.getString("SlideType")))
                                        SlideType = jsonSlide.getString("SlideType");
                                    if (funStringValidation(jsonSlide.getString("SlideName")))
                                        SlideName = jsonSlide.getString("SlideName");
                                    if (funStringValidation(jsonSlide.getString("Rating")))
                                        SlideRating = jsonSlide.getString("Rating");
                                    if (funStringValidation(jsonSlide.getString("Feedbk")))
                                        SlideRemarks = jsonSlide.getString("Feedbk");
                                }
                                StartTimeSlide.add(jsonSlide.getString("stm").substring(11));
                                EndTimeSlide.add(jsonSlide.getString("etm").substring(11));

                                // Log.v("jsonExtractOnline", "size---" + StartTimeSlide.size() + "---st---" + StartTimeSlide + "--size--" + EndTimeSlide.size() + "---et---" + EndTimeSlide);

                                remArray = new JSONArray();
                                for (int slide = 0; slide < StartTimeSlide.size(); slide++) {
                                    remObj = new JSONObject();
                                    remObj.put("sT", StartTimeSlide.get(slide));
                                    remObj.put("eT", EndTimeSlide.get(slide));
                                    remArray.put(remObj);
                                }
                            }
                            arrayStore.add(new StoreImageTypeUrl("", SlideName, SlideType, "", "0", "", remArray.toString(), ProductName, ProductCode, false));
                        }
                        DetailedFragment.callDetailingLists.add(new CallDetailingList(ProductName, ProductCode, SlideName, SlideType, "", StartTime.trim() + " " + EndTime.trim(), StartTime.trim(), Integer.parseInt(Rating), PrdFeedBack, Date));
                    }
                }
            }

            JSONArray jsonPrdArray = new JSONArray(json.getString("DCRDetail"));
            JSONObject js = jsonPrdArray.getJSONObject(0);

            latEdit = js.getString("lati");
            lngEdit = js.getString("long");

            if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1") || CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
                if (js.has("Product_Detail")) {
                    Log.v("jsonExtractOnline", "----" + "0000");
                    if (!js.getString("Product_Detail").isEmpty() && !js.getString("Product_Detail").equalsIgnoreCase("#")) {
                        Log.v("jsonExtractOnline", "----" + "1111");
                        String prd_names = extractValues(js.getString("Product_Detail"), "names");
                        getValues(prd_names, prdNameList);

                        String prd_codes = extractValues(js.getString("Product_Code"), "codes");
                        getValues(prd_codes, prdCodeList);

                        String sam_qty = extractValues(js.getString("Product_Detail"), "Sample");
                        getValues(sam_qty, prdSamQtyList);

                        String rx_qty = extractValues(js.getString("Product_Detail"), "Rx");
                        getValues(rx_qty, prdRxQtyList);
                    }
                }
            } else {
                if (js.has("Additional_Prod_Dtls")) {
                    if (!js.getString("Additional_Prod_Dtls").isEmpty()) {
                        String prd_names = extractValues(js.getString("Additional_Prod_Dtls"), "names");
                        getValues(prd_names, prdNameList);

                        String prd_codes = extractValues(js.getString("Additional_Prod_Code"), "codes");
                        getValues(prd_codes, prdCodeList);

                        String sam_qty = extractValues(js.getString("Additional_Prod_Dtls"), "Sample");
                        getValues(sam_qty, prdSamQtyList);

                        String rx_qty = extractValues(js.getString("Additional_Prod_Dtls"), "Rx");
                        getValues(rx_qty, prdRxQtyList);
                    }
                }
            }

            if (js.has("promoted_product")) {
                if (!js.getString("promoted_product").isEmpty()) {
                    productPromoted = filterPromoted(js.getString("promoted_product"));
                }
            }

            if (js.has("Gift_Name")) {
                if (!js.getString("Gift_Name").isEmpty()) {
                    input.add(js.getString("Gift_Name"));
                    inputQty.add(js.getString("Gift_Qty"));
                    inputCode.add(js.getString("Gift_Code"));
                }
            }

            if (js.has("Additional_Gift_Dtl")) {
                if (!js.getString("Additional_Gift_Dtl").isEmpty()) {
                    String input_names = extractValues(js.getString("Additional_Gift_Dtl"), "names");
                    getValues(input_names, input);

                    String input_Codes = extractValues(js.getString("Additional_Gift_Code"), "codes");
                    getValues(input_Codes, inputCode);

                    String inp_qty = extractValues(js.getString("Additional_Gift_Dtl"), "input");
                    getValues(inp_qty, inputQty);
                }
            }

        /*    if (js.has("Product_Stockist")) {
                if (!js.getString("Product_Stockist").isEmpty()) {
                    String stk_name = extractValues(js.getString("Product_Stockist"), "stockistname");
                    getValues(stk_name, prdStkName);

                    String stk_code = extractValues(js.getString("Product_Stockist"), "stockistcode");
                    getValues(stk_code, prdStkCode);
                }
            }*/

            if (js.has("Worked_with_Name")) {
                if (!js.getString("Worked_with_Name").isEmpty()) {
                    String[] separatedjoints = js.getString("Worked_with_Name").split(",");
                    for (String s : separatedjoints) {
                        JWOthersFragment.callAddedJointList.add(new CallCommonCheckedList(s, ""));
                    }
                }
            }

            //Remarks
            if (funStringValidation(js.getString("Activity_Remarks")))
                JWOthersFragment.editRemarks = js.getString("Activity_Remarks");

            //POB
            if (js.has("POB")) {
                JWOthersFragment.editPob = js.getString("POB");
            }

            //FeedBack
            if (js.has("Drcallfeedbackcode") && js.has("Call_Fdback")) {
                FeedbackSelectionSide.feedbackCode = js.getString("Drcallfeedbackcode");
                FeedbackSelectionSide.feedbackName = js.getString("Call_Fdback");
                JWOthersFragment.editFeedback = js.getString("Call_Fdback");
            }
            Log.v("jsonExtractOnline", "product--size--" + prdNameList.size());
            Log.v("jsonExtractOnline", "product-sam-size--" + prdSamQtyList.size());
            Log.v("jsonExtractOnline", "product-rx-size--" + prdRxQtyList.size());
            Log.v("jsonExtractOnline", "product-promoted--" + productPromoted);

            for (int m = 0; m < prdNameList.size(); m++) {
                for (int j = 0; j < ProductFragment.checkedPrdList.size(); j++) {
                    CallCommonCheckedList PrdList = ProductFragment.checkedPrdList.get(j);
                    if (PrdList.getCode().equalsIgnoreCase(prdCodeList.get(m))) {
                        int lastStock = Integer.parseInt(PrdList.getStock_balance()) + Integer.parseInt(prdSamQtyList.get(m));
                        if (productPromoted.toString().contains(prdNameList.get(m))) {
                            CheckProductListAdapter.saveCallProductListArrayList.add(new SaveCallProductList(prdNameList.get(m), prdCodeList.get(m), PrdList.getCategory(), PrdList.getStock_balance(), String.valueOf(lastStock), prdSamQtyList.get(m), prdRxQtyList.get(m), "", "0", true));
                        } else {
                            CheckProductListAdapter.saveCallProductListArrayList.add(new SaveCallProductList(prdNameList.get(m), prdCodeList.get(m), PrdList.getCategory(), PrdList.getStock_balance(), String.valueOf(lastStock), prdSamQtyList.get(m), prdRxQtyList.get(m), "", "1", true));
                        }
                        PrdList.setCheckedItem(true);
                        break;
                    }
                }
            }

            Log.v("jsonExtractOnline", "inputs--size--" + input.size());
            Log.v("jsonExtractOnline", "inputs-code-size--" + inputCode.size());
            Log.v("jsonExtractOnline", "inputs-qty-size--" + inputQty.size());

            for (int n = 0; n < input.size(); n++) {
                for (int j = 0; j < InputFragment.checkedInputList.size(); j++) {
                    CallCommonCheckedList InpList = InputFragment.checkedInputList.get(j);
                    if (inputCode.get(n).equalsIgnoreCase(InpList.getCode())) {
                        int lastStock = Integer.parseInt(InpList.getStock_balance()) + Integer.parseInt(inputQty.get(n));
                        CheckInputListAdapter.saveCallInputListArrayList.add(new SaveCallInputList(input.get(n), inputCode.get(n), inputQty.get(n), InpList.getStock_balance(), String.valueOf(lastStock)));
                        InpList.setCheckedItem(true);
                        break;
                    }
                }
            }

            //RCPA
            if (!json.getString("RCPAHead").equalsIgnoreCase("[]")) {
                JSONArray jsonArrayRcpa = new JSONArray(json.getString("RCPAHead"));
                Log.v("jsonExtractOnline", "----" + jsonArrayRcpa);
                for (int i = 0; i < jsonArrayRcpa.length(); i++) {
                    JSONObject jsonRcpa = jsonArrayRcpa.getJSONObject(i);
                    CompFullValues = jsonRcpa.getDouble("OPValue");
                    ChemName = jsonRcpa.getString("ChmName").replace(",", "").trim();
                    CheCode = jsonRcpa.getString("ChmCode").replace(",", "").trim();
                    JSONArray jsonArrayComp = jsonRcpa.getJSONArray("RCPADet");
                    Log.v("jsonExtractOnline", "----" + jsonArrayComp);
                    for (int j = 0; j < jsonArrayComp.length(); j++) {
                        JSONObject jsonComp = jsonArrayComp.getJSONObject(j);
                        CompFullValues = CompFullValues + jsonComp.getDouble("CPValue");
                        RCPASelectCompSide.rcpa_comp_list.add(new RCPAAddedCompList(ChemName, CheCode, jsonRcpa.getString("OPName"), jsonRcpa.getString("OPCode"), jsonComp.getString("CompName"), jsonComp.getString("CompCode"), jsonComp.getString("CompPName"), jsonComp.getString("CompPCode"), jsonComp.getString("CPQty"), jsonComp.getString("CPRate"), jsonComp.getString("CPValue"), jsonComp.getString("CPRemarks"), String.valueOf(CompFullValues)));
                    }

                    RCPAFragment.ProductSelectedList.add(new RCPAAddedProdList(ChemName, CheCode, jsonRcpa.getString("OPName"), jsonRcpa.getString("OPCode"), jsonRcpa.getString("OPQty"), jsonRcpa.getString("OPRate"), jsonRcpa.getString("OPValue"), String.valueOf(CompFullValues)));
                    double getTotalValue = 0.0;
                    ArrayList<Double> double_data = new ArrayList<>();
                    if (RCPAFragment.ProductSelectedList.size() > 0) {
                        for (int m = 0; m < RCPAFragment.ProductSelectedList.size(); m++) {
                            if (RCPAFragment.ProductSelectedList.get(m).getChe_codes().equalsIgnoreCase(CheCode)) {
                                double_data.add(Double.parseDouble(RCPAFragment.ProductSelectedList.get(m).getTotalPrdValue()));
                            }
                        }
                    } else {
                        RCPAFragment.ChemistSelectedList.add(new CustList(ChemName, CheCode, String.valueOf(CompFullValues), ""));
                    }

                    if (double_data.size() > 0) {
                        for (int m = 0; m < double_data.size(); m++) {
                            getTotalValue = getTotalValue + double_data.get(m);
                        }
                    }

                    double valueRounded = Math.round(getTotalValue * 100D) / 100D;

                    RCPAFragment.ChemistSelectedList.add(new CustList(ChemName, CheCode, String.valueOf(valueRounded), ""));

                    int count = RCPAFragment.ChemistSelectedList.size();
                    for (int m = 0; m < count; m++) {
                        for (int j = m + 1; j < count; j++) {
                            if (RCPAFragment.ChemistSelectedList.get(m).getCode().equalsIgnoreCase(RCPAFragment.ChemistSelectedList.get(j).getCode())) {
                                String value = RCPAFragment.ChemistSelectedList.get(j).getTotalRcpa();
                                RCPAFragment.ChemistSelectedList.remove(j--);
                                RCPAFragment.ChemistSelectedList.set(m, new CustList(ChemName, CheCode, value, ""));
                                count--;
                            }
                        }
                    }
                }
            }

            if (!json.getString("event_capture").equalsIgnoreCase("[]")) {
                JSONArray jsonArrayEC = new JSONArray(json.getString("event_capture"));
                for (int i = 0; i < jsonArrayEC.length(); i++) {
                    JSONObject jsonEC = jsonArrayEC.getJSONObject(i);
                    JWOthersFragment.callCaptureImageLists.add(new CallCaptureImageList(jsonEC.getString("title"), jsonEC.getString("remarks"), null, "", jsonEC.getString("imgurl"), false));
                }
            }

            Log.v("jsonExtractOnline", "----" + RCPAFragment.ProductSelectedList.size() + "----" + RCPASelectCompSide.rcpa_comp_list.size());
        } catch (Exception e) {
            Log.v("jsonExtractOnline", "----" + e);
        }
    }


    private void jsonExtractLocal(String jsonArray) {
        try {
            String samQty = "", inpQty = "";
            arrayStore = new ArrayList<>();
            DetailedFragment.callDetailingLists = new ArrayList<>();
            String SlideType = "", SlideRemarks = "", SlideName = "", SlidePath = "", SlideRating = "0", SlideScr = "", STm = "", ETm = "", dt = "";
            Log.v("jsonExtractLocal", "----" + jsonArray);
            JSONObject json = new JSONObject(jsonArray);

            int iEnd = json.getString("Entry_location").indexOf(":");
            if (iEnd != -1) {
                latEdit = json.getString("Entry_location").substring(0, iEnd);
            }
            lngEdit = json.getString("Entry_location").substring(json.getString("Entry_location").lastIndexOf(":") + 1);

            Log.v("jsonExtractLocal", "----" + latEdit + "---" + lngEdit);

            //Remarks
            if (funStringValidation(json.getString("Remarks")))
                JWOthersFragment.editRemarks = json.getString("Remarks");

            //POB
            if (json.has("DCSUPOB")) {
                JWOthersFragment.editPob = json.getString("DCSUPOB");
            }

            //FeedBack
            if (json.has("Drcallfeedbackcode")) {
                FeedbackSelectionSide.feedbackName = json.getString("Drcallfeedbackname");
                FeedbackSelectionSide.feedbackCode = json.getString("Drcallfeedbackcode");
                JWOthersFragment.editFeedback = json.getString("Drcallfeedbackname");
            }


            //Product
            JSONArray jsonPrdArray = new JSONArray(json.getString("Products"));
            for (int i = 0; i < jsonPrdArray.length(); i++) {

                JSONObject js = jsonPrdArray.getJSONObject(i);

                if (js.getString("Group").equalsIgnoreCase("0")) {
                    for (int j = 0; j < ProductFragment.checkedPrdList.size(); j++) {
                        CallCommonCheckedList PrdList = ProductFragment.checkedPrdList.get(j);
                        if (js.getString("Code").equalsIgnoreCase(PrdList.getCode())) {
                            samQty = js.getString("SmpQty");
                            if (samQty.isEmpty()) {
                                samQty = "0";
                            }
                            int lastStock = Integer.parseInt(PrdList.getStock_balance()) + Integer.parseInt(samQty);
                            CheckProductListAdapter.saveCallProductListArrayList.add(new SaveCallProductList(js.getString("Name"), js.getString("Code"), js.getString("category"), PrdList.getStock_balance(), String.valueOf(lastStock), samQty, js.getString("RxQty"), js.getString("RcpaQty"), js.getString("Promoted"), true));
                            PrdList.setCheckedItem(true);
                            break;
                        }
                    }
                } else if (js.getString("Group").equalsIgnoreCase("1")) {
                    Log.v("slide_data", js.getString("Name"));
                    JSONObject jsonTim = js.getJSONObject("Timesline");

                    if (funStringValidation(jsonTim.getString("sTm")))
                        dt = jsonTim.getString("sTm").substring(0, 11);
                    if (funStringValidation(jsonTim.getString("sTm")))
                        STm = jsonTim.getString("sTm").substring((jsonTim.getString("sTm").indexOf(" ")) + 1);
                    if (funStringValidation(jsonTim.getString("eTm")))
                        ETm = jsonTim.getString("eTm").substring((jsonTim.getString("eTm").indexOf(" ")) + 1);

                    JSONArray jsonArraySlide = js.getJSONArray("Slides");
                    for (int j = 0; j < jsonArraySlide.length(); j++) {

                        JSONObject jsonSlide = jsonArraySlide.getJSONObject(j);
                        if (funStringValidation(jsonSlide.getString("SlideType")))
                            SlideType = jsonSlide.getString("SlideType");
                        if (funStringValidation(jsonSlide.getString("Scribbles")))
                            SlideScr = jsonSlide.getString("Scribbles");
                        if (funStringValidation(jsonSlide.getString("SlideRemarks")))
                            SlideRemarks = jsonSlide.getString("SlideRemarks");
                        if (funStringValidation(jsonSlide.getString("Slide")))
                            SlideName = jsonSlide.getString("Slide");
                        if (funStringValidation(jsonSlide.getString("SlideRating")))
                            SlideRating = jsonSlide.getString("SlideRating");
                        if (funStringValidation(jsonSlide.getString("SlidePath")))
                            SlidePath = jsonSlide.getString("SlidePath");

                        String date = "", sTm = "", eTm = "";
                        JSONArray remArray = new JSONArray();
                        JSONObject remObj = new JSONObject();
                        JSONArray jsonTime = jsonSlide.getJSONArray("Times");
                        for (int k = 0; k < jsonTime.length(); k++) {
                            remObj = new JSONObject();
                            JSONObject jsomtime = jsonTime.getJSONObject(k);
                            if (funStringValidation(jsomtime.getString("sTm")))
                                date = jsomtime.getString("sTm").substring(0, 11);
                            if (funStringValidation(jsomtime.getString("sTm")))
                                sTm = jsomtime.getString("sTm").substring(11);
                            if (funStringValidation(jsomtime.getString("eTm")))
                                eTm = jsomtime.getString("eTm").substring(11);
                            Log.v("STart_date", String.valueOf(jsomtime));
                            remObj.put("sT", date + " " + sTm);
                            remObj.put("eT", date + " " + eTm);
                            Log.v("rem_obj_print", remObj.toString());
                            remArray.put(remObj);
                        }
                        arrayStore.add(new StoreImageTypeUrl(SlideScr, SlideName, SlideType, SlidePath, "0", SlideRating, remArray.toString(), js.getString("Name"), js.getString("Code"), false));
                    }
                    DetailedFragment.callDetailingLists.add(new CallDetailingList(js.getString("Name"), js.getString("Code"), SlideName, SlideType, SlidePath, STm.trim() + " " + ETm.trim(), STm.trim(), Integer.parseInt(js.getString("Rating")), js.getString("ProdFeedbk"), dt));
                }
            }

            //JointWork
            JSONArray jsonJoint = json.getJSONArray("JointWork");
            for (int w = 0; w < jsonJoint.length(); w++) {
                JSONObject jsJoint = jsonJoint.getJSONObject(w);
                String nam = "", code = "";
                if (funStringValidation(jsJoint.getString("Name"))) nam = jsJoint.getString("Name");
                if (funStringValidation(jsJoint.getString("Code")))
                    code = jsJoint.getString("Code");
                if (!nam.equalsIgnoreCase("")) {
                    JWOthersFragment.callAddedJointList.add(new CallCommonCheckedList(nam.trim(), code));
                }
            }

            //Input
            JSONArray jsonInput = json.getJSONArray("Inputs");
            Log.v("input_wrk", String.valueOf(jsonInput));
            String nam = "", inp_code = "", iqty = "";
            for (int ip = 0; ip < jsonInput.length(); ip++) {
                JSONObject jsIp = jsonInput.getJSONObject(ip);
                if (funStringValidation(jsIp.getString("Name"))) nam = jsIp.getString("Name");
                if (funStringValidation(jsIp.getString("Code"))) inp_code = jsIp.getString("Code");
                if (funStringValidation(jsIp.getString("IQty"))) iqty = jsIp.getString("IQty");
                for (int j = 0; j < InputFragment.checkedInputList.size(); j++) {
                    CallCommonCheckedList InpList = InputFragment.checkedInputList.get(j);
                    if (jsIp.getString("Code").equalsIgnoreCase(InpList.getCode())) {
                        if (iqty.isEmpty()) {
                            iqty = "0";
                        }
                        int lastStock = Integer.parseInt(InpList.getStock_balance()) + Integer.parseInt(iqty);
                        CheckInputListAdapter.saveCallInputListArrayList.add(new SaveCallInputList(nam, inp_code, iqty, InpList.getStock_balance(), String.valueOf(lastStock)));
                        InpList.setCheckedItem(true);
                        break;
                    }
                }
            }

            //Additional Call
            JSONArray jsonAdditional = json.getJSONArray("AdCuss");
            String code = "", townCode = "", townName = "";
            for (int aw = 0; aw < jsonAdditional.length(); aw++) {
                JSONObject jsAw = jsonAdditional.getJSONObject(aw);
                if (funStringValidation(jsAw.getString("Name"))) nam = jsAw.getString("Name");
                if (funStringValidation(jsAw.getString("Code"))) code = jsAw.getString("Code");
                if (funStringValidation(jsAw.getString("town_code")))
                    townCode = jsAw.getString("town_code");
                if (funStringValidation(jsAw.getString("town_name")))
                    townName = jsAw.getString("town_name");
                AdditionalCusListAdapter.saveAdditionalCallArrayList.add(new SaveAdditionalCall(nam, code, townName, townCode, false));
                for (int j = 0; j < AdditionalCallFragment.custListArrayList.size(); j++) {
                    CallCommonCheckedList CusList = AdditionalCallFragment.custListArrayList.get(j);
                    if (jsAw.getString("Code").equalsIgnoreCase(CusList.getCode())) {
                        CusList.setCheckedItem(true);
                        break;
                    }
                }

                JSONArray jsonArrayPrd = jsAw.getJSONArray("Products");
                for (int ap = 0; ap < jsonArrayPrd.length(); ap++) {
                    JSONObject jsPrd = jsonArrayPrd.getJSONObject(ap);
                    for (int jj = 0; jj < ProductFragment.checkedPrdList.size(); jj++) {
                        CallCommonCheckedList PrdList = ProductFragment.checkedPrdList.get(jj);
                        if (jsPrd.getString("Code").equalsIgnoreCase(PrdList.getCode())) {
                            samQty = jsPrd.getString("SamQty");
                            if (samQty.isEmpty()) {
                                samQty = "0";
                            }
                            int lastStock = Integer.parseInt(PrdList.getStock_balance()) + Integer.parseInt(samQty);
                            FinalAdditionalCallAdapter.nestedProduct.add(new AddSampleAdditionalCall(nam, code, jsPrd.getString("Name"), jsPrd.getString("Code"), PrdList.getStock_balance(), String.valueOf(lastStock), samQty, PrdList.getCategory()));
                            break;
                        }
                    }
                }

                JSONArray jsonArrayInp = jsAw.getJSONArray("Inputs");
                for (int ai = 0; ai < jsonArrayInp.length(); ai++) {
                    JSONObject jsInp = jsonArrayInp.getJSONObject(ai);
                    for (int jj = 0; jj < InputFragment.checkedInputList.size(); jj++) {
                        CallCommonCheckedList InpList = InputFragment.checkedInputList.get(jj);
                        if (jsInp.getString("Code").equalsIgnoreCase(InpList.getCode())) {
                            inpQty = jsInp.getString("InpQty");
                            if (inpQty.isEmpty()) {
                                inpQty = "0";
                            }
                            int lastStock = Integer.parseInt(InpList.getStock_balance()) + Integer.parseInt(inpQty);
                            FinalAdditionalCallAdapter.nestedInput.add(new AddInputAdditionalCall(nam, code, jsInp.getString("Name"), jsInp.getString("Code"), InpList.getStock_balance(), String.valueOf(lastStock), inpQty));
                            break;
                        }
                    }
                }
            }

            //EventCapture
            if (json.has("EventCapture")) {
                JSONArray jsonArrayEc = json.getJSONArray("EventCapture");
                if (jsonArrayEc.length() > 0) {
                    for (int j = 0; j < jsonArrayEc.length(); j++) {
                        JSONObject jsEC = jsonArrayEc.getJSONObject(j);
                        JWOthersFragment.callCaptureImageLists.add(new CallCaptureImageList(jsEC.getString("EventImageTitle"), jsEC.getString("EventImageDescription"), null, jsEC.getString("Eventfilepath"), jsEC.getString("EventImageName"), true));
                    }
                }
            }

            //RCPA
            if (!json.getString("RCPAEntry").equalsIgnoreCase("[]")) {
                JSONArray jsonArrayRcpa = new JSONArray(json.getString("RCPAEntry"));
                for (int i = 0; i < jsonArrayRcpa.length(); i++) {
                    JSONObject jsonRcpa = jsonArrayRcpa.getJSONObject(i);
                    CompFullValues = jsonRcpa.getDouble("OPValue");
                    JSONArray jsonArrayChem = jsonRcpa.getJSONArray("Chemists");
                    ChemName = jsonArrayChem.getJSONObject(0).getString("Name");
                    CheCode = jsonArrayChem.getJSONObject(0).getString("Code");
                    JSONArray jsonArrayComp = jsonRcpa.getJSONArray("Competitors");
                    Log.v("jsonExtractOnline", "----" + jsonArrayComp);
                    for (int j = 0; j < jsonArrayComp.length(); j++) {
                        JSONObject jsonComp = jsonArrayComp.getJSONObject(j);
                        CompFullValues = CompFullValues + jsonComp.getDouble("CPValue");
                        RCPASelectCompSide.rcpa_comp_list.add(new RCPAAddedCompList(ChemName, CheCode, jsonRcpa.getString("OPName"), jsonRcpa.getString("OPCode"), jsonComp.getString("CompName"), jsonComp.getString("CompCode"), jsonComp.getString("CompPName"), jsonComp.getString("CompPCode"), jsonComp.getString("CPQty"), jsonComp.getString("CPRate"), jsonComp.getString("CPValue"), jsonComp.getString("CPRemarks"), String.valueOf(CompFullValues)));
                    }

                    RCPAFragment.ProductSelectedList.add(new RCPAAddedProdList(ChemName, CheCode, jsonRcpa.getString("OPName"), jsonRcpa.getString("OPCode"), jsonRcpa.getString("OPQty"), jsonRcpa.getString("OPRate"), jsonRcpa.getString("OPValue"), String.valueOf(CompFullValues)));
                    double getTotalValue = 0.0;
                    ArrayList<Double> double_data = new ArrayList<>();
                    if (RCPAFragment.ProductSelectedList.size() > 0) {
                        for (int m = 0; m < RCPAFragment.ProductSelectedList.size(); m++) {
                            if (RCPAFragment.ProductSelectedList.get(m).getChe_codes().equalsIgnoreCase(CheCode)) {
                                double_data.add(Double.parseDouble(RCPAFragment.ProductSelectedList.get(m).getTotalPrdValue()));
                            }
                        }
                    } else {
                        RCPAFragment.ChemistSelectedList.add(new CustList(ChemName, CheCode, String.valueOf(CompFullValues), ""));
                    }

                    if (double_data.size() > 0) {
                        for (int m = 0; m < double_data.size(); m++) {
                            getTotalValue = getTotalValue + double_data.get(m);
                        }
                    }

                    double valueRounded = Math.round(getTotalValue * 100D) / 100D;

                    RCPAFragment.ChemistSelectedList.add(new CustList(ChemName, CheCode, String.valueOf(valueRounded), ""));

                    int count = RCPAFragment.ChemistSelectedList.size();
                    for (int m = 0; m < count; m++) {
                        for (int j = m + 1; j < count; j++) {
                            if (RCPAFragment.ChemistSelectedList.get(m).getCode().equalsIgnoreCase(RCPAFragment.ChemistSelectedList.get(j).getCode())) {
                                String value = RCPAFragment.ChemistSelectedList.get(j).getTotalRcpa();
                                RCPAFragment.ChemistSelectedList.remove(j--);
                                RCPAFragment.ChemistSelectedList.set(m, new CustList(ChemName, CheCode, value, ""));
                                count--;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.v("jsonExtractLocal", "----" + e);
        }
    }

    private void CreateJsonFileCall() {
        try {
            CurrentDate = CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd");
            CurrentTime = CommonUtilsMethods.getCurrentInstance("HH:mm:ss");
            if (isFromActivity.equalsIgnoreCase("new")) {
                gpsTrack = new GPSTrack(this);
                lat = gpsTrack.getLatitude();
                lng = gpsTrack.getLongitude();
                address = CommonUtilsMethods.gettingAddress(this, lat, lng, false);
            } else {
                address = CommonUtilsMethods.gettingAddress(this, Double.parseDouble(latEdit), Double.parseDouble(lngEdit), false);
            }


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
                Log.v("final_value_call", "---inputzise---" + CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty());
                if (CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty().isEmpty()) {
                    json_joint.put("IQty", "0");
                } else {
                    json_joint.put("IQty", CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty());
                }
                jsonArray.put(json_joint);
            }
            jsonSaveDcr.put("Inputs", jsonArray);


            //Detailing & Products
            jsonArray = new JSONArray();

            for (int i = 0; i < DetailedFragment.callDetailingLists.size(); i++) {
                arr.clear();
                JSONObject json_product = new JSONObject();
                JSONArray jsonArray2 = new JSONArray();
                json_product.put("Code", DetailedFragment.callDetailingLists.get(i).getBrandCode());
                json_product.put("Name", DetailedFragment.callDetailingLists.get(i).getBrandName());
                json_product.put("Group", "1");
                json_product.put("ProdFeedbk", DetailedFragment.callDetailingLists.get(i).getFeedback());
                json_product.put("Rating", DetailedFragment.callDetailingLists.get(i).getRating());
                JSONObject json_date1 = new JSONObject();
                json_date1.put("sTm", DetailedFragment.callDetailingLists.get(i).getDate() + " " + DetailedFragment.callDetailingLists.get(i).getSt_end_time().substring(0, (DetailedFragment.callDetailingLists.get(i).getSt_end_time().indexOf(" "))));
                json_date1.put("eTm", DetailedFragment.callDetailingLists.get(i).getDate() + " " + DetailedFragment.callDetailingLists.get(i).getSt_end_time().substring((DetailedFragment.callDetailingLists.get(i).getSt_end_time().indexOf(" ")) + 1));
                json_product.put("Timesline", json_date1);
                json_product.put("Appver", Constants.APP_VERSION);
                json_product.put("Mod", Constants.APP_MODE);
                json_product.put("SmpQty", "");
                json_product.put("RxQty", "");
                json_product.put("RcpaQty", "");
                json_product.put("Promoted", "");
                json_product.put("Type", CallActivityCustDetails.get(0).getType());
                json_product.put("StockistName", "");
                json_product.put("StockistCode", "");

                for (int j = 0; j < arrayStore.size(); j++) {
                    if (DetailedFragment.callDetailingLists.get(i).getBrandName().equalsIgnoreCase(arrayStore.get(j).getBrdName())) {
                        if (!arrayStore.get(j).getScribble().isEmpty()) {
                            arr.add(new StoreImageTypeUrl(arrayStore.get(j).getScribble(), arrayStore.get(j).getSlideNam(), arrayStore.get(j).getSlideTyp(), arrayStore.get(j).getSlideUrl(), arrayStore.get(j).getRemTime(), arrayStore.get(j).getSlideComments(), arrayStore.get(j).getTiming()));
                        } else {
                            arr.add(new StoreImageTypeUrl("", arrayStore.get(j).getSlideNam(), arrayStore.get(j).getSlideTyp(), arrayStore.get(j).getSlideUrl(), arrayStore.get(j).getRemTime(), "", arrayStore.get(j).getTiming()));
                        }
                    }
                }

                JSONObject jsonSlide;
                for (int i1 = 0; i1 < arr.size(); i1++) {
                    StoreImageTypeUrl mm1 = arr.get(i1);
                    jsonSlide = new JSONObject();
                    jsonSlide.put("Slide", mm1.getSlideNam());
                    jsonSlide.put("SlidePath", mm1.getSlideUrl());
                    jsonSlide.put("Scribbles", mm1.getScribble());
                    jsonSlide.put("SlideRemarks", mm1.getSlideComments());
                    jsonSlide.put("SlideType", mm1.getSlideTyp());
                    jsonSlide.put("SlideRating", mm1.getRemTime());
                    JSONObject json_date;
                    JSONArray savejsonTime = new JSONArray();
                    JSONArray jj = new JSONArray(mm1.getTiming());
                    for (int t = 0; t < jj.length(); t++) {
                        json_date = new JSONObject();
                        JSONObject js = jj.getJSONObject(t);
                        json_date.put("eTm", DetailedFragment.callDetailingLists.get(i).getDate() + " " + js.getString("eT"));
                        json_date.put("sTm", DetailedFragment.callDetailingLists.get(i).getDate() + " " + js.getString("sT"));
                        savejsonTime.put(json_date);
                    }
                    jsonSlide.put("Times", savejsonTime);
                    Log.v("final_value_call", "--slidededee--" + savejsonTime);
                    jsonArray2.put(jsonSlide);
                }

                json_product.put("Slides", jsonArray2);
                jsonArray.put(json_product);
            }


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
                if (CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().isEmpty()) {
                    json_product.put("SmpQty", "0");
                } else {
                    json_product.put("SmpQty", CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty());
                }

                if (CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().isEmpty()) {
                    json_product.put("RxQty", "0");
                } else {
                    json_product.put("RxQty", CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty());
                }

                if (CheckProductListAdapter.saveCallProductListArrayList.get(i).getRcpa_qty().isEmpty()) {
                    json_product.put("RcpaQty", "0");
                } else {
                    json_product.put("RcpaQty", CheckProductListAdapter.saveCallProductListArrayList.get(i).getRcpa_qty());
                }

                json_product.put("Promoted", CheckProductListAdapter.saveCallProductListArrayList.get(i).getPromoted());
                json_product.put("Type", CallActivityCustDetails.get(0).getType());
                json_product.put("StockistName", "");
                json_product.put("StockistCode", "");
                if (!UtilityClass.isNetworkAvailable(getApplicationContext())) {
                    json_product.put("category", CheckProductListAdapter.saveCallProductListArrayList.get(i).getCategory());
                    json_product.put("lastStock", CheckProductListAdapter.saveCallProductListArrayList.get(i).getLast_stock());
                    json_product.put("balStock", CheckProductListAdapter.saveCallProductListArrayList.get(i).getBalance_sam_stk());
                }
                JSONArray jsonArraySlides = new JSONArray();
                json_product.put("Slides", jsonArraySlides);
                jsonArray.put(json_product);
            }
            jsonSaveDcr.put("Products", jsonArray);

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
                            if (FinalAdditionalCallAdapter.nestedProduct.get(j).getSample_qty().isEmpty()) {
                                json_AdditionalInput.put("SamQty", "0");
                            } else {
                                json_AdditionalInput.put("SamQty", FinalAdditionalCallAdapter.nestedProduct.get(j).getSample_qty());
                            }
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
                            if (FinalAdditionalCallAdapter.nestedInput.get(j).getInp_qty().isEmpty()) {
                                json_AdditionalSample.put("InpQty", "0");
                            } else {
                                json_AdditionalSample.put("InpQty", FinalAdditionalCallAdapter.nestedInput.get(j).getInp_qty());
                            }
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

                for (int j = 0; j < RCPASelectCompSide.rcpa_comp_list.size(); j++) {
                    JSONObject json_Obj_comp = new JSONObject();
                    if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getPrd_code())) {
                        json_Obj_comp.put("CPQty", RCPASelectCompSide.rcpa_comp_list.get(j).getQty());
                        json_Obj_comp.put("CPRate", RCPASelectCompSide.rcpa_comp_list.get(j).getRate());
                        json_Obj_comp.put("CPValue", RCPASelectCompSide.rcpa_comp_list.get(j).getValue());
                        json_Obj_comp.put("CompCode", RCPASelectCompSide.rcpa_comp_list.get(j).getComp_company_code());
                        json_Obj_comp.put("CompName", RCPASelectCompSide.rcpa_comp_list.get(j).getComp_company_name());
                        json_Obj_comp.put("CompPCode", RCPASelectCompSide.rcpa_comp_list.get(j).getComp_product_code());
                        json_Obj_comp.put("CompPName", RCPASelectCompSide.rcpa_comp_list.get(j).getComp_product());
                        json_Obj_comp.put("Chemname", RCPASelectCompSide.rcpa_comp_list.get(j).getChem_names());
                        json_Obj_comp.put("Chemcode", RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code());
                        json_Obj_comp.put("CPRemarks", RCPASelectCompSide.rcpa_comp_list.get(j).getRemarks());
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
            if (isFromActivity.equalsIgnoreCase("new")) {
                jsonSaveDcr.put("Entry_location", lat + ":" + lng);
            } else {
                jsonSaveDcr.put("Entry_location", latEdit + ":" + lngEdit);
            }
            jsonSaveDcr.put("address", address);
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

            JSONArray jsonArrayWt = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i = 0; i < jsonArrayWt.length(); i++) {
                JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
                if (workTypeData.getString("FWFlg").equalsIgnoreCase("F")) {
                    jsonSaveDcr.put("WT_code", workTypeData.getString("Code"));
                    jsonSaveDcr.put("WTName", workTypeData.getString("Name"));
                    jsonSaveDcr.put("FWFlg", workTypeData.getString("FWFlg"));
                    FwFlag = workTypeData.getString("FWFlg");
                }
            }

            jsonSaveDcr.put("town_code", CallActivityCustDetails.get(0).getTown_code());
            jsonSaveDcr.put("town_name", CallActivityCustDetails.get(0).getTown_name());
            jsonSaveDcr.put("ModTime", CurrentDate + " " + CurrentTime);
            jsonSaveDcr.put("ReqDt", CurrentDate + " " + CurrentTime);
            jsonSaveDcr.put("vstTime", CurrentDate + " " + CurrentTime);
            jsonSaveDcr.put("Remarks", JWOthersFragment.jwOthersBinding.edRemarks.getText());
            if (isFromActivity.equalsIgnoreCase("edit_online")) {
                jsonSaveDcr.put("amc", CallActivityCustDetails.get(0).getADetSlNo());
            } else {
                jsonSaveDcr.put("amc", "");
            }

            if (!TextUtils.isEmpty(HosNeed)) {
                jsonSaveDcr.put("hospital_code", "");
                jsonSaveDcr.put("hospital_name", "");
            }

            //Validation
            jsonSaveDcr.put("sample_validation", SampleValidation);
            jsonSaveDcr.put("input_validation", InputValidation);

            //EventCapture
            jsonArray = new JSONArray();
            if (JWOthersFragment.callCaptureImageLists.size() > 0) {
                jsonImage = new JSONObject();
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

                jsonSaveDcr.put("filepath", "");

                for (int i = 0; i < JWOthersFragment.callCaptureImageLists.size(); i++) {
                    JSONObject json_Eve_cap = new JSONObject();
                    json_Eve_cap.put("EventCapture", "True");
                    json_Eve_cap.put("EventImageName", JWOthersFragment.callCaptureImageLists.get(i).getSystemImgName());
                    json_Eve_cap.put("EventImageTitle", JWOthersFragment.callCaptureImageLists.get(i).getImg_name());
                    json_Eve_cap.put("EventImageDescription", JWOthersFragment.callCaptureImageLists.get(i).getImg_description());
                    json_Eve_cap.put("Eventfilepath", JWOthersFragment.callCaptureImageLists.get(i).getFilePath());
                    jsonArray.put(json_Eve_cap);
                }
                jsonSaveDcr.put("EventCapture", jsonArray);
            }

            //POB
            String pobValue = Objects.requireNonNull(JWOthersFragment.jwOthersBinding.edPob.getText()).toString();
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
            isCreateJsonSuccess = false;
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

            switch (CallActivityCustDetails.get(0).getType()) {
                case "1": //Dr
                    //Caption
                    capPrd = loginResponse.getDoc_Product_caption();
                    capInp = loginResponse.getDoc_Input_caption();
                    CapSamQty = loginResponse.getDrSmpQCap();
                    CapRxQty = loginResponse.getDrRxQCap();
                    CapPob = loginResponse.getDoc_pob_caption();

                    //Need
                    RCPANeed = loginResponse.getRcpaNd();
                    PobNeed = loginResponse.getDoc_Pob_Need();
                    OverallFeedbackNeed = loginResponse.getDFNeed();
                    EventCaptureNeed = loginResponse.getDENeed();
                    JwNeed = loginResponse.getDoc_jointwork_Need();
                    PrdSamNeed = loginResponse.getDrSampNd();
                    PrdRxNeed = loginResponse.getDrRxNd();
                    CusCheckInOutNeed = loginResponse.getCustSrtNd();

                    //Mandatory
                    PrdMandatory = loginResponse.getDrPrdMd();
                    InpMandatory = loginResponse.getDrInpMd();
                    SamQtyMandatory = loginResponse.getDrSmpQMd();
                    RxQtyMandatory = loginResponse.getDrRxQMd();
                    RcpaMandatory = loginResponse.getRcpaMd();
                    MgrRcpaMandatory = loginResponse.getRcpaMd_Mgr();
                    EventCapMandatory = loginResponse.getDrEvent_Md();
                    PobMandatory = loginResponse.getDoc_Pob_Mandatory_Need();
                    FeedbackMandatory = loginResponse.getDrFeedMd();
                    JwMandatory = loginResponse.getDoc_jointwork_Mandatory_Need();
                    RemarkMandatory = loginResponse.getTempNd();
                    break;
                case "2": //Chemist
                    //Caption
                    capPrd = loginResponse.getChm_Product_caption();
                    capInp = loginResponse.getChm_Input_caption();
                    CapSamQty = loginResponse.getChmSmpCap();
                    CapRxQty = loginResponse.getChmQCap();
                    CapPob = loginResponse.getChm_pob_caption();

                    //Need
                    RCPANeed = loginResponse.getChm_RCPA_Need();
                    PobNeed = loginResponse.getChm_Pob_Need();
                    EventCaptureNeed = loginResponse.getCENeed();
                    OverallFeedbackNeed = loginResponse.getCFNeed();
                    JwNeed = loginResponse.getChm_jointwork_Need();
                    PrdSamNeed = loginResponse.getChmsamQty_need();
                    PrdRxNeed = loginResponse.getChmRxQty(); //1
                    CusCheckInOutNeed = loginResponse.getChmSrtNd();

                    //Mandatory
                    //   RcpaMandatory = loginResponse.getRcpaMd(); //Check This one
                    PobMandatory = loginResponse.getChm_Pob_Mandatory_Need();
                    EventCapMandatory = loginResponse.getChmEvent_Md();
                    JwMandatory = loginResponse.getChm_jointwork_Mandatory_Need();
                    RcpaMandatory = loginResponse.getChmRcpaMd();
                    MgrRcpaMandatory = loginResponse.getChmRcpaMd_Mgr();
                    break;
                case "3": //Stockiest
                    //Caption
                    capPrd = loginResponse.getStk_Product_caption();
                    capInp = loginResponse.getStk_Input_caption();
                    CapSamQty = "Samples";
                    CapRxQty = loginResponse.getStkQCap();
                    CapPob = loginResponse.getStk_pob_caption();

                    //Need
                    PobNeed = loginResponse.getStk_Pob_Need();
                    OverallFeedbackNeed = loginResponse.getSFNeed();
                    EventCaptureNeed = loginResponse.getSENeed();
                    JwNeed = loginResponse.getStk_jointwork_Need();
                    PrdSamNeed = "0";
                    PrdRxNeed = loginResponse.getStk_Pob_Need();
                    CusCheckInOutNeed = "1";

                    //Mandatory
                    EventCapMandatory = loginResponse.getStkEvent_Md();
                    JwMandatory = loginResponse.getStk_jointwork_Mandatory_Need();
                    break;
                case "4": //UNListed Dr
                    //Caption
                    capPrd = loginResponse.getUl_Product_caption();
                    capInp = loginResponse.getUl_Input_caption();
                    CapSamQty = loginResponse.getNLSmpQCap();
                    CapRxQty = loginResponse.getNLRxQCap();
                    CapPob = loginResponse.getUldoc_pob_caption();
                    CusCheckInOutNeed = loginResponse.getUnlistSrtNd();

                    //Need
                    PobNeed = loginResponse.getUl_Pob_Need();
                    OverallFeedbackNeed = loginResponse.getNFNeed();
                    EventCaptureNeed = loginResponse.getNENeed();
                    JwNeed = loginResponse.getUl_jointwork_Need();
                    PrdSamNeed = "0";
                    PrdRxNeed = loginResponse.getUl_Pob_Need();
                    CusCheckInOutNeed = "1";

                    //Mandatory
                    EventCapMandatory = loginResponse.getUlDrEvent_Md();
                    JwMandatory = loginResponse.getUl_jointwork_Mandatory_Need();
                    break;
                case "5"://CIP
                    //Caption
                    CapPob = loginResponse.getCIP_pob_caption();

                    //Need
                    PobNeed = loginResponse.getCIPPOBNd();
                    OverallFeedbackNeed = loginResponse.getCIP_FNeed();
                    EventCaptureNeed = loginResponse.getCipEvent_Md();
                    JwNeed = loginResponse.getCIP_jointwork_Need();
                    PrdSamNeed = "0";
                    PrdRxNeed = "0";
                    CusCheckInOutNeed = loginResponse.getCipSrtNd();

                    //Mandatory
                    EventCapMandatory = loginResponse.getCipEvent_Md();
                    PobMandatory = loginResponse.getCIPPOBMd();
                    break;
                case "6"://HOSPITAL
                    //Caption
                    CapPob = loginResponse.getHosp_pob_caption();
                    CusCheckInOutNeed = "1";

                    //Need
                    PobNeed = loginResponse.getHosPOBNd();
                    OverallFeedbackNeed = loginResponse.getHFNeed();
                    EventCaptureNeed = loginResponse.getHospEvent_Md();
                    JwNeed = "0";
                    PrdSamNeed = "0";
                    PrdRxNeed = "0";

                    //Mandatory
                    EventCapMandatory = loginResponse.getHospEvent_Md();
                    //   PobMandatory = loginResponse.getCIPPOBMd();
                    break;
            }

            SampleValidation = loginResponse.getSample_validation();
            InputValidation = loginResponse.getInput_validation();
            GeoChk = loginResponse.getGeoChk();
            HosNeed = loginResponse.getHosp_need();

            if (loginResponse.getSample_Val_Qty().equalsIgnoreCase("0")) {
                SamQtyRestriction = "1";
            } else {
                SamQtyRestriction = "0";
                SamQtyRestrictValue = loginResponse.getSample_Val_Qty();
            }

            if (loginResponse.getInput_Val_Qty().equalsIgnoreCase("0")) {
                InpQtyRestriction = "1";
            } else {
                InpQtyRestriction = "0";
                InpQtyRestrictValue = loginResponse.getInput_Val_Qty();
            }

            assert isFromActivity != null;
            if (isFromActivity.equalsIgnoreCase("new")) {
                try {
                    if (SfType.equalsIgnoreCase("1")) {
                        TodayPlanSfCode = SfCode;
                    } else {
                        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(this);
                        if (TodayPlanSfCode.isEmpty()) {
                            JSONArray jsonArray1 = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                            for (int i = 0; i < 1; i++) {
                                JSONObject jsonHQList = jsonArray1.getJSONObject(0);
                                TodayPlanSfCode = jsonHQList.getString("id");
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }


            if (isFromActivity.equalsIgnoreCase("edit_local")) {
                try {
                    JSONObject json = new JSONObject(CallActivityCustDetails.get(0).getJsonArray());
                    if (SfType.equalsIgnoreCase("2")) {
                        TodayPlanSfCode = json.getString("AppUserSF");
                    } else {
                        TodayPlanSfCode = SfCode;
                    }

                    JSONArray jsonPrdArray = new JSONArray(json.getString("Products"));
                    for (int i = 0; i < jsonPrdArray.length(); i++) {
                        JSONObject js = jsonPrdArray.getJSONObject(i);
                        if (js.getString("Group").equalsIgnoreCase("1")) {
                            isDetailingRequired = "true";
                            break;
                        }
                    }
                } catch (Exception ignored) {

                }
            }

            if (isFromActivity.equalsIgnoreCase("edit_online")) {
                try {
                    JSONObject json = new JSONObject(CallActivityCustDetails.get(0).getJsonArray());
                    if (SfType.equalsIgnoreCase("2")) {
                        JSONArray jsonSfCode = new JSONArray(json.getString("DCRDetail"));
                        JSONObject js = jsonSfCode.getJSONObject(0);
                        TodayPlanSfCode = js.getString("DataSF");
                    } else {
                        TodayPlanSfCode = SfCode;
                    }

                    if (!json.getString("DigitalHead").equalsIgnoreCase("[]")) {
                        JSONArray jsonPrdSlides = new JSONArray(json.getString("DigitalHead"));
                        for (int i = 0; i < jsonPrdSlides.length(); i++) {
                            JSONObject jsSlidesPrds = jsonPrdSlides.getJSONObject(i);
                            if (jsSlidesPrds.getString("GroupID").equalsIgnoreCase("1")) {
                                isDetailingRequired = "true";
                                break;
                            }
                        }
                    }
                } catch (Exception ignored) {

                }
            }

            if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
                DialogCheckOut();
            }

        } catch (Exception ignored) {

        }
    }

    private void AddJWData() {
        JWOthersFragment.callCaptureImageLists = new ArrayList<>();
        JWOthersFragment.callAddedJointList = new ArrayList<>();
        JointWorkSelectionSide.JwList = new ArrayList<>();
    }

    private void AddRCPAData() {
        RCPAFragment.ChemistSelectedList = new ArrayList<>();
        RCPAFragment.ProductSelectedList = new ArrayList<>();
        RCPASelectCompSide.rcpa_comp_list = new ArrayList<>();
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
                AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), false, jsonObject.getString("Tlvst")));
            }

            int count = AdditionalCallFragment.custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (AdditionalCallFragment.custListArrayList.get(i).getCode().equalsIgnoreCase(AdditionalCallFragment.custListArrayList.get(j).getCode())) {
                        AdditionalCallFragment.custListArrayList.set(i, new CallCommonCheckedList(AdditionalCallFragment.custListArrayList.get(i).getName(), AdditionalCallFragment.custListArrayList.get(i).getCode(), AdditionalCallFragment.custListArrayList.get(i).getTown_name(), AdditionalCallFragment.custListArrayList.get(i).getTown_code(), AdditionalCallFragment.custListArrayList.get(i).isCheckedItem(), AdditionalCallFragment.custListArrayList.get(i).getTotalVisit()));
                        AdditionalCallFragment.custListArrayList.remove(j--);
                        count--;
                    } else {
                        AdditionalCallFragment.custListArrayList.set(i, new CallCommonCheckedList(AdditionalCallFragment.custListArrayList.get(i).getName(), AdditionalCallFragment.custListArrayList.get(i).getCode(), AdditionalCallFragment.custListArrayList.get(i).getTown_name(), AdditionalCallFragment.custListArrayList.get(i).getTown_code(), AdditionalCallFragment.custListArrayList.get(i).isCheckedItem(), AdditionalCallFragment.custListArrayList.get(i).getTotalVisit()));
                    }
                }
            }

        } catch (Exception e) {
            Log.v("ChkAddCalls", "--error---" + e);
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

                if (!jsonObject.getString("Code").equalsIgnoreCase("-1")) {
                    JSONObject jsonFDate = new JSONObject(jsonObject.getString("EffF"));
                    JSONObject jsonTDate = new JSONObject(jsonObject.getString("EffT"));
                    Log.v("JsonInput", jsonFDate.getString("date").substring(0, 10) + "---" + jsonTDate.getString("date").substring(0, 10));

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    String todayData = CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd");

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
            }

            if (InputFragment.checkedInputList.size() == 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("Code").equalsIgnoreCase("-1")) {
                        InputFragment.checkedInputList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false));
                        AddCallSelectInpSide.callInputList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false));
                        StockInput.add(new CallCommonCheckedList(jsonObject.getString("Code"), "0", "0"));
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
            Log.v("chkSample", "---size--111----" + jsonArray.length() + "----" + jsonArrayPrdStk.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (!jsonObject.getString("Code").equalsIgnoreCase("-1")) {
                    if (!jsonObject.getString("Product_Mode").equalsIgnoreCase("Sample")) {
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
            }

            if (ProductFragment.checkedPrdList.size() == 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("Code").equalsIgnoreCase("-1")) {
                        ProductFragment.checkedPrdList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, jsonObject.getString("Product_Mode"), jsonObject.getString("Product_Mode")));
                        StockSample.add(new CallCommonCheckedList(jsonObject.getString("Code"), "0", "0"));
                        RCPASelectPrdSide.PrdFullList.add(new SaveCallProductList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("DRate")));
                    }
                }
            }


            if (SampleValidation.equalsIgnoreCase("1")) {
                for (int i = 0; i < ProductFragment.checkedPrdList.size(); i++) {
                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
                        if (!ProductFragment.checkedPrdList.get(i).getCategory().equalsIgnoreCase("Sale") && jsonObjectSample.getString("Code").equalsIgnoreCase(ProductFragment.checkedPrdList.get(i).getCode())) {
                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
                            StockSample.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), jsonObjectSample.getString("Balance_Stock")));
                            break;
                        } else {
                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
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

            Log.v("chkSample", "---size---" + AddCallSelectPrdSide.callSampleList.size());
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
        4) Add Promoted & RCPA Products for json_object to send the dcr call Customer
        5) Add Sample and Input Data for Additional_Call_Screen*/


