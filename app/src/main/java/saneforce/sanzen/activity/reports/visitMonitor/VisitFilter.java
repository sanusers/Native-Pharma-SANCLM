package saneforce.sanzen.activity.reports.visitMonitor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;

public class VisitFilter {
    private MasterDataDao masterDataDao;

    public static class MonthlyStats {
        public List<JSONObject> callList = new ArrayList<>();
        public Set<String> uniqueDoctors = new HashSet<>();
        public Set<String> uniqueChemists = new HashSet<>();
        public Set<String> uniqueStockiest = new HashSet<>();
        public Set<String> uniqueUnlisted = new HashSet<>();
        public Set<String> FWDays = new HashSet<>();
        public Map<String, Integer> customerVisitCounts = new HashMap<>();

        public int oneVisitCount = 0;
        public int twoVisitCount = 0;
        public int threeVisitCount = 0;
        public int threePlusVisitCount = 0;
    }

    public VisitFilter(MasterDataDao masterDataDao) {
        this.masterDataDao = masterDataDao;
    }

    public Map<String, MonthlyStats> callFilter() {
        Map<String, MonthlyStats> monthlyStatsMap = new HashMap<>();
        monthlyStatsMap.put("current", new MonthlyStats());
        monthlyStatsMap.put("previous", new MonthlyStats());
        monthlyStatsMap.put("prePrevious", new MonthlyStats());

        try {
            JSONArray jsonArray_call = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            JSONArray jsonArray_date = new JSONArray(masterDataDao.getDataByKey(Constants.DATE_SYNC));

            Set<String> rejectedDates = new HashSet<>();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < jsonArray_date.length(); i++) {
                JSONObject dateObj = jsonArray_date.getJSONObject(i);
                String flg = dateObj.optString("flg", "");
                if ("0".equals(flg)) continue;
                String fullDate = dateObj.getJSONObject("dt").getString("date");
                Date parsedDate = inputFormat.parse(fullDate);
                String formattedDate = outputFormat.format(parsedDate);
                rejectedDates.add(formattedDate);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar now = Calendar.getInstance();

            Calendar prevCal = (Calendar) now.clone();
            prevCal.add(Calendar.MONTH, -1);
            Calendar prePrevCal = (Calendar) now.clone();
            prePrevCal.add(Calendar.MONTH, -2);

            for (int i = 0; i < jsonArray_call.length(); i++) {
                JSONObject callObj = jsonArray_call.getJSONObject(i);
                String callDateStr = callObj.getString("Dcr_dt");

                if (!rejectedDates.contains(callDateStr)) {
                    Date callDate = sdf.parse(callDateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(callDate);

                    String monthKey = "";
                    if (cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                        monthKey = "current";
                    } else if (cal.get(Calendar.MONTH) == prevCal.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == prevCal.get(Calendar.YEAR)) {
                        monthKey = "previous";
                    } else if (cal.get(Calendar.MONTH) == prePrevCal.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == prePrevCal.get(Calendar.YEAR)) {
                        monthKey = "prePrevious";
                    }

                    if (!monthKey.isEmpty()) {
                        MonthlyStats stats = monthlyStatsMap.get(monthKey);
                        stats.callList.add(callObj);

                        String custType = callObj.optString("CustType");
                        String custCode = callObj.optString("CustCode");
                        String fwIndicator = callObj.optString("FW_Indicator");


                        if (custType.equalsIgnoreCase("1") && !custCode.isEmpty() && !custType.equals("0")) {
                            stats.uniqueDoctors.add(custCode);
                            int count = stats.customerVisitCounts.getOrDefault(custCode, 0);
                            stats.customerVisitCounts.put(custCode, count + 1);
                        } else if (custType.equalsIgnoreCase("2") && !custCode.isEmpty() && !custType.equals("0")) {
                            stats.uniqueChemists.add(custCode);
                        } else if (custType.equalsIgnoreCase("3") && !custCode.isEmpty() && !custType.equals("0")) {
                            stats.uniqueStockiest.add(custCode);
                        } else if (custType.equalsIgnoreCase("4") && !custCode.isEmpty() && !custType.equals("0")) {
                            stats.uniqueUnlisted.add(custCode);
                        }

                        if (custType.equalsIgnoreCase("0") && fwIndicator.equalsIgnoreCase("F")) {
                            stats.FWDays.add(callDateStr);
                        }
                    }
                }
            }


            for (Map.Entry<String, MonthlyStats> entry : monthlyStatsMap.entrySet()) {
                MonthlyStats stats = entry.getValue();

                for (Map.Entry<String, Integer> visitEntry : stats.customerVisitCounts.entrySet()) {
                    int visitCount = visitEntry.getValue();

                    if (visitCount == 1) {
                        stats.oneVisitCount++;
                    } else if (visitCount == 2) {
                        stats.twoVisitCount++;
                    } else if (visitCount == 3) {
                        stats.threeVisitCount++;
                    } else if (visitCount > 3) {
                        stats.threePlusVisitCount++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthlyStatsMap;
    }
}
