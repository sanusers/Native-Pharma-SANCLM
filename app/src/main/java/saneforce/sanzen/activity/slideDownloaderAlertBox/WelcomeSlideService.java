package saneforce.sanzen.activity.slideDownloaderAlertBox;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;

import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDataTable;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDao;
import saneforce.sanzen.storage.SharedPref;

public class WelcomeSlideService extends Service {

    private WelcomeSlidesDao welcomeSlidesDao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("Creating", "Created Services");
        RoomDB roomDB = RoomDB.getDatabase(getApplicationContext());
        welcomeSlidesDao = roomDB.welcomeSlidesDao();

        if (welcomeSlidesDao.getInProcessCount() == 0) {
            ArrayList<WelcomeSlidesDataTable> List = welcomeSlidesDao.cursorToArrayList();
            for (WelcomeSlidesDataTable mList : List) {
                if (mList.getBackgroundTask().equalsIgnoreCase("1")) {
                    if (mList.getDownloadingStatus().equalsIgnoreCase("1")) {
                        if (!MasterSyncActivity.welcomeSlideNames.contains(mList.getName())) {
                            String url = "https://" + SharedPref.getLogInsite(getApplicationContext()) + "/" + SharedPref.getWelcomeSlideUrl(getApplicationContext()) + mList.getName();
                            Data inputData = new Data.Builder()
                                    .putString("Flag", "1")
                                    .putString("file_url", url)
                                    .putString("Slide_name", mList.getName())
                                    .putString("FilePosition", mList.getListSlidePosition())
                                    .build();

                            OneTimeWorkRequest fileDownloadRequest = new OneTimeWorkRequest.Builder(WelcomeSlideDownloadWorker.class)
                                    .setInputData(inputData)
                                    .build();
                            WorkManager workManager = WorkManager.getInstance(this);
                            workManager.enqueue(fileDownloadRequest);
                            MasterSyncActivity.welcomeSlideNames.add(mList.getName());
                            break;
                        }
                    }
                } else {
                    stopSelf();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
