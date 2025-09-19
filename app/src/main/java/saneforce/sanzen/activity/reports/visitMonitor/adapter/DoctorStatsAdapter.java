//package saneforce.sanzen.activity.reports.visitMonitor.adapter;
//
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.List;
//
//import saneforce.sanzen.R;
//import saneforce.sanzen.activity.reports.visitMonitor.model.DoctorStatsModel;
//import saneforce.sanzen.storage.SharedPref;
//
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.charts.PieChart;
//import com.github.mikephil.charting.components.Legend;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.data.PieData;
//import com.github.mikephil.charting.data.PieDataSet;
//import com.github.mikephil.charting.data.PieEntry;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
//import com.github.mikephil.charting.formatter.PercentFormatter;
//import com.github.mikephil.charting.utils.ColorTemplate;
//
//import java.util.ArrayList;
//import java.util.Locale;
//
//public class DoctorStatsAdapter extends RecyclerView.Adapter<DoctorStatsAdapter.DoctorStatsViewHolder> {
//
//    private final List<DoctorStatsModel> dataList;
//    Context context;
//
//    public DoctorStatsAdapter(List<DoctorStatsModel> dataList) {
//        this.dataList = dataList;
//    }
//
//    @NonNull
//    @Override
//    public DoctorStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_doctor_visit_report, parent, false);
//        return new DoctorStatsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull DoctorStatsViewHolder holder, int position) {
//        DoctorStatsModel model = dataList.get(position);
//
//        Context context = holder.itemView.getContext();
//
//        Calendar now = Calendar.getInstance();
//
//
//        holder.totalDr.setText("Total"+" "+SharedPref.getDrCap(context));
//        holder.doctorVisitTxt.setText(SharedPref.getDrCap(context));
//        holder.totalDrCnt.setText(String.valueOf(model.getTotalDoctors()));
//        holder.visitedCnt.setText(String.valueOf(model.getVisitedDoctors()));
//        holder.missedCnt.setText(String.valueOf(model.getMissedDoctors()));
//        holder.FWDaysCnt.setText(String.valueOf(model.getFwDays()));
//        holder.callAvgCnt.setText(String.valueOf(model.getCallAvg()));
//        holder.callCvgCnt.setText(String.valueOf(model.getCoverage()));
//
//        barReport(holder, model);
//        setPieChart(holder, model);
//    }
//
//    public void barReport(DoctorStatsViewHolder holder, DoctorStatsModel model) {
//        Context context = holder.itemView.getContext();
//        List<BarEntry> entries = new ArrayList<>();
//        ArrayList<String> xVals = new ArrayList<>();
//        xVals.add(holder.itemView.getContext().getResources().getString(R.string.total));
//        xVals.add(holder.itemView.getContext().getResources().getString(R.string.visit));
//        xVals.add(holder.itemView.getContext().getResources().getString(R.string.miss));
//        xVals.add(holder.itemView.getContext().getResources().getString(R.string.avg));
//        entries.add(new BarEntry(0f, Integer.parseInt(model.getTotalDoctors())));
//        entries.add(new BarEntry(1f, Integer.parseInt(model.getVisitedDoctors())));
//        entries.add(new BarEntry(2f, Integer.parseInt(model.getMissedDoctors())));
//
//        if (model.getCallAvg() != null && model.getCallAvg().equalsIgnoreCase(""))
//            entries.add(new BarEntry(3f, 0));
//        else
//            entries.add(new BarEntry(3f, Float.parseFloat(model.getCallAvg())));
//
//
//        BarDataSet set = new BarDataSet(entries, "Visit Data");
//        int[] colors = new int[] {
//                context.getResources().getColor(R.color.indigo),
//                context.getResources().getColor(R.color.green_60),
//                context.getResources().getColor(R.color.pink_45),
//                context.getResources().getColor(R.color.blue_60)
//        };
//        set.setColors(colors);
//        BarData data = new BarData(set);
//        data.setBarWidth(0.5f);
//
//        holder.barChart.setData(data);
//        holder.barChart.getDescription().setEnabled(false);
//        holder.barChart.getLegend().setEnabled(false);
//        holder.barChart.setFitBars(true);
//
//        XAxis xAxis = holder.barChart.getXAxis();
//        xAxis.setDrawGridLines(false);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setLabelCount(xVals.size());
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
//
//        holder.barChart.animateY(1000);
//        holder.barChart.invalidate();
//    }
//
//    private void setPieChart(DoctorStatsViewHolder holder, DoctorStatsModel model) {
//        Context context = holder.itemView.getContext();
//        PieChart chart = holder.pieChart;
//
//        chart.setCenterText("Visits");
//        chart.setCenterTextSize(15f);
//        chart.setCenterTextColor(context.getResources().getColor(R.color.black));
//
//        chart.setUsePercentValues(false);
//        chart.getDescription().setEnabled(false);
//        chart.setExtraOffsets(5f, 10f, 5f, 5f);
//        chart.setDragDecelerationFrictionCoef(0.95f);
//        chart.setDrawHoleEnabled(true);
//        chart.setHoleColor(context.getResources().getColor(R.color.white));
//        chart.setTransparentCircleColor(context.getResources().getColor(R.color.white));
//        chart.setTransparentCircleAlpha(110);
//        chart.setHoleRadius(63f);
//        chart.setTransparentCircleRadius(61f);
//        chart.setRotationAngle(0);
//        chart.setRotationEnabled(true);
//        chart.setHighlightPerTapEnabled(true);
//
//        chart.setDrawEntryLabels(false);
//
//        ArrayList<PieEntry> entries = new ArrayList<>();
//
//        entries.add(new PieEntry(model.getOneVisitCount(), "1 Visit"));
//        entries.add(new PieEntry(model.getTwoVisitCount(), "2 Visits"));
//        entries.add(new PieEntry(model.getThreeVisitCount(), "3 Visits"));
//        entries.add(new PieEntry(model.getThreePlusVisitCount(), "3+ Visits"));
//
//        PieDataSet dataSet = new PieDataSet(entries, "");
//        dataSet.setSliceSpace(3f);
//        dataSet.setSelectionShift(5f);
//
//        ArrayList<Integer> colors = new ArrayList<>();
//        colors.add(context.getResources().getColor(R.color.blue_60));
//        colors.add(context.getResources().getColor(R.color.yellow_45));
//        colors.add(context.getResources().getColor(R.color.red_60));
//        colors.add(context.getResources().getColor(R.color.green_2));
//        dataSet.setColors(colors);
//
////     dataSet.setDrawValues(false);
//
//        PieData data = new PieData(dataSet);
//        chart.setData(data);
//
//        Legend l = chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);
//
//        chart.animateY(1400);
//        chart.invalidate();
//    }
//
//    @Override
//    public int getItemCount() {
//        return dataList.size();
//    }
//
//    public static class DoctorStatsViewHolder extends RecyclerView.ViewHolder {
//        final TextView doctorVisitTxt,totalDr, totalDrCnt, monthTxt, yearTxt;
//        final TextView visitedCnt;
//        final TextView missedCnt;
//        final TextView FWDaysCnt;
//        final TextView callAvgCnt;
//        final TextView callCvgCnt;
//        final BarChart barChart;
//        final PieChart pieChart;
//
//        public DoctorStatsViewHolder(@NonNull View itemView) {
//            super(itemView);
//            monthTxt = itemView.findViewById(R.id.monthTxt);
//            yearTxt = itemView.findViewById(R.id.yearTxt);
//            totalDr = itemView.findViewById(R.id.totalDr);
//            doctorVisitTxt = itemView.findViewById(R.id.doctorVisitTxt);
//            totalDrCnt = itemView.findViewById(R.id.totalDrCnt);
//            visitedCnt = itemView.findViewById(R.id.visitedCnt);
//            missedCnt = itemView.findViewById(R.id.missedCnt);
//            FWDaysCnt = itemView.findViewById(R.id.FWDaysCnt);
//            callAvgCnt = itemView.findViewById(R.id.callAvgCnt);
//            callCvgCnt = itemView.findViewById(R.id.callCvgCnt);
//            barChart = itemView.findViewById(R.id.barChartVisit);
//            pieChart = itemView.findViewById(R.id.pieChart_visit);
//        }
//    }
//}
