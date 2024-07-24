package saneforce.sanzen.activity.map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static java.lang.Double.parseDouble;
import static java.lang.Double.valueOf;
import static saneforce.sanzen.activity.approvals.geotagging.GeoTaggingAdapter.geoTagViewList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.camera.CameraActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustListAdapter;
import saneforce.sanzen.activity.map.custSelection.TagCustSelectionList;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityMapsBinding;
import saneforce.sanzen.databinding.DialogMasterSyncUpdateBinding;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static ArrayList<ViewTagModel> list = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static ActivityMapsBinding mapsBinding;
    @SuppressLint("StaticFieldLeak")
    public static TaggingAdapter taggingAdapter;
    public static ArrayList<TaggedMapList> taggedMapListArrayList = new ArrayList<>();
    public static ViewTagModel mm = null;
    public static Marker marker;
    public static GoogleMap mMap;
    public static String SelectedTab="", SelectedHqCode="", SelectedHqName="";
    public static String from_tagging = "", GeoTagImageNeed = "", GeoTagApprovalNeed = "", TaggedLat, TaggedLng, TaggedAdd;
    public static boolean isTagged = false;
    public static ProgressDialog progressDialog = null;
    Uri outputFileUri;
    ApiInterface api_interface;
    Double distanceTag;
    LocationManager locationManager;
    GPSTrack gpsTrack;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    String cust_name, town_code, town_name, SfName, SfType, img_url, cust_address, SfCode, DivCode, Designation, StateCode, SubDivisionCode, cust_code, filePath = "", imageName = "", taggedTime = "",geoTagStatus="";
    double lat, lng, limitKm = 0.5;
    Dialog dialogTagCust;
    CommonUtilsMethods commonUtilsMethods;
    private String destinationFilePath;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    AlertDialog customDialog;
    String selectedTap;
    Button btn_confirm;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("SuspiciousIndentation")
        @Override
        public void onActivityResult(ActivityResult result) {
            try {
                if (result.getResultCode() == Activity.RESULT_OK) {
//                    String finalPath = "/storage/emulated/0";
//                    filePath = outputFileUri.getPath();
//                    filePath = Objects.requireNonNull(filePath).substring(1);
//                    filePath = finalPath + filePath.substring(filePath.indexOf("/"));
                    DisplayDialog();
                }
            } catch (Exception ignored) {

            }
        }
    });

    public static double milesToMeters(double miles) {
        return miles * 1609.344;
    }

    public static BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mapsBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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

    @SuppressLint({"UseCompatLoadingForDrawables", "PotentialBehaviorOverride"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapsBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(mapsBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(MapsActivity.this);
        commonUtilsMethods.setUpLanguage(MapsActivity.this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        Bundle extra = getIntent().getExtras();
        Log.v("MapActvity","Oncreate");

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

        if(SelectedTab.equalsIgnoreCase("")){
            SelectedTab="D";
            SelectedHqCode = SharedPref.getHqCode(MapsActivity.this);
            SelectedHqName = SharedPref.getHqName(MapsActivity.this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(!CheckLocPermission()){
                RequestLocationPermission();
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(MapsActivity.this);
        }

        if (extra != null) {
            from_tagging = extra.getString("from");
            cust_name = extra.getString("cus_name");
            cust_code = extra.getString("cus_code");
            town_code = extra.getString("town_code");
            town_name = extra.getString("town_name");
            cust_address = extra.getString("cus_add");
            geoTagStatus = extra.getString("geoTagStatus");

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;

        if (from_tagging.equalsIgnoreCase("view_tag_approval")) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                mMap.setOnMarkerClickListener(this);
                mapsBinding.btnTag.setVisibility(View.GONE);
                mapsBinding.constraintTaggedView.setVisibility(View.VISIBLE);
                mapsBinding.tvMeters.setVisibility(View.GONE);
                mapsBinding.constraintMid.setVisibility(View.INVISIBLE);
                mapsBinding.imgRvRight.setVisibility(View.GONE);
                mapsBinding.imgRefreshMap.setVisibility(View.GONE);
                mapsBinding.imgCurLoc.setVisibility(View.GONE);
                mapsBinding.tagginglistlayout.setVisibility(View.GONE);
                mapsBinding.tvCustName.setText(geoTagViewList.get(0).getName());
                mapsBinding.tagSelection.setText(getResources().getString(R.string.geo_tagging));

                LatLng latLng = new LatLng(parseDouble(geoTagViewList.get(0).getLatitude()), parseDouble(geoTagViewList.get(0).getLongitude()));
                mMap.addMarker(new MarkerOptions().position(latLng).snippet(geoTagViewList.get(0).getAddress()).title(geoTagViewList.get(0).getName()).icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parseDouble(geoTagViewList.get(0).getLatitude()), parseDouble(geoTagViewList.get(0).getLongitude())), 15.2f));
                mapsBinding.tvTaggedAddress.setText(geoTagViewList.get(0).getAddress());

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mapsBinding.progressBar.setVisibility(View.GONE);
            });
        } else if(from_tagging.equalsIgnoreCase("tagging") || from_tagging.equalsIgnoreCase("view_tagged")) {
            mapsBinding.imgRefreshMap.setVisibility(View.VISIBLE);
            mapsBinding.imgCurLoc.setVisibility(View.VISIBLE);
            getRequiredData();
            mapFragment.getMapAsync(this);
            mapsBinding.progressBar.setVisibility(View.GONE);
        } else {
            mapsBinding.imgRefreshMap.setVisibility(View.VISIBLE);
            mapsBinding.imgCurLoc.setVisibility(View.VISIBLE);
            getRequiredData();
            mapFragment.getMapAsync(this);
        }

        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        isTagged = false;

        Log.v("map_selected_tab", SelectedTab + "--" + from_tagging + "---" + SfType + "---" + SelectedHqCode);

        mapsBinding.ivBack.setOnClickListener(view -> {
            if (from_tagging.equalsIgnoreCase("not_tagging")) {
                isTagged = false;
                TagCustSelectionList.SelectedCustPos = "";
                SelectedHqCode = "";
                SelectedHqName = "";
                getOnBackPressedDispatcher().onBackPressed();
            } else {
                TagCustSelectionList.SelectedCustPos = "";
                finish();
            }
        });

        mapsBinding.btnTag.setOnClickListener(view -> {
            if (from_tagging.equalsIgnoreCase("tagging")) {
                mapsBinding.imgRefreshMap.setVisibility(View.GONE);
                if (!mapsBinding.tvTaggedAddress.getText().toString().isEmpty() || !mapsBinding.tvTaggedAddress.getText().toString().equalsIgnoreCase("No Address Found")) {
                    if (GeoTagImageNeed.equalsIgnoreCase("0")) {
                        if (CheckCameraPermission()) {
                            RequestCameraPermission();
                        } else {
                            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            captureFile();
                            //  } else captureFileLower();
                        }
                    } else {
                        DisplayDialog();
                    }
                } else {
                    commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.not_able_to_find_address));
                    Intent intent1 = new Intent(MapsActivity.this, TagCustSelectionList.class);
                    startActivity(intent1);
                }
            } else {
                Intent intent1 = new Intent(MapsActivity.this, TagCustSelectionList.class);
                startActivity(intent1);
            }
        });

        mapsBinding.imgRvRight.setOnClickListener(view -> {
            mapsBinding.tagginglistlayout.setVisibility(View.GONE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.imgRvLeft.setVisibility(View.VISIBLE);
        });

        mapsBinding.imgRvLeft.setOnClickListener(view -> {
            mapsBinding.tagginglistlayout.setVisibility(View.VISIBLE);
            mapsBinding.imgRvRight.setVisibility(View.VISIBLE);
            mapsBinding.imgRvLeft.setVisibility(View.GONE);
        });

        mapsBinding.tagTvDoctor.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1")) TabSelected("D", SfCode);
            else TabSelected("D", SelectedHqCode);
        });

        mapsBinding.tagTvChemist.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1")) TabSelected("C", SfCode);
            else TabSelected("C", SelectedHqCode);
        });

        mapsBinding.tagTvStockist.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1")) TabSelected("S", SfCode);
            else TabSelected("S", SelectedHqCode);
        });

        mapsBinding.tagTvUndr.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1")) TabSelected("U", SfCode);
            else TabSelected("U", SelectedHqCode);
        });

        mapsBinding.tagTvCip.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1")) TabSelected("CIP", SfCode);
            else TabSelected("CIP", SelectedHqCode);
        });

        mapsBinding.tagTvHospital.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1")) TabSelected("H", SfCode);
            else TabSelected("H", SelectedHqCode);
        });

        mapsBinding.imgRefreshMap.setOnClickListener(view -> {

            if (CurrentLoc()) {
                lat = gpsTrack.getLatitude();
                lng = gpsTrack.getLongitude();
                LatLng latLng = new LatLng(lat, lng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                if (from_tagging.equalsIgnoreCase("tagging")) {
                    mapsBinding.tvCustName.setText(cust_name);
                    mapsBinding.tvTaggedAddress.setText(CommonUtilsMethods.gettingAddress(MapsActivity.this, lat, lng, false));
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));
                }

            }
        });

        mapsBinding.imgCurLoc.setOnClickListener(view -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f)));
    }

    private void getRequiredData() {

        SfType = SharedPref.getSfType(this);
        SfCode = SharedPref.getSfCode(this);
        SfName = SharedPref.getSfName(this);
        DivCode = SharedPref.getDivisionCode(this);
        SubDivisionCode = SharedPref.getSubdivisionCode(this);
        Designation = SharedPref.getDesig(this);
        StateCode = SharedPref.getStateCode(this);

        if (SharedPref.getDrNeed(this).equalsIgnoreCase("0")) {
            mapsBinding.tagTvDoctor.setVisibility(View.VISIBLE);
            mapsBinding.tagTvDoctor.setText(SharedPref.getDrCap(this));
        }

        if (SharedPref.getChmNeed(this).equalsIgnoreCase("0")) {
            mapsBinding.tagTvChemist.setVisibility(View.VISIBLE);
            mapsBinding.tagTvChemist.setText(SharedPref.getChmCap(this));
        }

        if (SharedPref.getStkNeed(this).equalsIgnoreCase("0")) {
            mapsBinding.tagTvStockist.setVisibility(View.VISIBLE);
            mapsBinding.tagTvStockist.setText(SharedPref.getStkCap(this));
        }

        if (SharedPref.getUnlNeed(this).equalsIgnoreCase("0")) {
            mapsBinding.tagTvUndr.setVisibility(View.VISIBLE);
            mapsBinding.tagTvUndr.setText(SharedPref.getUNLcap(this));
        }

        if (SharedPref.getCipNeed(this).equalsIgnoreCase("0")) {
            mapsBinding.tagTvCip.setVisibility(View.VISIBLE);
            mapsBinding.tagTvCip.setText(SharedPref.getCipCaption(this));
        }

        if (SharedPref.getHospNeed(this).equalsIgnoreCase("0")) {
            mapsBinding.tagTvHospital.setVisibility(View.VISIBLE);
            mapsBinding.tagTvHospital.setText(SharedPref.getHospCaption(this));
        }

        GeoTagImageNeed = SharedPref.getGeotagImg(this);
        GeoTagApprovalNeed = SharedPref.getGeotagApprovalNeed(this);
        limitKm = Double.parseDouble(SharedPref.getDisRad(this));
        img_url = SharedPref.getTagImageUrl(MapsActivity.this);

        if (SelectedHqCode.isEmpty()) {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                for (int i = 0; i < 1; i++) {
                    JSONObject jsonHQList = jsonArray.getJSONObject(0);
                    SelectedHqCode = jsonHQList.getString("id");
                    SelectedHqName = jsonHQList.getString("name");
                }
            } catch (Exception ignored) {

            }
        }

       /* try {
            JSONArray jsonArray;
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                setUpResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setUpResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);

                mapsBinding.tagTvDoctor.setText(setUpResponse.getCaptionDr());

                if (setUpResponse.getChemistNeed().equalsIgnoreCase("0")) {
                    mapsBinding.tagTvChemist.setVisibility(View.VISIBLE);
                    mapsBinding.tagTvChemist.setText(setUpResponse.getCaptionChemist());
                }

                if (setUpResponse.getStockistNeed().equalsIgnoreCase("0")) {
                    mapsBinding.tagTvStockist.setVisibility(View.VISIBLE);
                    mapsBinding.tagTvStockist.setText(setUpResponse.getCaptionStockist());
                }

                if (setUpResponse.getUnDrNeed().equalsIgnoreCase("0")) {
                    mapsBinding.tagTvUndr.setVisibility(View.VISIBLE);
                    mapsBinding.tagTvUndr.setText(setUpResponse.getCaptionUndr());
                }

                if (setupData.has("cip_need")) {
                    if (setUpResponse.getCIPNeed().equalsIgnoreCase("0")) {
                        mapsBinding.tagTvCip.setVisibility(View.VISIBLE);
                        mapsBinding.tagTvCip.setText(setUpResponse.getCaptionCip());
                    }
                }

                if (setupData.has("GeoTagApprovalNeed")) {
                    GeoTagApprovalNeed = setUpResponse.getGeoTagApprovalNeed();
                } else {
                    GeoTagApprovalNeed = "0";
                }

                if (!setUpResponse.getMapGeoFenceCircleRad().equalsIgnoreCase("0")) {
                    limitKm = parseDouble(setUpResponse.getMapGeoFenceCircleRad());
                }
            }

            jsonArray = sqLite.getMasterSyncDataByKey(Constants.CUSTOM_SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject CusSetupData = jsonArray.getJSONObject(0);

                customSetupResponse = new CustomSetupResponse();
                Type typeCustomSetup = new TypeToken<CustomSetupResponse>() {
                }.getType();
                customSetupResponse = new Gson().fromJson(String.valueOf(CusSetupData), typeCustomSetup);

                if (customSetupResponse.getHospNeed().equalsIgnoreCase("0")) {
                    mapsBinding.tagTvHospital.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }*/

    }

    public void captureFileLower() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 19);
    }

    public void captureFile() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        outputFileUri = FileProvider.getUriForFile(MapsActivity.this, getApplicationContext().getPackageName() + ".fileprovider", new File(Objects.requireNonNull(getExternalCacheDir()).getPath(), SfCode + "_" + cust_code + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "") + CommonUtilsMethods.getCurrentInstance("HHmmss") + ".jpeg"));
//        imageName = SfCode + "_" + cust_code + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "") + CommonUtilsMethods.getCurrentInstance("HHmmss") + ".jpeg";
//        taggedTime = CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd") + " " + CommonUtilsMethods.getCurrentInstance("HH:mm:ss");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        someActivityResultLauncher.launch(intent);
        imageName = SfCode + "_" + cust_code + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "") + CommonUtilsMethods.getCurrentInstance("HHmmss") + ".jpeg";
        Intent intent = new Intent(this, CameraActivity.class);
        File file = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(context.getExternalFilesDir(null) + "/NearMeTagging/");
        }else {
            Log.e("File Creation", "captureFile: " );
        }
        if(file != null && !file.exists()) {
            if(!file.mkdirs()) {
                Log.e("File Creation", "Directory Creation Failed.");
            }
        }
        File destinationFile = new File(file, imageName);
        try {
            if(!destinationFile.createNewFile()) {
                Log.e("File Creation", "Destination File Creation Failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        destinationFilePath = destinationFile.getAbsolutePath();
        intent.putExtra("FILE_PATH", destinationFilePath);
        intent.putExtra("FROM", "MapsActivity");
        intent.putExtra("L_FLAG", SharedPref.getGeoChk(this).equalsIgnoreCase("0"));
        intent.putExtra("CAMERA_MODE", "BACK");
        someActivityResultLauncher.launch(intent);
    }

    private void TabSelected(String CustSelected, String sfCode) {
        SelectedTab = CustSelected;
        runOnUiThread(() -> AddTaggedDetails(CustSelected, sfCode));

        if (CustSelected.equalsIgnoreCase("D")) {
            mapsBinding.tagTvDoctor.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_light_purple));
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
            mapsBinding.tagTvCip.setBackground(null);
            mapsBinding.tagTvHospital.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("C")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_light_purple));
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
            mapsBinding.tagTvCip.setBackground(null);
            mapsBinding.tagTvHospital.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("S")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_light_purple));
            mapsBinding.tagTvUndr.setBackground(null);
            mapsBinding.tagTvCip.setBackground(null);
            mapsBinding.tagTvHospital.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("U")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_light_purple));
            mapsBinding.tagTvCip.setBackground(null);
            mapsBinding.tagTvHospital.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("CIP")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
            mapsBinding.tagTvCip.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_light_purple));
            mapsBinding.tagTvHospital.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("H")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
            mapsBinding.tagTvCip.setBackground(null);
            mapsBinding.tagTvHospital.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_light_purple));
        }
    }

    private void addCircle(GoogleMap mMap) {
        lat = gpsTrack.getLatitude();
        lng = gpsTrack.getLongitude();
        LatLng latLng = new LatLng(lat, lng);
        int transparent = 0x12FD0B0B;
        CircleOptions circle = new CircleOptions().center(latLng).radius(limitKm * 1000.0).strokeWidth(4).strokeColor(Color.RED).fillColor(transparent).clickable(true);
        mMap.addCircle(circle);
        mapsBinding.progressBar.setVisibility(View.GONE);
    }

    public boolean CurrentLoc() {
        boolean val = false;
        gpsTrack = new GPSTrack(MapsActivity.this);
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new android.app.AlertDialog.Builder(MapsActivity.this).setTitle("Alert") // GPS not found
                        .setCancelable(false).setMessage("Activate the Gps to proceed further") // Want to enable?
                        .setPositiveButton("Yes", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
            } else {
                val = true;
            }
        } catch (Exception e) {
            commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.loc_not_detect));
        }
        return val;
    }

    private void DisplayDialog() {

        dialogTagCust = new Dialog(this);
        dialogTagCust.setContentView(R.layout.dialog_confirmtag_alert);
        dialogTagCust.setCancelable(false);
        Objects.requireNonNull(dialogTagCust.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTagCust.show();
        btn_confirm = dialogTagCust.findViewById(R.id.btn_confirm);
        Button btn_cancel = dialogTagCust.findViewById(R.id.btn_cancel);
        TextView tv_cust_name = dialogTagCust.findViewById(R.id.txt_cust_name);
        TextView tv_lat = dialogTagCust.findViewById(R.id.txt_lat);
        TextView tv_lng = dialogTagCust.findViewById(R.id.txt_lng);
        TextView tv_address = dialogTagCust.findViewById(R.id.txt_address);
        ProgressBar progressBar =  dialogTagCust.findViewById(R.id.progressBar);

        tv_cust_name.setText(cust_name);
        tv_lat.setText(String.format("Latitude : %s", lat));
        tv_lng.setText(String.format("Longitude : %s", lng));
        tv_address.setText(mapsBinding.tvTaggedAddress.getText().toString());

        JSONObject jsonImage = CommonUtilsMethods.CommonObjectParameter(this);
        try {
            jsonImage.put("tableName", "imgupload");
            jsonImage.put("sfcode", SfCode);
            jsonImage.put("division_code", DivCode);
            if (SfType.equalsIgnoreCase("1")) {
                jsonImage.put("Rsf", SfCode);
            } else {
                jsonImage.put("Rsf", SelectedHqCode);
            }
        } catch (Exception ignored) {

        }

        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
        try {
            jsonObject.put("tableName", "save_geo");
            jsonObject.put("lat", String.valueOf(lat));
            jsonObject.put("long", String.valueOf(lng));
            jsonObject.put("cuscode", cust_code);
            jsonObject.put("divcode", DivCode.replace(",", "").trim());
            jsonObject.put("cust", SelectedTab);
            jsonObject.put("tagged_time", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd") + " " + CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
            jsonObject.put("image_name", imageName);
            jsonObject.put("sfcode", SfCode);
            jsonObject.put("addr", mapsBinding.tvTaggedAddress.getText().toString());
            if (SfType.equalsIgnoreCase("1")) {
                jsonObject.put("tagged_cust_HQ", SfCode);
            } else {
                jsonObject.put("tagged_cust_HQ", SelectedHqCode);
            }
            jsonObject.put("cust_name", cust_name);
            jsonObject.put("towncode", town_code);
            jsonObject.put("townname", town_name);



            if (GeoTagApprovalNeed.equalsIgnoreCase("0")) {
                jsonObject.put("status", "1");
            } else {
                jsonObject.put("status", "0");
            }
        } catch (JSONException ignored) {
        }

        btn_confirm.setOnClickListener(view -> {
            if (UtilityClass.isNetworkAvailable(this)){
                progressBar.setVisibility(View.VISIBLE);
                btn_confirm.setEnabled(false);
                btn_confirm.setBackground(ContextCompat.getDrawable(context, R.drawable.tagging_disable_button));
                if (GeoTagImageNeed.equalsIgnoreCase("0")) {
                    CallImageAPI(jsonImage.toString(), jsonObject.toString(),progressBar);
                } else {
                    CallAPIGeo(jsonObject.toString(),progressBar);
                }
            }else {
                commonUtilsMethods.showToastMessage(this,getString(R.string.no_network));
            }

        });

        btn_cancel.setOnClickListener(view -> {
            dialogTagCust.dismiss();
            mapsBinding.btnTag.setText(R.string.tag);
            mapsBinding.imgRefreshMap.setVisibility(View.GONE);
            mapsBinding.tvTaggedAddress.setVisibility(View.VISIBLE);
            mapsBinding.constraintMid.setVisibility(View.INVISIBLE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.tagginglistlayout.setVisibility(View.GONE);
        });
    }

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(txt, MultipartBody.FORM);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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

    private void CallAPIGeo(String jsonTag,ProgressBar progressBar) {
        Log.v("test", jsonTag);
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "geodetails");
        Call<JsonElement> callSaveGeo = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonTag);

        callSaveGeo.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
//                        progressDialog.dismiss();
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("Msg").equalsIgnoreCase("Tagged Successfully")) {
                            commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.tagged_successfully));
                            dialogTagCust.dismiss();
                            CallAPIList(SelectedTab,progressBar);
                            isTagged = true;
                            TaggedLat = String.valueOf(lat);
                            TaggedLng = String.valueOf(lng);
                            TaggedAdd = mapsBinding.tvTaggedAddress.getText().toString();
                            //SharedPref.setTaggedSuccessfully(MapsActivity.this, "true");
//                            finish();
                        } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("Msg").equalsIgnoreCase("You have reached the maximum tags...")) {
                            commonUtilsMethods.showToastMessage(MapsActivity.this, jsonSaveRes.getString("Msg"));
                            dialogTagCust.dismiss();
                        } else {
                            commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
                            dialogTagCust.dismiss();
                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        dialogTagCust.dismiss();
                    }
                    btn_confirm.setEnabled(true);
                    btn_confirm.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple));
                } else {
                    progressBar.setVisibility(View.GONE);
                    commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
                    dialogTagCust.dismiss();
                    btn_confirm.setEnabled(true);
                    btn_confirm.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
                dialogTagCust.dismiss();
                btn_confirm.setEnabled(true);
                btn_confirm.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple));
            }
        });

    }

    public void prepareMasterToSync(String hqCode, String Cust_Selected,ProgressBar progressBar) {
        masterSyncArray.clear();
        MasterSyncItemModel ModelList = new MasterSyncItemModel();
        switch (Cust_Selected) {
            case "D":
                ModelList = new MasterSyncItemModel("Doctor", "getdoctors", Constants.DOCTOR + hqCode);
                break;
            case "C":
                ModelList = new MasterSyncItemModel("Doctor", "getchemist", Constants.CHEMIST + hqCode);
                break;
            case "S":
                ModelList = new MasterSyncItemModel("Doctor", "getstockist", Constants.STOCKIEST + hqCode);
                break;
            case "U":
                ModelList = new MasterSyncItemModel("Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode);
                break;
        }

        masterSyncArray.add(ModelList);
        for (int i = 0; i < masterSyncArray.size(); i++) {
            sync(masterSyncArray.get(i), hqCode,progressBar);
        }
    }

    public void sync(MasterSyncItemModel masterSyncItemModel, String hqCode,ProgressBar progressBar) {

        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(this);
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", SfCode);
                jsonObject.put("division_code", DivCode);
                jsonObject.put("Rsf", hqCode);
 Log.e("test"," : " + jsonObject);
                Call<JsonElement> call = null;
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Doctor")) {
                    Map<String, String> mapString = new HashMap<>();
                    mapString.put("axn", "table/dcrmasterdata");
                    call = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                }

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                            boolean success = false;
                            if (response.isSuccessful()) {
// Log.e("test","response : " + masterSyncItemModel.getRemoteTableName() +" : " + response.body().toString());
                                try {
                                    JsonElement jsonElement = response.body();
                                    JSONArray jsonArray = new JSONArray();
                                    assert jsonElement != null;
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                            if (!jsonObject1.has("success")) { // json object with "success" : "fail" will be received only when api call is failed ,"success will not be received when api call is success
                                                jsonArray.put(jsonObject1);
                                                success = true;
                                            } else if (jsonObject1.has("success") && !jsonObject1.getBoolean("success")) {
                                                masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        if (success) {
                                            customDialog.dismiss();
                                            progressBar.setVisibility(View.GONE);
                                            showToast(selectedTap);
                                            finish();
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 2));
                                        }
                                    } else {
                                        masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);

                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                customDialog.dismiss();
                                updateDialogBoxText(selectedTap);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            customDialog.dismiss();
                            Log.e("test", "failed : " + t);
                            updateDialogBoxText(selectedTap);
                            masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.no_network));
        }
    }


    private void CallAPIList(String CustSelected,ProgressBar progressBar) {
        selectedTap = CustSelected;
        String sfCode;
        if (SfType.equalsIgnoreCase("1")) {
            sfCode = SfCode;
        } else {
            sfCode = SelectedHqCode;
        }

        if (CustSelected.equalsIgnoreCase("D")) {
            showCustomDialog(SharedPref.getDrCap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_is_updating_please_wait), ContextCompat.getDrawable(context, R.drawable.baseline_cloud_sync_24), true);
            prepareMasterToSync(sfCode, "D",progressBar);
        } else if (CustSelected.equalsIgnoreCase("C")) {
            showCustomDialog(SharedPref.getChmCap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_is_updating_please_wait), ContextCompat.getDrawable(context, R.drawable.baseline_cloud_sync_24_1),true);
            prepareMasterToSync(sfCode, "C",progressBar);
        } else if (CustSelected.equalsIgnoreCase("S")) {
            showCustomDialog(SharedPref.getStkCap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_is_updating_please_wait),ContextCompat.getDrawable(context, R.drawable.baseline_cloud_sync_24_2),true);
            prepareMasterToSync(sfCode, "S",progressBar);
        } else if (CustSelected.equalsIgnoreCase("U")) {
            showCustomDialog(SharedPref.getUNLcap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_is_updating_please_wait),ContextCompat.getDrawable(context, R.drawable.baseline_cloud_sync_24_3),true);
            prepareMasterToSync(sfCode, "U",progressBar);
        }
    }

    private void CallImageAPI(String jsonImage, String jsonTag,ProgressBar progressBar) {
        try {
            ApiInterface apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getTagApiImageUrl(getApplicationContext()));
            Call<JsonObject> callImage;
            HashMap<String, RequestBody> values = field(jsonImage);
            MultipartBody.Part img = convertImg("UploadImg", destinationFilePath);
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
                                progressBar.setVisibility(View.VISIBLE);
                                CallAPIGeo(jsonTag,progressBar);
                            } else {
                                dialogTagCust.dismiss();
                                commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.tag_failed));
                            }
                        } catch (Exception e) {
                            Log.v("img_tag", e.toString());
                            dialogTagCust.dismiss();
                        }
                    } else {
                        dialogTagCust.dismiss();
                        commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    commonUtilsMethods.showToastMessage(MapsActivity.this, "Poor Connection Please Check After Sometime");
                    dialogTagCust.dismiss();
                }
            });
        } catch (Exception e) {
            dialogTagCust.dismiss();
        }
    }

    @Override
    protected void onResume() {
        CommonAlertBox.CheckLocationStatus(MapsActivity.this);
        timeZoneVerification();
        super.onResume();
    }

    @SuppressLint({"SetTextI18n", "PotentialBehaviorOverride"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(!CheckLocPermission()){
                RequestLocationPermission();
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(MapsActivity.this);
        }


        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        lat = gpsTrack.getLatitude();
        lng = gpsTrack.getLongitude();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f));
        if (from_tagging.equalsIgnoreCase("tagging")) {
            Log.v("hhh", "-000--");
            mapsBinding.btnTag.setText(R.string.tag);
            mapsBinding.constraintTaggedView.setVisibility(View.VISIBLE);
            mapsBinding.constraintMid.setVisibility(View.INVISIBLE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.tagginglistlayout.setVisibility(View.GONE);

            mapsBinding.tvCustName.setText(cust_name);
            mapsBinding.tvTaggedAddress.setText(CommonUtilsMethods.gettingAddress(MapsActivity.this, lat, lng, false));

            LatLng latLng = new LatLng(lat, lng);
            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);

            mMap.setOnCameraMoveListener(() -> {
                lat = mMap.getCameraPosition().target.latitude;
                lng = mMap.getCameraPosition().target.longitude;
                mapsBinding.tvTaggedAddress.setText(CommonUtilsMethods.gettingAddress(MapsActivity.this, lat, lng, false));
            });
        } else if (from_tagging.equalsIgnoreCase("view_tagged")) {
            mMap.setOnMarkerClickListener(this);
            mapsBinding.btnTag.setVisibility(View.GONE);
            mapsBinding.constraintTaggedView.setVisibility(View.VISIBLE);

            mapsBinding.tvMeters.setVisibility(View.VISIBLE);
            mapsBinding.constraintMid.setVisibility(View.INVISIBLE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.tagginglistlayout.setVisibility(View.GONE);
            mapsBinding.tvCustName.setText(cust_name);


            int getCount = 0;
            if (CustListAdapter.getCustListNew.size() > 0) {
                for (int i = 0; i < CustListAdapter.getCustListNew.size(); i++) {
                    Log.v("lat_lng", CustListAdapter.getCustListNew.get(i).getLatitude() + "---" + CustListAdapter.getCustListNew.get(i).getLongitude());
                    if (!CustListAdapter.getCustListNew.get(i).getLatitude().isEmpty() && !CustListAdapter.getCustListNew.get(i).getLongitude().isEmpty()) {
                        LatLng latLng = new LatLng(parseDouble(CustListAdapter.getCustListNew.get(i).getLatitude()), parseDouble(CustListAdapter.getCustListNew.get(i).getLongitude()));
                        mMap.addMarker(new MarkerOptions().position(latLng).snippet(CustListAdapter.getCustListNew.get(i).getAddress()).title(cust_name).icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));
                        getCount = i;
                    }
                }

                mapsBinding.tvTaggedAddress.setText(CustListAdapter.getCustListNew.get(0).getAddress());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parseDouble(CustListAdapter.getCustListNew.get(getCount).getLatitude()), parseDouble(CustListAdapter.getCustListNew.get(getCount).getLongitude())), 16.2f));
                double getDistance = getDistanceMeters(lat, lng, parseDouble(CustListAdapter.getCustListNew.get(getCount).getLatitude()), parseDouble(CustListAdapter.getCustListNew.get(getCount).getLongitude()));
                if (getDistance > 1000) {
                    getDistance = getDistance / 1000;
                    DecimalFormat decFor = new DecimalFormat("0.00");
                    getDistance = parseDouble(decFor.format(getDistance));
                    mapsBinding.tvMeters.setText(String.format("%s \n Kms", getDistance));
                } else {
                    mapsBinding.tvMeters.setText(String.format("%s \n Meters", getDistance));
                }
            } else {
                commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.no_data_found));
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(true);

        } else if (from_tagging.equalsIgnoreCase("not_tagging")) {
            Log.v("hhh", "-111--" + SelectedTab + "--" + SfType + "---" + SfCode + "---" + SelectedHqCode);
            if (SfType.equalsIgnoreCase("1")) {
                TabSelected(SelectedTab, SfCode);
            } else {
                TabSelected(SelectedTab, SelectedHqCode);
                mapsBinding.tvHqName.setVisibility(View.VISIBLE);
                mapsBinding.tvHqName.setText(SelectedHqName);
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
        }
    }


    @SuppressLint("PotentialBehaviorOverride")
    private void AddTaggedDetails(String selected, String sfCode) {
        mapsBinding.progressBar.setVisibility(View.VISIBLE);
        taggedMapListArrayList.clear();
        mMap.clear();
        list.clear();
        gpsTrack = new GPSTrack(this);
        lat = gpsTrack.getLatitude();
        lng = gpsTrack.getLongitude();

        switch (selected) {
            case "D":
                try {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + sfCode).getMasterSyncDataJsonArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("Addrs");
                        if (!jsonObject.getString("Lat").trim().isEmpty() || !jsonObject.getString("Long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "1", jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("Lat").equalsIgnoreCase("0.0") || jsonObject.getString("Long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, parseDouble(jsonObject.getString("Lat")), parseDouble(jsonObject.getString("Long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "1", jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.v("map_camera_tt", "error---dr-" + e);
                }
                break;

            case "C":
                try {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + sfCode).getMasterSyncDataJsonArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addrs");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "2", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, parseDouble(jsonObject.getString("lat")), parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "2", jsonObject.getString("lat"), jsonObject.getString("long"), cust_address, jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.v("map_camera_tt", "error---che-" + e);
                }
                break;
            case "S":
                try {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + sfCode).getMasterSyncDataJsonArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addrs");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "3", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, parseDouble(jsonObject.getString("lat")), parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "3", jsonObject.getString("lat"), jsonObject.getString("long"), cust_address, jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.v("map_camera_tt", "error---stk-" + e);
                }
                break;
            case "U":
                try {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + sfCode).getMasterSyncDataJsonArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addr");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "4", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, parseDouble(jsonObject.getString("lat")), parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "4", jsonObject.getString("lat"), jsonObject.getString("long"), cust_address, jsonObject.getString("img_name"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.v("map_camera_tt", "error---UnDr-" + e);
                }
                break;
        }

        try {

            for (int i = 0; i < list.size(); i++) {
                mm = list.get(i);
                LatLng latLng = new LatLng(parseDouble(mm.getLat()), parseDouble(mm.getLng()));
                float[] distance = new float[2];
                Location.distanceBetween(parseDouble(mm.getLat()), parseDouble(mm.getLng()), lat, lng, distance);

                if (distance[0] < limitKm * 1000.0) {
                    taggedMapListArrayList.add(new TaggedMapList(mm.getName(), mm.getType(), mm.getAddress(), mm.getCode(), false, mm.getLat(), mm.getLng(), mm.getImageName(), getDistanceMeters(lat, lng, parseDouble(mm.getLat()), parseDouble(mm.getLng()))));
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f));
                marker = mMap.addMarker(new MarkerOptions().position(latLng).snippet(mm.getName() + "&" + mm.getAddress() + "$" + mm.getCode() + "%" + mm.getLat() + "#" + mm.getLng() + "^" + mm.getImageName()).icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));

                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(mm, MapsActivity.this));
            }

            if (GeoTagImageNeed.equalsIgnoreCase("0")) {
                mMap.setOnInfoWindowClickListener(marker -> {
                    Dialog dialog = new Dialog(MapsActivity.this);
                    dialog.setContentView(R.layout.map_img_layout);
                    ImageView imageView = dialog.findViewById(R.id.img_dr_content);

                    if (Objects.requireNonNull(marker.getSnippet()).substring(marker.getSnippet().lastIndexOf("^") + 1).isEmpty()) {
                        commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.toast_no_img_found));
                    } else {
                        if (img_url.equalsIgnoreCase("null") || img_url.isEmpty()) {
                            commonUtilsMethods.showToastMessage(MapsActivity.this, getString(R.string.save_settings_con_screen));
                        } else {
                            Glide.with(getApplicationContext()).load(img_url + "photos/" + marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1)).centerCrop().into(imageView);
                            dialog.show();
                        }
                    }
                });
            }

            if (mapsBinding.rvList.getItemDecorationCount() > 0) {
                for (int i = 0; i < mapsBinding.rvList.getItemDecorationCount(); i++) {
                    mapsBinding.rvList.removeItemDecorationAt(i);
                }
            }
            taggingAdapter = new TaggingAdapter(MapsActivity.this, taggedMapListArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mapsBinding.rvList.setLayoutManager(mLayoutManager);
            mapsBinding.rvList.setItemAnimator(new DefaultItemAnimator());
            mapsBinding.rvList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
            mapsBinding.rvList.setAdapter(taggingAdapter);

            Collections.sort(taggedMapListArrayList, Comparator.comparingDouble(TaggedMapList::getMeters));

            if (taggedMapListArrayList.size() > 0) {
                mapsBinding.rvList.setVisibility(View.VISIBLE);
                mapsBinding.tagginglistlayout.setVisibility(View.VISIBLE);
                mapsBinding.rvList.setVisibility(View.VISIBLE);
                mapsBinding.noTagImage.setVisibility(View.GONE);
                mapsBinding.imgRvRight.setVisibility(View.VISIBLE);
                mapsBinding.imgRvLeft.setVisibility(View.GONE);
            } else {
                mapsBinding.tagginglistlayout.setVisibility(View.GONE);
                mapsBinding.rvList.setVisibility(View.GONE);
                mapsBinding.noTagImage.setVisibility(View.VISIBLE);
                mapsBinding.imgRvRight.setVisibility(View.GONE);
                mapsBinding.imgRvLeft.setVisibility(View.VISIBLE);
            }

            addCircle(mMap);
        } catch (Exception e) {
            Log.v("map_camera_tt", "error----" + e);
            mapsBinding.progressBar.setVisibility(View.GONE);
        }
    }

    private double getDistanceMeters(double CurLat, double CurLng, double CustLat, double CustLng) {
        distanceTag = distance(CurLat, CurLng, CustLat, CustLng);
        distanceTag = milesToMeters(distanceTag);
        DecimalFormat decFor = new DecimalFormat("0.00");
        distanceTag = valueOf(decFor.format(distanceTag));
        return distanceTag;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (from_tagging.equalsIgnoreCase("view_tagged") || from_tagging.equalsIgnoreCase("view_tag_approval")) {
            Log.v("position", String.valueOf(marker.getPosition()));
            double getDistance = getDistanceMeters(lat, lng, marker.getPosition().latitude, marker.getPosition().longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 18.0f));
            mapsBinding.tvTaggedAddress.setText(marker.getSnippet());
            if (getDistance > 1000) {
                getDistance = getDistance / 1000;
                DecimalFormat decFor = new DecimalFormat("0.00");
                getDistance = parseDouble(decFor.format(getDistance));
                mapsBinding.tvMeters.setText(String.format("%s \n Kms", getDistance));
            } else {
                mapsBinding.tvMeters.setText(String.format("%s \n Meters", getDistance));
            }
        }
        return true;
    }

    private void showCustomDialog(String textUpdate,Drawable drawable,boolean visibility) {
        DialogMasterSyncUpdateBinding masterSyncUpdateBinding = DialogMasterSyncUpdateBinding.inflate(LayoutInflater.from(getApplicationContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this, 0);
         customDialog = builder.create();
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setView(masterSyncUpdateBinding.getRoot());
        customDialog.setCancelable(false);
        customDialog.show();
        customDialog.show();
        masterSyncUpdateBinding.textMasterSync.setText(textUpdate);
        masterSyncUpdateBinding.syncImg.setBackground(drawable);
        if (!visibility){
            masterSyncUpdateBinding.progressBar.setVisibility(View.GONE);
            masterSyncUpdateBinding.pendingImg.setVisibility(View.VISIBLE);
            masterSyncUpdateBinding.closeBtn.setVisibility(View.VISIBLE);
        }
        masterSyncUpdateBinding.closeBtn.setOnClickListener(v -> {customDialog.dismiss();});
    }
  private void showToast(String selectedTap){
        if (selectedTap.equals("D")){
            commonUtilsMethods.showToastMessage(this,SharedPref.getDrCap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_updated_successfully));
        } else if (selectedTap.equals("C")) {
            commonUtilsMethods.showToastMessage(this,SharedPref.getChmCap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_updated_successfully));
        } else if (selectedTap.equals("S")) {
            commonUtilsMethods.showToastMessage(this,SharedPref.getStkCap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_updated_successfully));
        } else if (selectedTap.equals("U")) {
            commonUtilsMethods.showToastMessage(this,SharedPref.getUNLcap(MapsActivity.this) + MapsActivity.this.getString(R.string.list_updated_successfully));
        }
  }
  private void updateDialogBoxText(String selectedTap){
      if (selectedTap.equals("D")){
          showCustomDialog(MapsActivity.this.getString(R.string.failed_to_update) + SharedPref.getDrCap(MapsActivity.this)  + MapsActivity.this.getString(R.string.list_please_try_again), ContextCompat.getDrawable(context, R.drawable.baseline_do_disturb_24), false);
      } else if (selectedTap.equals("C")) {
          showCustomDialog(MapsActivity.this.getString(R.string.failed_to_update) + SharedPref.getChmCap(MapsActivity.this)  + MapsActivity.this.getString(R.string.list_please_try_again),    ContextCompat.getDrawable(context, R.drawable.baseline_do_disturb_24), false);
      } else if (selectedTap.equals("S")) {
          showCustomDialog(MapsActivity.this.getString(R.string.failed_to_update) + SharedPref.getStkCap(MapsActivity.this)  + MapsActivity.this.getString(R.string.list_please_try_again),    ContextCompat.getDrawable(context, R.drawable.baseline_do_disturb_24), false);
      } else if (selectedTap.equals("U")) {
          showCustomDialog(MapsActivity.this.getString(R.string.failed_to_update) + SharedPref.getUNLcap(MapsActivity.this)  + MapsActivity.this.getString(R.string.list_please_try_again),    ContextCompat.getDrawable(context, R.drawable.baseline_do_disturb_24), false);
      }
  }
  private void timeZoneVerification(){
      boolean isAutoTimeZoneEnabled = commonUtilsMethods.isAutoTimeEnabled(context) && commonUtilsMethods.isTimeZoneAutomatic(context);
      if (!isAutoTimeZoneEnabled) {
          CommonUtilsMethods.showCustomDialog(this);
      }
  }


    private void RequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            }
        }
    }
    public boolean CheckLocPermission() {
        int FineLocation = ContextCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION);
        int CoarseLocation = ContextCompat.checkSelfPermission(MapsActivity.this, ACCESS_COARSE_LOCATION);
        return FineLocation == PackageManager.PERMISSION_GRANTED && CoarseLocation == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);

            } else {
                // Permission denied, show a message to the user
               CommonUtilsMethods. RequestGPSPermission(MapsActivity.this,"Location");
            }
        }else if(requestCode == 102){
            boolean DontAskAgain = false;
                for (String allowedPermissions : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, allowedPermissions)) {
                        RequestCameraPermission();
                    } else if (PermissionChecker.checkCallingOrSelfPermission(this, allowedPermissions) != PermissionChecker.PERMISSION_GRANTED) {
                        DontAskAgain = true;
                        break;
                    } else {
                        captureFile();
                    }
                }
                if (DontAskAgain) {
                    CommonUtilsMethods. RequestGPSPermission(MapsActivity.this,"Camera");


                }
        }
    }

    public boolean CheckCameraPermission() {
        int Camera = ContextCompat.checkSelfPermission(MapsActivity.this, CAMERA);
        return Camera != PackageManager.PERMISSION_GRANTED;
    }

    private void RequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CAMERA}, 102);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CAMERA}, 102);
            }
        }
    }




}