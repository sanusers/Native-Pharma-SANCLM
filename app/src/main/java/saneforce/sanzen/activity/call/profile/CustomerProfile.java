package saneforce.sanzen.activity.call.profile;

import static saneforce.sanzen.activity.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.sanzen.activity.call.DCRCallActivity.CusCheckInOutNeed;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter.storingSlide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.call.profile.preCallAnalysis.PreCallAnalysisFragment;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.previewPresentation.PreviewActivity;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class CustomerProfile extends AppCompatActivity {
    public static boolean isPreAnalysisCalled = false;
    public static ProgressDialog progressDialog = null;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button btn_skip, btn_start;
    ImageView img_back;
    CommonUtilsMethods commonUtilsMethods;
    CustTabLayoutAdapter viewPagerAdapter;
    TextView cusName;
    private RoomDB roomDB;
    private CallOfflineDataDao callOfflineDataDao;
    private JSONObject checkInJsonObject = new JSONObject();

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("call", CallActivityCustDetails);
        outState.putString("CheckInJsonObject", checkInJsonObject.toString());
        if (HomeDashBoard.selectedDate != null) {
            outState.putString("date", HomeDashBoard.selectedDate.toString());
            outState.putInt(Manifest.permission.ACCESS_FINE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
            outState.putInt(Manifest.permission.ACCESS_COARSE_LOCATION, ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
            outState.putInt(Manifest.permission.CAMERA, ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                outState.putInt(Manifest.permission.READ_MEDIA_AUDIO, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO));
//                outState.putInt(Manifest.permission.READ_MEDIA_VIDEO, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO));
//                outState.putInt(Manifest.permission.READ_MEDIA_IMAGES, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES));
//            }
            outState.putInt(Manifest.permission.READ_EXTERNAL_STORAGE, ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE));
            outState.putInt(Manifest.permission.WRITE_EXTERNAL_STORAGE, ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        }
        outState.putBoolean("isSaved", true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cust_profile);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(this);
        callOfflineDataDao = roomDB.callOfflineDataDao();
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        btn_skip = findViewById(R.id.btn_skip);
        btn_start = findViewById(R.id.btn_start_det);
        img_back = findViewById(R.id.iv_back);
        cusName = findViewById(R.id.tag_selection);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isSaved")) {
            try {
                CallActivityCustDetails = savedInstanceState.getParcelableArrayList("call");
                checkInJsonObject = new JSONObject(savedInstanceState.getString("CheckInJsonObject"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (savedInstanceState.getString("date") != null) {
                HomeDashBoard.selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_FINE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != savedInstanceState.getInt(Manifest.permission.ACCESS_COARSE_LOCATION, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != savedInstanceState.getInt(Manifest.permission.CAMERA, -1)
//                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_AUDIO, -1)
//                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_VIDEO, -1)
//                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != savedInstanceState.getInt(Manifest.permission.READ_MEDIA_IMAGES, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.READ_EXTERNAL_STORAGE, -1)
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != savedInstanceState.getInt(Manifest.permission.WRITE_EXTERNAL_STORAGE, -1)) {
                CommonAlertBox.permissionChangeAlert(this);
            }
        }
        isPreAnalysisCalled = false;
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("CheckInJsonObject")) {
                String jsonObject = bundle.getString("CheckInJsonObject");
                checkInJsonObject = new JSONObject(jsonObject);
            } else {
                checkInJsonObject = new JSONObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPagerAdapter = new CustTabLayoutAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new OverviewFragment(), "Overview");
        viewPagerAdapter.add(new PreCallAnalysisFragment(), "Pre Call Analysis");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        if (CallActivityCustDetails != null) {
            cusName.setText(CallActivityCustDetails.get(0).getName());
        } else {
            Intent intent = new Intent(CustomerProfile.this, HomeDashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1 && !isPreAnalysisCalled) {
                    if (UtilityClass.isNetworkAvailable(CustomerProfile.this)) {
                        progressDialog = CommonUtilsMethods.createProgressDialog(CustomerProfile.this);
                        PreCallAnalysisFragment.CallPreCallAPI(getApplicationContext(), CustomerProfile.this);
                    } else {
                        commonUtilsMethods.showToastMessage(CustomerProfile.this, getString(R.string.no_network));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        btn_skip.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent intent1 = new Intent(CustomerProfile.this, DCRCallActivity.class);
                intent1.putExtra(Constants.DETAILING_REQUIRED, "false");
                intent1.putExtra(Constants.DCR_FROM_ACTIVITY, "new");
                intent1.putExtra("remainder_save", "0");
                intent1.putExtra("hq_code", "");
                intent1.putExtra("CheckInJsonObject", checkInJsonObject.toString());

                //  intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                callOfflineDataDao.saveOfflineCallIN(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
                startActivity(intent1);
            }
        });

        btn_start.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (HomeDashBoard.selectedDate != null) {
                    storingSlide.clear();
                    Intent intent = new Intent(CustomerProfile.this, PreviewActivity.class);
                    intent.putExtra("from", "call");
                    intent.putExtra("cus_name", CallActivityCustDetails.get(0).getName());
                    intent.putExtra("cus_code", CallActivityCustDetails.get(0).getCode());
                    PlaySlideDetailing.SpecialityCodePlay = CallActivityCustDetails.get(0).getSpecialistCode();
                    PlaySlideDetailing.MappedBrandsPlay = CallActivityCustDetails.get(0).getMappedBrands();
                    PlaySlideDetailing.MappedSlidesPlay = CallActivityCustDetails.get(0).getMappedSlides();
                    intent.putExtra("SpecialityCode", CallActivityCustDetails.get(0).getSpecialistCode());
                    intent.putExtra("SpecialityName", CallActivityCustDetails.get(0).getSpecialist());
                    intent.putExtra("MappedProdCode", CallActivityCustDetails.get(0).getMappedBrands());
                    intent.putExtra("MappedSlideCode", CallActivityCustDetails.get(0).getMappedSlides());
                    intent.putExtra("CusType", CallActivityCustDetails.get(0).getType());
                    intent.putExtra("CheckInJsonObject", checkInJsonObject.toString());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomerProfile.this, HomeDashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    CommonUtilsMethods.showToastMessage(CustomerProfile.this, getString(R.string.please_try_again));
                    finish();
                }
            }
        });

        String detailingNeed;
        switch (CallActivityCustDetails.get(0).getType()) {
            case "2":
                detailingNeed = SharedPref.getCHMDetailingNeed(this);
                break;
            case "3":
                detailingNeed = SharedPref.getSTKDetailingNeed(this);
                break;
            case "4":
                detailingNeed = SharedPref.getUNDRDetailingNeed(this);
                break;
            default:
                detailingNeed = "0";
                break;
        }

        if (detailingNeed.equalsIgnoreCase("0")) {
            btn_start.setVisibility(View.VISIBLE);
        } else {
            btn_start.setVisibility(View.GONE);
        }

        String skipNeed;
        switch (CallActivityCustDetails.get(0).getType()) {
            case "1":
                skipNeed = SharedPref.getSkipDetailingDr(this);
                break;
            case "2":
                skipNeed = SharedPref.getSkipDetailingChe(this);
                break;
            case "3":
                skipNeed = SharedPref.getSkipDetailingStk(this);
                break;
            case "4":
                skipNeed = SharedPref.getSkipDetailingUndr(this);
                break;
            default:
                skipNeed = "0";
                break;
        }
        if (skipNeed.equalsIgnoreCase("0")) {
            btn_skip.setVisibility(View.VISIBLE);
        } else {
            btn_skip.setVisibility(View.GONE);
        }


        img_back.setOnClickListener(view -> finish());
    }

}
