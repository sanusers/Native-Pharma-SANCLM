package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.VisitStatsAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class AsOnCallsFragment extends Fragment {

    private RoomDB roomDB;
    MasterDataDao masterDataDao;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_as_on_calls, container, false);
        TextView name = v.findViewById(R.id.headerCustName);
        TextView hq = v.findViewById(R.id.headerCustHq);
        TextView desig = v.findViewById(R.id.headerCustDesig);

        name.setText(SharedPref.getSfName(requireContext()));
        hq.setText(SharedPref.getHqName(requireContext()));
        desig.setText(SharedPref.getDesig(requireContext()));

        recyclerView = v.findViewById(R.id.recyclerView);
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


            // Example: Get the number of unique doctors visited in the current month
            int uniqueDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
            Log.d("TAG", "UniqueDr Cur Month: "+uniqueDoctorsCurrentMonth);
            int uniqueDoctorsPreviousMonth = previousMonthStats.uniqueDoctors.size();
            int uniqueDoctorsPre_PrevMonth = prePreviousMonthStats.uniqueDoctors.size();

            int uniqueChemistCurrentMonth = currentMonthStats.uniqueChemists.size();
            Log.d("TAG", "UniqueChe Cur Month: "+uniqueChemistCurrentMonth);
            int uniqueChemistPreviousMonth = previousMonthStats.uniqueChemists.size();
            int uniqueChemistPre_PrevMonth = prePreviousMonthStats.uniqueChemists.size();

            int uniqueStockiestCurrentMonth = currentMonthStats.uniqueStockiest.size();
            Log.d("TAG", "UniqueStk Cur Month: "+uniqueStockiestCurrentMonth);
            int uniqueStockiestPreviousMonth = previousMonthStats.uniqueStockiest.size();
            int uniqueStockiestPre_PrevMonth = previousMonthStats.uniqueStockiest.size();

            int uniqueUnlistedCurrentMonth = currentMonthStats.uniqueUnlisted.size();
            Log.d("TAG", "UniqueUnlist Cur Month: "+uniqueUnlistedCurrentMonth);
            int uniqueUnlistedPreviousMonth = currentMonthStats.uniqueUnlisted.size();
            int uniqueUnlistedPre_PrevMonth = currentMonthStats.uniqueUnlisted.size();

            //total visits


            int visitedDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
            Log.d("TAG", "visitedDr Cur Month: "+visitedDoctorsCurrentMonth);
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

            String doctorData = masterDataDao.getDataByKey(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray doctorArray = new JSONArray(doctorData);
            int totalDoctors = doctorArray.length();

            String chemistData = masterDataDao.getDataByKey(Constants.CHEMIST_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray chemistArray = new JSONArray(chemistData);
            int totalChemist = chemistArray.length();

            String stkData = masterDataDao.getDataByKey(Constants.STOCKIEST_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray stkArray = new JSONArray(stkData);
            int totalStk = stkArray.length();

            String unlistedData = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_MAS + SharedPref.getHqCode(requireContext()));
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

            callCvgCurrentMonthDoc =  (double) visitedDoctorsCurrentMonth / (fwDaysCurrentMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrent = String.format("%.2f", callCvgCurrentMonthDoc);

            callCvgPreviousMonthDoc = (double) visitedDoctorsPreviousMonth / (fwDaysPreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPrevious = String.format("%.2f", callCvgPreviousMonthDoc);

            callCvgPrePrevMonthDoc = (double) visitedDoctorsPre_PrevMonth / (fwDaysPrePreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_Previous = String.format("%.2f", callCvgPrePrevMonthDoc);

            //Che

            double callCvgCurrentMonthChe;
            double callCvgPreviousMonthChe;
            double callCvgPrePrevMonthChe;

            callCvgCurrentMonthChe = (double) visitedChemistCurrentMonth / (fwDaysCurrentMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentChe = String.format("%.2f", callCvgCurrentMonthChe);

            callCvgPreviousMonthChe = (double) visitedChemistPreviousMonth / (fwDaysPreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousChe = String.format("%.2f", callCvgPreviousMonthChe);

            callCvgPrePrevMonthChe = (double) visitedChemistPre_PrevMonth / (fwDaysPrePreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousChe = String.format("%.2f", callCvgPrePrevMonthChe);

            //Stk

            double callCvgCurrentMonthStk;
            double callCvgPreviousMonthStk;
            double callCvgPrePrevMonthStk;

            callCvgCurrentMonthStk = (double) visitedStockiestCurrentMonth / (fwDaysCurrentMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentStk = String.format("%.2f", callCvgCurrentMonthStk);

            callCvgPreviousMonthStk = (double) visitedStockiestPreviousMonth / (fwDaysPreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousStk = String.format("%.2f", callCvgPreviousMonthStk);

            callCvgPrePrevMonthStk = (double) visitedStockiestPre_PrevMonth / (fwDaysPrePreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousStk = String.format("%.2f", callCvgPrePrevMonthStk);

            //Unlisted

            double callCvgCurrentMonthUnlisted;
            double callCvgPreviousMonthUnlisted;
            double callCvgPrePrevMonthUnlisted;

            callCvgCurrentMonthUnlisted = (double) visitedUnlistedCurrentMonth / (fwDaysCurrentMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentUnlisted = String.format("%.2f", callCvgCurrentMonthUnlisted);

            callCvgPreviousMonthUnlisted = (double) visitedUnlistedPreviousMonth / (fwDaysPreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousUnlisted = String.format("%.2f", callCvgPreviousMonthUnlisted);

            callCvgPrePrevMonthUnlisted = (double) visitedUnlistedPre_PrevMonth / (fwDaysPrePreviousMonth * 100);
            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousUnlisted = String.format("%.2f", callCvgPrePrevMonthUnlisted);

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


//            RecyclerView recyclerView = findViewById(R.id.recyclerView);

            Log.w("DEBUG", "RecyclerView object = " + recyclerView);

            VisitStatsAdapter adapter = new VisitStatsAdapter(
                    monthlyData.size(),AsOnCallsFragment.super.getActivity(), // FragmentActivity
                    dataListDoc,
                    dataListChm,
                    dataListStk,
                    dataListUnlisted,requireContext()
            );
            Log.w("DEBUG", "Adapter created = " + adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
