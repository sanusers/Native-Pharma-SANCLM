package saneforce.sanzen.activity.call.dcrCallSelection;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
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

        dcrSelectionBinding.ivBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
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
        CommonAlertBox.CheckLocationStatus(DcrCallTabLayoutActivity.this);
    }
}