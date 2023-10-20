package saneforce.sanclm.activity.homeScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.sanclm.activity.homeScreen.adapters.CustomViewPager;
import saneforce.sanclm.activity.homeScreen.adapters.ViewpagetAdapter;
import saneforce.sanclm.activity.leave.Leave_Application;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.sanclm.activity.homeScreen.adapters.CustomViewPager;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.activity.homeScreen.adapters.ViewpagetAdapter;
import saneforce.sanclm.activity.leave.Leave_Application;
import saneforce.sanclm.activity.masterSync.MasterSyncActivity;
import saneforce.sanclm.activity.presentation.CreatePresentation;
import saneforce.sanclm.activity.presentation.Presentation;
import saneforce.sanclm.activity.tourPlan.TourPlanActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static ViewPager2 viewPager;
    public static CustomViewPager viewPager1;
    public static int DeviceWith;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView imageView;
    private DrawerLayout drawerLayout;
    TabLayout tabLayout;
    ViewpagetAdapter viewpagetAdapter;
    NavigationView navigationView;
    LinearLayout pre_layout, slide_layout, report_layout, anlas_layout;
    ImageView masterSync;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    SQLite sqLite;




    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, true);
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(HomeDashBoard.this);
        }
        commonUtilsMethods.FullScreencall();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreencall();
        sqLite = new SQLite(HomeDashBoard.this);
        sqLite.getWritableDatabase();
        pre_layout = findViewById(R.id.ll_presentation);
        slide_layout = findViewById(R.id.ll_slide);
        report_layout = findViewById(R.id.ll_report);
        anlas_layout = findViewById(R.id.ll_analys);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        imageView = findViewById(R.id.back_arrow);
        viewPager = findViewById(R.id.view_pager);
        viewPager1 = findViewById(R.id.view_pager1);
        tabLayout = findViewById(R.id.tablelayout);
        navigationView = findViewById(R.id.nav_view);
        masterSync = findViewById(R.id.img_sync);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceWith = displayMetrics.widthPixels;


//        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
//        layoutParams.width = DeviceWith / 3;// You can replace R.dimen.navigation_drawer_width with the width you want
//        DeviceWith=displayMetrics.widthPixels;

        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.width = DeviceWith/3;// You can replace R.dimen.navigation_drawer_width with the width you want
        navigationView.setLayoutParams(layoutParams);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        imageView.setBackgroundResource(R.drawable.bars_sort_img);

        viewpagetAdapter = new ViewpagetAdapter(this, 1);
        viewPager.setAdapter(viewpagetAdapter);

        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager1.setAdapter(adapter);

        int width = (int) ((((DeviceWith / 3) * 2) /3)-30);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        param.setMargins(0, 5, 10, 0);

        pre_layout.setLayoutParams(param);
        slide_layout.setLayoutParams(param);
        report_layout.setLayoutParams(param);
        anlas_layout.setLayoutParams(param);

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(""));
        mediator.attach();
        setupCustomTab(tabLayout, 0, "WorkPlan", false);
        setupCustomTab(tabLayout, 1, "Calls", false);
        setupCustomTab(tabLayout, 2, "Outbox", true);


        // Listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTitle = tab.getCustomView().findViewById(R.id.tablayname);
                tabTitle.setTextColor(getResources().getColor(R.color.text_dark));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTitle = tab.getCustomView().findViewById(R.id.tablayname);
                tabTitle.setTextColor(getResources().getColor(R.color.text_dark_65));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                imageView.setBackgroundResource(R.drawable.cross_img);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                imageView.setBackgroundResource(R.drawable.bars_sort_img);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    imageView.setBackgroundResource(R.drawable.bars_sort_img);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    imageView.setBackgroundResource(R.drawable.cross_img);

                }
            }
        });

        pre_layout.setOnClickListener(view -> startActivity(new Intent(HomeDashBoard.this, Presentation.class)));

        masterSync.setOnClickListener(view -> startActivity(new Intent(HomeDashBoard.this, MasterSyncActivity.class)));


    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_leave_appln) {
            startActivity(new Intent(HomeDashBoard.this, Leave_Application.class));
            return true;
        }

        if (id == R.id.nav_tour_plan) {
            startActivity(new Intent(HomeDashBoard.this, TourPlanActivity.class));
            return true;
        }

        if (id == R.id.nav_approvals) {
            startActivity(new Intent(HomeDashBoard.this, ApprovalsActivity.class));
            return true;
        }

        if (id == R.id.nav_create_presentation) {
            startActivity(new Intent(HomeDashBoard.this, CreatePresentation.class));
            return true;
        }

        if (id == R.id.nav_nearme) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                Intent intent = new Intent(HomeDashBoard.this, MapsActivity.class);
                intent.putExtra("from", "not_tagging");
                MapsActivity.SelectedTab = "D";
                if (SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this).equalsIgnoreCase("null")) {
                    MapsActivity.SelectedHqCode = "";
                    MapsActivity.SelectedHqName = "";
                } else {
                    MapsActivity.SelectedHqCode = SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this);
                    MapsActivity.SelectedHqName = SharedPref.getTodayDayPlanSfName(HomeDashBoard.this);
                }
                //SharedPref.setMapSelectedTab(HomeDashBoard.this, "D");
                //  SharedPref.setSelectedHqCode(HomeDashBoard.this, SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this));
                startActivity(intent);
                return true;
            } else {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.logout) {
            startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
        }


        return true;
    }


    private void setupCustomTab(TabLayout tabLayout, int tabIndex, String tabTitleText, boolean isTabTitleInvisible) {
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) {
            View customView = LayoutInflater.from(this).inflate(R.layout.customtab_item, null);
            tab.setCustomView(customView);
            TextView tabTitle = customView.findViewById(R.id.tablayname);
            if (tabIndex == 0) {
                tabTitle.setTextColor(getResources().getColor(R.color.text_dark));
            }

            tabTitle.setText(tabTitleText);
            TextView tabTitleInvisible = customView.findViewById(R.id.tv_filter_count);
            tabTitleInvisible.setVisibility(isTabTitleInvisible ? View.VISIBLE : View.GONE);
        }
    }
}

