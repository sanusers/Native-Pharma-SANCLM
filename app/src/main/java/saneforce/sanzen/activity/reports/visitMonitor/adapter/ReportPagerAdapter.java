package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.reports.visitMonitor.ChemistVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.DoctorFragment;
import saneforce.sanzen.activity.reports.visitMonitor.StockiestVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.UnlistedVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.storage.SharedPref;

public class ReportPagerAdapter extends FragmentStateAdapter {

//    private final List<String> monthData;
    private final List<VisitStatsModel> doctorStats;
    private final List<VisitStatsModel> chemistStats;
    private final List<VisitStatsModel> stockiestStats;
    private final List<VisitStatsModel> unlistedStats;
    int month;
    private final List<Integer> availableTabs = new ArrayList<>();
    public static final int TAB_DOCTOR = 0;
    public static final int TAB_CHEMIST = 1;
    public static final int TAB_STOCKIEST = 2;
    public static final int TAB_UNLISTED = 3;
    Context context;

    public ReportPagerAdapter(@NonNull FragmentActivity activity,
                              List<VisitStatsModel> doctorStats,
                              List<VisitStatsModel> chemistStats,
                              List<VisitStatsModel> stockiestStats,
                              List<VisitStatsModel> unlistedStats,int month) {
        super(activity);
        this.context = activity;
        this.doctorStats = doctorStats;
        this.chemistStats = chemistStats;
        this.stockiestStats = stockiestStats;
        this.unlistedStats = unlistedStats;
        this.month = month;

        if (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && doctorStats != null && !doctorStats.isEmpty()) availableTabs.add(TAB_DOCTOR);
        if (SharedPref.getChmNeed(context).equalsIgnoreCase("0") && chemistStats != null && !chemistStats.isEmpty()) availableTabs.add(TAB_CHEMIST);
        if (SharedPref.getStkNeed(context).equalsIgnoreCase("0") && stockiestStats != null && !stockiestStats.isEmpty()) availableTabs.add(TAB_STOCKIEST);
        if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0") && unlistedStats != null && !unlistedStats.isEmpty()) availableTabs.add(TAB_UNLISTED);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
      /*  switch (position) {
            case 0:
                return  DoctorFragment.newInstance(doctorStats,month);
            case 1:
                return  ChemistVisitFragment.newInstance(chemistStats,month);
            case 2:
                return  StockiestVisitFragment.newInstance(stockiestStats,month);
            case 3:
                return UnlistedVisitFragment.newInstance(unlistedStats,month);
            default:
                return DoctorFragment.newInstance(doctorStats,month);

        }*/
        int tab = availableTabs.get(position);
        switch (tab) {
            case TAB_DOCTOR:
                return DoctorFragment.newInstance(doctorStats, month);
            case TAB_CHEMIST:
                return ChemistVisitFragment.newInstance(chemistStats, month);
            case TAB_STOCKIEST:
                return StockiestVisitFragment.newInstance(stockiestStats, month);
            case TAB_UNLISTED:
                return UnlistedVisitFragment.newInstance(unlistedStats, month);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return availableTabs.size();
    }
}
