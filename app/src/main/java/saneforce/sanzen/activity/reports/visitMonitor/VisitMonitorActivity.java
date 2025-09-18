package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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

import saneforce.sanzen.activity.reports.visitMonitor.adapter.ReportPagerAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.VisitStatsAdapter;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.model.ChemistStatsModel;
import saneforce.sanzen.activity.reports.visitMonitor.model.DoctorStatsModel;
import saneforce.sanzen.activity.reports.visitMonitor.model.StockiestStatsModel;
import saneforce.sanzen.activity.reports.visitMonitor.model.UnlistedStatsModel;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

/*
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
        TextView name = binding.headerCustName;
        TextView hq   = binding.headerCustHq;
        TextView desig= binding.headerCustDesig;
        EditText search = binding.searchCust;

        name.setText(SharedPref.getSfName(this));
        hq.setText(SharedPref.getHqName(this));
        desig.setText(SharedPref.getDesig(this));

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

        if (savedInstanceState == null) {
            DoctorVisitFragment doctorFragment = new DoctorVisitFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.doctor_fragment_container, doctorFragment);
            fragmentTransaction.commit();

            ChemistVisitFragment chemistFragment = new ChemistVisitFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
            fragmentTransaction1.replace(R.id.doctor_fragment_container, chemistFragment);
            fragmentTransaction1.commit();

            StockiestVisitFragment StockiestFragment = new StockiestVisitFragment();
            FragmentManager fragmentManager2 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
            fragmentTransaction2.replace(R.id.doctor_fragment_container, StockiestFragment);
            fragmentTransaction2.commit();

            UnlistedVisitFragment UnlistedFragment = new UnlistedVisitFragment();
            FragmentManager fragmentManager3 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
            fragmentTransaction3.replace(R.id.doctor_fragment_container, UnlistedFragment);
            fragmentTransaction3.commit();
        }
*/

   /*     RecyclerView recyclerView = findViewById(R.id.recyclerView);
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
*/
       /*     List<VisitStatsModel> dataList = new ArrayList<>();

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

            VisitStatsAdapter adapter = new VisitStatsAdapter(dataList, (List<String>) monthlyData);
            recyclerView.setAdapter(adapter);*/

       /* } catch (Exception e) {
            e.printStackTrace();
        *///}


//    }
//}
public class VisitMonitorActivity extends AppCompatActivity {
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    ActivityVisitMonitorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisitMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayout backArrow = binding.backArrow;

        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        TextView name = binding.headerCustName;
        TextView hq = binding.headerCustHq;
        TextView desig = binding.headerCustDesig;
        name.setText(SharedPref.getSfName(this));
        hq.setText(SharedPref.getHqName(this));
        desig.setText(SharedPref.getDesig(this));
        backArrow.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


        custFilter();
    }

    public void custFilter() {
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


            int visitedDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
            int visitedDoctorsPreviousMonth = previousMonthStats.uniqueDoctors.size();
            int visitedDoctorsPre_PrevMonth = prePreviousMonthStats.uniqueDoctors.size();

            int visitedChemistCurrentMonth = currentMonthStats.uniqueChemists.size();
            int visitedChemistPreviousMonth = previousMonthStats.uniqueChemists.size();
            int visitedChemistPre_PrevMonth = prePreviousMonthStats.uniqueChemists.size();

            int visitedStockiestCurrentMonth = currentMonthStats.uniqueStockiest.size();
            int visitedStockiestPreviousMonth = previousMonthStats.uniqueStockiest.size();
            int visitedStockiestPre_PrevMonth = previousMonthStats.uniqueStockiest.size();

            int visitedUnlistedCurrentMonth = currentMonthStats.uniqueUnlisted.size();
            int visitedUnlistedPreviousMonth = currentMonthStats.uniqueUnlisted.size();
            int visitedUnlistedPre_PrevMonth = currentMonthStats.uniqueUnlisted.size();

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

            //Doctor
            List<VisitStatsModel> dataListDoc = new ArrayList<>();
            VisitStatsModel currentMonthDrStats = new VisitStatsModel(
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

            VisitStatsModel previousMonthDrStats = new VisitStatsModel(
                    String.valueOf(totalDoctors),
                    String.valueOf(uniqueDoctorsPreviousMonth),
                    String.valueOf(previousMonthMissed),
                    String.valueOf(fwDaysPreviousMonth),
                    formattedCallAvgPrevious,
                    formattedCallCvgPrevious,
                    previousMonthStats.oneVisitCount,
                    previousMonthStats.twoVisitCount,
                    previousMonthStats.threeVisitCount,
                    previousMonthStats.threePlusVisitCount
            );

            VisitStatsModel prePreviousMonthDrStats = new VisitStatsModel(
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

            //Chemist
            List<VisitStatsModel> dataListChm = new ArrayList<>();
            VisitStatsModel currentMonthCheStats = new VisitStatsModel(
                    String.valueOf(totalChemist),
                    String.valueOf(uniqueChemistCurrentMonth),
                    String.valueOf(currentMonthMissedChe),
                    String.valueOf(fwDaysCurrentMonth),
                    formattedCallAvgCurrentChe,
                    formattedCallCvgCurrentChe

            );

            VisitStatsModel previousMonthCheStats = new VisitStatsModel(
                    String.valueOf(totalChemist),
                    String.valueOf(uniqueChemistPreviousMonth),
                    String.valueOf(previousMonthMissedChe),
                    String.valueOf(fwDaysPreviousMonth),
                    formattedCallAvgPreviousChe,
                    formattedCallCvgPreviousChe

            );

            VisitStatsModel prePreviousMonthCheStats = new VisitStatsModel(
                    String.valueOf(totalChemist),
                    String.valueOf(uniqueChemistPre_PrevMonth),
                    String.valueOf(prePreviousMonthMissedChe),
                    String.valueOf(fwDaysPrePreviousMonth),
                    formattedCallAvgPre_PreviousChe,
                    formattedCallCvgPre_PreviousChe
            );

            //Stk
            List<VisitStatsModel> dataListStk = new ArrayList<>();
            VisitStatsModel currentMonthStkStats = new VisitStatsModel(
                    String.valueOf(totalStk),
                    String.valueOf(uniqueStockiestCurrentMonth),
                    String.valueOf(currentMonthMissedStk),
                    String.valueOf(fwDaysCurrentMonth),
                    formattedCallAvgCurrentStk,
                    formattedCallCvgCurrentStk

            );

            VisitStatsModel previousMonthStkStats = new VisitStatsModel(
                    String.valueOf(totalStk),
                    String.valueOf(uniqueStockiestPreviousMonth),
                    String.valueOf(previousMonthMissedStk),
                    String.valueOf(fwDaysPreviousMonth),
                    formattedCallAvgPreviousStk,
                    formattedCallCvgPreviousStk

            );

            VisitStatsModel prePreviousMonthStkStats = new VisitStatsModel(
                    String.valueOf(totalStk),
                    String.valueOf(uniqueStockiestPre_PrevMonth),
                    String.valueOf(prePreviousMonthMissedStk),
                    String.valueOf(fwDaysPrePreviousMonth),
                    formattedCallAvgPre_PreviousStk,
                    formattedCallCvgPre_PreviousStk
            );

            //unlisted
            List<VisitStatsModel> dataListUnlisted = new ArrayList<>();
            VisitStatsModel currentMonthUnlistedStats = new VisitStatsModel(
                    String.valueOf(totalUnlisted),
                    String.valueOf(uniqueUnlistedCurrentMonth),
                    String.valueOf(currentMonthMissedUnlisted),
                    String.valueOf(fwDaysCurrentMonth),
                    formattedCallAvgCurrentUnlisted,
                    formattedCallCvgCurrentUnlisted

            );

            VisitStatsModel previousMonthUnlistedStats = new VisitStatsModel(
                    String.valueOf(totalUnlisted),
                    String.valueOf(uniqueUnlistedPreviousMonth),
                    String.valueOf(previousMonthMissedUnlisted),
                    String.valueOf(fwDaysPreviousMonth),
                    formattedCallAvgPreviousUnlisted,
                    formattedCallCvgPreviousUnlisted

            );

            VisitStatsModel prePreviousMonthUnlistedStats = new VisitStatsModel(
                    String.valueOf(totalUnlisted),
                    String.valueOf(uniqueUnlistedPre_PrevMonth),
                    String.valueOf(prePreviousMonthMissedUnlisted),
                    String.valueOf(fwDaysPrePreviousMonth),
                    formattedCallAvgPre_PreviousUnlisted,
                    formattedCallCvgPre_PreviousUnlisted
            );
            //Dr
            dataListDoc.add(currentMonthDrStats);
            dataListDoc.add(previousMonthDrStats);
            dataListDoc.add(prePreviousMonthDrStats);
            //Che
            dataListChm.add(currentMonthCheStats);
            dataListChm.add(previousMonthCheStats);
            dataListChm.add(prePreviousMonthCheStats);
            //stk
            dataListStk.add(currentMonthStkStats);
            dataListStk.add(previousMonthStkStats);
            dataListStk.add(prePreviousMonthStkStats);
            //unlisted
            dataListUnlisted.add(currentMonthUnlistedStats);
            dataListUnlisted.add(previousMonthUnlistedStats);
            dataListUnlisted.add(prePreviousMonthUnlistedStats);


            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            Log.w("DEBUG", "RecyclerView object = " + recyclerView);

            VisitStatsAdapter adapter = new VisitStatsAdapter(
                    VisitMonitorActivity.this, // FragmentActivity
//                    monthData,
                    dataListDoc,
                    dataListChm,
                    dataListStk,
                    dataListUnlisted
            );
            Log.w("DEBUG", "Adapter created = " + adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
