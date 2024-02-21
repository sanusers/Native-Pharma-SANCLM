package saneforce.santrip.activity.homeScreen;

import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.SetupOutBoxAdapter;
import static saneforce.santrip.commonClasses.Constants.APP_MODE;
import static saneforce.santrip.commonClasses.Constants.APP_VERSION;
import static saneforce.santrip.commonClasses.Constants.CONNECTIVITY_ACTION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.Quiz.QuizActivity;
import saneforce.santrip.activity.approvals.ApprovalsActivity;
import saneforce.santrip.activity.forms.Forms_activity;
import saneforce.santrip.activity.homeScreen.adapters.Callstatusadapter;
import saneforce.santrip.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.santrip.activity.homeScreen.fragment.CallsFragment;
import saneforce.santrip.activity.homeScreen.fragment.OutboxFragment;
import saneforce.santrip.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.santrip.activity.homeScreen.modelClass.CallStatusModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EventCalenderModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.ModelNavDrawer;
import saneforce.santrip.activity.leave.Leave_Application;
import saneforce.santrip.activity.login.LoginActivity;
import saneforce.santrip.activity.map.MapsActivity;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.activity.myresource.MyResource_Activity;
import saneforce.santrip.activity.presentation.presentation.PresentationActivity;
import saneforce.santrip.activity.previewPresentation.PreviewActivity;
import saneforce.santrip.activity.remainderCall.RemainderCallActivity;
import saneforce.santrip.activity.reports.ReportsActivity;
import saneforce.santrip.activity.tourPlan.TourPlanActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityHomeDashBoardBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.CustomSetupResponse;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkChangeReceiver;
import saneforce.santrip.utility.TimeUtils;

public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static ActivityHomeDashBoardBinding binding;
    public static int DeviceWith;
    public static Dialog dialogCheckInOut, dialogAfterCheckIn, dialogPwdChange;
    public static String SfType, SfCode, SfName, DivCode, SfEmpId, EmpId, TodayPlanSfCode, Designation, StateCode, SubDivisionCode, SampleValidation, PresentationNeed, NearMeNeed, QuizNeed, ProfileNeed, ActivityNeed, ReminderCallNeed, InputValidation, SurveyNeed, TpNeed, CheckInOutNeed,DcFencingNeed, ChFencingNeed, StFencingNeed, HosFencingNeed,UnlistFencingNeed;
    public static LocalDate selectedDate;
    final ArrayList<CallStatusModelClass> callStatusList = new ArrayList<>();
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog progressDialog;
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    SQLite sqLite;
    ApiInterface apiInterface;
    LoginResponse loginResponse;
    CustomSetupResponse customSetupResponse;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    Callstatusadapter callstatusadapter;
    TabLayoutAdapter leftViewPagerAdapter;
    ArrayList<EventCalenderModelClass> calendarDays = new ArrayList<>();
    DrawerLayout.LayoutParams layoutParams;
    TextView tvDateTime, tvName, tvDateTimeAfter, tvLat, tvLong, tvAddress, tvHeading;
    Button btnCheckIn, btnClose;
    ImageView imgClose;
    double latitude, longitude;
    String CheckInOutStatus, address;
    JSONObject jsonCheck;
    ArrayList<EventCalenderModelClass> callsatuslist = new ArrayList<>();
    ArrayList<String> weeklyOffDays = new ArrayList<>();
    JSONArray holidayJSONArray = new JSONArray();
    String holidayMode = "", weeklyOffCaption = "";
    ArrayList<ModelNavDrawer> arrayNav = new ArrayList<>();
    private int passwordNotVisible = 1, passwordNotVisible1 = 1;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.v("AAAAAA", "PostCreate");
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        super.onResume();
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        Log.v("AAAAAA", "PostCreate");
        if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
            binding.myDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(HomeDashBoard.this, READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(HomeDashBoard.this, READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_AUDIO, READ_MEDIA_VIDEO}, false);
            }
        } else {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, false);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 5);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, true);
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(HomeDashBoard.this);
        }
       // registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  unregisterReceiver(receiver);
    }

    //To Hide the bottomNavigation When popup
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getWindow().getDecorView().setOnApplyWindowInsetsListener((view, windowInsets) -> {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            return view.onApplyWindowInsets(windowInsets);
        });


        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceWith = displayMetrics.widthPixels;
        sqLite = new SQLite(HomeDashBoard.this);
        sqLite.getWritableDatabase();
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        getRequiredData();
        AppIdentify();
        if (CheckInOutNeed.equalsIgnoreCase("0") && !SharedPref.getCheckTodayCheckInOut(this).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
            SharedPref.setCheckInTime(getApplicationContext(), "");
            SharedPref.setSkipCheckIn(getApplicationContext(), true);
            CheckInOutDate();
        } else {
            SharedPref.setSkipCheckIn(getApplicationContext(), false);
        }

        binding.rlDateLayoout.setOnClickListener(this);
        binding.viewCalerderLayout.rlCalenderSyn.setOnClickListener(this);
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
        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.navView.setLayoutParams(layoutParams);
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
        binding.viewPager.setCurrentItem(Integer.parseInt(SharedPref.getSetUpClickedTab(getApplicationContext())));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setOffscreenPageLimit(leftViewPagerAdapter.getCount());
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SharedPref.setSetUpClickedTab(getApplicationContext(), String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

      /*  viewPagerAdapter = new ViewPagerAdapter(this, 1);
        binding.viewPager.setAdapter(viewPagerAdapter);*/


        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        binding.viewPager1.setAdapter(adapter);

        binding.myDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.drMainlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

  /*      TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout.tablelayout, binding.viewPager, (tab, position) -> tab.setText(""));
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
        binding.myDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                binding.backArrow.setBackgroundResource(R.drawable.cross_img);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

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

            } else {
                binding.myDrawerLayout.openDrawer(GravityCompat.START);
                binding.backArrow.setBackgroundResource(R.drawable.cross_img);
            }
        });
        //   new Handler().postDelayed(this::refreshPendingFunction, 200);
    }

    private void addNavItem() {
        arrayNav.clear();
        arrayNav.add(new ModelNavDrawer(R.drawable.refresh, getString(R.string.refresh)));

        if (TpNeed.equalsIgnoreCase("0"))
            arrayNav.add(new ModelNavDrawer(R.drawable.calendar_clock, getString(R.string.tour_plan)));

        arrayNav.add(new ModelNavDrawer(R.drawable.my_resource, getString(R.string.my_resource)));
        arrayNav.add(new ModelNavDrawer(R.drawable.leave, getString(R.string.leave_application)));
        arrayNav.add(new ModelNavDrawer(R.drawable.report, getString(R.string.reports)));

        if (ActivityNeed.equalsIgnoreCase("0"))
            arrayNav.add(new ModelNavDrawer(R.drawable.activity, getString(R.string.activity)));

        if (NearMeNeed.equalsIgnoreCase("0"))
            arrayNav.add(new ModelNavDrawer(R.drawable.nearme, getString(R.string.near_me)));

        if (SfType.equalsIgnoreCase("2"))
            arrayNav.add(new ModelNavDrawer(R.drawable.leave, getString(R.string.approvals)));

        if (QuizNeed.equalsIgnoreCase("0"))
            arrayNav.add(new ModelNavDrawer(R.drawable.quiz, getString(R.string.quiz)));
        if (SurveyNeed.equalsIgnoreCase("0"))
            arrayNav.add(new ModelNavDrawer(R.drawable.survey, getString(R.string.survey)));
        arrayNav.add(new ModelNavDrawer(R.drawable.form, getString(R.string.forms)));

        if (ReminderCallNeed.equalsIgnoreCase("0"))
            arrayNav.add(new ModelNavDrawer(R.drawable.profiling, getString(R.string.remainder_call)));


        Menu sideMenu = binding.navView.getMenu();
        for (int i = 0; i < arrayNav.size(); i++) {
            ModelNavDrawer navList = arrayNav.get(i);
            sideMenu.add(0, i, i, navList.getText());
            sideMenu.getItem(i).setIcon(navList.getDrawable());
        }
    }

    private void CheckInOutDate() {
        dialogCheckInOut = new Dialog(this);
        dialogCheckInOut.setContentView(R.layout.dialog_daycheckin);
        dialogCheckInOut.setCancelable(false);
        Objects.requireNonNull(dialogCheckInOut.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = dialogCheckInOut.findViewById(R.id.txt_cus_name);
        tvName.setText(String.format("%s%s", getResources().getString(R.string.hi), SfName));

        tvDateTime = dialogCheckInOut.findViewById(R.id.txt_date_time);
        tvDateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));

        btnCheckIn = dialogCheckInOut.findViewById(R.id.btn_checkin);
        imgClose = dialogCheckInOut.findViewById(R.id.img_close);

        imgClose.setOnClickListener(v -> {
            dialogCheckInOut.dismiss();
            SharedPref.setSkipCheckIn(getApplicationContext(), true);
        });

        btnCheckIn.setOnClickListener(v -> {
            gpsTrack = new GPSTrack(this);
            latitude = gpsTrack.getLatitude();
            longitude = gpsTrack.getLongitude();
            if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                address = CommonUtilsMethods.gettingAddress(this, latitude, longitude, false);
            } else {
                address = "No Address Found";
            }
            SharedPref.setCheckInTime(getApplicationContext(), CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
            jsonCheck = new JSONObject();
            try {
                jsonCheck.put("tableName", "savetp_attendance");
                jsonCheck.put("sfcode", SfCode);
                jsonCheck.put("division_code", DivCode);
                jsonCheck.put("lat", latitude);
                jsonCheck.put("long", longitude);
                jsonCheck.put("address", address);
                jsonCheck.put("update", "0");
                jsonCheck.put("Appver", APP_VERSION);
                jsonCheck.put("Mod", APP_MODE);
                jsonCheck.put("sf_emp_id", SfEmpId);
                jsonCheck.put("sfname", SfName);
                jsonCheck.put("Employee_Id", EmpId);
                jsonCheck.put("Check_In", CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
                jsonCheck.put("Check_Out", "");
                jsonCheck.put("DateTime", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd") + " " + CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
                Log.v("CheckInOut", "--json--" + jsonCheck.toString());
            } catch (JSONException ignored) {
            }

            if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                progressDialog = CommonUtilsMethods.createProgressDialog(getApplicationContext());
                CallCheckInAPI();
            } else {
                SharedPref.setSkipCheckIn(getApplicationContext(), false);
                SharedPref.setCheckTodayCheckInOut(getApplicationContext(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                sqLite.saveCheckIn(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
                SetupOutBoxAdapter(this, sqLite, this);
                CallDialogAfterCheckIn();
            }
        });

        dialogCheckInOut.show();
    }

    private void CallCheckInAPI() {
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/activity");
        Call<JsonElement> callCheckInOut = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonCheck.toString());
        callCheckInOut.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("CheckInOut", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            CheckInOutStatus = obj.getString("msg");
                        }

                        if (CheckInOutStatus.equalsIgnoreCase("1")) {
                            CallDialogAfterCheckIn();
                        } else {
                            commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.toast_leave_posted), 100);
                        }
                        progressDialog.dismiss();
                    } catch (Exception ignored) {
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.contact_admin_in), 100);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.toast_response_failed), 100);
            }
        });
    }

    private void CallDialogAfterCheckIn() {
        dialogCheckInOut.dismiss();
        dialogAfterCheckIn = new Dialog(this);
        dialogAfterCheckIn.setContentView(R.layout.dialog_checkindata);
        dialogAfterCheckIn.setCancelable(false);
        Objects.requireNonNull(dialogAfterCheckIn.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnClose = dialogAfterCheckIn.findViewById(R.id.btn_close);
        tvHeading = dialogAfterCheckIn.findViewById(R.id.txt_heading);
        tvDateTimeAfter = dialogAfterCheckIn.findViewById(R.id.txt_date_time);
        tvAddress = dialogAfterCheckIn.findViewById(R.id.txt_address);
        tvLat = dialogAfterCheckIn.findViewById(R.id.txt_lat);
        tvLong = dialogAfterCheckIn.findViewById(R.id.txt_long);

        tvHeading.setText(getResources().getString(R.string.check_in));

        tvDateTimeAfter.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
        tvLat.setText(String.valueOf(latitude));
        tvLong.setText(String.valueOf(longitude));
        tvAddress.setText(address);

        btnClose.setOnClickListener(v -> dialogAfterCheckIn.dismiss());

        dialogAfterCheckIn.show();
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
        SfEmpId = loginResponse.getSf_emp_id();
        EmpId = loginResponse.getEmployee_Id();

        CheckInOutNeed = loginResponse.getSrtNd();
        SampleValidation = loginResponse.getSample_validation();
        InputValidation = loginResponse.getInput_validation();
        TpNeed = loginResponse.getTp_need();
        SurveyNeed = loginResponse.getSurveyNd();
        NearMeNeed = loginResponse.getGeoChk();
        ProfileNeed = loginResponse.getMCLDet();
        QuizNeed = loginResponse.getQuiz_need();
        ReminderCallNeed = loginResponse.getRmdrNeed();
        ActivityNeed = loginResponse.getActivityNd();

        DcFencingNeed=loginResponse.getGeoNeed();
        ChFencingNeed =loginResponse.getGEOTagNeedche();
        StFencingNeed =loginResponse.getGEOTagNeedstock();
        HosFencingNeed =loginResponse.getGeoTagNeedcip();
        UnlistFencingNeed =loginResponse.getGEOTagNeedunlst();

        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(this);

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.CUSTOM_SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                customSetupResponse = new CustomSetupResponse();
                Type typeSetup = new TypeToken<CustomSetupResponse>() {
                }.getType();
                customSetupResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
                PresentationNeed = customSetupResponse.getPresentationNeed();
            }
        } catch (Exception ignored) {
        }

        addNavItem();
        SetUpHolidayWeekEndData();

        try {
            if (PresentationNeed.equalsIgnoreCase("0")) {
                binding.llPresentation.setVisibility(View.VISIBLE);
                binding.llSlide.setVisibility(View.VISIBLE);
            } else {
                binding.llPresentation.setVisibility(View.GONE);
                binding.llSlide.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {

        }
    }

    private void SetUpHolidayWeekEndData() {
        try {
            holidayJSONArray = sqLite.getMasterSyncDataByKey(Constants.HOLIDAY); //Holiday data
            JSONArray weeklyOff = sqLite.getMasterSyncDataByKey(Constants.WEEKLY_OFF); // Weekly Off data
            for (int i = 0; i < weeklyOff.length(); i++) {
                JSONObject jsonObject = weeklyOff.getJSONObject(i);
                holidayMode = jsonObject.getString("Holiday_Mode");
                weeklyOffCaption = jsonObject.getString("WTname");
            }

            String[] holidayModeArray = holidayMode.split(",");
            weeklyOffDays = new ArrayList<>();
            for (String str : holidayModeArray) {
                switch (str) {
                    case "0": {
                        weeklyOffDays.add("Sunday");
                        break;
                    }
                    case "1": {
                        weeklyOffDays.add("Monday");
                        break;
                    }
                    case "2": {
                        weeklyOffDays.add("Tuesday");
                        break;
                    }
                    case "3": {
                        weeklyOffDays.add("Wednesday");
                        break;
                    }
                    case "4": {
                        weeklyOffDays.add("Thursday");
                        break;
                    }
                    case "5": {
                        weeklyOffDays.add("Friday");
                        break;
                    }
                    case "6": {
                        weeklyOffDays.add("Saturday");
                        break;
                    }
                }
            }
        } catch (Exception ignored) {

        }

    }

    private ArrayList<EventCalenderModelClass> daysInMonthArray(LocalDate date) {
        callsatuslist.clear();
        try {
            if (String.valueOf(date.getMonth()).equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance("MMMM"))) {
                JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.MY_DAY_PLAN);
                if (workTypeArray.length() > 0) {
                    JSONObject jsonObject = workTypeArray.getJSONObject(0);
                    String TPDt = jsonObject.getString("TPDt");
                    JSONObject jsonObject1 = new JSONObject(TPDt);
                    String dayPlan_Date = jsonObject1.getString("date");
                    String CurrentDate = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_15);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date1 = sdf.parse(dayPlan_Date);
                    Date date2 = sdf.parse(CurrentDate);
                    if (Objects.requireNonNull(date1).equals(date2)) {
                        String mFwFlg1 = jsonObject.getString("FWFlg");
                        if (!mFwFlg1.equalsIgnoreCase("F")) {
                            callsatuslist.add(new EventCalenderModelClass(CommonUtilsMethods.getCurrentInstance("d"), "N", CommonUtilsMethods.getCurrentInstance("M"), CommonUtilsMethods.getCurrentInstance("yyyy")));
                        } else {
                            callsatuslist.add(new EventCalenderModelClass(CommonUtilsMethods.getCurrentInstance("d"), "F", CommonUtilsMethods.getCurrentInstance("M"), CommonUtilsMethods.getCurrentInstance("yyyy")));
                        }
                    }
                }
            }
        } catch (Exception a) {
            a.printStackTrace();
        }


        try {
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            ArrayList<String> days = new ArrayList<>(daysInMonth(date));
            String monthYear = monthYearFromDate(date);
            String month = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_8, monthYear);
            String year = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);

            ArrayList<String> holidayDateArray = new ArrayList<>();
            for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(date.getMonthValue())))
                    holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
            }

            for (String day : days) {
                if (!day.isEmpty()) {
                    String dateName = day + " " + monthYear;
                    String dayName = formatter.format(new Date(dateName));
                    if (weeklyOffDays.contains(dayName)) // add weekly off object when the day is declared as Weekly Off
                    {
                        callsatuslist.add(new EventCalenderModelClass(day, "W", month, year));
                    }


                    if (holidayDateArray.contains(day)) {
                        callsatuslist.add(new EventCalenderModelClass(day, "H", month, year));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("Calender", "error-----" + e);
        }


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
            daysInMonthArray.add(new EventCalenderModelClass(String.valueOf(i), "", monthString, yearString));
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


        JSONArray dcrdatas = sqLite.getMasterSyncDataByKey(Constants.CALL_SYNC);
        if (dcrdatas.length() > 0) {
            for (int i = 0; i < dcrdatas.length(); i++) {
                try {
                    JSONObject jsonObject = dcrdatas.getJSONObject(i);
                    String CustType = jsonObject.optString("CustType");
                    String worktypeFlog = jsonObject.optString("FW_Indicator");
                    String mMonth = jsonObject.optString("Mnth");
                    String mYear = jsonObject.optString("Yr");
                    String date1 = jsonObject.optString("Dcr_dt");

                    if (CustType.equalsIgnoreCase("0") && monthString.equalsIgnoreCase(mMonth) && yearString.equalsIgnoreCase(mYear)) {
                        //   Log.v("Calender", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_21, TimeUtils.FORMAT_28, date1) + "---" + worktypeFlog + "---" + mMonth + "---" + mYear);
                        callsatuslist.add(new EventCalenderModelClass(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_21, TimeUtils.FORMAT_28, date1), worktypeFlog, mMonth, mYear));
                    }
                } catch (JSONException ignored) {
                }
            }


            JSONArray dateSync = sqLite.getMasterSyncDataByKey(Constants.DATE_SYNC);
            if (dateSync.length() > 0) {
                for (int i = 0; i < dateSync.length(); i++) {
                    try {
                        JSONObject jsonObject = dateSync.getJSONObject(i);
                        String flag = jsonObject.optString("flg");
                        String tbName = jsonObject.optString("tbname");
                        String mMonth1 = jsonObject.getJSONObject("dt").getString("date").substring(5, 7);
                        String mMonth = CommonUtilsMethods.setConvertDate("MM", "M", mMonth1);
                        String mYear = jsonObject.getJSONObject("dt").getString("date").substring(0, 4);
                        String date1 = jsonObject.getJSONObject("dt").getString("date").substring(0, 10);

                        String worktypeFlog = "";
                        if (tbName.equalsIgnoreCase("leave")) {
                            worktypeFlog = "L";
                        } else if (tbName.equalsIgnoreCase("missed")) {
                            worktypeFlog = "M";
                        } else if (tbName.equalsIgnoreCase("dcr") && flag.equalsIgnoreCase("2")) {
                            worktypeFlog = "RE";
                        } else if (tbName.equalsIgnoreCase("dcr") && flag.equalsIgnoreCase("3")) {
                            worktypeFlog = "R";
                        }

                        if (monthString.equalsIgnoreCase(mMonth) && yearString.equalsIgnoreCase(mYear)) {
                            // Log.v("Calender", date1 + " --- " + mMonth + " --- " + mYear);
                            callsatuslist.add(new EventCalenderModelClass(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_21, TimeUtils.FORMAT_28, date1), worktypeFlog, mMonth, mYear));
                        }
                    } catch (JSONException e) {
                        Log.v("Calender", "---" + e);
                    }
                }
            }

            for (EventCalenderModelClass list : callsatuslist) {
                int index = ListID.indexOf(list.getDateID());
                if (index != -1) {
                    daysInMonthArray.get(index).setWorktypeFlog(list.getWorktypeFlog());
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

        dialogPwdChange = new Dialog(this);

        dialogPwdChange.setContentView(R.layout.change_password);
        Window window1 = dialogPwdChange.getWindow();


        if (window1 != null) {
            WindowManager.LayoutParams layoutParams = window1.getAttributes();
            window1.setGravity(Gravity.CENTER);
            window1.setLayout(getResources().getDimensionPixelSize(R.dimen._210sdp), getResources().getDimensionPixelSize(R.dimen._220sdp));
            window1.setAttributes(layoutParams);
        }


        EditText old_password = dialogPwdChange.findViewById(R.id.old_pass);
        ImageView old_view = dialogPwdChange.findViewById(R.id.oldpas_icon);
        ImageView newPass_view = dialogPwdChange.findViewById(R.id.noepass_icon);
        EditText new_password = dialogPwdChange.findViewById(R.id.newpasswrd);
        EditText remain_password = dialogPwdChange.findViewById(R.id.repeatpass);
        LinearLayout update = dialogPwdChange.findViewById(R.id.update);
        ImageView cls_but = dialogPwdChange.findViewById(R.id.close);


        old_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(old_password)});
        new_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(new_password)});
        remain_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(remain_password)});

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
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.enter_old_pwd), 100);
            } else if (new_password.getText().toString().equals("")) {
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.enter_new_pwd), 100);
            } else if (remain_password.getText().toString().equals("")) {
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.enter_repeat_pwd), 100);
            } else {
                if (!password.equals(old_password.getText().toString())) {
                    commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.chk_old_pwd), 100);
                } else if (!new_password.getText().toString().equals(remain_password.getText().toString())) {
                    commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.pwd_not_match), 100);
                } else {
                    try {
                        progressDialog = CommonUtilsMethods.createProgressDialog(getApplicationContext());
                        CallChangePasswordAPI(old_password.getText().toString(), new_password.getText().toString(), remain_password.getText().toString());
                    } catch (Exception ignored) {

                    }

                }
            }

        });


        cls_but.setOnClickListener(v -> dialogPwdChange.dismiss());

        dialogPwdChange.show();

    }

    private void CallChangePasswordAPI(String oldPwd, String newPwd, String confirmPwd) {
        JSONObject jj = new JSONObject();
        try {
            jj.put("tableName", "savechpwd");
            jj.put("sfcode", SfCode);
            jj.put("division_code", DivCode);
            jj.put("Rsf", TodayPlanSfCode);
            jj.put("sf_type", SfType);
            jj.put("Designation", Designation);
            jj.put("state_code", StateCode);
            jj.put("subdivision_code", SubDivisionCode);
            jj.put("txOPW", oldPwd);
            jj.put("txNPW", newPwd);
            jj.put("txCPW", confirmPwd);
            Log.d("PassWord_Change", String.valueOf(jj));
        } catch (Exception ignored) {

        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/masterdata");
        Call<JsonElement> changePassword = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jj.toString());

        changePassword.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject js = new JSONObject(response.body().toString());
                        if (js.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.pwd_changed_successfully), 100);
                            SharedPref.saveLoginPwd(getApplicationContext(), confirmPwd);
                            startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
                            dialogPwdChange.dismiss();
                        } else {
                            commonUtilsMethods.ShowToast(getApplicationContext(), js.getString("msg"), 100);
                        }
                        progressDialog.dismiss();

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.something_wrong), 100);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.toast_response_failed), 100);
            }
        });

    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.v("nav_id", "----" + item.getItemId() + "----" + item.getTitle());
      /*  if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.home))) {
            if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
                binding.myDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.myDrawerLayout.openDrawer(GravityCompat.START);
                binding.backArrow.setBackgroundResource(R.drawable.cross_img);
            }
        }*/

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.refresh))) {
            gpsTrack = new GPSTrack(HomeDashBoard.this);
            double lat = gpsTrack.getLatitude();
            double lng = gpsTrack.getLongitude();
            CommonUtilsMethods.gettingAddress(HomeDashBoard.this, Double.parseDouble(String.valueOf(lat)), Double.parseDouble(String.valueOf(lng)), true);
            binding.myDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.presentation))) {
            startActivity(new Intent(HomeDashBoard.this, PresentationActivity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.tour_plan))) {
            startActivity(new Intent(HomeDashBoard.this, TourPlanActivity.class));
            return true;
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.quiz))) {
            startActivity(new Intent(HomeDashBoard.this, QuizActivity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.my_resource))) {
            startActivity(new Intent(HomeDashBoard.this, MyResource_Activity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.leave_application))) {
            startActivity(new Intent(HomeDashBoard.this, Leave_Application.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.approvals))) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                SharedPref.setApprovalsCounts(HomeDashBoard.this, "false");
                startActivity(new Intent(HomeDashBoard.this, ApprovalsActivity.class));
            } else {
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.no_network), 100);
            }
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.reports))) {
            if (UtilityClass.isNetworkAvailable(context)) {
                startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class));
            } else {
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.no_network), 100);
            }
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.activity))) {

        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.remainder_call))) {
            startActivity(new Intent(HomeDashBoard.this, RemainderCallActivity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.near_me))) {
            if (SharedPref.getSkipCheckIn(getApplicationContext())) {
                if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                    if (SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this).equalsIgnoreCase("null") || SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this).isEmpty()) {
                        commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.submit_mydayplan), 100);
                    } else {
                        Intent intent = new Intent(HomeDashBoard.this, MapsActivity.class);
                        intent.putExtra("from", "not_tagging");
                        MapsActivity.SelectedTab = "D";
                        MapsActivity.SelectedHqCode = SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this);
                        MapsActivity.SelectedHqName = SharedPref.getTodayDayPlanSfName(HomeDashBoard.this);
                        startActivity(intent);
                    }
                } else {
                    commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.no_network), 100);
                }
            } else {
                commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.submit_checkin), 100);
            }
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.survey))) {

        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.forms))) {
            startActivity(new Intent(HomeDashBoard.this, Forms_activity.class));
            return true;
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
            case R.id.rl_calender_syn:
                callAPIDateSync();
                break;
            case R.id.rl_date_layoout:
                if (binding.viewCalerderLayout.getRoot().getVisibility() == View.GONE) {
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
                } else {
                    binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    //  binding.tabLayout.getRoot().setVisibility(View.VISIBLE);
                    binding.tabLayout.setVisibility(View.VISIBLE);
                    binding.viewPager.setVisibility(View.VISIBLE);
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
                if (UtilityClass.isNetworkAvailable(context)) {
                    startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class));
                } else {
                    commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.no_network), 100);
                }
                break;

            case R.id.img_sync:
                startActivity(new Intent(HomeDashBoard.this, MasterSyncActivity.class));
                break;

            case R.id.img_account:
                showPopup(binding.imgAccount);

            case R.id.cancel_img:
                binding.drMainlayout.closeDrawer(GravityCompat.END);
                break;
        }
    }

    private void callAPIDateSync() {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        JSONObject jj = new JSONObject();
        try {
            jj.put("tableName", "getdcrdate");
            jj.put("sfcode", SfCode);
            jj.put("division_code", DivCode);
            jj.put("Rsf", TodayPlanSfCode);
            jj.put("sf_type", SfType);
            jj.put("Designation", Designation);
            jj.put("state_code", StateCode);
            jj.put("subdivision_code", SubDivisionCode);
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "home");
        Call<JsonElement> callSyncDate = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jj.toString());

        callSyncDate.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonElement jsonElement = response.body();
                        assert jsonElement != null;
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        sqLite.saveMasterSyncData(Constants.DATE_SYNC, jsonArray.toString(), 0);
                        binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                        binding.tabLayout.setVisibility(View.VISIBLE);
                        binding.viewPager.setVisibility(View.VISIBLE);
                        commonUtilsMethods.ShowToast(context, context.getString(R.string.updated_successfully), 100);
                        progressDialog.dismiss();
                    } catch (Exception ignored) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_response_failed), 100);
            }
        });
    }


    private ArrayList<String> daysInMonth(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        switch (dayOfWeek) {
            case 1: {
                dayOfWeek = 2;
                break;
            }
            case 2: {
                dayOfWeek = 3;
                break;
            }
            case 3: {
                dayOfWeek = 4;
                break;
            }
            case 4: {
                dayOfWeek = 5;
                break;
            }
            case 5: {
                dayOfWeek = 6;
                break;
            }
            case 6: {
                dayOfWeek = 7;
                break;
            }
            case 7: {
                dayOfWeek = 1;
                break;
            }
        }

        for (int i = 1; i <= 42; i++) {
            if (i < dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                if (i < daysInMonth + dayOfWeek) {
                    daysInMonthArray.add(String.valueOf((i + 1) - dayOfWeek));
                }
            }
        }


        //To eliminate the excess empty dates which comes with the LocalDate library
        if (daysInMonthArray.size() >= 22 && daysInMonthArray.size() <= 28) {
            for (int i = daysInMonthArray.size(); i < 28; i++) {
                daysInMonthArray.add("");
            }
        } else if (daysInMonthArray.size() >= 29 && daysInMonthArray.size() <= 35) {
            for (int i = daysInMonthArray.size(); i < 35; i++) {
                daysInMonthArray.add("");
            }
        } else if (daysInMonthArray.size() >= 36 && daysInMonthArray.size() <= 42) {
            for (int i = daysInMonthArray.size(); i < 42; i++) {
                daysInMonthArray.add("");
            }
        }

        return daysInMonthArray;
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



    public void AppIdentify() {

        if (NearMeNeed.equalsIgnoreCase("0")) {
            binding.tvLdot.setVisibility(View.VISIBLE);
        }
        if (DcFencingNeed.equalsIgnoreCase("1")) {
            binding.tvDdot.setVisibility(View.VISIBLE);
        }
        if (ChFencingNeed.equalsIgnoreCase("1")) {
            binding.tvCdot.setVisibility(View.VISIBLE);
        }
        if (StFencingNeed.equalsIgnoreCase("1")) {
            binding.tvSdot.setVisibility(View.VISIBLE);
        }
        if (UnlistFencingNeed.equalsIgnoreCase("1")) {
            binding.tvUdot.setVisibility(View.VISIBLE);
        }
        if (HosFencingNeed.equalsIgnoreCase("1")) {
            binding.tvHdot.setVisibility(View.VISIBLE);
        }

    }





}

