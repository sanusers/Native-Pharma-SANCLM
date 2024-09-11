package saneforce.sanzen.activity.myresource;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import static java.lang.Double.parseDouble;
import static java.lang.Double.valueOf;

import static saneforce.sanzen.activity.map.MapsActivity.BitmapFromVector;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.map.MapsActivity;
import saneforce.sanzen.activity.myresource.myresourcemodel.ResourcerviewModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.databinding.ActivityMyResourceMapviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class MyResource_mapview extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GPSTrack gpsTrack;
    String DCR_CODE, HQ_CODE, CUST_FLAG, ImageName = "";
    double CurrentLat, CurrentLong, limitKm, distanceTag;
    private GoogleMap mMap;
    Marker marker;
    ArrayList<ResourcerviewModelClass> loclist = new ArrayList<>();
    ActivityMyResourceMapviewBinding binding;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyResourceMapviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gpsTrack = new GPSTrack(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        DCR_CODE = getIntent().getStringExtra("DCR_CODE");
        HQ_CODE = getIntent().getStringExtra("HQ_CODE");
        CUST_FLAG = getIntent().getStringExtra("CUST_FLAG");
        limitKm = Double.parseDouble(SharedPref.getDisRad(this));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CurrentLat = gpsTrack.getLatitude();
        CurrentLong = gpsTrack.getLongitude();

        if (SharedPref.getGeotagImg(this).equalsIgnoreCase("0")) {
            binding.viewImg.setVisibility(View.VISIBLE);
        } else {
            binding.viewImg.setVisibility(View.GONE);
        }

        binding.mapbackArrow.setOnClickListener(v -> {
            finish();
        });

        binding.viewImg.setOnClickListener(v -> {
            showImagePopup();
        });

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        double getDistance = getDistanceMeters(CurrentLat, CurrentLong, marker.getPosition().latitude, marker.getPosition().longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 18.0f));

        if(marker.getSnippet() != null) {
            String[] snippetData = marker.getSnippet().split("@");
            if(snippetData.length>0) {
                ImageName = snippetData[1];
                binding.address.setText(snippetData[0]);
                if(getDistance>1000) {
                    getDistance = getDistance / 1000;
                    DecimalFormat decFor = new DecimalFormat("0.00");
                    getDistance = parseDouble(decFor.format(getDistance));
                    binding.distance.setText(String.valueOf(getDistance));
                    binding.disName.setText("Km");
                }else {
                    binding.distance.setText(String.valueOf(getDistance));
                    binding.disName.setText("Meters");
                }
            }
        }

        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (CUST_FLAG.equalsIgnoreCase("D")) {
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + HQ_CODE).getMasterSyncDataJsonArray().toString());
        } else if (CUST_FLAG.equalsIgnoreCase("C")) {
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + HQ_CODE).getMasterSyncDataJsonArray().toString());
        } else if (CUST_FLAG.equalsIgnoreCase("S")) {
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + HQ_CODE).getMasterSyncDataJsonArray().toString());
        } else if (CUST_FLAG.equalsIgnoreCase("U")) {
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + HQ_CODE).getMasterSyncDataJsonArray().toString());
        }

    }
    private void parseJsonData(String jsonResponse) {
        try {
            if(mMap != null) {
                mMap.clear();
            }
            JSONArray jsonArray = new JSONArray(jsonResponse);
            if (jsonArray.length() > 0) {
                loclist.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String docid = jsonObject1.optString("Code");
                    if (DCR_CODE.equals(docid)) {
                        if (CUST_FLAG.equalsIgnoreCase("D")) {
                            loclist.add(new ResourcerviewModelClass(jsonObject1.optString("Name"), jsonObject1.optString("Code"), jsonObject1.optString("Lat"), jsonObject1.optString("Long"), jsonObject1.optString("Addrs"), jsonObject1.optString("Town_Name"), jsonObject1.optString("img_name")));
                        } else if (CUST_FLAG.equalsIgnoreCase("C")) {
                            loclist.add(new ResourcerviewModelClass(jsonObject1.optString("Name"), jsonObject1.optString("Code"), jsonObject1.optString("lat"), jsonObject1.optString("long"), jsonObject1.optString("addrs"), jsonObject1.optString("Town_Name"), jsonObject1.optString("img_name")));
                        } else if (CUST_FLAG.equalsIgnoreCase("S")) {
                            loclist.add(new ResourcerviewModelClass(jsonObject1.optString("Name"), jsonObject1.optString("Code"), jsonObject1.optString("lat"), jsonObject1.optString("long"), jsonObject1.optString("addrs"), jsonObject1.optString("Town_Name"), jsonObject1.optString("img_name")));
                        } else if (CUST_FLAG.equalsIgnoreCase("U")) {
                            loclist.add(new ResourcerviewModelClass(jsonObject1.optString("Name"), jsonObject1.optString("Code"), jsonObject1.optString("lat"), jsonObject1.optString("long"), jsonObject1.optString("addr"), jsonObject1.optString("Town_Name"), jsonObject1.optString("img_name")));
                        }
                    }
                }

                for (int i = 0; i < loclist.size(); i++) {
                    if (loclist.get(i).getLat() != null && loclist.get(i).getLong().length() > 0) {
                        LatLng sydney = new LatLng(Double.parseDouble(loclist.get(i).getLat()), Double.parseDouble(loclist.get(i).getLong()));
                        LatLng sydney1 = new LatLng(Double.parseDouble(loclist.get(0).getLat()), Double.parseDouble(loclist.get(0).getLong()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(loclist.get(0).getLat()), Double.parseDouble(loclist.get(0).getLong())), 15.0f));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        mMap.setMyLocationEnabled(true);
                        marker = mMap.addMarker(new MarkerOptions().position(sydney).draggable(false).icon(BitmapFromVector(getApplicationContext(), R.drawable.marker_map)).snippet(loclist.get(i).getAdds() + "@" + loclist.get(i).getImageName()));
                        marker.setTag(loclist.get(i));
                        mMap.setOnMarkerClickListener(this);
                        binding.custname.setText(loclist.get(0).getCustname());
                        binding.address.setText(loclist.get(0).getAdds());
                        ImageName = loclist.get(0).getImageName();
                        mMap.setOnMarkerClickListener(this);
                        double getDistance = getDistanceMeters(CurrentLat, CurrentLong, Double.parseDouble(loclist.get(0).getLat()), Double.parseDouble(loclist.get(0).getLong()));
                        if (getDistance > 1000) {
                            getDistance = getDistance / 1000;
                            DecimalFormat decFor = new DecimalFormat("0.00");
                            getDistance = parseDouble(decFor.format(getDistance));
                            binding.distance.setText(String.valueOf(getDistance));
                            binding.disName.setText("Km");
                        } else {
                            binding.distance.setText(String.valueOf(getDistance));
                            binding.disName.setText("Meters");
                        }

                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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

    public static double milesToMeters(double miles) {
        return miles * 1609.344;
    }

    private void showImagePopup() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.map_img_layout);
        ImageView popupImageView = dialog.findViewById(R.id.img_dr_content);
        if (ImageName == null || ImageName.isEmpty() || ImageName.contains("noimage") || ImageName.endsWith(".jpg")) {

        } else {

            String url = SharedPref.getTagImageUrl(context) + "photos/" + ImageName;
            Log.e("Inmge", url);
            Picasso.get().load(url).into(popupImageView);
        }

        dialog.show();
    }


    private void RequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(MyResource_mapview.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MyResource_mapview.this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MyResource_mapview.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            } else {
                ActivityCompat.requestPermissions(MyResource_mapview.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            }
        }
    }

    public boolean CheckLocPermission() {
        int FineLocation = ContextCompat.checkSelfPermission(MyResource_mapview.this, ACCESS_FINE_LOCATION);
        int CoarseLocation = ContextCompat.checkSelfPermission(MyResource_mapview.this, ACCESS_COARSE_LOCATION);
        return FineLocation == PackageManager.PERMISSION_GRANTED && CoarseLocation == PackageManager.PERMISSION_GRANTED;
    }
}
