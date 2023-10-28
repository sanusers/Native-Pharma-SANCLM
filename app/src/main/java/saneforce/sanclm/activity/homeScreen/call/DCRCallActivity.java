package saneforce.sanclm.activity.homeScreen.call;

import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.callCaptureImageLists;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.jwothersBinding;
import static saneforce.sanclm.activity.profile.CustomerProfile.isDetailingRequired;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import saneforce.sanclm.commonClasses.CommonSharedPreference;
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
    public static String PobNeed, OverallFeedbackNeed, EventCaptureNeed, JwNeed, SampleValidation, InputValidation, PrdSamNeed, PrdRxNeed;
    String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode;
    DCRCallTabLayoutAdapter viewPagerAdapter;
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference mCommonSharedPreference;
    SQLite sqLite;
    ProgressDialog progressDialog = null;
    SetupResponse setUpResponse;
    CustomSetupResponse customSetupResponse;
    GPSTrack gpsTrack;
    LoginResponse loginResponse;
    JSONObject jsonSaveDcr;
    String capDrPrd, capChemPrd, capStkPrd, capUndrPrd, capCipPrd, capDrInp, HosNeed, capChemInp, capStkInp, capUndrInp, drRCPANeed, ChemRCPANeed, CurrentDate, CurrentTime;
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
        mCommonSharedPreference = new CommonSharedPreference(this);
        progressDialog = CommonUtilsMethods.createProgressDialog(DCRCallActivity.this);
        progressDialog.dismiss();
        sqLite = new SQLite(this);
        commonUtilsMethods.FullScreencall();
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

        getRequiredData();

        viewPagerAdapter = new DCRCallTabLayoutAdapter(getSupportFragmentManager());

        if (isDetailingRequired) {
            viewPagerAdapter.add(new DetailedFragment(), "Detailed");
        }

        if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
            viewPagerAdapter.add(new ProductFragment(), capDrPrd);
            viewPagerAdapter.add(new InputFragment(), capDrInp);
            viewPagerAdapter.add(new RCPAFragment(), "RCPA");
            viewPagerAdapter.add(new AdditionalCallFragment(), "Additional Calls");
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
            viewPagerAdapter.add(new ProductFragment(), capChemPrd);
            viewPagerAdapter.add(new InputFragment(), capChemInp);
            viewPagerAdapter.add(new RCPAFragment(), "RCPA");
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            viewPagerAdapter.add(new ProductFragment(), capStkPrd);
            viewPagerAdapter.add(new InputFragment(), capStkInp);
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
            viewPagerAdapter.add(new ProductFragment(), capUndrPrd);
            viewPagerAdapter.add(new InputFragment(), capUndrInp);
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
            CreateJsonFileCall();
            CallSaveDcrAPI(jsonSaveDcr.toString());
        });
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
                        Log.v("callsave", "---" + response.body());
                        Log.v("callsave", "---" + jsonSaveRes);
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                            Toast.makeText(DCRCallActivity.this, "Call Successfully Saved", Toast.LENGTH_SHORT).show();
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
            }
        });
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
            Cursor cursor = sqLite.getLoginData();
            loginResponse = new LoginResponse();
            String loginData = "";
            if (cursor.moveToNext()) {
                loginData = cursor.getString(0);
            }
            cursor.close();
            Type type = new TypeToken<LoginResponse>() {
            }.getType();
            loginResponse = new Gson().fromJson(loginData, type);

            SfType = loginResponse.getSf_type();
            SfCode = loginResponse.getSF_Code();
            SfName = loginResponse.getSF_Name();
            DivCode = loginResponse.getDivision_Code();
            SubDivisionCode = loginResponse.getSubdivision_code();
            Designation = loginResponse.getDesig();
            StateCode = loginResponse.getState_Code();

            JSONArray jsonArray = new JSONArray();
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                setUpResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setUpResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);

                if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
                    capDrPrd = setUpResponse.getCaptionDrPrd();
                    capDrInp = setUpResponse.getCaptionDrInp();
                    drRCPANeed = setUpResponse.getDrRcpaNeed();
                    PobNeed = setUpResponse.getDrPobNeed();
                    OverallFeedbackNeed = setUpResponse.getDrFeedbackNeed();
                    EventCaptureNeed = setUpResponse.getDrEveCapNeed();
                    JwNeed = setUpResponse.getDrJwNeed();
                    PrdSamNeed = setUpResponse.getDrSamNeed();
                    PrdRxNeed = setUpResponse.getDrRxNeed();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
                    capChemPrd = setUpResponse.getCaptionChemistPrd();
                    capChemInp = setUpResponse.getCaptionChemistInp();
                    ChemRCPANeed = setUpResponse.getChemistRcpaNeed();
                    PobNeed = setUpResponse.getChemistPobNeed();
                    EventCaptureNeed = setUpResponse.getChemistEveCapNeed();
                    OverallFeedbackNeed = setUpResponse.getChemistFeedbackNeed();
                    JwNeed = setUpResponse.getChemistJwNeed();
                    PrdSamNeed = setUpResponse.getChemistSamNeed();
                    PrdRxNeed = setUpResponse.getChemistRxNeed();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
                    capStkPrd = setUpResponse.getCaptionStokistPrd();
                    capStkInp = setUpResponse.getCaptionStokistInp();
                    OverallFeedbackNeed = setUpResponse.getStockistFeedbackNeed();
                    EventCaptureNeed = setUpResponse.getStockistEveCapNeed();
                    JwNeed = setUpResponse.getStockistJwNeed();
                    PrdRxNeed = setUpResponse.getStockistRxNeed();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
                    capUndrPrd = setUpResponse.getCaptionUndrPrd();
                    capUndrInp = setUpResponse.getCaptionUndrInp();
                    OverallFeedbackNeed = setUpResponse.getUndrFeedbackNeed();
                    EventCaptureNeed = setUpResponse.getUndrEveCapNeed();
                    JwNeed = setUpResponse.getUndrJwNeed();
                    PrdRxNeed = setUpResponse.getUndrRxNeed();
                }
                SampleValidation = setUpResponse.getSampleValidation();
                InputValidation = setUpResponse.getInputValidation();
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
                AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("Town_Code"), jsonObject.getString("Town_Name"), false));
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
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.INPUT);
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
                            InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false));
                            // Log.v("JsonInput", "d2 is in between d1 and d2");
                        } else {
                            //Log.v("JsonInput", "d2 is NOT in between d1 and d2");
                        }
                    } else {
                        //  Log.v("JsonInput", "d2 is NOT in between d1 and d2");
                    }
                }
            }

        } catch (Exception e) {
            Log.v("JsonInput", "error--" + e);
        }
        /*InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Pen", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Marker", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Key Chain", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Keyboard", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Watch", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Horlicks", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Umberlla", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Lunch Box", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Ball", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Jacket", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Bat", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Toys", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Plastic Bar", false));*/
    }

    private void AddProductsData() {

        ProductFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallProductListAdapter.saveCallProductListArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false, jsonObject.getString("Product_Mode")));
            }
        } catch (Exception e) {

        }

        for (int i = 0; i < ProductFragment.callCommonCheckedListArrayList.size(); i++) {
            if (ProductFragment.callCommonCheckedListArrayList.get(i).getCategory().equalsIgnoreCase("P1")) {
                Collections.sort(ProductFragment.callCommonCheckedListArrayList, Comparator.comparing(CallCommonCheckedList::getCategory));
            }
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