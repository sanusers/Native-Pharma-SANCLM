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
import android.os.Bundle;
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
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.map.custSelection.TagCustSelectionList;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.GPSTrack;
import saneforce.sanclm.databinding.ActivityMapsBinding;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static ArrayList<ViewTagModel> list = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static ActivityMapsBinding mapsBinding;
    @SuppressLint("StaticFieldLeak")
    public static TaggingAdapter taggingAdapter;
    public static ArrayList<TaggedMapList> taggedMapListArrayList = new ArrayList<>();
    private final int transparent = 0x17000000;
    ViewTagModel mm = null;
    Double distanceTag;
    Marker marker;
    LocationManager locationManager;
    GPSTrack gpsTrack;
    Cursor mCursor;
    SQLiteHandler sqLiteHandler;
    String getCustListDB, SfType, SfCode, img_url, cust_address, Selected;
    String from_tagging = "", tv_custName = "", l;
    double laty, lngy, limitKm = 0.5;
    Dialog dialogTagCust;
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference mCommonSharedPrefrence;
    private GoogleMap mMap;


    @Override
    public void onBackPressed() {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapsBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(mapsBinding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        sqLiteHandler = new SQLiteHandler(this);


        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(this);
        mCommonSharedPrefrence = new CommonSharedPreference(this);

        Selected = SharedPref.getMapSelectedTab(MapsActivity.this);
        limitKm = Double.parseDouble(SharedPref.getGeofencingCircleRadius(MapsActivity.this));
        SfType = SharedPref.getSfType(MapsActivity.this);
        SfCode = SharedPref.getSfCode(MapsActivity.this);
        img_url = SharedPref.getTagImageUrl(MapsActivity.this);


        Log.v("map_selected_tab", SharedPref.getTagImageUrl(MapsActivity.this));
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            tv_custName = extra.getString("cust_name");
            from_tagging = extra.getString("from");
      /*      if (!from.isEmpty()) {
                if (from.equalsIgnoreCase("tag_adapter")) {
                    mapsBinding.btnTag.setText("Tag");
                    mapsBinding.tvTaggedAddress.setVisibility(View.VISIBLE);
                    mapsBinding.constraintMid.setVisibility(View.GONE);
                    mapsBinding.imgRvRight.setVisibility(View.GONE);
                    mapsBinding.rvList.setVisibility(View.GONE);
                } else if (from.equalsIgnoreCase("cust_sel_list")) {
                    mapsBinding.tvTaggedAddress.setVisibility(View.GONE);
                    mapsBinding.constraintMid.setVisibility(View.VISIBLE);
                    mapsBinding.imgRvRight.setVisibility(View.VISIBLE);
                    mapsBinding.rvList.setVisibility(View.VISIBLE);
                }
            }*/
        }

        mapsBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (from.equalsIgnoreCase("tag_adapter")) {
                    startActivity(new Intent(MapsActivity.this, TagCustSelectionList.class));
                } else {*/
                startActivity(new Intent(MapsActivity.this, HomeDashBoard.class));
                //    }

            }
        });


        mapsBinding.btnTag.setOnClickListener(view -> {
            if (from_tagging.equalsIgnoreCase("tagging")) {
                mapsBinding.imgRefreshMap.setVisibility(View.GONE);
                DisplayDialog();
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
            TabSelected("D");
          /*  SharedPref.setMapSelectedTab(MapsActivity.this, "D");
            AddTaggedDetails("D");
            mapsBinding.tagTvDoctor.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);*/
        });

        mapsBinding.tagTvChemist.setOnClickListener(view -> {
            TabSelected("C");
         /*   SharedPref.setMapSelectedTab(MapsActivity.this, "C");
            AddTaggedDetails("C");
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);*/
        });

        mapsBinding.tagTvStockist.setOnClickListener(view -> {
            TabSelected("S");
           /* SharedPref.setMapSelectedTab(MapsActivity.this, "S");
            AddTaggedDetails("S");
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvUndr.setBackground(null);*/
        });

        mapsBinding.tagTvUndr.setOnClickListener(view -> {
            TabSelected("U");
          /*  SharedPref.setMapSelectedTab(MapsActivity.this, "U");
            AddTaggedDetails("U");
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));*/
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            }
        });

        mapsBinding.imgCurLoc.setOnClickListener(view -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f)));
    }

    private void TabSelected(String CustSelected) {
        SharedPref.setMapSelectedTab(MapsActivity.this, CustSelected);
        AddTaggedDetails(CustSelected);
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

        tv_cust_name.setText(tv_custName);

        btn_confirm.setOnClickListener(view -> {
            dialogTagCust.dismiss();
            Intent intent1 = new Intent(MapsActivity.this, TagCustSelectionList.class);
            startActivity(intent1);
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
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        laty = gpsTrack.getLatitude();
        lngy = gpsTrack.getLongitude();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        if (from_tagging.equalsIgnoreCase("tagging")) {
            mapsBinding.btnTag.setText("Tag");
            mapsBinding.tvTaggedAddress.setVisibility(View.VISIBLE);
            mapsBinding.constraintMid.setVisibility(View.GONE);
            mapsBinding.imgRvRight.setVisibility(View.GONE);
            mapsBinding.rvList.setVisibility(View.GONE);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f));
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
            TabSelected(Selected);
            mapsBinding.tvTaggedAddress.setVisibility(View.GONE);
            mapsBinding.constraintMid.setVisibility(View.VISIBLE);
            mapsBinding.imgRvRight.setVisibility(View.VISIBLE);
            mapsBinding.rvList.setVisibility(View.VISIBLE);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
        }
    }

    private void AddTaggedDetails(String selected) {
        taggedMapListArrayList.clear();
        mMap.clear();
        list.clear();
        gpsTrack = new GPSTrack(this);
        laty = gpsTrack.getLatitude();
        lngy = gpsTrack.getLongitude();
        sqLiteHandler.open();
        switch (selected) {
            case "D":
                try {
                    if (SfType.equalsIgnoreCase("1")) {
                        mCursor = sqLiteHandler.select_doctor_list(SfCode);
                    } else {

                    }
                    //    mCursor = sqLiteHandler.select_master_list("Doctor");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }
                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    Log.v("ggg", "len---" + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("Addrs");
                        Log.v("ggg", jsonObject.getString("Name"));
                        if (!jsonObject.getString("Lat").trim().isEmpty() || !jsonObject.getString("Long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("Lat"),
                                        jsonObject.getString("Long"), cust_address, jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("Lat").equalsIgnoreCase("0.0") || jsonObject.getString("Long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("Lat"),
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
                    mCursor = sqLiteHandler.select_master_list("Chemist");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }
                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addrs");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
                                        jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
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
                    mCursor = sqLiteHandler.select_master_list("Stockiest");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addrs");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
                                        jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
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
                    mCursor = sqLiteHandler.select_master_list("Unlisted_Doctor");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        cust_address = jsonObject.getString("addr");
                        if (!jsonObject.getString("lat").trim().isEmpty() || !jsonObject.getString("long").trim().isEmpty()) {
                            if (!cust_address.isEmpty()) {
                                list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
                                        jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("img_name"),
                                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                            } else {
                                if (jsonObject.getString("lat").equalsIgnoreCase("0.0") || jsonObject.getString("long").equalsIgnoreCase("0.0")) {
                                    cust_address = "No Address Found";
                                } else {
                                    cust_address = CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), false);
                                    list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
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
                float[] distance = new float[2];
                Location.distanceBetween(Double.parseDouble(mm.getLat()), Double.parseDouble(mm.getLng()), laty, lngy, distance);

                if (distance[0] < limitKm * 1000.0) {
                    taggedMapListArrayList.add(new TaggedMapList(mm.getName(), mm.getAddress(), mm.getCode(), false));
                }


                // Log.v("distance", "----" + distance(Double.parseDouble(mm.getLat()), Double.parseDouble(mm.getLng()), laty, lngy));
                LatLng latlngCur = new LatLng(laty, lngy);
                distanceTag = SphericalUtil.computeDistanceBetween(latlngCur, latLng);
                Log.v("distance", "----" + distanceTag);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f));
               /* marker = mMap.addMarker(new MarkerOptions().position(latLng).snippet(mm.getName() + "&" + mm.getAddress() + "^" + mm.getImageName()).
                        icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));*/
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

        /*    taggingAdapter.setOnItemClickListerner(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v("gkkk", "sdds" + adapterView.toString() + "---" + view.toString());
                }
            });*/


            addCircle(mMap);
        } catch (Exception e) {
            Log.v("map_camera_tt", "error----" + e);
        }

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
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        /*vectorDrawable.setBounds(
                0, 0, 40,
                40);*/

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}