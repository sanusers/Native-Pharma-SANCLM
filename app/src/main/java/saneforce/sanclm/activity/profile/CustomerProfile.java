package saneforce.sanclm.activity.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.presentation.Presentation;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.UtilityClass;


public class CustomerProfile extends AppCompatActivity {
    public static String tv_custName = "", tv_cust_area, tv_custCode = "";
    public static boolean isDetailingRequired;
    public static boolean isPreAnalysisCalled = false;
    public static ProgressDialog progressDialog = null;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button btn_skip, btn_start;
    ImageView img_back;
    CommonUtilsMethods commonUtilsMethods;
    CustTabLayoutAdapter viewPagerAdapter;

    @Override
    public void onBackPressed() {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cust_profile);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        btn_skip = findViewById(R.id.btn_skip);
        btn_start = findViewById(R.id.btn_start_det);
        img_back = findViewById(R.id.iv_back);
        isPreAnalysisCalled = false;
        commonUtilsMethods = new CommonUtilsMethods(this);
        // commonUtilsMethods.FullScreencall();
        progressDialog = CommonUtilsMethods.createProgressDialog(CustomerProfile.this);
        //  progressDialog.dismiss();
        viewPagerAdapter = new CustTabLayoutAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new OverviewFragment(), "Overview");
        viewPagerAdapter.add(new PreCallAnalysisFragment(), "Pre Call Analysis");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.v("sa", String.valueOf(tab.getPosition()));
                if (UtilityClass.isNetworkAvailable(CustomerProfile.this)) {
                    if (tab.getPosition() == 1 && !isPreAnalysisCalled) {
                        if (progressDialog == null) {
                            progressDialog = CommonUtilsMethods.createProgressDialog(CustomerProfile.this);
                            progressDialog.show();
                        } else {
                            progressDialog.show();
                        }
                        PreCallAnalysisFragment.CallPreCallAPI();
                    }
                } else {
                    Toast.makeText(CustomerProfile.this, "No internet connectivity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

      /*  Bundle extra = getIntent().getExtras();
        if (extra != null) {
            tv_custCode = extra.getString("cust_code");
            tv_custName = extra.getString("cust_name");
            tv_cust_area = extra.getString("cust_addr");
        }*/


        //Disable Touch Event in Tab
       /* LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> true);
        }
        */

        btn_skip.setOnClickListener(view -> {
            Intent intent1 = new Intent(CustomerProfile.this, DCRCallActivity.class);
            isDetailingRequired = false;
         /*   intent1.putExtra("cust_name", tv_custName);
            intent1.putExtra("cust_addr", tv_cust_area);
            intent1.putExtra("sf_code", TodayPlanSfCode);*/
            startActivity(intent1);
        });

        btn_start.setOnClickListener(view -> {
            startActivity(new Intent(CustomerProfile.this, Presentation.class));
            isDetailingRequired = true;
        });

        img_back.setOnClickListener(view -> finish());
    }

}
