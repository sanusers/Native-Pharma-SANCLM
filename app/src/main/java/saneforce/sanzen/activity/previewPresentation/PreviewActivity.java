package saneforce.sanzen.activity.previewPresentation;

import static saneforce.sanzen.activity.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.sanzen.activity.call.DCRCallActivity.arrayStore;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing.headingData;
import static saneforce.sanzen.activity.call.fragments.detailing.DetailedFragment.callDetailingLists;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.pojo.detailing.CallDetailingList;
import saneforce.sanzen.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.previewPresentation.fragment.BrandMatrix;
import saneforce.sanzen.activity.previewPresentation.fragment.MyPresentation;
import saneforce.sanzen.activity.previewPresentation.fragment.HomeBrands;
import saneforce.sanzen.activity.previewPresentation.fragment.Speciality;
import saneforce.sanzen.activity.previewPresentation.fragment.Therapist;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.response.CustomSetupResponse;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.utility.TimeUtils;

public class PreviewActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static saneforce.sanzen.databinding.ActivityPreviewBinding previewBinding;
    public static String SelectedTab = "Matrix", from_where = "", cus_name = "", SpecialityCode = "", SpecialityName = "", BrandCode = "", SlideCode = "", CusType = "";
    public static int SelectedPosPlay;
    PreviewTabAdapter viewPagerAdapter;
    String finalPrdNam;
    ArrayList<StoreImageTypeUrl> dummyArr = new ArrayList<>();
    String startT, endT, CustomPresentationNeed;
    CommonUtilsMethods commonUtilsMethods;
    CustomSetupResponse customSetupResponse;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private CallOfflineDataDao callOfflineDataDao;
    ProgressDialog progressDialog;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            previewBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previewBinding = saneforce.sanzen.databinding.ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(previewBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        callDetailingLists = new ArrayList<>();
        arrayStore = new ArrayList<>();

        getRequiredData();
        Bundle extra = getIntent().getExtras();
            if (extra != null) {
                from_where = extra.getString("from");
                assert from_where != null;
                if (from_where.equalsIgnoreCase("call")) {
                    cus_name = extra.getString("cus_name");
                    SpecialityCode = extra.getString("SpecialityCode");
                    SpecialityName = extra.getString("SpecialityName");
                    BrandCode = extra.getString("MappedProdCode");
                    SlideCode = extra.getString("MappedSlideCode");
                    CusType = extra.getString("CusType");
                    previewBinding.tagCustName.setText(cus_name);
                    previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
                }
            }
            viewPagerAdapter = new PreviewTabAdapter(getSupportFragmentManager());

            if (from_where.equalsIgnoreCase("call")) {
                headingData.clear();
                if (CusType.equalsIgnoreCase("1")) {
                    viewPagerAdapter.add(new HomeBrands(), getResources().getString(R.string.home));
                    headingData.add("A");
                    viewPagerAdapter.add(new BrandMatrix(), getResources().getString(R.string.brand_matrix));
                    headingData.add("B");
                    viewPagerAdapter.add(new Speciality(), getResources().getString(R.string.speciality));
                    headingData.add("C");
                    if (CustomPresentationNeed.equalsIgnoreCase("0")) {
                        viewPagerAdapter.add(new MyPresentation(), getResources().getString(R.string.my_presentation));
                        headingData.add("D");
                    }
                } else {
                    viewPagerAdapter.add(new HomeBrands(), getResources().getString(R.string.home));
                    headingData.add("A");
                    viewPagerAdapter.add(new Speciality(), getResources().getString(R.string.speciality));
                    headingData.add("C");
                    if (CustomPresentationNeed.equalsIgnoreCase("0")) {
                        viewPagerAdapter.add(new MyPresentation(), getResources().getString(R.string.my_presentation));
                        headingData.add("D");
                    }
                }
            } else {
                viewPagerAdapter.add(new HomeBrands(), getResources().getString(R.string.home));
                viewPagerAdapter.add(new BrandMatrix(), getResources().getString(R.string.brand_matrix));
                viewPagerAdapter.add(new Speciality(), getResources().getString(R.string.speciality));
//                viewPagerAdapter.add(new Therapist(), getString(R.string.therapist));
                if (CustomPresentationNeed.equalsIgnoreCase("0"))
                    viewPagerAdapter.add(new MyPresentation(), getResources().getString(R.string.my_presentation));
            }
            previewBinding.viewPager.setAdapter(viewPagerAdapter);
            previewBinding.tabLayout.setupWithViewPager(previewBinding.viewPager);
            previewBinding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

            previewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SelectedPosPlay = tab.getPosition();
                if (tab.getPosition() == 1) SelectedTab = "Matrix";
                if (tab.getPosition() == 2) SelectedTab = "Spec";
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        previewBinding.ivBack.setOnClickListener(v -> {
            if (from_where.equalsIgnoreCase("call")) {
                Intent intent = new Intent(PreviewActivity.this, DcrCallTabLayoutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        previewBinding.btnFinishDet.setOnClickListener(v -> {
            Collections.sort(arrayStore, new StoreImageTypeUrl.StoreImageComparator());
            String totalDuration = "";
            for (int j = 0; j < arrayStore.size(); j++) {
                if (j == 0) {
                    gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j);
                    finalPrdNam = arrayStore.get(j).getBrdName();
                } else if (finalPrdNam.equalsIgnoreCase(arrayStore.get(j).getBrdName())) {
                    try {
                        JSONArray jsonArray = new JSONArray(arrayStore.get(j-1).getRemTime());
                        for (int i = 0; i<jsonArray.length(); i++) {
                            String duration = TimeUtils.timeDurationHMS(jsonArray.getJSONObject(i).getString("sT"), jsonArray.getJSONObject(i).getString("eT"));
                            totalDuration = TimeUtils.addTime(totalDuration, duration);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String time = gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j) + " " + gettingProductTiming(arrayStore.get(j - 1).getBrdName());
                    if (time.contains("00:00:00")) {
                        time = time.replace("00:00:00", time.substring(0, 8));
                    }
                    Log.v("printing_all_time", time);
                    try {
                        JSONArray jsonArray = new JSONArray(arrayStore.get(j-1).getRemTime());
                        for (int i = 0; i<jsonArray.length(); i++) {
                            String duration = TimeUtils.timeDurationHMS(jsonArray.getJSONObject(i).getString("sT"), jsonArray.getJSONObject(i).getString("eT"));
                            totalDuration = TimeUtils.addTime(totalDuration, duration);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callDetailingLists.add(new CallDetailingList(arrayStore.get(j - 1).getBrdName(), arrayStore.get(j - 1).getBrdCode(), arrayStore.get(j - 1).getSlideNam(), arrayStore.get(j - 1).getSlideTyp(), arrayStore.get(j - 1).getSlideUrl(), time, time.substring(0, 8), 0, "", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), totalDuration));
                    finalPrdNam = arrayStore.get(j).getBrdName();
                    totalDuration = "";
                }
            }

            if (arrayStore.size() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(arrayStore.get(arrayStore.size()-1).getRemTime());
                    for (int i = 0; i<jsonArray.length(); i++) {
                        String duration = TimeUtils.timeDurationHMS(jsonArray.getJSONObject(i).getString("sT"), jsonArray.getJSONObject(i).getString("eT"));
                        totalDuration = TimeUtils.addTime(totalDuration, duration);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String time = gettingProductStartEndTime1(arrayStore.get(arrayStore.size() - 1).getRemTime(), arrayStore.size() - 1) + " " + gettingProductTiming(arrayStore.get(arrayStore.size() - 1).getBrdName());
                callDetailingLists.add(new CallDetailingList(arrayStore.get(arrayStore.size() - 1).getBrdName(), arrayStore.get(arrayStore.size() - 1).getBrdCode(), arrayStore.get(arrayStore.size() - 1).getSlideNam(), arrayStore.get(arrayStore.size() - 1).getSlideTyp(), arrayStore.get(arrayStore.size() - 1).getSlideUrl(), time, time.substring(0, 8), 0, "", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), totalDuration));
            }
            Intent intent1 = new Intent(PreviewActivity.this, DCRCallActivity.class);
            intent1.putExtra(Constants.DETAILING_REQUIRED, "true");
            intent1.putExtra(Constants.DCR_FROM_ACTIVITY, "new");
            intent1 .putExtra("remainder_save", "0");
            intent1.putExtra("hq_code", "" );
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (!UtilityClass.isNetworkAvailable(this)) {
                callOfflineDataDao.saveOfflineCallIN(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
            }
            startActivity(intent1);
        });
    }

    private void getRequiredData() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CUSTOM_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                customSetupResponse = new CustomSetupResponse();
                Type typeSetup = new TypeToken<CustomSetupResponse>() {
                }.getType();
                customSetupResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
                CustomPresentationNeed = customSetupResponse.getCustomizationPrsNeed();
            }
        } catch (Exception ignored) {
        }
    }


    public String gettingProductTiming(String BrandName) {
        String maxTime = null;
        String minTime = null;
        dummyArr.clear();
        try {
            for (int i = 0; i < arrayStore.size(); i++) {
                if (arrayStore.get(i).getBrdName().equalsIgnoreCase(BrandName)) {
                    dummyArr.add(new StoreImageTypeUrl(arrayStore.get(i).getScribble(), arrayStore.get(i).getSlideNam(), arrayStore.get(i).getSlideTyp(), arrayStore.get(i).getSlideUrl(), arrayStore.get(i).getRemTime(), arrayStore.get(i).getSlideComments(), arrayStore.get(i).getTiming()));
                }
            }
            ArrayList<String> timesMax = new ArrayList<>();
            ArrayList<String> timesMin = new ArrayList<>();
            for (int i1 = 0; i1 < dummyArr.size(); i1++) {
                StoreImageTypeUrl mm1 = dummyArr.get(i1);
                JSONArray jj = new JSONArray(mm1.getTiming());

                if (jj.length() > 0) {
                    JSONObject jsr = jj.getJSONObject(jj.length() - 1);
                    timesMax.add(jsr.getString("eT"));
                    timesMin.add(jsr.getString("sT"));
                }
            }
            String timesMaxnew = timesMax.toString().replace("[", "").replace("]", "");
            String timesMinnew = timesMin.toString().replace("[", "").replace("]", "");

            String[] allTimesMax = timesMaxnew.replaceAll(" ", "").split(",");
            String[] allTimesMin = timesMinnew.replaceAll(" ", "").split(",");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                maxTime = Stream.of(allTimesMax).max(String::compareTo).get();
                minTime = Stream.of(allTimesMin).min(String::compareTo).get();
            }

            return maxTime;
        } catch (Exception ignored) {
        }
        return maxTime;
    }

    public String gettingProductStartEndTime1(String jsonvalue, int i) {
        String finalTime = null;
        StoreImageTypeUrl mm, mm1, mm2;
        try {
            JSONArray json = new JSONArray(jsonvalue);
            JSONArray json2 = new JSONArray(jsonvalue);
            mm = arrayStore.get(i);
            json = new JSONArray(mm.getRemTime());
            JSONObject jjj = json.getJSONObject(0);
            Log.v("last_value_time", jjj.getString("sT"));
            startT = jjj.getString("sT");
            //  finalTime = startT + " " + jjj.getString("eT");
            finalTime = startT;
            if (i == arrayStore.size() - 1) {
                mm1 = arrayStore.get(arrayStore.size() - 1);
                json = new JSONArray(mm1.getRemTime());
                JSONObject jj = json.getJSONObject(0);
                endT = jj.getString("eT");
                for (int j = 0; j < i; j++) {
                    if (arrayStore.get(j).getBrdName().equals(mm1.getBrdName())) {
                        mm2 = arrayStore.get(j);
                        json2 = new JSONArray(mm2.getRemTime());
                        JSONObject jj2 = json2.getJSONObject(0);
                        startT = jj2.getString("sT");
                        break;
                    }
                }
                finalTime = startT;
            }
            return finalTime;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalTime;
    }

    public String gettingProductStartEndTime(String jsonvalue, int i) {
        String finalTime = null;
        StoreImageTypeUrl mm, mm1;
        try {
            JSONArray json;
            if (i != 0) {

                mm1 = arrayStore.get(i - 1);
                json = new JSONArray(mm1.getRemTime());
                JSONObject jj = json.getJSONObject(0);
                endT = jj.getString("eT");
            }
            finalTime = startT;
            mm = arrayStore.get(i);
            json = new JSONArray(mm.getRemTime());
            JSONObject jj = json.getJSONObject(0);
            Log.v("last_value_timemid", jj.getString("sT"));
            startT = jj.getString("sT");
            if (arrayStore.size() == 1) {
                mm = arrayStore.get(i);
                json = new JSONArray(mm.getRemTime());
                JSONObject jjj = json.getJSONObject(0);
                Log.v("last_value_time", jjj.getString("sT"));
                startT = jjj.getString("sT");
                finalTime = startT;
            }
            return finalTime;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalTime;
    }

}
