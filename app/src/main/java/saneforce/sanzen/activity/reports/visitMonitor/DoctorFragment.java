package saneforce.sanzen.activity.reports.visitMonitor;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

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
import saneforce.sanzen.utility.TimeUtils;

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
    private  List<VisitStatsModel> dataListDoc;
    int position;

    public DoctorFragment(List<VisitStatsModel> dataListDoc) {
        this.dataListDoc = dataListDoc;
    }

    public static DoctorFragment newInstance(List<VisitStatsModel> statsData,int position) {

        DoctorFragment fragment = new DoctorFragment(statsData);
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;

        /*DoctorFragment fragment = new DoctorFragment(statsData);
        return fragment;*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            position = getArguments().getInt(ARG_POSITION, position);
                position = getArguments().getInt(ARG_POSITION, 2);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adapter_doctor_visit_report, container, false);

        TextView doctorVst = v.findViewById(R.id.custTxt);
        TextView totalDrTxt = v.findViewById(R.id.totalCust);
        TextView monthTxt = v.findViewById(R.id.monthTxt);
        TextView yearTxt = v.findViewById(R.id.yearTxt);
        TextView totalDrCnt = v.findViewById(R.id.totalCustCnt);
        TextView visitedCnt = v.findViewById(R.id.visitedCnt);
        TextView missedCnt = v.findViewById(R.id.missedCnt);
        TextView FWDaysCnt = v.findViewById(R.id.FWDaysCnt);
        TextView callAvgCnt = v.findViewById(R.id.callAvgCnt);
        TextView callCvgCnt = v.findViewById(R.id.callCvgCnt);


        barChart  = v.findViewById(R.id.barChartVisit);
        pieChart  = v.findViewById(R.id.pieChart_visit);
        pieChart.setVisibility(View.VISIBLE);

        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());

        if (monthData != null && monthData.size() >= 2) {
            monthTxt.setText(monthData.get(0));
            yearTxt.setText(monthData.get(1));
        }
        monthTxt.setText(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_23));
        doctorVst.setText(SharedPref.getDrCap(requireContext())+" "+"Visit");
        totalDrTxt.setText("Total"+" "+SharedPref.getDrCap(requireContext()));
        if (dataListDoc != null && !dataListDoc.isEmpty()) {
            switch (position){
                case 0:
                    VisitStatsModel model = dataListDoc.get(0);
                    totalDrCnt.setText(model.getTotalCustomers());
                    visitedCnt.setText(model.getVisitedCustomers());
                    missedCnt.setText(model.getMissedCustomers());
                    FWDaysCnt.setText(model.getFwDays());
                    callAvgCnt.setText(model.getCallAvg());
                    callCvgCnt.setText(model.getCoverage() + "%");

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
                    break;
                case 1:
                    VisitStatsModel model1 = dataListDoc.get(1);
                    totalDrCnt.setText(model1.getTotalCustomers());
                    visitedCnt.setText(model1.getVisitedCustomers());
                    missedCnt.setText(model1.getMissedCustomers());
                    FWDaysCnt.setText(model1.getFwDays());
                    callAvgCnt.setText(model1.getCallAvg());
                    callCvgCnt.setText(model1.getCoverage() + "%");

                    setupBarChart(
                            Integer.parseInt(model1.getTotalCustomers()),
                            Integer.parseInt(model1.getVisitedCustomers()),
                            Integer.parseInt(model1.getMissedCustomers()),
                            Double.parseDouble(model1.getCallAvg())
                    );
                    setupPieChart(
                            model1.getOneVisitCount(),
                            model1.getTwoVisitCount(),
                            model1.getThreeVisitCount(),
                            model1.getThreePlusVisitCount()
                    );
                    break;
                case 2:
                    VisitStatsModel model2 = dataListDoc.get(2);
                    totalDrCnt.setText(model2.getTotalCustomers());
                    visitedCnt.setText(model2.getVisitedCustomers());
                    missedCnt.setText(model2.getMissedCustomers());
                    FWDaysCnt.setText(model2.getFwDays());
                    callAvgCnt.setText(model2.getCallAvg());
                    callCvgCnt.setText(model2.getCoverage() + "%");

                    setupBarChart(
                            Integer.parseInt(model2.getTotalCustomers()),
                            Integer.parseInt(model2.getVisitedCustomers()),
                            Integer.parseInt(model2.getMissedCustomers()),
                            Double.parseDouble(model2.getCallAvg())
                    );
                    setupPieChart(
                            model2.getOneVisitCount(),
                            model2.getTwoVisitCount(),
                            model2.getThreeVisitCount(),
                            model2.getThreePlusVisitCount()
                    );
                    break;

            }

            /*VisitStatsModel model = dataListDoc.get(position);
            totalDrCnt.setText(model.getTotalCustomers());
            visitedCnt.setText(model.getVisitedCustomers());
            missedCnt.setText(model.getMissedCustomers());
            FWDaysCnt.setText(model.getFwDays());
            callAvgCnt.setText(model.getCallAvg());
            callCvgCnt.setText(model.getCoverage() + "%");

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
            );*/
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
        set.setDrawValues(false);
        int[] colors = new int[] {
                requireContext().getResources().getColor(R.color.indigo),
                requireContext().getResources().getColor(R.color.green_60),
                requireContext().getResources().getColor(R.color.pink_45),
                requireContext().getResources().getColor(R.color.blue_60)
        };
        set.setColors(colors);
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

    private void setupPieChart(int oneVisit, int twoVisit, int threeVisit, int threePlusVisit) {
        pieChart.setCenterText("Visits");
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