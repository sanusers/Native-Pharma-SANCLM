package saneforce.santrip.activity.profile;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.BrandCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SlideCode;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.adapter.detailing.PlaySlideDetailing;
import saneforce.santrip.activity.presentation.presentation.PresentationActivity;
import saneforce.santrip.activity.previewPresentation.PreviewActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.storage.SQLite;


public class CustomerProfile extends AppCompatActivity {

    public static boolean isPreAnalysisCalled = false;
    public static ProgressDialog progressDialog = null;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button btn_skip, btn_start;
    ImageView img_back;
    CommonUtilsMethods commonUtilsMethods;
    CustTabLayoutAdapter viewPagerAdapter;
    SQLite sqLite;

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
            viewPager.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cust_profile);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        sqLite = new SQLite(this);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        btn_skip = findViewById(R.id.btn_skip);
        btn_start = findViewById(R.id.btn_start_det);
        img_back = findViewById(R.id.iv_back);
        isPreAnalysisCalled = false;
        commonUtilsMethods = new CommonUtilsMethods(this);
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
                        PreCallAnalysisFragment.CallPreCallAPI(CustomerProfile.this);
                    } else {
                        Toast.makeText(CustomerProfile.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
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
            intent1.putExtra("isDetailedRequired", "false");
            intent1.putExtra("from_activity", "new");
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (!UtilityClass.isNetworkAvailable(this)) {
                sqLite.saveOfflineCallIN(CommonUtilsMethods.getCurrentDate(), CommonUtilsMethods.getCurrentTimeAMPM(), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
            }
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
            intent.putExtra("MappedProdCode", CallActivityCustDetails.get(0).getMappedBrands());
            intent.putExtra("MappedSlideCode", CallActivityCustDetails.get(0).getMappedSlides());
            intent.putExtra("CusType", CallActivityCustDetails.get(0).getType());
            startActivity(intent);
        });

        img_back.setOnClickListener(view -> finish());
    }

}
