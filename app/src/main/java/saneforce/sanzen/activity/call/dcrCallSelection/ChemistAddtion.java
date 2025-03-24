package saneforce.sanzen.activity.call.dcrCallSelection;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
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
import java.util.Arrays;
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
import saneforce.sanzen.AWS.AWSBuckets;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.camera.CameraActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityChemistadditionBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ChemistAddtion extends AppCompatActivity {
    public static ActivityChemistadditionBinding chemistadditionbinding;
    CommonUtilsMethods commonUtilsMethods;
    String SfType = "", SfCode = "", SfName = "", DivCode = "", terrname = "", terrcode = "",usersfcode="";
    public static String filePath = "";
    public String  imageName = "";
    int imgindx = 0;
    private String destinationFilePath;
    String txt_qua = "", txt_cat = "", txt_class = "", txt_spec = "", txt_terr = "",txt_hq="";
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    double latitude, longitude;
    GPSTrack gpsTrack;
    ArrayList<MasterSyncItemModel> ChemistModelArray = new ArrayList<>();
    int chemistStatus = 0,categoryStatus = 0;
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();
    int ChemistStatus = 0;
    MasterDataDao masterDataDao;
    private RoomDB roomDB;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private int currentImageIndex = 0;
    static String TagImgNd="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chemistadditionbinding = ActivityChemistadditionBinding.inflate(getLayoutInflater());
        setContentView(chemistadditionbinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        if (SharedPref.getChmCap(this).isEmpty() || SharedPref.getChmCap(this) == null) {
            chemistadditionbinding.chmtagname.setText(getResources().getString(R.string.add) + " " + "Chemist");
        } else {
            chemistadditionbinding.chmtagname.setText(getResources().getString(R.string.add) + " " + SharedPref.getChmCap(this));
        }
        SfType = SharedPref.getSfType(this);
        usersfcode = SharedPref.getSfCode(this);
        TagImgNd=SharedPref.getGeotagImg(this);
        if(SfType.equalsIgnoreCase("1")){
            chemistadditionbinding.two.setVisibility(View.GONE);
            // Adjust weight of remaining layouts to take equal space
            LinearLayout.LayoutParams paramsOne = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            LinearLayout.LayoutParams paramsThree = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            paramsThree.setMargins(15, 0, 0, 0);
            chemistadditionbinding.one.setLayoutParams(paramsOne);
            chemistadditionbinding.three.setLayoutParams(paramsThree);
        }
        else{
            chemistadditionbinding.two.setVisibility(View.VISIBLE);
        }
        if(SharedPref.getGeoChk(this).equalsIgnoreCase("0")){
            chemistadditionbinding.layout6.setVisibility(View.VISIBLE);
        }
        else{
            chemistadditionbinding.layout6.setVisibility(View.GONE);
        }

        chemistadditionbinding.btnChmsave.setOnClickListener(v -> {
            chemistadditionbinding.btnChmsave.setEnabled(false);
            if(chemistadditionbinding.edtDctr.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.name));
                chemistadditionbinding.btnChmsave.setEnabled(true);
            }
            else if (chemistadditionbinding.edtDctr.getText().toString().contains("'")) {
                chemistadditionbinding.edtDctr.setError("Invalid Character");
                chemistadditionbinding.btnChmsave.setEnabled(true);
            }
            else if(SharedPref.getSfType(this).equalsIgnoreCase("2"))
            {
                if (chemistadditionbinding.txtSelectHq.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.headquarter));
                    chemistadditionbinding.btnChmsave.setEnabled(true);
                }
                else if(chemistadditionbinding.txtSelectTerritory.getText().toString().isEmpty())
                {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.territory));
                    chemistadditionbinding.btnChmsave.setEnabled(true);
                }
                else{
                    Log.v("qualification_txt", "arent_empty");
                    chemistadditionbinding.btnChmsave.setEnabled(false);
                    JSONObject json =CommonUtilsMethods.CommonObjectParameter(this);
                    try {

                        SfName = SharedPref.getSfName(this);
                        DivCode = SharedPref.getDivisionCode(this);
                        json.put("tableName", "savenew_master");
                        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                            SfCode = SharedPref.getHqCode(this);
                        }
                        else {
                            SfCode = SharedPref.getSfCode(this);
                        }
                        json.put("sfcode", SfCode);
                        json.put("division_code", DivCode);
                        json.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32));
                        json.put("DeviceID", SharedPref.getDeviceId(context));
                        json.put("DrName", chemistadditionbinding.edtDctr.getText().toString());
                        json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(context)));
                        json.put("DrqulNm", chemistadditionbinding.txtSelectQua.getText().toString());
                        json.put("DrClsCd", "");
                        json.put("DrClsNm","");
                        json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(context)));
                        json.put("DrCatNm", chemistadditionbinding.txtSelectCategory.getText().toString());
                        json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(context)));
                        json.put("DrSpcNm", chemistadditionbinding.txtSelectSpec.getText().toString());
                        json.put("DrAddr", chemistadditionbinding.edtHomeaddr.getText().toString());
                        json.put("DrHospAddr", chemistadditionbinding.edtHospaddr.getText().toString());
                        json.put("DrClusNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                        json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                        json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                        json.put("DrTerNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                            json.put("DrHQCd", String.valueOf(SharedPref.getHq(ChemistAddtion.this)));
                            json.put("DrHQNm", chemistadditionbinding.txtSelectHq.getText().toString());
                        }
                        else {
                            json.put("DrHQCd", SfCode);
                            json.put("DrHQNm", SfName);
                        }
                        json.put("key", SharedPref.getSaveLicenseSetting(context));
                        json.put("DrType", "C");
                        json.put("DrDOB", chemistadditionbinding.edtDob.getText().toString()+" 00:00:00");
                        json.put("DrDOW", chemistadditionbinding.edtDow.getText().toString()+" 00:00:00");
                        json.put("DrPhone", chemistadditionbinding.edtPhone.getText().toString());
                        json.put("DrMob", chemistadditionbinding.edtMob.getText().toString());
                        json.put("imagePath", destinationFilePath);
                        json.put("imageName", imageName);
                        gpsTrack = new GPSTrack(this);
                        latitude = gpsTrack.getLatitude();
                        longitude = gpsTrack.getLongitude();
                        json.put("DrLat", String.valueOf(latitude));
                        json.put("DrLong", String.valueOf(longitude));
                        json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(context));
                        Log.v("printing_add_dr", json.toString());
                        chemistadditionbinding.btnChmsave.setEnabled(false);
                        addChm(json.toString());
                    }
                    catch (Exception e) {
                        chemistadditionbinding.btnChmsave.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }
            else if(chemistadditionbinding.txtSelectTerritory.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.territory));
                chemistadditionbinding.btnChmsave.setEnabled(true);
            }

            else{
                Log.v("qualification_txt", "arent_empty");
                chemistadditionbinding.btnChmsave.setEnabled(false);
                JSONObject json =CommonUtilsMethods.CommonObjectParameter(this);
                try {

                    SfName = SharedPref.getSfName(this);
                    DivCode = SharedPref.getDivisionCode(this);
                    json.put("tableName", "savenew_master");
                    if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                        SfCode = SharedPref.getHqCode(this);
                    }
                    else {
                        SfCode = SharedPref.getSfCode(this);
                    }
                    json.put("sfcode", SfCode);
                    json.put("division_code", DivCode);
                    json.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32));
                    json.put("DeviceID", SharedPref.getDeviceId(context));
                    json.put("DrName", chemistadditionbinding.edtDctr.getText().toString());
                    json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(context)));
                    json.put("DrqulNm", chemistadditionbinding.txtSelectQua.getText().toString());
                    json.put("DrClsCd", "");
                    json.put("DrClsNm","");
                    json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(context)));
                    json.put("DrCatNm", chemistadditionbinding.txtSelectCategory.getText().toString());
                    json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(context)));
                    json.put("DrSpcNm", chemistadditionbinding.txtSelectSpec.getText().toString());
                    json.put("DrAddr", chemistadditionbinding.edtHomeaddr.getText().toString());
                    json.put("DrHospAddr", chemistadditionbinding.edtHospaddr.getText().toString());
                    json.put("DrClusNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                    json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                    json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                    json.put("DrTerNm", chemistadditionbinding.txtSelectTerritory.getText().toString());
                    if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                        json.put("DrHQCd", String.valueOf(SharedPref.getHq(ChemistAddtion.this)));
                        json.put("DrHQNm", chemistadditionbinding.txtSelectHq.getText().toString());
                    }
                    else {
                        json.put("DrHQCd", SfCode);
                        json.put("DrHQNm", SfName);
                    }
                    json.put("key", SharedPref.getSaveLicenseSetting(context));
                    json.put("DrType", "C");
                    json.put("DrDOB", chemistadditionbinding.edtDob.getText().toString()+" 00:00:00");
                    json.put("DrDOW", chemistadditionbinding.edtDow.getText().toString()+" 00:00:00");
                    json.put("DrPhone", chemistadditionbinding.edtPhone.getText().toString());
                    json.put("DrMob", chemistadditionbinding.edtMob.getText().toString());
                    json.put("imagePath", destinationFilePath);
                    json.put("imageName", imageName);
                    gpsTrack = new GPSTrack(this);
                    latitude = gpsTrack.getLatitude();
                    longitude = gpsTrack.getLongitude();
                    json.put("DrLat", String.valueOf(latitude));
                    json.put("DrLong", String.valueOf(longitude));
                    json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(context));
                    Log.v("printing_add_dr", json.toString());
                    chemistadditionbinding.btnChmsave.setEnabled(false);
                    addChm(json.toString());
                }
                catch (Exception e) {
                    chemistadditionbinding.btnChmsave.setEnabled(true);
                    e.printStackTrace();
                }
            }
        });
        chemistadditionbinding.btnChmcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChemistAddtion.this);
                alertDialogBuilder.setTitle("Warning!");
                alertDialogBuilder.setIcon(getDrawable(R.drawable.icon_sync_failed));
                alertDialogBuilder.setMessage("Are you sure, you want to cancel?");
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

                alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getOnBackPressedDispatcher().onBackPressed();
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton(android.R.string.no, null);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        chemistadditionbinding.addchmClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChemistAddtion.this);
                alertDialogBuilder.setTitle("Warning!");
                alertDialogBuilder.setIcon(getDrawable(R.drawable.icon_sync_failed));
                alertDialogBuilder.setMessage("Are you sure, you want to go back?");
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

                alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getOnBackPressedDispatcher().onBackPressed();
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton(android.R.string.no, null);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.edtDow.getWindowToken(), 0);
                }
                showDatePickerDialogforDOW();
            }
        });
        // OnClickListener for image capture
        chemistadditionbinding.dctrimage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
            } else {
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
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapsAddition.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Additionfrom", "C");
                    context.startActivity(intent);

                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                }
            }

        });

        chemistadditionbinding.txtSelectQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectQua.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChmquali.setVisibility(View.VISIBLE);
            }
        });

        chemistadditionbinding.txtSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectCategory.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChmcat.setVisibility(View.VISIBLE);
            }
        });



        chemistadditionbinding.txtSelectSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectSpec.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChmspeciality.setVisibility(View.VISIBLE);
            }
        });

        chemistadditionbinding.txtSelectTerritory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectTerritory.getWindowToken(), 0);
                }
                if(SfType.equalsIgnoreCase("2")){
                    if(chemistadditionbinding.txtSelectHq.getText().toString().equalsIgnoreCase("")){
                        commonUtilsMethods.showToastMessage(ChemistAddtion.this,getResources().getString(R.string.select_headquater));
                    }
                    else{
                        chemistadditionbinding.fragmentSelectChmcluster.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    chemistadditionbinding.fragmentSelectChmcluster.setVisibility(View.VISIBLE);
                }

            }
        });
        chemistadditionbinding.txtSelectHq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(chemistadditionbinding.txtSelectHq.getWindowToken(), 0);
                }
                chemistadditionbinding.fragmentSelectChemisthq.setVisibility(View.VISIBLE);
            }
        });

    }
    public static void setAddressText(String addressText) {
        chemistadditionbinding.edtGeotagaddr.setText(addressText); // Set the text on the EditText in ClassB
        if(TagImgNd.equalsIgnoreCase("0")){
            chemistadditionbinding.layout7.setVisibility(View.VISIBLE);
        }
        else{
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
            if (progressDialog == null) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(this);
                progressDialog = CommonUtilsMethods.createProgressDialog(this);
                progressDialog.show();
            } else {
                progressDialog.show();
            }

            if (isNetworkConnected()) {
                String baseUrl = SharedPref.getBaseWebUrl(this);
                String pathUrl = SharedPref.getPhpPathUrl(this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                Log.e("test", "login url : " + baseUrl + replacedUrl);
                apiInterface = RetrofitClient.getRetrofit(this, baseUrl + replacedUrl);
                Log.d("save_obj", String.valueOf(val));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/masterdata");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, val);

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                progressDialog.dismiss();
                                try {
                                    assert response.body() != null;
                                    JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                                    if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                        Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                        if(SharedPref.getSfType(ChemistAddtion.this).equalsIgnoreCase("2")){
                                            SfCode = SharedPref.getHqCode(ChemistAddtion.this);}
                                        else {
                                            SfCode = SharedPref.getSfCode(ChemistAddtion.this);
                                        }
                                        SyncChemist(SfCode);
                                        if(SharedPref.getGeotagImg(ChemistAddtion.this).equalsIgnoreCase("0")) {
                                            if(!imageName.equalsIgnoreCase("")) {
                                                JSONObject jsonImage = CommonUtilsMethods.CommonObjectParameter(ChemistAddtion.this);
                                                try {
                                                    jsonImage.put("tableName", "imgupload");
                                                    jsonImage.put("sfcode", SfCode);
                                                    jsonImage.put("division_code", DivCode);
                                                    if (SfType.equalsIgnoreCase("1")) {
                                                        jsonImage.put("Rsf", SfCode);
                                                    } else {
                                                        jsonImage.put("Rsf", SharedPref.getHqCode(context));
                                                    }
                                                } catch (Exception ignored) {
                                                }
                                                tag_Image();
                                                CallImageAPI(jsonImage.toString(), destinationFilePath);

                                            }


                                        }
//                                        getOnBackPressedDispatcher().onBackPressed();
//                                        finish();
                                    }
                                } catch (Exception e) {
                                }
                            }
                            else{
                                chemistadditionbinding.btnChmsave.setEnabled(true);
                                progressDialog.dismiss();
                                commonUtilsMethods.showToastMessage(ChemistAddtion.this, getResources().getString(R.string.something_wrong));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            chemistadditionbinding.btnChmsave.setEnabled(true);
                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(ChemistAddtion.this, getResources().getString(R.string.no_network));
                        }
                    });
                }
            }
            else {
                chemistadditionbinding.btnChmsave.setEnabled(true);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(ChemistAddtion.this, getResources().getString(R.string.no_network));
            }

        }
        catch (Exception e) {
            chemistadditionbinding.btnChmsave.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) ChemistAddtion.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public void SyncChemist(String hqCode) {
        ChemistModelArray.clear();
        chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST + hqCode);
        categoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY);
        MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(this),  Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode, chemistStatus, false);
        MasterSyncItemModel chemistCategory = new MasterSyncItemModel(Constants.CATEGORY,  Constants.DOCTOR, "getchem_categorys", Constants.CATEGORY_CHEMIST, categoryStatus, false);
        ChemistModelArray.add(cheModel);
        ChemistModelArray.add(chemistCategory);
        arrayForAdapter.clear();
        arrayForAdapter.addAll(ChemistModelArray);
        populateAdapter(arrayForAdapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        try{
            for (int i = 0; i < masterSyncItemModels.size(); i++) {
                MasterSyncItemModel item = masterSyncItemModels.get(i);
                sync(item.getMasterOf(), item.getRemoteTableName(), ChemistModelArray, i);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void sync(String masterOf, String remoteTableName, ArrayList<MasterSyncItemModel> masterSyncItemModels, int position) {

        try {
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(this);
            jsonObject.put("tableName", remoteTableName);
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SfCode);
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            Map<String, String> mapString = new HashMap<>();
            Log.e("API Object", "master sync obj : " + jsonObject);
            Call<JsonElement> call = null;
            if (masterOf.equalsIgnoreCase(Constants.DOCTOR)) {
                mapString.put("axn", "table/dcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
            }
            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        // masterSyncItemModels.get(position).setPBarVisibility(false);
                        Log.e("response :   ",  remoteTableName + " : " + response.body().toString());
                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject2=new JSONObject();
                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                        if (!jsonObject2.has("success")) {
                                            // response as jsonObject with {"success" : "fail" } will be received only when there are unformed object passed or there are no data in back end.
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                            finish();
                                        }
                                        else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1); // only update sync status and no need to overwrite previously saved data when failed
                                            masterSyncItemModels.get(position).setSyncSuccess(1);
                                        }
                                    }

                                    if (success) {
                                        masterSyncItemModels.get(position).setCount(jsonArray.length());
                                        masterSyncItemModels.get(position).setSyncSuccess(2);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(), 2));
                                        new Handler().postDelayed(() -> {
                                            commonUtilsMethods.showToastMessage(ChemistAddtion.this, getResources().getString(R.string.saved_successfully));
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
                if (Boolean.TRUE.equals(cameraPermission)) {
                    captureFile();
                } else {
                    CommonUtilsMethods. RequestGPSPermission(ChemistAddtion.this,"Camera");

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
//        File file = new File(context.getExternalFilesDir(null) + "/AdditionTagged/");
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
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("File Creation", "Directory Creation Failed.");
            return;
        }

        File destinationFile = new File(directory, imageName);
        try {
            if (!destinationFile.createNewFile()) {
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
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap photo = BitmapFactory.decodeFile(destinationFilePath);
                        if (photo == null) return;

                        // Set captured image to ImageView
                        chemistadditionbinding.dctrimage.setImageBitmap(photo);
                        chemistadditionbinding.dctrimage.setTag(destinationFilePath);

                        // Set click listener to show image popup
                        chemistadditionbinding.dctrimage.setOnClickListener(view -> showImagePopup(destinationFilePath));

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.d("Camera", "onActivityResult: Canceled");
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
        if (imagePath == null) return;

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
    //    private void CallImageAPI(String jsonImage,String file) {
//        try {
//            ApiInterface apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getTagApiImageUrl(getApplicationContext()));
//            Call<JsonObject> callImage;
//            HashMap<String, RequestBody> values = field(jsonImage);
//            MultipartBody.Part img = convertImg("UploadImg", file);
//            callImage = apiInterface.SaveImg(values, img);
//
//            callImage.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                    assert response.body() != null;
//                    Log.v("img_tag", response + "---" + response.body() + "---" + response.message() + "---" + call);
//                    if (response.isSuccessful()) {
//                        try {
//                            JSONObject jsonImgRes;
//                            jsonImgRes = new JSONObject(response.body().toString());
//                            Log.v("img_tag", jsonImgRes.getString("success"));
//                            if (jsonImgRes.getString("success").equalsIgnoreCase("true")) {
//                                //commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.tag_failed));
//                            }
//                        } catch (Exception e) {
//                            Log.v("img_tag", e.toString());
//                        }
//                    } else {
//
//                        // commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                    //commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
//
//                }
//            });
//        } catch (Exception e) {
//
//        }
//    }
    private void CallImageAPI(String jsonImage,String file) {
        if(jsonImage != null) {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "ap-south-1:c4c0fc81-118d-43e3-84cf-051f1bd831b9", Regions.AP_SOUTH_1);
            AmazonS3Client S3Client = new AmazonS3Client(credentialsProvider);
            File fileToUpload = new File(destinationFilePath);
            Log.d("fileToUpload", "CallImageAPI: "+ fileToUpload.getAbsolutePath());
            if (!fileToUpload.exists()) {
                Log.e("S3Upload", "File does not exist: " + destinationFilePath);
                commonUtilsMethods.showToastMessage(ChemistAddtion.this, "File does not exist.");
                return;
            }
            String bucketName = "san.one";
            String fileKey = "uploads/" + fileToUpload.getName();
            String upload_url = "https://"+"s3."+"ap-south-1."+"amazonaws.com/"+bucketName+"/"+fileKey ;
            Log.i("s3url", "Uploading to S3: " + upload_url);

            TransferUtility transferUtility = TransferUtility.builder()
                    .context(getApplicationContext())
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .s3Client(S3Client)
                    .build();

            TransferObserver uploadObserver = transferUtility.upload(
                    bucketName,
                    fileKey,
                    fileToUpload);

            uploadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        Log.v("S3Upload", "Upload successful"+file);
                    }else if (state == TransferState.FAILED) {
                        Log.e("S3Upload", "Upload failed");
                        commonUtilsMethods.showToastMessage(ChemistAddtion.this, getString(R.string.tag_failed));
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

                    commonUtilsMethods.showToastMessage(ChemistAddtion.this, "Upload failed. Please try again.");
                }
            });

        }
    }
    public void tag_Image() {
        File imageFile = new File(destinationFilePath);
        if(imageFile != null){
            Log.d("tag_Image", "imageFile: "+"the file exists"+imageFile);
        }else {
            Log.d("tag_Image", "imageFile: "+"the file do not exist");
        }
        new AWSBuckets(ChemistAddtion.this, imageName, imageFile, "");      //SharedPref.getDivisionName(ChemistAddtion.this)
        Log.d("tag_Image", "image" + imageFile);
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
        } catch (Exception ignored) {
        }
        return yy;
    }


}