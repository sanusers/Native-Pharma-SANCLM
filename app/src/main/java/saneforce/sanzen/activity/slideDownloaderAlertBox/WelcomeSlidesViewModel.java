package saneforce.sanzen.activity.slideDownloaderAlertBox;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDao;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDataTable;

public class WelcomeSlidesViewModel extends AndroidViewModel {

    private final LiveData<List<WelcomeSlidesDataTable>> allSlides;
    private final LiveData<Integer> downloadingCount;
    private final LiveData<Integer> statusNewCount;
    private final LiveData<Integer> processDoneCount;

    public WelcomeSlidesViewModel(@NonNull Application application) {
        super(application);
        RoomDB db = RoomDB.getDatabase(application.getApplicationContext());
        WelcomeSlidesDao welcomeSlidesDao = db.welcomeSlidesDao();
        allSlides = welcomeSlidesDao.getAllSlidesList();
        downloadingCount = welcomeSlidesDao.getCountOfSlidesWithDownloadingStatus();
        statusNewCount = welcomeSlidesDao.getCountNewStatus();
        processDoneCount = welcomeSlidesDao.getCountOfDownloadingProcessDone();
    }

    public LiveData<List<WelcomeSlidesDataTable>> getAllSlides() {
        return allSlides;
    }

    public LiveData<Integer> getDownloadingCount() {
        return downloadingCount;
    }

    public LiveData<Integer> getCountOfDownloadingProcessDone() {
        return processDoneCount;
    }

    public LiveData<Integer> slideNewCount() {
        return statusNewCount;
    }

}
