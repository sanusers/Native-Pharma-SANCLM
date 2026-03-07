package saneforce.sanzen.activity.tourPlan;

import static saneforce.sanzen.activity.tourPlan.session.SessionEditAdapter.inputDataArray;
import static saneforce.sanzen.activity.tourPlan.session.SessionEditAdapter.inputDataArrayOneBuild;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
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
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.StandardTourPlanActivity;
import saneforce.sanzen.activity.tourPlan.calendar.CalendarAdapter;
import saneforce.sanzen.activity.tourPlan.model.DoctorDataModel;
import saneforce.sanzen.activity.tourPlan.model.DoctorVisitModel;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQHeaderModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQItemModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.activity.tourPlan.model.ReceiveModel;
import saneforce.sanzen.activity.tourPlan.overview.TourPlanOverviewActivity;
import saneforce.sanzen.activity.tourPlan.session.SessionEditAdapter;
import saneforce.sanzen.activity.tourPlan.session.SessionInterface;
import saneforce.sanzen.activity.tourPlan.session.SessionInterfaceOneBuild;
import saneforce.sanzen.activity.tourPlan.session.SessionViewAdapter;
import saneforce.sanzen.activity.tourPlan.summary.SummaryAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityTourPlanBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.sanzen.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataTable;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class TourPlanActivity extends AppCompatActivity {
    public static LinearLayout addSaveBtnLayout, clrSaveBtnLayout;
    ApiInterface apiInterface;
    private RoomDB roomDB;
    private TourPlanOfflineDataDao tourPlanOfflineDataDao;
    private TourPlanOnlineDataDao tourPlanOnlineDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private MasterDataDao masterDataDao;
    CalendarAdapter calendarAdapter = new CalendarAdapter();
    SummaryAdapter summaryAdapter = new SummaryAdapter();
    SessionEditAdapter sessionEditAdapter = new SessionEditAdapter();
    SessionViewAdapter sessionViewAdapter = new SessionViewAdapter();
    ArrayList<ModelClass> dayWiseArrayCurrentMonth = new ArrayList<>();
    ArrayList<ModelClass> dayWiseArrayPrevMonth = new ArrayList<>();
    ArrayList<ModelClass> dayWiseArrayNextMonth = new ArrayList<>();
    private ArrayList<ModelClass> modelClassList = new ArrayList<>();
    //OneBuild
    public ArrayList<OneBuildModelClass> dayWiseArrayCurrentMonthOneBuild = new ArrayList<>();
    public ArrayList<OneBuildModelClass> dayWiseArrayPreviousMonthOneBuild = new ArrayList<>();
    public ArrayList<OneBuildModelClass> dayWiseArrayNextMonthOneBuild = new ArrayList<>();
    private ArrayList<OneBuildModelClass> oneBuildModelClassList = new ArrayList<>();

    private Map<String, String> doctorMap = new HashMap<>();
    public static Map<String, DoctorDataModel> doctorDataMap = new HashMap<>();
    public static Map<String, DoctorVisitModel> doctorVisitMap = new HashMap<>();

    ArrayList<String> weeklyOffDays = new ArrayList<>();
    JSONArray holidayJSONArray = new JSONArray();

//    List<ArrayList> draftDates = new ArrayList<>();

    ModelClass.SessionList.WorkType weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType();
    ModelClass.SessionList.WorkType holidayWorkTypeModel = new ModelClass.SessionList.WorkType();

    //OneBuild
    OneBuildModelClass.SessionList.WorkType weeklyOffWorkTypeModelOneBuild = new OneBuildModelClass.SessionList.WorkType();
    OneBuildModelClass.SessionList.WorkType holidayWorkTypeModelOneBuild = new OneBuildModelClass.SessionList.WorkType();

    LocalDate localDate;
    String drNeed = "", chemistNeed = "", jwNeed = "", stockiestNeed = "", unListedDrNeed = "", cipNeed = "", hospNeed = "", minDrCount = "", maxDrCount = "", addSessionNeed = "", addSessionCountLimit = "", FW_meetup_mandatory = "", holidayMode = "", weeklyOffCaption = "", holidayEditable = "", weeklyOffEditable = "", remarksNeed = "", planAllDr = "", visitFrequencyNeed = "", minimumGap = "";
    private String drCap, chmCap, stkCap, unListDrCap, cipCap, hosCap, masters;
    int monthInAdapterFlag = 0; // 0 -> current month , 1 -> next month , -1 -> previous month
    boolean isDataAvailable, isEdited;
    CommonUtilsMethods commonUtilsMethods;
    public static ActivityTourPlanBinding binding;
    public static String SFTP_Date_sp = "", SFTP_Date = "";
    public static int JoningDate, JoiningMonth, JoinYear;
    private boolean isSTPBasedTP;
    String isFrom = "";
    JsonObject jbonj = new JsonObject();
    String isNameClicked = "";
    String changeStatus = "";

    public static ModelClass.SessionList prepareSessionListForAdapter(ArrayList<ModelClass.SessionList.SubClass> clusterArray, ArrayList<ModelClass.SessionList.SubClass> jcArray, ArrayList<ModelClass.SessionList.SubClass> drArray, ArrayList<ModelClass.SessionList.SubClass> chemistArray, ArrayList<ModelClass.SessionList.SubClass> stockArray, ArrayList<ModelClass.SessionList.SubClass> unListedDrArray, ArrayList<ModelClass.SessionList.SubClass> cipArray, ArrayList<ModelClass.SessionList.SubClass> hospArray, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass hq, String remarks) {
        return new ModelClass.SessionList("", true, remarks, workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public static ModelClass.SessionList prepareSessionListForAdapter(ArrayList<ModelClass.SessionList.SubClass> clusterArray, ArrayList<ModelClass.SessionList.SubClass> jcArray, ArrayList<ModelClass.SessionList.SubClass> drArray, ArrayList<ModelClass.SessionList.SubClass> chemistArray, ArrayList<ModelClass.SessionList.SubClass> stockArray, ArrayList<ModelClass.SessionList.SubClass> unListedDrArray, ArrayList<ModelClass.SessionList.SubClass> cipArray, ArrayList<ModelClass.SessionList.SubClass> hospArray, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass hq, ArrayList<ModelClass.SessionList.SubClass> hqs, ArrayList<MultiHQHeaderModelClass> clusters, ArrayList<MultiHQHeaderModelClass> JCs, ArrayList<MultiHQHeaderModelClass> listedDrs, ArrayList<MultiHQHeaderModelClass> chemists, ArrayList<MultiHQHeaderModelClass> stockiests, ArrayList<MultiHQHeaderModelClass> unListedDrs, ArrayList<MultiHQHeaderModelClass> cips, ArrayList<MultiHQHeaderModelClass> hospitals, String remarks) {
        return new ModelClass.SessionList("", true, remarks, workType, hq, hqs, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray, clusters, JCs, listedDrs, chemists, stockiests, unListedDrs, cips, hospitals);
    }

    public static ModelClass.SessionList prepareSessionListForAdapter() {
        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType("", "", "", "");
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("", "");
        ArrayList<ModelClass.SessionList.SubClass> hqArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        ArrayList<MultiHQHeaderModelClass> clustersArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> jcsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> drsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> chemistsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> stocksArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> unListedDrsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> cipsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> hospsArray = new ArrayList<>();

        return new ModelClass.SessionList("", true, "", workType, hq, hqArray, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray, clustersArray, jcsArray, drsArray, chemistsArray, stocksArray, unListedDrsArray, cipsArray, hospsArray);
    }

    public static OneBuildModelClass.SessionList prepareSessionListForAdapterOneBuild(ArrayList<OneBuildModelClass.SessionList.SubClass> clusterArray, ArrayList<OneBuildModelClass.SessionList.SubClass> jcArray, ArrayList<OneBuildModelClass.SessionList.SubClass> drArray, ArrayList<OneBuildModelClass.SessionList.SubClass> chemistArray, ArrayList<OneBuildModelClass.SessionList.SubClass> stockArray, ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrArray, ArrayList<OneBuildModelClass.SessionList.SubClass> cipArray, ArrayList<OneBuildModelClass.SessionList.SubClass> hospArray, OneBuildModelClass.SessionList.WorkType workType, OneBuildModelClass.SessionList.SubClass headquarters, String remarks) {
        return new OneBuildModelClass.SessionList("", true, remarks, "", "", "", workType, headquarters, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public static OneBuildModelClass.SessionList prepareSessionListForAdapterOneBuild() {
        OneBuildModelClass.SessionList.WorkType workType = new OneBuildModelClass.SessionList.WorkType("", "", "", "");
        OneBuildModelClass.SessionList.SubClass headquarters = new OneBuildModelClass.SessionList.SubClass("", "");
        ArrayList<OneBuildModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        return new OneBuildModelClass.SessionList("", true, "", "", "", "", workType, headquarters, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public static ModelClass.SessionList prepareSessionListForAdapter1(ArrayList<ModelClass.SessionList.SubClass> clusterArray, ArrayList<ModelClass.SessionList.SubClass> jcArray, ArrayList<ModelClass.SessionList.SubClass> drArray, ArrayList<ModelClass.SessionList.SubClass> chemistArray, ArrayList<ModelClass.SessionList.SubClass> stockArray, ArrayList<ModelClass.SessionList.SubClass> unListedDrArray, ArrayList<ModelClass.SessionList.SubClass> cipArray, ArrayList<ModelClass.SessionList.SubClass> hospArray, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass hq) {
        return new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

//    public static ModelClass.SessionList prepareSessionListForAdapter1(ArrayList<ModelClass.SessionList.SubClass> clusterArray, ArrayList<ModelClass.SessionList.SubClass> jcArray, ArrayList<ModelClass.SessionList.SubClass> drArray, ArrayList<ModelClass.SessionList.SubClass> chemistArray, ArrayList<ModelClass.SessionList.SubClass> stockArray, ArrayList<ModelClass.SessionList.SubClass> unListedDrArray, ArrayList<ModelClass.SessionList.SubClass> cipArray, ArrayList<ModelClass.SessionList.SubClass> hospArray, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass hq) {
//        return new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
//    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourPlanBinding.inflate(getLayoutInflater());

        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        roomDB = RoomDB.getDatabase(getApplicationContext());
        tourPlanOfflineDataDao = roomDB.tourPlanOfflineDataDao();
        tourPlanOnlineDataDao = roomDB.tourPlanOnlineDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        masterDataDao = roomDB.masterDataDao();
        String status;
        if (SharedPref.getOneBuild(this).equalsIgnoreCase("0")) {
            status = "2";
        } else {
            status = "0";
        }
//        if(SharedPref.getSfType(this).equalsIgnoreCase("1") && SharedPref.getStpNeed(TourPlanActivity.this).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(TourPlanActivity.this).equalsIgnoreCase("0") && (SharedPref.getStpStatus(TourPlanActivity.this).isEmpty() || SharedPref.getStpStatus(TourPlanActivity.this).equalsIgnoreCase("Planning...") || SharedPref.getStpStatus(TourPlanActivity.this).equalsIgnoreCase("Rejected"))) {
        if (SharedPref.getSfType(this).equalsIgnoreCase("1")
                && SharedPref.getStpNeed(TourPlanActivity.this).equalsIgnoreCase("0")
                && SharedPref.getStpBasedMtp(TourPlanActivity.this).equalsIgnoreCase("0")
                && (stpOfflineDataDao.isNotApproved(status) || masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() == 0)
                && SharedPref.getTpMandatoryNeed(this).equalsIgnoreCase("0") && SharedPref.getTpNeed(this).equalsIgnoreCase("0")
                && !SharedPref.getTpStartDate(this).equalsIgnoreCase("0") && !SharedPref.getTpStartDate(this).equalsIgnoreCase("-1")
                && !SharedPref.getTpEndDate(this).equalsIgnoreCase("0") && !SharedPref.getTpEndDate(this).equalsIgnoreCase("-1")) {
            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.prepare)+" " + SharedPref.getStpCaption(this)+" " + getString(R.string.and_get_approved_to_prepare_tour_plan));
            Intent intent = new Intent(getApplicationContext(), StandardTourPlanActivity.class);
            startActivity(intent);
            finish();
        } else if (SharedPref.getSfType(this).equalsIgnoreCase("1") && SharedPref.getStpNeed(TourPlanActivity.this).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(TourPlanActivity.this).equalsIgnoreCase("0") && (SharedPref.getStpStatus(TourPlanActivity.this).isEmpty() || SharedPref.getStpStatus(TourPlanActivity.this).equalsIgnoreCase("Waiting for approval"))) {
            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.standard_tour_plan_must_be_approved_to_enter_tour_plan));
            finish();
        }
//        else if(SharedPref.getSfType(this).equalsIgnoreCase("1") && SharedPref.getStpNeed(TourPlanActivity.this).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(TourPlanActivity.this).equalsIgnoreCase("0") && (SharedPref.getStpStatus(TourPlanActivity.this).isEmpty() || SharedPref.getStpStatus(TourPlanActivity.this).equalsIgnoreCase("Waiting for approval"))) {
//            commonUtilsMethods.showToastMessage(TourPlanActivity.this, "Standard Tour Plan must be approved to enter Tour Plan");
//            Intent intent = new Intent(getApplicationContext(), StandardTourPlanActivity.class);
//            startActivity(intent);
//            finish();
//        }

        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        addSaveBtnLayout = binding.tpNavigation.addSaveLayout;
        clrSaveBtnLayout = binding.tpNavigation.clrSaveBtnLayout;
        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            checkTpApiStatusOneBuild();
        } else {
            checkTpApiStaus();
        }
        try {
            SFTP_Date_sp = SharedPref.getSftpDate(TourPlanActivity.this);
            JSONObject obj = new JSONObject(SFTP_Date_sp);
            SFTP_Date = obj.getString("date");
        } catch (Exception e) {
            e.printStackTrace();
        }

        isEdited = false;
        JoningDate = Integer.valueOf(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_1, TimeUtils.FORMAT_7, SFTP_Date));
        JoiningMonth = Integer.valueOf(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_1, TimeUtils.FORMAT_8, SFTP_Date));
        JoinYear = Integer.valueOf(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_1, TimeUtils.FORMAT_10, SFTP_Date));
        isSTPBasedTP = SharedPref.getStpNeed(TourPlanActivity.this).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(TourPlanActivity.this).equalsIgnoreCase("0");

        localDate = LocalDate.now();
        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            uiInitializationOneBuild();
        } else {
            uiInitialization();
        }
        getDoctorData();
        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            if (HomeDashBoard.TourplanFlog.equalsIgnoreCase("1")) {
                binding.calendarPrevButton.setEnabled(true);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_black, null));
                localDate = localDate.plusMonths(1);
                if (LocalDate.now().plusMonths(1).isEqual(localDate)) {
                    binding.calendarNextButton.setEnabled(false);
                    binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
                }
                if (localDate.getMonthValue() == JoiningMonth && localDate.getYear() == JoinYear) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                }
                monthInAdapterFlag = 1;
                if (dayWiseArrayNextMonthOneBuild.size() == 0) {
                    dayWiseArrayNextMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                }
                populateCalenderAdapterOneBuild(dayWiseArrayNextMonthOneBuild);
            } else {
                if (localDate.getMonthValue() == JoiningMonth && localDate.getYear() == JoinYear) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                }
                dayWiseArrayCurrentMonthOneBuild.clear();
                dayWiseArrayCurrentMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                populateCalenderAdapterOneBuild(dayWiseArrayCurrentMonthOneBuild);
            }
        } else {
            if (HomeDashBoard.TourplanFlog.equalsIgnoreCase("1")) {
                binding.calendarPrevButton.setEnabled(true);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_black, null));
                localDate = localDate.plusMonths(1);
                if (LocalDate.now().plusMonths(1).isEqual(localDate)) {
                    binding.calendarNextButton.setEnabled(false);
                    binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
                }
                if (localDate.getMonthValue() == JoiningMonth && localDate.getYear() == JoinYear) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                }
                monthInAdapterFlag = 1;
                if (dayWiseArrayNextMonth.size() == 0) {
                    dayWiseArrayNextMonth = prepareModelClassForMonth(localDate);
                }
                populateCalendarAdapter(dayWiseArrayNextMonth);
            } else {
                if (localDate.getMonthValue() == JoiningMonth && localDate.getYear() == JoinYear) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                }
                dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                populateCalendarAdapter(dayWiseArrayCurrentMonth);
            }
        }

        binding.tpDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.tpDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                binding.tpDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                binding.tpNavigation.clrSaveBtnLayout.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                UtilityClass.hideKeyboard(TourPlanActivity.this);
            }
        });

        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            binding.tvSync.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
//                handler.post(runnable);
                    NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
                        //                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void isNetworkAvailable(Boolean status) {
                            if (status) {
                                isFrom = getString(R.string.sync);
                                binding.tvSync.setEnabled(true);
                                binding.progressBar.setVisibility(View.VISIBLE);
                                LocalDate localDate1 = LocalDate.now();
                                if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDateUI(localDate1.minusMonths(1)))) {
                                    getDraftSaveOneBuild("previous", monthYearFromDateUI(localDate1.minusMonths(1)), dayWiseArrayPreviousMonthOneBuild, isFrom, status);
                                } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDateUI(localDate1))) {
                                    getDraftSaveOneBuild("current", monthYearFromDateUI(localDate1), dayWiseArrayCurrentMonthOneBuild, isFrom, status);
                                } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDateUI(localDate1.plusMonths(1)))) {
                                    getDraftSaveOneBuild("next", monthYearFromDateUI(localDate1.plusMonths(1)), dayWiseArrayNextMonthOneBuild, isFrom, status);
                                }
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
                            }
                        }
                    });
                    networkStatusTask.execute();
                }
            });
        } else {
            binding.tvSync.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
                        @Override
                        public void isNetworkAvailable(Boolean status) {
                            if (status) {
                                binding.tvSync.setEnabled(false);
                                binding.progressBar.setVisibility(View.VISIBLE);

                                LocalDate localDate1 = LocalDate.now();
                                if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDateUI(localDate1.minusMonths(1)))) {
                                    get3MonthRemoteTPData("previous");
                                } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDateUI(localDate1))) {
                                    get3MonthRemoteTPData("current");
                                } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDateUI(localDate1.plusMonths(1)))) {
                                    get3MonthRemoteTPData("next");
                                }
                            } else {
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
                            }
                        }
                    });
                    networkStatusTask.execute();
                }
            });
        }

        binding.backArrow.setOnClickListener(view -> {
            if (SharedPref.getTpMandatoryNeed(TourPlanActivity.this).equalsIgnoreCase("0") && SharedPref.getTpNeed(TourPlanActivity.this).equalsIgnoreCase("0") &&
                    !SharedPref.getTpStartDate(TourPlanActivity.this).equalsIgnoreCase("0") && !SharedPref.getTpStartDate(TourPlanActivity.this).equalsIgnoreCase("-1") &&
                    !SharedPref.getTpEndDate(TourPlanActivity.this).equalsIgnoreCase("0") && !SharedPref.getTpEndDate(TourPlanActivity.this).equalsIgnoreCase("-1")) {
                SharedPref.setTpSKIPDate(TourPlanActivity.this, TimeUtils.getCurrentDateTimeTp(TimeUtils.FORMAT_4));
            }
            getOnBackPressedDispatcher().onBackPressed();
            finish();
        });
        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            binding.calendarNextButton.setOnClickListener(view -> {
                binding.calendarPrevButton.setEnabled(true);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_black, null));
                localDate = localDate.plusMonths(1).withDayOfMonth(1);
                if (LocalDate.now().plusMonths(1).withDayOfMonth(1).isEqual(localDate)) {
                    binding.calendarNextButton.setEnabled(false);
                    binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
                } else binding.calendarNextButton.setEnabled(true);

                if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                    monthInAdapterFlag = 0;
                    if (dayWiseArrayCurrentMonthOneBuild.size() == 0) {
                        dayWiseArrayCurrentMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                    }
                    oneBuildModelClassList = dayWiseArrayCurrentMonthOneBuild;
                    populateCalenderAdapterOneBuild(dayWiseArrayCurrentMonthOneBuild);
                } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().plusMonths(1).getMonth().toString())) {
                    monthInAdapterFlag = 1;
                    if (dayWiseArrayNextMonthOneBuild.size() == 0) {
                        dayWiseArrayNextMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                    }
                    dayWiseArrayNextMonthOneBuild.clear();
                    dayWiseArrayNextMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                    oneBuildModelClassList = dayWiseArrayNextMonthOneBuild;
                    populateCalenderAdapterOneBuild(dayWiseArrayNextMonthOneBuild);
                }
            });
        } else {
            binding.calendarNextButton.setOnClickListener(view -> {
                binding.calendarPrevButton.setEnabled(true);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_black, null));
                localDate = localDate.plusMonths(1).withDayOfMonth(1);
                if (LocalDate.now().plusMonths(1).withDayOfMonth(1).isEqual(localDate)) {
                    binding.calendarNextButton.setEnabled(false);
                    binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
                } else binding.calendarNextButton.setEnabled(true);

                if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                    monthInAdapterFlag = 0;
                    if (dayWiseArrayCurrentMonth.size() == 0) {
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    modelClassList = dayWiseArrayCurrentMonth;
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().plusMonths(1).getMonth().toString())) {
                    monthInAdapterFlag = 1;
                    if (dayWiseArrayNextMonth.size() == 0) {
                        dayWiseArrayNextMonth = prepareModelClassForMonth(localDate);
                    }
                    modelClassList = dayWiseArrayNextMonth;
                    populateCalendarAdapter(dayWiseArrayNextMonth);
                }
            });
        }

        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            binding.calendarPrevButton.setOnClickListener(view -> {
                binding.calendarNextButton.setEnabled(true);
                binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_black, null));
                localDate = localDate.minusMonths(1).withDayOfMonth(1);
                if (LocalDate.now().minusMonths(1).withDayOfMonth(1).isEqual(localDate)) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                } else if (localDate.getMonthValue() == JoiningMonth && localDate.getYear() == JoinYear) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                } else binding.calendarPrevButton.setEnabled(true);

                if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                    monthInAdapterFlag = 0;
                    if (dayWiseArrayCurrentMonthOneBuild.size() == 0) {
                        dayWiseArrayCurrentMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                    }
                    oneBuildModelClassList = dayWiseArrayCurrentMonthOneBuild;
                    populateCalenderAdapterOneBuild(dayWiseArrayCurrentMonthOneBuild);
                } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().minusMonths(1).getMonth().toString())) {
                    monthInAdapterFlag = -1;
                    if (dayWiseArrayPreviousMonthOneBuild.size() == 0) {
                        dayWiseArrayPreviousMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                    }
                    dayWiseArrayPreviousMonthOneBuild.clear();
                    dayWiseArrayPreviousMonthOneBuild = prepareModelClassForMonthOneBuild(localDate);
                    oneBuildModelClassList = dayWiseArrayPreviousMonthOneBuild;
                    populateCalenderAdapterOneBuild(dayWiseArrayPreviousMonthOneBuild);
                }
            });
        } else {
            binding.calendarPrevButton.setOnClickListener(view -> {
                binding.calendarNextButton.setEnabled(true);
                binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_black, null));
                localDate = localDate.minusMonths(1).withDayOfMonth(1);
                if (LocalDate.now().minusMonths(1).withDayOfMonth(1).isEqual(localDate)) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                } else if (localDate.getMonthValue() == JoiningMonth && localDate.getYear() == JoinYear) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                } else
                    binding.calendarPrevButton.setEnabled(true);

                if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                    monthInAdapterFlag = 0;
                    if (dayWiseArrayCurrentMonth.size() == 0) {
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    modelClassList = dayWiseArrayCurrentMonth;
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().minusMonths(1).getMonth().toString())) {
                    monthInAdapterFlag = -1;
                    if (dayWiseArrayPrevMonth.size() == 0) {
                        dayWiseArrayPrevMonth = prepareModelClassForMonth(localDate);
                    }
                    modelClassList = dayWiseArrayPrevMonth;
                    populateCalendarAdapter(dayWiseArrayPrevMonth);
                }
            });
        }

        binding.tpNavigation.tpDrawerCloseIcon.setOnClickListener(view -> {
//            @Override
//            public void onSafeClick(View view) {
            binding.tpDrawer.closeDrawer(GravityCompat.END);
//            }
        });

        binding.tpNavigation.itemClear.setOnClickListener(view -> {
//            @Override
//            public void onSafeClick(View view) {
            SessionEditAdapter.MyViewHolder viewHolder = (SessionEditAdapter.MyViewHolder) binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(sessionEditAdapter.itemPosition);
            sessionEditAdapter.clearCheckBox(viewHolder);
//            }
        });

        binding.tpNavigation.checkBoxSave.setOnClickListener(view -> {
//            @Override
//            public void onSafeClick(View view) {
            SessionEditAdapter.MyViewHolder viewHolder = (SessionEditAdapter.MyViewHolder) binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(sessionEditAdapter.itemPosition);
            if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                sessionEditAdapter.saveCheckedItemOneBuild(viewHolder);
            } else {
                sessionEditAdapter.saveCheckedItem(viewHolder);
            }
//            }
        });

        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            binding.tpNavigation.addSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilityClass.hideKeyboard(TourPlanActivity.this);
                    boolean isEmpty = false;
                    int position = 0;

                    OneBuildModelClass oneBuildModelClass = inputDataArrayOneBuild;
                    ArrayList<OneBuildModelClass.SessionList> sessionLists = oneBuildModelClass.getSessionList();
                    if (sessionLists.size() < Integer.parseInt(addSessionCountLimit)) {
                        for (int i = 0; i < sessionLists.size(); i++) {
                            OneBuildModelClass.SessionList modelClass1 = sessionLists.get(i);
                            if (modelClass1.getWorkType().getName().isEmpty()) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.complete_session) + (i + 1));
                                break;
                            } else if (modelClass1.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                                if (modelClass1.getHeadquarters().getName().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
                                    break;
                                } else if (modelClass1.getTerritories().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
                                    break;
                                } else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                                    if (FW_meetup_mandatory.equals("0")) {
                                        if (drNeed.equals("0")) {
                                            if ((modelClass1.getDoctors().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                                isEmpty = true;
                                                position = i;
                                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                                break;
                                            }
                                            if ((modelClass1.getDoctors().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
                                                isEmpty = true;
                                                position = i;
                                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                                break;
                                            }
                                        }

                                        if (modelClass1.getDoctors().size() == 0 && modelClass1.getChemists().size() == 0 && modelClass1.getStockists().size() == 0 && modelClass1.getUnlistedDoctors().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospitals().size() == 0) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any_masters) + " " + (i + 1));
                                            break;
                                        }
                                    }
                                }
                            } else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                                if (FW_meetup_mandatory.equals("0")) {
                                    if (drNeed.equals("0")) {
                                        if (modelClass1.getDoctors().size() == 0) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.in_session) + " " + (i + 1));
                                            break;
                                        } else if ((modelClass1.getDoctors().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                            break;
                                        } else if ((modelClass1.getDoctors().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.more_than_limit) + " " + maxDrCount);
                                            break;
                                        }
                                    }
                                    if (modelClass1.getDoctors().size() == 0 && modelClass1.getChemists().size() == 0 && modelClass1.getStockists().size() == 0 && modelClass1.getUnlistedDoctors().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospitals().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any_masters) + " " + (i + 1));
                                        break;
                                    }
                                }
                            }
                        }

                        if (!isEmpty) {
                            sessionLists.add(prepareSessionListForAdapterOneBuild());
                            populateSessionEditAdapterOneBuild(oneBuildModelClass);
                            scrollToPosition(oneBuildModelClass.getSessionList().size() - 1, false);
                        } else {
                            scrollToPosition(position, true);
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.reached_session_limit));
                    }
                }
            });
        } else {
            binding.tpNavigation.addSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilityClass.hideKeyboard(TourPlanActivity.this);
                    boolean isEmpty = false;
                    int position = 0;

                    ModelClass modelClass = inputDataArray;
                    ArrayList<ModelClass.SessionList> sessionLists = modelClass.getSessionList();
                    if (sessionLists.size() < Integer.parseInt(addSessionCountLimit)) {
                        for (int i = 0; i < sessionLists.size(); i++) {
                            ModelClass.SessionList modelClass1 = sessionLists.get(i);
                            if (modelClass1.getWorkType().getName().isEmpty()) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.complete_session) + (i + 1));
                                break;
                            } else if (modelClass1.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                                if (modelClass1.getHQs().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
                                    break;
                                } else if ((modelClass1.getCluster().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1"))
                                        || (modelClass1.getClusters().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0"))) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
                                    break;
                                }
//                            if(modelClass1.getHQ().getName().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2")) {
//                                isEmpty = true;
//                                position = i;
//                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
//                                break;
//                            }else if(modelClass1.getCluster().size() == 0) {
//                                isEmpty = true;
//                                position = i;
//                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
//                                break;
//                            }
                                else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                                    if (FW_meetup_mandatory.equals("0")) {
                                        if (drNeed.equals("0")) {
//                                        if(modelClass1.getListedDr().size() == 0) {
//                                            isEmpty = true;
//                                            position = i;
//                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.in_session) + " " + (i + 1));
//                                            break;
//                                        }else
                                            if ((modelClass1.getListedDr().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                                isEmpty = true;
                                                position = i;
                                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                                break;
                                            }
                                            if ((modelClass1.getListedDr().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                                                isEmpty = true;
                                                position = i;
                                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                                break;
                                            } else if ((Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                                try {
                                                    int drsCount = 0;
                                                    for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass1.getListedDrs()) {
                                                        drsCount += multiHQHeaderModelClass.getItemsList().size();
                                                    }
                                                    if ((drsCount > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
                                                        isEmpty = true;
                                                        position = i;
                                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                                        break;
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                    if ((SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && modelClass1.getListedDr().isEmpty() && modelClass1.getChemist().isEmpty() && modelClass1.getStockiest().isEmpty() && modelClass1.getUnListedDr().isEmpty() && modelClass1.getCip().isEmpty() && modelClass1.getHospital().isEmpty())
                                            || (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0") && modelClass1.getListedDrs().isEmpty() && modelClass1.getChemists().isEmpty() && modelClass1.getStockiests().isEmpty() && modelClass1.getUnListedDrs().isEmpty() && modelClass1.getCips().isEmpty() && modelClass1.getHospitals().isEmpty())) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + " " + masters + " " + getString(R.string.in_session) + " " + (i + 1));
                                        break;
                                    }
                                }
                            } else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                                if (FW_meetup_mandatory.equals("0")) {
                                    if (drNeed.equals("0")) {
//                                    if(modelClass1.getListedDr().size() == 0) {
//                                        isEmpty = true;
//                                        position = i;
//                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.in_session) + (i + 1));
//                                        break;
//                                    }else
                                        if ((modelClass1.getListedDr().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                            break;
                                        }
                                        if ((modelClass1.getListedDr().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                            break;
                                        } else if ((Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                            try {
                                                int drsCount = 0;
                                                for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass1.getListedDrs()) {
                                                    drsCount += multiHQHeaderModelClass.getItemsList().size();
                                                }
                                                if ((drsCount > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
                                                    isEmpty = true;
                                                    position = i;
                                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    if ((SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && modelClass1.getListedDr().isEmpty() && modelClass1.getChemist().isEmpty() && modelClass1.getStockiest().isEmpty() && modelClass1.getUnListedDr().isEmpty() && modelClass1.getCip().isEmpty() && modelClass1.getHospital().isEmpty())
                                            || (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0") && modelClass1.getListedDrs().isEmpty() && modelClass1.getChemists().isEmpty() && modelClass1.getStockiests().isEmpty() && modelClass1.getUnListedDrs().isEmpty() && modelClass1.getCips().isEmpty() && modelClass1.getHospitals().isEmpty())) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + " " + masters + " " + getString(R.string.in_session) + " " + (i + 1));
                                        break;
                                    }
                                }
                            }
                        }

                        if (!isEmpty) {
                            sessionLists.add(prepareSessionListForAdapter());
                            populateSessionEditAdapter(modelClass);
                            scrollToPosition(modelClass.getSessionList().size() - 1, false);
                        } else {
                            scrollToPosition(position, true);
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.reached_session_limit));
                    }
                }
            });
        }

        if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
            //Session Save
            binding.tpNavigation.sessionSave.setOnClickListener(view -> {
             /*   @Override
                public void onSafeClick(View view) {*/
                UtilityClass.hideKeyboard(TourPlanActivity.this);
                boolean isEmpty = false;
                int position = 0;

                OneBuildModelClass dataModelOneBuild = inputDataArrayOneBuild;
                ArrayList<OneBuildModelClass.SessionList> sessionLists = dataModelOneBuild.getSessionList();
                String dayNo = String.valueOf(sessionLists.size());
                for (int i = 0; i < sessionLists.size(); i++) {
                    OneBuildModelClass.SessionList oneBuildModelClass = sessionLists.get(i);
                    if (oneBuildModelClass.getWorkType().getName().isEmpty()) {
                        isEmpty = true;
                        position = i;
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.complete_session) + (i + 1));
                        break;
                    } else if (oneBuildModelClass.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) { // TerrSlFlg is "Y" (yes) means head quarter and clusters are mandatory
                        if (oneBuildModelClass.getHeadquarters().getName().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                            isEmpty = true;
                            position = i;
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
                            break;
                        } else if (oneBuildModelClass.getTerritories().size() == 0) {
                            isEmpty = true;
                            position = i;
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
                            break;
                        } else if (oneBuildModelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            if (FW_meetup_mandatory.equals("0")) {
                                if (drNeed.equals("0")) {
                                    if (oneBuildModelClass.getDoctors().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.in_session) + " " + (i + 1));
                                        break;
                                    } else if ((oneBuildModelClass.getDoctors().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                        break;
                                    } else if ((oneBuildModelClass.getDoctors().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                        break;
                                    }
                                }

                                if (oneBuildModelClass.getDoctors().isEmpty() && oneBuildModelClass.getChemists().isEmpty() && oneBuildModelClass.getStockists().isEmpty() && oneBuildModelClass.getUnlistedDoctors().isEmpty() && oneBuildModelClass.getCip().isEmpty() && oneBuildModelClass.getHospitals().isEmpty()) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any_masters) + (i + 1));
                                    break;
                                }
                            }
                        }
                    } else if (oneBuildModelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) { // if the selected work type is "F" means Field Work then we need to check the FW_meetup_mandatory
                        if (FW_meetup_mandatory.equals("0")) { // "0"-- yes
                            if (drNeed.equals("0")) { // Dr meet up mandatory
                                if (oneBuildModelClass.getDoctors().isEmpty()) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.in_session) + " " + (i + 1));
                                    break;
                                } else if ((oneBuildModelClass.getDoctors().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                    break;
                                } else if ((oneBuildModelClass.getDoctors().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) { //Selected Dr count should not be more than maxDrCount setup limit
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                    break;
                                }
                            }
                            if (oneBuildModelClass.getDoctors().isEmpty() && oneBuildModelClass.getChemists().isEmpty() && oneBuildModelClass.getStockists().isEmpty() && oneBuildModelClass.getUnlistedDoctors().isEmpty() && oneBuildModelClass.getCip().isEmpty() && oneBuildModelClass.getHospitals().isEmpty()) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any_masters) + (i + 1));
                                break;
                            }
                        }
                    }
                    if (remarksNeed.equalsIgnoreCase("0") && oneBuildModelClass.getRemarks().isEmpty()) {
                        isEmpty = true;
                        position = i;
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.need_remarks) + (i + 1));
                        break;
                    }
                }

                dataModelOneBuild.setSyncStatus("1");
                TourPlanOfflineDataTable tourPlanOfflineDataTable1 = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
                tourPlanOfflineDataDao.saveMonthlySyncStatus(tourPlanOfflineDataTable1.getTpMonth(), "1");


                if (!isEmpty) {
                    binding.tpDrawer.closeDrawer(GravityCompat.END);
                    dataModelOneBuild.setSubmittedTime(TimeUtils.getCurrentDateTimeTp(TimeUtils.FORMAT_37));
                    if (monthInAdapterFlag == 0) {
                        for (int i = 0; i < dayWiseArrayCurrentMonthOneBuild.size(); i++) {
                            if (dayWiseArrayCurrentMonthOneBuild.get(i).getDate().equalsIgnoreCase(dataModelOneBuild.getDate())) {
                                dayWiseArrayCurrentMonthOneBuild.remove(i);
                                dayWiseArrayCurrentMonthOneBuild.add(i, dataModelOneBuild);
                                calendarAdapter.notifyDataSetChanged();// removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapterOneBuild(dayWiseArrayCurrentMonthOneBuild);
                        saveTpLocalOneBuild(dayWiseArrayCurrentMonthOneBuild, dayNo, TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModelOneBuild.getDate()), "1");


//                prepareObjectToSendForApprovalOneBuild(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModelOneBuild.getDate()), dayNo, dayWiseArrayCurrentMonthOneBuild, false);
                    } else if (monthInAdapterFlag == 1) {
                        for (int i = 0; i < dayWiseArrayNextMonthOneBuild.size(); i++) {
                            if (dayWiseArrayNextMonthOneBuild.get(i).getDate().equalsIgnoreCase(dataModelOneBuild.getDate())) {
                                dayWiseArrayNextMonthOneBuild.remove(i);
                                dayWiseArrayNextMonthOneBuild.add(i, dataModelOneBuild);
                                calendarAdapter.notifyDataSetChanged();// removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapterOneBuild(dayWiseArrayNextMonthOneBuild);
                        saveTpLocalOneBuild(dayWiseArrayNextMonthOneBuild, dayNo, TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModelOneBuild.getDate()), "1");
//                prepareObjectToSendForApprovalOneBuild(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModelOneBuild.getDate()), dayNo, dayWiseArrayNextMonthOneBuild, false);

                    } else if (monthInAdapterFlag == -1) {
                        for (int i = 0; i < dayWiseArrayPreviousMonthOneBuild.size(); i++) {
                            if (dayWiseArrayPreviousMonthOneBuild.get(i).getDate().equalsIgnoreCase(dataModelOneBuild.getDate())) {
                                dayWiseArrayPreviousMonthOneBuild.remove(i);
                                dayWiseArrayPreviousMonthOneBuild.add(i, dataModelOneBuild);
                                calendarAdapter.notifyDataSetChanged();// removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapterOneBuild(dayWiseArrayPreviousMonthOneBuild);
                        saveTpLocalOneBuild(dayWiseArrayPreviousMonthOneBuild, dayNo, TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModelOneBuild.getDate()), "1");
//                prepareObjectToSendForApprovalOneBuild(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModelOneBuild.getDate()), dayNo, dayWiseArrayPrevMonthOneBuild, false);

                    }

                    if (isEdited) {
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.updated_successfully));
                        calendarAdapter.notifyDataSetChanged();
                        isEdited = false;
                    } else {
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.saved_successfully));
                    }
                    calendarAdapter.notifyDataSetChanged();
                } else {
                    scrollToPosition(position, true);
                }
                binding.tpStatusTxt.setText(R.string.planning);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                binding.rejectionReasonLayout.setVisibility(View.GONE);
                binding.tpNavigation.sessionEdit.setEnabled(true);
                binding.rejectedReasonTxt.setText("");
//                }
            });
//Edit
            binding.tpNavigation.sessionEdit.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    isEdited = true;
                    binding.tpNavigation.addEditViewTxt.setText(R.string.edit_plan);
                    populateSessionEditAdapterOneBuild(sessionViewAdapter.inputDataModelOneBuild);
                }
            });
            //Approval
            binding.tpSendToApproval.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && planAllDr.equalsIgnoreCase("0") && drNeed.equalsIgnoreCase("0")) {
                        ArrayList<OneBuildModelClass> oneBuildModelClassList = new ArrayList<>();
                        LocalDate localDate1 = LocalDate.now();
                        if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1.minusMonths(1)))) {
                            oneBuildModelClassList = dayWiseArrayPreviousMonthOneBuild;
                        } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1))) {
                            oneBuildModelClassList = dayWiseArrayCurrentMonthOneBuild;
                        } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1.plusMonths(1)))) {
                            oneBuildModelClassList = dayWiseArrayNextMonthOneBuild;
                        }
                        if (validatePlanAllDrs(true, oneBuildModelClassList, new ArrayList<>())) {
                            if (visitFrequencyNeed.equalsIgnoreCase("0")) {
                                if (validateVisitFrequency()) {
                                    sendToApprovalOneBuild();
                                } else {
                                    CommonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_plan_all_the) + drCap + " " + getString(R.string.visit));
                                }
                            } else {
                                sendToApprovalOneBuild();
                            }
                        } else {
                            CommonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_plan_all_the) + drCap + getString(R.string.at_least_one_time));
                        }
                    } else if (visitFrequencyNeed.equalsIgnoreCase("0")) {
                        if (validateVisitFrequency()) {
                            sendToApprovalOneBuild();
                        } else {
                            CommonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_plan_all_the) + drCap + " " + getString(R.string.visit));
                        }
                    } else {
                        sendToApprovalOneBuild();
                    }
                }
            });
        } else {
            binding.tpNavigation.sessionSave.setOnClickListener(view -> {
//                @Override
//                public void onSafeClick(View view) {
                UtilityClass.hideKeyboard(TourPlanActivity.this);
                boolean isEmpty = false;
                int position = 0;

                ModelClass dataModel = inputDataArray;
                ArrayList<ModelClass.SessionList> sessionLists = dataModel.getSessionList();
                String dayNo = dataModel.getDayNo();
                for (int i = 0; i < sessionLists.size(); i++) {
                    ModelClass.SessionList modelClass = sessionLists.get(i);
                    if (modelClass.getWorkType().getName().isEmpty()) {
                        isEmpty = true;
                        position = i;
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.complete_session) + (i + 1));
                        break;
                    } else if (modelClass.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) { // TerrSlFlg is "Y" (yes) means head quarter and clusters are mandatory
//                    if(modelClass.getHQ().getName().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2")) {
//                        isEmpty = true;
//                        position = i;
//                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
//                        break;
//                    }else if(modelClass.getCluster().size() == 0) {
//                        isEmpty = true;
//                        position = i;
//                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
//                        break;
//                    }else if(modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
//                        if(FW_meetup_mandatory.equals("0")) {
//                            if(drNeed.equals("0")) {
////                                if(modelClass.getListedDr().size() == 0) {
////                                    isEmpty = true;
////                                    position = i;
////                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + " " +SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.in_session) + (i + 1));
////                                    break;
////                                }else
//                                    if((modelClass.getListedDr().size()>Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
//                                    isEmpty = true;
//                                    position = i;
//                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
//                                    break;
//                                }
//                            }
//
//                            if(modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 && modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) {
//                                isEmpty = true;
//                                position = i;
//                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + " " + masters + " " + getString(R.string.in_session) + (i + 1));
//                                break;
//                            }
//                        }
//                    }
                        ArrayList<MultiHQItemModelClass> checkList = new ArrayList<>();
//                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getClusters()) {
//                                checkList.addAll(multiHQHeaderModelClass.getItemsList());
//                            }
                        boolean isClusterNotSelectedForHQ = false;
                        String hqName = "";
                        ArrayList<String> hqCodes = new ArrayList<>();
                        ArrayList<String> hqNames = new ArrayList<>();
                        ArrayList<String> hqNamesDup = new ArrayList<>();
                        for (ModelClass.SessionList.SubClass subClass : modelClass.getHQs()) {
                            hqCodes.add(subClass.getCode());
                            hqNames.add(subClass.getName());
                            hqNamesDup.add(subClass.getName());
                        }
                        for (int k = 0; k < modelClass.getClusters().size(); k++) {
                            MultiHQHeaderModelClass multiHQHeaderModelClass = modelClass.getClusters().get(k);
                            ArrayList<MultiHQItemModelClass> dataList = multiHQHeaderModelClass.getItemsList();
                            checkList.addAll(dataList);
                            boolean isClusterNotSelected = false;
                            for (int j = 0; j < dataList.size(); j++) {
                                if (dataList.get(j).isChecked()) {
                                    isClusterNotSelected = true;
                                }
                            }
                            if (!dataList.isEmpty()) {
                                for (ModelClass.SessionList.SubClass subClass : modelClass.getHQs()) {
                                    if (subClass.getCode().equalsIgnoreCase(multiHQHeaderModelClass.getCode())) {
                                        int index = hqCodes.indexOf(subClass.getCode());
                                        hqCodes.remove(index);
                                        hqNames.remove(index);
                                        break;
                                    }
                                }
                            }
                            if (!isClusterNotSelected) {
                                isClusterNotSelectedForHQ = true;
                                hqName = multiHQHeaderModelClass.getName();
                                break;
                            }
                        }

                        if (hqName.isEmpty() && !hqNames.isEmpty()) {
                            hqName = hqNames.get(0);
                        }

                        if ((isClusterNotSelectedForHQ || !hqName.isEmpty()) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                            isEmpty = true;
                            position = i;
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + SharedPref.getClusterCap(TourPlanActivity.this) + " for " + hqName);
                            break;
                        }
                        if (modelClass.getHQs().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                            isEmpty = true;
                            position = i;
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
                            break;
                        } else if ((modelClass.getCluster().isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1"))
                                || (checkList.isEmpty() && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0"))) {
                            isEmpty = true;
                            position = i;
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
                            break;
                        } else if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            if (FW_meetup_mandatory.equals("0")) {
                                if (drNeed.equals("0")) {
//                                if(modelClass.getListedDr().size() == 0) {
//                                    isEmpty = true;
//                                    position = i;
//                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + " " +SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.in_session) + (i + 1));
//                                    break;
//                                }else
                                    if ((modelClass.getListedDr().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                        break;
                                    }
                                    if ((modelClass.getListedDr().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                        break;
                                    } else if ((Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                        try {
                                            int drsCount = 0;
                                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getListedDrs()) {
                                                drsCount = multiHQHeaderModelClass.getItemsList().size();
                                                if ((drsCount > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
                                                    isEmpty = true;
                                                    position = i;
                                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                                    break;
                                                }
                                            }
                                            if (isEmpty) {
                                                break;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                HashMap<String, ArrayList<MultiHQItemModelClass>> hqData = new HashMap<>();
                                for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getListedDrs()) {
                                    ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                    if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                        dataList = hqData.get(multiHQHeaderModelClass.getName());
                                    }
                                    if (dataList != null) {
                                        dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                    }
                                    hqData.put(multiHQHeaderModelClass.getName(), dataList);
                                }
                                for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getChemists()) {
                                    ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                    if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                        dataList = hqData.get(multiHQHeaderModelClass.getName());
                                    }
                                    if (dataList != null) {
                                        dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                    }
                                    hqData.put(multiHQHeaderModelClass.getName(), dataList);
                                }
                                for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getStockiests()) {
                                    ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                    if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                        dataList = hqData.get(multiHQHeaderModelClass.getName());
                                    }
                                    if (dataList != null) {
                                        dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                    }
                                    hqData.put(multiHQHeaderModelClass.getName(), dataList);
                                }
                                for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getUnListedDrs()) {
                                    ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                    if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                        dataList = hqData.get(multiHQHeaderModelClass.getName());
                                    }
                                    if (dataList != null) {
                                        dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                    }
                                    hqData.put(multiHQHeaderModelClass.getName(), dataList);
                                }
                                for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getCips()) {
                                    ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                    if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                        dataList = hqData.get(multiHQHeaderModelClass.getName());
                                    }
                                    if (dataList != null) {
                                        dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                    }
                                    hqData.put(multiHQHeaderModelClass.getName(), dataList);
                                }
                                for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getHospitals()) {
                                    ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                    if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                        dataList = hqData.get(multiHQHeaderModelClass.getName());
                                    }
                                    if (dataList != null) {
                                        dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                    }
                                    hqData.put(multiHQHeaderModelClass.getName(), dataList);
                                }

                                if ((SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && modelClass.getListedDr().isEmpty() && modelClass.getChemist().isEmpty() && modelClass.getStockiest().isEmpty() && modelClass.getUnListedDr().isEmpty() && modelClass.getCip().isEmpty() && modelClass.getHospital().isEmpty())) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + " " + masters + " " + getString(R.string.in_session) + " " + (i + 1));
                                    break;
                                } else if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2")
                                        && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                    for (String hq : hqNamesDup) {
                                        if (!hqData.containsKey(hq) || hqData.get(hq).isEmpty()) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + " " + masters + " for " + hq + " " + getString(R.string.in_session) + " " + (i + 1));
                                            break;
                                        }
                                    }
                                    if (isEmpty) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (remarksNeed.equalsIgnoreCase("0") && modelClass.getRemarks().isEmpty()) {
                            isEmpty = true;
                            position = i;
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.need_remarks) + (i + 1));
                            break;
                        }
                    } else if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) { // if the selected work type is "F" means Field Work then we need to check the FW_meetup_mandatory
                        if (FW_meetup_mandatory.equals("0")) { // "0"-- yes
                            if (drNeed.equals("0")) { // Dr meet up mandatory
//                            if(modelClass.getListedDr().size() == 0) {
//                                isEmpty = true;
//                                position = i;
//                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.in_session) + (i + 1));
//                                break;
//                            }else
                                if ((modelClass.getListedDr().size() < Integer.parseInt(minDrCount)) && (Integer.parseInt(minDrCount) > 0)) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_select_at_least) + minDrCount + " " + SharedPref.getDrCap(TourPlanActivity.this));
                                    break;
                                }
                                if ((modelClass.getListedDr().size() > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                    break;
                                } else if ((Integer.parseInt(maxDrCount) > 0) && SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                    try {
                                        int drsCount = 0;
                                        for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getListedDrs()) {
                                            drsCount = multiHQHeaderModelClass.getItemsList().size();
                                            if ((drsCount > Integer.parseInt(maxDrCount)) && (Integer.parseInt(maxDrCount) > 0)) {
                                                isEmpty = true;
                                                position = i;
                                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + " " + SharedPref.getDrCap(TourPlanActivity.this) + " " + getString(R.string.more_than_limit) + " " + maxDrCount);
                                                break;
                                            }
                                        }
                                        if (isEmpty) {
                                            break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            ArrayList<String> hqNamesDup = new ArrayList<>();
                            for (ModelClass.SessionList.SubClass subClass : modelClass.getHQs()) {
                                hqNamesDup.add(subClass.getName());
                            }

                            HashMap<String, ArrayList<MultiHQItemModelClass>> hqData = new HashMap<>();
                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getListedDrs()) {
                                ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                    dataList = hqData.get(multiHQHeaderModelClass.getName());
                                }
                                if (dataList != null) {
                                    dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                }
                                hqData.put(multiHQHeaderModelClass.getName(), dataList);
                            }
                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getChemists()) {
                                ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                    dataList = hqData.get(multiHQHeaderModelClass.getName());
                                }
                                if (dataList != null) {
                                    dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                }
                                hqData.put(multiHQHeaderModelClass.getName(), dataList);
                            }
                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getStockiests()) {
                                ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                    dataList = hqData.get(multiHQHeaderModelClass.getName());
                                }
                                if (dataList != null) {
                                    dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                }
                                hqData.put(multiHQHeaderModelClass.getName(), dataList);
                            }
                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getUnListedDrs()) {
                                ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                    dataList = hqData.get(multiHQHeaderModelClass.getName());
                                }
                                if (dataList != null) {
                                    dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                }
                                hqData.put(multiHQHeaderModelClass.getName(), dataList);
                            }
                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getCips()) {
                                ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                    dataList = hqData.get(multiHQHeaderModelClass.getName());
                                }
                                if (dataList != null) {
                                    dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                }
                                hqData.put(multiHQHeaderModelClass.getName(), dataList);
                            }
                            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelClass.getHospitals()) {
                                ArrayList<MultiHQItemModelClass> dataList = new ArrayList<>();
                                if (hqData.containsKey(multiHQHeaderModelClass.getName())) {
                                    dataList = hqData.get(multiHQHeaderModelClass.getName());
                                }
                                if (dataList != null) {
                                    dataList.addAll(multiHQHeaderModelClass.getItemsList());
                                }
                                hqData.put(multiHQHeaderModelClass.getName(), dataList);
                            }

                            if ((SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && modelClass.getListedDr().isEmpty() && modelClass.getChemist().isEmpty() && modelClass.getStockiest().isEmpty() && modelClass.getUnListedDr().isEmpty() && modelClass.getCip().isEmpty() && modelClass.getHospital().isEmpty())) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + " " + masters + " " + getString(R.string.in_session) + " " + (i + 1));
                                break;
                            } else if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2")
                                    && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                for (String hq : hqNamesDup) {
                                    if (!hqData.containsKey(hq) || hqData.get(hq).isEmpty()) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any) + " " + masters + " for " + hq + " " + getString(R.string.in_session) + " " + (i + 1));
                                        break;
                                    }
                                }
                                if (isEmpty) {
                                    break;
                                }
                            }
                        }
                    }
                    if (remarksNeed.equalsIgnoreCase("0") && modelClass.getRemarks().isEmpty()) {
                        isEmpty = true;
                        position = i;
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.need_remarks) + (i + 1));
                        break;
                    }
                }

                if (!isEmpty) {
                    binding.tpDrawer.closeDrawer(GravityCompat.END);
                    dataModel.setSubmittedTime(TimeUtils.getCurrentDateTimeTp(TimeUtils.FORMAT_37));
                    if (monthInAdapterFlag == 0) {
                        for (int i = 0; i < dayWiseArrayCurrentMonth.size(); i++) {
                            if (dayWiseArrayCurrentMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                                dayWiseArrayCurrentMonth.remove(i);
                                dayWiseArrayCurrentMonth.add(i, dataModel); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayCurrentMonth);
                        prepareObjectToSendForApproval(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayCurrentMonth, false);
                    } else if (monthInAdapterFlag == 1) {
                        for (int i = 0; i < dayWiseArrayNextMonth.size(); i++) {
                            if (dayWiseArrayNextMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                                dayWiseArrayNextMonth.remove(i);
                                dayWiseArrayNextMonth.add(i, dataModel); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayNextMonth);
                        saveTpLocal(dayWiseArrayNextMonth, dayNo, TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), "1");
                        prepareObjectToSendForApproval(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayNextMonth, false);
                    } else if (monthInAdapterFlag == -1) {
                        for (int i = 0; i < dayWiseArrayPrevMonth.size(); i++) {
                            if (dayWiseArrayPrevMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                                dayWiseArrayPrevMonth.remove(i);
                                dayWiseArrayPrevMonth.add(i, dataModel); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayPrevMonth);
                        saveTpLocal(dayWiseArrayPrevMonth, dayNo, TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), "1");
                        prepareObjectToSendForApproval(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayPrevMonth, false);
                    }

                    if (isEdited) {
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.updated_successfully));
                        isEdited = false;
                    } else {
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.saved_successfully));
                    }
                    calendarAdapter.notifyDataSetChanged();
                } else {
                    scrollToPosition(position, true);
                }
//                }
            });

            binding.tpNavigation.sessionEdit.setOnClickListener(view -> {
//                @Override
//                public void onSafeClick(View view) {
                isEdited = true;
                binding.tpNavigation.addEditViewTxt.setText(R.string.edit_plan);
                populateSessionEditAdapter(sessionViewAdapter.inputDataModel);
//                }
            });

            binding.tpSendToApproval.setOnClickListener(view -> {
                if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && planAllDr.equalsIgnoreCase("0") && drNeed.equalsIgnoreCase("0")) {
                    ArrayList<ModelClass> arrayList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
                        Type type = new TypeToken<ArrayList<ModelClass>>() {
                        }.getType();
                        if (jsonArray.length() > 0) {
                            arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (validatePlanAllDrs(false, new ArrayList<>(), arrayList)) {
                        sendToApproval();
                    } else {
                        CommonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.please_plan_all_the) + drCap + getString(R.string.at_least_one_time));
                    }
                } else {
                    sendToApproval();
                }
            });
        }

        if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
            binding.planOverview.setVisibility(View.VISIBLE);
            binding.planOverview.setOnClickListener(view -> {
                Intent intent = new Intent(TourPlanActivity.this, TourPlanOverviewActivity.class);
                try {
                    intent.putExtra("month", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()));
                    intent.putExtra("is_one_build", SharedPref.getOneBuild(TourPlanActivity.this).equals("0"));
                    intent.putExtra("visit_frequency_need", visitFrequencyNeed.equals("0"));
                    intent.putExtra("dr_need", drNeed.equals("0"));
                    intent.putExtra("chm_need", chemistNeed.equals("0"));
                    intent.putExtra("one_build_data", oneBuildModelClassList);
                    intent.putExtra("data", modelClassList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            });
        } else {
            binding.planOverview.setVisibility(View.GONE);
        }
    }

    private boolean validateVisitFrequency() {
        boolean isValid = true;
        for (DoctorVisitModel doctorVisitModel : doctorVisitMap.values()) {
            if (doctorVisitModel.getPlannedVisit() < doctorVisitModel.getTotalVisit()) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    private void getDoctorData() {
        try {
            doctorMap = new HashMap<>();
            doctorDataMap = new HashMap<>();
            doctorVisitMap = new HashMap<>();
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(TourPlanActivity.this)).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String name = "-", code = "-", clusterName = "-", clusterCode = "-", category = "-", categoryCode = "-", classs = "-", classsCode = "-", speciality = "-", specialityCode = "-", qualificationCode = "-";
                int visitCount = 0;
                name = jsonObject.optString("Name");
                code = jsonObject.optString("Code");
                clusterName = jsonObject.optString("Town_Name", "-");
                clusterCode = jsonObject.optString("Town_Code", "-");
                category = jsonObject.optString("Category", "-");
                categoryCode = jsonObject.optString("CategoryCode", "-");
                classs = jsonObject.optString("Doc_Class_ShortName", "-");
                classsCode = jsonObject.optString("Doc_ClsCode", "-");
                speciality = jsonObject.optString("Specialty", "-");
                specialityCode = jsonObject.optString("SpecialtyCode", "-");
                qualificationCode = jsonObject.optString("DocQuacode", "-");
                String vstCount = jsonObject.optString("Tlvst", "");
                if (!vstCount.equalsIgnoreCase("null") && !vstCount.isEmpty()) {
                    visitCount = Integer.parseInt(vstCount);
                }
                doctorMap.put(code, name);
                doctorDataMap.put(code, new DoctorDataModel(name, code, clusterName, clusterCode, category, categoryCode, classs, classsCode, speciality, specialityCode, "-", qualificationCode, visitCount));
                doctorVisitMap.put(code, new DoctorVisitModel(code, name, category, new HashSet<>(), visitCount, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToApproval() {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, status -> {
            if (status) {
                binding.tpSendToApproval.setEnabled(false);
                JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
                ArrayList<ModelClass> arrayList;
                ArrayList<String> dummy = new ArrayList<>();
                Type type = new TypeToken<ArrayList<ModelClass>>() {
                }.getType();
                if (jsonArray.length() > 0) {
                    arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
                    for (ModelClass modelClass : arrayList) {
                        if (!modelClass.getDate().equals("") && !modelClass.getSyncStatus().equals("0")) {
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, " Offline TourPlan Uploading…");
                            dummy.add(modelClass.getDayNo());
                            Log.v("tpApproval", "---" + modelClass.getDayNo());
                            binding.progressBar.setVisibility(View.VISIBLE);
                            prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, modelClass.getDate()), modelClass.getDayNo(), arrayList, true);
                            break;
                        }
                    }
                }
                if (dummy.size() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    sendWholeMonthStatus(localDate);
                }
            } else {
                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
            }
        });
        networkStatusTask.execute();
    }

    private void sendToApprovalOneBuild() {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, status -> {
            if (status) {
                isFrom = "sendToApproval";
                binding.tpSendToApproval.setEnabled(false);
                binding.progressBar.setVisibility(View.VISIBLE);
                LocalDate localDate1 = LocalDate.now();
                if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1.minusMonths(1)))) {
                    getDraftSaveOneBuild("previous", monthYearFromDate(localDate1.minusMonths(1)), dayWiseArrayPreviousMonthOneBuild, isFrom, status);
                } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1))) {
                    getDraftSaveOneBuild("current", monthYearFromDate(localDate1), dayWiseArrayCurrentMonthOneBuild, isFrom, status);
                } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1.plusMonths(1)))) {
                    getDraftSaveOneBuild("next", monthYearFromDate(localDate1.plusMonths(1)), dayWiseArrayNextMonthOneBuild, isFrom, status);
                }
            } else {
                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
            }
        });
        networkStatusTask.execute();
    }

    private boolean validatePlanAllDrs(boolean isOneBuild, ArrayList<OneBuildModelClass> oneBuildModelClassList, ArrayList<ModelClass> modelClassList) {
        Map<String, String> drMap = new HashMap<>(doctorMap);
        if (isOneBuild) {
            for (OneBuildModelClass oneBuildModelClass : oneBuildModelClassList) {
                for (OneBuildModelClass.SessionList sessionList : oneBuildModelClass.getSessionList()) {
                    if (sessionList.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                        for (OneBuildModelClass.SessionList.SubClass dr : sessionList.getDoctors()) {
                            Log.d("TP DR", "validatePlanAllDrs: " + dr.getCode() + " -> " + dr.getName());
                            drMap.remove(dr.getCode());
                        }
                    }
                }
            }
            Log.v("TP", "validatePlanAllDrs: " + drMap.toString());
        } else {
            for (ModelClass modelClass : modelClassList) {
                for (ModelClass.SessionList sessionList : modelClass.getSessionList()) {
                    if (sessionList.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                        for (ModelClass.SessionList.SubClass dr : sessionList.getListedDr()) {
                            Log.d("TP DR", "validatePlanAllDrs: " + dr.getCode() + " -> " + dr.getName());
                            drMap.remove(dr.getCode());
                        }
                    }
                }
            }
        }
        return drMap.isEmpty();
    }

    private void checkAndSetSTPDataAvailable(LocalDate localDate1) {
        boolean isSTPdataSet = false;
        try {
            List<STPOfflineDataTable> stpOfflineDataTableList = stpOfflineDataDao.getAllSTPData();
            if (stpOfflineDataTableList.isEmpty()) {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dayID = jsonObject.optString("Day_Plan_ShortName");
                        String dayCaption = jsonObject.optString("Day_Plan_Name");
                        String dayPlanCode = jsonObject.optString("Day_Plan_Code");
                        String clusterCode = jsonObject.optString("Patch_Code");
                        String clusterName = jsonObject.optString("Patch_Name");
                        String doctorCode = jsonObject.optString("Dr_Code");
                        String doctorName = jsonObject.optString("Dr_Name");
                        String chemistCode = jsonObject.optString("Chem_Code");
                        String chemistName = jsonObject.optString("Chem_Name");
                        String dateTime = jsonObject.optString("Created_Date");
                        String activeFlag = jsonObject.optString("Active_Flag");
                        Log.d("STP master data", "saveSTPDataToLocal: " + jsonObject);
                        if (activeFlag.equalsIgnoreCase("0")) {
                            SharedPref.setStpStatus(TourPlanActivity.this, "Approved");
                        }

                        JSONObject jsonSave = new JSONObject();
                        jsonSave = CommonUtilsMethods.CommonObjectParameter(this);
                        jsonSave.put("sfcode", SharedPref.getSfCode(this));
                        jsonSave.put("DivCode", SharedPref.getDivisionCode(this));
                        jsonSave.put("Rsf", SharedPref.getHqCode(this));
                        jsonSave.put("town_code", clusterCode);
                        jsonSave.put("town_name", clusterName);
                        jsonSave.put("Doctor_Id", doctorCode);
                        jsonSave.put("Doctor_Name", doctorName);
                        jsonSave.put("Chemist_Id", chemistCode);
                        jsonSave.put("Chemist_Name", chemistName);
                        jsonSave.put("Plan_Name", dayCaption);
                        jsonSave.put("Plan_SName", dayID);
                        jsonSave.put("Plan_Code", dayPlanCode);
                        jsonSave.put("StpFlag", activeFlag);
                        jsonSave.put("tableName", "save_stp");
                        jsonSave.put("ReqDt", dateTime);
                        Log.d("STP save data", "saveSTPDataToLocal: " + jsonSave);
                        int stpFlag = 3;
                        try {
                            stpFlag = Integer.parseInt(activeFlag);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, SharedPref.getSfCode(TourPlanActivity.this), dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, jsonObject.toString(), stpFlag, "0"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SharedPref.getStpStatus(TourPlanActivity.this).equalsIgnoreCase("Approved")) {
            String month = "current";
            try {
                JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.TOUR_PLAN).getMasterSyncDataJsonArray();
                if (jsonArray1.length() > 0) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                    if (LocalDate.now().getMonth() == localDate1.getMonth() && LocalDate.now().getYear() == localDate1.getYear()) {
                        month = "current";
                    } else if (LocalDate.now().minusMonths(1).getMonth() == localDate1.getMonth() && LocalDate.now().minusMonths(1).getYear() == localDate1.getYear()) {
                        month = "previous";
                    } else if (LocalDate.now().plusMonths(1).getMonth() == localDate1.getMonth() && LocalDate.now().plusMonths(1).getYear() == localDate1.getYear()) {
                        month = "next";
                    }
                    JSONArray jsonArray = jsonObject1.getJSONArray(month);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String stpCode = jsonObject.optString("STP_Code");
                        if (!stpCode.isEmpty()) {
                            isSTPdataSet = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isSTPdataSet && !month.equalsIgnoreCase("previous")) {
                tourPlanOfflineDataDao.deleteByMonth(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate1)));
            }
        }
    }

    private ModelClass.SessionList prepareSessionListForAdapterEmpty() {
        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType("", "", "", "");
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("", "");

        ArrayList<ModelClass.SessionList.SubClass> hqArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> clustersArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> jcsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> drsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> chemistsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> stocksArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> unListedDrsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> cipsArray = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> hospsArray = new ArrayList<>();

        return new ModelClass.SessionList("", true, "", workType, hq, hqArray, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray, clustersArray, jcsArray, drsArray, chemistsArray, stocksArray, unListedDrsArray, cipsArray, hospsArray);
    }

    private OneBuildModelClass.SessionList prepareSessionListForAdapterEmptyOneBuild() {
        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        OneBuildModelClass.SessionList.WorkType workType = new OneBuildModelClass.SessionList.WorkType("", "", "", "");
        OneBuildModelClass.SessionList.SubClass headquarters = new OneBuildModelClass.SessionList.SubClass("", "");

        ArrayList<OneBuildModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        return new OneBuildModelClass.SessionList("", true, "", "", "", "", workType, headquarters, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public void uiInitializationOneBuild() {
        drCap = SharedPref.getDrCap(TourPlanActivity.this);
        chmCap = SharedPref.getChmCap(TourPlanActivity.this);
        stkCap = SharedPref.getStkCap(TourPlanActivity.this);
        unListDrCap = SharedPref.getUNLcap(TourPlanActivity.this);
        cipCap = SharedPref.getCipCaption(TourPlanActivity.this);
        hosCap = SharedPref.getHospCaption(TourPlanActivity.this);

        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                drNeed = jsonObject.optString("DrNeed");
                minDrCount = jsonObject.optString("min_doc", "0");
                maxDrCount = jsonObject.optString("max_doc");
                addSessionNeed = jsonObject.optString("AddsessionNeed");
                addSessionCountLimit = jsonObject.optString("AddsessionCount");
                FW_meetup_mandatory = jsonObject.optString("FW_meetup_mandatory");
                holidayEditable = jsonObject.optString("Holiday_Editable");
                weeklyOffEditable = jsonObject.optString("Weeklyoff_Editable");
                chemistNeed = jsonObject.optString("ChmNeed");
                jwNeed = jsonObject.optString("JWNeed");
                stockiestNeed = jsonObject.optString("StkNeed");
                unListedDrNeed = jsonObject.optString("UnDrNeed");
                cipNeed = jsonObject.optString("Cip_Need");
                hospNeed = jsonObject.optString("HospNeed");
                remarksNeed = jsonObject.optString("tp_objective_mandatory");
                planAllDr = jsonObject.optString("Plan_All_Drs", "1");
                visitFrequencyNeed = jsonObject.optString("visit_freq_need", "1");
                minimumGap = jsonObject.optString("min_gap_need", "0");
            }

            StringBuilder masters = new StringBuilder();
            if (drNeed.equalsIgnoreCase("0")) {
                masters.append(drCap);
                masters.append(" / ");
            }
            if (chemistNeed.equalsIgnoreCase("0")) {
                masters.append(chmCap);
                masters.append(" / ");
            }
            if (stockiestNeed.equalsIgnoreCase("0")) {
                masters.append(stkCap);
                masters.append(" / ");
            }
            if (unListedDrNeed.equalsIgnoreCase("0")) {
                masters.append(unListDrCap);
                masters.append(" / ");
            }
            if (cipNeed.equalsIgnoreCase("0")) {
                masters.append(cipCap);
                masters.append(" / ");
            }
            if (hospNeed.equalsIgnoreCase("0")) {
                masters.append(hosCap);
                masters.append(" / ");
            }

            this.masters = masters.toString();
            if (this.masters.endsWith("/")) {
                this.masters = this.masters.substring(0, this.masters.length() - 1);
            }

            if (addSessionNeed.equalsIgnoreCase("0"))
                binding.tpNavigation.addSession.setVisibility(View.VISIBLE);
            else binding.tpNavigation.addSession.setVisibility(View.GONE);

            holidayJSONArray = masterDataDao.getMasterDataTableOrNew(Constants.HOLIDAY).getMasterSyncDataJsonArray(); //Holiday data
            JSONArray weeklyOff = masterDataDao.getMasterDataTableOrNew(Constants.WEEKLY_OFF).getMasterSyncDataJsonArray(); // Weekly Off data

            for (int i = 0; i < weeklyOff.length(); i++) {
                JSONObject jsonObject = weeklyOff.getJSONObject(i);
                holidayMode = jsonObject.getString("Holiday_Mode");
                weeklyOffCaption = jsonObject.getString("WTname");
            }
            if (holidayMode.contains(",")) {
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
            }
            JSONArray workTypeArray1 = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray(); //List of Work Types
            for (int i = 0; i < workTypeArray1.length(); i++) {
                JSONObject jsonObject = workTypeArray1.getJSONObject(i);
                if (jsonObject.getString("FWFlg").equalsIgnoreCase("W"))
                    weeklyOffWorkTypeModelOneBuild = new OneBuildModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
                else if (jsonObject.getString("FWFlg").equalsIgnoreCase("H"))
                    holidayWorkTypeModelOneBuild = new OneBuildModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void uiInitialization() {
        drCap = SharedPref.getDrCap(TourPlanActivity.this);
        chmCap = SharedPref.getChmCap(TourPlanActivity.this);
        stkCap = SharedPref.getStkCap(TourPlanActivity.this);
        unListDrCap = SharedPref.getUNLcap(TourPlanActivity.this);
        cipCap = SharedPref.getCipCaption(TourPlanActivity.this);
        hosCap = SharedPref.getHospCaption(TourPlanActivity.this);

        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                drNeed = jsonObject.optString("DrNeed");
                minDrCount = jsonObject.optString("min_doc", "0");
                maxDrCount = jsonObject.optString("max_doc");
                addSessionNeed = jsonObject.optString("AddsessionNeed");
                addSessionCountLimit = jsonObject.optString("AddsessionCount");
                FW_meetup_mandatory = jsonObject.optString("FW_meetup_mandatory");
                holidayEditable = jsonObject.optString("Holiday_Editable");
                weeklyOffEditable = jsonObject.optString("Weeklyoff_Editable");
                chemistNeed = jsonObject.optString("ChmNeed");
                jwNeed = jsonObject.optString("JWNeed");
                stockiestNeed = jsonObject.optString("StkNeed");
                unListedDrNeed = jsonObject.optString("UnDrNeed");
                cipNeed = jsonObject.optString("Cip_Need");
                hospNeed = jsonObject.optString("HospNeed");
                remarksNeed = jsonObject.optString("tp_objective_mandatory");
                planAllDr = jsonObject.optString("Plan_All_Drs", "1");
                visitFrequencyNeed = jsonObject.optString("visit_freq_need", "1");
                minimumGap = jsonObject.optString("min_gap_need", "0");
            }

            StringBuilder masters = new StringBuilder();
            if (drNeed.equalsIgnoreCase("0")) {
                masters.append(drCap);
                masters.append(" / ");
            }
            if (chemistNeed.equalsIgnoreCase("0")) {
                masters.append(chmCap);
                masters.append(" / ");
            }
            if (stockiestNeed.equalsIgnoreCase("0")) {
                masters.append(stkCap);
                masters.append(" / ");
            }
            if (unListedDrNeed.equalsIgnoreCase("0")) {
                masters.append(unListDrCap);
                masters.append(" / ");
            }
            if (cipNeed.equalsIgnoreCase("0")) {
                masters.append(cipCap);
                masters.append(" / ");
            }
            if (hospNeed.equalsIgnoreCase("0")) {
                masters.append(hosCap);
                masters.append(" / ");
            }

            this.masters = masters.toString();
            if (this.masters.endsWith("/")) {
                this.masters = this.masters.substring(0, this.masters.length() - 1);
            }

            if (addSessionNeed.equalsIgnoreCase("0"))
                binding.tpNavigation.addSession.setVisibility(View.VISIBLE);
            else binding.tpNavigation.addSession.setVisibility(View.GONE);

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

            JSONArray workTypeArray1 = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray(); //List of Work Types
            for (int i = 0; i < workTypeArray1.length(); i++) {
                JSONObject jsonObject = workTypeArray1.getJSONObject(i);
                if (jsonObject.getString("FWFlg").equalsIgnoreCase("W"))
                    weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
                else if (jsonObject.getString("FWFlg").equalsIgnoreCase("H"))
                    holidayWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        if (dayOfWeek == 7) dayOfWeek = 1;
        else dayOfWeek++;

        int trailingNumOfDaysEmpty = 7 - ((daysInMonth + dayOfWeek - 1) % 7);
        for (int i = 1; i < dayOfWeek + daysInMonth + trailingNumOfDaysEmpty; i++) {
            if (i >= dayOfWeek && i < (daysInMonth + dayOfWeek))
                daysInMonthArray.add(String.valueOf((i + 1) - dayOfWeek));
            else daysInMonthArray.add("");
        }

        return daysInMonthArray;
    }

    private String monthYearFromDateUI(LocalDate date) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    private String dayMonthYearFromDate(LocalDate date, String format) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern(format);

        return date.format(formatter);
    }

    public ArrayList<ModelClass> prepareModelClassForMonth(LocalDate localDate1) {
        if (isSTPBasedTP) {
            checkAndSetSTPDataAvailable(localDate1);
        }
        ArrayList<ModelClass> modelClasses = new ArrayList<>();
        try {
            //Data from Tour Plan table
            Log.v("getTp", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate1)));

            JSONArray savedDataArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate1))).getTpDataJSONArray();

            if (savedDataArray.length() > 0) { //Use the saved data if Tour Plan table has data of a selected month
                Type type = new TypeToken<ArrayList<ModelClass>>() {
                }.getType();
                modelClasses = new Gson().fromJson(savedDataArray.toString(), type);
                if (visitFrequencyNeed.equalsIgnoreCase("0")) {
                    prepareDoctorVisitData(modelClasses);
                }
            } else { //If tour plan table has no data
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate1));
                String monthYear = monthYearFromDate(localDate1);
                String month = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_31, monthYear);
                String year = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);

                ArrayList<String> holidayDateArray = new ArrayList<>();
                for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                    if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate1.getMonthValue())))
                        holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
                }

                boolean LocalWeelyHolidayFlag = false;

                for (String day : days) {
                    if (!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        ModelClass.SessionList sessionList = new ModelClass.SessionList();
                        sessionList = prepareSessionListForAdapter();


                        if (Integer.valueOf(month) == JoiningMonth && Integer.valueOf(year) == JoinYear && Integer.valueOf(day) < JoningDate) {
                            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            ModelClass modelClass = new ModelClass(day, date, dayName, month, year, false, sessionLists);
                            modelClasses.add(modelClass);
                            LocalWeelyHolidayFlag = false;
                        } else {
                            String dayOfWeek = "";
                            if (isSTPBasedTP) {
                                try {
                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(TimeUtils.FORMAT_38, Locale.ENGLISH);
                                    LocalDate localDate = LocalDate.parse(date, dateFormatter);
                                    dayOfWeek = getDayOfWeekOccurrence(localDate);
                                    Log.i("STP date", "populateCalendarAdapter: " + dayOfWeek + " date : " + date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (isSTPBasedTP && !dayOfWeek.isEmpty() && stpOfflineDataDao.isDayAvailable(dayOfWeek)) {
                                ModelClass modelClass = prepareAndSaveSTPModelClass(day, date, dayName, dayOfWeek, localDate1);
                                if (holidayDateArray.contains(day)) {
                                    sessionList.setWorkType(holidayWorkTypeModel);  // add holiday work type model object when current date is declared as holiday
                                    ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                                    sessionLists.add(sessionList);
                                    modelClass = new ModelClass(day, date, dayName, month, year, false, sessionLists);
                                } else if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                    sessionList.setWorkType(weeklyOffWorkTypeModel);
                                    ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                                    sessionLists.add(sessionList);
                                    modelClass = new ModelClass(day, date, dayName, month, year, false, sessionLists);
                                }
                                modelClasses.add(modelClass);
                                LocalWeelyHolidayFlag = true;
                            } else {
                                if (holidayDateArray.contains(day)) {
                                    sessionList.setWorkType(holidayWorkTypeModel);  // add holiday work type model object when current date is declared as holiday
                                    LocalWeelyHolidayFlag = true;
                                } else if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                    sessionList.setWorkType(weeklyOffWorkTypeModel);
                                    LocalWeelyHolidayFlag = true;
                                } else {
                                    LocalWeelyHolidayFlag = false;
                                }
                                ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                                sessionLists.add(sessionList);
                                ModelClass modelClass = new ModelClass(day, date, dayName, month, year, false, sessionLists);
                                modelClasses.add(modelClass);
                            }
                        }

                        if (LocalWeelyHolidayFlag) {
                            saveTpLocal(modelClasses, day, monthYear, "1");
                        } else {
                            saveTpLocal(modelClasses, day, monthYear, "0");
                        }

                    } else {
                        //  Log.v("getTp","--333--" + day);
                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        ModelClass modelClass = new ModelClass(day, "", "", "", "", true, sessionLists);
                        modelClasses.add(modelClass);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e("--Errr--", "" + e);
            e.printStackTrace();
        }
        modelClassList = modelClasses;
        return modelClasses;
    }

    public ArrayList<OneBuildModelClass> prepareModelClassForMonthOneBuild(LocalDate localDate2) {
        if (isSTPBasedTP) {
            checkAndSetSTPDataAvailable(localDate2);
        }
        ArrayList<OneBuildModelClass> oneBuildModelClasses = new ArrayList<>();
        try {
            Log.v("getTp", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate2)));
            JSONArray savedDataArrayOneBuild = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate2))).getTpDataJSONArray();
            if (savedDataArrayOneBuild.length() > 0) {
                Type type = new TypeToken<ArrayList<OneBuildModelClass>>() {
                }.getType();
                oneBuildModelClasses = new Gson().fromJson(savedDataArrayOneBuild.toString(), type);
                if (visitFrequencyNeed.equalsIgnoreCase("0")) {
                    prepareDoctorVisitDataOneBuild(oneBuildModelClasses);
                }
            } else {//If tour plan table has no data
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate2));
                String monthYear = monthYearFromDate(localDate2);
                String month = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_31, monthYear);
                String year = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);

                ArrayList<String> holidayDateArray = new ArrayList<>();
                for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                    if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate2.getMonthValue())))
                        holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
                }

                boolean LocalWeelyHolidayFlag = false;

                for (String day : days) {
                    if (!day.isEmpty()) {
//                        String date = day + " " + monthYear;
//                        String dayName = formatter.format(new Date(date));

                        String date = day + " " + monthYear;

                        SimpleDateFormat parser = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
                        Date parsedDate = parser.parse(date);
                        String dayName = formatter.format(parsedDate);

                        OneBuildModelClass.SessionList sessionListOneBuild = new OneBuildModelClass.SessionList();
                        sessionListOneBuild = prepareSessionListForAdapterOneBuild();

                        if (Integer.valueOf(month) == JoiningMonth && Integer.valueOf(year) == JoinYear && Integer.valueOf(day) < JoningDate) {
                            ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionListOneBuild);
                            OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, month, year, true, sessionLists);
                            oneBuildModelClasses.add(oneBuildModelClass);
                            LocalWeelyHolidayFlag = false;
                        } else {
                            String dayOfWeek = "";
                            if (isSTPBasedTP) {
                                try {
                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(TimeUtils.FORMAT_38, Locale.ENGLISH);
                                    LocalDate localDate = LocalDate.parse(date, dateFormatter);
                                    dayOfWeek = getDayOfWeekOccurrence(localDate);
                                    Log.i("STP date", "populateCalendarAdapter: " + dayOfWeek + " date : " + date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (isSTPBasedTP && !dayOfWeek.isEmpty() && stpOfflineDataDao.isDayAvailable(dayOfWeek)) {
                                OneBuildModelClass oneBuildModelClass = prepareAndSaveSTPModelClassOneBuild(day, date, dayName, dayOfWeek, localDate2);
                                if (holidayDateArray.contains(day)) {
                                    sessionListOneBuild.setWorkType(holidayWorkTypeModelOneBuild);  // add holiday work type model object when current date is declared as holiday
                                    ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                                    sessionLists.add(sessionListOneBuild);
                                    oneBuildModelClass = new OneBuildModelClass(day, date, dayName, month, year, true, sessionLists);
                                } else if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                    sessionListOneBuild.setWorkType(weeklyOffWorkTypeModelOneBuild);
                                    ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                                    sessionLists.add(sessionListOneBuild);
                                    oneBuildModelClass = new OneBuildModelClass(day, date, dayName, month, year, true, sessionLists);
                                }
                                oneBuildModelClasses.add(oneBuildModelClass);
                                LocalWeelyHolidayFlag = true;
                            } else {
                                if (holidayDateArray.contains(day)) {
                                    sessionListOneBuild.setWorkType(holidayWorkTypeModelOneBuild);  // add holiday work type model object when current date is declared as holiday
                                    LocalWeelyHolidayFlag = true;
                                } else if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                    sessionListOneBuild.setWorkType(weeklyOffWorkTypeModelOneBuild);
                                    LocalWeelyHolidayFlag = true;
                                } else {
                                    LocalWeelyHolidayFlag = false;
                                }
                                ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                                sessionLists.add(sessionListOneBuild);
                                OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, month, year, true, sessionLists);
                                oneBuildModelClasses.add(oneBuildModelClass);
                            }
                        }
                        if (LocalWeelyHolidayFlag) {
                            saveTpLocalOneBuild(oneBuildModelClasses, day, monthYear, "1");
                        } else {
                            saveTpLocalOneBuild(oneBuildModelClasses, day, monthYear, "0");
                        }
                    } else {
                        ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                        OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, "", "", "", "", true, sessionLists);
                        oneBuildModelClasses.add(oneBuildModelClass);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("--Errr--", "" + e);
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        oneBuildModelClassList = oneBuildModelClasses;
        return oneBuildModelClasses;
    }

    private void prepareDoctorVisitDataOneBuild(ArrayList<OneBuildModelClass> oneBuildModelClassList) {
        if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && (visitFrequencyNeed.equalsIgnoreCase("0") || Integer.parseInt(minimumGap) > 0)) {
            try {
//            doctorVisitMap = new HashMap<>();
                for (DoctorVisitModel doctorVisitModel : doctorVisitMap.values()) {
                    doctorVisitModel.setPlannedVisit(0);
                    doctorVisitModel.setPlannedDates(new HashSet<>());
                }
                for (OneBuildModelClass day : oneBuildModelClassList) {
                    String dayNo = day.getDayNo();
                    if (day.getSessionList() == null || day.getSessionList().isEmpty()) continue;
                    for (OneBuildModelClass.SessionList session : day.getSessionList()) {
                        if (session == null || session.getWorkType() == null) continue;
                        if (!"F".equalsIgnoreCase(session.getWorkType().getFWFlg())) continue;
                        if (session.getDoctors() == null || session.getDoctors().isEmpty())
                            continue;
                        for (OneBuildModelClass.SessionList.SubClass doctorSub : session.getDoctors()) {
                            if (doctorSub == null) continue;
                            DoctorDataModel doctorDataModel = doctorDataMap.get(doctorSub.getCode());
                            if (doctorDataModel == null) continue;
                            DoctorVisitModel doctorVisitModel = doctorVisitMap.get(doctorDataModel.getCode());
                            if (doctorVisitModel == null) {
                                Set<String> dates = new HashSet<>();
                                dates.add(dayNo);
                                doctorVisitModel = new DoctorVisitModel(doctorDataModel.getCode(), doctorDataModel.getName(), doctorDataModel.getCategory(), dates, doctorDataModel.getVisitCount(), 1);
                            } else {
                                Set<String> dates = doctorVisitModel.getPlannedDates();
                                dates.add(dayNo);
                                doctorVisitModel.setPlannedDates(dates);
                                doctorVisitModel.setPlannedVisit(doctorVisitModel.getPlannedDates().size());
                            }
                            doctorVisitMap.put(doctorDataModel.getCode(), doctorVisitModel);
                        }
                    }
                }
                Log.d("Doc Map", "prepareDoctorVisitData: " + doctorVisitMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareDoctorVisitData(ArrayList<ModelClass> modelClassList) {
        if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") && (visitFrequencyNeed.equalsIgnoreCase("0") || Integer.parseInt(minimumGap) > 0)) {
            try {
//            doctorVisitMap = new HashMap<>();
                for (DoctorVisitModel doctorVisitModel : doctorVisitMap.values()) {
                    doctorVisitModel.setPlannedVisit(0);
                    doctorVisitModel.setPlannedDates(new HashSet<>());
                }
                for (ModelClass day : modelClassList) {
                    String dayNo = day.getDayNo();
                    if (day.getSessionList() == null || day.getSessionList().isEmpty()) continue;
                    for (ModelClass.SessionList session : day.getSessionList()) {
                        if (session == null || session.getWorkType() == null) continue;
                        if (!"F".equalsIgnoreCase(session.getWorkType().getFWFlg())) continue;
                        if (session.getListedDr() == null || session.getListedDr().isEmpty())
                            continue;
                        for (ModelClass.SessionList.SubClass doctorSub : session.getListedDr()) {
                            if (doctorSub == null) continue;
                            DoctorDataModel doctorDataModel = doctorDataMap.get(doctorSub.getCode());
                            if (doctorDataModel == null) continue;
                            DoctorVisitModel doctorVisitModel = doctorVisitMap.get(doctorDataModel.getCode());
                            if (doctorVisitModel == null) {
                                Set<String> dates = new HashSet<>();
                                dates.add(dayNo);
                                doctorVisitModel = new DoctorVisitModel(doctorDataModel.getCode(), doctorDataModel.getName(), doctorDataModel.getCategory(), dates, doctorDataModel.getVisitCount(), 1);
                            } else {
                                Set<String> dates = doctorVisitModel.getPlannedDates();
                                dates.add(dayNo);
                                doctorVisitModel.setPlannedDates(dates);
                                doctorVisitModel.setPlannedVisit(doctorVisitModel.getPlannedDates().size());
                            }
                            doctorVisitMap.put(doctorDataModel.getCode(), doctorVisitModel);
                        }
                    }
                }
                Log.d("Doc Map", "prepareDoctorVisitData: " + doctorVisitMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void prepareDoctorVisitDataOld(ArrayList<OneBuildModelClass> oneBuildModelClassList) {
//        try {
//            doctorVisitMap = new HashMap<>();
//            for (OneBuildModelClass oneBuildModelClass : oneBuildModelClassList) {
//                for (OneBuildModelClass.SessionList sessionList : oneBuildModelClass.getSessionList()) {
//                    if (sessionList.getWorkType() != null) {
//                        String FWFlag = sessionList.getWorkType().getFWFlg();
//                        if (FWFlag.equalsIgnoreCase("F")) {
//                            for (OneBuildModelClass.SessionList.SubClass subClass : sessionList.getDoctors()) {
//                                if (subClass != null && doctorDataMap.containsKey(subClass.getCode())) {
//                                    DoctorDataModel doctorDataModel = doctorDataMap.get(subClass.getCode());
//                                    if (doctorVisitMap.containsKey(subClass.getCode())) {
//                                        DoctorVisitModel doctorVisitModel = doctorVisitMap.get(subClass.getCode());
//                                        if (doctorVisitModel != null) {
//                                            String visitDates = doctorVisitModel.getPlannedDates();
//                                            int visitCount = doctorVisitModel.getPlannedVisit();
//                                            visitDates += "," + oneBuildModelClass.getDayNo();
//                                            visitCount ++;
//                                            doctorVisitModel.setPlannedDates(visitDates);
//                                            doctorVisitModel.setPlannedVisit(visitCount);
//                                            doctorVisitMap.put(doctorVisitModel.getCode(), doctorVisitModel);
//                                        } else {
//                                            doctorVisitMap.put(doctorDataModel.getCode(), new DoctorVisitModel(doctorDataModel.getCode(), doctorDataModel.getName(), doctorDataModel.getCategory(), oneBuildModelClass.getDayNo(), doctorDataModel.getVisitCount(), 1));
//                                        }
//                                    } else {
//                                        doctorVisitMap.put(doctorDataModel.getCode(), new DoctorVisitModel(doctorDataModel.getCode(), doctorDataModel.getName(), doctorDataModel.getCategory(), oneBuildModelClass.getDayNo(), doctorDataModel.getVisitCount(), 1));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            Log.d("Doc Map", "prepareDoctorVisitData: " + doctorVisitMap.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private ModelClass prepareAndSaveSTPModelClass(String day, String date, String dayName, String dayOfWeek, LocalDate localDate1) {
        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDay(dayOfWeek, SharedPref.getHqCode(TourPlanActivity.this));
        String monthYear = monthYearFromDate(localDate1);
        String month = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_31, monthYear);
        String year = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);
        ArrayList<ModelClass> modelClasses = new ArrayList<>();
        ModelClass.SessionList sessionList = prepareSessionListForAdapter();
        if (stpOfflineDataTable != null) {
            sessionList.setHQ(new ModelClass.SessionList.SubClass(SharedPref.getHqName(TourPlanActivity.this), SharedPref.getHqCode(TourPlanActivity.this)));

            ModelClass.SessionList.WorkType workType = getWorkType("F");
            sessionList.setWorkType(workType);

            List<ModelClass.SessionList.SubClass> clusterList = prepareList(stpOfflineDataTable.getClusterCode(), stpOfflineDataTable.getClusterName());
            List<ModelClass.SessionList.SubClass> doctorList = prepareList(stpOfflineDataTable.getDoctorCode(), stpOfflineDataTable.getDoctorName());
            List<ModelClass.SessionList.SubClass> chemistList = prepareList(stpOfflineDataTable.getChemistCode(), stpOfflineDataTable.getChemistName());
            sessionList.setCluster(clusterList);
            sessionList.setListedDr(doctorList);
            sessionList.setChemist(chemistList);

            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
            sessionLists.add(sessionList);

            ModelClass modelClass = new ModelClass(day, date, dayName, month, year, false, sessionLists, stpOfflineDataTable.getDayID(), stpOfflineDataTable.getDayCaption());
//            modelClasses.add(modelClass);

//            saveTpLocal(modelClasses, day, monthYear, "0");
            return modelClass;
        }
        return new ModelClass();
    }

    private OneBuildModelClass prepareAndSaveSTPModelClassOneBuild(String day, String date, String dayName, String dayOfWeek, LocalDate localDate2) {
//        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDay(dayOfWeek, SharedPref.getHqCode(TourPlanActivity.this));
        String monthYear = monthYearFromDate(localDate2);
        String month = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_31, monthYear);
        String year = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);
//        ArrayList<OneBuildModelClass> oneBuildModelClasses = new ArrayList<>();
        OneBuildModelClass.SessionList sessionListOneBuild = prepareSessionListForAdapterOneBuild();
        if (stpOfflineDataTable != null) {
            sessionListOneBuild.setHeadquarters(new OneBuildModelClass.SessionList.SubClass(SharedPref.getHqName(TourPlanActivity.this), SharedPref.getHqCode(TourPlanActivity.this)));

            OneBuildModelClass.SessionList.WorkType workType = getWorkTypeOneBuild("F");
            sessionListOneBuild.setWorkType(workType);

            List<OneBuildModelClass.SessionList.SubClass> clusterList = prepareListOneBuild(stpOfflineDataTable.getClusterCode(), stpOfflineDataTable.getClusterName());
            List<OneBuildModelClass.SessionList.SubClass> doctorList = prepareListOneBuild(stpOfflineDataTable.getDoctorCode(), stpOfflineDataTable.getDoctorName());
            List<OneBuildModelClass.SessionList.SubClass> chemistList = prepareListOneBuild(stpOfflineDataTable.getChemistCode(), stpOfflineDataTable.getChemistName());
            sessionListOneBuild.setTerritories(clusterList);
            sessionListOneBuild.setDoctors(doctorList);
            sessionListOneBuild.setChemists(chemistList);

            sessionListOneBuild.setSTPCode(stpOfflineDataTable.getDayID());
            sessionListOneBuild.setSTPName(stpOfflineDataTable.getDayCaption());

            ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
            sessionLists.add(sessionListOneBuild);

            OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, month, year, false, sessionLists, stpOfflineDataTable.getDayID(), stpOfflineDataTable.getDayCaption());
//            modelClasses.add(modelClass);
//            saveTpLocal(modelClasses, day, monthYear, "0");
            return oneBuildModelClass;
        }
        return new OneBuildModelClass();
    }

    private OneBuildModelClass.SessionList prepareAndSaveSTP(String hqCode, String hqName, String dayOfWeek) {
        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDay(dayOfWeek, hqCode);
        OneBuildModelClass.SessionList sessionListOneBuild = prepareSessionListForAdapterOneBuild();
        if (stpOfflineDataTable != null) {
            sessionListOneBuild.setHeadquarters(new OneBuildModelClass.SessionList.SubClass(hqName, hqCode));

            OneBuildModelClass.SessionList.WorkType workType = getWorkTypeOneBuild("F");
            sessionListOneBuild.setWorkType(workType);

            List<OneBuildModelClass.SessionList.SubClass> clusterList = prepareListOneBuild(stpOfflineDataTable.getClusterCode(), stpOfflineDataTable.getClusterName());
            List<OneBuildModelClass.SessionList.SubClass> doctorList = prepareListOneBuild(stpOfflineDataTable.getDoctorCode(), stpOfflineDataTable.getDoctorName());
            List<OneBuildModelClass.SessionList.SubClass> chemistList = prepareListOneBuild(stpOfflineDataTable.getChemistCode(), stpOfflineDataTable.getChemistName());
            sessionListOneBuild.setTerritories(clusterList);
            sessionListOneBuild.setDoctors(doctorList);
            sessionListOneBuild.setChemists(chemistList);

            sessionListOneBuild.setSTPCode(stpOfflineDataTable.getDayID());
            sessionListOneBuild.setSTPName(stpOfflineDataTable.getDayCaption());

            return sessionListOneBuild;
        }
        return new OneBuildModelClass.SessionList();
    }

    private OneBuildModelClass prepareAndSaveSTPModelClassOneBuildMGR(String day, String date, String dayName, String dayOfWeek, LocalDate localDate2, String hqCode, String hqName) {
//        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDay(dayOfWeek, SharedPref.getHqCode(TourPlanActivity.this));
        String monthYear = monthYearFromDate(localDate2);
        String month = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_31, monthYear);
        String year = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);
//        ArrayList<OneBuildModelClass> oneBuildModelClasses = new ArrayList<>();
        OneBuildModelClass.SessionList sessionListOneBuild = prepareSessionListForAdapterOneBuild();
        if (stpOfflineDataTable != null) {
            sessionListOneBuild.setHeadquarters(new OneBuildModelClass.SessionList.SubClass(hqName, hqCode));

            OneBuildModelClass.SessionList.WorkType workType = getWorkTypeOneBuild("F");
            sessionListOneBuild.setWorkType(workType);

            List<OneBuildModelClass.SessionList.SubClass> clusterList = prepareListOneBuild(stpOfflineDataTable.getClusterCode(), stpOfflineDataTable.getClusterName());
            List<OneBuildModelClass.SessionList.SubClass> doctorList = prepareListOneBuild(stpOfflineDataTable.getDoctorCode(), stpOfflineDataTable.getDoctorName());
            List<OneBuildModelClass.SessionList.SubClass> chemistList = prepareListOneBuild(stpOfflineDataTable.getChemistCode(), stpOfflineDataTable.getChemistName());
            sessionListOneBuild.setTerritories(clusterList);
            sessionListOneBuild.setDoctors(doctorList);
            sessionListOneBuild.setChemists(chemistList);

            sessionListOneBuild.setSTPCode(stpOfflineDataTable.getDayID());
            sessionListOneBuild.setSTPName(stpOfflineDataTable.getDayCaption());

            ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
            sessionLists.add(sessionListOneBuild);

            OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, month, year, false, sessionLists, stpOfflineDataTable.getDayID(), stpOfflineDataTable.getDayCaption());
//            modelClasses.add(modelClass);
//            saveTpLocal(modelClasses, day, monthYear, "0");
            return oneBuildModelClass;
        }
        return new OneBuildModelClass();
    }

    private void getSTPMGR(OneBuildModelClass arrayListOneBuild, int position, String hqCode, String hqName, String dayOfWeek, String dayName) {
        if (UtilityClass.isNetworkAvailable(TourPlanActivity.this)) {
            try {
                binding.progressBar.setVisibility(View.VISIBLE);
                apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                jsonObject.put("tableName", "getstp_details_mgr");
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                jsonObject.put("workday", dayOfWeek);

                Log.v("STP", "--json-- " + jsonObject);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "get/stp");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();

                        if (response.isSuccessful()) {
                            Log.e("test STP MGR", "response : " + Objects.requireNonNull(response.body()));
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                        jsonArray = new JSONArray(jsonArray1.toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                        JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                        if (!jsonObject2.has("success")) {
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(Constants.STANDARD_TOUR_PLAN, 1);
                                        }
                                    }

                                    if (success) {
                                        if (jsonArray.length() == 0) {
                                            CommonUtilsMethods.showToastMessage(TourPlanActivity.this, "No STP plan available");
                                        } else {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STANDARD_TOUR_PLAN, jsonArray.toString(), 2));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                        stpOfflineDataDao.deleteAllData("0");
                            if (jsonArray.length() == 0) {
                                CommonUtilsMethods.showToastMessage(TourPlanActivity.this, "No STP plan available");
                            } else {
                                saveSTPDataToLocal(hqCode);
                                OneBuildModelClass.SessionList sessionList = prepareAndSaveSTP(hqCode, hqName, dayOfWeek);
                                arrayListOneBuild.getSessionList().remove(position);
                                arrayListOneBuild.getSessionList().add(position, sessionList);
                                for (int i = 0; i < arrayListOneBuild.getSessionList().size(); i++) {
                                    arrayListOneBuild.getSessionList().get(i).setVisible(true);
                                }

                                populateSessionEditAdapterOneBuild(arrayListOneBuild);
                            }
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        Log.e("STP", "onFailure: ");
                        binding.progressBar.setVisibility(View.GONE);
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        } else {
            binding.progressBar.setVisibility(View.GONE);
            CommonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
        }
    }

    private int getCountFromCommaString(String value) {
        if (value == null || value.trim().isEmpty()) return 0;

        int count = 0;
        for (String s : value.split(",")) {
            if (!s.trim().isEmpty()) count++;
        }
        return count;
    }

    private void saveSTPDataToLocal(String hqCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
            JSONArray jsonDoc_mas = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getSfCode(TourPlanActivity.this)).getMasterSyncDataJsonArray();
            JSONArray jsonChm_mas = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + SharedPref.getSfCode(TourPlanActivity.this)).getMasterSyncDataJsonArray();
            JSONArray jsonCluster = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getSfCode(TourPlanActivity.this)).getMasterSyncDataJsonArray();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dayID = jsonObject.optString("Day_Plan_ShortName");
                        String dayCaption = jsonObject.optString("Day_Plan_Name");
                        String dayPlanCode = jsonObject.optString("Day_Plan_Code");
                        String clusterCode = jsonObject.optString("Patch_Code");
                        String clusterName = jsonObject.optString("Patch_Name");
                        String doctorCode = jsonObject.optString("Dr_Code");
                        String doctorName = jsonObject.optString("Dr_Name");
                        String chemistCode = jsonObject.optString("Chem_Code");
                        String chemistName = jsonObject.optString("Chem_Name");
                        String doctorSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Dr_Code")));
                        String chemistSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Chem_Code")));
                        String clusterSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Patch_Code")));
                        String doctorSpeciality = jsonObject.optString("Speciality_Name");
                        String doctorCategory = jsonObject.optString("CategoryName");
                        String doctorCategoryCode = jsonObject.optString("CategoryCode");
                        String doctorClass = jsonObject.optString("Class_Name");
                        String dateTime = jsonObject.optString("Created_Date");
                        String activeFlag = jsonObject.optString("Active_Flag");
                        Log.d("STP master data", "saveSTPDataToLocal1: " + jsonObject);

                        JSONObject jsonSave = new JSONObject();
                        jsonSave = CommonUtilsMethods.CommonObjectParameter(this);
                        jsonSave.put("sfcode", hqCode);
                        jsonSave.put("DivCode", SharedPref.getDivisionCode(this));
                        jsonSave.put("Rsf", SharedPref.getHqCode(this));
                        jsonSave.put("town_code", clusterCode);
                        jsonSave.put("town_name", clusterName);
                        jsonSave.put("Doctor_Id", doctorCode);
                        jsonSave.put("Doctor_Name", doctorName);
                        jsonSave.put("Chemist_Id", chemistCode);
                        jsonSave.put("Chemist_Name", chemistName);
                        jsonSave.put("Planned_Territory_Count", clusterSize + " (" + jsonCluster.length() + ")");
                        jsonSave.put("Planned_Doctor_Count", doctorSize + " (" + jsonDoc_mas.length() + ")");
                        jsonSave.put("Planned_Chemist_Count", chemistSize + " (" + jsonChm_mas.length() + ")");
                        jsonSave.put("Planned_Hospital_Count", "0" + " (" + "0" + ")");
                        jsonSave.put("Speciality_Name", doctorSpeciality);
                        jsonSave.put("Category_Name", doctorCategory);
                        jsonSave.put("Class_Name", doctorClass);
                        jsonSave.put("Plan_Name", dayCaption);
                        jsonSave.put("Plan_SName", dayID);
                        jsonSave.put("Plan_Code", dayPlanCode);
                        jsonSave.put("StpFlag", activeFlag);
                        jsonSave.put("tableName", "save_stp");
                        jsonSave.put("ReqDt", dateTime);
                        Log.d("STP save data", "saveSTPDataToLocal2: " + jsonSave);
                        int stpFlag = 3;
                        try {
                            stpFlag = Integer.parseInt(activeFlag);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, hqCode, dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, doctorSpeciality, doctorCategory, doctorClass, doctorCategoryCode, jsonObject.toString(), stpFlag, "0"));
                    } else {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dayID = jsonObject.optString("Day_Plan_ShortName");
                        String dayCaption = jsonObject.optString("Day_Plan_Name");
                        String dayPlanCode = jsonObject.optString("Day_Plan_Code");
                        String clusterCode = jsonObject.optString("Patch_Code");
                        String clusterName = jsonObject.optString("Patch_Name");
                        String doctorCode = jsonObject.optString("Dr_Code");
                        String doctorName = jsonObject.optString("Dr_Name");
                        String chemistCode = jsonObject.optString("Chem_Code");
                        String chemistName = jsonObject.optString("Chem_Name");
                        String dateTime = jsonObject.optString("Created_Date");
                        String activeFlag = jsonObject.optString("Active_Flag");
                        Log.d("STP master data", "saveSTPDataToLocal: " + jsonObject);

                        JSONObject jsonSave = new JSONObject();
                        jsonSave = CommonUtilsMethods.CommonObjectParameter(this);
                        jsonSave.put("sfcode", hqCode);
                        jsonSave.put("DivCode", SharedPref.getDivisionCode(this));
                        jsonSave.put("Rsf", SharedPref.getHqCode(this));
                        jsonSave.put("town_code", clusterCode);
                        jsonSave.put("town_name", clusterName);
                        jsonSave.put("Doctor_Id", doctorCode);
                        jsonSave.put("Doctor_Name", doctorName);
                        jsonSave.put("Chemist_Id", chemistCode);
                        jsonSave.put("Chemist_Name", chemistName);
                        jsonSave.put("Plan_Name", dayCaption);
                        jsonSave.put("Plan_SName", dayID);
                        jsonSave.put("Plan_Code", dayPlanCode);
                        jsonSave.put("StpFlag", activeFlag);
                        jsonSave.put("tableName", "save_stp");
                        jsonSave.put("ReqDt", dateTime);
                        Log.d("STP save data", "saveSTPDataToLocal: " + jsonSave);
                        int stpFlag = 3;
                        try {
                            stpFlag = Integer.parseInt(activeFlag);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, hqCode, dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, jsonObject.toString(), stpFlag, "0"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ModelClass.SessionList.SubClass> prepareList(String codes, String names) {
        List<ModelClass.SessionList.SubClass> list = new ArrayList<>();
        try {
            String[] codeArray = CommonUtilsMethods.removeLastComma(codes).split(",");
            String[] nameArray = CommonUtilsMethods.removeLastComma(names).split(",");
            for (int index = 0; index < codeArray.length; index++) {
                if (!(codeArray[index].isEmpty() || nameArray[index].isEmpty())) {
                    list.add(new ModelClass.SessionList.SubClass(nameArray[index], codeArray[index]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<OneBuildModelClass.SessionList.SubClass> prepareListOneBuild(String codes, String names) {
        List<OneBuildModelClass.SessionList.SubClass> list = new ArrayList<>();
        try {
            String[] codeArray = CommonUtilsMethods.removeLastComma(codes).split(",");
            String[] nameArray = CommonUtilsMethods.removeLastComma(names).split(",");
            for (int index = 0; index < codeArray.length; index++) {
                if (!(codeArray[index].isEmpty() || nameArray[index].isEmpty())) {
                    list.add(new OneBuildModelClass.SessionList.SubClass(nameArray[index], codeArray[index]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getDayOfWeekOccurrence(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate firstDayOfWeekInMonth = firstDayOfMonth.with(TemporalAdjusters.firstInMonth(dayOfWeek));

        String occurrence;
        int daysBetween = date.getDayOfYear() - firstDayOfWeekInMonth.getDayOfYear();
        int weeksBetween = daysBetween / 7 + 1;
        if (weeksBetween > 4) weeksBetween = 1;
        occurrence = weeksBetween + "";

        return dayOfWeek.name().substring(0, 2) + occurrence;
    }

    public void populateCalendarAdapter(ArrayList<ModelClass> arrayList) {
        binding.monthYear.setText(monthYearFromDateUI(localDate));

        calendarAdapter = new CalendarAdapter(arrayList, TourPlanActivity.this, (position, date, modelClass) -> {
            if (!date.isEmpty()) {
                binding.tpDrawer.openDrawer(GravityCompat.END);
                binding.tpNavigation.planDate.setText(modelClass.getDate());
                ModelClass modelClass1 = new ModelClass(modelClass);
                if (!modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("")) {
                    binding.tpNavigation.addEditViewTxt.setText(R.string.view_plan);
                    populateSessionViewAdapter(modelClass1);
                } else {
                    binding.tpNavigation.addEditViewTxt.setText(getString(R.string.add_plan));
                    populateSessionEditAdapter(modelClass1);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        binding.calendarRecView.setLayoutManager(layoutManager);
        binding.calendarRecView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();
        populateSummaryAdapter(arrayList);
    }

    public void populateCalenderAdapterOneBuild(ArrayList<OneBuildModelClass> arrayListOneBuild) {
        binding.monthYear.setText(monthYearFromDateUI(localDate));

        calendarAdapter = new CalendarAdapter(TourPlanActivity.this, arrayListOneBuild, (position, date, oneBuildmodelClass) -> {
            if (!date.isEmpty()) {
                binding.tpDrawer.openDrawer(GravityCompat.END);
                binding.tpNavigation.planDate.setText(oneBuildmodelClass.getDate());
                OneBuildModelClass oneBuildModelClass1 = new OneBuildModelClass(oneBuildmodelClass);
                if (!oneBuildmodelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("")) {
                    binding.tpNavigation.addEditViewTxt.setText(R.string.view_plan);
                    populateSessionViewAdapterOneBuild(oneBuildModelClass1);
                } else {
                    binding.tpNavigation.addEditViewTxt.setText(R.string.add_plan);
                    populateSessionEditAdapterOneBuild(oneBuildModelClass1);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        binding.calendarRecView.setLayoutManager(layoutManager);
        binding.calendarRecView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();
        populateSummaryAdapterOneBuild(arrayListOneBuild);
    }

    public void populateSessionEditAdapter(ModelClass arrayList) {
        binding.tpDrawer.openDrawer(GravityCompat.END);
        if (modelClassList != null) {
            prepareDoctorVisitData(modelClassList);
        }
        sessionEditAdapter = new SessionEditAdapter(arrayList, TourPlanActivity.this, new SessionInterface() {
            @Override
            public void deleteClicked(ModelClass arrayList, int position) {
                inputDataArray.getSessionList().remove(position);
                populateSessionEditAdapter(inputDataArray);
            }

            @Override
            public void fieldWorkSelected(ModelClass arrayList, int position) {
                ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(arrayList.getSessionList().get(position).getWorkType());
                ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("", "");
                ArrayList<ModelClass.SessionList.SubClass> hqArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

                ArrayList<MultiHQHeaderModelClass> clustersArray = new ArrayList<>();
                ArrayList<MultiHQHeaderModelClass> jcsArray = new ArrayList<>();
                ArrayList<MultiHQHeaderModelClass> drsArray = new ArrayList<>();
                ArrayList<MultiHQHeaderModelClass> chemistsArray = new ArrayList<>();
                ArrayList<MultiHQHeaderModelClass> stocksArray = new ArrayList<>();
                ArrayList<MultiHQHeaderModelClass> unListedDrsArray = new ArrayList<>();
                ArrayList<MultiHQHeaderModelClass> cipsArray = new ArrayList<>();
                ArrayList<MultiHQHeaderModelClass> hospsArray = new ArrayList<>();

                ModelClass.SessionList modelClass = new ModelClass.SessionList("", true, "", workType, hq, hqArray, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray, clustersArray, jcsArray, drsArray, chemistsArray, stocksArray, unListedDrsArray, cipsArray, hospsArray);
                arrayList.getSessionList().remove(position);
                arrayList.getSessionList().add(position, modelClass);

                arrayList.setSTP_Code("");
                arrayList.setSTP_Name("");

                for (int i = 0; i < arrayList.getSessionList().size(); i++) {
                    arrayList.getSessionList().get(i).setVisible(true);
                }

                populateSessionEditAdapter(arrayList);
                scrollToPosition(position, false);
            }

            @Override
            public void hqChanged(ModelClass arrayList, int position, boolean changed) {
//                if (changed) {
//                    ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(arrayList.getSessionList().get(position).getWorkType());
//                    ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass(arrayList.getSessionList().get(position).getHQ());
//                    ArrayList<ModelClass.SessionList.SubClass> hqArray = new ArrayList<>(arrayList.getSessionList().get(position).getHQs());
//                    ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
//                    ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
//                    ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
//                    ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
//                    ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
//                    ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
//                    ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
//                    ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();
//
//                    ArrayList<MultiHQHeaderModelClass> clustersArray = new ArrayList<>();
//                    ArrayList<MultiHQHeaderModelClass> jcsArray = new ArrayList<>();
//                    ArrayList<MultiHQHeaderModelClass> drsArray = new ArrayList<>();
//                    ArrayList<MultiHQHeaderModelClass> chemistsArray = new ArrayList<>();
//                    ArrayList<MultiHQHeaderModelClass> stocksArray = new ArrayList<>();
//                    ArrayList<MultiHQHeaderModelClass> unListedDrsArray = new ArrayList<>();
//                    ArrayList<MultiHQHeaderModelClass> cipsArray = new ArrayList<>();
//                    ArrayList<MultiHQHeaderModelClass> hospsArray = new ArrayList<>();
//
//                    ModelClass.SessionList modelClass = new ModelClass.SessionList("", true, "", workType, hq, hqArray, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray, clustersArray, jcsArray, drsArray, chemistsArray, stocksArray, unListedDrsArray, cipsArray, hospsArray);
//                    arrayList.getSessionList().remove(position);
//                    arrayList.getSessionList().add(modelClass);
//                }
//                for (int i = 0; i < arrayList.getSessionList().size(); i++) {
//                    arrayList.getSessionList().get(i).setVisible(true);
//                    arrayList.getSessionList().get(i).setLayoutVisible("");
//                }
                populateSessionEditAdapter(arrayList);
                scrollToPosition(position, false);
            }

            @Override
            public void clusterChanged(ModelClass arrayList, int position) {
                populateSessionEditAdapter(arrayList);
                scrollToPosition(position, false);
            }

            @Override
            public void workDayChanged(ModelClass modelClass, int position) {
                ModelClass modelClass1 = prepareAndSaveSTPModelClass(modelClass.getDayNo(), modelClass.getDate(), modelClass.getDay(), modelClass.getSTP_Code(), localDate);
                arrayList.getSessionList().remove(position);
                arrayList.getSessionList().add(position, modelClass1.getSessionList().get(0));

                for (int i = 0; i < arrayList.getSessionList().size(); i++) {
                    arrayList.getSessionList().get(i).setVisible(true);
                }

                populateSessionEditAdapter(arrayList);
                scrollToPosition(position, false);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionEditAdapter);

        addSaveBtnLayout.setVisibility(View.VISIBLE);
        binding.tpNavigation.editLayout.setVisibility(View.GONE);
    }

    public void populateSessionEditAdapterOneBuild(OneBuildModelClass arrayListOneBuild) {
        binding.tpDrawer.openDrawer(GravityCompat.END);
        if (oneBuildModelClassList != null) {
            prepareDoctorVisitDataOneBuild(oneBuildModelClassList);
        }
        sessionEditAdapter = new SessionEditAdapter(TourPlanActivity.this, arrayListOneBuild, new SessionInterfaceOneBuild() {

            @Override
            public void deleteClickedOneBuild(OneBuildModelClass oneBuildModelClass, int position) {
                inputDataArrayOneBuild.getSessionList().remove(position);
                populateSessionEditAdapterOneBuild(inputDataArrayOneBuild);
            }

            @Override
            public void fieldWorkSelectedOneBuild(OneBuildModelClass oneBuildModelClass, int position) {
                OneBuildModelClass.SessionList.WorkType workType = new OneBuildModelClass.SessionList.WorkType(arrayListOneBuild.getSessionList().get(position).getWorkType());
                OneBuildModelClass.SessionList.SubClass hq = new OneBuildModelClass.SessionList.SubClass("", "");
                ArrayList<OneBuildModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
                ArrayList<OneBuildModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
                ArrayList<OneBuildModelClass.SessionList.SubClass> drArray = new ArrayList<>();
                ArrayList<OneBuildModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
                ArrayList<OneBuildModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
                ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
                ArrayList<OneBuildModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
                ArrayList<OneBuildModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

                OneBuildModelClass.SessionList oneBuildModelClasses = new OneBuildModelClass.SessionList("", true, "", "", "", "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                arrayListOneBuild.getSessionList().remove(position);
                arrayListOneBuild.getSessionList().add(position, oneBuildModelClasses);

                arrayListOneBuild.setSTP_Code("");
                arrayListOneBuild.setSTP_Name("");

                for (int i = 0; i < arrayListOneBuild.getSessionList().size(); i++) {
                    arrayListOneBuild.getSessionList().get(i).setVisible(true);
                }

                populateSessionEditAdapterOneBuild(arrayListOneBuild);
                scrollToPosition(position, false);
            }

            @Override
            public void hqChangedOneBuild(OneBuildModelClass oneBuildModelClass, int position, boolean changed) {
                if (changed) {
                    OneBuildModelClass.SessionList.WorkType workType = new OneBuildModelClass.SessionList.WorkType(arrayListOneBuild.getSessionList().get(position).getWorkType());
                    OneBuildModelClass.SessionList.SubClass hq = new OneBuildModelClass.SessionList.SubClass(arrayListOneBuild.getSessionList().get(position).getHeadquarters());
                    ArrayList<OneBuildModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
                    ArrayList<OneBuildModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
                    ArrayList<OneBuildModelClass.SessionList.SubClass> drArray = new ArrayList<>();
                    ArrayList<OneBuildModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
                    ArrayList<OneBuildModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
                    ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
                    ArrayList<OneBuildModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
                    ArrayList<OneBuildModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

                    OneBuildModelClass.SessionList modelClass = new OneBuildModelClass.SessionList("", true, "", "", "", "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                    arrayListOneBuild.getSessionList().remove(position);
                    arrayListOneBuild.getSessionList().add(modelClass);
                }
                for (int i = 0; i < arrayListOneBuild.getSessionList().size(); i++) {
                    arrayListOneBuild.getSessionList().get(i).setVisible(true);
                    arrayListOneBuild.getSessionList().get(i).setLayoutVisible("");
                }
                arrayListOneBuild.setSTP_Code("");
                arrayListOneBuild.setSTP_Name("");
                populateSessionEditAdapterOneBuild(arrayListOneBuild);
                scrollToPosition(position, false);
            }

            @Override
            public void clusterChangedOneBuild(OneBuildModelClass oneBuildModelClass, int position) {
                populateSessionEditAdapterOneBuild(arrayListOneBuild);
                scrollToPosition(position, false);
            }

            @Override
            public void workDayChangedOneBuild(OneBuildModelClass oneBuildModelClass, String stpCode, String stpName, int position) {
                if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
//                    OneBuildModelClass modelClass1 = prepareAndSaveSTPModelClassOneBuild(oneBuildModelClass.getDayNo(), oneBuildModelClass.getDate(), oneBuildModelClass.getDay(), oneBuildModelClass.getSTP_Code(), localDate);
                    OneBuildModelClass.SessionList sessionList = prepareAndSaveSTP(SharedPref.getHqCode(TourPlanActivity.this), SharedPref.getHqName(TourPlanActivity.this), stpCode);
                    arrayListOneBuild.getSessionList().remove(position);
                    arrayListOneBuild.getSessionList().add(position, sessionList);

                    for (int i = 0; i < arrayListOneBuild.getSessionList().size(); i++) {
                        arrayListOneBuild.getSessionList().get(i).setVisible(true);
                    }

                    populateSessionEditAdapterOneBuild(arrayListOneBuild);
                } else {
                    String selectedHQCode = "", selectedHQName = "";
//                    for (OneBuildModelClass.SessionList sessionList : oneBuildModelClass.getSessionList()) {
                    OneBuildModelClass.SessionList sessionList = oneBuildModelClass.getSessionList().get(position);
                        if (sessionList.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            selectedHQCode = sessionList.getHeadquarters().getCode();
                            selectedHQName = sessionList.getHeadquarters().getName();
//                            break;
                        }
//                    }
                    if (UtilityClass.isNetworkAvailable(TourPlanActivity.this)) {
                        getSTPMGR(arrayListOneBuild, position, selectedHQCode, selectedHQName, stpCode, stpName);
                    } else {
                        CommonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
                    }
                }
                scrollToPosition(position, false);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionEditAdapter);

        addSaveBtnLayout.setVisibility(View.VISIBLE);
        binding.tpNavigation.editLayout.setVisibility(View.GONE);
    }

    public void populateSessionViewAdapter(ModelClass modelClass) {
        binding.tpDrawer.openDrawer(GravityCompat.END);
        sessionViewAdapter = new SessionViewAdapter(modelClass, TourPlanActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionViewAdapter);

        addSaveBtnLayout.setVisibility(View.GONE);
        clrSaveBtnLayout.setVisibility(View.GONE);
        binding.tpNavigation.editLayout.setVisibility(View.GONE);
        if (modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("W")) {
            if (weeklyOffEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        } else if (modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("H")) {
            if (holidayEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        } else {
            binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
        }
    }

    public void populateSessionViewAdapterOneBuild(OneBuildModelClass oneBuildModelClass) {
        binding.tpDrawer.openDrawer(GravityCompat.END);
        sessionViewAdapter = new SessionViewAdapter(oneBuildModelClass, TourPlanActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionViewAdapter);

        addSaveBtnLayout.setVisibility(View.GONE);
        clrSaveBtnLayout.setVisibility(View.GONE);
        if (oneBuildModelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("W")) {
            if (weeklyOffEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            } else {
                binding.tpNavigation.editLayout.setVisibility(View.GONE);
            }
        } else if (oneBuildModelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("H")) {
            if (holidayEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            } else {
                binding.tpNavigation.editLayout.setVisibility(View.GONE);
            }
        } else {
            binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
        }
    }

    public void populateSummaryAdapter(ArrayList<ModelClass> arrayList) {
        try {
            ArrayList<ModelClass> modelClasses = new ArrayList<>();
            int fw = 0, nfw = 0, wo = 0, ho = 0, l = 0;
            for (ModelClass modelClass : arrayList) {
                if (!modelClass.getDayNo().isEmpty() && !modelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) {
                    modelClasses.add(modelClass);
                }
                try {
                    boolean isFWFound = false, isNFWFound = false, isWOFound = false, isHoFound = false, isLFound = false;
                    for (ModelClass.SessionList sessionList : modelClass.getSessionList()) {
                        String fwFlag = sessionList.getWorkType().getFWFlg();
                        if (fwFlag.equalsIgnoreCase("F")) {
                            isFWFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("L")) {
                            isLFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("H")) {
                            isHoFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("W")) {
                            isWOFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("N")) {
                            isNFWFound = true;
                        }
                    }
                    if (isFWFound) fw++;
                    else if (isLFound) l++;
                    else if (isWOFound) wo++;
                    else if (isHoFound) ho++;
                    else if (isNFWFound) nfw++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            binding.fwCount.setText(String.valueOf(fw));
            binding.nfwCount.setText(String.valueOf(nfw));
            binding.leaveCount.setText(String.valueOf(l));
            binding.weekOffCount.setText(String.valueOf(wo));
            binding.holidayCount.setText(String.valueOf(ho));

            summaryAdapter = new SummaryAdapter(modelClasses, TourPlanActivity.this, (modelClass, position) -> {
                populateSessionViewAdapter(modelClass);
                binding.tpNavigation.addEditViewTxt.setText(R.string.view_plan);
                binding.tpNavigation.planDate.setText(modelClass.getDate());
            });
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
            binding.summaryRecView.setLayoutManager(layoutManager);
            binding.summaryRecView.setAdapter(summaryAdapter);

            changeApprovalBtnState(arrayList);
        } catch (Exception e) {
            Log.v("error", "---" + e);
        }
    }

    public void populateSummaryAdapterOneBuild(ArrayList<OneBuildModelClass> arrayListOneBuild) {
        try {
            ArrayList<OneBuildModelClass> oneBuildModelClasses = new ArrayList<>();
            int fw = 0, nfw = 0, wo = 0, ho = 0, l = 0;
            for (OneBuildModelClass oneBuildModelClass : arrayListOneBuild) {
                if (!oneBuildModelClass.getDayNo().isEmpty() && !oneBuildModelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) {
                    oneBuildModelClasses.add(oneBuildModelClass);
                }
                try {
                    boolean isFWFound = false, isNFWFound = false, isWOFound = false, isHoFound = false, isLFound = false;
                    for (OneBuildModelClass.SessionList sessionList : oneBuildModelClass.getSessionList()) {
                        String fwFlag = sessionList.getWorkType().getFWFlg();
                        if (fwFlag.equalsIgnoreCase("F")) {
                            isFWFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("L")) {
                            isLFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("H")) {
                            isHoFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("W")) {
                            isWOFound = true;
                            break;
                        } else if (fwFlag.equalsIgnoreCase("N")) {
                            isNFWFound = true;
                        }
                    }
                    if (isFWFound) fw++;
                    else if (isLFound) l++;
                    else if (isWOFound) wo++;
                    else if (isHoFound) ho++;
                    else if (isNFWFound) nfw++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            binding.fwCount.setText(String.valueOf(fw));
            binding.nfwCount.setText(String.valueOf(nfw));
            binding.leaveCount.setText(String.valueOf(l));
            binding.weekOffCount.setText(String.valueOf(wo));
            binding.holidayCount.setText(String.valueOf(ho));

            summaryAdapter = new SummaryAdapter(TourPlanActivity.this, oneBuildModelClasses, (oneBuildmodelClass, position) -> {
                populateSessionViewAdapterOneBuild(oneBuildmodelClass);
                binding.tpNavigation.addEditViewTxt.setText(R.string.view_plan);
                binding.tpNavigation.planDate.setText(oneBuildmodelClass.getDate());
            });
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
            binding.summaryRecView.setLayoutManager(layoutManager);
            binding.summaryRecView.setAdapter(summaryAdapter);

            changeApprovalBtnStateOneBuild(arrayListOneBuild);
        } catch (Exception e) {
            Log.v("error", "---" + e);
        }
    }

    public void changeApprovalBtnState(ArrayList<ModelClass> arrayList) { // To set send to approval btn enable/disable  based on syncStatus and  workType
        boolean wholeMonthTpCompleted = false, isDataAvailable = false;
        for (int i = 0; i < arrayList.size(); i++) { // to enable/disable the send to approval button
            if (!arrayList.get(i).getDayNo().isEmpty()) {
                ModelClass.SessionList.WorkType workType = arrayList.get(i).getSessionList().get(0).getWorkType();
                if (!workType.getName().isEmpty()) {
                    wholeMonthTpCompleted = true;
                    isDataAvailable = true;
                } else {
                    if (Integer.valueOf(arrayList.get(i).getDayNo()) < TourPlanActivity.JoningDate && Integer.valueOf(arrayList.get(i).getMonth()) == TourPlanActivity.JoiningMonth && Integer.valueOf(arrayList.get(i).getYear()) == TourPlanActivity.JoinYear) {
                        wholeMonthTpCompleted = true;
                    } else {
                        wholeMonthTpCompleted = false;
                        break;
                    }
                }
            }
        }
        String status = "";
        String reason = "";
        TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
        if (tourPlanOfflineDataTable != null) {
            status = tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty();
            reason = tourPlanOfflineDataTable.getTpRejectionReasonOrEmpty();
        }

        switch (status) {
            case "1":  // when waiting for approval
                binding.tpNavigation.sessionEdit.setEnabled(false);
                binding.rejectedReasonTxt.setText("");
                binding.rejectionReasonLayout.setVisibility(View.GONE);
                break;
            case "3":  //Approved by manager
                binding.rejectionReasonLayout.setVisibility(View.GONE);
                binding.rejectedReasonTxt.setText(reason);
                binding.tpNavigation.sessionEdit.setEnabled(false);
                break;
            case "2":  //Rejected by manager
                binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                binding.tpNavigation.sessionEdit.setEnabled(true);
                binding.rejectedReasonTxt.setText(reason);
                break;
            case "":
            case "0":
            case "-1":  // when planning(0),monthly status send call failed(-1)
                binding.rejectionReasonLayout.setVisibility(View.GONE);
                binding.tpNavigation.sessionEdit.setEnabled(true);
                binding.rejectedReasonTxt.setText("");
                break;
        }

        binding.tpSendToApproval.setEnabled(wholeMonthTpCompleted && (status.equals("0") || status.equals("-1") || status.equals("") || status.equals("2")));

        switch (status) {
            case "":
            case "0": {
                binding.tpStatusTxt.setText(R.string.planning);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                break;
            }
            case "-1": {
                binding.tpStatusTxt.setText(R.string.tp_pending);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                break;
            }
            case "1": {
                binding.tpStatusTxt.setText(R.string.tp_waiting_for_approval);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                break;
            }
            case "2": {
                binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                binding.tpStatusTxt.setText(R.string.tp_rejected);
                binding.tpStatusTxt.setTextColor(getColor(R.color.pink));
                break;
            }
            case "3": {
                binding.tpStatusTxt.setText(R.string.tp_approved);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                SetTpRangeStatus();
                break;
            }
        }

        if (!isDataAvailable) {
            binding.rejectionReasonLayout.setVisibility(View.GONE);
            binding.rejectedReasonTxt.setText("");
            binding.tpStatusTxt.setText(R.string.planning);
        }
    }

    public void changeApprovalBtnStateOneBuild(ArrayList<OneBuildModelClass> arrayList) {
        boolean wholeMonthTpCompleted = false, isDataAvailable = false;
        for (int i = 0; i < arrayList.size(); i++) { // to enable/disable the send to approval button
            if (!arrayList.get(i).getDayNo().isEmpty()) {
                OneBuildModelClass.SessionList.WorkType workType = arrayList.get(i).getSessionList().get(0).getWorkType();
                if (!workType.getName().isEmpty()) {
                    wholeMonthTpCompleted = true;
                    isDataAvailable = true;
                } else {
                    if (Integer.valueOf(arrayList.get(i).getDayNo()) < TourPlanActivity.JoningDate && Integer.valueOf(arrayList.get(i).getMonth()) == TourPlanActivity.JoiningMonth && Integer.valueOf(arrayList.get(i).getYear()) == TourPlanActivity.JoinYear) {
                        wholeMonthTpCompleted = true;
                    } else {
                        wholeMonthTpCompleted = false;
                        break;
                    }
                }
            }
        }
        String status = "";
        String reason = "";
        TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
        if (tourPlanOfflineDataTable != null) {
            status = tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty();
            reason = tourPlanOfflineDataTable.getTpRejectionReasonOrEmpty();
        }

        switch (status) {
            case "1":  // when waiting for approval
                binding.tpNavigation.sessionEdit.setEnabled(false);
                binding.rejectedReasonTxt.setText("");
                binding.rejectionReasonLayout.setVisibility(View.GONE);
                calendarAdapter.notifyDataSetChanged();
                break;
            case "3":  //Approved by manager
                binding.rejectionReasonLayout.setVisibility(View.GONE);
                binding.rejectedReasonTxt.setText(reason);
                binding.tpNavigation.sessionEdit.setEnabled(false);
                calendarAdapter.notifyDataSetChanged();
                break;
            case "2":  //Rejected by manager
                binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                binding.tpNavigation.sessionEdit.setEnabled(true);
                binding.rejectedReasonTxt.setText(reason);
                calendarAdapter.notifyDataSetChanged();
                break;
            case "":
            case "0":
            case "-1":  // when planning(0),monthly status send call failed(-1)
                binding.rejectionReasonLayout.setVisibility(View.GONE);
                binding.tpNavigation.sessionEdit.setEnabled(true);
                binding.rejectedReasonTxt.setText("");
                calendarAdapter.notifyDataSetChanged();

                break;
        }

        binding.tpSendToApproval.setEnabled(wholeMonthTpCompleted && (status.equals("0") || status.equals("-1") || status.equals("") || status.equals("2")));

        switch (status) {
            case "":
            case "0": {
                binding.tpStatusTxt.setText(R.string.planning);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                calendarAdapter.notifyDataSetChanged();
                break;
            }
            case "-1": {
                binding.tpStatusTxt.setText(R.string.tp_pending);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                calendarAdapter.notifyDataSetChanged();
                break;
            }
            case "1": {
                binding.tpStatusTxt.setText(R.string.tp_waiting_for_approval);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                calendarAdapter.notifyDataSetChanged();
                break;
            }
            case "2": {
                binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                binding.tpStatusTxt.setText(R.string.tp_rejected);
                binding.tpStatusTxt.setTextColor(getColor(R.color.pink));
                calendarAdapter.notifyDataSetChanged();
//                SetTpRangeStatus();
                break;
            }
            case "3": {
                binding.tpStatusTxt.setText(R.string.tp_approved);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                calendarAdapter.notifyDataSetChanged();
                SetTpRangeStatus();
                break;
            }
        }
        if (!isDataAvailable) {
            binding.rejectionReasonLayout.setVisibility(View.GONE);
            binding.rejectedReasonTxt.setText("");
            binding.tpStatusTxt.setText(R.string.planning);
            calendarAdapter.notifyDataSetChanged();
        }
    }

    public void scrollToPosition(int position, boolean fieldEmpty) {
        new Handler().postDelayed(() -> {
            if (fieldEmpty) {
                RecyclerView.ViewHolder holder = binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(position);
                if (holder != null) {
                    holder.itemView.findViewById(R.id.relativeLayout).setSelected(true);
                }
            }
            binding.tpNavigation.tpSessionRecView.scrollToPosition(position);
        }, 50);
    }

    private void syncTPSetup() {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
            if (status) {
                try {
                    apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                    JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(TourPlanActivity.this);
                    jsonObject.put("tableName", "gettpsetup");
                    jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
                    jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
                    jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                    Log.v("tpGetPlan", "--json--" + jsonObject);

                    Map<String, String> mapString = new HashMap<>();
                    mapString.put("axn", "table/setups");
                    Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            try {
                                Log.v("tpGetPlan", "----" + response.body());
                                if (response.body() != null && !response.body().isJsonNull()) {
                                    JSONObject jsonObject1;
                                    if (response.body().isJsonObject()) {
                                        jsonObject1 = new JSONObject(response.body().getAsJsonObject().toString());
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.TP_SETUP, (new JSONArray().put(jsonObject1)).toString(), 2));
                                    }
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.tvSync.setEnabled(true);
                                    if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                        checkTpApiStatusOneBuild();
                                    } else {
                                        checkTpApiStaus();
                                    }
                                } else {
                                    if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                        SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                        checkTpApiStatusOneBuild();
                                    } else {
                                        SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                        checkTpApiStaus();
                                    }
                                }
                            } catch (JSONException e) {
                                if (SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                    SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                    checkTpApiStatusOneBuild();
                                } else {
                                    SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                    checkTpApiStaus();
                                }
                                binding.progressBar.setVisibility(View.GONE);
                                binding.tvSync.setEnabled(true);
                                Log.v("tpGetPlan", "--error--2--" + e);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                            binding.progressBar.setVisibility(View.GONE);
                            Log.e("tpGetPlan", "error getTp : " + t);
                        }
                    });
                } catch (JSONException e) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvSync.setEnabled(true);
                    Log.v("tpGetPlan", "--error--1--" + e);
                }
            }
        });
        networkStatusTask.execute();
    }

    public void get3MonthRemoteTPData(String isClickedName) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
            if (status) {
                try {
                    apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                    JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(TourPlanActivity.this);
                    if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1") || SharedPref.getOneBuild(this).equalsIgnoreCase("0")) {
                        jsonObject.put("tableName", "getall_tp");
                    } else {
                        jsonObject.put("tableName", "getall_multitpnew");
                    }
                    jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
                    jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
                    jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                    jsonObject.put("tp_month", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_25, TimeUtils.FORMAT_31, LocalDate.now().getMonth().toString()));
                    jsonObject.put("tp_year", LocalDate.now().getYear());
                    Log.v("tpGetPlan", "--json--" + jsonObject);

                    Map<String, String> mapString = new HashMap<>();
                    mapString.put("axn", "get/tp");
                    Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(TourPlanActivity.this), mapString, jsonObject.toString());
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            try {
                                Log.v("tpGetPlan", "----" + response.body());
                                if (response.body() != null && !response.body().isJsonNull()) {
                                    SharedPref.setTpSyncStaus(TourPlanActivity.this, true);
                                    JSONObject jsonObject1;
                                    if (response.body().isJsonObject()) {
                                        jsonObject1 = new JSONObject(response.body().getAsJsonObject().toString());
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.TOUR_PLAN, (new JSONArray().put(jsonObject1)).toString(), 2));
                                        SaveTourPlanWholeMonth(jsonObject1, isClickedName);
                                    }
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.tvSync.setEnabled(true);

                                    dayWiseArrayPrevMonth = prepareModelClassForMonth(localDate.minusMonths(1));
                                    dayWiseArrayCurrentMonth = prepareModelClassForMonth(LocalDate.now());
                                    dayWiseArrayNextMonth = prepareModelClassForMonth(localDate.plusMonths(1));

                                    switch (isClickedName) {
                                        case "previous":
                                            localDate = localDate.minusMonths(1);
                                            populateCalendarAdapter(dayWiseArrayPrevMonth);
                                            break;
                                        case "current":
                                            localDate = LocalDate.now();
                                            populateCalendarAdapter(dayWiseArrayCurrentMonth);
                                            break;
                                        case "next":
                                            localDate = localDate.plusMonths(1);
                                            populateCalendarAdapter(dayWiseArrayNextMonth);
                                            break;
                                    }

                                    checkTpApiStaus();
                                } else {
                                    SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                    checkTpApiStaus();
                                }
                            } catch (JSONException e) {
                                SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                checkTpApiStaus();
                                binding.progressBar.setVisibility(View.GONE);
                                binding.tvSync.setEnabled(true);
                                Log.v("tpGetPlan", "--error--2--" + e);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                            binding.progressBar.setVisibility(View.GONE);
                            Log.e("tpGetPlan", "error getTp : " + t);
                        }
                    });
                } catch (JSONException e) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvSync.setEnabled(true);
                    Log.v("tpGetPlan", "--error--1--" + e);
                }
            }
        });
        networkStatusTask.execute();
    }

    public void get3MonthRemoteTPDataOneBuild(String isClickedName) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
            if (status) {
                try {
                    apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getBaseWebUrl(TourPlanActivity.this));
                    JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(TourPlanActivity.this);
                    jsonObject.put("tableName", "gettp_onebuild");
                    jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
                    jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
                    jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                    jsonObject.put("tp_month", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_25, TimeUtils.FORMAT_31, LocalDate.now().getMonth().toString()));
                    jsonObject.put("tp_year", LocalDate.now().getYear());
                    Log.v("tpGetPlan", "--json--" + jsonObject);

                    Map<String, String> mapString = new HashMap<>();
                    mapString.put("axn", "get/tp");
                    Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getBaseWebUrl(TourPlanActivity.this) + "iOSServer/db_api.php/", mapString, jsonObject.toString());
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            try {
                                Log.v("tpGetPlan", "----" + response.body());
                                if (response.body() != null && !response.body().isJsonNull()) {
                                    SharedPref.setTpSyncStaus(TourPlanActivity.this, true);
                                    JSONObject jsonObject1;
                                    if (response.body().isJsonObject()) {
                                        jsonObject1 = new JSONObject(response.body().getAsJsonObject().toString());
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.TOUR_PLAN, (new JSONArray().put(jsonObject1)).toString(), 2));
                                        SaveTourPlanWholeMonthOneBuild(jsonObject1, isClickedName);
                                    }
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.tvSync.setEnabled(true);
                                    dayWiseArrayPreviousMonthOneBuild = prepareModelClassForMonthOneBuild(localDate.minusMonths(1));
                                    dayWiseArrayCurrentMonthOneBuild = prepareModelClassForMonthOneBuild(LocalDate.now());
                                    dayWiseArrayNextMonthOneBuild = prepareModelClassForMonthOneBuild(localDate.plusMonths(1));

                                    switch (isClickedName) {
                                        case "previous":
                                            localDate = localDate.minusMonths(1);
                                            populateCalenderAdapterOneBuild(dayWiseArrayPreviousMonthOneBuild);
                                            break;
                                        case "current":
                                            localDate = LocalDate.now();
                                            populateCalenderAdapterOneBuild(dayWiseArrayCurrentMonthOneBuild);
                                            break;
                                        case "next":
                                            localDate = localDate.plusMonths(1);
                                            populateCalenderAdapterOneBuild(dayWiseArrayNextMonthOneBuild);
                                            break;
                                    }
                                } else {
                                    SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                    checkTpApiStatusOneBuild();
                                }
                            } catch (JSONException e) {
                                SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                checkTpApiStatusOneBuild();
                                binding.progressBar.setVisibility(View.GONE);
                                binding.tvSync.setEnabled(true);
                                Log.v("tpGetPlan", "--error--2--" + e);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                            binding.progressBar.setVisibility(View.GONE);
                            Log.e("tpGetPlan", "error getTp : " + t);
                        }
                    });
                } catch (JSONException e) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvSync.setEnabled(true);
                    Log.v("tpGetPlan", "--error--1--" + e);
                }
            }
        });
        networkStatusTask.execute();
    }

    public void getDraftSaveOneBuild(String isClickedName, String monthYear, ArrayList<OneBuildModelClass> arrayList, String isFrom, boolean statusOffline) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Mod", "AndroidDetailing");
                    TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
                    if (tourPlanOfflineDataTable != null) {
                        if(tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty().equalsIgnoreCase("2")){
                            jsonObject.addProperty("Status",3);
                        }else {
                            jsonObject.addProperty("Status",0);
                        }
                    }
                    jsonObject.addProperty("SubmissionDate", TimeUtils.getCurrentDateTimeTp(TimeUtils.FORMAT_37));
                    jsonObject.addProperty("tableName","savetpzen");
                    JsonObject tourPlan = new JsonObject();
                    for (OneBuildModelClass oneBuildModelClassTp : arrayList) {
                        if (!oneBuildModelClassTp.getDayNo().isEmpty() /*&& !oneBuildModelClassTp.getSessionList().get(0).getWorkType().getName().isEmpty()*/) {
                            tourPlan.addProperty("SFCode", SharedPref.getSfCode(TourPlanActivity.this));
                            tourPlan.addProperty("SFName", SharedPref.getSfName(TourPlanActivity.this));
                            tourPlan.addProperty("Month", oneBuildModelClassTp.getMonth());
                            tourPlan.addProperty("Year", oneBuildModelClassTp.getYear());
                            tourPlan.addProperty("DivisionCode", SharedPref.getDivisionCode(TourPlanActivity.this).replace(",", ""));
                            tourPlan.addProperty("SFType",SharedPref.getSfType(TourPlanActivity.this));
                        }
                    }
                    JsonArray detailsArray = new JsonArray();

                    for (OneBuildModelClass oneBuildModelClass : arrayList) {
                        if (!oneBuildModelClass.getDayNo().isEmpty()) {
                            if (!oneBuildModelClass.getDayNo().equals(arrayList)) {
                                String WorkTypeName = "", WorkTypeFlag = "", WorkTypeCode = "", SessionId = "", Remarks = "", WorkTypeName2 = "", WorkTypeFlag2 = "", WorkTypeCode2 = "", SessionId2 = "", Remarks2 = "", WorkTypeName3 = "", WorkTypeFlag3 = "", WorkTypeCode3 = "", SessionId3 = "", Remarks3 = "";
                                String HeadquartersName = "", HeadquartersCode = "", TerritoriesName = "", TerritoriesCode = "", JWName = "", JWCode = "", CheName = "", CheCode = "", DrName = "", DrCode = "", UnDrName = "", UnDrCode = "", StkName = "", StkCode = "", CipName = "", CipCode = "", HospName = "", HospCode = "";
                                String HeadquartersName2 = "", HeadquartersCode2 = "", TerritoriesName2 = "", TerritoriesCode2 = "", JWName2 = "", JWCode2 = "", CheName2 = "", CheCode2 = "", DrName2 = "", DrCode2 = "", UnDrName2 = "", UnDrCode2 = "", StkName2 = "", StkCode2 = "", CipName2 = "", CipCode2 = "", HospName2 = "", HospCode2 = "";
                                String HeadquartersName3 = "", HeadquartersCode3 = "", TerritoriesName3 = "", TerritoriesCode3 = "", JWName3 = "", JWCode3 = "", CheName3 = "", CheCode3 = "", DrName3 = "", DrCode3 = "", UnDrName3 = "", UnDrCode3 = "", StkName3 = "", StkCode3 = "", CipName3 = "", CipCode3 = "", HospName3 = "", HospCode3 = "";
                                String STPCode = "", STPName = "", STPCode2 = "", STPName2 = "", STPCode3 = "", STPName3 = "";
                                STPCode = oneBuildModelClass.getSTP_Code();
                                STPName = oneBuildModelClass.getSTP_Name();
                                if (STPCode == null) STPCode = "";
                                if (STPName == null) STPName = "";
                                for (int i = 0; i < oneBuildModelClass.getSessionList().size(); i++) {
                                    OneBuildModelClass.SessionList sessionList_OneBuild = oneBuildModelClass.getSessionList().get(i);

                                    if (i == 0) {
                                        WorkTypeName = sessionList_OneBuild.getWorkType().getName();
                                        WorkTypeCode = sessionList_OneBuild.getWorkType().getCode();
                                        WorkTypeFlag = sessionList_OneBuild.getWorkType().getFWFlg();
                                        SessionId = String.valueOf(oneBuildModelClass.getSessionList().get(i));
                                        Remarks = sessionList_OneBuild.getRemarks();

                                        if (sessionList_OneBuild.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                                            HeadquartersName = sessionList_OneBuild.getHeadquarters().getName();
                                            HeadquartersCode = sessionList_OneBuild.getHeadquarters().getCode();
                                        }
                                        TerritoriesName = textBuilder_OB(sessionList_OneBuild.getTerritories(), false);
                                        TerritoriesCode = textBuilder_OB(sessionList_OneBuild.getTerritories(), true);
                                        JWName = textBuilder_OB(sessionList_OneBuild.getJointWorks(), false);
                                        JWCode = textBuilder_OB(sessionList_OneBuild.getJointWorks(), true);

                                        CheName = textBuilder_OB(sessionList_OneBuild.getChemists(), false);
                                        CheCode = textBuilder_OB(sessionList_OneBuild.getChemists(), true);
                                        DrName = textBuilder_OB(sessionList_OneBuild.getDoctors(), false);
                                        DrCode = textBuilder_OB(sessionList_OneBuild.getDoctors(), true);
                                        UnDrName = textBuilder_OB(sessionList_OneBuild.getUnlistedDoctors(), false);
                                        UnDrCode = textBuilder_OB(sessionList_OneBuild.getUnlistedDoctors(), true);
                                        StkName = textBuilder_OB(sessionList_OneBuild.getStockists(), false);
                                        StkCode = textBuilder_OB(sessionList_OneBuild.getStockists(), true);
                                        CipName = textBuilder_OB(sessionList_OneBuild.getCip(), false);
                                        CipCode = textBuilder_OB(sessionList_OneBuild.getCip(), true);
                                        HospName = textBuilder_OB(sessionList_OneBuild.getHospitals(), false);
                                        HospCode = textBuilder_OB(sessionList_OneBuild.getHospitals(), true);
                                        STPCode = sessionList_OneBuild.getSTPCode();
                                        STPName = sessionList_OneBuild.getSTPName();
                                    } else if (i == 1) {
                                        WorkTypeName2 = sessionList_OneBuild.getWorkType().getName();
                                        WorkTypeCode2 = sessionList_OneBuild.getWorkType().getCode();
                                        WorkTypeFlag2 = sessionList_OneBuild.getWorkType().getFWFlg();
                                        SessionId2 = String.valueOf(oneBuildModelClass.getSessionList().get(i));
                                        Remarks2 = sessionList_OneBuild.getRemarks();

                                        HeadquartersName2 = sessionList_OneBuild.getHeadquarters().getName();
                                        HeadquartersCode2 = sessionList_OneBuild.getHeadquarters().getCode();
                                        TerritoriesName2 = textBuilder_OB(sessionList_OneBuild.getTerritories(), false);
                                        TerritoriesCode2 = textBuilder_OB(sessionList_OneBuild.getTerritories(), true);
                                        JWName2 = textBuilder_OB(sessionList_OneBuild.getJointWorks(), false);
                                        JWCode2 = textBuilder_OB(sessionList_OneBuild.getJointWorks(), true);

                                        CheName2 = textBuilder_OB(sessionList_OneBuild.getChemists(), false);
                                        CheCode2 = textBuilder_OB(sessionList_OneBuild.getChemists(), true);
                                        DrName2 = textBuilder_OB(sessionList_OneBuild.getDoctors(), false);
                                        DrCode2 = textBuilder_OB(sessionList_OneBuild.getDoctors(), true);
                                        UnDrName2 = textBuilder_OB(sessionList_OneBuild.getUnlistedDoctors(), false);
                                        UnDrCode2 = textBuilder_OB(sessionList_OneBuild.getUnlistedDoctors(), true);
                                        StkName2 = textBuilder_OB(sessionList_OneBuild.getStockists(), false);
                                        StkCode2 = textBuilder_OB(sessionList_OneBuild.getStockists(), true);
                                        CipName2 = textBuilder_OB(sessionList_OneBuild.getCip(), false);
                                        CipCode2 = textBuilder_OB(sessionList_OneBuild.getCip(), true);
                                        HospName2 = textBuilder_OB(sessionList_OneBuild.getHospitals(), false);
                                        HospCode2 = textBuilder_OB(sessionList_OneBuild.getHospitals(), true);
                                        STPCode2 = sessionList_OneBuild.getSTPCode();
                                        STPName2 = sessionList_OneBuild.getSTPName();

                                    } else if (i == 2) {
                                        WorkTypeName3 = sessionList_OneBuild.getWorkType().getName();
                                        WorkTypeCode3 = sessionList_OneBuild.getWorkType().getCode();
                                        WorkTypeFlag3 = sessionList_OneBuild.getWorkType().getFWFlg();
                                        SessionId3 = String.valueOf(oneBuildModelClass.getSessionList().get(i));
                                        Remarks3 = sessionList_OneBuild.getRemarks();

                                        HeadquartersName3 = sessionList_OneBuild.getHeadquarters().getName();
                                        HeadquartersCode3 = sessionList_OneBuild.getHeadquarters().getCode();
                                        TerritoriesName3 = textBuilder_OB(sessionList_OneBuild.getTerritories(), false);
                                        TerritoriesCode3 = textBuilder_OB(sessionList_OneBuild.getTerritories(), true);

                                        JWName3 = textBuilder_OB(sessionList_OneBuild.getJointWorks(), false);
                                        JWCode3 = textBuilder_OB(sessionList_OneBuild.getJointWorks(), true);
                                        CheName3 = textBuilder_OB(sessionList_OneBuild.getChemists(), false);
                                        CheCode3 = textBuilder_OB(sessionList_OneBuild.getChemists(), true);
                                        DrName3 = textBuilder_OB(sessionList_OneBuild.getDoctors(), false);
                                        DrCode3 = textBuilder_OB(sessionList_OneBuild.getDoctors(), true);
                                        UnDrName3 = textBuilder_OB(sessionList_OneBuild.getUnlistedDoctors(), false);
                                        UnDrCode3 = textBuilder_OB(sessionList_OneBuild.getUnlistedDoctors(), true);
                                        StkName3 = textBuilder_OB(sessionList_OneBuild.getStockists(), false);
                                        StkCode3 = textBuilder_OB(sessionList_OneBuild.getStockists(), true);
                                        CipName3 = textBuilder_OB(sessionList_OneBuild.getCip(), false);
                                        CipCode3 = textBuilder_OB(sessionList_OneBuild.getCip(), true);
                                        HospName3 = textBuilder_OB(sessionList_OneBuild.getHospitals(), false);
                                        HospCode3 = textBuilder_OB(sessionList_OneBuild.getHospitals(), true);
                                        STPCode3 = sessionList_OneBuild.getSTPCode();
                                        STPName3 = sessionList_OneBuild.getSTPName();
                                    }
                                }

                                //Sessions
                                JsonArray Sessions = new JsonArray();
                                for (int i = 0; i < oneBuildModelClass.getSessionList().size(); i++) {
                                    if (!WorkTypeName.isEmpty() && !WorkTypeCode.isEmpty()) {
                                        JsonObject SessionsObj = new JsonObject();
                                        if (i == 0) {
                                            if (!WorkTypeName.isEmpty() && !WorkTypeCode.isEmpty()) {
                                                SessionsObj.addProperty("WorkTypeCode", WorkTypeCode);
                                                SessionsObj.addProperty("WorkTypeName", WorkTypeName);
                                                SessionsObj.addProperty("Id", i);
                                                SessionsObj.addProperty("ObjectiveId", STPCode);
                                                SessionsObj.addProperty("ObjectiveName", STPName);
                                                SessionsObj.addProperty("WorkTypeFlag", WorkTypeFlag);
                                                SessionsObj.addProperty("Remark", Remarks);
                                            }
                                        } else if (i == 1) {
                                            if (!WorkTypeName.isEmpty() && !WorkTypeCode.isEmpty()) {
                                                SessionsObj.addProperty("WorkTypeCode", WorkTypeCode2);
                                                SessionsObj.addProperty("WorkTypeName", WorkTypeName2);
                                                SessionsObj.addProperty("Id", i);
                                                SessionsObj.addProperty("ObjectiveId", STPCode2);
                                                SessionsObj.addProperty("ObjectiveName", STPName2);
                                                SessionsObj.addProperty("WorkTypeFlag", WorkTypeFlag2);
                                                SessionsObj.addProperty("Remark", Remarks2);
                                            }
                                        } else if (i == 2) {
                                            if (!WorkTypeName.isEmpty() && !WorkTypeCode.isEmpty()) {
                                                SessionsObj.addProperty("WorkTypeCode", WorkTypeCode3);
                                                SessionsObj.addProperty("WorkTypeName", WorkTypeName3);
                                                SessionsObj.addProperty("Id", i);
                                                SessionsObj.addProperty("ObjectiveId", STPCode3);
                                                SessionsObj.addProperty("ObjectiveName", STPName3);
                                                SessionsObj.addProperty("WorkTypeFlag", WorkTypeFlag3);
                                                SessionsObj.addProperty("Remark", Remarks3);
                                            }
                                        }

                                        JsonArray JointWorks = new JsonArray();

                                        String nameJw = "";
                                        String codeJw = "";

                                        if (i == 0) {
                                            nameJw = JWName;
                                            codeJw = JWCode;
                                        } else if (i == 1) {
                                            nameJw = JWName2;
                                            codeJw = JWCode2;
                                        } else if (i == 2) {
                                            nameJw = JWName3;
                                            codeJw = JWCode3;
                                        }

                                        if (!nameJw.isEmpty() && !codeJw.isEmpty()) {
                                            String[] nameArr = nameJw.split(",");
                                            String[] codeArr = codeJw.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject JointWorks_obj = new JsonObject();
                                                JointWorks_obj.addProperty("Name", nameArr[j].trim());
                                                JointWorks_obj.addProperty("Id", codeArr[j].trim());
                                                JointWorks.add(JointWorks_obj);
                                            }
                                        }

                                        //Territories
                                        JsonArray Territories = new JsonArray();
                                        String nameTerr = "";
                                        String codeTerr = "";
                                        if (i == 0) {
                                            nameTerr = TerritoriesName;
                                            codeTerr = TerritoriesCode;
                                        } else if (i == 1) {
                                            nameTerr = TerritoriesName2;
                                            codeTerr = TerritoriesCode2;
                                        } else if (i == 2) {
                                            nameTerr = TerritoriesName3;
                                            codeTerr = TerritoriesCode3;
                                        }
                                        if (!nameTerr.isEmpty() && !codeTerr.isEmpty()) {
                                            String[] nameArr = nameTerr.split(",");
                                            String[] codeArr = codeTerr.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject Territories_obj = new JsonObject();
                                                Territories_obj.addProperty("Name", nameArr[j].trim());
                                                Territories_obj.addProperty("Id", codeArr[j].trim());
                                                Territories.add(Territories_obj);
                                            }
                                        }

                                        //Headquarters
                                        JsonArray Headquarters = new JsonArray();
                                        String nameHq = "";
                                        String codeHq = "";
                                        if (i == 0) {
                                            nameHq = HeadquartersName;
                                            codeHq = HeadquartersCode;
                                        } else if (i == 1) {
                                            nameHq = HeadquartersName2;
                                            codeHq = HeadquartersCode2;
                                        } else if (i == 2) {
                                            nameHq = HeadquartersName3;
                                            codeHq = HeadquartersCode3;
                                        }
                                        if (!nameHq.isEmpty() && !codeHq.isEmpty()) {
                                            String[] nameArr = nameHq.split(",");
                                            String[] codeArr = codeHq.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject Headquarters_obj = new JsonObject();
                                                Headquarters_obj.addProperty("Name", nameArr[j].trim());
                                                Headquarters_obj.addProperty("Id", codeArr[j].trim());
                                                Headquarters.add(Headquarters_obj);
                                            }
                                        }

                                        //Hospitals
                                        JsonArray Hospitals = new JsonArray();
                                        String nameHosp = "";
                                        String codeHosp = "";
                                        if (i == 0) {
                                            nameHosp = HospName;
                                            codeHosp = HospCode;
                                        } else if (i == 1) {
                                            nameHosp = HospName2;
                                            codeHosp = HospCode2;
                                        } else if (i == 2) {
                                            nameHosp = HospName3;
                                            codeHosp = HospCode3;
                                        }
                                        if (!nameHosp.isEmpty() && !codeHosp.isEmpty()) {
                                            String[] nameArr = nameHosp.split(",");
                                            String[] codeArr = codeHosp.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject Hospitals_obj = new JsonObject();
                                                Hospitals_obj.addProperty("Name", nameArr[j].trim());
                                                Hospitals_obj.addProperty("Id", codeArr[j].trim());
                                                Hospitals.add(Hospitals_obj);
                                            }
                                        }

                                        //Chemists
                                        JsonArray Chemists = new JsonArray();
                                        String nameChe = "";
                                        String codeChe = "";
                                        if (i == 0) {
                                            nameChe = CheName;
                                            codeChe = CheCode;
                                        } else if (i == 1) {
                                            nameChe = CheName2;
                                            codeChe = CheCode2;
                                        } else if (i == 2) {
                                            nameChe = CheName3;
                                            codeChe = CheCode3;
                                        }
                                        if (!nameChe.isEmpty() && !codeChe.isEmpty()) {
                                            String[] nameArr = nameChe.split(",");
                                            String[] codeArr = codeChe.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject Chemists_obj = new JsonObject();
                                                Chemists_obj.addProperty("Name", nameArr[j].trim());
                                                Chemists_obj.addProperty("Id", codeArr[j].trim());
                                                Chemists.add(Chemists_obj);
                                            }
                                        }

                                        //StockLists
                                        JsonArray Stockists = new JsonArray();
                                        String nameStk = "";
                                        String codeStk = "";
                                        if (i == 0) {
                                            nameStk = StkName;
                                            codeStk = StkCode;
                                        } else if (i == 1) {
                                            nameStk = StkName2;
                                            codeStk = StkCode2;
                                        } else if (i == 2) {
                                            nameStk = StkName3;
                                            codeStk = StkCode3;
                                        }
                                        if (!nameStk.isEmpty() && !codeStk.isEmpty()) {
                                            String[] nameArr = nameStk.split(",");
                                            String[] codeArr = codeStk.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject Stockists_obj = new JsonObject();
                                                Stockists_obj.addProperty("Name", nameArr[j].trim());
                                                Stockists_obj.addProperty("Id", codeArr[j].trim());
                                                Stockists.add(Stockists_obj);
                                            }
                                        }

                                        //ListedDr
                                        JsonArray Doctors = new JsonArray();
                                        String nameDoc = "";
                                        String codeDoc = "";
                                        if (i == 0) {
                                            nameDoc = DrName;
                                            codeDoc = DrCode;
                                        } else if (i == 1) {
                                            nameDoc = DrName2;
                                            codeDoc = DrCode2;
                                        } else if (i == 2) {
                                            nameDoc = DrName3;
                                            codeDoc = DrCode3;
                                        }
                                        if (!nameDoc.isEmpty() && !codeDoc.isEmpty()) {
                                            String[] nameArr = nameDoc.split(",");
                                            String[] codeArr = codeDoc.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject Doctors_obj = new JsonObject();
                                                Doctors_obj.addProperty("Name", nameArr[j].trim());
                                                Doctors_obj.addProperty("Id", codeArr[j].trim());
                                                Doctors.add(Doctors_obj);
                                            }
                                        }

                                        //UnlistedDr
                                        JsonArray UnlistedDoctors = new JsonArray();
                                        String nameUnDr = "";
                                        String codeUmDr = "";
                                        if (i == 0) {
                                            nameUnDr = UnDrName;
                                            codeUmDr = UnDrCode;
                                        } else if (i == 1) {
                                            nameUnDr = UnDrName2;
                                            codeUmDr = UnDrCode2;
                                        } else if (i == 2) {
                                            nameUnDr = UnDrName3;
                                            codeUmDr = UnDrCode3;
                                        }
                                        if (!nameUnDr.isEmpty() && !codeUmDr.isEmpty()) {
                                            String[] nameArr = nameUnDr.split(",");
                                            String[] codeArr = codeUmDr.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);
                                            for (int j = 0; j < length; j++) {
                                                JsonObject UnlistedDoctors_obj = new JsonObject();
                                                UnlistedDoctors_obj.addProperty("Name", nameArr[j].trim());
                                                UnlistedDoctors_obj.addProperty("Id", codeArr[j].trim());
                                                UnlistedDoctors.add(UnlistedDoctors_obj);
                                            }
                                        }

                                        //Cip
                                   /*     JSONArray Cip = new JSONArray();
                                        String nameCip = "";
                                        String codeCip = "";
                                        if (i == 0) {
                                            nameCip = CipName;
                                            codeCip = CipCode;
                                        } else if (i == 1) {
                                            nameCip = CipName2;
                                            codeCip = CipCode2;
                                        } else if (i == 2) {
                                            nameCip = CipName3;
                                            codeCip = CipCode3;
                                        }
                                        if (!nameCip.isEmpty() && !codeCip.isEmpty()) {
                                            String[] nameArr = nameCip.split(",");
                                            String[] codeArr = codeCip.split(",");

                                            int length = Math.min(nameArr.length, codeArr.length);

                                            for (int j = 0; j < length; j++) {
                                                JSONObject Cip_obj = new JSONObject();
                                                Cip_obj.put("Name", nameArr[j].trim());
                                                Cip_obj.put("Id", codeArr[j].trim());
                                                Cip.put(Cip_obj);
                                            }
                                        }*/

                                        SessionsObj.add("JointWorks", JointWorks);
                                        SessionsObj.add("Headquarters", Headquarters);
                                        SessionsObj.add("Territories", Territories);
                                        SessionsObj.add("Doctors", Doctors);
                                        SessionsObj.add("Chemists", Chemists);
                                        SessionsObj.add("Stockists", Stockists);
                                        SessionsObj.add("UnlistedDoctors", UnlistedDoctors);
                                        SessionsObj.add("Hospitals", Hospitals);
//                                        SessionsObj.put("Cip", Cip);
                                        Sessions.add(SessionsObj);
                                    }
                                }

                                //Details
                                JsonObject DetailsObj = new JsonObject();
                                DetailsObj.addProperty("Id", 0);
                                String planningStatus = oneBuildModelClass.getSyncStatus();
                                if(planningStatus == null || planningStatus.isEmpty()){
                                    planningStatus = "1";
                                }
                                DetailsObj.addProperty("Planning_Status", planningStatus);
                                DetailsObj.add("Others", new JsonArray());
                                DetailsObj.addProperty("TDate", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_19, TimeUtils.FORMAT_4, oneBuildModelClass.getDate()));
                                DetailsObj.add("Sessions", Sessions);
                                detailsArray.add(DetailsObj);

                            }
                        }
                        tourPlan.add("Details", detailsArray);
                        jsonObject.add("tourPlan", tourPlan);
                        Gson gson = new Gson();

                        jbonj = gson.fromJson(jsonObject.toString(), JsonObject.class);
//                        System.out.println(jbonj.toString());
                    }

                    Log.d("JSON_One_Build", "isNetworkAvailable: " + jsonObject);
                    String isSynced = inputDataArrayOneBuild.getSyncStatus();
                    TourPlanOfflineDataTable tourPlanOfflineDataTable1 = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
                    changeStatus = tourPlanOfflineDataTable1.getTpMonthSyncedOrEmpty();
                    if ((Objects.equals(changeStatus, "0") || Objects.equals(changeStatus, "2")) && tourPlanOfflineDataTable1.getTpMonthSynced().equalsIgnoreCase("1") || isSynced.equalsIgnoreCase("1")){
                        apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getBaseWebUrl(TourPlanActivity.this));
                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "save/tp");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(TourPlanActivity.this), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.v("tpGetPlan", "----" + response.body());
                                try {
                                    JSONObject json = new JSONObject(response.body().toString());
                                    Log.d("TAG", "onResponse: " + json);
                                    boolean success = json.optBoolean("success");
                                    System.out.println(success);
                                    if(success){
                                        Log.d("TAG", "onResponse: "+"success TRUE");
                                    }else{
                                        Log.d("TAG", "onResponse: "+"success FALSE");
                                    }
                                    if (response.body() != null && !response.body().isJsonNull() && success) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body().toString());
                                            SharedPref.setTpSyncStaus(TourPlanActivity.this, true);
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.draft_save));
                                            tourPlanOfflineDataTable1.setTpMonthSynced("0");
                                            tourPlanOfflineDataDao.saveMonthlySyncStatus(tourPlanOfflineDataTable1.getTpMonth(), "0");
                                            inputDataArrayOneBuild.setSyncStatus("0");
                                            binding.progressBar.setVisibility(View.GONE);
                                            try {
                                                JSONObject outerJsonObject = new JSONObject(response.body().getAsJsonObject().toString());
                                                if (!isFrom.equalsIgnoreCase("sendToApproval")) {
                                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.TOUR_PLAN, (new JSONArray().put(outerJsonObject)).toString(), 2));
                                                    binding.progressBar.setVisibility(View.GONE);
                                                    binding.tvSync.setEnabled(true);
                                                }
                                                if (isFrom.equalsIgnoreCase("sendToApproval")) {

                                                    JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
                                                    ArrayList<OneBuildModelClass> arrayList;

                                                    Type type = new TypeToken<ArrayList<OneBuildModelClass>>() {
                                                    }.getType();
                                                    if (jsonArray.length() >= 0) {
                                                        arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
                                                        sendTpForApprovalOneBuild(jbonj, arrayList, localDate.toString(), monthYearFromDateUI(localDate), statusOffline, isClickedName);
                                                    }
                                                }else {
                                                    get1MonthRemoteTPDataOneBuild(localDate);
                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.something_wrong));
                                        SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                        binding.progressBar.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                SharedPref.setTpSyncStaus(TourPlanActivity.this, false);
                                binding.progressBar.setVisibility(View.GONE);
                                Log.e("tpGetPlan", "error getTp : " + t);
                            }
                        });
                    } else {
                        if (isFrom.equalsIgnoreCase("sendToApproval")) {

                            JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
                            ArrayList<OneBuildModelClass> arrayList1;

                            Type type = new TypeToken<ArrayList<OneBuildModelClass>>() {
                            }.getType();
                            if (jsonArray.length() >= 0) {
                                arrayList1 = new Gson().fromJson(String.valueOf(jsonArray), type);
                                sendTpForApprovalOneBuild(jbonj, arrayList1, localDate.toString(), monthYearFromDateUI(localDate), statusOffline, isClickedName);
                            }
                        }else {
                            get1MonthRemoteTPDataOneBuild(localDate);
                        }
                    }
                } catch (JsonIOException e) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvSync.setEnabled(true);
                    Log.v("tpGetPlan", "--error--1--" + e);
                }
        });
        networkStatusTask.execute();
    }

    private void SaveTourPlanWholeMonth(JSONObject jsonObject1, String isClickedName) {
        try {
            localDate = LocalDate.now();
            if (jsonObject1.has("previous")) {
                JSONArray previousArray = new JSONArray(jsonObject1.getJSONArray("previous").toString());
                SaveLocalOnlineTable(localDate.minusMonths(1), previousArray, dayWiseArrayPrevMonth);
            }

            if (jsonObject1.has("current")) {
                JSONArray currentArray = new JSONArray(jsonObject1.getJSONArray("current").toString());
                SaveLocalOnlineTable(localDate, currentArray, dayWiseArrayCurrentMonth);
            }

            if (jsonObject1.has("next")) {
                JSONArray nextArray = new JSONArray(jsonObject1.getJSONArray("next").toString());
                SaveLocalOnlineTable(localDate.plusMonths(1), nextArray, dayWiseArrayNextMonth);
            }

        } catch (Exception e) {
            binding.tvSync.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
            Log.v("getTp", "----error---" + e);
        }
    }

    private void SaveTourPlanWholeMonthOneBuild(JSONObject jsonObject, String isClickedName) {
        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        try {
            localDate = LocalDate.now();
            if (jsonObject.has("previous")) {
                JSONArray previousArray = new JSONArray(jsonObject.getJSONArray("previous").toString());
                SaveLocalOnlineTableOneBuild(localDate.minusMonths(1), previousArray, dayWiseArrayPreviousMonthOneBuild);
            }

            if (jsonObject.has("current")) {
                JSONArray currentArray = new JSONArray(jsonObject.getJSONArray("current").toString());
                SaveLocalOnlineTableOneBuild(localDate, currentArray, dayWiseArrayCurrentMonthOneBuild);
            }

            if (jsonObject.has("next")) {
                JSONArray nextArray = new JSONArray(jsonObject.getJSONArray("next").toString());
                SaveLocalOnlineTableOneBuild(localDate.plusMonths(1), nextArray, dayWiseArrayNextMonthOneBuild);
            }

        } catch (Exception e) {
            binding.tvSync.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
            Log.v("getTp", "----error---" + e);
        }
    }

    private void SaveLocalOnlineTable(LocalDate localDate, JSONArray listArray, ArrayList<ModelClass> dayWiseSaveTp) {
        try { // check this method
            dayWiseSaveTp = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate));

            String monthYear = monthYearFromDate(localDate);
            String monthNo = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_31, monthYear);
            String year = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);
            String monthName = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate));
            ArrayList<ModelClass> modelClasses = new ArrayList<>();

            ArrayList<String> holidayDateArray = new ArrayList<>();
            ArrayList<String> holidayNameArray = new ArrayList<>();
            for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate.getMonthValue()))) {
                    holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
                    holidayNameArray.add(holidayJSONArray.getJSONObject(i).getString("Holiday_Name"));
                }
            }


            JSONArray savedDataArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
            ArrayList<ModelClass> modelClassLocal = new ArrayList<>();
            if (savedDataArray.length() > 0) { //Use the saved data if Tour Plan table has data of a selected month
                Type typeLocal = new TypeToken<ArrayList<ModelClass>>() {
                }.getType();
                modelClassLocal = new Gson().fromJson(savedDataArray.toString(), typeLocal);
            }

            Type type = new TypeToken<ArrayList<ReceiveModel>>() {
            }.getType();
            ArrayList<ReceiveModel> arrayList = new Gson().fromJson(listArray.toString(), type);

            if (listArray.length() > 0) {
                String rejectionReason = listArray.getJSONObject(0).getString("Rejection_Reason");
                String status = listArray.getJSONObject(0).getString("Change_Status");
                tourPlanOnlineDataDao.saveTpData(new TourPlanOnlineDataTable(monthName, listArray.toString(), status, rejectionReason));
                boolean LocalWeelyHolidayFlag;
                for (String day : days) {
                    if (!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        isDataAvailable = false;

                        if (modelClassLocal.size() > 0) {
                            for (int j = 0; j < modelClassLocal.size(); j++) {
                                if (modelClassLocal.get(j).getDayNo().equalsIgnoreCase(day) && modelClassLocal.get(j).getSyncStatus().equalsIgnoreCase("0")) {

                                    for (int i = 0; i < arrayList.size(); i++) {
                                        ReceiveModel receiveModel = arrayList.get(i);
                                        if (modelClassLocal.get(j).getDayNo().equalsIgnoreCase(receiveModel.getDayno())) {
                                            SaveTpLocalFull(receiveModel, modelClasses, dayWiseSaveTp, day, monthName, date, dayName, monthNo, year);
                                        }
                                    }
                                } else if (modelClassLocal.get(j).getDayNo().equalsIgnoreCase(day) && modelClassLocal.get(j).getSyncStatus().equalsIgnoreCase("1")) {
                                    isDataAvailable = true;
                                    ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, modelClassLocal.get(j).getSessionList());
                                    modelClasses.add(modelClass);
                                    dayWiseSaveTp = modelClasses;
                                    saveTpLocal(dayWiseSaveTp, day, monthName, "1");
                                }
                            }
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                ReceiveModel receiveModel = arrayList.get(i);
                                if (day.equalsIgnoreCase(receiveModel.getDayno())) {
                                    SaveTpLocalFull(receiveModel, modelClasses, dayWiseSaveTp, day, monthName, date, dayName, monthNo, year);
                                }
                            }
                        }

                        if (!isDataAvailable) {
                            ModelClass.SessionList sessionList = new ModelClass.SessionList();
                            sessionList = prepareSessionListForAdapterEmpty();

                            if (Integer.valueOf(monthNo) == JoiningMonth && Integer.valueOf(year) == JoinYear && Integer.valueOf(day) < JoningDate) {
                                ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                                sessionLists.add(sessionList);
                                ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, false, sessionLists);
                                modelClasses.add(modelClass);
                                LocalWeelyHolidayFlag = false;
                            } else {

                                if (holidayDateArray.contains(day)) {
                                    int index = holidayDateArray.indexOf(day);
                                    sessionList.setRemarks(holidayNameArray.get(index));
                                    sessionList.setWorkType(holidayWorkTypeModel);  // add holiday work type model object when current date is declared as holiday
                                    LocalWeelyHolidayFlag = true;
                                } else if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                    sessionList.setWorkType(weeklyOffWorkTypeModel);
                                    LocalWeelyHolidayFlag = true;
                                } else {
                                    LocalWeelyHolidayFlag = false;
                                }

                                ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                                sessionLists.add(sessionList);
                                ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                                modelClasses.add(modelClass);

                            }
                            dayWiseSaveTp = modelClasses;

                            if (LocalWeelyHolidayFlag) {
                                saveTpLocal(dayWiseSaveTp, day, monthYear, "1");
                            } else {
                                saveTpLocal(dayWiseSaveTp, day, monthYear, "0");
                            }
                        }
                    } else {
                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        ModelClass modelClass = new ModelClass(day, "", "", "", "", true, sessionLists);
                        modelClasses.add(modelClass);
                        dayWiseSaveTp = modelClasses;
                        saveTpLocal(dayWiseSaveTp, day, monthName, "");
                    }
                }

//                sqLite.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, rejectionReason);
                tourPlanOfflineDataDao.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, rejectionReason);
            } else {  //If tour plan table has no data

                boolean LocalWeelyHolidayFlag;

                for (String day : days) {
                    if (!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        ModelClass.SessionList sessionList = new ModelClass.SessionList();
                        sessionList = prepareSessionListForAdapterEmpty();

                        if (Integer.valueOf(monthNo) == JoiningMonth && Integer.valueOf(year) == JoinYear && Integer.valueOf(day) < JoningDate) {
                            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, false, sessionLists);
                            modelClasses.add(modelClass);
                            LocalWeelyHolidayFlag = false;
                        } else {
                            if (holidayDateArray.contains(day)) {
                                int index = holidayDateArray.indexOf(day);
                                sessionList.setRemarks(holidayNameArray.get(index));
                                sessionList.setWorkType(holidayWorkTypeModel);  // add holiday work type model object when current date is declared as holiday
                                LocalWeelyHolidayFlag = true;
                            } else if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                sessionList.setWorkType(weeklyOffWorkTypeModel);
                                LocalWeelyHolidayFlag = true;
                            } else {
                                LocalWeelyHolidayFlag = false;
                            }
                            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                            modelClasses.add(modelClass);
                        }

                        dayWiseSaveTp = modelClasses;
                        if (LocalWeelyHolidayFlag) {
                            saveTpLocal(dayWiseSaveTp, day, monthYear, "1");
                        } else {
                            saveTpLocal(dayWiseSaveTp, day, monthYear, "0");
                        }
                    } else {
                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        ModelClass modelClass = new ModelClass(day, "", "", "", "", true, sessionLists);
                        modelClasses.add(modelClass);
                        dayWiseSaveTp = modelClasses;
                        saveTpLocal(dayWiseSaveTp, day, monthName, "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SaveLocalOnlineTableOneBuild(LocalDate localDate, JSONArray
            listArray, ArrayList<OneBuildModelClass> dayWiseSaveTp) {
        try { // check this method
            dayWiseSaveTp = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate));

            String monthYear = monthYearFromDate(localDate);
            String monthNo = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_31, monthYear);
            String year = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);
            String monthName = TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate));
            ArrayList<OneBuildModelClass> oneBuildModelClasses = new ArrayList<>();
            ArrayList<String> holidayDateArray = new ArrayList<>();
            ArrayList<String> holidayNameArray = new ArrayList<>();
            for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate.getMonthValue()))) {
                    holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
                    holidayNameArray.add(holidayJSONArray.getJSONObject(i).getString("Holiday_Name"));
                }
            }


            JSONArray savedDataArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
            ArrayList<OneBuildModelClass> oneBuildModelClassLocal = new ArrayList<>();
            if (savedDataArray.length() > 0) { //Use the saved data if Tour Plan table has data of a selected month
                Type typeLocal = new TypeToken<ArrayList<OneBuildModelClass>>() {
                }.getType();
                oneBuildModelClassLocal = new Gson().fromJson(savedDataArray.toString(), typeLocal);
            }

            Type type = new TypeToken<ArrayList<ReceiveModel>>() {
            }.getType();
            ArrayList<ReceiveModel> arrayList = new Gson().fromJson(listArray.toString(), type);

            if (listArray.length() > 0) {
                String rejectionReason = listArray.getJSONObject(0).getString("Rejection_Reason");
                String status = listArray.getJSONObject(0).getString("Change_Status");
                tourPlanOnlineDataDao.saveTpData(new TourPlanOnlineDataTable(monthName, listArray.toString(), status, rejectionReason));
                boolean LocalWeelyHolidayFlag;
                for (String day : days) {
                    if (!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        isDataAvailable = false;

                        if (oneBuildModelClassLocal.size() > 0) {
                            for (int j = 0; j < oneBuildModelClassLocal.size(); j++) {
                                if (oneBuildModelClassLocal.get(j).getDayNo().equalsIgnoreCase(day) && oneBuildModelClassLocal.get(j).getSyncStatus().equalsIgnoreCase("0")) {

                                    for (int i = 0; i < arrayList.size(); i++) {
                                        ReceiveModel receiveModel = arrayList.get(i);
                                        if (oneBuildModelClassLocal.get(j).getDayNo().equalsIgnoreCase(receiveModel.getDayno())) {
                                            SaveTpLocalFullOneBuild(receiveModel, oneBuildModelClasses, dayWiseSaveTp, day, monthName, date, dayName, monthNo, year);
                                        }
                                    }
                                } else if (oneBuildModelClassLocal.get(j).getDayNo().equalsIgnoreCase(day) && oneBuildModelClassLocal.get(j).getSyncStatus().equalsIgnoreCase("1")) {
                                    isDataAvailable = true;
                                    OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, monthNo, year, true, oneBuildModelClassLocal.get(j).getSessionList());
                                    oneBuildModelClasses.add(oneBuildModelClass);
                                    dayWiseSaveTp = oneBuildModelClasses;
                                    saveTpLocalOneBuild(dayWiseSaveTp, day, monthName, "1");
                                }
                            }
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                ReceiveModel receiveModel = arrayList.get(i);
                                if (day.equalsIgnoreCase(receiveModel.getDayno())) {
                                    SaveTpLocalFullOneBuild(receiveModel, oneBuildModelClasses, dayWiseSaveTp, day, monthName, date, dayName, monthNo, year);
                                }
                            }
                        }

                        if (!isDataAvailable) {
                            OneBuildModelClass.SessionList sessionList = new OneBuildModelClass.SessionList();
                            sessionList = prepareSessionListForAdapterEmptyOneBuild();

                            if (Integer.valueOf(monthNo) == JoiningMonth && Integer.valueOf(year) == JoinYear && Integer.valueOf(day) < JoningDate) {
                                ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                                sessionLists.add(sessionList);
                                OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, monthNo, year, false, sessionLists);
                                oneBuildModelClasses.add(oneBuildModelClass);
                                LocalWeelyHolidayFlag = false;
                            } else {

                                if (holidayDateArray.contains(day)) {
                                    int index = holidayDateArray.indexOf(day);
                                    sessionList.setRemarks(holidayNameArray.get(index));
                                    sessionList.setWorkType(holidayWorkTypeModelOneBuild);  // add holiday work type model object when current date is declared as holiday
                                    LocalWeelyHolidayFlag = true;
                                } else if (weeklyOffDays.contains(dayName)) {   // add weekly off object when the day is declared as Weekly Off
                                    sessionList.setWorkType(weeklyOffWorkTypeModelOneBuild);
                                    LocalWeelyHolidayFlag = true;
                                } else {
                                    LocalWeelyHolidayFlag = false;
                                }

                                ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                                sessionLists.add(sessionList);
                                OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                                oneBuildModelClasses.add(oneBuildModelClass);

                            }
                            dayWiseSaveTp = oneBuildModelClasses;

                            if (LocalWeelyHolidayFlag) {
                                saveTpLocalOneBuild(dayWiseSaveTp, day, monthYear, "1");
                            } else {
                                saveTpLocalOneBuild(dayWiseSaveTp, day, monthYear, "0");
                            }
                        }
                    } else {
                        ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                        OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, "", "", "", "", true, sessionLists);
                        oneBuildModelClasses.add(oneBuildModelClass);
                        dayWiseSaveTp = oneBuildModelClasses;
                        saveTpLocalOneBuild(dayWiseSaveTp, day, monthName, "");
                    }
                }

//                sqLite.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, rejectionReason);
                tourPlanOfflineDataDao.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, rejectionReason);
            } else {  //If tour plan table has no data

                boolean LocalWeelyHolidayFlag;

                for (String day : days) {
                    if (!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        OneBuildModelClass.SessionList sessionList = new OneBuildModelClass.SessionList();
                        sessionList = prepareSessionListForAdapterEmptyOneBuild();

                        if (Integer.valueOf(monthNo) == JoiningMonth && Integer.valueOf(year) == JoinYear && Integer.valueOf(day) < JoningDate) {
                            ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, monthNo, year, false, sessionLists);
                            oneBuildModelClasses.add(oneBuildModelClass);
                            LocalWeelyHolidayFlag = false;
                        } else {
                            if (holidayDateArray.contains(day)) {
                                int index = holidayDateArray.indexOf(day);
                                sessionList.setRemarks(holidayNameArray.get(index));
                                sessionList.setWorkType(holidayWorkTypeModelOneBuild);  // add holiday work type model object when current date is declared as holiday
                                LocalWeelyHolidayFlag = true;
                            } else if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                sessionList.setWorkType(weeklyOffWorkTypeModelOneBuild);
                                LocalWeelyHolidayFlag = true;
                            } else {
                                LocalWeelyHolidayFlag = false;
                            }
                            ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                            oneBuildModelClasses.add(oneBuildModelClass);
                        }

                        dayWiseSaveTp = oneBuildModelClasses;
                        if (LocalWeelyHolidayFlag) {
                            saveTpLocalOneBuild(dayWiseSaveTp, day, monthYear, "1");
                        } else {
                            saveTpLocalOneBuild(dayWiseSaveTp, day, monthYear, "0");
                        }
                    } else {
                        ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
                        OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(day, "", "", "", "", true, sessionLists);
                        oneBuildModelClasses.add(oneBuildModelClass);
                        dayWiseSaveTp = oneBuildModelClasses;
                        saveTpLocalOneBuild(dayWiseSaveTp, day, monthName, "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SaveTpData(ArrayList<OneBuildModelClass> oneBuildModelClasses) {
        if (!dayWiseArrayPreviousMonthOneBuild.isEmpty()) {
            Log.v("ggggg", "previous");
            dayWiseArrayPreviousMonthOneBuild = oneBuildModelClasses;
//                saveTpLocalOneBuild(dayWiseArrayPrevMonthOneBuild, day, monthName, status);
        } else if (!dayWiseArrayCurrentMonthOneBuild.isEmpty()) {
            Log.v("ggggg", "current");
            dayWiseArrayCurrentMonthOneBuild = oneBuildModelClasses;
//                saveTpLocalOneBuild(dayWiseArrayCurrentMonthOneBuild, day, monthName, status);
        } else if (!dayWiseArrayNextMonthOneBuild.isEmpty()) {
            Log.v("ggggg", "next");
            dayWiseArrayNextMonthOneBuild = oneBuildModelClasses;

//                   saveTpLocalOneBuild(dayWiseArrayNextMonthOneBuild, day, monthName, status);
        }
    }

    private void SaveTpLocalFull(ReceiveModel receiveModel, ArrayList<ModelClass> modelClasses, ArrayList<ModelClass> dayWiseSaveTp, String day, String monthName, String date, String dayName, String monthNo, String year) {
        ModelClass.SessionList sessionList = new ModelClass.SessionList();
        ModelClass.SessionList sessionList2 = new ModelClass.SessionList();
        ModelClass.SessionList sessionList3 = new ModelClass.SessionList();

        isDataAvailable = true;
        boolean session2 = false;
        boolean session3 = false;

        String terrSlFlag = findTerrSlFlag(receiveModel.getWTCode());
        String remarks = receiveModel.getDayRemarks();
        String submittedTime = receiveModel.getSubmitted_time_dt();

        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stkArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        ArrayList<ModelClass.SessionList.SubClass> hqs = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> clusters = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> JCs = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> listedDrs = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> chemists = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> stockiests = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> unListedDrs = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> cips = new ArrayList<>();
        ArrayList<MultiHQHeaderModelClass> hospitals = new ArrayList<>();

        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(receiveModel.getFWFlg(), receiveModel.getWTName(), terrSlFlag, receiveModel.getWTCode());
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass(receiveModel.getHQNames(), receiveModel.getHQCodes());

        //   if (receiveModel.getFWFlg().equalsIgnoreCase("F")) {
        if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
            if (!receiveModel.getClusterName().isEmpty())
                clusterArray = addExtraData(receiveModel.getClusterName(), receiveModel.getClusterCode());
            if (!receiveModel.getJWNames().isEmpty())
                jcArray = addExtraData(receiveModel.getJWNames(), receiveModel.getJWCodes());
            if (!receiveModel.getDr_Name().isEmpty())
                drArray = addExtraData(receiveModel.getDr_Name(), receiveModel.getDr_Code());
            if (!receiveModel.getChem_Name().isEmpty())
                chemArray = addExtraData(receiveModel.getChem_Name(), receiveModel.getChem_Code());
            if (!receiveModel.getStockist_Name().isEmpty())
                stkArray = addExtraData(receiveModel.getStockist_Name(), receiveModel.getStockist_Code());
        } else {
            if (!receiveModel.getHQCodes().isEmpty()) {
                hqs = addExtraData(receiveModel.getHQNames(), receiveModel.getHQCodes());
            }
            if (!receiveModel.getClusterName().isEmpty()) {
                clusters = addExtraData(hqs, receiveModel.getClusterName(), receiveModel.getClusterCode());
            }
            if (!receiveModel.getJWNames().isEmpty()) {
                JCs = addExtraData(hqs, receiveModel.getJWNames(), receiveModel.getJWCodes());
            }
            if (!receiveModel.getDr_Name().isEmpty()) {
                listedDrs = addExtraData(hqs, receiveModel.getDr_Name(), receiveModel.getDr_Code());
            }
            if (!receiveModel.getChem_Name().isEmpty()) {
                chemists = addExtraData(hqs, receiveModel.getChem_Name(), receiveModel.getChem_Code());
            }
            if (!receiveModel.getStockist_Name().isEmpty()) {
                stockiests = addExtraData(hqs, receiveModel.getStockist_Name(), receiveModel.getStockist_Code());
            }
        }
        //     }
        if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
            sessionList = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks);
        } else {
            sessionList = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, hqs, clusters, JCs, listedDrs, chemists, stockiests, unListedDrs, cips, hospitals, remarks);
        }
        if (!receiveModel.getWTName2().isEmpty()) {
            session2 = true;
            String terrSlFlag2 = findTerrSlFlag(receiveModel.getWTCode2());
            String remarks2 = receiveModel.getDayRemarks2();
            workType = new ModelClass.SessionList.WorkType(receiveModel.getFWFlg2(), receiveModel.getWTName2(), terrSlFlag2, receiveModel.getWTCode2());
            hq = new ModelClass.SessionList.SubClass(receiveModel.getHQNames2(), receiveModel.getHQCodes2());
            clusterArray = new ArrayList<>();
            jcArray = new ArrayList<>();
            drArray = new ArrayList<>();
            chemArray = new ArrayList<>();
            stkArray = new ArrayList<>();
            unListedDrArray = new ArrayList<>();
            cipArray = new ArrayList<>();
            hospArray = new ArrayList<>();

            hqs = new ArrayList<>();
            clusters = new ArrayList<>();
            JCs = new ArrayList<>();
            listedDrs = new ArrayList<>();
            chemists = new ArrayList<>();
            stockiests = new ArrayList<>();
            unListedDrs = new ArrayList<>();
            cips = new ArrayList<>();
            hospitals = new ArrayList<>();

            if (receiveModel.getFWFlg2().equalsIgnoreCase("F")) {
                if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
                    if (!receiveModel.getClusterName2().isEmpty())
                        clusterArray = addExtraData(receiveModel.getClusterName2(), receiveModel.getClusterCode2());
                    if (!receiveModel.getJWNames2().isEmpty())
                        jcArray = addExtraData(receiveModel.getJWNames2(), receiveModel.getJWCodes2());
                    if (!receiveModel.getDr_two_name().isEmpty())
                        drArray = addExtraData(receiveModel.getDr_two_name(), receiveModel.getDr_two_code());
                    if (!receiveModel.getChem_two_name().isEmpty())
                        chemArray = addExtraData(receiveModel.getChem_two_name(), receiveModel.getChem_two_code());
                    if (!receiveModel.getStockist_two_name().isEmpty())
                        stkArray = addExtraData(receiveModel.getStockist_two_name(), receiveModel.getStockist_two_code());
                } else {
                    if (!receiveModel.getHQCodes2().isEmpty()) {
                        hqs = addExtraData(receiveModel.getHQNames2(), receiveModel.getHQCodes2());
                    }
                    if (!receiveModel.getClusterName2().isEmpty()) {
                        clusters = addExtraData(hqs, receiveModel.getClusterName2(), receiveModel.getClusterCode2());
                    }
                    if (!receiveModel.getJWNames2().isEmpty()) {
                        JCs = addExtraData(hqs, receiveModel.getJWNames2(), receiveModel.getJWCodes2());
                    }
                    if (!receiveModel.getDr_two_name().isEmpty()) {
                        listedDrs = addExtraData(hqs, receiveModel.getDr_two_name(), receiveModel.getDr_two_code());
                    }
                    if (!receiveModel.getChem_two_name().isEmpty()) {
                        chemists = addExtraData(hqs, receiveModel.getChem_two_name(), receiveModel.getChem_two_code());
                    }
                    if (!receiveModel.getStockist_two_name().isEmpty()) {
                        stockiests = addExtraData(hqs, receiveModel.getStockist_two_name(), receiveModel.getStockist_two_code());
                    }
                }
            }
            if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
                sessionList2 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks2);
            } else {
                sessionList2 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, hqs, clusters, JCs, listedDrs, chemists, stockiests, unListedDrs, cips, hospitals, remarks2);
            }
        }

        if (!receiveModel.getWTName3().isEmpty()) {
            session3 = true;
            String terrSlFlag3 = findTerrSlFlag(receiveModel.getWTCode3());
            String remarks3 = receiveModel.getDayRemarks2();
            workType = new ModelClass.SessionList.WorkType(receiveModel.getFWFlg3(), receiveModel.getWTName3(), terrSlFlag3, receiveModel.getWTCode3());
            hq = new ModelClass.SessionList.SubClass(receiveModel.getHQNames3(), receiveModel.getHQCodes3());
            clusterArray = new ArrayList<>();
            jcArray = new ArrayList<>();
            drArray = new ArrayList<>();
            chemArray = new ArrayList<>();
            stkArray = new ArrayList<>();
            unListedDrArray = new ArrayList<>();
            cipArray = new ArrayList<>();
            hospArray = new ArrayList<>();

            hqs = new ArrayList<>();
            clusters = new ArrayList<>();
            JCs = new ArrayList<>();
            listedDrs = new ArrayList<>();
            chemists = new ArrayList<>();
            stockiests = new ArrayList<>();
            unListedDrs = new ArrayList<>();
            cips = new ArrayList<>();
            hospitals = new ArrayList<>();

            if (receiveModel.getFWFlg3().equalsIgnoreCase("F")) {
                if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
                    if (!receiveModel.getClusterName3().isEmpty())
                        clusterArray = addExtraData(receiveModel.getClusterName3(), receiveModel.getClusterCode3());
                    if (!receiveModel.getJWNames3().isEmpty())
                        jcArray = addExtraData(receiveModel.getJWNames3(), receiveModel.getJWCodes3());
                    if (!receiveModel.getDr_three_name().isEmpty())
                        drArray = addExtraData(receiveModel.getDr_three_name(), receiveModel.getDr_three_code());
                    if (!receiveModel.getChem_three_name().isEmpty())
                        chemArray = addExtraData(receiveModel.getChem_three_name(), receiveModel.getChem_three_code());
                    if (!receiveModel.getStockist_three_name().isEmpty())
                        stkArray = addExtraData(receiveModel.getStockist_three_name(), receiveModel.getStockist_three_code());
                } else {
                    if (!receiveModel.getHQCodes3().isEmpty()) {
                        hqs = addExtraData(receiveModel.getHQNames3(), receiveModel.getHQCodes3());
                    }
                    if (!receiveModel.getClusterName3().isEmpty()) {
                        clusters = addExtraData(hqs, receiveModel.getClusterName3(), receiveModel.getClusterCode3());
                    }
                    if (!receiveModel.getJWNames3().isEmpty()) {
                        JCs = addExtraData(hqs, receiveModel.getJWNames3(), receiveModel.getJWCodes3());
                    }
                    if (!receiveModel.getDr_three_name().isEmpty()) {
                        listedDrs = addExtraData(hqs, receiveModel.getDr_three_name(), receiveModel.getDr_three_code());
                    }
                    if (!receiveModel.getChem_three_name().isEmpty()) {
                        chemists = addExtraData(hqs, receiveModel.getChem_three_name(), receiveModel.getChem_three_code());
                    }
                    if (!receiveModel.getStockist_three_name().isEmpty()) {
                        stockiests = addExtraData(hqs, receiveModel.getStockist_three_name(), receiveModel.getStockist_three_code());
                    }
                }
            }
            if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
                sessionList3 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks3);
            } else {
                sessionList3 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, hqs, clusters, JCs, listedDrs, chemists, stockiests, unListedDrs, cips, hospitals, remarks3);
            }
        }

        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
        sessionLists.add(sessionList);
        if (session2) sessionLists.add(sessionList2);
        if (session3) sessionLists.add(sessionList3);
        ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists, receiveModel.getSTP_Code(), receiveModel.getSTP_Name());
        modelClass.setSubmittedTime(submittedTime);
        modelClasses.add(modelClass);
        dayWiseSaveTp = modelClasses;
        saveTpLocal(dayWiseSaveTp, day, monthName, "0");
    }

    private void SaveTpLocalFullOneBuild(ReceiveModel receiveModel, ArrayList<OneBuildModelClass> oneBuildModelClasses, ArrayList<OneBuildModelClass> dayWiseSaveTp, String day, String monthName, String date, String dayName, String monthNo, String year) {
        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        OneBuildModelClass.SessionList sessionList = new OneBuildModelClass.SessionList();
        OneBuildModelClass.SessionList sessionList2 = new OneBuildModelClass.SessionList();
        OneBuildModelClass.SessionList sessionList3 = new OneBuildModelClass.SessionList();
        isDataAvailable = true;
        boolean session2 = false;
        boolean session3 = false;

        String terrSlFlag = findTerrSlFlag(receiveModel.getWTCode());
        String remarks = receiveModel.getDayRemarks();
        String submittedTime = receiveModel.getSubmitted_time_dt();

        ArrayList<OneBuildModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> chemArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> stkArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        OneBuildModelClass.SessionList.WorkType workType = new OneBuildModelClass.SessionList.WorkType(receiveModel.getFWFlg(), receiveModel.getWTName(), terrSlFlag, receiveModel.getWTCode());
        OneBuildModelClass.SessionList.SubClass hq = new OneBuildModelClass.SessionList.SubClass(receiveModel.getHQNames(), receiveModel.getHQCodes());

        //   if (receiveModel.getFWFlg().equalsIgnoreCase("F")) {
        if (!receiveModel.getClusterName().isEmpty())
            clusterArray = addExtraDataOneBuild(receiveModel.getClusterName(), receiveModel.getClusterCode());
        if (!receiveModel.getJWNames().isEmpty())
            jcArray = addExtraDataOneBuild(receiveModel.getJWNames(), receiveModel.getJWCodes());
        if (!receiveModel.getDr_Name().isEmpty())
            drArray = addExtraDataOneBuild(receiveModel.getDr_Name(), receiveModel.getDr_Code());
        if (!receiveModel.getChem_Name().isEmpty())
            chemArray = addExtraDataOneBuild(receiveModel.getChem_Name(), receiveModel.getChem_Code());
        if (!receiveModel.getStockist_Name().isEmpty())
            stkArray = addExtraDataOneBuild(receiveModel.getStockist_Name(), receiveModel.getStockist_Code());
        //     }
        sessionList = prepareSessionListForAdapterOneBuild(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks);

        if (!receiveModel.getWTName2().isEmpty()) {
            session2 = true;
            String terrSlFlag2 = findTerrSlFlag(receiveModel.getWTCode2());
            String remarks2 = receiveModel.getDayRemarks2();
            workType = new OneBuildModelClass.SessionList.WorkType(receiveModel.getFWFlg2(), receiveModel.getWTName2(), terrSlFlag2, receiveModel.getWTCode2());
            hq = new OneBuildModelClass.SessionList.SubClass(receiveModel.getHQNames2(), receiveModel.getHQCodes2());
            clusterArray = new ArrayList<>();
            jcArray = new ArrayList<>();
            drArray = new ArrayList<>();
            chemArray = new ArrayList<>();
            stkArray = new ArrayList<>();
            unListedDrArray = new ArrayList<>();
            cipArray = new ArrayList<>();
            hospArray = new ArrayList<>();

            if (!receiveModel.getClusterName2().isEmpty())
                clusterArray = addExtraDataOneBuild(receiveModel.getClusterName2(), receiveModel.getClusterCode2());
            if (!receiveModel.getJWNames2().isEmpty())
                jcArray = addExtraDataOneBuild(receiveModel.getJWNames2(), receiveModel.getJWCodes2());
            if (!receiveModel.getDr_two_name().isEmpty())
                drArray = addExtraDataOneBuild(receiveModel.getDr_two_name(), receiveModel.getDr_two_code());
            if (!receiveModel.getChem_two_name().isEmpty())
                chemArray = addExtraDataOneBuild(receiveModel.getChem_two_name(), receiveModel.getChem_two_code());
            if (!receiveModel.getStockist_two_name().isEmpty())
                stkArray = addExtraDataOneBuild(receiveModel.getStockist_two_name(), receiveModel.getStockist_two_code());

            sessionList2 = prepareSessionListForAdapterOneBuild(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks2);

        }
        if (!receiveModel.getWTName3().isEmpty()) {
            session3 = true;
            String terrSlFlag3 = findTerrSlFlag(receiveModel.getWTCode3());
            String remarks3 = receiveModel.getDayRemarks2();
            workType = new OneBuildModelClass.SessionList.WorkType(receiveModel.getFWFlg3(), receiveModel.getWTName3(), terrSlFlag3, receiveModel.getWTCode3());
            hq = new OneBuildModelClass.SessionList.SubClass(receiveModel.getHQNames3(), receiveModel.getHQCodes3());
            clusterArray = new ArrayList<>();
            jcArray = new ArrayList<>();
            drArray = new ArrayList<>();
            chemArray = new ArrayList<>();
            stkArray = new ArrayList<>();
            unListedDrArray = new ArrayList<>();
            cipArray = new ArrayList<>();
            hospArray = new ArrayList<>();

            if (!receiveModel.getClusterName3().isEmpty())
                clusterArray = addExtraDataOneBuild(receiveModel.getClusterName3(), receiveModel.getClusterCode3());
            if (!receiveModel.getJWNames3().isEmpty())
                jcArray = addExtraDataOneBuild(receiveModel.getJWNames3(), receiveModel.getJWCodes3());
            if (!receiveModel.getDr_three_name().isEmpty())
                drArray = addExtraDataOneBuild(receiveModel.getDr_three_name(), receiveModel.getDr_three_code());
            if (!receiveModel.getChem_three_name().isEmpty())
                chemArray = addExtraDataOneBuild(receiveModel.getChem_three_name(), receiveModel.getChem_three_code());
            if (!receiveModel.getStockist_three_name().isEmpty())
                stkArray = addExtraDataOneBuild(receiveModel.getStockist_three_name(), receiveModel.getStockist_three_code());
            sessionList3 = prepareSessionListForAdapterOneBuild(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks3);
        }
        ArrayList<OneBuildModelClass.SessionList> sessionLists = new ArrayList<>();
        sessionLists.add(sessionList);
        if (session2) sessionLists.add(sessionList2);
        if (session3) sessionLists.add(sessionList3);
        OneBuildModelClass modelClass = new OneBuildModelClass(day, date, dayName, monthNo, year, true, sessionLists, receiveModel.getSTP_Code(), receiveModel.getSTP_Name());
        modelClass.setSubmittedTime(submittedTime);
        oneBuildModelClasses.add(modelClass);
        dayWiseSaveTp = oneBuildModelClasses;
        saveTpLocalOneBuild(dayWiseSaveTp, day, monthName, "0");
    }

    private ArrayList<ModelClass.SessionList.SubClass> addExtraData(String Name, String Code) {
        String[] arrName = Name.split(",");
        String[] arrCode = Code.split(",");
        ArrayList<String> dummyName = new ArrayList<>(Arrays.asList(arrName));
        ArrayList<String> dummyCode = new ArrayList<>(Arrays.asList(arrCode));
        ArrayList<ModelClass.SessionList.SubClass> Array = new ArrayList<>();

        for (int i = 0; i < dummyName.size(); i++) {
            Array.add(new ModelClass.SessionList.SubClass(dummyName.get(i), dummyCode.get(i)));
        }

        return Array;
    }

    private ArrayList<OneBuildModelClass.SessionList.SubClass> addExtraDataOneBuild(String Name, String Code) {
        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        String[] arrName = Name.split(",");
        String[] arrCode = Code.split(",");
        ArrayList<String> dummyName = new ArrayList<>(Arrays.asList(arrName));
        ArrayList<String> dummyCode = new ArrayList<>(Arrays.asList(arrCode));
        ArrayList<OneBuildModelClass.SessionList.SubClass> Array = new ArrayList<>();

        for (int i = 0; i < dummyName.size(); i++) {
            Array.add(new OneBuildModelClass.SessionList.SubClass(dummyName.get(i), dummyCode.get(i)));
        }

        return Array;
    }

    private ArrayList<MultiHQHeaderModelClass> addExtraData(ArrayList<ModelClass.SessionList.SubClass> hqs, String Name, String Code) {
        String[] arrName = Name.split("\\$");
        String[] arrCode = Code.split("\\$");
        ArrayList<MultiHQHeaderModelClass> resultArray = new ArrayList<>();

        for (int i = 0; i < hqs.size(); i++) {
            ModelClass.SessionList.SubClass hq = hqs.get(i);
            if (arrCode.length > i) {
                try {
                    String[] names = arrName[i].split(",");
                    String[] codes = arrCode[i].split(",");
                    ArrayList<MultiHQItemModelClass> itemsList = new ArrayList<>();
                    MultiHQHeaderModelClass multiHQHeaderModelClass = new MultiHQHeaderModelClass(hq.getName(), hq.getCode(), itemsList, true);
                    for (int j = 0; j < codes.length; j++) {
                        MultiHQItemModelClass multiHQItemModelClass = new MultiHQItemModelClass(names[j], codes[j], hq.getCode(), "", "", true);
                        itemsList.add(multiHQItemModelClass);
                    }
                    multiHQHeaderModelClass.setItemsList(itemsList);
                    if (!itemsList.isEmpty()) {
                        resultArray.add(multiHQHeaderModelClass);
                    }
                } catch (Exception e) {
                    Log.d("TAG", "addExtraData: " + Name + Code);
                    Log.d("TAG", "addExtraData: " + Arrays.asList(arrName).toString() + Arrays.asList(arrCode).toString());
                    e.printStackTrace();
                }
            }
        }
        return resultArray;
    }

    public void get1MonthRemoteTPData(LocalDate localDate1) {
        try {
            apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(TourPlanActivity.this);
            if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                jsonObject.put("tableName", "gettpdetail");
            } else {
                jsonObject.put("tableName", "gettpmultihqdetailnew");
            }
            jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
            jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
            jsonObject.put("Month", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_25, TimeUtils.FORMAT_8, localDate1.getMonth().toString()));
            jsonObject.put("Year", localDate1.getYear());
            Log.d("TAG", "get1MonthRemoteTPData: " + jsonObject);


            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "get/tp");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(TourPlanActivity.this), mapString, jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isJsonArray()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.body().getAsJsonArray().toString());
                                if (jsonArray.length() > 0) {
                                    String status = jsonArray.getJSONObject(0).getString("Change_Status");
                                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), status);

                                    switch (status) {
                                        case "0": {
                                            binding.tpStatusTxt.setText(R.string.planning);
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                                            break;
                                        }
                                        case "1": {
                                            binding.tpStatusTxt.setText(R.string.tp_waiting_for_approval);
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                                            break;
                                        }
                                        case "2": {
                                            binding.tpStatusTxt.setText(R.string.tp_rejected);
                                            binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                                            SetTpRangeStatus();
                                            break;
                                        }
                                        case "3": {
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.pink));
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setText(R.string.tp_approved);
                                            break;
                                        }
                                        default: {
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setText(R.string.planning);
                                            break;
                                        }
                                    }
                                    binding.tpSendToApproval.setEnabled(false);
                                    binding.tpNavigation.sessionEdit.setEnabled(false);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void get1MonthRemoteTPDataOneBuild(LocalDate localDate1) {
        try {
//            ProgressDialog progressDialog = new ProgressDialog(this);  //remove this
//            progressDialog.show();
            apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getBaseWebUrl(TourPlanActivity.this));
            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(TourPlanActivity.this);
            jsonObject.put("tableName", "gettpdetail_onebuild");
            jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
            jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
            jsonObject.put("Month", localDate1.getMonthValue()/*TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_25, TimeUtils.FORMAT_8, localDate1.getMonth().toString())*/);
            jsonObject.put("Year", localDate1.getYear());
            Log.v("TAG", "json--" + jsonObject);

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "get/tp");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getBaseWebUrl(TourPlanActivity.this) + "iOSServer/db_api.php/", mapString, jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful() && response.body() != null) {
//                        progressDialog.dismiss();
                        binding.progressBar.setVisibility(View.GONE);
                        uiInitializationOneBuild();
                        if (response.body().isJsonArray()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.body().getAsJsonArray().toString());
                                if (jsonArray.length() > 0) {
                                    String status = jsonArray.getJSONObject(0).getString("Change_Status");
                                    String reason = jsonArray.getJSONObject(0).getString("Rejection_Reason");
                                    tourPlanOfflineDataDao.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), status, reason);

                                    TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate1)));
                                    if (tourPlanOfflineDataTable != null) {
                                        status = tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty();
                                        reason = tourPlanOfflineDataTable.getTpRejectionReasonOrEmpty();
                                    }
                                    switch (status) {
                                        case "0": {
                                            binding.tpStatusTxt.setText(R.string.planning);
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                                            System.out.println("status 0");
                                            break;
                                        }
                                        case "1": {
                                            binding.tpStatusTxt.setText(R.string.tp_waiting_for_approval);
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                                            binding.tpSendToApproval.setEnabled(false);
                                            binding.tpNavigation.sessionEdit.setEnabled(false);
                                            System.out.println("status 1");
                                            break;
                                        }
                                        case "2": {
                                            binding.tpStatusTxt.setText(R.string.tp_rejected);
                                            binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.pink));
                                            binding.rejectedReasonTxt.setText(reason);
                                            binding.tpSendToApproval.setEnabled(true);
                                            binding.tpNavigation.sessionEdit.setEnabled(true);
                                            System.out.println("status 2");
                                            break;
                                        }
                                        case "3": {
                                            binding.tpStatusTxt.setText(R.string.tp_approved);
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                                            binding.tpSendToApproval.setEnabled(false);
                                            binding.tpNavigation.sessionEdit.setEnabled(false);
                                            SetTpRangeStatus();
                                            System.out.println("status 3");
                                            break;
                                        }
                                        default: {
                                            binding.rejectionReasonLayout.setVisibility(View.GONE);
                                            binding.tpStatusTxt.setText(R.string.planning);
                                            break;
                                        }
                                    }
//                                    binding.tpSendToApproval.setEnabled(false);
//                                    binding.tpNavigation.sessionEdit.setEnabled(false);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
//                            progressDialog.dismiss();
                            binding.progressBar.setVisibility(View.GONE);
                            System.out.println("No Response");
                        }
                    } else {
                        System.out.println("No Response");
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    System.out.println("On Failure");

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendWholeMonthStatus(LocalDate localDate1) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(TourPlanActivity.this);
                        jsonObject.put("tableName", "tpsend_appr");
                        jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                        jsonObject.put("TPMonth", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_25, TimeUtils.FORMAT_31, localDate1.getMonth().toString()));
                        jsonObject.put("TPYear", localDate1.getYear());

                        Log.v("ApprovalObject", String.valueOf(jsonObject));

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "save/tp");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(TourPlanActivity.this), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.v("tpApproval", "--ressapproval--" + response.body());
                                binding.progressBar.setVisibility(View.GONE);
                                if (response.body() != null) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body().getAsJsonObject().toString());
                                        if (jsonObject1.has("success") && jsonObject1.getBoolean("success")) {
//                                            sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "0"); // "0" - success
                                            tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "0");
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.send_approved_successfully));
                                            get1MonthRemoteTPData(localDate1);
                                        } else {
//                                            sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                                            tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.failed_to_send_approval));
                                        }
                                    } catch (JSONException e) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        e.printStackTrace();
                                    }
                                } else {
                                    binding.progressBar.setVisibility(View.GONE);
//                                    sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                binding.progressBar.setVisibility(View.GONE);
//                                sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                                tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                            }
                        });
                    } catch (JSONException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    binding.progressBar.setVisibility(View.GONE);
//                    sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                }
            }
        });
        networkStatusTask.execute();
    }

    public void sendWholeMonthStatusOneBuild(LocalDate localDate1, String isClickedName) {
        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getBaseWebUrl(TourPlanActivity.this));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(TourPlanActivity.this);
                        jsonObject.put("tableName", "tpsend_appr_onebuild");
                        jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                        jsonObject.put("TPMonth", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_25, TimeUtils.FORMAT_31, localDate1.getMonth().toString()));
                        jsonObject.put("TPYear", localDate1.getYear());
                        jsonObject.put("Status",1);


                        Log.v("ApprovalObject", String.valueOf(jsonObject));

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "save/tp");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getBaseWebUrl(TourPlanActivity.this) + "iOSServer/db_api.php/", mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.v("tpApproval", "--ressapproval--" + response.body());
                                binding.progressBar.setVisibility(View.GONE);
                                if (response.body() != null) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body().getAsJsonObject().toString());
                                        if (jsonObject1.has("success") && jsonObject1.getBoolean("success")) {

                                            tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "0");
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.send_approved_successfully));

                                            switch (isClickedName) {
                                                case "previous":
                                                    changeApprovalBtnStateOneBuild(dayWiseArrayPreviousMonthOneBuild);
                                                    break;
                                                case "current":
                                                    changeApprovalBtnStateOneBuild(dayWiseArrayCurrentMonthOneBuild);
                                                    break;
                                                case "next":
                                                    changeApprovalBtnStateOneBuild(dayWiseArrayNextMonthOneBuild);
                                                    break;
                                            }

                                        } else {
                                            tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.failed_to_send_approval));
                                        }
                                        get1MonthRemoteTPDataOneBuild(localDate);
                                    } catch (JSONException e) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        e.printStackTrace();
                                    }
                                } else {
                                    binding.progressBar.setVisibility(View.GONE);

                                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                binding.progressBar.setVisibility(View.GONE);
                                tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                            }
                        });
                    } catch (JSONException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                }
            }
        });
        networkStatusTask.execute();
    }

    public String findTerrSlFlag(String code) {
        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray(); //List of Work Types
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (code.equals(jsonObject.getString("Code")))
                    return jsonObject.getString("TerrSlFlg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ModelClass.SessionList.WorkType getWorkType(String fwFlag) {
        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (fwFlag.equals(jsonObject.getString("FWFlg"))) {
                    return new ModelClass.SessionList.WorkType(jsonObject.optString("FWFlg"), jsonObject.optString("Name"), jsonObject.optString("TerrSlFlg"), jsonObject.optString("Code"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OneBuildModelClass.SessionList.WorkType getWorkTypeOneBuild(String fwFlag) {
        SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0");
        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (fwFlag.equals(jsonObject.getString("FWFlg"))) {
                    return new OneBuildModelClass.SessionList.WorkType(jsonObject.optString("FWFlg"), jsonObject.optString("Name"), jsonObject.optString("TerrSlFlg"), jsonObject.optString("Code"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void prepareObjectToSendForApproval(String month, String dateForApproval, ArrayList<ModelClass> arrayList, boolean statusOffline) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if (status) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (ModelClass modelClass : arrayList) {
                            if (!modelClass.getDayNo().isEmpty() && !modelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) {
                                if (modelClass.getDayNo().equals(dateForApproval)) {
                                    JSONObject jsonObject = new JSONObject();

                                    jsonObject.put("SFCode", SharedPref.getSfCode(TourPlanActivity.this));
                                    jsonObject.put("SFName", SharedPref.getSfName(TourPlanActivity.this));
                                    jsonObject.put("Div", SharedPref.getDivisionCode(TourPlanActivity.this));
                                    jsonObject.put("Mnth", modelClass.getMonth());
                                    jsonObject.put("Yr", modelClass.getYear());
                                    jsonObject.put("dayno", modelClass.getDayNo());
                                    jsonObject.put("Change_Status", "0");
                                    jsonObject.put("Rejection_Reason", "");
                                    jsonObject.put("TPDt", TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_19, TimeUtils.FORMAT_4, modelClass.getDate()) + " 00:00:00");
                                    jsonObject.put("submitted_time", TimeUtils.getCurrentDateTimeTp(TimeUtils.FORMAT_37));
                                    jsonObject.put("Entry_mode", Constants.APP_MODE);
                                    jsonObject.put("Approve_mode", "");
                                    jsonObject.put("Approved_time", "");
                                    jsonObject.put("app_version", BuildConfig.VERSION_NAME);
                                    jsonObject.put("Mode", "Android-Edet");
                                    if (isSTPBasedTP) {
                                        jsonObject.put("STP_Code", modelClass.getSTP_Code() != null ? modelClass.getSTP_Code() : "");
                                        jsonObject.put("STP_Name", modelClass.getSTP_Name() != null ? modelClass.getSTP_Name() : "");
                                    }

                                    String WTCode = "", WTName = "", FWFlg = "", HQCodes = "", HQNames = "", clusterCodes = "", clusterNames = "", JWCodes = "", JWNames = "", Dr_Code = "", Dr_Name = "", Chem_Code = "", Chem_Name = "", Stockist_Code = "", Stockist_Name = "", cip_code = "", cip_name = "", hosp_code = "", hosp_Name = "", DayRemarks = "";
                                    String WTCode2 = "", WTName2 = "", FWFlg2 = "", HQCodes2 = "", HQNames2 = "", clusterCode2 = "", clusterName2 = "", JWCodes2 = "", JWNames2 = "", Dr_two_code = "", Dr_two_name = "", Chem_two_code = "", Chem_two_name = "", Stockist_two_code = "", Stockist_two_name = "", cip_code2 = "", cip_name2 = "", hosp_code2 = "", hosp_Name2 = "", DayRemarks2 = "";
                                    String WTCode3 = "", WTName3 = "", FWFlg3 = "", HQCodes3 = "", HQNames3 = "", clusterCode3 = "", clusterName3 = "", JWCodes3 = "", JWNames3 = "", Dr_three_code = "", Dr_three_name = "", Chem_three_code = "", Chem_three_name = "", Stockist_three_code = "", Stockist_three_name = "", cip_code3 = "", cip_name3 = "", hosp_code3 = "", hosp_Name3 = "", DayRemarks3 = "";

                                    for (int i = 0; i < modelClass.getSessionList().size(); i++) {
                                        ModelClass.SessionList sessionList = modelClass.getSessionList().get(i);
                                        if (i == 0) {
                                            WTCode = sessionList.getWorkType().getCode();
                                            WTName = sessionList.getWorkType().getName();
                                            FWFlg = sessionList.getWorkType().getFWFlg();
                                            if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                                                HQCodes = sessionList.getHQ().getCode();
                                                HQNames = sessionList.getHQ().getName();
                                                clusterCodes = textBuilder(sessionList.getCluster(), true);
                                                clusterNames = textBuilder(sessionList.getCluster(), false);
                                                JWCodes = textBuilder(sessionList.getJC(), true);
                                                JWNames = textBuilder(sessionList.getJC(), false);
                                                Dr_Code = textBuilder(sessionList.getListedDr(), true);
                                                Dr_Name = textBuilder(sessionList.getListedDr(), false);
                                                Chem_Code = textBuilder(sessionList.getChemist(), true);
                                                Chem_Name = textBuilder(sessionList.getChemist(), false);
                                                Stockist_Code = textBuilder(sessionList.getStockiest(), true);
                                                Stockist_Name = textBuilder(sessionList.getStockiest(), false);
                                                cip_code = textBuilder(sessionList.getCip(), true);
                                                cip_name = textBuilder(sessionList.getCip(), false);
                                                hosp_code = textBuilder(sessionList.getHospital(), true);
                                                hosp_Name = textBuilder(sessionList.getHospital(), false);
                                            } else if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                                HQCodes = textBuilder(sessionList.getHQs(), true);
                                                HQNames = textBuilder(sessionList.getHQs(), false);
                                                clusterCodes = textBuilderMultiHQ(sessionList.getClusters(), true);
                                                clusterNames = textBuilderMultiHQ(sessionList.getClusters(), false);
                                                JWCodes = textBuilderMultiHQ(sessionList.getJCs(), true);
                                                JWNames = textBuilderMultiHQ(sessionList.getJCs(), false);
                                                Dr_Code = textBuilderMultiHQ(sessionList.getListedDrs(), true);
                                                Dr_Name = textBuilderMultiHQ(sessionList.getListedDrs(), false);
                                                Chem_Code = textBuilderMultiHQ(sessionList.getChemists(), true);
                                                Chem_Name = textBuilderMultiHQ(sessionList.getChemists(), false);
                                                Stockist_Code = textBuilderMultiHQ(sessionList.getStockiests(), true);
                                                Stockist_Name = textBuilderMultiHQ(sessionList.getStockiests(), false);
                                                cip_code = textBuilderMultiHQ(sessionList.getCips(), true);
                                                cip_name = textBuilderMultiHQ(sessionList.getCips(), false);
                                                hosp_code = textBuilderMultiHQ(sessionList.getHospitals(), true);
                                                hosp_Name = textBuilderMultiHQ(sessionList.getHospitals(), false);
                                            }
                                            DayRemarks = sessionList.getRemarks();
                                        } else if (i == 1) {
                                            WTCode2 = sessionList.getWorkType().getCode();
                                            WTName2 = sessionList.getWorkType().getName();
                                            FWFlg2 = sessionList.getWorkType().getFWFlg();
                                            if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                                                HQCodes2 = sessionList.getHQ().getCode();
                                                HQNames2 = sessionList.getHQ().getName();
                                                clusterCode2 = textBuilder(sessionList.getCluster(), true);
                                                clusterName2 = textBuilder(sessionList.getCluster(), false);
                                                JWCodes2 = textBuilder(sessionList.getJC(), true);
                                                JWNames2 = textBuilder(sessionList.getJC(), false);
                                                Dr_two_code = textBuilder(sessionList.getListedDr(), true);
                                                Dr_two_name = textBuilder(sessionList.getListedDr(), false);
                                                Chem_two_code = textBuilder(sessionList.getChemist(), true);
                                                Chem_two_name = textBuilder(sessionList.getChemist(), false);
                                                Stockist_two_code = textBuilder(sessionList.getStockiest(), true);
                                                Stockist_two_name = textBuilder(sessionList.getStockiest(), false);
                                                cip_code2 = textBuilder(sessionList.getCip(), true);
                                                cip_name2 = textBuilder(sessionList.getCip(), false);
                                                hosp_code2 = textBuilder(sessionList.getHospital(), true);
                                                hosp_Name2 = textBuilder(sessionList.getHospital(), false);
                                            } else if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                                HQCodes2 = textBuilder(sessionList.getHQs(), true);
                                                HQNames2 = textBuilder(sessionList.getHQs(), false);
                                                clusterCode2 = textBuilderMultiHQ(sessionList.getClusters(), true);
                                                clusterName2 = textBuilderMultiHQ(sessionList.getClusters(), false);
                                                JWCodes2 = textBuilderMultiHQ(sessionList.getJCs(), true);
                                                JWNames2 = textBuilderMultiHQ(sessionList.getJCs(), false);
                                                Dr_two_code = textBuilderMultiHQ(sessionList.getListedDrs(), true);
                                                Dr_two_name = textBuilderMultiHQ(sessionList.getListedDrs(), false);
                                                Chem_two_code = textBuilderMultiHQ(sessionList.getChemists(), true);
                                                Chem_two_name = textBuilderMultiHQ(sessionList.getChemists(), false);
                                                Stockist_two_code = textBuilderMultiHQ(sessionList.getStockiests(), true);
                                                Stockist_two_name = textBuilderMultiHQ(sessionList.getStockiests(), false);
                                                cip_code2 = textBuilderMultiHQ(sessionList.getCips(), true);
                                                cip_name2 = textBuilderMultiHQ(sessionList.getCips(), false);
                                                hosp_code2 = textBuilderMultiHQ(sessionList.getHospitals(), true);
                                                hosp_Name2 = textBuilderMultiHQ(sessionList.getHospitals(), false);
                                            }
                                            DayRemarks2 = sessionList.getRemarks();
                                        } else if (i == 2) {
                                            WTCode3 = sessionList.getWorkType().getCode();
                                            WTName3 = sessionList.getWorkType().getName();
                                            FWFlg3 = sessionList.getWorkType().getFWFlg();
                                            if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
                                                HQCodes3 = sessionList.getHQ().getCode();
                                                HQNames3 = sessionList.getHQ().getName();
                                                clusterCode3 = textBuilder(sessionList.getCluster(), true);
                                                clusterName3 = textBuilder(sessionList.getCluster(), false);
                                                JWCodes3 = textBuilder(sessionList.getJC(), true);
                                                JWNames3 = textBuilder(sessionList.getJC(), false);
                                                Dr_three_code = textBuilder(sessionList.getListedDr(), true);
                                                Dr_three_name = textBuilder(sessionList.getListedDr(), false);
                                                Chem_three_code = textBuilder(sessionList.getChemist(), true);
                                                Chem_three_name = textBuilder(sessionList.getChemist(), false);
                                                Stockist_three_code = textBuilder(sessionList.getStockiest(), true);
                                                Stockist_three_name = textBuilder(sessionList.getStockiest(), false);
                                                cip_code3 = textBuilder(sessionList.getCip(), true);
                                                cip_name3 = textBuilder(sessionList.getCip(), false);
                                                hosp_code3 = textBuilder(sessionList.getHospital(), true);
                                                hosp_Name3 = textBuilder(sessionList.getHospital(), false);
                                            } else if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("2") && !SharedPref.getOneBuild(TourPlanActivity.this).equalsIgnoreCase("0")) {
                                                HQCodes3 = textBuilder(sessionList.getHQs(), true);
                                                HQNames3 = textBuilder(sessionList.getHQs(), false);
                                                clusterCode3 = textBuilderMultiHQ(sessionList.getClusters(), true);
                                                clusterName3 = textBuilderMultiHQ(sessionList.getClusters(), false);
                                                JWCodes3 = textBuilderMultiHQ(sessionList.getJCs(), true);
                                                JWNames3 = textBuilderMultiHQ(sessionList.getJCs(), false);
                                                Dr_three_code = textBuilderMultiHQ(sessionList.getListedDrs(), true);
                                                Dr_three_name = textBuilderMultiHQ(sessionList.getListedDrs(), false);
                                                Chem_three_code = textBuilderMultiHQ(sessionList.getChemists(), true);
                                                Chem_three_name = textBuilderMultiHQ(sessionList.getChemists(), false);
                                                Stockist_three_code = textBuilderMultiHQ(sessionList.getStockiests(), true);
                                                Stockist_three_name = textBuilderMultiHQ(sessionList.getStockiests(), false);
                                                cip_code3 = textBuilderMultiHQ(sessionList.getCips(), true);
                                                cip_name3 = textBuilderMultiHQ(sessionList.getCips(), false);
                                                hosp_code3 = textBuilderMultiHQ(sessionList.getHospitals(), true);
                                                hosp_Name3 = textBuilderMultiHQ(sessionList.getHospitals(), false);
                                            }
                                        }
                                    }

                                    jsonObject.put("WTCode", WTCode);
                                    jsonObject.put("WTName", WTName);
                                    jsonObject.put("FWFlg", FWFlg);
                                    jsonObject.put("HQCodes", HQCodes);
                                    jsonObject.put("HQNames", HQNames);
                                    jsonObject.put("ClusterCode", clusterCodes);
                                    jsonObject.put("ClusterName", clusterNames);
                                    jsonObject.put("JWCodes", JWCodes);
                                    jsonObject.put("JWNames", JWNames);
                                    jsonObject.put("Dr_Code", Dr_Code);
                                    jsonObject.put("Dr_Name", Dr_Name);
                                    jsonObject.put("Chem_Code", Chem_Code);
                                    jsonObject.put("Chem_Name", Chem_Name);
                                    jsonObject.put("Stockist_Code", Stockist_Code);
                                    jsonObject.put("Stockist_Name", Stockist_Name);
                                    jsonObject.put("cip_code", cip_code);
                                    jsonObject.put("cip_name", cip_name);
                                    jsonObject.put("hosp_code", hosp_code);
                                    jsonObject.put("hosp_Name", hosp_Name);
                                    jsonObject.put("DayRemarks", DayRemarks);

                                    jsonObject.put("WTCode2", WTCode2);
                                    jsonObject.put("WTName2", WTName2);
                                    jsonObject.put("FWFlg2", FWFlg2);
                                    jsonObject.put("HQCodes2", HQCodes2);
                                    jsonObject.put("HQNames2", HQNames2);
                                    jsonObject.put("ClusterCode2", clusterCode2);
                                    jsonObject.put("ClusterName2", clusterName2);
                                    jsonObject.put("JWCodes2", JWCodes2);
                                    jsonObject.put("JWNames2", JWNames2);
                                    jsonObject.put("Dr_two_code", Dr_two_code);
                                    jsonObject.put("Dr_two_name", Dr_two_name);
                                    jsonObject.put("Chem_two_code", Chem_two_code);
                                    jsonObject.put("Chem_two_name", Chem_two_name);
                                    jsonObject.put("Stockist_two_code", Stockist_two_code);  //Stockist_two_name
                                    jsonObject.put("Stockist_two_name", Stockist_two_name);
                                    jsonObject.put("cip_code2", cip_code2);
                                    jsonObject.put("cip_name2", cip_name2);
                                    jsonObject.put("hosp_code2", hosp_code2);
                                    jsonObject.put("hosp_Name2", hosp_Name2);
                                    jsonObject.put("DayRemarks2", DayRemarks2);

                                    jsonObject.put("WTCode3", WTCode3);
                                    jsonObject.put("WTName3", WTName3);
                                    jsonObject.put("FWFlg3", FWFlg3);
                                    jsonObject.put("HQCodes3", HQCodes3);
                                    jsonObject.put("HQNames3", HQNames3);
                                    jsonObject.put("ClusterCode3", clusterCode3);
                                    jsonObject.put("ClusterName3", clusterName3);
                                    jsonObject.put("JWCodes3", JWCodes3);
                                    jsonObject.put("JWNames3", JWNames3);
                                    jsonObject.put("Dr_three_code", Dr_three_code);
                                    jsonObject.put("Dr_three_name", Dr_three_name);
                                    jsonObject.put("Chem_three_code", Chem_three_code);
                                    jsonObject.put("Chem_three_name", Chem_three_name);
                                    jsonObject.put("Stockist_three_code", Stockist_three_code);
                                    jsonObject.put("Stockist_three_name", Stockist_three_name);
                                    jsonObject.put("cip_code3", cip_code3);
                                    jsonObject.put("cip_name3", cip_name3);
                                    jsonObject.put("hosp_code3", hosp_code3);
                                    jsonObject.put("hosp_Name3", hosp_Name3);
                                    jsonObject.put("DayRemarks3", DayRemarks3);

                                    jsonArray.put(jsonObject);
                                    Log.d("TAG", "prepare json: " + jsonArray);
                                    break;
                                }
                            }
                        }
                        sendTpForApproval(jsonArray, arrayList, dateForApproval, month, statusOffline);
                    } catch (JSONException ex) {
                        if (statusOffline) {
                            binding.progressBar.setVisibility(View.GONE);
                        }
                        ex.printStackTrace();
                    }
                } else {
                    if (statusOffline) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                    saveTpLocal(arrayList, dateForApproval, month, "1"); // Sync Failed
                }
            }
        });
        networkStatusTask.execute();
    }

    public String textBuilder(List<ModelClass.SessionList.SubClass> sessionLists, boolean codeOrName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < sessionLists.size(); i++) {
            if (codeOrName) { // true -> code
                stringBuilder.append(sessionLists.get(i).getCode()).append(",");
            } else { // false -> name
                stringBuilder.append(sessionLists.get(i).getName()).append(",");
            }
        }
        return stringBuilder.toString();
    }

    public static String textBuilder_OB(List<OneBuildModelClass.SessionList.SubClass> sessionLists, boolean codeOrName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < sessionLists.size(); i++) {
            if (codeOrName) { // true -> code
                stringBuilder.append(sessionLists.get(i).getCode()).append(",");
            } else { // false -> name
                stringBuilder.append(sessionLists.get(i).getName()).append(",");
            }
        }
        return stringBuilder.toString();
    }

    public String textBuilderMultiHQ(List<MultiHQHeaderModelClass> multiHQHeaderModelClassList, boolean getCode) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < multiHQHeaderModelClassList.size(); i++) {
            MultiHQHeaderModelClass multiHQHeaderModelClass = multiHQHeaderModelClassList.get(i);
            List<MultiHQItemModelClass> multiHQItemModelClassList = multiHQHeaderModelClass.getItemsList();
            boolean isDataFound = false;
            for (MultiHQItemModelClass multiHQItemModelClass : multiHQItemModelClassList) {
                if (getCode) { // true -> code
                    stringBuilder.append(multiHQItemModelClass.getCode()).append(",");
                    isDataFound = true;
                } else { // false -> name
                    stringBuilder.append(multiHQItemModelClass.getName()).append(",");
                    isDataFound = true;
                }
            }
            if (!stringBuilder.toString().isEmpty() && isDataFound) {
                stringBuilder.append("$");
            } else if (!isDataFound) {
                stringBuilder.append(",$");
            }
        }
        Log.i("TAG", "textBuilderMultiHQ: " + stringBuilder);
        return stringBuilder.toString();
    }

    public void sendTpForApproval(JSONArray jsonArray, ArrayList<ModelClass> modelClassArrayList, String date, String month, Boolean statusOffline) {
        apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
        Log.v("tpApproval", "--json--" + jsonArray.toString());
        Map<String, String> mapString = new HashMap<>();
        if (SharedPref.getSfType(TourPlanActivity.this).equalsIgnoreCase("1")) {
            mapString.put("axn", "savenew/tp");
        } else {
            mapString.put("axn", "multihqsavenew/tp");
        }
        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonArray.toString());
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.v("tpApproval", "--res--" + response.body());
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject jsonObject1 = new JSONObject(response.body().toString());
                        if (jsonObject1.getBoolean("success")) {
                            saveTpLocal(modelClassArrayList, date, month, "0");// Sync Success
                            if (statusOffline) {
                                JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
                                ArrayList<ModelClass> arrayList;
                                ArrayList<String> dummy = new ArrayList<>();
                                Type type = new TypeToken<ArrayList<ModelClass>>() {
                                }.getType();
                                if (jsonArray.length() > 0) {
                                    arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
                                    for (ModelClass modelClass : arrayList) {
                                        if (!modelClass.getDate().equals("") && !modelClass.getSyncStatus().equals("0")) {
                                            dummy.add(modelClass.getDayNo());
                                            prepareObjectToSendForApproval(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, modelClass.getDate()), modelClass.getDayNo(), arrayList, true);
                                            break;
                                        }
                                    }
                                    if (dummy.size() == 0) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        changeApprovalBtnState(arrayList);
                                    }

                                }
                            }
                        } else {
                            saveTpLocal(modelClassArrayList, date, month, "1"); // Sync Failed
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.something_wrong));
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.something_wrong));
                        saveTpLocal(modelClassArrayList, date, month, "1"); // Sync Failed
                    }
                    binding.progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    if (statusOffline) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if (statusOffline) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
                saveTpLocal(modelClassArrayList, date, month, "1"); // Sync Failed
            }
        });

    }

    public void sendTpForApprovalOneBuild(JsonObject jsonObject, ArrayList<OneBuildModelClass> oneBuildModelClassArrayList, String date, String month, Boolean statusOffline, String isClickedName) {
        localDate = localDate;
        JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
        ArrayList<OneBuildModelClass> arrayList;
        ArrayList<String> dummy = new ArrayList<>();
        Type type = new TypeToken<ArrayList<OneBuildModelClass>>() {
        }.getType();
        if (jsonArray.length() >= 0) {
            binding.progressBar.setVisibility(View.VISIBLE);
            sendWholeMonthStatusOneBuild(localDate,isClickedName);
        }else{
            get1MonthRemoteTPDataOneBuild(localDate);
        }
       /* apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getBaseWebUrl(TourPlanActivity.this));
        Log.v("tpApproval", "--json--" + jsonObject.toString());
        System.out.println(jsonObject);

//        Call<JsonElement> call = apiInterface.getJSONElementOneBuild("/MasterFiles/tourPlan/TourPlanWebService.asmx/SaveTourPlan", jsonObject);
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/tp");
        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(TourPlanActivity.this), mapString, jsonObject.toString());
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.v("tpApproval", "--res--" + response.body());
                try {
                    if (response.isSuccessful() && response.body() != null) {
//                        JSONObject jsonObject1 = new JSONObject(response.body().toString());
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.send_approved_successfully));

                        binding.progressBar.setVisibility(View.GONE);
                        *//*if (statusOffline) {*//*
                        JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
                        ArrayList<OneBuildModelClass> arrayList;
                        ArrayList<String> dummy = new ArrayList<>();
                        Type type = new TypeToken<ArrayList<OneBuildModelClass>>() {
                        }.getType();
                        if (jsonArray.length() >= 0) {
                            binding.progressBar.setVisibility(View.VISIBLE);
                            sendWholeMonthStatusOneBuild(localDate,isClickedName);
                        }

                        *//*}else{
                            get3MonthRemoteTPData(isClickedName);
                        }*//*

                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.something_wrong));
                        saveTpLocalOneBuild(oneBuildModelClassArrayList, date, month, "1"); // Sync Failed
                    }

                } catch (Exception e) {
                    if (statusOffline) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                if (statusOffline) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                saveTpLocalOneBuild(oneBuildModelClassArrayList, date, month, "1"); // Sync Failed
                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
            }
        });*/

    }

    public void saveTpLocal(ArrayList<ModelClass> arrayList, String date, String month, String status) {
        for (ModelClass modelClass : arrayList) {
            if (modelClass.getDayNo().equals(date)) {
                modelClass.setSyncStatus(status);
                break;
            }
        }
        tourPlanOfflineDataDao.saveTpData(new TourPlanOfflineDataTable(month, new Gson().toJson(arrayList)));
    }

    public void saveTpLocalOneBuild(ArrayList<OneBuildModelClass> arrayList, String date, String month, String status) {
        for (OneBuildModelClass oneBuildModelClass : arrayList) {
            if (oneBuildModelClass.getDayNo().equals(date)) {
                oneBuildModelClass.setSyncStatus(status);
                break;
            }
        }
        tourPlanOfflineDataDao.saveTpData(new TourPlanOfflineDataTable(month, new Gson().toJson(arrayList)));
    }

    public void checkTpApiStaus() {
        if (!SharedPref.getTpSyncStaus(TourPlanActivity.this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.warning_label)).setMessage(getString(R.string.tour_plan_not_sync_properly_once_sync_again)).setCancelable(false).setIcon(getDrawable(R.drawable.icon_sync_failed)).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(getString(R.string.sync), (dialog, which) -> {
                syncTPSetup();
                get3MonthRemoteTPData("current");
            }).setNegativeButton(android.R.string.no, (dialog, which) -> getOnBackPressedDispatcher().onBackPressed());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void checkTpApiStatusOneBuild() {
        if (!SharedPref.getTpSyncStaus(TourPlanActivity.this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.warning_label)).setMessage(getString(R.string.tour_plan_not_sync_properly_once_sync_again)).setCancelable(false).setIcon(getDrawable(R.drawable.icon_sync_failed)).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(getString(R.string.sync), (dialog, which) -> {
                        syncTPSetup();
                        get3MonthRemoteTPDataOneBuild("current");
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> getOnBackPressedDispatcher().onBackPressed());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void SetTpRangeStatus() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        SimpleDateFormat date = new SimpleDateFormat("dd", Locale.ENGLISH);
        String mCurrDate = date.format(calendar.getTime());
        String currentDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        String nextMonthDate = sdf.format(calendar.getTime());

        String tp_start = SharedPref.getTpStartDate(TourPlanActivity.this);
        String tp_end = SharedPref.getTpEndDate(TourPlanActivity.this);
        int Start_Date = Integer.parseInt(tp_start);
        int End_Date = Integer.parseInt(tp_end);
        int mCurrentDate = Integer.parseInt(mCurrDate);

        if (!tourPlanOfflineDataDao.getApprovalStatusByMonth(currentDate).equalsIgnoreCase("3")) {
            SharedPref.setTpStatus(TourPlanActivity.this, true);
        } else if (!tourPlanOfflineDataDao.getApprovalStatusByMonth(nextMonthDate).equalsIgnoreCase("3") && (mCurrentDate >= Start_Date)) {
            SharedPref.setTpStatus(TourPlanActivity.this, End_Date < mCurrentDate);
        } else {
            SharedPref.setTpStatus(TourPlanActivity.this, false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, new IntentFilter("com.saneforce.SYNC_COMPLETED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if (type != null && type.matches("(?i)DR|CH|ST|UL|HOS|CIP|SE|TM|WT|OTR|FSD|AMS")) {
                if (binding.tpDrawer.isOpen()) {
                    binding.tpDrawer.closeDrawer(GravityCompat.END);
                }
                startActivity(new Intent(TourPlanActivity.this, TourPlanActivity.class));
                finish();
            }
        }
    };
}
