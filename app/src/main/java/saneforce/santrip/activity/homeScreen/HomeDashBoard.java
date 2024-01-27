package saneforce.santrip.activity.homeScreen;

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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.ApprovalsActivity;
import saneforce.santrip.activity.forms.Forms_activity;
import saneforce.santrip.activity.homeScreen.adapters.Callstatusadapter;
import saneforce.santrip.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.santrip.activity.homeScreen.adapters.CustomViewPager;
import saneforce.santrip.activity.homeScreen.adapters.ViewPagerAdapter;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.santrip.activity.homeScreen.fragment.CallsFragment;
import saneforce.santrip.activity.homeScreen.fragment.OutboxFragment;
import saneforce.santrip.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.santrip.activity.homeScreen.modelClass.CallStatusModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EventCalenderModelClass;
import saneforce.santrip.activity.leave.Leave_Application;
import saneforce.santrip.activity.login.LoginActivity;
import saneforce.santrip.activity.map.MapsActivity;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.activity.myresource.MyResource_Activity;
import saneforce.santrip.activity.presentation.presentation.PresentationActivity;
import saneforce.santrip.activity.previewPresentation.PreviewActivity;
import saneforce.santrip.activity.reports.ReportsActivity;
import saneforce.santrip.activity.tourPlan.TourPlanActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityHomeDashBoardBinding;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;


public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static ActivityHomeDashBoardBinding binding;
    ViewPagerAdapter viewPagerAdapter;
    public static int DeviceWith;
    final ArrayList<CallStatusModelClass> callStatusList = new ArrayList<>();
    public ActionBarDrawerToggle actionBarDrawerToggle;
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    SQLite sqLite;
    LoginResponse loginResponse;

    Callstatusadapter callstatusadapter;
    TabLayoutAdapter leftViewPagerAdapter;
    ArrayList<EventCalenderModelClass> calendarDays = new ArrayList<>();
    private int passwordNotVisible = 1, passwordNotVisible1 = 1;
    private LocalDate selectedDate;
    String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode;
    private static String CalenderFlag = "0";
    DrawerLayout.LayoutParams layoutParams;

    ArrayList<EventCalenderModelClass> callsatuslist = new ArrayList<>();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.v("AAAAAA","PostCreate");
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.v("AAAAAA","PostCreate");
        if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
            binding.myDrawerLayout.closeDrawer(GravityCompat.START);
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

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.rlHead.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceWith = displayMetrics.widthPixels;

        sqLite = new SQLite(HomeDashBoard.this);
        sqLite.getWritableDatabase();

        getRequiredData();
        Log.v("AAAAAA","onCreate");

        binding.rlDateLayoout.setOnClickListener(this);
        binding.viewCalerderLayout.llNextMonth.setOnClickListener(this);
        binding.viewCalerderLayout.llBfrMonth.setOnClickListener(this);
        binding.imgAccount.setOnClickListener(this);
        binding.llReport.setOnClickListener(this);
        binding.imgSync.setOnClickListener(this);
        binding.llPresentation.setOnClickListener(this);
        binding.llSlide.setOnClickListener(this);
        binding.llNav.cancelImg.setOnClickListener(this);

        binding.viewDummy.setVisibility(View.VISIBLE);

        layoutParams = (DrawerLayout.LayoutParams) binding.navView.getLayoutParams();
        layoutParams.width = DeviceWith / 3;
        binding.navView.setLayoutParams(layoutParams);
        if (SfType.equalsIgnoreCase("1")) {
            binding.navView.getMenu().clear();
            binding.navView.inflateMenu(R.menu.activity_navigation_drawer_mr);
        } else {
            binding.navView.getMenu().clear();
            binding.navView.inflateMenu(R.menu.activity_navigation_drawer_drawer);
        }
        binding.navView.setNavigationItemSelectedListener(this);
        setSupportActionBar(binding.Toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.myDrawerLayout, R.string.nav_open, R.string.nav_close);
        binding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();

        binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);

        leftViewPagerAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        leftViewPagerAdapter.add(new WorkPlanFragment(), "Work Plan");
        leftViewPagerAdapter.add(new CallsFragment(), "Calls");
        leftViewPagerAdapter.add(new OutboxFragment(), "Outbox");
        binding.viewPager.setAdapter(leftViewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setOffscreenPageLimit(leftViewPagerAdapter.getCount());

       /* viewPagerAdapter = new ViewPagerAdapter(this, 1);
        binding.viewPager.setAdapter(viewPagerAdapter);
*/
        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        binding.viewPager1.setAdapter(adapter);

        binding.myDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.drMainlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


       /* TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout.tablelayout, binding.viewPager, (tab, position) -> tab.setText(""));
        mediator.attach();
        setupCustomTab(binding.tabLayout.tablelayout, 0, "WorkPlan", false);
        setupCustomTab(binding.tabLayout.tablelayout, 1, "Calls", false);
        setupCustomTab(binding.tabLayout.tablelayout, 2, "Outbox", false);*/


        ViewTreeObserver vto = binding.rlQuickLink.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(() -> {
            int getLayout = binding.rlQuickLink.getMeasuredWidth();
            int width1 = getLayout / 3 - 8;
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(width1, ViewGroup.LayoutParams.MATCH_PARENT);
            param1.setMargins(0, 5, 10, 0);
            binding.llPresentation.setLayoutParams(param1);
            binding.llSlide.setLayoutParams(param1);
            binding.llReport.setLayoutParams(param1);
            binding.llAnalys.setLayoutParams(param1);


        });
  /*      binding.tabLayout.tablelayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });*/
     binding.myDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                binding.myDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                binding.backArrow.setBackgroundResource(R.drawable.cross_img);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
            }
        });

        binding.drMainlayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                binding.drMainlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        });
        binding.backArrow.setOnClickListener(v -> {

            if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
                binding.myDrawerLayout.closeDrawer(GravityCompat.START);
            }else {
                binding.myDrawerLayout.openDrawer(GravityCompat.START);
                binding.backArrow.setBackgroundResource(R.drawable.cross_img);

            }
        });
    }

    private void getRequiredData() {
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();
    }

    private ArrayList<EventCalenderModelClass> daysInMonthArray(LocalDate date) {
        callsatuslist.clear();


        ArrayList<EventCalenderModelClass> daysInMonthArray = new ArrayList<>();
        ArrayList<String> ListID = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy");

        String monthString = date.format(formatter);
        String yearString = date.format(formatter1);


        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();


        for (int i = 0; i < dayOfWeek; i++) {
            daysInMonthArray.add(new EventCalenderModelClass("", "", "", ""));
            ListID.add("");
        }

        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(new EventCalenderModelClass(String.valueOf(i), "", "", ""));
            ListID.add(String.valueOf(i));
        }

        int totalCells = 6 * 7; // 6 rows x 7 columns
        while (daysInMonthArray.size() < totalCells) {
            daysInMonthArray.add(new EventCalenderModelClass("", "", "", ""));
            ListID.add("");
        }


        if (daysInMonthArray.get(6).getDateID().equalsIgnoreCase("")) {
            for (int i = 6; i >= 0; i--) {
                daysInMonthArray.remove(i);
                ListID.remove(i);
            }
        } else if (daysInMonthArray.get(35).getDateID().equalsIgnoreCase("")) {
            for (int i = 41; i >= 35; i--) {
                daysInMonthArray.remove(i);
                ListID.remove(i);
            }
        }


        JSONArray dcrdatas = sqLite.getMasterSyncDataByKey(Constants.DCR);
        if (dcrdatas.length() > 0) {
            for (int i = 0; i < dcrdatas.length(); i++) {
                try {
                    JSONObject jsonObject = dcrdatas.getJSONObject(i);
                    String CustType = jsonObject.optString("CustType");
                    String worktypeFlog = jsonObject.optString("FW_Indicator");
                    String mMonth = jsonObject.optString("Mnth");
                    String mYear = jsonObject.optString("Yr");
                    String date1 = jsonObject.optString("Dcr_dt");

                    if (CustType.equalsIgnoreCase("0") && monthString.equalsIgnoreCase(mMonth) && yearString.equalsIgnoreCase(mYear))
                        callsatuslist.add(new EventCalenderModelClass(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_21, TimeUtils.FORMAT_28, date1), worktypeFlog, mMonth, mYear));

                } catch (JSONException a) {
                    a.printStackTrace();
                }
            }

            for (EventCalenderModelClass list : callsatuslist) {
                int index = ListID.indexOf(list.getDateID());
                if (index != -1) {
                    daysInMonthArray.get(index).setWorktypeFlog(list.getWorktypeFlog());
                    daysInMonthArray.get(index).setMonth(list.getMonth());
                    daysInMonthArray.get(index).setYear(list.getYear());
                }
            }

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
        commonUtilsMethods.FullScreencall();

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        Dialog dialog = new Dialog(this);
        Dialog dialog1 = new Dialog(this);

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
            binding.myDrawerLayout.closeDrawer(GravityCompat.START);
        }


        if (id == R.id.nav_leave_appln) {
            startActivity(new Intent(HomeDashBoard.this, Leave_Application.class));
            return true;
        }

        if (id == R.id.nav_home) {
            if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
                binding.myDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.myDrawerLayout.openDrawer(GravityCompat.START);
                binding.backArrow.setBackgroundResource(R.drawable.cross_img);

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
                if (binding.viewCalerderLayout.getRoot().getVisibility() == View.GONE) {
                    binding.viewDummy.setVisibility(View.GONE);

                    getCallsDataToCalender();
                    selectedDate = LocalDate.now();
                    binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
                    calendarDays.clear();
                    calendarDays = daysInMonthArray(selectedDate);
                    callstatusadapter = new Callstatusadapter(calendarDays, getApplicationContext(), selectedDate);
                    binding.viewCalerderLayout.calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
                    binding.viewCalerderLayout.calendarRecyclerView.setAdapter(callstatusadapter);

                    binding.viewCalerderLayout.getRoot().setVisibility(View.VISIBLE);
                    //   binding.tabLayout.getRoot().setVisibility(View.GONE);
                    binding.tabLayout.setVisibility(View.GONE);
                    binding.viewPager.setVisibility(View.GONE);
                    binding.imgDoubleVecer.setImageDrawable(getDrawable(R.drawable.double_vec));
                } else {
                    binding.viewDummy.setVisibility(View.VISIBLE);
                    binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    //  binding.tabLayout.getRoot().setVisibility(View.VISIBLE);
                    binding.tabLayout.setVisibility(View.VISIBLE);
                    binding.viewPager.setVisibility(View.VISIBLE);
                    binding.imgDoubleVecer.setImageDrawable(getDrawable(R.drawable.arrow_bot_top_img));
                }
                break;

            case R.id.ll_next_month:
                calendarDays.clear();
                selectedDate = selectedDate.plusMonths(1);
                binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
                calendarDays = daysInMonthArray(selectedDate);
                callstatusadapter = new Callstatusadapter(calendarDays, getApplicationContext(), selectedDate);
                callstatusadapter.notifyDataSetChanged();
                binding.viewCalerderLayout.calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
                binding.viewCalerderLayout.calendarRecyclerView.setAdapter(callstatusadapter);
                callstatusadapter.notifyDataSetChanged();

                break;
            case R.id.ll_bfr_month:

                calendarDays.clear();
                selectedDate = selectedDate.minusMonths(1);
                binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
                calendarDays = daysInMonthArray(selectedDate);
                callstatusadapter = new Callstatusadapter(calendarDays, getApplicationContext(), selectedDate);
                binding.viewCalerderLayout.calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
                binding.viewCalerderLayout.calendarRecyclerView.setAdapter(callstatusadapter);
                callstatusadapter.notifyDataSetChanged();
                break;
            case R.id.ll_presentation:
                startActivity(new Intent(HomeDashBoard.this, PresentationActivity.class));
                break;

            case R.id.ll_slide:
                Intent intent = new Intent(HomeDashBoard.this, PreviewActivity.class);
                intent.putExtra("from", "homedDashBoard");
                startActivity(intent);
                break;

            case R.id.ll_report:
                startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class));
                break;
            case R.id.img_sync:
                startActivity(new Intent(HomeDashBoard.this, MasterSyncActivity.class));
                finish();
                break;
            case R.id.img_account:
                showPopup(binding.imgAccount);
            case R.id.cancel_img:
                binding.drMainlayout.closeDrawer(GravityCompat.END);
                break;

        }

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

}

