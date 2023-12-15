package saneforce.sanclm.activity.homeScreen;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.activity.forms.Forms_activity;
import saneforce.sanclm.activity.homeScreen.adapters.Callstatusadapter;
import saneforce.sanclm.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.sanclm.activity.homeScreen.adapters.CustomViewPager;
import saneforce.sanclm.activity.homeScreen.adapters.ViewPagerAdapter;
import saneforce.sanclm.activity.homeScreen.modelClass.CallStatusModelClass;
import saneforce.sanclm.activity.leave.Leave_Application;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.masterSync.MasterSyncActivity;
import saneforce.sanclm.activity.myresource.MyResource_Activity;
import saneforce.sanclm.activity.presentation.presentation.PresentationActivity;
import saneforce.sanclm.activity.reports.ReportsActivity;
import saneforce.sanclm.activity.tourPlan.TourPlanActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.GPSTrack;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static ViewPager2 viewPager;
    public static CustomViewPager viewPager1;
    @SuppressLint("StaticFieldLeak")
    public static ListView wk_listview;
    public static RecyclerView wk_recycler_view;
    public static int DeviceWith;
    public static DrawerLayout drawerLayout;
    @SuppressLint("StaticFieldLeak")
    public static TextView text_date, txt_wt_plan, txt_cl_done;
    @SuppressLint("StaticFieldLeak")
    public static EditText et_search;
    @SuppressLint("StaticFieldLeak")
    public static View ll_tab_layout, view_calender_layout;
    final ArrayList<CallStatusModelClass> callStatusList = new ArrayList<>();
    final ArrayList<CallStatusModelClass> callStatusList1 = new ArrayList<>();
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView imageView;
    TabLayout tabLayout;
    GPSTrack gpsTrack;
    ViewPagerAdapter viewPagerAdapter;
    NavigationView navigationView;
    LinearLayout pre_layout, slide_layout, report_layout, anLast_layout, ll_next_month, ll_bfr_month;
    ImageView masterSync, img_account, img_notification;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    SQLite sqLite;
    LoginResponse loginResponse;
    Dialog dialog, dialog1;
    LinearLayout user_view;
    RecyclerView calendarRecyclerView;
    RelativeLayout rl_quick_link, rl_date_layout;
    TextView monthYearText;
    Callstatusadapter callstatusadapter;
    ArrayList<String> calendarDays = new ArrayList<>();
    private int passwordNotVisible = 1, passwordNotVisible1 = 1;
    private LocalDate selectedDate;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            imageView.setBackgroundResource(R.drawable.bars_sort_img);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, true);
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(HomeDashBoard.this);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);
        sqLite = new SQLite(HomeDashBoard.this);
        sqLite.getWritableDatabase();
        dialog = new Dialog(this);
        dialog1 = new Dialog(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initWidgets();
        getCallsDataToCalender();
        rl_date_layout.setOnClickListener(this);
        ll_next_month.setOnClickListener(this);
        ll_bfr_month.setOnClickListener(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceWith = displayMetrics.widthPixels;

        Log.e("test", "fcm token : " + SharedPref.getFcmToken(HomeDashBoard.this));

        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.width = DeviceWith / 3;// You can replace R.dimen.navigation_drawer_width with the width you want
        navigationView.setLayoutParams(layoutParams);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        imageView.setBackgroundResource(R.drawable.bars_sort_img);

        viewPagerAdapter = new ViewPagerAdapter(this, 1);
        viewPager.setAdapter(viewPagerAdapter);
        // viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);

        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager1.setAdapter(adapter);
        // viewPager1.setOffscreenPageLimit(0);

        int width = (((DeviceWith / 3) * 2) / 3) - 30;
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        param.setMargins(0, 5, 10, 0);

        pre_layout.setLayoutParams(param);
        slide_layout.setLayoutParams(param);
        report_layout.setLayoutParams(param);
        anLast_layout.setLayoutParams(param);

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(""));
        mediator.attach();
        setupCustomTab(tabLayout, 0, "WorkPlan", false);
        setupCustomTab(tabLayout, 1, "Calls", false);
        setupCustomTab(tabLayout, 2, "Outbox", true);


        // Listener
        ViewTreeObserver vto = rl_quick_link.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(() -> {

            int getLayout = rl_quick_link.getMeasuredWidth();
            int width1 = getLayout / 3 - 8;
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(width1, ViewGroup.LayoutParams.MATCH_PARENT);
            param1.setMargins(0, 5, 10, 0);
            pre_layout.setLayoutParams(param1);
            slide_layout.setLayoutParams(param1);
            report_layout.setLayoutParams(param1);
            anLast_layout.setLayoutParams(param1);


        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tablayname);
                tabTitle.setTextColor(ContextCompat.getColor(context, R.color.text_dark));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tablayname);
                tabTitle.setTextColor(ContextCompat.getColor(context, R.color.text_dark_65));
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

        imageView.setOnClickListener(v -> {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                imageView.setBackgroundResource(R.drawable.bars_sort_img);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
                imageView.setBackgroundResource(R.drawable.cross_img);

            }
        });

        pre_layout.setOnClickListener(v -> startActivity(new Intent(HomeDashBoard.this, PresentationActivity.class)));
        report_layout.setOnClickListener(view -> startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class)));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        masterSync.setOnClickListener(v -> startActivity(new Intent(HomeDashBoard.this, MasterSyncActivity.class)));
//        drawerLayout.closeDrawer(Gravity.END);

        img_account.setOnClickListener(v -> showPopup(img_account));

        /// Call status

        selectedDate = LocalDate.now();
        monthYearText.setText(monthYearFromDate(selectedDate));
        calendarDays.clear();
        calendarDays = daysInMonthArray(selectedDate);

        callstatusadapter = new Callstatusadapter(calendarDays, getApplicationContext(), selectedDate);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        calendarRecyclerView.setAdapter(callstatusadapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 0; i < dayOfWeek; i++) {
            daysInMonthArray.add("");
        }

        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(String.valueOf(i));
        }

        int totalCells = 6 * 7; // 6 rows x 7 columns
        while (daysInMonthArray.size() < totalCells) {
            daysInMonthArray.add("");
        }
        if (daysInMonthArray.get(6).equalsIgnoreCase("")) {
            daysInMonthArray.subList(0, 7).clear();
        } else if (daysInMonthArray.get(35).equalsIgnoreCase("")) {
            daysInMonthArray.subList(35, 42).clear();
        }

        return daysInMonthArray;
    }

    @SuppressLint("WrongConstant")
    public void showPopup(ImageView viewed_img) {

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.user_details, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        popupWindow.showAtLocation(viewed_img, Gravity.END, 68, -148);
        int y = -(getResources().getDimensionPixelSize(R.dimen._64sdp));
        int x = (getResources().getDimensionPixelSize(R.dimen._20sdp));
        popupWindow.showAtLocation(viewed_img, Gravity.END, x, y);

        TextView user_name = popupView.findViewById(R.id.user_name);
        TextView sf_name = popupView.findViewById(R.id.sf_name);
        TextView Cluster = popupView.findViewById(R.id.clut);
        LinearLayout l_click = popupView.findViewById(R.id.change_passwrd);
        LinearLayout user_logout = popupView.findViewById(R.id.user_logout);

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        user_logout.setOnClickListener(v -> {
            SharedPref.saveLoginState(HomeDashBoard.this, false);
            startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
        });

        String username = loginResponse.getSF_Name();
        String user_roll = loginResponse.getDS_name();
        String hq_name = loginResponse.getHQName();
        user_name.setText(username);
        sf_name.setText(user_roll);
        Cluster.setText(hq_name);

        l_click.setOnClickListener(v -> {
            popupWindow.dismiss();
            changePassword();
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

    }


    @SuppressLint({"MissingInflatedId", "WrongConstant", "UseCompatLoadingForDrawables"})
    public void changePassword() {

        //  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreenCall();

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        dialog1.setContentView(R.layout.change_password);
        Window window1 = dialog1.getWindow();

        if (window1 != null) {
            WindowManager.LayoutParams layoutParams = window1.getAttributes();
            window1.setGravity(Gravity.CENTER);
            window1.setLayout(getResources().getDimensionPixelSize(R.dimen._210sdp), getResources().getDimensionPixelSize(R.dimen._220sdp));
            window1.setAttributes(layoutParams);
        }


        EditText old_password = dialog1.findViewById(R.id.old_pass);
        ImageView old_view = dialog1.findViewById(R.id.oldpas_icon);
        ImageView newPass_view = dialog1.findViewById(R.id.noepass_icon);
        EditText new_password = dialog1.findViewById(R.id.newpasswrd);
        EditText remain_password = dialog1.findViewById(R.id.repeatpass);
        LinearLayout update = dialog1.findViewById(R.id.update);
        ImageView cls_but = dialog1.findViewById(R.id.close);

        String password = loginResponse.getSF_Password();
        old_view.setOnClickListener(v -> {

            if (!old_password.getText().toString().equals("")) {

                if (passwordNotVisible == 1) {
                    old_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    old_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eye_hide));
                    passwordNotVisible = 0;
                } else {
                    old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    old_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eye_visible));
                    passwordNotVisible = 1;
                }
                old_password.setSelection(old_password.length());
            }
        });


        newPass_view.setOnClickListener(v -> {
            if (!new_password.getText().toString().equals("")) {
                if (passwordNotVisible1 == 1) {
                    new_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    remain_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    newPass_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eye_hide));
                    passwordNotVisible1 = 0;
                } else {
                    new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    remain_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPass_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eye_visible));
                    passwordNotVisible1 = 1;
                }
                new_password.setSelection(new_password.length());
            }
        });


        update.setOnClickListener(v -> {

            if (old_password.getText().toString().equals("")) {
                Toast.makeText(HomeDashBoard.this, "Please submit old password", Toast.LENGTH_SHORT).show();
            } else if (new_password.getText().toString().equals("")) {
                Toast.makeText(HomeDashBoard.this, "Please submit new password", Toast.LENGTH_SHORT).show();
            } else if (remain_password.getText().toString().equals("")) {
                Toast.makeText(HomeDashBoard.this, "Please submit repeat password", Toast.LENGTH_SHORT).show();
            } else {
                if (!password.equals(old_password.getText().toString())) {
                    Toast.makeText(HomeDashBoard.this, "Please Check Old Password", Toast.LENGTH_SHORT).show();
                } else if (!new_password.getText().toString().equals(remain_password.getText().toString())) {
                    Toast.makeText(HomeDashBoard.this, "Please Check not match Password", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("Oldpassword", old_password.getText().toString());
                        jsonObj.put("Newpassword", new_password.getText().toString());
                        Log.d("PassWord_Change", String.valueOf(jsonObj));
                    } catch (Exception ignored) {

                    }

                    Toast.makeText(HomeDashBoard.this, "Password changed", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }
            }

        });


        cls_but.setOnClickListener(v -> dialog1.dismiss());

        dialog1.show();

    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_refresh) {
            gpsTrack = new GPSTrack(HomeDashBoard.this);
            double lat = gpsTrack.getLatitude();
            double lng = gpsTrack.getLongitude();
            CommonUtilsMethods.gettingAddress(HomeDashBoard.this, Double.parseDouble(String.valueOf(lat)), Double.parseDouble(String.valueOf(lng)), true);
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        if (id == R.id.nav_leave_appln) {
            startActivity(new Intent(HomeDashBoard.this, Leave_Application.class));
            return true;
        }

        if (id == R.id.nav_home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                imageView.setBackgroundResource(R.drawable.bars_sort_img);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
                imageView.setBackgroundResource(R.drawable.cross_img);
            }
        }
        if (id == R.id.nav_tour_plan) {
            startActivity(new Intent(HomeDashBoard.this, TourPlanActivity.class));
            return true;
        }
        if (id == R.id.nav_myresource) {
            startActivity(new Intent(HomeDashBoard.this, MyResource_Activity.class));
            return true;
        }

        if (id == R.id.nav_approvals) {
            SharedPref.setApprovalsCounts(HomeDashBoard.this, "false");
            startActivity(new Intent(HomeDashBoard.this, ApprovalsActivity.class));
            return true;
        }

        if (id == R.id.nav_forms) {
            startActivity(new Intent(HomeDashBoard.this, Forms_activity.class));
            return true;
        }

        if (id == R.id.nav_presentation) {
            startActivity(new Intent(HomeDashBoard.this, PresentationActivity.class));
            return true;
        }

        if (id == R.id.nav_reports) {
            startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class));
            return true;
        }

        if (id == R.id.nav_nearme) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                if (SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this).equalsIgnoreCase("null") || SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this).isEmpty()) {
                    Toast.makeText(HomeDashBoard.this, "Kindly Submit MyDayPlan", Toast.LENGTH_SHORT).show();
                    //   MapsActivity.SelectedHqCode = "";
                    //   MapsActivity.SelectedHqName = "";
                } else {
                    Intent intent = new Intent(HomeDashBoard.this, MapsActivity.class);
                    intent.putExtra("from", "not_tagging");
                    MapsActivity.SelectedTab = "D";
                    MapsActivity.SelectedHqCode = SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this);
                    MapsActivity.SelectedHqName = SharedPref.getTodayDayPlanSfName(HomeDashBoard.this);
                    startActivity(intent);
                }
                return true;
            } else {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    private void setupCustomTab(TabLayout tabLayout, int tabIndex, String tabTitleText, boolean isTabTitleInvisible) {
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) {
            @SuppressLint("InflateParams") View customView = LayoutInflater.from(this).inflate(R.layout.customtab_item, null);
            tab.setCustomView(customView);
            TextView tabTitle = customView.findViewById(R.id.tablayname);
            if (tabIndex == 0) {
                tabTitle.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
            }

            tabTitle.setText(tabTitleText);
            TextView tabTitleInvisible = customView.findViewById(R.id.tv_filter_count);
            tabTitleInvisible.setVisibility(isTabTitleInvisible ? View.VISIBLE : View.GONE);
        }
    }


    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public void onClick(View v) {


        switch (v.getId()) {


            case R.id.rl_date_layoout:

                if (view_calender_layout.getVisibility() == View.GONE) {
                    view_calender_layout.setVisibility(View.VISIBLE);
                    ll_tab_layout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                } else {
                    view_calender_layout.setVisibility(View.GONE);
                    ll_tab_layout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.ll_next_month:
                callStatusList1.clear();
                calendarDays.clear();
                selectedDate = selectedDate.plusMonths(1);
                monthYearText.setText(monthYearFromDate(selectedDate));
                calendarDays = daysInMonthArray(selectedDate);
                callstatusadapter = new Callstatusadapter(calendarDays, getApplicationContext(), selectedDate);
                calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
                calendarRecyclerView.setAdapter(callstatusadapter);
                callstatusadapter.notifyDataSetChanged();

                break;
            case R.id.ll_bfr_month:


                calendarDays.clear();
                selectedDate = selectedDate.minusMonths(1);
                monthYearText.setText(monthYearFromDate(selectedDate));
                calendarDays = daysInMonthArray(selectedDate);
                callstatusadapter = new Callstatusadapter(calendarDays, getApplicationContext(), selectedDate);
                calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
                calendarRecyclerView.setAdapter(callstatusadapter);
                callstatusadapter.notifyDataSetChanged();
                break;


        }

    }


    private void initWidgets() {

        pre_layout = findViewById(R.id.ll_presentation);
        slide_layout = findViewById(R.id.ll_slide);
        report_layout = findViewById(R.id.ll_report);
        anLast_layout = findViewById(R.id.ll_analys);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        imageView = findViewById(R.id.back_arrow);
        viewPager = findViewById(R.id.view_pager);
        viewPager1 = findViewById(R.id.view_pager1);
        tabLayout = findViewById(R.id.tablelayout);
        navigationView = findViewById(R.id.nav_view);
        masterSync = findViewById(R.id.img_sync);
        img_account = findViewById(R.id.img_account);
        img_notification = findViewById(R.id.img_notofication);
        user_view = findViewById(R.id.user_view);
        rl_quick_link = findViewById(R.id.rl_quick_link);
        rl_date_layout = findViewById(R.id.rl_date_layoout);
        view_calender_layout = findViewById(R.id.view_calerder_layout);
        ll_tab_layout = findViewById(R.id.tab_layout);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

        ll_next_month = findViewById(R.id.ll_next_month);
        ll_bfr_month = findViewById(R.id.ll_bfr_month);
        text_date = findViewById(R.id.text_date);

        et_search = findViewById(R.id.et_search);
        txt_wt_plan = findViewById(R.id.tv_searchheader);
        wk_listview = findViewById(R.id.wk_list_view);
        txt_cl_done = findViewById(R.id.txt_cl_done);
        wk_recycler_view = findViewById(R.id.wk_recyeler_view);

    }


    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }


    private void getCallsDataToCalender() {
        callStatusList.clear();
        JSONArray dcrData = sqLite.getMasterSyncDataByKey("DCR");
        if (dcrData.length() > 0) {
            for (int i = 0; i < dcrData.length(); i++) {
                try {
                    JSONObject jsonObject = dcrData.getJSONObject(i);
                    String CustType = jsonObject.optString("CustType");
                    String workType = jsonObject.optString("FW_Indicator");
                    String month = jsonObject.optString("Mnth");
                    String year = jsonObject.optString("Yr");
                    String date = jsonObject.optString("Dcr_dt");

                    if (CustType.equalsIgnoreCase("0"))
                        callStatusList.add(new CallStatusModelClass(month, year, date, workType));

                } catch (JSONException a) {
                    a.printStackTrace();
                }
            }
        }
    }


    private ArrayList<CallStatusModelClass> getCallsDataToCalender1(ArrayList<String> dateList, ArrayList<CallStatusModelClass> workTypeList, String month, String year) {

        ArrayList<CallStatusModelClass> nCallList = new ArrayList<>();
        ArrayList<CallStatusModelClass> mCallList = new ArrayList<>();

        for (CallStatusModelClass list : workTypeList) {
            if (list.getMonth().equalsIgnoreCase(month) && list.getYear().equalsIgnoreCase(year)) {
                mCallList.add(new CallStatusModelClass(list.getMonth(), list.getYear(), list.getDate(), list.getWorktype()));
            }
        }

        for (String list1 : dateList) {
            if (list1.length() > 0) {

                for (CallStatusModelClass workTypeList123 : mCallList) {
                    if (list1.equalsIgnoreCase(workTypeList123.getDate())) {
                        nCallList.add(new CallStatusModelClass("", "", list1, workTypeList123.getWorktype()));
                    } else {
                        nCallList.add(new CallStatusModelClass("", "", list1, ""));
                    }
                }
            } else {
                nCallList.add(new CallStatusModelClass("", "", list1, ""));
            }
        }

        return nCallList;
    }


}

