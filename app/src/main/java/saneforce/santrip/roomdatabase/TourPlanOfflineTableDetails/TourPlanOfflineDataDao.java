package saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TourPlanOfflineDataDao {
    @Insert
    void insert(TourPlanOfflineDataTable tourPlanOfflineDataTable);

    @Update
    void update(TourPlanOfflineDataTable tourPlanOfflineDataTable);

    @Delete
    void delete(TourPlanOfflineDataTable tourPlanOfflineDataTable);

    @Query("DELETE FROM `TOUR_PLAN_OFFLINE_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveTpData(TourPlanOfflineDataTable tourPlanOfflineDataTable);

    @Query("SELECT * FROM `TOUR_PLAN_OFFLINE_TABLE` WHERE `TP_MONTH` = :month")
    TourPlanOfflineDataTable getTpDataOfMonth(String month);

    @Query("UPDATE `TOUR_PLAN_OFFLINE_TABLE` SET `TP_MONTH_SYNCED` = :status WHERE `TP_MONTH` = :month")
    void saveMonthlySyncStatus(String month, String status);

    @Query("UPDATE `TOUR_PLAN_OFFLINE_TABLE` SET `TP_MONTH_SYNCED` = :status, `TP_REJECTION_REASON` = :rejectionReason WHERE `TP_MONTH` = :month")
    void saveMonthlySyncStatusMaster(String month, String status, String rejectionReason);

    default TourPlanOfflineDataTable getTpDataOfMonthOrNew(String month) {
        TourPlanOfflineDataTable tourPlanOfflineDataTable = getTpDataOfMonth(month);
        if(tourPlanOfflineDataTable == null) tourPlanOfflineDataTable = new TourPlanOfflineDataTable();
        return tourPlanOfflineDataTable;
    }
}
