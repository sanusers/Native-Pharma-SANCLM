package saneforce.sanzen.activity.reports.missedReport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

//public class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {
//    private final Context context;
//    private final List<MissedStatsModel> statsList;
//    String val;
//    public InnerAdapter(Context context, List<MissedStatsModel> statsList, String val) {
//        this.context = context;
//        this.statsList = statsList;
//        this.val = val;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_drchm_missed_current, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        MissedStatsModel model = statsList.get(position);
//        if (model.getType().equalsIgnoreCase("1")) {
//            int totalCount = model.getTotalCustomers().length();
//            int visitedCount = model.getUniqueCustomers().size();
//            int missedCount = totalCount - visitedCount;
//            holder.totalDrCnt.setText(String.valueOf(totalCount));
//            holder.visitedCnt.setText(String.valueOf(visitedCount));
//            holder.missedCnt.setText(String.valueOf(missedCount));
//            float missedPercentage = (((float) missedCount / (float) totalCount) * 100.0f);
//            ArrayList<Integer> colors = new ArrayList<>();
//            colors.add(context.getResources().getColor(R.color.green_60));
//            colors.add(context.getResources().getColor(R.color.mildRed));
//            ArrayList<PieEntry> missedDataList = new ArrayList<>();
//            missedDataList.add(new PieEntry(100.0f - missedPercentage)); // visited
//            missedDataList.add(new PieEntry(missedPercentage, ""));      // missed
//            PieDataSet missedDataSet = new PieDataSet(missedDataList, "");
//            missedDataSet.setColors(colors);
//            PieData missedData = new PieData(missedDataSet);
//            missedData.setValueTextSize(0f);
//            missedData.setValueTextColor(Color.WHITE);
//            holder.missedChart.setData(missedData);
//            holder.missedChart.setUsePercentValues(true);
//            holder.missedChart.setDrawHoleEnabled(true);
//            holder.missedChart.setCenterTextSize(18f);
//            holder.missedChart.setCenterTextColor(holder.missedChart.getContext().getColor(R.color.black));
//            holder.missedChart.setTransparentCircleRadius(30f);
//            holder.missedChart.setHoleRadius(75f);
//            holder.missedChart.animateXY(1400, 1400);
//            holder.missedChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
//            holder.missedChart.setCenterText(String.format("%.1f %%", missedPercentage));
//            Description description2 = holder.missedChart.getDescription();
//            description2.setEnabled(false);
//            Legend legend2 = holder.missedChart.getLegend();
//            legend2.setEnabled(false);
//            holder.missedChart.invalidate();
//        }
//        if (model.getType().equalsIgnoreCase("2")) {
//            int totalCountchm = model.getTotalCustomers().length();
//            int visitedCountchm = model.getUniqueCustomers().size();
//            int missedCountchm = totalCountchm - visitedCountchm;
//            holder.totalchmCnt.setText(String.valueOf(totalCountchm));
//            holder.chmvisitedCnt.setText(String.valueOf(visitedCountchm));
//            holder.chmmissedCnt.setText(String.valueOf(missedCountchm));
//            float missedPercentagechm = (((float) missedCountchm / (float) totalCountchm) * 100.0f);
//            ArrayList<Integer> colorschm = new ArrayList<>();
//            colorschm.add(context.getResources().getColor(R.color.green_60));
//            colorschm.add(context.getResources().getColor(R.color.mildRed));
//            ArrayList<PieEntry> missedDataListchm = new ArrayList<>();
//            missedDataListchm.add(new PieEntry(100.0f - missedPercentagechm)); // visited
//            missedDataListchm.add(new PieEntry(missedPercentagechm, ""));      // missed
//            PieDataSet missedDataSetchm = new PieDataSet(missedDataListchm, "");
//            missedDataSetchm.setColors(colorschm);
//            PieData missedDatachm = new PieData(missedDataSetchm);
//            missedDatachm.setValueTextSize(0f);
//            missedDatachm.setValueTextColor(Color.WHITE);
//            holder.missedChartChem.setData(missedDatachm);
//            holder.missedChartChem.setUsePercentValues(true);
//            holder.missedChart.setDrawHoleEnabled(true);
//            holder.missedChartChem.setCenterTextSize(18f);
//            holder.missedChartChem.setCenterTextColor(holder.missedChartChem.getContext().getColor(R.color.black));
//            holder.missedChartChem.setTransparentCircleRadius(30f);
//            holder.missedChartChem.setHoleRadius(75f);
//            holder.missedChartChem.animateXY(1400, 1400);
//            holder.missedChartChem.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
//            holder.missedChartChem.setCenterText(String.format("%.1f %%", missedPercentagechm));
//            Description description2chm = holder.missedChartChem.getDescription();
//            description2chm.setEnabled(false);
//            Legend legend2chm = holder.missedChartChem.getLegend();
//            legend2chm.setEnabled(false);
//            holder.missedChartChem.invalidate();
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return statsList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public PieChart missedChart, missedChartChem;
//        TextView totalDr, visited, missed, totalDrCnt, visitedCnt, missedCnt;
//        TextView totalchm, totalchmCnt, chmvisited, chmvisitedCnt, chmmissed, chmmissedCnt;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            missedChart = itemView.findViewById(R.id.pBar);
//            totalDr = itemView.findViewById(R.id.totalDr);
//            totalDrCnt = itemView.findViewById(R.id.totalDrCnt);
//            visited = itemView.findViewById(R.id.visited);
//            visitedCnt = itemView.findViewById(R.id.visitedCnt);
//            missed = itemView.findViewById(R.id.missed);
//            missedCnt = itemView.findViewById(R.id.missedCnt);
//            missedChartChem = itemView.findViewById(R.id.pBar2);
//            totalchm = itemView.findViewById(R.id.totalchm);
//            totalchmCnt = itemView.findViewById(R.id.totalchmCnt);
//            chmvisited = itemView.findViewById(R.id.chmvisited);
//            chmvisitedCnt = itemView.findViewById(R.id.chmvisitedCnt);
//            chmmissed = itemView.findViewById(R.id.chmmissed);
//            chmmissedCnt = itemView.findViewById(R.id.chmmissedCnt);
//        }
//    }
//}

public class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {

    private final Context context;
    private final List<MissedStatsModel> statsList;
    private final String sfCode;
    private final String date;
    private final String monthName;


    // Hold separate references for types
    private MissedStatsModel doctor, chemist, stockist, unlisted;

    public InnerAdapter(Context context, List<MissedStatsModel> statsList, String sfCode, String date, String monthName) {
        this.context = context;
        this.statsList = statsList;
        this.sfCode = sfCode;
        this.date = date;
        this.monthName = monthName;

        // Separate models by type once
        for (MissedStatsModel model : statsList) {
            switch (model.getType()) {
                case "1":
                    doctor = model;
                    break;
                case "2":
                    chemist = model;
                    break;
                case "3":
                    stockist = model;
                    break;
                case "4":
                    unlisted = model;
                    break;
            }
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_drchm_missed_current, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MissedStatsModel doctor = statsList.get(position);
        if (position == 0) {
            Log.d("DoctorList", "Total: " + doctor.getTotalCustomers());
            Log.d("DoctorList", "Visited: " + doctor.getUniqueCustomers());
            Log.d("DoctorList", "Missed: " + doctor.getMissedCustomers());

            // Card 1 → Doctor + Chemist
            holder.card1Layout.setVisibility(View.VISIBLE);
            holder.card2Layout.setVisibility(View.GONE);
            if (doctor != null)
                setPieChart(holder.missedChart, holder.totalDrCnt, holder.visitedCnt, holder.missedCnt, doctor);
            holder.missedBox.setOnClickListener(v -> {
                try {
                    //Prepare array
                    JSONArray missedDoctors = doctor.getMissedCustomers();
                    JSONArray visitDoctors = doctor.getVisitedCustomers();

                    Log.d("MissedDoctorsJSON", missedDoctors.toString());
                    Log.d("MissedDoctorsCount", "Length = " + missedDoctors.length());
                    // Save to RoomDB
                    RoomDB localRoomDB = RoomDB.getDatabase(context);
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, missedDoctors.toString());
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, visitDoctors.toString());
                    // Pass to next activity
                    Intent intent = new Intent(context, DoctorVisitActivity.class);
                    intent.putExtra("missed_array", missedDoctors.toString());
                    intent.putExtra("visit", visitDoctors.toString());
                    intent.putExtra("sfcode", sfCode);
                    intent.putExtra("date", date);
                    intent.putExtra("selected_month", monthName);
                    intent.putExtra("clicked_type", doctor.getType());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
//
            if (chemist != null)
                setPieChart(holder.missedChartChem, holder.totalchmCnt, holder.chmvisitedCnt, holder.chmmissedCnt, chemist);
            holder.chemistBox.setOnClickListener(v -> {
                try {
                    //Prepare array
                    JSONArray chemistDoctors = chemist.getMissedCustomers();
                    JSONArray visitchemist = chemist.getVisitedCustomers();

                    Log.d("MissedDoctorsJSON", chemistDoctors.toString());
                    Log.d("MissedDoctorsCount", "Length = " + chemistDoctors.length());
                    // Save to RoomDB
                    RoomDB localRoomDB = RoomDB.getDatabase(context);
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, chemistDoctors.toString());
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, visitchemist.toString());
                    // Pass to next activity
                    Intent intent = new Intent(context, DoctorVisitActivity.class);
                    intent.putExtra("missed_array", chemistDoctors.toString());
                    intent.putExtra("visit", visitchemist.toString());
                    intent.putExtra("sfcode", sfCode);
                    intent.putExtra("date", date);
                    intent.putExtra("selected_month", monthName);
                    intent.putExtra("clicked_type", chemist.getType());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else if (position == 1) {
            // Card 2 → Stockist + Unlisted
            holder.card1Layout.setVisibility(View.GONE);
            holder.card2Layout.setVisibility(View.VISIBLE);
            if (stockist != null)
                setPieChart(holder.missedChartStk, holder.totalstkCnt, holder.stkvisitedCnt, holder.stkmissedCnt, stockist);
            holder.stockiestBox.setOnClickListener(v -> {
                try {
                    //Prepare array
                    JSONArray stockistDoctors = stockist.getMissedCustomers();
                    JSONArray visitstockist = stockist.getVisitedCustomers();

                    Log.d("MissedDoctorsJSON", stockistDoctors.toString());
                    Log.d("MissedDoctorsCount", "Length = " + stockistDoctors.length());
                    // Save to RoomDB
                    RoomDB localRoomDB = RoomDB.getDatabase(context);
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, stockistDoctors.toString());
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, visitstockist.toString());
                    // Pass to next activity
                    Intent intent = new Intent(context, DoctorVisitActivity.class);
                    intent.putExtra("missed_array", stockistDoctors.toString());
                    intent.putExtra("visit", visitstockist.toString());
                    intent.putExtra("sfcode", sfCode);
                    intent.putExtra("date", date);
                    intent.putExtra("selected_month", monthName);
                    intent.putExtra("clicked_type", stockist.getType());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            if (unlisted != null)
                setPieChart(holder.missedChartUnlst, holder.totalunlstCnt, holder.unlstvisitedCnt, holder.unlstmissedCnt, unlisted);
            holder.unlistedBox.setOnClickListener(v -> {

                try {
                    //Prepare array
                    JSONArray unlistedDoctors = unlisted.getMissedCustomers();
                    JSONArray visitunlisted = unlisted.getVisitedCustomers();

                    Log.d("MissedDoctorsJSON", unlistedDoctors.toString());
                    Log.d("MissedDoctorsCount", "Length = " + unlistedDoctors.length());
                    // Save to RoomDB
                    RoomDB localRoomDB = RoomDB.getDatabase(context);
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, unlistedDoctors.toString());
                    localRoomDB.doctorVisitDao().saveVisitJson(sfCode, date, visitunlisted.toString());
                    // Pass to next activity
                    Intent intent = new Intent(context, DoctorVisitActivity.class);
                    intent.putExtra("missed_array", unlistedDoctors.toString());
                    intent.putExtra("visit", visitunlisted.toString());
                    intent.putExtra("sfcode", sfCode);
                    intent.putExtra("date", date);
                    intent.putExtra("selected_month", monthName);
                    intent.putExtra("clicked_type", unlisted.getType());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 2;

    }

    private void setPieChart(PieChart chart, TextView total, TextView visited, TextView missed, MissedStatsModel model) {
        int totalCount = model.getTotalCustomers().length();
        int visitedCount = model.getUniqueCustomers().size();
        int missedCount = totalCount - visitedCount;

        total.setText(String.valueOf(totalCount));
        visited.setText(String.valueOf(visitedCount));
        missed.setText(String.valueOf(missedCount));

        float missedPercentage = (((float) missedCount / (float) totalCount) * 100.0f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(context.getResources().getColor(R.color.green_60));
        colors.add(context.getResources().getColor(R.color.mildRed));

        ArrayList<PieEntry> dataList = new ArrayList<>();
        dataList.add(new PieEntry(100.0f - missedPercentage));
        dataList.add(new PieEntry(missedPercentage, ""));

        PieDataSet dataSet = new PieDataSet(dataList, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setCenterText(String.format("%.1f %%", missedPercentage));
        chart.setCenterTextSize(18f);
        chart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        chart.setCenterTextColor(context.getColor(R.color.black));
        chart.setHoleRadius(75f);
        chart.setTransparentCircleRadius(30f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateXY(1400, 1400);
        chart.invalidate();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout card1Layout, card2Layout; // root layouts for swipe cards

        PieChart missedChart, missedChartChem, missedChartStk, missedChartUnlst;
        TextView totalDrCnt, visitedCnt, missedCnt;
        TextView totalchmCnt, chmvisitedCnt, chmmissedCnt;
        TextView totalstkCnt, stkvisitedCnt, stkmissedCnt;
        TextView totalunlstCnt, unlstvisitedCnt, unlstmissedCnt;
        LinearLayout missedBox, chemistBox,stockiestBox,unlistedBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card1Layout = itemView.findViewById(R.id.missedcard1);
            card2Layout = itemView.findViewById(R.id.missedcard2);

            missedChart = itemView.findViewById(R.id.pBar);
            totalDrCnt = itemView.findViewById(R.id.missedtotalDrCnt);
            visitedCnt = itemView.findViewById(R.id.missedvisitedCnt);
            missedCnt = itemView.findViewById(R.id.missedmissedCnt);
            missedBox = itemView.findViewById(R.id.grid3);

            missedChartChem = itemView.findViewById(R.id.pBar2);
            totalchmCnt = itemView.findViewById(R.id.missedtotalchmCnt);
            chmvisitedCnt = itemView.findViewById(R.id.missedchmvisitedCnt);
            chmmissedCnt = itemView.findViewById(R.id.missedchmmissedCnt);
            chemistBox = itemView.findViewById(R.id.chmgrid3);

            missedChartStk = itemView.findViewById(R.id.pBar3);
            totalstkCnt = itemView.findViewById(R.id.missedtotalstkCnt);
            stkvisitedCnt = itemView.findViewById(R.id.missedstkvisitedCnt);
            stkmissedCnt = itemView.findViewById(R.id.missedstkmissedCnt);
            stockiestBox = itemView.findViewById(R.id.stkgrid3);

            missedChartUnlst = itemView.findViewById(R.id.pBar4);
            totalunlstCnt = itemView.findViewById(R.id.missedtotalunlstCnt);
            unlstvisitedCnt = itemView.findViewById(R.id.missedunlstvisitedCnt);
            unlstmissedCnt = itemView.findViewById(R.id.missedunlstmissedCnt);
            unlistedBox = itemView.findViewById(R.id.unlstgrid3);

        }
    }
}


//// InnerAdapter.java
//package saneforce.sanzen.activity.reports.missedReport;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//
//import java.util.List;
//
//import saneforce.sanzen.R;
//
//public class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.InnerAdapterViewHolder> {
//
//    private final List<InnerAdapter> doctorStats;
//    private final List<InnerAdapter> chemistStats;
//    private final List<InnerAdapter> stockiestStats;
//    private final List<InnerAdapter> unlistedStats;
//    private final FragmentActivity fragmentActivity;
//    int position;
//
//    public InnerAdapter(FragmentActivity fragmentActivity,
//                             List<InnerAdapter> doctorStats,
//                             List<InnerAdapter> chemistStats,
//                             List<InnerAdapter> stockiestStats,
//                             List<InnerAdapter> unlistedStats) {
//        this.fragmentActivity = fragmentActivity;
//        this.doctorStats = doctorStats;
//        this.chemistStats = chemistStats;
//        this.stockiestStats = stockiestStats;
//        this.unlistedStats = unlistedStats;
//    }
//
//    public InnerAdapter(MissedReportGraph fragmentActivity, List<MissedStatsModel> allMonthsFlatList) {
//    }
//
//    @NonNull
//    @Override
//    public InnerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_row, parent, false);
//        return new InnerAdapter.InnerAdapterViewHolder(view);
//    }
//
//
//
//    @Override
//    public void onBindViewHolder(@NonNull InnerAdapter.InnerAdapterViewHolder holder, int position) {
//        InnerAdapter InnerAdapter = new InnerAdapter(
//                fragmentActivity,
//                doctorStats,
//                chemistStats,
//                stockiestStats,
//                unlistedStats
//        );
//        holder.viewPager2.setAdapter(InnerAdapter);
//
//
//        holder.viewPager2.setOffscreenPageLimit(4);
//    }
//    @Override
//    public int getItemCount() {
//    /*    switch (position){
//            case 0:
//                return doctorStats.size();
//            case 1:
//                return chemistStats.size();
//            case 2:
//                return stockiestStats.size();
//            case 3:
//                return unlistedStats.size();
//        }
//       return position; */
//        return 3;
//    }
//
//    static class InnerAdapterViewHolder extends RecyclerView.ViewHolder {
//        ViewPager2 viewPager2;
//        InnerAdapterViewHolder(@NonNull View itemView) {
//            super(itemView);
//            viewPager2 = itemView.findViewById(R.id.missed_pager);
//        }
//
//
//    }
//}
