package saneforce.sanzen.activity.slideDownloaderAlertBox;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesDao;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesTableDeatils;

public class SlidesViewModel extends AndroidViewModel {

    private SlidesDao slidesDao;
    private LiveData<List<SlidesTableDeatils>> allSlides;

    private LiveData<Integer>  DownlaodindCount;

    private LiveData<Integer>  StatusNewCount;
    private LiveData<Integer>  ProcessDoneCount;


    public SlidesViewModel(@NonNull Application application) {
        super(application);
        RoomDB db = RoomDB.getDatabase(application.getApplicationContext());
        slidesDao = db.slidesDao();
        allSlides = slidesDao.getAllSlides1();
        DownlaodindCount = slidesDao.getCountOfSlidesWithDownloadingStatus();
        StatusNewCount=slidesDao.getCountNewStatus();
        ProcessDoneCount=slidesDao.getCountOfDownloadingProcessDone();
    }

    public LiveData<List<SlidesTableDeatils>> getAllSlides() {
        return allSlides;
    }
    public LiveData<Integer> GetDowloaingCount() {
        return DownlaodindCount;
    }
    public LiveData<Integer> getCountOfDownloadingProcessDone() {
        return ProcessDoneCount;
    }


    public LiveData<Integer> SlideNewCount() {
        return StatusNewCount;
    }

}
