package saneforce.santrip.activity.call.dcrCallSelection;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.IsFromDCR;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.activity.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.santrip.activity.call.dcrCallSelection.fragments.CIPFragment;
import saneforce.santrip.activity.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.santrip.activity.call.dcrCallSelection.fragments.HospitalFragment;
import saneforce.santrip.activity.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.santrip.activity.call.dcrCallSelection.fragments.StockiestFragment;
import saneforce.santrip.activity.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.databinding.CallDcrSelectionBinding;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class DcrCallTabLayoutActivity extends AppCompatActivity {

    public static String TodayPlanSfCode, TodayPlanSfName;
    public static double lat, lng, limitKm = 0.5;
    public static ArrayList<String> TodayPlanClusterList = new ArrayList<>();
    CallDcrSelectionBinding dcrSelectionBinding;

    TabLayoutAdapter viewPagerAdapter;
    SQLite sqLite;
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            dcrSelectionBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrSelectionBinding = CallDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(dcrSelectionBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        sqLite = new SQLite(this);

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
                    JSONArray jsonArray1 = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                    for (int i = 0; i < 1; i++) {
                        JSONObject jsonHQList = jsonArray1.getJSONObject(0);
                        TodayPlanSfCode = jsonHQList.getString("id");
                        TodayPlanSfName = jsonHQList.getString("name");
                    }
                }
            }

            TodayPlanClusterList.clear();
            JSONArray jsonArray2 = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + TodayPlanSfCode);
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



}