package saneforce.sanzen.activity.Quiz.AssertDownloadAlert;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;

import saneforce.sanzen.activity.Quiz.QuizActivity;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDao;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class AssertDownloadService extends Service {

    private QuizAssertsDao quizAssertsDao;

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
        quizAssertsDao = roomDB.quizAssertsDao();

        if (quizAssertsDao.getInProcessCount() == 0) {
            ArrayList<QuizAssertsDataTable> List = quizAssertsDao.cursorToArrayList();
            for (QuizAssertsDataTable mList : List) {
                if (mList.getBackgroundTask().equalsIgnoreCase("1")) {
                    if (mList.getDownloadingStatus().equalsIgnoreCase("1")) {
                        if (!QuizActivity.assertsNames.contains(mList.getName())) {
                            String url = "https://" + SharedPref.getBaseUrl(getApplicationContext()) + "/" + SharedPref.getOptionFilesUrl(getApplicationContext()) + mList.getName();
                            Log.i("QuizAssert", "onCreate: " + url);
                            Data inputData = new Data.Builder()
                                    .putString("Flag", "1")
                                    .putString("file_url", url)
                                    .putString("Assert_name", mList.getName())
                                    .putString("FilePosition", mList.getListAssertPosition())
                                    .build();

                            OneTimeWorkRequest fileDownloadRequest = new OneTimeWorkRequest.Builder(AssertDownloadWorker.class)
                                    .setInputData(inputData)
                                    .build();
                            WorkManager workManager = WorkManager.getInstance(this);
                            workManager.enqueue(fileDownloadRequest);
                            QuizActivity.assertsNames.add(mList.getName());
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
