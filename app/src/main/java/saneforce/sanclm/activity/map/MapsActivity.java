package saneforce.sanclm.activity.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.GPSTrack;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.map.custSelection.TagCustSelectionList;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.databinding.ActivityMapsBinding;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityMapsBinding mapsBinding;
    TaggingAdapter taggingAdapter;
    GPSTrack gpsTrack;
    String from = "", tv_custName = "", laty = "", lngy = "";
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

        //  setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        gpsTrack = new GPSTrack(this);

        commonUtilsMethods = new CommonUtilsMethods(this);
        mCommonSharedPrefrence = new CommonSharedPreference(this);

        commonUtilsMethods.FullScreencall();

    /*    tv_doctor = findViewById(R.id.tag_tv_doctor);
        tv_chemist = findViewById(R.id.tag_tv_chemist);
        tv_stockist = findViewById(R.id.tag_tv_stockist);
        tv_undr = findViewById(R.id.tag_tv_undr);
        backArrow = findViewById(R.id.iv_back);

        constraintShowCustList = findViewById(R.id.constraint_mid);

        img_arrow_right = findViewById(R.id.img_rv_right);
        img_arrow_left = findViewById(R.id.img_rv_left);
        iv_back = findViewById(R.id.iv_back);
        img_cur_loc = findViewById(R.id.img_cur_loc);
        img_refresh = findViewById(R.id.img_refresh_map);
        tv_tag_addr = findViewById(R.id.tv_tagged_address);
        btn_tag = findViewById(R.id.btn_tag);
        rv_list = findViewById(R.id.rv_list);
        view_one = findViewById(R.id.view_one);
        view_two = findViewById(R.id.view_two);*/
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

     /*   backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(MapsActivity.this, HomeDashBoard.class));
            }
        });*/

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
            mapsBinding.tagTvDoctor.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
        });

        mapsBinding.tagTvChemist.setOnClickListener(view -> {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(null);
        });

        mapsBinding.tagTvStockist.setOnClickListener(view -> {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            mapsBinding.tagTvUndr.setBackground(null);
        });

        mapsBinding.tagTvUndr.setOnClickListener(view -> {
            mapsBinding.tagTvDoctor.setBackground(null);
            mapsBinding.tagTvChemist.setBackground(null);
            mapsBinding.tagTvStockist.setBackground(null);
            mapsBinding.tagTvUndr.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
        });

        mapsBinding.imgCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f));
            }
        });
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTrack.getLatitude(), gpsTrack.getLongitude()), 15.0f));
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

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        mMap.setOnCameraMoveListener(() -> {
            Log.v("centerLat_move", mMap.getCameraPosition().target.latitude + "");
            laty = String.valueOf(mMap.getCameraPosition().target.latitude);
            lngy = String.valueOf(mMap.getCameraPosition().target.longitude);
        });
    }
}