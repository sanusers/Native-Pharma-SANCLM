// InnerAdapter.java
package saneforce.sanzen.activity.reports.missedReport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import saneforce.sanzen.R;

public class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.InnerAdapterViewHolder> {

    private final List<InnerAdapter> doctorStats;
    private final List<InnerAdapter> chemistStats;
    private final List<InnerAdapter> stockiestStats;
    private final List<InnerAdapter> unlistedStats;
    private final FragmentActivity fragmentActivity;
    int position;

    public InnerAdapter(FragmentActivity fragmentActivity,
                             List<InnerAdapter> doctorStats,
                             List<InnerAdapter> chemistStats,
                             List<InnerAdapter> stockiestStats,
                             List<InnerAdapter> unlistedStats) {
        this.fragmentActivity = fragmentActivity;
        this.doctorStats = doctorStats;
        this.chemistStats = chemistStats;
        this.stockiestStats = stockiestStats;
        this.unlistedStats = unlistedStats;
    }

    @NonNull
    @Override
    public InnerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new InnerAdapter.InnerAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull InnerAdapter.InnerAdapterViewHolder holder, int position) {
        InnerAdapter InnerAdapter = new InnerAdapter(
                fragmentActivity,
                doctorStats,
                chemistStats,
                stockiestStats,
                unlistedStats
        );
        holder.viewPager2.setAdapter(InnerAdapter);


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

    static class InnerAdapterViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager2;
        InnerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.missed_pager);
        }


    }
}
