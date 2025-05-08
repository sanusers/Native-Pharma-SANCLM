package saneforce.sanzen.activity.call.dcrCallSelection;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import saneforce.sanzen.activity.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.CIPFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.HospitalFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.StockiestFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.databinding.CallDcrSelectionBinding;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DcrCallTabLayoutActivity extends AppCompatActivity {

    public static String TodayPlanSfCode, TodayPlanSfName;
    public static double lat, lng, limitKm = 0.5;
    public static ArrayList<String> TodayPlanClusterList = new ArrayList<>();
    CallDcrSelectionBinding dcrSelectionBinding;

    TabLayoutAdapter viewPagerAdapter;
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            dcrSelectionBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(HomeDashBoard.selectedDate != null) {
            outState.putString("date", HomeDashBoard.selectedDate.toString());
            outState.putInt(Manifest.permission.ACCESS_FINE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
            outState.putInt(Manifest.permission.ACCESS_COARSE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
            outState.putInt(Manifest.permission.CAMERA, ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
                outState.putInt(Manifest.permission.READ_MEDIA_AUDIO, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO));
                outState.putInt(Manifest.permission.READ_MEDIA_VIDEO, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO));
                outState.putInt(Manifest.permission.READ_MEDIA_IMAGES, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES));
            }
            outState.putInt(Manifest.permission.READ_EXTERNAL_STORAGE, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE));
            outState.putInt(Manifest.permission.WRITE_EXTERNAL_STORAGE, ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        }
        outState.putBoolean("isSaved", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrSelectionBinding = CallDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(dcrSelectionBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();

        if(savedInstanceState != null && savedInstanceState.getBoolean("isSaved")) {
            if(savedInstanceState.getString("date") != null) {
                HomeDashBoard.selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
            }
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_FINE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_COARSE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != savedInstanceState.getInt(Manifest.permission.CAMERA, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_AUDIO, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_VIDEO, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_IMAGES, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.READ_EXTERNAL_STORAGE, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.WRITE_EXTERNAL_STORAGE, -1) ) {
                CommonAlertBox.permissionChangeAlert(this);
            }
        }

        getRequiredData();

        viewPagerAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        if (SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ListedDoctorFragment(), SharedPref.getDrCap(context));
        }

        if (SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistFragment(), SharedPref.getChmCap(context));
        }
        if (SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new CIPFragment(), SharedPref.getCipCaption(context));
        }
        if (SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new StockiestFragment(), SharedPref.getStkCap(context));
        }
        if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new UnlistedDoctorFragment(), SharedPref.getUNLcap(context));
        }
        if (SharedPref.getHospNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new HospitalFragment(), SharedPref.getHospCaption(context));
        }

        dcrSelectionBinding.viewPagerCallSelection.setAdapter(viewPagerAdapter);
        dcrSelectionBinding.tabLayoutCall.setupWithViewPager(dcrSelectionBinding.viewPagerCallSelection);
        //dcrSelectionBinding.viewPagerCallSelection.setOffscreenPageLimit(viewPagerAdapter.getCount());
        dcrSelectionBinding.viewPagerCallSelection.setOffscreenPageLimit(7);

        if (SharedPref.getGeotagNeed(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
        } else {
            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
        }

        dcrSelectionBinding.tabLayoutCall.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("Call Selection Tab Layout", "onTabSelected: " + tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        if(SharedPref.getGeotagNeed(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        }else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 1:
                        if(SharedPref.getGeotagNeedChe(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        }else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        if(SharedPref.getGeotagNeedStock(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        }else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 3:
                        if(SharedPref.getGeotagNeedUnlst(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        }else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 4:
                        if(SharedPref.getGeotagNeedCip(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        }else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        dcrSelectionBinding.imgLocation.setOnClickListener(new CommonUtilsMethods.DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                setGpsTrack();
            }
        });

        dcrSelectionBinding.ivBack.setOnClickListener(view -> {
//            getOnBackPressedDispatcher().onBackPressed()
            startActivity(new Intent(this, HomeDashBoard.class));
            finishAffinity();
        });

    }

    private void setGpsTrack() {
        gpsTrack = new GPSTrack(this);
        double lat = gpsTrack.getLatitude();
        double lng = gpsTrack.getLongitude();
        if(CommonUtilsMethods.isLocationEnabled(getApplicationContext())) {
            CommonUtilsMethods.gettingAddress(this, Double.parseDouble(String.valueOf(lat)), Double.parseDouble(String.valueOf(lng)), true);
        }else {
            CommonUtilsMethods.RequestGPSPermission(this);
        }
    }

    private void getRequiredData() {
        try {
            gpsTrack = new GPSTrack(this);
            lat = gpsTrack.getLatitude();
            lng = gpsTrack.getLongitude();


            if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
                TodayPlanSfCode = SharedPref.getSfCode(this);
                TodayPlanSfName = SharedPref.getSfName(this);
            } else {
                TodayPlanSfCode = SharedPref.getHqCode(this);
                TodayPlanSfName = SharedPref.getHqName(this);
                if (TodayPlanSfCode.equalsIgnoreCase("null") || TodayPlanSfCode.isEmpty()) {
                    JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                    for (int i = 0; i < 1; i++) {
                        JSONObject jsonHQList = jsonArray1.getJSONObject(0);
                        TodayPlanSfCode = jsonHQList.getString("id");
                        TodayPlanSfName = jsonHQList.getString("name");
                    }
                }
            }

            TodayPlanClusterList.clear();
            JSONArray jsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + TodayPlanSfCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject jsonClusterList = jsonArray2.getJSONObject(i);
                if (SharedPref.getTodayDayPlanClusterCode(this).contains(jsonClusterList.getString("Code"))) {
                    TodayPlanClusterList.add(jsonClusterList.getString("Code"));
                    TodayPlanClusterList.add(jsonClusterList.getString("Name"));
                }
            }

            Log.v("required_data", "---" + TodayPlanSfCode + "---" + TodayPlanClusterList);

        } catch (Exception e) {
            Log.v("required_data", "--tab-dcr-" + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonAlertBox.CheckLocationStatus(DcrCallTabLayoutActivity.this, gpsTrack);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, new IntentFilter("com.saneforce.SYNC_COMPLETED"));
    }

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if(type != null && type.matches("(?i)DR|CH|ST|UL|HOS|CIP|AMS|FSD|SE|PR|GIF|TM")) {
                startActivity(new Intent(DcrCallTabLayoutActivity.this, DcrCallTabLayoutActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }
}