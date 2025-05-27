package saneforce.sanzen.activity.myresource;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static saneforce.sanzen.activity.map.MapsActivity.BitmapFromVector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import saneforce.sanzen.R;
//import saneforce.sanzen.activity.call.dcrCallSelection.ChemistAddtion;
import saneforce.sanzen.activity.call.dcrCallSelection.MapsAddition;
import saneforce.sanzen.activity.call.dcrCallSelection.UnlistedDoctorAddition;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.databinding.ActivityMapViewBinding;
import saneforce.sanzen.databinding.ActivityMapsadditionBinding;
import saneforce.sanzen.storage.SharedPref;

public class MapView extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    ActivityMapViewBinding activitymapview;
    CommonUtilsMethods commonUtilsMethods;
    public static GoogleMap mMap;
    GPSTrack gpsTrack;
    double lat, lng;
    String valueFrom="",Latitude="",Longitude="";
    LocationManager locationManager;
    public static Marker marker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitymapview = ActivityMapViewBinding.inflate(getLayoutInflater());
        setContentView(activitymapview.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        gpsTrack = new GPSTrack(this);
//        lat = gpsTrack.getLatitude();
//        lng = gpsTrack.getLongitude();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            Latitude=extra.getString("Lat");
            Longitude=extra.getString("Long");
            lat= Double.parseDouble(Latitude);
            lng= Double.parseDouble(Longitude);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonAlertBox.permissionChangeAlert(this);
        }
        locationCheck();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        activitymapview.imgRefreshMap.setVisibility(View.VISIBLE);
        activitymapview.imgCurLoc.setVisibility(View.VISIBLE);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.v("position", String.valueOf(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 18.0f));
        activitymapview.tvTaggedAddress.setText(marker.getSnippet());
        return true;
    }

    @SuppressLint({"SetTextI18n", "PotentialBehaviorOverride"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        //locationCheck();
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f));


        activitymapview.constraintTaggedView.setVisibility(View.VISIBLE);
        activitymapview.constraintMid.setVisibility(View.INVISIBLE);
        activitymapview.imgRefreshMap.setVisibility(View.VISIBLE);
        activitymapview.imgCurLoc.setVisibility(View.VISIBLE);
        LatLng latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapFromVector(this, R.drawable.marker_map)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.2f));
        activitymapview.tvTaggedAddress.setText(CommonUtilsMethods.gettingAddress(MapView.this, lat, lng, false));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

//        mMap.setOnCameraMoveListener(() -> {
//            lat = mMap.getCameraPosition().target.latitude;
//            lng = mMap.getCameraPosition().target.longitude;
//            activitymapview.tvTaggedAddress.setText(CommonUtilsMethods.gettingAddress(MapView.this, lat, lng, false));
//        });
        activitymapview.ivBack.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
            finish();
        });

//        binding.btnTag.setOnClickListener(view -> {
//            Log.d("SharedPrefDebug", "Retrieved Latitude: " + lat);
//            SharedPref.setSaveLatitude(MapsAddition.this,lat);
//            SharedPref.setSaveLongitutde(MapsAddition.this, lng);
//            SharedPref.setSaveTaggedAddress(MapsAddition.this, binding.tvTaggedAddress.getText().toString());
//            if(valueFrom.equalsIgnoreCase("C")){
//                ChemistAddtion.setAddressText(binding.tvTaggedAddress.getText().toString());
//            }
//            else {
//                UnlistedDoctorAddition.setAddressText(binding.tvTaggedAddress.getText().toString());
//            }
//            commonUtilsMethods.showToastMessage(MapsAddition.this, getString(R.string.location_add));
//            finish();
//        });
//        activitymapview.imgRefreshMap.setOnClickListener(view -> {
//
//            if (CurrentLoc()) {
//                lat = gpsTrack.getLatitude();
//                lng = gpsTrack.getLongitude();
//                LatLng latLng1 = new LatLng(lat, lng);
//                mMap.addMarker(new MarkerOptions().position(latLng1).icon(BitmapFromVector(this, R.drawable.marker_map)));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.2f));
//                binding.tvTaggedAddress.setText(CommonUtilsMethods.gettingAddress(MapsAddition.this, lat, lng, false));
//            }
//        });
//        binding.imgCurLoc.setOnClickListener(view -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 16.2f)));


    }
    //    public boolean CurrentLoc() {
//        boolean val = false;
//        gpsTrack = new GPSTrack(MapsAddition.this);
//        try {
//            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                new android.app.AlertDialog.Builder(MapsAddition.this).setTitle("Alert") // GPS not found
//                        .setCancelable(false).setMessage("Activate the Gps to proceed further") // Want to enable?
//                        .setPositiveButton("Yes", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
//            } else {
//                val = true;
//            }
//        } catch (Exception e) {
//            commonUtilsMethods.showToastMessage(MapsAddition.this, getString(R.string.loc_not_detect));
//        }
//        return val;
//    }
    private void locationCheck() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
           ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if(!CheckLocPermission()){
                RequestLocationPermission();
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(MapView.this);
        }
    }
    private void RequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(MapView.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapView.this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapView.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            } else {
                ActivityCompat.requestPermissions(MapView.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            }
        }
    }
    public boolean CheckLocPermission() {
        int FineLocation = ContextCompat.checkSelfPermission(MapView.this, ACCESS_FINE_LOCATION);
        int CoarseLocation = ContextCompat.checkSelfPermission(MapView.this, ACCESS_COARSE_LOCATION);
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
                CommonUtilsMethods. RequestGPSPermission(MapView.this,"Location");
            }
        }
    }

}
