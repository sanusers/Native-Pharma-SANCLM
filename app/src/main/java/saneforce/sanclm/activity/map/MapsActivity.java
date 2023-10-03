package saneforce.sanclm.activity.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.map.custSelection.TagCustSelectionList;
import saneforce.sanclm.activity.masterSync.MasterSyncActivity;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.GPSTrack;
import saneforce.sanclm.databinding.ActivityMapsBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static ArrayList<ViewTagModel> list = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static ActivityMapsBinding mapsBinding;
    @SuppressLint("StaticFieldLeak")
    public static TaggingAdapter taggingAdapter;
    public static ArrayList<TaggedMapList> taggedMapListArrayList = new ArrayList<>();
    private final int transparent = 0x15000000;
  //  private final int transparent = 0xFFFFF0F2;
    ViewTagModel mm = null;
    Uri outputFileUri;
    ApiInterface api_interface;
    Double distanceTag;
    SQLite sqLite;
    Marker marker;
    LocationManager locationManager;
    GPSTrack gpsTrack;
    SQLiteHandler sqLiteHandler;
    String cust_name, town_code, town_name, SfName, SfType, img_url, cust_address, Selected, SfCode, DivCode, SelectedSFCode, TodayPlanSfCode, RSFCode, Designation, StateCode, SubDivisionCode, cust_code, filePath = "", imageName = "", taggedTime = "";
    String from_tagging = "", l;
    double laty, lngy, limitKm = 0.5;
    Dialog dialogTagCust;

    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference mCommonSharedPrefrence;
    private GoogleMap mMap;

    public static double milesToMeters(double miles) {
        return miles * 1609.344;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String finalPath = "/storage/emulated/0";
        try {
            filePath = outputFileUri.getPath();
            filePath = filePath.substring(1);
            filePath = finalPath + filePath.substring(filePath.indexOf("/"));
            String result = String.valueOf(resultCode);
            if (result.equalsIgnoreCase("-1")) {
                DisplayDialog();
            } else {
            }
        } catch (Exception e) {

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapsBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(mapsBinding.getRoot());

        sqLiteHandler = new SQLiteHandler(this);
        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(this);
        mCommonSharedPrefrence = new CommonSharedPreference(this);
        sqLite = new SQLite(getApplicationContext());

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            from_tagging = extra.getString("from");
            cust_name = extra.getString("cust_name");
            cust_code = extra.getString("cust_code");
            town_code = extra.getString("town_code");
            town_name = extra.getString("town_name");
        }

        String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
        String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
        String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);
        SharedPref.setTaggedSuccessfully(MapsActivity.this, "false");
        Selected = SharedPref.getMapSelectedTab(MapsActivity.this);
        limitKm = Double.parseDouble(SharedPref.getGeofencingCircleRadius(MapsActivity.this));
        SfType = SharedPref.getSfType(MapsActivity.this);
        SfName = SharedPref.getSfName(MapsActivity.this);
        SfCode = SharedPref.getSfCode(MapsActivity.this);
        DivCode = SharedPref.getDivisionCode(MapsActivity.this);
        Designation = SharedPref.getDesignationName(MapsActivity.this);
        StateCode = SharedPref.getStateCode(MapsActivity.this);
        SubDivisionCode = SharedPref.getSubdivCode(MapsActivity.this);
        img_url = SharedPref.getTagImageUrl(MapsActivity.this);
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(MapsActivity.this);
        SelectedSFCode = SharedPref.getSelectedHqCode(MapsActivity.this);

        if (SelectedSFCode.isEmpty()) {
            try {
                JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Subordinate");
                for (int i = 0; i < 1; i++) {
                    JSONObject jsonHQList = jsonArray.getJSONObject(0);
                    SelectedSFCode = jsonHQList.getString("id");
                    SharedPref.setSelectedHqName(MapsActivity.this, jsonHQList.getString("name"));
                }
            } catch (Exception e) {

            }
        }

        mapsBinding.tagTvDoctor.setText(SharedPref.getCaptionDr(MapsActivity.this));
        mapsBinding.tagTvChemist.setText(SharedPref.getCaptionChemist(MapsActivity.this));
        mapsBinding.tagTvStockist.setText(SharedPref.getCaptionStockist(MapsActivity.this));
        mapsBinding.tagTvUndr.setText(SharedPref.getCaptionUnDr(MapsActivity.this));

        Log.v("map_selected_tab", Selected + "--" + from_tagging + "---" + SfType + "---" + SelectedSFCode);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mapsBinding.ivBack.setOnClickListener(view -> {
            if (mapsBinding.btnTag.getText().toString().equalsIgnoreCase("Tag")) {
                SharedPref.setCustomerPosition(MapsActivity.this, "");
                finish();
                // startActivity(new Intent(MapsActivity.this, TagCustSelectionList.class));
            } else {
                SharedPref.setTaggedSuccessfully(MapsActivity.this, "");
                SharedPref.setCustomerPosition(MapsActivity.this, "");
                SharedPref.setSelectedHqCode(MapsActivity.this, "");
                SharedPref.setSelectedHqName(MapsActivity.this, "");
                startActivity(new Intent(MapsActivity.this, HomeDashBoard.class));
            }
        });

        mapsBinding.btnTag.setOnClickListener(view -> {
            if (from_tagging.equalsIgnoreCase("tagging")) {
                mapsBinding.imgRefreshMap.setVisibility(View.GONE);
                if (SharedPref.getGeotagImage(getApplicationContext()).equalsIgnoreCase("0")) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CAMERA}, 5);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            captureFile();
                        } else captureFileLower();
                    }
                } else {
                    DisplayDialog();
                }
            } else {
                Intent intent1 = new Intent(MapsActivity.this, TagCustSelectionList.class);
                startActivity(intent1);
            }
        });

        mapsBinding.imgRvRight.setOnClickListener(view -> {
            mapsBinding.rvList.setVisibility(View.GONE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.imgRvLeft.setVisibility(View.VISIBLE);
        });

        mapsBinding.imgRvLeft.setOnClickListener(view -> {
            mapsBinding.rvList.setVisibility(View.VISIBLE);
            mapsBinding.imgRvRight.setVisibility(View.VISIBLE);
            mapsBinding.imgRvLeft.setVisibility(View.GONE);
        });

        mapsBinding.tagTvDoctor.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1"))
                TabSelected("D", SfCode);
            else TabSelected("D", SelectedSFCode);
        });

        mapsBinding.tagTvChemist.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1"))
                TabSelected("C", SfCode);
            else TabSelected("C", SelectedSFCode);
        });

        mapsBinding.tagTvStockist.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1"))
                TabSelected("S", SfCode);
            else TabSelected("S", SelectedSFCode);
        });

        mapsBinding.tagTvUndr.setOnClickListener(view -> {
            if (SfType.equalsIgnoreCase("1"))
                TabSelected("U", SfCode);
            else TabSelected("U", SelectedSFCode);
        });

        mapsBinding.imgRefreshMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurrentLoc()) {
                    laty = gpsTrack.getLatitude();
                    lngy = gpsTrack.getLongitude();
                 /*   SharedPreferences shares = getContext().getSharedPreferences("location", 0);
                    SharedPreferences.Editor editor = shares.edit();
                    editor.putString("lat", String.valueOf(laty));
                    editor.putString("lng", String.valueOf(lngy));
                    editor.apply();*/
                    CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(String.valueOf(laty)), Double.parseDouble(String.valueOf(lngy)), true);
                }
                LatLng latLng = new LatLng(laty, lngy);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
            }
        });

        mapsBinding.imgCurLoc.setOnClickListener(view -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f)));
    }

    public void captureFileLower() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Uri outputFileUri = Uri.fromFile(new File(getExternalCacheDir().getPath(), "pickImageResult.jpeg"));
        //outputFileUri = FileProvider.getUriForFile(FeedbackActivity.this, getApplicationContext().getPackageName() + ".fileprovider", new File(getExternalCacheDir().getPath(), "pickImageResult"+System.currentTimeMillis()+".jpeg"));
        //Log.v("priniting_uri",outputFileUri.toString()+" output "+outputFileUri.getPath()+" raw_msg "+getExternalCacheDir().getPath());
        //content://com.saneforce.sbiapplication.fileprovider/shared_video/Android/data/com.saneforce.sbiapplication/cache/pickImageResult.jpeg
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 19);
    }

    public void captureFile() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Uri outputFileUri = Uri.fromFile(new File(getExternalCacheDir().getPath(), "pickImageResult.jpeg"));
        outputFileUri = FileProvider.getUriForFile(MapsActivity.this, getApplicationContext().getPackageName() + ".fileprovider", new File(getExternalCacheDir().getPath(), SfCode + "_" + cust_code + "_" + CommonUtilsMethods.getCurrentDateDMY().replace("-", "") + CommonUtilsMethods.getCurrentTime().replace(":", "") + ".jpeg"));
        imageName = SfCode + "_" + cust_code + "_" + CommonUtilsMethods.getCurrentDateDMY().replace("-", "") + CommonUtilsMethods.getCurrentTime().replace(":", "") + ".jpeg";
        taggedTime = CommonUtilsMethods.getCurrentInstance() + " " + CommonUtilsMethods.getCurrentTime();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 15);
    }

    private void TabSelected(String CustSelected, String sfCode) {
        SharedPref.setMapSelectedTab(MapsActivity.this, CustSelected);
        AddTaggedDetails(CustSelected, sfCode);
        if (CustSelected.equalsIgnoreCase("D")) {
            mapsBinding.tagTvDoctor.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("C")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("S")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvUndr.setBackground(null);
        } else if (CustSelected.equalsIgnoreCase("U")) {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
        }
    }

    private void addCircle(GoogleMap mMap) {
        laty = gpsTrack.getLatitude();
        lngy = gpsTrack.getLongitude();
        LatLng latLng = new LatLng(laty, lngy);
        CircleOptions circle = new CircleOptions()
                .center(latLng)
                .radius(limitKm * 1000.0)
                .strokeWidth(4)
                .strokeColor(Color.RED)
                .fillColor(transparent)
                .clickable(true);
        mMap.addCircle(circle);
    }

    public boolean CurrentLoc() {
        boolean val = false;
        gpsTrack = new GPSTrack(MapsActivity.this);
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new android.app.AlertDialog.Builder(MapsActivity.this).setTitle("Alert") // GPS not found
                        .setCancelable(false).setMessage("Activate the Gps to proceed futher") // Want to enable?
                        .setPositiveButton("Yes", (dialogInterface, i) ->
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
            } else {
                val = true;
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(MapsActivity.this, getResources().getString(R.string.loc_not_detect), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            val = false;
        }
        return val;
    }

    private void DisplayDialog() {
        dialogTagCust = new Dialog(this);
        dialogTagCust.setContentView(R.layout.dialog_confirmtag_alert);
        dialogTagCust.setCancelable(false);
        dialogTagCust.show();

        Button btn_confirm = dialogTagCust.findViewById(R.id.btn_confirm);
        Button btn_cancel = dialogTagCust.findViewById(R.id.btn_cancel);
        TextView tv_cust_name = dialogTagCust.findViewById(R.id.txt_cust_name);

        tv_cust_name.setText(cust_name);

        JSONObject jsonImage = new JSONObject();
        try {
            jsonImage.put("tableName", "imgupload");
            jsonImage.put("sfcode", SfCode);
            jsonImage.put("division_code", DivCode);
            if (SfType.equalsIgnoreCase("1")) {
                jsonImage.put("Rsf", SfCode);
            } else {
                jsonImage.put("Rsf", SelectedSFCode);
            }
            jsonImage.put("sf_type", SfType);
            jsonImage.put("Designation", Designation);
            jsonImage.put("state_code", StateCode);
            jsonImage.put("subdivision_code", SubDivisionCode);
        } catch (Exception e) {

        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tableName", "save_geo");
            jsonObject.put("lat", laty);
            jsonObject.put("long", lngy);
            jsonObject.put("cuscode", cust_code);
            jsonObject.put("divcode", DivCode);
            jsonObject.put("cust", Selected);
            jsonObject.put("tagged_time", CommonUtilsMethods.getCurrentInstance() + " " + CommonUtilsMethods.getCurrentTime());
            jsonObject.put("image_name", imageName);
            jsonObject.put("sfname", SfName);
            jsonObject.put("sfcode", SfCode);
            jsonObject.put("addr", mapsBinding.tvTaggedAddress.getText().toString());
            if (SfType.equalsIgnoreCase("1")) {
                jsonObject.put("tagged_cust_HQ", SfCode);
            } else {
                jsonObject.put("tagged_cust_HQ", "");
            }
            jsonObject.put("cust_name", cust_name);
            jsonObject.put("mode", Constants.APP_MODE);
            jsonObject.put("version", Constants.APP_VERSION);
            jsonObject.put("towncode", town_code);
            jsonObject.put("townname", town_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_confirm.setOnClickListener(view -> {
            if (SharedPref.getGeotagImage(MapsActivity.this).equalsIgnoreCase("0")) {
                CallImageAPI(jsonImage.toString(), jsonObject.toString());
            } else {
                CallAPIGeo(jsonObject.toString());
            }
          /*  Intent intent1 = new Intent(MapsActivity.this, TagCustSelectionList.class);
            startActivity(intent1);*/
        });

        btn_cancel.setOnClickListener(view -> {
            dialogTagCust.dismiss();
            mapsBinding.btnTag.setText("Tag");
            mapsBinding.imgRefreshMap.setVisibility(View.GONE);
            mapsBinding.tvTaggedAddress.setVisibility(View.VISIBLE);
            mapsBinding.constraintMid.setVisibility(View.GONE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.rvList.setVisibility(View.GONE);
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

    private void CallAPIGeo(String jsonTag) {
        Call<JsonObject> callSaveGeo = null;
        callSaveGeo = api_interface.saveMapGeoTag(jsonTag);
        callSaveGeo.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true") &&
                                jsonSaveRes.getString("Msg").equalsIgnoreCase("Tagged Successfully")) {
                            Toast.makeText(MapsActivity.this, "Tagged Successfully", Toast.LENGTH_SHORT).show();
                            dialogTagCust.dismiss();
                            CallAPIList(Selected);
                          /*  Intent intent1 = new Intent(MapsActivity.this, TagCustSelectionList.class);
                            intent1.putExtra("cus_pos", position);*/
                            SharedPref.setTaggedSuccessfully(MapsActivity.this, "true");
                            finish();
                            //startActivity(intent1);
                        } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") &&
                                jsonSaveRes.getString("Msg").equalsIgnoreCase("You have reached the maximum tags...")) {
                            Toast.makeText(MapsActivity.this, jsonSaveRes.getString("Msg"), Toast.LENGTH_SHORT).show();
                            dialogTagCust.dismiss();
                        } else {
                            Toast.makeText(MapsActivity.this, "Something went Wrong! Please Try Again", Toast.LENGTH_SHORT).show();
                            dialogTagCust.dismiss();
                        }
                    } catch (Exception e) {
                        dialogTagCust.dismiss();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                    dialogTagCust.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialogTagCust.dismiss();
            }
        });

    }

    private void CallAPIList(String CustSelected) {
        String sfCode;
        if (SfType.equalsIgnoreCase("1")) {
            sfCode = SfCode;
        } else {
            sfCode = SelectedSFCode;
        }
        if (CustSelected.equalsIgnoreCase("D")) {
            MasterSyncActivity.callList(sqLite, api_interface, getApplicationContext(), "Doctor", "getdoctors", SfCode, DivCode, sfCode, SfType, Designation, StateCode, SubDivisionCode);
        } else if (CustSelected.equalsIgnoreCase("C")) {
            MasterSyncActivity.callList(sqLite, api_interface, getApplicationContext(), "Chemist", "getchemist", SfCode, DivCode, sfCode, SfType, Designation, StateCode, SubDivisionCode);
        } else if (CustSelected.equalsIgnoreCase("S")) {
            MasterSyncActivity.callList(sqLite, api_interface, getApplicationContext(), "Stockiest", "getstockist", SfCode, DivCode, sfCode, SfType, Designation, StateCode, SubDivisionCode);
        } else if (CustSelected.equalsIgnoreCase("U")) {
            MasterSyncActivity.callList(sqLite, api_interface, getApplicationContext(), "Unlisted_Doctor", "getunlisteddr", SfCode, DivCode, sfCode, SfType, Designation, StateCode, SubDivisionCode);
        }
    }

    private void CallImageAPI(String jsonImage, String jsonTag) {
        try {
            Call<JsonObject> callImage = null;
            HashMap<String, RequestBody> values = field(jsonImage);
            MultipartBody.Part imgg = convertimg("UploadImg", filePath);
            callImage = api_interface.imgUploadMap(values, imgg);

            callImage.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.v("img_tag", response + "---" + response.body().toString() + "---" + response.message() + "---" + call);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonImgRes = new JSONObject(response.body().toString());
                            Log.v("img_tag", jsonImgRes.getString("success"));
                            if (jsonImgRes.getString("success").equalsIgnoreCase("true") &&
                                    jsonImgRes.getString("msg").equalsIgnoreCase("Photo Has Been Updated")) {
                                CallAPIGeo(jsonTag);
                            } else {
                                dialogTagCust.dismiss();
                                Toast.makeText(MapsActivity.this, "Tag Failed! Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.v("img_tag", e.toString());
                            dialogTagCust.dismiss();
                        }
                    } else {
                        dialogTagCust.dismiss();
                        Toast.makeText(MapsActivity.this, "Something went Wrong! Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialogTagCust.dismiss();
                }
            });
        } catch (Exception e) {
            dialogTagCust.dismiss();
        }
    }

    @Override
    protected void onResume() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, true);
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(MapsActivity.this);
        }
        commonUtilsMethods.FullScreencall();
        super.onResume();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.v("hhh", "---");
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        laty = gpsTrack.getLatitude();
        lngy = gpsTrack.getLongitude();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f));
        if (from_tagging.equalsIgnoreCase("tagging")) {
            Log.v("hhh", "-000--");
            mapsBinding.btnTag.setText("Tag");
            mapsBinding.tagSelection.setText("Tagging" + " -- > " + cust_name);
            mapsBinding.tvTaggedAddress.setVisibility(View.VISIBLE);
            mapsBinding.constraintMid.setVisibility(View.GONE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.rvList.setVisibility(View.GONE);

            LatLng latLng = new LatLng(laty, lngy);
            marker = mMap.addMarker(new MarkerOptions().position(latLng).
                    icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);

            mMap.setOnCameraMoveListener(() -> {
                laty = mMap.getCameraPosition().target.latitude;
                lngy = mMap.getCameraPosition().target.longitude;
                mapsBinding.tvTaggedAddress.setText(CommonUtilsMethods.gettingAddress(MapsActivity.this, laty, lngy, false));
            });

        } else if (from_tagging.equalsIgnoreCase("not_tagging")) {
            Log.v("hhh", "-111--" + Selected + "--" + SfType + "---" + SfCode + "---" + SelectedSFCode);
            if (SfType.equalsIgnoreCase("1")) {
                TabSelected(Selected, SfCode);
            } else {
                TabSelected(Selected, SelectedSFCode);
            }
            /*mapsBinding.tvTaggedAddress.setVisibility(View.GONE);
            mapsBinding.constraintMid.setVisibility(View.VISIBLE);
            mapsBinding.imgRvRight.setVisibility(View.VISIBLE);
            mapsBinding.rvList.setVisibility(View.VISIBLE);*/

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
        }
    }

    private void AddTaggedDetails(String selected, String sfCode) {
        taggedMapListArrayList.clear();
        mMap.clear();
        list.clear();
        gpsTrack = new GPSTrack(this);
        laty = gpsTrack.getLatitude();
        lngy = gpsTrack.getLongitude();
        sqLiteHandler.open();
        Log.v("final_assigned", "-111--" + selected + "--" + SfType + "---" + SfCode + "---" + SelectedSFCode);
        switch (selected) {
            case "D":
                try {
                  /*  mCursor = sqLiteHandler.select_master_list("Doctor_" + sfCode);
                    Log.v("ggg", "count---" + mCursor.getCount());
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }
                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Doctor_" + sfCode);
                    Log.v("ggg", "len---" + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("Addrs");
                        Log.v("ggg", jsonObject.getString("Name"));
                        if (!jsonObject.getString("Lat").trim().isEmpty() || !jsonObject.getString("Long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"),"1", jsonObject.getString("Lat"),
                                        jsonObject.getString("Long"), cust_address, jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("Lat").equalsIgnoreCase("0.0") || jsonObject.getString("Long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"),"1", jsonObject.getString("Lat"),
                                            jsonObject.getString("Long"), cust_address, jsonObject.getString("img_name"),
                                            jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.v("error", "error---dr-" + e);
                }
                break;
            case "C":
                try {
                 /*   mCursor = sqLiteHandler.select_master_list("Chemist_" + sfCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }
                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Chemist_" + sfCode);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addrs");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "2",jsonObject.getString("lat"),
                                        jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "2",jsonObject.getString("lat"),
                                            jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                            jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
                break;
            case "S":
                try {
                   /* mCursor = sqLiteHandler.select_master_list("Stockiest_" + sfCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Stockiest_" + sfCode);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addrs");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "3",jsonObject.getString("lat"),
                                        jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "3",jsonObject.getString("lat"),
                                            jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                            jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
                break;
            case "U":
                try {
                   /* mCursor = sqLiteHandler.select_master_list("Unlisted_Doctor_" + sfCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Unlisted_Doctor_" + sfCode);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addr");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), "4",jsonObject.getString("lat"),
                                        jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"),"4", jsonObject.getString("lat"),
                                            jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("img_name"),
                                            jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
                break;
        }

        try {
            for (int i = 0; i < list.size(); i++) {
                mm = list.get(i);
                LatLng latLng = new LatLng(Double.parseDouble(mm.getLat()), Double.parseDouble(mm.getLng()));
                LatLng latlngCur = new LatLng(laty, lngy);
                float[] distance = new float[2];
                Location.distanceBetween(Double.parseDouble(mm.getLat()), Double.parseDouble(mm.getLng()), laty, lngy, distance);

                if (distance[0] < limitKm * 1000.0) {
                    taggedMapListArrayList.add(new TaggedMapList(mm.getName(),mm.getType(), mm.getAddress(), mm.getCode(), false, getDistanceMeteres(laty, lngy, Double.parseDouble(mm.getLat()), Double.parseDouble(mm.getLng()))));
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f));
                marker = mMap.addMarker(new MarkerOptions().position(latLng).snippet(mm.getName() + "&" + mm.getAddress() + "$" + mm.getCode() + "^" + mm.getImageName()).
                        icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));

                Log.v("map_camera_tt", mm.getName());
                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(mm, MapsActivity.this));
            }

            if (SharedPref.getGeotagImage(MapsActivity.this).equalsIgnoreCase("0")) {
                mMap.setOnInfoWindowClickListener(marker -> {
                    Dialog dialog = new Dialog(MapsActivity.this);
                    dialog.setContentView(R.layout.map_img_layout);
                    ImageView imageView = dialog.findViewById(R.id.img_dr_content);
                    ImageView imgclose = dialog.findViewById(R.id.imgBtn_close);

                    if (marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1).isEmpty()) {
                        Toast.makeText(MapsActivity.this, getResources().getString(R.string.toast_no_img_found), Toast.LENGTH_SHORT).show();
                    } else {
                        if (img_url.equalsIgnoreCase("null")
                                || img_url.isEmpty()) {
                            Toast.makeText(MapsActivity.this, "Kindly Save Settings in Configuration Screen", Toast.LENGTH_SHORT).show();
                        } else {
                            Glide.with(getApplicationContext())
                                    .load(img_url + "photos/" + marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1))
                                    .into(imageView);
                            dialog.show();
                        }
                    }
                    imgclose.setOnClickListener(v -> dialog.dismiss());
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

            Log.v("hhh", "---" + taggedMapListArrayList.size());
            if (taggedMapListArrayList.size() > 0) {
                mapsBinding.rvList.setVisibility(View.VISIBLE);
                mapsBinding.imgRvRight.setVisibility(View.VISIBLE);
                mapsBinding.imgRvLeft.setVisibility(View.GONE);
            } else {
                mapsBinding.rvList.setVisibility(View.GONE);
                mapsBinding.imgRvRight.setVisibility(View.GONE);
                mapsBinding.imgRvLeft.setVisibility(View.VISIBLE);
            }

            addCircle(mMap);
        } catch (Exception e) {
            Log.v("map_camera_tt", "error----" + e);
        }
    }


    private double getDistanceMeteres(double Curlaty, double Curlngy, double Custlaty, double CustLngt) {
        //distanceTag = SphericalUtil.computeDistanceBetween(latlngCur, latLng);
        distanceTag = distance(Curlaty, Curlngy, Custlaty, CustLngt);
        distanceTag = milesToMeters(distanceTag);
        DecimalFormat decfor = new DecimalFormat("0.00");
        distanceTag = Double.valueOf(decfor.format(distanceTag));
        Log.v("distance", "--meters--" + decfor.format(distanceTag));
        return distanceTag;
    }

    private double distance(double lat1, double lon1, double lat2, double
            lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
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

    public BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}