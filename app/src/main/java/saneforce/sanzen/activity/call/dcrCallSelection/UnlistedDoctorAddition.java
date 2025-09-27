package saneforce.sanzen.activity.call.dcrCallSelection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.AWS.AWSBucketsTag;
import saneforce.sanzen.AWS.Util;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.camera.CameraActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.Keys;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityUnlistedadditionBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class UnlistedDoctorAddition extends AppCompatActivity {
    public static ActivityUnlistedadditionBinding unlistedadditionbinding;
    CommonUtilsMethods commonUtilsMethods;
    String SfType = "", SfCode = "", SfName = "", DivCode = "", terrname = "", terrcode = "", usersfcode = "";
    public static String filePath = "", GeoTagImageNeed = "";
    public static GoogleMap mMap;
    int imgindx = 0;
    private String destinationFilePath = "";
    String txt_qua = "", txt_cat = "", txt_class = "", txt_spec = "", txt_terr = "", txt_hq = "";
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    double latitude, longitude;
    GPSTrack gpsTrack;

    String imageName = "";
    ArrayList<MasterSyncItemModel> UnlistedModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();
    int UnlistedStatus = 0;
    MasterDataDao masterDataDao;
    private RoomDB roomDB;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private int currentImageIndex = 0;
    static String TagImgNd = "";
    Util util;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unlistedadditionbinding = ActivityUnlistedadditionBinding.inflate(getLayoutInflater());
        setContentView(unlistedadditionbinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        util = new Util();
        if(SharedPref.getUNLcap(this).isEmpty() || SharedPref.getUNLcap(this) == null) {
            unlistedadditionbinding.drtagname.setText(getResources().getString(R.string.add) + " " + "Unlisted Doctor");
        }else {
            unlistedadditionbinding.drtagname.setText(getResources().getString(R.string.add) + " " + SharedPref.getUNLcap(this));
        }
        String clusterCap = SharedPref.getClusterCap(this);
        Log.d("ClusterCap", "Value: " + clusterCap);
        String firstChar = "";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.name) + "</font>";
        unlistedadditionbinding.txtDr.setText(Html.fromHtml(firstChar + firstChar2));
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.headquarter) + "</font>";
        unlistedadditionbinding.txtHq.setText(Html.fromHtml(firstChar + firstChar2));
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.speciality) + "</font>";
        unlistedadditionbinding.txtSpec.setText(Html.fromHtml(firstChar + firstChar2));
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.category) + "</font>";
        unlistedadditionbinding.txtCat.setText(Html.fromHtml(firstChar + firstChar2));
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.clases) + "</font>";
        unlistedadditionbinding.txtClass.setText(Html.fromHtml(firstChar + firstChar2));
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.qualifications) + "</font>";
        unlistedadditionbinding.txtQua.setText(Html.fromHtml(firstChar + firstChar2));
        if(SharedPref.getClusterCap(this).isEmpty() || SharedPref.getClusterCap(this) == null) {
            firstChar = "<font color='#000000'>" + getResources().getString(R.string.cluster) + "</font>";
            unlistedadditionbinding.txtTerritory.setHint(getResources().getString(R.string.select_cluster));
        }else {
            firstChar = "<font color='#000000'>" + SharedPref.getClusterCap(this) + "</font>";
            unlistedadditionbinding.txtTerritory.setHint(getResources().getString(R.string.select) + " " + clusterCap);
        }
        unlistedadditionbinding.txtTerritory.setText(Html.fromHtml(firstChar + firstChar2));

        SfType = SharedPref.getSfType(this);
        usersfcode = SharedPref.getSfCode(this);
        TagImgNd = SharedPref.getGeotagImg(this);
        if(SfType.equalsIgnoreCase("1")) {
            unlistedadditionbinding.two.setVisibility(View.GONE);
            // Adjust weight of remaining layouts to take equal space
            LinearLayout.LayoutParams paramsOne = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            LinearLayout.LayoutParams paramsThree = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            paramsThree.setMargins(15, 0, 0, 0);
            unlistedadditionbinding.one.setLayoutParams(paramsOne);
            unlistedadditionbinding.three.setLayoutParams(paramsThree);
        }else {
            unlistedadditionbinding.two.setVisibility(View.VISIBLE);
        }
        if(SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
            unlistedadditionbinding.layout6.setVisibility(View.VISIBLE);
        }else {
            unlistedadditionbinding.layout6.setVisibility(View.GONE);
        }

        unlistedadditionbinding.btnUnlstsave.setOnClickListener(v -> {
            unlistedadditionbinding.btnUnlstsave.setEnabled(false);
            if(unlistedadditionbinding.edtDctr.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.name));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }else if(unlistedadditionbinding.edtDctr.getText().toString().contains("'")) {
                unlistedadditionbinding.edtDctr.setError("Invalid Character");
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }else if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                if(unlistedadditionbinding.txtSelectHq.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.headquarter));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }else if(unlistedadditionbinding.txtSelectTerritory.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.territory));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }else if(unlistedadditionbinding.txtSelectSpec.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.speciality));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }else if(unlistedadditionbinding.txtSelectCategory.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.category));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }else if(unlistedadditionbinding.txtSelectClass.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.clases));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }else if(unlistedadditionbinding.txtSelectQua.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.qualifications));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }else if(!unlistedadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("") &&
                        TagImgNd.equalsIgnoreCase("0") && destinationFilePath.equalsIgnoreCase("")) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.Photo_mand));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);

                }else {
                    Log.v("qualification_txt", "arent_empty");
                    unlistedadditionbinding.btnUnlstsave.setEnabled(false);
                    JSONObject json = CommonUtilsMethods.CommonObjectParameter(this);
                    try {

                        SfName = SharedPref.getSfName(this);
                        DivCode = SharedPref.getDivisionCode(this);
                        json.put("tableName", "savenew_master");
                        if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                            SfCode = SharedPref.getHqCode(this);
                        }else {
                            SfCode = SharedPref.getSfCode(this);
                        }
                        json.put("sfcode", SfCode);
                        json.put("division_code", DivCode);
                        json.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32));
                        json.put("DeviceID", SharedPref.getDeviceId(UnlistedDoctorAddition.this));
                        json.put("DrName", unlistedadditionbinding.edtDctr.getText().toString());
                        json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(UnlistedDoctorAddition.this)));
                        json.put("DrqulNm", unlistedadditionbinding.txtSelectQua.getText().toString());
                        json.put("DrClsCd", String.valueOf(SharedPref.getSelectedClass(UnlistedDoctorAddition.this)));
                        json.put("DrClsNm", unlistedadditionbinding.txtSelectClass.getText().toString());
                        json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(UnlistedDoctorAddition.this)));
                        json.put("DrCatNm", unlistedadditionbinding.txtSelectCategory.getText().toString());
                        json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(UnlistedDoctorAddition.this)));
                        json.put("DrSpcNm", unlistedadditionbinding.txtSelectSpec.getText().toString());
                        json.put("DrAddr", unlistedadditionbinding.edtHomeaddr.getText().toString());
                        json.put("DrHospAddr", unlistedadditionbinding.edtHospaddr.getText().toString());
                        json.put("DrClusNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                        json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(UnlistedDoctorAddition.this)));
                        json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(UnlistedDoctorAddition.this)));
                        json.put("DrTerNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                        if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                            json.put("DrHQCd", String.valueOf(SharedPref.getHq(UnlistedDoctorAddition.this)));
                            json.put("DrHQNm", unlistedadditionbinding.txtSelectHq.getText().toString());
                        }else {
                            json.put("DrHQCd", SfCode);
                            json.put("DrHQNm", SfName);
                        }
                        json.put("key", SharedPref.getSaveLicenseSetting(UnlistedDoctorAddition.this));
                        json.put("DrType", "U");
                        if(!unlistedadditionbinding.edtDob.getText().toString().equalsIgnoreCase("")){
                            json.put("DrDOB", unlistedadditionbinding.edtDob.getText().toString() + " 00:00:00");
                        }
                        else{
                            json.put("DrDOB","-");
                        }
                        if(!unlistedadditionbinding.edtDob.getText().toString().equalsIgnoreCase("")){
                            json.put("DrDOW", unlistedadditionbinding.edtDow.getText().toString() + " 00:00:00");
                        }
                        else{
                            json.put("DrDOW","-");
                        }
                        json.put("DrPhone", unlistedadditionbinding.edtPhone.getText().toString());
                        json.put("DrMob", unlistedadditionbinding.edtMob.getText().toString());
                        json.put("imagePath", destinationFilePath);
                        json.put("imageName", imageName);

                        if(SharedPref.getGeoChk(this).equalsIgnoreCase("0") && !unlistedadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("")) {
                            gpsTrack = new GPSTrack(this);
                            latitude = gpsTrack.getLatitude();
                            longitude = gpsTrack.getLongitude();
                            json.put("DrLat", String.valueOf(latitude));
                            json.put("DrLong", String.valueOf(longitude));
                        }
                        else{
                            json.put("DrLat", "");
                            json.put("DrLong", "");
                        }
                        json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(UnlistedDoctorAddition.this));
                        Log.v("printing_add_dr", json.toString());
                        unlistedadditionbinding.btnUnlstsave.setEnabled(false);
                        addDoctor(json.toString());
                    } catch (Exception e) {
                        unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }else if(unlistedadditionbinding.txtSelectTerritory.getText().toString().isEmpty()) {if(SharedPref.getClusterCap(this).isEmpty() || SharedPref.getClusterCap(this) == null) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.cluster));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }else {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + SharedPref.getClusterCap(this));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            }else if(unlistedadditionbinding.txtSelectSpec.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.speciality));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }else if(unlistedadditionbinding.txtSelectCategory.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.category));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }else if(unlistedadditionbinding.txtSelectClass.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.clases));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }else if(unlistedadditionbinding.txtSelectQua.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.qualifications));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }else if(!unlistedadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("") &&
                    TagImgNd.equalsIgnoreCase("0") && destinationFilePath.equalsIgnoreCase("")) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.Photo_mand));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);

            }else {
                Log.v("qualification_txt", "arent_empty");
                unlistedadditionbinding.btnUnlstsave.setEnabled(false);
                JSONObject json = CommonUtilsMethods.CommonObjectParameter(this);
                try {

                    SfName = SharedPref.getSfName(this);
                    DivCode = SharedPref.getDivisionCode(this);
                    json.put("tableName", "savenew_master");
                    if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                        SfCode = SharedPref.getHqCode(this);
                    }else {
                        SfCode = SharedPref.getSfCode(this);
                    }
                    json.put("sfcode", SfCode);
                    json.put("division_code", DivCode);
                    json.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32));
                    json.put("DeviceID", SharedPref.getDeviceId(UnlistedDoctorAddition.this));
                    json.put("DrName", unlistedadditionbinding.edtDctr.getText().toString());
                    json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(UnlistedDoctorAddition.this)));
                    json.put("DrqulNm", unlistedadditionbinding.txtSelectQua.getText().toString());
                    json.put("DrClsCd", String.valueOf(SharedPref.getSelectedClass(UnlistedDoctorAddition.this)));
                    json.put("DrClsNm", unlistedadditionbinding.txtSelectClass.getText().toString());
                    json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(UnlistedDoctorAddition.this)));
                    json.put("DrCatNm", unlistedadditionbinding.txtSelectCategory.getText().toString());
                    json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(UnlistedDoctorAddition.this)));
                    json.put("DrSpcNm", unlistedadditionbinding.txtSelectSpec.getText().toString());
                    json.put("DrAddr", unlistedadditionbinding.edtHomeaddr.getText().toString());
                    json.put("DrHospAddr", unlistedadditionbinding.edtHospaddr.getText().toString());
                    json.put("DrClusNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                    json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(UnlistedDoctorAddition.this)));
                    json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(UnlistedDoctorAddition.this)));
                    json.put("DrTerNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                    if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                        json.put("DrHQCd", String.valueOf(SharedPref.getHq(UnlistedDoctorAddition.this)));
                        json.put("DrHQNm", unlistedadditionbinding.txtSelectHq.getText().toString());
                    }else {
                        json.put("DrHQCd", SfCode);
                        json.put("DrHQNm", SfName);
                    }
                    json.put("key", SharedPref.getSaveLicenseSetting(UnlistedDoctorAddition.this));
                    json.put("DrType", "U");
                    if(!unlistedadditionbinding.edtDob.getText().toString().equalsIgnoreCase("")){
                        json.put("DrDOB", unlistedadditionbinding.edtDob.getText().toString() + " 00:00:00");
                    }
                    else{
                        json.put("DrDOB","");
                    }
                    if(!unlistedadditionbinding.edtDob.getText().toString().equalsIgnoreCase("")){
                        json.put("DrDOW", unlistedadditionbinding.edtDow.getText().toString() + " 00:00:00");
                    }
                    else{
                        json.put("DrDOW","");
                    }
                    json.put("DrPhone", unlistedadditionbinding.edtPhone.getText().toString());
                    json.put("DrMob", unlistedadditionbinding.edtMob.getText().toString());
                    json.put("imagePaths", destinationFilePath);
                    json.put("imageName", imageName);

                    if(SharedPref.getGeoChk(this).equalsIgnoreCase("0") && !unlistedadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("")) {
                        gpsTrack = new GPSTrack(this);
                        latitude = gpsTrack.getLatitude();
                        longitude = gpsTrack.getLongitude();
                        json.put("DrLat", String.valueOf(latitude));
                        json.put("DrLong", String.valueOf(longitude));
                    }
                    else{
                        json.put("DrLat", "");
                        json.put("DrLong", "");
                    }
                    json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(UnlistedDoctorAddition.this));
                    Log.v("printing_add_dr", json.toString());
                    unlistedadditionbinding.btnUnlstsave.setEnabled(false);
                    addDoctor(json.toString());
                } catch (Exception e) {
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                    e.printStackTrace();
                }
            }
        });
        unlistedadditionbinding.btnUnlstcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancel();
            }
        });
        unlistedadditionbinding.adddrClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancel();
            }
        });
        unlistedadditionbinding.dobCalendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogforDOB();
            }
        });
        unlistedadditionbinding.edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.edtDob.getWindowToken(), 0);
                }
                showDatePickerDialogforDOB();
            }
        });
        unlistedadditionbinding.edtPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)}); // Max length 13

        unlistedadditionbinding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0 && s.length()<7) {
                    // Show error only if input is between 1 and 6 characters
                    unlistedadditionbinding.edtPhone.setError(getResources().getString(R.string.enter_valid_phone));
                }else {
                    // Remove error when field is empty or valid
                    unlistedadditionbinding.edtPhone.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        unlistedadditionbinding.edtMob.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)}); // Max length 13

        unlistedadditionbinding.edtMob.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0 && s.length()<7) {
                    // Show error only if input is between 1 and 6 characters
                    unlistedadditionbinding.edtMob.setError(getResources().getString(R.string.enter_valid_Mobile));
                }else {
                    // Remove error when field is empty or valid
                    unlistedadditionbinding.edtMob.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        unlistedadditionbinding.dowCalenderIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogforDOW();
            }
        });
        unlistedadditionbinding.edtDow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.edtDow.getWindowToken(), 0);
                }
                showDatePickerDialogforDOW();
            }
        });
        // OnClickListener for image capture
        unlistedadditionbinding.dctrimage.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
            }else {
                captureFile();
            }
        });

        unlistedadditionbinding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UtilityClass.isNetworkAvailable(UnlistedDoctorAddition.this)) {
                    Intent intent = new Intent(UnlistedDoctorAddition.this, MapsAddition.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Additionfrom", "U");
                    UnlistedDoctorAddition.this.startActivity(intent);

                }else {
                    commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, UnlistedDoctorAddition.this.getString(R.string.no_network));
                }
            }

        });

        unlistedadditionbinding.txtSelectQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectQua.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectQuali.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectCategory.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectCat.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectClass.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectClass.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectSpec.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectSpeciality.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectTerritory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectTerritory.getWindowToken(), 0);
                }
                if(SfType.equalsIgnoreCase("2")) {
                    if(unlistedadditionbinding.txtSelectHq.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getResources().getString(R.string.select_headquater));
                    }else {
                        unlistedadditionbinding.fragmentSelectCluster.setVisibility(View.VISIBLE);
                    }
                }else {
                    unlistedadditionbinding.fragmentSelectCluster.setVisibility(View.VISIBLE);
                }

            }
        });
        unlistedadditionbinding.txtSelectHq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectHq.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectHq.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.imgClearTag.setOnClickListener(view -> {
            unlistedadditionbinding.edtGeotagaddr.setText("");
            unlistedadditionbinding.layout7.setVisibility(View.GONE);
            destinationFilePath = "";
            imageName = "";
            unlistedadditionbinding.dctrimage.setImageBitmap(null);
            unlistedadditionbinding.dctrimage.setImageResource(R.drawable.ic_camera);
            unlistedadditionbinding.dctrimage.setOnClickListener(v -> {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
                }else {
                    captureFile();
                }
            });
            view.setVisibility(View.GONE);
        });

    }

    public static void setAddressText(String addressText) {
        unlistedadditionbinding.edtGeotagaddr.setText(addressText); // Set the text on the EditText in ClassB
        unlistedadditionbinding.imgClearTag.setVisibility(View.VISIBLE);
        if(TagImgNd.equalsIgnoreCase("0")) {
            unlistedadditionbinding.layout7.setVisibility(View.VISIBLE);
        }else {
            unlistedadditionbinding.layout7.setVisibility(View.GONE);
        }
    }

    public void commonFun() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } catch (Exception e) {

        }
    }

    public void addDoctor(String val) {
        try {
            if(progressDialog == null) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(this);
                progressDialog = CommonUtilsMethods.createProgressDialog(this);
                progressDialog.show();
            }else {
                progressDialog.show();
            }

            if(isNetworkConnected()) {
                String baseUrl = SharedPref.getBaseWebUrl(this);
                String pathUrl = SharedPref.getPhpPathUrl(this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                Log.e("test", "login url : " + baseUrl + replacedUrl);
                apiInterface = RetrofitClient.getRetrofit(this, baseUrl + replacedUrl);
                Log.d("save_obj", String.valueOf(val));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/masterdata");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(UnlistedDoctorAddition.this), mapString, val);

                if(call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if(response.isSuccessful()) {
                                progressDialog.dismiss();
                                try {
                                    assert response.body() != null;
                                    JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                                    if(jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                        Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                        if(SharedPref.getSfType(UnlistedDoctorAddition.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(UnlistedDoctorAddition.this).equalsIgnoreCase("0")) {
                                            SfCode = SharedPref.getHq(UnlistedDoctorAddition.this);
                                        }else if(SharedPref.getSfType(UnlistedDoctorAddition.this).equalsIgnoreCase("2")) {
                                            SfCode = SharedPref.getHqCode(UnlistedDoctorAddition.this);
                                        }else {
                                            SfCode = SharedPref.getSfCode(UnlistedDoctorAddition.this);
                                        }
                                        SyncUnlisted(SfCode);
                                        //loadFragment(new UnlistedDoctorFragment());
                                        if(SharedPref.getGeotagImg(UnlistedDoctorAddition.this).equalsIgnoreCase("0")) {
                                            if(!imageName.equalsIgnoreCase("")) {
                                                JSONObject jsonImage = CommonUtilsMethods.CommonObjectParameter(UnlistedDoctorAddition.this);
                                                try {
                                                    jsonImage.put("tableName", "imgupload");
                                                    jsonImage.put("sfcode", SfCode);
                                                    jsonImage.put("division_code", DivCode);
                                                    if(SfType.equalsIgnoreCase("1")) {
                                                        jsonImage.put("Rsf", SfCode);
                                                    }else {
                                                        jsonImage.put("Rsf", SharedPref.getHqCode(UnlistedDoctorAddition.this));
                                                    }
                                                } catch (Exception ignored) {

                                                }
                                                if(SharedPref.getS3BucketNeed(getApplicationContext()).equalsIgnoreCase("0")) {
                                                    tag_Image();
                                                    CallImageAPIS3(jsonImage.toString(), destinationFilePath);
                                                }else{
                                                    CallImageAPI(jsonImage.toString(),destinationFilePath);
                                                }
                                            }
                                        }
                                        commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getResources().getString(R.string.saved_successfully));
                                    }
                                } catch (Exception e) {
                                }
                            }else {
                                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                                progressDialog.dismiss();
                                commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getResources().getString(R.string.something_wrong));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getResources().getString(R.string.no_network));
                        }
                    });
                }
            }else {
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getResources().getString(R.string.no_network));
            }

        } catch (Exception e) {
            unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) UnlistedDoctorAddition.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void SyncUnlisted(String hqCode) {
        UnlistedModelArray.clear();
        UnlistedStatus = masterDataDao.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR_GEO + hqCode);
//        MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(UnlistedDoctorAddition.this), Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, UnlistedStatus, false);

        MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(UnlistedDoctorAddition.this), Constants.DOCTOR_MAS, "getunlisteddr_master", Constants.UNLISTED_DOCTOR_MAS + hqCode, UnlistedStatus, false);
        MasterSyncItemModel unListModel_geo = new MasterSyncItemModel(SharedPref.getUNLcap(UnlistedDoctorAddition.this), Constants.DOCTOR_MAS, "getunlisteddr_geo", Constants.UNLISTED_DOCTOR_GEO + hqCode, UnlistedStatus, false);

        UnlistedModelArray.add(unListModel);
        UnlistedModelArray.add(unListModel_geo);
        arrayForAdapter.clear();
        arrayForAdapter.addAll(UnlistedModelArray);
        populateAdapter(arrayForAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        try {
            for (int i = 0; i<masterSyncItemModels.size(); i++) {
                MasterSyncItemModel item = masterSyncItemModels.get(i);
                sync(item.getMasterOf(), item.getRemoteTableName(), UnlistedModelArray, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sync(String masterOf, String remoteTableName, ArrayList<MasterSyncItemModel> masterSyncItemModels, int position) {

        try {
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
            jsonObject.put("tableName", remoteTableName);
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SfCode);
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            Map<String, String> mapString = new HashMap<>();
            Log.e("API Object", "master sync obj : " + jsonObject);
            Call<JsonElement> call = null;
            if(masterOf.equalsIgnoreCase(Constants.DOCTOR_MAS)) {
                mapString.put("axn", "table/dcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
            }
            if(call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        // masterSyncItemModels.get(position).setPBarVisibility(false);
                        Log.e("response :   ", remoteTableName + " : " + response.body().toString());
                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject2 = new JSONObject();
                        if(response.isSuccessful()) {
                            Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                            try {
                                JsonElement jsonElement = response.body();
                                if(!jsonElement.isJsonNull()) {
                                    if(jsonElement.isJsonArray()) {
                                        jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                        success = true;
                                    }else if(jsonElement.isJsonObject()) {
                                        jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                        if(!jsonObject2.has("success")) {
                                            // response as jsonObject with {"success" : "fail" } will be received only when there are unformed object passed or there are no data in back end.
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                            finish();
                                        }else if(jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1); // only update sync status and no need to overwrite previously saved data when failed
                                            masterSyncItemModels.get(position).setSyncSuccess(1);
                                        }
                                    }

                                    if(success) {
                                        masterSyncItemModels.get(position).setCount(jsonArray.length());
                                        masterSyncItemModels.get(position).setSyncSuccess(2);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(), 2));
                                        Intent resultIntent = new Intent();
                                        setResult(Activity.RESULT_OK, resultIntent);
                                        finish(); // Close the activity
                                    }
                                    //SetupAdapter();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    }
                });
            }
        } catch (Exception e) {
            Log.v("masterCheck", "--error-" + e);
            e.printStackTrace();
        }

    }

    private void showDatePickerDialogforDOB() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the calendar
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date as "1970-01-01 00:00:00"
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = formatter.format(calendar.getTime());

                    // Set the formatted date to the TextView
                    unlistedadditionbinding.edtDob.setText(formattedDate);
                },
                year, month, day
        );

        // Restrict future date selection
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showDatePickerDialogforDOW() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the calendar
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date as "1970-01-01 00:00:00"
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = formatter.format(calendar.getTime());

                    // Set the formatted date to the TextView
                    unlistedadditionbinding.edtDow.setText(formattedDate);
                },
                year, month, day
        );

        // Restrict future date selection
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean cameraPermission = result.getOrDefault(Manifest.permission.CAMERA, false);
                if(Boolean.TRUE.equals(cameraPermission)) {
                    captureFile();
                }else {
                    CommonUtilsMethods.RequestGPSPermission(UnlistedDoctorAddition.this, "Camera");

                }
            });

    public void captureFile() {
        imageName = usersfcode + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "")
                + CommonUtilsMethods.getCurrentInstance("HHmmss") + ".jpeg";

        File directory = new File(getExternalFilesDir(null), "AdditionTagged");
        if(!directory.exists() && !directory.mkdirs()) {
            Log.e("File Creation", "Directory Creation Failed.");
            return;
        }

        File destinationFile = new File(directory, imageName);
        try {
            if(!destinationFile.createNewFile()) {
                Log.e("File Creation", "Destination File Creation Failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        destinationFilePath = destinationFile.getAbsolutePath();

        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("FILE_PATH", destinationFilePath);
        intent.putExtra("FROM", "UnlistedAddition");
        intent.putExtra("L_FLAG", SharedPref.getGeoChk(this).equalsIgnoreCase("0"));
        intent.putExtra("CAMERA_MODE", "ALL");

        someActivityResultLauncher.launch(intent);
    }

    // Initialize ActivityResultLauncher
    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap photo = BitmapFactory.decodeFile(destinationFilePath);
                        if(photo == null) return;

                        // Set captured image to ImageView
                        unlistedadditionbinding.dctrimage.setImageBitmap(photo);
                        unlistedadditionbinding.dctrimage.setTag(destinationFilePath);

                        // Set click listener to show image popup
                        unlistedadditionbinding.dctrimage.setOnClickListener(view -> showImagePopup(destinationFilePath));

                    }else if(result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.d("Camera", "onActivityResult: Canceled");
                        destinationFilePath = "";
                    }
                } catch (Exception e) {
                    Log.e("Camera", "onActivityResult Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
    );

    private void showImagePopup(String imagePath) {
        if(imagePath == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fullscreen_image, null);

        ImageView fullScreenImage = dialogView.findViewById(R.id.fullscreen_image);
        ImageButton closeButton = dialogView.findViewById(R.id.close_button);

        fullScreenImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.5), // 80% of screen width
                (int) (getResources().getDisplayMetrics().heightPixels * 0.9) // 60% of screen height
        );
        // Move popup slightly downwards
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.TOP;  // Aligns the popup to the top
        params.y = 150;  // Moves it 150 pixels downward (adjust as needed)
        dialog.getWindow().setAttributes(params);
        closeButton.setOnClickListener(v -> dialog.dismiss()); // Close popup when clicked
    }

    private void CallImageAPI(String jsonImage,String file) {
        try {
            ApiInterface apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getTagApiImageUrl(getApplicationContext()));
            Call<JsonObject> callImage;
            HashMap<String, RequestBody> values = field(jsonImage);
            MultipartBody.Part img = convertImg("UploadImg", file);
            callImage = apiInterface.SaveImg(values, img);

            callImage.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    assert response.body() != null;
                    Log.v("img_tag", response + "---" + response.body() + "---" + response.message() + "---" + call);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonImgRes;
                            jsonImgRes = new JSONObject(response.body().toString());
                            Log.v("img_tag", jsonImgRes.getString("success"));
                            if (jsonImgRes.getString("success").equalsIgnoreCase("true")) {
                                //commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.tag_failed));
                            }
                        } catch (Exception e) {
                            Log.v("img_tag", e.toString());
                        }
                    } else {
//                         commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    //commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallImageAPIS3(String jsonImage, String file) {
        if(jsonImage != null) {

/*
            String accessKey = Keys.ACCESS_KEY;
            String secretKey = Keys.SECRET_KEY;
            Regions region = Regions.EU_NORTH_1;
            String bucketName = "san-edet";
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
            AmazonS3Client s3Client = new AmazonS3Client(credentials);
            s3Client.setRegion(Region.getRegion(region));*/

            util.getS3Client(getApplicationContext());
            String bucketName = "san-one";
            File fileToUpload = new File(destinationFilePath);
            Log.d("fileToUpload", "CallImageAPIS3: " + fileToUpload.getAbsolutePath());
            if(!fileToUpload.exists()) {
                Log.e("S3Upload", "File does not exist: " + destinationFilePath);
                commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, "File does not exist.");
                return;
            }

//            String fileKey = SharedPref.getDivisionCode(getApplicationContext()).replace(",","/")+"Tagging"+"/"+ fileToUpload.getName();
            String fileKey = "uploads/"+SharedPref.getDivisionSname(getApplicationContext())+SharedPref.getDivisionCode(getApplicationContext()).replace(",","/")+"Tagging"+"/"+ fileToUpload.getName();
            /*String upload_url = "https://" + "s3." + "eu-north-1." + "amazonaws.com/" + bucketName + "/" + fileKey;
            Log.i("s3url", "Uploading to S3: " + upload_url);*/

            TransferUtility transferUtility = TransferUtility.builder()
                    .context(getApplicationContext())
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .s3Client(util.getS3Client(getApplicationContext()))
                    .build();

            TransferObserver uploadObserver = transferUtility.upload(
                    bucketName,
                    fileKey,
                    fileToUpload);

            uploadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if(state == TransferState.COMPLETED) {
//                        Log.v("S3Upload", "Upload successful"+file);
                    }else if(state == TransferState.FAILED) {
                        Log.e("S3Upload", "Upload failed");
//                        commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getString(R.string.tag_failed));
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentDone = (int) ((bytesCurrent / (float) bytesTotal) * 100);
                    Log.d("S3Upload", "Progress: " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("S3Upload", "Error: " + ex.getMessage());

//                    commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, "Upload failed. Please try again.");
                }
            });

        }
    }

    public void tag_Image() {
        File imageFile = new File(destinationFilePath);
        if(imageFile != null) {
            Log.d("tag_Image", "imageFile: " + "the file exists" + imageFile);
        }else {
            Log.d("tag_Image", "imageFile: " + "the file do not exist");
        }
        new AWSBucketsTag(UnlistedDoctorAddition.this, imageName, imageFile, "");             //SharedPref.getDivisionName(UnlistedDoctorAddition.this)

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
            if(path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(getApplicationContext()).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            }else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);
        } catch (Exception ignored) {
        }
        return yy;
    }

    private void handleCancel() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(view12 -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        btn_no.setOnClickListener(view12 -> {
            dialog.dismiss();
        });

    }
}