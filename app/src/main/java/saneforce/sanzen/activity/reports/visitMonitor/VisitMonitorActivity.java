package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.VisitStatsAdapter;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class VisitMonitorActivity extends AppCompatActivity {
    ActivityVisitMonitorBinding binding;

    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisitMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayout backArrow = binding.backArrow;
        TextView title = binding.title;
        TextView note = binding.tvNote;
        TextView self = binding.self;
        TextView live = binding.live;
        EditText search = binding.searchCust;

        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();

        backArrow.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        live.setOnClickListener(view -> {
            // Your logic for the 'live' button click
        });

        if ("2".equalsIgnoreCase(SharedPref.getSfType(this))) {
            search.setVisibility(View.VISIBLE);
        } else {
            search.setVisibility(View.VISIBLE);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        VisitFilter visitFilter = new VisitFilter(masterDataDao);

        Map<String, VisitFilter.MonthlyStats> monthlyData = visitFilter.callFilter();


        try {
            VisitFilter.MonthlyStats currentMonthStats = monthlyData.get("current");
            VisitFilter.MonthlyStats previousMonthStats = monthlyData.get("previous");
            VisitFilter.MonthlyStats prePreviousMonthStats = monthlyData.get("prePrevious");


            // Example: Get the number of unique doctors visited in the current month
            int uniqueDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
            int uniqueDoctorsPreviousMonth = previousMonthStats.uniqueDoctors.size();
            int uniqueDoctorsPre_PrevMonth = prePreviousMonthStats.uniqueDoctors.size();

            int uniqueChemistCurrentMonth = currentMonthStats.uniqueChemists.size();
            int uniqueChemistPreviousMonth = previousMonthStats.uniqueChemists.size();
            int uniqueChemistPre_PrevMonth = prePreviousMonthStats.uniqueChemists.size();

            int uniqueStockiestCurrentMonth = currentMonthStats.uniqueStockiest.size();
            int uniqueStockiestPreviousMonth = previousMonthStats.uniqueStockiest.size();
            int uniqueStockiestPre_PrevMonth = previousMonthStats.uniqueStockiest.size();

            int uniqueUnlistedCurrentMonth = currentMonthStats.uniqueUnlisted.size();
            int uniqueUnlistedPreviousMonth = currentMonthStats.uniqueUnlisted.size();
            int uniqueUnlistedPre_PrevMonth = currentMonthStats.uniqueUnlisted.size();

            //total visits


            int visitedDoctorsCurrentMonth = currentMonthStats.visitedDoctors.size();
            int visitedDoctorsPreviousMonth = previousMonthStats.visitedDoctors.size();
            int visitedDoctorsPre_PrevMonth = prePreviousMonthStats.visitedDoctors.size();

            int visitedChemistCurrentMonth = currentMonthStats.visitedChemists.size();
            int visitedChemistPreviousMonth = previousMonthStats.visitedChemists.size();
            int visitedChemistPre_PrevMonth = prePreviousMonthStats.visitedChemists.size();

            int visitedStockiestCurrentMonth = currentMonthStats.visitedStockiest.size();
            int visitedStockiestPreviousMonth = previousMonthStats.visitedStockiest.size();
            int visitedStockiestPre_PrevMonth = previousMonthStats.visitedStockiest.size();

            int visitedUnlistedCurrentMonth = currentMonthStats.visitedUnlisted.size();
            int visitedUnlistedPreviousMonth = currentMonthStats.visitedUnlisted.size();
            int visitedUnlistedPre_PrevMonth = currentMonthStats.visitedUnlisted.size();

            String doctorData = masterDataDao.getDataByKey(Constants.DOCTOR_MAS + SharedPref.getHqCode(this));
            JSONArray doctorArray = new JSONArray(doctorData);
            int totalDoctors = doctorArray.length();

            String chemistData = masterDataDao.getDataByKey(Constants.CHEMIST_MAS + SharedPref.getHqCode(this));
            JSONArray chemistArray = new JSONArray(chemistData);
            int totalChemist = chemistArray.length();

            String stkData = masterDataDao.getDataByKey(Constants.STOCKIEST_MAS + SharedPref.getHqCode(this));
            JSONArray stkArray = new JSONArray(stkData);
            int totalStk = stkArray.length();

            String unlistedData = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_MAS + SharedPref.getHqCode(this));
            JSONArray unlistedArray = new JSONArray(unlistedData);
            int totalUnlisted = unlistedArray.length();

            //missed
            //Doc
            int currentMonthMissed = totalDoctors - uniqueDoctorsCurrentMonth;
            int previousMonthMissed = totalDoctors - uniqueDoctorsPreviousMonth;
            int prePreviousMonthMissed = totalDoctors - uniqueDoctorsPre_PrevMonth;
            //che
            int currentMonthMissedChe = totalChemist - uniqueChemistCurrentMonth;
            int previousMonthMissedChe = totalChemist - uniqueChemistPreviousMonth;
            int prePreviousMonthMissedChe = totalChemist - uniqueChemistPre_PrevMonth;
            //Stk
            int currentMonthMissedStk = totalStk - uniqueStockiestCurrentMonth;
            int previousMonthMissedStk = totalStk - uniqueStockiestPreviousMonth;
            int prePreviousMonthMissedStk = totalStk - uniqueStockiestPre_PrevMonth;
            //Unlist
            int currentMonthMissedUnlisted = totalUnlisted - uniqueUnlistedCurrentMonth;
            int previousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPreviousMonth;
            int prePreviousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPre_PrevMonth;

            //FWDays
            int fwDaysCurrentMonth = currentMonthStats.FWDays.size();
            int fwDaysPreviousMonth = previousMonthStats.FWDays.size();
            int fwDaysPrePreviousMonth = prePreviousMonthStats.FWDays.size();


            //Call Average

            //Doc
            double callAvgCurrentMonthDoc;
            double callAvgPreviousMonthDoc;
            double callAvgPrePrevMonthDoc;

            callAvgCurrentMonthDoc = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgCurrent = String.format("%.1f", callAvgCurrentMonthDoc);

            callAvgPreviousMonthDoc = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPrevious = String.format("%.1f", callAvgPreviousMonthDoc);

            callAvgPrePrevMonthDoc = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPre_Previous = String.format("%.1f", callAvgPrePrevMonthDoc);

            //Che
            double callAvgCurrentMonthChe;
            double callAvgPreviousMonthChe;
            double callAvgPrePrevMonthChe;

            callAvgCurrentMonthChe = (double) visitedChemistCurrentMonth / fwDaysCurrentMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgCurrentChe = String.format("%.1f", callAvgCurrentMonthChe);

            callAvgPreviousMonthChe = (double) visitedChemistPreviousMonth / fwDaysPreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPreviousChe = String.format("%.1f", callAvgPreviousMonthChe);

            callAvgPrePrevMonthChe = (double) visitedChemistPre_PrevMonth / fwDaysPrePreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPre_PreviousChe = String.format("%.1f", callAvgPrePrevMonthChe);

            //Stk
            double callAvgCurrentMonthStk;
            double callAvgPreviousMonthStk;
            double callAvgPrePrevMonthStk;

            callAvgCurrentMonthStk = (double) visitedStockiestCurrentMonth / fwDaysCurrentMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgCurrentStk = String.format("%.1f", callAvgCurrentMonthStk);

            callAvgPreviousMonthStk = (double) visitedStockiestPreviousMonth / fwDaysPreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPreviousStk = String.format("%.1f", callAvgPreviousMonthStk);

            callAvgPrePrevMonthStk = (double) visitedStockiestPre_PrevMonth / fwDaysPrePreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPre_PreviousStk = String.format("%.1f", callAvgPrePrevMonthStk);

            //Unlisted
            double callAvgCurrentMonthUnlisted;
            double callAvgPreviousMonthUnlisted;
            double callAvgPrePrevMonthUnlisted;

            callAvgCurrentMonthUnlisted = (double) visitedUnlistedCurrentMonth / fwDaysCurrentMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgCurrentUnlisted = String.format("%.1f", callAvgCurrentMonthUnlisted);

            callAvgPreviousMonthUnlisted = (double) visitedUnlistedPreviousMonth / fwDaysPreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPreviousUnlisted = String.format("%.1f", callAvgPreviousMonthUnlisted);

            callAvgPrePrevMonthUnlisted = (double) visitedUnlistedPre_PrevMonth / fwDaysPrePreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPre_PreviousUnlisted = String.format("%.1f", callAvgPrePrevMonthUnlisted);


            //Call Coverage
            //Doc

            double callCvgCurrentMonthDoc;
            double callCvgPreviousMonthDoc;
            double callCvgPrePrevMonthDoc;

            callCvgCurrentMonthDoc = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrent = String.format("%.1f", callCvgCurrentMonthDoc);

            callCvgPreviousMonthDoc = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPrevious = String.format("%.1f", callCvgPreviousMonthDoc);

            callCvgPrePrevMonthDoc = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_Previous = String.format("%.1f", callCvgPrePrevMonthDoc);

            //Che

            double callCvgCurrentMonthChe;
            double callCvgPreviousMonthChe;
            double callCvgPrePrevMonthChe;

            callCvgCurrentMonthChe = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentChe = String.format("%.1f", callCvgCurrentMonthChe);

            callCvgPreviousMonthChe = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousChe = String.format("%.1f", callCvgPreviousMonthChe);

            callCvgPrePrevMonthChe = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousChe = String.format("%.1f", callCvgPrePrevMonthChe);

            //Stk

            double callCvgCurrentMonthStk;
            double callCvgPreviousMonthStk;
            double callCvgPrePrevMonthStk;

            callCvgCurrentMonthStk = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentStk = String.format("%.1f", callCvgCurrentMonthStk);

            callCvgPreviousMonthStk = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousStk = String.format("%.1f", callCvgPreviousMonthStk);

            callCvgPrePrevMonthStk = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousStk = String.format("%.1f", callCvgPrePrevMonthStk);

            //Unlisted

            double callCvgCurrentMonthUnlisted;
            double callCvgPreviousMonthUnlisted;
            double callCvgPrePrevMonthUnlisted;

            callCvgCurrentMonthUnlisted = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentUnlisted = String.format("%.1f", callCvgCurrentMonthUnlisted);

            callCvgPreviousMonthUnlisted = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousUnlisted = String.format("%.1f", callCvgPreviousMonthUnlisted);

            callCvgPrePrevMonthUnlisted = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousUnlisted = String.format("%.1f", callCvgPrePrevMonthUnlisted);


            List<VisitStatsModel> dataList = new ArrayList<>();

            VisitStatsModel currentMonthStatsDoc = new VisitStatsModel(
                    String.valueOf(totalDoctors),
                    String.valueOf(uniqueDoctorsCurrentMonth),
                    String.valueOf(currentMonthMissed),
                    String.valueOf(fwDaysCurrentMonth),
                    formattedCallAvgCurrent,
                    formattedCallCvgCurrent,
                    currentMonthStats.oneVisitCount,
                    currentMonthStats.twoVisitCount,
                    currentMonthStats.threeVisitCount,
                    currentMonthStats.threePlusVisitCount
            );

            VisitStatsModel previousMonthStatsDoc = new VisitStatsModel(
                    String.valueOf(totalDoctors),
                    String.valueOf(uniqueDoctorsPreviousMonth),
                    String.valueOf(previousMonthMissed),
                    String.valueOf(previousMonthMissed),
                    formattedCallAvgPrevious,
                    formattedCallCvgPrevious,
                    previousMonthStats.oneVisitCount,
                    previousMonthStats.twoVisitCount,
                    previousMonthStats.threeVisitCount,
                    previousMonthStats.threePlusVisitCount
            );

            VisitStatsModel prePreviousMonthStatsDoc = new VisitStatsModel(
                    String.valueOf(totalDoctors),
                    String.valueOf(uniqueDoctorsPre_PrevMonth),
                    String.valueOf(prePreviousMonthMissed),
                    String.valueOf(fwDaysPrePreviousMonth),
                    formattedCallAvgPre_Previous,
                    formattedCallCvgPre_Previous,
                    prePreviousMonthStats.oneVisitCount,
                    prePreviousMonthStats.twoVisitCount,
                    prePreviousMonthStats.threeVisitCount,
                    prePreviousMonthStats.threePlusVisitCount
            );


            dataList.add(currentMonthStatsDoc);
            dataList.add(previousMonthStatsDoc);
            dataList.add(prePreviousMonthStatsDoc);

            VisitStatsAdapter adapter = new VisitStatsAdapter(dataList);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }



//        VisitFilter visitFilter = new VisitFilter(masterDataDao);
//
//        Map<String, VisitFilter.MonthlyStats> monthlyData = visitFilter.callFilter();


//        try {
//            VisitFilter.MonthlyStats currentMonthStats = monthlyData.get("current");
//            VisitFilter.MonthlyStats previousMonthStats = monthlyData.get("previous");
//            VisitFilter.MonthlyStats prePreviousMonthStats = monthlyData.get("prePrevious");
//
//
//            // Example: Get the number of unique doctors visited in the current month
//            int uniqueDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
//            int uniqueDoctorsPreviousMonth = previousMonthStats.uniqueDoctors.size();
//            int uniqueDoctorsPre_PrevMonth = prePreviousMonthStats.uniqueDoctors.size();
//
//            int uniqueChemistCurrentMonth = currentMonthStats.uniqueChemists.size();
//            int uniqueChemistPreviousMonth = previousMonthStats.uniqueChemists.size();
//            int uniqueChemistPre_PrevMonth = prePreviousMonthStats.uniqueChemists.size();
//
//            int uniqueStockiestCurrentMonth = currentMonthStats.uniqueStockiest.size();
//            int uniqueStockiestPreviousMonth = previousMonthStats.uniqueStockiest.size();
//            int uniqueStockiestPre_PrevMonth = previousMonthStats.uniqueStockiest.size();
//
//            int uniqueUnlistedCurrentMonth = currentMonthStats.uniqueUnlisted.size();
//            int uniqueUnlistedPreviousMonth = currentMonthStats.uniqueUnlisted.size();
//            int uniqueUnlistedPre_PrevMonth = currentMonthStats.uniqueUnlisted.size();
//
//            //total visits
//
//
//            int visitedDoctorsCurrentMonth = currentMonthStats.visitedDoctors.size();
//            int visitedDoctorsPreviousMonth = previousMonthStats.visitedDoctors.size();
//            int visitedDoctorsPre_PrevMonth = prePreviousMonthStats.visitedDoctors.size();
//
//            int visitedChemistCurrentMonth = currentMonthStats.visitedChemists.size();
//            int visitedChemistPreviousMonth = previousMonthStats.visitedChemists.size();
//            int visitedChemistPre_PrevMonth = prePreviousMonthStats.visitedChemists.size();
//
//            int visitedStockiestCurrentMonth = currentMonthStats.visitedStockiest.size();
//            int visitedStockiestPreviousMonth = previousMonthStats.visitedStockiest.size();
//            int visitedStockiestPre_PrevMonth = previousMonthStats.visitedStockiest.size();
//
//            int visitedUnlistedCurrentMonth = currentMonthStats.visitedUnlisted.size();
//            int visitedUnlistedPreviousMonth = currentMonthStats.visitedUnlisted.size();
//            int visitedUnlistedPre_PrevMonth = currentMonthStats.visitedUnlisted.size();
//
//            String doctorData = masterDataDao.getDataByKey(Constants.DOCTOR_MAS + SharedPref.getHqCode(this));
//            JSONArray doctorArray = new JSONArray(doctorData);
//            int totalDoctors = doctorArray.length();
//
//            String chemistData = masterDataDao.getDataByKey(Constants.CHEMIST_MAS + SharedPref.getHqCode(this));
//            JSONArray chemistArray = new JSONArray(chemistData);
//            int totalChemist = chemistArray.length();
//
//            String stkData = masterDataDao.getDataByKey(Constants.STOCKIEST_MAS + SharedPref.getHqCode(this));
//            JSONArray stkArray = new JSONArray(stkData);
//            int totalStk = stkArray.length();
//
//            String unlistedData = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_MAS + SharedPref.getHqCode(this));
//            JSONArray unlistedArray = new JSONArray(unlistedData);
//            int totalUnlisted = unlistedArray.length();
//
//            //missed
//            //Doc
//            int currentMonthMissed = totalDoctors - uniqueDoctorsCurrentMonth;
//            int previousMonthMissed = totalDoctors - uniqueDoctorsPreviousMonth;
//            int prePreviousMonthMissed = totalDoctors - uniqueDoctorsPre_PrevMonth;
//            //che
//            int currentMonthMissedChe = totalChemist - uniqueChemistCurrentMonth;
//            int previousMonthMissedChe = totalChemist - uniqueChemistPreviousMonth;
//            int prePreviousMonthMissedChe = totalChemist - uniqueChemistPre_PrevMonth;
//            //Stk
//            int currentMonthMissedStk = totalStk - uniqueStockiestCurrentMonth;
//            int previousMonthMissedStk = totalStk - uniqueStockiestPreviousMonth;
//            int prePreviousMonthMissedStk = totalStk - uniqueStockiestPre_PrevMonth;
//            //Unlist
//            int currentMonthMissedUnlisted = totalUnlisted - uniqueUnlistedCurrentMonth;
//            int previousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPreviousMonth;
//            int prePreviousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPre_PrevMonth;
//
//            //FWDays
//            int fwDaysCurrentMonth = currentMonthStats.FWDays.size();
//            int fwDaysPreviousMonth = previousMonthStats.FWDays.size();
//            int fwDaysPrePreviousMonth = prePreviousMonthStats.FWDays.size();
//
//
//            //Call Average
//
//            //Doc
//            double callAvgCurrentMonthDoc;
//            double callAvgPreviousMonthDoc;
//            double callAvgPrePrevMonthDoc;
//
//            callAvgCurrentMonthDoc = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale")   String formattedCallAvgCurrent = String.format("%.1f", callAvgCurrentMonthDoc);
//
//            callAvgPreviousMonthDoc = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgPrevious = String.format("%.1f", callAvgPreviousMonthDoc);
//
//            callAvgPrePrevMonthDoc = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgPre_Previous = String.format("%.1f", callAvgPrePrevMonthDoc);
//
//            //Che
//            double callAvgCurrentMonthChe;
//            double callAvgPreviousMonthChe;
//            double callAvgPrePrevMonthChe;
//
//            callAvgCurrentMonthChe = (double) visitedChemistCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgCurrentChe = String.format("%.1f", callAvgCurrentMonthChe);
//
//            callAvgPreviousMonthChe = (double) visitedChemistPreviousMonth / fwDaysPreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPreviousChe = String.format("%.1f", callAvgPreviousMonthChe);
//
//            callAvgPrePrevMonthChe = (double) visitedChemistPre_PrevMonth / fwDaysPrePreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPre_PreviousChe = String.format("%.1f", callAvgPrePrevMonthChe);
//
//            //Stk
//            double callAvgCurrentMonthStk;
//            double callAvgPreviousMonthStk;
//            double callAvgPrePrevMonthStk;
//
//            callAvgCurrentMonthStk = (double) visitedStockiestCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgCurrentStk = String.format("%.1f", callAvgCurrentMonthStk);
//
//            callAvgPreviousMonthStk = (double) visitedStockiestPreviousMonth / fwDaysPreviousMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgPreviousStk = String.format("%.1f", callAvgPreviousMonthStk);
//
//            callAvgPrePrevMonthStk = (double) visitedStockiestPre_PrevMonth / fwDaysPrePreviousMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgPre_PreviousStk = String.format("%.1f", callAvgPrePrevMonthStk);
//
//            //Unlisted
//            double callAvgCurrentMonthUnlisted;
//            double callAvgPreviousMonthUnlisted;
//            double callAvgPrePrevMonthUnlisted;
//
//            callAvgCurrentMonthUnlisted = (double) visitedUnlistedCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgCurrentUnlisted = String.format("%.1f", callAvgCurrentMonthUnlisted);
//
//            callAvgPreviousMonthUnlisted = (double) visitedUnlistedPreviousMonth / fwDaysPreviousMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgPreviousUnlisted = String.format("%.1f", callAvgPreviousMonthUnlisted);
//
//            callAvgPrePrevMonthUnlisted = (double) visitedUnlistedPre_PrevMonth / fwDaysPrePreviousMonth;
//            @SuppressLint("DefaultLocale")  String formattedCallAvgPre_PreviousUnlisted = String.format("%.1f", callAvgPrePrevMonthUnlisted);
//
//
//            //Call Coverage
//            //Doc
//
//            double callCvgCurrentMonthDoc;
//            double callCvgPreviousMonthDoc;
//            double callCvgPrePrevMonthDoc;
//
//            callCvgCurrentMonthDoc = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
//            @SuppressLint("DefaultLocale")   String formattedCallCvgCurrent = String.format("%.1f", callCvgCurrentMonthDoc);
//
//            callCvgPreviousMonthDoc = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPrevious = String.format("%.1f", callCvgPreviousMonthDoc);
//
//            callCvgPrePrevMonthDoc = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPre_Previous = String.format("%.1f", callCvgPrePrevMonthDoc);
//
//            //Che
//
//            double callCvgCurrentMonthChe;
//            double callCvgPreviousMonthChe;
//            double callCvgPrePrevMonthChe;
//
//            callCvgCurrentMonthChe = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
//            @SuppressLint("DefaultLocale")   String formattedCallCvgCurrentChe = String.format("%.1f", callCvgCurrentMonthChe);
//
//            callCvgPreviousMonthChe = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPreviousChe = String.format("%.1f", callCvgPreviousMonthChe);
//
//            callCvgPrePrevMonthChe = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPre_PreviousChe = String.format("%.1f", callCvgPrePrevMonthChe);
//
//            //Stk
//
//            double callCvgCurrentMonthStk;
//            double callCvgPreviousMonthStk;
//            double callCvgPrePrevMonthStk;
//
//            callCvgCurrentMonthStk = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
//            @SuppressLint("DefaultLocale")   String formattedCallCvgCurrentStk = String.format("%.1f", callCvgCurrentMonthStk);
//
//            callCvgPreviousMonthStk = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPreviousStk = String.format("%.1f", callCvgPreviousMonthStk);
//
//            callCvgPrePrevMonthStk = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPre_PreviousStk = String.format("%.1f", callCvgPrePrevMonthStk);
//
//            //Unlisted
//
//            double callCvgCurrentMonthUnlisted;
//            double callCvgPreviousMonthUnlisted;
//            double callCvgPrePrevMonthUnlisted;
//
//            callCvgCurrentMonthUnlisted = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
//            @SuppressLint("DefaultLocale")   String formattedCallCvgCurrentUnlisted = String.format("%.1f", callCvgCurrentMonthUnlisted);
//
//            callCvgPreviousMonthUnlisted = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPreviousUnlisted = String.format("%.1f", callCvgPreviousMonthUnlisted);
//
//            callCvgPrePrevMonthUnlisted = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale")  String formattedCallCvgPre_PreviousUnlisted = String.format("%.1f", callCvgPrePrevMonthUnlisted);
//
//            List<VisitStatsModel> dataList = new ArrayList<>();
//
//            VisitStatsModel currentMonthStatsDoc = new VisitStatsModel(
//                    String.valueOf(totalDoctors),
//                    String.valueOf(uniqueDoctorsCurrentMonth),
//                    String.valueOf(currentMonthMissed),
//                    String.valueOf(fwDaysCurrentMonth),
//                    formattedCallAvgCurrent,
//                    formattedCallCvgCurrent,"currentMonth"
//                   /* oneVisitCount_Cm,
//                    twoVisitCount_Cm,
//                    threeVisitCount_Cm,
//                    threePlusVisitCount_Cm*/
//
//            );
//
//            VisitStatsModel previousMonthStatsDoc = new VisitStatsModel(
//                    String.valueOf(totalDoctors),
//                    String.valueOf(uniqueDoctorsPreviousMonth),
//                    String.valueOf(previousMonthMissed),
//                    String.valueOf(previousMonthMissed),
//                    formattedCallAvgPrevious,
//                    formattedCallCvgPrevious,"previousMonth"
//                  /*  oneVisitCount_Pm,
//                    twoVisitCount_Pm,
//                    threeVisitCount_Pm,
//                    threePlusVisitCount_Pm*/
//            );
//
//            VisitStatsModel prePreviousMonthStatsDoc = new VisitStatsModel(
//                    String.valueOf(totalDoctors),
//                    String.valueOf(uniqueDoctorsPre_PrevMonth),
//                    String.valueOf(prePreviousMonthMissed),
//                    String.valueOf(fwDaysPrePreviousMonth),
//                    formattedCallAvgPre_Previous,
//                    formattedCallCvgPre_Previous,"pre_PreviousMonth"
//                   /* oneVisitCount_Ppm,
//                    twoVisitCount_Ppm,
//                    threeVisitCount_Ppm,
//                    threePlusVisitCount_Ppm*/
//            );
//
//
//            dataList.add(currentMonthStatsDoc);
//            dataList.add(previousMonthStatsDoc);
//            dataList.add(prePreviousMonthStatsDoc);
////            DoctorStatsAdapter adapter = new DoctorStatsAdapter(dataList);
/*            VisitStatsAdapter adapter = new VisitStatsAdapter(dataList);
            recyclerView.setAdapter(adapter);*/
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }
}
