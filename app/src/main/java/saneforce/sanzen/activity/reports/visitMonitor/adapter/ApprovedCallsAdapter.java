package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.missedReport.MissedReportItem;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.storage.SharedPref;


public class ApprovedCallsAdapter extends RecyclerView.Adapter<ApprovedCallsAdapter.ViewHolder> implements Filterable {

    Context context;
    List<VisitStatsModel> visitStatsModelList;
//    List<MissedReportItem> reportList;


    public ApprovedCallsAdapter(Context context, List<VisitStatsModel> visitStatsModelList) {
        this.context = context;
        this.visitStatsModelList = visitStatsModelList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_doctor_visit_report, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ApprovedCallsAdapter.ViewHolder holder, int position) {
        VisitStatsModel model = visitStatsModelList.get(position);

        holder.name.setText(model.getName());
        holder.hqName.setVisibility(View.VISIBLE);
        holder.hqName.setText(model.getHq());
        holder.designation.setVisibility(View.VISIBLE);
        holder.designation.setText(model.getDesignation());
        holder.view1.setVisibility(View.VISIBLE);
        holder.view2.setVisibility(View.VISIBLE);
        holder.drTxt.setText(SharedPref.getDrCap(context));
        holder.totCus.setText(context.getString(R.string.total) + " " + SharedPref.getDrCap(context));
        holder.totCusCnt.setText(model.getTotalCustomers());
        holder.vstCusCnt.setText(model.getVisitedCustomers());
        holder.misCusCnt.setText(model.getMissedCustomers());
        holder.fwDaysCnt.setText(model.getFwDays());
        holder.callAvgCnr.setText(model.getCallAvg());
        holder.callCvgCnt.setText(model.getCoverage());
        holder.pieChart.setVisibility(View.GONE);
        setupBarChart(
                holder.barChart,
                Integer.parseInt(model.getTotalCustomers()),
                Integer.parseInt(model.getVisitedCustomers()),
                Integer.parseInt(model.getMissedCustomers()),
                Double.parseDouble(model.getCallAvg())
        );
//        setupPieChart(
//                holder.pieChart,
//                model.getOneVisitCount(),
//                model.getTwoVisitCount(),
//                model.getThreeVisitCount(),
//                model.getThreePlusVisitCount()
//        );

    }
    

    @Override
    public int getItemCount() {
        return visitStatsModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,hqName,designation,drTxt,totCus,vstCus,misCus,fwDays,callAvg,callCvg,drTxtCnt,totCusCnt,vstCusCnt,misCusCnt,fwDaysCnt,callAvgCnr,callCvgCnt;
        View view1,view2;
        BarChart barChart;
        PieChart pieChart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.monthTxt);
            hqName = itemView.findViewById(R.id.headerTxt);
            designation = itemView.findViewById(R.id.yearTxt);
            drTxt = itemView.findViewById(R.id.custTxt);
            totCus = itemView.findViewById(R.id.totalCust);
            vstCus = itemView.findViewById(R.id.visited);
            misCus = itemView.findViewById(R.id.missed);
            fwDays  = itemView.findViewById(R.id.FWDays);
            callAvg = itemView.findViewById(R.id.callAvg);
            callCvg = itemView.findViewById(R.id.callCvg);
            totCusCnt = itemView.findViewById(R.id.totalCustCnt);
            vstCusCnt = itemView.findViewById(R.id.visitedCnt);
            misCusCnt = itemView.findViewById(R.id.missedCnt);
            fwDaysCnt = itemView.findViewById(R.id.FWDaysCnt);
            callAvgCnr = itemView.findViewById(R.id.callAvgCnt);
            callCvgCnt = itemView.findViewById(R.id.callCvgCnt);
            view1 = itemView.findViewById(R.id.sep1);
            view2 = itemView.findViewById(R.id.sep2);
            barChart = itemView.findViewById(R.id.in_chart_visit);
            pieChart = itemView.findViewById(R.id.pieChart_visit);


        }
    }
    private void setupBarChart(BarChart barChart,int total, int visited, int missed, double callAvg) {
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("Total");
        xVals.add("Visited");
        xVals.add("Missed");
        xVals.add("Average");

        entries.add(new BarEntry(0f, total));
        entries.add(new BarEntry(1f, visited));
        entries.add(new BarEntry(2f, missed));
        entries.add(new BarEntry(3f, (float) callAvg));

        BarDataSet set = new BarDataSet(entries, "Visit Data");
        set.setDrawValues(false);
        int[] colors = new int[]{
                context.getResources().getColor(R.color.indigo),
                context.getResources().getColor(R.color.green_60),
                context.getResources().getColor(R.color.pink_45),
                context.getResources().getColor(R.color.blue_60)
        };
        set.setColors(colors);
        BarData data = new BarData(set);
        data.setBarWidth(0.3f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(xVals.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void setupPieChart(PieChart pieChart,int oneVisit, int twoVisit, int threeVisit, int threePlusVisit) {
        pieChart.setCenterText("Visits");
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextColor(context.getResources().getColor(R.color.black));
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(context.getResources().getColor(R.color.white));
        pieChart.setTransparentCircleColor(context.getResources().getColor(R.color.gray_med));
        pieChart.setHoleRadius(63f);
        pieChart.setTransparentCircleRadius(10f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawEntryLabels(false);

        ArrayList<PieEntry> entries = new ArrayList<>();
        if(oneVisit>0) entries.add(new PieEntry(oneVisit, "1 Visit"));
        if(twoVisit>0)entries.add(new PieEntry(twoVisit, "2 Visits"));
        if(threeVisit>0)entries.add(new PieEntry(threeVisit, "3 Visits"));
        if(threePlusVisit>0)entries.add(new PieEntry(threePlusVisit, "3+ Visits"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(context.getResources().getColor(R.color.blue_60));
        colors.add(context.getResources().getColor(R.color.yellow_45));
        colors.add(context.getResources().getColor(R.color.red_60));
        colors.add(context.getResources().getColor(R.color.green_2));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);

        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        data.setValueTextSize(20f);
        data.setValueTextColor(context.getResources().getColor(R.color.black));

        pieChart.setData(data);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        pieChart.animateY(1400);
        pieChart.invalidate();
    }
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("FILTER", "Filtering with: " + constraint);

                List<VisitStatsModel> filteredResults = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(visitStatsModelList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (VisitStatsModel item : visitStatsModelList) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
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
                visitStatsModelList.clear();
                visitStatsModelList.addAll((List<VisitStatsModel>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
