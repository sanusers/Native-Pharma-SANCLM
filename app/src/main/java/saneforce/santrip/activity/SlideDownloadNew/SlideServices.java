package saneforce.santrip.activity.SlideDownloadNew;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;

import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.roomdatabase.SlideTable.SlidesDao;
import saneforce.santrip.roomdatabase.SlideTable.SlidesTableDeatils;
import saneforce.santrip.storage.SharedPref;

public class SlideServices extends Service {


    private static boolean isServiceRunning = false;

    RoomDB roomDB;
    SlidesDao SlidesDao;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;

        Log.v("Creating","Created Srvices");
        roomDB = RoomDB.getDatabase(getApplicationContext());
        SlidesDao = roomDB.slidesDao();

        ArrayList<SlidesTableDeatils> List= SlidesDao.cursorToArrayList();

        for (SlidesTableDeatils mList:List){
            if(mList.getBackgroundtask().equalsIgnoreCase("1")){
           if (mList.getDownloadingStaus().equalsIgnoreCase("1")||mList.getDownloadingStaus().equalsIgnoreCase("0")){
               if(!MasterSyncActivity.SlideIds.contains(mList.getSlideId())) {
                   String url = "https://" + SharedPref.getLogInsite(getApplicationContext()) + "/" + SharedPref.getSlideUrl(getApplicationContext()) + mList.getSlideName();
                   Data inputData = new Data.Builder()
                           .putString("Flag", "1")
                           .putString("file_url", url)
                           .putString("Slide_id", mList.getSlideId())
                           .putString("Slide_name", mList.getSlideName())
                           .build();

                   OneTimeWorkRequest fileDownloadRequest = new OneTimeWorkRequest.Builder(FileDownloadWorker.class)
                           .setInputData(inputData)
                           .build();
                   WorkManager workManager = WorkManager.getInstance(this);
                   workManager.enqueue(fileDownloadRequest);
                   MasterSyncActivity.SlideIds.add(mList.getSlideId());
                   break;
               }


           }
        }else {
               stopSelf();
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
        isServiceRunning = false;
    }

    public static boolean isRunning() {
        return isServiceRunning;
    }
}
