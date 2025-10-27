package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ChemistVisitFragment extends Fragment {

    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    private static final String ARG_MONTH_DATA = "monthData";
    private static final String ARG_POSITION = "position";
    private List<String> monthData;
    private BarChart barChart;
    private  List<VisitStatsModel> dataListChm;
    int position;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtils.FORMAT_23, Locale.getDefault());

    public ChemistVisitFragment(List<VisitStatsModel> dataListChm) {
        this.dataListChm = dataListChm;
    }

    public static ChemistVisitFragment newInstance( List<VisitStatsModel> chemistStats, int position) {
       ChemistVisitFragment fragment = new ChemistVisitFragment(chemistStats);
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.monthData = getArguments().getStringArrayList(ARG_MONTH_DATA);
            position = getArguments().getInt(ARG_POSITION, 0);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adapter_doctor_visit_report, container, false);

        TextView chemistVst = view.findViewById(R.id.custTxt);
        TextView totalChmTxt = view.findViewById(R.id.totalCust);
        TextView monthTxt = view.findViewById(R.id.monthTxt);
        TextView totalChmCnt = view.findViewById(R.id.totalCustCnt);
        TextView visitedCnt = view.findViewById(R.id.visitedCnt);
        TextView missedCnt = view.findViewById(R.id.missedCnt);
        TextView FWDaysCnt = view.findViewById(R.id.FWDaysCnt);
        TextView callAvgCnt = view.findViewById(R.id.callAvgCnt);
        TextView callCvgCnt = view.findViewById(R.id.callCvgCnt);
        ImageView ChemistImage = view.findViewById(R.id.custImg);

        barChart  = view.findViewById(R.id.in_chart_visit);

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

        chemistVst.setText(SharedPref.getChmCap(requireContext()));
        totalChmTxt.setText("Total"+" "+SharedPref.getChmCap(requireContext()));

        ChemistImage.setImageDrawable(getResources().getDrawable(R.drawable.chemist_img));


        if (dataListChm != null && !dataListChm.isEmpty()) {
            VisitStatsModel model = dataListChm.get(position);

            totalChmCnt.setText(model.getTotalCustomers());
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
       /*     setupPieChart(
                    (int) model.getOneVisitCount(),
                    (int) model.getTwoVisitCount(),
                    (int) model.getThreeVisitCount(),
                    (int) model.getThreePlusVisitCount()
            );*/
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
        int[] colors = new int[] {
                requireContext().getResources().getColor(R.color.indigo),
                requireContext().getResources().getColor(R.color.green_60),
                requireContext().getResources().getColor(R.color.pink_45),
                requireContext().getResources().getColor(R.color.blue_60)
        };
        set.setColors(colors);
        BarData data = new BarData(set);
        data.setBarWidth(0.2f);

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



}
