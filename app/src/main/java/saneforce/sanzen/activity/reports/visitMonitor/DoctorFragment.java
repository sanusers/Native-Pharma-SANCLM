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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
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


    private List<VisitStatsModel> dataListDoc;
    int position;

    public DoctorFragment(List<VisitStatsModel> dataListDoc) {
        this.dataListDoc = dataListDoc;
    }

    public static DoctorFragment newInstance(List<VisitStatsModel> statsData, int position) {

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
            position = getArguments().getInt(ARG_POSITION, 0);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adapter_doctor_visit_report, container, false);

        TextView doctorVst = view.findViewById(R.id.custTxt);
        TextView totalDrTxt = view.findViewById(R.id.totalCust);
        TextView monthTxt = view.findViewById(R.id.monthTxt);
        TextView totalDrCnt = view.findViewById(R.id.totalCustCnt);
        TextView visitedCnt = view.findViewById(R.id.visitedCnt);
        TextView missedCnt = view.findViewById(R.id.missedCnt);
        TextView FWDaysCnt = view.findViewById(R.id.FWDaysCnt);
        TextView callAvgCnt = view.findViewById(R.id.callAvgCnt);
        TextView callCvgCnt = view.findViewById(R.id.callCvgCnt);


        barChart = view.findViewById(R.id.in_chart_visit);
        pieChart = view.findViewById(R.id.pieChart_visit);
        pieChart.setVisibility(View.VISIBLE);

        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());

        switch (position) {
            case 0:
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 0);
                Date currentMonthDate = calendar.getTime();
                String currentMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(currentMonthDate);
                String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(currentMonthDate);
                String formattedDate = currentMonth + " " + currentYear;
                monthTxt.setText(formattedDate);
                break;
            case 1:
                Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.MONTH, -1);
                Date previousMonthDate = calendar1.getTime();
                String previousMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(previousMonthDate);
                String currentYear1 = new SimpleDateFormat("yyyy", Locale.getDefault()).format(previousMonthDate);
                String formattedDate1 = previousMonth + " " + currentYear1;
                monthTxt.setText(formattedDate1);
                break;
            case 2:
                Calendar calendar2 = Calendar.getInstance();
                calendar2.add(Calendar.MONTH, -2);
                Date prePreviousMonthDate = calendar2.getTime();
                String prePreviousMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(prePreviousMonthDate);
                String currentYear2 = new SimpleDateFormat("yyyy", Locale.getDefault()).format(prePreviousMonthDate);
                String formattedDate2 = prePreviousMonth + " " + currentYear2;
                monthTxt.setText(formattedDate2);
                break;
        }


        doctorVst.setText(SharedPref.getDrCap(requireContext()));
        totalDrTxt.setText("Total" + " " + SharedPref.getDrCap(requireContext()));
        if (dataListDoc != null && !dataListDoc.isEmpty()) {
            if (position < dataListDoc.size()) {
                VisitStatsModel model = dataListDoc.get(position);
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
            }
        }

        return view;
    }


    private void setupBarChart(int total, int visited, int missed, double callAvg) {
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add(getString(R.string.total));
        xVals.add(getString(R.string.visited));
        xVals.add(getString(R.string.miss));
        xVals.add(getString(R.string.avg));

        entries.add(new BarEntry(0f, total));
        entries.add(new BarEntry(1f, visited));
        entries.add(new BarEntry(2f, missed));
        entries.add(new BarEntry(3f, (float) callAvg));

        BarDataSet set = new BarDataSet(entries, "Visit Data");
        set.setDrawValues(false);
        int[] colors = new int[]{
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
            pieChart.setHoleRadius(63f);
            pieChart.setTransparentCircleRadius(10f);
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

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return value == 0 ? "" : String.valueOf((int) value);
                }
            });
            data.setValueTextSize(20f);
            data.setValueTextColor(requireContext().getResources().getColor(R.color.bg_lit_white));

            pieChart.setData(data);

            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setXEntrySpace(7f);
            legend.setYEntrySpace(0f);
            legend.setYOffset(0f);
            legend.setWordWrapEnabled(true);
            legend.setForm(Legend.LegendForm.CIRCLE);

            pieChart.animateY(1400);
            pieChart.invalidate();

        }
   /* private void setupPieChart(int oneVisit, int twoVisit, int threeVisit, int threePlusVisit) {
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextColor(requireContext().getResources().getColor(R.color.black));
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(requireContext().getResources().getColor(R.color.white));
        pieChart.setHoleRadius(63f);
        pieChart.setTransparentCircleRadius(10f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawEntryLabels(false);

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

            // Normal data
            pieChart.setCenterText("Visits");
            entries.add(new PieEntry(oneVisit, "1 Visit"));
            entries.add(new PieEntry(twoVisit, "2 Visits"));
            entries.add(new PieEntry(threeVisit, "3 Visits"));
            entries.add(new PieEntry(threePlusVisit, "3+ Visits"));

            colors.add(requireContext().getResources().getColor(R.color.blue_60));
            colors.add(requireContext().getResources().getColor(R.color.yellow_45));
            colors.add(requireContext().getResources().getColor(R.color.red_60));
            colors.add(requireContext().getResources().getColor(R.color.green_2));


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value == 0 ? "" : String.valueOf((int) value);
            }
        });
        data.setValueTextSize(20f);
        data.setValueTextColor(requireContext().getResources().getColor(R.color.bg_lit_white));

        pieChart.setData(data);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setWordWrapEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);

        pieChart.animateY(1400);
        pieChart.invalidate();
    }*/

    }
