package saneforce.santrip.activity.homeScreen.call.dcrCallSelection;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments.CIPFragment;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments.HospitalFragment;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments.StockiestFragment;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.databinding.CallDcrSelectionBinding;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class DcrCallTabLayoutActivity extends AppCompatActivity {
    public static String TodayPlanSfCode, TodayPlanSfName, SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, TpBasedDcr, DrGeoTag, CheGeoTag, CipGeoTag, StockiestGeoTag, UnDrGeoTag, GeoTagApproval, DrNeed,ChemistNeed, CipNeed, StockistNeed, UnDrNeed, CapDr, CapChemist, CapStockist, CapCip, CapUnDr, CapHos, HospNeed;
    public static double lat, lng, limitKm = 0.5;
    public static ArrayList<String> TodayPlanClusterList = new ArrayList<>();
    CallDcrSelectionBinding dcrSelectionBinding;
    LoginResponse loginResponse;
    TabLayoutAdapter viewPagerAdapter;
    SQLite sqLite;
    GPSTrack gpsTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrSelectionBinding = CallDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(dcrSelectionBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sqLite = new SQLite(this);

        getRequiredData();

        viewPagerAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        if (DrNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ListedDoctorFragment(), CapDr);
        }

        if (ChemistNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistFragment(), CapChemist);
        }
        if (CipNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new CIPFragment(), CapCip);
        }
        if (StockistNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new StockiestFragment(), CapStockist);
        }
        if (UnDrNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new UnlistedDoctorFragment(), CapUnDr);
        }
        if (HospNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new HospitalFragment(), CapHos);
        }

        dcrSelectionBinding.viewPagerCallSelection.setAdapter(viewPagerAdapter);
        dcrSelectionBinding.tabLayoutCall.setupWithViewPager(dcrSelectionBinding.viewPagerCallSelection);
        //dcrSelectionBinding.viewPagerCallSelection.setOffscreenPageLimit(viewPagerAdapter.getCount());
        dcrSelectionBinding.viewPagerCallSelection.setOffscreenPageLimit(0);

        dcrSelectionBinding.ivBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        //  dcrSelectionBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(DcrCallTabLayoutActivity.this, HomeDashBoard.class)));
    }

    private void getRequiredData() {
        try {
            gpsTrack = new GPSTrack(this);
            lat = gpsTrack.getLatitude();
            lng = gpsTrack.getLongitude();
            loginResponse = new LoginResponse();
            loginResponse = sqLite.getLoginData();

            SfType = loginResponse.getSf_type();
            SfCode = loginResponse.getSF_Code();
            SfName = loginResponse.getSF_Name();

            GeoTagApproval = loginResponse.getGeoTagApprovalNeed();
            TpBasedDcr = loginResponse.getTPbasedDCR();

            CapDr = loginResponse.getDrCap();
            CapChemist = loginResponse.getChmCap();
            CapStockist = loginResponse.getStkCap();
            CapUnDr = loginResponse.getNLCap();
            CapCip = loginResponse.getCIP_Caption();
            CapHos = loginResponse.getHosp_caption();

            CipNeed = loginResponse.getCip_need();
            DrNeed = loginResponse.getDrNeed();
            ChemistNeed = loginResponse.getChmNeed();
            StockistNeed = loginResponse.getStkNeed();
            UnDrNeed = loginResponse.getUNLNeed();
            HospNeed = loginResponse.getHosp_need();

            DrGeoTag = loginResponse.getGEOTagNeed();
            CheGeoTag = loginResponse.getGEOTagNeedche();
            CipGeoTag = loginResponse.getGeoTagNeedcip();
            StockiestGeoTag = loginResponse.getGEOTagNeedstock();
            UnDrGeoTag = loginResponse.getGEOTagNeedunlst();

            if (SfType.equalsIgnoreCase("1")) {
                TodayPlanSfCode = SfCode;
                TodayPlanSfName = SfName;
            } else {
                TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(this);
                TodayPlanSfName = SharedPref.getTodayDayPlanSfName(this);
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

        } catch (Exception e) {
            Log.v("required_data", "--tab-dcr-" + e);
        }
    }
}