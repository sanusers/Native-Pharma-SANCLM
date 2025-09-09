package saneforce.sanzen.activity.reports.missedReport;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanzen.R;
import saneforce.sanzen.databinding.ActivityMissedReportGraphBinding;
import saneforce.sanzen.storage.SharedPref;

public class MissedReportGraph  extends AppCompatActivity {
    private ActivityMissedReportGraphBinding binding;
    ProgressBar pBardrcurrent,pBarchmcurrent,pBarstkcurrent,pBarunlstcurrent;
    ProgressBar pBardrprev,pBarchmprev,pBarstkprev,pBarunlstprev;
    ProgressBar pBardrprev1,pBarchmprev1,pBarstkprev1,pBarunlstprev1;
    TextView pbarcurrentpercentage,pBarchmcurrentpercentage,pBarstkcurrentpercentage,pBarunlstcurrentpercentage;
    TextView pbarprevpercentage,pBarchmprevpercentage,pBarstkprevpercentage,pBarunlstprevpercentage;
    TextView pbarprev1percentage,pBarchmprev1percentage,pBarstkprev1percentage,pBarunlstprev1percentage;
    LinearLayout DrMissedCurrent,DrMissedCurrentprev,DrMissedCurrentprev1;
    LinearLayout ChmMissedCurrent,ChmMissedprev,ChmMissedprev1;
    LinearLayout StkMissedCurrent,StkMissedCurrentnew,StkMissedprev,StkMissedprevnew,StkMissedprev1,StkMissedprev1new;
    LinearLayout UnlstMissedCurrent,UnlstMissedCurrentnew,UnlstMissedprev,UnlstMissedprevnew,UnlstMissedprev1,UnlstMissedprev1new;
    static int pBarCount = 1;
    int maxCount=0,currentCount=0;
    View drchmcur,stkunlstcur,drchmprev,stkunlstprev,drchmprev1,stkunlstprev1;
    View drvisitcur,drmissedcur,chmvisitcur,chmmissedcur,stkvisitcur,stkmissedcur,unlstvisitcur,unlstmissedcur;
    View drvisitprev,drmissedprev,chmvisitprev,chmmissedprev,stkvisitprev,stkmissedprev,unlstvisitprev,unlstmissedprev;
    View drvisitprev1,drmissedprev1,chmvisitprev1,chmmissedprev1,stkvisitprev1,stkmissedprev1,unlstvisitprev1,unlstmissedprev1;
    TextView totaldrcur,totaldrvisited,totaldrmissed,totalchmcur,totalchmvisited,totalchmmissed,totalstkcur,totalstkvisited,totalstkmissed,totalunlstcur,totalunlstvisited,totalunlstmissed;
    TextView prevtotaldr,prevtotaldrvisited,prevtotaldrmissed,prevtotalchmcur,prevtotalchmvisited,prevtotalchmmissed,prevtotalstkcur,prevtotalstkvisited,prevtotalstkmissed,prevtotalunlstcur,prevtotalunlstvisited,prevtotalunlstmissed;
    TextView prev1totaldr,prev1totaldrvisited,prev1totaldrmissed,prev1totalchmcur,prev1totalchmvisited,prev1totalchmmissed,prev1totalstkcur,prev1totalstkvisited,prev1totalstkmissed,prev1totalunlstcur,prev1totalunlstvisited,prev1totalunlstmissed;
    View stknewvisitcur,stknewmissedcur,unlstnewvisitcur,unlstnewmissedcur;
    View stkprevvisitcur,stkprevmissedcur,unlstprevvisitcur,unlstprevmissedcur;
    View stkprev1visitcur,stkprev1missedcur,unlstprev1visitcur,unlstprev1missedcur;
    TextView totalstknewcur,totalstknewvisited,totalstknewmissed,totalunlstnewcur,totalunlstnewvisited,totalunlstnewmissed;
    TextView totalstkprevcur,totalstkprevvisited,totalstkprevmissed,totalunlstprevcur,totalunlstprevvisited,totalunlstprevmissed;

    TextView totalstkprev1cur,totalstkprev1visited,totalstkprev1missed,totalunlstprev1cur,totalunlstprev1visited,totalunlstprev1missed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMissedReportGraphBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideSystemBars();

        drchmcur= findViewById(R.id.layoutDrChmCurrent);
        stkunlstcur= findViewById(R.id.layoutStkUnlstCurrent);
        drchmprev= findViewById(R.id.layoutDrChmPrev);
        stkunlstprev= findViewById(R.id.layoutStkUnlstPrev);
        drchmprev1= findViewById(R.id.layoutDrChmPrev1);
        stkunlstprev1= findViewById(R.id.layoutStkUnlstPrev1);

        //current drchm layout,stk,unlst hidden
        drvisitcur= findViewById(R.id.visitedcheck);
        drmissedcur= findViewById(R.id.missedcheck);
        chmvisitcur= findViewById(R.id.check_box_chmvisted);
        chmmissedcur= findViewById(R.id.check_box_chmmissed);
        stkvisitcur= findViewById(R.id.stkvisitedcheck);
        stkmissedcur= findViewById(R.id.stkmissedcheck);
        unlstvisitcur= findViewById(R.id.check_box_unlstvisted);
        unlstmissedcur= findViewById(R.id.check_box_unlstmissed);
        totaldrcur=findViewById(R.id.totalDrCnt);
        totaldrvisited=findViewById(R.id.visitedCnt);
        totaldrmissed=findViewById(R.id.missedCnt);
        totalchmcur=findViewById(R.id.totalchmCnt);
        totalchmvisited=findViewById(R.id.chmvisitedCnt);
        totalchmmissed=findViewById(R.id.chmmissedCnt);
        totalstkcur=findViewById(R.id.totalstkCnt);
        totalstkvisited=findViewById(R.id.stkvisitedCnt);
        totalstkmissed=findViewById(R.id.stkmissedCnt);
        totalunlstcur=findViewById(R.id.totalunlstCnt);
        totalunlstvisited=findViewById(R.id.unlstvisitedCnt);
        totalunlstmissed=findViewById(R.id.unlstmissedCnt);
        //current stk,unlst layout
        stknewvisitcur=findViewById(R.id.stknewvisitedcheck);
        stknewmissedcur=findViewById(R.id.stknewmissedcheck);
        unlstnewvisitcur=findViewById(R.id.unlstnewvistedcheck);
        unlstnewmissedcur=findViewById(R.id.unlstnewmissedcheck);
        totalstknewcur=findViewById(R.id.totalstknewCntcur);
        totalstknewvisited=findViewById(R.id.stknewvisitedCntcur);
        totalstknewmissed=findViewById(R.id.stknewmissedCntcur);
        totalunlstnewcur=findViewById(R.id.totalunlstnewCntcur);
        totalunlstnewvisited=findViewById(R.id.unlstnewvisitedCntcur);
        totalunlstnewmissed=findViewById(R.id.unlstnewmissedCntcur);

        //prev drchm layout,stk,unlst hidden
        drvisitprev= findViewById(R.id.visitedcheck_prev);
        drmissedprev= findViewById(R.id.missedcheck_prev);
        chmvisitprev= findViewById(R.id.check_box_chmvisted_prev);
        chmmissedprev= findViewById(R.id.check_box_chmmissed_prev);
        stkvisitprev= findViewById(R.id.stkvisitedcheck_prev);
        stkmissedprev= findViewById(R.id.stkmissedcheck_prev);
        unlstvisitprev= findViewById(R.id.check_box_unlstvisted_prev);
        unlstmissedprev= findViewById(R.id.check_box_unlstmissed_prev);
        prevtotaldr=findViewById(R.id.totalDrCnt_prev);
        prevtotaldrvisited=findViewById(R.id.visitedCnt_prev);
        prevtotaldrmissed=findViewById(R.id.missedCnt_prev);
        prevtotalchmcur=findViewById(R.id.totalchmCnt_prev);
        prevtotalchmvisited=findViewById(R.id.chmvisitedCnt_prev);
        prevtotalchmmissed=findViewById(R.id.chmmissedCnt_prev);
        prevtotalstkcur=findViewById(R.id.totalstkCnt_prev);
        prevtotalstkvisited=findViewById(R.id.stkvisitedCnt_prev);
        prevtotalstkmissed=findViewById(R.id.stkmissedCnt_prev);
        prevtotalunlstcur=findViewById(R.id.totalunlstCnt_prev);
        prevtotalunlstvisited=findViewById(R.id.unlstvisitedCnt_prev);
        prevtotalunlstmissed=findViewById(R.id.unlstmissedCnt_prev);
        //prev stk,unlst layout
        stkprevvisitcur=findViewById(R.id.stknewvisitedcheck_prev);
        stkprevmissedcur=findViewById(R.id.stknewmissedcheck_prev);
        unlstprevvisitcur=findViewById(R.id.check_box_unlstnewvisted_prev);
        unlstprevmissedcur=findViewById(R.id.check_box_unlstnewmissed_prev);
        totalstkprevcur=findViewById(R.id.totalstknewCnt_prev);
        totalstkprevvisited=findViewById(R.id.stknewvisitedCnt_prev);
        totalstkprevmissed=findViewById(R.id.stknewmissedCnt_prev);
        totalunlstprevcur=findViewById(R.id.totalunlstnewCnt_prev);
        totalunlstprevvisited=findViewById(R.id.unlstnewvisitedCnt_prev);
        totalunlstprevmissed=findViewById(R.id.unlstnewmissedCnt_prev);

        //prev1 drchm layout,stk,unlst hidden
        drvisitprev1= findViewById(R.id.visitedcheck_prev2);
        drmissedprev1= findViewById(R.id.missedcheck_prev2);
        chmvisitprev1= findViewById(R.id.check_box_chmvisted_prev2);
        chmmissedprev1= findViewById(R.id.check_box_chmmissed_prev2);
        stkvisitprev1= findViewById(R.id.stkvisitedcheck_prev2);
        stkmissedprev1= findViewById(R.id.stkmissedcheck_prev2);
        unlstvisitprev1= findViewById(R.id.check_box_unlstvisted_prev2);
        unlstmissedprev1= findViewById(R.id.check_box_unlstmissed_prev2);
        prev1totaldr=findViewById(R.id.totalDrCnt_prev2);
        prev1totaldrvisited=findViewById(R.id.visitedCnt_prev2);
        prev1totaldrmissed=findViewById(R.id.missedCnt_prev2);
        prev1totalchmcur=findViewById(R.id.totalchmCnt_prev2);
        prev1totalchmvisited=findViewById(R.id.chmvisitedCnt_prev2);
        prev1totalchmmissed=findViewById(R.id.chmmissedCnt_prev2);
        prev1totalstkcur=findViewById(R.id.totalstkCnt_prev2);
        prev1totalstkvisited=findViewById(R.id.stkvisitedCnt_prev2);
        prev1totalstkmissed=findViewById(R.id.stkmissedCnt_prev2);
        prev1totalunlstcur=findViewById(R.id.totalunlstCnt_prev2);
        prev1totalunlstvisited=findViewById(R.id.unlstvisitedCnt_prev2);
        prev1totalunlstmissed=findViewById(R.id.unlstmissedCnt_prev2);
        //prev1 stk,unlst layout
        stkprev1visitcur=findViewById(R.id.stknewvisitedcheck_prev2);
        stkprev1missedcur=findViewById(R.id.stknewmissedcheck_prev2);
        unlstprev1visitcur=findViewById(R.id.check_box_unlstnewvisted_prev2);
        unlstprev1missedcur=findViewById(R.id.check_box_unlstnewmissed_prev2);
        totalstkprev1cur=findViewById(R.id.totalstknewCnt_prev2);
        totalstkprev1visited=findViewById(R.id.stknewvisitedCnt_prev2);
        totalstkprev1missed=findViewById(R.id.stknewmissedCnt_prev2);
        totalunlstprev1cur=findViewById(R.id.totalunlstnewCnt_prev2);
        totalunlstprev1visited=findViewById(R.id.unlstnewvisitedCnt_prev2);
        totalunlstprev1missed=findViewById(R.id.unlstnewmissedCnt_prev2);

        DrMissedCurrent = findViewById(R.id.doctor_missed_current);
        DrMissedCurrentprev = findViewById(R.id.doctor_missed_previous);
        DrMissedCurrentprev1 = findViewById(R.id.doctor_missed_previous1);

        ChmMissedCurrent = findViewById(R.id.chemist_missed_current);
        ChmMissedprev = findViewById(R.id.chemist_missed_previous);
        ChmMissedprev1 = findViewById(R.id.chemist_missed_previous1);

        StkMissedCurrent = findViewById(R.id.stk_missed_current);
        StkMissedCurrentnew = findViewById(R.id.stk_missed_currentnew);
        StkMissedprev = findViewById(R.id.stk_missed_previous);
        StkMissedprevnew = findViewById(R.id.stk_missed_previousnew);
        StkMissedprev1 = findViewById(R.id.stk_missed_previous1);
        StkMissedprev1new = findViewById(R.id.stk_missed_previous1new);

        UnlstMissedCurrent = findViewById(R.id.unlst_missed_current);
        UnlstMissedCurrentnew = findViewById(R.id.unlst_missed_currentnew);
        UnlstMissedprev = findViewById(R.id.unlst_missed_previous);
        UnlstMissedprevnew = findViewById(R.id.unlst_missed_previousnew);
        UnlstMissedprev1 = findViewById(R.id.unlst_missed_previous1);
        UnlstMissedprev1new = findViewById(R.id.unlst_missed_previous1new);

        pBardrcurrent=findViewById(R.id.pBar);
        pBarchmcurrent=findViewById(R.id.pBarchm);
        pBarstkcurrent=findViewById(R.id.pBarstk);
        pBarunlstcurrent=findViewById(R.id.pBarunlst);

        pBardrprev=findViewById(R.id.pBar_prev);
        pBarchmprev=findViewById(R.id.pBarchm_prev);
        pBarstkprev=findViewById(R.id.pBarstk_prev);
        pBarunlstprev=findViewById(R.id.pBarunlst_prev);

        pBardrprev1=findViewById(R.id.pBar_prev1);
        pBarchmprev1=findViewById(R.id.pBarchm_prev1);
        pBarstkprev1=findViewById(R.id.pBarstk_prev1);
        pBarunlstprev1=findViewById(R.id.pBarunlst_prev1);

        pbarcurrentpercentage =findViewById(R.id.pbar_percentage);
        pBarchmcurrentpercentage =findViewById(R.id.pbar_chmpercentage);
        pBarstkcurrentpercentage =findViewById(R.id.pbar_stkpercentage);
        pBarunlstcurrentpercentage =findViewById(R.id.pbar_unlstpercentage);

        pbarprevpercentage =findViewById(R.id.pbar_percentageprev);
        pBarchmprevpercentage =findViewById(R.id.pbar_chmpercentageprev);
        pBarstkprevpercentage =findViewById(R.id.pbar_stkpercentageprev);
        pBarunlstprevpercentage =findViewById(R.id.pbar_unlstpercentageprev);

        pbarprev1percentage =findViewById(R.id.pbar_percentageprev1);
        pBarchmprev1percentage =findViewById(R.id.pbar_chmpercentageprev1);
        pBarstkprev1percentage =findViewById(R.id.pbar_stkpercentageprev1);
        pBarunlstprev1percentage =findViewById(R.id.pbar_unlstpercentageprev1);

        String dr  = SharedPref.getDrNeed(this);
        String chm = SharedPref.getChmNeed(this);
        String stk = SharedPref.getStkNeed(this);
        String unl = SharedPref.getUnlNeed(this);

        boolean isDr  = dr.equalsIgnoreCase("0");
        boolean isChm = chm.equalsIgnoreCase("0");
        boolean isStk = stk.equalsIgnoreCase("0");
        boolean isUnl = unl.equalsIgnoreCase("0");

       // Build key → "1" if value == "0", else "0"
        String key = (isDr ? "1" : "0") +
                (isChm ? "1" : "0") +
                (isStk ? "1" : "0") +
                (isUnl ? "1" : "0");

        switch (key) {
            case "0000":
                // none = "0"
                LoadDefaultZeroValues();
                break;
            case "1000":
                // only DrNeed = "0"
                LoadDrOnlyValues();
                break;
            case "0100":
                // only ChmNeed = "0"
                LoadChmOnlyValues();
                break;
            case "0010":
                // only StkNeed = "0"
                LoadStkOnlyValues();
                break;
            case "0001":
                // only UnlNeed = "0"
                LoadUnlstOnlyValues();
                break;
            case "1100":
                // DrNeed + ChmNeed = "0"
                LoadDrChmValues();
                break;
            case "1010":
                // DrNeed + StkNeed = "0"
                LoadDrStkValues();
                break;
            case "1001":
                // DrNeed + UnlNeed = "0"
                LoadDrUnlstValues();
                break;
            case "0110":
                // ChmNeed + StkNeed = "0"
                LoadChmStkValues();
                break;
            case "0101":
                // ChmNeed + UnlNeed = "0"
                LoadChmUnlstValues();
                break;
            case "0011":
                // StkNeed + UnlNeed = "0"
                LoadStkUnlstValues();
                break;
            case "1110":
                // DrNeed + ChmNeed + StkNeed = "0"
                LoadDrChmStkValues();
                break;
            case "1101":
                // DrNeed + ChmNeed + UnlNeed = "0"
                LoadDrChmUnlstValues();
                break;
            case "1011":
                // DrNeed + StkNeed + UnlNeed = "0"
                LoadDrStkUnlstValues();
                break;
            case "0111":
                // ChmNeed + StkNeed + UnlNeed = "0"
                LoadChmStkUnlstValues();
                break;
            case "1111":
                // All = "0"
                LoadDrChmStkUnlstValues();
                break;
        }

        DrMissedCurrent.setOnClickListener(v -> {
            Intent intentWeb= new Intent(MissedReportGraph.this, MissedReport.class);
            MissedReportGraph.this.startActivity(intentWeb);
        });
        DrMissedCurrentprev.setOnClickListener(v -> {
            Intent intentWeb= new Intent(MissedReportGraph.this, MissedReport.class);
            MissedReportGraph.this.startActivity(intentWeb);
        });
        DrMissedCurrentprev1.setOnClickListener(v -> {
            Intent intentWeb= new Intent(MissedReportGraph.this, MissedReport.class);
            MissedReportGraph.this.startActivity(intentWeb);
        });
        binding.imageBack.setOnClickListener(v -> {
            finish();
        });

        // Add swipe listeners
        setSwipeListener(binding.viewFlipper);
        setSwipeListener(binding.viewFlipper2);
        setSwipeListener(binding.viewFlipper3);
    }
    public static void progressBarAnimation(final int max,ProgressBar pBar)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                if (max != 0) {
                    while (pBarCount <= max) {
                        try {
                            pBar.setProgress(max);
                        }
                        catch (Exception e) {
                        }
                    }
                }
                else {
                    try {
                        pBar.setProgress(0);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void setSwipeListener(ViewFlipper flipper) {
        flipper.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();
                        float moveY = event.getY();
                        float diffX = moveX - downX;
                        float diffY = moveY - downY;

                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        float upX = event.getX();
                        float deltaX = upX - downX; // 👉 Right swipe = positive, Left swipe = negative

                        int currentIndex = flipper.getDisplayedChild();
                        int childCount = flipper.getChildCount();

                        if (Math.abs(deltaX) > 150) { // threshold
                            if (deltaX < 0) { // 👉 Swipe Left → Next
                                if (currentIndex < childCount - 1) {
                                    flipper.setInAnimation(MissedReportGraph.this, R.anim.slide_in_right);
                                    flipper.setOutAnimation(MissedReportGraph.this, R.anim.slide_out_left);
                                    flipper.showNext();
                                    return true;
                                }
                            } else { // 👉 Swipe Right → Previous
                                if (currentIndex > 0) {
                                    flipper.setInAnimation(MissedReportGraph.this, R.anim.slide_in_left);
                                    flipper.setOutAnimation(MissedReportGraph.this, R.anim.slide_out_right);
                                    flipper.showPrevious();
                                    return true;
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });
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

    public void LoadDefaultZeroValues()
    {
        binding.viewFlipper.setVisibility(View.GONE);
        binding.viewFlipper2.setVisibility(View.GONE);
        binding.viewFlipper3.setVisibility(View.GONE);
    }

    public void LoadDrOnlyValues(){
        ChmMissedCurrent.setVisibility(View.GONE);
        ChmMissedprev.setVisibility(View.GONE);
        ChmMissedprev1.setVisibility(View.GONE);
        stkunlstcur.setVisibility(View.GONE);
        stkunlstprev.setVisibility(View.GONE);
        stkunlstprev1.setVisibility(View.GONE);
    }
    public void LoadChmOnlyValues(){
        DrMissedCurrent.setVisibility(View.GONE);
        DrMissedCurrentprev.setVisibility(View.GONE);
        DrMissedCurrentprev1.setVisibility(View.GONE);
        stkunlstcur.setVisibility(View.GONE);
        stkunlstprev.setVisibility(View.GONE);
        stkunlstprev1.setVisibility(View.GONE);
    }
    public void LoadStkOnlyValues(){
        UnlstMissedCurrentnew.setVisibility(View.GONE);
        UnlstMissedprevnew.setVisibility(View.GONE);
        UnlstMissedprev1new.setVisibility(View.GONE);
        drchmcur.setVisibility(View.GONE);
        drchmprev.setVisibility(View.GONE);
        drchmprev1.setVisibility(View.GONE);
    }
    public void LoadUnlstOnlyValues(){
        StkMissedCurrentnew.setVisibility(View.GONE);
        StkMissedprevnew.setVisibility(View.GONE);
        StkMissedprev1new.setVisibility(View.GONE);
        drchmcur.setVisibility(View.GONE);
        drchmprev.setVisibility(View.GONE);
        drchmprev1.setVisibility(View.GONE);
    }
    public void LoadDrChmValues(){
        stkunlstcur.setVisibility(View.GONE);
        stkunlstprev.setVisibility(View.GONE);
        stkunlstprev1.setVisibility(View.GONE);
    }
    public void LoadDrStkValues(){
        ChmMissedCurrent.setVisibility(View.GONE);
        StkMissedCurrent.setVisibility(View.VISIBLE);
        ChmMissedprev.setVisibility(View.GONE);
        StkMissedprev.setVisibility(View.VISIBLE);
        ChmMissedprev1.setVisibility(View.GONE);
        StkMissedprev1.setVisibility(View.VISIBLE);
        stkunlstcur.setVisibility(View.GONE);
        stkunlstprev.setVisibility(View.GONE);
        stkunlstprev1.setVisibility(View.GONE);
    }
    public void LoadDrUnlstValues(){
        ChmMissedCurrent.setVisibility(View.GONE);
        UnlstMissedCurrent.setVisibility(View.VISIBLE);
        ChmMissedprev.setVisibility(View.GONE);
        UnlstMissedprev.setVisibility(View.VISIBLE);
        ChmMissedprev1.setVisibility(View.GONE);
        UnlstMissedprev1.setVisibility(View.VISIBLE);
        stkunlstcur.setVisibility(View.GONE);
        stkunlstprev.setVisibility(View.GONE);
        stkunlstprev1.setVisibility(View.GONE);
    }
    public void LoadChmStkValues(){
        DrMissedCurrent.setVisibility(View.GONE);
        StkMissedCurrent.setVisibility(View.VISIBLE);
        DrMissedCurrentprev.setVisibility(View.GONE);
        StkMissedprev.setVisibility(View.VISIBLE);
        DrMissedCurrentprev1.setVisibility(View.GONE);
        StkMissedprev1.setVisibility(View.VISIBLE);
        stkunlstcur.setVisibility(View.GONE);
        stkunlstprev.setVisibility(View.GONE);
        stkunlstprev1.setVisibility(View.GONE);
    }
    public void LoadChmUnlstValues(){
        DrMissedCurrent.setVisibility(View.GONE);
        UnlstMissedCurrent.setVisibility(View.VISIBLE);
        DrMissedCurrentprev.setVisibility(View.GONE);
        UnlstMissedprev.setVisibility(View.VISIBLE);
        DrMissedCurrentprev1.setVisibility(View.GONE);
        UnlstMissedprev1.setVisibility(View.VISIBLE);
        stkunlstcur.setVisibility(View.GONE);
        stkunlstprev.setVisibility(View.GONE);
        stkunlstprev1.setVisibility(View.GONE);
    }
    public void LoadStkUnlstValues(){
        drchmcur.setVisibility(View.GONE);
        drchmprev.setVisibility(View.GONE);
        drchmprev1.setVisibility(View.GONE);
    }
    public void LoadDrChmStkValues(){
        UnlstMissedCurrentnew.setVisibility(View.GONE);
        UnlstMissedprevnew.setVisibility(View.GONE);
        UnlstMissedprev1new.setVisibility(View.GONE);
    }
    public void LoadDrChmUnlstValues(){
        StkMissedCurrentnew.setVisibility(View.GONE);
        StkMissedprevnew.setVisibility(View.GONE);
        StkMissedprev1new.setVisibility(View.GONE);
    }
    public void LoadDrStkUnlstValues(){
        ChmMissedCurrent.setVisibility(View.GONE);
        StkMissedCurrent.setVisibility(View.VISIBLE);
        ChmMissedprev.setVisibility(View.GONE);
        StkMissedprev.setVisibility(View.VISIBLE);
        ChmMissedprev1.setVisibility(View.GONE);
        StkMissedprev1.setVisibility(View.VISIBLE);
        StkMissedCurrentnew.setVisibility(View.GONE);
        StkMissedprevnew.setVisibility(View.GONE);
        StkMissedprev1new.setVisibility(View.GONE);
    }
    public void LoadChmStkUnlstValues(){
        DrMissedCurrent.setVisibility(View.GONE);
        DrMissedCurrentprev.setVisibility(View.GONE);
        DrMissedCurrentprev1.setVisibility(View.GONE);
        StkMissedCurrent.setVisibility(View.VISIBLE);
        StkMissedCurrentnew.setVisibility(View.GONE);
        StkMissedprev.setVisibility(View.VISIBLE);
        StkMissedprevnew.setVisibility(View.GONE);
        StkMissedprev1.setVisibility(View.VISIBLE);
        StkMissedprev1new.setVisibility(View.GONE);
    }
    public void LoadDrChmStkUnlstValues(){
        //Dr Current Month
        pBardrcurrent.setMax(maxCount);
        pbarcurrentpercentage.setText(String.valueOf(currentCount)+"%");
        pBardrcurrent.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBardrcurrent);
        totaldrcur.setText("");
        totaldrvisited.setText("");
        totaldrmissed.setText("");
        //Dr Prev Month
        pBardrprev.setMax(maxCount);
        pbarprevpercentage.setText(String.valueOf(currentCount)+"%");
        pBardrprev.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBardrprev);
        prevtotaldr.setText("");
        prevtotaldrvisited.setText("");
        prevtotaldrmissed.setText("");
        //Dr Prev Month
        pBardrprev1.setMax(maxCount);
        pbarprev1percentage.setText(String.valueOf(currentCount)+"%");
        pBardrprev1.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBardrprev1);
        prev1totaldr.setText("");
        prev1totaldrvisited.setText("");
        prev1totaldrmissed.setText("");

        //Chm Current Month
        pBarchmcurrent.setMax(maxCount);
        pBarchmcurrentpercentage.setText(String.valueOf(currentCount)+"%");
        pBarchmcurrent.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarchmcurrent);
        totalchmcur.setText("");
        totalchmvisited.setText("");
        totalchmmissed.setText("");
        //Chm Prev Month
        pBarchmprev.setMax(maxCount);
        pBarchmprevpercentage.setText(String.valueOf(currentCount)+"%");
        pBarchmprev.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarchmprev);
        prevtotalchmcur.setText("");
        prevtotalchmvisited.setText("");
        prevtotalchmmissed.setText("");
        //Chm Prev Month
        pBarchmprev1.setMax(maxCount);
        pBarchmprev1percentage.setText(String.valueOf(currentCount)+"%");
        pBarchmprev1.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarchmprev1);
        prev1totalchmcur.setText("");
        prev1totalchmvisited.setText("");
        prev1totalchmmissed.setText("");

        //Stk Current Month
        pBarstkcurrent.setMax(maxCount);
        pBarstkcurrentpercentage.setText(String.valueOf(currentCount)+"%");
        pBarstkcurrent.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarstkcurrent);
        totalstkcur.setText("");
        totalstkvisited.setText("");
        totalstkmissed.setText("");
        //Stk Prev Month
        pBarstkprev.setMax(maxCount);
        pBarstkprevpercentage.setText(String.valueOf(currentCount)+"%");
        pBarstkprev.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarstkprev);
        prevtotalstkcur.setText("");
        prevtotalstkvisited.setText("");
        prevtotalstkmissed.setText("");
        //Stk Prev Month
        pBarstkprev1.setMax(maxCount);
        pBarstkprev1percentage.setText(String.valueOf(currentCount)+"%");
        pBarstkprev1.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarstkprev1);
        prev1totalstkcur.setText("");
        prev1totalstkvisited.setText("");
        prev1totalstkmissed.setText("");

        //Unlst Current Month
        pBarunlstcurrent.setMax(maxCount);
        pBarunlstcurrentpercentage.setText(String.valueOf(currentCount)+"%");
        pBarunlstcurrent.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarunlstcurrent);
        totalunlstcur.setText("");
        totalunlstvisited.setText("");
        totalunlstmissed.setText("");
        //Unlst Prev Month
        pBarunlstprev.setMax(maxCount);
        pBarunlstprevpercentage.setText(String.valueOf(currentCount)+"%");
        pBarunlstprev.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarunlstprev);
        prevtotalunlstcur.setText("");
        prevtotalunlstvisited.setText("");
        prevtotalunlstmissed.setText("");
        //Unlst Prev Month
        pBarunlstprev1.setMax(maxCount);
        pBarunlstprev1percentage.setText(String.valueOf(currentCount)+"%");
        pBarunlstprev1.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBarAnimation(currentCount,pBarunlstprev1);
        prev1totalunlstcur.setText("");
        prev1totalunlstvisited.setText("");
        prev1totalunlstmissed.setText("");
    }
}
