package saneforce.santrip.activity.homeScreen.timeZone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

import saneforce.santrip.commonClasses.CommonUtilsMethods;


public class AutoTimezone extends Service
{
    boolean isRunning=false;
    static boolean result=false;
    Context context;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        checkTimeZone();
        return START_STICKY;
    }

    public AutoTimezone() {
    }

    public  AutoTimezone(Context context) {
        this.context = context;
        commonUtilsMethods = new CommonUtilsMethods(context);
        checkTimeZone();
    }

    public void checkTimeZone() {
        try {
            if (commonUtilsMethods.isAutoTimeZoneEnabled(context)) {
                result = false;
            }
            else {
                commonUtilsMethods.showCustomDialog(context);
                result = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}