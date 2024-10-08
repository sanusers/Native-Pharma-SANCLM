package saneforce.sanzen.roomdatabase.STPOfflineTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface STPOfflineDataDao {

    @Insert
    void insert(STPOfflineDataTable stpOfflineDataTable);

    @Update
    void update(STPOfflineDataTable stpOfflineDataTable);

    @Delete
    void delete(STPOfflineDataTable stpOfflineDataTable);

    @Query("DELETE FROM `STP_OFFLINE_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSTPData(STPOfflineDataTable stpOfflineDataTable);

    @Query("SELECT * FROM `STP_OFFLINE_TABLE` WHERE `DAY_ID` = :dayID")
    STPOfflineDataTable getSTPDataOfDay(String dayID);

    @Query("UPDATE `STP_OFFLINE_TABLE` SET `SYNC_STATUS` = :status WHERE `DAY_ID` = :dayID")
    void saveDaySyncStatus(String dayID, String status);

    @Query("SELECT * FROM `STP_OFFLINE_TABLE`")
    List<STPOfflineDataTable> getAllSTPData();

    default STPOfflineDataTable getSTPDataOfDayOrNew(String dayID) {
        STPOfflineDataTable stpOfflineDataTable = getSTPDataOfDay(dayID);
        if(stpOfflineDataTable == null) stpOfflineDataTable = new STPOfflineDataTable();
        return stpOfflineDataTable;
    }
}
