package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import saneforce.sanzen.activity.reports.missedReport.MissedReportItem;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.VisitStatsAdapter;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class VisitMonitorActivity extends AppCompatActivity {
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    ActivityVisitMonitorBinding binding;
    String selectedTab, AsOnCalls,ApprovedCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVisitMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayout backArrow = binding.backArrow;

        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();



        binding.note.findViewById(R.id.note);

        backArrow.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


        if(SharedPref.getSfType(VisitMonitorActivity.this).equalsIgnoreCase("2")){
            binding.self.setVisibility(View.GONE);
            binding.tvNote.setText("You Are Live! Select Month to get Data");
            loadFragment(new ApprovedCallsFragment());
        }else{
            binding.self.setVisibility(View.VISIBLE);
            loadFragment(new AsOnCallsFragment());
        }


        binding.self.setOnClickListener(view -> {
            selectedTab = "As on Calls";
            binding.note.setVisibility(View.VISIBLE);
            binding.tvNote.setText("Sync to get Live Data!");
            updateReportUi();
            loadFragment(new AsOnCallsFragment());
        });

        binding.live.setOnClickListener(view -> {
            selectedTab = "Approved Calls";
            binding.note.setVisibility(View.VISIBLE);
            binding.tvNote.setText("You Are Live! Select Month to get Data");
            updateReportUi();
            loadFragment(new ApprovedCallsFragment());
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void updateReportUi(){
        binding.self.setBackground(null);
        binding.live.setBackground(null);

        binding.self.setTextColor(getColor(R.color.dark_purple));
        binding.live.setTextColor(getColor(R.color.dark_purple));

        switch (selectedTab){
            case "As on Calls":
                binding.self.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                binding.self.setTextColor(getColor(R.color.white));
                break;
            case "Approved Calls":
                binding.live.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                binding.live.setTextColor(getColor(R.color.white));
                break;
        }

    }
   /* public void custFilter() {
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
                    monthlyData.size(),VisitMonitorActivity.this, // FragmentActivity
                    dataListDoc,
                    dataListChm,
                    dataListStk,
                    dataListUnlisted,this
            );
            Log.w("DEBUG", "Adapter created = " + adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
