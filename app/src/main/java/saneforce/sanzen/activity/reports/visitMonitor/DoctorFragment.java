package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.VisitStatsAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.DoctorStatsModel;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentDoctorVisitReportBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class DoctorFragment extends Fragment {

    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    private BarChart barChart;
    private PieChart pieChart;
    private static final String ARG_MONTH_DATA = "monthData";
    private static final String ARG_STATS_DATA = "statsData";
    private static final String ARG_POSITION = "position";

    private List<String> monthData;
    private  List<VisitStatsModel> dataList;
    int position;


    public static DoctorFragment newInstance(List<String> monthData, List<VisitStatsModel> statsData) {
        DoctorFragment fragment = new DoctorFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_MONTH_DATA, new ArrayList<>(monthData));
        args.putInt(ARG_POSITION, 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adapter_doctor_visit_report, container, false);

        TextView doctorVst = v.findViewById(R.id.doctorVisitTxt);
        TextView totalDrTxt = v.findViewById(R.id.totalDr);
        TextView monthTxt = v.findViewById(R.id.monthTxt);
        TextView yearTxt = v.findViewById(R.id.yearTxt);
        TextView totalDrCnt = v.findViewById(R.id.totalDrCnt);
        TextView visitedCnt = v.findViewById(R.id.visitedCnt);
        TextView missedCnt = v.findViewById(R.id.missedCnt);
        TextView FWDaysCnt = v.findViewById(R.id.FWDaysCnt);
        TextView callAvgCnt = v.findViewById(R.id.callAvgCnt);
        TextView callCvgCnt = v.findViewById(R.id.callCvgCnt);

        // Assign the charts to the member variables
        barChart  = v.findViewById(R.id.in_chart_visit);
        pieChart  = v.findViewById(R.id.pieChart_visit);

        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());

        if (monthData != null && monthData.size() >= 2) {
            monthTxt.setText(monthData.get(0));
            yearTxt.setText(monthData.get(1));
        }
        doctorVst.setText(SharedPref.getDrCap(requireContext())+" "+"Visit");
        totalDrTxt.setText("Total"+" "+SharedPref.getDrCap(requireContext()));
        // Dummy data for example purposes;

        if (dataList != null && !dataList.isEmpty()) {
            VisitStatsModel model = dataList.get(position);

            // Set the TextViews with the data from the VisitStatsModel object
            totalDrCnt.setText(model.getTotalCustomers());
            visitedCnt.setText(model.getVisitedCustomers());
            missedCnt.setText(model.getMissedCustomers());
            FWDaysCnt.setText(model.getFwDays());
            callAvgCnt.setText(model.getCallAvg());
            callCvgCnt.setText(model.getCoverage() + "%");

            // Call the setup methods for the charts using the data from the model
            setupBarChart(
                    Integer.parseInt(model.getTotalCustomers()),
                    Integer.parseInt(model.getVisitedCustomers()),
                    Integer.parseInt(model.getMissedCustomers()),
                    Double.parseDouble(model.getCallAvg())
            );
            setupPieChart(
                    model.getOneVisitCount(),
                    model.getTwoVisitCount(),
                    model.getThreeVisitCount(),
                    model.getThreePlusVisitCount()
            );
        }

        return v;
    }


    private void setupBarChart(int total, int visited, int missed, double callAvg) {
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add(getString(R.string.total));
        xVals.add(getString(R.string.visit));
        xVals.add(getString(R.string.miss));
        xVals.add(getString(R.string.avg));

        entries.add(new BarEntry(0f, total));
        entries.add(new BarEntry(1f, visited));
        entries.add(new BarEntry(2f, missed));
        entries.add(new BarEntry(3f, (float) callAvg));

        BarDataSet set = new BarDataSet(entries, "Visit Data");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(set);
        data.setBarWidth(0.5f);

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

    // New method to handle the Pie Chart setup
    private void setupPieChart(int oneVisit, int twoVisit, int threeVisit, int threePlusVisit) {
        pieChart.setCenterText("Visit Analysis");
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextColor(requireContext().getResources().getColor(R.color.black));
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(requireContext().getResources().getColor(R.color.white));
        pieChart.setTransparentCircleColor(requireContext().getResources().getColor(R.color.white));
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(63f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawEntryLabels(false);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(oneVisit, "1 Visit"));
        entries.add(new PieEntry(twoVisit, "2 Visits"));
        entries.add(new PieEntry(threeVisit, "3 Visits"));
        entries.add(new PieEntry(threePlusVisit, "3+ Visits"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(requireContext().getResources().getColor(R.color.blue_60));
        colors.add(requireContext().getResources().getColor(R.color.yellow_45));
        colors.add(requireContext().getResources().getColor(R.color.red_60));
        colors.add(requireContext().getResources().getColor(R.color.green_2));
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        pieChart.animateY(1400);
        pieChart.invalidate();
    }
}