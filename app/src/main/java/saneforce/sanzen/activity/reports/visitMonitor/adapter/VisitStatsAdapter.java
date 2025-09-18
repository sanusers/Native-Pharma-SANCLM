package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.ReportPagerAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;

public class VisitStatsAdapter extends RecyclerView.Adapter<VisitStatsAdapter.VisitStatsViewHolder> {

//    private final List<String> monthData;
    private final List<VisitStatsModel> doctorStats;
    private final List<VisitStatsModel> chemistStats;
    private final List<VisitStatsModel> stockiestStats;
    private final List<VisitStatsModel> unlistedStats;
    private final FragmentActivity fragmentActivity;
    int position;

    public VisitStatsAdapter(FragmentActivity fragmentActivity,
                             List<VisitStatsModel> doctorStats,
                             List<VisitStatsModel> chemistStats,
                             List<VisitStatsModel> stockiestStats,
                             List<VisitStatsModel> unlistedStats) {
        this.fragmentActivity = fragmentActivity;
        this.doctorStats = doctorStats;
        this.chemistStats = chemistStats;
        this.stockiestStats = stockiestStats;
        this.unlistedStats = unlistedStats;
    }

    @NonNull
    @Override
    public VisitStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visit_view_pager, parent, false);
        return new VisitStatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitStatsViewHolder holder, int position) {
        ReportPagerAdapter pagerAdapter = new ReportPagerAdapter(
                fragmentActivity,
                doctorStats,
                chemistStats,
                stockiestStats,
                unlistedStats
        );
        holder.viewPager2.setAdapter(pagerAdapter);


        holder.viewPager2.setOffscreenPageLimit(4);
    }

    @Override
    public int getItemCount() {
    /*    switch (position){
            case 0:
                return doctorStats.size();
            case 1:
                return chemistStats.size();
            case 2:
                return stockiestStats.size();
            case 3:
                return unlistedStats.size();
        }
       return position; */
        return 3;
    }

    static class VisitStatsViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager2;
        VisitStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.visit_pager);
        }
    }
}
