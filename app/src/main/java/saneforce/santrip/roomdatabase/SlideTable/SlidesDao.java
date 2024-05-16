package saneforce.santrip.roomdatabase.SlideTable;

import android.annotation.SuppressLint;
import android.database.Cursor;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SlidesDao {
    @Insert
    void insert(SlidesTableDeatils SlidesTableDeatils);

    @Update
    void update(SlidesTableDeatils SlidesTableDeatils);

    @Delete
    void delete(SlidesTableDeatils SlidesTableDeatils);


    @Query("SELECT * FROM SlidesTableDeatils")
    Cursor getAllSlides();


  default ArrayList<SlidesTableDeatils> cursorToArrayList() {
          Cursor cursor=getAllSlides();
        ArrayList<SlidesTableDeatils> slides = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String slideId = cursor.getString(cursor.getColumnIndex("SlideId"));
                @SuppressLint("Range") String slideName = cursor.getString(cursor.getColumnIndex("SlideName"));
                @SuppressLint("Range") String slideSize = cursor.getString(cursor.getColumnIndex("SlideSize"));
                @SuppressLint("Range") String downloadingStatus = cursor.getString(cursor.getColumnIndex("DownloadingStaus")); // Corrected column name

                SlidesTableDeatils slide = new SlidesTableDeatils(slideId, slideName, slideSize, downloadingStatus);
                slides.add(slide);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return slides;
    }


}
