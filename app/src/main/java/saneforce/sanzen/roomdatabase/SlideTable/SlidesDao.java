package saneforce.sanzen.roomdatabase.SlideTable;

import android.annotation.SuppressLint;
import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.ArrayList;
import java.util.List;



@Dao
public interface SlidesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SlidesTableDeatils SlidesTableDeatils);

    @Update
    void update(SlidesTableDeatils SlidesTableDeatils);

    @Delete
    void delete(SlidesTableDeatils SlidesTableDeatils);
    @Query("DELETE FROM `SlidesTableDeatils`")
    void deleteAllData();

    @Query("SELECT * FROM SlidesTableDeatils")
    Cursor getAllSlides();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSlideData(SlidesTableDeatils SlidesTableDeatils);

    @Query("SELECT * FROM SlidesTableDeatils")
    LiveData<List<SlidesTableDeatils>> getAllSlides1();


    @Query("SELECT COUNT(*) FROM SlidesTableDeatils WHERE DownloadingStaus = '3'")
    LiveData<Integer> getCountOfSlidesWithDownloadingStatus();

    @Query("SELECT COUNT(*) FROM SlidesTableDeatils WHERE DownloadingStaus = '1'")
    LiveData<Integer> getCountNewStatus();
    @Query("SELECT COUNT(*) FROM SlidesTableDeatils WHERE DownloadingStaus = '2'")
    int getInProcessCount();

    @Query("SELECT COUNT(*) FROM SlidesTableDeatils")
    LiveData<Integer> TotalSlidecount();


      @Query("Update SlidesTableDeatils set `Background task`=:New WHERE `Background task` = :old")
     void Changestatus(String New,String old);



    default ArrayList<SlidesTableDeatils> cursorToArrayList() {
          Cursor cursor=getAllSlides();
        ArrayList<SlidesTableDeatils> slides = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String slideId = cursor.getString(cursor.getColumnIndex("SlideId"));
                @SuppressLint("Range") String slideName = cursor.getString(cursor.getColumnIndex("SlideName"));
                @SuppressLint("Range") String slideSize = cursor.getString(cursor.getColumnIndex("SlideSize"));
                @SuppressLint("Range") String downloadingStatus = cursor.getString(cursor.getColumnIndex("DownloadingStaus")); // Corrected column name
                @SuppressLint("Range") String Progress = cursor.getString(cursor.getColumnIndex("Progress"));
                @SuppressLint("Range") String Backgroundtask = cursor.getString(cursor.getColumnIndex("Background task"));
                @SuppressLint("Range") String FilePosition = cursor.getString(cursor.getColumnIndex("FilePosition"));
                SlidesTableDeatils slide = new SlidesTableDeatils(slideId, slideName, slideSize, downloadingStatus,Progress,Backgroundtask,FilePosition);
                slides.add(slide);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return slides;
    }


}
