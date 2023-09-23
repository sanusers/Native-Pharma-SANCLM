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
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
    private final int transparent = 0x17000000;
    ViewTagModel mm = null;
    Marker marker;
    ActivityMapsBinding mapsBinding;
    TaggingAdapter taggingAdapter;
    LocationManager locationManager;
    GPSTrack gpsTrack;
    Cursor mCursor;
    SQLiteHandler sqLiteHandler;
    String getCustListDB;
    String from = "", tv_custName = "", l;
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
        limitKm = Double.parseDouble(SharedPref.getGeofencingCircleRadius(MapsActivity.this));
        if (SharedPref.getMapSelectedTab(MapsActivity.this).isEmpty()) {
            SharedPref.setMapSelectedTab(MapsActivity.this, "D");
        }
        Log.v("map_selected_tab", SharedPref.getMapSelectedTab(MapsActivity.this));

        dummyAdapter();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            tv_custName = extra.getString("cust_name");
            from = extra.getString("from");
            if (!from.isEmpty()) {
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
            }
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
            if (from.equalsIgnoreCase("tag_adapter")) {
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
            SharedPref.setMapSelectedTab(MapsActivity.this, "D");
            AddTaggedDetails("D");
            mapsBinding.tagTvDoctor.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
        });

        mapsBinding.tagTvChemist.setOnClickListener(view -> {
            SharedPref.setMapSelectedTab(MapsActivity.this, "C");
            AddTaggedDetails("C");
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
        });

        mapsBinding.tagTvStockist.setOnClickListener(view -> {
            SharedPref.setMapSelectedTab(MapsActivity.this, "S");
            AddTaggedDetails("S");
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvUndr.setBackground(null);
        });

        mapsBinding.tagTvUndr.setOnClickListener(view -> {
            SharedPref.setMapSelectedTab(MapsActivity.this, "U");
            AddTaggedDetails("U");
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
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
                    Log.v("Location Updates1", laty + " lngy " + lngy);
                    CommonUtilsMethods.gettingAddress(MapsActivity.this, Double.parseDouble(String.valueOf(laty)), Double.parseDouble(String.valueOf(lngy)));
                }
                mMap.clear();
                LatLng latLng = new LatLng(laty, lngy);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                addCircle(mMap);
            }
        });

        mapsBinding.imgCurLoc.setOnClickListener(view -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f)));
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

    private void dummyAdapter() {
        ArrayList<TaggedMapList> taggedMapListArrayList = new ArrayList<>();
        TaggedMapList taggedMapList = new TaggedMapList("Aasik", "23,B Kaja Thoppu, Thennur, Trichy - 620017");
        TaggedMapList taggedMapList1 = new TaggedMapList("Venkat", "67/b Maildamy Road, 3rd Street, Cuddalore - 627889");
        TaggedMapList taggedMapList2 = new TaggedMapList("Aravindh", "No,31, 3rd Indra Street, Kanu Nagar, Nesapakkam, Chennai - 600008");
        TaggedMapList taggedMapList3 = new TaggedMapList("Surya", "78/98 Aravindhan Nagar, Fork Road, 1st Street, Hyderabad - 987666");
        TaggedMapList taggedMapList4 = new TaggedMapList("Aathif Mirza", "No: 56/87, Walaja Street, Annai terasa Road, Ground Floor, Madurai - 650001");
        taggedMapListArrayList.add(taggedMapList);
        taggedMapListArrayList.add(taggedMapList1);
        taggedMapListArrayList.add(taggedMapList2);
        taggedMapListArrayList.add(taggedMapList3);
        taggedMapListArrayList.add(taggedMapList4);

        taggingAdapter = new TaggingAdapter(MapsActivity.this, taggedMapListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mapsBinding.rvList.setLayoutManager(mLayoutManager);
        mapsBinding.rvList.setItemAnimator(new DefaultItemAnimator());
        mapsBinding.rvList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mapsBinding.rvList.setAdapter(taggingAdapter);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        laty = gpsTrack.getLatitude();
        lngy = gpsTrack.getLongitude();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        AddTaggedDetails(SharedPref.getMapSelectedTab(MapsActivity.this));

        mMap.setOnCameraMoveListener(() -> {
            Log.v("centerLat_move", mMap.getCameraPosition().target.latitude + "");
            laty = mMap.getCameraPosition().target.latitude;
            lngy = mMap.getCameraPosition().target.longitude;
        });
    }

    private void AddTaggedDetails(String Selected) {
        mMap.clear();
        list.clear();
        sqLiteHandler.open();
        switch (Selected) {
            case "D":
                try {
                    mCursor = sqLiteHandler.select_master_list("Doctor");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }
                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("Lat"),
                                jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("img_name"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                    }
                } catch (Exception e) {

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
                        list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
                                jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
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
                        list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
                                jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("img_name"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
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
                        list.add(new ViewTagModel(jsonObject.getString("Code"), jsonObject.getString("Name"), jsonObject.getString("lat"),
                                jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("img_name"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code")));
                    }
                } catch (Exception e) {

                }
                break;
        }

        addCircle(mMap);
        try {
            for (int i = 0; i < list.size(); i++) {
                mm = list.get(i);
                LatLng latLng = new LatLng(Double.parseDouble(mm.getLat()), Double.parseDouble(mm.getLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f));
                marker = mMap.addMarker(new MarkerOptions().position(latLng).snippet(mm.getName() + "&" + mm.getAddress() + "^" + mm.getImageName()).
                        icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)));
                Log.v("map_camera_tt", mm.getImageName());
                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(mm, MapsActivity.this));
            }


        } catch (Exception e) {
            Log.v("map_camera_tt", e.toString());
        }

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