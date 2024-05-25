package saneforce.sanzen.activity.homeScreen;

import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.SetupOutBoxAdapter;
import static saneforce.sanzen.commonClasses.Constants.APP_MODE;

import static saneforce.sanzen.commonClasses.Constants.CONNECTIVITY_ACTION;

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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
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
import saneforce.sanzen.R;
import saneforce.sanzen.activity.Quiz.QuizActivity;
import saneforce.sanzen.activity.SlideDownloadNew.SlideServices;
import saneforce.sanzen.activity.activityModule.Activity;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.forms.Forms_activity;
import saneforce.sanzen.activity.homeScreen.adapters.Callstatusadapter;
import saneforce.sanzen.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.sanzen.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.sanzen.activity.homeScreen.modelClass.CallStatusModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EventCalenderModelClass;

import saneforce.sanzen.activity.leave.Leave_Application;
import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.activity.map.MapsActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.activity.myresource.MyResource_Activity;
import saneforce.sanzen.activity.presentation.presentation.PresentationActivity;
import saneforce.sanzen.activity.previewPresentation.PreviewActivity;


import saneforce.sanzen.activity.reports.ReportsActivity;
import saneforce.sanzen.activity.reports.dayReport.MapViewActvity;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.MyDayPlanEntriesNeeded;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityHomeDashBoardBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.activity.remaindercalls.RemaindercallsActivity;
import saneforce.sanzen.response.CustomSetupResponse;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesDao;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkChangeReceiver;
import saneforce.sanzen.utility.TimeUtils;

public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static ActivityHomeDashBoardBinding binding;
    public static int DeviceWith;
    public static Dialog dialog;
    public static Dialog dialogCheckInOut, dialogAfterCheckIn, dialogPwdChange;
    public static String CustomPresentationNeed, PresentationNeed, SequentialEntry;
    public static LocalDate selectedDate;
    final ArrayList<CallStatusModelClass> callStatusList = new ArrayList<>();
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog progressDialog;
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    ApiInterface apiInterface;

    CustomSetupResponse customSetupResponse;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    Callstatusadapter callstatusadapter;
    static TabLayoutAdapter leftViewPagerAdapter;
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
    public static CustomPagerAdapter adapter;
    private int passwordNotVisible = 1, passwordNotVisible1 = 1;
    RoomDB roomDB;
   SlidesDao slidesDao;
    static MasterDataDao masterDataDao;
    OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    TourPlanOfflineDataDao tourPlanOfflineDataDao;
    public static boolean tpRangeCheck;

    private static FragmentManager fragmentManager;
    public static boolean canMoveNextDate = true;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ACTIVITY_STATUS", "OnResume");
        checkAndSetEntryDate(this);

        if (tpRangeCheck) {
            CheckedTpRange();
        }

        commonUtilsMethods.setUpLanguage(HomeDashBoard.this);
        if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
            binding.myDrawerLayout.closeDrawer(GravityCompat.START);
        }

//        if (SharedPref.getSrtNd(this).equalsIgnoreCase("0") && !SharedPref.getCheckTodayCheckInOut(this).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
//            SharedPref.setCheckInTime(getApplicationContext(), "");
//            SharedPref.setSkipCheckIn(getApplicationContext(), true);
//            CheckInOutDate();
//        } else {
//            SharedPref.setSkipCheckIn(getApplicationContext(), false);
//        }

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
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, false);
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(HomeDashBoard.this);
        }
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                registerReceiver(receiver, intentFilter, RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(receiver, intentFilter);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("ACTIVITY_STATUS", "OnCreate");
        binding = ActivityHomeDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // THIS CODE IS DESIGN
        DisplayMetrics displayMetrics = new DisplayMetrics();
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceWith = displayMetrics.widthPixels;
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
        fragmentManager = getSupportFragmentManager();


        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();
        slidesDao=roomDB.slidesDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();

        tourPlanOfflineDataDao = roomDB.tourPlanOfflineDataDao();

        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        tpRangeCheck = false;
        CheckedTpRange();

        binding.toolbarTitle.setText(SharedPref.getDivisionName(this));

        binding.imgNotofication.setOnClickListener(view -> {

            startActivity(new Intent(HomeDashBoard.this, MapViewActvity.class));
        });


        binding.imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(HomeDashBoard.this, SlideServices.class);
                startService(serviceIntent);
            }
        });

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

        adapter = new CustomPagerAdapter(getSupportFragmentManager());
        binding.viewPager1.setAdapter(adapter);
        binding.viewPager1.setOffscreenPageLimit(3);
        binding.myDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.drMainlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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


        // THIS CODE IS DEVELOPMENT
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();


        getRequiredData();
        AppIdentify();
        onClickListener();
        if (SharedPref.getSrtNd(this).equalsIgnoreCase("0") && !SharedPref.getCheckTodayCheckInOut(this).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
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
    }

    private static void setupLeftViewPager(Context context, FragmentManager fragmentManager) {
        leftViewPagerAdapter = new TabLayoutAdapter(fragmentManager);
        leftViewPagerAdapter.add(new WorkPlanFragment(), "Work Plan");
        leftViewPagerAdapter.add(new CallsFragment(), "Calls");
        leftViewPagerAdapter.add(new OutboxFragment(), "Outbox");
        binding.viewPager.setAdapter(leftViewPagerAdapter);
        binding.viewPager.setCurrentItem(Integer.parseInt(SharedPref.getSetUpClickedTab(context)));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setOffscreenPageLimit(leftViewPagerAdapter.getCount());
    }

    private void CheckInOutDate() {
        dialogCheckInOut = new Dialog(this);
        dialogCheckInOut.setContentView(R.layout.dialog_daycheckin);
        dialogCheckInOut.setCancelable(false);
        Objects.requireNonNull(dialogCheckInOut.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = dialogCheckInOut.findViewById(R.id.txt_cus_name);
        tvName.setText(String.format("%s%s", getResources().getString(R.string.hi), SharedPref.getSfName(this)));

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
            SharedPref.setCheckInTime(getApplicationContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
            jsonCheck = new JSONObject();
            try {
                jsonCheck.put("tableName", "savetp_attendance");
                jsonCheck.put("sfcode", SharedPref.getSfCode(this));
                jsonCheck.put("division_code", SharedPref.getDivisionCode(this).replaceAll(",", ""));
                jsonCheck.put("lat", latitude);
                jsonCheck.put("long", longitude);
                jsonCheck.put("address", address);
                jsonCheck.put("update", "0");
                jsonCheck.put("Appver", getResources().getString(R.string.app_version));
                jsonCheck.put("Mod", APP_MODE);
                jsonCheck.put("sf_emp_id", SharedPref.getSfEmpId(this));
                jsonCheck.put("sfname", SharedPref.getSfName(this));
                jsonCheck.put("Employee_Id", "");
                jsonCheck.put("Check_In", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                jsonCheck.put("Check_Out", "");
                jsonCheck.put("DateTime", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                Log.v("CheckInOut", "--json--" + jsonCheck.toString());
            } catch (JSONException ignored) {
            }

            if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                progressDialog = CommonUtilsMethods.createProgressDialog(getApplicationContext());
                CallCheckInAPI();
            } else {
                SharedPref.setSkipCheckIn(getApplicationContext(), false);
                SharedPref.setCheckTodayCheckInOut(getApplicationContext(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                offlineCheckInOutDataDao.saveCheckIn(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
                SetupOutBoxAdapter(this, this);
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
                            commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.toast_leave_posted));
                        }
                        progressDialog.dismiss();
                    } catch (Exception ignored) {
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.contact_admin_in));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.toast_response_failed));
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
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CUSTOM_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                customSetupResponse = new CustomSetupResponse();
                Type typeSetup = new TypeToken<CustomSetupResponse>() {
                }.getType();
                customSetupResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
                PresentationNeed = customSetupResponse.getPresentationNeed();
                CustomPresentationNeed = customSetupResponse.getCustomizationPrsNeed();
            }
            if (PresentationNeed != null && PresentationNeed.equalsIgnoreCase("0")) {
                if (CustomPresentationNeed != null && CustomPresentationNeed.equalsIgnoreCase("0")) {
                    binding.llPresentation.setVisibility(View.VISIBLE);
                } else {
                    binding.llPresentation.setVisibility(View.GONE);
                }
                binding.llSlide.setVisibility(View.VISIBLE);
            } else {
                binding.llPresentation.setVisibility(View.GONE);
                binding.llSlide.setVisibility(View.GONE);
            }
            SequentialEntry = SharedPref.getDcrSequential(this);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private void SetUpHolidayWeekEndData() {
        try {
            holidayJSONArray = masterDataDao.getMasterDataTableOrNew(Constants.HOLIDAY).getMasterSyncDataJsonArray(); //Holiday data
            JSONArray weeklyOff = masterDataDao.getMasterDataTableOrNew(Constants.WEEKLY_OFF).getMasterSyncDataJsonArray(); // Weekly Off data
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
                JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.MY_DAY_PLAN).getMasterSyncDataJsonArray();

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
        try {
            JSONArray dcrdatas = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            if (dcrdatas.length() > 0) {
                for (int i = 0; i < dcrdatas.length(); i++) {

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

                }

                JSONArray dateSync = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray();

                if (dateSync.length() > 0) {
                    for (int i = 0; i < dateSync.length(); i++) {

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
                    }
                }

                for (EventCalenderModelClass list : callsatuslist) {
                    int index = ListID.indexOf(list.getDateID());
                    if (index != -1) {
                        daysInMonthArray.get(index).setWorktypeFlog(list.getWorktypeFlog());
                    }
                }
            }

        } catch (JSONException ignored) {
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

        user_logout.setOnClickListener(v -> {
            SharedPref.saveLoginState(HomeDashBoard.this, false);
            startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
            finish();
        });

        user_name.setText(SharedPref.getSfName(this));
        sf_name.setText(SharedPref.getDsName(this));
        Cluster.setText(SharedPref.getHqNameMain(this));

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
        TextView update = dialogPwdChange.findViewById(R.id.update);
        ImageView cls_but = dialogPwdChange.findViewById(R.id.close);
        ProgressBar progressBar = dialogPwdChange.findViewById(R.id.progressBar);


        old_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (str.length() > 0 && str.contains(" ")) {
//                    old_password.setError("Space is not allowed");
                    String val = old_password.getText().toString();
                    val = val.replace(" ", "");
                    old_password.setText("");
                    old_password.setText(val);

                    old_password.setSelection(val.length());
                }
                if (str.length() > 30) {
                    String truncated = str.substring(0, 30);
                    old_password.setText(truncated);
                    old_password.setSelection(truncated.length());
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this,"Maximum password length reached. Please keep it under 30 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
        });
        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (str.length() > 0 && str.contains(" ")) {
//                    old_password.setError("Space is not allowed");
                    String val = new_password.getText().toString();
                    val = val.replace(" ", "");
                    new_password.setText("");
                    new_password.setText(val);
                    new_password.setSelection(val.length());
                }
                if (str.length() > 30) {
                    String truncated = str.substring(0, 30);
                    new_password.setText(truncated);
                    new_password.setSelection(truncated.length());
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this,"Maximum password length reached. Please keep it under 30 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
        });
        remain_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (str.length() > 0 && str.contains(" ")) {
//                    old_password.setError("Space is not allowed");
                    String val = remain_password.getText().toString();
                    val = val.replace(" ", "");
                    remain_password.setText("");
                    remain_password.setText(val);
                    remain_password.setSelection(val.length());
                }
                if (str.length() > 30) {
                    String truncated = str.substring(0, 30);
                    remain_password.setText(truncated);
                    remain_password.setSelection(truncated.length());
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this,"Maximum password length reached. Please keep it under 30 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
        });

        old_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(old_password)});
        new_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(new_password)});
        remain_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(remain_password)});
        String password = SharedPref.getLoginUserPwd(this);
        System.out.println("loginPassword--->"+password);

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
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.enter_old_pwd));
            } else if (new_password.getText().toString().equals("")) {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.enter_new_pwd));
            } else if (remain_password.getText().toString().equals("")) {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.enter_repeat_pwd));
            } else {
                if (!password.equals(old_password.getText().toString())) {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.chk_old_pwd));
                } else if (!new_password.getText().toString().equals(remain_password.getText().toString())) {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.pwd_not_match));
                } else if (new_password.getText().toString().equals(password)) {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.change_new_password));
                } else {
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        CallChangePasswordAPI(old_password.getText().toString(), new_password.getText().toString(), remain_password.getText().toString(),progressBar);
                    } catch (Exception ignored) {
                    }

                }
            }
        });

        cls_but.setOnClickListener(v -> dialogPwdChange.dismiss());

        dialogPwdChange.setCanceledOnTouchOutside(false);
        dialogPwdChange.show();
    }

    private void CallChangePasswordAPI(String oldPwd, String newPwd, String confirmPwd,ProgressBar progressBar) {
        JSONObject jj = new JSONObject();
        try {
            jj.put("tableName", "savechpwd");
            jj.put("sfcode", SharedPref.getSfCode(this));
            jj.put("division_code", SharedPref.getDivisionCode(this));
            jj.put("Rsf", SharedPref.getHqCode(this));
            jj.put("sf_type", SharedPref.getSfType(this));
            jj.put("Designation", SharedPref.getDesig(this));
            jj.put("state_code", SharedPref.getStateCode(this));
            jj.put("subdivision_code", SharedPref.getSubdivisionCode(this));
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
                            SharedPref.saveLoginPwd(getApplicationContext(), confirmPwd);
                            commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.pwd_changed_successfully));
//                            startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
                            commonUtilsMethods.loginNavigation(HomeDashBoard.this,confirmPwd);
                            dialogPwdChange.dismiss();
                        } else {
                            commonUtilsMethods.showToastMessage(HomeDashBoard.this, js.getString("msg"));
                        }
                        progressBar.setVisibility(View.GONE);

                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.something_wrong));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.toast_response_failed));
            }
        });

    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.refresh))) {
            setGpsTrack();
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.presentation))) {
            startActivity(new Intent(HomeDashBoard.this, PresentationActivity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.tour_plan))) {
            Intent intent=new Intent(getApplicationContext(), TourPlanActivity.class);
            intent.putExtra("isFrom","isNav");
            intent.putExtra("Month","3");
            startActivity(intent);

            return true;
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.quiz))) {
            startActivity(new Intent(HomeDashBoard.this, QuizActivity.class));
            return true;
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.activity))) {
            startActivity(new Intent(HomeDashBoard.this, Activity.class));
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
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
            }
            return true;
        }

    /*    if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.reports))) {
            if (UtilityClass.isNetworkAvailable(context)) {
                startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class));
            } else {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
            }
            return true;
        }*/

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.remainder_call))) {
            startActivity(new Intent(HomeDashBoard.this, RemaindercallsActivity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.near_me))) {
            if (SharedPref.getSrtNd(this).equalsIgnoreCase("0")) {
                if (SharedPref.getSkipCheckIn(getApplicationContext())) {
                    goToNearMeActivity();
                } else {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.submit_checkin));
                }
            } else {
                goToNearMeActivity();
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

    private void goToNearMeActivity() {
        if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
            if (SharedPref.getHqCode(HomeDashBoard.this).equalsIgnoreCase("null") || SharedPref.getHqCode(HomeDashBoard.this).isEmpty()) {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.submit_mydayplan));
            } else {
                Intent intent = new Intent(HomeDashBoard.this, MapsActivity.class);
                intent.putExtra("from", "not_tagging");
                MapsActivity.SelectedTab = "D";
                MapsActivity.SelectedHqCode = SharedPref.getHqCode(HomeDashBoard.this);
                MapsActivity.SelectedHqName = SharedPref.getHqName(HomeDashBoard.this);
                startActivity(intent);
            }
        } else {
            commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
        }
    }

    public static void checkAndSetEntryDate(Context context) {
        binding.viewPagerProgress.setVisibility(View.VISIBLE);
        MyDayPlanEntriesNeeded.updateMyDayPlanEntryDates(context, false, new MyDayPlanEntriesNeeded.SyncTaskStatus() {
            @Override
            public void datesFound() {
                if (SequentialEntry != null && SequentialEntry.equalsIgnoreCase("0")) {
                    String dateRequired = SharedPref.getSelectedDateCal(context);
                    String monthDateYear = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_12, dateRequired);
                    selectedDate = LocalDate.parse(dateRequired, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34));
                    binding.textDate.setText(monthDateYear);
                    SharedPref.setCheckDateTodayPlan(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, dateRequired));
                    Log.e("TAG 0", "checkAndSetEntryDate: " + selectedDate);
                } else if (canMoveNextDate) {
                    String dateRequired = SharedPref.getSelectedDateCal(context);
                    String monthDateYear = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_12, dateRequired);
                    selectedDate = LocalDate.parse(dateRequired, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34));
                    binding.textDate.setText(monthDateYear);
                    SharedPref.setCheckDateTodayPlan(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, dateRequired));
                    Log.e("TAG 1", "checkAndSetEntryDate: " + selectedDate);
                } else {
                    canMoveNextDate = true;
                    selectedDate = null;
                    binding.textDate.setText(null);
                    Log.e("TAG 3", "checkAndSetEntryDate: " + selectedDate);
                }
                binding.viewPagerProgress.setVisibility(View.GONE);
                setupLeftViewPager(context, fragmentManager);
            }

            @Override
            public void noDatesFound() {
                selectedDate = null;
                binding.textDate.setText(null);
                binding.viewPagerProgress.setVisibility(View.GONE);
                setupLeftViewPager(context, fragmentManager);
                Log.e("TAG 4", "checkAndSetEntryDate: " + selectedDate);
            }
        });
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_calender_syn:
                callAPIDateSync();
                break;
            case R.id.rl_date_layoout:
                if(SequentialEntry.equalsIgnoreCase("0")) {
                    commonUtilsMethods.showToastMessage(this, getString(R.string.sequential_entry_cannot_change_date));
                } else if(!SharedPref.getDayPlanStartedDate(this).isEmpty() && MyDayPlanEntriesNeeded.datesNeeded.contains(SharedPref.getDayPlanStartedDate(this))) {
                    commonUtilsMethods.showToastMessage(this, getString(R.string.complete_day));
                }
//                else if(HomeDashBoard.selectedDate != null){
//                    commonUtilsMethods.showToastMessage(this, getString(R.string.complete_day));
//                }
                else if(!MyDayPlanEntriesNeeded.datesNeeded.isEmpty() || !SharedPref.getSelectedDateCal(this).isEmpty()) {
                    SetUpHolidayWeekEndData();
                    if (binding.viewCalerderLayout.getRoot().getVisibility() == View.GONE) {
                        getCallsDataToCalender();
                        if (selectedDate == null) {
                            selectedDate = LocalDate.now();
                        }
                        binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
                        calendarDays.clear();
                        calendarDays = daysInMonthArray(selectedDate);
                        callstatusadapter = new Callstatusadapter(calendarDays, HomeDashBoard.this, selectedDate);
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
                } else {
                    commonUtilsMethods.showToastMessage(this, "No pending dates to select");
                }
                break;

            case R.id.ll_next_month:
                calendarDays.clear();
                selectedDate = selectedDate.plusMonths(1);
                binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
                calendarDays = daysInMonthArray(selectedDate);
                callstatusadapter = new Callstatusadapter(calendarDays, HomeDashBoard.this, selectedDate);
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
                callstatusadapter = new Callstatusadapter(calendarDays, HomeDashBoard.this, selectedDate);
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
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
                }
                break;

            case R.id.img_sync:

                Intent intent1 = new Intent(HomeDashBoard.this, MasterSyncActivity.class);
                startActivity(intent1);
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
            jj.put("sfcode", SharedPref.getSfCode(this));
            jj.put("division_code", SharedPref.getDivisionCode(this));
            jj.put("Rsf", SharedPref.getHqCode(this));
            jj.put("sf_type", SharedPref.getSfType(this));
            jj.put("Designation", SharedPref.getDesig(this));
            jj.put("state_code", SharedPref.getStateCode(this));
            jj.put("subdivision_code", SharedPref.getSubdivisionCode(this));
            Log.d("object", jj.toString());
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
                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, jsonArray.toString(), 0));

                        binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                        binding.tabLayout.setVisibility(View.VISIBLE);
                        binding.viewPager.setVisibility(View.VISIBLE);
                        commonUtilsMethods.showToastMessage(HomeDashBoard.this, context.getString(R.string.updated_successfully));
                        progressDialog.dismiss();
                    } catch (Exception ignored) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.toast_response_failed));
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
        JSONArray dcrData = masterDataDao.getMasterDataTableOrNew(Constants.DCR).getMasterSyncDataJsonArray();

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

        Menu menu = binding.navView.getMenu();
        if (SharedPref.getTpNeed(this).equalsIgnoreCase("0"))
            menu.findItem(R.id.tp).setVisible(true);
        else
            menu.findItem(R.id.tp).setVisible(false);

        if (SharedPref.getActivityNd(this).equalsIgnoreCase("0"))
            menu.findItem(R.id.activity).setVisible(true);
        else
            menu.findItem(R.id.activity).setVisible(false);

        if (SharedPref.getGeoChk(this).equalsIgnoreCase("0"))
            menu.findItem(R.id.nearme).setVisible(true);
        else
            menu.findItem(R.id.nearme).setVisible(false);

        if (SharedPref.getSfType(this).equalsIgnoreCase("2"))
            menu.findItem(R.id.approval).setVisible(true);
        else
            menu.findItem(R.id.approval).setVisible(false);

        if (SharedPref.getQuizNeed(this).equalsIgnoreCase("0"))
            menu.findItem(R.id.quiz).setVisible(true);
        else
            menu.findItem(R.id.quiz).setVisible(false);


        if (SharedPref.getSurveyNd(this).equalsIgnoreCase("0"))
            menu.findItem(R.id.survey).setVisible(true);
        else
            menu.findItem(R.id.survey).setVisible(false);


        if (SharedPref.getRmdrNeed(this).equalsIgnoreCase("0"))
            menu.findItem(R.id.remaindercall).setVisible(true);
        else
            menu.findItem(R.id.remaindercall).setVisible(false);

        menu.findItem(R.id.form).setVisible(false);

        if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
            binding.tvLdot.setVisibility(View.VISIBLE);
            binding.imgLocation.setImageResource(R.drawable.location_img);
        } else {
            binding.imgLocation.setImageResource(R.drawable.locationget_img);
        }
        if (SharedPref.getGeotagNeed(this).equalsIgnoreCase("1")) {
            binding.tvDdot.setVisibility(View.VISIBLE);
        }
        if (SharedPref.getGeotagNeedChe(this).equalsIgnoreCase("1")) {
            binding.tvCdot.setVisibility(View.VISIBLE);
        }
        if (SharedPref.getGeotagNeedStock(this).equalsIgnoreCase("1")) {
            binding.tvSdot.setVisibility(View.VISIBLE);
        }
        if (SharedPref.getGeotagNeedUnlst(this).equalsIgnoreCase("1")) {
            binding.tvUdot.setVisibility(View.VISIBLE);
        }
        if (SharedPref.getGeotagNeedCip(this).equalsIgnoreCase("1")) {
            binding.tvHdot.setVisibility(View.VISIBLE);
        }

    }

    public void commonFun() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }


    void CheckedTpRange() {

        if (!SharedPref.getskipDate(HomeDashBoard.this).equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
            if (SharedPref.getTpMandatoryNeed(context).equalsIgnoreCase("0") && SharedPref.getTpNeed(context).equalsIgnoreCase("0") &&
                    !SharedPref.getTpStartDate(context).equalsIgnoreCase("1") && !SharedPref.getTpStartDate(context).equalsIgnoreCase("-1") &&
                    !SharedPref.getTpEndDate(context).equalsIgnoreCase("1") && !SharedPref.getTpEndDate(context).equalsIgnoreCase("-1")) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                SimpleDateFormat date = new SimpleDateFormat("dd", Locale.ENGLISH);
                String mCurrDate = date.format(calendar.getTime());
                String currentDate = sdf.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                String nextMonthDate = sdf.format(calendar.getTime());


                Log.e("currentStatus",tourPlanOfflineDataDao.getApprovalStatusByMonth(currentDate));
                Log.e("NextMonthStatus",tourPlanOfflineDataDao.getApprovalStatusByMonth(nextMonthDate));

                if (!tourPlanOfflineDataDao.getApprovalStatusByMonth(currentDate).equalsIgnoreCase("3")) {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, "Prepare  Tourplan....");
                    SharedPref.setSKIP(HomeDashBoard.this, false);
                    Intent intent = new Intent(getApplicationContext(), TourPlanActivity.class);
                    intent.putExtra("isFrom", "isHome");
                    intent.putExtra("Month", "0");
                    startActivity(intent);


                } else if (!tourPlanOfflineDataDao.getApprovalStatusByMonth(nextMonthDate).equalsIgnoreCase("3")) {
                    String tp_start = SharedPref.getTpStartDate(context);
                    String tp_end = SharedPref.getTpEndDate(context);
                    int Start_Date = Integer.parseInt(tp_start);
                    int End_Date = Integer.parseInt(tp_end);

                    int mCurrentDate = Integer.parseInt(mCurrDate);
                    if (((mCurrentDate >= Start_Date) && (mCurrentDate <= End_Date))) {
                        commonUtilsMethods.showToastMessage(HomeDashBoard.this, "Prepare  Tourplan...");
                        if (End_Date >= mCurrentDate) {
                            SharedPref.setSKIP(HomeDashBoard.this, true);
                        } else {
                            SharedPref.setSKIP(HomeDashBoard.this, false);
                        }

                        Intent intent = new Intent(getApplicationContext(), TourPlanActivity.class);
                        intent.putExtra("isFrom", "isHome");
                        intent.putExtra("Month", "1");
                        startActivity(intent);

                    }
                }
            }


        }
    }

    private void onClickListener() {
        binding.imgLocation.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                setGpsTrack();
            }
        });
    }

    public class DoubleClickListener implements View.OnClickListener {
        private static final long DOUBLE_CLICK_TIME_DELTA = 300;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
            }
            lastClickTime = clickTime;
        }

        public void onDoubleClick(View v) {
        }
    }

    private void setGpsTrack() {
        gpsTrack = new GPSTrack(HomeDashBoard.this);
        double lat = gpsTrack.getLatitude();
        double lng = gpsTrack.getLongitude();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            CommonUtilsMethods.gettingAddress(HomeDashBoard.this, Double.parseDouble(String.valueOf(lat)), Double.parseDouble(String.valueOf(lng)), true);
            binding.myDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            CommonUtilsMethods.RequestGPSPermission(HomeDashBoard.this);
        }
        if (CommonUtilsMethods.isLocationFounded) {
            binding.imgLocation.setImageResource(R.drawable.location_img);
        } else {
            binding.imgLocation.setImageResource(R.drawable.locationget_img);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        slidesDao.Changestatus("0","1");

    }
}

