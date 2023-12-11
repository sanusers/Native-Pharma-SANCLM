package saneforce.sanclm.activity.tourPlan;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.BuildConfig;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.tourPlan.calendar.CalendarAdapter;
import saneforce.sanclm.activity.tourPlan.calendar.OnDayClickInterface;
import saneforce.sanclm.activity.tourPlan.model.ModelClass;
import saneforce.sanclm.activity.tourPlan.model.ObjectModelClass;
import saneforce.sanclm.activity.tourPlan.session.SessionInterface;
import saneforce.sanclm.activity.tourPlan.session.SessionEditAdapter;
import saneforce.sanclm.activity.tourPlan.session.SessionViewAdapter;
import saneforce.sanclm.activity.tourPlan.summary.SummaryAdapter;
import saneforce.sanclm.activity.tourPlan.summary.SummaryInterface;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.ActivityTourPlanBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.TimeUtils;

public class TourPlanActivity extends AppCompatActivity {
    private ActivityTourPlanBinding binding;
    ApiInterface apiInterface;
    SQLite sqLite;
    LoginResponse loginResponse;
    public static LinearLayout addSaveBtnLayout, clrSaveBtnLayout;
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
    String sfName = "", sfCode = "", division_code = "", sfType = "", designation = "", state_code = "", subdivision_code = "";
    String drNeed = "", maxDrCount = "", addSessionNeed = "", addSessionCountLimit = "", FW_meetup_mandatory = "", holidayMode = "", weeklyOffCaption = "", holidayEditable = "", weeklyOffEditable = "";
    int monthInAdapterFlag = 0; // 0 -> current month , 1 -> next month , -1 -> previous month


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        sqLite = new SQLite(getApplicationContext());
        addSaveBtnLayout = binding.tpNavigation.addSaveLayout;
        clrSaveBtnLayout = binding.tpNavigation.clrSaveBtnLayout;

        uiInitialization();
        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
        populateCalendarAdapter(dayWiseArrayCurrentMonth);

//        getTPData();
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

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressed();
                startActivity(new Intent(TourPlanActivity.this, HomeDashBoard.class));
            }
        });

        binding.calendarNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.calendarPrevButton.setEnabled(true);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_black, null));
                localDate = localDate.plusMonths(1);

                if(LocalDate.now().plusMonths(1).isEqual(localDate)) {
                    binding.calendarNextButton.setEnabled(false);
                    binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
                }else {
                    binding.calendarNextButton.setEnabled(true);
                }

                if(localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                    monthInAdapterFlag = 0;
                    if(dayWiseArrayCurrentMonth.size() == 0) {
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                }else if(localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().plusMonths(1).getMonth().toString())) {
                    monthInAdapterFlag = 1;
                    if(dayWiseArrayNextMonth.size() == 0) {
                        dayWiseArrayNextMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayNextMonth);
                }
            }
        });

        binding.calendarPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.calendarNextButton.setEnabled(true);
                binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_black, null));
                localDate = localDate.minusMonths(1);
                if(LocalDate.now().minusMonths(1).isEqual(localDate)) {
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                }else {
                    binding.calendarPrevButton.setEnabled(true);
                }

                if(localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())) {
                    monthInAdapterFlag = 0;
                    if(dayWiseArrayCurrentMonth.size() == 0) {
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                }else if(localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().minusMonths(1).getMonth().toString())) {
                    monthInAdapterFlag = -1;
                    if(dayWiseArrayPrevMonth.size() == 0) {
                        dayWiseArrayPrevMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayPrevMonth);
                }
            }
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

                ModelClass modelClass = SessionEditAdapter.inputDataArray;
                ArrayList<ModelClass.SessionList> sessionLists = modelClass.getSessionList();
                if(sessionLists.size()<Integer.parseInt(addSessionCountLimit)) {
                    for (int i = 0; i<sessionLists.size(); i++) {
                        ModelClass.SessionList modelClass1 = sessionLists.get(i);
                        if(modelClass1.getWorkType().getName().isEmpty()) {
                            isEmpty = true;
                            position = i;
                            Toast.makeText(TourPlanActivity.this, "Complete Session " + (i + 1), Toast.LENGTH_SHORT).show();
                            break;
                        }else if(modelClass1.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                            if(modelClass1.getHQ().getName().isEmpty()) {
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select Head Quarters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            }else if(modelClass1.getCluster().size() == 0) {
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select Clusters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            }else if(modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                                if(FW_meetup_mandatory.equals("0")) {
                                    if(drNeed.equals("0")) {
                                        if(modelClass1.getListedDr().size() == 0) {
                                            isEmpty = true;
                                            position = i;
                                            Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                            break;
                                        }else if(modelClass1.getListedDr().size()>Integer.parseInt(maxDrCount)) {
                                            isEmpty = true;
                                            position = i;
                                            Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }

                                    if(modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 &&
                                            modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        }else if(modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            if(FW_meetup_mandatory.equals("0")) {
                                if(drNeed.equals("0")) {
                                    if(modelClass1.getListedDr().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }else if(modelClass1.getListedDr().size()>Integer.parseInt(maxDrCount)) {
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                if(modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 &&
                                        modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }

                    if(!isEmpty) {
                        sessionLists.add(prepareSessionListForAdapter());
                        populateSessionEditAdapter(modelClass);
                        scrollToPosition(modelClass.getSessionList().size() - 1, false);
                    }else {
                        scrollToPosition(position, true);
                    }
                }else {
                    Toast.makeText(TourPlanActivity.this, "Reached Session limit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tpNavigation.sessionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilityClass.hideKeyboard(TourPlanActivity.this);
                boolean isEmpty = false;
                int position = 0;

                ModelClass dataModel = SessionEditAdapter.inputDataArray;
                ArrayList<ModelClass.SessionList> sessionLists = dataModel.getSessionList();
                String dayNo = dataModel.getDayNo();
                for (int i = 0; i<sessionLists.size(); i++) {
                    ModelClass.SessionList modelClass = sessionLists.get(i);
                    if(modelClass.getWorkType().getName().isEmpty()) {
                        isEmpty = true;
                        position = i;
                        Toast.makeText(TourPlanActivity.this, "Complete Session " + (i + 1), Toast.LENGTH_SHORT).show();
                        break;
                    }else if(modelClass.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) { // TerrrSlFlg is "Y" (yes) means head quarter and clusters are mandatory
                        if(modelClass.getHQ().getName().isEmpty()) {
                            isEmpty = true;
                            position = i;
                            Toast.makeText(TourPlanActivity.this, "Select Head Quarters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                            break;
                        }else if(modelClass.getCluster().size() == 0) {
                            isEmpty = true;
                            position = i;
                            Toast.makeText(TourPlanActivity.this, "Select Clusters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                            break;
                        }else if(modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            if(FW_meetup_mandatory.equals("0")) {
                                if(drNeed.equals("0")) {
                                    if(modelClass.getListedDr().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }else if(modelClass.getListedDr().size()>Integer.parseInt(maxDrCount)) {
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }

                                if(modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 &&
                                        modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }else if(modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) { // if the selected work type is "F" means Field Work then we need to check the FW_meetup_mandatory
                        if(FW_meetup_mandatory.equals("0")) { // "0"-- yes
                            if(drNeed.equals("0")) { // Dr meet up mandatory
                                if(modelClass.getListedDr().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }else if(modelClass.getListedDr().size()>Integer.parseInt(maxDrCount)) { //Selected Dr count should not be more than maxDrCount setup limit
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if(modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 &&
                                    modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) { // when Dr meetup not mandatory but FW meetup mandatory.So check any of the meetup selected
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }

                if(!isEmpty) {
                    binding.tpDrawer.closeDrawer(GravityCompat.END);
                    dataModel.setSubmittedTime(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
                    if(monthInAdapterFlag == 0) {
                        for (int i = 0; i<dayWiseArrayCurrentMonth.size(); i++) {
                            if(dayWiseArrayCurrentMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                                dayWiseArrayCurrentMonth.remove(i);
                                dayWiseArrayCurrentMonth.add(i, dataModel); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayCurrentMonth);
                        prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayCurrentMonth);
                    }else if(monthInAdapterFlag == 1) {
                        for (int i = 0; i<dayWiseArrayNextMonth.size(); i++) {
                            if(dayWiseArrayNextMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                                dayWiseArrayNextMonth.remove(i);
                                dayWiseArrayNextMonth.add(i, dataModel); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayNextMonth);
                        prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayNextMonth);
                    }else if(monthInAdapterFlag == -1) {
                        for (int i = 0; i<dayWiseArrayPrevMonth.size(); i++) {
                            if(dayWiseArrayPrevMonth.get(i).getDate().equalsIgnoreCase(dataModel.getDate())) {
                                dayWiseArrayPrevMonth.remove(i);
                                dayWiseArrayPrevMonth.add(i, dataModel); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayPrevMonth);
                        prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, dataModel.getDate()), dayNo, dayWiseArrayPrevMonth);
                    }
                    calendarAdapter.notifyDataSetChanged();
                }else {
                    scrollToPosition(position, true);
                }

            }
        });

        binding.tpNavigation.sessionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tpNavigation.addEditViewTxt.setText("Edit Plan");
                populateSessionEditAdapter(sessionViewAdapter.inputDataModel);
            }
        });

        binding.tpSendToApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<ObjectModelClass> objectModelClasses = new ArrayList<>();
                JSONArray jsonArray = new JSONArray();
                if(monthInAdapterFlag == 0) {
                    jsonArray = sqLite.getTPData(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
                }else if(monthInAdapterFlag == 1) {
                    jsonArray = sqLite.getTPData(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate.plusMonths(1))));
                }else if(monthInAdapterFlag == -1) {
                    jsonArray = sqLite.getTPData(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate.minusMonths(1))));
                }

                ArrayList<ModelClass> arrayList = new ArrayList<>();
                if(jsonArray.length()>0) {
                    Type type = new TypeToken<ArrayList<ModelClass>>() {
                    }.getType();
                    arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);

                    for (ModelClass modelClass : arrayList) {
                        if(!modelClass.getDate().equals("") && !modelClass.isSentForApproval()) {
                            prepareObjectToSendForApproval(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_23, modelClass.getDate()), modelClass.getDayNo(), arrayList);
                        }
                    }
                }

            }
        });

    }

    public void uiInitialization() {

        localDate = LocalDate.now();
        loginResponse = sqLite.getLoginData();
        sfName = loginResponse.getSF_Name();
        sfCode = loginResponse.getSF_Code();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        state_code = loginResponse.getState_Code();
        sfType = loginResponse.getSf_type();

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.TP_SETUP);  //Tour Plan setup
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                drNeed = jsonObject.getString("DrNeed");
                maxDrCount = jsonObject.getString("max_doc");
                addSessionNeed = jsonObject.getString("AddsessionNeed");
                addSessionCountLimit = jsonObject.getString("AddsessionCount");
                FW_meetup_mandatory = jsonObject.getString("FW_meetup_mandatory");
                holidayEditable = jsonObject.getString("Holiday_Editable");
                weeklyOffEditable = jsonObject.getString("Weeklyoff_Editable");
            }

            if(addSessionNeed.equalsIgnoreCase("0")) {
                binding.tpNavigation.addSession.setVisibility(View.VISIBLE);
            }else {
                binding.tpNavigation.addSession.setVisibility(View.GONE);
            }

            holidayJSONArray = sqLite.getMasterSyncDataByKey(Constants.HOLIDAY); //Holiday data
            JSONArray weeklyOff = sqLite.getMasterSyncDataByKey(Constants.WEEKLY_OFF); // Weekly Off data
            for (int i = 0; i<weeklyOff.length(); i++) {
                JSONObject jsonObject = weeklyOff.getJSONObject(i);
                holidayMode = jsonObject.getString("Holiday_Mode");
                weeklyOffCaption = jsonObject.getString("WTname");
            }
            String[] holidayModeArray = holidayMode.split(",");
            weeklyOffDays = new ArrayList<>();
            for (String str : holidayModeArray) {
                switch (str){
                    case "0":{
                        weeklyOffDays.add("Sunday");
                        break;
                    }
                    case "1":{
                        weeklyOffDays.add("Monday");
                        break;
                    }
                    case "2":{
                        weeklyOffDays.add("Tuesday");
                        break;
                    }
                    case "3":{
                        weeklyOffDays.add("Wednesday");
                        break;
                    }
                    case "4":{
                        weeklyOffDays.add("Thursday");
                        break;
                    }
                    case "5":{
                        weeklyOffDays.add("Friday");
                        break;
                    }
                    case "6":{
                        weeklyOffDays.add("Saturday");
                        break;
                    }
                }
            }

            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE); //List of Work Types
            for (int i = 0; i<workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if(jsonObject.getString("Name").equalsIgnoreCase("Weekly Off")) {
                    weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
                }else if(jsonObject.getString("Name").equalsIgnoreCase("Holiday")) {
                    holidayWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = localDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        switch (dayOfWeek){
            case 1:{
                dayOfWeek = 2;
                break;
            }
            case 2:{
                dayOfWeek = 3;
                break;
            }
            case 3:{
                dayOfWeek = 4;
                break;
            }
            case 4:{
                dayOfWeek = 5;
                break;
            }
            case 5:{
                dayOfWeek = 6;
                break;
            }
            case 6:{
                dayOfWeek = 7;
                break;
            }
            case 7:{
                dayOfWeek = 1;
                break;
            }
        }

        for (int i = 1; i<=42; i++) {
            if(i<dayOfWeek) {
                daysInMonthArray.add("");
            }else {
                if(i<daysInMonth + dayOfWeek) {
                    daysInMonthArray.add(String.valueOf((i + 1) - dayOfWeek));
                }
            }
        }

//        for(int i = 1; i <= 42; i++) {
//            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
//                daysInMonthArray.add("");
//            } else {
//                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
//            }
//        }

        //To eliminate the excess empty dates which comes with the LocalDate library
        if(daysInMonthArray.size()>=22 && daysInMonthArray.size()<=28) {
            for (int i = daysInMonthArray.size(); i<28; i++) {
                daysInMonthArray.add("");
            }
        }else if(daysInMonthArray.size()>=29 && daysInMonthArray.size()<=35) {
            for (int i = daysInMonthArray.size(); i<35; i++) {
                daysInMonthArray.add("");
            }
        }else if(daysInMonthArray.size()>=36 && daysInMonthArray.size()<=42) {
            for (int i = daysInMonthArray.size(); i<42; i++) {
                daysInMonthArray.add("");
            }
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
            JSONArray savedDataArray = new JSONArray(sqLite.getTPData(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate1))).toString());
            if(savedDataArray.length()>0) { //Use the saved data if Tour Plan table has data of a selected month
                Type type = new TypeToken<ArrayList<ModelClass>>() {
                }.getType();
                modelClasses = new Gson().fromJson(savedDataArray.toString(), type);
            }else { //if tour plan table has no data

                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate1));
                String monthYear = monthYearFromDate(localDate1);
                String month = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_8, monthYear);
                String year = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);

                ArrayList<String> holidayDateArray = new ArrayList<>();
                for (int i = 0; i<holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                    if(holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate1.getMonthValue()))) {
                        holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
                    }
                }

                for (String day : days) {
                    if(!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        ModelClass.SessionList sessionList = new ModelClass.SessionList();
                        sessionList = prepareSessionListForAdapter();

                        if(weeklyOffDays.contains(dayName)) // add weekly off object when the day is declared as Weekly Off
                            sessionList.setWorkType(weeklyOffWorkTypeModel);

                        if(holidayDateArray.contains(day))
                            sessionList.setWorkType(holidayWorkTypeModel); // add holiday work type model object when current date is declared as holiday

                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        sessionLists.add(sessionList);
                        ModelClass modelClass = new ModelClass(day, date, dayName, month, year, true, sessionLists);
                        modelClasses.add(modelClass);
                    }else {
                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        ModelClass modelClass = new ModelClass(day, "", "", "", "", true, sessionLists);
                        modelClasses.add(modelClass);
                    }
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return modelClasses;
    }

    public ModelClass.SessionList prepareSessionListForAdapter() {
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

    public void populateCalendarAdapter(ArrayList<ModelClass> arrayList) {
        binding.monthYear.setText(monthYearFromDate(localDate));

        calendarAdapter = new CalendarAdapter(arrayList, TourPlanActivity.this, new OnDayClickInterface() {
            @Override
            public void onDayClicked(int position, String date, ModelClass modelClass) {

                if(!date.equals("")) {
                    binding.tpDrawer.openDrawer(GravityCompat.END);
                    binding.tpNavigation.planDate.setText(modelClass.getDate());
                    ModelClass modelClass1 = new ModelClass(modelClass);
                    if(!modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("")) {
                        binding.tpNavigation.addEditViewTxt.setText("View Plan");
                        populateSessionViewAdapter(modelClass1);
                    }else {
                        binding.tpNavigation.addEditViewTxt.setText("Add Plan");
                        populateSessionEditAdapter(modelClass1);
                    }
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
                SessionEditAdapter.inputDataArray.getSessionList().remove(position);
                populateSessionEditAdapter(SessionEditAdapter.inputDataArray);
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

                for (int i = 0; i<arrayList.getSessionList().size(); i++) {
                    arrayList.getSessionList().get(i).setVisible(true);
                }

                populateSessionEditAdapter(arrayList);
                scrollToPosition(position, false);
            }

            @Override
            public void hqChanged(ModelClass arrayList, int position, boolean changed) {
                if(changed) {
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
                for (int i = 0; i<arrayList.getSessionList().size(); i++) {
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
        if(modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("Weekly Off")) {
            if(weeklyOffEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        }else if(modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("Holiday")) {
            if(holidayEditable.equals("0")) {
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        }else {
            binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
        }
    }

    public void populateSummaryAdapter(ArrayList<ModelClass> arrayList) {

        ArrayList<ModelClass> modelClasses = new ArrayList<>();
        for (ModelClass modelClass : arrayList) {
            if(!modelClass.getDayNo().isEmpty() && !modelClass.getSessionList().get(0).getWorkType().getName().isEmpty())
                modelClasses.add(modelClass);
        }

        summaryAdapter = new SummaryAdapter(modelClasses, TourPlanActivity.this, new SummaryInterface() {
            @Override
            public void onClick(ModelClass modelClass, int position) {
                populateSessionViewAdapter(modelClass);
                binding.tpNavigation.addEditViewTxt.setText("View Plan");
                binding.tpNavigation.planDate.setText(modelClass.getDate());
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.summaryRecView.setLayoutManager(layoutManager);
        binding.summaryRecView.setAdapter(summaryAdapter);

        boolean wholeMonthTpCompleted = false;
        for (int i = 0; i<arrayList.size(); i++) {  // to enable/disable the send to approval button
            if(!arrayList.get(i).getDayNo().isEmpty()) {
                ModelClass.SessionList.WorkType workType = arrayList.get(i).getSessionList().get(0).getWorkType();
                if(!workType.getName().isEmpty()) {
                    wholeMonthTpCompleted = true;
                }else {
                    wholeMonthTpCompleted = false;
                    break;
                }
            }
        }
        binding.tpSendToApproval.setEnabled(wholeMonthTpCompleted);

    }

    public void scrollToPosition(int position, boolean fieldEmpty) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fieldEmpty) {
                    RecyclerView.ViewHolder holder = binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(position);
                    if(holder != null) {
                        holder.itemView.findViewById(R.id.relativeLayout).setSelected(true);
                    }
                }
                binding.tpNavigation.tpSessionRecView.scrollToPosition(position);
            }
        }, 50);
    }

    public void getTPData() {

        try {
            if(UtilityClass.isNetworkAvailable(TourPlanActivity.this)) {

                apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", "getall_tp");
                jsonObject.put("sfcode", loginResponse.getSF_Code());
                jsonObject.put("division_code", loginResponse.getDivision_Code());
                jsonObject.put("Rsf", SharedPref.getHqCode(TourPlanActivity.this));
                jsonObject.put("sf_type", loginResponse.getSf_type());
                jsonObject.put("Designation", loginResponse.getDesig());
                jsonObject.put("state_code", loginResponse.getState_Code());
                jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
                jsonObject.put("tp_month", "10,");
                jsonObject.put("tp_year", "2023,");

                Call<JsonElement> call = apiInterface.getTP(jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        Log.e("test", "getTp response : " + response.body());
                        if(response.body() != null) {

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        Log.e("test", "error getTp : " + t);
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void prepareObjectToSendForApproval(String month, String dateForApproval, ArrayList<ModelClass> arrayList) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (ModelClass modelClass : arrayList) {
                if(!modelClass.getDayNo().isEmpty()) {
                    if(modelClass.getDayNo().equals(dateForApproval)) {
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("SFCode", sfCode);
                        jsonObject.put("SFName", sfName);
                        jsonObject.put("Div", division_code);
                        jsonObject.put("Mnth", modelClass.getMonth());
                        jsonObject.put("Yr", modelClass.getYear());
                        jsonObject.put("dayno", modelClass.getDayNo());
                        jsonObject.put("Change_Status", "0");
                        jsonObject.put("Rejection_Reason", "");
                        jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_19, TimeUtils.FORMAT_4, modelClass.getDate()) + " 00:00:00");
                        jsonObject.put("submitted_time", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
                        jsonObject.put("Entry_mode", "0");
                        jsonObject.put("Approve_mode", "");
                        jsonObject.put("Approved_time", "");
                        jsonObject.put("app_version", BuildConfig.VERSION_NAME);
                        jsonObject.put("Mode", "Android-Edet");

                        String WTCode = "", WTName = "", FWFlg = "", HQCodes = "", HQNames = "", clusterCodes = "", clusterNames = "", JWCodes = "", JWNames = "", Dr_Code = "", Dr_Name = "", Chem_Code = "", Chem_Name = "", Stockist_Code = "", Stockist_Name = "", cip_code = "", cip_name = "", hosp_code = "", hosp_Name = "", DayRemarks = "";
                        String WTCode2 = "", WTName2 = "", FWFlg2 = "", HQCodes2 = "", HQNames2 = "", clusterCode2 = "", clusterName2 = "", JWCodes2 = "", JWNames2 = "", Dr_two_code = "", Dr_two_name = "", Chem_two_code = "", Chem_two_name = "", Stockist_two_code = "", Stockist_two_name = "", cip_code2 = "", cip_name2 = "", hosp_code2 = "", hosp_Name2 = "", DayRemarks2 = "";
                        String WTCode3 = "", WTName3 = "", FWFlg3 = "", HQCodes3 = "", HQNames3 = "", clusterCode3 = "", clusterName3 = "", JWCodes3 = "", JWNames3 = "", Dr_three_code = "", Dr_three_name = "", Chem_three_code = "", Chem_three_name = "", Stockist_three_code = "", Stockist_three_name = "", cip_code3 = "", cip_name3 = "", hosp_code3 = "", hosp_Name3 = "", DayRemarks3 = "";

                        for (int i = 0; i<modelClass.getSessionList().size(); i++) {
                            ModelClass.SessionList sessionList = modelClass.getSessionList().get(i);
                            if(i == 0) {
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

                            }else if(i == 1) {
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

                            }else if(i == 2) {
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
                        jsonObject.put("clusterCode", clusterCodes);
                        jsonObject.put("clusterName", clusterNames);
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
                        jsonObject.put("clusterCode2", clusterCode2);
                        jsonObject.put("clusterName2", clusterName2);
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
                        jsonObject.put("clusterCode3", clusterCode3);
                        jsonObject.put("clusterName3", clusterName3);
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

            sendTpForApproval(jsonArray, arrayList, dateForApproval, month);

        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }

    }

    public String textBuilder(List<ModelClass.SessionList.SubClass> sessionLists, boolean codeOrName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<sessionLists.size(); i++) {
            if(codeOrName) { // true -> code
                stringBuilder.append(sessionLists.get(i).getCode()).append(",");
            }else { // false -> name
                stringBuilder.append(sessionLists.get(i).getName()).append(",");
            }
        }
        return stringBuilder.toString();
    }

    public void sendTpForApproval(JSONArray jsonArray, ArrayList<ModelClass> modelClassArrayList, String date, String month) {

        if(UtilityClass.isNetworkAvailable(TourPlanActivity.this)) {
            apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
            Call<JsonObject> call = apiInterface.saveTP(jsonArray.toString());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    Log.e("test", "saveTp response : " + response.body());
                    try {
                        if(response.isSuccessful() && response.body() != null) {
                            JsonObject jsonObject = response.body();
                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                            saveTpLocal(modelClassArrayList, date, month, jsonObject.has("success") && jsonObject1.getBoolean("success"));
                        }else {
                            saveTpLocal(modelClassArrayList, date, month, false);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("test", "onFailure saveTp : " + t);
                    saveTpLocal(modelClassArrayList, date, month, false);
                }
            });

        }else {
            Toast.makeText(getApplicationContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
            saveTpLocal(modelClassArrayList, date, month, false);
        }

    }

    public void saveTpLocal(ArrayList<ModelClass> arrayList, String date, String month, boolean status) {
        for (ModelClass modelClass : arrayList) {
            if(modelClass.getDayNo().equals(date)) {
                modelClass.setSentForApproval(status);
                break;
            }

        }
        sqLite.saveTPData(month, new Gson().toJson(arrayList));
    }



}