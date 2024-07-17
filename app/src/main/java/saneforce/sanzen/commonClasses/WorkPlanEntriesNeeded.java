package saneforce.sanzen.commonClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class WorkPlanEntriesNeeded {

    public static TreeSet<String> datesNeeded = new TreeSet<>();
    private static MasterDataDao masterDataDao;
    private static OfflineDaySubmitDao offlineDaySubmitDao;
    private static ApiInterface apiInterface;
    private static int callSyncSuccess = 0, dateSyncSuccess = 0;
    private static SyncTaskStatus syncTaskStatus;
    public static boolean isPlanningDateFound = false;

    public static void updateMyDayPlanEntryDates(Context context, boolean shouldSync, SyncTaskStatus syncTaskStatus) {
        masterDataDao = RoomDB.getDatabase(context).masterDataDao();
        offlineDaySubmitDao = RoomDB.getDatabase(context).offlineDaySubmitDao();
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        WorkPlanEntriesNeeded.syncTaskStatus = syncTaskStatus;
//        if(shouldSync) {
//            syncCallAndDate(context);
//        }else {
            setupMyDayPlanEntriesNeeded(context);
//        }
    }

    public static void syncCallAndDate(Context context) {
//        callSyncSuccess = 0;
//        dateSyncSuccess = 0;
        JSONObject jj = CommonUtilsMethods.CommonObjectParameter(context);
        try {
            jj.put("tableName", "gethome"); // -> call sync
            jj.put("sfcode", SharedPref.getSfCode(context));
            jj.put("division_code", SharedPref.getDivisionCode(context));
            jj.put("Rsf", SharedPref.getHqCode(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "home");
        for (int i = 0; i<2; i++) {
            try {
                if(i == 1) jj.put("tableName", "getdcrdate"); // -> date sync
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v("call and date sync", "--json-- " + jj);
            int finalI = i;
            Call<JsonElement> sync = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jj.toString());
            sync.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if(response.isSuccessful()) {
                        try {
                            JsonElement jsonElement = response.body();
                            assert jsonElement != null;
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            if(finalI == 0) {
                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, jsonArray.toString(), 2));
//                                callSyncSuccess = 1;
                            }
                            if(finalI == 1) {
                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, jsonArray.toString(), 2));
//                                dateSyncSuccess = 1;
                            }
//                            if(callSyncSuccess == 1 && dateSyncSuccess == 1) {
//                                setupMyDayPlanEntriesNeeded(context);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    Log.e("MyDayPlanEntry", "onFailure: " + context.getString(R.string.toast_response_failed));
//                    setupMyDayPlanEntriesNeeded(context);
//                    syncTaskStatus.onFailed();
                }
            });
        }
    }

    public static void deleteDate(String date) {
        if(datesNeeded.contains(date)) {
            datesNeeded.remove(date);
        }else Log.e("MyDayPlanEntry", "Date is not available to delete!");
    }

    private static void setupMyDayPlanEntriesNeeded(Context context) {
        datesNeeded.clear();
        TreeMap<String, String> dates = new TreeMap<>();
        boolean isCallDataAvailable = false;
        String planningDate = "";
        TreeSet<String> pastDates = new TreeSet<>();
        isPlanningDateFound = false;

        try {
            LocalDate dateBefore;
            LocalDate currentDate = LocalDate.now().plusDays(1);
            LocalDate limitDate = LocalDate.now().minusDays(91);
            boolean isTodayPresent = false, isTodayNotFinished = false;
            String SFDCR_Date_sp = SharedPref.getSfDCRDate(context);
            JSONObject obj = new JSONObject(SFDCR_Date_sp);
            String SFDCR_Date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_4, obj.getString("date"));

            if(SharedPref.getDcrSequential(context).equalsIgnoreCase("0")) {
                pastDates = getAllDatesForPastThreeMonths();
            }

            JSONArray dcrdatas = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
            if(dcrdatas.length()>0) {
                isCallDataAvailable = true;
                for (int i = 0; i<dcrdatas.length(); i++) {
                    JSONObject jsonObject = dcrdatas.getJSONObject(i);
                    String cusType = jsonObject.optString("CustType");
                    String dayStatus = jsonObject.optString("day_status");
                    String date = jsonObject.optString("Dcr_dt");
                    if(SharedPref.getDcrSequential(context).equalsIgnoreCase("0")) {
                        pastDates.remove(date);
                    }
                    if(cusType.equalsIgnoreCase("0") && date.equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4)))
                        isTodayPresent = true;
                    if(cusType.equalsIgnoreCase("0") && !dayStatus.equalsIgnoreCase("1")) {
                        dateBefore = LocalDate.parse(date);
                        if(dateBefore != null && dateBefore.isBefore(currentDate) && dateBefore.isAfter(limitDate)) {
                            dates.put(date, cusType+dayStatus);
                            datesNeeded.add(date);
                        }
                        if(date.equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4)))
                            isTodayNotFinished = true;
                    }
                }
                Log.v("TAG 1", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
            }
            JSONArray dateSync = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray();
            if(dateSync.length()>0) {
                for (int i = 0; i<dateSync.length(); i++) {
                    JSONObject jsonObject = dateSync.getJSONObject(i);
                    String flag = jsonObject.optString("flg");
                    String tbName = jsonObject.optString("tbname");
                    String date = jsonObject.getJSONObject("dt").getString("date").substring(0, 10);
                    if(SharedPref.getDcrSequential(context).equalsIgnoreCase("0")) {
                        pastDates.remove(date);
                    }
                    if(tbName.equalsIgnoreCase("missed") ||
                            (tbName.equalsIgnoreCase("dcr") && (flag.equalsIgnoreCase("2") || (flag.equalsIgnoreCase("3")))) ||
                            flag.equalsIgnoreCase("0")) {
                        if(flag.equalsIgnoreCase("0")){
                            Log.v("status 0 ", "setupMyDayPlanEntriesNeeded: " + date);
                            if(!isPlanningDateFound) {
                                isPlanningDateFound = true;
                                planningDate = date;
                            }
                        }
                        dateBefore = LocalDate.parse(date);
                        if(dateBefore != null && dateBefore.isBefore(currentDate) && dateBefore.isAfter(limitDate)) {
                            datesNeeded.add(date);
                        }
                    }
                }
            }
            Log.v("TAG 2", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
            List<String> offlineDaySubmitDates = offlineDaySubmitDao.getAllOfflineDaySubmitDates();
            if(!offlineDaySubmitDates.isEmpty()) {
                for (String date : offlineDaySubmitDates) {
                    datesNeeded.remove(date);
                }
                Log.v("TAG 3", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
            }
            if(!isTodayPresent || isTodayNotFinished) {
                datesNeeded.add(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
                Log.v("TAG 4", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
            }

            JSONArray holidayJSONArray = masterDataDao.getMasterDataTableOrNew(Constants.HOLIDAY).getMasterSyncDataJsonArray();
            for (int i = 0; i < holidayJSONArray.length(); i++) {
                JSONObject jsonObject = holidayJSONArray.getJSONObject(i);
                String holidayDate = jsonObject.optString("holiday_date");
                if(datesNeeded != null && !datesNeeded.isEmpty()) {
                    datesNeeded.remove(holidayDate);
                }
            }

            JSONArray weeklyOff = masterDataDao.getMasterDataTableOrNew(Constants.WEEKLY_OFF).getMasterSyncDataJsonArray();
            String holidayMode = "";
            for (int i = 0; i < weeklyOff.length(); i++) {
                JSONObject jsonObject = weeklyOff.getJSONObject(i);
                holidayMode = jsonObject.getString("Holiday_Mode");
            }
            String[] holidayModeArray = holidayMode.split(",");
            ArrayList<String> weeklyOffDays = new ArrayList<>();
            for (String str : holidayModeArray) {
                switch (str) {
                    case "0": {
                        weeklyOffDays.add("Sunday");
                        break;
                    }
                    case "1": {
                        weeklyOffDays.add("Monday");
                        break;
                    }
                    case "2": {
                        weeklyOffDays.add("Tuesday");
                        break;
                    }
                    case "3": {
                        weeklyOffDays.add("Wednesday");
                        break;
                    }
                    case "4": {
                        weeklyOffDays.add("Thursday");
                        break;
                    }
                    case "5": {
                        weeklyOffDays.add("Friday");
                        break;
                    }
                    case "6": {
                        weeklyOffDays.add("Saturday");
                        break;
                    }
                }
            }

            TreeSet<String> datesNeededDup = new TreeSet<>(datesNeeded);
            for (String date : datesNeededDup) {
                String dayName = LocalDate.parse(date, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
                if(weeklyOffDays.contains(dayName)) {
                    datesNeeded.remove(date);
                }
            }

            if(SharedPref.getDcrSequential(context).equalsIgnoreCase("0")) {
                pastDates.addAll(datesNeeded);
                datesNeeded = pastDates;
                Log.i("past dates", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(pastDates.toArray()));
            }

            datesNeededDup = new TreeSet<>(datesNeeded);
            for (String dt : datesNeededDup) {
                LocalDate date = LocalDate.parse(dt, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                LocalDate joiningDate = LocalDate.parse(SFDCR_Date, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                if(date.isBefore(joiningDate)) {
                    datesNeeded.remove(dt);
                } else if(date.isEqual(joiningDate)) {
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String date = null;
        Log.i("TAG", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
        if(!SharedPref.getDayPlanStartedDate(context).isEmpty() && datesNeeded.contains(SharedPref.getDayPlanStartedDate(context)))  {
            Log.e("set date switched1 ", "setupMyDayPlanEntriesNeeded: " + SharedPref.getDayPlanStartedDate(context));
            date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, SharedPref.getDayPlanStartedDate(context));
        }
        else if(SharedPref.getDcrSequential(context).equalsIgnoreCase("0")) {
            Log.e("set date sequential", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
            date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first());
        }
        else if(!SharedPref.getSelectedDateCal(context).isEmpty() && datesNeeded.contains(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, SharedPref.getSelectedDateCal(context)))) {
            Log.e("set date switched2 ", "setupMyDayPlanEntriesNeeded: " + SharedPref.getSelectedDateCal(context));
            date = SharedPref.getSelectedDateCal(context);
        }
        else if(SharedPref.getSelectedDateCal(context).isEmpty() && !datesNeeded.isEmpty() && isCallDataAvailable){
            Log.e("set date first", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
            date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first());
        }
        else if(SharedPref.getSelectedDateCal(context).isEmpty() && !datesNeeded.isEmpty() && !isCallDataAvailable){
            Log.e("not set date first", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
        }
        else if(dates.containsKey(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4)) && Objects.requireNonNull(dates.get(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))).equalsIgnoreCase("01") && datesNeeded.isEmpty()) {
            Log.e("set date today finished", "setupMyDayPlanEntriesNeeded: dates empty");
        }
        else if(!datesNeeded.isEmpty()) {
            Log.e("set date", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
            date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first());
        }else {
            Log.e("set date empty", "setupMyDayPlanEntriesNeeded: dates empty");
        }

        if(isPlanningDateFound) {
            Log.d("Planning found", "setupMyDayPlanEntriesNeeded: Planning date found");
            SharedPref.setSelectedDateCal(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, planningDate));
            syncTaskStatus.datesFound();
        } else if(SharedPref.getDcrSequential(context).equalsIgnoreCase("0")) {
            if(date != null && !date.isEmpty()) {
                SharedPref.setSelectedDateCal(context, date);
                syncTaskStatus.datesFound();
            } else {
                SharedPref.setSelectedDateCal(context, null);
                syncTaskStatus.noDatesFound();
            }
        } else {
            if(!SharedPref.getSelectedDateCal(context).isEmpty()) {
                syncTaskStatus.datesFound();
            }else {
                SharedPref.setSelectedDateCal(context, null);
                syncTaskStatus.noDatesFound();
            }
        }
    }

    public interface SyncTaskStatus {
        void datesFound();
        void noDatesFound();
    }

    public static TreeSet<String> getAllDatesForPastThreeMonths() {
        TreeSet<String> dates = new TreeSet<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Get dates for current month up to current date
        int currentDay = currentDate.getDayOfMonth();
        for (int day =1; day <= currentDay; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            dates.add(date.format(formatter));
        }

        // Get dates for past two months
        for (int i = 1; i <= 2; i++) {
            LocalDate pastMonth = currentDate.minusMonths(i);
            for (int day = 1; day <= pastMonth.lengthOfMonth(); day++) {
                LocalDate date = pastMonth.withDayOfMonth(day);
                dates.add(date.format(formatter));
            }
        }

        return dates;
    }

}
