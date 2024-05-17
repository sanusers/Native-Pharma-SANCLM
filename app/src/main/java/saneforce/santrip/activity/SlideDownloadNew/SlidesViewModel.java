package saneforce.santrip.activity.SlideDownloadNew;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.util.List;

import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.roomdatabase.SlideTable.SlidesDao;
import saneforce.santrip.roomdatabase.SlideTable.SlidesTableDeatils;

public class SlidesViewModel extends AndroidViewModel {

    private SlidesDao slidesDao;
    private LiveData<List<SlidesTableDeatils>> allSlides;

    private LiveData<Integer>  DownlaodindCount;
    private LiveData<Integer>  SlidTotalCount;

    public SlidesViewModel(@NonNull Application application) {
        super(application);
        RoomDB db = RoomDB.getDatabase(application.getApplicationContext());
        slidesDao = db.slidesDao();
        allSlides = slidesDao.getAllSlides1();
        DownlaodindCount = slidesDao.getCountOfSlidesWithDownloadingStatus();
        SlidTotalCount=slidesDao.TotalSlidecount();
    }

    public LiveData<List<SlidesTableDeatils>> getAllSlides() {
        return allSlides;
    }
    public LiveData<Integer> GetDowloaingCount() {
        return DownlaodindCount;
    }
    public LiveData<Integer> SlideTotalCount() {
        return SlidTotalCount;
    }
}
