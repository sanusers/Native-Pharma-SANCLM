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
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.storage.SharedPref;

public class MissedReportAdapter extends RecyclerView.Adapter<MissedReportAdapter.ViewHolder> implements Filterable {
    public interface OnItemClickListener {
        void onMissedClick(MissedReportItem item, int position);
    }

    private final Context context;
    private  List<MissedReportItem> fullList;
    private final List<MissedReportItem> reportList;
    private final OnItemClickListener listener;

    public MissedReportAdapter(Context context, List<MissedReportItem> reportList, OnItemClickListener listener) {
        this.context = context;
        this.reportList = reportList;
        this.listener = listener;
        this.fullList = new ArrayList<>(reportList);
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
       // holder.box1Textview.setText(context.getString(R.string.total) + " " + SharedPref.getDrCap(context));
        //        holder.captionTextView.setText(item.getCluster());
       holder.totalDoctorsTextView.setText(item.getTotalDoctors());
//        holder.totalDoctorsTextView.setText(
//                context.getString(R.string.total_doctor) + ": " + item.getTotalDoctors()
//        );

        holder.visitedTextView.setText(item.getVisited());
        holder.missedTextView.setText(item.getMissed());

        //       int totalDoctor = 0;
        //        int visited = 0;
        //        int missed = 0;
        int totalDoctor = Integer.parseInt(item.getTotalDoctors());
        int visited = Integer.parseInt(item.getVisited());
        int missed = Integer.parseInt(item.getMissed());


        String DrCap = SharedPref.getDrCap(context);
        holder.captionTextView.setText(SharedPref.getDrCap(context));
        holder.box1Textview.setText("Total " + SharedPref.getDrCap(context));

        holder.missedBox.setOnClickListener(view ->  {
                if (missed == 0) {
                    Toast.makeText(context, "No missed" + DrCap, Toast.LENGTH_SHORT).show();
                    holder.itemView.setClickable(false);
                    return;
                }
                if (listener != null && !listener.equals("0")) {
                    listener.onMissedClick(item, position);
                }
        });
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
        float missedPercentage =(((float)missed / (float) totalDotor) * 100.0f);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(context.getColor(R.color.green_60));
        colors.add(context.getColor(R.color.light_grey));
        ArrayList<PieEntry> missedDataList = new ArrayList<>();
        missedDataList.add(new PieEntry(100.0f - missedPercentage));//visited 100-missed%
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
        missedChart.setCenterText(String.format(Locale.US,"%.1f %%", missedPercentage));
        Description description2 = missedChart.getDescription();
        description2.setEnabled(false);
        Legend legend2 = missedChart.getLegend();
        legend2.setEnabled(false);
        missedChart.invalidate();
    }
    public void updateData(List<MissedReportItem> newList) {
        Log.d("AdapterUpdate", "updateData called. Size: " + newList.size());

        fullList.clear();
        fullList = new ArrayList<>(newList);
//            reportList.clear();
//            reportList.addAll(newList);

        notifyDataSetChanged();
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("FILTER", "Filtering with: " + constraint);

                List<MissedReportItem> filteredResults = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(fullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (MissedReportItem item : fullList) {
                        if (item.getName().toLowerCase().contains(filterPattern) ||
                                item.getCluster().toLowerCase().contains(filterPattern)) {
                            filteredResults.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                results.count = filteredResults.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                reportList.clear();
                reportList.addAll((List<MissedReportItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }

}