package saneforce.santrip.activity.homeScreen;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import static saneforce.santrip.commonClasses.Constants.APP_MODE;
import static saneforce.santrip.commonClasses.Constants.APP_VERSION;
import static saneforce.santrip.commonClasses.Constants.CONNECTIVITY_ACTION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.ApprovalsActivity;
import saneforce.santrip.activity.forms.Forms_activity;
import saneforce.santrip.activity.homeScreen.adapters.Callstatusadapter;
import saneforce.santrip.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.santrip.activity.homeScreen.adapters.OutBoxHeaderAdapter;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.adapter.TabLayoutAdapter;
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
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkChangeReceiver;
import saneforce.santrip.utility.NetworkCheckInterface;
import saneforce.santrip.utility.TimeUtils;


public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static ActivityHomeDashBoardBinding binding;
    static NetworkCheckInterface mCheckNetwork;
    public static int DeviceWith;
    final ArrayList<CallStatusModelClass> callStatusList = new ArrayList<>();
    public ActionBarDrawerToggle actionBarDrawerToggle;
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    SQLite sqLite;
    ApiInterface apiInterface;
    LoginResponse loginResponse;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    Callstatusadapter callstatusadapter;
    Dialog dialogCheckInOut, dialogAfterCheckIn;
    TabLayoutAdapter leftViewPagerAdapter;
    ArrayList<EventCalenderModelClass> calendarDays = new ArrayList<>();
    private int passwordNotVisible = 1, passwordNotVisible1 = 1;
    private LocalDate selectedDate;
    public static String SfType, SfCode, SfName, DivCode, SfEmpId, EmpId, TodayPlanSfCode, Designation, StateCode, SubDivisionCode, SampleValidation, InputValidation, CheckInOutNeed;
    private static String CalenderFlag = "0";
    DrawerLayout.LayoutParams layoutParams;
    TextView tvDateTime, tvName, tvDateTimeAfter, tvLat, tvLong, tvAddress, tvHeading;
    Button btnCheckIn, btnClose;
    double latitude, longitude;
    String CheckInOutStatus, address;
    Dialog dialogPwdChange;

    ArrayList<EventCalenderModelClass> callsatuslist = new ArrayList<>();
    public static boolean isFinishCall;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishCall) {
            isFinishCall = false;
            finish();
            startActivity(getIntent());
        }

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
        registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
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
            binding.getRoot().setSystemUiVisibility(
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
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceWith = displayMetrics.widthPixels;

        sqLite = new SQLite(HomeDashBoard.this);
        sqLite.getWritableDatabase();

        getRequiredData();

        if (CheckInOutNeed.equalsIgnoreCase("0") && !SharedPref.getCheckTodayDate(this).equalsIgnoreCase(new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date()))) {
            CheckInOutDate();
        }

        binding.rlDateLayoout.setOnClickListener(this);
        binding.viewCalerderLayout.llNextMonth.setOnClickListener(this);
        binding.viewCalerderLayout.llBfrMonth.setOnClickListener(this);
        binding.imgAccount.setOnClickListener(this);
        binding.llReport.setOnClickListener(this);
        binding.imgSync.setOnClickListener(this);
        binding.llPresentation.setOnClickListener(this);
        binding.llSlide.setOnClickListener(this);
        binding.llNav.cancelImg.setOnClickListener(this);

        if (SfType.equalsIgnoreCase("1")) {
            binding.navView.getMenu().clear();
            binding.navView.inflateMenu(R.menu.activity_navigation_drawer_mr);
        } else {
            binding.navView.getMenu().clear();
            binding.navView.inflateMenu(R.menu.activity_navigation_drawer_drawer);
        }

        layoutParams = (DrawerLayout.LayoutParams) binding.navView.getLayoutParams();
        binding.navView.setLayoutParams(layoutParams);
        binding.navView.setNavigationItemSelectedListener(this);

        layoutParams.width = DeviceWith / 3;
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
        setupCustomTab(binding.tabLayout.tablelayout, 2, "Outbox", true);*/

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

    private void CheckInOutDate() {
        dialogCheckInOut = new Dialog(this);
        dialogCheckInOut.setContentView(R.layout.dialog_daycheckin);
        dialogCheckInOut.setCancelable(false);
        Objects.requireNonNull(dialogCheckInOut.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        tvName = dialogCheckInOut.findViewById(R.id.txt_cus_name);
        tvName.setText(String.format("%s%s", getResources().getString(R.string.hi), SfName));

        tvDateTime = dialogCheckInOut.findViewById(R.id.txt_date_time);
        tvDateTime.setText(CommonUtilsMethods.getCurrentDateWithMonthName());

        btnCheckIn = dialogCheckInOut.findViewById(R.id.btn_checkin);

        btnCheckIn.setOnClickListener(v -> {
            gpsTrack = new GPSTrack(this);
            latitude = gpsTrack.getLatitude();
            longitude = gpsTrack.getLongitude();
            address = CommonUtilsMethods.gettingAddress(this, latitude, longitude, false);
            if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                CallCheckInAPI();
            } else {
            }
        });

        dialogAfterCheckIn.show();
    }

    private void CallCheckInAPI() {
        JSONObject js = new JSONObject();
        try {
            js.put("tableName", "savetp_attendance");
            js.put("sfcode", SfCode);
            js.put("division_code", DivCode);
            js.put("lat", latitude);
            js.put("long", longitude);
            js.put("address", address);
            js.put("update", "0");
            js.put("Appver", APP_VERSION);
            js.put("Mod", APP_MODE);
            js.put("sf_emp_id", SfEmpId);
            js.put("sfname", SfName);
            js.put("Employee_Id", EmpId);
            js.put("DateTime", CommonUtilsMethods.getCurrentInstance() + " " + CommonUtilsMethods.getCurrentTime());
            Log.v("CheckInOut", "--json--" + js.toString());
        } catch (JSONException ignored) {
        }

        Call<JsonArray> callCheckInOut;
        callCheckInOut = apiInterface.saveCheckInOut(js.toString());
        callCheckInOut.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
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
                            SharedPref.setCheckTodayDate(getApplicationContext(), CommonUtilsMethods.getCurrentDate());
                            CallDialogAfterCheckIn();
                        } else {
                            commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.toast_leave_posted), 100);
                        }


                    } catch (Exception ignored) {

                    }
                } else {
                    commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.contact_admin), 100);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.toast_response_failed), 100);
            }
        });
    }

    private void CallDialogAfterCheckIn() {
        dialogAfterCheckIn.dismiss();
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

        tvDateTimeAfter.setText(CommonUtilsMethods.getCurrentDateWithMonthName());
        tvLat.setText(String.valueOf(latitude));
        tvLong.setText(String.valueOf(longitude));
        tvAddress.setText(address);

        btnClose.setOnClickListener(v -> dialogAfterCheckIn.dismiss());

        dialogAfterCheckIn.show();

    }

    /*  public static void NetworkConnectCallHomeDashBoard(String log) {
          if (!TextUtils.isEmpty(log)) {
              if (!log.equalsIgnoreCase("NOT_CONNECT")) {
                  if (mCheckNetwork != null)
                      mCheckNetwork.checkNetwork();
              }
          }
      }

      public static void SendOfflineData(NetworkCheckInterface mCheckNetworkData) {
          mCheckNetwork = mCheckNetworkData;
      }

      private void refreshPendingFunction() {
          HomeDashBoard.SendOfflineData(this::sendingOfflineCalls);
      }

      private void sendingOfflineCalls() {
          Log.v("SendOutboxCall","connected");
          apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

          //Call Data
          ArrayList<OutBoxCallList> outBoxCallLists = sqLite.getOutBoxCallsFullList("Call Failed", "Waiting for Sync");
          CallApiList(outBoxCallLists);
      }

      private void CallApiList(ArrayList<OutBoxCallList> outBoxCallLists) {
          if (outBoxCallLists.size() > 0) {
              isCallAvailable = false;
              for (int i = 0; i < outBoxCallLists.size(); i++) {
                  OutBoxCallList outBoxCallList = outBoxCallLists.get(i);
                  if (outBoxCallList.getSyncCount() <= 4) {
                      isCallAvailable = true;
                      Log.v("SendOutboxCall", "----" + outBoxCallList.getCusName());
                      CallSendAPI(outBoxCallLists, i, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getSyncCount());
                      break;
                  }
              }
          } else {
              isCallAvailable = false;
          }

          if (!isCallAvailable) {
              //Call Event Capture
              CallsFragment.CallTodayCallsAPI(context, apiInterface, sqLite,false);
              ArrayList<EcModelClass> ecModelClasses = sqLite.getEcListFull();
              CallApiLocalEC(ecModelClasses);
          }

      }

      private void CallApiLocalEC(ArrayList<EcModelClass> ecModelClasses) {
          if (ecModelClasses.size() > 0) {
              for (int i = 0; i < ecModelClasses.size(); i++) {
                  EcModelClass ecModelClass = ecModelClasses.get(i);
                  Log.v("SendOutboxCall", "----" + ecModelClass.getDates() + "---" + ecModelClass.getName());
                  CallSendAPIImage(ecModelClasses, i, ecModelClass.getId(), ecModelClass.getJson_values(), ecModelClass.getFilePath());
                  break;
              }
          } else {
              CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, context);
          }
      }


      public HashMap<String, RequestBody> field(String val) {
          HashMap<String, RequestBody> xx = new HashMap<>();
          xx.put("data", createFromString(val));
          return xx;
      }

      private RequestBody createFromString(String txt) {
          return RequestBody.create(txt, MultipartBody.FORM);
      }


      public MultipartBody.Part convertImg(String tag, String path) {
          Log.d("path", tag + "-" + path);
          MultipartBody.Part yy = null;
          try {
              File file;
              if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                  file = new Compressor(context).compressToFile(new File(path));
                  Log.d("path", tag + "-" + path);
              } else {
                  file = new File(path);
              }
              RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
              yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

              Log.d("path", String.valueOf(yy));
          } catch (Exception ignored) {
          }
          return yy;
      }

      private void CallSendAPIImage(ArrayList<EcModelClass> ecModelClasses, int position, String id, String jsonValues, String filePath) {
          MultipartBody.Part img = convertImg("EventImg", filePath);
          HashMap<String, RequestBody> values = field(jsonValues.toString());
          Call<JsonObject> saveImgDcr = apiInterface.saveImgDcr(values, img);

          saveImgDcr.enqueue(new Callback<JsonObject>() {
              @Override
              public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                  if (response.isSuccessful()) {
                      try {
                          assert response.body() != null;
                          JSONObject json = new JSONObject(response.body().toString());
                          Log.v("ImgUpload", json.toString());
                          if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Photo Has Been Updated")) {
                              DeleteCacheFile(ecModelClasses, filePath, id, position);
                          } else {
                              DeleteCacheFile(ecModelClasses, filePath, id, position);
                          }
                      } catch (Exception ignored) {
                          DeleteCacheFile(ecModelClasses, filePath, id, position);
                      }
                  }
              }

              @Override
              public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                  DeleteCacheFile(ecModelClasses, filePath, id, position);
              }
          });
      }

      private void DeleteCacheFile(ArrayList<EcModelClass> ecModelClasses, String filePath, String id, int position) {
          File fileDelete = new File(filePath);
          if (fileDelete.exists()) {
              if (fileDelete.delete()) {
                  System.out.println("file Deleted :" + filePath);
              } else {
                  System.out.println("file not Deleted :" + filePath);
              }
          }
          sqLite.deleteOfflineEC(id);
          ecModelClasses.remove(position);
          CallApiLocalEC(ecModelClasses);
      }


      private void CallSendAPI(ArrayList<OutBoxCallList> outBoxCallList, int position, String dates, String cusName, String cusCode, String jsonData, int syncCount) {
          JSONObject jsonSaveDcr;
          try {
              jsonSaveDcr = new JSONObject(jsonData);
              Call<JsonObject> callSaveDcr;
              callSaveDcr = apiInterface.saveDcr(jsonSaveDcr.toString());
              callSaveDcr.enqueue(new Callback<JsonObject>() {
                  @Override
                  public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                      if (response.isSuccessful()) {
                          try {
                              JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                              if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                                  sqLite.deleteOfflineCalls(cusCode, cusName, dates);
                                  outBoxCallList.remove(position);
                              } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                  sqLite.saveOfflineUpdateStatus(dates, cusCode, String.valueOf(5), "Duplicate Call");
                                  outBoxCallList.set(position, new OutBoxCallList(cusName, cusCode, dates, jsonData, "Duplicate Call", 5));
                                  JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                                  for (int i = 0; i < jsonArray.length(); i++) {
                                      JSONObject jsonObject = jsonArray.getJSONObject(i);
                                      if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(dates) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                                          jsonArray.remove(i);
                                          break;
                                      }
                                  }

                                  sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
                              }

                              CallApiList(outBoxCallList);
                          } catch (Exception e) {
                              outBoxCallList.remove(position);
                              CallApiList(outBoxCallList);
                              Log.v("SendOutboxCall", "---" + e);
                          }
                      }
                  }

                  @SuppressLint("NotifyDataSetChanged")
                  @Override
                  public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                      sqLite.saveOfflineUpdateStatus(dates, cusCode, String.valueOf(syncCount + 1), "Call Failed");
                      outBoxCallList.set(position, new OutBoxCallList(cusName, cusCode, dates, jsonData, "Call Failed", syncCount + 1));
                      CallApiList(outBoxCallList);
                  }
              });

          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
      }
  */
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

        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(this);
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
                commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.enter_old_pwd), 100);
            } else if (new_password.getText().toString().equals("")) {
                commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.enter_new_pwd), 100);
            } else if (remain_password.getText().toString().equals("")) {
                commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.enter_repeat_pwd), 100);
                ;
            } else {
                if (!password.equals(old_password.getText().toString())) {
                    commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.chk_old_pwd), 100);
                } else if (!new_password.getText().toString().equals(remain_password.getText().toString())) {
                    commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.pwd_not_match), 100);
                } else {
                    try {
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

        Call<JsonObject> changePassword = null;
        changePassword = apiInterface.ChangePwd(jj.toString());

        changePassword.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject js = new JSONObject(response.body().toString());
                        if (js.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.pwd_changed_successfully), 100);
                            dialogPwdChange.dismiss();
                        } else {
                            commonUtilsMethods.ShowToast(getApplicationContext(), js.getString("msg"), 100);
                        }

                    } catch (Exception e) {
                        commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.something_wrong), 100);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.toast_response_failed), 100);
            }
        });

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
                    commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.submit_mydayplan), 100);
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
                commonUtilsMethods.ShowToast(getApplicationContext(), getApplicationContext().getString(R.string.no_network), 100);
            }
        }

        return true;
    }

    private void setupCustomTab(TabLayout tabLayout, int tabIndex, String tabTitleText,
                                boolean isTabTitleInvisible) {
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
                startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class));
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


    private ArrayList<CallStatusModelClass> getCallsDataToCalender1
            (ArrayList<String> dateList, ArrayList<CallStatusModelClass> workTypeList, String
                    month, String year) {

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

