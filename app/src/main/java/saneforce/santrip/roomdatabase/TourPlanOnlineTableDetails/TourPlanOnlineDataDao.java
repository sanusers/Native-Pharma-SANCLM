package saneforce.santrip.roomdatabase.TourPlanOnlineTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TourPlanOnlineDataDao {
    @Insert
    void insert(TourPlanOnlineDataTable tourPlanOnlineDataTable);

    @Update
    void update(TourPlanOnlineDataTable tourPlanOnlineDataTable);

    @Delete
    void delete(TourPlanOnlineDataTable tourPlanOnlineDataTable);

    @Query("DELETE FROM `TOUR_PLAN_ONLINE_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveTpData(TourPlanOnlineDataTable tourPlanOnlineDataTable);

    @Query("SELECT * FROM `TOUR_PLAN_ONLINE_TABLE` WHERE `TP_MONTH` = :month")
    TourPlanOnlineDataTable getTpDataOfMonth(String month);

    @Query("UPDATE `TOUR_PLAN_ONLINE_TABLE` SET `TP_REJECTION_REASON` = :rejectionReason WHERE `TP_MONTH` = :month")
    void saveMonthlySyncStatusMaster(String month, String rejectionReason);

    default TourPlanOnlineDataTable getTpDataOfMonthOrNew(String month) {
        TourPlanOnlineDataTable tourPlanOnlineDataTable = getTpDataOfMonth(month);
        if(tourPlanOnlineDataTable == null) tourPlanOnlineDataTable = new TourPlanOnlineDataTable();
        return tourPlanOnlineDataTable;
    }
}
