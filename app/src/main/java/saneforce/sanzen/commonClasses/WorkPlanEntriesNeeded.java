package saneforce.sanzen.commonClasses;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
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
        try {
            LocalDate dateBefore;
            LocalDate currentDate = LocalDate.now().plusDays(1);
            LocalDate limitDate = LocalDate.now().minusDays(91);
            boolean isTodayPresent = false, isTodayNotFinished = false;

            JSONArray dcrdatas = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
            if(dcrdatas.length()>0) {
                isCallDataAvailable = true;
                for (int i = 0; i<dcrdatas.length(); i++) {
                    JSONObject jsonObject = dcrdatas.getJSONObject(i);
                    String cusType = jsonObject.optString("CustType");
                    String dayStatus = jsonObject.optString("day_status");
                    String date = jsonObject.optString("Dcr_dt");
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
                    if(tbName.equalsIgnoreCase("missed") ||
                            (tbName.equalsIgnoreCase("dcr") && (flag.equalsIgnoreCase("2") || (flag.equalsIgnoreCase("3")))) ||
                            flag.equalsIgnoreCase("0")) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String date = null;
        Log.i("TAG", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
        if(!SharedPref.getDayPlanStartedDate(context).isEmpty() && datesNeeded.contains(SharedPref.getDayPlanStartedDate(context)))  {
            Log.e("set date switched1 ", "setupMyDayPlanEntriesNeeded: " + SharedPref.getDayPlanStartedDate(context));
//            SharedPref.setSelectedDateCal(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, SharedPref.getDayPlanStartedDate(context)));
//            syncTaskStatus.datesFound();
            date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, SharedPref.getDayPlanStartedDate(context));
        }
        else if(!SharedPref.getSelectedDateCal(context).isEmpty() && datesNeeded.contains(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, SharedPref.getSelectedDateCal(context)))) {
            Log.e("set date switched2 ", "setupMyDayPlanEntriesNeeded: " + SharedPref.getSelectedDateCal(context));
//            syncTaskStatus.datesFound();
            date = SharedPref.getSelectedDateCal(context);
        }
        else if(SharedPref.getSelectedDateCal(context).isEmpty() && !datesNeeded.isEmpty() && isCallDataAvailable){
//            SharedPref.setSelectedDateCal(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first()));
            Log.e("set date first", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
//            syncTaskStatus.datesFound();
            date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first());
        }
        else if(SharedPref.getSelectedDateCal(context).isEmpty() && !datesNeeded.isEmpty() && !isCallDataAvailable){
            Log.e("not set date first", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
//            syncTaskStatus.noDatesFound();
        }
        else if(dates.containsKey(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4)) && Objects.requireNonNull(dates.get(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))).equalsIgnoreCase("01") && datesNeeded.isEmpty()) {
//            SharedPref.setSelectedDateCal(context, "");
            Log.e("set date today finished", "setupMyDayPlanEntriesNeeded: dates empty");
//            syncTaskStatus.noDatesFound();
        }
        else if(!datesNeeded.isEmpty()) {
//            if(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_34).equalsIgnoreCase(SharedPref.getSelectedDateCal(context))) {
//                new CommonUtilsMethods(context).showToastMessage(context, context.getString(R.string.not_chose_after_date));
//                Log.e("TAG", "setupMyDayPlanEntriesNeeded: today" );
//            }
//            SharedPref.setSelectedDateCal(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first()));
            Log.e("set date", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
//            syncTaskStatus.datesFound();
            date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first());
        }else {
//            SharedPref.setSelectedDateCal(context, "");
            Log.e("set date empty", "setupMyDayPlanEntriesNeeded: dates empty");
//            syncTaskStatus.noDatesFound();
        }
        if(SharedPref.getDcrSequential(context).equalsIgnoreCase("0")) {
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
}
