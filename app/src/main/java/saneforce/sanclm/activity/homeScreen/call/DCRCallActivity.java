package saneforce.sanclm.activity.homeScreen.call;

import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.callAddedJointList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.callCaptureImageLists;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.jwothersBinding;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.CallAddCustListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.CallInputListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.product.CallProductListAdapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.DetailedFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.FeedbackSelectionSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.InputFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JointworkSelectionSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragmentSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
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
    public static ActivityDcrcallBinding dcrcallBinding;
    public static String PobNeed, OverallFeedbackNeed, EventCaptureNeed, JwNeed, SampleValidation, InputValidation, PrdSamNeed, PrdRxNeed, CapSamQty, CapRxqty;
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
    String GeoChk, capPrd, capInp, RCPANeed, HosNeed, FeedbackMandatory, CurrentDate, MgrRcpaMandatory, EventCapMandatory, JwMandatory, CurrentTime, PrdMandatory, InpMandatory, RcpaMandatory, PobMandatory, RemarkMandatory, SamQtyMandatory, RxQtyMandatory;
    double lat, lng;
    ApiInterface api_interface;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrcallBinding = ActivityDcrcallBinding.inflate(getLayoutInflater());
        setContentView(dcrcallBinding.getRoot());

        commonUtilsMethods = new CommonUtilsMethods(this);
        progressDialog = CommonUtilsMethods.createProgressDialog(DCRCallActivity.this);
        sqLite = new SQLite(this);
        //  commonUtilsMethods.FullScreencall();
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

        dcrcallBinding.viewPager.setAdapter(viewPagerAdapter);
        dcrcallBinding.tabLayout.setupWithViewPager(dcrcallBinding.viewPager);
        dcrcallBinding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        dcrcallBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dcrcallBinding.tabLayout.getWindowToken(), 0);
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

        dcrcallBinding.tagCustName.setText(CallActivityCustDetails.get(0).getName());

        dcrcallBinding.ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
            startActivity(intent);
        });

        dcrcallBinding.btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
            startActivity(intent);
        });

        dcrcallBinding.btnFinalSubmit.setOnClickListener(view -> {
            /*CreateJsonFileCall();
            CallSaveDcrAPI(jsonSaveDcr.toString());*/
            if (CheckRequiredFunctions() && CheckCurrentLoc()) {
                CreateJsonFileCall();
                CallSaveDcrAPI(jsonSaveDcr.toString());
            }
        });
    }

    public boolean CheckRequiredFunctions() {
        switch (CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (SfType.equalsIgnoreCase("1")) {
                    if (PrdMandatory.equalsIgnoreCase("1")) {
                        if (CallProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter the " + capPrd, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    if (PrdSamNeed.equalsIgnoreCase("1") && SamQtyMandatory.equalsIgnoreCase("1")) {
                        if (CallProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter the " + capPrd, Toast.LENGTH_LONG).show();
                            return false;
                        } else {
                            for (int i = 0; i < CallProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (CallProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().isEmpty() || CallProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty().equalsIgnoreCase("0")) {
                                    Toast.makeText(getApplicationContext(), "Enter the " + CapSamQty + " Values", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            }
                        }
                    }

                    if (PrdRxNeed.equalsIgnoreCase("1") && RxQtyMandatory.equalsIgnoreCase("1")) {
                        if (CallProductListAdapter.saveCallProductListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter the " + capPrd, Toast.LENGTH_LONG).show();
                            return false;
                        } else {
                            for (int i = 0; i < CallProductListAdapter.saveCallProductListArrayList.size(); i++) {
                                if (CallProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().isEmpty() || CallProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty().equalsIgnoreCase("0")) {
                                    Toast.makeText(getApplicationContext(), "Enter the " + CapRxqty + " Values", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            }
                        }
                    }

                    if (InpMandatory.equalsIgnoreCase("1")) {
                        if (CallInputListAdapter.saveCallInputListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Select the " + capInp, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    if (RCPANeed.equalsIgnoreCase("0") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPAFragmentSide.rcpaAddedProdListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Add RCPA Values", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                } else {
                    if (MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPAFragmentSide.rcpaAddedProdListArrayList.size() == 0) {
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
                    if (jwothersBinding.edPob.getText().toString().isEmpty() || jwothersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
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
                    if (jwothersBinding.edRemarks.getText().toString().isEmpty() || jwothersBinding.edRemarks.getText().toString().equalsIgnoreCase("")) {
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

                break;
            case "2":
                if (SfType.equalsIgnoreCase("1")) {
                    if (RCPANeed.equalsIgnoreCase("1") && RcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPAFragmentSide.rcpaAddedProdListArrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Add RCPA Values", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                } else {
                    if (MgrRcpaMandatory.equalsIgnoreCase("0")) {
                        if (RCPAFragmentSide.rcpaAddedProdListArrayList.size() == 0) {
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
                    if (jwothersBinding.edPob.getText().toString().isEmpty() || jwothersBinding.edPob.getText().toString().equalsIgnoreCase("")) {
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
                break;
            case "3":
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
                            .setCancelable(false).setMessage("Activate the Gps to proceed futher") // Want to enable?
                            .setPositiveButton("Yes", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
                } else {
                    val = true;
                }
            } catch (Exception e) {
                val = false;
            }
        } else {
            val = true;
        }
        return val;
    }

    private void CallSaveDcrAPI(String jsonSaveDcr) {
        if (progressDialog == null) {
            progressDialog = CommonUtilsMethods.createProgressDialog(DCRCallActivity.this);
            progressDialog.show();
        } else {
            progressDialog.show();
        }
        Log.v("callsave", "---" + jsonSaveDcr);
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
        } catch (Exception e) {

        }
        if (callCaptureImageLists.size() > 0) {
            for (int i = 0; i < callCaptureImageLists.size(); i++) {
                Log.v("ImgUpload", callCaptureImageLists.get(i).getFilePath());
                MultipartBody.Part imgg = convertimg("EventImg", callCaptureImageLists.get(i).getFilePath());
                HashMap<String, RequestBody> values = field(jsonImage.toString());
                Call<JsonObject> saveImgDcr = api_interface.saveImgDcr(values, imgg);
                saveImgDcr.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject json = new JSONObject(response.body().toString());
                                Log.v("ImgUpload", json.toString());
                                if (json.getString("success").equalsIgnoreCase("true")) {
                                    // Toast.makeText(DCRCallActivity.this, "Picture Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        }

        Call<JsonObject> callSaveDcr = null;
        callSaveDcr = api_interface.saveDcr(jsonSaveDcr);
        callSaveDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                        Log.v("callsave", "---" + jsonSaveRes);
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
                        Log.v("callsave", "---" + e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.v("callsave", "---" + t);
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
            for (int i = 0; i < CallProductListAdapter.saveCallProductListArrayList.size(); i++) {
                //SampleStockChange
                for (int j = 0; j < jsonArraySamStk.length(); j++) {
                    JSONObject jsonObject = jsonArraySamStk.getJSONObject(j);
                    Log.v("chkSamStk", CallProductListAdapter.saveCallProductListArrayList.get(i).getCode() + "-----" + jsonObject.getString("Code") + "----" + CallProductListAdapter.saveCallProductListArrayList.get(i).getCode().equalsIgnoreCase(jsonObject.getString("Code")));
                    if (CallProductListAdapter.saveCallProductListArrayList.get(i).getCode().equalsIgnoreCase(jsonObject.getString("Code"))) {
                        jsonObject.remove("Balance_Stock");
                        jsonObject.put("Balance_Stock", Integer.parseInt(CallProductListAdapter.saveCallProductListArrayList.get(i).getBalance_sam_stk()));
                        break;
                    }
                }
            }
            sqLite.saveMasterSyncData(Constants.STOCK_BALANCE, jsonArraySamStk.toString(), 0);
        } catch (Exception e) {
        }
    }

    private void UpdateInputStock() {
        try {
            JSONArray jsonArrayInpStk = sqLite.getMasterSyncDataByKey(Constants.INPUT_BALANCE);
            for (int i = 0; i < CallInputListAdapter.saveCallInputListArrayList.size(); i++) {
                //InputStockChange
                for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                    JSONObject jsonObject = jsonArrayInpStk.getJSONObject(j);
                    Log.v("chkInpStk", CallInputListAdapter.saveCallInputListArrayList.get(i).getInp_code() + "-----" + jsonObject.getString("Code") + "----" + CallInputListAdapter.saveCallInputListArrayList.get(i).getInp_code().equalsIgnoreCase(jsonObject.getString("Code")));
                    if (CallInputListAdapter.saveCallInputListArrayList.get(i).getInp_code().equalsIgnoreCase(jsonObject.getString("Code"))) {
                        jsonObject.remove("Balance_Stock");
                        jsonObject.put("Balance_Stock", Integer.parseInt(CallInputListAdapter.saveCallInputListArrayList.get(i).getBalance_inp_stk()));
                        break;
                    }
                }
            }
            sqLite.saveMasterSyncData(Constants.INPUT_BALANCE, jsonArrayInpStk.toString(), 0);
        } catch (Exception e) {
        }
    }

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<String, RequestBody>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(MultipartBody.FORM, txt);
    }

    public MultipartBody.Part convertimg(String tag, String path) {
        Log.d("pathaa1", tag + "-" + path);
        MultipartBody.Part yy = null;
        try {
            File file;
            if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(getApplicationContext()).compressToFile(new File(path));
                Log.d("pathaa2", tag + "-" + path);
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("pathaa3", String.valueOf(yy));
        } catch (Exception e) {
        }
        return yy;
    }


   /* @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {
                String finalPath = "/storage/emulated/0";
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                filePath = outputFileUri.getPath();
                filePath = filePath.substring(1);
                filePath = finalPath + filePath.substring(filePath.indexOf("/"));
                String result = String.valueOf(resultCode);
                if (result.equalsIgnoreCase("-1")) {
                    callCaptureImageLists.add(0, new CallCaptureImageList("", "", photo, filePath, imageName));
                    adapterCallCaptureImage = new AdapterCallCaptureImage(this, callCaptureImageLists);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                    jwothersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
                    jwothersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
                    jwothersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
                    jwothersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);
                }
            }
        } catch (Exception e) {
            Log.e("imagerror", "--" + e);
        }
    }*/

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
            for (int i = 0; i < CallInputListAdapter.saveCallInputListArrayList.size(); i++) {
                JSONObject json_joint = new JSONObject();
                json_joint.put("Code", CallInputListAdapter.saveCallInputListArrayList.get(i).getInp_code());
                json_joint.put("Name", CallInputListAdapter.saveCallInputListArrayList.get(i).getInput_name());
                json_joint.put("IQty", CallInputListAdapter.saveCallInputListArrayList.get(i).getInp_qty());
                jsonArray.put(json_joint);
            }
            jsonSaveDcr.put("Inputs", jsonArray);

            //Products
            jsonArray = new JSONArray();
            for (int i = 0; i < CallProductListAdapter.saveCallProductListArrayList.size(); i++) {
                JSONObject json_product = new JSONObject();
                json_product.put("Code", CallProductListAdapter.saveCallProductListArrayList.get(i).getCode());
                json_product.put("Name", CallProductListAdapter.saveCallProductListArrayList.get(i).getName());
                json_product.put("Group", "0");
                json_product.put("ProdFeedbk", "");
                json_product.put("Rating", "");
                JSONObject json_date = new JSONObject();
                json_date.put("sTm", CurrentDate + " " + CurrentTime);
                json_date.put("eTm", CurrentDate + " " + CurrentTime);
                json_product.put("Timesline", json_date);
                json_product.put("Appver", Constants.APP_VERSION);
                json_product.put("Mod", Constants.APP_MODE);
                json_product.put("SmpQty", CallProductListAdapter.saveCallProductListArrayList.get(i).getSample_qty());
                json_product.put("RxQty", CallProductListAdapter.saveCallProductListArrayList.get(i).getRx_qty());
                json_product.put("Type", CallActivityCustDetails.get(0).getType());
                json_product.put("StockistName", "");
                json_product.put("StockistCode", "");
                JSONArray jsonArraySlides = new JSONArray();
                json_product.put("Slides", jsonArraySlides);
                jsonArray.put(json_product);
            }
            jsonSaveDcr.put("Products", jsonArray);

            //Additional Call
            jsonArray = new JSONArray();
            for (int i = 0; i < CallAddCustListAdapter.saveAdditionalCallArrayList.size(); i++) {
                JSONObject json_AdditionalCall = new JSONObject();
                json_AdditionalCall.put("Code", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getCode());
                json_AdditionalCall.put("Name", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getName());
                json_AdditionalCall.put("town_code", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getTown_code());
                json_AdditionalCall.put("town_name", CallAddCustListAdapter.saveAdditionalCallArrayList.get(i).getTown_name());
                jsonArray.put(json_AdditionalCall);
            }
            jsonSaveDcr.put("AdCuss", jsonArray);

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

            JSONArray jsonArrayWt = new JSONArray();
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

            //RCPA
           /* JSONArray jsonRcpa = null;
            String jsonarrray = "";
            jsonRcpa = new JSONArray(jsonarrray);
            jointObj.put("RCPAEntry", jsonRcpa);*/

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
            if (PobNeed.equalsIgnoreCase("0") && !Objects.requireNonNull(jwothersBinding.edPob.getText()).toString().isEmpty()) {
                jsonSaveDcr.put("DCSUPOB", jwothersBinding.edPob.getText().toString());
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
            EventCapMandatory = loginResponse.getCipEvent_Md();

            JSONArray jsonArray = new JSONArray();
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
                    CapRxqty = setUpResponse.getCaptionDrRxQty();

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
                    CapRxqty = setUpResponse.getCaptionChemistRxQty();

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
                    CapRxqty = setUpResponse.getCaptionStkRxQty();

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
                    CapRxqty = setUpResponse.getCaptionUndrRxQty();

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
                JSONObject CustsetupData = jsonArray.getJSONObject(0);
                customSetupResponse = new CustomSetupResponse();
                Type typeCustomSetup = new TypeToken<CustomSetupResponse>() {
                }.getType();
                customSetupResponse = new Gson().fromJson(String.valueOf(CustsetupData), typeCustomSetup);
                HosNeed = customSetupResponse.getHospNeed();
                if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {

                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {

                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
                    PobNeed = customSetupResponse.getStockistPobNeed();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
                    PobNeed = customSetupResponse.getUndrPobNeed();
                }
            }

        } catch (Exception e) {

        }
    }

    private void AddJWData() {
        callCaptureImageLists = new ArrayList<>();
        JWOthersFragment.callAddedJointList = new ArrayList<>();
        JointworkSelectionSide.JwList = new ArrayList<>();
    }

    private void AddRCPAData() {
        RCPAFragmentSide.RCPAAddCompSideViewArrayList = new ArrayList<>();
        RCPAFragmentSide.rcpaAddedProdListArrayList = new ArrayList<>();
        RCPAFragmentSide.rcpa_Added_list = new ArrayList<>();
        RCPAFragmentSide.chemistNames = new ArrayList<>();
    }

    private void AddAdditionalCallData() {
        AdditionalCallFragment.custListArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.addInputAdditionalCallArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.addSampleAdditionalCallArrayList = new ArrayList<>();
        CallAddCustListAdapter.saveAdditionalCallArrayList = new ArrayList<>();
        SaveAdditionalCallAdapter.nestedAddSampleCallDetails = new ArrayList<>();
        SaveAdditionalCallAdapter.nestedAddInputCallDetails = new ArrayList<>();

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

        } catch (Exception e) {

        }
/*


        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Aasik", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Aravindh", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Eman", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Rahamathullah", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Vignesh", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Joseph", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Philip", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Govindh", false));*/
    }

    private void AddInputData() {
        InputFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallInputListAdapter.saveCallInputListArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.callInputList = new ArrayList<>();

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.INPUT);
            JSONArray jsonArrayInpStk = sqLite.getMasterSyncDataByKey(Constants.INPUT_BALANCE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject jsonFdate = new JSONObject(jsonObject.getString("EffF"));
                JSONObject jsonTdate = new JSONObject(jsonObject.getString("EffT"));
                Log.v("JsonInput", jsonFdate.getString("date").substring(0, 10) + "---" + jsonTdate.getString("date").substring(0, 10));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                String todayData = CommonUtilsMethods.getCurrentInstance();

                Date d1 = sdf.parse(jsonFdate.getString("date").substring(0, 10));
                Date d2 = sdf.parse(todayData);
                Date d3 = sdf.parse(jsonTdate.getString("date").substring(0, 10));

                if (d2 != null) {
                    if (d2.compareTo(d1) >= 0) {
                        if (d2.compareTo(d3) <= 0) {
                            InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false));
                            AdditionalCallDetailedSide.callInputList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false));
                        }
                    }
                }
            }

            if (InputValidation.equalsIgnoreCase("1")) {
                for (int i = 0; i < InputFragment.callCommonCheckedListArrayList.size(); i++) {
                    for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                        JSONObject jsonObjectInput = jsonArrayInpStk.getJSONObject(j);
                        Log.v("chkinput", InputFragment.callCommonCheckedListArrayList.get(i).getCode() + "---" + jsonObjectInput.getString("Code") + "--chkk-----" + jsonObjectInput.getString("Code").equalsIgnoreCase(InputFragment.callCommonCheckedListArrayList.get(i).getCode()));
                        if (jsonObjectInput.getString("Code").equalsIgnoreCase(InputFragment.callCommonCheckedListArrayList.get(i).getCode())) {
                            InputFragment.callCommonCheckedListArrayList.set(i, new CallCommonCheckedList(InputFragment.callCommonCheckedListArrayList.get(i).getName(), InputFragment.callCommonCheckedListArrayList.get(i).getCode(), jsonObjectInput.getString("Balance_Stock"), InputFragment.callCommonCheckedListArrayList.get(i).isCheckedItem()));
                            break;
                        } else {
                            InputFragment.callCommonCheckedListArrayList.set(i, new CallCommonCheckedList(InputFragment.callCommonCheckedListArrayList.get(i).getName(), InputFragment.callCommonCheckedListArrayList.get(i).getCode(), InputFragment.callCommonCheckedListArrayList.get(i).getStock_balance(), InputFragment.callCommonCheckedListArrayList.get(i).isCheckedItem()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.v("JsonInput", "error--" + e);
        }
    }

    private void AddProductsData() {

        ProductFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallProductListAdapter.saveCallProductListArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.callSampleList = new ArrayList<>();
        try {
            Log.v("prdds", "prioritycode---" + CallActivityCustDetails.get(0).getPriorityPrdCode());
            int Priority_count = 1;
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
            JSONArray jsonArrayPrdStk = sqLite.getMasterSyncDataByKey(Constants.STOCK_BALANCE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (CallActivityCustDetails.get(0).getPriorityPrdCode().contains(jsonObject.getString("Code"))) {
                    ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, "P" + Priority_count++, jsonObject.getString("Product_Mode")));
                } else {
                    ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, jsonObject.getString("Product_Mode"), jsonObject.getString("Product_Mode")));
                }
                AdditionalCallDetailedSide.callSampleList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), "0", false, jsonObject.getString("Product_Mode"), jsonObject.getString("Product_Mode")));
            }

            if (SampleValidation.equalsIgnoreCase("1")) {
                for (int i = 0; i < ProductFragment.callCommonCheckedListArrayList.size(); i++) {
                    for (int j = 0; j < jsonArrayPrdStk.length(); j++) {
                        JSONObject jsonObjectSample = jsonArrayPrdStk.getJSONObject(j);
                        Log.v("chksample", ProductFragment.callCommonCheckedListArrayList.get(i).getCode() + "---" + jsonObjectSample.getString("Code") + "--chkk-----" + jsonObjectSample.getString("Code").equalsIgnoreCase(ProductFragment.callCommonCheckedListArrayList.get(i).getCode()));
                        if (jsonObjectSample.getString("Code").equalsIgnoreCase(ProductFragment.callCommonCheckedListArrayList.get(i).getCode())) {
                            ProductFragment.callCommonCheckedListArrayList.set(i, new CallCommonCheckedList(ProductFragment.callCommonCheckedListArrayList.get(i).getName(), ProductFragment.callCommonCheckedListArrayList.get(i).getCode(), jsonObjectSample.getString("Balance_Stock"), ProductFragment.callCommonCheckedListArrayList.get(i).isCheckedItem(), ProductFragment.callCommonCheckedListArrayList.get(i).getCategory(), ProductFragment.callCommonCheckedListArrayList.get(i).getCategoryExtra()));
                            break;
                        } else {
                            ProductFragment.callCommonCheckedListArrayList.set(i, new CallCommonCheckedList(ProductFragment.callCommonCheckedListArrayList.get(i).getName(), ProductFragment.callCommonCheckedListArrayList.get(i).getCode(), ProductFragment.callCommonCheckedListArrayList.get(i).getStock_balance(), ProductFragment.callCommonCheckedListArrayList.get(i).isCheckedItem(), ProductFragment.callCommonCheckedListArrayList.get(i).getCategory(), ProductFragment.callCommonCheckedListArrayList.get(i).getCategoryExtra()));
                        }
                    }
                }
            }

            Collections.sort(ProductFragment.callCommonCheckedListArrayList, Comparator.comparing(CallCommonCheckedList::getCategory));

            Log.v("prdds", "final_size---" + ProductFragment.callCommonCheckedListArrayList.size());
           /* for (int i = 0; i < ProductFragment.callCommonCheckedListArrayList.size(); i++) {
                if (ProductFragment.callCommonCheckedListArrayList.get(i).getCategory().contains("P")) {
                    Collections.sort(ProductFragment.callCommonCheckedListArrayList, Comparator.comparing(CallCommonCheckedList::getCategory));
                }
            }*/

        } catch (Exception e) {
            Log.v("chksample", "---errorr---" + e);
        }




      /*  ProductFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallProductListAdapter.saveCallProductListArrayList = new ArrayList<>();
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("AtriFlam Tuesday data Para", "90", false, "P1"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Insat", "90", false, "SM"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Meff", "90", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Sucraz", "90", false, "P2"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Stanvit", "90", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Calch 500", "90", false, "SM/SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Arizon 700", "90", false, "P1"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Terracite", "90", false, "SM"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Paracemetol", "90", false, "P2"));
        for (int i = 0; i < ProductFragment.callCommonCheckedListArrayList.size(); i++) {
            if (ProductFragment.callCommonCheckedListArrayList.get(i).getCategory().equalsIgnoreCase("P1")) {
                Collections.sort(ProductFragment.callCommonCheckedListArrayList, Comparator.comparing(CallCommonCheckedList::getCategory));
            }
        }
*/
  /*  for (int i = 0;i<ProductFragment.callCommonCheckedListArrayList.size();i++){
        if (ProductFragment.callCommonCheckedListArrayList.get(i).getCategory().equalsIgnoreCase("P1")) {
            ProductFragment.callCommonCheckedListArrayList.set(0, new CallCommonCheckedList(ProductFragment.callCommonCheckedListArrayList.get(i).getName(), ProductFragment.callCommonCheckedListArrayList.get(i).getCode(), ProductFragment.callCommonCheckedListArrayList.get(i).isCheckedItem(), ProductFragment.callCommonCheckedListArrayList.get(i).getCategory()));
        }
    }*/
    }
}


  /*  Backend Pending:
        1) Add CustStatus for Setup; --> GeoTagApprovalNeed
        2) Add Cust_status for all Doctor,Chemist,Cip,Stockist,Undr --> cust_status
        3) Add Cust_status for json_object to send the mapped tagged Customer
        4) Add Promoted Products for json_object to send the dcr call Customer
        5) RCPA in Products Screen*/
