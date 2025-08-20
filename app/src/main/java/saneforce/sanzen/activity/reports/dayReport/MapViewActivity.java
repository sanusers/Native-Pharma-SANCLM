package saneforce.sanzen.activity.reports.dayReport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.MapViewActivityBinding;


public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapViewActivityBinding binding;
    private String title = "", checkInDateTime = "", checkOutDateTime = "", checkInAddress = "", checkOutAddress = "";
    private double CheckINLat =0.0,CheckINLong=0.0, CheckOUTLat=0.0,CheckOUTLong=0.0;
    private float zoomLevel = 14.0f;
    private boolean isCheckIn = false;
    private Marker checkInMarker, checkOutMarker;
    private CameraUpdate checkInCameraUpdate, checkOutCameraUpdate;
    private CommonUtilsMethods commonUtilsMethods;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapViewActivityBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        commonUtilsMethods = new CommonUtilsMethods(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.backArrow.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
            finish();
        });

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            try {
                if(extra.containsKey("INDateTime") && extra.getString("INDateTime") != null && !extra.getString("INDateTime").isEmpty()) {
                    checkInDateTime = extra.getString("INDateTime");
                }
                if(extra.containsKey("OUTDateTime") && extra.getString("OUTDateTime") != null && !extra.getString("OUTDateTime").isEmpty()) {
                    checkOutDateTime = extra.getString("OUTDateTime");
                }
                if(extra.containsKey("INAddress") && extra.getString("INAddress") != null && !extra.getString("INAddress").isEmpty()) {
                    checkInAddress = extra.getString("INAddress");
                }
                if(extra.containsKey("OUTAddress") && extra.getString("OUTAddress") != null && !extra.getString("OUTAddress").isEmpty()) {
                    checkOutAddress = extra.getString("OUTAddress");
                }
                if(extra.containsKey("title") && extra.getString("title") != null && !extra.getString("title").isEmpty()) {
                    title = extra.getString("title");
                    if(title != null && !title.isEmpty()) {
                        binding.title.setText(title);
                    }
                }
                if(extra.containsKey("INLat") && extra.getString("INLat") != null && !extra.getString("INLat").isEmpty()) {
                    CheckINLat = Double.parseDouble(extra.getString("INLat"));
                }
                if(extra.containsKey("INLong") && extra.getString("INLong") != null && !extra.getString("INLong").isEmpty()) {
                    CheckINLong = Double.parseDouble(extra.getString("INLong"));
                }
                if(extra.containsKey("OUTLat") && extra.getString("OUTLat") != null && !extra.getString("OUTLat").isEmpty()) {
                    CheckOUTLat = Double.parseDouble(extra.getString("OUTLat"));
                }
                if(extra.containsKey("OUTLong") && extra.getString("OUTLong") != null && !extra.getString("OUTLong").isEmpty()) {
                    CheckOUTLong = Double.parseDouble(extra.getString("OUTLong"));
                }
                if(extra.containsKey("ISCheckIN")) {
                    isCheckIn = extra.getBoolean("ISCheckIN", true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        binding.tvInDateTime.setText(checkInDateTime);
        binding.tvInLatLong.setText(String.format("%s , %s", CheckINLat, CheckINLong));
        binding.tvInAddress.setText(checkInAddress);
        if(checkOutAddress.isEmpty()) {
            binding.tvOutDateTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            binding.tvOutLatLong.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            binding.tvOutAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            binding.tvOutDateTime.setText("Not Checked Out");
        } else {
            binding.tvOutDateTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_calendar_month_14, 0, 0, 0);
            binding.tvOutLatLong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_my_location_14, 0, 0, 0);
            binding.tvOutAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_home_pin_14, 0, 0, 0);
            binding.tvOutDateTime.setText(checkOutDateTime);
            binding.tvOutLatLong.setText(String.format("%s , %s", CheckOUTLat, CheckOUTLong));
            binding.tvOutAddress.setText(checkOutAddress);
        }

        binding.llCheckIn.setOnClickListener(view -> {
            if(mMap != null) {
                if(checkInMarker != null) {
                    checkInMarker.remove();
                }
                LatLng yourLocation = new LatLng(CheckINLat, CheckINLong);
                checkInMarker = mMap.addMarker(new MarkerOptions().position(yourLocation).title(getString(R.string.check_in)).icon(BitmapDescriptorFactory.defaultMarker(164.0F)));
                checkInMarker.showInfoWindow();
                checkInCameraUpdate = CameraUpdateFactory.newLatLngZoom(yourLocation, zoomLevel);
                mMap.moveCamera(checkInCameraUpdate);
            } else {
                commonUtilsMethods. showToastMessage(this, "Not Checked In");
            }
        });

        binding.llCheckOut.setOnClickListener(view -> {
            if(mMap != null) {
                if(checkOutMarker != null) {
                    checkOutMarker.remove();
                }
                LatLng yourLocation = new LatLng(CheckOUTLat, CheckOUTLong);
                checkOutMarker = mMap.addMarker(new MarkerOptions().position(yourLocation).title(getString(R.string.check_out)).icon(BitmapDescriptorFactory.defaultMarker(347.05884F)));
                checkOutMarker.showInfoWindow();
                checkOutCameraUpdate = CameraUpdateFactory.newLatLngZoom(yourLocation, zoomLevel);
                mMap.moveCamera(checkOutCameraUpdate);
            } else {
                commonUtilsMethods.showToastMessage(this, "Not Checked Out");
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if(CheckINLat != 0.0 && CheckINLong != 0.0 && isCheckIn){
            LatLng yourLocation = new LatLng(CheckINLat, CheckINLong);
            checkInMarker = mMap.addMarker(new MarkerOptions().position(yourLocation).title(getString(R.string.check_in)).icon(BitmapDescriptorFactory.defaultMarker(164.0F)));
            checkInCameraUpdate = CameraUpdateFactory.newLatLngZoom(yourLocation, zoomLevel);
            mMap.moveCamera(checkInCameraUpdate);
        }

        if(CheckOUTLat != 0.0 && CheckOUTLong != 0.0 && !isCheckIn){
            LatLng yourLocation = new LatLng(CheckOUTLat, CheckOUTLong);
            checkOutMarker = mMap.addMarker(new MarkerOptions().position(yourLocation).title(getString(R.string.check_out)).icon(BitmapDescriptorFactory.defaultMarker(347.05884F)));
            checkOutCameraUpdate = CameraUpdateFactory.newLatLngZoom(yourLocation, zoomLevel);
            mMap.moveCamera(checkOutCameraUpdate);
        }

//        if(isCheckIn) {
//            checkOutMarker.remove();
//            checkInMarker.remove();
//            LatLng yourLocation = new LatLng(CheckINLat, CheckINLong);
//            checkInMarker = mMap.addMarker(new MarkerOptions().position(yourLocation).title(getString(R.string.check_in)).icon(BitmapDescriptorFactory.defaultMarker(164.0F)));
//            checkInCameraUpdate = CameraUpdateFactory.newLatLngZoom(yourLocation,zoomLevel);
//            mMap.moveCamera(checkInCameraUpdate);
//        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
    }
}
