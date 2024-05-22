package saneforce.sanzen.activity.call.profile;

import static saneforce.sanzen.activity.call.DCRCallActivity.CallActivityCustDetails;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.time.format.DateTimeFormatter;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.previewPresentation.PreviewActivity;
import saneforce.sanzen.activity.call.profile.preCallAnalysis.PreCallAnalysisFragment;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        cusName.setText(CallActivityCustDetails.get(0).getName());
        isPreAnalysisCalled = false;
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        viewPagerAdapter = new CustTabLayoutAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new OverviewFragment(), "Overview");
        viewPagerAdapter.add(new PreCallAnalysisFragment(), "Pre Call Analysis");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

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


        btn_skip.setOnClickListener(view -> {
            Intent intent1 = new Intent(CustomerProfile.this, DCRCallActivity.class);
            intent1.putExtra(Constants.DETAILING_REQUIRED, "false");
            intent1.putExtra(Constants.DCR_FROM_ACTIVITY, "new");
            intent1.putExtra("remainder_save", "0");
            intent1.putExtra("hq_code", "" );

            //  intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            callOfflineDataDao.saveOfflineCallIN(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
            startActivity(intent1);
        });

        btn_start.setOnClickListener(view -> {

            Intent intent = new Intent(CustomerProfile.this, PreviewActivity.class);
            intent.putExtra("from", "call");
            intent.putExtra("cus_name", CallActivityCustDetails.get(0).getName());
            PlaySlideDetailing.SpecialityCodePlay = CallActivityCustDetails.get(0).getSpecialistCode();
            PlaySlideDetailing.MappedBrandsPlay = CallActivityCustDetails.get(0).getMappedBrands();
            PlaySlideDetailing.MappedSlidesPlay = CallActivityCustDetails.get(0).getMappedSlides();
            intent.putExtra("SpecialityCode", CallActivityCustDetails.get(0).getSpecialistCode());
            intent.putExtra("SpecialityName", CallActivityCustDetails.get(0).getSpecialist());
            intent.putExtra("MappedProdCode", CallActivityCustDetails.get(0).getMappedBrands());
            intent.putExtra("MappedSlideCode", CallActivityCustDetails.get(0).getMappedSlides());
            intent.putExtra("CusType", CallActivityCustDetails.get(0).getType());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        img_back.setOnClickListener(view -> finish());
    }

}
