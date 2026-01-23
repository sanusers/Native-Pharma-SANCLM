package saneforce.sanzen.activity.call;

import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter.storingSlide;
import static saneforce.sanzen.activity.call.fragments.jwOthers.JWOthersFragment.JWKCodeList;
import static saneforce.sanzen.activity.call.fragments.jwOthers.JWOthersFragment.callCaptureImageLists;
import static saneforce.sanzen.activity.call.fragments.jwOthers.JWOthersFragment.jwOthersBinding;
import static saneforce.sanzen.activity.call.fragments.signature.SignatureFragment1.callSignCaptureImage;
import static saneforce.sanzen.activity.call.fragments.signature.SignatureFragment1.imageName;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.IsFromDCR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.activityModule.model.ActivityDetailsModelClass;
import saneforce.sanzen.activity.call.adapter.DCRCallTabLayoutAdapter;
import saneforce.sanzen.activity.call.adapter.additionalCalls.AdditionalCusListAdapter;
import saneforce.sanzen.activity.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.sanzen.activity.call.adapter.input.CheckInputListAdapter;
import saneforce.sanzen.activity.call.adapter.product.CheckProductListAdapter;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.fragments.activity.ActivityFragment;
import saneforce.sanzen.activity.call.fragments.additionalCall.AddCallSelectInpSide;
import saneforce.sanzen.activity.call.fragments.additionalCall.AddCallSelectPrdSide;
import saneforce.sanzen.activity.call.fragments.additionalCall.AdditionalCallDetailedSide;
import saneforce.sanzen.activity.call.fragments.additionalCall.AdditionalCallFragment;
import saneforce.sanzen.activity.call.fragments.detailing.DetailedFragment;
import saneforce.sanzen.activity.call.fragments.input.InputFragment;
import saneforce.sanzen.activity.call.fragments.jwOthers.FeedbackSelectionSide;
import saneforce.sanzen.activity.call.fragments.jwOthers.JWOthersFragment;
import saneforce.sanzen.activity.call.fragments.jwOthers.JointWorkSelectionSide;
import saneforce.sanzen.activity.call.fragments.product.ProductFragment;
import saneforce.sanzen.activity.call.fragments.rcpa.RCPAFragment;
import saneforce.sanzen.activity.call.fragments.rcpa.RCPASelectCompSide;
import saneforce.sanzen.activity.call.fragments.rcpa.RCPASelectPrdSide;
import saneforce.sanzen.activity.call.fragments.signature.SignatureFragment1;
import saneforce.sanzen.activity.call.pojo.CallCaptureImageList;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.activity.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanzen.activity.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanzen.activity.call.pojo.additionalCalls.SaveAdditionalCall;
import saneforce.sanzen.activity.call.pojo.detailing.CallDetailingList;
import saneforce.sanzen.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.sanzen.activity.call.pojo.input.SaveCallInputList;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.activity.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanzen.activity.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.activity.remaindercalls.RemaindercallsActivity;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonSharedPreference;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityDcrcallBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OutboxUtil;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DCRCallActivity extends AppCompatActivity {
    public static ArrayList<CustList> CallActivityCustDetails;
    public static ArrayList<StoreImageTypeUrl> arrayStore;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrcallBinding dcrCallBinding;
    public static String clickedLocalDate, SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, PobNeed, CapPob, OverallFeedbackNeed, EventCaptureNeed, JwNeed, CusCheckInOutNeed, SampleValidation, InputValidation, PrdSamNeed, PrdRxNeed, PrdRcpaQtyNeed, CapSamQty, CapRxQty, RcpaCompetitorAdd, SamQtyRestriction, SamQtyRestrictValue, InpQtyRestriction, InpQtyRestrictValue, TodayPlanSfCode, PrdMandatory = "0", InpMandatory = "0", SignNeed, SignMandatory;
    public static ArrayList<CallCommonCheckedList> StockSample = new ArrayList<>();
    public static ArrayList<CallCommonCheckedList> StockInput = new ArrayList<>();
    public static String isFromActivity, save_valid, hqcode, detailedProducts;
    public static String isDetailingRequired;
    ArrayList<StoreImageTypeUrl> arr = new ArrayList<>();
    DCRCallTabLayoutAdapter viewPagerAdapter;
    private final ArrayList<String> pages = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference commonSharedPreference;
    ProgressDialog progressDialog = null;
    GPSTrack gpsTrack;
    private JSONObject checkInOutJsonObject = new JSONObject(), checkOutJsonObject, jsonSaveDcr, jsonImage, jsonSign;
    String GeoChk, capPrd, capInp, capActivity, RCPANeed, HosNeed, FeedbackMandatory, CurrentDate, MgrRcpaMandatory, EventCapMandatory, JwMandatory, CurrentTime, RcpaMandatory, PobMandatory, RemarkMandatory, SamQtyMandatory, RxQtyMandatory, InputNeed, ProductNeed, AdditionalCallNeed, ActivityNeed, ActivityMandatory/*, SignNeed, SignMandatory*/;
    double lat, lng;
    ApiInterface api_interface;
    String ChemName = "", CheCode = "";
    JSONArray remArray = new JSONArray();
    JSONObject remObj;
    double CompFullValues = 0;
    boolean isCreateJsonSuccess;
    String FwFlag, FeildName;
    Dialog dialogCheckOut;
    Button btnCheckOut;
    TextView tv_address_in, tv_dateTime_in, tvLatLong_in;
    TextView tv_address, tv_dateTime, tvLatLong;
    ImageView imgClose;
    String address, latEdit, lngEdit, VistTime, activityDate;
    int mBatteryPercent = 0;
    SignatureFragment1 signatureFragment1;
    RoomDB roomDB;
    MasterDataDao masterDataDao;
    private CallOfflineECDataDao callOfflineECDataDao;
    private CallOfflineSignDataDao callOfflineSignDataDao;
    private CallOfflineDataDao callOfflineDataDao;
    private OutboxUtil outboxUtil;
    AlertDialog customDialog;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Handler handler1 = new Handler();
    long delay = 4000;
    Runnable runnable;
    private Runnable runnable1;
    private int limit = 1;
    private ProgressBar progressBar;
    private RelativeLayout refreshLocation;

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("call", CallActivityCustDetails);
        outState.putString("CheckInJsonObject", checkInOutJsonObject.toString());
        if (HomeDashBoard.selectedDate != null) {
            outState.putString("date", HomeDashBoard.selectedDate.toString());
            outState.putInt(Manifest.permission.ACCESS_FINE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
            outState.putInt(Manifest.permission.ACCESS_COARSE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
            outState.putInt(Manifest.permission.CAMERA, ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                outState.putInt(Manifest.permission.READ_MEDIA_AUDIO, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO));
//                outState.putInt(Manifest.permission.READ_MEDIA_VIDEO, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO));
//                outState.putInt(Manifest.permission.READ_MEDIA_IMAGES, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES));
//            }
            outState.putInt(Manifest.permission.READ_EXTERNAL_STORAGE, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE));
            outState.putInt(Manifest.permission.WRITE_EXTERNAL_STORAGE, ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        }
        outState.putBoolean("isSaved", true);
        Log.d("save instance", "onSaveInstanceState: " + outState.size() + " -> " + Arrays.toString(outState.keySet().toArray()));
    }

    private final Handler handler = new Handler();
    //    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
    private final Runnable updateClock = new Runnable() {
        @Override
        public void run() {
//            String currentTime = sdf.format(new Date());
            handler.postDelayed(this, 10000);
            try {
//                String checkInData = SharedPref.getDayCheckInData(DCRCallActivity.this);
                boolean checkInNeed = false;
                switch (CallActivityCustDetails.get(0).getType()) {
                    case "1":
                        checkInNeed = SharedPref.getCustSrtNd(DCRCallActivity.this).equalsIgnoreCase("0");
                        break;
                    case "2":
                        checkInNeed = SharedPref.getChmSrtNd(DCRCallActivity.this).equalsIgnoreCase("0");
                        break;
                    case "4":
                        checkInNeed = SharedPref.getUnlistSrtNd(DCRCallActivity.this).equalsIgnoreCase("0");
                        break;
                }
                if (checkInNeed && checkInOutJsonObject != null && !checkInOutJsonObject.toString().equalsIgnoreCase((new JSONObject()).toString()) && HomeDashBoard.selectedDate != null) {
                    JSONObject checkInObj = checkInOutJsonObject;
                    String currentDate = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5),
                            previousDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_5, (LocalDate.now().minusDays(1)).toString()),
                            homeDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_5, HomeDashBoard.selectedDate.toString()),
                            checkInDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, checkInObj.optString("InDateTime"));
                    if (checkInDate.equalsIgnoreCase(previousDate) && homeDate.equalsIgnoreCase(previousDate) && !SharedPref.getCheckInSkipDate(DCRCallActivity.this).equalsIgnoreCase(currentDate)) {
                        SharedPref.setCheckInSkipDate(DCRCallActivity.this, currentDate);
                        Log.d("Clock", "run: log out");
                        SharedPref.saveLoginState(DCRCallActivity.this, false);
                        Intent intent = new Intent(DCRCallActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrCallBinding = ActivityDcrcallBinding.inflate(getLayoutInflater());
        setContentView(dcrCallBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonSharedPreference = new CommonSharedPreference(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        roomDB = RoomDB.getDatabase(getApplicationContext());
        masterDataDao = roomDB.masterDataDao();
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineSignDataDao = roomDB.callOfflineSignDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        outboxUtil = new OutboxUtil(this);
        gpsTrack = new GPSTrack(this);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

        if (savedInstanceState != null && savedInstanceState.getBoolean("isSaved")) {
            Log.i("TAG1", "onCreate: " + savedInstanceState.size());
            Log.i("TAG2", "onCreate: " + Arrays.toString(savedInstanceState.keySet().toArray()));
            CallActivityCustDetails = savedInstanceState.getParcelableArrayList("call");

            boolean checkInNeed = false;
            switch (CallActivityCustDetails.get(0).getType()) {
                case "1":
                    checkInNeed = SharedPref.getCustSrtNd(DCRCallActivity.this).equalsIgnoreCase("0");
                    break;
                case "2":
                    checkInNeed = SharedPref.getChmSrtNd(DCRCallActivity.this).equalsIgnoreCase("0");
                    break;
                case "4":
                    checkInNeed = SharedPref.getUnlistSrtNd(DCRCallActivity.this).equalsIgnoreCase("0");
                    break;
            }
            if (checkInNeed) {
                String jsonObject = savedInstanceState.getString("CheckInJsonObject");
                try {
                    checkInOutJsonObject = new JSONObject(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("date") != null) {
                HomeDashBoard.selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
            }
//            CommonAlertBox.permissionChangeAlert(this);
//            if(SharedPref.getGeoNeed(this).equalsIgnoreCase("0")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_FINE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_COARSE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != savedInstanceState.getInt(Manifest.permission.CAMERA, -1)
//                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_AUDIO, -1)
//                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_VIDEO, -1)
//                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_IMAGES, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.READ_EXTERNAL_STORAGE, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.WRITE_EXTERNAL_STORAGE, -1)) {
                CommonAlertBox.permissionChangeAlert(this);
            }
//                    Log.e("TAG", "onCreate: Location permission disabled FL");
//                }
//                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    Log.e("TAG", "onCreate: Location permission disabled CL");
//                }
//            }
//            boolean ecNeed = false;
//            if(CallActivityCustDetails != null && !CallActivityCustDetails.isEmpty()) {
//                switch (CallActivityCustDetails.get(0).getType()) {
//                    case "1":
//                        if(SharedPref.getDeNeed(this).equalsIgnoreCase("0")) ecNeed = true;
//                        break;
//                    case "2":
//                        if(SharedPref.getCeNeed(this).equalsIgnoreCase("0")) ecNeed = true;
//                        break;
//                    case "3":
//                        if(SharedPref.getSeNeed(this).equalsIgnoreCase("0")) ecNeed = true;
//                        break;
//                    case "4":
//                        if(SharedPref.getNeNeed(this).equalsIgnoreCase("0")) ecNeed = true;
//                        break;
//                }
//                if(ecNeed){
//                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        Log.e("TAG", "onCreate: Camera permission disabled");
//                    }
//                }
//            }
//            HomeDashBoard.binding.textDate.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_27, savedInstanceState.getString("date")));
        }

        detailedProducts = "";
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            isDetailingRequired = extra.getString(Constants.DETAILING_REQUIRED);
            isFromActivity = extra.getString(Constants.DCR_FROM_ACTIVITY);
            detailedProducts = extra.getString("DetailedProducts", "");
            save_valid = extra.getString("remainder_save");
            hqcode = extra.getString("hq_code");
            if (extra.containsKey("CheckInJsonObject")) {
                String jsonObject = extra.getString("CheckInJsonObject");
                try {
                    checkInOutJsonObject = new JSONObject(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
//        handler.post(updateClock);

        dcrCallBinding.tagCustName.setText(CallActivityCustDetails.get(0).getName());
        getRequiredData();
        SetupTabLayout();
        AddProductsData();
        AddInputData();
        AddAdditionalCallData();
        AddRCPAData();
        AddJWData();
        AddActivityData();
        AddSignData();

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

        dcrCallBinding.ivBack.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
//            storingSlide.clear();
//            if (isFromActivity.equalsIgnoreCase("new")) {
//                callsUtil.deleteOfflineCalls(CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
//                Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            } else {
//                getOnBackPressedDispatcher().onBackPressed();
//            }
                handleCancel();
            }
        });

        dcrCallBinding.btnCancel.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
//                Dialog   dialog = new Dialog(this);
//                dialog.setContentView(R.layout.dcr_cancel_alert);
//                dialog.setCancelable(false);
//                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//                TextView btn_yes=dialog.findViewById(R.id.btn_yes);
//                TextView btn_no=dialog.findViewById(R.id.btn_no);
//
//
//            btn_yes.setOnClickListener(view12 -> {
//                storingSlide.clear();
//                dialog.dismiss();
//                if (save_valid.equalsIgnoreCase("1")){
//                    finish();
//                }else{
//                    if (isFromActivity.equalsIgnoreCase("new")) {
//                        callsUtil.deleteOfflineCalls(CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
//                        Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        getOnBackPressedDispatcher().onBackPressed();
//                    }
//                }
//            });
//
//            btn_no.setOnClickListener(view12 -> {
//                dialog.dismiss();
//            });
                handleCancel();
            }
        });

        dcrCallBinding.btnFinalSubmit.setOnClickListener(view -> {
                onSubmitClicked();
        });

        assert isFromActivity != null;
        if (isFromActivity.equalsIgnoreCase("edit_local")) {
            jsonExtractLocal(CallActivityCustDetails.get(0).getJsonArray());
        } else if (isFromActivity.equalsIgnoreCase("edit_online")) {
            jsonExtractOnline(CallActivityCustDetails.get(0).getJsonArray());
        }
    }

    private void onSubmitClicked() {
        gpsTrack = new GPSTrack(this);
        RemaindercallsActivity.vals_rm = "";
        progressDialog = CommonUtilsMethods.createProgressDialog(DCRCallActivity.this);
        if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
            if (gpsTrack != null && ((gpsTrack.getLatitude() != 0.0) || (gpsTrack.getLongitude() != 0.0))) {
                submitCall();
            } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.no_location_please_try_again));
                progressDialog.dismiss();
            }
        } else {
            submitCall();
        }
    }

    private void submitCall() {
        if (save_valid.equalsIgnoreCase("1")) {
            remainder_calls();
        } else {
            isCreateJsonSuccess = true;
//            if(CusCheckInOutNeed.equalsIgnoreCase("0")) {
//                if(UtilityClass.isNetworkAvailable(getApplicationContext())) {
//                    gpsTrack = new GPSTrack(this);
//                    double lat = gpsTrack.getLatitude();
//                    double lng = gpsTrack.getLongitude();
//                    address = CommonUtilsMethods.gettingAddress(this, lat, lng, false);
//                }else {
//                    tv_address.setText(DCRCallActivity.this.getString(R.string.no_network));
//                }
//            }
            if (signatureFragment1 != null) {
                signatureFragment1.getSignatureBitmap();
            }
            if (checkRequiredFunctions() && checkCurrentLoc()) {
                if (CusCheckInOutNeed.equalsIgnoreCase("0")
                        && checkInOutJsonObject != null && !checkInOutJsonObject.toString().isEmpty() && !checkInOutJsonObject.toString().equalsIgnoreCase("[]")
                        && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))
                        && !isFromActivity.equalsIgnoreCase("edit_local")
                        && !isFromActivity.equalsIgnoreCase("edit_online")) {
                    dialogCheckOut();
                    progressDialog.dismiss();
                } else {
                    callSubmit();
                }
            } else {
                progressDialog.dismiss();
            }
        }
    }

    private void callSubmit() {

        createJsonFileCall();
        if (isCreateJsonSuccess) {
            Log.d("CreateJsonFileCall", "submitCall: " + "JSON FIle call is successful");

            if (isFromActivity.equalsIgnoreCase("new") || isFromActivity.equalsIgnoreCase("edit_online")) {
                InsertVisitControl();
                callOfflineDataDao.saveOfflineCallOut(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("HH:mm:ss"), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType(), jsonSaveDcr.toString(), Constants.WAITING_FOR_SYNC);
            } else if (isFromActivity.equalsIgnoreCase("edit_local")) {
                callOfflineDataDao.saveOfflineCallOut(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_15, TimeUtils.FORMAT_4, activityDate), CommonUtilsMethods.getCurrentInstance("HH:mm:ss"), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType(), jsonSaveDcr.toString(), Constants.WAITING_FOR_SYNC);
            }
//                                if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
//                                    dialogCheckOut.show();
//                                } else {
//                                    Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                    finish();
//                                }

            if (!JWOthersFragment.callCaptureImageLists.isEmpty()) {
                for (int i = 0; i < JWOthersFragment.callCaptureImageLists.size(); i++) {
                    if (!callCaptureImageLists.get(i).getFilePath().isEmpty() && !callCaptureImageLists.get(i).getSystemImgName().isEmpty()) {
                        callOfflineECDataDao.saveOfflineEC(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), JWOthersFragment.callCaptureImageLists.get(i).getSystemImgName(), JWOthersFragment.callCaptureImageLists.get(i).getFilePath(), jsonImage.toString(), Constants.WAITING_FOR_SYNC, 0);
                    }
                }
            }
            if (SignatureFragment1.callSignCaptureImage != null) {
                for (int i = 0; i < SignatureFragment1.callSignCaptureImage.size(); i++) {
                    if (!callSignCaptureImage.get(i).getFilepath().isEmpty() && !callSignCaptureImage.get(i).getImg_Name().isEmpty()) {
                        callOfflineSignDataDao.saveOfflineSign(SignatureFragment1.callSignCaptureImage.get(i).getImg_Name(), SignatureFragment1.callSignCaptureImage.get(i).getFilepath(), jsonSign.toString(), Constants.WAITING_FOR_SYNC, 0, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CallActivityCustDetails.get(i).getCode(), CallActivityCustDetails.get(i).getName());
                    }
                }
            }
            UpdateInputStock();
            UpdateSampleStock();

            storingSlide.clear();
            SharedPref.setLastCallDate(this, HomeDashBoard.selectedDate.toString());
            SharedPref.setDayPlanStartedDate(this, HomeDashBoard.selectedDate.toString());
//                    if(CusCheckInOutNeed.equalsIgnoreCase("0")) {
            //    progressDialog.dismiss();
//                        dialogCheckOut.show();
//                    }else {
            progressDialog.dismiss();
            IsFromDCR = true;
            HomeDashBoard.isDcrFrom = true;
            CallsFragment.syncCalls = true;
            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            if (!UtilityClass.isNetworkAvailable(getApplicationContext())) {
//                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_saved_locally));
                Toast.makeText(DCRCallActivity.this, getString(R.string.call_saved_locally), Toast.LENGTH_LONG).show();
            } else {
//                        commonUtilsMethods.showToastMessage(this, getString(R.string.call_saved_successfully));
                Toast.makeText(DCRCallActivity.this, getString(R.string.call_saved_successfully), Toast.LENGTH_LONG).show();
                //progressDialog.dismiss();
            }
            finish();
//                    }
        } else {
            progressDialog.dismiss();
        }
    }

    private void handleCancel() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                storingSlide.clear();
                dialog.dismiss();
                if (save_valid.equalsIgnoreCase("1")) {
                    finish();
                } else {
                    if (isFromActivity.equalsIgnoreCase("new")) {
                        outboxUtil.deleteOfflineCalls(CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                        outboxUtil.deleteOfflineActivity(CallActivityCustDetails.get(0).getCode(), HomeDashBoard.selectedDate.toString());
                        Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                }
            }
        });

        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void SetupTabLayout() {
        viewPagerAdapter = new DCRCallTabLayoutAdapter(getSupportFragmentManager());

        if (isDetailingRequired.equalsIgnoreCase("true")) {
            viewPagerAdapter.add(new DetailedFragment(), DCRCallActivity.this.getString(R.string.detailed));
            pages.add(DCRCallActivity.this.getString(R.string.detailed));
        } else {
            DetailedFragment.callDetailingLists = new ArrayList<>();
        }
//if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
//            viewPagerAdapter.add(new ProductFragment(), capPrd);
//            viewPagerAdapter.add(new InputFragment(), capInp);
//            if (RCPANeed.equalsIgnoreCase("1")) viewPagerAdapter.add(new RCPAFragment(), "RCPA");
//            if (isFromActivity.equalsIgnoreCase("new") || isFromActivity.equalsIgnoreCase("edit_local"))
//                viewPagerAdapter.add(new AdditionalCallFragment(), "Additional Calls");
//            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
//        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
//            viewPagerAdapter.add(new ProductFragment(), capPrd);
//            viewPagerAdapter.add(new InputFragment(), capInp);
//            if (RCPANeed.equalsIgnoreCase("1")) {
//                viewPagerAdapter.add(new RCPAFragment(), "RCPA");
//            }
//            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
//        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
//            viewPagerAdapter.add(new ProductFragment(), capPrd);
//            viewPagerAdapter.add(new InputFragment(), capInp);
//            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
//        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
//            viewPagerAdapter.add(new ProductFragment(), capPrd);
//            viewPagerAdapter.add(new InputFragment(), capInp);
//            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
//        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("5")) {
//            viewPagerAdapter.add(new ProductFragment(), "Product");
//            viewPagerAdapter.add(new InputFragment(), "Input");
//            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
//        }


        signatureFragment1 = new SignatureFragment1();
        if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
            if (ProductNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new ProductFragment(), capPrd);
                pages.add(capPrd);
            }
            if (save_valid.equalsIgnoreCase("0")) {
                if (InputNeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new InputFragment(), capInp);
                    pages.add(capInp);
                }
                if (AdditionalCallNeed.equalsIgnoreCase("0") && !(isFromActivity.equalsIgnoreCase("edit_local") || isFromActivity.equalsIgnoreCase("edit_online"))) {
                    viewPagerAdapter.add(new AdditionalCallFragment(), DCRCallActivity.this.getString(R.string.additional_call));
                    pages.add(DCRCallActivity.this.getString(R.string.additional_call));
                }
                if (RCPANeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new RCPAFragment(), "RCPA");
                    pages.add("RCPA");
                }
                if (ActivityNeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new ActivityFragment(), capActivity);
                    pages.add(capActivity);
                }
            }
            viewPagerAdapter.add(new JWOthersFragment(), DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));
            pages.add(DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));

            viewPagerAdapter.add(signatureFragment1, DCRCallActivity.this.getString(R.string.signature));
            pages.add(DCRCallActivity.this.getString(R.string.signature));
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
            if (ProductNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new ProductFragment(), capPrd);
                pages.add(capPrd);
            }
            if (save_valid.equalsIgnoreCase("0")) {
                if (InputNeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new InputFragment(), capInp);
                    pages.add(capInp);
                }
                if (RCPANeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new RCPAFragment(), "RCPA");
                    pages.add("RCPA");
                }
                if (ActivityNeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new ActivityFragment(), capActivity);
                    pages.add(capActivity);
                }
            }
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
            pages.add("JFW/Others");

            viewPagerAdapter.add(signatureFragment1, DCRCallActivity.this.getString(R.string.signature));
            pages.add(DCRCallActivity.this.getString(R.string.signature));
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            if (ProductNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new ProductFragment(), capPrd);
                pages.add(capPrd);
            }
            if (InputNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new InputFragment(), capInp);
                pages.add(capInp);
            }
            if (ActivityNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new ActivityFragment(), capActivity);
                pages.add(capActivity);
            }
            viewPagerAdapter.add(new JWOthersFragment(), DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));
            pages.add(DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));

            viewPagerAdapter.add(signatureFragment1, DCRCallActivity.this.getString(R.string.signature));
            pages.add(DCRCallActivity.this.getString(R.string.signature));
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
            if (ProductNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new ProductFragment(), capPrd);
                pages.add(capPrd);
            }
            if (InputNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new InputFragment(), capInp);
                pages.add(capInp);
            }
            if (ActivityNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new ActivityFragment(), capActivity);
                pages.add(capActivity);
            }
            viewPagerAdapter.add(new JWOthersFragment(), DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));
            pages.add(DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));

            viewPagerAdapter.add(signatureFragment1, DCRCallActivity.this.getString(R.string.signature));
            pages.add(DCRCallActivity.this.getString(R.string.signature));
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("5")) {
            viewPagerAdapter.add(new ProductFragment(), capPrd);
            pages.add(capPrd);
            viewPagerAdapter.add(new InputFragment(), capInp);
            pages.add(capInp);
            if (ActivityNeed.equalsIgnoreCase("0")) {
                viewPagerAdapter.add(new ActivityFragment(), capActivity);
                pages.add(capActivity);
            }
            viewPagerAdapter.add(new JWOthersFragment(), DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));
            pages.add(DCRCallActivity.this.getString(R.string.jfw)+"/"+getString(R.string.others));

            viewPagerAdapter.add(signatureFragment1, DCRCallActivity.this.getString(R.string.signature));
            pages.add(DCRCallActivity.this.getString(R.string.signature));
        }

        dcrCallBinding.viewPager.setAdapter(viewPagerAdapter);
        dcrCallBinding.viewPager.setOffscreenPageLimit(4);
        dcrCallBinding.tabLayout.setupWithViewPager(dcrCallBinding.viewPager);
        dcrCallBinding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());
    }

    private void prepareCheckInOutJsonObject(JSONObject jsonObject) {
        try {
            jsonObject.put("OutDateTime", CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_1));
            jsonObject.put("OutLatitude", String.valueOf(lat));
            jsonObject.put("OutLongitude", String.valueOf(lng));
            jsonObject.put("OutAddress", String.valueOf(address));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogCheckOut() {
        dialogCheckOut = new Dialog(this);
        dialogCheckOut.setContentView(R.layout.dialog_cus_checkout);
        dialogCheckOut.setCancelable(false);
        if (dialogCheckOut.getWindow() != null) {
            dialogCheckOut.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (UtilityClass.isNetworkAvailable(this)) {
            gpsTrack = new GPSTrack(this);
            lat = gpsTrack.getLatitude();
            lng = gpsTrack.getLongitude();
            address = CommonUtilsMethods.gettingAddress(this, lat, lng, false);
        } else {
            gpsTrack = new GPSTrack(this);
            lat = gpsTrack.getLatitude();
            lng = gpsTrack.getLongitude();
            address = getString(R.string.no_address_found);
        }

        if (checkInOutJsonObject != null) {
            tv_address_in = dialogCheckOut.findViewById(R.id.txt_address_in);
            tv_dateTime_in = dialogCheckOut.findViewById(R.id.txt_date_time_in);
            tvLatLong_in = dialogCheckOut.findViewById(R.id.txt_lat_lng_in);
            try {
                tv_address_in.setText(checkInOutJsonObject.optString("InAddress"));
                tv_dateTime_in.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_16, checkInOutJsonObject.optString("InDateTime")));
                tvLatLong_in.setText(String.format(Locale.getDefault(), "%s , %s", checkInOutJsonObject.optString("InLatitude"), checkInOutJsonObject.optString("InLongitude")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnCheckOut = dialogCheckOut.findViewById(R.id.btn_checkOut);
        tv_address = dialogCheckOut.findViewById(R.id.txt_address);
        tv_dateTime = dialogCheckOut.findViewById(R.id.txt_date_time);
        tvLatLong = dialogCheckOut.findViewById(R.id.txt_lat_lng);
        imgClose = dialogCheckOut.findViewById(R.id.img_close);
        progressBar = dialogCheckOut.findViewById(R.id.progress_bar);
        refreshLocation = dialogCheckOut.findViewById(R.id.rl_refresh_location);
        progressBar.setVisibility(View.GONE);

        tv_address.setText(address);
        tv_dateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
        startClock();
        tvLatLong.setText(String.format(Locale.getDefault(), "%f , %f", lat, lng));

        dialogCheckOut.show();

        refreshLocation.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                try {
                    btnCheckOut.setEnabled(false);
                    stopClock();
                    startClock();
                    progressBar.setVisibility(View.VISIBLE);
                    gpsTrack = new GPSTrack(DCRCallActivity.this);
                    gpsTrack.setLocationChangeListener(location -> {
                        try {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            if (UtilityClass.isNetworkAvailable(DCRCallActivity.this)) {
                                address = CommonUtilsMethods.gettingAddress(DCRCallActivity.this, lat, lng, false);
                            } else {
                                address = getString(R.string.no_address_found);
                            }

                            tvLatLong.setText(String.format(Locale.getDefault(), "%f , %f", lat, lng));
                            tv_address.setText(address);
                            progressBar.setVisibility(View.GONE);
                            btnCheckOut.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                stopClock();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                dialogCheckOut.dismiss();
            }
        });

        btnCheckOut.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                stopClock();
                prepareCheckInOutJsonObject(checkInOutJsonObject);
                commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.checked_out_successfully));
//            onSubmitClicked();
                callSubmit();
                dialogCheckOut.dismiss();
            }
        });
    }

    private void startClock() {
        handler1 = new Handler();
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.getDefault());
        runnable1 = new Runnable() {
            @Override
            public void run() {
                if (tv_dateTime != null && dialogCheckOut != null && dialogCheckOut.isShowing()) {
                    Calendar calendar = Calendar.getInstance();
                    String currentTime = timeFormat.format(calendar.getTime());
                    tv_dateTime.setText(currentTime);
                    handler1.postDelayed(this, 1000);
                    limit++;
                    if (limit == 120) {
                        stopClock();
                        handleIdleTime();
                        dialogCheckOut.dismiss();
                    }
                } else {
                    stopClock();
                }
            }
        };
        handler1.post(runnable1);
    }

    private void stopClock() {
        if (gpsTrack != null) {
            gpsTrack.setLocationChangeListener(null);
        }
        if (handler1 != null && runnable1 != null) {
            handler1.removeCallbacks(runnable1);
            handler1 = null;
            runnable1 = null;
        }
    }

    private void handleIdleTime() {
        Dialog dialog = new Dialog(DCRCallActivity.this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
        TextView content = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        btn_no.setVisibility(View.GONE);
        btn_yes.setText(getResources().getString(R.string.ok));
        content.setText(getString(R.string.you_have_been_idle_for_2_minutes_kindly_re_check_out));

        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });

        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void InsertVisitControl() {

        try {
            String mnt = String.valueOf(HomeDashBoard.selectedDate.getMonth().getValue());
            String month = HomeDashBoard.selectedDate.getMonth().name(), year = String.valueOf(HomeDashBoard.selectedDate.getYear());
            Log.e("TAG", "InsertVisitControl: " + mnt + " -< " + month + " -< " + year);
            JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CustCode", CallActivityCustDetails.get(0).getCode());
            jsonObject.put("CustType", CallActivityCustDetails.get(0).getType());
            jsonObject.put("Dcr_dt", HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
            jsonObject.put("month_name", month);
            jsonObject.put("Mnth", mnt);
            jsonObject.put("Yr", year);
            jsonObject.put("vtm", CommonUtilsMethods.getCurrentInstance("hh:mm aa"));
            jsonObject.put("CustName", CallActivityCustDetails.get(0).getName());
            jsonObject.put("town_code", CallActivityCustDetails.get(0).getTown_code());
            jsonObject.put("town_name", CallActivityCustDetails.get(0).getTown_name());
            jsonObject.put("Dcr_flag", "");
            jsonObject.put("SF_Code", SfCode);
            jsonObject.put("Trans_SlNo", "");
            jsonObject.put("FW_Indicator", FwFlag);
            jsonObject.put("AMSLNo", "");
            jsonObject.put("WorkType_Name", FeildName);
            jsonObject.put("day_status", "0");
            jsonObject.put("versionNo", getResources().getString(R.string.app_version));
            jsonObject.put("mod", Constants.APP_MODE);
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("AppName", getString(R.string.str_app_name));
            jsonObject.put("language", SharedPref.getSelectedLanguage(this));
            jsonArray.put(jsonObject);
            Log.d("VC", "InsertVisitControl: " + jsonObject);
            MasterDataTable inputdata = new MasterDataTable();
            inputdata.setMasterKey(Constants.CALL_SYNC);
            inputdata.setMasterValues(jsonArray.toString());
            inputdata.setSyncStatus(0);
            MasterDataTable nChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
            if (nChecked != null) {
                masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
            } else {
                masterDataDao.insert(inputdata);
            }

            if (!AdditionalCusListAdapter.saveAdditionalCallArrayList.isEmpty()) {
                for (int i = 0; i < AdditionalCusListAdapter.saveAdditionalCallArrayList.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("CustCode", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getCode());
                    jsonObject.put("CustType", "1");
                    jsonObject.put("Dcr_dt", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                    jsonObject.put("month_name", month);
                    jsonObject.put("Mnth", mnt);
                    jsonObject.put("Yr", year);
                    jsonObject.put("vtm", CommonUtilsMethods.getCurrentInstance("hh:mm aa"));
                    jsonObject.put("CustName", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getName());
                    jsonObject.put("town_code", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getTown_code());
                    jsonObject.put("town_name", AdditionalCusListAdapter.saveAdditionalCallArrayList.get(i).getTown_name());
                    jsonObject.put("Dcr_flag", "");
                    jsonObject.put("SF_Code", SfCode);
                    jsonObject.put("Trans_SlNo", "");
                    jsonObject.put("FW_Indicator", FwFlag);
                    jsonObject.put("WorkType_Name", FeildName);
                    jsonObject.put("day_status", "0");
                    jsonObject.put("AMSLNo", "");
                    jsonArray.put(jsonObject);

                    MasterDataTable mData = new MasterDataTable();
                    mData.setMasterKey(Constants.CALL_SYNC);
                    mData.setMasterValues(jsonArray.toString());
                    mData.setSyncStatus(0);
                    MasterDataTable Checked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                    if (Checked != null) {
                        masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
                    } else {
                        masterDataDao.insert(mData);
                    }
                }
            }

            CallDataRestClass.resetcallValues(DCRCallActivity.this);

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public boolean funStringValidation(String val) {
        return !TextUtils.isEmpty(val);
    }

    private void CallUploadImage() {
        if (callCaptureImageLists.size() > 0) {
            ApiInterface apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getTagApiImageUrl(getApplicationContext()));
            for (int i = 0; i < callCaptureImageLists.size(); i++) {
                if (callCaptureImageLists.get(i).isNewlyAdded()) {
                    Log.v("ImgUpload", callCaptureImageLists.get(i).getFilePath());
                    MultipartBody.Part img = convertImg("EventImg", callCaptureImageLists.get(i).getFilePath());
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
                                    DeleteFileCache(callCaptureImageLists.get(finalI).getFilePath());
                                } catch (Exception ignored) {
                                    DeleteFileCache(callCaptureImageLists.get(finalI).getFilePath());
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                            callOfflineECDataDao.saveOfflineEC(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), callCaptureImageLists.get(finalI1).getSystemImgName(), callCaptureImageLists.get(finalI1).getFilePath(), jsonImage.toString(), Constants.WAITING_FOR_SYNC, 1);
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
//                System.out.println("file Deleted :" + filePath);
            } else {
//                System.out.println("file not Deleted :" + filePath);
            }
        }
    }

    public boolean checkRequiredFunctions() {
        if (ActivityNeed.equalsIgnoreCase("0")) {
            for (String slNo : ActivityFragment.activityAnswerData.keySet()) {
                LinkedHashMap<String, ActivityDetailsModelClass> activityDetailsModelClassMap = ActivityFragment.activityAnswerData.get(slNo);
                if (!ActivityFragment.savedActivityList.contains(slNo) && activityDetailsModelClassMap != null) {
                    for (ActivityDetailsModelClass activityDetailsModelClass : activityDetailsModelClassMap.values()) {
                        if (!activityDetailsModelClass.getAnswerTxt().isEmpty() && (!activityDetailsModelClass.getControlId().equalsIgnoreCase("0") && !activityDetailsModelClass.getControlId().equalsIgnoreCase("17"))) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s", getString(R.string.save).trim(), capActivity));
                            moveToPage(capActivity);
                            return false;
                        }
                    }
                }
            }
        }
        switch (CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (ProductNeed.equalsIgnoreCase("0")) {
                    if (PrdMandatory.equalsIgnoreCase("1")) {
                        if (CheckProductListAdapter.saveCallProductListArrayList.isEmpty() && !CheckProductListAdapter.noProductSelected) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s", getString(R.string.select_the).trim(), capPrd));
                            moveToPage(capPrd);
                            return false;
                        }

                        if (PrdSamNeed.equalsIgnoreCase("1") && SamQtyMandatory.equalsIgnoreCase("1")) {
                            if (CheckProductListAdapter.saveCallProductListArrayList.isEmpty() && !CheckProductListAdapter.noProductSelected) {
                                commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s", getString(R.string.select_the).trim(), capPrd));
                                moveToPage(capPrd);
                                return false;
                            } else {
                                for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                    if (!CheckProductListAdapter.saveCallProductListArrayList.get(i).getCategory().equalsIgnoreCase("Sale")
                                            && (CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().isEmpty())) {
                                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s %s", getString(R.string.enter_the).trim(), CapSamQty, getString(R.string.value)));
                                        moveToPage(capPrd);
                                        return false;
                                    }
                                }
                            }
                        }

                        if (PrdRxNeed.equalsIgnoreCase("1") && RxQtyMandatory.equalsIgnoreCase("1")) {
                            if (CheckProductListAdapter.saveCallProductListArrayList.isEmpty() && !CheckProductListAdapter.noProductSelected) {
                                commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s", getString(R.string.select_the).trim(), capPrd));
                                moveToPage(capPrd);
                                return false;
                            } else {
                                for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                    if (!CheckProductListAdapter.saveCallProductListArrayList.get(i).getCategory().equalsIgnoreCase("Sample") && (CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().isEmpty())) {
                                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s %s", getString(R.string.enter_the).trim(), CapRxQty, getString(R.string.value)));
                                        moveToPage(capPrd);
                                        return false;
                                    }
                                }
                            }
                        }
                    } else {
                        if (PrdSamNeed.equalsIgnoreCase("1") && SamQtyMandatory.equalsIgnoreCase("1")) {
//                            if(CheckProductListAdapter.saveCallProductListArrayList.isEmpty() && !CheckProductListAdapter.noProductSelected) {
//                                commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s", getString(R.string.select_the).trim(), capPrd));
//                                moveToPage(capPrd);
//                                return false;
//                            }else {
                            for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (!CheckProductListAdapter.saveCallProductListArrayList.get(i).getCategory().equalsIgnoreCase("Sale")
                                        && (CheckProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().isEmpty())) {
                                    commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s %s", getString(R.string.enter_the).trim(), CapSamQty, getString(R.string.value)));
                                    moveToPage(capPrd);
                                    return false;
                                }
                            }
//                            }
                        }

                        if (PrdRxNeed.equalsIgnoreCase("1") && RxQtyMandatory.equalsIgnoreCase("1")) {
//                            if(CheckProductListAdapter.saveCallProductListArrayList.isEmpty() && !CheckProductListAdapter.noProductSelected) {
//                                commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s", getString(R.string.select_the).trim(), capPrd));
//                                moveToPage(capPrd);
//                                return false;
//                            }else {
                            for (int i = 0; i < CheckProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (!CheckProductListAdapter.saveCallProductListArrayList.get(i).getCategory().equalsIgnoreCase("Sample") && (CheckProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().isEmpty())) {
                                    commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s %s", getString(R.string.enter_the).trim(), CapRxQty, getString(R.string.value)));
                                    moveToPage(capPrd);
                                    return false;
                                }
                            }
//                            }
                        }
                    }
                }

                if (InputNeed.equalsIgnoreCase("0")) {
                    if (InpMandatory.equalsIgnoreCase("1")) {
                        if (CheckInputListAdapter.saveCallInputListArrayList.isEmpty() && !CheckInputListAdapter.noInputSelected) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s", getString(R.string.select_the).trim(), capInp));
                            moveToPage(capInp);
                            return false;
                        }
                    }
                    for (int i = 0; i < CheckInputListAdapter.saveCallInputListArrayList.size(); i++) {
                        if (CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty().isEmpty()) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s %s", getString(R.string.enter_the).trim(), "Qty", getString(R.string.value)));
                            moveToPage(capInp);
                            return false;
                        }
                    }
                }
                if (!validateInput()) return false;

                if (SfType.equalsIgnoreCase("1")) {
                    if (RCPANeed.equalsIgnoreCase("0") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (!validateRCPA()) return false;
                    }
                } else {
                    if (RCPANeed.equalsIgnoreCase("0") && MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (!validateRCPA()) return false;
                    }
                }
                if (RCPANeed.equalsIgnoreCase("0")) {
                    if (!validateRCPACompQty()) return false;
                }

                if (ActivityNeed.equalsIgnoreCase("0") && ActivityMandatory.equalsIgnoreCase("0")) {
                    if (ActivityFragment.savedActivityList != null && ActivityFragment.savedActivityList.isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.activity_mandatory));
                        moveToPage(capActivity);
                        return false;
                    }
                }

                if (PobNeed.equalsIgnoreCase("0") && PobMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(jwOthersBinding.edPob.getText()).toString().isEmpty() || jwOthersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_pob_values));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }
                if (SharedPref.getDfNeed(this).equals("0")) {
                    if (FeedbackMandatory.equalsIgnoreCase("1")) {
                        if (jwOthersBinding.tvFeedback.getText().toString().isEmpty()) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_feedback));
                            moveToPage("JFW/Others");
                            return false;
                        }
                    }
                }

                if (RemarkMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(jwOthersBinding.edRemarks.getText()).toString().isEmpty() || jwOthersBinding.edRemarks.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_remark));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }

                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }

                if (JwNeed.equalsIgnoreCase("0") && JwMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callAddedJointList.isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.jointwork_need));
                        moveToPage("JFW/Others");
                        return false;
                    }
//                    if (SignNeed.equalsIgnoreCase("0")){
//                        if(SignatureFragment1.callSignCaptureImageLists.isEmpty()){
//                            commonUtilsMethods.showToastMessage(DCRCallActivity.this,getString(R.string.signneed));
//                            moveToPage("Signature");
//                            return false;
//                        }

//                    }
                }
                break;
            case "2":
                if (SfType.equalsIgnoreCase("1")) {
                    if (RCPANeed.equalsIgnoreCase("0") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (!validateRCPA()) return false;
                    }
                } else {
                    if (RCPANeed.equalsIgnoreCase("0") && MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (!validateRCPA()) return false;
                    }
                }
                if (RCPANeed.equalsIgnoreCase("0")) {
                    if (!validateRCPACompQty()) return false;
                }

                if (PobNeed.equalsIgnoreCase("0") && PobMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(jwOthersBinding.edPob.getText()).toString().isEmpty() || jwOthersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_pob_values));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }

                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }

                if (JwNeed.equalsIgnoreCase("0") && JwMandatory.equalsIgnoreCase("0")) {
                    if (SharedPref.getChmJointworkNeed(this).equals("0")) {
                        if (JWOthersFragment.callAddedJointList.isEmpty()) {
                            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.jointwork_need));
                            moveToPage("JFW/Others");
                            return false;
                        }
                    }
                }
//                if (SignNeed.equalsIgnoreCase("0")){
//                    if(SignatureFragment1.callSignCaptureImageLists.isEmpty()){
//                            commonUtilsMethods.showToastMessage(DCRCallActivity.this,getString(R.string.signneed));
//                        moveToPage("Signature");
//                        return false;
//                    }

//                }

//              - Has been moved to validateRCPA()
//                for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
//                    ArrayList<String> dummyChk = new ArrayList<>();
//                    for (int j = 0; j < RCPASelectCompSide.rcpa_comp_list.size(); j++) {
//                        if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getPrd_code())) {
//                            dummyChk.add(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code());
//                        }
//                    }
//
//                    if (dummyChk.size() == 0) {
//                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.need_competitors_for_prd));
//                        moveToPage("RCPA");
//                        return false;
//                    }
//                }
//
//                for (int i = 0; i < RCPASelectCompSide.rcpa_comp_list.size(); i++) {
//                    if (RCPASelectCompSide.rcpa_comp_list.get(i).getQty().equalsIgnoreCase("0") || RCPASelectCompSide.rcpa_comp_list.get(i).getQty().isEmpty()) {
//                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.rcpa_need_qty));
//                        moveToPage("RCPA");
//                        return false;
//                    }
//                }

                break;
            case "3":
            case "4":
                if (PobNeed.equalsIgnoreCase("0") && PobMandatory.equalsIgnoreCase("0")) {
                    if (Objects.requireNonNull(jwOthersBinding.edPob.getText()).toString().isEmpty() || jwOthersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_pob_values));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }
                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }
                if (JwNeed.equalsIgnoreCase("0") && JwMandatory.equalsIgnoreCase("0")) {
                    if (JWOthersFragment.callAddedJointList.isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.jointwork_need));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }
                break;
            case "5":
                if (EventCaptureNeed.equalsIgnoreCase("0") && EventCapMandatory.equalsIgnoreCase("0")) {
                    if (callCaptureImageLists.isEmpty()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.event_capture_needed));
                        moveToPage("JFW/Others");
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    private boolean validateRCPACompQty() {
        for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
            ArrayList<String> dummyChk = new ArrayList<>();
            for (int j = 0; j < RCPASelectCompSide.rcpa_comp_list.size(); j++) {
                if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getPrd_code())) {
                    dummyChk.add(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code());
                }
            }
            if (dummyChk.isEmpty()) {
                commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.need_competitors_for_prd));
                moveToPage("RCPA");
                return false;
            }
        }
        for (int i = 0; i < RCPASelectCompSide.rcpa_comp_list.size(); i++) {
//            if (RCPASelectCompSide.rcpa_comp_list.get(i).getQty().isEmpty() || Integer.parseInt(RCPASelectCompSide.rcpa_comp_list.get(i).getQty()) == 0) {
            if (RCPASelectCompSide.rcpa_comp_list.get(i).getQty().isEmpty()) {
                commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.rcpa_need_qty));
                moveToPage("RCPA");
                return false;
            }
        }
        return true;
    }

    private void moveToPage(String pageName) {
        int currentPageIndex = dcrCallBinding.viewPager.getCurrentItem();
        int requiredPageIndex = pages.indexOf(pageName);
        if (currentPageIndex != requiredPageIndex) {
            viewPagerAdapter.getItem(requiredPageIndex);
            dcrCallBinding.viewPager.setCurrentItem(requiredPageIndex, true);
        }
    }

    private boolean validateInput() {
        for (int i = 0; i < CheckInputListAdapter.saveCallInputListArrayList.size(); i++) {
            if (CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty().isEmpty() || CheckInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty().equalsIgnoreCase("0")) {
                commonUtilsMethods.showToastMessage(DCRCallActivity.this, String.format("%s %s %s", getString(R.string.enter_the).trim(), "Input Qty", getString(R.string.value)));
                moveToPage(capInp);
                return false;
            }
        }
        return true;
    }

    private boolean validateRCPA() {
        if (RCPAFragment.ProductSelectedList.isEmpty()) {
            commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.add_rcpa_values));
            moveToPage("RCPA");
            return false;
        }
        for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
            ArrayList<String> dummyChk = new ArrayList<>();
            for (int j = 0; j < RCPASelectCompSide.rcpa_comp_list.size(); j++) {
                if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code()) && RCPAFragment.ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(RCPASelectCompSide.rcpa_comp_list.get(j).getPrd_code())) {
                    dummyChk.add(RCPASelectCompSide.rcpa_comp_list.get(j).getChem_Code());
                }
            }
            if (dummyChk.isEmpty()) {
                commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.need_competitors_for_prd));
                moveToPage("RCPA");
                return false;
            }
        }
        for (int i = 0; i < RCPASelectCompSide.rcpa_comp_list.size(); i++) {
            if (RCPASelectCompSide.rcpa_comp_list.get(i).getQty().isEmpty()) {
                commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.rcpa_need_qty));
                moveToPage("RCPA");
                return false;
            }
        }
        return true;
    }

    public boolean checkCurrentLoc() {
        boolean val = false;
        if (GeoChk.equalsIgnoreCase("0")) {
            gpsTrack = new GPSTrack(DCRCallActivity.this);
            try {
                if (!CommonUtilsMethods.isLocationEnabled(getApplicationContext())) {
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
        Call<JsonElement> callSaveDcr = api_interface.getJSONElement(SharedPref.getCallApiUrl(DCRCallActivity.this), mapString, jsonSaveDcr);
        callSaveDcr.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.v("callSaveApi", "---" + response);
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

                        outboxUtil.deleteOfflineCalls(CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                        progressDialog.dismiss();
                        if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
//                            dialogCheckOut.show();
                        } else {
                            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, getString(R.string.call_saved_something_wrong));
                        if (CusCheckInOutNeed.equalsIgnoreCase("0")) {
//                            dialogCheckOut.show();
                        } else {
                            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
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
//                    dialogCheckOut.show();
                } else {
                    Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                UpdateSampleStock();
                UpdateInputStock();
            }
        });
    }

    private void UpdateSampleStock() {
        try {

            JSONArray jsonArraySamStk = masterDataDao.getMasterDataTableOrNew(Constants.STOCK_BALANCE).getMasterSyncDataJsonArray();
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
            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STOCK_BALANCE, jsonArraySamStk.toString(), 2));
        } catch (Exception e) {
            Log.v("chkSamStk", "error---" + e);
        }
    }

    private void UpdateInputStock() {
        try {
            JSONArray jsonArrayInpStk = masterDataDao.getMasterDataTableOrNew(Constants.INPUT_BALANCE).getMasterSyncDataJsonArray();
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
            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.INPUT_BALANCE, jsonArrayInpStk.toString(), 2));
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
        ArrayList<String> prdRcpaQtyList = new ArrayList<>();
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

                        String timeDuration = "";
                        JSONArray jsonArray = jsSlidesPrds.getJSONArray("DigitalDet");
                        Log.v("DcrDetail_extract", "----DigitalSlides--len--" + jsonArray.length());
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonSlide = jsonArray.getJSONObject(j);

//                            if (!SlideName.equalsIgnoreCase(jsonSlide.getString("SlideName"))) {
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
                                String duration = TimeUtils.timeDurationHMS(StartTimeSlide.get(slide), EndTimeSlide.get(slide));
                                timeDuration = TimeUtils.addTime(timeDuration, duration);
                                remArray.put(remObj);
                            }
//                            }
                            arrayStore.add(new StoreImageTypeUrl("", "", SlideName, SlideType, "", "0", "", remArray.toString(), ProductName, ProductCode, "", false));
                        }
                        DetailedFragment.callDetailingLists.add(new CallDetailingList(ProductName, ProductCode, SlideName, SlideType, "", StartTime.trim() + " " + EndTime.trim(), StartTime.trim(), Integer.parseInt(Rating), PrdFeedBack, Date, timeDuration));
                    }
                }
            }

            JSONArray jsonPrdArray = new JSONArray(json.getString("DCRDetail"));
            if (jsonPrdArray.length() > 0) {
                JSONObject js = jsonPrdArray.getJSONObject(0);

                latEdit = js.getString("lati");
                lngEdit = js.getString("long");

                if (!isFromActivity.equalsIgnoreCase("new ") && GeoChk.equalsIgnoreCase("0") && (latEdit.isEmpty() || lngEdit.isEmpty())) {
                    gpsTrack = new GPSTrack(this);
                    latEdit = String.valueOf(gpsTrack.getLatitude());
                    lngEdit = String.valueOf(gpsTrack.getLongitude());
                }


                if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1") || CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
                    if (js.has("Product_Detail")) {
                        String time = js.getString("tm");
                        JSONArray jsonmain = new JSONArray(json.getString("DCRMain"));
                        JSONObject js1 = jsonmain.getJSONObject(0);
                        JSONObject activityDateObject = js1.getJSONObject("Activity_Date");
                        String activityDate = activityDateObject.getString("date").substring(0, 10);
                        VistTime = activityDate + " " + time;

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
                            String Rcpa_qty = extractValues(js.getString("Product_Detail"), "Rcpa");
                            getValues(Rcpa_qty, prdRcpaQtyList);
                        }
                    }
                } else {
                    JSONObject js1 = jsonPrdArray.getJSONObject(0);
                    JSONObject vstTime = js1.getJSONObject("vstTime");
                    VistTime = vstTime.getString("date");

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

                            String Rcpa_qty = extractValues(js.getString("Additional_Prod_Dtls"), "Rcpa");
                            getValues(Rcpa_qty, prdRcpaQtyList);
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
                        String[] separatedjointscode = js.getString("Worked_with_Code").split("\\$\\$");
                        for (int index = 0; index < separatedjoints.length; index++) {
                            JWOthersFragment.callAddedJointList.add(new CallCommonCheckedList(separatedjoints[index], separatedjointscode[index]));
                        }
                    }
                }

                Log.e("TAG1", "jsonExtractOnline: " + JWOthersFragment.editRemarks);
                //Remarks
                JWOthersFragment.editRemarks = "";
                JWOthersFragment.editPob = "";
                if (funStringValidation(js.getString("Activity_Remarks"))) {
                    JWOthersFragment.editRemarks = js.getString("Activity_Remarks");
                }
                Log.e("TAG2", "jsonExtractOnline: " + JWOthersFragment.editRemarks);

                //POB
                if (js.has("POB")) {
                    JWOthersFragment.editPob = js.getString("POB");
                }

                //FeedBack
                FeedbackSelectionSide.feedbackCode = "";
                FeedbackSelectionSide.feedbackName = "";
                if (js.has("Drcallfeedbackcode") && js.has("Call_Fdback")) {
                    FeedbackSelectionSide.feedbackCode = js.getString("Drcallfeedbackcode");
                    FeedbackSelectionSide.feedbackName = js.getString("Call_Fdback");
                    JWOthersFragment.editFeedback = js.getString("Call_Fdback");
                }
                Log.v("jsonExtractOnline", "product--size--" + prdNameList.size());
                Log.v("jsonExtractOnline", "product-sam-size--" + prdSamQtyList.size());
                Log.v("jsonExtractOnline", "product-rx-size--" + prdRxQtyList.size());
                Log.v("jsonExtractOnline", "product-rRCpa-size--" + prdRcpaQtyList.size());
                Log.v("jsonExtractOnline", "product-promoted--" + productPromoted);

                for (int m = 0; m < prdNameList.size(); m++) {
                    for (int j = 0; j < ProductFragment.checkedPrdList.size(); j++) {
                        CallCommonCheckedList PrdList = ProductFragment.checkedPrdList.get(j);
                        if (PrdList.getCode().equalsIgnoreCase(prdCodeList.get(m))) {
                            int lastStock = Integer.parseInt(PrdList.getStock_balance()) + Integer.parseInt(prdSamQtyList.get(m));
                            if (productPromoted.toString().contains(prdNameList.get(m))) {
                                CheckProductListAdapter.saveCallProductListArrayList.add(new SaveCallProductList(prdNameList.get(m), prdCodeList.get(m), PrdList.getCategory(), PrdList.getStock_balance(), String.valueOf(lastStock), prdSamQtyList.get(m), prdRxQtyList.get(m), prdRcpaQtyList.get(m), "0", true));
                            } else {
                                CheckProductListAdapter.saveCallProductListArrayList.add(new SaveCallProductList(prdNameList.get(m), prdCodeList.get(m), PrdList.getCategory(), PrdList.getStock_balance(), String.valueOf(lastStock), prdSamQtyList.get(m), prdRxQtyList.get(m), prdRcpaQtyList.get(m), "1", true));
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
            } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.syncing_calls) + getString(R.string.please_try_again));
                CallsFragment.syncCalls();
                IsFromDCR = true;
                HomeDashBoard.isDcrFrom = true;
                Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            //RCPA
            if (json.has("RCPAHead") && !json.getString("RCPAHead").equalsIgnoreCase("[]")) {
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
            Log.v("jsonExtractOnline", "----" + RCPAFragment.ProductSelectedList.size() + "----" + RCPASelectCompSide.rcpa_comp_list.size());

            if (json.has("event_capture") && !json.getString("event_capture").equalsIgnoreCase("[]")) {
                JSONArray jsonArrayEC = new JSONArray(json.getString("event_capture"));
                for (int i = 0; i < jsonArrayEC.length(); i++) {
                    JSONObject jsonEC = jsonArrayEC.getJSONObject(i);
                    callCaptureImageLists.add(new CallCaptureImageList(jsonEC.getString("title"), jsonEC.getString("remarks"), null, "", jsonEC.getString("imgurl"), false, false));
                    Log.d("jsonExtractOnline", "jsonExtractOnline: " + jsonEC);
                }
            }
            if (json.has("sign_Img") && !json.optString("sign_Img").isEmpty()) {
                imageName = json.optString("sign_Img");
            } else {
                imageName = "";
            }

            if (json.has("Dcr_activity") && !json.getString("Dcr_activity").equalsIgnoreCase("[]")) {
                JSONArray jsonArrayActivity = new JSONArray(json.getString("Dcr_activity"));
                ActivityFragment.activityAnswerData = new LinkedHashMap<>();
                ActivityFragment.savedActivityList = new HashSet<>();
                if (jsonArrayActivity.length() > 0) {
                    for (int i = 0; i < jsonArrayActivity.length(); i++) {
                        JSONObject jsonObject = jsonArrayActivity.optJSONObject(i);
                        String slNo = jsonObject.getString("Main_id");
                        String activityName = jsonObject.getString("Main_Name");
                        String sfCode = jsonObject.getString("Main_id");
                        String groupID = jsonObject.getString("Main_id");
                        String status = jsonObject.getString("Main_id");
                        JSONObject dateObj = jsonObject.optJSONObject("DCR_Date");
                        if (dateObj != null) {
                            String date = dateObj.optString("date");
                        }

                        ActivityFragment.activityAnswerData.put(slNo, new LinkedHashMap<>());
                        ActivityFragment.savedActivityList.add(slNo);

                        JSONArray jsonArrayActivityData = jsonObject.optJSONArray("Activity_data");
                        if (jsonArrayActivityData != null && jsonArrayActivityData.length() > 0) {
                            for (int j = 0; j < jsonArrayActivityData.length(); j++) {
                                JSONObject jsonObjectActivityData = jsonArrayActivityData.optJSONObject(j);
                                String SFCode = jsonObjectActivityData.optString("Sf_Code");
                                String controlID = jsonObjectActivityData.optString("Ctrl_id");
                                String creationID = jsonObjectActivityData.optString("Creation_id");
                                String creationCode = jsonObjectActivityData.optString("Creation_Code");
                                String creationName = jsonObjectActivityData.optString("Creation_Name");

                                String answer1 = "", answer2 = "";
                                if (controlID.equalsIgnoreCase("5") || controlID.equalsIgnoreCase("7") || controlID.equalsIgnoreCase("16")) {
                                    String[] answers = creationName.split(",");
                                    if (answers.length > 0) {
                                        answer1 = answers[0];
                                    }
                                    if (answers.length > 1) {
                                        answer2 = answers[1];
                                    }
                                } else if (controlID.equalsIgnoreCase("17")) {
                                    String[] answers = creationName.split("\\$", 2);
                                    if (answers.length > 0) {
                                        answer1 = answers[0];
                                    }
                                    if (answers.length > 1) {
                                        answer2 = answers[1];
                                    }
                                } else {
                                    answer1 = creationName;
                                }
                                ActivityDetailsModelClass activityDetailsModelClass = new ActivityDetailsModelClass(j, "", answer1, answer2, controlID, creationID, "", "", "", creationID, creationCode, slNo);
                                if (!ActivityFragment.activityAnswerData.containsKey(slNo)) {
                                    ActivityFragment.activityAnswerData.put(slNo, new LinkedHashMap<>());
                                    ActivityFragment.savedActivityList.add(slNo);
                                }
                                ActivityFragment.activityAnswerData.get(slNo).put(creationID, activityDetailsModelClass);
                            }
                        }

                    }
                }
                Log.d("jsonExtractOnline", "----- " + ActivityFragment.activityAnswerData);
            } else {
                if (ActivityNeed.equalsIgnoreCase("0") && ActivityMandatory.equalsIgnoreCase("0")) {
                    ActivityFragment.savedActivityList.add("-1");
                }
            }

            if (json.has("Dcr_checkin") && !json.getString("Dcr_checkin").equalsIgnoreCase("[]")) {
                JSONArray checkInOutJsonArray = json.optJSONArray("Dcr_checkin");
                JSONObject jsonObject = checkInOutJsonArray.optJSONObject(0);
                JSONObject checkInDateTimeObj = jsonObject.optJSONObject("Checkin_time"), checkOutDateTimeObj = jsonObject.optJSONObject("Checkout_time");
                String checkInDateTime = checkInDateTimeObj.optString("date"),
                        checkInLatitude = jsonObject.optString("Checkin_Lat"),
                        checkInLongitude = jsonObject.optString("Checkin_Long"),
                        checkInAddress = jsonObject.optString("Checkin_addrs"),
                        checkOutDateTime = checkOutDateTimeObj.optString("date"),
                        checkOutLatitude = jsonObject.optString("Checkout_Lat"),
                        checkOutLongitude = jsonObject.optString("Checkout_Long"),
                        checkOutAddress = jsonObject.optString("Checkout_addrs");

                checkInOutJsonObject = new JSONObject();

                checkInOutJsonObject.put("InDateTime", checkInDateTime);
                checkInOutJsonObject.put("InLatitude", checkInLatitude);
                checkInOutJsonObject.put("InLongitude", checkInLongitude);
                checkInOutJsonObject.put("InAddress", checkInAddress);
                checkInOutJsonObject.put("OutDateTime", checkOutDateTime);
                checkInOutJsonObject.put("OutLatitude", checkOutLatitude);
                checkInOutJsonObject.put("OutLongitude", checkOutLongitude);
                checkInOutJsonObject.put("OutAddress", checkOutAddress);
            }
        } catch (Exception e) {
            Log.v("jsonExtractOnline", "----" + e);
            e.printStackTrace();
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

            VistTime = json.getString("vstTime");
            activityDate = json.getString("ReqDt");
            int iEnd = json.getString("Entry_location").indexOf(":");
            if (iEnd != -1) {
                latEdit = json.getString("Entry_location").substring(0, iEnd);
            }

            lngEdit = json.getString("Entry_location").substring(json.getString("Entry_location").lastIndexOf(":") + 1);

            Log.v("jsonExtractLocal", "----" + latEdit + "---" + lngEdit);

            //Remarks
            JWOthersFragment.editRemarks = "";
            JWOthersFragment.editPob = "";
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

                    String timeDuration = "";
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
                            String duration = TimeUtils.timeDurationHMS(sTm, eTm);
                            timeDuration = TimeUtils.addTime(timeDuration, duration);
                            Log.v("rem_obj_print", remObj.toString());
                            remArray.put(remObj);
                        }
                        arrayStore.add(new StoreImageTypeUrl(SlideScr, "", SlideName, SlideType, SlidePath, "0", SlideRating, remArray.toString(), js.getString("Name"), js.getString("Code"), "", false));
                    }
                    Log.e("TAG", "jsonExtractLocal: " + timeDuration);
                    DetailedFragment.callDetailingLists.add(new CallDetailingList(js.getString("Name"), js.getString("Code"), SlideName, SlideType, SlidePath, STm.trim() + " " + ETm.trim(), STm.trim(), Integer.parseInt(js.getString("Rating")), js.getString("ProdFeedbk"), dt, timeDuration));
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
                        callCaptureImageLists.add(new CallCaptureImageList(jsEC.getString("EventImageTitle"), jsEC.getString("EventImageDescription"), null, jsEC.getString("EventFilePath"), jsEC.getString("EventImageName"), true));
                    }
                }
            }

            //Signature
            if (json.has("sign_Img") && !json.optString("sign_Img").isEmpty()) {
                imageName = json.optString("sign_Img");
            } else {
                imageName = "";
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

            //Activity
            if (json.has("ActivityDCR") && !json.optString("ActivityDCR").equalsIgnoreCase("[]")) {
                ActivityFragment.activityAnswerData = new LinkedHashMap<>();
                ActivityFragment.savedActivityList = new HashSet<>();
                JSONArray jsonArrayActivity = new JSONArray(json.optString("ActivityDCR"));
                for (int i = 0; i < jsonArrayActivity.length(); i++) {
                    JSONArray jsonArrayVal = jsonArrayActivity.optJSONArray(i);
                    if (jsonArrayVal == null) jsonArrayVal = new JSONArray();
                    for (int j = 0; j < jsonArrayVal.length(); j++) {
                        JSONObject jsonObject = jsonArrayVal.getJSONObject(j);
                        String activityDate = jsonObject.optString("act_date");
                        String dcrDate = jsonObject.optString("dcr_date");
                        String updateTime = jsonObject.optString("update_time");
                        String modTime = jsonObject.optString("ModTime");
                        String slNo = jsonObject.optString("slno");
                        String controlID = jsonObject.optString("ctrl_id");
                        String creationID = jsonObject.optString("creat_id");
                        String groupCreationID = jsonObject.optString("group_creat_id");
                        String wt = jsonObject.optString("WT");
                        String pl = jsonObject.optString("Pl");
                        String cusCode = jsonObject.optString("cus_code");
                        String latitude = jsonObject.optString("lat");
                        String longitude = jsonObject.optString("lng");
                        String cusName = jsonObject.optString("cusname");
                        String dataSF = jsonObject.optString("DataSF");
                        String type = jsonObject.optString("type");
                        String wtCode = jsonObject.optString("WT_code");
                        String wtName = jsonObject.optString("WTName");
                        String fwFlag = jsonObject.optString("FWFlg");
                        String TownCode = jsonObject.optString("town_code");
                        String TownName = jsonObject.optString("town_name");
                        String RSF = jsonObject.optString("Rsf");
                        String values = jsonObject.optString("values");
                        String codes = jsonObject.optString("codes");
                        String answer1 = "", answer2 = "";
                        if (controlID.equalsIgnoreCase("5") || controlID.equalsIgnoreCase("7") || controlID.equalsIgnoreCase("16")) {
                            String[] answers = values.split(",");
                            if (answers.length > 0) {
                                answer1 = answers[0];
                            }
                            if (answers.length > 1) {
                                answer2 = answers[1];
                            }
                        } else if (controlID.equalsIgnoreCase("17")) {
                            String[] answers = values.split("\\$");
                            if (answers.length > 0) {
                                answer1 = answers[0];
                            }
                            if (answers.length > 1) {
                                answer2 = answers[1];
                            }
                        } else {
                            answer1 = values;
                        }
                        ActivityDetailsModelClass activityDetailsModelClass = new ActivityDetailsModelClass(j, "", answer1, answer2, controlID, creationID, "", "", "", groupCreationID, codes, slNo);
                        if (!ActivityFragment.activityAnswerData.containsKey(slNo)) {
                            ActivityFragment.activityAnswerData.put(slNo, new LinkedHashMap<>());
                            ActivityFragment.savedActivityList.add(slNo);
                        }
                        ActivityFragment.activityAnswerData.get(slNo).put(creationID, activityDetailsModelClass);
                    }
                }
                Log.d("activity", "jsonExtractLocal: " + ActivityFragment.activityAnswerData.keySet().toString());
            } else {
                if (ActivityNeed.equalsIgnoreCase("0") && ActivityMandatory.equalsIgnoreCase("0")) {
                    ActivityFragment.savedActivityList.add("-1");
                }
            }

            if (json.has("CheckInOut") && !json.getString("CheckInOut").equalsIgnoreCase("[]")) {
                JSONArray checkInOutJsonArray = json.optJSONArray("CheckInOut");
                checkInOutJsonObject = checkInOutJsonArray.optJSONObject(0);
            }

        } catch (Exception e) {
            Log.v("jsonExtractLocal", "----" + e);
        }
    }

    public void remainder_calls() {
        try {
            String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
            api_interface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);

//            api_interface = RetrofitClient.getRetrofit(DCRCallActivity.this, SharedPref.getCallApiUrl(DCRCallActivity.this));
//            CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CommonUtilsMethods.getCurrentInstance("HH:mm:ss")
            CurrentDate = CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd");
            CurrentTime = CommonUtilsMethods.getCurrentInstance("HH:mm:ss");
            gpsTrack = new GPSTrack(this);
            lat = gpsTrack.getLatitude();
            lng = gpsTrack.getLongitude();
            String Doc_code = "", Doc_name = "", Pro_name = "", Pro_code = "", Join_wrk = "", Join_code = "";


            JSONArray jn = new JSONArray();
            JSONObject jnobj = new JSONObject();
            JSONObject jsonobjlist = CommonUtilsMethods.CommonObjectParameter(DCRCallActivity.this);

            jsonobjlist.put("Doctor_ID", Doc_code);
            jsonobjlist.put("Doctor_Name", Doc_name);

            if (JWOthersFragment.callAddedJointList.size() != 0) {
                for (int i = 0; i < JWOthersFragment.callAddedJointList.size(); i++) {

                    Join_wrk = Join_wrk + JWOthersFragment.callAddedJointList.get(i).getName() + ",";
                    Join_code = Join_code + JWOthersFragment.callAddedJointList.get(i).getCode() + ",";
                    jsonobjlist.put("WWith", Join_code);
                    jsonobjlist.put("WWithNm", Join_wrk);
                }
            } else {
                jsonobjlist.put("WWith", "");
                jsonobjlist.put("WWithNm", "");
            }


            if (CheckProductListAdapter.saveCallProductListArrayList.size() != 0) {
                for (int vv = 0; vv < CheckProductListAdapter.saveCallProductListArrayList.size(); vv++) {
                    String rxcount = "";

                    rxcount = CheckProductListAdapter.saveCallProductListArrayList.get(vv).getRx_qty();
                    if (rxcount.equals("") || rxcount.isEmpty() || rxcount.equals(null)) {
                        rxcount = "0";
                    }
                    Pro_code = Pro_code + CheckProductListAdapter.saveCallProductListArrayList.get(vv).getCode() + "( " + rxcount + " )" + ",";
                    Pro_name = Pro_name + CheckProductListAdapter.saveCallProductListArrayList.get(vv).getName() + "( " + rxcount + " )" + ",";

                    jsonobjlist.put("Prods", Pro_code);
                    jsonobjlist.put("ProdsNm", Pro_name);

                }

            } else {
                jsonobjlist.put("Prods", "");
                jsonobjlist.put("ProdsNm", "");
            }

            jsonobjlist.put("Remarks", jwOthersBinding.edRemarks.getText());
            jsonobjlist.put("feedback_id", FeedbackSelectionSide.feedbackCode);
            jsonobjlist.put("feedback_value", FeedbackSelectionSide.feedbackName);
            jsonobjlist.put("location", lat + ":" + lng);
            jsonobjlist.put("geoaddress", CommonUtilsMethods.gettingAddress(this, lat, lng, false));

            String pobValue = Objects.requireNonNull(jwOthersBinding.edPob.getText()).toString();
            jsonobjlist.put("rcallpob", pobValue);//sf_emp_id,sfcode,vstTime
            jsonobjlist.put("sfcode", hqcode);

            // Get current date and time
            Date currentDate = new Date();

            // Format the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(currentDate);

            // Print current date and time
//            System.out.println(formattedDate);
            jsonobjlist.put("vstTime", (formattedDate));

            jnobj.put("tbRemdrCall", jsonobjlist);
            JSONObject jnob = new JSONObject(String.valueOf(jnobj));
            jn.put(jnob);

            Log.d("tbRemdrCall", String.valueOf(jn));


            Call<JsonElement> call = null;
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/remainder");
            call = api_interface.getJSONElement(SharedPref.getCallApiUrl(DCRCallActivity.this), mapString, jn.toString());

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        commonUtilsMethods.showToastMessage(DCRCallActivity.this, DCRCallActivity.this.getString(R.string.remaindercalls_added_successfully));
                        finish();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createJsonFileCall() {
        try {
            CurrentDate = CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd");
            CurrentTime = CommonUtilsMethods.getCurrentInstance("HH:mm:ss");
            if (UtilityClass.isNetworkAvailable(this)) {
                if (isFromActivity.equalsIgnoreCase("new")) {
                    gpsTrack = new GPSTrack(this);
                    lat = gpsTrack.getLatitude();
                    lng = gpsTrack.getLongitude();
                    address = CommonUtilsMethods.gettingAddress(this, lat, lng, false);
                } else {
                    address = CommonUtilsMethods.gettingAddress(this, Double.parseDouble(latEdit), Double.parseDouble(lngEdit), false);
                }
            } else {
                gpsTrack = new GPSTrack(this);
                lat = gpsTrack.getLatitude();
                lng = gpsTrack.getLongitude();
                address = getString(R.string.no_address_found);
            }

            Log.v("final_value_call", "---injonite---");
            JSONArray jsonArray = new JSONArray();
            jsonSaveDcr = CommonUtilsMethods.CommonObjectParameter(DCRCallActivity.this);

            if (CusCheckInOutNeed.equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
                jsonArray = new JSONArray();
                jsonArray.put(checkInOutJsonObject);
//                Log.v("final_value_call", "---checkin---"+ jsonArray);
//                jsonSaveDcr.put("CheckIn", jsonArray);

//                jsonArray = new JSONArray();
//                jsonArray.put(checkOutJsonObject);
                Log.v("final_value_call", "---check-in-out---" + jsonArray);
                jsonSaveDcr.put("CheckInOut", jsonArray);
            }

            jsonArray = new JSONArray();
            JWKCodeList.clear();
            //JointWork
            for (int i = 0; i < JWOthersFragment.callAddedJointList.size(); i++) {
                JSONObject json_joint = new JSONObject();
                json_joint.put("Code", JWOthersFragment.callAddedJointList.get(i).getCode());
                json_joint.put("Name", JWOthersFragment.callAddedJointList.get(i).getName());
                if (JWOthersFragment.callAddedJointList.get(i).getCode().equalsIgnoreCase(SharedPref.getSfCode(DCRCallActivity.this))) {
                    json_joint.put("Name", SharedPref.getSfName(DCRCallActivity.this));
                }
                JWKCodeList.add(JWOthersFragment.callAddedJointList.get(i).getCode());
                jsonArray.put(json_joint);
            }
            Log.v("final_value_call", "---inputzise---" + jsonArray);
            jsonSaveDcr.put("JointWork", jsonArray);
            Map<String, List<String>> jcMap = SharedPref.getJCMap(DCRCallActivity.this);
            if (jcMap == null) {
                jcMap = new HashMap<>();
            }
            jcMap.put(TodayPlanSfCode, JWKCodeList);
            SharedPref.setJWKCODE(DCRCallActivity.this, JWKCodeList, HomeDashBoard.selectedDate.toString());
            SharedPref.saveJCMap(DCRCallActivity.this, jcMap, HomeDashBoard.selectedDate.toString());

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
                json_product.put("Appver", getResources().getString(R.string.app_version));
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
                        if (isFromActivity.equalsIgnoreCase("edit_local")) {
                            json_date.put("eTm", js.getString("eT"));
                            json_date.put("sTm", js.getString("sT"));
                        }
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
                json_product.put("Appver", getResources().getString(R.string.app_version));
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

            //Activity
            if (ActivityNeed.equalsIgnoreCase("0")) {

                String wtCode = "", wtName = "", fwFlag = "";
                JSONArray jsonArrayWt = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
                for (int j = 0; j < jsonArrayWt.length(); j++) {
                    JSONObject workTypeData = jsonArrayWt.getJSONObject(j);
                    if (workTypeData.getString("FWFlg").equalsIgnoreCase("F")) {
                        wtCode = workTypeData.getString("Code");
                        wtName = workTypeData.getString("Name");
                        fwFlag = workTypeData.getString("FWFlg");
                    }
                }
                Date today = new Date();
                String dateTime = TimeUtils.GetCurrentTimeStamp(TimeUtils.FORMAT_1);
                String dateToStr = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_1, HomeDashBoard.binding.textDate.getText().toString());
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String dateToStr1 = format1.format(today) + " 00:00:00";

                if (ActivityFragment.activityAnswerData != null && !ActivityFragment.activityAnswerData.isEmpty()) {
                    for (String slNo : ActivityFragment.activityAnswerData.keySet()) {
                        if (ActivityFragment.savedActivityList.contains(slNo)) {
                            try {
                                JSONArray jsonArrayActivity = new JSONArray();
                                if (ActivityFragment.activityAnswerData.get(slNo) != null && !ActivityFragment.activityAnswerData.get(slNo).isEmpty()) {
                                    for (String creationID : ActivityFragment.activityAnswerData.get(slNo).keySet()) {
                                        ActivityDetailsModelClass List = ActivityFragment.activityAnswerData.get(slNo).get(creationID);
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                                        jsonObject.put("act_date", dateToStr);
                                        jsonObject.put("dcr_date", dateToStr1);
                                        jsonObject.put("update_time", dateTime);
                                        jsonObject.put("ModTime", "");
                                        jsonObject.put("slno", List.getSlno());
                                        jsonObject.put("ctrl_id", List.getControlId());
                                        jsonObject.put("creat_id", List.getCreationId());
                                        jsonObject.put("group_creat_id", List.getCreationId());
                                        jsonObject.put("WT", wtCode);
                                        jsonObject.put("Pl", "0"); // cluster code
                                        jsonObject.put("cus_code", CallActivityCustDetails.get(0).getCode());
                                        jsonObject.put("lat", gpsTrack.getLatitude());
                                        jsonObject.put("lng", gpsTrack.getLongitude());
                                        jsonObject.put("cusname", CallActivityCustDetails.get(0).getName());
                                        jsonObject.put("DataSF", SharedPref.getSfCode(this));
                                        jsonObject.put("type", CallActivityCustDetails.get(0).getType());
                                        jsonObject.put("WT_code", wtCode);
                                        jsonObject.put("WTName", wtName);
                                        jsonObject.put("FWFlg", fwFlag);
                                        jsonObject.put("town_code", CallActivityCustDetails.get(0).getTown_code());
                                        jsonObject.put("town_name", CallActivityCustDetails.get(0).getTown_name());
                                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
                                        jsonObject.put("sf_type", SharedPref.getSfType(this));
                                        jsonObject.put("Designation", SharedPref.getDesig(this));
                                        jsonObject.put("state_code", SharedPref.getStateCode(this));
                                        jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));

                                        if (List.getControlId().equalsIgnoreCase("5") || List.getControlId().equalsIgnoreCase("7") || List.getControlId().equalsIgnoreCase("16")) {
                                            if (List.getMandatory().equalsIgnoreCase("1") && (List.getAnswerTxt().equalsIgnoreCase(""))) {
                                                commonUtilsMethods.showToastMessage(this, getString(R.string.fill_the_from) + List.getFieldName());
                                                break;
                                            } else if (List.getMandatory().equalsIgnoreCase("1") && (List.getAnswerTxt2().equalsIgnoreCase(""))) {
                                                commonUtilsMethods.showToastMessage(this, getString(R.string.fill_the_to) + List.getFieldName());
                                                break;
                                            } else {
                                                jsonObject.put("values", List.getAnswerTxt() + "," + List.getAnswerTxt2());
                                                jsonObject.put("codes", List.getCodes());
                                                Log.v("codes", List.getCodes());
                                            }
                                        } else if (List.getControlId().equalsIgnoreCase("17")) {
                                            if (List.getMandatory().equalsIgnoreCase("1") && List.getAnswerTxt().equalsIgnoreCase("")) {
                                                commonUtilsMethods.showToastMessage(this, getString(R.string.choose_the) + List.getFieldName() + "");
                                                break;
                                            } else {
                                                jsonObject.put("values", List.getAnswerTxt() + "$" + List.getAnswerTxt2());
                                                jsonObject.put("codes", List.getCodes());
                                            }
                                        } else {
                                            if (List.getMandatory().equalsIgnoreCase("1") && List.getAnswerTxt().equalsIgnoreCase("")) {
                                                commonUtilsMethods.showToastMessage(this, getString(R.string.fill_the) + List.getFieldName() + "");
                                                break;
                                            } else {
                                                jsonObject.put("values", List.getAnswerTxt());
                                                jsonObject.put("codes", List.getCodes());
                                                Log.v("codes", List.getCodes());
                                            }
                                        }
                                        jsonArrayActivity.put(jsonObject);
                                    }
                                    JSONObject MainObject = new JSONObject();
                                    MainObject.put("val", jsonArrayActivity);
                                    Log.v("JsonObject  :", MainObject.toString());
                                    ActivityFragment.activityData.add(MainObject);
                                }
                            } catch (Exception a) {
                                a.printStackTrace();
                            }
                        }
                    }
                    Log.w("DCRCallActivity", "jsonExtractOnline: " + ActivityFragment.activityData);
                }

                jsonArray = new JSONArray();
                for (int i = 0; i < ActivityFragment.activityData.size(); i++) {
                    jsonArray.put(ActivityFragment.activityData.get(i).getJSONArray("val"));
                }
                jsonSaveDcr.put("ActivityDCR", jsonArray);
            }

            //Customer Details
            jsonSaveDcr.put("CateCode", CallActivityCustDetails.get(0).getCategoryCode());
            jsonSaveDcr.put("CusType", CallActivityCustDetails.get(0).getType());
            jsonSaveDcr.put("CustCode", CallActivityCustDetails.get(0).getCode());
            jsonSaveDcr.put("CustName", CallActivityCustDetails.get(0).getName());
            if (isFromActivity.equalsIgnoreCase("new")) {
                if (SharedPref.getGeoChk(this).equalsIgnoreCase("0") && (lat == 0.0 || lng == 0.0)) {
                    commonUtilsMethods.showToastMessage(this, getString(R.string.gathering_location_information_failed_please_try_again));
                    isCreateJsonSuccess = false;
                    return;
                } else {
                    jsonSaveDcr.put("Entry_location", lat + ":" + lng);
                }
            } else {
                jsonSaveDcr.put("Entry_location", latEdit + ":" + lngEdit);
            }
            jsonSaveDcr.put("address", address);
            jsonSaveDcr.put("sfcode", SfCode);
            jsonSaveDcr.put("Rsf", TodayPlanSfCode);
            jsonSaveDcr.put("division_code", DivCode);
            jsonSaveDcr.put("AppUserSF", TodayPlanSfCode);
            jsonSaveDcr.put("SpecCode", "2");
            jsonSaveDcr.put("mappedProds", "");
            jsonSaveDcr.put("mode", "0");
            jsonSaveDcr.put("Appver", getResources().getString(R.string.app_version));
            jsonSaveDcr.put("activitynd", ActivityNeed);

            JSONArray jsonArrayWt = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArrayWt.length(); i++) {
                JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
                if (workTypeData.getString("FWFlg").equalsIgnoreCase("F")) {
                    jsonSaveDcr.put("WT_code", workTypeData.getString("Code"));
                    jsonSaveDcr.put("WTName", workTypeData.getString("Name"));
                    jsonSaveDcr.put("FWFlg", workTypeData.getString("FWFlg"));
                    FwFlag = workTypeData.getString("FWFlg");
                    FeildName = workTypeData.getString("Name");
                }
            }

            jsonSaveDcr.put("town_code", CallActivityCustDetails.get(0).getTown_code());
            jsonSaveDcr.put("town_name", CallActivityCustDetails.get(0).getTown_name());
            if (isFromActivity.equalsIgnoreCase("edit_online")) {
                jsonSaveDcr.put("town_code", new JSONObject(CallActivityCustDetails.get(0).getJsonArray()).getJSONArray("DCRDetail").getJSONObject(0).getString("SDP"));
                String townName = new JSONObject(CallActivityCustDetails.get(0).getJsonArray()).getJSONArray("DCRDetail").getJSONObject(0).getString("SDP_Name");
                int index = townName.indexOf("(");
                if (index > 0) {
                    townName = townName.substring(0, index).trim();
                }
                jsonSaveDcr.put("town_name", townName);
            }
            jsonSaveDcr.put("ModTime", CurrentDate + " " + CurrentTime);
            jsonSaveDcr.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
            if (isFromActivity.equalsIgnoreCase("edit_local")) {
                jsonSaveDcr.put("ReqDt", activityDate);
            }
            jsonSaveDcr.put("day_flag", "0");

            if (isFromActivity.equalsIgnoreCase("new")) {
                jsonSaveDcr.put("vstTime", HomeDashBoard.selectedDate.toString() + " " + CurrentTime);
            } else {
                jsonSaveDcr.put("vstTime", VistTime);
            }
            jsonSaveDcr.put("Remarks", jwOthersBinding.edRemarks.getText());
            if (isFromActivity.equalsIgnoreCase("edit_online")) {
                jsonSaveDcr.put("amc", CallActivityCustDetails.get(0).getADetSlNo());
                jsonSaveDcr.put("headerno", CallActivityCustDetails.get(0).getTransNo());
                jsonSaveDcr.put("detno", CallActivityCustDetails.get(0).getADetSlNo());

            } else if (isFromActivity.equalsIgnoreCase("edit_local")) {
                jsonSaveDcr.put("amc", CallActivityCustDetails.get(0).getADetSlNo());
                jsonSaveDcr.put("headerno", CallActivityCustDetails.get(0).getTransNo());
                jsonSaveDcr.put("detno", CallActivityCustDetails.get(0).getADetSlNo());

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
            if (!callCaptureImageLists.isEmpty()) {
                jsonImage = CommonUtilsMethods.CommonObjectParameter(DCRCallActivity.this);
                try {
                    jsonImage.put("tableName", "uploadphoto");
                    jsonImage.put("sfcode", SfCode);
                    jsonImage.put("division_code", DivCode);
                    jsonImage.put("Rsf", TodayPlanSfCode);

                } catch (Exception ignored) {

                }

                jsonSaveDcr.put("filepath", "");

                for (int i = 0; i < callCaptureImageLists.size(); i++) {
                    JSONObject json_Eve_cap = new JSONObject();
                    json_Eve_cap.put("EventCapture", "True");
                    json_Eve_cap.put("EventImageName", callCaptureImageLists.get(i).getSystemImgName());
                    json_Eve_cap.put("EventImageTitle", callCaptureImageLists.get(i).getImg_name());
                    json_Eve_cap.put("EventImageDescription", callCaptureImageLists.get(i).getImg_description());
                    json_Eve_cap.put("EventFilePath", callCaptureImageLists.get(i).getFilePath());
                    jsonArray.put(json_Eve_cap);
                }
                jsonSaveDcr.put("EventCapture", jsonArray);
            }

            //Signature

            if (!callSignCaptureImage.isEmpty()) {
                Log.d("Json", "CreateJsonFileCall: " + "json is not null");
                jsonSign = CommonUtilsMethods.CommonObjectParameter(DCRCallActivity.this);
                try {
                    jsonSign.put("tableName", "uploadsign");
                    jsonSign.put("sfcode", SfCode);
                    jsonSign.put("division_code", DivCode);
                    jsonSign.put("Rsf", TodayPlanSfCode);
                    Log.d("TAG", "CreateJsonFileCall: " + jsonSign);

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                jsonSaveDcr.put("sign_path", callSignCaptureImage.get(0).getFilepath());
                jsonSaveDcr.put("sign_Img", callSignCaptureImage.get(0).getImg_Name());
//                jsonSaveDcr.put("sign_Img", callSignCaptureImage.get(0).getImg_Name());
                Log.d("TAG", "CreateJsonFileCall: " + jsonSaveDcr);


            } else {
                Log.d("json", "CreateJsonFileCall: " + "json is NULL");
            }
            //POB
            String pobValue = Objects.requireNonNull(jwOthersBinding.edPob.getText()).toString();
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
            e.printStackTrace();
            isCreateJsonSuccess = false;
        }
    }

    private void getRequiredData() {
        try {
            SfType = SharedPref.getSfType(this);
            SfCode = SharedPref.getSfCode(this);
            SfName = SharedPref.getSfName(this);
            DivCode = SharedPref.getDivisionCode(this);
            SubDivisionCode = SharedPref.getSubdivisionCode(this);
            Designation = SharedPref.getDesig(this);
            StateCode = SharedPref.getStateCode(this);
            RcpaCompetitorAdd = SharedPref.getRcpaCompetitorAdd(this);
            EventCapMandatory = SharedPref.getCipEventMd(this);
            capActivity = SharedPref.getActivityCap(this);
            ActivityNeed = SharedPref.getActivityNd(this);
            ActivityMandatory = SharedPref.getActivityMand(this);

            switch (CallActivityCustDetails.get(0).getType()) {
                case "1": //Dr
                    //Caption
                    capPrd = SharedPref.getDocProductCaption(this);
                    capInp = SharedPref.getDocInputCaption(this);
                    CapSamQty = SharedPref.getDrSmpQCap(this);
                    CapRxQty = SharedPref.getDrRxQCap(this);
                    if (!SharedPref.getDocPobCaption(this).isEmpty()) {
                        CapPob = SharedPref.getDocPobCaption(this);
                    } else {
                        CapPob = "Pob";
                    }

                    //Need
                    ProductNeed = SharedPref.getDpNeed(this);
                    InputNeed = SharedPref.getDiNeed(this);
                    RCPANeed = SharedPref.getRcpaNd(this);
                    PobNeed = SharedPref.getDocPobNeed(this);
                    OverallFeedbackNeed = SharedPref.getDfNeed(this);
                    EventCaptureNeed = SharedPref.getDeNeed(this);
                    JwNeed = SharedPref.getDocJointworkNeed(this);
                    PrdSamNeed = SharedPref.getDrSampNd(this);
                    PrdRxNeed = SharedPref.getDrRxNd(this);
                    PrdRcpaQtyNeed = SharedPref.getRcpaQtyNeed(this);
                    CusCheckInOutNeed = SharedPref.getCustSrtNd(this);
                    AdditionalCallNeed = SharedPref.getAdditionalCallNeed(this);

                    //Mandatory
                    PrdMandatory = SharedPref.getDrPrdMd(this);
                    InpMandatory = SharedPref.getDrInpMd(this);
                    SamQtyMandatory = SharedPref.getDrSmpQMd(this);
                    RxQtyMandatory = SharedPref.getDrRxQMd(this);
                    ;
                    RcpaMandatory = SharedPref.getRcpaMd(this);
                    MgrRcpaMandatory = SharedPref.getRcpaMdMgr(this);
                    EventCapMandatory = SharedPref.getDrEventMd(this);
                    PobMandatory = SharedPref.getDocPobMandatoryNeed(this);
                    FeedbackMandatory = SharedPref.getDrFeedMd(this);
                    JwMandatory = SharedPref.getDocJointworkMandatoryNeed(this);
                    RemarkMandatory = SharedPref.getTempNd(this);
                    break;
                case "2": //Chemist
                    //Caption
                    capPrd = SharedPref.getChmProductCaption(this);
                    capInp = SharedPref.getChmInputCaption(this);
                    ;
                    CapSamQty = SharedPref.getChmSmpCap(this);
                    CapRxQty = SharedPref.getChmQCap(this);
                    if (!SharedPref.getChmPobCaption(this).isEmpty()) {
                        CapPob = SharedPref.getChmPobCaption(this);
                    } else {
                        CapPob = "Pob";
                    }

                    //Need
                    ProductNeed = SharedPref.getCpNeed(this);
                    InputNeed = SharedPref.getCiNeed(this);
                    RCPANeed = SharedPref.getChmRcpaNeed(this);
                    PobNeed = SharedPref.getChmPobNeed(this);
                    EventCaptureNeed = SharedPref.getCeNeed(this);
                    OverallFeedbackNeed = SharedPref.getCfNeed(this);
                    JwNeed = SharedPref.getChmJointworkNeed(this);
                    PrdSamNeed = SharedPref.getChmsamqtyNeed(this);
                    PrdRxNeed = SharedPref.getChmRxNd(this); //1
                    PrdRcpaQtyNeed = "0"; //0
                    CusCheckInOutNeed = SharedPref.getChmSrtNd(this);

                    //Mandatory
                    //   RcpaMandatory = loginResponse.getRcpaMd(); //Check This one
                    PobMandatory = SharedPref.getChmPobMandatoryNeed(this);
                    EventCapMandatory = SharedPref.getChmEventMd(this);
                    JwMandatory = SharedPref.getChmJointworkMandatoryNeed(this);
                    RcpaMandatory = SharedPref.getChmRcpaMd(this);
                    MgrRcpaMandatory = SharedPref.getChmRcpaMdMgr(this);
                    break;
                case "3": //Stockiest
                    //Caption
                    capPrd = SharedPref.getStkProductCaption(this);
                    ;
                    capInp = SharedPref.getStkInputCaption(this);
                    CapSamQty = "Samples";
                    CapRxQty = SharedPref.getStkQCap(this);
                    if (!SharedPref.getStkPobCaption(this).isEmpty()) {
                        CapPob = SharedPref.getStkPobCaption(this);
                    } else {
                        CapPob = "Pob";
                    }

                    //Need
                    ProductNeed = SharedPref.getSpNeed(this);
                    InputNeed = SharedPref.getSiNeed(this);
//                    PobNeed = SharedPref.getStkPobNeed(this);
                    OverallFeedbackNeed = SharedPref.getSfNeed(this);
                    EventCaptureNeed = SharedPref.getSeNeed(this);
                    JwNeed = SharedPref.getStkJointworkNeed(this);
                    PrdSamNeed = "0"; //0
                    PrdRxNeed = SharedPref.getStkPobNeed(this);
                    PrdRcpaQtyNeed = "0"; //0
                    CusCheckInOutNeed = "1"; //1
                    PobNeed = SharedPref.getStockistPobNeed(this);

                    //Mandatory
                    PobMandatory = SharedPref.getStkPobMandatoryNeed(this);
                    EventCapMandatory = SharedPref.getStkEventMd(this);
                    JwMandatory = SharedPref.getStkJointworkMandatoryNeed(this);
                    break;
                case "4": //UNListed Dr
                    //Caption
                    ProductNeed = SharedPref.getNpNeed(this);
                    InputNeed = SharedPref.getNiNeed(this);
                    capPrd = SharedPref.getUlProductCaption(this);
                    ;
                    capInp = SharedPref.getUlInputCaption(this);
                    CapSamQty = SharedPref.getNlSmpQCap(this);
                    CapRxQty = SharedPref.getNlRxQCap(this);
                    if (!SharedPref.getUldocPobCaption(this).isEmpty()) {
                        CapPob = SharedPref.getUldocPobCaption(this);
                    } else {
                        CapPob = "Pob";
                    }
                    CusCheckInOutNeed = SharedPref.getUnlistSrtNd(this);
                    ;

                    //Need
//                    PobNeed = SharedPref.getUlPobNeed(this);
                    OverallFeedbackNeed = SharedPref.getNfNeed(this);
                    EventCaptureNeed = SharedPref.getNeNeed(this);
                    JwNeed = SharedPref.getUlJointworkNeed(this);
                    PrdSamNeed = "0"; //0
                    PrdRxNeed = SharedPref.getUlPobNeed(this);
                    PrdRcpaQtyNeed = "0"; //0
                    PobNeed = SharedPref.getUnlistedDoctorPobNeed(this);

                    //Mandatory
                    PobMandatory = SharedPref.getUlPobMandatoryNeed(this);
                    EventCapMandatory = SharedPref.getUldrEventMd(this);
                    JwMandatory = SharedPref.getUlJointworkMandatoryNeed(this);
                    break;
                case "5"://CIP
                    //Caption
                    if (!SharedPref.getCipPobCaption(this).isEmpty()) {
                        CapPob = SharedPref.getCipPobCaption(this);
                    } else {
                        CapPob = "Pob";
                    }

                    //Need
                    PobNeed = SharedPref.getCipPNeed(this);
                    OverallFeedbackNeed = SharedPref.getCipFNeed(this);
                    EventCaptureNeed = SharedPref.getCipEventMd(this);
                    JwNeed = SharedPref.getCipJointworkNeed(this);
                    PrdSamNeed = "0";
                    PrdRxNeed = "0";
                    PrdRcpaQtyNeed = SharedPref.getRcpaQtyNeed(this);
                    CusCheckInOutNeed = SharedPref.getCipSrtNd(this);

                    //Mandatory
                    EventCapMandatory = SharedPref.getCipEventMd(this);
                    PobMandatory = SharedPref.getCipPNeed(this);
                    break;
                case "6"://HOSPITAL
                    //Caption
                    if (!SharedPref.getHospPobCaption(this).isEmpty()) {
                        CapPob = SharedPref.getHospPobCaption(this);
                    } else {
                        CapPob = "Pob";
                    }
                    CusCheckInOutNeed = "1";

                    //Need
                    PobNeed = SharedPref.getHosPobNd(this);
                    OverallFeedbackNeed = SharedPref.getHfNeed(this);
                    EventCaptureNeed = SharedPref.getHospEventMd(this);
                    JwNeed = "0";
                    PrdSamNeed = "0";
                    PrdRxNeed = "0";
                    PrdRcpaQtyNeed = SharedPref.getRcpaQtyNeed(this);

                    //Mandatory
                    EventCapMandatory = SharedPref.getHospEventMd(this);
                    //   PobMandatory = loginResponse.getCIPPOBMd();
                    break;
            }

            SampleValidation = SharedPref.getSampleValidation(this);
            InputValidation = SharedPref.getInputValidation(this);
            GeoChk = SharedPref.getGeoChk(this);
            HosNeed = SharedPref.getHospNeed(this);

            if (SharedPref.getSampleValQty(this).equalsIgnoreCase("0")) {
                SamQtyRestriction = "1";
            } else {
                SamQtyRestriction = "0";
                SamQtyRestrictValue = SharedPref.getSampleValQty(this);
            }

            if (SharedPref.getInputValQty(this).equalsIgnoreCase("0")) {
                InpQtyRestriction = "1";
            } else {
                InpQtyRestriction = "0";
                InpQtyRestrictValue = SharedPref.getInputValQty(this);
            }

            assert isFromActivity != null;
            if (isFromActivity.equalsIgnoreCase("new")) {
                try {
                    if (SfType.equalsIgnoreCase("1")) {
                        TodayPlanSfCode = SfCode;
                    } else {
                        TodayPlanSfCode = SharedPref.getHqCode(this);
                        if (TodayPlanSfCode.isEmpty()) {
                            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
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

//            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CUSTOM_SETUP).getMasterSyncDataJsonArray();
//            JSONObject setupData = jsonArray.getJSONObject(0);
//            Type typeSetup = new TypeToken<CustomSetupResponse>() {
//            }.getType();
//            CustomSetupResponse customSetupResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
//            if(CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
//                AdditionalCallNeed = customSetupResponse.getAdditionalCall();
//            }else if(CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
//                PobNeed = customSetupResponse.getStockistPobNeed();
//            }else if(CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
//                PobNeed = customSetupResponse.getUndrPobNeed();
//            }

            if (CusCheckInOutNeed.equalsIgnoreCase("0")
                    && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))
                    && !isFromActivity.equalsIgnoreCase("edit_local")
                    && !isFromActivity.equalsIgnoreCase("edit_online")) {
                dcrCallBinding.btnFinalSubmit.setText(R.string.submit_check_out);
            } else {
                dcrCallBinding.btnFinalSubmit.setText(R.string.submit);
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private void AddJWData() {
        callCaptureImageLists = new ArrayList<>();
        JWOthersFragment.callAddedJointList = new ArrayList<>();
        JointWorkSelectionSide.JwList = new ArrayList<>();
    }

    private void AddSignData() {        // just added for reff
        callSignCaptureImage = new ArrayList<>();
    }

    private void AddRCPAData() {
        RCPAFragment.ChemistSelectedList = new ArrayList<>();
        RCPAFragment.ProductSelectedList = new ArrayList<>();
        RCPASelectCompSide.rcpa_comp_list = new ArrayList<>();
    }

    private void AddActivityData() {
        ActivityFragment.activityAnswerData = new LinkedHashMap<>();
        ActivityFragment.savedActivityList = new HashSet<>();
        ActivityFragment.activityData = new ArrayList<>();
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
//            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + TodayPlanSfCode).getMasterSyncDataJsonArray();
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + TodayPlanSfCode).getMasterSyncDataJsonArray();
            Log.v("length", jsonArray.length() + "---" + Constants.DOCTOR_MAS + TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), false, jsonObject.getString("Tlvst"), jsonObject.getString("MProd")));
            }

            int count = AdditionalCallFragment.custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (AdditionalCallFragment.custListArrayList.get(i).getCode().equalsIgnoreCase(AdditionalCallFragment.custListArrayList.get(j).getCode())) {
                        AdditionalCallFragment.custListArrayList.set(i, new CallCommonCheckedList(AdditionalCallFragment.custListArrayList.get(i).getName(), AdditionalCallFragment.custListArrayList.get(i).getCode(), AdditionalCallFragment.custListArrayList.get(i).getTown_name(), AdditionalCallFragment.custListArrayList.get(i).getTown_code(), AdditionalCallFragment.custListArrayList.get(i).isCheckedItem(), AdditionalCallFragment.custListArrayList.get(i).getTotalVisit(), AdditionalCallFragment.custListArrayList.get(i).getPriorityCodes()));
                        AdditionalCallFragment.custListArrayList.remove(j--);
                        count--;
                    } else {
                        AdditionalCallFragment.custListArrayList.set(i, new CallCommonCheckedList(AdditionalCallFragment.custListArrayList.get(i).getName(), AdditionalCallFragment.custListArrayList.get(i).getCode(), AdditionalCallFragment.custListArrayList.get(i).getTown_name(), AdditionalCallFragment.custListArrayList.get(i).getTown_code(), AdditionalCallFragment.custListArrayList.get(i).isCheckedItem(), AdditionalCallFragment.custListArrayList.get(i).getTotalVisit(), AdditionalCallFragment.custListArrayList.get(i).getPriorityCodes()));
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
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.INPUT).getMasterSyncDataJsonArray();
            JSONArray jsonArrayInpStk = masterDataDao.getMasterDataTableOrNew(Constants.INPUT_BALANCE).getMasterSyncDataJsonArray();
//            if(InpMandatory.equalsIgnoreCase("1") && isFromActivity.equalsIgnoreCase("new")) {
            InputFragment.checkedInputList.add(new CallCommonCheckedList("No Input", "-10", "", false));
//            } else {
//                InputFragment.checkedInputList.add(new CallCommonCheckedList("No Input", "-10", "", true));
//            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (!jsonObject.getString("Code").equalsIgnoreCase("-1")) {
                    JSONObject jsonFDate = new JSONObject(jsonObject.getString("EffF"));
                    JSONObject jsonTDate = new JSONObject(jsonObject.getString("EffT"));
                    Log.v("JsonInput", jsonFDate.getString("date").substring(0, 10) + "---" + jsonTDate.getString("date").substring(0, 10));

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    String todayData = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));

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

            if (InputFragment.checkedInputList.isEmpty()) {
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
                for (int i = 1; i < InputFragment.checkedInputList.size(); i++) {
                    for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                        JSONObject jsonObjectInput = jsonArrayInpStk.getJSONObject(j);
                        Log.v("chkInput", InputFragment.checkedInputList.get(i).getCode() + "---" + jsonObjectInput.getString("Code") + "--chk-----" + jsonObjectInput.getString("Code").equalsIgnoreCase(InputFragment.checkedInputList.get(i).getCode()));
                        if (jsonObjectInput.getString("Code").equalsIgnoreCase(InputFragment.checkedInputList.get(i).getCode())) {
                            InputFragment.checkedInputList.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), jsonObjectInput.getString("Balance_Stock"), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            AddCallSelectInpSide.callInputList.set(i-1, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), jsonObjectInput.getString("Balance_Stock"), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            StockInput.set(i-1, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getCode(), jsonObjectInput.getString("Balance_Stock"), jsonObjectInput.getString("Balance_Stock")));
                            break;
                        } else {
                            InputFragment.checkedInputList.set(i, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), InputFragment.checkedInputList.get(i).getStock_balance(), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            AddCallSelectInpSide.callInputList.set(i-1, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getName(), InputFragment.checkedInputList.get(i).getCode(), InputFragment.checkedInputList.get(i).getStock_balance(), InputFragment.checkedInputList.get(i).isCheckedItem()));
                            StockInput.set(i-1, new CallCommonCheckedList(InputFragment.checkedInputList.get(i).getCode(), InputFragment.checkedInputList.get(i).getStock_balance(), InputFragment.checkedInputList.get(i).getStock_balance()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.v("JsonInput", "error--" + e);
            e.printStackTrace();
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
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT).getMasterSyncDataJsonArray();
            JSONArray jsonArrayPrdStk = masterDataDao.getMasterDataTableOrNew(Constants.STOCK_BALANCE).getMasterSyncDataJsonArray();
            Log.v("chkSample", "---size--111----" + jsonArray.length() + "----" + jsonArrayPrdStk.length());
//            if(PrdMandatory.equalsIgnoreCase("1") && isFromActivity.equalsIgnoreCase("new")) {
            ProductFragment.checkedPrdList.add(new CallCommonCheckedList("No Product", "-10", "", false, "", "", ""));
//            } else {
//                ProductFragment.checkedPrdList.add(new CallCommonCheckedList("No Product", "-10", "", true, "", ""));
//            }
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                String priorityCode = "", prdCodes = CallActivityCustDetails.get(0).getMappedSlides();
//                ArrayList<String> priorityCodes = new ArrayList<>(Arrays.asList(prdCodes.split(",")));
//                if(priorityCodes.contains(jsonObject.optString("Code"))) {
//                    priorityCode = "P"+count;
//                    count++;
//                }
//                if (!jsonObject.getString("Code").equalsIgnoreCase("-1")) {
//                    if (!jsonObject.getString("Product_Mode").equalsIgnoreCase("Sample")) {
//                        RCPASelectPrdSide.PrdFullList.add(new SaveCallProductList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("DRate"), jsonObject.getString("Product_Mode"), priorityCode));
//                    }
//
//                    if (CallActivityCustDetails.get(0).getPriorityPrdCode().contains(jsonObject.getString("Code"))) {
//                        ProductFragment.checkedPrdList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, "P" + Priority_count++, jsonObject.getString("Product_Mode"), priorityCode));
//                        StockSample.add(new CallCommonCheckedList(jsonObject.getString("Code"), "0", "0"));
//                        if (!jsonObject.getString("Product_Mode").equalsIgnoreCase("Sale")) {
//                            AddCallSelectPrdSide.callSampleList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, "P" + Priority_count++, jsonObject.getString("Product_Mode"), ""));
//                        }
//                    } else {
//                        ProductFragment.checkedPrdList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, jsonObject.getString("Product_Mode"), jsonObject.getString("Product_Mode"), priorityCode));
//                        StockSample.add(new CallCommonCheckedList(jsonObject.getString("Code"), "0", "0"));
//                        if (!jsonObject.getString("Product_Mode").equalsIgnoreCase("Sale")) {
//                            AddCallSelectPrdSide.callSampleList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, jsonObject.getString("Product_Mode"), jsonObject.getString("Product_Mode"), ""));
//                        }
//                    }
//                }
//            }

            String prdCodes = CallActivityCustDetails.get(0).getMappedSlides();
            if (prdCodes == null) prdCodes = "";
            String[] priorityCodes = prdCodes.split(",");
            Map<String, String> codeToPriorityMap = new LinkedHashMap<>();
            Map<String, String> codeToPriorityRCPAMap = new LinkedHashMap<>();

            Set<String> jsonCodes = new HashSet<>();
            Set<String> jsonCodesRCPA = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonCodes.add(jsonArray.optJSONObject(i).optString("Code"));
                if (!jsonArray.optJSONObject(i).optString("Product_Mode").equalsIgnoreCase("Sample")) {
                    jsonCodesRCPA.add(jsonArray.optJSONObject(i).optString("Code"));
                }
            }

            int labelCount = 1, labelCountRCPA = 1;
            for (String code : priorityCodes) {
                if (jsonCodes.contains(code)) {
                    codeToPriorityMap.put(code, "P" + labelCount++);
                }
                if (jsonCodesRCPA.contains(code)) {
                    codeToPriorityRCPAMap.put(code, "P" + labelCountRCPA++);
                }
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String code = jsonObject.optString("Code");
                String name = jsonObject.optString("Name");
                String dRate = jsonObject.optString("DRate");
                String productMode = jsonObject.optString("Product_Mode");
                String priorityCode = codeToPriorityMap.getOrDefault(code, "");
                String priorityCodeRCPA = codeToPriorityRCPAMap.getOrDefault(code, "");

                if (!code.equalsIgnoreCase("-1") && !code.isEmpty()) {
                    if (!productMode.equalsIgnoreCase("Sample")) {
                        RCPASelectPrdSide.PrdFullList.add(new SaveCallProductList(name, code, dRate, productMode, priorityCodeRCPA));
                    }
                    if (CallActivityCustDetails.get(0).getPriorityPrdCode().contains(code)) {
                        ProductFragment.checkedPrdList.add(new CallCommonCheckedList(name, code, "0", false, "P" + Priority_count++, productMode, priorityCode));
                        StockSample.add(new CallCommonCheckedList(code, "0", "0"));
                        if (!productMode.equalsIgnoreCase("Sale")) {
                            AddCallSelectPrdSide.callSampleList.add(new CallCommonCheckedList(name, code, "0", false, "P" + Priority_count++, productMode, ""));
                        }
                    } else {
                        ProductFragment.checkedPrdList.add(new CallCommonCheckedList(name, code, "0", false, productMode, productMode, priorityCode));
                        StockSample.add(new CallCommonCheckedList(code, "0", "0"));
                        if (!productMode.equalsIgnoreCase("Sale")) {
                            AddCallSelectPrdSide.callSampleList.add(new CallCommonCheckedList(name, code, "0", false, productMode, productMode, ""));
                        }
                    }
                }
            }

//            if (SampleValidation.equalsIgnoreCase("1")) {
//                for (int i = 0; i < ProductFragment.checkedPrdList.size(); i++) {
//                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
//                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
//                        if (!ProductFragment.checkedPrdList.get(i).getCategory().equalsIgnoreCase("Sale") && jsonObjectSample.getString("Code").equalsIgnoreCase(ProductFragment.checkedPrdList.get(i).getCode())) {
//                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
//                            StockSample.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), jsonObjectSample.getString("Balance_Stock")));
//                            break;
//                        } else {
//                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra()));
//                            StockSample.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).getStock_balance()));
//                        }
//                    }
//                }
//
//                for (int i = 0; i < AddCallSelectPrdSide.callSampleList.size(); i++) {
//                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
//                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
//                        if (jsonObjectSample.getString("Code").equalsIgnoreCase(AddCallSelectPrdSide.callSampleList.get(i).getCode())) {
//                            AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(AddCallSelectPrdSide.callSampleList.get(i).getName(), AddCallSelectPrdSide.callSampleList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), AddCallSelectPrdSide.callSampleList.get(i).isCheckedItem(), AddCallSelectPrdSide.callSampleList.get(i).getCategory(), AddCallSelectPrdSide.callSampleList.get(i).getCategoryExtra()));
//                            break;
//                        } else {
//                            AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(AddCallSelectPrdSide.callSampleList.get(i).getName(), AddCallSelectPrdSide.callSampleList.get(i).getCode(), AddCallSelectPrdSide.callSampleList.get(i).getStock_balance(), AddCallSelectPrdSide.callSampleList.get(i).isCheckedItem(), AddCallSelectPrdSide.callSampleList.get(i).getCategory(), AddCallSelectPrdSide.callSampleList.get(i).getCategoryExtra()));
//                        }
//                    }
//                }
//            }
            if (SampleValidation.equalsIgnoreCase("1")) {
                for (int i = 0; i < ProductFragment.checkedPrdList.size(); i++) {
                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
                        if (!ProductFragment.checkedPrdList.get(i).getCategory().equalsIgnoreCase("Sale") && jsonObjectSample.getString("Code").equalsIgnoreCase(ProductFragment.checkedPrdList.get(i).getCode())) {
                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra(), ProductFragment.checkedPrdList.get(i).getPriorityCodes()));
                            CallCommonCheckedList stockItem = new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), jsonObjectSample.getString("Balance_Stock"));
                            if (i < StockSample.size()) {
                                StockSample.set(i, stockItem);
                            } else {
                                StockSample.add(stockItem);
                            }
                            break;
                        } else {
                            ProductFragment.checkedPrdList.set(i, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getName(), ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).isCheckedItem(), ProductFragment.checkedPrdList.get(i).getCategory(), ProductFragment.checkedPrdList.get(i).getCategoryExtra(), ProductFragment.checkedPrdList.get(i).getPriorityCodes()));
                            CallCommonCheckedList stockItem = new CallCommonCheckedList(ProductFragment.checkedPrdList.get(i).getCode(), ProductFragment.checkedPrdList.get(i).getStock_balance(), ProductFragment.checkedPrdList.get(i).getStock_balance());
                            if (i < StockSample.size()) {
                                StockSample.set(i, stockItem);
                            } else {
                                StockSample.add(stockItem);
                            }
                        }
                    }
                }

                // The same kind of safe update logic applies to this loop too:
                for (int i = 0; i < AddCallSelectPrdSide.callSampleList.size(); i++) {
                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
                        if (jsonObjectSample.getString("Code").equalsIgnoreCase(AddCallSelectPrdSide.callSampleList.get(i).getCode())) {
                            AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(AddCallSelectPrdSide.callSampleList.get(i).getName(), AddCallSelectPrdSide.callSampleList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), AddCallSelectPrdSide.callSampleList.get(i).isCheckedItem(), AddCallSelectPrdSide.callSampleList.get(i).getCategory(), AddCallSelectPrdSide.callSampleList.get(i).getCategoryExtra(), AddCallSelectPrdSide.callSampleList.get(i).getPriorityCodes()));
                            break;
                        } else {
                            AddCallSelectPrdSide.callSampleList.set(i, new CallCommonCheckedList(AddCallSelectPrdSide.callSampleList.get(i).getName(), AddCallSelectPrdSide.callSampleList.get(i).getCode(), AddCallSelectPrdSide.callSampleList.get(i).getStock_balance(), AddCallSelectPrdSide.callSampleList.get(i).isCheckedItem(), AddCallSelectPrdSide.callSampleList.get(i).getCategory(), AddCallSelectPrdSide.callSampleList.get(i).getCategoryExtra(), AddCallSelectPrdSide.callSampleList.get(i).getPriorityCodes()));
                        }
                    }
                }
            }

            try {
                if (detailedProducts != null && !detailedProducts.isEmpty()) {
                    Set<String> detailedProductsSet = new HashSet<>(Arrays.asList(detailedProducts.split(",")));
                    for (int j = 0; j < ProductFragment.checkedPrdList.size(); j++) {
                        CallCommonCheckedList PrdList = ProductFragment.checkedPrdList.get(j);
                        if (detailedProductsSet.contains(PrdList.getCode())) {
                            CheckProductListAdapter.saveCallProductListArrayList.add(new SaveCallProductList(PrdList.getName(), PrdList.getCode(), PrdList.getCategory(), PrdList.getStock_balance(), PrdList.getStock_balance(), "", "", "", "0", true));
                            PrdList.setCheckedItem(true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.v("chkSample", "---size---" + AddCallSelectPrdSide.callSampleList.size());
            Collections.sort(ProductFragment.checkedPrdList, (a, b) -> {
                boolean aIsInvalid = "-10".equals(a.getCode());
                boolean bIsInvalid = "-10".equals(b.getCode());

                if (aIsInvalid && !bIsInvalid) return -1;
                if (!aIsInvalid && bIsInvalid) return 1;
                if (aIsInvalid && bIsInvalid) return 0;

                int priorityCompare = Integer.compare(extractPriorityNumber(a.getPriorityCodes()), extractPriorityNumber(b.getPriorityCodes()));
                if (priorityCompare != 0) return priorityCompare;

                return a.getCategory().compareToIgnoreCase(b.getCategory());
            });
//            Collections.sort(ProductFragment.checkedPrdList, Comparator.comparing(CallCommonCheckedList::getCategory));
            Collections.sort(AddCallSelectPrdSide.callSampleList, (a, b) -> {
                boolean aIsInvalid = "-10".equals(a.getCode());
                boolean bIsInvalid = "-10".equals(b.getCode());

                if (aIsInvalid && !bIsInvalid) return -1;
                if (!aIsInvalid && bIsInvalid) return 1;
                if (aIsInvalid && bIsInvalid) return 0;

                int priorityCompare = Integer.compare(extractPriorityNumber(a.getPriorityCodes()), extractPriorityNumber(b.getPriorityCodes()));
                if (priorityCompare != 0) return priorityCompare;

                return a.getCategory().compareToIgnoreCase(b.getCategory());
            });
//            Collections.sort(AddCallSelectPrdSide.callSampleList, Comparator.comparing(CallCommonCheckedList::getCategory));
            Collections.sort(RCPASelectPrdSide.PrdFullList, (a, b) -> {
//                boolean aIsInvalid = "-10".equals(a.getCode());
//                boolean bIsInvalid = "-10".equals(b.getCode());
//
//                if(aIsInvalid && !bIsInvalid) return -1;
//                if(!aIsInvalid && bIsInvalid) return 1;
//                if(aIsInvalid && bIsInvalid) return 0;

                int priorityCompare = Integer.compare(extractPriorityNumber(a.getPriority()), extractPriorityNumber(b.getPriority()));
                if (priorityCompare != 0) return priorityCompare;

                return a.getCategory().compareToIgnoreCase(b.getCategory());
            });
            Log.d("TAG", "AddProductsData: " + RCPASelectPrdSide.PrdFullList);
        } catch (Exception e) {
            Log.v("chkSample", "---error---" + e);
            e.printStackTrace();
        }
    }

    private int extractPriorityNumber(String priority) {
        if (priority == null || priority.isEmpty()) return Integer.MAX_VALUE;
        if (priority.matches("P\\d+")) {
            return Integer.parseInt(priority.substring(1));
        }
        return Integer.MAX_VALUE;
    }

    private String extractValues(String s, String data) {
        if (TextUtils.isEmpty(s)) return "";

        String[] clstarrrayqty = s.split("#");
        StringBuilder ss1 = new StringBuilder();

        for (String value : clstarrrayqty) {
            String[] dataArray = value.substring(value.indexOf("~") + 1).split("\\$");

            if (data.equalsIgnoreCase("sample")) {
                ss1.append(value.substring(value.indexOf("~") + 1));
                ss1 = new StringBuilder(ss1.toString().replace("$0^0", "") + ",");
                int index = ss1.indexOf("$");
                ss1 = new StringBuilder(ss1.substring(0, index) + ",");

            } else if (data.equalsIgnoreCase("input")) {
                ss1.append(value.substring(value.indexOf("~") + 1)).append(",");
            } else if (data.equalsIgnoreCase("names") || data.equalsIgnoreCase("codes")) {
                ss1.append(value.substring(0, value.indexOf("~"))).append(",");
            } else if (data.equalsIgnoreCase("stockistname")) {
                ss1.append(value.substring(0, value.indexOf("^")).substring(value.lastIndexOf("~") + 1)).append(",");
            } else if (data.equalsIgnoreCase("stockistcode")) {
                ss1.append(value.substring(value.indexOf("^") + 1)).append(",");
            } else if (data.equalsIgnoreCase("Rx")) {
                ss1.append(dataArray[1]).append(",");
            } else if (data.equalsIgnoreCase("Rcpa")) {
                String[] rcpa = dataArray[2].replace("^", ",").split("[,]");
                ss1.append(rcpa[1]).append(",");
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

    @Override
    protected void onResume() {
        super.onResume();
        CommonAlertBox.CheckLocationStatus(DCRCallActivity.this, gpsTrack);
        Log.e("TAG", "onResume: ");
        timeZoneVerification();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, new IntentFilter("com.saneforce.SYNC_COMPLETED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        handler.postDelayed(runnable, delay);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if(type != null && type.matches("(?i)DR|CH|ST|UL|HOS|CIP|SE|PR|GIF|TM|OTR|FSD|AMS")) {
                startActivity(new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class));
                finish();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(updateClock);
    }

    private void timeZoneVerification() {
        boolean isAutoTimeZoneEnabled = commonUtilsMethods.isAutoTimeEnabled(DCRCallActivity.this) && commonUtilsMethods.isTimeZoneAutomatic(DCRCallActivity.this);
        if (!isAutoTimeZoneEnabled) {
            CommonUtilsMethods.showCustomDialog(this);
        }
    }
}
