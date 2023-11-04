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
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
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
    public static LinearLayout addSaveBtnLayout,clrSaveBtnLayout;
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
    private DrawerLayout drawerLayout;
    String sfName = "",sfCode = "",division_code = "",sfType = "",designation = "",state_code ="",subdivision_code = "";
    String drNeed = "" , maxDocCount = "",addSessionNeed= "", addSessionCountLimit = "",FW_meetup_mandatory = "" ,holidayMode = "" , weeklyOffCaption = "",holidayEditable = "",weeklyOffEditable = "";
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
//        new Async().execute(localDate);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run () {
        populateCalendarAdapter(dayWiseArrayCurrentMonth);
        populateSummaryAdapter(dayWiseArrayCurrentMonth);

//            }
//        },100);

//        getTPData();
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

                if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())){
                    if (dayWiseArrayCurrentMonth.size() == 0){
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                    populateSummaryAdapter(dayWiseArrayCurrentMonth);
                    monthInAdapter = 0;
                } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().plusMonths(1).getMonth().toString())) {
                    if (dayWiseArrayNextMonth.size() == 0){
                        dayWiseArrayNextMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayNextMonth);
                    populateSummaryAdapter(dayWiseArrayNextMonth);
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

                if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().getMonth().toString())){
                    if (dayWiseArrayCurrentMonth.size() == 0){
                        dayWiseArrayCurrentMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayCurrentMonth);
                    populateSummaryAdapter(dayWiseArrayCurrentMonth);
                    monthInAdapter = 0;
                } else if (localDate.getMonth().toString().equalsIgnoreCase(LocalDate.now().minusMonths(1).getMonth().toString())) {
                    if (dayWiseArrayPrevMonth.size() == 0){
                        dayWiseArrayPrevMonth = prepareModelClassForMonth(localDate);
                    }
                    populateCalendarAdapter(dayWiseArrayPrevMonth);
                    populateSummaryAdapter(dayWiseArrayPrevMonth);
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

        binding.tpNavigation.checkBoxSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                SessionEditAdapter.MyViewHolder viewHolder = (SessionEditAdapter.MyViewHolder)  binding.tpNavigation.tpSessionRecView.findViewHolderForAdapterPosition(sessionEditAdapter.itemPosition);
                sessionEditAdapter.saveCheckedItem(viewHolder);
            }
        });

        binding.tpNavigation.addSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                UtilityClass.hideKeyboard(TourPlanActivity.this);
                boolean isEmpty = false;
                int position = 0;

                ModelClass modelClass = SessionEditAdapter.inputData;
                ArrayList<ModelClass.SessionList> sessionLists = modelClass.getSessionList();
                if (sessionLists.size() < Integer.parseInt(addSessionCountLimit)){
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
                            } else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                                if (FW_meetup_mandatory.equals("0")) {
                                    if (drNeed.equals("0")){
                                        if (modelClass1.getListedDr().size() == 0){
                                            isEmpty = true;
                                            position = i;
                                            Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                            break;
                                        } else if (modelClass1.getListedDr().size() > Integer.parseInt(maxDocCount)){
                                            isEmpty = true;
                                            position = i;
                                            Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }

                                    if (modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 &&
                                            modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        } else if (modelClass1.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            if (FW_meetup_mandatory.equals("0")) {
                                if (drNeed.equals("0")){
                                    if (modelClass1.getListedDr().size() == 0){
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    } else if (modelClass1.getListedDr().size() > Integer.parseInt(maxDocCount)){
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                if (modelClass1.getListedDr().size() == 0 && modelClass1.getChemist().size() == 0 && modelClass1.getStockiest().size() == 0 &&
                                        modelClass1.getUnListedDr().size() == 0 && modelClass1.getCip().size() == 0 && modelClass1.getHospital().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }
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
                UtilityClass.hideKeyboard(TourPlanActivity.this);
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
                        } else if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                            if (FW_meetup_mandatory.equals("0")) {
                                if (drNeed.equals("0")) {
                                    if (modelClass.getListedDr().size() == 0){
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    } else if (modelClass.getListedDr().size() > Integer.parseInt(maxDocCount)){
                                        isEmpty = true;
                                        position = i;
                                        Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }

                                if (modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 &&
                                        modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) {
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    } else if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                        if (FW_meetup_mandatory.equals("0")) {
                            if (drNeed.equals("0")){
                                if (modelClass.getListedDr().size() == 0){
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "Select " + loginResponse.getDrCap() + " in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                } else if (modelClass.getListedDr().size() > Integer.parseInt(maxDocCount)){
                                    isEmpty = true;
                                    position = i;
                                    Toast.makeText(TourPlanActivity.this, "You have selected the " + loginResponse.getDrCap() + " more than limits " + (i + 1), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if (modelClass.getListedDr().size() == 0 && modelClass.getChemist().size() == 0 && modelClass.getStockiest().size() == 0 &&
                                    modelClass.getUnListedDr().size() == 0 && modelClass.getCip().size() == 0 && modelClass.getHospital().size() == 0) {
                                isEmpty = true;
                                position = i;
                                Toast.makeText(TourPlanActivity.this, "Select any masters in session " + (i + 1), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }

                if (!isEmpty){
                    binding.tpDrawer.closeDrawer(GravityCompat.END);
                    inputModelClass.setSubmittedTime(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
                    if (monthInAdapter == 0){
                        for (int i=0;i<dayWiseArrayCurrentMonth.size();i++){
                            if (dayWiseArrayCurrentMonth.get(i).getDate().equalsIgnoreCase(inputModelClass.getDate())){
                                dayWiseArrayCurrentMonth.remove(i);
                                dayWiseArrayCurrentMonth.add(i,inputModelClass); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayCurrentMonth);
                        sqLite.saveTPData(Constants.CURRENT_MONTH, new Gson().toJson(dayWiseArrayCurrentMonth));
                    } else if (monthInAdapter == 1) {
                        for (int i=0;i<dayWiseArrayNextMonth.size();i++){
                            if (dayWiseArrayNextMonth.get(i).getDate().equalsIgnoreCase(inputModelClass.getDate())){
                                dayWiseArrayNextMonth.remove(i);
                                dayWiseArrayNextMonth.add(i,inputModelClass); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayNextMonth);
                        sqLite.saveTPData(Constants.NEXT_MONTH, new Gson().toJson(dayWiseArrayNextMonth));
                    } else if (monthInAdapter == -1) {
                        for (int i=0;i<dayWiseArrayPrevMonth.size();i++){
                            if (dayWiseArrayPrevMonth.get(i).getDate().equalsIgnoreCase(inputModelClass.getDate())){
                                dayWiseArrayPrevMonth.remove(i);
                                dayWiseArrayPrevMonth.add(i,inputModelClass); // removed and replaced the object with updated session data
                                break;
                            }
                        }
                        populateSummaryAdapter(dayWiseArrayPrevMonth);
                        sqLite.saveTPData(Constants.PREVIOUS_MONTH, new Gson().toJson(dayWiseArrayPrevMonth));
                    }
                    calendarAdapter.notifyDataSetChanged();
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
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
            }
        });

        binding.tpNavigation.sessionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.tpNavigation.addEditViewTxt.setText("Edit Plan");
                populateSessionEditAdapter(sessionViewAdapter.inputDataModel);
            }
        });

        binding.tpSendToApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                try {
                    ArrayList<ModelClass> arrayList = new ArrayList<>();
                    ArrayList<ObjectModelClass> objectModelClasses = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray();
                    if (monthInAdapter == 0){
                        jsonArray = sqLite.getTPData(Constants.CURRENT_MONTH);
                    } else if (monthInAdapter == 1) {
                        jsonArray = sqLite.getTPData(Constants.NEXT_MONTH);
                    } else if (monthInAdapter == -1) {
                        jsonArray = sqLite.getTPData(Constants.PREVIOUS_MONTH);
                    }

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject  jsonObject = jsonArray.getJSONObject(i);
                        ModelClass modelClass = new ModelClass();
                        Type type = new TypeToken<ModelClass>() {
                        }.getType();
                        modelClass = new Gson().fromJson(String.valueOf(jsonObject), type);
                        arrayList.add(modelClass);
                    }

                    ArrayList<ObjectModelClass.TPData> tpDataArrayList = new ArrayList<>();
                    for (int i=0;i<arrayList.size();i++) {
                        if (!arrayList.get(i).getDayNo().isEmpty()) {
                            ModelClass modelClass = arrayList.get(i);
                            String date = modelClass.getDate();
                            String dayNo = modelClass.getDayNo();
                            String day = modelClass.getDay();
                            ObjectModelClass.TPData.SubmittedTime submittedTime = new ObjectModelClass.TPData.SubmittedTime(modelClass.getSubmittedTime(),"3","Asia/Kolkata");

                            ArrayList<ObjectModelClass.TPData.Sessions> sessions = new ArrayList<>();
                            for (int j=0;j<modelClass.getSessionList().size();j++) {
                                ModelClass.SessionList sessionList =modelClass.getSessionList().get(j);
                                String workTypeCode = sessionList.getWorkType().getCode();
                                String workTypeName = sessionList.getWorkType().getName();
                                String FWFlg = sessionList.getWorkType().getFWFlg();
                                String hqCode = sessionList.getHQ().getCode();
                                String hqName = sessionList.getHQ().getName();
                                String clusterCode = "",clusterName="",jwCode = "", jwName = "", drCode = "", drName = "",cheCode = "", cheName = "";
                                String stockCode = "", stockName = "",unDrCode = "",unDrName="", cipCode = "", cipName = "",hospCode = "", hospName = "", remarks = "";

                                clusterCode = textBuilder(sessionList.getCluster(),true);
                                clusterName = textBuilder(sessionList.getCluster(),false);

                                jwCode = textBuilder(sessionList.getJC(),true);
                                jwName = textBuilder(sessionList.getJC(),false);

                                drCode = textBuilder(sessionList.getListedDr(),true);
                                drName = textBuilder(sessionList.getListedDr(),false);

                                cheCode = textBuilder(sessionList.getChemist(),true);
                                cheName = textBuilder(sessionList.getChemist(),false);

                                stockCode = textBuilder(sessionList.getStockiest(),true);
                                stockName = textBuilder(sessionList.getStockiest(),false);

                                unDrCode = textBuilder(sessionList.getUnListedDr(),true);
                                unDrName = textBuilder(sessionList.getUnListedDr(),false);

                                cipCode = textBuilder(sessionList.getCip(),true);
                                cipName = textBuilder(sessionList.getCip(),false);

                                hospCode = textBuilder(sessionList.getHospital(),true);
                                hospName = textBuilder(sessionList.getHospital(),false);

                                remarks = sessionList.getRemarks();
                                ObjectModelClass.TPData.Sessions sessions1 = new ObjectModelClass.TPData.Sessions(workTypeCode, workTypeName, FWFlg, hqCode, hqName, clusterCode, clusterName, jwCode, jwName,
                                                                                                                  drCode, drName, cheCode, cheName, stockCode, stockName, unDrCode, unDrName, cipCode, cipName,
                                                                                                                  hospCode, hospName, remarks);
                                sessions.add(sessions1);
                            }

                            ObjectModelClass.TPData TPDataModel = new ObjectModelClass.TPData(date, dayNo, day, "", "", "", submittedTime, sessions);
                            tpDataArrayList.add(TPDataModel);
                        }
                    }
                    ObjectModelClass objectModelClass = new ObjectModelClass(loginResponse.getSF_Code(),loginResponse.getSF_Name(),loginResponse.getDivision_Code(),localDate.getMonthValue(),localDate.getMonth().toString(),localDate.getYear(),tpDataArrayList);
                    Log.e("test","tpData : " + new Gson().toJson(objectModelClass));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    public void uiInitialization(){

        loginResponse = sqLite.getLoginData(true);
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
                drNeed = jsonObject.getString("DrNeed");
                maxDocCount = jsonObject.getString("max_doc");
                maxDocCount = "5";
                addSessionNeed = jsonObject.getString("AddsessionNeed");
                addSessionCountLimit = jsonObject.getString("AddsessionCount");
                FW_meetup_mandatory = jsonObject.getString("FW_meetup_mandatory");
                FW_meetup_mandatory = "0";
                holidayEditable = jsonObject.getString("Holiday_Editable");
                weeklyOffEditable = jsonObject.getString("Weeklyoff_Editable");
            }

            if (addSessionNeed.equalsIgnoreCase("0")){ // if else changed temporarily
                binding.tpNavigation.addSession.setVisibility(View.GONE);
            } else {
                binding.tpNavigation.addSession.setVisibility(View.VISIBLE);
            }

            holidayJSONArray = sqLite.getMasterSyncDataByKey(Constants.HOLIDAY);
            JSONArray weeklyOff = sqLite.getMasterSyncDataByKey(Constants.WEEKLY_OFF);
            for (int i=0;i<weeklyOff.length();i++){
                JSONObject jsonObject = weeklyOff.getJSONObject(i);
                holidayMode = jsonObject.getString("Holiday_Mode");
                weeklyOffCaption = jsonObject.getString("WTname");
            }
            String[] holidayModeArray = holidayMode.split(",");
            weeklyOffDays = new ArrayList<>();
            for (String str : holidayModeArray) {
                switch (str) {
                    case "0" : {
                        weeklyOffDays.add("Sunday");
                        break;
                    }
                    case "1" : {
                        weeklyOffDays.add("Monday");
                        break;
                    }
                    case "2" : {
                        weeklyOffDays.add("Tuesday");
                        break;
                    }
                    case "3" : {
                        weeklyOffDays.add("Wednesday");
                        break;
                    }
                    case "4" : {
                        weeklyOffDays.add("Thursday");
                        break;
                    }
                    case "5" : {
                        weeklyOffDays.add("Friday");
                        break;
                    }
                    case "6" : {
                        weeklyOffDays.add("Saturday");
                        break;
                    }
                }
            }

            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i=0;i<workTypeArray.length();i++){
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (jsonObject.getString("Name").equalsIgnoreCase("Weekly Off")) {
                    weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
                } else if (jsonObject.getString("Name").equalsIgnoreCase("Holiday")) {
                    holidayWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
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

        try {
            ArrayList<String> holidayDateArray = new ArrayList<>();
            for (int i=0;i<holidayJSONArray.length();i++) {
                if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate1.getMonthValue()))){
                    holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
                }
            }

            for (String day : days) {
                if (!day.isEmpty()){
                    String date = day + " " + monthYear  ;
                    Date date1 = new Date(date);
                    String dayName = formatter.format(date1);
                    ModelClass.SessionList sessionList = new ModelClass.SessionList();
                    sessionList = prepareClassForTpAdapter();
                    for (String weeklyOff : weeklyOffDays){
                        if (weeklyOff.equalsIgnoreCase(dayName)) // add weekly off object when the day is holiday
                            sessionList.setWorkType(weeklyOffWorkTypeModel);
                    }

                    for (String holiday : holidayDateArray){
                        if (holiday.equalsIgnoreCase(day)){
                            sessionList.setWorkType(holidayWorkTypeModel); // add holiday work type model object when current month has holiday
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
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

        return new ModelClass.SessionList("", true,"", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public void populateCalendarAdapter (ArrayList<ModelClass> arrayList){
        binding.monthYear.setText(monthYearFromDate(localDate));

        calendarAdapter = new CalendarAdapter(arrayList, TourPlanActivity.this, new OnDayClickInterface() {
            @Override
            public void onDayClicked (int position, String date,ModelClass modelClass) {

                if(!date.equals("")) {
                    binding.tpDrawer.openDrawer(GravityCompat.END);
                    String selectedDate = date + " " +  monthYearFromDate(localDate) ;
                    binding.tpNavigation.planDate.setText(modelClass.getDate());
                    if (!modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("")) {
                        binding.tpNavigation.addEditViewTxt.setText("View Plan");
                        ModelClass modelClass1 = new ModelClass(modelClass);
                        populateSessionViewAdapter(modelClass1);
                    } else {
                        binding.tpNavigation.addEditViewTxt.setText("Add Plan");
                        ModelClass modelClass1 = new ModelClass(modelClass);
                        populateSessionEditAdapter(modelClass1);
                    }
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        binding.calendarRecView.setLayoutManager(layoutManager);
        binding.calendarRecView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

//        populateSummaryAdapter(arrayList);

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

                ModelClass.SessionList modelClass = new ModelClass.SessionList("",true,"",workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                arrayList.getSessionList().remove(position);
                arrayList.getSessionList().add(position,modelClass);

                for (int i=0;i<arrayList.getSessionList().size();i++){
                    arrayList.getSessionList().get(i).setVisible(true);
                }

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

                    ModelClass.SessionList modelClass = new ModelClass.SessionList("",true,"",workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
                    arrayList.getSessionList().remove(position);
                    arrayList.getSessionList().add(modelClass);
                }
                for (int i=0;i<arrayList.getSessionList().size();i++){
                    arrayList.getSessionList().get(i).setVisible(true);
                    arrayList.getSessionList().get(i).setLayoutVisible("");
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

    public void populateSessionViewAdapter(ModelClass modelClass){
        binding.tpDrawer.openDrawer(GravityCompat.END);
        sessionViewAdapter = new SessionViewAdapter(modelClass);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.tpNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        binding.tpNavigation.tpSessionRecView.setAdapter(sessionViewAdapter);
        sessionViewAdapter.notifyDataSetChanged();
        addSaveBtnLayout.setVisibility(View.GONE);
        clrSaveBtnLayout.setVisibility(View.GONE);
        if (modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("Weekly Off")){
            if (weeklyOffEditable.equals("0")){
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        } else if (modelClass.getSessionList().get(0).getWorkType().getName().equalsIgnoreCase("Holiday")){
            if (holidayEditable.equals("0")){
                binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
            }
        } else {
            binding.tpNavigation.editLayout.setVisibility(View.VISIBLE);
        }
    }

    public void populateSummaryAdapter(ArrayList<ModelClass> arrayList){

        ArrayList<ModelClass> modelClasses = new ArrayList<>();
        for (ModelClass modelClass : arrayList) {
            if (!modelClass.getDayNo().isEmpty()){
                modelClasses.add(modelClass);
            }
        }

        summaryAdapter = new SummaryAdapter(modelClasses, TourPlanActivity.this, new SummaryInterface() {
            @Override
            public void onClick (ModelClass modelClass, int position) {
                populateSessionViewAdapter(modelClass);
                binding.tpNavigation.addEditViewTxt.setText("View Plan");
                binding.tpNavigation.planDate.setText(modelClass.getDate());
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TourPlanActivity.this);
        binding.summaryRecView.setLayoutManager(layoutManager);
        binding.summaryRecView.setAdapter(summaryAdapter);
        summaryAdapter.notifyDataSetChanged();

        boolean wholeMonthTpCompleted = false;
        for (int i=0;i<arrayList.size();i++) {  // to enable/disable send to approval button
            if (!arrayList.get(i).getDayNo().isEmpty()) {
                ModelClass.SessionList.WorkType workType = arrayList.get(i).getSessionList().get(0).getWorkType();
                if (!workType.getName().isEmpty()){
                    wholeMonthTpCompleted = true;
                } else {
                    wholeMonthTpCompleted = false;
                    break;
                }
            }
        }
        binding.tpSendToApproval.setEnabled(wholeMonthTpCompleted);

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

    public void getTPData() {

        try {
            if (UtilityClass.isNetworkAvailable(TourPlanActivity.this)){

                apiInterface = RetrofitClient.getRetrofit(TourPlanActivity.this, SharedPref.getCallApiUrl(TourPlanActivity.this));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName","getall_tp");
                jsonObject.put("sfcode",loginResponse.getSF_Code());
                jsonObject.put("division_code",loginResponse.getDivision_Code());
                jsonObject.put("Rsf",SharedPref.getHqCode(TourPlanActivity.this));
                jsonObject.put( "sf_type",loginResponse.getSf_type());
                jsonObject.put("Designation",loginResponse.getDesig());
                jsonObject.put("state_code",loginResponse.getState_Code());
                jsonObject.put("subdivision_code",loginResponse.getSubdivision_code());
                jsonObject.put("tp_month","10,");
                jsonObject.put("tp_year","2023,");

                Call<JsonElement> call = apiInterface.getTP(jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse (@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        Log.e("test","getTp response : " + response.body());
                        if (response.body() != null){

                        }
                    }

                    @Override
                    public void onFailure (@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        Log.e("test","error getTp : " + t);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public String textBuilder(List<ModelClass.SessionList.SubClass> sessionLists, boolean codeOrName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int k=0;k<sessionLists.size();k++){
            if (codeOrName) { // true -> code
                if (stringBuilder.length() == 0)
                    stringBuilder = new StringBuilder(sessionLists.get(k).getCode());
                else
                    stringBuilder.append(",").append(sessionLists.get(k).getCode());
            } else { // false -> name
                if (stringBuilder.length() == 0)
                    stringBuilder = new StringBuilder(sessionLists.get(k).getName());
                else
                    stringBuilder.append(",").append(sessionLists.get(k).getName());
            }
        }
        return stringBuilder.toString();
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

    private class Async extends AsyncTask<LocalDate, Void, ArrayList<ModelClass>>{

        @Override
        protected ArrayList<ModelClass> doInBackground (LocalDate... localDates) {
            ArrayList<ModelClass> modelClasses = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDates[0]));
            String monthYear = monthYearFromDate(localDates[0]);

            for (String day : days) {
                if (!day.isEmpty()){
                    String date = day + " " + monthYear  ;
                    Date date1 = new Date(date);
                    String dayName = formatter.format(date1);
                    ModelClass.SessionList sessionList = new ModelClass.SessionList();
                    sessionList = prepareClassForTpAdapter();
                    for (String holiday : weeklyOffDays){
                        if (holiday.equalsIgnoreCase(dayName)){
                            sessionList.setWorkType(weeklyOffWorkTypeModel);
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

        @Override
        protected void onPostExecute (ArrayList<ModelClass> modelClasses) {
            super.onPostExecute(modelClasses);
            Log.e("test","size in onPostExecute : " + modelClasses.size());
        }
    }

}