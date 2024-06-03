package saneforce.sanzen.activity.myresource;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityMyResourceMapviewBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class MyResource_mapview extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    LinearLayout mapbackArrow, view_img, tagging_meters, Doc_name;
    //    String[] PERMISSIONSloc = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    int PERMISSION_ALL1 = 1;

    GPSTrack gpsTrack;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 1;
    private GoogleMap mMap;
    private LatLng currentLocation;

    Circle mCircle;
    ArrayList<Mapview_modelclass> listed_cust = new ArrayList<>();
    String Cust_type, Cust_name, Dcrname, pos_name, Lat, Long, Town;

    Marker mCurrLocationMarker;

    Location mLastLocation;
    double str1, str2;
    String add_crt;
    ImageView DCr_icons;
    TextView custname, town_name, address, distance, dis_name, haed_name, Doc_taggsave;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    String imge = "", Dcr_val = "";
    LinearLayout l_view, tag_doc;
    ProgressDialog progressDialog = null;
    ApiInterface api_interface;
    ActivityMyResourceMapviewBinding Resmap_binding;

    protected LocationManager mLocationManager;
    Location gps_loc, network_loc, final_loc;
    public Criteria criteria;
    public String bestProvider;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resmap_binding = ActivityMyResourceMapviewBinding.inflate(getLayoutInflater());
        setContentView(Resmap_binding.getRoot());

        DCr_icons = new ImageView(this);
        gpsTrack = new GPSTrack(this);
        Cust_type = getIntent().getStringExtra("type");
        Cust_name = getIntent().getStringExtra("cust_name");
        Dcrname = getIntent().getStringExtra("Dcr_name");
        Town = getIntent().getStringExtra("Town_loct");
        pos_name = getIntent().getStringExtra("pos_name");

        roomDB =  RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();


        Log.e("dcr_doctor", Cust_type + "--" + Cust_name);

        Resmap_binding.mapbackArrow.setOnClickListener(v -> {
            finish();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Cust_name.equals("Res_doc")) {
            Resmap_binding.taggingMeters.setVisibility(View.GONE);
            Resmap_binding.DocName.setVisibility(View.GONE);
            Resmap_binding.disName.setText(Dcrname);
            Resmap_binding.haedName.setTypeface(null, Typeface.BOLD);
            Resmap_binding.tagDoclat.setVisibility(View.VISIBLE);//tag_doclat

        }

        Resmap_binding.viewImg.setOnClickListener(v -> {
            showImagePopup(1);
        });
        Resmap_binding.tagDoclat.setOnClickListener(view -> {
            submit_profiling();
        });
//        CallAPIList("D");

        enableGPS();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        str1 = gpsTrack.getLatitude();
        str2 = gpsTrack.getLongitude();
        add_crt = getAddress(str1, str2);




        String Dcr_list = masterDataDao.getDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this));
        String chm_list = masterDataDao.getDataByKey(Constants.CHEMIST + SharedPref.getHqCode(this));
        String str_list = masterDataDao.getDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(this));
        String unlisted = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(this));
        if (pos_name.equals(Dcr_list)) {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
            DCR_VAlues(jsonArray, "1");
        } else if (Cust_name.equals(chm_list)) {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
            DCR_VAlues(jsonArray, "2");
        } else if (Cust_name.equals(str_list)) {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
            DCR_VAlues(jsonArray, "3");
        } else if (Cust_name.equals(unlisted)) {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
            DCR_VAlues(jsonArray, "4");
        } else if (Cust_name.equals("Res_doc")) {
            tagging_location();


//            UiSettings uiSettings = mMap.getUiSettings();
//            uiSettings.setAllGesturesEnabled(false);
        }
    }

    @SuppressLint("MissingPermission")
    public void tagging_location() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        Resmap_binding.custname.setText(Dcrname + " - " + Town);
//        mMap.setOnMyLocationButtonClickListener(() -> {
//            Toast.makeText(MyResource_mapview.this, "My Location button clicked", Toast.LENGTH_SHORT).show();
//            return false;
//        });
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);


        // Get current location
        // For simplicity, let's assume current location is already obtained and stored in currentLocation
        currentLocation = new LatLng(str1, str2); // Example: New York City coordinates
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        LatLng location = new LatLng(str1, str2); // Replace with your desired latitude and longitude
        Log.e("loc_latlong", str1 + "--" + str2 + "--" + location);
        marker.setTag(location);
        Resmap_binding.address.setText(getAddress(str1, str2));


        // Move camera to current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13)); // Zoom level 13
    }


    public boolean CurrentLoc() {
        boolean val = false;
        gpsTrack = new GPSTrack(MyResource_mapview.this);
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new AlertDialog.Builder(MyResource_mapview.this).setTitle("Alert") // GPS not found
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
//                        Log.d("listsize", String.valueOf(listed_cust));
                    }
                }

                for (int i = 0; i < listed_cust.size(); i++) {
                    if (listed_cust.get(i).getStrlat() != null && listed_cust.get(i).getStrlat().length() > 0) {
                        LatLng sydney1 = new LatLng(Double.parseDouble(listed_cust.get(0).getStrlat()), Double.parseDouble(listed_cust.get(0).getStrlong()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
                        LatLng location = new LatLng(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong())); // Replace with your desired latitude and longitude
//                        Log.e("loc_latlong", str1 + "--" + str2 + "--" + location);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        mMap.setMyLocationEnabled(true);
                        if (listed_cust.get(i).getImgs().equals("")) {
                            Resmap_binding.viewImg.setVisibility(View.GONE);
                        } else {
                            Resmap_binding.viewImg.setVisibility(View.VISIBLE);
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
                        Resmap_binding.custname.setText(listed_cust.get(i).getStrname() + " - " + listed_cust.get(i).getStr_townname());
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
                        Resmap_binding.address.setText(getAddress(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong())));

                        marker.setTag(listed_cust.get(i));
                        mMap.setOnMarkerClickListener(this);
//                        Log.e("location_latlong", str1 + "--" + str2);
                        if (CurrentLoc()) {
                            float[] results = new float[10];
                            Location.distanceBetween(str1, str2, Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong()), results);
                            float dd = results[0] / 1000;
                            if (dd > 1) {
                                Resmap_binding.distance.setText(String.format("%.1f", dd));
                                Resmap_binding.disName.setText("Km");
                            } else {
                                String ss = String.format("%.3f", dd);
                                double k = Double.parseDouble(ss);
                                double kk = k * 1000;
                                String km = String.format("%.0f", kk);
                                Resmap_binding.distance.setText(km);
                                Resmap_binding.disName.setText("Meters");
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
        Resmap_binding.custname.setText(markerData.getStrname() + " - " + markerData.getStr_townname());

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
        Resmap_binding.address.setText(getAddress(Double.parseDouble(markerData.getStrlat()), Double.parseDouble(markerData.getStrlong())));
        if (markerData.getImgs().equals("")) {
            Resmap_binding.viewImg.setVisibility(View.GONE);
        } else {
            Resmap_binding.viewImg.setVisibility(View.VISIBLE);
        }
        float[] results = new float[10];
        Location.distanceBetween(str1, str2, Double.parseDouble(markerData.getStrlat()), Double.parseDouble(markerData.getStrlong()), results);
        float dd = results[0] / 1000;
        if (dd > 1) {
            Resmap_binding.distance.setText(String.format("%.1f", dd));
            Resmap_binding.disName.setText("Km");
        } else {
            String ss = String.format("%.3f", dd);
            double k = Double.parseDouble(ss);
            double kk = k * 1000;
            String km = String.format("%.0f", kk);
            Resmap_binding.distance.setText(km);
            Resmap_binding.disName.setText("Meters");
        }
        imge = markerData.getImgs();
//        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
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
        if (val == 1) {
            if (imge.equals("") || imge.contains("noimage") || imge.endsWith(".jpg")) {
            } else {
                String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
                String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
                String replacedUrl = baseUrl.replaceAll("\\?.*", "/");
                String TypedWebURL = replacedUrl;
                String vidurl = /*"http://" +*/ TypedWebURL + "/";
                loadimage = (vidurl + "photos" + "/" + imge);
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

    public void submit_profiling() {
        try {
            api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
            progressDialog = CommonUtilsMethods.createProgressDialog(this);
            JSONObject jsonobj = new JSONObject();

            jsonobj.put("tableName", "save_geo");
            jsonobj.put("lat", String.valueOf(str1));
            jsonobj.put("long", String.valueOf(str2));//Cust_type + "--" + Cust_name
            jsonobj.put("cuscode", Cust_type);
            jsonobj.put("cust_name", Dcrname);
            jsonobj.put("divcode", SharedPref.getDivisionCode(this).replace(",", "").trim());//
            jsonobj.put("cust", pos_name);
            jsonobj.put("tagged_time", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd") + " " + CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
            jsonobj.put("image_name", "");
            jsonobj.put("sfname", SharedPref.getSfType(this));
            jsonobj.put("sfcode", SharedPref.getSfCode(this));
            jsonobj.put("addr", add_crt);
            jsonobj.put("tagged_cust_HQ", SharedPref.getSfCode(this));
            jsonobj.put("mode", Constants.APP_MODE);
            jsonobj.put("version", Constants.APP_VERSION);
            jsonobj.put("versionNo",  getString(R.string.app_version));
            jsonobj.put("mod", Constants.APP_MODE);
            jsonobj.put("Device_version", Build.VERSION.RELEASE);
            jsonobj.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonobj.put("AppName", getString(R.string.str_app_name));
            jsonobj.put("language", SharedPref.getSelectedLanguage(this));
//            Log.d("prifiling", jsonobj.toString());
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "geodetails");

            Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonobj.toString());
            call.enqueue(new Callback<JsonElement>() {

                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        try {
                            progressDialog.dismiss();
                            assert response.body() != null;
                            JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("Msg").equalsIgnoreCase("Tagged Successfully")) {
                                Toast.makeText(MyResource_mapview.this, "Tagged Successfully", Toast.LENGTH_SHORT).show();
                                CallAPIList(Cust_type);
                                finish();
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("Msg").equalsIgnoreCase("You have reached the maximum tags...")) {
                                Toast.makeText(MyResource_mapview.this, jsonSaveRes.getString("Msg"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(MyResource_mapview.this, "Something went Wrong! Please Try Again", Toast.LENGTH_SHORT).show();

                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();

                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MyResource_mapview.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(MyResource_mapview.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallAPIList(String CustSelected) {
        String sfCode;
        sfCode = SharedPref.getSfCode(this);

        if (CustSelected.equalsIgnoreCase("D")) {
            prepareMasterToSync(sfCode, "D");
        } else if (CustSelected.equalsIgnoreCase("C")) {
            prepareMasterToSync(sfCode, "C");
        } else if (CustSelected.equalsIgnoreCase("S")) {
            prepareMasterToSync(sfCode, "S");
        } else if (CustSelected.equalsIgnoreCase("U")) {
            prepareMasterToSync(sfCode, "U");
        }
    }

    public void prepareMasterToSync(String hqCode, String Cust_Selected) {
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
            sync(masterSyncArray.get(i), hqCode);
        }
    }


    public void sync(MasterSyncItemModel masterSyncItemModel, String hqCode) {

        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this).replace(",", "").trim());
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", SharedPref.getSfType(this));
                jsonObject.put("Designation", SharedPref.getDesig(this));
                jsonObject.put("state_code", SharedPref.getStateCode(this));
                jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));
                jsonObject.put("versionNo",  getString(R.string.app_version));
                jsonObject.put("mod", Constants.APP_MODE);
                jsonObject.put("Device_version", Build.VERSION.RELEASE);
                jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
                jsonObject.put("AppName", getString(R.string.str_app_name));
                jsonObject.put("language", SharedPref.getSelectedLanguage(this));

                Log.e("test", "master sync obj in TP : " + jsonObject + "--" + hqCode);
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());

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
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0));
                                        }
                                    } else {
                                        masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }


}
