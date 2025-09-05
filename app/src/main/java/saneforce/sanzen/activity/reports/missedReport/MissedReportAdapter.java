package saneforce.sanzen.activity.reports.missedReport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.storage.SharedPref;

public class MissedReportAdapter extends RecyclerView.Adapter<MissedReportAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onMissedClick(MissedReportItem item, int position);
    }

    private final Context context;
    private final List<MissedReportItem> reportList;
    private final OnItemClickListener listener;

    public MissedReportAdapter(Context context, List<MissedReportItem> reportList, OnItemClickListener listener) {
        this.context = context;
        this.reportList = reportList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_missed_report, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MissedReportItem item = reportList.get(position);
        holder.nameTextView.setText(item.getName() + " -     " + item.getCluster());
//        holder.captionTextView.setText(item.getCluster());
        holder.totalDoctorsTextView.setText(item.getTotalDoctors());
        holder.visitedTextView.setText(item.getVisited());
        holder.missedTextView.setText(item.getMissed());

//       int totalDoctor = 0;
//        int visited = 0;
//        int missed = 0;
        int totalDoctor = Integer.parseInt(item.getTotalDoctors());
        int visited = Integer.parseInt(item.getVisited());
        int missed = Integer.parseInt(item.getMissed());


//        if (missed == 0) {
//            holder.visitedTextView.setText(String.valueOf(totalDoctor));
//        } else {
//            holder.visitedTextView.setText(item.getVisited());
//        }
        String DrCap = SharedPref.getDrCap(context);
        holder.captionTextView.setText(SharedPref.getDrCap(context));
        holder.box1Textview.setText("Total " + SharedPref.getDrCap(context));
//        holder.missedBox.setOnClickListener(v -> {
//            Log.d("MissedReportAdapter", "missedBox clicked at position: " + position);
//            if (listener != null) {
//                listener.onMissedClick(item, position);
//            } else {
//                Log.d("MissedReportAdapter", "Listener is null!");
//            }
//        });

        holder.missedBox.setOnClickListener(v -> {
            if (missed == 0) {
                Toast.makeText(context, "No missed"+ DrCap , Toast.LENGTH_SHORT).show();
                holder.itemView.setClickable(false);
                return;
            }




            if (listener != null) {
                listener.onMissedClick(item, position);
            }
        });

//            Intent intent = new Intent(context, DoctorVisitActivity.class);
//            intent.putExtra("missed", item.getMissed());
//            context.startActivity(intent);
        setupMissedChart(context, holder.missedChart, totalDoctor, visited, missed);
    }


    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PieChart missedChart;
        TextView nameTextView, captionTextView, totalDoctorsTextView, visitedTextView, missedTextView, box1Textview;
        LinearLayout missedBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.centerText);
            captionTextView = itemView.findViewById(R.id.textDoctor);
            totalDoctorsTextView = itemView.findViewById(R.id.tvCountDoctor);
            box1Textview = itemView.findViewById(R.id.box1);
            visitedTextView = itemView.findViewById(R.id.visitedCount);
            missedTextView = itemView.findViewById(R.id.missedCount);
            missedBox = itemView.findViewById(R.id.missedBox);
            missedChart = itemView.findViewById(R.id.piechart);

        }
    }

    private void setupMissedChart(Context context, PieChart missedChart, int totalDotor, int visited, int missed) {
        int missedPercentage = (missed / totalDotor) * 100;
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(229, 231, 233));
        colors.add(context.getColor(R.color.green_60));
        ArrayList<PieEntry> missedDataList = new ArrayList<>();
        missedDataList.add(new PieEntry(100 - missedPercentage, ""));//visited 100-missed%
        missedDataList.add(new PieEntry(missedPercentage, ""));//Missed %
        PieDataSet missedDataSet = new PieDataSet(missedDataList, "");
        missedDataSet.setColors(colors);
        PieData missedData = new PieData(missedDataSet);
        missedChart.setData(missedData);
        missedData.setValueTextSize(0f);
        missedData.setValueTextColor(Color.WHITE);
        missedChart.setUsePercentValues(true);
        missedChart.setDrawHoleEnabled(true);
        missedChart.setCenterTextSize(18f);
        missedChart.setCenterTextColor(context.getColor(R.color.black));
        missedChart.setTransparentCircleRadius(40f);
        missedChart.setHoleRadius(72f);
        missedChart.animateXY(1400, 1400);
        missedChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        missedChart.setCenterText(missedPercentage + " % ");//missed%value
        Description description2 = missedChart.getDescription();
        description2.setEnabled(false);
        Legend legend2 = missedChart.getLegend();
        legend2.setEnabled(false);
        missedChart.invalidate();
    }
}
