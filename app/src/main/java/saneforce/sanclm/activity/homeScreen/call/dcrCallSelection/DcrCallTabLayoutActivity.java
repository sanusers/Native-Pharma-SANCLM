package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.DCRCallSelectionTabLayoutAdapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.StockiestFragment;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.GPSTrack;
import saneforce.sanclm.databinding.CallDcrSelectionBinding;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.response.SetupResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class DcrCallTabLayoutActivity extends AppCompatActivity {
    public static String TodayPlanSfCode, TodayPlanSfName, SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, TpBasedDcr, DrGeoTag, CheGeoTag, CipGeoTag, StokistGeoTag, UndrGeoTag, GeoTagApproval, ChemistNeed, CipNeed, StockistNeed, UndrNeed, CapDr, CapChemist, CapStockist, CapCip, CapUndr;
    public static double laty, lngy, limitKm = 0.5;
    public static ArrayList<String> TodayPlanClusterList = new ArrayList<>();
    CallDcrSelectionBinding dcrSelectionBinding;
    CommonUtilsMethods commonUtilsMethods;
    LoginResponse loginResponse;
    SetupResponse setupResponse;
    DCRCallSelectionTabLayoutAdapter viewPagerAdapter;
    SQLite sqLite;
    GPSTrack gpsTrack;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrSelectionBinding = CallDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(dcrSelectionBinding.getRoot());
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreencall();
        sqLite = new SQLite(this);

        getRequiredData();

        viewPagerAdapter = new DCRCallSelectionTabLayoutAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new ListedDoctorFragment(), CapDr);

        if (ChemistNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistFragment(), CapChemist);
        }

        if (CipNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistFragment(), CapCip);
        }
        if (StockistNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new StockiestFragment(), CapStockist);
        }
        if (UndrNeed.equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new UnlistedDoctorFragment(), CapUndr);
        }

        Log.v("dfsdf", "---" + viewPagerAdapter.getCount());
        dcrSelectionBinding.viewPagerCallSelection.setAdapter(viewPagerAdapter);
        dcrSelectionBinding.tabLayoutCall.setupWithViewPager(dcrSelectionBinding.viewPagerCallSelection);
        dcrSelectionBinding.viewPagerCallSelection.setOffscreenPageLimit(viewPagerAdapter.getCount());
        dcrSelectionBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(DcrCallTabLayoutActivity.this, HomeDashBoard.class)));
    }

    private void getRequiredData() {
        try {
            gpsTrack = new GPSTrack(this);
            laty = gpsTrack.getLatitude();
            lngy = gpsTrack.getLongitude();
            loginResponse = new LoginResponse();
            loginResponse = sqLite.getLoginData(true);

            SfType = loginResponse.getSf_type();
            SfCode = loginResponse.getSF_Code();
            SfName = loginResponse.getSF_Name();

            JSONArray jsonArray = new JSONArray();
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);

                setupResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setupResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
                limitKm = Double.parseDouble(setupResponse.getMapGeoFenceCircleRad());
                GeoTagApproval = setupResponse.getGeoTagApprovalNeed();
                TpBasedDcr = setupResponse.getTpBasedDcr();

                DrGeoTag = setupResponse.getDrGeoTagNeed();
                CheGeoTag = setupResponse.getChemistGeoTagNeed();
                CipGeoTag = setupResponse.getCipGeoTagNeed();
                StokistGeoTag = setupResponse.getStockistGeoTagNeed();
                UndrGeoTag = setupResponse.getUndrGeoTagNeed();

                CapDr = setupResponse.getCaptionDr();
                ChemistNeed = setupResponse.getChemistNeed();
                CapChemist = setupResponse.getCaptionChemist();

                if (setupData.has("cip_need")) {
                    CipNeed = setupResponse.getCIPNeed();
                    CapCip = setupResponse.getCaptionCip();
                } else {
                    CipNeed = "1";
                }
                StockistNeed = setupResponse.getStockistNeed();
                CapStockist = setupResponse.getCaptionStockist();
                UndrNeed = setupResponse.getUnDrNeed();
                CapUndr = setupResponse.getCaptionUndr();
            }
            Log.v("fff", "-00--" + TodayPlanSfCode);
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


            JSONArray jsonArray2 = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + TodayPlanSfCode);
            for (int i = 0; i < 2; i++) {
                JSONObject jsonClusterList = jsonArray2.getJSONObject(i);
                TodayPlanClusterList.add(jsonClusterList.getString("Code"));
                TodayPlanClusterList.add(jsonClusterList.getString("Name"));
            }


        } catch (Exception e) {
            Log.v("fff", e.toString());
        }
    }
}