package saneforce.santrip.activity.reports.dayReport;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import saneforce.santrip.R;
import saneforce.santrip.databinding.ActivityBinding;
import saneforce.santrip.databinding.MapViewActivityBinding;


public class MapViewActvity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    MapViewActivityBinding binding;

    double CHECKINLat=0.0,CheckINLong=0.0, CheckOUTLat=0.0,CheckOUTLong=0.0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapViewActivityBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        binding.backArrow.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            CHECKINLat = Double.valueOf(extra.getString("INLat"));
            CheckINLong = Double.valueOf(extra.getString("INLong"));
            CheckOUTLat = Double.valueOf(extra.getString("OUTLat"));
            CheckOUTLong = Double.valueOf(extra.getString("OUTLong"));
        }


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 14.0f;



        if(CHECKINLat!=0.0&&CheckINLong!=0.0){
            LatLng yourLocation = new LatLng(CHECKINLat, CheckINLong);
            mMap.addMarker(new MarkerOptions().position(yourLocation).title("CHECK IN").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation,zoomLevel));
        }


        if(CHECKINLat!=0.0&&CheckINLong!=0.0){
            LatLng yourLocation = new LatLng(CheckOUTLat, CheckOUTLong);
            mMap.addMarker(new MarkerOptions().position(yourLocation).title("CHECK OUT").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
        }






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
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

    }
}
