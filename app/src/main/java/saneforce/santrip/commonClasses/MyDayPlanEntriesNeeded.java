package saneforce.santrip.commonClasses;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;

public class MyDayPlanEntriesNeeded {

    public static TreeSet<String> datesNeeded = new TreeSet<>();
    private static MasterDataDao masterDataDao;
    private static OfflineDaySubmitDao offlineDaySubmitDao;
    private static ApiInterface apiInterface;
    private static int callSyncSuccess = 0, dateSyncSuccess = 0;
    private static SyncTaskStatus syncTaskStatus;

    public static void updateMyDayPlanEntriesNeeded(Context context, boolean shouldSync, SyncTaskStatus syncTaskStatus) {
        masterDataDao = RoomDB.getDatabase(context).masterDataDao();
        offlineDaySubmitDao = RoomDB.getDatabase(context).offlineDaySubmitDao();
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        MyDayPlanEntriesNeeded.syncTaskStatus = syncTaskStatus;
        if(shouldSync) {
            syncCallAndDate(context);
        }else {
            setupMyDayPlanEntriesNeeded(context);
        }
    }

    private static void syncCallAndDate(Context context) {
        callSyncSuccess = 0;
        dateSyncSuccess = 0;
        JSONObject jj = new JSONObject();
        try {
            jj.put("tableName", "gethome"); // -> call sync
            jj.put("sfcode", SharedPref.getSfCode(context));
            jj.put("division_code", SharedPref.getDivisionCode(context));
            jj.put("Rsf", SharedPref.getHqCode(context));
            jj.put("sf_type", SharedPref.getSfType(context));
            jj.put("Designation", SharedPref.getDesig(context));
            jj.put("state_code", SharedPref.getStateCode(context));
            jj.put("subdivision_code", SharedPref.getSubdivisionCode(context));
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
                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, jsonArray.toString(), 0));
                                callSyncSuccess = 1;
                            }
                            if(finalI == 1) {
                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, jsonArray.toString(), 0));
                                dateSyncSuccess = 1;
                            }
                            if(callSyncSuccess == 1 && dateSyncSuccess == 1) {
                                setupMyDayPlanEntriesNeeded(context);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    Log.e("MyDayPlanEntry", "onFailure: " + context.getString(R.string.toast_response_failed));
                    setupMyDayPlanEntriesNeeded(context);
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
        try {
            JSONArray dcrdatas = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
            if(dcrdatas.length()>0) {
                for (int i = 0; i<dcrdatas.length(); i++) {
                    JSONObject jsonObject = dcrdatas.getJSONObject(i);
                    String CustType = jsonObject.optString("CustType");
                    String worktypeFlog = jsonObject.optString("FW_Indicator");
                    String date = jsonObject.optString("Dcr_dt");
                    dates.put(date, CustType + worktypeFlog);
                }
                Log.v("TAG 1", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(dates.keySet().toArray()));
                for (String date : dates.keySet()) {
                    if(dates.get(date).equalsIgnoreCase("0F")) {
                        datesNeeded.add(date);
                    }
                }
                Log.v("TAG 1.1", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
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
                        datesNeeded.add(date);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("TAG", "setupMyDayPlanEntriesNeeded: " + Arrays.toString(datesNeeded.toArray()));
        if(!SharedPref.getSelectedDateCal(context).isEmpty() && datesNeeded.contains(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, SharedPref.getSelectedDateCal(context)))) {
            Log.e("set date switched", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
            syncTaskStatus.datesFound();
        }else if(dates.containsKey(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4)) && datesNeeded.isEmpty()) {
            SharedPref.setSelectedDateCal(context, "");
            Log.e("set date today finished", "setupMyDayPlanEntriesNeeded: dates empty");
            syncTaskStatus.noDatesFound();
        }else if(!datesNeeded.isEmpty()) {
            if(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_34).equalsIgnoreCase(SharedPref.getSelectedDateCal(context))) {
                new CommonUtilsMethods(context).showToastMessage(context, context.getString(R.string.not_chose_after_date));
                Log.e("TAG", "setupMyDayPlanEntriesNeeded: today" );
            }
            SharedPref.setSelectedDateCal(context, TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_34, datesNeeded.first()));
            Log.e("set date", "setupMyDayPlanEntriesNeeded: " + datesNeeded.first());
            syncTaskStatus.datesFound();
        }else {
            SharedPref.setSelectedDateCal(context, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_34));
            Log.e("set date today", "setupMyDayPlanEntriesNeeded: dates empty");
            syncTaskStatus.datesFound();
        }
    }

    public interface SyncTaskStatus {
        void datesFound();
        void noDatesFound();
    }
}
