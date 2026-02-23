package saneforce.sanzen.activity.homeScreen;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static saneforce.sanzen.commonClasses.Constants.CONNECTIVITY_ACTION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.FAQ.FAQ;
import saneforce.sanzen.activity.Quiz.QuizActivity;
import saneforce.sanzen.activity.ViewModel.LeaveViewModel;
import saneforce.sanzen.activity.activityModule.DynamicActivity;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.sanzen.activity.forms.Forms_activity;
import saneforce.sanzen.activity.homeScreen.adapters.Callstatusadapter;
import saneforce.sanzen.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.sanzen.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.sanzen.activity.homeScreen.modelClass.CallStatusModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EventCalenderModelClass;
import saneforce.sanzen.activity.homeScreen.notification.NotificationViewModel;
import saneforce.sanzen.activity.homeScreen.notification.NotificationsAdapter;
import saneforce.sanzen.activity.leave.Leave_Application;
import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.activity.map.MapsActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.activity.myresource.MyResource_Activity;
import saneforce.sanzen.activity.myresource.ProfilingActivity;
import saneforce.sanzen.activity.myresource.profile.ProfileViewScreen;
import saneforce.sanzen.activity.presentation.presentation.PresentationActivity;
import saneforce.sanzen.activity.previewPresentation.PreviewActivity;
import saneforce.sanzen.activity.remaindercalls.RemaindercallsActivity;
import saneforce.sanzen.activity.reports.DynamicMenuHome;
import saneforce.sanzen.activity.reports.ReportsActivity;
import saneforce.sanzen.activity.reports.ReportsAdapter;
import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.StandardTourPlanActivity;
import saneforce.sanzen.activity.survey.SurveyActivity;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.application.AppActivityTracker;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.ContinuousLogCollector;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.InAppUpdate;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.commonClasses.WorkPlanEntriesNeeded;
import saneforce.sanzen.databinding.ActivityHomeDashBoardBinding;
import saneforce.sanzen.databinding.DialogTimezoneBinding;
import saneforce.sanzen.databinding.HomeNavigationFooterBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OutboxUtil;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesDao;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.sanzen.services.NotificationDialog;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkChangeReceiver;
import saneforce.sanzen.utility.NetworkUtil;
import saneforce.sanzen.utility.TimeUtils;

public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    @SuppressLint("StaticFieldLeak")
    public static ActivityHomeDashBoardBinding binding;
    public static HomeDashBoard homeDashBoardActivity;
    public static int DeviceWith;
    public static Dialog dialog;
    public static Dialog dialogCheckInOut, dialogAfterCheckIn, dialogPwdChange;
    public static String CustomPresentationNeed, PresentationNeed, SequentialEntry, CheckInOutNeed;
    public static LocalDate selectedDate;
    public static String workingDate = "";
    final ArrayList<CallStatusModelClass> callStatusList = new ArrayList<>();
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog progressDialog;
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    ApiInterface apiInterface;
    public static boolean isDcrFrom = false;
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

    public static String TourplanFlog = "", SFDCR_Date_sp = "", SFDCR_Date = "";
    AlertDialog customDialog;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Handler handler1 = new Handler();
    long delay = 1000;
    Runnable runnable;
    private static boolean isDateSelectionClicked = false;
    private LeaveViewModel leaveViewModel;
    public String isFrom = "";
    public static int JoiningDate, JoiningMonth, JoiningYear;
    private InAppUpdate inAppUpdate;
    private static OutboxUtil outboxUtil;
    private static HomeDashBoard activity;
    public static boolean isFakeLocationDetected = false;
    private static final int NOTIFICATION_PERMISSION_CODE = 102;
    private NotificationViewModel notificationViewModel;
    private PopupWindow notificationPopupWindow;
    private final Set<Integer> syncingIds = new HashSet<>();
    private HomeNavigationFooterBinding navigationFooterBinding;
    private MediaController mediaController;
    private String videoUrl = "";
    YouTubePlayer youTubePlayer;
    boolean isPlaying = true;
    private String previousDate = "";
    float dX, dY;
    int lastAction;
    String dynamicLinKNeed = "0";
    ArrayList<MenuModel> menuList = new ArrayList<>();
    ReportsAdapter reportsAdapter;
    private final Handler handler = new Handler();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtils.FORMAT_4, Locale.ENGLISH);
    private boolean isLocationPermissionRequested = false;
    private android.app.AlertDialog locationDialog;
    private ConnectivityManager.NetworkCallback networkCallback;
    int position;
    private final Runnable updateClock = new Runnable() {
        @Override
        public void run() {
            String currentTime = sdf.format(new Date()).toUpperCase();
//            binding.clock.setText(currentTime);
            handler.postDelayed(this, 1000);
            try {
//                String checkInData = SharedPref.getDayCheckInData(HomeDashBoard.this);
//                if(SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && !checkInData.isEmpty() && HomeDashBoard.selectedDate != null) {
//                    JSONObject checkInObj = new JSONObject(checkInData);
////                    SharedPref.setCheckInSkipDate(requireContext(), "");
//                    String currentDate = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5),
//                            previousDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_5, (LocalDate.now().minusDays(1)).toString()),
//                            homeDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_5, HomeDashBoard.selectedDate.toString()),
//                            checkInDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, checkInObj.optString("DateTime"));
//                    if(checkInDate.equalsIgnoreCase(previousDate) && homeDate.equalsIgnoreCase(previousDate) && !SharedPref.getCheckInSkipDate(requireContext()).equalsIgnoreCase(currentDate) && !SharedPref.getCheckTodayCheckInOut(requireContext()).isEmpty()) {
//                        SharedPref.setCheckInSkipDate(requireContext(), currentDate);
//                        Log.d("Clock", "run: log out");
//                        SharedPref.saveLoginState(requireContext(), false);
//                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        requireActivity().finishAffinity();
//                    }
//                }
                String currentDate = dateFormat.format(new Date());
                try {
                    if (!previousDate.equals(currentDate)) {
                        if (masterDataDao != null) {
                            JSONArray workPlanArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();
                            if (workPlanArray.toString().equals("[]")) {
                                checkAndSetEntryDate(HomeDashBoard.this, true);
                                if (HomeDashBoard.homeDashBoardActivity != null && !HomeDashBoard.homeDashBoardActivity.isFinishing() && !HomeDashBoard.homeDashBoardActivity.isDestroyed()) {
                                    HomeDashBoard.homeDashBoardActivity.setUpCalendar();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                previousDate = currentDate;

                String remainderTime = SharedPref.getDoctorRemainingShownDate(HomeDashBoard.this);
                String time = TimeUtils.getCurrentDateTimeTp(TimeUtils.FORMAT_29);

                int currentTimeInt = Integer.parseInt(time.replace(":", ""));
                int remainderTimeInt = Integer.parseInt(remainderTime.replace(":", ""));

                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                String lastShownDate = SharedPref.getTodayPopupShown(HomeDashBoard.this);

                if (SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("1") && !today.equals(lastShownDate) && (currentTimeInt >= remainderTimeInt)) {
                    checkAndShowDoctorPopup();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

//        mediaController = new MediaController(this);
//        binding.videoView.setMediaController(mediaController);
//        String jsonStr = "{ \"video_url\" : \"https://www.html5rocks.com/en/tutorials/video/basics/devstories.webm\" }";
//        try {
//            JSONObject jsonObject = new JSONObject(jsonStr);
//            videoUrl = jsonObject.getString("video_url");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        if (!videoUrl.isEmpty()) {
//            playVideo(videoUrl);
//        }

//        binding.youtubePlayerView.getPlayerUiController().showUi(false);
//        binding.youtubePlayerView.setEnableAutomaticInitialization(true);
//        getLifecycle().addObserver(binding.youtubePlayerView);

//        binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer player) {
//                youTubePlayer = player;
//                youTubePlayer.loadVideo("jZwyEuVrUKA", 0);
//            }
//        });

        // Play / Pause
        binding.btnPlayPause.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (youTubePlayer != null) {
                    if (isPlaying) {
                        youTubePlayer.pause();
                        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        youTubePlayer.play();
                        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    }
                    isPlaying = !isPlaying;
                }
            }
        });

        // Close
        binding.btnClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (youTubePlayer != null) {
                    youTubePlayer.pause();
                    isPlaying = false;
                    youTubePlayer = null;
                }
//            binding.youtubePlayerView.release();
//            getLifecycle().removeObserver(binding.youtubePlayerView);
                binding.floatingPlayer.setVisibility(View.GONE);
            }
        });

        // Dragging
//        binding.floatingPlayer.setOnTouchListener(new View.OnTouchListener() {
//            private int lastX, lastY;
//            private int paramsX, paramsY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        lastX = (int) event.getRawX();
//                        lastY = (int) event.getRawY();
//                        paramsX = (int) v.getX();
//                        paramsY = (int) v.getY();
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        int dx = (int) event.getRawX() - lastX;
//                        int dy = (int) event.getRawY() - lastY;
//                        v.setX(paramsX + dx);
//                        v.setY(paramsY + dy);
//                        return true;
//                }
//                return false;
//            }
//        });
//        binding.floatingPlayer.setOnTouchListener((v, event) -> {
//            switch (event.getActionMasked()) {
//                case MotionEvent.ACTION_DOWN:
//                    dX = v.getX() - event.getRawX();
//                    dY = v.getY() - event.getRawY();
//                    lastAction = MotionEvent.ACTION_DOWN;
//                    return true;
//
//                case MotionEvent.ACTION_MOVE:
//                    v.animate()
//                            .x(event.getRawX() + dX)
//                            .y(event.getRawY() + dY)
//                            .setDuration(0)
//                            .start();
//                    lastAction = MotionEvent.ACTION_MOVE;
//                    return true;
//
//                case MotionEvent.ACTION_UP:
//                    if (lastAction == MotionEvent.ACTION_DOWN) {
//                        // Could detect tap here if needed
//                    }
//                    return true;
//
//                default:
//                    return false;
//            }
//        });

        binding.dragOverlay.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            int lastAction;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                View root = binding.getRoot(); // <-- your root container id
                if (root == null) return false;

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        // Prevent parent (e.g., RecyclerView/ScrollView) from stealing events
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        dX = binding.floatingPlayer.getX() - event.getRawX();
                        dY = binding.floatingPlayer.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        return true;

                    case MotionEvent.ACTION_MOVE: {
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        // Clamp to root bounds
                        int rootW = root.getWidth();
                        int rootH = root.getHeight();
                        int fpW = binding.floatingPlayer.getWidth();
                        int fpH = binding.floatingPlayer.getHeight();

                        newX = Math.max(0, Math.min(newX, rootW - fpW));
                        newY = Math.max(0, Math.min(newY, rootH - fpH));

                        binding.floatingPlayer.setX(newX);
                        binding.floatingPlayer.setY(newY);
                        lastAction = MotionEvent.ACTION_MOVE;
                        return true;
                    }

                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return lastAction == MotionEvent.ACTION_MOVE;

                    default:
                        return false;
                }
            }
        });
//        checkAndShowDoctorPopup();
        // Show binding.floatingPlayer player initially
//        binding.floatingPlayer.setVisibility(View.VISIBLE);
        //checkAndShow5PMDoctorPopup();
    }

    private void syncSetup() {
        MasterSyncItemModel setupModel = new MasterSyncItemModel(Constants.SETUP, Constants.SETUP, "getsetups_edet", Constants.SETUP, 0, false);
        try {
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
            jsonObject.put("tableName", setupModel.getRemoteTableName());
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SharedPref.getSfCode(this));
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
            Log.e("API Object", "master sync obj : " + jsonObject);
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "table/setups");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        Log.e("response :   ", setupModel.getRemoteTableName() + " : " + response.body().toString());
                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject2 = new JSONObject();
                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + setupModel.getMasterOf() + " -- " + setupModel.getRemoteTableName() + " : " + response.body().toString());
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                        if (!jsonObject2.has("success")) {
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(setupModel.getLocalTableKeyName(), 1);
                                            setupModel.setSyncSuccess(1);
                                        }
                                    }
                                    if (success) {
                                        setupModel.setCount(jsonArray.length());
                                        setupModel.setSyncSuccess(2);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(setupModel.getLocalTableKeyName(), jsonArray.toString(), 2));
                                        if (jsonArray.length() > 0) {
                                            SharedPref.setIsSetupSynced(HomeDashBoard.this, true);
                                            SharedPref.InsertLogInData(HomeDashBoard.this, jsonArray.getJSONObject(0));
                                            String signInTime = SharedPref.getSignInTime(HomeDashBoard.this);
                                            if (signInTime.isEmpty()) {
                                                changePassword(HomeDashBoard.this.getString(R.string.reset_password));
                                            } else {
                                                try {
//                                                    JSONObject jsonObject = new JSONObject(signInTime);
//                                                    String date = jsonObject.optString("date");
//                                                    Log.i("Login date", "onPostCreate: " + date);
                                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeUtils.FORMAT_1);
                                                    LocalDateTime givenDate = LocalDateTime.parse(signInTime, formatter);
                                                    LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
                                                    if (givenDate.isBefore(ninetyDaysAgo)) {
                                                        changePassword(HomeDashBoard.this.getString(R.string.reset_password));
                                                    } else {
                                                        System.out.println("The given date is within the last 90 days.");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
//                                        JSONArray input = masterDataDao.getMasterDataTableOrNew(Constants.SETUP).getMasterSyncDataJsonArray();
//                                        for (int bean = 0; bean < input.length(); bean++) {
//                                            try {
//                                                JSONObject setUpObject = input.getJSONObject(bean);
//                                                String appAccess = setUpObject.getString("sanzen_edet");
//                                                if (!appAccess.equals("1")){
//                                                    CommonUtilsMethods.accessDialogBox(HomeDashBoard.this);
//                                                }
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
                                    }
                                } else {
                                    setupModel.setSyncSuccess(1);
                                    masterDataDao.saveMasterSyncStatus(setupModel.getLocalTableKeyName(), 1);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            setupModel.setSyncSuccess(1);
                            masterDataDao.saveMasterSyncStatus(setupModel.getLocalTableKeyName(), 1);
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        masterDataDao.saveMasterSyncStatus(setupModel.getLocalTableKeyName(), 1);
                        setupModel.setPBarVisibility(false);
                        setupModel.setSyncSuccess(1);
                    }
                });
            }
        } catch (Exception e) {
            Log.v("masterCheck", "--error-" + e);
            e.printStackTrace();
        }

    }

    private void playVideo(String url) {
//        binding.videoView.setVideoURI(Uri.parse(url));
//        binding.videoView.setVisibility(VideoView.VISIBLE);
//        binding.videoView.start();
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
//        enterPipMode();
        if (youTubePlayer != null) {
            youTubePlayer.pause();
            isPlaying = false;
            youTubePlayer = null;
        }
//        binding.youtubePlayerView.release();
//        getLifecycle().removeObserver(binding.youtubePlayerView);
        binding.floatingPlayer.setVisibility(View.GONE);
    }

    private void enterPipMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(16, 9);
            PictureInPictureParams params = new PictureInPictureParams.Builder()
                    .setAspectRatio(aspectRatio)
                    .build();
            enterPictureInPictureMode(params);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                enterPictureInPictureMode();
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

//        if (isInPictureInPictureMode) {
//            // Hide extra dashboard UI if needed
//            mediaController.hide();
//        } else {
//            // Restore full UI when back
//            mediaController.show();
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedDate != null) {
            outState.putString("date", selectedDate.toString());
        }
        outState.putBoolean("isSaved", true);
    }

    @Override
    protected void onResume() {
        if (!isFakeLocationDetected) {
            timeZoneVerification();
            if (isDateSelectionClicked) {
                setUpCalendar();
            }
            accessibility();
            super.onResume();
            AppIdentify();
            Log.d("ACTIVITY_STATUS", "OnResume");
            commonUtilsMethods = new CommonUtilsMethods(HomeDashBoard.this);
            commonUtilsMethods.setUpLanguage(HomeDashBoard.this);
            if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
                binding.myDrawerLayout.closeDrawer(GravityCompat.START);
            }
            //  String tpDoctor = SharedPref.getTodayTPDoctor(this);
            // showNotVisitedDoctorsPopup(tpDoctor, false); // false → respects time restriction

//            String tpDoctor = SharedPref.getTodayTPDoctor(this);
//
//            if (tpDoctor != null && !tpDoctor.isEmpty()) {
//                if (SharedPref.isDataCleared(this)) {
//                    Log.e("PopupCheck", "✅ Data was cleared → showing immediate popup");
//                    showNotVisitedDoctorsPopup(tpDoctor, true);  // immediate popup
//                    SharedPref.setDataCleared(this, false);      // reset flag
//                } else {
//                    showNotVisitedDoctorsPopup(tpDoctor, false); // normal popup
//                }
//            } else {
//                Log.e("PopupCheck", "❌ TP Doctor empty. Skipping popup.");
//            }
//
            try {
                if (Build.VERSION.SDK_INT >= 33) {
                    registerReceiver(receiver, intentFilter, RECEIVER_NOT_EXPORTED);
                } else {
                    registerReceiver(receiver, intentFilter);
                }
            } catch (Exception ignored) {

            }

            Menu menu = binding.navView.getMenu();
            if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                menu.findItem(R.id.approval).setVisible(SharedPref.getApproveNeed(this).equalsIgnoreCase("0"));
                menu.findItem(R.id.stp).setVisible(false);
            } else {
                menu.findItem(R.id.approval).setVisible(false);
                menu.findItem(R.id.stp).setVisible(SharedPref.getStpNeed(this).equalsIgnoreCase("0"));
            }

            if (SharedPref.getTpdcrMgrappr(this).equalsIgnoreCase("0")) {
                binding.viewCalerderLayout.txtTpDeviation.setVisibility(View.VISIBLE);
                binding.viewCalerderLayout.txtTpDeviationRele.setVisibility(View.VISIBLE);
            } else {
                binding.viewCalerderLayout.txtTpDeviation.setVisibility(View.GONE);
                binding.viewCalerderLayout.txtTpDeviationRele.setVisibility(View.GONE);
            }

            CommonAlertBox.CheckLocationStatus(HomeDashBoard.this, gpsTrack);

            boolean isResetPasswordVisible = false;
            if (SharedPref.getResetPasswordNeed(HomeDashBoard.this).equalsIgnoreCase("0")) {
                String signInTime = SharedPref.getSignInTime(HomeDashBoard.this);
                if (signInTime.isEmpty() && !SharedPref.getIsSetupSynced(HomeDashBoard.this)) {
                    isResetPasswordVisible = true;
                    changePassword(HomeDashBoard.this.getString(R.string.reset_password));
                } else if (signInTime.isEmpty()) {
                    syncSetup();
                } else {
                    try {
//                    JSONObject jsonObject = new JSONObject(signInTime);
//                    String date = jsonObject.optString("date");
//                    Log.i("Login date", "onPostCreate: " + date);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeUtils.FORMAT_1);
                        LocalDateTime givenDate = LocalDateTime.parse(signInTime, formatter);
                        String resetPasswordDays = SharedPref.getResetPasswordDays(HomeDashBoard.this);
                        int days = 90;
                        if (resetPasswordDays != null && !resetPasswordDays.isEmpty()) {
                            days = Integer.parseInt(resetPasswordDays);
                        }
                        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(days);
                        if (givenDate.isBefore(ninetyDaysAgo)) {
                            isResetPasswordVisible = true;
                            changePassword(HomeDashBoard.this.getString(R.string.reset_password));
                        } else {
                            System.out.println("The given date is within the last 90 days.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!isResetPasswordVisible) {
                if (SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("2") && SharedPref.getApprMandatoryNeed(HomeDashBoard.this).equalsIgnoreCase("0")) {
                    CheckingManatoryApprovals();
                }
                CheckedTpRange();
                //  showBirthdayPopup();
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

                if (!today.equals(SharedPref.getBirthdayShownDate(HomeDashBoard.this)) || !today.equals(SharedPref.getAnniversaryShownDate(HomeDashBoard.this))) {
                    new Handler().postDelayed(this::showCombinedWishesPopup, 1000);
                }
//                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                if (!today.equals(SharedPref.getBirthdayShownDate(HomeDashBoard.this))) {
//                    new Handler().postDelayed(this::showCombinedWishesPopup, 1000);
//                    //showBirthdayPopup();
//                    //SharedPref.setBirthdayShownDate(HomeDashBoard.this, today);
//                }
////
////                //  showAnniversaryPopup();
//                String today2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                if (!today2.equals(SharedPref.getAnniversaryShownDate(HomeDashBoard.this))) {
//                    new Handler().postDelayed(this::showCombinedWishesPopup, 1000);
//                   // showAnniversaryPopup();
//                   // SharedPref.setAnniversaryShownDate(HomeDashBoard.this, today);
//                }
            }

//            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//            if (!today.equals(SharedPref.getBirthdayShownDate(HomeDashBoard.this))) {
//                showBirthdayPopup();
//                SharedPref.setBirthdayShownDate(HomeDashBoard.this, today);
//            }
            checkAndSetEntryDate(this, true);
            if (isDcrFrom) {
                binding.viewPager.setCurrentItem(1);
                isDcrFrom = false;
            }
        } else {
            super.onResume();
        }
        if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
            checkUserStatus();
        }
        if(SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("1")) {
            checkAndShowDoctorPopup();
        }
    }

    private void checkAndShowDoctorPopup() {
        try {
            if (SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("2")){
                Log.d("Tag","No Popup for MGR (check and show)");
                return;
            }
                RoomDB roomDB = RoomDB.getDatabase(this);
            MasterDataDao masterDataDao = roomDB.masterDataDao();

            try {
                JSONArray tpArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();
                if (tpArray.length() == 0) return;

                JSONObject targetSession = null;

                for (int i = 0; i < tpArray.length(); i++) {
                    JSONObject sessionObj = tpArray.getJSONObject(i);
                    if (sessionObj.optString("FWFlg", "").equalsIgnoreCase("F")) {
                        targetSession = sessionObj;
                        break;
                    }
                }

                if (targetSession == null) {
//                    Log.d("PopupCheck", "No session in the TP data has FWFlg = 'F'.");
                    return;
                }

                String tpDoctorCodes = targetSession.optString("TP_Doctor", "").trim();

                if (tpDoctorCodes.isEmpty()) {
//                    Log.e("PopupCheck", "Field Work session found, but TP_Doctor codes are empty.");
                    return;
                }

                SharedPref.setTodayTPDoctor(this, tpDoctorCodes);

                JSONArray doctorMasArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();

                if (SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("1") && doctorMasArray == null || doctorMasArray.length() == 0) {
                    new Handler(Looper.getMainLooper()).postDelayed(this::checkAndShowDoctorPopup, 2000);
                    return;
                }

                boolean forceImmediate = SharedPref.isDataCleared(this);
                if (forceImmediate) {
                    SharedPref.clearCumulativeVisitedDoctors(this);
                }

                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                String lastShownDate = SharedPref.getTodayPopupShown(this);

                if (today.equals(lastShownDate) && !forceImmediate) {
//                    Log.d("PopupCheck", "Popup already shown for today.");
                    return;
                }

                String remainderTime = SharedPref.getDoctorRemainingShownDate(this);
                String time = TimeUtils.getCurrentDateTimeTp(TimeUtils.FORMAT_29);

                int currentTimeInt = Integer.parseInt(time.replace(":", ""));
                int remainderTimeInt = Integer.parseInt(remainderTime.replace(":", ""));

                if (!tpDoctorCodes.equals("null") && !tpDoctorCodes.isEmpty()) {
                    if (!today.equals(lastShownDate) && currentTimeInt >= remainderTimeInt) {
                        showNotVisitedDoctorsPopup(tpDoctorCodes, forceImmediate, doctorMasArray);
                        SharedPref.setTodayPopupShown(this, today);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "Error in Popup Logic: " + e.getMessage());
            }



        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error in Popup Logic: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        handler1.postDelayed(runnable, delay);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == InAppUpdate.updateRequestCode) {
            if (resultCode != RESULT_OK) {
                // commonUtilsMethods.showToastMessage(this, "Update canceled !");
                commonUtilsMethods.showToastMessage(this, getString(R.string.update_canceled));
                // Handle update failure or cancellation
            }
//            else {
//                commonUtilsMethods.showToastMessage(this, "Updated");
//            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (inAppUpdate != null) {
            inAppUpdate.stopUpdate();
        }
        unregisterNetworkCallback();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeDashBoardActivity = this;
        previousDate = dateFormat.format(new Date());
        handler.post(updateClock);
        Log.d("ACTIVITY_STATUS", "OnCreate");
        commonUtilsMethods = new CommonUtilsMethods(HomeDashBoard.this);
        commonUtilsMethods.setUpLanguage(HomeDashBoard.this);
        binding = ActivityHomeDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        activity = this;
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isSaved")) {
            if (savedInstanceState.getString("date") != null) {
                binding.textDate.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_27, savedInstanceState.getString("date")));
                selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                workingDate = selectedDate.toString();
            }
        }

        // THIS CODE IS DESIGN
        apiInterface = RetrofitClient.getRetrofit(HomeDashBoard.this, SharedPref.getCallApiUrl(HomeDashBoard.this));

        LinearLayout containerLayout = new LinearLayout(this);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        @SuppressLint("RestrictedApi") NavigationMenuView menuView = (NavigationMenuView) binding.navView.getChildAt(0);
        binding.navView.removeView(menuView);
        menuView.setVerticalScrollBarEnabled(false);
        menuView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        scrollView.addView(menuView);
        containerLayout.addView(scrollView);

        View divider = new View(this);
        divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(getResources().getColor(R.color.bg_grey_2));
        containerLayout.addView(divider);

        navigationFooterBinding = HomeNavigationFooterBinding.inflate(getLayoutInflater());
        containerLayout.addView(navigationFooterBinding.getRoot());

        binding.navView.addView(containerLayout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
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
        gpsTrack = new GPSTrack(this);
        roomDB = RoomDB.getDatabase(HomeDashBoard.this);
        masterDataDao = roomDB.masterDataDao();
        roomDB = RoomDB.getDatabase(HomeDashBoard.this);
        masterDataDao = roomDB.masterDataDao();
        slidesDao = roomDB.slidesDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();
//        leaveViewModel = new LeaveViewModel(this);
        outboxUtil = new OutboxUtil(this);
        inAppUpdate = new InAppUpdate(this);

        tourPlanOfflineDataDao = roomDB.tourPlanOfflineDataDao();
        commonUtilsMethods = new CommonUtilsMethods(HomeDashBoard.this);
        commonUtilsMethods.setUpLanguage(HomeDashBoard.this);
        binding.toolbarTitle.setText(SharedPref.getDivisionName(this));
        binding.subDivision.setText(SharedPref.getSubDivisionNames(this));
        isDateSelectionClicked = false;

        if (SharedPref.getGeoChk(HomeDashBoard.this).equalsIgnoreCase("0")) {
            if (!CheckLocPermission()) {
                RequestLocationPermission();
            }
        }
        requestNotificationPermission();
        notificationViewModel.getUnreadNotificationCount().observe(this, count -> {
            if (count != null && count > 0) {
                binding.notificationRedDot.setVisibility(View.VISIBLE);
            } else {
                binding.notificationRedDot.setVisibility(View.GONE);
            }
        });

        notificationViewModel.getAllUnsyncedNotifications().observe(this, list -> {
            if (!list.isEmpty()) {
                for (NotificationDataTable notificationData : list) {
                    if (syncingIds.contains(notificationData.getId())) continue;
                    syncingIds.add(notificationData.getId());
                    String title = notificationData.getTitle(), body = notificationData.getMessage(), time = notificationData.getDateTime(), type = "", hqCode = "";
                    int id = notificationData.getId();
                    hqCode = SharedPref.getHqCode(this);
                    if (hqCode == null || hqCode.isEmpty()) {
                        hqCode = SharedPref.getSfCode(this);
                    }
                    if (body.contains("$")) {
                        try {
                            if (body.contains("-MR")) {
                                type = body.substring(body.lastIndexOf("$") + 1, body.lastIndexOf("-MR"));
                                hqCode = body.substring(body.lastIndexOf("-MR") + 1);
                            } else {
                                type = body.substring(body.lastIndexOf("$") + 1);
                            }
                            body = body.substring(0, body.lastIndexOf("$"));
                            body = body.replace(" Kindly Sync it.", "");
                            body = body.replace(" Kindly Logout the App &", "");
                            body = body.replace("Kindly Logout the App.", "");
                            body = body.replace(" Kindly Logout & Login the App.", "");
                            body = body.replace(" Kindly Sync these in Master Sync Screen.", "");
                            showNotificationDialog(title, body, type, hqCode, id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        binding.imgChat.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ContinuousLogCollector.stopLogging(getApplicationContext());
//            startActivity(new Intent(HomeDashBoard.this, MapViewActvity.class));
            }
        });

        binding.imgNotification.setOnClickListener(view -> {
            showNotificationPopup();
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.v("BBB", "" + tab.getPosition());
                SharedPref.setSetUpClickedTab(HomeDashBoard.this, tab.getPosition());
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
        accessibility();

        gpsTrack = new GPSTrack(this);
        latitude = gpsTrack.getLatitude();
        longitude = gpsTrack.getLongitude();
        if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
            address = CommonUtilsMethods.gettingAddress(this, latitude, longitude, false);
        } else {
            address = getString(R.string.no_address_found);
        }
        Log.e("addresss", " :" + latitude + "   :" + longitude + " :" + address);

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
//                leaveViewModel.updateLeaveStatusMasterSync();
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
        binding.backArrow.setOnClickListener(view -> {
            if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
                binding.myDrawerLayout.closeDrawer(GravityCompat.START);

            } else {
                binding.myDrawerLayout.openDrawer(GravityCompat.START);
                binding.backArrow.setBackgroundResource(R.drawable.cross_img);
            }
        });
        if(SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("1")) {
            checkAndShowDoctorPopup();
        }
    }

    private void registerNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                runOnUiThread(() -> checkUserStatus());
            }
        };
        cm.registerNetworkCallback(request, networkCallback);
    }

    private void unregisterNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (networkCallback != null) {
            cm.unregisterNetworkCallback(networkCallback);
        }
    }

    private void checkUserStatus() {
        String lastCheckedDate = SharedPref.getStatusCheckedDate(HomeDashBoard.this);
        if (lastCheckedDate.isEmpty() || !LocalDate.now().toString().equals(lastCheckedDate)) {
            JSONObject jj = CommonUtilsMethods.CommonObjectParameter(this);
            try {
                @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                jj.put("tableName", "getuserstatus");
                jj.put("sfcode", SharedPref.getSfCode(this));
                jj.put("division_code", SharedPref.getDivisionCode(this));
                jj.put("Rsf", SharedPref.getHqCode(this));
                jj.put("Username", SharedPref.getLoginId(this));
                jj.put("Password", SharedPref.getLoginUserPwd(this));
                jj.put("DeviceID", deviceId);

                Log.d("user status", String.valueOf(jj));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "table/dcrmasterdata");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(HomeDashBoard.this), mapString, jj.toString());

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        Log.i("User status", "onResponse: " + response.body().toString());
                        SharedPref.setStatusCheckedDate(HomeDashBoard.this, LocalDate.now().toString());
                        try {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            if (response.body().isJsonObject()) {
                                jsonObject = new JSONObject(response.body().toString());
                                jsonArray.put(jsonObject);
                            } else if (response.body().isJsonArray()) {
                                jsonArray = new JSONArray(response.body().toString());
                            }
                            jsonObject = jsonArray.optJSONObject(0);
                            String success = jsonObject.optString("success", "true"), key = jsonObject.optString("key", "");
                            if (success.equalsIgnoreCase("false") || !key.isEmpty()) {
                                String reason = "";
                                switch (key) {
                                    case "PC": {
                                        reason = getString(R.string.str_password_changed);
                                        break;
                                    }
                                    case "DC": {
                                        reason = getString(R.string.str_device_id_updated);
                                        break;
                                    }
                                    case "AD": {
                                        reason = getString(R.string.str_access_denied);
                                        break;
                                    }
                                    case "V": {
                                        reason = getString(R.string.str_user_status_vacant);
                                        break;
                                    }
                                    case "H": {
                                        reason = getString(R.string.str_user_status_hold);
                                        break;
                                    }
                                    case "B": {
                                        reason = getString(R.string.str_user_status_blocked);
                                        break;
                                    }
                                    case "D": {
                                        reason = getString(R.string.str_device_not_valid);
                                        break;
                                    }
//                                    default: {
//                                        reason = "Kindly logout and login!";
//                                        break;
//                                    }
                                }
                                if (!reason.isEmpty()) {
                                    showStatusDialog(reason);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void showStatusDialog(String reason) {
        Dialog dialog = new Dialog(HomeDashBoard.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (!isFinishing()) {
            dialog.show();
        }
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        TextView content = dialog.findViewById(R.id.ed_alert_msg);
        content.setText(reason);
        content.setHint("");
        btn_yes.setText(getString(R.string.logout));
        btn_no.setVisibility(View.GONE);
        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
                SharedPref.setLogoutReason(HomeDashBoard.this, reason);
                SharedPref.saveLoginState(HomeDashBoard.this, false);
                SharedPref.saveLoginPwd(HomeDashBoard.this, "");
                Intent intent = new Intent(HomeDashBoard.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showNotificationPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.notification_popup_view, null);
        notificationPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        int y = (getResources().getDimensionPixelSize(R.dimen._26sdp));
        int x = (getResources().getDimensionPixelSize(R.dimen._13sdp));
        notificationPopupWindow.showAtLocation(binding.imgNotification, Gravity.END | Gravity.TOP, x, y);
        notificationPopupWindow.setOutsideTouchable(true);

        ImageView ivClearAll = popupView.findViewById(R.id.iv_clear_all);
        ivClearAll.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                notificationViewModel.clearAll();
            }
        });

        TextView tvNoNewNotification = popupView.findViewById(R.id.tv_no_notification);
        RecyclerView rvNotification = popupView.findViewById(R.id.rv_notification);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNotification.setLayoutManager(layoutManager);
        final NotificationsAdapter adapter = new NotificationsAdapter(HomeDashBoard.this, notificationDataTable -> notificationViewModel.deleteNotification(notificationDataTable.getId()));
        rvNotification.setAdapter(adapter);

        rvNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    markVisibleItemsAsRead(recyclerView, layoutManager, adapter);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        notificationViewModel.getAllNotifications().observe(this, notificationDataTables -> {
            if (notificationDataTables.isEmpty()) {
                tvNoNewNotification.setVisibility(View.VISIBLE);
                rvNotification.setVisibility(View.GONE);
            } else {
                tvNoNewNotification.setVisibility(View.GONE);
                rvNotification.setVisibility(View.VISIBLE);
                adapter.setNotifications(notificationDataTables);
                rvNotification.post(() -> markVisibleItemsAsRead(rvNotification, layoutManager, adapter));
            }
        });
        notificationPopupWindow.update();
    }

    private void showNotificationDialog(String title, String body, String type, String hqCode, int id) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            Activity currentActivity = AppActivityTracker.getInstance().getCurrentActivity();
            if (currentActivity != null) {
                currentActivity.runOnUiThread(() -> {
                    NotificationDialog.showDialog(currentActivity, title, body, type, hqCode, id);
                });
            }
        });
    }

    private void markVisibleItemsAsRead(RecyclerView recyclerView, LinearLayoutManager layoutManager, NotificationsAdapter adapter) {
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        List<NotificationDataTable> notifications = adapter.getNotifications();

        if (notifications == null || notifications.isEmpty()) {
            return;
        }

        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            if (i < notifications.size()) {
                NotificationDataTable notification = notifications.get(i);
                if (notification.getIsRead() == 0) {
                    notificationViewModel.markAsRead(notification.getId());
                }
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private static void setupLeftViewPager(Context context, FragmentManager fragmentManager) {
        leftViewPagerAdapter = new TabLayoutAdapter(fragmentManager);
        leftViewPagerAdapter.add(new WorkPlanFragment(), homeDashBoardActivity.getString(R.string.work_plan));
        leftViewPagerAdapter.add(new CallsFragment(), homeDashBoardActivity.getString(R.string.calls));
        leftViewPagerAdapter.add(new OutboxFragment(), homeDashBoardActivity.getString(R.string.outbox));
        // leftViewPagerAdapter.add(new OutboxFragment(), "Outbox");
        binding.viewPager.post(() -> {
            binding.viewPager.setAdapter(leftViewPagerAdapter);
        });

//        binding.viewPager.setAdapter(leftViewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setOffscreenPageLimit(leftViewPagerAdapter.getCount());
    }

    private void CallDialogAfterCheckIn() {
        dialogCheckInOut.dismiss();
        dialogAfterCheckIn = new Dialog(this);
        dialogAfterCheckIn.setContentView(R.layout.dialog_day_check_out);
        dialogAfterCheckIn.setCancelable(false);
        if (dialogAfterCheckIn.getWindow() != null) {
            dialogAfterCheckIn.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

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

        btnClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogAfterCheckIn.dismiss();
            }
        });

        if (!HomeDashBoard.this.isFinishing()) {
            dialogAfterCheckIn.show();
        }
    }

    private void getRequiredData() {
        try {
//            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CUSTOM_SETUP).getMasterSyncDataJsonArray();
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject setupData = jsonArray.getJSONObject(0);
//                customSetupResponse = new CustomSetupResponse();
//                Type typeSetup = new TypeToken<CustomSetupResponse>() {
//                }.getType();
//                customSetupResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
//                PresentationNeed = customSetupResponse.getPresentationNeed();
//                CustomPresentationNeed = customSetupResponse.getCustomizationPrsNeed();
//            }
            CheckInOutNeed = SharedPref.getSrtNd(this);
            PresentationNeed = SharedPref.getPresentationNeed(this);
            CustomPresentationNeed = SharedPref.getCustomizationPresentationNeed(this);
            SequentialEntry = SharedPref.getDcrSequential(this);
            if (PresentationNeed.equalsIgnoreCase("0")) {
//                if (CustomPresentationNeed.equalsIgnoreCase("0")) {
                binding.llPresentation.setVisibility(View.VISIBLE);
            } else {
                binding.llPresentation.setVisibility(View.GONE);
            }
//                binding.llSlide.setVisibility(View.VISIBLE);
//            } else {
//                binding.llPresentation.setVisibility(View.GONE);
//                binding.llSlide.setVisibility(View.GONE);
//            }
        } catch (Exception e) {
            Log.e("Presentation", "getRequiredData: " + e.getMessage());
            e.printStackTrace();
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
                JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();

                if (workTypeArray.length() > 0) {
                    JSONObject jsonObject = workTypeArray.getJSONObject(0);
                    String TPDt = jsonObject.getString("TPDt");
                    JSONObject jsonObject1 = new JSONObject(TPDt);
                    String dayPlan_Date = jsonObject1.getString("date");
                    String CurrentDate = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_15);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
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
            JSONArray dcrdatas = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
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
            }
            for (EventCalenderModelClass list : callsatuslist) {
                int index = ListID.indexOf(list.getDateID());
                if (index != -1) {
                    if (daysInMonthArray.size() > index && daysInMonthArray.get(index) != null) {
                        daysInMonthArray.get(index).setWorkTypeFlag(list.getWorkTypeFlag());
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
        if (!activity.isFinishing() && !activity.isDestroyed()) {
            popupWindow.showAtLocation(viewed_img, Gravity.END, x, y);
        }

        TextView user_name = popupView.findViewById(R.id.user_name);
        TextView sf_name = popupView.findViewById(R.id.sf_name);
        TextView Cluster = popupView.findViewById(R.id.clut);
        LinearLayout l_click = popupView.findViewById(R.id.change_passwrd);
        LinearLayout user_logout = popupView.findViewById(R.id.user_logout);
        ImageView info = popupView.findViewById(R.id.info);

        user_logout.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                SharedPref.saveLoginState(HomeDashBoard.this, false);
                Intent intent = new Intent(HomeDashBoard.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //    commonUtilsMethods.showToastMessage(HomeDashBoard.this,"Logout Successfully")
//            commonUtilsMethods.showToastMessage(HomeDashBoard.this, HomeDashBoard.this.getString(R.string.logout_successfully));
                Toast.makeText(HomeDashBoard.this, getString(R.string.logout_successfully), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        user_name.setText(SharedPref.getSfName(this));
        sf_name.setText(SharedPref.getDsName(this));
        Cluster.setText(SharedPref.getHqNameMain(this));

        info.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                popupWindow.dismiss();
                startActivity(new Intent(HomeDashBoard.this, ProfileViewScreen.class));
            }
        });

        if (SharedPref.getPwdSetup(this).equalsIgnoreCase("0")) {
            l_click.setVisibility(View.VISIBLE);
        } else {
            l_click.setVisibility(View.GONE);
        }

        l_click.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                    popupWindow.dismiss();
                    changePassword(HomeDashBoard.this.getString(R.string.change_password));
                } else {
                    //CommonUtilsMethods.showToastMessage(HomeDashBoard.this, "Please Check The Internet Connection");
                    CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.please_check_the_internet_connection));
                }
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

    }

    @SuppressLint({"MissingInflatedId", "WrongConstant", "UseCompatLoadingForDrawables"})
    public void changePassword(String title) {
        //  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreencall();
        try {
            if (dialogPwdChange != null && dialogPwdChange.isShowing()) {
                dialogPwdChange.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogPwdChange = new Dialog(this);
        dialogPwdChange.setCancelable(false);

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
        TextView tvTitle = dialogPwdChange.findViewById(R.id.title);
        ImageView cls_but = dialogPwdChange.findViewById(R.id.close);
        ProgressBar progressBar = dialogPwdChange.findViewById(R.id.progressBar);
        tvTitle.setText(title);

        if (title.equals(HomeDashBoard.this.getString(R.string.reset_password))) {
            cls_but.setVisibility(View.GONE);
        } else {
            cls_but.setVisibility(View.VISIBLE);
        }

        old_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
//                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, "Maximum password length reached. Please keep it under 30 characters");
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.maximum_password_length_reached_please_keep_it_under_30_characters));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    // commonUtilsMethods.showToastMessage(HomeDashBoard.this, "Maximum password length reached. Please keep it under 30 characters");
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.maximum_password_length_reached_please_keep_it_under_30_characters));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        remain_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    // commonUtilsMethods.showToastMessage(HomeDashBoard.this, "Maximum password length reached. Please keep it under 30 characters");
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.maximum_password_length_reached_please_keep_it_under_30_characters));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        old_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(old_password, 100)});
        new_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(new_password, 100)});
        remain_password.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(remain_password, 100)});
        String password = SharedPref.getLoginUserPwd(this).toLowerCase();
//        System.out.println("loginPassword--->"+password);

        old_view.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (!old_password.getText().toString().equals("")) {

                    if (passwordNotVisible == 1) {
                        old_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        old_view.setImageDrawable(ContextCompat.getDrawable(HomeDashBoard.this, R.drawable.eye_hide));
                        passwordNotVisible = 0;
                    } else {
                        old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        old_view.setImageDrawable(ContextCompat.getDrawable(HomeDashBoard.this, R.drawable.eye_visible));
                        passwordNotVisible = 1;
                    }
                    old_password.setSelection(old_password.length());
                }
            }
        });

        newPass_view.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (!new_password.getText().toString().equals("")) {
                    if (passwordNotVisible1 == 1) {
                        new_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        remain_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        newPass_view.setImageDrawable(ContextCompat.getDrawable(HomeDashBoard.this, R.drawable.eye_hide));
                        passwordNotVisible1 = 0;
                    } else {
                        new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        remain_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        newPass_view.setImageDrawable(ContextCompat.getDrawable(HomeDashBoard.this, R.drawable.eye_visible));
                        passwordNotVisible1 = 1;
                    }
                    new_password.setSelection(new_password.length());
                }
            }
        });


        update.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (old_password.getText().toString().equals("")) {
                    CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.enter_old_pwd));
                } else if (new_password.getText().toString().equals("")) {
                    CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.enter_new_pwd));
                } else if (remain_password.getText().toString().equals("")) {
                    CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.enter_repeat_pwd));
                } else {
                    if (!password.equals(old_password.getText().toString().toLowerCase())) {
                        CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.chk_old_pwd));
                    } else if (!new_password.getText().toString().toLowerCase().equals(remain_password.getText().toString().toLowerCase())) {
                        CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.pwd_not_match));
                    } else if (new_password.getText().toString().toLowerCase().equals(password)) {
                        CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.change_new_password));
                    } else {
                        try {
                            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                                progressBar.setVisibility(View.VISIBLE);
                                CallChangePasswordAPI(old_password.getText().toString(), new_password.getText().toString(), remain_password.getText().toString(), progressBar, title);
                            } else {
                                //CommonUtilsMethods.showToastMessage(HomeDashBoard.this, "Please check Your Internet Connection");
                                CommonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.please_check_your_internet_connection));
                            }
                        } catch (Exception ignored) {
                        }

                    }
                }
            }
        });

        cls_but.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogPwdChange.dismiss();
            }
        });

        dialogPwdChange.setCanceledOnTouchOutside(false);
        dialogPwdChange.show();
    }

    private void CallChangePasswordAPI(String oldPwd, String newPwd, String confirmPwd, ProgressBar progressBar, String title) {
        JSONObject jj = CommonUtilsMethods.CommonObjectParameter(this);
        try {
            jj.put("tableName", "savechpwd");
            jj.put("sfcode", SharedPref.getSfCode(this));
            jj.put("division_code", SharedPref.getDivisionCode(this));
            jj.put("Rsf", SharedPref.getHqCode(this));
            jj.put("txOPW", oldPwd);
            jj.put("txNPW", newPwd);
            jj.put("txCPW", confirmPwd);

            Log.d("PassWord_Change", String.valueOf(jj));
        } catch (Exception ignored) {

        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/masterdata");
        Call<JsonElement> changePassword = apiInterface.getJSONElement(SharedPref.getCallApiUrl(HomeDashBoard.this), mapString, jj.toString());

        changePassword.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject js = new JSONObject(response.body().toString());
                        if (js.getString("success").equalsIgnoreCase("true")) {
                            SharedPref.saveLoginPwd(HomeDashBoard.this, confirmPwd);
                            commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.pwd_changed_successfully));
//                            startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
                            if (title.equalsIgnoreCase(HomeDashBoard.this.getString(R.string.reset_password))) {
                                SharedPref.saveLoginState(HomeDashBoard.this, false);
                                Intent intent = new Intent(HomeDashBoard.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                commonUtilsMethods.loginNavigation(HomeDashBoard.this);
                            }
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
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.refresh_location))) {
            setGpsTrack();
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.presentation))) {
            startActivity(new Intent(HomeDashBoard.this, PresentationActivity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.tour_plan))) {
            if (SharedPref.getStpNeed(this).equalsIgnoreCase("0")
                    && SharedPref.getStpBasedMtp(this).equalsIgnoreCase("0")
                    && !SharedPref.getSfType(this).equalsIgnoreCase("2")
                    && !SharedPref.getStpStatus(this).equalsIgnoreCase("Approved")) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.standard_tour_plan_must_be_approved_to_enter_tour_plan));
            } else {
                Intent intent = new Intent(HomeDashBoard.this, TourPlanActivity.class);
                startActivity(intent);
            }
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(SharedPref.getStpCaption(this))) {
            Intent intent = new Intent(HomeDashBoard.this, StandardTourPlanActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.faq))) {
            Intent intent = new Intent(HomeDashBoard.this, FAQ.class);
            startActivity(intent);
            return true;
        }
        if (item.getTitle().toString().equalsIgnoreCase(SharedPref.getQuizHeading(HomeDashBoard.this))) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                if (HomeDashBoard.selectedDate != null) {
                    startActivity(new Intent(HomeDashBoard.this, QuizActivity.class));
                } else {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.please_select_a_date));
                    binding.backArrow.setBackgroundResource(R.drawable.bars_sort_img);
                    binding.myDrawerLayout.closeDrawer(GravityCompat.START);
                }
            } else {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
            }
            return true;
        }
        if (item.getTitle().toString().equalsIgnoreCase(SharedPref.getActivityCap(this))) {
            startActivity(new Intent(HomeDashBoard.this, DynamicActivity.class));
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
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.clear_slides))) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.warning_label));
            alertDialogBuilder.setIcon(getDrawable(R.drawable.icon_sync_failed));
            alertDialogBuilder.setMessage(getString(R.string.are_you_sure_you_want_to_clear_slides));
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    roomDB.slidesDao().deleteAllData();
                    roomDB.welcomeSlidesDao().deleteAllData();

                    File slidesFolder = null;
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        slidesFolder = new File(HomeDashBoard.this.getExternalFilesDir(null), "Slides");
                    }

                    if (slidesFolder.exists()) {
                        deleteRecursive(slidesFolder);
                    }
                    slidesFolder.delete();

                    File thumbnailStorage = new File(getApplicationContext().getExternalFilesDir(null), "/Thumbnails/");
                    if (thumbnailStorage.exists() && thumbnailStorage.isDirectory()) {
                        File[] files = thumbnailStorage.listFiles();
                        for (File file : files) {
                            if (file.isFile()) {
                                file.delete();
                            }
                        }
                    }

//                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, " Slides Cleared Successfully");
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.slides_cleared_successfully));

                }
            });

            alertDialogBuilder.setNegativeButton(android.R.string.no, null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

// LEFT align message safely
            TextView message = alertDialog.findViewById(android.R.id.message);
            if (message != null) {
                message.setGravity(Gravity.LEFT); // START instead of LEFT is safer for RTL/LTR
                message.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            }

// LEFT align title safely
            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            TextView title = alertDialog.findViewById(titleId);
            if (title != null) {
                title.setGravity(Gravity.START); // START instead of LEFT
                title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            }
//            TextView msg = alertDialog.findViewById(android.R.id.message);
//            if (msg != null) {
//                msg.setGravity(Gravity.LEFT); // LEFT for English
//                msg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
//            }
//            alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
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
        String dynamicOptionCaps = SharedPref.getDynamicOptionCaps(HomeDashBoard.this);
        String optionCaps;
        if (dynamicOptionCaps == null || dynamicOptionCaps.isEmpty()) {
            optionCaps = getString(R.string.option);
        } else {
            optionCaps = dynamicOptionCaps;
        }
        if (item.getTitle().toString().equalsIgnoreCase(optionCaps)) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                startActivity(new Intent(HomeDashBoard.this, DynamicMenuHome.class));
            } else {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
            }
            return true;
        }

    /*    if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.reports))) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                startActivity(new Intent(HomeDashBoard.this, ReportsActivity.class));
            } else {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
            }
            return true;
        }*/

        if (item.getTitle().toString().equalsIgnoreCase(SharedPref.getRemainderCallCap(this))) {
            startActivity(new Intent(HomeDashBoard.this, RemaindercallsActivity.class));
            return true;
        }
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.doctor_business))) {
//            startActivity(new Intent(HomeDashBoard.this, DoctorBusinessActivity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.near_me))) {
//            if (SharedPref.getSrtNd(this).equalsIgnoreCase("0")) {
//                if (SharedPref.getSkipCheckIn(getApplicationContext())) {
//                    goToNearMeActivity();
//                } else {
//                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.submit_checkin));
//                }
//            } else {
            goToNearMeActivity();
//            }
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.survey))) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                startActivity(new Intent(HomeDashBoard.this, SurveyActivity.class));
            } else {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
            }
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.forms))) {
            startActivity(new Intent(HomeDashBoard.this, Forms_activity.class));
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.check_for_update))) {
            inAppUpdate.doCheckAndUpdate();
        }

        return true;
    }

    private void goToNearMeActivity() {
        if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
            if (!WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F") && !WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F") || WorkPlanFragment.binding.txtSave.isEnabled()) {
//            if (SharedPref.getHqCode(HomeDashBoard.this).equalsIgnoreCase("null") || SharedPref.getHqCode(HomeDashBoard.this).isEmpty()) {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.kindly_submit_field_work));
            } else if (WorkPlanFragment.deviation.equalsIgnoreCase("1") && SharedPref.getTpdcrMgrappr(HomeDashBoard.this).equalsIgnoreCase("0") && SharedPref.getTpdcrDeviationApprStatus(HomeDashBoard.this).equalsIgnoreCase("3")) {
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.get_deviation_approval));
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

    public static void checkAndSetEntryDate(Context context, boolean shouldShowCalender) {
        binding.viewPagerProgress.setVisibility(View.VISIBLE);
        isDateSelectionClicked = false;
        WorkPlanEntriesNeeded.updateMyDayPlanEntryDates(context, false, new WorkPlanEntriesNeeded.SyncTaskStatus() {
            @Override
            public void datesFound() {
                if (outboxUtil != null && outboxUtil.getOutboxDates().size() > 2 && !SharedPref.getLastOutboxAlertDate(context).equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_4))) {
                    CommonAlertBox.outboxDataAvailableAlert(activity);
                    SharedPref.setLastOutboxAlertDate(context, CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_4));
                }

                if (SequentialEntry != null && SequentialEntry.equalsIgnoreCase("0")) {
                    String dateRequired = SharedPref.getSelectedDateCal(context);
                    String monthDateYear = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_27, dateRequired);
                    selectedDate = LocalDate.parse(dateRequired, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34));
                    binding.textDate.setText(monthDateYear);
                    workingDate = selectedDate.toString();
                    SharedPref.setCheckDateTodayPlan(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, dateRequired));
                    Log.e("TAG 0", "checkAndSetEntryDate: " + selectedDate);
                } else if (canMoveNextDate) {
                    String dateRequired = SharedPref.getSelectedDateCal(context);
                    String monthDateYear = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_27, dateRequired);
                    selectedDate = LocalDate.parse(dateRequired, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34));
                    binding.textDate.setText(monthDateYear);
                    workingDate = selectedDate.toString();
                    SharedPref.setCheckDateTodayPlan(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, dateRequired));
                    Log.e("TAG 1", "checkAndSetEntryDate: " + selectedDate);
                } else {
                    canMoveNextDate = true;
                    selectedDate = null;
                    binding.textDate.setText(null);
                    workingDate = "";
                    Log.e("TAG 3", "checkAndSetEntryDate: " + selectedDate);
                }
                binding.viewPagerProgress.setVisibility(View.GONE);
                if (shouldShowCalender) {
                    if (HomeDashBoard.selectedDate != null && !SharedPref.getLastCallDate(context).equalsIgnoreCase(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)))) {
                        SharedPref.setLastCallDate(context, "");
                    }
                    setupLeftViewPager(context, fragmentManager);
                    if (SharedPref.getQuizNeed(context).equalsIgnoreCase("0")
                            && SharedPref.getQuizNeedMandt(context).equalsIgnoreCase("0")) {
                        activity.setUpQuiz();
                    }
                } else {
                    isDateSelectionClicked = true;
                }
            }

            @Override
            public void noDatesFound() {
                selectedDate = null;
                binding.textDate.setText(null);
                workingDate = "";
                binding.viewPagerProgress.setVisibility(View.GONE);
                if (shouldShowCalender) {
                    setupLeftViewPager(context, fragmentManager);
                } else {
                    isDateSelectionClicked = true;
                }
                Log.e("TAG 4", "checkAndSetEntryDate: " + selectedDate);
            }
        });
    }

    private void setUpQuiz() {
        if (selectedDate != null && SharedPref.getLastQuizSyncDate(this).equalsIgnoreCase(selectedDate.toString())) {
            Log.d("TAG", "setUpQuiz: new sync");
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.QUIZ).getMasterSyncDataJsonArray();
            if (!jsonArray.toString().equalsIgnoreCase("[]")) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.complete) + SharedPref.getQuizHeading(HomeDashBoard.this));
                startActivity(new Intent(this, QuizActivity.class));
            }
        } else if (selectedDate != null && !SharedPref.getLastQuizSyncDate(this).equalsIgnoreCase(selectedDate.toString())) {
            Log.d("TAG", "setUpQuiz: new sync");
            CallQuizSyncAPI();
        }
    }

    private void CallQuizSyncAPI() {
        if (selectedDate != null) {
            if (UtilityClass.isNetworkAvailable(this)) {
                binding.flSyncQuizProgress.setVisibility(View.VISIBLE);
                try {
                    apiInterface = RetrofitClient.getRetrofit(HomeDashBoard.this, SharedPref.getCallApiUrl(HomeDashBoard.this));
                    JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                    jsonObject.put("tableName", "getquiz");
                    jsonObject.put("sfcode", SharedPref.getSfCode(this));
                    jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                    jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_22, HomeDashBoard.selectedDate.toString()));
                    Log.i("QUIZ", "quiz: json -- " + jsonObject);
                    Map<String, String> qry = new HashMap<>();
                    qry.put("axn", "table/additionaldcrmasterdata");
                    Call<JsonElement> quiz = apiInterface.getJSONElement(SharedPref.getCallApiUrl(HomeDashBoard.this), qry, jsonObject.toString());
                    if (quiz != null) {
                        quiz.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> quiz, @NonNull Response<JsonElement> response) {
                                binding.flSyncQuizProgress.setVisibility(View.GONE);
                                SharedPref.setLastQuizSyncDate(HomeDashBoard.this, HomeDashBoard.selectedDate.toString());
                                if (response.isSuccessful()) {
                                    Log.e("quiz sync", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                    try {
                                        String responseData = response.body().toString();
                                        if (!responseData.equalsIgnoreCase("[]") && !responseData.isEmpty()) {
                                            JSONObject object = new JSONObject(responseData);
                                            JSONArray jsonArray = new JSONArray();
                                            jsonArray.put(object);
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.QUIZ, jsonArray.toString(), 2));
                                            SharedPref.setQuizAvailableDate(HomeDashBoard.this, HomeDashBoard.selectedDate.toString());
                                            commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.complete) + SharedPref.getQuizHeading(HomeDashBoard.this));
                                            Intent intent = new Intent(HomeDashBoard.this, QuizActivity.class);
                                            intent.putExtra(QuizActivity.SYNC_NEEDED, false);
                                            startActivity(intent);
                                        } else {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.QUIZ, "[]", 2));
                                        }
                                    } catch (Exception e) {
                                        binding.flSyncQuizProgress.setVisibility(View.GONE);
                                        Intent intent = new Intent(HomeDashBoard.this, QuizActivity.class);
                                        intent.putExtra(QuizActivity.SYNC_NEEDED, true);
                                        startActivity(intent);
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> quiz, @NonNull Throwable t) {
                                t.printStackTrace();
                                binding.flSyncQuizProgress.setVisibility(View.GONE);
                                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.poor_connection));
                                Intent intent = new Intent(HomeDashBoard.this, QuizActivity.class);
                                intent.putExtra(QuizActivity.SYNC_NEEDED, true);
                                startActivity(intent);
//                                SharedPref.setLastQuizSyncDate(activity, HomeDashBoard.selectedDate.toString());
                            }
                        });
                    }
                } catch (Exception e) {
                    binding.flSyncQuizProgress.setVisibility(View.GONE);
                    Intent intent = new Intent(HomeDashBoard.this, QuizActivity.class);
                    intent.putExtra(QuizActivity.SYNC_NEEDED, true);
                    startActivity(intent);
//                    SharedPref.setLastQuizSyncDate(activity, HomeDashBoard.selectedDate.toString());
                    e.printStackTrace();
                }
            } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
                Intent intent = new Intent(HomeDashBoard.this, QuizActivity.class);
                intent.putExtra(QuizActivity.SYNC_NEEDED, true);
                startActivity(intent);
            }
        }
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_calender_syn:
                binding.viewCalerderLayout.calendarProgressBar.setVisibility(View.VISIBLE);
                callAPIDateSync();
                break;

            case R.id.rl_date_layoout:
                isDateSelectionClicked = !isDateSelectionClicked;
                setUpCalendar();
                break;

            case R.id.ll_next_month:
                binding.viewCalerderLayout.llNextMonth.setEnabled(false);
                calendarDays.clear();
                if (selectedDate == null) {
                    selectedDate = LocalDate.now();
                }
                selectedDate = selectedDate.plusMonths(1);
                validateMonth(selectedDate);
                binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
                calendarDays = daysInMonthArray(selectedDate);
                callstatusadapter = new Callstatusadapter(calendarDays, HomeDashBoard.this, selectedDate);
                callstatusadapter.notifyDataSetChanged();
                binding.viewCalerderLayout.calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
                binding.viewCalerderLayout.calendarRecyclerView.setAdapter(callstatusadapter);
                callstatusadapter.notifyDataSetChanged();
                new Handler(Looper.getMainLooper()).postDelayed(() -> binding.viewCalerderLayout.llNextMonth.setEnabled(true), 500);
                break;

            case R.id.ll_bfr_month:
                binding.viewCalerderLayout.llBfrMonth.setEnabled(false);
                calendarDays.clear();
                if (selectedDate == null) {
                    selectedDate = LocalDate.now();
                }
                selectedDate = selectedDate.minusMonths(1);
                validateMonth(selectedDate);
                binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
                calendarDays = daysInMonthArray(selectedDate);
                callstatusadapter = new Callstatusadapter(calendarDays, HomeDashBoard.this, selectedDate);
                binding.viewCalerderLayout.calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
                binding.viewCalerderLayout.calendarRecyclerView.setAdapter(callstatusadapter);
                callstatusadapter.notifyDataSetChanged();
                new Handler(Looper.getMainLooper()).postDelayed(() -> binding.viewCalerderLayout.llBfrMonth.setEnabled(true), 500);
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
                if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
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
                if (!HomeDashBoard.this.isFinishing() && !HomeDashBoard.this.isDestroyed()) {
                    showPopup(binding.imgAccount);
                }
                break;

            case R.id.cancel_img:
                binding.drMainlayout.closeDrawer(GravityCompat.END);
                break;
        }
    }

    private void validateMonth(LocalDate date) {
        if (LocalDate.now().getMonth().getValue() == JoiningMonth && LocalDate.now().getYear() == JoiningYear) {
            binding.viewCalerderLayout.llNextMonth.setVisibility(View.INVISIBLE);
            binding.viewCalerderLayout.llBfrMonth.setVisibility(View.INVISIBLE);
        } else if (LocalDate.now().minusMonths(1).getMonth().getValue() == JoiningMonth && LocalDate.now().minusMonths(1).getYear() == JoiningYear) {
            if (date.getMonth() == LocalDate.now().getMonth()) {
                binding.viewCalerderLayout.llNextMonth.setVisibility(View.INVISIBLE);
                binding.viewCalerderLayout.llBfrMonth.setVisibility(View.VISIBLE);
            } else if (date.getMonth() == LocalDate.now().minusMonths(1).getMonth()) {
                binding.viewCalerderLayout.llNextMonth.setVisibility(View.VISIBLE);
                binding.viewCalerderLayout.llBfrMonth.setVisibility(View.INVISIBLE);
            } else {
                binding.viewCalerderLayout.llNextMonth.setVisibility(View.VISIBLE);
                binding.viewCalerderLayout.llBfrMonth.setVisibility(View.VISIBLE);
            }
        } else {
            if (date.getMonth() == LocalDate.now().getMonth()) {
                binding.viewCalerderLayout.llNextMonth.setVisibility(View.INVISIBLE);
                binding.viewCalerderLayout.llBfrMonth.setVisibility(View.VISIBLE);
            } else if (date.getMonth() == LocalDate.now().minusMonths(1).getMonth()) {
                binding.viewCalerderLayout.llNextMonth.setVisibility(View.VISIBLE);
                binding.viewCalerderLayout.llBfrMonth.setVisibility(View.VISIBLE);
            } else {
                binding.viewCalerderLayout.llNextMonth.setVisibility(View.VISIBLE);
                binding.viewCalerderLayout.llBfrMonth.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void setUpCalendar() {
//        binding.viewCalerderLayout.calendarProgressBar.setVisibility(View.VISIBLE);
//        if(SequentialEntry.equalsIgnoreCase("0")) {
//            commonUtilsMethods.showToastMessage(this, getString(R.string.sequential_entry_cannot_change_date));
//        } else if(!SharedPref.getDayPlanStartedDate(this).isEmpty() && MyDayPlanEntriesNeeded.datesNeeded.contains(SharedPref.getDayPlanStartedDate(this))) {
//            commonUtilsMethods.showToastMessage(this, getString(R.string.complete_day));
//        }
//                else if(HomeDashBoard.selectedDate != null){
//                    commonUtilsMethods.showToastMessage(this, getString(R.string.complete_day));
//                }
//        else
//        if(!MyDayPlanEntriesNeeded.datesNeeded.isEmpty() || !SharedPref.getSelectedDateCal(this).isEmpty()) {
        SetUpHolidayWeekEndData();
        if (isDateSelectionClicked) {
//                getCallsDataToCalender();
            if (selectedDate == null) {
                selectedDate = LocalDate.now();
            }
            binding.viewCalerderLayout.monthYearTV.setText(monthYearFromDate(selectedDate));
            validateMonth(selectedDate);
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
            if (!binding.textDate.getText().toString().trim().isEmpty()) {
                selectedDate = LocalDate.parse(binding.textDate.getText().toString().trim(), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_27, Locale.ENGLISH));
            }
            binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
            //  binding.tabLayout.getRoot().setVisibility(View.VISIBLE);
            binding.tabLayout.setVisibility(View.VISIBLE);
            if (SharedPref.getTpdcrMgrappr(this).equalsIgnoreCase("0")) {
                binding.viewCalerderLayout.txtTpDeviation.setVisibility(View.VISIBLE);
                binding.viewCalerderLayout.txtTpDeviationRele.setVisibility(View.VISIBLE);
            } else {
                binding.viewCalerderLayout.txtTpDeviation.setVisibility(View.GONE);
                binding.viewCalerderLayout.txtTpDeviationRele.setVisibility(View.GONE);
            }
            binding.viewPager.setVisibility(View.VISIBLE);
        }
//        }
//        else {
//            commonUtilsMethods.showToastMessage(this, "No pending dates to select");
//        }
        binding.viewCalerderLayout.calendarProgressBar.setVisibility(View.GONE);
    }

    private void callAPIDateSync() {
        if (UtilityClass.isNetworkAvailable(this)) {
//        progressDialog = CommonUtilsMethods.createProgressDialog(this);
            JSONObject jj = CommonUtilsMethods.CommonObjectParameter(this);
            try {
                jj.put("tableName", "getdcrdate");
                jj.put("sfcode", SharedPref.getSfCode(this));
                jj.put("division_code", SharedPref.getDivisionCode(this));
                jj.put("Rsf", SharedPref.getHqCode(this));
                Log.d("object", jj.toString());
            } catch (Exception ignored) {
            }

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "home");
            Call<JsonElement> callSyncDate = apiInterface.getJSONElement(SharedPref.getCallApiUrl(HomeDashBoard.this), mapString, jj.toString());

            callSyncDate.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        try {
                            JsonElement jsonElement = response.body();
                            assert jsonElement != null;
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, jsonArray.toString(), 2));
                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC_DUP, jsonArray.toString(), 2));
                            checkAndSetEntryDate(HomeDashBoard.this, true);
//                        binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
//                        binding.tabLayout.setVisibility(View.VISIBLE);
//                        binding.viewPager.setVisibility(View.VISIBLE);
                            commonUtilsMethods.showToastMessage(HomeDashBoard.this, HomeDashBoard.this.getString(R.string.synced_successfully));
//                        progressDialog.dismiss();
                            setUpCalendar();
                        } catch (Exception ignored) {
//                        progressDialog.dismiss();
                            binding.viewCalerderLayout.calendarProgressBar.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//                progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.no_network));
                    binding.viewCalerderLayout.calendarProgressBar.setVisibility(View.GONE);
                }
            });
        } else {
            commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
            binding.viewCalerderLayout.calendarProgressBar.setVisibility(View.GONE);
        }
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
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
        binding.navView.getLayoutDirection();
        menu.findItem(R.id.remaindercall).setTitle(SharedPref.getRemainderCallCap(this));

        if (SharedPref.getTpNeed(this).equalsIgnoreCase("0")) {
            menu.findItem(R.id.tp).setVisible(true);
        } else {
            menu.findItem(R.id.tp).setVisible(false);
        }

        if (SharedPref.getFaq(this).equalsIgnoreCase("0")) {
            menu.findItem(R.id.faq).setVisible(true);
        } else {
            menu.findItem(R.id.faq).setVisible(false);
        }

        if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
            menu.findItem(R.id.nearme).setVisible(true);
            menu.findItem(R.id.loctionrefresh).setVisible(true);
        } else {
            menu.findItem(R.id.loctionrefresh).setVisible(false);
            menu.findItem(R.id.nearme).setVisible(false);
        }

        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
            menu.findItem(R.id.approval).setVisible(SharedPref.getApproveNeed(this).equalsIgnoreCase("0"));
            menu.findItem(R.id.stp).setVisible(false);
        } else {
            menu.findItem(R.id.approval).setVisible(false);
            menu.findItem(R.id.stp).setVisible(SharedPref.getStpNeed(this).equalsIgnoreCase("0"));
            if (SharedPref.getStpCaption(this).isEmpty()) {
                menu.findItem(R.id.stp).setTitle(Constants.STANDARD_TOUR_PLAN);
            } else {
                menu.findItem(R.id.stp).setTitle(SharedPref.getStpCaption(this));
            }
        }

        if (SharedPref.getActivityNd(this).equalsIgnoreCase("0")) {
            menu.findItem(R.id.activity).setVisible(true);
            menu.findItem(R.id.activity).setTitle(SharedPref.getActivityCap(HomeDashBoard.this));
        } else {
            menu.findItem(R.id.activity).setVisible(false);
        }

        if (SharedPref.getQuizNeed(this).equalsIgnoreCase("0")) {
            menu.findItem(R.id.quiz).setVisible(true);
            menu.findItem(R.id.quiz).setTitle(SharedPref.getQuizHeading(HomeDashBoard.this));
        } else {
            menu.findItem(R.id.quiz).setVisible(false);
        }

        if (SharedPref.getSurveyNd(this).equalsIgnoreCase("0")) {
            menu.findItem(R.id.survey).setVisible(true);
        } else {
            menu.findItem(R.id.survey).setVisible(false);
        }

        if (SharedPref.getRmdrNeed(this).equalsIgnoreCase("0")) {
            menu.findItem(R.id.remaindercall).setVisible(true);
        } else {
            menu.findItem(R.id.remaindercall).setVisible(false);
        }

//        if (SharedPref.getDocBusinessProduct(this).equalsIgnoreCase("0")) {
//            menu.findItem(R.id.docbusinessentry).setVisible(true);
//        } else {
        if (SharedPref.getDynamicOptionNeed(HomeDashBoard.this).equalsIgnoreCase("0")) {
            if (!SharedPref.getDynamicOptionCaps(HomeDashBoard.this).equalsIgnoreCase("")) {
                menu.findItem(R.id.dyn_link).setTitle(SharedPref.getDynamicOptionCaps(HomeDashBoard.this));
                menu.findItem(R.id.dyn_link).setVisible(true);
            } else {
                menu.findItem(R.id.dyn_link).setTitle(R.string.option);
                menu.findItem(R.id.dyn_link).setVisible(true);
            }
        } else {
            menu.findItem(R.id.dyn_link).setVisible(false);
        }
        menu.findItem(R.id.docbusinessentry).setVisible(false);
//        }

        menu.findItem(R.id.form).setVisible(false);

        if (SharedPref.getDcrSequential(this).equalsIgnoreCase("0")) {
            navigationFooterBinding.sequentialDot.setVisibility(View.VISIBLE);
        } else {
            navigationFooterBinding.sequentialDot.setVisibility(View.GONE);
        }

        if (SharedPref.getTpNeed(this).equalsIgnoreCase("0")
                && SharedPref.getTpMandatoryNeed(this).equalsIgnoreCase("0")
                && SharedPref.getTpbasedDcr(this).equalsIgnoreCase("0")
                && !SharedPref.getTpStartDate(HomeDashBoard.this).equalsIgnoreCase("0") && !SharedPref.getTpStartDate(HomeDashBoard.this).equalsIgnoreCase("-1")
                && !SharedPref.getTpEndDate(HomeDashBoard.this).equalsIgnoreCase("0") && !SharedPref.getTpEndDate(HomeDashBoard.this).equalsIgnoreCase("-1")) {
            navigationFooterBinding.tvTdot.setVisibility(View.VISIBLE);
        } else {
            navigationFooterBinding.tvTdot.setVisibility(View.GONE);
        }

        if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
            navigationFooterBinding.tvLdot.setVisibility(View.VISIBLE);
            binding.imgLocation.setVisibility(View.VISIBLE);
            binding.imgLocation.setImageResource(R.drawable.location_img);
            menu.findItem(R.id.loctionrefresh).setVisible(true);
        } else {
            navigationFooterBinding.tvLdot.setVisibility(View.GONE);
            binding.imgLocation.setVisibility(View.GONE);
            binding.imgLocation.setImageResource(R.drawable.locationget_img);
            menu.findItem(R.id.loctionrefresh).setVisible(false);
        }

        if (SharedPref.getGeotagNeed(this).equalsIgnoreCase("1"))
            navigationFooterBinding.tvDdot.setVisibility(View.VISIBLE);

        if (SharedPref.getGeotagNeedChe(this).equalsIgnoreCase("1"))
            navigationFooterBinding.tvCdot.setVisibility(View.VISIBLE);

        if (SharedPref.getGeotagNeedStock(this).equalsIgnoreCase("1"))
            navigationFooterBinding.tvSdot.setVisibility(View.VISIBLE);

        if (SharedPref.getGeotagNeedUnlst(this).equalsIgnoreCase("1"))
            navigationFooterBinding.tvUdot.setVisibility(View.VISIBLE);

//        if (SharedPref.getGeotagNeedCip(this).equalsIgnoreCase("1"))
//            navigationFooterBinding.tvHdot.setVisibility(View.VISIBLE);


        try {
            SFDCR_Date_sp = SharedPref.getSfDCRDate(HomeDashBoard.this);
            JSONObject obj = new JSONObject(SFDCR_Date_sp);
            SFDCR_Date = obj.getString("date");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SFDCR_Date != null && !SFDCR_Date.isEmpty()) {
            JoiningDate = Integer.parseInt(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_7, SFDCR_Date));
            JoiningMonth = Integer.parseInt(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_8, SFDCR_Date));
            JoiningYear = Integer.parseInt(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_10, SFDCR_Date));
        }
    }

    public void commonFun() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void showCombinedWishesPopup() {
        Log.d("CombinedWishes", "Checking both Birthday and Anniversary...");
        try {
            RoomDB roomDB = RoomDB.getDatabase(this);
            MasterDataDao masterDataDao = roomDB.masterDataDao();

            JSONArray doctorJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d", Locale.US);
            String todayStr = outputFormat.format(new Date());
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            String birthdayMsg = "";
            String anniversaryMsg = "";

            for (int i = 0; i < doctorJsonArray.length(); i++) {
                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
                String doctorName = doctorObj.optString("Name");
                String territory = doctorObj.optString("Town_Name");

                // 🎂 Check for birthday
                JSONObject dobObject = doctorObj.optJSONObject("DctrDOB");
                if (dobObject != null) {
                    String dobDateStr = dobObject.optString("date", "").trim();
                    if (!dobDateStr.isEmpty() && !dobDateStr.startsWith("1900")) {
                        Date date = inputFormat.parse(dobDateStr.split(" ")[0]);
                        String birthDate = outputFormat.format(date);
                        if (birthDate.equalsIgnoreCase(todayStr)) {
                            int count = birthdayMsg.isEmpty() ? 1 : birthdayMsg.split("\n\n").length + 1;
                            birthdayMsg += count + ". Dr. " + doctorName + "\n• " + territory + "\n\n";
//                        if (birthDate.equalsIgnoreCase(todayStr)) {
//                           // birthdayMsg = "🎉 Wish Dr. " + doctorName + "\n" + territory;
//                            birthdayMsg += (birthdayMsg.isEmpty() ? "1" : String.valueOf(birthdayMsg.split("\n\n").length + 1))
//                                    + ". " + doctorName + "\n " + territory + "\n\n";

                            //break; // stop once found
                        }
                    }
                }
            }

            for (int i = 0; i < doctorJsonArray.length(); i++) {
                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
                String doctorName = doctorObj.optString("Name");
                String territory = doctorObj.optString("Town_Name");

                // 💐 Check for anniversary
                JSONObject dowObject = doctorObj.optJSONObject("DctrDOW");
                if (dowObject != null) {
                    String dowDateStr = dowObject.optString("date", "").trim();
                    if (!dowDateStr.isEmpty() && !dowDateStr.startsWith("1900")) {
                        Date date = inputFormat.parse(dowDateStr.split(" ")[0]);
                        String annivDate = outputFormat.format(date);
                        if (annivDate.equalsIgnoreCase(todayStr)) {
                            int count = anniversaryMsg.isEmpty() ? 1 : anniversaryMsg.split("\n\n").length + 1;
                            anniversaryMsg += count + ". Dr. " + doctorName + "\n• " + territory + "\n\n";
//                        if (annivDate.equalsIgnoreCase(todayStr)) {
//                           // anniversaryMsg = "💐 Congratulate Dr. " + doctorName + " \n " + territory;
//                            anniversaryMsg += (anniversaryMsg.isEmpty() ? "1" : String.valueOf(anniversaryMsg.split("\n\n").length + 1))
//                                    + ". " + doctorName + "\n " + territory + "\n\n";

                            // break; // stop once found
                        }
                    }
                }
            }

            // ✅ Show combined alert if any message exists
            if (!birthdayMsg.isEmpty() || !anniversaryMsg.isEmpty()) {
                SharedPref.setBirthdayShownDate(this, today);
                SharedPref.setAnniversaryShownDate(this, today);
                CommonAlertBox.ShowCombinedWishesAlert(this, birthdayMsg, anniversaryMsg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CheckedTpRange() {
        if (!SharedPref.getskipDate(HomeDashBoard.this).equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
            if (SharedPref.getTpMandatoryNeed(HomeDashBoard.this).equalsIgnoreCase("0") && SharedPref.getTpNeed(HomeDashBoard.this).equalsIgnoreCase("0") &&
                    !SharedPref.getTpStartDate(HomeDashBoard.this).equalsIgnoreCase("0") && !SharedPref.getTpStartDate(HomeDashBoard.this).equalsIgnoreCase("-1") &&
                    !SharedPref.getTpEndDate(HomeDashBoard.this).equalsIgnoreCase("0") && !SharedPref.getTpEndDate(HomeDashBoard.this).equalsIgnoreCase("-1")) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
                SimpleDateFormat date = new SimpleDateFormat("dd", Locale.ENGLISH);
                String mCurrDate = date.format(calendar.getTime());
                String currentDate = sdf.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                String nextMonthDate = sdf.format(calendar.getTime());

                String tp_start = SharedPref.getTpStartDate(HomeDashBoard.this);
                String tp_end = SharedPref.getTpEndDate(HomeDashBoard.this);
                int Start_Date = Integer.parseInt(tp_start);
                int End_Date = Integer.parseInt(tp_end);
                int mCurrentDate = Integer.parseInt(mCurrDate);


                if (tourPlanOfflineDataDao.getApprovalStatusByMonth(currentDate) != null && !tourPlanOfflineDataDao.getApprovalStatusByMonth(currentDate).equalsIgnoreCase("3")) {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.prepare_your_tourplan));
                    TourplanFlog = "0";
                    SharedPref.setTpStatus(HomeDashBoard.this, true);
                    Intent intent = new Intent(HomeDashBoard.this, TourPlanActivity.class);
                    startActivity(intent);
                } else if (tourPlanOfflineDataDao.getApprovalStatusByMonth(nextMonthDate) != null && !tourPlanOfflineDataDao.getApprovalStatusByMonth(nextMonthDate).equalsIgnoreCase("3") && ((mCurrentDate >= Start_Date))) {
                    commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.prepare_your_tourplan));
                    if (End_Date < mCurrentDate) {
                        SharedPref.setTpStatus(HomeDashBoard.this, true);
                    } else {
                        SharedPref.setTpStatus(HomeDashBoard.this, false);
                    }
                    Intent intent = new Intent(HomeDashBoard.this, TourPlanActivity.class);
                    TourplanFlog = "1";
                    startActivity(intent);
                } else {
                    SharedPref.setTpStatus(HomeDashBoard.this, false);
                }
            }
        }
    }

    private void onClickListener() {
        binding.imgLocation.setOnClickListener(new CommonUtilsMethods.DoubleClickListener() {
            @Override
            public void onDoubleClick(View view) {
                setGpsTrack();
            }
        });
    }

    private void setGpsTrack() {
        gpsTrack = new GPSTrack(HomeDashBoard.this);
        double lat = gpsTrack.getLatitude();
        double lng = gpsTrack.getLongitude();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (CommonUtilsMethods.isLocationEnabled(getApplicationContext())) {
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
        handler.removeCallbacks(updateClock);
        slidesDao.Changestatus("0", "1");

        if (notificationPopupWindow != null && notificationPopupWindow.isShowing()) {
            notificationPopupWindow.dismiss();
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    private void timeZoneVerification() {
        runnable = new Runnable() {
            public void run() {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean isAutoTimeZoneEnabled = commonUtilsMethods.isAutoTimeEnabled(HomeDashBoard.this) && commonUtilsMethods.isTimeZoneAutomatic(HomeDashBoard.this);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isAutoTimeZoneEnabled) {
                                    if (customDialog != null) {
                                        customDialog.dismiss();
                                        customDialog.cancel();
                                    }
                                } else {
                                    timeZoneVerificationDialog();
                                }
                                handler1.removeCallbacks(runnable);
                            }
                        });
                    }
                });
            }
        };
        handler1.postDelayed(runnable, delay);
    }

    private void timeZoneVerificationDialog() {
        DialogTimezoneBinding timezoneBinding = DialogTimezoneBinding.inflate(LayoutInflater.from(HomeDashBoard.this));
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeDashBoard.this, 0);
        customDialog = builder.create();
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setView(timezoneBinding.getRoot());
        customDialog.setCancelable(false);
        customDialog.show();
        timezoneBinding.btnOpenSettings.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                HomeDashBoard.this.finishAffinity();
                System.exit(0);
            }
        });
    }

    public void CheckingManatoryApprovals() {
        if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
            try {
                JSONObject jsonGetCount = CommonUtilsMethods.CommonObjectParameter(HomeDashBoard.this);
                jsonGetCount.put("tableName", "getapprovalcheck");
                jsonGetCount.put("sfcode", SharedPref.getSfCode(this));
                jsonGetCount.put("division_code", SharedPref.getDivisionCode(this));
                jsonGetCount.put("Rsf", SharedPref.getHqCode(this));
                jsonGetCount.put("dcr_approval_need", SharedPref.getDcrApprovalNeed(this));
                jsonGetCount.put("Tp_need", SharedPref.getTpNeed(this));
                jsonGetCount.put("geotag_need", SharedPref.getGeotagNeed(this));
                jsonGetCount.put("TPdev_need", SharedPref.getTpdcrMgrappr(this));
                jsonGetCount.put("STP_Need", SharedPref.getStpNeed(this));
                jsonGetCount.put("OneBuild_Need", SharedPref.getOneBuild(this));

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "get/approvals");
                Call<JsonElement> callGetCountApprovals = apiInterface.getJSONElement(SharedPref.getCallApiUrl(HomeDashBoard.this), mapString, jsonGetCount.toString());
                callGetCountApprovals.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        assert response.body() != null;
                        Log.v("counts", "-0-" + response.body());
                        if (response.isSuccessful()) {
                            try {
                                int DcrCount = 0, TpCount = 0, LeaveCount = 0, DeviationCount = 0, GeoTagCount = 0, STPCount = 0;
                                JSONObject jsonObject1 = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject1.getJSONArray("apprCount");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonCounts = jsonArray.getJSONObject(i);
                                    if (jsonCounts.has("dcrappr_count"))
                                        DcrCount = jsonCounts.getInt("dcrappr_count");
                                    if (jsonCounts.has("tpappr_count"))
                                        TpCount = jsonCounts.getInt("tpappr_count");
                                    if (jsonCounts.has("leaveappr_count"))
                                        LeaveCount = jsonCounts.getInt("leaveappr_count");
                                    if (jsonCounts.has("devappr_count"))
                                        DeviationCount = jsonCounts.getInt("devappr_count");
                                    if (jsonCounts.has("geotag_count"))
                                        GeoTagCount = jsonCounts.getInt("geotag_count");
                                    if (jsonCounts.has("stp_count"))
                                        STPCount = jsonCounts.getInt("stp_count");
                                }

                                if (DcrCount > 0 || TpCount > 0 || LeaveCount > 0 || DeviationCount > 0 || GeoTagCount > 0 || STPCount > 0) {
                                    SharedPref.setApprvalManatoryStatus(HomeDashBoard.this, true);
                                    if (!SharedPref.getApprovalskipDate(HomeDashBoard.this).equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
                                        SharedPref.setApprovalsCounts(HomeDashBoard.this, "false");
                                        Intent intent = new Intent(HomeDashBoard.this, ApprovalsActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    SharedPref.setApprvalManatoryStatus(HomeDashBoard.this, false);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SharedPref.getGeoChk(HomeDashBoard.this).equalsIgnoreCase("0")) {
            locationCheck();
            CommonAlertBox.CheckLocationStatus(HomeDashBoard.this, gpsTrack);
        }
        requestNotificationPermission();
        registerNetworkCallback();
    }

    private void locationCheck() {
        if (CommonUtilsMethods.isLocationEnabled(getApplicationContext())) {
            if (CheckLocPermission()) {
                return; // already granted
            }

            if (!isLocationPermissionRequested) {
                isLocationPermissionRequested = true;
                RequestLocationPermission();
                return;
            }
            showPermissionMandatoryDialog();
//            if (!CheckLocPermission()) {
//                RequestLocationPermission();
//            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(HomeDashBoard.this);
        }
    }

    private void RequestLocationPermission() {
        isLocationPermissionRequested = true;
        if (ContextCompat.checkSelfPermission(HomeDashBoard.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeDashBoard.this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(HomeDashBoard.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            } else {
                ActivityCompat.requestPermissions(HomeDashBoard.this, new String[]{ACCESS_FINE_LOCATION}, 101);
            }
        }
    }

    public boolean CheckLocPermission() {
        int FineLocation = ContextCompat.checkSelfPermission(HomeDashBoard.this, ACCESS_FINE_LOCATION);
        int CoarseLocation = ContextCompat.checkSelfPermission(HomeDashBoard.this, ACCESS_COARSE_LOCATION);
        return FineLocation == PackageManager.PERMISSION_GRANTED && CoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (locationDialog != null) {
                    locationDialog.dismiss();
                }
                requestNotificationPermission();
                return;
            }
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Permanently denied
                showGoToSettingsDialog();
            } else {
                // Denied normally
                showPermissionMandatoryDialog();
            }
        }
    }

    private void showPermissionMandatoryDialog() {
        if (locationDialog != null) {
            locationDialog.dismiss();
        }
        locationDialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Location Required")
                .setMessage("Location permission required. Please allow it.")
                .setCancelable(false)
                .setPositiveButton("Allow", (dialog, which) -> RequestLocationPermission()).create();

//                .setNegativeButton("Back", (dialog, which) -> finish())
        locationDialog.show();
    }

    private void showGoToSettingsDialog() {
        if (locationDialog != null) {
            locationDialog.dismiss();
        }
        locationDialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("Location permission is permanently denied. Please enable it in App Settings.")
                .setCancelable(false)
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }).create();
//                .setNegativeButton("Back", (dialog, which) -> finish())
        locationDialog.show();
    }

    public boolean CheckCameraPermission() {
        int Camera = ContextCompat.checkSelfPermission(HomeDashBoard.this, CAMERA);
        return Camera != PackageManager.PERMISSION_GRANTED;
    }

    private void RequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(HomeDashBoard.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeDashBoard.this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(HomeDashBoard.this, new String[]{Manifest.permission.CAMERA}, 102);
            } else {
                ActivityCompat.requestPermissions(HomeDashBoard.this, new String[]{Manifest.permission.CAMERA}, 102);
            }
        }
    }

    private void accessibility() {
        JSONArray input = masterDataDao.getMasterDataTableOrNew(Constants.SETUP).getMasterSyncDataJsonArray();
        for (int bean = 0; bean < input.length(); bean++) {
            try {
                JSONObject setUpObject = input.getJSONObject(bean);
                String appAccess = setUpObject.getString("sanzen_edet");
                if (!appAccess.equals("1")) {
                    CommonUtilsMethods.accessDialogBox(this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!SharedPref.getAppAccess(this).equals("1")) {
            CommonUtilsMethods.accessDialogBox(this);
        }
    }

    public void showDoctorPlanPopup(String tpDoctor, boolean isFromTP) {
        try {
            if (SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("2")  || tpDoctor == null){
                Log.d("Tag","No Popup for MGR(Show doc popup");
                return;
            }
                if (!isFromTP) {
                    Log.e("DoctorPopup", "Popup skipped because isFromTP=false");
                    return;
                }

                RoomDB roomDB = RoomDB.getDatabase(this);
                MasterDataDao masterDataDao = roomDB.masterDataDao();
                JSONArray doctorMasArray = masterDataDao
                        .getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(this))
                        .getMasterSyncDataJsonArray();

                Log.e("DoctorPopup", "Doctor Master count: " + doctorMasArray.length());
                List<String> plannedDoctorCodes = new ArrayList<>();
                if (tpDoctor != null && !tpDoctor.isEmpty()) {
                    for (String code : tpDoctor.split(",")) {
                        if (!code.trim().isEmpty()) {
                            plannedDoctorCodes.add(code.trim());
                        }
                    }
                }
                Log.e("DoctorPopup", "PlannedDoctorCodes => " + plannedDoctorCodes);
                List<String> matchedDoctors = new ArrayList<>();
                for (int i = 0; i < doctorMasArray.length(); i++) {
                    JSONObject doc = doctorMasArray.getJSONObject(i);
                    String docCode = doc.optString("Code", "").trim();

                    if (plannedDoctorCodes.contains(docCode)) {
                        String docName = doc.optString("Name", "Unknown Doctor");
                        matchedDoctors.add("Dr. " + docName);
                        Log.e("DoctorPopup", "Matched Doctor: " + docName + " (" + docCode + ")");
                    }
                }
                if (!matchedDoctors.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    for (int i = 0; i < matchedDoctors.size(); i++) {
                        message.append(i + 1).append(". ").append(matchedDoctors.get(i)).append("\n");
                    }
//            for (String entry : matchedDoctors) {
//                message.append(entry).append("\n");
//            }
                    CommonAlertBox.DoctorPlanPopup(this, message.toString().trim());

                } else {
                    Log.e("DoctorPopup", "No matching doctors found for today.");
                }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DoctorPopup", "Error: " + e.getMessage());
        }
    }

    private void showNotVisitedDoctorsPopup(String tpDoctorCodes, boolean isImmediatePopup, JSONArray doctorMasArray) {
        try {
            if (SharedPref.getSfType(HomeDashBoard.this).equalsIgnoreCase("2") || tpDoctorCodes == null || tpDoctorCodes.isEmpty()) return;

            // ✅ Skip normal reminder check if immediate
          /*  if (!isImmediatePopup) {
                String remainderTime = SharedPref.getDoctorRemainingShownDate(this);
                if (remainderTime != null && !remainderTime.isEmpty()) {
                    String[] parts = remainderTime.split(":");
                    int reminderHour = Integer.parseInt(parts[0]);
                    int reminderMinute = Integer.parseInt(parts[1]);
                    Calendar now = Calendar.getInstance();
                    int currentHour = now.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = now.get(Calendar.MINUTE);
                    if (currentHour > reminderHour || (currentHour == reminderHour && currentMinute > reminderMinute)) {
                        return;
                    }
                }
            }*/

            // ✅ Prepare planned doctors
            List<String> plannedDoctorCodes = new ArrayList<>();
            for (String code : tpDoctorCodes.split(",")) {
                code = code.trim();
                if (!code.isEmpty()) plannedDoctorCodes.add(code);
            }
            if (plannedDoctorCodes.isEmpty()) return;

            RoomDB roomDB = RoomDB.getDatabase(this);
            MasterDataDao masterDataDao = roomDB.masterDataDao();

            // ✅ Determine visited doctors
            Set<String> visitedDoctors = new HashSet<>();
            JSONArray jsonArray_call = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            String selectedDateStr = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            for (int i = 0; i < jsonArray_call.length(); i++) {
                JSONObject obj = jsonArray_call.getJSONObject(i);
                String custCode = obj.optString("CustCode", "").trim();
                String callDate = obj.optString("Dcr_dt", "").trim();
                if (!custCode.isEmpty() && plannedDoctorCodes.contains(custCode) && selectedDateStr.equals(callDate)) {
                    visitedDoctors.add(custCode);
                    // SharedPref.addVisitedDoctor(this, custCode);
                }
            }

            // ✅ Map codes → names
            Map<String, String> doctorNameMap = new HashMap<>();
            for (int i = 0; i < doctorMasArray.length(); i++) {
                JSONObject obj = doctorMasArray.getJSONObject(i);
                String code = obj.optString("Code", "").trim();
                String name = obj.optString("Name", "").trim();
                if (!code.isEmpty() && !name.isEmpty()) doctorNameMap.put(code, name);
            }

            // ✅ Not visited doctors
            List<String> notVisitedCodes = new ArrayList<>();
            for (String code : plannedDoctorCodes) {
                if (!visitedDoctors.contains(code)) notVisitedCodes.add(code);
            }

            // ✅ Build popup message
            SpannableStringBuilder msg = new SpannableStringBuilder();
            msg.append(new SpannableString("Visited Doctors:\n") {{
                setSpan(new StyleSpan(Typeface.BOLD), 0, length(), 0);
                setSpan(new ForegroundColorSpan(Color.BLACK), 0, length(), 0);
            }});
            if (visitedDoctors.isEmpty()) msg.append("None\n");
            else {
                int i = 1;
                for (String code : visitedDoctors) {
                    String name = doctorNameMap.getOrDefault(code, code);
                    msg.append(String.valueOf(i++)).append(" . Dr . ").append(name).append("\n");
                }
            }

            msg.append("\n");
            msg.append(new SpannableString("Not Visited Doctors:\n") {{
                setSpan(new StyleSpan(Typeface.BOLD), 0, length(), 0);
                setSpan(new ForegroundColorSpan(Color.BLACK), 0, length(), 0);
            }});
            if (notVisitedCodes.isEmpty()) msg.append("None\n");
            else {
                int i = 1;
                for (String code : notVisitedCodes) {
                    String name = doctorNameMap.getOrDefault(code, code);
                    msg.append(String.valueOf(i++)).append(" . Dr . ").append(name).append("\n");
                }
            }

            // ✅ Show popup
            CommonAlertBox.DoctorPlanPopup(this, msg);

            // ✅ Mark popup shown today (for normal flow)
//            if (!isImmediatePopup) SharedPref.setDoctorRemainingShownDate(this, " ");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PopupError", e.getMessage());
        }
    }

    BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String type = intent.getStringExtra("type");
                commonUtilsMethods.showToastMessage(HomeDashBoard.this, getString(R.string.sync_completed));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}





