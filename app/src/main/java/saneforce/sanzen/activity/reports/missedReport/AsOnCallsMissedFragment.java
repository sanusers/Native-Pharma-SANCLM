package saneforce.sanzen.activity.reports.missedReport;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.VisitFilter;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;


public class AsOnCallsMissedFragment extends Fragment {
    private RoomDB roomDB;
    MasterDataDao masterDataDao;
    private RecyclerView recyclerView;
    private String sfCode;
    private String date;
    int uniqueDoctorsCurrentMonth, totalDoctors, currentMonthMissed;
    int visitedDoctorsCurrentMonth;
    int previousMonthMissed, visitedDoctorsPreviousMonth, prePreviousMonthMissed, uniqueDoctorsPreviousMonth;
    //chemist
    int uniqueChemistCurrentMonth, totalChemist, currentMonthMissedChe;
    int visitedChemistCurrentMonth;
    int previousMonthMissedChe, visitedChemistPreviousMonth, prePreviousMonthMissedChe, uniqueChemistPreviousMonth;
    //stockiest
    int uniqueStockistCurrentMonth, totalStockist, currentMonthMissedStk;
    int visitedStockistCurrentMonth;
    int previousMonthMissedStk, visitedStockistPreviousMonth, prePreviousMonthMissedStk, uniqueStockistPreviousMonth;
    //unlisted
    int uniqueUnlistedCurrentMonth,totalUnlisted,currentMonthMissedUnlisted;
    int visitedUnlistedCurrentMonth;
    int previousMonthMissedUnlisted,visitedUnlistedPreviousMonth,prePreviousMonthMissedUnlisted,uniqueUnlistedPreviousMonth;
    int uniqueDoctorsPre_PrevMonth,uniqueChemistPre_PrevMonth,uniqueStockiestPre_PrevMonth,uniqueUnlistedPre_PrevMonth,visitedDoctorsPre_PrevMonth,visitedChemistPre_PrevMonth,visitedStockistPre_PrevMonth,visitedUnlistedPre_PrevMonth;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_as_on_calls_missed, container, false);
        View v = inflater.inflate(R.layout.fragment_as_on_calls_missed, container, false);
        sfCode = SharedPref.getSfCode(requireContext());
        date = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5);
        TextView name = v.findViewById(R.id.Name);
        name.setText(SharedPref.getSfName(requireContext()) + " - " + SharedPref.getDsName(requireContext()) + " - " + SharedPref.getHqNameMain(requireContext()));

        recyclerView = v.findViewById(R.id.recyclerDoctorMissedReports);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();

        custFilter();
        return v;

    }
    public void custFilter() {
        VisitFilter visitFilter = new VisitFilter(masterDataDao);
        Map<String, VisitFilter.MonthlyStats> monthlyData = visitFilter.callFilter();


        try {

            VisitFilter.MonthlyStats currentMonthStats = monthlyData.get("current");
            VisitFilter.MonthlyStats previousMonthStats = monthlyData.get("previous");
            VisitFilter.MonthlyStats prePreviousMonthStats = monthlyData.get("prePrevious");


            uniqueDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
            uniqueDoctorsPreviousMonth = previousMonthStats.uniqueDoctors.size();
             uniqueDoctorsPre_PrevMonth = prePreviousMonthStats.uniqueDoctors.size();

            uniqueChemistCurrentMonth = currentMonthStats.uniqueChemists.size();
            uniqueChemistPreviousMonth = previousMonthStats.uniqueChemists.size();
            uniqueChemistPre_PrevMonth = prePreviousMonthStats.uniqueChemists.size();

            uniqueStockistCurrentMonth = currentMonthStats.uniqueStockiest.size();
            uniqueStockistPreviousMonth = previousMonthStats.uniqueStockiest.size();
            uniqueStockiestPre_PrevMonth = prePreviousMonthStats.uniqueStockiest.size();

            uniqueUnlistedCurrentMonth = currentMonthStats.uniqueUnlisted.size();
            uniqueUnlistedPreviousMonth = previousMonthStats.uniqueUnlisted.size();
            uniqueUnlistedPre_PrevMonth = prePreviousMonthStats.uniqueUnlisted.size();

            //added total doctor

            totalDoctors = 0;
            visitedDoctorsPreviousMonth = 0;
             visitedDoctorsPre_PrevMonth = 0;


            //added total visit
            totalChemist = 0;
            visitedChemistPreviousMonth = 0;
            visitedChemistPre_PrevMonth = 0;

            //added totalstockiest
            totalStockist = 0;
            visitedStockistPreviousMonth = 0;
            visitedStockistPre_PrevMonth = 0;

            //total unlisted
            totalUnlisted = 0;
            visitedUnlistedPreviousMonth = 0;
            visitedUnlistedPre_PrevMonth = 0;

            visitedDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
            visitedDoctorsPreviousMonth = previousMonthStats.uniqueDoctors.size();
            visitedDoctorsPre_PrevMonth = prePreviousMonthStats.uniqueDoctors.size();

            visitedChemistCurrentMonth = currentMonthStats.uniqueChemists.size();
            visitedChemistPreviousMonth = previousMonthStats.uniqueChemists.size();
            visitedChemistPre_PrevMonth = prePreviousMonthStats.uniqueChemists.size();

            visitedStockistCurrentMonth = currentMonthStats.uniqueStockiest.size();
            visitedStockistPreviousMonth = previousMonthStats.uniqueStockiest.size();
            visitedStockistPre_PrevMonth = prePreviousMonthStats.uniqueStockiest.size();

            visitedUnlistedCurrentMonth = currentMonthStats.uniqueUnlisted.size();
            visitedUnlistedPreviousMonth = previousMonthStats.uniqueUnlisted.size();
            visitedUnlistedPre_PrevMonth = prePreviousMonthStats.uniqueUnlisted.size();


            //doctor
            String doctorData = masterDataDao.getDataByKey(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray doctorArray = new JSONArray(doctorData);
            totalDoctors = doctorArray.length();

            //chemist
            String chemistData = masterDataDao.getDataByKey(Constants.CHEMIST_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray chemistArray = new JSONArray(chemistData);
            totalChemist = chemistArray.length();

            //stockiest
            String stkData = masterDataDao.getDataByKey(Constants.STOCKIEST_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray stkArray = new JSONArray(stkData);
            int totalStk = stkArray.length();

            //unlisted
            String unlistedData = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray unlistedArray = new JSONArray(unlistedData);
            int totalUnlisted = unlistedArray.length();


            //missed
            //Doc
            currentMonthMissed = totalDoctors - uniqueDoctorsCurrentMonth;
            previousMonthMissed = totalDoctors - uniqueDoctorsPreviousMonth;
            prePreviousMonthMissed = totalDoctors - uniqueDoctorsPre_PrevMonth;
            //che
            currentMonthMissedChe = totalChemist - uniqueChemistCurrentMonth;
            previousMonthMissedChe = totalChemist - uniqueChemistPreviousMonth;
            prePreviousMonthMissedChe = totalChemist - uniqueChemistPre_PrevMonth;
            //Stk
            currentMonthMissedStk = totalStk - uniqueStockistCurrentMonth;
            previousMonthMissedStk = totalStk - uniqueStockistPreviousMonth;
            prePreviousMonthMissedStk = totalStk - uniqueStockiestPre_PrevMonth;
            //Unlist
            currentMonthMissedUnlisted = totalUnlisted - uniqueUnlistedCurrentMonth;
            previousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPreviousMonth;
            prePreviousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPre_PrevMonth;

            //FWDays
            int fwDaysCurrentMonth = currentMonthStats.FWDays.size();
            int fwDaysPreviousMonth = previousMonthStats.FWDays.size();
            int fwDaysPrePreviousMonth = prePreviousMonthStats.FWDays.size();


            //missed


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

            callAvgCurrentMonthStk = (double) visitedStockistCurrentMonth / fwDaysCurrentMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgCurrentStk = String.format("%.1f", callAvgCurrentMonthStk);

            callAvgPreviousMonthStk = (double) visitedStockistPreviousMonth / fwDaysPreviousMonth;
            @SuppressLint("DefaultLocale") String formattedCallAvgPreviousStk = String.format("%.1f", callAvgPreviousMonthStk);

            callAvgPrePrevMonthStk = (double) visitedStockistPre_PrevMonth / fwDaysPrePreviousMonth;
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
            List<String> monthData = new ArrayList<>();


            //Doctor
            List<MissedStatsModel> dataList = new ArrayList<>();
            MissedStatsModel currentMonthDrStats = new MissedStatsModel(
                    "1",
                    doctorArray,
                    currentMonthStats.uniqueDoctors);

//                    String.valueOf(totalDoctors),
//                    String.valueOf(uniqueDoctorsCurrentMonth),
//                    String.valueOf(currentMonthMissed),
//                    String.valueOf(fwDaysCurrentMonth),
//                    formattedCallAvgCurrent,
//                    formattedCallCvgCurrent


            MissedStatsModel previousMonthDrStats = new MissedStatsModel(
                    "1",
                    doctorArray,
                    previousMonthStats.uniqueDoctors);
//                    String.valueOf(totalDoctors),
//                    String.valueOf(uniqueDoctorsPreviousMonth),
//                    String.valueOf(previousMonthMissed),
//                    String.valueOf(fwDaysPreviousMonth),
//                    formattedCallAvgPrevious,
//                    formattedCallCvgPrevious
//
//            );

            MissedStatsModel prePreviousMonthDrStats = new MissedStatsModel(
                    "1",
                    doctorArray,
                    prePreviousMonthStats.uniqueDoctors);
//                    String.valueOf(totalDoctors),
//                    String.valueOf(uniqueDoctorsPre_PrevMonth),
//                    String.valueOf(prePreviousMonthMissed),
//                    String.valueOf(fwDaysPrePreviousMonth),
//                    formattedCallAvgPre_Previous,
//                    formattedCallCvgPre_Previous
//
//            );

            //Chemist
            MissedStatsModel currentMonthCheStats = new MissedStatsModel(
                    "2",
                    chemistArray,
                    currentMonthStats.uniqueChemists);
//                    String.valueOf(totalChemist),
//                    String.valueOf(uniqueChemistCurrentMonth),
//                    String.valueOf(currentMonthMissed),
//                    String.valueOf(fwDaysCurrentMonth),
//                    formattedCallAvgCurrentChe,
//                    formattedCallCvgCurrentChe
//
//            );

            MissedStatsModel previousMonthCheStats = new MissedStatsModel(
                    "2",
                    chemistArray,
                    previousMonthStats.uniqueChemists);
//                    String.valueOf(totalChemist),
//                    String.valueOf(uniqueChemistPreviousMonth),
//                    String.valueOf(previousMonthMissed),
//                    String.valueOf(fwDaysPreviousMonth),
//                    formattedCallAvgPreviousChe,
//                    formattedCallCvgPreviousChe
//
//            );

            MissedStatsModel prePreviousMonthCheStats = new MissedStatsModel(
                    "2",
                    chemistArray,
                    prePreviousMonthStats.uniqueChemists);
//                    String.valueOf(totalChemist),
//                    String.valueOf(uniqueChemistPre_PrevMonth),
//                    String.valueOf(prePreviousMonthMissed),
//                    String.valueOf(fwDaysPrePreviousMonth),
//                    formattedCallAvgPre_PreviousChe,
//                    formattedCallCvgPre_PreviousChe
//            );

            //Stk

            MissedStatsModel currentMonthStkStats = new MissedStatsModel(
                    "3",
                    stkArray,
                    currentMonthStats.uniqueStockiest);
//                    String.valueOf(totalStk),
//                    String.valueOf(uniqueStockiestCurrentMonth),
//                    String.valueOf(currentMonthMissed),
//                    String.valueOf(fwDaysCurrentMonth),
//                    formattedCallAvgCurrentStk,
//                    formattedCallCvgCurrentStk
//            );

            MissedStatsModel previousMonthStkStats = new MissedStatsModel(
                    "3",
                    stkArray,
                    previousMonthStats.uniqueStockiest);
//                    String.valueOf(totalStk),
//                    String.valueOf(uniqueStockiestPreviousMonth),
//                    String.valueOf(previousMonthMissed),
//                    String.valueOf(fwDaysPreviousMonth),
//                    formattedCallAvgPreviousStk,
//                    formattedCallCvgPreviousStk
//
//            );

            MissedStatsModel prePreviousMonthStkStats = new MissedStatsModel(
                    "3",
                    stkArray,
                    prePreviousMonthStats.uniqueStockiest);
//                    String.valueOf(totalStk),
//                    String.valueOf(uniqueStockiestPre_PrevMonth),
//                    String.valueOf(prePreviousMonthMissed),
//                    String.valueOf(fwDaysPrePreviousMonth),
//                    formattedCallAvgPre_PreviousStk,
//                    formattedCallCvgPre_PreviousStk
//            );

            //unlisted
            MissedStatsModel currentMonthUnlistedStats = new MissedStatsModel(
                    "4",
                    unlistedArray,
                    currentMonthStats.uniqueUnlisted);
//                    String.valueOf(totalUnlisted),
//                    String.valueOf(uniqueUnlistedCurrentMonth),
//                    String.valueOf(currentMonthMissed),
//                    String.valueOf(fwDaysCurrentMonth),
//                    formattedCallAvgCurrentUnlisted,
//                    formattedCallCvgCurrentUnlisted
//
//            );

            MissedStatsModel previousMonthUnlistedStats = new MissedStatsModel(
                    "4",
                    unlistedArray,
                    previousMonthStats.uniqueUnlisted);
//                    String.valueOf(totalUnlisted),
//                    String.valueOf(uniqueUnlistedPreviousMonth),
//                    String.valueOf(previousMonthMissed),
//                    String.valueOf(fwDaysPreviousMonth),
//                    formattedCallAvgPreviousUnlisted,
//                    formattedCallCvgPreviousUnlisted
//
//            );

            MissedStatsModel prePreviousMonthUnlistedStats = new MissedStatsModel(
                    "4",
                    unlistedArray,
                    prePreviousMonthStats.uniqueUnlisted);
//                    String.valueOf(totalUnlisted),
//                    String.valueOf(uniqueUnlistedPre_PrevMonth),
//                    String.valueOf(prePreviousMonthMissed),
//                    String.valueOf(fwDaysPrePreviousMonth),
//                    formattedCallAvgPre_PreviousUnlisted,
//                    formattedCallCvgPre_PreviousUnlisted
//            );

            //Dr
            dataList.add(currentMonthDrStats);
            dataList.add(previousMonthDrStats);
            dataList.add(prePreviousMonthDrStats);
            //Che
            dataList.add(currentMonthCheStats);
            dataList.add(previousMonthCheStats);
            dataList.add(prePreviousMonthCheStats);
            //stk
            dataList.add(currentMonthStkStats);
            dataList.add(previousMonthStkStats);
            dataList.add(prePreviousMonthStkStats);
            //unlisted
            dataList.add(currentMonthUnlistedStats);
            dataList.add(previousMonthUnlistedStats);
            dataList.add(prePreviousMonthUnlistedStats);
            // Example monthData

       /*     InnerAdapter adapter = new InnerAdapter( MissedReportGraph.this,
                    dataListDoc,
                    dataListChm,
                    dataListStk,
                    dataListUnlisted
            );
            Log.w("DEBUG", "Adapter created = " + adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);*/

            //current month
            List<MissedStatsModel> currentMonthList = new ArrayList<>();
            currentMonthList.add(currentMonthDrStats);
            currentMonthList.add(currentMonthCheStats);
            currentMonthList.add(currentMonthStkStats);
            currentMonthList.add(currentMonthUnlistedStats);

            //previous month
            List<MissedStatsModel> previousMonthList = new ArrayList<>();
            previousMonthList.add(previousMonthDrStats);
            previousMonthList.add(previousMonthCheStats);
            previousMonthList.add(previousMonthStkStats);
            previousMonthList.add(previousMonthUnlistedStats);

            //pre-previous month
            List<MissedStatsModel> prePreviousMonthList = new ArrayList<>();
            prePreviousMonthList.add(prePreviousMonthDrStats);
            prePreviousMonthList.add(prePreviousMonthCheStats);
            prePreviousMonthList.add(prePreviousMonthStkStats);
            prePreviousMonthList.add(prePreviousMonthUnlistedStats);


//   all months into  list
            List<List<MissedStatsModel>> allMonthsFlatList = new ArrayList<>();
            allMonthsFlatList.add(currentMonthList);
            allMonthsFlatList.add(previousMonthList);
            allMonthsFlatList.add(prePreviousMonthList);


            OuterAdapter adapter = new OuterAdapter(requireContext(), allMonthsFlatList, sfCode, date);
            //RecyclerView recyclerView = findViewById(R.id.recyclerDoctor);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    }
