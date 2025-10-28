package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.ReportPagerAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;

public class VisitStatsAdapter extends RecyclerView.Adapter<VisitStatsAdapter.VisitStatsViewHolder> {


    private final List<VisitStatsModel> doctorStats;
    private final List<VisitStatsModel> chemistStats;
    private final List<VisitStatsModel> stockiestStats;
    private final List<VisitStatsModel> unlistedStats;
    private final FragmentActivity fragmentActivity;
    int month;
    Context context;

    public VisitStatsAdapter(int month, FragmentActivity fragmentActivity,
                             List<VisitStatsModel> doctorStats,
                             List<VisitStatsModel> chemistStats,
                             List<VisitStatsModel> stockiestStats,
                             List<VisitStatsModel> unlistedStats,Context context) {
        this.month = month;
        this.fragmentActivity = fragmentActivity;
        this.doctorStats = doctorStats;
        this.chemistStats = chemistStats;
        this.stockiestStats = stockiestStats;
        this.unlistedStats = unlistedStats;
        this.context = context;
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
                unlistedStats,
                position
        );
        holder.viewPager2.setAdapter(pagerAdapter);
        holder.viewPager2.setOffscreenPageLimit(pagerAdapter.getItemCount());
        holder.viewPager2.post(()->addDotsIndicator(holder,0));

        holder.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                addDotsIndicator(holder,position);
                Log.d("TAG", "onPageSelected: "+ position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    private void addDotsIndicator(VisitStatsViewHolder holder, int position) {
        int tabCount = holder.viewPager2.getAdapter() != null ? holder.viewPager2.getAdapter().getItemCount() : 0;

        holder.dots = new ImageView[tabCount];
        holder.dotsLayout.removeAllViews();

        for (int i = 0; i < tabCount; i++) {
            holder.dots[i] = new ImageView(context);
            if (i == position) {
                holder.dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));
            } else {
                holder.dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            holder.dotsLayout.addView(holder.dots[i], params);
        }
    }

    @Override
    public int getItemCount() {
        return month;
    }

    static class VisitStatsViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager2;
        private LinearLayout dotsLayout;
        private ImageView[] dots;

        VisitStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.visit_pager);
            dotsLayout = itemView.findViewById(R.id.dotsLayout);
        }
    }
}
