package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


import saneforce.sanzen.R;

import saneforce.sanzen.activity.reports.visitMonitor.model.StockiestStatsModel;
import saneforce.sanzen.storage.SharedPref;

public class StockiestStatsAdapter extends RecyclerView.Adapter<StockiestStatsAdapter.StockiestStatsViewHolder> {

    private final List<StockiestStatsModel> dataList;

    public StockiestStatsAdapter(List<StockiestStatsModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public StockiestStatsAdapter.StockiestStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_stockiest_visit_report, parent, false);
        return new StockiestStatsAdapter.StockiestStatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockiestStatsAdapter.StockiestStatsViewHolder holder, int position) {
        StockiestStatsModel model = dataList.get(position);

        Context context = holder.itemView.getContext();

        holder.stockiestVisitTxt.setText(SharedPref.getStkCap(context));
        holder.totalStk.setText("Total"+" "+SharedPref.getStkCap(context));
        holder.totalStkCnt.setText(String.valueOf(model.getTotalStockiest()));
        holder.visitedCnt.setText(String.valueOf(model.getVisitedStockiest()));
        holder.missedCnt.setText(String.valueOf(model.getMissedStockiest()));
        holder.FWDaysCnt.setText(String.valueOf(model.getFwDays()));
        holder.callAvgCnt.setText(String.valueOf(model.getCallAvg()));
        holder.callCvgCnt.setText(String.valueOf(model.getCoverage()));

        barReport(holder, model);
    }
    public void barReport(StockiestStatsAdapter.StockiestStatsViewHolder holder, StockiestStatsModel model) {
        Context context = holder.itemView.getContext();
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.total));
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.visit));
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.miss));
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.avg));
        entries.add(new BarEntry(0f, Integer.parseInt(model.getTotalStockiest())));
        entries.add(new BarEntry(1f, Integer.parseInt(model.getVisitedStockiest())));
        entries.add(new BarEntry(2f, Integer.parseInt(model.getMissedStockiest())));

        if (model.getCallAvg() != null && model.getCallAvg().equalsIgnoreCase(""))
            entries.add(new BarEntry(3f, 0));
        else
            entries.add(new BarEntry(3f, Float.parseFloat(model.getCallAvg())));

        BarDataSet set = new BarDataSet(entries, "Visit Data");

        int[] colors = new int[] {
                context.getResources().getColor(R.color.bg_orange),
                context.getResources().getColor(R.color.green_60),
                context.getResources().getColor(R.color.purple_200),
                context.getResources().getColor(R.color.blue_60)
        };
        set.setColors(colors);
        BarData data = new BarData(set);
        data.setBarWidth(0.3f);

        holder.barChart.setData(data);
        holder.barChart.getDescription().setEnabled(false);
        holder.barChart.getLegend().setEnabled(false);
        holder.barChart.setFitBars(true);

        XAxis xAxis = holder.barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(xVals.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        holder.barChart.animateY(1000);
        holder.barChart.invalidate();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class StockiestStatsViewHolder extends RecyclerView.ViewHolder {
        final TextView stockiestVisitTxt,totalStk,totalStkCnt,monthTxt,yearTxt;
        final TextView visitedCnt;
        final TextView missedCnt;
        final TextView FWDaysCnt;
        final TextView callAvgCnt;
        final TextView callCvgCnt;
        final BarChart barChart;

        public StockiestStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize TextViews from the list_item_card.xml layout.
            monthTxt = itemView.findViewById(R.id.monthTxt);
            yearTxt = itemView.findViewById(R.id.yearTxt);
            stockiestVisitTxt = itemView.findViewById(R.id.stockiestVisitTxt);
            totalStk = itemView.findViewById(R.id.totalStkVisit);
            totalStkCnt = itemView.findViewById(R.id.totalStkVisitCnt);
            visitedCnt = itemView.findViewById(R.id.stkVisitedCnt);
            missedCnt = itemView.findViewById(R.id.stkMissedCnt);
            FWDaysCnt = itemView.findViewById(R.id.FWDaysCnt);
            callAvgCnt = itemView.findViewById(R.id.callAvgCnt);
            callCvgCnt = itemView.findViewById(R.id.callCvgCnt);
            barChart = itemView.findViewById(R.id.barChartVisit);

        }
    }

}
