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
import saneforce.sanzen.activity.reports.visitMonitor.VisitMonitorActivity;
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
        if(SharedPref.getSfType(MissedReportGraph.this).equalsIgnoreCase("2")){
            binding.self.setVisibility(View.GONE);
            binding.tvNote.setText("You Are Live! Select Month to get Data");
            binding.live.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_dark_purple));
            binding.live.setTextColor(getColor(R.color.white));
            loadFragment(new FragmentApprovedCallsMissed());
        }else{
            binding.self.setVisibility(View.VISIBLE);
            loadFragment(new AsOnCallsMissedFragment());
        }

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
                    binding.self.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_dark_purple));
                    binding.self.setTextColor(getColor(R.color.white));
                    break;
                case "Approved Calls":
                    binding.live.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_dark_purple));
                    binding.live.setTextColor(getColor(R.color.white));
                    break;
            }




        hideSystemBars();
        drchmcur = findViewById(id.layoutDrChmCurrent);
        stkunlstcur = findViewById(id.layoutStkUnlstCurrent);
        drchmprev = findViewById(id.layoutDrChmPrev);
        stkunlstprev = findViewById(id.layoutStkUnlstPrev);
        drchmprev1 = findViewById(id.layoutDrChmPrev1);
        stkunlstprev1 = findViewById(id.layoutStkUnlstPrev1);

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
        unlstvisitprev = findViewById(id.check_box_unlstvisted_prev);
        unlstmissedprev = findViewById(id.check_box_unlstmissed_prev);
        prevtotaldr = findViewById(id.totalDrCnt_prev);
        prevtotaldrvisited = findViewById(id.visitedCnt_prev);
        prevtotaldrmissed = findViewById(id.missedCnt_prev);

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


        ChmMissedprev1 = findViewById(id.chemist_missed_previous1);

        StkMissedCurrentnew = findViewById(id.stk_missed_currentnew);
        StkMissedprevnew = findViewById(id.stk_missed_previousnew);
        StkMissedprev1 = findViewById(id.stk_missed_previous1);
        StkMissedprev1new = findViewById(id.stk_missed_previous1new);

        UnlstMissedCurrent = findViewById(id.unlst_missed_current);
        UnlstMissedCurrentnew = findViewById(id.unlst_missed_currentnew);
        UnlstMissedprev = findViewById(id.unlst_missed_previous);
        UnlstMissedprevnew = findViewById(id.unlst_missed_previousnew);
        UnlstMissedprev1 = findViewById(id.unlst_missed_previous1);
        UnlstMissedprev1new = findViewById(id.unlst_missed_previous1new);


        pBarunlstprev = findViewById(id.pBarunlst_prev);
        pBarstknewprev = findViewById(id.pBarstknew_prev);
        pBarunlstnewprev = findViewById(id.pBarunlstnew_prev);

        pBardrprev1 = findViewById(id.pBar_prev1);
        pBarchmprev1 = findViewById(id.pBarchm_prev1);
        pBarstkprev1 = findViewById(id.pBarstk_prev1);
        pBarunlstprev1 = findViewById(id.pBarunlst_prev1);
        pBarstknewprev1 = findViewById(id.pBarstknew_prev1);
        pBarunlstnewprev1 = findViewById(id.pBarunlstnew_prev1);


        pBarunlstprevpercentage = findViewById(id.pbar_unlstpercentageprev);
        pBarstknewprevpercentage = findViewById(id.pbar_stknewpercentageprev);
        pBarunlstnewprevpercentage = findViewById(id.pbar_unlstnewpercentageprev);

        pbarprev1percentage = findViewById(id.pbar_percentageprev1);
        pBarchmprev1percentage = findViewById(id.pbar_chmpercentageprev1);
        pBarstkprev1percentage = findViewById(id.pbar_stkpercentageprev1);
        pBarunlstprev1percentage = findViewById(id.pbar_unlstpercentageprev1);
        pBarstknewprev1percentage = findViewById(id.pbar_stknewpercentageprev1);
        pBarunlstnewprev1percentage = findViewById(id.pbar_unlstnewpercentageprev1);

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
}

