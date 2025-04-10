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
public interface WelcomeSlidesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WelcomeSlidesDataTable welcomeSlidesDataTable);

    @Update
    void update(WelcomeSlidesDataTable welcomeSlidesDataTable);

    @Delete
    void delete(WelcomeSlidesDataTable welcomeSlidesDataTable);

    @Query("DELETE FROM `WELCOME_SLIDES_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWelcomeSlideData(WelcomeSlidesDataTable welcomeSlidesDataTable);

    @Query("SELECT * FROM `WELCOME_SLIDES_TABLE` WHERE `NAME` = :name")
    WelcomeSlidesDataTable getWelcomeSlidesDataByName(String name);

    @Query("SELECT * FROM `WELCOME_SLIDES_TABLE`")
    LiveData<List<WelcomeSlidesDataTable>> getAllSlidesList();

    @Query("SELECT COUNT(*) FROM `WELCOME_SLIDES_TABLE` WHERE `downloading_status` = '3'")
    LiveData<Integer> getCountOfSlidesWithDownloadingStatus();

    @Query("SELECT COUNT(*) FROM `WELCOME_SLIDES_TABLE` WHERE `downloading_status` = '3' OR `downloading_status` = '0'")
    LiveData<Integer> getCountOfDownloadingProcessDone();

    @Query("SELECT COUNT(*) FROM `WELCOME_SLIDES_TABLE` WHERE `downloading_status` = '1'")
    LiveData<Integer> getCountNewStatus();

    @Query("SELECT COUNT(*) FROM `WELCOME_SLIDES_TABLE` WHERE `downloading_status` = '2'")
    int getInProcessCount();

    @Query("SELECT COUNT(*) FROM `WELCOME_SLIDES_TABLE`")
    int getTotalSlideCount();

    @Query("SELECT `NAME` FROM `WELCOME_SLIDES_TABLE`")
    List<String> getAllSlideNames();

    @Query("DELETE FROM `WELCOME_SLIDES_TABLE` WHERE `NAME` = :name")
    void deleteSlideByName(String name);

    @Query("Update `WELCOME_SLIDES_TABLE` set `BACKGROUND_TASK`=:New WHERE `BACKGROUND_TASK` = :old")
    void setChangeStatus(String New, String old);

    @Query("SELECT `NAME` FROM `WELCOME_SLIDES_TABLE` WHERE `NAME` = :name")
    String getSlideName(String name);

    @Query("SELECT * FROM `WELCOME_SLIDES_TABLE`")
    Cursor getAllSlides();

    default ArrayList<WelcomeSlidesDataTable> cursorToArrayList() {
        Cursor cursor = getAllSlides();
        ArrayList<WelcomeSlidesDataTable> slides = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String slideName = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String slideSize = cursor.getString(cursor.getColumnIndex("slide_size"));
                @SuppressLint("Range") String downloadingStatus = cursor.getString(cursor.getColumnIndex("downloading_status"));
                @SuppressLint("Range") String progress = cursor.getString(cursor.getColumnIndex("progress"));
                @SuppressLint("Range") String backgroundTask = cursor.getString(cursor.getColumnIndex("background_task"));
                @SuppressLint("Range") String filePosition = cursor.getString(cursor.getColumnIndex("file_position"));
                WelcomeSlidesDataTable slide = new WelcomeSlidesDataTable(slideName, slideSize, downloadingStatus, progress, backgroundTask, filePosition);
                slides.add(slide);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return slides;
    }

}
