package saneforce.santrip.activity.tourPlan;


import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.tourPlan.session.SessionEditAdapter.inputDataArray;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.BuildConfig;
import com.google.gson.Gson;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.login.LoginActivity;
import saneforce.santrip.activity.tourPlan.calendar.CalendarAdapter;
import saneforce.santrip.activity.tourPlan.model.ModelClass;
import saneforce.santrip.activity.tourPlan.model.ReceiveModel;
import saneforce.santrip.activity.tourPlan.session.SessionEditAdapter;
import saneforce.santrip.activity.tourPlan.session.SessionInterface;
import saneforce.santrip.activity.tourPlan.session.SessionViewAdapter;
import saneforce.santrip.activity.tourPlan.summary.SummaryAdapter;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityTourPlanBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.santrip.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataDao;
import saneforce.santrip.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataTable;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkStatusTask;
import saneforce.santrip.utility.TimeUtils;

public class TourPlanActivity extends AppCompatActivity {
    public static LinearLayout addSaveBtnLayout, clrSaveBtnLayout;
    ApiInterface apiInterface;
    private RoomDB roomDB;
    private TourPlanOfflineDataDao tourPlanOfflineDataDao;
    private TourPlanOnlineDataDao tourPlanOnlineDataDao;
    private MasterDataDao masterDataDao;
    CalendarAdapter calendarAdapter = new CalendarAdapter();
    SummaryAdapter summaryAdapter = new SummaryAdapter();
    SessionEditAdapter sessionEditAdapter = new SessionEditAdapter();
    SessionViewAdapter sessionViewAdapter = new SessionViewAdapter();
    ArrayList<ModelClass> dayWiseArrayCurrentMonth = new ArrayList<>();
    ArrayList<ModelClass> dayWiseArrayPrevMonth = new ArrayList<>();
    ArrayList<ModelClass> dayWiseArrayNextMonth = new ArrayList<>();
    ArrayList<String> weeklyOffDays = new ArrayList<>();
    JSONArray holidayJSONArray = new JSONArray();
    ModelClass.SessionList.WorkType weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType();
    ModelClass.SessionList.WorkType holidayWorkTypeModel = new ModelClass.SessionList.WorkType();
    LocalDate localDate;
    String drNeed = "", maxDrCount = "", addSessionNeed = "", addSessionCountLimit = "", FW_meetup_mandatory = "", holidayMode = "", weeklyOffCaption = "", holidayEditable = "", weeklyOffEditable = "";
    int monthInAdapterFlag = 0; // 0 -> current month , 1 -> next month , -1 -> previous month
    boolean isDataAvailable;
    CommonUtilsMethods commonUtilsMethods;
    public static ActivityTourPlanBinding binding;

    public static   String isFrom="",SFTP_Date_sp="",SFTP_Date="";
    public static  int JoningDate,JoiningMonth, JoinYear;

    public static ModelClass.SessionList prepareSessionListForAdapter(ArrayList<ModelClass.SessionList.SubClass> clusterArray, ArrayList<ModelClass.SessionList.SubClass> jcArray, ArrayList<ModelClass.SessionList.SubClass> drArray, ArrayList<ModelClass.SessionList.SubClass> chemistArray, ArrayList<ModelClass.SessionList.SubClass> stockArray, ArrayList<ModelClass.SessionList.SubClass> unListedDrArray, ArrayList<ModelClass.SessionList.SubClass> cipArray, ArrayList<ModelClass.SessionList.SubClass> hospArray, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass hq, String remarks) {
        return new ModelClass.SessionList("", true, remarks, workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public static ModelClass.SessionList prepareSessionListForAdapter() {

        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType("", "", "", "");
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("", "");
        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        return new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public static ModelClass.SessionList prepareSessionListForAdapter1(ArrayList<ModelClass.SessionList.SubClass> clusterArray, ArrayList<ModelClass.SessionList.SubClass> jcArray, ArrayList<ModelClass.SessionList.SubClass> drArray, ArrayList<ModelClass.SessionList.SubClass> chemistArray, ArrayList<ModelClass.SessionList.SubClass> stockArray, ArrayList<ModelClass.SessionList.SubClass> unListedDrArray, ArrayList<ModelClass.SessionList.SubClass> cipArray, ArrayList<ModelClass.SessionList.SubClass> hospArray, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass hq) {
        return new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(getApplicationContext());
        tourPlanOfflineDataDao = roomDB.tourPlanOfflineDataDao();
        tourPlanOnlineDataDao = roomDB.tourPlanOnlineDataDao();
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        addSaveBtnLayout = binding.tpNavigation.addSaveLayout;
        clrSaveBtnLayout = binding.tpNavigation.clrSaveBtnLayout;


        try {
            SFTP_Date_sp = SharedPref.getSftpDate(TourPlanActivity.this);
            JSONObject obj = new JSONObject(SFTP_Date_sp);
            SFTP_Date = obj.getString("date");
        } catch (Exception e) {
            e.printStackTrace();
        }


        JoningDate=Integer.valueOf(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1,TimeUtils.FORMAT_7,SFTP_Date));
        JoiningMonth=Integer.valueOf(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1,TimeUtils.FORMAT_8,SFTP_Date));
        JoinYear=Integer.valueOf(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1,TimeUtils.FORMAT_10,SFTP_Date));



        uiInitialization();
        isFrom=getIntent().getStringExtra("isFrom");


        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
        populateCalendarAdapter(dayWiseArrayCurrentMonth);


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

        binding.tvSync.setOnClickListener(v -> {


            NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void isNetworkAvailable(Boolean status) {
                    if(status){
                        binding.tvSync.setEnabled(false);
                        binding.progressBar.setVisibility(View.VISIBLE);

                        LocalDate localDate1 = LocalDate.now();
                        if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1.minusMonths(1)))) {
                            get3MonthRemoteTPData("prev");
                        } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1))) {
                            get3MonthRemoteTPData("current");
                        } else if (binding.monthYear.getText().toString().equalsIgnoreCase(monthYearFromDate(localDate1.plusMonths(1)))) {
                            get3MonthRemoteTPData("next");
                        }
                    }
                    else{
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.no_network));
                    }

                }
            });
            networkStatusTask.execute();
        });


        binding.backArrow.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
            HomeDashBoard.tpRangeCheck=true;
            finish();
        });
        binding.calendarNextButton.setOnClickListener(view -> {
            binding.calendarPrevButton.setEnabled(true);
            binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_black, null));
            localDate = localDate.plusMonths(1);
            if (LocalDate.now().plusMonths(1).isEqual(localDate)) {
                binding.calendarNextButton.setEnabled(false);
                binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
            } else binding.calendarNextButton.setEnabled(true);

            if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                monthInAdapterFlag = 0;
                if (dayWiseArrayCurrentMonth.size() == 0) {
                    dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                }
                populateCalendarAdapter(dayWiseArrayCurrentMonth);
            } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().plusMonths(1).getMonth().toString())) {
                monthInAdapterFlag = 1;
                if (dayWiseArrayNextMonth.size() == 0) {
                    dayWiseArrayNextMonth = prepareModelClassForMonth(localDate);
                }
                populateCalendarAdapter(dayWiseArrayNextMonth);
            }
        });

        binding.calendarPrevButton.setOnClickListener(view -> {
            binding.calendarNextButton.setEnabled(true);
            binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_black, null));
            localDate = localDate.minusMonths(1);
            if (LocalDate.now().minusMonths(1).isEqual(localDate)) {
                binding.calendarPrevButton.setEnabled(false);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
            }else if(localDate.getMonthValue()==JoiningMonth&& localDate.getYear()==JoinYear){
                binding.calendarPrevButton.setEnabled(false);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
            }
            else binding.calendarPrevButton.setEnabled(true);

            if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                monthInAdapterFlag = 0;
                if (dayWiseArrayCurrentMonth.size() == 0) {
                    dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                }
                populateCalendarAdapter(dayWiseArrayCurrentMonth);
            } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().minusMonths(1).getMonth().toString())) {
                monthInAdapterFlag = -1;
                if (dayWiseArrayPrevMonth.size() == 0) {
                    dayWiseArrayPrevMonth = prepareModelClassForMonth(localDate);
                }
                populateCalendarAdapter(dayWiseArrayPrevMonth);
            }
        });


        if (isFrom.equalsIgnoreCase("isHome")) {
            if (SharedPref.getskipstatus(TourPlanActivity.this)){
                binding.txtSkip.setVisibility(View.VISIBLE);
                binding.txtLogout.setVisibility(View.GONE);
            }
            else{
                binding.txtLogout.setVisibility(View.VISIBLE);
                binding.txtSkip.setVisibility(View.GONE);}
        } else {
            binding.txtLogout.setVisibility(View.GONE);
            binding.txtSkip.setVisibility(View.GONE);
        }

        binding.txtSkip.setOnClickListener(view -> {
            HomeDashBoard.tpRangeCheck=true;
            SharedPref.setSKIPDate(TourPlanActivity.this,TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.txtLogout.setOnClickListener(view -> {
            SharedPref.saveLoginState(TourPlanActivity.this, false);
            startActivity(new Intent(TourPlanActivity.this, LoginActivity.class));
            finish();
        });

        binding.tpNavigation.tpDrawerCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tpDrawer.closeDrawer(GravityCompat.END);
            }
        });

        binding.tpNavigation.itemClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionEditAdapter.MyViewHolder viewHolder = (SessionEditAdapter.MyViewHolder) binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(sessionEditAdapter.itemPosition);
                sessionEditAdapter.clearCheckBox(viewHolder);
            }
        });

        binding.tpNavigation.checkBoxSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionEditAdapter.MyViewHolder viewHolder = (SessionEditAdapter.MyViewHolder) binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(sessionEditAdapter.itemPosition);
                sessionEditAdapter.saveCheckedItem(viewHolder);
            }
        });

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
                            if (modelClass1.getHQ().getName().isEmpty()) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
                                break;
                            } else if (modelClass1.getCluster().size() == 0) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
                                break;
                            } else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                                if (FW_meetup_mandatory.equals("0")) {
                                    if (drNeed.equals("0")) {
                                        if (modelClass1.getListedDr().size() == 0) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.in_session) + (i + 1));
                                            break;
                                        } else if (modelClass1.getListedDr().size() > Integer.parseInt(maxDrCount)) {
                                            isEmpty = true;
                                            position = i;
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.more_than_limit) + (i + 1));
                                            break;
                                        }
                                    }

                                    if (modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 && modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any_masters) + (i + 1));
                                        break;
                                    }
                                }
                            }
                        } else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            if (FW_meetup_mandatory.equals("0")) {
                                if (drNeed.equals("0")) {
                                    if (modelClass1.getListedDr().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.in_session) + (i + 1));
                                        break;
                                    } else if (modelClass1.getListedDr().size() > Integer.parseInt(maxDrCount)) {
                                        isEmpty = true;
                                        position = i;
                                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.more_than_limit) + (i + 1));
                                        break;
                                    }
                                }
                                if (modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 && modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.more_than_limit) + (i + 1));
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

        binding.tpNavigation.sessionSave.setOnClickListener(view -> {
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
                    if (modelClass.getHQ().getName().isEmpty()) {
                        isEmpty = true;
                        position = i;
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_hq_in_session) + (i + 1));
                        break;
                    } else if (modelClass.getCluster().size() == 0) {
                        isEmpty = true;
                        position = i;
                        commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_clusters_in_session) + (i + 1));
                        break;
                    } else if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                        if (FW_meetup_mandatory.equals("0")) {
                            if (drNeed.equals("0")) {
                                if (modelClass.getListedDr().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.in_session) + (i + 1));
                                    break;
                                } else if (modelClass.getListedDr().size() > Integer.parseInt(maxDrCount)) {
                                    isEmpty = true;
                                    position = i;
                                    commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.more_than_limit) + (i + 1));
                                    break;
                                }
                            }

                            if (modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 && modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any_masters) + (i + 1));
                                break;
                            }
                        }
                    }
                } else if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) { // if the selected work type is "F" means Field Work then we need to check the FW_meetup_mandatory
                    if (FW_meetup_mandatory.equals("0")) { // "0"-- yes
                        if (drNeed.equals("0")) { // Dr meet up mandatory
                            if (modelClass.getListedDr().size() == 0) {
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.in_session) + (i + 1));
                                break;
                            } else if (modelClass.getListedDr().size() > Integer.parseInt(maxDrCount)) { //Selected Dr count should not be more than maxDrCount setup limit
                                isEmpty = true;
                                position = i;
                                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.you_have_select) + SharedPref.getDrCap(TourPlanActivity.this) + getString(R.string.more_than_limit) + (i + 1));
                                break;
                            }
                        }
                        if (modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 && modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) { // when Dr meetup not mandatory but FW meetup mandatory.So check any of the meetup selected
                            isEmpty = true;
                            position = i;
                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.select_any_masters) + (i + 1));
                            break;
                        }
                    }
                }
            }

            if (!isEmpty) {
                binding.tpDrawer.closeDrawer(GravityCompat.END);
                dataModel.setSubmittedTime(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
                if (monthInAdapterFlag == 0) {
                    for (int i = 0; i < dayWiseArrayCurrentMonth.size(); i++) {
                        if (dayWiseArrayCurrentMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                            dayWiseArrayCurrentMonth.remove(i);
                            dayWiseArrayCurrentMonth.add(i, dataModel); // removed and replaced the object with updated session data
                            break;
                        }
                    }
                    populateSummaryAdapter(dayWiseArrayCurrentMonth);
                    prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayCurrentMonth, false);
                } else if (monthInAdapterFlag == 1) {
                    for (int i = 0; i < dayWiseArrayNextMonth.size(); i++) {
                        if (dayWiseArrayNextMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                            dayWiseArrayNextMonth.remove(i);
                            dayWiseArrayNextMonth.add(i, dataModel); // removed and replaced the object with updated session data
                            break;
                        }
                    }
                    populateSummaryAdapter(dayWiseArrayNextMonth);
                    //saveTpLocal(dayWiseArrayNextMonth, dayNo, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), "1");
                    prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayNextMonth, false);
                } else if (monthInAdapterFlag == -1) {
                    for (int i = 0; i < dayWiseArrayPrevMonth.size(); i++) {
                        if (dayWiseArrayPrevMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                            dayWiseArrayPrevMonth.remove(i);
                            dayWiseArrayPrevMonth.add(i, dataModel); // removed and replaced the object with updated session data
                            break;
                        }
                    }
                    populateSummaryAdapter(dayWiseArrayPrevMonth);
                    //  saveTpLocal(dayWiseArrayPrevMonth, dayNo, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), "1");
                    prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayPrevMonth, false);
                }
                calendarAdapter.notifyDataSetChanged();
            } else {
                scrollToPosition(position, true);
            }

        });

        binding.tpNavigation.sessionEdit.setOnClickListener(view -> {
            binding.tpNavigation.addEditViewTxt.setText("Edit Plan");
            populateSessionEditAdapter(sessionViewAdapter.inputDataModel);
        });

        binding.tpSendToApproval.setOnClickListener(view -> {

            JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
            ArrayList<ModelClass> arrayList;
            ArrayList<String> dummy = new ArrayList<>();
            Type type = new TypeToken<ArrayList<ModelClass>>() {
            }.getType();
            if (jsonArray.length() > 0) {
                arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
                for (ModelClass modelClass : arrayList) {
                    if (!modelClass.getDate().equals("") && !modelClass.getSyncStatus().equals("0")) {
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
        });

    }



    private ModelClass.SessionList prepareSessionListForAdapterEmpty() {
        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType("", "", "", "");
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("", "");

        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        return new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }



    public void uiInitialization() {
        localDate = LocalDate.now();
        try {

            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                drNeed = jsonObject.getString("DrNeed");
                maxDrCount = jsonObject.getString("max_doc");
                addSessionNeed = jsonObject.getString("AddsessionNeed");
                addSessionCountLimit = jsonObject.getString("AddsessionCount");
                FW_meetup_mandatory = jsonObject.getString("FW_meetup_mandatory");
                holidayEditable = jsonObject.getString("Holiday_Editable");
                weeklyOffEditable = jsonObject.getString("Weeklyoff_Editable");
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
                if (jsonObject.getString("Name").equalsIgnoreCase("Weekly Off"))
                    weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
                else if (jsonObject.getString("Name").equalsIgnoreCase("Holiday"))
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
        if(dayOfWeek == 7) dayOfWeek = 1;
        else dayOfWeek++;

        int trailingNumOfDaysEmpty = 7 - ((daysInMonth + dayOfWeek -1) % 7);
        for (int i = 1; i< dayOfWeek + daysInMonth + trailingNumOfDaysEmpty; i++) {
            if(i>=dayOfWeek && i<(daysInMonth + dayOfWeek))
                daysInMonthArray.add(String.valueOf((i + 1) - dayOfWeek));
            else daysInMonthArray.add("");
        }

        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        return date.format(formatter);
    }

    private String dayMonthYearFromDate(LocalDate date, String format) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern(format);

        return date.format(formatter);
    }

    public ArrayList<ModelClass> prepareModelClassForMonth(LocalDate localDate1) {
        ArrayList<ModelClass> modelClasses = new ArrayList<>();
        try {
            //Data from Tour Plan table
            Log.v("getTp", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate1)));

            JSONArray savedDataArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate1))).getTpDataJSONArray();

            if (savedDataArray.length() > 0) { //Use the saved data if Tour Plan table has data of a selected month
                Type type = new TypeToken<ArrayList<ModelClass>>() {
                }.getType();
                modelClasses = new Gson().fromJson(savedDataArray.toString(), type);
            } else { //If tour plan table has no data
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate1));
                String monthYear = monthYearFromDate(localDate1);
                String month = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_8, monthYear);
                String year = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);

                ArrayList<String> holidayDateArray = new ArrayList<>();
                for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                    if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate1.getMonthValue())))
                        holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
                }

        boolean LocalWeelyHolidayFlag;

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
                            LocalWeelyHolidayFlag =false;
                        } else {
                            if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                sessionList.setWorkType(weeklyOffWorkTypeModel);
                                LocalWeelyHolidayFlag =true;
                            }
                            else if (holidayDateArray.contains(day)) {
                                sessionList.setWorkType(holidayWorkTypeModel);  // add holiday work type model object when current date is declared as holiday
                                LocalWeelyHolidayFlag =true;
                            }else {
                                LocalWeelyHolidayFlag =false;
                            }
                            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            ModelClass modelClass = new ModelClass(day, date, dayName, month, year, false, sessionLists);
                            modelClasses.add(modelClass);
                        }

                        if(LocalWeelyHolidayFlag){
                            saveTpLocal(modelClasses, day, monthYear, "1");
                        }else {
                            saveTpLocal(modelClasses, day, monthYear, "0");
                        }
                    }
                    else {
                        //  Log.v("getTp","--333--" + day);
                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        ModelClass modelClass = new ModelClass(day, "", "", "", "", true, sessionLists);
                        modelClasses.add(modelClass);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e("--Errr--",""+e);
            e.printStackTrace();
        }
        return modelClasses;
    }

    public void populateCalendarAdapter(ArrayList<ModelClass> arrayList) {
        binding.monthYear.setText(monthYearFromDate(localDate));
        calendarAdapter = new CalendarAdapter(arrayList, TourPlanActivity.this, (position, date, modelClass) -> {

            if (!date.equals("")) {
                binding.tpDrawer.openDrawer(GravityCompat.END);
                binding.tpNavigation.planDate.setText(modelClass.getDate());
                ModelClass modelClass1 = new ModelClass(modelClass);
                if (!modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("")) {
                    binding.tpNavigation.addEditViewTxt.setText("View Plan");
                    populateSessionViewAdapter(modelClass1);
                } else {
                    binding.tpNavigation.addEditViewTxt.setText("Add Plan");
                    populateSessionEditAdapter(modelClass1);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        binding.calendarRecView.setLayoutManager(layoutManager);
        binding.calendarRecView.setAdapter(calendarAdapter);

        populateSummaryAdapter(arrayList);
    }

    public void populateSessionEditAdapter(ModelClass arrayList) {
        binding.tpDrawer.openDrawer(GravityCompat.END);
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
                ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

                ModelClass.SessionList modelClass = new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                arrayList.getSessionList().remove(position);
                arrayList.getSessionList().add(position, modelClass);

                for (int i = 0; i < arrayList.getSessionList().size(); i++) {
                    arrayList.getSessionList().get(i).setVisible(true);
                }

                populateSessionEditAdapter(arrayList);
                scrollToPosition(position, false);
            }

            @Override
            public void hqChanged(ModelClass arrayList, int position, boolean changed) {
                if (changed) {
                    ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(arrayList.getSessionList().get(position).getWorkType());
                    ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass(arrayList.getSessionList().get(position).getHQ());
                    ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
                    ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
                    ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
                    ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
                    ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
                    ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
                    ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
                    ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

                    ModelClass.SessionList modelClass = new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                    arrayList.getSessionList().remove(position);
                    arrayList.getSessionList().add(modelClass);
                }
                for (int i = 0; i < arrayList.getSessionList().size(); i++) {
                    arrayList.getSessionList().get(i).setVisible(true);
                    arrayList.getSessionList().get(i).setLayoutVisible("");
                }
                populateSessionEditAdapter(arrayList);
                scrollToPosition(position, false);
            }

            @Override
            public void clusterChanged(ModelClass arrayList, int position) {
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

    public void populateSessionViewAdapter(ModelClass modelClass) {
        binding.tpDrawer.openDrawer(GravityCompat.END);
        sessionViewAdapter = new SessionViewAdapter(modelClass);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionViewAdapter);

        addSaveBtnLayout.setVisibility(View.GONE);
        clrSaveBtnLayout.setVisibility(View.GONE);
        if (modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("Weekly Off")) {
            if (weeklyOffEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        } else if (modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("Holiday")) {
            if (holidayEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        }else {
            binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
        }
    }

    public void populateSummaryAdapter(ArrayList<ModelClass> arrayList) {
        try {
            ArrayList<ModelClass> modelClasses = new ArrayList<>();
            for (ModelClass modelClass : arrayList) {
                if (!modelClass.getDayNo().isEmpty() && !modelClass.getSessionList().get(0).getWorkType().getName().isEmpty())
                    modelClasses.add(modelClass);
            }

            summaryAdapter = new SummaryAdapter(modelClasses, TourPlanActivity.this, (modelClass, position) -> {

                populateSessionViewAdapter(modelClass);
                binding.tpNavigation.addEditViewTxt.setText("View Plan");
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

    public void changeApprovalBtnState(ArrayList<ModelClass> arrayList) { // To set send to approval btn enable/disable  based on syncStatus and  workType
        boolean wholeMonthTpCompleted = false;
        for (int i = 0; i < arrayList.size(); i++) { // to enable/disable the send to approval button
            if (!arrayList.get(i).getDayNo().isEmpty()) {
                ModelClass.SessionList.WorkType workType = arrayList.get(i).getSessionList().get(0).getWorkType();
                if (!workType.getName().isEmpty()) {
                    wholeMonthTpCompleted = true;
                } else {
                    if(Integer.valueOf(arrayList.get(i).getDayNo())< TourPlanActivity.JoningDate &&Integer.valueOf(arrayList.get(i).getMonth())==TourPlanActivity.JoiningMonth  && Integer.valueOf(arrayList.get(i).getYear())==TourPlanActivity.JoinYear ) {
                        wholeMonthTpCompleted = true;
                    }else {
                        wholeMonthTpCompleted = false;
                        break;
                    }
                }
            }
        }
        String status = "";
        String reason = "";
        TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
        if(tourPlanOfflineDataTable != null) {
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
                binding.tpStatusTxt.setText(Constants.STATUS_0);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                break;
            }
            case "-1": {
                binding.tpStatusTxt.setText(Constants.STATUS_4);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                break;
            }
            case "1": {
                binding.tpStatusTxt.setText(Constants.STATUS_1);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                break;
            }
            case "2": {
                binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                binding.tpStatusTxt.setText(Constants.STATUS_2);
                binding.tpStatusTxt.setTextColor(getColor(R.color.pink));
                break;
            }
            case "3": {
                binding.tpStatusTxt.setText(Constants.STATUS_3);
                binding.tpStatusTxt.setTextColor(getColor(R.color.green_2));
                break;
            }
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


    public void get3MonthRemoteTPData(String isClickedName) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
            if (status) {
                try {
                    apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tableName", "getall_tp");
                    jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
                    jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
                    jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                    jsonObject.put("sf_type", SharedPref.getSfType(TourPlanActivity.this));
                    jsonObject.put("Designation", SharedPref.getDesig(TourPlanActivity.this));
                    jsonObject.put("state_code", SharedPref.getStateCode(TourPlanActivity.this));
                    jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(TourPlanActivity.this));
                    jsonObject.put("tp_month", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_25, TimeUtils.FORMAT_8, LocalDate.now().getMonth().toString()));
                    jsonObject.put("tp_year", LocalDate.now().getYear());
                    Log.v("tpGetPlan", "--json--" + jsonObject);

                    Map<String, String> mapString = new HashMap<>();
                    mapString.put("axn", "get/tp");
                    Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            try {
                                Log.v("tpGetPlan", "----" + response.body());
                                if (response.body() != null && !response.body().isJsonNull()) {
                                    JSONObject jsonObject1;
                                    if (response.body().isJsonObject()) {
                                        jsonObject1 = new JSONObject(response.body().getAsJsonObject().toString());
                                        SaveTourPlanWholeMonth(jsonObject1, isClickedName);
                                    }
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.tvSync.setEnabled(true);
                                    HomeDashBoard.tpRangeCheck=true;
                                    switch (isClickedName) {
                                        case "prev":
                                            Log.v("tpGetPlan", "--prev--" + dayWiseArrayPrevMonth.size());
                                            localDate = localDate.minusMonths(1);

                                            populateCalendarAdapter(dayWiseArrayPrevMonth);
                                            break;
                                        case "current":
                                            Log.v("tpGetPlan", "--current--" + dayWiseArrayCurrentMonth.size());
                                            localDate = LocalDate.now();
                                            populateCalendarAdapter(dayWiseArrayCurrentMonth);
                                            break;
                                        case "next":
                                            Log.v("tpGetPlan", "--next--" + dayWiseArrayNextMonth.size());
                                            localDate = localDate.plusMonths(1);
                                            populateCalendarAdapter(dayWiseArrayNextMonth);
                                            break;
                                    }


                                    Intent intent = getIntent();
                                    overridePendingTransition(0, 0);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);

                                }
                            } catch (JSONException e) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.tvSync.setEnabled(true);
                                Log.v("tpGetPlan", "--error--2--" + e);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
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

    private void SaveLocalOnlineTable(LocalDate localDate, JSONArray listArray, ArrayList<ModelClass> dayWiseSaveTp) {
        try {
            dayWiseSaveTp = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate));

            String monthYear = monthYearFromDate(localDate);
            String monthNo = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_8, monthYear);
            String year = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);
            String monthName = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate));
            ArrayList<ModelClass> modelClasses = new ArrayList<>();

            ArrayList<String> holidayDateArray = new ArrayList<>();
            for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate.getMonthValue())))
                    holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
            }


            JSONArray savedDataArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
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
                                LocalWeelyHolidayFlag =false;
                            }
                            else {
                                if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                    sessionList.setWorkType(weeklyOffWorkTypeModel);
                                    LocalWeelyHolidayFlag =true;
                                }
                                else if (holidayDateArray.contains(day)) {
                                    sessionList.setWorkType(holidayWorkTypeModel);  // add holiday work type model object when current date is declared as holiday
                                    LocalWeelyHolidayFlag =true;
                                }else {
                                    LocalWeelyHolidayFlag =false;
                                }

                                ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                                sessionLists.add(sessionList);
                                ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                                modelClasses.add(modelClass);

                            }
                                dayWiseSaveTp = modelClasses;

                            if(LocalWeelyHolidayFlag){
                                saveTpLocal(dayWiseSaveTp, day, monthYear, "1");
                            }else {
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

//                sqLite.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, rejectionReason);
                tourPlanOfflineDataDao.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, rejectionReason);

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
                            LocalWeelyHolidayFlag =false;
                        }else {
                            if (weeklyOffDays.contains(dayName)) {// add weekly off object when the day is declared as Weekly Off
                                sessionList.setWorkType(weeklyOffWorkTypeModel);
                                LocalWeelyHolidayFlag =true;
                            }
                            else if (holidayDateArray.contains(day)) {
                                sessionList.setWorkType(holidayWorkTypeModel);  // add holiday work type model object when current date is declared as holiday
                                LocalWeelyHolidayFlag =true;
                            }else {
                                LocalWeelyHolidayFlag =false;
                            }
                            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                            modelClasses.add(modelClass);
                        }

                        dayWiseSaveTp = modelClasses;
                        if(LocalWeelyHolidayFlag){
                            saveTpLocal(dayWiseSaveTp, day, monthYear, "1");
                        }else {
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
        } catch (Exception ignored) {

        }

    }

    private void SaveTpData(ArrayList<ModelClass> modelClasses, String isClickedName, String day, String monthName, String status) {
        switch (isClickedName) {
            case "prev":
                Log.v("ggggg", "prev");
                dayWiseArrayPrevMonth = modelClasses;
                saveTpLocal(dayWiseArrayPrevMonth, day, monthName, status);
                break;
            case "current":
                Log.v("ggggg", "current");
                dayWiseArrayCurrentMonth = modelClasses;
                saveTpLocal(dayWiseArrayCurrentMonth, day, monthName, status);
                break;
            case "next":
                Log.v("ggggg", "next");
                dayWiseArrayNextMonth = modelClasses;
                saveTpLocal(dayWiseArrayNextMonth, day, monthName, status);
                break;
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

        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(receiveModel.getFWFlg(), receiveModel.getWTName(), terrSlFlag, receiveModel.getWTCode());
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass(receiveModel.getHQNames(), receiveModel.getHQCodes());

        if (receiveModel.getFWFlg().equalsIgnoreCase("F")) {
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
        }
        sessionList = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks);

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

            if (receiveModel.getFWFlg2().equalsIgnoreCase("F")) {
                if (!receiveModel.getClusterName().isEmpty())
                    clusterArray = addExtraData(receiveModel.getClusterName2(), receiveModel.getClusterCode2());
                if (!receiveModel.getJWNames2().isEmpty())
                    jcArray = addExtraData(receiveModel.getJWNames2(), receiveModel.getJWCodes2());
                if (!receiveModel.getDr_two_name().isEmpty())
                    drArray = addExtraData(receiveModel.getDr_two_name(), receiveModel.getDr_two_code());
                if (!receiveModel.getChem_Name().isEmpty())
                    chemArray = addExtraData(receiveModel.getChem_two_name(), receiveModel.getChem_two_code());
                if (!receiveModel.getStockist_two_name().isEmpty())
                    stkArray = addExtraData(receiveModel.getStockist_two_name(), receiveModel.getStockist_two_code());
            }
            sessionList2 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks2);

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

            if (receiveModel.getFWFlg3().equalsIgnoreCase("F")) {
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
            }
            sessionList3 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks3);
        }

        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
        sessionLists.add(sessionList);
        if (session2) sessionLists.add(sessionList2);
        if (session3) sessionLists.add(sessionList3);
        ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
        modelClass.setSubmittedTime(submittedTime);
        modelClasses.add(modelClass);
        dayWiseSaveTp = modelClasses;
        saveTpLocal(dayWiseSaveTp, day, monthName, "0");
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


    public void get1MonthRemoteTPData(LocalDate localDate1) {
        try {
            apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "gettpdetail");
            jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
            jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
            jsonObject.put("sf_type", SharedPref.getSfType(TourPlanActivity.this));
            jsonObject.put("Designation", SharedPref.getDesig(TourPlanActivity.this));
            jsonObject.put("state_code", SharedPref.getStateCode(TourPlanActivity.this));
            jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(TourPlanActivity.this));
            jsonObject.put("Month", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_25, TimeUtils.FORMAT_8, localDate1.getMonth().toString()));
            jsonObject.put("Year", localDate1.getYear());


            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "get/tp");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isJsonArray()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.body().getAsJsonArray().toString());
                                if (jsonArray.length() > 0) {
                                    String status = jsonArray.getJSONObject(0).getString("Change_Status");
                                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), status);

                                    switch (status) {
                                        case "0": {
                                            binding.tpStatusTxt.setText(Constants.STATUS_0);
                                            break;
                                        }
                                        case "1": {
                                            binding.tpStatusTxt.setText(Constants.STATUS_1);
                                            break;
                                        }
                                        case "2": {
                                            binding.tpStatusTxt.setText(Constants.STATUS_2);
                                            binding.rejectionReasonLayout.setVisibility(View.VISIBLE);
                                            break;
                                        }
                                        case "3": {
                                            binding.tpStatusTxt.setText(Constants.STATUS_3);
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

    public void sendWholeMonthStatus(LocalDate localDate1) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tableName", "tpsend_appr");
                        jsonObject.put("sfcode", SharedPref.getSfCode(TourPlanActivity.this));
                        jsonObject.put("SFName", SharedPref.getSfName(TourPlanActivity.this));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(TourPlanActivity.this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                        jsonObject.put("Designation", SharedPref.getDesig(TourPlanActivity.this));
                        jsonObject.put("state_code", SharedPref.getStateCode(TourPlanActivity.this));
                        jsonObject.put("TPMonth", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_25, TimeUtils.FORMAT_8, localDate1.getMonth().toString()));
                        jsonObject.put("TPYear", localDate1.getYear());

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "save/tp");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.v("tpApproval", "--ressapproval--" + response.body());
                                binding.progressBar.setVisibility(View.GONE);
                                if (response.body() != null) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body().getAsJsonObject().toString());
                                        if (jsonObject1.has("success") && jsonObject1.getBoolean("success")) {
//                                            sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "0"); // "0" - success
                                            tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "0");
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.send_approved_successfully));
                                            get1MonthRemoteTPData(localDate1);
                                        } else {
//                                            sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                                            tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                                            commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.failed_to_send_approval));
                                        }
                                    } catch (JSONException e) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        e.printStackTrace();
                                    }
                                } else {
                                    binding.progressBar.setVisibility(View.GONE);
//                                    sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                binding.progressBar.setVisibility(View.GONE);
//                                sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                                tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                            }
                        });
                    } catch (JSONException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    binding.progressBar.setVisibility(View.GONE);
//                    sqLite.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1"); // "-1" - failed
                    tourPlanOfflineDataDao.saveMonthlySyncStatus(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate1.toString()), "-1");
                }
            }
        });
        networkStatusTask.execute();
    }

    public String findTerrSlFlag(String code) {
        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray(); //List of Work Types
//            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE); //List of Work Types
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

    public void prepareObjectToSendForApproval(String month, String dateForApproval, ArrayList<ModelClass> arrayList, boolean statusOffline) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(TourPlanActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if (status) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (ModelClass modelClass : arrayList) {
                            if (!modelClass.getDayNo().isEmpty()&&!modelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) {
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
                                    jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_19, TimeUtils.FORMAT_4, modelClass.getDate()) + " 00:00:00");
                                    jsonObject.put("submitted_time", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
                                    jsonObject.put("Entry_mode", "Android -Edetailing");
                                    jsonObject.put("Approve_mode", "");
                                    jsonObject.put("Approved_time", "");
                                    jsonObject.put("app_version", BuildConfig.VERSION_NAME);
                                    jsonObject.put("Mode", "Android-Edet");

                                    String WTCode = "", WTName = "", FWFlg = "", HQCodes = "", HQNames = "", clusterCodes = "", clusterNames = "", JWCodes = "", JWNames = "", Dr_Code = "", Dr_Name = "", Chem_Code = "", Chem_Name = "", Stockist_Code = "", Stockist_Name = "", cip_code = "", cip_name = "", hosp_code = "", hosp_Name = "", DayRemarks = "";
                                    String WTCode2 = "", WTName2 = "", FWFlg2 = "", HQCodes2 = "", HQNames2 = "", clusterCode2 = "", clusterName2 = "", JWCodes2 = "", JWNames2 = "", Dr_two_code = "", Dr_two_name = "", Chem_two_code = "", Chem_two_name = "", Stockist_two_code = "", Stockist_two_name = "", cip_code2 = "", cip_name2 = "", hosp_code2 = "", hosp_Name2 = "", DayRemarks2 = "";
                                    String WTCode3 = "", WTName3 = "", FWFlg3 = "", HQCodes3 = "", HQNames3 = "", clusterCode3 = "", clusterName3 = "", JWCodes3 = "", JWNames3 = "", Dr_three_code = "", Dr_three_name = "", Chem_three_code = "", Chem_three_name = "", Stockist_three_code = "", Stockist_three_name = "", cip_code3 = "", cip_name3 = "", hosp_code3 = "", hosp_Name3 = "", DayRemarks3 = "";

                                    for (int i = 0; i < modelClass.getSessionList().size(); i++) {
                                        ModelClass.SessionList sessionList = modelClass.getSessionList().get(i);
                                        if (i == 0) {
                                            WTCode = sessionList.getWorkType().getCode();
                                            WTName = sessionList.getWorkType().getName();
                                            FWFlg = sessionList.getWorkType().getFWFlg();
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
                                            DayRemarks = sessionList.getRemarks();
                                        } else if (i == 1) {
                                            WTCode2 = sessionList.getWorkType().getCode();
                                            WTName2 = sessionList.getWorkType().getName();
                                            FWFlg2 = sessionList.getWorkType().getFWFlg();
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
                                            DayRemarks2 = sessionList.getRemarks();
                                        } else if (i == 2) {
                                            WTCode3 = sessionList.getWorkType().getCode();
                                            WTName3 = sessionList.getWorkType().getName();
                                            FWFlg3 = sessionList.getWorkType().getFWFlg();
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
                                            DayRemarks3 = sessionList.getRemarks();
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
                                    jsonObject.put("Stockist_two_code", Stockist_two_code);
                                    jsonObject.put("Stockist_Name2", Stockist_two_name);
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

    public void sendTpForApproval(JSONArray jsonArray, ArrayList<ModelClass> modelClassArrayList, String date, String month, Boolean statusOffline) {
        apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
        Log.v("tpApproval", "--json--" + jsonArray.toString());
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "savenew/tp");
        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonArray.toString());
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
                                JSONArray jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();

                                ArrayList<ModelClass> arrayList;
                                Type type = new TypeToken<ArrayList<ModelClass>>() {
                                }.getType();
                                if (jsonArray.length() > 0) {
                                    arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
                                    for (ModelClass modelClass : arrayList) {
                                        if (!modelClass.getDate().equals("") && !modelClass.getSyncStatus().equals("0")) {
                                            Log.v("tpApproval", "--11--" + modelClass.getDayNo());
                                            prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, modelClass.getDate()), modelClass.getDayNo(), arrayList, true);
                                            break;
                                        }
                                    }
                                }

//                                jsonArray = sqLite.getTPDataOfMonth(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
                                jsonArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
                                arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
                                boolean allDateSynced = false;
                                for (ModelClass modelClass : arrayList) {
                                    if (!modelClass.getDate().isEmpty()) {
                                        if (modelClass.getSyncStatus().equals("0")) {
                                            allDateSynced = true;
                                        } else {
                                            allDateSynced = false;
                                            break;
                                        }
                                    }
                                }

                                if (allDateSynced) {
                                    Log.v("tpApproval", "--8888--");
                                    sendWholeMonthStatus(localDate);
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
                commonUtilsMethods.showToastMessage(TourPlanActivity.this, getString(R.string.toast_response_failed));
                saveTpLocal(modelClassArrayList, date, month, "1"); // Sync Failed
            }
        });

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

}