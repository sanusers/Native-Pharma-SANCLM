package saneforce.santrip.activity.myresource;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import saneforce.santrip.R;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class MyResource_mapview extends FragmentActivity implements  OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    LinearLayout mapbackArrow, view_img;
    //    String[] PERMISSIONSloc = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    int PERMISSION_ALL1 = 1;

    GPSTrack gpsTrack;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 1;
    private GoogleMap mMap;
    Circle mCircle;
    ArrayList<Mapview_modelclass> listed_cust = new ArrayList<>();
    SQLite sqLite;
    String Cust_type, Cust_name;

    Marker mCurrLocationMarker;

    Location mLastLocation;
    double str1, str2;
    ImageView DCr_icons;
    TextView custname, town_name, address, distance, dis_name;
    String imge = "", Dcr_val = "";

    protected LocationManager mLocationManager;
    Location gps_loc, network_loc, final_loc;
    public Criteria criteria;
    public String bestProvider;


    @SuppressLint({"MissingInflatedId", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_resource_mapview);
        mapbackArrow = findViewById(R.id.mapbackArrow);
        custname = findViewById(R.id.custname);
        town_name = findViewById(R.id.town_name);
        address = findViewById(R.id.address);
        distance = findViewById(R.id.distance);
        dis_name = findViewById(R.id.dis_name);
        view_img = findViewById(R.id.view_img);


        DCr_icons = new ImageView(this);
        DCr_icons = new ImageView(this);

        gpsTrack = new GPSTrack(this);

        sqLite = new SQLite(this);
        Cust_type = getIntent().getStringExtra("type");
        Cust_name = getIntent().getStringExtra("cust_name");
        Log.e("dcr_doctor", Cust_type + "--" + Cust_name);


        mapbackArrow.setOnClickListener(v -> {
            finish();
        });





        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        view_img.setOnClickListener(v -> {
            showImagePopup(1);


        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        str1 = gpsTrack.getLatitude();
        str2 = gpsTrack.getLongitude();


        String Dcr_list = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this)));
        String chm_list = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(this)));
        String str_list = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(this)));
        String unlisted = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(this)));
        if (Cust_name.equals(Dcr_list)) {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this));
            DCR_VAlues(jsonArray, "1");
        } else if (Cust_name.equals(chm_list)) {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(this));
            DCR_VAlues(jsonArray, "2");
        } else if (Cust_name.equals(str_list)) {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(this));
            DCR_VAlues(jsonArray, "3");
        } else if (Cust_name.equals(unlisted)) {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(this));
            DCR_VAlues(jsonArray, "4");
        }
    }



    public boolean CurrentLoc() {
        boolean val = false;
        gpsTrack = new GPSTrack(MyResource_mapview.this);
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new android.app.AlertDialog.Builder(MyResource_mapview.this).setTitle("Alert") // GPS not found
                        .setCancelable(false).setMessage("Activate the Gps to proceed futher") // Want to enable?
                        .setPositiveButton("Yes", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
            } else {
                val = true;
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyResource_mapview.this, getResources().getString(R.string.loc_not_detect), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            val = false;
        }
        return val;
    }




    public void DCR_VAlues(JSONArray jsonArray, String val) {
        mMap.clear();
        listed_cust.clear();
        try {
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (Cust_type.equals(jsonObject.getString("Code"))) {

                        String latitude = "", longtitue = "", address = "";
                        if (val.equals("1")) {
                            latitude = (jsonObject.getString("Lat"));
                            longtitue = (jsonObject.getString("Long"));
                            address = (jsonObject.getString("Addrs"));


                        } else {


                            latitude = (jsonObject.getString("lat"));
                            longtitue = (jsonObject.getString("long"));


                            if (val.equals("4")) {
                                address = (jsonObject.getString("addr"));
                            } else {
                                address = (jsonObject.getString("addrs"));
                            }

                        }
                        Dcr_val = val;

                        String custname = (jsonObject.getString("Name"));
                        String townname = (jsonObject.getString("Town_Name"));
                        String imgname = (jsonObject.getString("img_name"));//img_name
                        Mapview_modelclass vals = new Mapview_modelclass(latitude, longtitue, address, custname, townname, imgname);
                        listed_cust.add(vals);
                        Log.d("listsize", String.valueOf(listed_cust));
                    }

                }

                for (int i = 0; i < listed_cust.size(); i++) {

                    if (listed_cust.get(i).getStrlat() != null && listed_cust.get(i).getStrlat().length() > 0) {
                        LatLng sydney = new LatLng(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong()));
                        LatLng sydney1 = new LatLng(Double.parseDouble(listed_cust.get(0).getStrlat()), Double.parseDouble(listed_cust.get(0).getStrlong()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
                        LatLng location = new LatLng(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong())); // Replace with your desired latitude and longitude


                        Log.e("loc_latlong", str1 + "--" + str2 + "--" + location);

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
                        mMap.setMyLocationEnabled(true);

                        if (listed_cust.get(i).getImgs().equals("")) {
                            view_img.setVisibility(View.GONE);
                        } else {
                            view_img.setVisibility(View.VISIBLE);
                            imge = listed_cust.get(i).getImgs();
                        }


                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));


                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 10f);
                        mMap.moveCamera(cameraUpdate);
                        mMap.setOnMarkerClickListener(this);
                        custname.setText(listed_cust.get(i).getStrname() + " - " + listed_cust.get(i).getStr_townname());
                        if (val.equals("1")) {
                            DCr_icons = findViewById(R.id.DCr_icons); // Replace with your ImageView ID
                            Drawable drawable = getResources().getDrawable(R.drawable.tp_dr_icon); // Replace with your drawable resource ID
                            DCr_icons.setImageDrawable(drawable);
                        } else if (val.equals("2")) {
                            DCr_icons = findViewById(R.id.DCr_icons);
                            Drawable drawable = getResources().getDrawable(R.drawable.tp_chemist_icon);
                            DCr_icons.setImageDrawable(drawable);
                        } else if (val.equals("3")) {
                            DCr_icons = findViewById(R.id.DCr_icons);
                            Drawable drawable = getResources().getDrawable(R.drawable.tp_cip_icon);
                            DCr_icons.setImageDrawable(drawable);
                        } else if (val.equals("4")) {
                            DCr_icons = findViewById(R.id.DCr_icons);
                            Drawable drawable = getResources().getDrawable(R.drawable.tp_unlist_dr_icon);
                            DCr_icons.setImageDrawable(drawable);
                        }
                        address.setText(getAddress(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong())));

                        marker.setTag(listed_cust.get(i));
                        mMap.setOnMarkerClickListener(this);

                        Log.e("location_latlong", str1 + "--" + str2);




                        if (CurrentLoc()) {

                            float[] results = new float[10];
                            Location.distanceBetween(str1, str2, Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong()), results);
                            float dd = results[0] / 1000;

                            if (dd > 1) {
                                distance.setText(String.format("%.1f", dd));
                                dis_name.setText("Km");

                            } else {
                                String ss = String.format("%.3f", dd);
                                double k = Double.parseDouble(ss);
                                double kk = k * 1000;
                                String km = String.format("%.0f", kk);
                                distance.setText(km);
                                dis_name.setText("Meters");

                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String getAddress(double la, double ln) {
        Geocoder geocoder;
        List<Address> addresses;
        String address = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(la, ln, 1);
            if (addresses.size() == 0) {

            } else {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void enableGPS() {
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (isFineLocationPermissionGranted() && (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            showSettingsAlert(manager);
        } else if (!isFineLocationPermissionGranted()) {

//            if (!hasPermissions(this, PERMISSIONSloc)) {
//                ActivityCompat.requestPermissions(this, PERMISSIONSloc, PERMISSION_ALL1);
//            }
        }
    }

    public boolean isFineLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean hasPermissions(Context Context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(Context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean CheckLocPermission() {
        int FineLocation = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        int CoarseLocation = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        return FineLocation == PackageManager.PERMISSION_GRANTED && CoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    public void showSettingsAlert(final LocationManager manager) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Setting");//GPS Setting,GPS is Not Enabled,Settings,Enable the loaction and procced...!
        alertDialog.setCancelable(false);
        alertDialog.setMessage("GPS is Not Enabled");
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(MyResource_mapview.this, "Settings,Enable the loaction and procced...!", Toast.LENGTH_SHORT).show();
                showSettingsAlert(manager);
            } else {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }





    public boolean onMarkerClick(@NonNull Marker marker) {
        Mapview_modelclass markerData = (Mapview_modelclass) marker.getTag();
        custname.setText(markerData.getStrname() + " - " + markerData.getStr_townname());

        if (Dcr_val.equals("1")) {
            DCr_icons = findViewById(R.id.DCr_icons); // Replace with your ImageView ID
            Drawable drawable = getResources().getDrawable(R.drawable.tp_dr_icon); // Replace with your drawable resource ID
            DCr_icons.setImageDrawable(drawable);

        } else if (Dcr_val.equals("2")) {
            DCr_icons = findViewById(R.id.DCr_icons);
            Drawable drawable = getResources().getDrawable(R.drawable.tp_chemist_icon);
            DCr_icons.setImageDrawable(drawable);

        } else if (Dcr_val.equals("3")) {
            DCr_icons = findViewById(R.id.DCr_icons);
            Drawable drawable = getResources().getDrawable(R.drawable.tp_cip_icon);
            DCr_icons.setImageDrawable(drawable);

        } else if (Dcr_val.equals("4")) {
            DCr_icons = findViewById(R.id.DCr_icons);
            Drawable drawable = getResources().getDrawable(R.drawable.tp_unlist_dr_icon);
            DCr_icons.setImageDrawable(drawable);
        }
        address.setText(getAddress(Double.parseDouble(markerData.getStrlat()), Double.parseDouble(markerData.getStrlong())));

        if (markerData.getImgs().equals("")) {
            view_img.setVisibility(View.GONE);
        } else {
            view_img.setVisibility(View.VISIBLE);
        }

        float[] results = new float[10];
        Location.distanceBetween(str1, str2, Double.parseDouble(markerData.getStrlat()), Double.parseDouble(markerData.getStrlong()), results);
        float dd = results[0] / 1000;

        if (dd > 1) {
            distance.setText(String.format("%.1f", dd));
            dis_name.setText("Km");

        } else {
            String ss = String.format("%.3f", dd);
            double k = Double.parseDouble(ss);
            double kk = k * 1000;
            String km = String.format("%.0f", kk);
            distance.setText(km);
            dis_name.setText("Meters");
        }
        imge = markerData.getImgs();
//        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        Log.e("img_val", imge);
        if (isNetworkConnected()) {
            marker.showInfoWindow();
        } else {
        }

        return true;
    }





    private void showImagePopup(int val) {


        String loadimage;

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.res_imageview);
        ImageView popupImageView = dialog.findViewById(R.id.webview);


        Log.e("imageval", imge);


        if (val == 1) {

            if (imge.equals("") || imge.contains("noimage") || imge.endsWith(".jpg")) {

            } else {

                String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
                String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
                String replacedUrl = baseUrl.replaceAll("\\?.*", "/");
                String TypedWebURL = replacedUrl;
                String vidurl = /*"http://" +*/ TypedWebURL + "/";
                loadimage = (vidurl + "photos" + "/" + imge);
                Log.d("tagitsgdjkwgukehhmag", loadimage);

                Picasso.get()
                        .load(loadimage)
                        .into(popupImageView);
            }

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = window.getAttributes();

                // Set the top margin value (in pixels)
                layoutParams.y = 70;

                // Set other properties if needed
                window.setLayout(400, 600);
                window.setGravity(Gravity.RIGHT);

                window.setAttributes(layoutParams);
            }

            dialog.show();

        }



    }




}
