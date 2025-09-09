package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.DoctorFragment;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
public class VisitStatsAdapter extends RecyclerView.Adapter<VisitStatsAdapter.VisitStatsViewHolder>{
    private final List<VisitStatsModel> dataList;
    private final List<String> monthData;
    MasterDataDao masterDataDao;
    Context context;

    public VisitStatsAdapter(List<VisitStatsModel> dataList, List<String> monthData) {
        this.dataList = dataList;

        this.monthData = monthData;
    }
    @NonNull
    @Override
    public VisitStatsAdapter.VisitStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_view_pager, parent, false);
        return new VisitStatsAdapter.VisitStatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitStatsAdapter.VisitStatsViewHolder holder, int position) {
        VisitStatsModel model = dataList.get(position);
        Context context = holder.itemView.getContext();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class VisitStatsViewHolder extends RecyclerView.ViewHolder {

        ViewPager2 viewPager2;

        public VisitStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.visit_pager);
        }
    }

}
