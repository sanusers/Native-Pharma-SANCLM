package saneforce.sanzen.activity.call.dcrCallSelection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import saneforce.sanzen.activity.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.CIPFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.HospitalFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.StockiestFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.CallDcrSelectionBinding;
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
    private ListedDoctorFragment listedDoctorFragment;
    private ChemistFragment chemistFragment;
    private StockiestFragment stockiestFragment;
    private UnlistedDoctorFragment unlistedDoctorFragment;
    private CIPFragment cipFragment;
    private HospitalFragment hospitalFragment;

    public interface HQChangeListener {
        void onHQChange(String hqID, String hqName);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

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
        if (HomeDashBoard.selectedDate != null) {
            outState.putString("date", HomeDashBoard.selectedDate.toString());
            outState.putInt(Manifest.permission.ACCESS_FINE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
            outState.putInt(Manifest.permission.ACCESS_COARSE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
            outState.putInt(Manifest.permission.CAMERA, ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        if (savedInstanceState != null && savedInstanceState.getBoolean("isSaved")) {
            if (savedInstanceState.getString("date") != null) {
                HomeDashBoard.selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_FINE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_COARSE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != savedInstanceState.getInt(Manifest.permission.CAMERA, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_AUDIO, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_VIDEO, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_IMAGES, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.READ_EXTERNAL_STORAGE, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.WRITE_EXTERNAL_STORAGE, -1)) {
                CommonAlertBox.permissionChangeAlert(this);
            }
        }

        getRequiredData();
        HQChangeListener hqChangeListener = (String hqID, String hqName) -> {
            DcrCallTabLayoutActivity.TodayPlanSfCode = hqID;
            DcrCallTabLayoutActivity.TodayPlanSfName = hqName;
            SharedPref.saveHq(DcrCallTabLayoutActivity.this, DcrCallTabLayoutActivity.TodayPlanSfName, DcrCallTabLayoutActivity.TodayPlanSfCode);
            prepareClusterList();
            if (listedDoctorFragment != null) {
                listedDoctorFragment.SetupAdapter();
            }
            if (chemistFragment != null) {
                chemistFragment.SetupAdapter();
            }
            if (stockiestFragment != null) {
                stockiestFragment.SetupAdapter();
            }
            if (unlistedDoctorFragment != null) {
                unlistedDoctorFragment.SetupAdapter();
            }
            if (cipFragment != null) {
                cipFragment.SetupAdapter();
            }
            if (hospitalFragment != null) {
                hospitalFragment.SetupAdapter();
            }
        };

        viewPagerAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        if (SharedPref.getDrNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("0")) {
            listedDoctorFragment = new ListedDoctorFragment(hqChangeListener);
            viewPagerAdapter.add(listedDoctorFragment, SharedPref.getDrCap(DcrCallTabLayoutActivity.this));
        }
        if (SharedPref.getChmNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("0")) {
            chemistFragment = new ChemistFragment(hqChangeListener);
            viewPagerAdapter.add(chemistFragment, SharedPref.getChmCap(DcrCallTabLayoutActivity.this));
        }
        if (SharedPref.getStkNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("0")) {
            stockiestFragment = new StockiestFragment(hqChangeListener);
            viewPagerAdapter.add(stockiestFragment, SharedPref.getStkCap(DcrCallTabLayoutActivity.this));
        }
        if (SharedPref.getUnlNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("0")) {
            unlistedDoctorFragment = new UnlistedDoctorFragment(hqChangeListener);
            viewPagerAdapter.add(unlistedDoctorFragment, SharedPref.getUNLcap(DcrCallTabLayoutActivity.this));
        }
        if (SharedPref.getCipNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("0")) {
            cipFragment = new CIPFragment(hqChangeListener);
            viewPagerAdapter.add(cipFragment, SharedPref.getCipCaption(DcrCallTabLayoutActivity.this));
        }
        if (SharedPref.getHospNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("0")) {
            hospitalFragment = new HospitalFragment(hqChangeListener);
            viewPagerAdapter.add(hospitalFragment, SharedPref.getHospCaption(DcrCallTabLayoutActivity.this));
        }

        dcrSelectionBinding.viewPagerCallSelection.setAdapter(viewPagerAdapter);
        dcrSelectionBinding.tabLayoutCall.setupWithViewPager(dcrSelectionBinding.viewPagerCallSelection);
        //dcrSelectionBinding.viewPagerCallSelection.setOffscreenPageLimit(viewPagerAdapter.getCount());
        dcrSelectionBinding.viewPagerCallSelection.setOffscreenPageLimit(7);

        if (SharedPref.getGeotagNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("1") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
        } else {
            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
        }

        dcrSelectionBinding.tabLayoutCall.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("Call Selection Tab Layout", "onTabSelected: " + tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        if (SharedPref.getGeotagNeed(DcrCallTabLayoutActivity.this).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        } else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 1:
                        if (SharedPref.getGeotagNeedChe(DcrCallTabLayoutActivity.this).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        } else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        if (SharedPref.getGeotagNeedStock(DcrCallTabLayoutActivity.this).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        } else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 3:
                        if (SharedPref.getGeotagNeedUnlst(DcrCallTabLayoutActivity.this).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        } else {
                            dcrSelectionBinding.imgLocation.setVisibility(View.GONE);
                        }
                        break;
                    case 4:
                        if (SharedPref.getGeotagNeedCip(DcrCallTabLayoutActivity.this).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            dcrSelectionBinding.imgLocation.setVisibility(View.VISIBLE);
                        } else {
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
            public void onDoubleClick(View view) {
                setGpsTrack();
            }
        });

        dcrSelectionBinding.ivBack.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent intent = new Intent(DcrCallTabLayoutActivity.this, HomeDashBoard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setGpsTrack() {
        gpsTrack = new GPSTrack(this);
        double lat = gpsTrack.getLatitude();
        double lng = gpsTrack.getLongitude();
        if (CommonUtilsMethods.isLocationEnabled(getApplicationContext())) {
            CommonUtilsMethods.gettingAddress(this, Double.parseDouble(String.valueOf(lat)), Double.parseDouble(String.valueOf(lng)), true);
        } else {
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
                if (TodayPlanSfCode == null || TodayPlanSfCode.isEmpty()) {
                    TodayPlanSfCode = SharedPref.getHqCode(this);
                    TodayPlanSfName = SharedPref.getHqName(this);
                    if (TodayPlanSfCode == null || TodayPlanSfCode.isEmpty()) {
//                        JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
//                        for (int i = 0; i < 1; i++) {
//                            JSONObject jsonHQList = jsonArray1.getJSONObject(0);
//                            TodayPlanSfCode = jsonHQList.getString("id");
//                            TodayPlanSfName = jsonHQList.getString("name");
//                        }
                        saveHQ();
                    }

                    if (TodayPlanSfCode == null || TodayPlanSfCode.isEmpty()) {
                        JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                        for (int i = 0; i < 1; i++) {
                            JSONObject jsonHQList = jsonArray1.getJSONObject(0);
                            TodayPlanSfCode = jsonHQList.getString("id");
                            TodayPlanSfName = jsonHQList.getString("name");
                        }
                    }
                } else {
                    if ((WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F") || WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))
                            && (!(Arrays.asList(CommonUtilsMethods.removeLastComma(WorkPlanFragment.mHQCode1).split(",")).contains(TodayPlanSfCode))
                             && !(Arrays.asList(CommonUtilsMethods.removeLastComma(WorkPlanFragment.mHQCode2).split(",")).contains(TodayPlanSfCode)))) {
                        saveHQ();
                    }
                }
            }

            prepareClusterList();

            Log.v("required_data", "---" + TodayPlanSfCode + "---" + TodayPlanClusterList);

            limitKm = Double.parseDouble(SharedPref.getDisRad(this));
        } catch (Exception e) {
            Log.v("required_data", "--tab-dcr-" + e);
        }
    }

    private void saveHQ() {
        if (WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F")) {
            saveHQ(WorkPlanFragment.mHQCode1, WorkPlanFragment.mHQName1);
        } else if (WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F")) {
            saveHQ(WorkPlanFragment.mHQCode2, WorkPlanFragment.mHQName2);
        }
    }

    private void saveHQ(String mHQCode, String mHQName) {
        String[] hqCode = CommonUtilsMethods.removeLastComma(mHQCode).split(",");
        String[] hqName = CommonUtilsMethods.removeLastComma(mHQName).split(",");
        if (hqCode.length > 0 && hqName.length > 0) {
            SharedPref.saveHq(DcrCallTabLayoutActivity.this, hqName[0], hqCode[0]);
            TodayPlanSfCode = hqCode[0];
            TodayPlanSfName = hqName[0];
        }
    }

    /*   private void prepareClusterList() {
           try {
               if(WorkPlanFragment.mHQCode1 != null && WorkPlanFragment.mHQCode1.contains(TodayPlanSfCode) && WorkPlanFragment.mTowncode1 != null && !WorkPlanFragment.mTowncode1.isEmpty()){
                   SharedPref.setTodayDayPlanClusterCode(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTowncode1);
                   SharedPref.setTodayDayPlanClusterName(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTownname1);
               } else if(WorkPlanFragment.mHQCode2 != null && WorkPlanFragment.mHQCode2.contains(TodayPlanSfCode) && WorkPlanFragment.mTowncode2 != null && !WorkPlanFragment.mTowncode2.isEmpty()){
                   SharedPref.setTodayDayPlanClusterCode(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTowncode2);
                   SharedPref.setTodayDayPlanClusterName(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTownname2);
               }
               TodayPlanClusterList.clear();
               JSONArray jsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + TodayPlanSfCode).getMasterSyncDataJsonArray();
               for (int i = 0; i<jsonArray2.length(); i++) {
                   JSONObject jsonClusterList = jsonArray2.getJSONObject(i);
                   if(SharedPref.getTodayDayPlanClusterCode(DcrCallTabLayoutActivity.this).contains(jsonClusterList.getString("Code"))) {
                       TodayPlanClusterList.add(jsonClusterList.getString("Code"));
                       TodayPlanClusterList.add(jsonClusterList.getString("Name"));
                   }
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }*/
    private void prepareClusterList() {
        try {
            if (WorkPlanFragment.mHQCode1 != null && WorkPlanFragment.mHQCode1.contains(TodayPlanSfCode) && WorkPlanFragment.mTowncode1 != null && !WorkPlanFragment.mTowncode1.isEmpty()) {
                SharedPref.setTodayDayPlanClusterCode(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTowncode1);
                SharedPref.setTodayDayPlanClusterName(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTownname1);
            } else if (WorkPlanFragment.mHQCode2 != null && WorkPlanFragment.mHQCode2.contains(TodayPlanSfCode) && WorkPlanFragment.mTowncode2 != null && !WorkPlanFragment.mTowncode2.isEmpty()) {
                SharedPref.setTodayDayPlanClusterCode(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTowncode2);
                SharedPref.setTodayDayPlanClusterName(DcrCallTabLayoutActivity.this, WorkPlanFragment.mTownname2);
            }
            TodayPlanClusterList.clear();
            JSONArray jsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + TodayPlanSfCode).getMasterSyncDataJsonArray();
            List<String> selectedClusterList = Arrays.asList(CommonUtilsMethods.removeLastComma(CommonUtilsMethods.removeDollar(SharedPref.getTodayDayPlanClusterCode(DcrCallTabLayoutActivity.this))).split(","));
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject jsonClusterList = jsonArray2.getJSONObject(i);
                if (selectedClusterList.contains(jsonClusterList.getString("Code"))) {
                    TodayPlanClusterList.add(jsonClusterList.getString("Code"));
                    TodayPlanClusterList.add(jsonClusterList.getString("Name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonAlertBox.CheckLocationStatus(DcrCallTabLayoutActivity.this, gpsTrack);
    }
}