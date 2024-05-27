package saneforce.sanzen.roomdatabase.OfflineDaySubmit;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.DaySubmitModelClass;

@Dao
public interface OfflineDaySubmitDao {
    @Insert
    void insert(OfflineDaySubmitDataTable offlineDaySubmitDataTable);

    @Update
    void update(OfflineDaySubmitDataTable offlineDaySubmitDataTable);

    @Delete
    void delete(OfflineDaySubmitDataTable offlineDaySubmitDataTable);

    @Query("DELETE FROM `OFFLINE_DAY_SUBMIT_TABLE`")
    void deleteAllData();

    @Query("DELETE FROM `OFFLINE_DAY_SUBMIT_TABLE` WHERE `DATE` = :date")
    void delete(String date);

    @Query("SELECT EXISTS(SELECT 1 FROM `OFFLINE_DAY_SUBMIT_TABLE`)")
    boolean isAvailableDaySubmit();

    @Query("SELECT * FROM `OFFLINE_DAY_SUBMIT_TABLE` WHERE `DATE` = :date")
    OfflineDaySubmitDataTable getDaySubmit(String date);

    @Query("UPDATE `OFFLINE_DAY_SUBMIT_TABLE` SET `SYNC_STATUS` = :syncStatus WHERE `DATE` = :date")
    void updateDaySubmitStatus(String date, int syncStatus);

    @Query("SELECT `DATE` FROM `OFFLINE_DAY_SUBMIT_TABLE`")
    List<String> getAllOfflineDaySubmitDates();

    @Query("SELECT * FROM `OFFLINE_DAY_SUBMIT_TABLE`")
    List<OfflineDaySubmitDataTable> getAllOfflineDaySubmit();

    default DaySubmitModelClass getDaySubmitModelClass(String date) {
        DaySubmitModelClass daySubmitModelClass = null;
        OfflineDaySubmitDataTable offlineDaySubmitDataTable = getDaySubmit(date);
        if(offlineDaySubmitDataTable != null) {
            daySubmitModelClass = new DaySubmitModelClass(offlineDaySubmitDataTable.getDate(), offlineDaySubmitDataTable.getJsonDaySubmit(), offlineDaySubmitDataTable.getSyncStatus());
        }
        return daySubmitModelClass;
    }
}
