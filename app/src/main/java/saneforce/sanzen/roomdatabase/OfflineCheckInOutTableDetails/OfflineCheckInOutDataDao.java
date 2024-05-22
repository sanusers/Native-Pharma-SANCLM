package saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.CheckInOutModelClass;

@Dao
public interface OfflineCheckInOutDataDao {
    @Insert
    void insert(OfflineCheckInOutDataTable offlineCheckInOutDataTable);

    @Update
    void update(OfflineCheckInOutDataTable offlineCheckInOutDataTable);

    @Delete
    void delete(OfflineCheckInOutDataTable offlineCheckInOutDataTable);

    @Query("DELETE FROM `OFFLINE_CHECK_IN_OUT_TABLE`")
    void deleteAllData();

    @Query("SELECT EXISTS(SELECT 1 FROM `OFFLINE_CHECK_IN_OUT_TABLE`)")
    boolean isAvailableCheckInOut();

    @Query("SELECT * FROM `OFFLINE_CHECK_IN_OUT_TABLE` WHERE `OFFLINE_CHECK_IN_OUT_DATE` = :date")
    List<OfflineCheckInOutDataTable> getCheckInOut(String date);

    @Query("SELECT COUNT(*) FROM `OFFLINE_CHECK_IN_OUT_TABLE` WHERE `OFFLINE_CHECK_IN_OUT_DATE` = :date")
    int getCheckInOutCount(String date);

    @Query("UPDATE `OFFLINE_CHECK_IN_OUT_TABLE` SET `OFFLINE_SYNC_STATUS` = :checkSynced WHERE `ID` = :id")
    void updateCheckInOutStatus(int id, int checkSynced);

    @Query("DELETE FROM `OFFLINE_CHECK_IN_OUT_TABLE` WHERE `OFFLINE_CHECK_IN_OUT_DATE` = :date AND `OFFLINE_DATE_COUNT` = :count")
    void deleteOfflineCheckInOut(String date, int count);

    @Query("SELECT `OFFLINE_CHECK_IN_OUT_DATE` FROM `OFFLINE_CHECK_IN_OUT_TABLE`")
    List<String> getAllOfflineCheckInOutDates();

    default void saveCheckIn(String date, String checkInTime, String jsonValues) {
        insert(new OfflineCheckInOutDataTable(date, checkInTime, "", jsonValues, "", getCheckInOutCount(date), 0));
    }

    default void saveCheckOut(String date, String checkOutTime, String jsonValues) {
        insert(new OfflineCheckInOutDataTable(date, null, checkOutTime, null, jsonValues, getCheckInOutCount(date), 0));
    }

    default ArrayList<CheckInOutModelClass> getCheckInOutTime(String date) {
        ArrayList<CheckInOutModelClass> list = new ArrayList<>();
        for (OfflineCheckInOutDataTable offlineCheckInOutDataTable : getCheckInOut(date)) {
            list.add(new CheckInOutModelClass(offlineCheckInOutDataTable.getId(), offlineCheckInOutDataTable.getOfflineCheckInOutDate(), offlineCheckInOutDataTable.getOfflineCheckInTime(), offlineCheckInOutDataTable.getOfflineCheckOutTime(), offlineCheckInOutDataTable.getOfflineCheckInJson(), offlineCheckInOutDataTable.getOfflineCheckOutJson(), offlineCheckInOutDataTable.getOfflineDateCount(), offlineCheckInOutDataTable.getOfflineSyncStatus()));
        }
        return list;
    }
}
