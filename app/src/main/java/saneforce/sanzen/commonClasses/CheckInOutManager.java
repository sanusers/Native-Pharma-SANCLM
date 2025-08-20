package saneforce.sanzen.commonClasses;

import android.content.Context;

import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class CheckInOutManager {
    public static boolean isCheckedId(Context context) {
        return SharedPref.getSrtNd(context).equalsIgnoreCase("0")
                && HomeDashBoard.selectedDate != null
                && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))
                && !SharedPref.getCheckTodayCheckInOut(context).equalsIgnoreCase(HomeDashBoard.selectedDate.toString());
    }

    public static boolean isCheckInAvailable(Context context) {
        return SharedPref.getSrtNd(context).equalsIgnoreCase("0")
                && HomeDashBoard.selectedDate != null
                && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))
                && SharedPref.getCheckTodayCheckInOut(context).equalsIgnoreCase(HomeDashBoard.selectedDate.toString());
    }
}
