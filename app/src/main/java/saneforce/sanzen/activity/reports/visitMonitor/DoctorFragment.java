package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.DoctorStatsAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.DoctorStatsModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentDoctorVisitReportBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class DoctorFragment extends Fragment {


    TextView headerTxt, headerTxt1, headerTxt2, doctorVisitTxt, dateTxt, totalDr, totalDrCnt, visited, visitedCnt, missed, missedCnt, FWDays, FWDaysCnt, callAvg, callAvgCnt, callCvg, callCvgCnt;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_visit_report, container, false);
      /*  headerTxt = v.findViewById(R.id.headerTxt);
        headerTxt1 = v.findViewById(R.id.headerTxt1);
        headerTxt2 = v.findViewById(R.id.headerTxt2);
        doctorVisitTxt = v.findViewById(R.id.doctorVisitTxt);
        dateTxt = v.findViewById(R.id.dateTxt);
        totalDr = v.findViewById(R.id.totalDr);
        totalDrCnt = v.findViewById(R.id.totalDrCnt);
        visited = v.findViewById(R.id.visited);
        visitedCnt = v.findViewById(R.id.visitedCnt);
        missed = v.findViewById(R.id.missed);
        missedCnt = v.findViewById(R.id.missedCnt);
        FWDays = v.findViewById(R.id.FWDays);
        FWDaysCnt = v.findViewById(R.id.FWDaysCnt);
        callAvg = v.findViewById(R.id.callAvg);
        callAvgCnt = v.findViewById(R.id.callAvgCnt);
        callCvg = v.findViewById(R.id.callCvg);
        callCvgCnt = v.findViewById(R.id.callCvgCnt);

        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());


        headerTxt.setText(SharedPref.getSfName(requireContext()));
        headerTxt1.setText(SharedPref.getHqName(requireContext()));
        headerTxt2.setText(SharedPref.getDesig(requireContext()));
        doctorVisitTxt.setText(String.format(SharedPref.getDrCap(requireContext())) + " " + "Visit");
        totalDr.setText(String.format(getString(R.string.total))+" " + SharedPref.getDrCap(requireContext()));*/


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

            Set<String> currentMonthDoctors = new HashSet<>();
            Set<String> previousMonthDoctors = new HashSet<>();
            Set<String> prePreviousMonthDoctors = new HashSet<>();

            Set<String> currentMonthFWDays = new HashSet<>();
            Set<String> previousMonthFWDays = new HashSet<>();
            Set<String> prePreviousMonthFWDays = new HashSet<>();

            Map<String, Integer> doctorVisitCounts_Cm = new HashMap<>();
            Map<String, Integer> doctorVisitCounts_Pm = new HashMap<>();
            Map<String, Integer> doctorVisitCounts_Ppm = new HashMap<>();
            /*float oneVisitCount_Cm = 0;
            float twoVisitCount_Cm = 0;
            float threeVisitCount_Cm = 0;
            float threePlusVisitCount_Cm = 0;*/


            for (JSONObject callObj : currentMonthFilteredList) {
                String doctorId = callObj.optString("CustCode", "");
                if (!doctorId.isEmpty()) {
                    currentMonthDoctors.add(doctorId);
                    currentMonthDoctors.size();

                    int count = doctorVisitCounts_Cm.getOrDefault(doctorId, 0);
                    doctorVisitCounts_Cm.put(doctorId, count + 1);
                }



          /*      for (int count : doctorVisitCounts.values()) {
                    if (count == 1) {
                        oneVisitCount_Cm++;
                    } else if (count == 2) {
                        twoVisitCount_Cm++;
                    } else if (count == 3) {
                        threeVisitCount_Cm++;
                    } else if (count > 3) {
                        threePlusVisitCount_Cm++;
                    }
                }*/
                    String FW_Code = callObj.optString("CustType");
                    String FW_Indi = callObj.optString("FW_Indicator");
                    String callDateStr = callObj.optString("Dcr_dt", "");
                    if (FW_Code.equalsIgnoreCase("0") && FW_Indi.equalsIgnoreCase("F")) {
                        currentMonthFWDays.add(callDateStr);
                        currentMonthFWDays.size();
                    }
            }
            float oneVisitCount_Cm = 0, twoVisitCount_Cm = 0, threeVisitCount_Cm = 0, threePlusVisitCount_Cm = 0;
            for (int count : doctorVisitCounts_Cm.values()) {
                if (count == 1) oneVisitCount_Cm++;
                else if (count == 2) twoVisitCount_Cm++;
                else if (count == 3) threeVisitCount_Cm++;
                else if (count > 3) threePlusVisitCount_Cm++;
            }
          /*  float oneVisitCount_pm = 0;
            float twoVisitCount_pm = 0;
            float threeVisitCount_pm = 0;
            float threePlusVisitCount_pm = 0;*/
            for (JSONObject callObj : previousMonthFilteredList) {
                String doctorId = callObj.optString("CustCode", "");
                if (!doctorId.isEmpty()) {
                    previousMonthDoctors.add(doctorId);
                    previousMonthDoctors.size();

                    int count = doctorVisitCounts_Pm.getOrDefault(doctorId, 0);
                    doctorVisitCounts_Pm.put(doctorId, count + 1);
                } else {
                    Log.d("TAG", "callFilter: " + "previousMonthDoctors month ");
                }


             /*   for (int count : doctorVisitCounts.values()) {
                    if (count == 1) {
                        oneVisitCount_pm++;
                    } else if (count == 2) {
                        twoVisitCount_pm++;
                    } else if (count == 3) {
                        threeVisitCount_pm++;
                    } else if (count > 3) {
                        threePlusVisitCount_pm++;
                    }
                }*/
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
            float oneVisitCount_Pm = 0, twoVisitCount_Pm = 0, threeVisitCount_Pm = 0, threePlusVisitCount_Pm = 0;
            for (int count : doctorVisitCounts_Pm.values()) {
                if (count == 1) oneVisitCount_Pm++;
                else if (count == 2) twoVisitCount_Pm++;
                else if (count == 3) threeVisitCount_Pm++;
                else if (count > 3) threePlusVisitCount_Pm++;
            }

          /*  float oneVisitCount_ppm = 0;
            float twoVisitCount_ppm = 0;
            float threeVisitCount_ppm = 0;
            float threePlusVisitCount_ppm = 0;*/

            for (JSONObject callObj : pre_PreviousMonthFilteredList) {
                String doctorId = callObj.optString("CustCode", "");
                if (!doctorId.isEmpty()) {
                    prePreviousMonthDoctors.add(doctorId);
                    prePreviousMonthDoctors.size();

                    int count = doctorVisitCounts_Ppm.getOrDefault(doctorId, 0);
                    doctorVisitCounts_Ppm.put(doctorId, count + 1);
                }


         /*       for (int count : doctorVisitCounts.values()) {
                    if (count == 1) {
                        oneVisitCount_ppm++;
                    } else if (count == 2) {
                        twoVisitCount_ppm++;
                    } else if (count == 3) {
                        threeVisitCount_ppm++;
                    } else if (count > 3) {
                        threePlusVisitCount_ppm++;
                    }
                }*/
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
            float oneVisitCount_Ppm = 0, twoVisitCount_Ppm = 0, threeVisitCount_Ppm = 0, threePlusVisitCount_Ppm = 0;
            for (int count : doctorVisitCounts_Ppm.values()) {
                if (count == 1) oneVisitCount_Ppm++;
                else if (count == 2) twoVisitCount_Ppm++;
                else if (count == 3) threeVisitCount_Ppm++;
                else if (count > 3) threePlusVisitCount_Ppm++;
            }


            String doctorData = masterDataDao.getDataByKey(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext()));
            JSONArray doctorArray = new JSONArray(doctorData);
            int totalDoctors = doctorArray.length();

            int currentMonthMissed = totalDoctors - currentMonthDoctors.size();
            int previousMonthMissed = totalDoctors - previousMonthDoctors.size();
            int prePreviousMonthMissed = totalDoctors - prePreviousMonthDoctors.size();

            double currentMonthCallAvg = (double) currentMonthFilteredList.size() / currentMonthFWDays.size();
            double previousMonthCallAvg = (double) previousMonthFilteredList.size() / previousMonthFWDays.size();
            double pre_PreviousMonthCallAvg = (double) pre_PreviousMonthFilteredList.size() / prePreviousMonthFWDays.size();

            double currentMonthCvg = (double) currentMonthDoctors.size() / totalDoctors * 100;
            double previousMonthCvg = (double) previousMonthDoctors.size() / totalDoctors * 100;
            double prePreviousMonthCvg = (double) prePreviousMonthDoctors.size() / totalDoctors * 100;


            // Debug
            System.out.println("Filtered JSONArray size: " + filteredCalls.length());
            System.out.println("Filtered List size: " + filteredCallList.size());
            //Month
            System.out.println("Month Filtered List size: " + currentMonthFilteredList.size());
            System.out.println("Month Filtered List size: " + previousMonthFilteredList.size());
            System.out.println("Month Filtered List size: " + pre_PreviousMonthFilteredList.size());

            //Doc
            System.out.println("Unique Doctors Current Month: " + currentMonthDoctors.size());
            System.out.println("Unique Doctors Previous Month: " + previousMonthDoctors.size());
            System.out.println("Unique Doctors Pre-Previous Month: " + prePreviousMonthDoctors.size());

            //total Doctors
            System.out.println("Total Doctors: " + totalDoctors);
            //Missed Doctors
            System.out.println("Doctors Missed CurrentMonth: " + currentMonthMissed);
            System.out.println("Doctors Missed PreviousMonth: " + previousMonthMissed);
            System.out.println("Doctors Missed Pre_PreviousMonth: " + prePreviousMonthMissed);
            //Field Work Days
            System.out.println("Field Work Days Current Month: " + currentMonthFWDays.size());
            System.out.println("Field Work Days Previous Month: " + previousMonthFWDays.size());
            System.out.println("Field Work Days Pre_Previous Month: " + prePreviousMonthFWDays.size());
            //Call Average
            System.out.println("Current Month Call Average: " + currentMonthCallAvg);
            System.out.println("Previous Month Call Average: " + previousMonthCallAvg);
            System.out.println("Pre Previous Month Call Average: " + pre_PreviousMonthCallAvg);
            //Call Coverage
            System.out.println("Current Month Coverage: " + currentMonthCvg);
            System.out.println("Previous Month Coverage: " + previousMonthCvg);
            System.out.println("Pre_Previous Month Coverage: " + prePreviousMonthCvg);


            List<DoctorStatsModel> dataList = new ArrayList<>();
            DoctorStatsModel currentMonthStats = new DoctorStatsModel(
                    String.valueOf(totalDoctors),
                    String.valueOf(currentMonthDoctors.size()),
                    String.valueOf(currentMonthMissed),
                    String.valueOf(currentMonthFWDays.size()),
                    String.valueOf(Math.round(currentMonthCallAvg)),
                    String.valueOf(Math.round(currentMonthCvg)),
                    oneVisitCount_Cm,
                    twoVisitCount_Cm,
                    threeVisitCount_Cm,
                    threePlusVisitCount_Cm

            );

            DoctorStatsModel previousMonthStats = new DoctorStatsModel(
                    String.valueOf(totalDoctors),
                    String.valueOf(previousMonthDoctors.size()),
                    String.valueOf(previousMonthMissed),
                    String.valueOf(previousMonthFWDays.size()),
                    String.valueOf(Math.round(previousMonthCallAvg)),
                    String.valueOf(Math.round(previousMonthCvg)),
                    oneVisitCount_Pm,
                    twoVisitCount_Pm,
                    threeVisitCount_Pm,
                    threePlusVisitCount_Pm
            );

            DoctorStatsModel prePreviousMonthStats = new DoctorStatsModel(
                    String.valueOf(totalDoctors),
                    String.valueOf(prePreviousMonthDoctors.size()),
                    String.valueOf(prePreviousMonthMissed),
                    String.valueOf(prePreviousMonthFWDays.size()),
                    String.valueOf(Math.round(pre_PreviousMonthCallAvg)),
                    String.valueOf(Math.round(prePreviousMonthCvg)),
                    oneVisitCount_Ppm,
                    twoVisitCount_Ppm,
                    threeVisitCount_Ppm,
                    threePlusVisitCount_Ppm
            );


            dataList.add(currentMonthStats);
            dataList.add(previousMonthStats);
            dataList.add(prePreviousMonthStats);
            DoctorStatsAdapter adapter = new DoctorStatsAdapter(dataList);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
