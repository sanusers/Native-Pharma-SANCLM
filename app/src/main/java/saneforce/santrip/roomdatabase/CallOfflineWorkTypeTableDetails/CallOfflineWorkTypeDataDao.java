package saneforce.santrip.roomdatabase.CallOfflineWorkTypeTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface CallOfflineWorkTypeDataDao {
    @Insert
    void insert(CallOfflineWorkTypeDataTable callOfflineWorkTypeDataTable);

    @Update
    void update(CallOfflineWorkTypeDataTable callOfflineWorkTypeDataTable);

    @Delete
    void delete(CallOfflineWorkTypeDataTable callOfflineWorkTypeDataTable);

    @Query("DELETE FROM `CALL_OFFLINE_WORK_TYPE_TABLE`")
    void deleteAllData();

    @Query("SELECT EXISTS(SELECT 1 FROM `CALL_OFFLINE_WORK_TYPE_TABLE`)")
    boolean isAvailableWT();

    @Query("SELECT `CALL_OFFLINE_WT_NAME` FROM `CALL_OFFLINE_WORK_TYPE_TABLE` WHERE `CALL_OFFLINE_WT_DATE` = :date")
    List<String> getListOfflineWTNames(String date);

    @Query("SELECT `CALL_OFFLINE_WT_DATE` FROM `CALL_OFFLINE_WORK_TYPE_TABLE`")
    List<String> getAllCallOfflineWTDates();

    default String getListOfflineWT(String date) {
        List<String> list = getListOfflineWTNames(date);
        StringBuilder fieldWork = new StringBuilder();
        for(String wtName : list) {
            if (fieldWork.toString().isEmpty()) {
                fieldWork.append(wtName);
            } else {
                fieldWork.append(",").append(wtName);
            }
        }
        return fieldWork.toString();
    }

}
