package saneforce.sanzen.activity.myresource;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static java.lang.Double.parseDouble;
import static java.lang.Double.valueOf;

import static saneforce.sanzen.activity.map.MapsActivity.BitmapFromVector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import saneforce.sanzen.AWS.AWSBucketsTag;
import saneforce.sanzen.AWS.S3DownloadFiles;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.myresource.myresourcemodel.ResourcerviewModelClass;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.databinding.ActivityMyResourceMapviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;

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
    CommonUtilsMethods commonUtilsMethods;

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

        binding.mapbackArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                finish();
            }
        });

        binding.viewImg.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                showImagePopup();
            }
        });

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        double getDistance = getDistanceMeters(CurrentLat, CurrentLong, marker.getPosition().latitude, marker.getPosition().longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 18.0f));

        if (marker.getSnippet() != null) {
            String[] snippetData = marker.getSnippet().split("@");
            if (snippetData.length > 0) {
                ImageName = snippetData[1];
                binding.address.setText(snippetData[0]);
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

        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (CUST_FLAG.equalsIgnoreCase("D")) {
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_GEO + HQ_CODE).getMasterSyncDataJsonArray().toString());
            //parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + HQ_CODE).getMasterSyncDataJsonArray().toString());
        } else if (CUST_FLAG.equalsIgnoreCase("C")) {
//            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + HQ_CODE).getMasterSyncDataJsonArray().toString());
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_GEO + HQ_CODE).getMasterSyncDataJsonArray().toString());
        } else if (CUST_FLAG.equalsIgnoreCase("S")) {
//            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + HQ_CODE).getMasterSyncDataJsonArray().toString());
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_GEO + HQ_CODE).getMasterSyncDataJsonArray().toString());
        } else if (CUST_FLAG.equalsIgnoreCase("U")) {
//            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + HQ_CODE).getMasterSyncDataJsonArray().toString());
            parseJsonData(masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR_GEO + HQ_CODE).getMasterSyncDataJsonArray().toString());
        }

        if (SharedPref.getGeotagImg(this).equalsIgnoreCase("0")) {
            if (loclist != null && !loclist.isEmpty() && !loclist.get(0).getImageName().isEmpty()) {
                binding.viewImg.setVisibility(View.VISIBLE);
            } else {
                binding.viewImg.setVisibility(View.GONE);
            }
        } else {
            binding.viewImg.setVisibility(View.GONE);
        }
    }
    private void parseJsonData(String jsonResponse) {
        try {
            if (mMap != null) {
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
//                            loclist.add(new ResourcerviewModelClass(jsonObject1.optString("Name"), jsonObject1.optString("Code"), jsonObject1.optString("Lat"), jsonObject1.optString("Long"), jsonObject1.optString("Addrs"), jsonObject1.optString("Town_Name"), jsonObject1.optString("img_name")));
                            loclist.add(new ResourcerviewModelClass(jsonObject1.optString("Name"), jsonObject1.optString("Code"), jsonObject1.optString("lat"), jsonObject1.optString("long"), jsonObject1.optString("addrs"), jsonObject1.optString("Town_Name"), jsonObject1.optString("img_name")));
                        } else if (CUST_FLAG.equalsIgnoreCase("C")) {
                            loclist.add(new ResourcerviewModelClass(jsonObject1.optString("Name"), jsonObject1.optString("Code"), jsonObject1.optString("lat"), jsonObject1.optString("long"), jsonObject1.optString("addr"), jsonObject1.optString("Town_Name"), jsonObject1.optString("img_name")));
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
        ImageName = loclist.get(0).getImageName();
        String fileName = ImageName;
        if (fileName.isEmpty()) {
            return;
        }
        if (SharedPref.getS3BucketNeed(getApplicationContext()).equalsIgnoreCase("0")) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, new NetworkStatusTask.NetworkStatusInterface() {
                @Override
                public void isNetworkAvailable(Boolean status) {
                    if (status){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_fullscreen_image, null);
                        ImageView fullScreenImage = dialogView.findViewById(R.id.fullscreen_image);
                        ImageButton closeButton = dialogView.findViewById(R.id.close_button);
                        ProgressBar progressBar = dialogView.findViewById(R.id.loading_progress);
//        ImageView popupImageView = dialog.findViewById(R.id.img_dr_content);
                        fullScreenImage.setImageBitmap(BitmapFactory.decodeFile(fileName));
                        builder.setView(dialogView);
                        AlertDialog dialog_fullScreen = builder.create();
                        fullScreenImage.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        dialog_fullScreen.show();
                        dialog_fullScreen.getWindow().setLayout(
                                (int) (getResources().getDisplayMetrics().widthPixels * 0.5),
                                (int) (getResources().getDisplayMetrics().heightPixels * 0.9)
                        );
                        if (ImageName == null || ImageName.isEmpty() || ImageName.contains("noimage") || ImageName.endsWith(".jpg")) {
                            Log.d("Image Name", "showImagePopup: " + "no image Found");
                        } else {
                            TransferNetworkLossHandler.getInstance(getApplicationContext());
                            File MapView = new File(MyResource_mapview.this.getFilesDir(), fileName);
                            Log.d("TAG", "AddTaggedDetails: " + MapView.getAbsolutePath());
                            new AWSBucketsTag(MyResource_mapview.this, fileName, MapView, 0, "", new S3DownloadFiles() {
                                @Override
                                public void fileDataAdd(int pos, Bitmap bitmap) {
                                    if (bitmap != null) {
                                        Log.d("bitmap image", "image: " + "bitmap map is not null");
                                        fullScreenImage.setImageBitmap(bitmap);
                                        fullScreenImage.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Log.d("bitmap image", "image: " + "bitmap image is null");
                                        fullScreenImage.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        dialog_fullScreen.dismiss();
                                    }
                                }
                            });
                            closeButton.setOnClickListener(view -> dialog_fullScreen.dismiss());
                        }
                    }else{
                        commonUtilsMethods.showToastMessage(MyResource_mapview.this, getString(R.string.no_network));
                    }
                }
            });
            networkStatusTask.execute();


        } else {
//            Dialog dialog = new Dialog(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_fullscreen_image, null);

            ImageView fullScreenImage = dialogView.findViewById(R.id.fullscreen_image);
            ImageButton closeButton = dialogView.findViewById(R.id.close_button);
            ProgressBar progressBar = dialogView.findViewById(R.id.loading_progress);
//            fullScreenImage.setImageBitmap(BitmapFactory.decodeFile(fileName));
            builder.setView(dialogView);
            AlertDialog dialog_fullScreen = builder.create();
            fullScreenImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            dialog_fullScreen.show();
            dialog_fullScreen.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.5),
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.9)
            );
            if (ImageName == null || ImageName.isEmpty() || ImageName.contains("noimage") || ImageName.endsWith(".jpg")) {
                Log.d("Image Name", "showImagePopup: " + "no image Found");

            } else {

                String url = SharedPref.getTagImageUrl(context) + "photos/" + ImageName;
                Log.e("Inmge", url);
//                Picasso.get().load(url).into(fullScreenImage);
                Glide.with(getApplicationContext()).load(url).into(fullScreenImage);
                fullScreenImage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                dialog_fullScreen.show();
            }
            closeButton.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    dialog_fullScreen.dismiss();
                }
            });
        }

    }
//    private void showImagePopup() {
//        ImageName = loclist.get(0).getImageName();
//        String fileName = ImageName;
//        if (fileName.isEmpty()) {
//            return;
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        Dialog dialog = new Dialog(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_fullscreen_image, null);
//        ImageView fullScreenImage = dialogView.findViewById(R.id.fullscreen_image);
//        ImageButton closeButton = dialogView.findViewById(R.id.close_button);
//        dialog.setContentView(R.layout.map_img_layout);

    /// /        ImageView popupImageView = dialog.findViewById(R.id.img_dr_content);
//        fullScreenImage.setImageBitmap(BitmapFactory.decodeFile(fileName));
//        builder.setView(dialogView);
//        AlertDialog dialog_fullScreen = builder.create();
//        dialog_fullScreen.show();
//        dialog_fullScreen.getWindow().setLayout(
//                (int) (getResources().getDisplayMetrics().widthPixels * 0.5),
//                (int) (getResources().getDisplayMetrics().heightPixels * 0.9)
//        );
//        if (ImageName == null || ImageName.isEmpty() || ImageName.contains("noimage") || ImageName.endsWith(".jpg")) {
//            Log.d("Image Name", "showImagePopup: "+"no image Found");
//        } else {
//            TransferNetworkLossHandler.getInstance(getApplicationContext());
//            File MapView = new File(MyResource_mapview.this.getFilesDir(), fileName);
//            Log.d("TAG", "AddTaggedDetails: " + MapView.getAbsolutePath());
//            new AWSBuckets(MyResource_mapview.this, fileName, MapView, 0, "", new S3DownloadFiles() {
//                @Override
//                public void fileDataAdd(int pos, Bitmap bitmap) {
//                    if (bitmap != null) {
//                        Log.d("bitmap image", "image: " + "bitmap map is not null");
//                        fullScreenImage.setImageBitmap(bitmap);
//                        fullScreenImage.setVisibility(View.VISIBLE);
//                    } else {
//                        Log.d("bitmap image", "image: " + "bitmap image is null");
//                    }
//                }
//            });
//        }
//        closeButton.setOnClickListener(v -> dialog_fullScreen.dismiss());
//
//    }
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
