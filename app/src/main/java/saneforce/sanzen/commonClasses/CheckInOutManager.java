package saneforce.sanzen.commonClasses;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class CheckInOutManager {
    public static boolean isCheckedIn(Context context) {
        if(SharedPref.getSrtNd(context).equalsIgnoreCase("0")) {
            try {
                RoomDB roomDB = RoomDB.getDatabase(context);
                MasterDataDao masterDataDao = roomDB.masterDataDao();
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHECK_IN).getMasterSyncDataJsonArray();
                if(jsonArray.length()>0) {
                    JSONObject jsonObject = jsonArray.optJSONObject(0);
                    String status = jsonObject.optString("status"), date = "";
                    JSONObject activityDateObj = jsonObject.optJSONObject("Activity_Date");
                    if(activityDateObj != null) {
                        date = activityDateObj.optString("date");
                        date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_4, date);
                    }
                    if((status.equalsIgnoreCase("1") || status.equalsIgnoreCase("0")) && HomeDashBoard.selectedDate != null && date.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                        return true;
                    }
                }else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
//        return SharedPref.getSrtNd(context).equalsIgnoreCase("0")
//                && HomeDashBoard.selectedDate != null
//                && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))
//                && !SharedPref.getCheckTodayCheckInOut(context).equalsIgnoreCase(HomeDashBoard.selectedDate.toString());
    }

    public static boolean isCheckInAvailable(Context context) {
        if(SharedPref.getSrtNd(context).equalsIgnoreCase("0")) {
            try {
                RoomDB roomDB = RoomDB.getDatabase(context);
                MasterDataDao masterDataDao = roomDB.masterDataDao();
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHECK_IN).getMasterSyncDataJsonArray();
                if(jsonArray.length()>0) {
                    JSONObject jsonObject = jsonArray.optJSONObject(0);
                    String status = jsonObject.optString("status"), date = "";
                    JSONObject activityDateObj = jsonObject.optJSONObject("Activity_Date");
                    if(activityDateObj != null) {
                        date = activityDateObj.optString("date");
                        date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_4, date);
                    }
                    if(status.equalsIgnoreCase("1") && HomeDashBoard.selectedDate != null && date.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                        return true;
                    }
                }else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
//        return SharedPref.getSrtNd(context).equalsIgnoreCase("0")
//                && HomeDashBoard.selectedDate != null
//                && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))
//                && SharedPref.getCheckTodayCheckInOut(context).equalsIgnoreCase(HomeDashBoard.selectedDate.toString());
    }
}
