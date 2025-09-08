package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.ChemistVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.DoctorFragment;
import saneforce.sanzen.activity.reports.visitMonitor.StockiestVisitFragment;
import saneforce.sanzen.activity.reports.visitMonitor.UnlistedVisitFragment;
import java.util.List;

public class ReportPagerAdapter extends FragmentStateAdapter {

    private final List<String> monthData;

    public ReportPagerAdapter(@NonNull Fragment fragment, List<String> monthData) {
        super(fragment);
        this.monthData = monthData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return DoctorFragment.newInstance(monthData);
            case 1:
                return ChemistVisitFragment.newInstance(monthData);
            case 2:
                return StockiestVisitFragment.newInstance(monthData);
            case 3:
                return UnlistedVisitFragment.newInstance(monthData);
            default:
                return DoctorFragment.newInstance(monthData);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of report types
    }
}
