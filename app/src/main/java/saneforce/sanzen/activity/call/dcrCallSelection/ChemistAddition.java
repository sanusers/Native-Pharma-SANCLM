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
import android.os.Handler;
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
import saneforce.sanzen.activity.map.MapsActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.Keys;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityChemistadditionBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ChemistAddition extends AppCompatActivity {
    public static ActivityChemistadditionBinding chemistadditionbinding;
    CommonUtilsMethods commonUtilsMethods;
    String SfType = "", SfCode = "", SfName = "", DivCode = "", terrname = "", terrcode = "", usersfcode = "";
    public static String filePath = "";
    String imageName = "";
    int imgindx = 0;
    private String destinationFilePath = "";
    String txt_qua = "", txt_cat = "", txt_class = "", txt_spec = "", txt_terr = "", txt_hq = "";
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    double latitude, longitude;
    GPSTrack gpsTrack;
    ArrayList<MasterSyncItemModel> ChemistModelArray = new ArrayList<>();
    int chemistStatus = 0, categoryStatus = 0;
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();
    int ChemistStatus = 0;
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

        chemistadditionbinding = ActivityChemistadditionBinding.inflate(getLayoutInflater());
        setContentView(chemistadditionbinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        util = new Util();
        masterDataDao = roomDB.masterDataDao();
        if(SharedPref.getChmCap(this).isEmpty() || SharedPref.getChmCap(this) == null) {
            chemistadditionbinding.chmtagname.setText(getResources().getString(R.string.add) + " " + "Chemist");
        }else {
            chemistadditionbinding.chmtagname.setText(getResources().getString(R.string.add) + " " + SharedPref.getChmCap(this));
        }
        String clusterCap = SharedPref.getClusterCap(this);
        Log.d("ClusterCap", "Value: " + clusterCap);
        String firstChar = "";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.name) + "</font>";
        chemistadditionbinding.txtDr.setText(Html.fromHtml(firstChar + firstChar2));
        firstChar = "<font color='#000000'>" + getResources().getString(R.string.headquarter) + "</font>";
        chemistadditionbinding.txtHq.setText(Html.fromHtml(firstChar + firstChar2));
        if(SharedPref.getClusterCap(this).isEmpty() || SharedPref.getClusterCap(this) == null) {
            firstChar = "<font color='#000000'>" + getResources().getString(R.string.cluster) + "</font>";
            chemistadditionbinding.txtTerritory.setHint(getResources().getString(R.string.select_cluster));
        }else {
            firstChar = "<font color='#000000'>" + SharedPref.getClusterCap(this) + "</font>";
            chemistadditionbinding.txtTerritory.setHint(getResources().getString(R.string.select) + " " + clusterCap);
        }
        chemistadditionbinding.txtTerritory.setText(Html.fromHtml(firstChar + firstChar2));

        SfType = SharedPref.getSfType(this);
        usersfcode = SharedPref.getSfCode(this);
        TagImgNd = SharedPref.getGeotagImg(this);
        if(SfType.equalsIgnoreCase("1")) {
            chemistadditionbinding.two.setVisibility(View.GONE);
            // Adjust weight of remaining layouts to take equal space
            LinearLayout.LayoutParams paramsOne = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            LinearLayout.LayoutParams paramsThree = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            paramsThree.setMargins(15, 0, 0, 0);
            chemistadditionbinding.one.setLayoutParams(paramsOne);
            chemistadditionbinding.three.setLayoutParams(paramsThree);
        }else {
            chemistadditionbinding.two.setVisibility(View.VISIBLE);
        }
        if(SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
            chemistadditionbinding.layout6.setVisibility(View.VISIBLE);
        }else {
            chemistadditionbinding.layout6.setVisibility(View.GONE);
        }

        chemistadditionbinding.btnChmsave.setOnClickListener(v -> {
            chemistadditionbinding.btnChmsave.setEnabled(false);
            if(chemistadditionbinding.edtDctr.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.name));
                chemistadditionbinding.btnChmsave.setEnabled(true);
            }else if(chemistadditionbinding.edtDctr.getText().toString().contains("'")) {
                chemistadditionbinding.edtDctr.setError("Invalid Character");
                chemistadditionbinding.btnChmsave.setEnabled(true);
            }else if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                if(chemistadditionbinding.txtSelectHq.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.headquarter));
                    chemistadditionbinding.btnChmsave.setEnabled(true);
                }else if(chemistadditionbinding.txtSelectTerritory.getText().toString().isEmpty()) {
                    if(SharedPref.getClusterCap(this).isEmpty() || SharedPref.getClusterCap(this) == null) {
                        commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.cluster));
                        chemistadditionbinding.btnChmsave.setEnabled(true);
                    }else {
                        commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + SharedPref.getClusterCap(this));
                        chemistadditionbinding.btnChmsave.setEnabled(true);
                    }
                }else if(!chemistadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("") &&
                        TagImgNd.equalsIgnoreCase("0") && destinationFilePath.equalsIgnoreCase("")) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.Photo_mand));
                    chemistadditionbinding.btnChmsave.setEnabled(true);

                }else {
                    Log.v("qualification_txt", "arent_empty");
                    chemistadditionbinding.btnChmsave.setEnabled(false);
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
                        json.put("DeviceID", SharedPref.getDeviceId(ChemistAddition.this));
                        json.put("DrName", chemistadditionbinding.edtDctr.getText().toString());
                        json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(ChemistAddition.this)));
                        json.put("DrqulNm", chemistadditionbinding.txtSelectQua.getText().toString());
                        json.put("DrClsCd", "");
                        json.put("DrClsNm", "");
                        json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(ChemistAddition.this)));
                        json.put("DrCatNm", chemistadditionbinding.txtSelectCategory.getText().toString());
                        json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(ChemistAddition.this)));
                        json.put("DrSpcNm", chemistadditionbinding.txtSelectSpec.getText().toString());
                        json.put("DrAddr", chemistadditionbinding.edtHomeaddr.getText().toString());
                        json.put("DrHospAddr", chemistadditionbinding.edtHospaddr.getText().toString());
                        json.put("DrClusNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                        json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(ChemistAddition.this)));
                        json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(ChemistAddition.this)));
                        json.put("DrTerNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                        if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                            json.put("DrHQCd", String.valueOf(SharedPref.getHq(ChemistAddition.this)));
                            json.put("DrHQNm", chemistadditionbinding.txtSelectHq.getText().toString());
                        }else {
                            json.put("DrHQCd", SfCode);
                            json.put("DrHQNm", SfName);
                        }
                        json.put("key", SharedPref.getSaveLicenseSetting(ChemistAddition.this));
                        json.put("DrType", "C");
                        json.put("DrDOB", chemistadditionbinding.edtDob.getText().toString() + " 00:00:00");
                        json.put("DrDOW", chemistadditionbinding.edtDow.getText().toString() + " 00:00:00");
                        json.put("DrPhone", chemistadditionbinding.edtPhone.getText().toString());
                        json.put("DrMob", chemistadditionbinding.edtMob.getText().toString());
                        json.put("imagePath", destinationFilePath);
                        json.put("imageName", imageName);

                        if(SharedPref.getGeoChk(this).equalsIgnoreCase("0") && !chemistadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("")) {
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
                        json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(ChemistAddition.this));
                        Log.v("printing_add_dr", json.toString());
                        chemistadditionbinding.btnChmsave.setEnabled(false);
                        addChm(json.toString());
                    } catch (Exception e) {
                        chemistadditionbinding.btnChmsave.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }else if(chemistadditionbinding.txtSelectTerritory.getText().toString().isEmpty()) {
                if(SharedPref.getClusterCap(this).isEmpty() || SharedPref.getClusterCap(this) == null) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.cluster));
                    chemistadditionbinding.btnChmsave.setEnabled(true);
                }else {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + SharedPref.getClusterCap(this));
                    chemistadditionbinding.btnChmsave.setEnabled(true);
                }
            }else if(!chemistadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("") &&
                    TagImgNd.equalsIgnoreCase("0") && destinationFilePath.equalsIgnoreCase("")) {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.Photo_mand));
                chemistadditionbinding.btnChmsave.setEnabled(true);

            }else {
                Log.v("qualification_txt", "arent_empty");
                chemistadditionbinding.btnChmsave.setEnabled(false);
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
                    json.put("DeviceID", SharedPref.getDeviceId(ChemistAddition.this));
                    json.put("DrName", chemistadditionbinding.edtDctr.getText().toString());
                    json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(ChemistAddition.this)));
                    json.put("DrqulNm", chemistadditionbinding.txtSelectQua.getText().toString());
                    json.put("DrClsCd", "");
                    json.put("DrClsNm", "");
                    json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(ChemistAddition.this)));
                    json.put("DrCatNm", chemistadditionbinding.txtSelectCategory.getText().toString());
                    json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(ChemistAddition.this)));
                    json.put("DrSpcNm", chemistadditionbinding.txtSelectSpec.getText().toString());
                    json.put("DrAddr", chemistadditionbinding.edtHomeaddr.getText().toString());
                    json.put("DrHospAddr", chemistadditionbinding.edtHospaddr.getText().toString());
                    json.put("DrClusNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                    json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(ChemistAddition.this)));
                    json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(ChemistAddition.this)));
                    json.put("DrTerNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                    if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                        json.put("DrHQCd", String.valueOf(SharedPref.getHq(ChemistAddition.this)));
                        json.put("DrHQNm", chemistadditionbinding.txtSelectHq.getText().toString());
                    }else {
                        json.put("DrHQCd", SfCode);
                        json.put("DrHQNm", SfName);
                    }
                    json.put("key", SharedPref.getSaveLicenseSetting(ChemistAddition.this));
                    json.put("DrType", "C");
                    json.put("DrDOB", chemistadditionbinding.edtDob.getText().toString() + " 00:00:00");
                    json.put("DrDOW", chemistadditionbinding.edtDow.getText().toString() + " 00:00:00");
                    json.put("DrPhone", chemistadditionbinding.edtPhone.getText().toString());
                    json.put("DrMob", chemistadditionbinding.edtMob.getText().toString());
                    json.put("imagePath", destinationFilePath);
                    json.put("imageName", imageName);

                    if(SharedPref.getGeoChk(this).equalsIgnoreCase("0") && !chemistadditionbinding.edtGeotagaddr.getText().toString().equalsIgnoreCase("")) {
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
                    json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(ChemistAddition.this));
                    Log.v("printing_add_dr", json.toString());
                    chemistadditionbinding.btnChmsave.setEnabled(false);
                    addChm(json.toString());
                } catch (Exception e) {
                    chemistadditionbinding.btnChmsave.setEnabled(true);
                    e.printStackTrace();
                }
            }
        });
        chemistadditionbinding.btnChmcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancel();
            }
        });
        chemistadditionbinding.addchmClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancel();
            }
        });
        chemistadditionbinding.dobCalendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogforDOB();
            }
        });
        chemistadditionbinding.edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.edtDob.getWindowToken(), 0);
                }
                showDatePickerDialogforDOB();
            }
        });
        chemistadditionbinding.dowCalenderIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogforDOW();
            }
        });
        chemistadditionbinding.edtDow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.edtDow.getWindowToken(), 0);
                }
                showDatePickerDialogforDOW();
            }
        });
        chemistadditionbinding.edtPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)}); // Max length 13

        chemistadditionbinding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0 && s.length()<7) {
                    // Show error only if input is between 1 and 6 characters
                    chemistadditionbinding.edtPhone.setError(getResources().getString(R.string.enter_valid_phone));
                }else {
                    // Remove error when field is empty or valid
                    chemistadditionbinding.edtPhone.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        chemistadditionbinding.edtMob.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)}); // Max length 13

        chemistadditionbinding.edtMob.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0 && s.length()<7) {
                    // Show error only if input is between 1 and 6 characters
                    chemistadditionbinding.edtMob.setError(getResources().getString(R.string.enter_valid_Mobile));
                }else {
                    // Remove error when field is empty or valid
                    chemistadditionbinding.edtMob.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        // OnClickListener for image capture
        chemistadditionbinding.dctrimage.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
            }else {
                captureFile();
            }
        });
//        chemistadditionbinding.dctrimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(ChemistAddtion.this, Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED )
//                {
//                    requestMultiplePermissionsLauncher.launch(new String[]{
//                            Manifest.permission.CAMERA,});
//                } else {
//                    captureFile(0);
//
//                }
//            }
//        });
//        chemistadditionbinding.dctrimage1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(ChemistAddtion.this, Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED )
//                {
//                    requestMultiplePermissionsLauncher.launch(new String[]{
//                            Manifest.permission.CAMERA,});
//                } else {
//                    captureFile(1);
//
//                }
//            }
//
//        });
//        chemistadditionbinding.dctrimage2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(ChemistAddtion.this, Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED )
//                {
//                    requestMultiplePermissionsLauncher.launch(new String[]{
//                            Manifest.permission.CAMERA,});
//                } else {
//                    captureFile(2);
//
//                }
//            }
//
//        });
        chemistadditionbinding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UtilityClass.isNetworkAvailable(ChemistAddition.this)) {
                    Intent intent = new Intent(ChemistAddition.this, MapsAddition.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Additionfrom", "C");
                    ChemistAddition.this.startActivity(intent);

                }else {
                    commonUtilsMethods.showToastMessage(ChemistAddition.this, ChemistAddition.this.getString(R.string.no_network));
                }
            }

        });

        chemistadditionbinding.txtSelectQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectQua.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChmquali.setVisibility(View.VISIBLE);
            }
        });

        chemistadditionbinding.txtSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectCategory.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChmcat.setVisibility(View.VISIBLE);
            }
        });


        chemistadditionbinding.txtSelectSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectSpec.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChmspeciality.setVisibility(View.VISIBLE);
            }
        });

        chemistadditionbinding.txtSelectTerritory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectTerritory.getWindowToken(), 0);
                }
                if(SfType.equalsIgnoreCase("2")) {
                    if(chemistadditionbinding.txtSelectHq.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(ChemistAddition.this, getResources().getString(R.string.select_headquater));
                    }else {
                        chemistadditionbinding.fragmentSelectChmcluster.setVisibility(View.VISIBLE);
                    }
                }else {
                    chemistadditionbinding.fragmentSelectChmcluster.setVisibility(View.VISIBLE);
                }

            }
        });
        chemistadditionbinding.txtSelectHq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectHq.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChemisthq.setVisibility(View.VISIBLE);
            }
        });
        chemistadditionbinding.imgClearTag.setOnClickListener(view -> {
            chemistadditionbinding.edtGeotagaddr.setText("");
            chemistadditionbinding.layout7.setVisibility(View.GONE);
            destinationFilePath = "";
            imageName = "";
            chemistadditionbinding.dctrimage.setImageBitmap(null);
            chemistadditionbinding.dctrimage.setImageResource(R.drawable.ic_camera);
            chemistadditionbinding.dctrimage.setOnClickListener(v -> {
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
        chemistadditionbinding.edtGeotagaddr.setText(addressText); // Set the text on the EditText in ClassB
        chemistadditionbinding.imgClearTag.setVisibility(View.VISIBLE);
        if(TagImgNd.equalsIgnoreCase("0")) {
            chemistadditionbinding.layout7.setVisibility(View.VISIBLE);
        }else {
            chemistadditionbinding.layout7.setVisibility(View.GONE);
        }
    }

    public void commonFun() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } catch (Exception e) {

        }
    }

    public void addChm(String val) {
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
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(ChemistAddition.this), mapString, val);

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
                                        if(SharedPref.getSfType(ChemistAddition.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(ChemistAddition.this).equalsIgnoreCase("0")) {
                                            SfCode = SharedPref.getHq(ChemistAddition.this);
                                        }else if(SharedPref.getSfType(ChemistAddition.this).equalsIgnoreCase("2")) {
                                            SfCode = SharedPref.getHqCode(ChemistAddition.this);
                                        }else {
                                            SfCode = SharedPref.getSfCode(ChemistAddition.this);
                                        }
                                        SyncChemist(SfCode);
                                        if(SharedPref.getGeotagImg(ChemistAddition.this).equalsIgnoreCase("0")) {
                                            if(!imageName.equalsIgnoreCase("")) {
                                                JSONObject jsonImage = CommonUtilsMethods.CommonObjectParameter(ChemistAddition.this);
                                                try {
                                                    jsonImage.put("tableName", "imgupload");
                                                    jsonImage.put("sfcode", SfCode);
                                                    jsonImage.put("division_code", DivCode);
                                                    if(SfType.equalsIgnoreCase("1")) {
                                                        jsonImage.put("Rsf", SfCode);
                                                    }else {
                                                        jsonImage.put("Rsf", SharedPref.getHqCode(ChemistAddition.this));
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
                                        commonUtilsMethods.showToastMessage(ChemistAddition.this, getResources().getString(R.string.saved_successfully));

//                                        getOnBackPressedDispatcher().onBackPressed();
//                                        finish();
                                    }
                                } catch (Exception e) {
                                }
                            }else {
                                chemistadditionbinding.btnChmsave.setEnabled(true);
                                progressDialog.dismiss();
                                commonUtilsMethods.showToastMessage(ChemistAddition.this, getResources().getString(R.string.something_wrong));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            chemistadditionbinding.btnChmsave.setEnabled(true);
                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(ChemistAddition.this, getResources().getString(R.string.no_network));
                        }
                    });
                }
            }else {
                chemistadditionbinding.btnChmsave.setEnabled(true);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(ChemistAddition.this, getResources().getString(R.string.no_network));
            }

        } catch (Exception e) {
            chemistadditionbinding.btnChmsave.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) ChemistAddition.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void SyncChemist(String hqCode) {
        ChemistModelArray.clear();
//        chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST + hqCode);
        chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST_GEO + hqCode);
        categoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY);
//        MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(this), Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode, chemistStatus, false);
        MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(this), Constants.DOCTOR_MAS, "getchemist_master", Constants.CHEMIST_MAS + hqCode, chemistStatus, false);
        MasterSyncItemModel cheModel_geo = new MasterSyncItemModel(SharedPref.getChmCap(this), Constants.DOCTOR_MAS, "getchemist_geo", Constants.CHEMIST_GEO + hqCode, chemistStatus, false);

        MasterSyncItemModel chemistCategory = new MasterSyncItemModel(Constants.CATEGORY, Constants.DOCTOR_MAS, "getchem_categorys", Constants.CATEGORY_CHEMIST, categoryStatus, false);
        ChemistModelArray.add(cheModel);
        ChemistModelArray.add(cheModel_geo);
        ChemistModelArray.add(chemistCategory);
        arrayForAdapter.clear();
        arrayForAdapter.addAll(ChemistModelArray);
        populateAdapter(arrayForAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        try {
            for (int i = 0; i<masterSyncItemModels.size(); i++) {
                MasterSyncItemModel item = masterSyncItemModels.get(i);
                sync(item.getMasterOf(), item.getRemoteTableName(), ChemistModelArray, i);
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
                                        new Handler().postDelayed(() -> {
                                            commonUtilsMethods.showToastMessage(ChemistAddition.this, getResources().getString(R.string.saved_successfully));
                                            Intent resultIntent = new Intent();
                                            setResult(Activity.RESULT_OK, resultIntent);
                                            finish();
                                        }, 2000);
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
                    chemistadditionbinding.edtDob.setText(formattedDate);
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
                    chemistadditionbinding.edtDow.setText(formattedDate);
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
                    CommonUtilsMethods.RequestGPSPermission(ChemistAddition.this, "Camera");

                }
            });

    //    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
//                Boolean cameraPermission = result.getOrDefault(Manifest.permission.CAMERA, false);
//                if (Boolean.TRUE.equals(cameraPermission)) {
//                    captureFile(0);
//                } else {
//                    CommonUtilsMethods. RequestGPSPermission(ChemistAddtion.this,"Camera");
//
//                }
//            });
    // Method to capture images dynamically
//    public void captureFile(int imageIndex) {
//        String imageName = usersfcode + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "")
//                + CommonUtilsMethods.getCurrentInstance("HHmmss") + "_" + imageIndex + ".jpeg";
//        Intent intent = new Intent(ChemistAddtion.this, CameraActivity.class);
//        File file = new File(ChemistAddition.this.getExternalFilesDir(null) + "/AdditionTagged/");
//        if (!file.exists() && !file.mkdirs()) {
//            Log.e("File Creation", "Directory Creation Failed.");
//            return;
//        }
//        File destinationFile = new File(file, imageName);
//        try {
//            if (!destinationFile.createNewFile()) {
//                Log.e("File Creation", "Destination File Creation Failed.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        destinationFilePath= destinationFile.getAbsolutePath();
//        imgindx=imageIndex;
//        intent.putExtra("FILE_PATH", destinationFilePath);
//        intent.putExtra("FROM", "UnlistedAddition");
//        intent.putExtra("L_FLAG", "1");
//        intent.putExtra("CAMERA_MODE", "ALL");
//        intent.putExtra("IMAGE_INDEX", imageIndex);  // Pass the image index
//        someActivityResultLauncher.launch(intent);
//    }
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

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap photo = BitmapFactory.decodeFile(destinationFilePath);
                        if(photo == null) return;

                        // Set captured image to ImageView
                        chemistadditionbinding.dctrimage.setImageBitmap(photo);
                        chemistadditionbinding.dctrimage.setTag(destinationFilePath);

                        // Set click listener to show image popup
                        chemistadditionbinding.dctrimage.setOnClickListener(view -> showImagePopup(destinationFilePath));

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
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    try {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Bitmap photo = BitmapFactory.decodeFile(destinationFilePath);
//                            if (photo == null) return; // Prevent null image crash
//                            if (imagePaths.size() < 3) { // Allow max 3 images
//                                imagePaths.add(destinationFilePath);
//                            }
//                            rearrangeImages(); // Update UI dynamically
//                        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
//                            Log.d("Camera", "onActivityResult: Canceled");
//                        }
//                    } catch (Exception e) {
//                        Log.e("Camera", "onActivityResult: " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            }
//    );
//    private void deleteImage(int imageIndex) {
//        if (imageIndex >= imagePaths.size()) return; // Prevent out-of-bounds errors
//        imagePaths.remove(imageIndex);
//        rearrangeImages();
//    }
//    private void rearrangeImages() {
//        List<ImageView> imageViews = Arrays.asList(
//                chemistadditionbinding.dctrimage,
//                chemistadditionbinding.dctrimage1,
//                chemistadditionbinding.dctrimage2);
//
//        List<ImageView> deleteButtons = Arrays.asList(
//                chemistadditionbinding.delete,
//                chemistadditionbinding.delete1,
//                chemistadditionbinding.delete2);
//
//        // Clear all images but keep the buttons visible
//        for (int i = 0; i < imageViews.size(); i++) {
//            if (i < imagePaths.size()) {
//                Bitmap photo = BitmapFactory.decodeFile(imagePaths.get(i));
//                imageViews.get(i).setImageBitmap(photo);
//                imageViews.get(i).setTag(imagePaths.get(i));
//                imageViews.get(i).setVisibility(View.VISIBLE);
//                deleteButtons.get(i).setVisibility(View.VISIBLE);
//
//                int finalI = i;
//                deleteButtons.get(i).setOnClickListener(view -> deleteImage(finalI));
//                imageViews.get(i).setOnClickListener(view -> showImagePopup((String) view.getTag()));
//            } else {
//                imageViews.get(i).setImageResource(R.drawable.camera_icon);
//                imageViews.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
//                imageViews.get(i).setTag(null);
//                imageViews.get(i).setVisibility(i == imagePaths.size() ? View.VISIBLE : View.GONE);
//                deleteButtons.get(i).setVisibility(View.GONE);
//
//                int finalI = i;
//                imageViews.get(i).setOnClickListener(view -> captureFile(finalI));  // Ensure button is clickable
//            }
//        }
//    }

//    private void showImagePopup(String imagePath) {
//        if (imagePath == null) return;
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_fullscreen_image, null);
//
//        ImageView fullScreenImage = dialogView.findViewById(R.id.fullscreen_image);
//        ImageButton closeButton = dialogView.findViewById(R.id.close_button);
//
//        fullScreenImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
//
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        closeButton.setOnClickListener(v -> dialog.dismiss()); // Close popup when clicked
//    }

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


      /*      String accessKey = Keys.ACCESS_KEY;
            String secretKey = Keys.SECRET_KEY;
            Regions region = Regions.EU_NORTH_1;
            String bucketName = "san-edet";

            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);

            AmazonS3Client s3Client = new AmazonS3Client(credentials);
            s3Client.setRegion(Region.getRegion(region));*/

            util.getS3Client(getApplicationContext());
            String bucketName = "san-one";
            File fileToUpload = new File(destinationFilePath);
            Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
            if(!fileToUpload.exists()) {
                Log.e("S3Upload", "File does not exist: " + destinationFilePath);
                commonUtilsMethods.showToastMessage(ChemistAddition.this, "File does not exist.");
                return;
            }

            String fileKey = "uploads/"+SharedPref.getDivisionSname(getApplicationContext())+SharedPref.getDivisionCode(getApplicationContext()).replace(",","/")+"Tagging"+"/"+ fileToUpload.getName();

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
                        commonUtilsMethods.showToastMessage(ChemistAddition.this, getString(R.string.tag_failed));
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

                    commonUtilsMethods.showToastMessage(ChemistAddition.this, "Upload failed. Please try again.");
                }
            });

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

    public void tag_Image() {
        File imageFile = new File(destinationFilePath);
        if(imageFile != null) {
            Log.d("tag_Image", "imageFile: " + "the file exists" + imageFile);
        }else {
            Log.d("tag_Image", "imageFile: " + "the file do not exist");
        }
        new AWSBucketsTag(ChemistAddition.this, imageName, imageFile, "");
        Log.d("tag_Image", "image" + imageFile);
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