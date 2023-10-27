package saneforce.sanclm.activity.tourPlan;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.ColorSpace;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.PublicKey;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.activity.tourPlan.calendar.CalendarAdapter;
import saneforce.sanclm.activity.tourPlan.calendar.OnDayClickInterface;
import saneforce.sanclm.activity.tourPlan.session.SessionInterface;
import saneforce.sanclm.activity.tourPlan.session.SessionEditAdapter;
import saneforce.sanclm.activity.tourPlan.session.SessionViewAdapter;
import saneforce.sanclm.activity.tourPlan.summary.SummaryAdapter;
import saneforce.sanclm.activity.tourPlan.summary.SummaryInterface;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.ActivityTourPlanBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;

public class TourPlanActivity extends AppCompatActivity {
    private ActivityTourPlanBinding binding;
    public static LinearLayout addSaveBtnLayout,clrSaveBtnLayout;

    CalendarAdapter calendarAdapter = new CalendarAdapter();
    SummaryAdapter summaryAdapter = new SummaryAdapter();
    SessionEditAdapter sessionEditAdapter = new SessionEditAdapter();
    SessionViewAdapter sessionViewAdapter = new SessionViewAdapter();
    public static ArrayList<ModelClass> dayWiseArrayCurrentMonth = new ArrayList<>();
    ArrayList<ModelClass> dayWiseArrayPrevMonth = new ArrayList<>();
    ArrayList<ModelClass> dayWiseArrayNextMonth = new ArrayList<>();
    ArrayList<String> holidaysDay = new ArrayList<>();

    ModelClass.SessionList.WorkType weeklyOffModel = new ModelClass.SessionList.WorkType();
    ModelClass.SessionList.WorkType holidayModel = new ModelClass.SessionList.WorkType();
    ApiInterface apiInterface;
    SQLite sqLite;
    LocalDate localDate;
    private DrawerLayout drawerLayout;
    String sfName = "",sfCode = "",division_code = "",sfType = "",designation = "",state_code ="",subdivision_code = "";
    String addSessionNeed= "",addSessionCount = "",FW_meetup_mandatory = "" ,holidayMode = "" , weeklyOffCaption = "";
    int monthInAdapter = 0; // 0 - current month , 1 - next month , -1 - previous month


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        sqLite = new SQLite(getApplicationContext());
        addSaveBtnLayout = binding.tpNavigation.addSaveLayout;
        clrSaveBtnLayout = binding.tpNavigation.clrSaveBtnLayout;

        localDate = LocalDate.now();
        uiInitialization();
        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run () {
                populateCalendarAdapter(dayWiseArrayCurrentMonth);
//            }
//        },100);

        binding.tpDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        binding.tpDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened (View drawerView) {
                super.onDrawerOpened(drawerView);
                binding.tpNavigation.clrSaveBtnLayout.setVisibility(View.GONE);
            }


            @Override
            public void onDrawerClosed (View drawerView) {
                super.onDrawerClosed(drawerView);
                UtilityClass.hideKeyboard(TourPlanActivity.this);

            }
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
//                onBackPressed();
                startActivity(new Intent(TourPlanActivity.this, HomeDashBoard.class));
            }
        });

        binding.calendarNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.calendarPrevButton.setEnabled(true);
                binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_black, null));
                localDate = localDate.plusMonths(1);

                if (LocalDate.now().plusMonths(1).isEqual(localDate)){
                    binding.calendarNextButton.setEnabled(false);
                    binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
                }else{
                    binding.calendarNextButton.setEnabled(true);
                }

                if (localDate.isEqual(LocalDate.now())){
                    if (dayWiseArrayCurrentMonth.size() == 0){
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                    monthInAdapter = 0;
                } else if (localDate.isEqual(LocalDate.now().plusMonths(1))) {
                    if (dayWiseArrayNextMonth.size() == 0){
                        dayWiseArrayNextMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayNextMonth);
                    monthInAdapter = 1;
                }
            }
        });

        binding.calendarPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.calendarNextButton.setEnabled(true);
                binding.calendarNextButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_black, null));
                localDate = localDate.minusMonths(1);
                if (LocalDate.now().minusMonths(1).isEqual(localDate)){
                    binding.calendarPrevButton.setEnabled(false);
                    binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.less_than_gray, null));
                }else{
                    binding.calendarPrevButton.setEnabled(true);
                }

                if (localDate.isEqual(LocalDate.now())){
                    if (dayWiseArrayCurrentMonth.size() == 0){
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                    monthInAdapter = 0;
                } else if (localDate.isEqual(LocalDate.now().minusMonths(1))) {
                    if (dayWiseArrayPrevMonth.size() == 0){
                        dayWiseArrayPrevMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayPrevMonth);
                    monthInAdapter = -1;
                }
            }
        });

        binding.tpNavigation.tpDrawerCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.tpDrawer.closeDrawer(GravityCompat.END);
            }
        });

        binding.tpNavigation.itemClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                SessionEditAdapter.MyViewHolder viewHolder = (SessionEditAdapter.MyViewHolder) binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(sessionEditAdapter.itemPosition);
                sessionEditAdapter.clearCheckBox(viewHolder);
            }
        });

        binding.tpNavigation.childSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                SessionEditAdapter.MyViewHolder viewHolder = (SessionEditAdapter.MyViewHolder)  binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(sessionEditAdapter.itemPosition);
                sessionEditAdapter.saveCheckedItem(viewHolder);

            }
        });

        binding.tpNavigation.addSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                boolean isEmpty = false;
                int position = 0;

                ModelClass modelClass = SessionEditAdapter.inputData;
                ArrayList<ModelClass.SessionList> sessionLists = modelClass.getSessionList();

                if (sessionLists.size() < Integer.parseInt(addSessionCount)){
                    for (int i = 0; i< sessionLists.size(); i++){
                        ModelClass.SessionList modelClass1 = sessionLists.get(i);
                        if (modelClass1.getWorkType().getName().isEmpty()){
                            isEmpty = true;
                            position = i;
                            Toast.makeText(TourPlanActivity.this, "Complete Session " + (i + 1), Toast.LENGTH_SHORT).show();
                            break;
                        } else if (modelClass1.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                            if (modelClass1.getHQ().getName().isEmpty()){
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select Head Quarters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            } else if (modelClass1.getCluster().size() == 0) {
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select Clusters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            } else if (FW_meetup_mandatory.equals("0")) {
                                if (modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 &&
                                        modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        } else if (FW_meetup_mandatory.equals("0")) {
                            if (modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 &&
                                    modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }

                    if (!isEmpty){
                        sessionLists.add(prepareClassForTpAdapter());
                        populateSessionEditAdapter(modelClass);
                        scrollToPosition(modelClass.getSessionList().size()-1, false);
                    }else{
                        scrollToPosition(position,true);
                    }
                } else {
                    Toast.makeText(TourPlanActivity.this, "Reached Session limit", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.tpNavigation.sessionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                boolean isEmpty = false;
                int position = 0;

                ModelClass inputModelClass = SessionEditAdapter.inputData;
                ArrayList<ModelClass.SessionList> sessionLists = inputModelClass.getSessionList();
                for (int i = 0; i< sessionLists.size(); i++){
                    ModelClass.SessionList modelClass = sessionLists.get(i);
                    if (modelClass.getWorkType().getName().isEmpty()){
                        isEmpty = true;
                        position = i;
                        Toast.makeText(TourPlanActivity.this, "Complete Session " + (i + 1), Toast.LENGTH_SHORT).show();
                        break;
                    } else if (modelClass.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                        if (modelClass.getHQ().getName().isEmpty()){
                            isEmpty = true;
                            position = i;
                            Toast.makeText(TourPlanActivity.this, "Select Head Quarters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                            break;
                        } else if (modelClass.getCluster().size() == 0) {
                            isEmpty = true;
                            position = i;
                            Toast.makeText(TourPlanActivity.this, "Select Clusters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                            break;
                        } else if (FW_meetup_mandatory.equals("0")) {
                            if (modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 &&
                                    modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) {
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    } else if (FW_meetup_mandatory.equals("0")) {
                        if (modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 &&
                                modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) {
                            isEmpty = true;
                            position = i;
                            Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                if (!isEmpty){
                    binding.tpDrawer.closeDrawer(GravityCompat.END);

                    if (monthInAdapter == 0){
                        for (int i=0;i<dayWiseArrayCurrentMonth.size();i++){
                            if (dayWiseArrayCurrentMonth.get(i).getDate().equalsIgnoreCase(inputModelClass.getDate())){
                                dayWiseArrayCurrentMonth.remove(i);
                                dayWiseArrayCurrentMonth.add(i,inputModelClass);
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayCurrentMonth);
                    } else if (monthInAdapter == 1) {
                        for (int i=0;i<dayWiseArrayNextMonth.size();i++){
                            if (dayWiseArrayNextMonth.get(i).getDate().equalsIgnoreCase(inputModelClass.getDate())){
                                dayWiseArrayNextMonth.remove(i);
                                dayWiseArrayNextMonth.add(i,inputModelClass);
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayNextMonth);
                    } else if (monthInAdapter == -1) {
                        for (int i=0;i<dayWiseArrayPrevMonth.size();i++){
                            if (dayWiseArrayPrevMonth.get(i).getDate().equalsIgnoreCase(inputModelClass.getDate())){
                                dayWiseArrayPrevMonth.remove(i);
                                dayWiseArrayPrevMonth.add(i,inputModelClass);
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayPrevMonth);
                    }

                }else{
                    scrollToPosition(position,true);
                }

//                JSONObject tpObject = new JSONObject();
//                try {
//                    tpObject.put("tableName",dayMonthYearFromDate(localDate,"dd"));
//                    tpObject.put("TPDatas","");
//
//                    tpObject.put("dayno",dayMonthYearFromDate(localDate,"dd"));
//                    tpObject.put("DivCode",division_code);
//                    tpObject.put("SFName",sfName);
//                    tpObject.put("SF",sfCode);
//                    tpObject.put("TPDt",TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17,TimeUtils.FORMAT_1,selectedDate));
//                    tpObject.put("TPMonth",dayMonthYearFromDate(localDate,"MM"));
//                    tpObject.put("TPYear",dayMonthYearFromDate(localDate,"yyyy"));
//                    Log.e("test","tpObject : " + tpObject);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }

            }
        });

        binding.tpNavigation.sessionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.tpNavigation.addEditDate.setText("Edit (" + sessionViewAdapter.arrayList.getDate() + ")");
                populateSessionEditAdapter(sessionViewAdapter.arrayList);
            }
        });


    }

    public void uiInitialization(){

        Cursor cursor = sqLite.getLoginData();
        LoginResponse loginResponse = new LoginResponse();
        String loginData = "";
        if (cursor.moveToNext()){
            loginData = cursor.getString(0);
        }
        cursor.close();
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        loginResponse = new Gson().fromJson(loginData, type);

        sfName = loginResponse.getSF_Name();
        sfCode = loginResponse.getSF_Code();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        state_code = loginResponse.getState_Code();
        sfType = loginResponse.getSf_type();
//        hq_code = SharedPref.getHqCode(TourPlanActivity.this); // Selected HQ code in master sync ,it will be changed if any other HQ selected in Add Plan

        //Tour Plan setup
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.TP_PLAN);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                addSessionNeed = jsonObject.getString("AddsessionNeed");
                addSessionCount = jsonObject.getString("AddsessionCount");
                FW_meetup_mandatory = jsonObject.getString("FW_meetup_mandatory");
            }

            if (addSessionNeed.equalsIgnoreCase("0")){
                binding.tpNavigation.addSession.setVisibility(View.GONE);
            } else {
                binding.tpNavigation.addSession.setVisibility(View.VISIBLE);
            }

            JSONArray weeklyOff = sqLite.getMasterSyncDataByKey(Constants.WEEKLY_OFF);
            for (int i=0;i<weeklyOff.length();i++){
                JSONObject jsonObject = weeklyOff.getJSONObject(i);
                holidayMode = jsonObject.getString("Holiday_Mode");
                weeklyOffCaption = jsonObject.getString("WTname");
            }
            String[] holidayModeArray = holidayMode.split(",");
            holidaysDay = new ArrayList<>();
            for (String str : holidayModeArray){
                switch (str) {
                    case "0" : {
                        holidaysDay.add("Sunday");
                        break;
                    }
                    case "1" : {
                        holidaysDay.add("Monday");
                        break;
                    }
                    case "2" : {
                        holidaysDay.add("Tuesday");
                        break;
                    }
                    case "3" : {
                        holidaysDay.add("Wednesday");
                        break;
                    }
                    case "4" : {
                        holidaysDay.add("Thursday");
                        break;
                    }
                    case "5" : {
                        holidaysDay.add("Friday");
                        break;
                    }
                    case "6" : {
                        holidaysDay.add("Saturday");
                        break;
                    }
                }
            }

            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i=0;i<workTypeArray.length();i++){
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (jsonObject.getString("Name").equalsIgnoreCase("Weekly Off")) {
                    weeklyOffModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"),jsonObject.getString("Name"),jsonObject.getString("TerrSlFlg"),jsonObject.getString("Code"));
                } else if (jsonObject.getString("Name").equalsIgnoreCase("Holiday")) {
                    holidayModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"),jsonObject.getString("Name"),jsonObject.getString("TerrSlFlg"),jsonObject.getString("Code"));
                }
            }

        }catch (JSONException e){
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
            case 1 :{
                dayOfWeek = 2;
                break;
            }
            case 2 :{
                dayOfWeek = 3;
                break;
            }
            case 3 :{
                dayOfWeek = 4;
                break;
            }
            case 4 :{
                dayOfWeek = 5;
                break;
            }
            case 5 :{
                dayOfWeek = 6;
                break;
            }
            case 6 :{
                dayOfWeek = 7;
                break;
            }
            case 7 :{
                dayOfWeek = 1;
                break;
            }
        }

        for(int i = 1; i <= 42; i++) {
            if(i < dayOfWeek ) {
                daysInMonthArray.add("");
            } else {
                if (i < daysInMonth + dayOfWeek){
                    daysInMonthArray.add(String.valueOf((i+1)-dayOfWeek));
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

        if (daysInMonthArray.size() >= 22 && daysInMonthArray.size() <=28){
            for (int i=daysInMonthArray.size();i<28;i++){
                daysInMonthArray.add("");
            }
        } else if (daysInMonthArray.size() >= 29 && daysInMonthArray.size() <=35) {
            for (int i=daysInMonthArray.size();i<35;i++){
                daysInMonthArray.add("");
            }
        } else if (daysInMonthArray.size() >= 36 && daysInMonthArray.size() <=42) {
            for (int i=daysInMonthArray.size();i<42;i++){
                daysInMonthArray.add("");
            }
        }

        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        return date.format(formatter);
    }

    private String dayMonthYearFromDate(LocalDate date,String format) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern(format);

        return date.format(formatter);
    }

    public ArrayList<ModelClass> prepareModelClassForMonth(LocalDate localDate1){

        ArrayList<ModelClass> modelClasses = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate1));
        String monthYear = monthYearFromDate(localDate1);

        for (String day : days) {
            if (!day.isEmpty()){
                String date = day + " " + monthYear  ;
                Date date1 = new Date(date);
                String dayName = formatter.format(date1);
                ModelClass.SessionList sessionList = new ModelClass.SessionList();
                sessionList = prepareClassForTpAdapter();
                for (String holiday : holidaysDay){
                    if (holiday.equalsIgnoreCase(dayName)){
                        sessionList.setWorkType(weeklyOffModel);
                    }
                }
                ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                sessionLists.add(sessionList);
                ModelClass modelClass = new ModelClass(day,date,dayName,true,sessionLists);
                modelClasses.add(modelClass);
            } else {
                ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                ModelClass modelClass = new ModelClass(day,"","",true,sessionLists);
                modelClasses.add(modelClass);
            }
        }
        return modelClasses;
    }

    public ModelClass.SessionList prepareClassForTpAdapter(){
        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType("","","","");
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("","");

        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        return new ModelClass.SessionList("", true, workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public void populateCalendarAdapter (ArrayList<ModelClass> arrayList){
        binding.monthYear.setText(monthYearFromDate(localDate));

        calendarAdapter = new CalendarAdapter(arrayList, TourPlanActivity.this, new OnDayClickInterface() {
            @Override
            public void onDayClicked (int position, String date,ModelClass modelClass) {

                if(!date.equals("")) {
                    binding.tpDrawer.openDrawer(GravityCompat.END);
                    String selectedDate = date + " " +  monthYearFromDate(localDate) ;
                    ModelClass modelClass1 = new ModelClass(modelClass);
                    populateSessionEditAdapter(modelClass1);
                    binding.tpNavigation.addEditDate.setText("Add Plan");
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        binding.calendarRecView.setLayoutManager(layoutManager);
        binding.calendarRecView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

        populateSummaryAdapter(arrayList);

    }

    public void populateSessionEditAdapter (ModelClass arrayList){
        binding.tpDrawer.openDrawer(GravityCompat.END);
        sessionEditAdapter = new SessionEditAdapter(arrayList, TourPlanActivity.this, new SessionInterface() {
            @Override
            public void deleteClicked (ModelClass arrayList, int position) {
                SessionEditAdapter.inputData.getSessionList().remove(position);
                populateSessionEditAdapter(SessionEditAdapter.inputData);
            }

            @Override
            public void fieldWorkSelected (ModelClass arrayList, int position) {
                ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(arrayList.getSessionList().get(position).getWorkType());
                ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("","");
                ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

                ModelClass.SessionList modelClass = new ModelClass.SessionList("",true,workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                arrayList.getSessionList().remove(position);
                arrayList.getSessionList().add(position,modelClass);

                populateSessionEditAdapter(arrayList);
                scrollToPosition(position,false);
            }

            @Override
            public void hqChanged (ModelClass arrayList, int position,boolean changed) {
                if (changed){
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

                    ModelClass.SessionList modelClass = new ModelClass.SessionList("",true,workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                    arrayList.getSessionList().remove(position);
                    arrayList.getSessionList().add(modelClass);
                }
                populateSessionEditAdapter(arrayList);
                scrollToPosition(position,false);
            }

            @Override
            public void clusterChanged (ModelClass arrayList, int position) {
                populateSessionEditAdapter(arrayList);
                scrollToPosition(position,false);
            }

        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionEditAdapter);
        sessionEditAdapter.notifyDataSetChanged();
        addSaveBtnLayout.setVisibility(View.VISIBLE);
        binding.tpNavigation.editLayout.setVisibility(View.GONE);

    }

    public void populateSessionViewAdapter(ModelClass arrayList){
        binding.tpDrawer.openDrawer(GravityCompat.END);
        sessionViewAdapter = new SessionViewAdapter(arrayList,TourPlanActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionViewAdapter);
        sessionViewAdapter.notifyDataSetChanged();
        addSaveBtnLayout.setVisibility(View.GONE);
        clrSaveBtnLayout.setVisibility(View.GONE);
        binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
    }

    public void populateSummaryAdapter(ArrayList<ModelClass> arrayList){

        ArrayList<ModelClass> modelClasses = new ArrayList<>();
        for (ModelClass modelClass : arrayList) {
            if (!modelClass.getDayNo().isEmpty()){
                modelClasses.add(modelClass);
            }
        }

        Log.e("test","size : " + modelClasses.size());

        summaryAdapter = new SummaryAdapter(modelClasses, TourPlanActivity.this, new SummaryInterface() {
            @Override
            public void onClick (ModelClass arrayList, int position) {
                populateSessionViewAdapter(arrayList);
                binding.tpNavigation.addEditDate.setText(arrayList.getDate());
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.summaryRecView.setLayoutManager(layoutManager);
        binding.summaryRecView.setAdapter(summaryAdapter);
        summaryAdapter.notifyDataSetChanged();
    }

    public void scrollToPosition(int position,boolean fieldEmpty){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run () {
                if (fieldEmpty){
                    RecyclerView.ViewHolder holder = binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(position);
                    if (holder != null) {
                    holder.itemView.findViewById(R.id.relativeLayout).setSelected(true);
                    }
                }
                binding.tpNavigation.tpSessionRecView.scrollToPosition(position);
            }
        },50);
    }

    @Override
    protected void onStart () {
        super.onStart();
//        uiInitialization();
    }

    @Override
    protected void onResume () {
        super.onResume();
//        uiInitialization();
    }
}