package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import saneforce.sanzen.activity.reports.visitMonitor.ChemistVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.DoctorFragment;
import saneforce.sanzen.activity.reports.visitMonitor.StockiestVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.UnlistedVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;

public class ReportPagerAdapter extends FragmentStateAdapter {

//    private final List<String> monthData;
    private final List<VisitStatsModel> doctorStats;
    private final List<VisitStatsModel> chemistStats;
    private final List<VisitStatsModel> stockiestStats;
    private final List<VisitStatsModel> unlistedStats;

    public ReportPagerAdapter(@NonNull FragmentActivity activity,
                              List<VisitStatsModel> doctorStats,
                              List<VisitStatsModel> chemistStats,
                              List<VisitStatsModel> stockiestStats,
                              List<VisitStatsModel> unlistedStats) {
        super(activity);
        this.doctorStats = doctorStats;
        this.chemistStats = chemistStats;
        this.stockiestStats = stockiestStats;
        this.unlistedStats = unlistedStats;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return  DoctorFragment.newInstance(doctorStats,position);
            case 1:
                return  ChemistVisitFragment.newInstance(chemistStats);
            case 2:
                return  StockiestVisitFragment.newInstance(stockiestStats);
            case 3:
                return UnlistedVisitFragment.newInstance(unlistedStats);
            default:
                return DoctorFragment.newInstance(doctorStats,position);

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
