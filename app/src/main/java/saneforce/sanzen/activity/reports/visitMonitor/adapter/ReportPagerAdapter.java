package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import saneforce.sanzen.activity.reports.visitMonitor.ChemistVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.DoctorVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.StockiestVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.UnlistedVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;

public class ReportPagerAdapter extends FragmentStateAdapter {

    private final List<String> monthData;

    public ReportPagerAdapter(@NonNull FragmentActivity activity,
                              List<String> monthData,
                              List<VisitStatsModel> doctorStats,
                              List<VisitStatsModel> chemistStats,
                              List<VisitStatsModel> stockiestStats,
                              List<VisitStatsModel> unlistedStats) {
        super(activity);
        this.monthData = monthData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return DoctorVisitFragment.newInstance(monthData);
            case 1: return ChemistVisitFragment.newInstance(monthData);
            case 2: return StockiestVisitFragment.newInstance(monthData);
            case 3: return UnlistedVisitFragment.newInstance(monthData);
            default: return DoctorVisitFragment.newInstance(monthData);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
