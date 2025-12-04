package saneforce.sanzen.activity.reports.missedReport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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
        ViewHolder holder = new ViewHolder(view);

        // Set static text here (works fine)
        holder.totalDr.setText(context.getString(R.string.total) + " " + SharedPref.getDrCap(context));
       holder.totalchm.setText(context.getString(R.string.total) + " " + SharedPref.getChmCap(context));
        holder.totalstk.setText(context.getString(R.string.total) + " " + SharedPref.getStkCap(context));
        holder.totalunlst.setText(context.getString(R.string.total) + " " + SharedPref.getUNLcap(context));
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

            holder.card1Layout.setVisibility(View.VISIBLE);
            holder.card2Layout.setVisibility(View.GONE);
            if (doctor != null && SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
                holder.drMissedCurrent.setVisibility(View.VISIBLE);
                holder.drVisitTxt.setText(SharedPref.getDrCap(context));
                setPieChart(holder.missedChart, holder.totalDrCnt, holder.visitedCnt, holder.missedCnt, doctor);
                holder.missedBox.setClickable(true);
                holder.missedBox.setFocusable(true);

                holder.missedBox.setOnClickListener(v -> {
                    Log.d("CLICK_TEST", "Missed box clicked at position " + position);
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
                        intent.putExtra("source", "local");
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                holder.drMissedCurrent.setVisibility(View.GONE);
            }

            if (chemist != null && SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
                holder.cheMissedCurrent.setVisibility(View.VISIBLE);
                setPieChart(holder.missedChartChem, holder.totalchmCnt, holder.chmvisitedCnt, holder.chmmissedCnt, chemist);
                holder.chmVisitTxt.setText(SharedPref.getChmCap(context));
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
                        intent.putExtra("source", "local");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                holder.cheMissedCurrent.setVisibility(View.GONE);
            }
            if (stockist != null && SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
                holder.stkMissedCurrent1.setVisibility(View.VISIBLE);
                setPieChart(holder.missedChartStk1, holder.totalstkCnt1, holder.stkvisitedCnt1, holder.stkmissedCnt1, stockist);
                holder.stkVisitTxt.setText(SharedPref.getStkCap(context));
                holder.stockiestBox1.setOnClickListener(v -> {
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
                        intent.putExtra("source", "local");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                holder.stkMissedCurrent1.setVisibility(View.GONE);
            }
            if (unlisted != null && SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
                holder.unlstMissedCurrent1.setVisibility(View.VISIBLE);
                setPieChart(holder.missedChartUnlst1, holder.totalunlstCnt1, holder.unlstvisitedCnt1, holder.unlstmissedCnt1, unlisted);
                holder.unlstVisitTxt.setText(SharedPref.getUNLcap(context));
                holder.unlistedBox1.setOnClickListener(v -> {

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
                        intent.putExtra("source", "local");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                holder.unlstMissedCurrent1.setVisibility(View.GONE);

            }

        } else if (position == 1) {

            holder.card1Layout.setVisibility(View.GONE);
            holder.card2Layout.setVisibility(View.VISIBLE);

            if (stockist != null && (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && SharedPref.getChmNeed(context).equalsIgnoreCase("0"))
                    || (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && SharedPref.getChmNeed(context).equalsIgnoreCase("0") && SharedPref.getUnlNeed(context).equalsIgnoreCase("1"))) {
                holder.stkMissedCurrent.setVisibility(View.VISIBLE);
                setPieChart(holder.missedChartStk, holder.totalstkCnt, holder.stkvisitedCnt, holder.stkmissedCnt, stockist);
                holder.stkVisitTxt.setText(SharedPref.getStkCap(context));
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
                        intent.putExtra("source", "local");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                holder.stkMissedCurrent.setVisibility(View.GONE);
            }

            if (unlisted != null && (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && SharedPref.getChmNeed(context).equalsIgnoreCase("0") && SharedPref.getStkNeed(context).equalsIgnoreCase("0"))
            || (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && SharedPref.getChmNeed(context).equalsIgnoreCase("0") && SharedPref.getStkNeed(context).equalsIgnoreCase("1"))) {
                holder.unlstMissedCurrent.setVisibility(View.VISIBLE);
                setPieChart(holder.missedChartUnlst, holder.totalunlstCnt, holder.unlstvisitedCnt, holder.unlstmissedCnt, unlisted);
                holder.unlstVisitTxt.setText(SharedPref.getUNLcap(context));
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
                        intent.putExtra("source", "local");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                holder.unlstMissedCurrent.setVisibility(View.GONE);
            }
        }

        if ((SharedPref.getDrNeed(context).equalsIgnoreCase("0") && SharedPref.getChmNeed(context).equalsIgnoreCase("0") && SharedPref.getStkNeed(context).equalsIgnoreCase("1") && SharedPref.getUnlNeed(context).equalsIgnoreCase("1")) ||
                (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && SharedPref.getChmNeed(context).equalsIgnoreCase("1") && SharedPref.getStkNeed(context).equalsIgnoreCase("0") && SharedPref.getUnlNeed(context).equalsIgnoreCase("1")) ||
                (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && SharedPref.getChmNeed(context).equalsIgnoreCase("1") && SharedPref.getStkNeed(context).equalsIgnoreCase("1") && SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) ||
                (SharedPref.getDrNeed(context).equalsIgnoreCase("1") && SharedPref.getChmNeed(context).equalsIgnoreCase("0") && SharedPref.getStkNeed(context).equalsIgnoreCase("0") && SharedPref.getUnlNeed(context).equalsIgnoreCase("1")) ||
                (SharedPref.getDrNeed(context).equalsIgnoreCase("1") && SharedPref.getChmNeed(context).equalsIgnoreCase("0") && SharedPref.getStkNeed(context).equalsIgnoreCase("1") && SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) ||
                (SharedPref.getDrNeed(context).equalsIgnoreCase("1") && SharedPref.getChmNeed(context).equalsIgnoreCase("1") && SharedPref.getStkNeed(context).equalsIgnoreCase("0") && SharedPref.getUnlNeed(context).equalsIgnoreCase("0"))) {

            holder.card2Layout.setVisibility(View.GONE);
        }else{
            holder.card2Layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        int count = 0;

        if (SharedPref.getDrNeed(context).equalsIgnoreCase("0")) count++;
        if (SharedPref.getChmNeed(context).equalsIgnoreCase("0")) count++;
        if (SharedPref.getStkNeed(context).equalsIgnoreCase("0")) count++;
        if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) count++;

        if (count == 0)
            return 0;

        if (count == 1)
            return 1;

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
        if(missedCount == 0 && totalCount == 0){
            missedPercentage = 0.0F;
        }

        int colorVisited;
        switch (model.getType()) {
            case "1":
                colorVisited = context.getColor(R.color.green_60);
                break;   // Doctor
            case "2":
                colorVisited = context.getColor(R.color.blue_60);
                break; // Chemist
            case "3":
                colorVisited = context.getColor(R.color.txt_sample);
                break;  // Stockist
            case "4":
                colorVisited = context.getColor(R.color.gray_45);
                break; // Unlisted
            default:
                colorVisited = context.getColor(R.color.gray_20);
        }
        int colorMissed = context.getResources().getColor(R.color.tab_gray);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(colorVisited);  // visited color based on type
        colors.add(colorMissed);

//        colors.add(context.getResources().getColor(R.color.green_60));
//        colors.add(context.getResources().getColor(R.color.mildRed));

        ArrayList<PieEntry> dataList = new ArrayList<>();
        dataList.add(new PieEntry(100.0f - missedPercentage));
        dataList.add(new PieEntry(missedPercentage, ""));

        PieDataSet dataSet = new PieDataSet(dataList, "");
        dataSet.setColors(colorVisited, colorMissed);
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setCenterText(String.format(Locale.US,"%.1f %%", missedPercentage));
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

        PieChart missedChart, missedChartChem, missedChartStk, missedChartUnlst, missedChartStk1, missedChartUnlst1;
        TextView totalDr,totalchm,totalunlst,totalstk;
        TextView totalDrCnt, visitedCnt, missedCnt;
        TextView totalchmCnt, chmvisitedCnt, chmmissedCnt;
        TextView totalstkCnt, stkvisitedCnt, stkmissedCnt, totalstkCnt1, stkvisitedCnt1, stkmissedCnt1;
        TextView totalunlstCnt, unlstvisitedCnt, unlstmissedCnt, totalunlstCnt1, unlstvisitedCnt1, unlstmissedCnt1;
        LinearLayout missedBox, chemistBox, stockiestBox, unlistedBox, drMissedCurrent, cheMissedCurrent, stkMissedCurrent, unlstMissedCurrent, stkMissedCurrent1, unlstMissedCurrent1, stockiestBox1, unlistedBox1;
        View visitedLegend, missedLegend;
        TextView drVisitTxt, chmVisitTxt, stkVisitTxt, unlstVisitTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            drMissedCurrent = itemView.findViewById(R.id.doctor_missed_current);
            cheMissedCurrent = itemView.findViewById(R.id.chemist_missed_current);
            stkMissedCurrent = itemView.findViewById(R.id.stk_missed_current);
            unlstMissedCurrent = itemView.findViewById(R.id.unlst_missed_current);

            stkMissedCurrent1 = itemView.findViewById(R.id.stk_missed_current1);
            unlstMissedCurrent1 = itemView.findViewById(R.id.unlst_missed_current1);

            drVisitTxt = itemView.findViewById(R.id.doctorVisitTxt);
            chmVisitTxt = itemView.findViewById(R.id.chemistTxt);
            stkVisitTxt = itemView.findViewById(R.id.stktxt);
            unlstVisitTxt = itemView.findViewById(R.id.unlsttxt);

            card1Layout = itemView.findViewById(R.id.missedcard1);
            card2Layout = itemView.findViewById(R.id.missedcard2);

            missedChart = itemView.findViewById(R.id.pBar);
            totalDr=itemView.findViewById(R.id.totalDr);
            totalDrCnt = itemView.findViewById(R.id.missedtotalDrCnt);
            visitedCnt = itemView.findViewById(R.id.missedvisitedCnt);
            missedCnt = itemView.findViewById(R.id.missedmissedCnt);
            missedBox = itemView.findViewById(R.id.grid3);

            missedChartChem = itemView.findViewById(R.id.pBar2);
            totalchm=itemView.findViewById(R.id.totalchm);
            totalchmCnt = itemView.findViewById(R.id.missedtotalchmCnt);
            chmvisitedCnt = itemView.findViewById(R.id.missedchmvisitedCnt);
            chmmissedCnt = itemView.findViewById(R.id.missedchmmissedCnt);
            chemistBox = itemView.findViewById(R.id.chmgrid3);

            missedChartStk = itemView.findViewById(R.id.pBar3);
            totalstk=itemView.findViewById(R.id.totalstk);
            totalstkCnt = itemView.findViewById(R.id.missedtotalstkCnt);
            stkvisitedCnt = itemView.findViewById(R.id.missedstkvisitedCnt);
            stkmissedCnt = itemView.findViewById(R.id.missedstkmissedCnt);
            stockiestBox = itemView.findViewById(R.id.stkgrid3);

            missedChartStk1 = itemView.findViewById(R.id.pBar31);
            totalstkCnt1 = itemView.findViewById(R.id.missedtotalstkCnt1);
            stkvisitedCnt1 = itemView.findViewById(R.id.missedstkvisitedCnt1);
            stkmissedCnt1 = itemView.findViewById(R.id.missedstkmissedCnt1);
            stockiestBox1 = itemView.findViewById(R.id.stkgrid31);

            missedChartUnlst = itemView.findViewById(R.id.pBar4);
            totalunlst=itemView.findViewById(R.id.totalunlst);
            totalunlstCnt = itemView.findViewById(R.id.missedtotalunlstCnt);
            unlstvisitedCnt = itemView.findViewById(R.id.missedunlstvisitedCnt);
            unlstmissedCnt = itemView.findViewById(R.id.missedunlstmissedCnt);
            unlistedBox = itemView.findViewById(R.id.unlstgrid3);

            missedChartUnlst1 = itemView.findViewById(R.id.pBar41);
            totalunlstCnt1 = itemView.findViewById(R.id.missedtotalunlstCnt1);
            unlstvisitedCnt1 = itemView.findViewById(R.id.missedunlstvisitedCnt1);
            unlstmissedCnt1 = itemView.findViewById(R.id.missedunlstmissedCnt1);
            unlistedBox1 = itemView.findViewById(R.id.unlstgrid31);

            visitedLegend = itemView.findViewById(R.id.visitedLegend);
            missedLegend = itemView.findViewById(R.id.missedLegend);

        }
    }
}
