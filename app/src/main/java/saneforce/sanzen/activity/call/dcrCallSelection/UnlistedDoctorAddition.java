package saneforce.sanzen.activity.call.dcrCallSelection;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import static saneforce.sanzen.activity.call.DCRCallActivity.SfCode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
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
import saneforce.sanzen.AWS.S3DownloadFiles;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.jwOthers.AdapterCallCaptureImage;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ClusterFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.sanzen.activity.call.pojo.CallCaptureImageList;
import saneforce.sanzen.activity.camera.CameraActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
//import saneforce.sanzen.activity.homeScreen.adapters.AdapterPopupSpinnerSelection;
import saneforce.sanzen.activity.map.MapsActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
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
    String SfType = "", SfCode = "", SfName = "", DivCode = "", terrname = "", terrcode = "",usersfcode="";
    public static String filePath = "";
    public String imageName = "";
    public static GoogleMap mMap;
    int imgindx = 0;
    private static String destinationFilePath;
    String txt_qua = "", txt_cat = "", txt_class = "", txt_spec = "", txt_terr = "",txt_hq="";
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    double latitude, longitude;
    GPSTrack gpsTrack;
    ArrayList<MasterSyncItemModel> UnlistedModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();
    int UnlistedStatus = 0;
    MasterDataDao masterDataDao;
    private RoomDB roomDB;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private int currentImageIndex = 0;
    static String TagImgNd="";
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
        if (SharedPref.getUNLcap(this).isEmpty() || SharedPref.getUNLcap(this) == null) {
            unlistedadditionbinding.drtagname.setText(getResources().getString(R.string.add) + " " + "Unlisted Doctor");
        } else {
            unlistedadditionbinding.drtagname.setText(getResources().getString(R.string.add) + " " + SharedPref.getUNLcap(this));
        }
        SfType = SharedPref.getSfType(this);
        usersfcode = SharedPref.getSfCode(this);
        TagImgNd=SharedPref.getGeotagImg(this);
        if(SfType.equalsIgnoreCase("1")){
            unlistedadditionbinding.two.setVisibility(View.GONE);
            // Adjust weight of remaining layouts to take equal space
            LinearLayout.LayoutParams paramsOne = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            LinearLayout.LayoutParams paramsThree = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            paramsThree.setMargins(15, 0, 0, 0);
            unlistedadditionbinding.one.setLayoutParams(paramsOne);
            unlistedadditionbinding.three.setLayoutParams(paramsThree);
        }
        else{
            unlistedadditionbinding.two.setVisibility(View.VISIBLE);
        }
        if(SharedPref.getGeoChk(this).equalsIgnoreCase("0")){
            unlistedadditionbinding.layout6.setVisibility(View.VISIBLE);
        }
        else{
            unlistedadditionbinding.layout6.setVisibility(View.GONE);
        }

        unlistedadditionbinding.btnUnlstsave.setOnClickListener(v -> {
            unlistedadditionbinding.btnUnlstsave.setEnabled(false);
            if(unlistedadditionbinding.edtDctr.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.name));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            else if (unlistedadditionbinding.edtDctr.getText().toString().contains("'")) {
                unlistedadditionbinding.edtDctr.setError("Invalid Character");
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            else if(SharedPref.getSfType(this).equalsIgnoreCase("2"))
            {
                if (unlistedadditionbinding.txtSelectHq.getText().toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill) + " " + getResources().getString(R.string.headquarter));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }
                else if(unlistedadditionbinding.txtSelectTerritory.getText().toString().isEmpty())
                {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.territory));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }
                else if(unlistedadditionbinding.txtSelectSpec.getText().toString().isEmpty())
                {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.speciality));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }
                else if(unlistedadditionbinding.txtSelectCategory.getText().toString().isEmpty())
                {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.category));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }
                else if(unlistedadditionbinding.txtSelectClass.getText().toString().isEmpty())
                {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.clases));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }
                else if(unlistedadditionbinding.txtSelectQua.getText().toString().isEmpty())
                {
                    commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.qualifications));
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                }
                else{
                    Log.v("qualification_txt", "arent_empty");
                    unlistedadditionbinding.btnUnlstsave.setEnabled(false);
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
                        json.put("DrName", unlistedadditionbinding.edtDctr.getText().toString());
                        json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(context)));
                        json.put("DrqulNm", unlistedadditionbinding.txtSelectQua.getText().toString());
                        json.put("DrClsCd", String.valueOf(SharedPref.getSelectedClass(context)));
                        json.put("DrClsNm", unlistedadditionbinding.txtSelectClass.getText().toString());
                        json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(context)));
                        json.put("DrCatNm", unlistedadditionbinding.txtSelectCategory.getText().toString());
                        json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(context)));
                        json.put("DrSpcNm", unlistedadditionbinding.txtSelectSpec.getText().toString());
                        json.put("DrAddr", unlistedadditionbinding.edtHomeaddr.getText().toString());
                        json.put("DrHospAddr", unlistedadditionbinding.edtHospaddr.getText().toString());
                        json.put("DrClusNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                        json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                        json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                        json.put("DrTerNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                            json.put("DrHQCd", String.valueOf(SharedPref.getHq(UnlistedDoctorAddition.this)));
                            json.put("DrHQNm", unlistedadditionbinding.txtSelectHq.getText().toString());
                        }
                        else {
                            json.put("DrHQCd", SfCode);
                            json.put("DrHQNm", SfName);
                        }
                        json.put("key", SharedPref.getSaveLicenseSetting(context));
                        json.put("DrType", "U");
                        json.put("DrDOB", unlistedadditionbinding.edtDob.getText().toString()+ " 00:00:00");
                        json.put("DrDOW", unlistedadditionbinding.edtDow.getText().toString()+ " 00:00:00");
                        json.put("DrPhone", unlistedadditionbinding.edtPhone.getText().toString());
                        json.put("DrMob", unlistedadditionbinding.edtMob.getText().toString());
                        json.put("imagePath", destinationFilePath);
                        json.put("imageName", imageName);
                        gpsTrack = new GPSTrack(this);
                        latitude = gpsTrack.getLatitude();
                        longitude = gpsTrack.getLongitude();
                        json.put("DrLat", String.valueOf(latitude));
                        json.put("DrLong", String.valueOf(longitude));
                        json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(context));
                        Log.v("printing_add_dr", json.toString());
                        unlistedadditionbinding.btnUnlstsave.setEnabled(false);
                        addDoctor(json.toString());
                    }
                    catch (Exception e) {
                        unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }
            else if(unlistedadditionbinding.txtSelectTerritory.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.territory));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            else if(unlistedadditionbinding.txtSelectSpec.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.speciality));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            else if(unlistedadditionbinding.txtSelectCategory.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.category));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            else if(unlistedadditionbinding.txtSelectClass.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.clases));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            else if(unlistedadditionbinding.txtSelectQua.getText().toString().isEmpty())
            {
                commonUtilsMethods.showToastMessage(this, getResources().getString(R.string.fill)+" "+getResources().getString(R.string.qualifications));
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            }
            else{
                Log.v("qualification_txt", "arent_empty");
                unlistedadditionbinding.btnUnlstsave.setEnabled(false);
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
                    json.put("DrName", unlistedadditionbinding.edtDctr.getText().toString());
                    json.put("DrQulCd", String.valueOf(SharedPref.getSelectedQualification(context)));
                    json.put("DrqulNm", unlistedadditionbinding.txtSelectQua.getText().toString());
                    json.put("DrClsCd", String.valueOf(SharedPref.getSelectedClass(context)));
                    json.put("DrClsNm", unlistedadditionbinding.txtSelectClass.getText().toString());
                    json.put("DrCatCd", String.valueOf(SharedPref.getSelectedCategory(context)));
                    json.put("DrCatNm", unlistedadditionbinding.txtSelectCategory.getText().toString());
                    json.put("DrSpcCd", String.valueOf(SharedPref.getSelectedSpeciality(context)));
                    json.put("DrSpcNm", unlistedadditionbinding.txtSelectSpec.getText().toString());
                    json.put("DrAddr", unlistedadditionbinding.edtHomeaddr.getText().toString());
                    json.put("DrHospAddr", unlistedadditionbinding.edtHospaddr.getText().toString());
                    json.put("DrClusNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                    json.put("DrClusCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                    json.put("DrTerCd", String.valueOf(SharedPref.getSelectedCluster(context)));
                    json.put("DrTerNm", unlistedadditionbinding.txtSelectTerritory.getText().toString());
                    if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                        json.put("DrHQCd", String.valueOf(SharedPref.getHq(UnlistedDoctorAddition.this)));
                        json.put("DrHQNm", unlistedadditionbinding.txtSelectHq.getText().toString());
                    }
                    else {
                        json.put("DrHQCd", SfCode);
                        json.put("DrHQNm", SfName);
                    }
                    json.put("key", SharedPref.getSaveLicenseSetting(context));
                    json.put("DrType", "U");
                    json.put("DrDOB", unlistedadditionbinding.edtDob.getText().toString()+ " 00:00:00");
                    json.put("DrDOW", unlistedadditionbinding.edtDow.getText().toString()+ " 00:00:00");
                    json.put("DrPhone", unlistedadditionbinding.edtPhone.getText().toString());
                    json.put("DrMob", unlistedadditionbinding.edtMob.getText().toString());
                    json.put("imagePaths", destinationFilePath);
                    json.put("imageName", imageName);
                    gpsTrack = new GPSTrack(this);
                    latitude = gpsTrack.getLatitude();
                    longitude = gpsTrack.getLongitude();
                    json.put("DrLat", String.valueOf(latitude));
                    json.put("DrLong", String.valueOf(longitude));
                    json.put("DrLocAddr", SharedPref.getSaveTaggedAddress(context));
                    Log.v("printing_add_dr", json.toString());
                    unlistedadditionbinding.btnUnlstsave.setEnabled(false);
                    addDoctor(json.toString());
                }
                catch (Exception e) {
                    unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                    e.printStackTrace();
                }
            }
        });
        unlistedadditionbinding.btnUnlstcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UnlistedDoctorAddition.this);
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
        unlistedadditionbinding.adddrClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UnlistedDoctorAddition.this);
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
        unlistedadditionbinding.dobCalendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogforDOB();
            }
        });
        unlistedadditionbinding.edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.edtDob.getWindowToken(), 0);
                }
                showDatePickerDialogforDOB();
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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.edtDow.getWindowToken(), 0);
                }
                showDatePickerDialogforDOW();
            }
        });
        // OnClickListener for image capture
        unlistedadditionbinding.dctrimage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
            } else {
                captureFile();
            }
        });
//        unlistedadditionbinding.dctrimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(UnlistedDoctorAddition.this, Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED )
//                {
//                    requestMultiplePermissionsLauncher.launch(new String[]{
//                            Manifest.permission.CAMERA,});
//                } else {
//                    captureFile();
//
//                }
//            }
//        });
//        unlistedadditionbinding.dctrimage1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(UnlistedDoctorAddition.this, Manifest.permission.CAMERA)
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
//        unlistedadditionbinding.dctrimage2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(UnlistedDoctorAddition.this, Manifest.permission.CAMERA)
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
        unlistedadditionbinding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapsAddition.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Additionfrom", "U");
                    context.startActivity(intent);

                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                }
            }

        });

        unlistedadditionbinding.txtSelectQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectQua.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectQuali.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectCategory.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectCat.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectClass.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectClass.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectSpec.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectSpeciality.setVisibility(View.VISIBLE);
            }
        });

        unlistedadditionbinding.txtSelectTerritory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectTerritory.getWindowToken(), 0);
                }
                if(SfType.equalsIgnoreCase("2")){
                    if(unlistedadditionbinding.txtSelectHq.getText().toString().equalsIgnoreCase("")){
                        commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this,getResources().getString(R.string.select_headquater));
                    }
                    else{
                        unlistedadditionbinding.fragmentSelectCluster.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    unlistedadditionbinding.fragmentSelectCluster.setVisibility(View.VISIBLE);
                }

            }
        });
        unlistedadditionbinding.txtSelectHq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(unlistedadditionbinding.txtSelectHq.getWindowToken(), 0);
                }
                unlistedadditionbinding.fragmentSelectHq.setVisibility(View.VISIBLE);
            }
        });

    }
    public static void setAddressText(String addressText) {
        unlistedadditionbinding.edtGeotagaddr.setText(addressText); // Set the text on the EditText in ClassB
        if(TagImgNd.equalsIgnoreCase("0")){
            unlistedadditionbinding.layout7.setVisibility(View.VISIBLE);
        }
        else{
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
                                        if(SharedPref.getSfType(UnlistedDoctorAddition.this).equalsIgnoreCase("2")){
                                            SfCode = SharedPref.getHqCode(UnlistedDoctorAddition.this);}
                                        else {
                                            SfCode = SharedPref.getSfCode(UnlistedDoctorAddition.this);
                                        }
                                        SyncUnlisted(SfCode);
                                        //loadFragment(new UnlistedDoctorFragment());
                                        if(SharedPref.getGeotagImg(UnlistedDoctorAddition.this).equalsIgnoreCase("0")) {
                                            if(!imageName.equalsIgnoreCase("")){
                                            JSONObject jsonImage = CommonUtilsMethods.CommonObjectParameter(UnlistedDoctorAddition.this);
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
                                        commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getResources().getString(R.string.saved_successfully));
                                    }
                                } catch (Exception e) {
                                }
                            }
                            else{
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
            }
            else {
                unlistedadditionbinding.btnUnlstsave.setEnabled(true);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, getResources().getString(R.string.no_network));
            }

        }
        catch (Exception e) {
            unlistedadditionbinding.btnUnlstsave.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_select_cluster, fragment);  // Replace another fragment
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) UnlistedDoctorAddition.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public void SyncUnlisted(String hqCode) {
        UnlistedModelArray.clear();
        UnlistedStatus = masterDataDao.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR + hqCode);
        MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(UnlistedDoctorAddition.this),  Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, UnlistedStatus, false);
        UnlistedModelArray.add(unListModel);
        arrayForAdapter.clear();
        arrayForAdapter.addAll(UnlistedModelArray);
        populateAdapter(arrayForAdapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        try{
            for (int i = 0; i < masterSyncItemModels.size(); i++) {
                MasterSyncItemModel item = masterSyncItemModels.get(i);
                sync(item.getMasterOf(), item.getRemoteTableName(), UnlistedModelArray, i);
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
                if (Boolean.TRUE.equals(cameraPermission)) {
                    captureFile();
                } else {
                    CommonUtilsMethods. RequestGPSPermission(UnlistedDoctorAddition.this,"Camera");

                }
            });
    // Method to capture images dynamically
//    public void captureFile(int imageIndex) {
//        String imageName = usersfcode + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "")
//                + CommonUtilsMethods.getCurrentInstance("HHmmss") + "_" + imageIndex + ".jpeg";
//        Intent intent = new Intent(UnlistedDoctorAddition.this, CameraActivity.class);
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
//        intent.putExtra("IMAGE_INDEX", imageIndex);  // Pass the image inde
//        someActivityResultLauncher.launch(intent);
//    }
// Capture File Method
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

    // Initialize ActivityResultLauncher
    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap photo = BitmapFactory.decodeFile(destinationFilePath);
                        if (photo == null) return;

                        // Set captured image to ImageView
                        unlistedadditionbinding.dctrimage.setImageBitmap(photo);
                        unlistedadditionbinding.dctrimage.setTag(destinationFilePath);

                        // Set click listener to show image popup
                        unlistedadditionbinding.dctrimage.setOnClickListener(view -> showImagePopup(destinationFilePath));

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
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
//                            //if (imagePaths.size() < 3) { // Allow max 3 images
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
//                unlistedadditionbinding.dctrimage,
//                unlistedadditionbinding.dctrimage1,
//                unlistedadditionbinding.dctrimage2);
//
//        List<ImageView> deleteButtons = Arrays.asList(
//                unlistedadditionbinding.delete,
//                unlistedadditionbinding.delete1,
//                unlistedadditionbinding.delete2);
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
//private void rearrangeImages() {
//        List<ImageView> imageViews = Arrays.asList(
//                unlistedadditionbinding.dctrimage,
//                unlistedadditionbinding.dctrimage1,
//                unlistedadditionbinding.dctrimage2);
//
//        List<ImageView> deleteButtons = Arrays.asList(
//                unlistedadditionbinding.delete,
//                unlistedadditionbinding.delete1,
//                unlistedadditionbinding.delete2);
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

  /*  private void CallImageAPI(String jsonImage,String file) {
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

                       // commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    //commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");

                }
            });
        } catch (Exception e) {

        }
    }*/

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
                commonUtilsMethods.showToastMessage(UnlistedDoctorAddition.this, "File does not exist.");
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
//                        Log.v("S3Upload", "Upload successful"+file);
                    }else if (state == TransferState.FAILED) {
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
        if(imageFile != null){
            Log.d("tag_Image", "imageFile: "+"the file exists"+imageFile);
        }else {
            Log.d("tag_Image", "imageFile: "+"the file do not exist");
        }
        new AWSBuckets(UnlistedDoctorAddition.this, imageName, imageFile, "");             //SharedPref.getDivisionName(UnlistedDoctorAddition.this)

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