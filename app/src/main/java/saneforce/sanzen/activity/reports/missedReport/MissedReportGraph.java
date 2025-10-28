package saneforce.sanzen.activity.reports.missedReport;

import static java.security.AccessController.getContext;
import static saneforce.sanzen.R.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.ApprovedCallsFragment;
import saneforce.sanzen.activity.reports.visitMonitor.AsOnCallsFragment;
import saneforce.sanzen.activity.reports.visitMonitor.VisitFilter;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.ReportPagerAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityMissedReportGraphBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.MissedDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class MissedReportGraph extends AppCompatActivity {
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private ActivityMissedReportGraphBinding binding;
    private String sfCode;
    private String date;
    public DoctorVisitDao doctorVisitDao;
    public MissedDao missedDao;


    //private PieChart missedChart, missedChart2;
    ProgressBar pBarchmcurrent, pBarstkcurrent, pBarunlstcurrent, pBarstknewcurrent, pBarunlstnewcurrent;
    ProgressBar pBardrprev, pBarchmprev, pBarstkprev, pBarunlstprev, pBarstknewprev, pBarunlstnewprev;
    ProgressBar pBardrprev1, pBarchmprev1, pBarstkprev1, pBarunlstprev1, pBarstknewprev1, pBarunlstnewprev1;
    TextView pbarcurrentpercentage, pBarchmcurrentpercentage, pBarstkcurrentpercentage, pBarunlstcurrentpercentage, pBarstknewcurrentpercentage, pBarunlstnewcurrentpercentage;
    TextView pbarprevpercentage, pBarchmprevpercentage, pBarstkprevpercentage, pBarunlstprevpercentage, pBarstknewprevpercentage, pBarunlstnewprevpercentage;
    TextView pbarprev1percentage, pBarchmprev1percentage, pBarstkprev1percentage, pBarunlstprev1percentage, pBarstknewprev1percentage, pBarunlstnewprev1percentage;
    LinearLayout DrMissedCurrent, DrMissedCurrentprev, DrMissedCurrentprev1;
    LinearLayout ChmMissedCurrent, ChmMissedprev, ChmMissedprev1;
    LinearLayout StkMissedCurrent, StkMissedCurrentnew, StkMissedprev, StkMissedprevnew, StkMissedprev1, StkMissedprev1new;
    LinearLayout UnlstMissedCurrent, UnlstMissedCurrentnew, UnlstMissedprev, UnlstMissedprevnew, UnlstMissedprev1, UnlstMissedprev1new;
    static int pBarCount = 1;
    int maxCount = 0, currentCount = 0;
    View drchmcur, stkunlstcur, drchmprev, stkunlstprev, drchmprev1, stkunlstprev1;
    View drvisitcur, drmissedcur, chmvisitcur, chmmissedcur, stkvisitcur, stkmissedcur, unlstvisitcur, unlstmissedcur;
    View drvisitprev, drmissedprev, chmvisitprev, chmmissedprev, stkvisitprev, stkmissedprev, unlstvisitprev, unlstmissedprev;
    View drvisitprev1, drmissedprev1, chmvisitprev1, chmmissedprev1, stkvisitprev1, stkmissedprev1, unlstvisitprev1, unlstmissedprev1;
    TextView totaldrcur, totaldrvisited, totaldrmissed, totalchmcur, totalchmvisited, totalchmmissed, totalstkcur, totalstkvisited, totalstkmissed, totalunlstcur, totalunlstvisited, totalunlstmissed;
    TextView prevtotaldr, prevtotaldrvisited, prevtotaldrmissed, prevtotalchmcur, prevtotalchmvisited, prevtotalchmmissed, prevtotalstkcur, prevtotalstkvisited, prevtotalstkmissed, prevtotalunlstcur, prevtotalunlstvisited, prevtotalunlstmissed;
    TextView prev1totaldr, prev1totaldrvisited, prev1totaldrmissed, prev1totalchmcur, prev1totalchmvisited, prev1totalchmmissed, prev1totalstkcur, prev1totalstkvisited, prev1totalstkmissed, prev1totalunlstcur, prev1totalunlstvisited, prev1totalunlstmissed;
    View stknewvisitcur, stknewmissedcur, unlstnewvisitcur, unlstnewmissedcur;
    View stkprevvisitcur, stkprevmissedcur, unlstprevvisitcur, unlstprevmissedcur;
    View stkprev1visitcur, stkprev1missedcur, unlstprev1visitcur, unlstprev1missedcur;
    TextView totalstknewcur, totalstknewvisited, totalstknewmissed, totalunlstnewcur, totalunlstnewvisited, totalunlstnewmissed;
    TextView totalstkprevcur, totalstkprevvisited, totalstkprevmissed, totalunlstprevcur, totalunlstprevvisited, totalunlstprevmissed;
    TextView totalstkprev1cur, totalstkprev1visited, totalstkprev1missed, totalunlstprev1cur, totalunlstprev1visited, totalunlstprev1missed;
//Doctor
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
    String selectedTab, AsOnCalls,ApprovedCalls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        doctorVisitDao = roomDB.doctorVisitDao();
        missedDao = roomDB.missedDao();
        super.onCreate(savedInstanceState);
        binding = ActivityMissedReportGraphBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        sfCode = SharedPref.getSfCode(this);
        date = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5);
        loadFragment(new AsOnCallsMissedFragment());
        binding.note.findViewById(R.id.note);
        binding.imageBack.setOnClickListener(v -> {
            missedDao.deleteAll();
            doctorVisitDao.deleteAll();
            finish();
        });
        //binding.calendarLyt.findViewById(R.id.calendar_lyt);

        binding.self.setOnClickListener(view -> {
            selectedTab = "As on Calls";
            binding.note.setVisibility(View.VISIBLE);
            binding.tvNote.setText("Sync to get Live Data!");
            updateReportUi();
            loadFragment(new AsOnCallsMissedFragment());
        });
        binding.live.setOnClickListener(view -> {
            selectedTab = "Approved Calls";
            binding.note.setVisibility(View.VISIBLE);
            binding.tvNote.setText("You Are Live! Select Month to get Data");
            updateReportUi();
            loadFragment(new FragmentApprovedCallsMissed());
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

          //  binding.calender.setOnClickListener(v -> showMonthYearPicker(binding.calender));
            //login name - cluster
//        TextView headerTv = findViewById(R.id.Name);
//        headerTv.setText(SharedPref.getSfName(this) + " - " + SharedPref.getDsName(this) + " - " + SharedPref.getHqNameMain(this));
        //loadFragment(new AsOnCallsFragment());

//        binding.note.findViewById(R.id.note);
//        binding.searchCust.findViewById(R.id.search_cust);
//
////        image_back.setOnClickListener(view -> {
////            getOnBackPressedDispatcher().onBackPressed();
////        });
//
//        binding.calendarLyt.findViewById(R.id.calendar_lyt);
//
//
//        binding.self.setOnClickListener(view -> {
//            selectedTab = "As on Calls";
//            binding.searchCust.setVisibility(View.GONE);
//            binding.note.setVisibility(View.VISIBLE);
//            binding.calendarLyt.setVisibility(View.GONE);
//            updateReportUi();
//            loadFragment(new AsOnCallsFragment());
//        });
//
//        binding.live.setOnClickListener(view -> {
//            selectedTab = "Approved Calls";
//            binding.searchCust.setVisibility(View.VISIBLE);
//            binding.note.setVisibility(View.GONE);
//            binding.calendarLyt.setVisibility(View.VISIBLE);
//            updateReportUi();
//            loadFragment(new ApprovedCallsFragment());
//        });
//    }
//
//    private void loadFragment(Fragment fragment) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .commit();
//    }
//
//    public void updateReportUi(){
//        binding.self.setBackground(null);
//        binding.live.setBackground(null);
//
//        binding.self.setTextColor(getColor(R.color.dark_purple));
//        binding.live.setTextColor(getColor(R.color.dark_purple));
//
//        switch (selectedTab){
//            case "As on Calls":
//                binding.self.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
//                binding.self.setTextColor(getColor(R.color.white));
//                break;
//            case "Approved Calls":
//                binding.live.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
//                binding.live.setTextColor(getColor(R.color.white));
//                break;
//        }


//        binding.self.setOnClickListener(v -> {
//            binding.self.setBackgroundResource(R.drawable.bg_darkpurple_sharp_bottom_end);
//            binding.self.setTextColor(getResources().getColor(R.color.white));
//
//            binding.live.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//            binding.live.setTextColor(getResources().getColor(R.color.dark_purple));
//
//            //binding.recyclerDoctorMissedReports.setAdapter(new GraphAdapter(asOnCallDataList));
//            binding.recyclerDoctorMissedReports.setVisibility(View.VISIBLE);
//
//            binding.tvNote.setText("Sync to get Live Data!");
//        });
//
//        binding.live.setOnClickListener(v -> {
//            binding.live.setBackgroundResource(R.drawable.bg_darkpurple_sharp_bottom_end);
//            binding.live.setTextColor(getResources().getColor(R.color.white));
//
//            binding.self.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//            binding.self.setTextColor(getResources().getColor(R.color.dark_purple));
//
//            // Open MissedReport activity
//            Intent intent = new Intent(MissedReportGraph.this, MissedReport.class);
//            startActivity(intent);
//        });

//        doctorCard = findViewById(R.id.doctor_missed_previous);
//        chemistCard = findViewById(R.id.chemist_missed_previous);
//        stockistCard = findViewById(R.id.stk_missed_previous);
//        unlistedCard = findViewById(R.id.unlst_missed_previous);

        // Default: show only first card

//        findViewById(R.id.Name).setText(SharedPref.getSfName(this) + " - " + SharedPref.getHqNameMain(this));
//
//        String name = SharedPref.getSfName(this);
//        String cluster = SharedPref.getHqNameMain(this);
//        String displayName = name + " - " + cluster;
//        TextView headerTv = findViewById(R.id.Name);
//        headerTv.setText(displayName);
//        TextView headerTv = findViewById(R.id.Name);
//        String name = SharedPref.getSfName(this);
//        String desig = SharedPref.getDesig(this);
//        String cluster = SharedPref.getHqNameMain(this);
//        headerTv.setText(name + " - " + desig + " - " + cluster);

//        TextView name = findViewById(R.id.Name);
//        TextView Cluster = findViewById(id.Cluster);
//        TextView design = findViewById(R.id.Design);
//        name.setText(SharedPref.getSfName(this));
//        Cluster.setText(SharedPref.getHqNameMain(this));
//        design.setText(SharedPref.getDesig(this));

       // custFilter();
        hideSystemBars();
        drchmcur = findViewById(id.layoutDrChmCurrent);
        stkunlstcur = findViewById(id.layoutStkUnlstCurrent);
        drchmprev = findViewById(id.layoutDrChmPrev);
        stkunlstprev = findViewById(id.layoutStkUnlstPrev);
        drchmprev1 = findViewById(id.layoutDrChmPrev1);
        stkunlstprev1 = findViewById(id.layoutStkUnlstPrev1);
        //missedChart = findViewById(id.pBar);

        //     TextView Name = findViewById(R.id.name);
//        String userName = SharedPref.getUserName(this);     // getter method
//        String hq = SharedPref.getHQ(this);
//        String designation = SharedPref.getDesignation(this);
//
//        tvName.setText(userName + " - " + hq + " - " + designation);
//    }

//        MasterDataTable doctorMaster = masterDataDao.getMasterDataTableOrNew("DOCTOR_MAS");
//        if (doctorMaster == null) return;
//
//        int totalDoctors = 0;
//        try {
//            totalDoctors = doctorMaster.getMasterSyncDataJsonArray().length();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        VisitFilter visitFilter = new VisitFilter(masterDataDao);
//        Map<String, VisitFilter.MonthlyStats> statsMap = visitFilter.callFilter();
//        VisitFilter.MonthlyStats currentMonthStats = statsMap.get("current");
//        if (currentMonthStats == null) return;
//
//// Define variables here
//       // ProgressBar pBardrcurrent = findViewById(R.id.pBar);
//        int uniqueDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
//        int currentMonthMissed = totalDoctors - uniqueDoctorsCurrentMonth;


        //current drchm layout,stk,unlst hidden
//        drvisitcur = findViewById(id.visitedcheck);
//        drmissedcur = findViewById(id.missedcheck);
////        chmvisitcur = findViewById(id.check_box_chmvisted);
////        chmmissedcur = findViewById(id.check_box_chmmissed);
////        stkvisitcur = findViewById(id.stkvisitedcheck);
////        stkmissedcur = findViewById(id.stkmissedcheck);
////        unlstvisitcur = findViewById(id.check_box_unlstvisted);
//        unlstmissedcur = findViewById(id.check_box_unlstmissed);
//        totaldrcur = findViewById(id.totalDrCnt);
//        totaldrvisited = findViewById(id.visitedCnt);
//        totaldrmissed = findViewById(id.missedCnt);
////        totalchmcur = findViewById(id.totalchmCnt);

////        totalchmvisited = findViewById(id.chmvisitedCnt);
////        totalchmmissed = findViewById(id.chmmissedCnt);
////        totalstkcur = findViewById(id.totalstkCnt);
////        totalstkvisited = findViewById(id.stkvisitedCnt);
////        totalstkmissed = findViewById(id.stkmissedCnt);
//        totalunlstcur = findViewById(id.totalunlstCnt);
//        totalunlstvisited = findViewById(id.unlstvisitedCnt);
//        totalunlstmissed = findViewById(id.unlstmissedCnt);
        //current stk,unlst layout
        stknewvisitcur = findViewById(id.stknewvisitedcheck);
        stknewmissedcur = findViewById(id.stknewmissedcheck);
        unlstnewvisitcur = findViewById(id.unlstnewvistedcheck);
        unlstnewmissedcur = findViewById(id.unlstnewmissedcheck);
        totalstknewcur = findViewById(id.totalstknewCntcur);
        totalstknewvisited = findViewById(id.stknewvisitedCntcur);
        totalstknewmissed = findViewById(id.stknewmissedCntcur);
        totalunlstnewcur = findViewById(id.totalunlstnewCntcur);
        totalunlstnewvisited = findViewById(id.unlstnewvisitedCntcur);
        totalunlstnewmissed = findViewById(id.unlstnewmissedCntcur);

        //prev drchm layout,stk,unlst hidden
        drvisitprev = findViewById(id.visitedcheck_prev);
        drmissedprev = findViewById(id.missedcheck_prev);
//        chmvisitprev = findViewById(id.check_box_chmvisted_prev);
//        chmmissedprev = findViewById(id.check_box_chmmissed_prev);
//        stkvisitprev = findViewById(id.stkvisitedcheck_prev);
//        stkmissedprev = findViewById(id.stkmissedcheck_prev);
        unlstvisitprev = findViewById(id.check_box_unlstvisted_prev);
        unlstmissedprev = findViewById(id.check_box_unlstmissed_prev);
        prevtotaldr = findViewById(id.totalDrCnt_prev);
        prevtotaldrvisited = findViewById(id.visitedCnt_prev);
        prevtotaldrmissed = findViewById(id.missedCnt_prev);
//        prevtotalchmcur = findViewById(id.totalchmCnt_prev);
//        prevtotalchmvisited = findViewById(id.chmvisitedCnt_prev);
//        prevtotalchmmissed = findViewById(id.chmmissedCnt_prev);
//        prevtotalstkcur = findViewById(id.totalstkCnt_prev);
//        prevtotalstkvisited = findViewById(id.stkvisitedCnt_prev);
//        prevtotalstkmissed = findViewById(id.stkmissedCnt_prev);
        prevtotalunlstcur = findViewById(id.totalunlstCnt_prev);
        prevtotalunlstvisited = findViewById(id.unlstvisitedCnt_prev);
        prevtotalunlstmissed = findViewById(id.unlstmissedCnt_prev);
        //prev stk,unlst layout
        stkprevvisitcur = findViewById(id.stknewvisitedcheck_prev);
        stkprevmissedcur = findViewById(id.stknewmissedcheck_prev);
        unlstprevvisitcur = findViewById(id.check_box_unlstnewvisted_prev);
        unlstprevmissedcur = findViewById(id.check_box_unlstnewmissed_prev);
        totalstkprevcur = findViewById(id.totalstknewCnt_prev);
        totalstkprevvisited = findViewById(id.stknewvisitedCnt_prev);
        totalstkprevmissed = findViewById(id.stknewmissedCnt_prev);
        totalunlstprevcur = findViewById(id.totalunlstnewCnt_prev);
        totalunlstprevvisited = findViewById(id.unlstnewvisitedCnt_prev);
        totalunlstprevmissed = findViewById(id.unlstnewmissedCnt_prev);

        //prev1 drchm layout,stk,unlst hidden
        drvisitprev1 = findViewById(id.visitedcheck_prev2);
        drmissedprev1 = findViewById(id.missedcheck_prev2);
        chmvisitprev1 = findViewById(id.check_box_chmvisted_prev2);
        chmmissedprev1 = findViewById(id.check_box_chmmissed_prev2);
        stkvisitprev1 = findViewById(id.stkvisitedcheck_prev2);
        stkmissedprev1 = findViewById(id.stkmissedcheck_prev2);
        unlstvisitprev1 = findViewById(id.check_box_unlstvisted_prev2);
        unlstmissedprev1 = findViewById(id.check_box_unlstmissed_prev2);
        prev1totaldr = findViewById(id.totalDrCnt_prev2);
        prev1totaldrvisited = findViewById(id.visitedCnt_prev2);
        prev1totaldrmissed = findViewById(id.missedCnt_prev2);
        prev1totalchmcur = findViewById(id.totalchmCnt_prev2);
        prev1totalchmvisited = findViewById(id.chmvisitedCnt_prev2);
        prev1totalchmmissed = findViewById(id.chmmissedCnt_prev2);
        prev1totalstkcur = findViewById(id.totalstkCnt_prev2);
        prev1totalstkvisited = findViewById(id.stkvisitedCnt_prev2);
        prev1totalstkmissed = findViewById(id.stkmissedCnt_prev2);
        prev1totalunlstcur = findViewById(id.totalunlstCnt_prev2);
        prev1totalunlstvisited = findViewById(id.unlstvisitedCnt_prev2);
        prev1totalunlstmissed = findViewById(id.unlstmissedCnt_prev2);
        //prev1 stk,unlst layout
        stkprev1visitcur = findViewById(id.stknewvisitedcheck_prev2);
        stkprev1missedcur = findViewById(id.stknewmissedcheck_prev2);
        unlstprev1visitcur = findViewById(id.check_box_unlstnewvisted_prev2);
        unlstprev1missedcur = findViewById(id.check_box_unlstnewmissed_prev2);
        totalstkprev1cur = findViewById(id.totalstknewCnt_prev2);
        totalstkprev1visited = findViewById(id.stknewvisitedCnt_prev2);
        totalstkprev1missed = findViewById(id.stknewmissedCnt_prev2);
        totalunlstprev1cur = findViewById(id.totalunlstnewCnt_prev2);
        totalunlstprev1visited = findViewById(id.unlstnewvisitedCnt_prev2);
        totalunlstprev1missed = findViewById(id.unlstnewmissedCnt_prev2);

        DrMissedCurrent = findViewById(id.doctor_missed_current);
        DrMissedCurrentprev = findViewById(id.doctor_missed_previous);
        DrMissedCurrentprev1 = findViewById(id.doctor_missed_previous1);

//        ChmMissedCurrent = findViewById(id.chemist_missed_current);
//        ChmMissedprev = findViewById(id.chemist_missed_previous);
        ChmMissedprev1 = findViewById(id.chemist_missed_previous1);

//        StkMissedCurrent = findViewById(id.stk_missed_current);
        StkMissedCurrentnew = findViewById(id.stk_missed_currentnew);
//        StkMissedprev = findViewById(id.stk_missed_previous);
        StkMissedprevnew = findViewById(id.stk_missed_previousnew);
        StkMissedprev1 = findViewById(id.stk_missed_previous1);
        StkMissedprev1new = findViewById(id.stk_missed_previous1new);

        UnlstMissedCurrent = findViewById(id.unlst_missed_current);
        UnlstMissedCurrentnew = findViewById(id.unlst_missed_currentnew);
        UnlstMissedprev = findViewById(id.unlst_missed_previous);
        UnlstMissedprevnew = findViewById(id.unlst_missed_previousnew);
        UnlstMissedprev1 = findViewById(id.unlst_missed_previous1);
        UnlstMissedprev1new = findViewById(id.unlst_missed_previous1new);

        //pBardrcurrent = findViewById(id.pBar);
        // missedChart = findViewById(id.pBar);
//        pBarchmcurrent = findViewById(id.pBarchm);
//        pBarstkcurrent = findViewById(id.pBarstk);
        //pBarunlstcurrent = findViewById(id.pBarunlst);
//        pBarstknewcurrent = findViewById(id.pBarstknew);
//        pBarunlstnewcurrent = findViewById(id.pBarunlstnew);

        // pBardrprev = findViewById(id.pBar_prev);
        // missedChart2 = findViewById(id.pBar_prev);
//        pBarchmprev = findViewById(id.pBarchm_prev);
//        pBarstkprev = findViewById(id.pBarstk_prev);
        pBarunlstprev = findViewById(id.pBarunlst_prev);
        pBarstknewprev = findViewById(id.pBarstknew_prev);
        pBarunlstnewprev = findViewById(id.pBarunlstnew_prev);

        pBardrprev1 = findViewById(id.pBar_prev1);
        pBarchmprev1 = findViewById(id.pBarchm_prev1);
        pBarstkprev1 = findViewById(id.pBarstk_prev1);
        pBarunlstprev1 = findViewById(id.pBarunlst_prev1);
        pBarstknewprev1 = findViewById(id.pBarstknew_prev1);
        pBarunlstnewprev1 = findViewById(id.pBarunlstnew_prev1);

        // pbarcurrentpercentage = findViewById(id.pbar_percentage);
//        pBarchmcurrentpercentage = findViewById(id.pbar_chmpercentage);
//        pBarstkcurrentpercentage = findViewById(id.pbar_stkpercentage);
//        pBarunlstcurrentpercentage = findViewById(id.pbar_unlstpercentage);
//        pBarstknewcurrentpercentage = findViewById(id.pbar_stknewpercentage);
//        pBarunlstnewcurrentpercentage = findViewById(id.pbar_unlstnewpercentage);

        // pbarprevpercentage = findViewById(id.pbar_percentageprev);
//        pBarchmprevpercentage = findViewById(id.pbar_chmpercentageprev);
//        pBarstkprevpercentage = findViewById(id.pbar_stkpercentageprev);
        pBarunlstprevpercentage = findViewById(id.pbar_unlstpercentageprev);
        pBarstknewprevpercentage = findViewById(id.pbar_stknewpercentageprev);
        pBarunlstnewprevpercentage = findViewById(id.pbar_unlstnewpercentageprev);

        pbarprev1percentage = findViewById(id.pbar_percentageprev1);
        pBarchmprev1percentage = findViewById(id.pbar_chmpercentageprev1);
        pBarstkprev1percentage = findViewById(id.pbar_stkpercentageprev1);
        pBarunlstprev1percentage = findViewById(id.pbar_unlstpercentageprev1);
        pBarstknewprev1percentage = findViewById(id.pbar_stknewpercentageprev1);
        pBarunlstnewprev1percentage = findViewById(id.pbar_unlstnewpercentageprev1);

//        String dr = SharedPref.getDrNeed(this);
//        String chm = SharedPref.getChmNeed(this);
//        String stk = SharedPref.getStkNeed(this);
//        String unl = SharedPref.getUnlNeed(this);
//
//        boolean isDr = dr.equalsIgnoreCase("0");
//        boolean isChm = chm.equalsIgnoreCase("0");
//        boolean isStk = stk.equalsIgnoreCase("0");
//        boolean isUnl = unl.equalsIgnoreCase("0");
//
//        // Build key → "1" if value == "0", else "0"
//        String key = (isDr ? "1" : "0") +
//                (isChm ? "1" : "0") +
//                (isStk ? "1" : "0") +
//                (isUnl ? "1" : "0");
//
//        switch (key) {
////            case "0000":
////                // none = "0"
////                LoadDefaultZeroValues();
////                break;
//            case "1000":
//                // only DrNeed = "0"
//                LoadDrOnlyValues();
//                break;
//            case "0100":
//                // only ChmNeed = "0"
//                LoadChmOnlyValues();
//                break;
//            case "0010":
//                // only StkNeed = "0"
//                LoadStkOnlyValues();
//                break;
//            case "0001":
//                // only UnlNeed = "0"
//                LoadUnlstOnlyValues();
//                break;
//            case "1100":
//                // DrNeed + ChmNeed = "0"
//                LoadDrChmValues();
//                break;
//            case "1010":
//                // DrNeed + StkNeed = "0"
//                LoadDrStkValues();
//                break;
//            case "1001":
//                // DrNeed + UnlNeed = "0"
//                LoadDrUnlstValues();
//                break;
//            case "0110":
//                // ChmNeed + StkNeed = "0"
//                LoadChmStkValues();
//                break;
//            case "0101":
//                // ChmNeed + UnlNeed = "0"
//                LoadChmUnlstValues();
//                break;
//            case "0011":
//                // StkNeed + UnlNeed = "0"
//                LoadStkUnlstValues();
//                break;
//            case "1110":
//                // DrNeed + ChmNeed + StkNeed = "0"
//                LoadDrChmStkValues();
//                break;
//            case "1101":
//                // DrNeed + ChmNeed + UnlNeed = "0"
//                LoadDrChmUnlstValues();
//                break;
//            case "1011":
//                // DrNeed + StkNeed + UnlNeed = "0"
//                LoadDrStkUnlstValues();
//                break;
//            case "0111":
//                // ChmNeed + StkNeed + UnlNeed = "0"
//                LoadChmStkUnlstValues();
//                break;
//            case "1111":
//                // All = "0"
//                //LoadDrChmStkUnlstValues();
//      LoadDrChmStkUnlstValues(missedChart, missedChart2, totalDoctors, uniqueDoctorsCurrentMonth, currentMonthMissed, previousMonthMissed);
        //      LoadDrChmStkUnlstValues( totalDoctors, uniqueDoctorsCurrentMonth, currentMonthMissed, previousMonthMissed);
//                break;
//        }

//        DrMissedCurrent.setOnClickListener(v -> {
//            Intent intentWeb = new Intent(MissedReportGraph.this, MissedReport.class);
//            MissedReportGraph.this.startActivity(intentWeb);
//        });
//        DrMissedCurrentprev.setOnClickListener(v -> {
//            Intent intentWeb = new Intent(MissedReportGraph.this, MissedReport.class);
//            MissedReportGraph.this.startActivity(intentWeb);
//        });
//        DrMissedCurrentprev1.setOnClickListener(v -> {
//            Intent intentWeb = new Intent(MissedReportGraph.this, MissedReport.class);
//            MissedReportGraph.this.startActivity(intentWeb);
//        });


//
//        // Add swipe listeners
////        setSwipeListener(binding.viewFlipper);
////        setSwipeListener(binding.viewFlipper2);
////        setSwipeListener(binding.viewFlipper3);
//    }


//    public static void progressBarAnimation(final int max, ProgressBar pBar) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (max != 0) {
//                    while (pBarCount <= max) {
//                        try {
//                            pBar.setProgress(max);
//                        } catch (Exception e) {
//                        }
//                    }
//                } else {
//                    try {
//                        pBar.setProgress(0);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
//
//
//    private void setSwipeListener(ViewFlipper flipper) {
//        flipper.setOnTouchListener(new View.OnTouchListener() {
//            float downX, downY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        downX = event.getX();
//                        downY = event.getY();
//                        return true;
//
//                    case MotionEvent.ACTION_MOVE:
//                        float moveX = event.getX();
//                        float moveY = event.getY();
//                        float diffX = moveX - downX;
//                        float diffY = moveY - downY;
//
//                        if (Math.abs(diffX) > Math.abs(diffY)) {
//                            v.getParent().requestDisallowInterceptTouchEvent(true);
//                        }
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        float upX = event.getX();
//                        float deltaX = upX - downX; // 👉 Right swipe = positive, Left swipe = negative
//
//                        int currentIndex = flipper.getDisplayedChild();
//                        int childCount = flipper.getChildCount();
//
//                        if (Math.abs(deltaX) > 150) { // threshold
//                            if (deltaX < 0) { // 👉 Swipe Left → Next
//                                if (currentIndex < childCount - 1) {
//                                    flipper.setInAnimation(MissedReportGraph.this, R.anim.slide_in_right);
//                                    flipper.setOutAnimation(MissedReportGraph.this, R.anim.slide_out_left);
//                                    flipper.showNext();
//                                    return true;
//                                }
//                            } else { // 👉 Swipe Right → Previous
//                                if (currentIndex > 0) {
//                                    flipper.setInAnimation(MissedReportGraph.this, R.anim.slide_in_left);
//                                    flipper.setOutAnimation(MissedReportGraph.this, R.anim.slide_out_right);
//                                    flipper.showPrevious();
//                                    return true;
//                                }
//                            }
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
    }

    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30+
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Legacy for API < 30
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

//    public void LoadDefaultZeroValues() {
//        binding.viewFlipper.setVisibility(View.GONE);
//        binding.viewFlipper2.setVisibility(View.GONE);
//        binding.viewFlipper3.setVisibility(View.GONE);
//    }

//    public void LoadDrOnlyValues() {
//        ChmMissedCurrent.setVisibility(View.GONE);
//        ChmMissedprev.setVisibility(View.GONE);
//        ChmMissedprev1.setVisibility(View.GONE);
//        stkunlstcur.setVisibility(View.GONE);
//        stkunlstprev.setVisibility(View.GONE);
//        stkunlstprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadChmOnlyValues() {
//        DrMissedCurrent.setVisibility(View.GONE);
//        DrMissedCurrentprev.setVisibility(View.GONE);
//        DrMissedCurrentprev1.setVisibility(View.GONE);
//        stkunlstcur.setVisibility(View.GONE);
//        stkunlstprev.setVisibility(View.GONE);
//        stkunlstprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadStkOnlyValues() {
//        UnlstMissedCurrentnew.setVisibility(View.GONE);
//        UnlstMissedprevnew.setVisibility(View.GONE);
//        UnlstMissedprev1new.setVisibility(View.GONE);
//        drchmcur.setVisibility(View.GONE);
//        drchmprev.setVisibility(View.GONE);
//        drchmprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadUnlstOnlyValues() {
//        StkMissedCurrentnew.setVisibility(View.GONE);
//        StkMissedprevnew.setVisibility(View.GONE);
//        StkMissedprev1new.setVisibility(View.GONE);
//        drchmcur.setVisibility(View.GONE);
//        drchmprev.setVisibility(View.GONE);
//        drchmprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadDrChmValues() {
//        stkunlstcur.setVisibility(View.GONE);
//        stkunlstprev.setVisibility(View.GONE);
//        stkunlstprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadDrStkValues() {
//        ChmMissedCurrent.setVisibility(View.GONE);
//        StkMissedCurrent.setVisibility(View.VISIBLE);
//        ChmMissedprev.setVisibility(View.GONE);
//        StkMissedprev.setVisibility(View.VISIBLE);
//        ChmMissedprev1.setVisibility(View.GONE);
//        StkMissedprev1.setVisibility(View.VISIBLE);
//        stkunlstcur.setVisibility(View.GONE);
//        stkunlstprev.setVisibility(View.GONE);
//        stkunlstprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadDrUnlstValues() {
//        ChmMissedCurrent.setVisibility(View.GONE);
//        UnlstMissedCurrent.setVisibility(View.VISIBLE);
//        ChmMissedprev.setVisibility(View.GONE);
//        UnlstMissedprev.setVisibility(View.VISIBLE);
//        ChmMissedprev1.setVisibility(View.GONE);
//        UnlstMissedprev1.setVisibility(View.VISIBLE);
//        stkunlstcur.setVisibility(View.GONE);
//        stkunlstprev.setVisibility(View.GONE);
//        stkunlstprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadChmStkValues() {
//        DrMissedCurrent.setVisibility(View.GONE);
//        StkMissedCurrent.setVisibility(View.VISIBLE);
//        DrMissedCurrentprev.setVisibility(View.GONE);
//        StkMissedprev.setVisibility(View.VISIBLE);
//        DrMissedCurrentprev1.setVisibility(View.GONE);
//        StkMissedprev1.setVisibility(View.VISIBLE);
//        stkunlstcur.setVisibility(View.GONE);
//        stkunlstprev.setVisibility(View.GONE);
//        stkunlstprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadChmUnlstValues() {
//        DrMissedCurrent.setVisibility(View.GONE);
//        UnlstMissedCurrent.setVisibility(View.VISIBLE);
//        DrMissedCurrentprev.setVisibility(View.GONE);
//        UnlstMissedprev.setVisibility(View.VISIBLE);
//        DrMissedCurrentprev1.setVisibility(View.GONE);
//        UnlstMissedprev1.setVisibility(View.VISIBLE);
//        stkunlstcur.setVisibility(View.GONE);
//        stkunlstprev.setVisibility(View.GONE);
//        stkunlstprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadStkUnlstValues() {
//        drchmcur.setVisibility(View.GONE);
//        drchmprev.setVisibility(View.GONE);
//        drchmprev1.setVisibility(View.GONE);
//    }
//
//    public void LoadDrChmStkValues() {
//        UnlstMissedCurrentnew.setVisibility(View.GONE);
//        UnlstMissedprevnew.setVisibility(View.GONE);
//        UnlstMissedprev1new.setVisibility(View.GONE);
//    }
//
//    public void LoadDrChmUnlstValues() {
//        StkMissedCurrentnew.setVisibility(View.GONE);
//        StkMissedprevnew.setVisibility(View.GONE);
//        StkMissedprev1new.setVisibility(View.GONE);
//    }
//
//    public void LoadDrStkUnlstValues() {
//        ChmMissedCurrent.setVisibility(View.GONE);
//        StkMissedCurrent.setVisibility(View.VISIBLE);
//        ChmMissedprev.setVisibility(View.GONE);
//        StkMissedprev.setVisibility(View.VISIBLE);
//        ChmMissedprev1.setVisibility(View.GONE);
//        StkMissedprev1.setVisibility(View.VISIBLE);
//        StkMissedCurrentnew.setVisibility(View.GONE);
//        StkMissedprevnew.setVisibility(View.GONE);
//        StkMissedprev1new.setVisibility(View.GONE);
//    }
//
//    public void LoadChmStkUnlstValues() {
//        DrMissedCurrent.setVisibility(View.GONE);
//        DrMissedCurrentprev.setVisibility(View.GONE);
//        DrMissedCurrentprev1.setVisibility(View.GONE);
//        StkMissedCurrent.setVisibility(View.VISIBLE);
//        StkMissedCurrentnew.setVisibility(View.GONE);
//        StkMissedprev.setVisibility(View.VISIBLE);
//        StkMissedprevnew.setVisibility(View.GONE);
//        StkMissedprev1.setVisibility(View.VISIBLE);
//        StkMissedprev1new.setVisibility(View.GONE);
//    }
//    //public void LoadDrChmStkUnlstValues(PieChart pBardrcurrent, int totalDoctors, int uniqueDoctorsCurrentMonth, int currentMonthMissed) {

    public void LoadDrChmStkUnlstValues(
            PieChart missedChart,
            PieChart missedChart2,
            int totalDoctors,
            int uniqueDoctorsCurrentMonth,
            int currentMonthMissed,
            int previousMonthMissed) {

//        float missedPercentage = (((float) currentMonthMissed / (float) totalDoctors) * 100.0f);
//        ArrayList<Integer> colors = new ArrayList<>();
//        // colors.add(Color.rgb(0, 144, 255));
//        colors.add(getResources().getColor(R.color.green_60));
//        colors.add(getResources().getColor(R.color.mildRed));
//        ArrayList<PieEntry> missedDataList = new ArrayList<>();
//        missedDataList.add(new PieEntry(100.0f - missedPercentage)); // visited
//        missedDataList.add(new PieEntry(missedPercentage, ""));      // missed
//        PieDataSet missedDataSet = new PieDataSet(missedDataList, "");
//        missedDataSet.setColors(colors);
//        PieData missedData = new PieData(missedDataSet);
//        missedData.setValueTextSize(0f);
//        missedData.setValueTextColor(Color.WHITE);
//        missedChart.setData(missedData);
//        missedChart.setUsePercentValues(true);
//        missedChart.setDrawHoleEnabled(true);
//        missedChart.setCenterTextSize(18f);
//        missedChart.setCenterTextColor(missedChart.getContext().getColor(R.color.black));
//        missedChart.setTransparentCircleRadius(30f);
//        missedChart.setHoleRadius(75f);
//        missedChart.animateXY(1400, 1400);
//        missedChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
//        missedChart.setCenterText(String.format("%.1f %%", missedPercentage));
//        Description description2 = missedChart.getDescription();
//        description2.setEnabled(false);
//        Legend legend2 = missedChart.getLegend();
//        legend2.setEnabled(false);
//        missedChart.invalidate();
//        totaldrcur.setText(String.valueOf(totalDoctors));
//        totaldrvisited.setText(String.valueOf(visitedDoctorsCurrentMonth));
//        totaldrmissed.setText(String.valueOf(currentMonthMissed));

 /*       //Dr Current Month
//        pBardrcurrent.setData(100);
//        pbarcurrentpercentage.setText(String.valueOf(25) + "%");
//        pBardrcurrent.setProgressTintList(ColorStateList.valueOf(getColor(color.green_2)));
//        progressBarAnimation(25, pBardrcurrent);
//        totaldrcur.setText("100");
//        totaldrvisited.setText("75");
//        totaldrmissed.setText("25");
//        drvisitcur.setBackgroundColor(getColor(color.green_2));
//        drmissedcur.setBackgroundColor(getColor(color.backround_graey));

        //Chm Current Month
       // pBarchmcurrent.setMax(100);
        pBarchmcurrentpercentage.setText(String.valueOf(56) + "%");
        pBarchmcurrent.setProgressTintList(ColorStateList.valueOf(getColor(color.blue_60)));
        progressBarAnimation(56, pBarchmcurrent);
        totalchmcur.setText("114");
        totalchmvisited.setText("4");
        totalchmmissed.setText("110");
        chmvisitcur.setBackgroundColor(getColor(color.blue_60));
        chmmissedcur.setBackgroundColor(getColor(color.backround_graey));
        //Stk Current Month
        //pBarstknewcurrent.setMax(100);
        pBarstknewcurrentpercentage.setText(String.valueOf(45) + "%");
        pBarstknewcurrent.setProgressTintList(ColorStateList.valueOf(getColor(color.red_60)));
        progressBarAnimation(45, pBarstknewcurrent);
        totalstknewcur.setText("75");
        totalstknewvisited.setText("5");
        totalstknewmissed.setText("70");
        stknewvisitcur.setBackgroundColor(getColor(color.red_60));
        stknewmissedcur.setBackgroundColor(getColor(color.backround_graey));
        //Unlst Current Month
        pBarunlstnewcurrent.setMax(100);
        pBarunlstnewcurrentpercentage.setText(String.valueOf(20) + "%");
        pBarunlstnewcurrent.setProgressTintList(ColorStateList.valueOf(getColor(color.gray_med)));
        progressBarAnimation(20, pBarunlstnewcurrent);
        totalunlstnewcur.setText("80");
        totalunlstnewvisited.setText("15");
        totalunlstnewmissed.setText("65");
        unlstnewvisitcur.setBackgroundColor(getColor(color.gray_med));
        unlstnewmissedcur.setBackgroundColor(getColor(color.backround_graey));
        //  }, 200);*/
        // previous month sp
//        float missedPercentage2 = (((float) previousMonthMissed / (float) totalDoctors) * 100.0f);
//        ArrayList<Integer> colors2 = new ArrayList<>();
//        colors.add(Color.rgb(0, 144, 255));
//        colors2.add(getResources().getColor(R.color.green_60));
//        colors2.add(getResources().getColor(R.color.mildRed));
//        ArrayList<PieEntry> missedDataList2 = new ArrayList<>();
//        missedDataList2.add(new PieEntry(100.0f - missedPercentage2)); // visited
//        missedDataList2.add(new PieEntry(missedPercentage, ""));      // missed
//        PieDataSet missedDataSet2 = new PieDataSet(missedDataList2, "");
//        missedDataSet2.setColors(colors);
//        PieData missedData2 = new PieData(missedDataSet2);
//        missedData2.setValueTextSize(0f);
//        missedData2.setValueTextColor(Color.WHITE);
//        missedChart2.setData(missedData2);
//        missedChart2.setUsePercentValues(true);
//        missedChart2.setDrawHoleEnabled(true);
//        missedChart2.setCenterTextSize(18f);
//        missedChart2.setCenterTextColor(missedChart.getContext().getColor(R.color.black));
//        missedChart2.setTransparentCircleRadius(30f);
//        missedChart2.setHoleRadius(70f);
//        missedChart2.animateXY(1400, 1400);
//        missedChart2.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
//        missedChart2.setCenterText(String.format("%.1f %%", missedPercentage));
//        Description description3 = missedChart2.getDescription();
//        description3.setEnabled(false);
//        Legend legend3 = missedChart2.getLegend();
//        legend3.setEnabled(false);
//        missedChart2.invalidate();
//        prevtotaldr.setText(String.valueOf(totalDoctors));
//        prevtotaldrvisited.setText(String.valueOf(visitedDoctorsPreviousMonth));
//        prevtotaldrmissed.setText(String.valueOf(previousMonthMissed));


        /*new Handler().postDelayed(() -> {
            //Dr Prev Month
            pBardrprev.setMax(100);
            pbarprevpercentage.setText(String.valueOf(50) + "%");
            pBardrprev.setProgressTintList(ColorStateList.valueOf(getColor(color.green_2)));
            progressBarAnimation(50, pBardrprev);
            prevtotaldr.setText("100");
            prevtotaldrvisited.setText("50");
            prevtotaldrmissed.setText("50");
            drvisitprev.setBackgroundColor(getColor(color.green_2));
            drmissedprev.setBackgroundColor(getColor(color.backround_graey));
            //Chm Prev Month
            pBarchmprev.setMax(100);
            pBarchmprevpercentage.setText(String.valueOf(60) + "%");
            pBarchmprev.setProgressTintList(ColorStateList.valueOf(getColor(color.blue_60)));
            progressBarAnimation(60, pBarchmprev);
            prevtotalchmcur.setText("114");
            prevtotalchmvisited.setText("56");
            prevtotalchmmissed.setText("58");
            chmvisitprev.setBackgroundColor(getColor(color.blue_60));
            chmmissedprev.setBackgroundColor(getColor(color.backround_graey));
            //Stk Prev Month
            pBarstknewprev.setMax(100);
            pBarstknewprevpercentage.setText(String.valueOf(90) + "%");
            pBarstknewprev.setProgressTintList(ColorStateList.valueOf(getColor(color.red_60)));
            progressBarAnimation(90, pBarstknewprev);
            totalstkprevcur.setText("75");
            totalstkprevvisited.setText("50");
            totalstkprevmissed.setText("20");
            stkprevvisitcur.setBackgroundColor(getColor(color.red_60));
            stkprevmissedcur.setBackgroundColor(getColor(color.backround_graey));
            //Unlst Prev Month
            pBarunlstnewprev.setMax(100);
            pBarunlstnewprevpercentage.setText(String.valueOf(50) + "%");
            pBarunlstnewprev.setProgressTintList(ColorStateList.valueOf(getColor(color.gray_med)));
            progressBarAnimation(50, pBarunlstnewprev);
            totalunlstprevcur.setText("80");
            totalunlstprevvisited.setText("75");
            totalunlstprevmissed.setText("5");
            unlstprevvisitcur.setBackgroundColor(getColor(color.gray_med));
            unlstprevmissedcur.setBackgroundColor(getColor(color.backround_graey));
        }, 500);

        new Handler().postDelayed(() -> {
            //Dr Pre-Prev Month
            pBardrprev1.setMax(100);
            pbarprev1percentage.setText(String.valueOf(25) + "%");
            pBardrprev1.setProgressTintList(ColorStateList.valueOf(getColor(color.green_2)));
            progressBarAnimation(25, pBardrprev1);
            prev1totaldr.setText("100");
            prev1totaldrvisited.setText("25");
            prev1totaldrmissed.setText("75");
            drvisitprev1.setBackgroundColor(getColor(color.green_2));
            drmissedprev1.setBackgroundColor(getColor(color.backround_graey));
            //Chm Pre-Prev Month
            pBarchmprev1.setMax(100);
            pBarchmprev1percentage.setText(String.valueOf(35) + "%");
            pBarchmprev1.setProgressTintList(ColorStateList.valueOf(getColor(color.blue_60)));
            progressBarAnimation(35, pBarchmprev1);
            prev1totalchmcur.setText("114");
            prev1totalchmvisited.setText("100");
            prev1totalchmmissed.setText("14");
            chmvisitprev1.setBackgroundColor(getColor(color.blue_60));
            chmmissedprev1.setBackgroundColor(getColor(color.backround_graey));
            //Stk Pre-Prev Month
            pBarstknewprev1.setMax(100);
            pBarstknewprev1percentage.setText(String.valueOf(80) + "%");
            pBarstknewprev1.setProgressTintList(ColorStateList.valueOf(getColor(color.red_60)));
            progressBarAnimation(80, pBarstknewprev1);
            totalstkprev1cur.setText("75");
            totalstkprev1visited.setText("75");
            totalstkprev1missed.setText("0");
            stkprev1visitcur.setBackgroundColor(getColor(color.red_60));
            stkprev1missedcur.setBackgroundColor(getColor(color.backround_graey));
            //Unlst Pre-Prev Month
            pBarunlstnewprev1.setMax(100);
            pBarunlstnewprev1percentage.setText(String.valueOf(60) + "%");
            pBarunlstnewprev1.setProgressTintList(ColorStateList.valueOf(getColor(color.gray_med)));
            progressBarAnimation(60, pBarunlstnewprev1);
            totalunlstprev1cur.setText("80");
            totalunlstprev1visited.setText("70");
            totalunlstprev1missed.setText("10");
            unlstprev1visitcur.setBackgroundColor(getColor(color.gray_med));
            unlstprev1missedcur.setBackgroundColor(getColor(color.backround_graey));
        }, 800);*/
    }

//    public void custFilter() {
//        VisitFilter visitFilter = new VisitFilter(masterDataDao);
//        Map<String, VisitFilter.MonthlyStats> monthlyData = visitFilter.callFilter();
//
//
//        try {
//
//            VisitFilter.MonthlyStats currentMonthStats = monthlyData.get("current");
//            VisitFilter.MonthlyStats previousMonthStats = monthlyData.get("previous");
//            VisitFilter.MonthlyStats prePreviousMonthStats = monthlyData.get("prePrevious");
//
//            // Example: Get the number of unique doctors visited in the current month
//            uniqueDoctorsCurrentMonth = currentMonthStats.uniqueDoctors.size();
//            uniqueDoctorsPreviousMonth = previousMonthStats.uniqueDoctors.size();
//            int uniqueDoctorsPre_PrevMonth = prePreviousMonthStats.uniqueDoctors.size();
//
//            uniqueChemistCurrentMonth = currentMonthStats.uniqueChemists.size();
//            uniqueChemistPreviousMonth = previousMonthStats.uniqueChemists.size();
//            int uniqueChemistPre_PrevMonth = prePreviousMonthStats.uniqueChemists.size();
//
//            uniqueStockistCurrentMonth = currentMonthStats.uniqueStockiest.size();
//            uniqueStockistPreviousMonth = previousMonthStats.uniqueStockiest.size();
//            int uniqueStockiestPre_PrevMonth = previousMonthStats.uniqueStockiest.size();
//
//             uniqueUnlistedCurrentMonth = currentMonthStats.uniqueUnlisted.size();
//            uniqueUnlistedPreviousMonth = currentMonthStats.uniqueUnlisted.size();
//            int uniqueUnlistedPre_PrevMonth = currentMonthStats.uniqueUnlisted.size();
//
//            //added total doctor
//
//            totalDoctors = 0;
//            visitedDoctorsPreviousMonth = 0;
//            int visitedDoctorsPre_PrevMonth = 0;
//
//
//            //added total visit
//            totalChemist = 0;
//            visitedChemistPreviousMonth = 0;
//            int visitedChemistPre_PrevMonth = 0;
//
//            //added totalstockiest
//            totalStockist = 0;
//            visitedStockistPreviousMonth = 0;
//            int visitedStockistPre_PrevMonth = 0;
//
//            //total unlisted
//            totalUnlisted = 0;
//            visitedUnlistedPreviousMonth = 0;
//            visitedDoctorsCurrentMonth = currentMonthStats.visitedDoctors.size();
//            visitedDoctorsPreviousMonth = previousMonthStats.visitedDoctors.size();
//            visitedDoctorsPre_PrevMonth = prePreviousMonthStats.visitedDoctors.size();
//
//            visitedChemistCurrentMonth = currentMonthStats.visitedChemists.size();
//            visitedChemistPreviousMonth = previousMonthStats.visitedChemists.size();
//            visitedChemistPre_PrevMonth = prePreviousMonthStats.visitedChemists.size();
//
//            visitedStockistCurrentMonth = currentMonthStats.visitedStockiest.size();
//            visitedStockistPreviousMonth = previousMonthStats.visitedStockiest.size();
//            visitedStockistPre_PrevMonth = previousMonthStats.visitedStockiest.size();
//
//           visitedUnlistedCurrentMonth = currentMonthStats.visitedUnlisted.size();
//            visitedUnlistedPreviousMonth = currentMonthStats.visitedUnlisted.size();
//            int visitedUnlistedPre_PrevMonth = currentMonthStats.visitedUnlisted.size();
//
//
//            //doctor
//            String doctorData = masterDataDao.getDataByKey(Constants.DOCTOR_MAS + SharedPref.getHqCode(this));
//            JSONArray doctorArray = new JSONArray(doctorData);
//            totalDoctors = doctorArray.length();
//
//            //chemist
//            String chemistData = masterDataDao.getDataByKey(Constants.CHEMIST_MAS + SharedPref.getHqCode(this));
//            JSONArray chemistArray = new JSONArray(chemistData);
//            totalChemist = chemistArray.length();
//
//            //stockiest
//            String stkData = masterDataDao.getDataByKey(Constants.STOCKIEST_MAS + SharedPref.getHqCode(this));
//            JSONArray stkArray = new JSONArray(stkData);
//            int totalStk = stkArray.length();
//
//            //unlisted
//            String unlistedData = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_MAS + SharedPref.getHqCode(this));
//            JSONArray unlistedArray = new JSONArray(unlistedData);
//            int totalUnlisted = unlistedArray.length();
//
//
//            //missed
//            //Doc
//            currentMonthMissed = totalDoctors - uniqueDoctorsCurrentMonth;
//            previousMonthMissed = totalDoctors - uniqueDoctorsPreviousMonth;
//            prePreviousMonthMissed = totalDoctors - uniqueDoctorsPre_PrevMonth;
//            //che
//            currentMonthMissedChe = totalChemist - uniqueChemistCurrentMonth;
//            previousMonthMissedChe = totalChemist - uniqueChemistPreviousMonth;
//            prePreviousMonthMissedChe = totalChemist - uniqueChemistPre_PrevMonth;
//            //Stk
//            currentMonthMissedStk = totalStk - uniqueStockistCurrentMonth;
//            previousMonthMissedStk = totalStk - uniqueStockistPreviousMonth;
//            prePreviousMonthMissedStk = totalStk - uniqueStockiestPre_PrevMonth;
//            //Unlist
//            currentMonthMissedUnlisted = totalUnlisted - uniqueUnlistedCurrentMonth;
//            previousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPreviousMonth;
//             prePreviousMonthMissedUnlisted = totalUnlisted - uniqueUnlistedPre_PrevMonth;
//
//            //FWDays
//            int fwDaysCurrentMonth = currentMonthStats.FWDays.size();
//            int fwDaysPreviousMonth = previousMonthStats.FWDays.size();
//            int fwDaysPrePreviousMonth = prePreviousMonthStats.FWDays.size();
//
//
//            //missed
//
//
//            //Call Average
//            //Doc
//            double callAvgCurrentMonthDoc;
//            double callAvgPreviousMonthDoc;
//            double callAvgPrePrevMonthDoc;
//
//            callAvgCurrentMonthDoc = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgCurrent = String.format("%.1f", callAvgCurrentMonthDoc);
//
//            callAvgPreviousMonthDoc = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPrevious = String.format("%.1f", callAvgPreviousMonthDoc);
//
//            callAvgPrePrevMonthDoc = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPre_Previous = String.format("%.1f", callAvgPrePrevMonthDoc);
//
//            //Che
//            double callAvgCurrentMonthChe;
//            double callAvgPreviousMonthChe;
//            double callAvgPrePrevMonthChe;
//
//            callAvgCurrentMonthChe = (double) visitedChemistCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgCurrentChe = String.format("%.1f", callAvgCurrentMonthChe);
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
//            callAvgCurrentMonthStk = (double) visitedStockistCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgCurrentStk = String.format("%.1f", callAvgCurrentMonthStk);
//
//            callAvgPreviousMonthStk = (double) visitedStockistPreviousMonth / fwDaysPreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPreviousStk = String.format("%.1f", callAvgPreviousMonthStk);
//
//            callAvgPrePrevMonthStk = (double) visitedStockistPre_PrevMonth / fwDaysPrePreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPre_PreviousStk = String.format("%.1f", callAvgPrePrevMonthStk);
//
//            //Unlisted
//            double callAvgCurrentMonthUnlisted;
//            double callAvgPreviousMonthUnlisted;
//            double callAvgPrePrevMonthUnlisted;
//
//            callAvgCurrentMonthUnlisted = (double) visitedUnlistedCurrentMonth / fwDaysCurrentMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgCurrentUnlisted = String.format("%.1f", callAvgCurrentMonthUnlisted);
//
//            callAvgPreviousMonthUnlisted = (double) visitedUnlistedPreviousMonth / fwDaysPreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPreviousUnlisted = String.format("%.1f", callAvgPreviousMonthUnlisted);
//
//            callAvgPrePrevMonthUnlisted = (double) visitedUnlistedPre_PrevMonth / fwDaysPrePreviousMonth;
//            @SuppressLint("DefaultLocale") String formattedCallAvgPre_PreviousUnlisted = String.format("%.1f", callAvgPrePrevMonthUnlisted);
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
//            @SuppressLint("DefaultLocale") String formattedCallCvgCurrent = String.format("%.1f", callCvgCurrentMonthDoc);
//
//            callCvgPreviousMonthDoc = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPrevious = String.format("%.1f", callCvgPreviousMonthDoc);
//
//            callCvgPrePrevMonthDoc = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPre_Previous = String.format("%.1f", callCvgPrePrevMonthDoc);
//
//            //Che
//
//            double callCvgCurrentMonthChe;
//            double callCvgPreviousMonthChe;
//            double callCvgPrePrevMonthChe;
//
//            callCvgCurrentMonthChe = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentChe = String.format("%.1f", callCvgCurrentMonthChe);
//
//            callCvgPreviousMonthChe = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousChe = String.format("%.1f", callCvgPreviousMonthChe);
//
//            callCvgPrePrevMonthChe = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousChe = String.format("%.1f", callCvgPrePrevMonthChe);
//
//            //Stk
//
//            double callCvgCurrentMonthStk;
//            double callCvgPreviousMonthStk;
//            double callCvgPrePrevMonthStk;
//
//            callCvgCurrentMonthStk = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentStk = String.format("%.1f", callCvgCurrentMonthStk);
//
//            callCvgPreviousMonthStk = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousStk = String.format("%.1f", callCvgPreviousMonthStk);
//
//            callCvgPrePrevMonthStk = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousStk = String.format("%.1f", callCvgPrePrevMonthStk);
//
//            //Unlisted
//
//            double callCvgCurrentMonthUnlisted;
//            double callCvgPreviousMonthUnlisted;
//            double callCvgPrePrevMonthUnlisted;
//
//            callCvgCurrentMonthUnlisted = (double) visitedDoctorsCurrentMonth / fwDaysCurrentMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgCurrentUnlisted = String.format("%.1f", callCvgCurrentMonthUnlisted);
//
//            callCvgPreviousMonthUnlisted = (double) visitedDoctorsPreviousMonth / fwDaysPreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPreviousUnlisted = String.format("%.1f", callCvgPreviousMonthUnlisted);
//
//            callCvgPrePrevMonthUnlisted = (double) visitedDoctorsPre_PrevMonth / fwDaysPrePreviousMonth * 100;
//            @SuppressLint("DefaultLocale") String formattedCallCvgPre_PreviousUnlisted = String.format("%.1f", callCvgPrePrevMonthUnlisted);
//            List<String> monthData = new ArrayList<>();
//
//
//            //Doctor
//            List<MissedStatsModel> dataList = new ArrayList<>();
//            MissedStatsModel currentMonthDrStats = new MissedStatsModel(
//                    "1",
//                    doctorArray,
//                    currentMonthStats.uniqueDoctors);
//
////                    String.valueOf(totalDoctors),
////                    String.valueOf(uniqueDoctorsCurrentMonth),
////                    String.valueOf(currentMonthMissed),
////                    String.valueOf(fwDaysCurrentMonth),
////                    formattedCallAvgCurrent,
////                    formattedCallCvgCurrent
//
//
//            MissedStatsModel previousMonthDrStats = new MissedStatsModel(
//                    "1",
//                    doctorArray,
//                    previousMonthStats.uniqueDoctors);
////                    String.valueOf(totalDoctors),
////                    String.valueOf(uniqueDoctorsPreviousMonth),
////                    String.valueOf(previousMonthMissed),
////                    String.valueOf(fwDaysPreviousMonth),
////                    formattedCallAvgPrevious,
////                    formattedCallCvgPrevious
////
////            );
//
//            MissedStatsModel prePreviousMonthDrStats = new MissedStatsModel(
//                    "1",
//                    doctorArray,
//                    prePreviousMonthStats.uniqueDoctors);
////                    String.valueOf(totalDoctors),
////                    String.valueOf(uniqueDoctorsPre_PrevMonth),
////                    String.valueOf(prePreviousMonthMissed),
////                    String.valueOf(fwDaysPrePreviousMonth),
////                    formattedCallAvgPre_Previous,
////                    formattedCallCvgPre_Previous
////
////            );
//
//            //Chemist
//            MissedStatsModel currentMonthCheStats = new MissedStatsModel(
//                    "2",
//                    chemistArray,
//                    currentMonthStats.uniqueChemists);
////                    String.valueOf(totalChemist),
////                    String.valueOf(uniqueChemistCurrentMonth),
////                    String.valueOf(currentMonthMissed),
////                    String.valueOf(fwDaysCurrentMonth),
////                    formattedCallAvgCurrentChe,
////                    formattedCallCvgCurrentChe
////
////            );
//
//            MissedStatsModel previousMonthCheStats = new MissedStatsModel(
//                    "2",
//                    chemistArray,
//                    previousMonthStats.uniqueChemists);
////                    String.valueOf(totalChemist),
////                    String.valueOf(uniqueChemistPreviousMonth),
////                    String.valueOf(previousMonthMissed),
////                    String.valueOf(fwDaysPreviousMonth),
////                    formattedCallAvgPreviousChe,
////                    formattedCallCvgPreviousChe
////
////            );
//
//            MissedStatsModel prePreviousMonthCheStats = new MissedStatsModel(
//                    "2",
//                    chemistArray,
//                    prePreviousMonthStats.uniqueChemists);
////                    String.valueOf(totalChemist),
////                    String.valueOf(uniqueChemistPre_PrevMonth),
////                    String.valueOf(prePreviousMonthMissed),
////                    String.valueOf(fwDaysPrePreviousMonth),
////                    formattedCallAvgPre_PreviousChe,
////                    formattedCallCvgPre_PreviousChe
////            );
//
//            //Stk
//
//            MissedStatsModel currentMonthStkStats = new MissedStatsModel(
//                    "3",
//                    stkArray,
//                    currentMonthStats.uniqueStockiest);
////                    String.valueOf(totalStk),
////                    String.valueOf(uniqueStockiestCurrentMonth),
////                    String.valueOf(currentMonthMissed),
////                    String.valueOf(fwDaysCurrentMonth),
////                    formattedCallAvgCurrentStk,
////                    formattedCallCvgCurrentStk
////            );
//
//            MissedStatsModel previousMonthStkStats = new MissedStatsModel(
//                    "3",
//                    stkArray,
//                    previousMonthStats.uniqueStockiest);
////                    String.valueOf(totalStk),
////                    String.valueOf(uniqueStockiestPreviousMonth),
////                    String.valueOf(previousMonthMissed),
////                    String.valueOf(fwDaysPreviousMonth),
////                    formattedCallAvgPreviousStk,
////                    formattedCallCvgPreviousStk
////
////            );
//
//            MissedStatsModel prePreviousMonthStkStats = new MissedStatsModel(
//                    "3",
//                    stkArray,
//                    prePreviousMonthStats.uniqueStockiest);
////                    String.valueOf(totalStk),
////                    String.valueOf(uniqueStockiestPre_PrevMonth),
////                    String.valueOf(prePreviousMonthMissed),
////                    String.valueOf(fwDaysPrePreviousMonth),
////                    formattedCallAvgPre_PreviousStk,
////                    formattedCallCvgPre_PreviousStk
////            );
//
//            //unlisted
//            MissedStatsModel currentMonthUnlistedStats = new MissedStatsModel(
//                    "4",
//                    unlistedArray,
//                    currentMonthStats.uniqueUnlisted);
////                    String.valueOf(totalUnlisted),
////                    String.valueOf(uniqueUnlistedCurrentMonth),
////                    String.valueOf(currentMonthMissed),
////                    String.valueOf(fwDaysCurrentMonth),
////                    formattedCallAvgCurrentUnlisted,
////                    formattedCallCvgCurrentUnlisted
////
////            );
//
//            MissedStatsModel previousMonthUnlistedStats = new MissedStatsModel(
//                    "4",
//                    unlistedArray,
//                    previousMonthStats.uniqueUnlisted);
////                    String.valueOf(totalUnlisted),
////                    String.valueOf(uniqueUnlistedPreviousMonth),
////                    String.valueOf(previousMonthMissed),
////                    String.valueOf(fwDaysPreviousMonth),
////                    formattedCallAvgPreviousUnlisted,
////                    formattedCallCvgPreviousUnlisted
////
////            );
//
//            MissedStatsModel prePreviousMonthUnlistedStats = new MissedStatsModel(
//                    "4",
//                    unlistedArray,
//                    prePreviousMonthStats.uniqueUnlisted);
////                    String.valueOf(totalUnlisted),
////                    String.valueOf(uniqueUnlistedPre_PrevMonth),
////                    String.valueOf(prePreviousMonthMissed),
////                    String.valueOf(fwDaysPrePreviousMonth),
////                    formattedCallAvgPre_PreviousUnlisted,
////                    formattedCallCvgPre_PreviousUnlisted
////            );
//
//            //Dr
//            dataList.add(currentMonthDrStats);
//            dataList.add(previousMonthDrStats);
//            dataList.add(prePreviousMonthDrStats);
//            //Che
//            dataList.add(currentMonthCheStats);
//            dataList.add(previousMonthCheStats);
//            dataList.add(prePreviousMonthCheStats);
//            //stk
//            dataList.add(currentMonthStkStats);
//            dataList.add(previousMonthStkStats);
//            dataList.add(prePreviousMonthStkStats);
//            //unlisted
//            dataList.add(currentMonthUnlistedStats);
//            dataList.add(previousMonthUnlistedStats);
//            dataList.add(prePreviousMonthUnlistedStats);
//            // Example monthData
//
//       /*     InnerAdapter adapter = new InnerAdapter( MissedReportGraph.this,
//                    dataListDoc,
//                    dataListChm,
//                    dataListStk,
//                    dataListUnlisted
//            );
//            Log.w("DEBUG", "Adapter created = " + adapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            recyclerView.setAdapter(adapter);*/
//
//            //current month
//            List<MissedStatsModel> currentMonthList = new ArrayList<>();
//            currentMonthList.add(currentMonthDrStats);
//            currentMonthList.add(currentMonthCheStats);
//            currentMonthList.add(currentMonthStkStats);
//            currentMonthList.add(currentMonthUnlistedStats);
//
//            //previous month
//            List<MissedStatsModel> previousMonthList = new ArrayList<>();
//            previousMonthList.add(previousMonthDrStats);
//            previousMonthList.add(previousMonthCheStats);
//            previousMonthList.add(previousMonthStkStats);
//            previousMonthList.add(previousMonthUnlistedStats);
//
//            //pre-previous month
//            List<MissedStatsModel> prePreviousMonthList = new ArrayList<>();
//            prePreviousMonthList.add(prePreviousMonthDrStats);
//            prePreviousMonthList.add(prePreviousMonthCheStats);
//            prePreviousMonthList.add(prePreviousMonthStkStats);
//            prePreviousMonthList.add(prePreviousMonthUnlistedStats);
//
//
////   all months into  list
//            List<List<MissedStatsModel>> allMonthsFlatList = new ArrayList<>();
//            allMonthsFlatList.add(currentMonthList);
//            allMonthsFlatList.add(previousMonthList);
//            allMonthsFlatList.add(prePreviousMonthList);
//
//
//            OuterAdapter adapter = new OuterAdapter(this, allMonthsFlatList, sfCode, date);
//            RecyclerView recyclerView = findViewById(R.id.recyclerDoctorMissedReports);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            recyclerView.setAdapter(adapter);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    //}
}

