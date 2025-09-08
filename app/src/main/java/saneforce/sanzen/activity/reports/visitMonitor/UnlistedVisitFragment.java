package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.ChemistStatsAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.UnlistedDoctorStatsAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.ChemistStatsModel;
import saneforce.sanzen.activity.reports.visitMonitor.model.UnlistedStatsModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class UnlistedVisitFragment extends Fragment {

    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    private static final String ARG_MONTH_DATA = "monthData";
    private List<String> monthData;

    public static UnlistedVisitFragment newInstance(List<String> monthData) {
        UnlistedVisitFragment fragment = new UnlistedVisitFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_MONTH_DATA, new ArrayList<>(monthData));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.monthData = getArguments().getStringArrayList(ARG_MONTH_DATA);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_unlisteddr_visit_report, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        callFilter(recyclerView);

        return v;
    }
    public void callFilter(RecyclerView recyclerView) {
        try {
            JSONArray jsonArray_call = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            JSONArray jsonArray_date = new JSONArray(masterDataDao.getDataByKey(Constants.DATE_SYNC));

            Set<String> rejectedDates = new HashSet<>();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < jsonArray_date.length(); i++) {
                JSONObject dateObj = jsonArray_date.getJSONObject(i);
                String flg = dateObj.optString("flg", "");
                if ("0".equals(flg)) {
                    continue;
                }
                String fullDate = dateObj.getJSONObject("dt").getString("date");
                Date parsedDate = inputFormat.parse(fullDate);
                String formattedDate = outputFormat.format(parsedDate);
                rejectedDates.add(formattedDate);
            }

            JSONArray filteredCalls = new JSONArray();
            List<JSONObject> filteredCallList = new ArrayList<>();

            for (int i = 0; i < jsonArray_call.length(); i++) {
                JSONObject callObj = jsonArray_call.getJSONObject(i);
                String callDate = callObj.getString("Dcr_dt");
                if (!rejectedDates.contains(callDate)) {
                    filteredCalls.put(callObj);
                    filteredCallList.add(callObj);
                }
            }

            List<JSONObject> currentMonthFilteredList = new ArrayList<>();
            List<JSONObject> previousMonthFilteredList = new ArrayList<>();
            List<JSONObject> pre_PreviousMonthFilteredList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


            Calendar now = Calendar.getInstance();
            int currentMonth = now.get(Calendar.MONTH);
            int currentYear = now.get(Calendar.YEAR);

            // Previous month
            Calendar prevCal = (Calendar) now.clone();
            prevCal.add(Calendar.MONTH, -1);
            int previousMonth = prevCal.get(Calendar.MONTH);
            int previousYear = prevCal.get(Calendar.YEAR);

            // Pre-Previous month
            Calendar prePrevCal = (Calendar) now.clone();
            prePrevCal.add(Calendar.MONTH, -2);
            int prePreviousMonth = prePrevCal.get(Calendar.MONTH);
            int prePreviousYear = prePrevCal.get(Calendar.YEAR);

            for (JSONObject callObj : filteredCallList) {
                String callDateStr = callObj.optString("Dcr_dt", "");
                if (!callDateStr.isEmpty()) {
                    Date callDate = sdf.parse(callDateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(callDate);

                    int callMonth = cal.get(Calendar.MONTH);
                    int callYear = cal.get(Calendar.YEAR);

                    if (callMonth == currentMonth && callYear == currentYear) {
                        currentMonthFilteredList.add(callObj);
                    } else if (callMonth == previousMonth && callYear == previousYear) {
                        previousMonthFilteredList.add(callObj);
                    } else if (callMonth == prePreviousMonth && callYear == prePreviousYear) {
                        pre_PreviousMonthFilteredList.add(callObj);
                    }
                }
            }

            Set<String> currentMonthUnlisted = new HashSet<>();
            Set<String> previousMonthUnlisted = new HashSet<>();
            Set<String> prePreviousMonthUnlisted = new HashSet<>();

            Set<String> currentMonthFWDays = new HashSet<>();
            Set<String> previousMonthFWDays = new HashSet<>();
            Set<String> prePreviousMonthFWDays = new HashSet<>();

            for (JSONObject callObj : currentMonthFilteredList) {
                String chemistId = callObj.optString("CustCode", "");
                String custType = callObj.optString("CustType", "");
                if (!chemistId.isEmpty() && custType.equalsIgnoreCase("4")) {
                    currentMonthUnlisted.add(chemistId);
                    currentMonthUnlisted.size();

                }

                String FW_Code = callObj.optString("CustType");
                String FW_Indi = callObj.optString("FW_Indicator");
                String callDateStr = callObj.optString("Dcr_dt", "");
                if (FW_Code.equalsIgnoreCase("0") && FW_Indi.equalsIgnoreCase("F")) {
                    currentMonthFWDays.add(callDateStr);
                    currentMonthFWDays.size();
                }
            }

            for (JSONObject callObj : previousMonthFilteredList) {
                String chemistId = callObj.optString("CustCode", "");
                String custType = callObj.optString("CustType", "");
                if (!chemistId.isEmpty() && custType.equalsIgnoreCase("4")) {
                    previousMonthUnlisted.add(chemistId);
                    previousMonthUnlisted.size();
                } else {
                    Log.d("TAG", "callFilter: " + "previousMonthChemist month is 0");
                }

                for (JSONObject callObj1 : currentMonthFilteredList) {
                    String FW_Code = callObj1.optString("CustType");
                    String FW_Indi = callObj1.optString("FW_Indicator");
                    String callDateStr = callObj.optString("Dcr_dt", "");
                    if (FW_Code.equalsIgnoreCase("0") && FW_Indi.equalsIgnoreCase("F")) {
                        previousMonthFWDays.add(callDateStr);
                        previousMonthFWDays.size();
                    }
                }
            }

            for (JSONObject callObj : pre_PreviousMonthFilteredList) {
                String chemistId = callObj.optString("CustCode", "");
                String custType = callObj.optString("CustType", "");
                if (!chemistId.isEmpty() && custType.equalsIgnoreCase("4")) {
                    prePreviousMonthUnlisted.add(chemistId);
                    prePreviousMonthUnlisted.size();
                }

                for (JSONObject callObj1 : pre_PreviousMonthFilteredList) {
                    String FW_Code = callObj1.optString("CustType");
                    String FW_Indi = callObj1.optString("FW_Indicator");
                    String callDateStr = callObj.optString("Dcr_dt", "");
                    if (FW_Code.equalsIgnoreCase("0") && FW_Indi.equalsIgnoreCase("F")) {
                        prePreviousMonthFWDays.add(callDateStr);
                        prePreviousMonthFWDays.size();
                    }
                }
            }

            String UnlistedData = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray UnlistedArray = new JSONArray(UnlistedData);
            int totalUnlisted = UnlistedArray.length();

            int currentMonthMissed = totalUnlisted - currentMonthUnlisted.size();
            int previousMonthMissed = totalUnlisted - previousMonthUnlisted.size();
            int prePreviousMonthMissed = totalUnlisted - prePreviousMonthUnlisted.size();

            double currentMonthCallAvg = (double) currentMonthFilteredList.size() / currentMonthFWDays.size();
            double previousMonthCallAvg = (double) previousMonthFilteredList.size() / previousMonthFWDays.size();
            double pre_PreviousMonthCallAvg = (double) pre_PreviousMonthFilteredList.size() / prePreviousMonthFWDays.size();

            double currentMonthCvg = (double) currentMonthUnlisted.size() / totalUnlisted * 100;
            double previousMonthCvg = (double) previousMonthUnlisted.size() / totalUnlisted * 100;
            double prePreviousMonthCvg = (double) prePreviousMonthUnlisted.size() / totalUnlisted * 100;

            List<UnlistedStatsModel> dataList = new ArrayList<>();
            UnlistedStatsModel currentMonthStats = new UnlistedStatsModel(
                    String.valueOf(totalUnlisted),
                    String.valueOf(currentMonthUnlisted.size()),
                    String.valueOf(currentMonthMissed),
                    String.valueOf(currentMonthFWDays.size()),
                    String.valueOf(Math.round(currentMonthCallAvg)),
                    String.valueOf(Math.round(currentMonthCvg))

            );

            UnlistedStatsModel previousMonthStats = new UnlistedStatsModel(
                    String.valueOf(totalUnlisted),
                    String.valueOf(previousMonthUnlisted.size()),
                    String.valueOf(previousMonthMissed),
                    String.valueOf(previousMonthFWDays.size()),
                    String.valueOf(Math.round(previousMonthCallAvg)),
                    String.valueOf(Math.round(previousMonthCvg))

            );

            UnlistedStatsModel prePreviousMonthStats = new UnlistedStatsModel(
                    String.valueOf(totalUnlisted),
                    String.valueOf(prePreviousMonthUnlisted.size()),
                    String.valueOf(prePreviousMonthMissed),
                    String.valueOf(prePreviousMonthFWDays.size()),
                    String.valueOf(Math.round(pre_PreviousMonthCallAvg)),
                    String.valueOf(Math.round(prePreviousMonthCvg))
            );

            dataList.add(currentMonthStats);
            dataList.add(previousMonthStats);
            dataList.add(prePreviousMonthStats);
            UnlistedDoctorStatsAdapter adapter = new UnlistedDoctorStatsAdapter(dataList);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
