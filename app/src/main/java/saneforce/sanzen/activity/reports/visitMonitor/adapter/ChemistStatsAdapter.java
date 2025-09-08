package saneforce.sanzen.activity.reports.visitMonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.model.ChemistStatsModel;
import saneforce.sanzen.activity.reports.visitMonitor.model.DoctorStatsModel;
import saneforce.sanzen.storage.SharedPref;

public class ChemistStatsAdapter extends RecyclerView.Adapter<ChemistStatsAdapter.ChemistStatsViewHolder> {
    private final List<ChemistStatsModel> dataList;

    public ChemistStatsAdapter(List<ChemistStatsModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ChemistStatsAdapter.ChemistStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chemist_visit_report, parent, false);
        return new ChemistStatsAdapter.ChemistStatsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChemistStatsAdapter.ChemistStatsViewHolder holder, int position) {
        ChemistStatsModel model = dataList.get(position);

        Context context = holder.itemView.getContext();
        holder.mrName.setText(SharedPref.getSfName(context));
        holder.mrHq.setText(SharedPref.getHqName(context));
        holder.mrDesignation.setText(SharedPref.getDesig(context));
//        holder.dateTxt.setText();

        holder.totalCheCnt.setText(String.valueOf(model.getTotalChemists()));
        holder.visitedCnt.setText(String.valueOf(model.getVisitedChemists()));
        holder.missedCnt.setText(String.valueOf(model.getMissedChemists()));
        holder.FWDaysCnt.setText(String.valueOf(model.getFwDays()));
        holder.callAvgCnt.setText(String.valueOf(model.getCallAvg()));
        holder.callCvgCnt.setText(String.valueOf(model.getCoverage()));

        barReport(holder, model);
    }

    public void barReport(ChemistStatsAdapter.ChemistStatsViewHolder holder, ChemistStatsModel model) {
        Context context = holder.itemView.getContext();
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.total));
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.visit));
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.miss));
        xVals.add(holder.itemView.getContext().getResources().getString(R.string.avg));
        entries.add(new BarEntry(0f, Integer.parseInt(model.getTotalChemists())));
        entries.add(new BarEntry(1f, Integer.parseInt(model.getVisitedChemists())));
        entries.add(new BarEntry(2f, Integer.parseInt(model.getMissedChemists())));

        if (model.getCallAvg() != null && model.getCallAvg().equalsIgnoreCase(""))
            entries.add(new BarEntry(3f, 0));
        else
            entries.add(new BarEntry(3f, Float.parseFloat(model.getCallAvg())));



        BarDataSet set = new BarDataSet(entries, "Visit Data");

        set.setColors(ColorTemplate.COLORFUL_COLORS);
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

    public static class ChemistStatsViewHolder extends RecyclerView.ViewHolder {
        final TextView chemistVisitTxt,totalCheCnt,mrName,mrHq,mrDesignation,dateTxt;
        final TextView visitedCnt;
        final TextView missedCnt;
        final TextView FWDaysCnt;
        final TextView callAvgCnt;
        final TextView callCvgCnt;
        final BarChart barChart;

        public ChemistStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize TextViews from the list_item_card.xml layout.
            mrName = itemView.findViewById(R.id.headerTxt);
            mrHq = itemView.findViewById(R.id.headerTxt1);
            mrDesignation = itemView.findViewById(R.id.headerTxt2);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            chemistVisitTxt = itemView.findViewById(R.id.chemistVisitTxt);
            totalCheCnt = itemView.findViewById(R.id.totalCheCnt);
            visitedCnt = itemView.findViewById(R.id.cheVisitCnt);
            missedCnt = itemView.findViewById(R.id.cheMissedCnt);
            FWDaysCnt = itemView.findViewById(R.id.FWDaysCnt);
            callAvgCnt = itemView.findViewById(R.id.callAvgCnt);
            callCvgCnt = itemView.findViewById(R.id.callCvgCnt);
            barChart = itemView.findViewById(R.id.barChartVisit);

        }
    }

}
