package saneforce.sanzen.roomdatabase.ActivityTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import saneforce.sanzen.commonClasses.Constants;

@Dao
public interface ActivityDetailsDataDao {

    @Insert
    void insert(ActivityDetailsDataTable activityDetailsDataTable);

    @Update
    void update(ActivityDetailsDataTable activityDetailsDataTable);

    @Delete
    void delete(ActivityDetailsDataTable activityDetailsDataTable);

    @Query("DELETE FROM `ACTIVITY_DETAILS_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveActivityDetailsData(ActivityDetailsDataTable activityDetailsDataTable);

    @Query("DELETE FROM `ACTIVITY_DETAILS_TABLE` WHERE `ID` = :id")
    void deleteByID(String id);

    @Query("SELECT EXISTS(SELECT 1 FROM `ACTIVITY_DETAILS_TABLE`)")
    boolean isDataAvailable();

    @Query("SELECT EXISTS(SELECT 1 FROM `ACTIVITY_DETAILS_TABLE` WHERE `ID` = :id)")
    boolean isActivityDataAvailable(String id);

    @Query("SELECT * FROM `ACTIVITY_DETAILS_TABLE` WHERE `ID` = :id")
    ActivityDetailsDataTable getActivityDetailsByID(String id);

    @Query("UPDATE `ACTIVITY_DETAILS_TABLE` SET `STATUS` = :status WHERE `ID` = :id")
    void updateStatusByID(String id, String status);

    @Query("SELECT * FROM `ACTIVITY_DETAILS_TABLE`")
    List<ActivityDetailsDataTable> getAllActivityDetails();

    @Query("SELECT `ID` FROM `ACTIVITY_DETAILS_TABLE`")
    List<String> getAllActivityDetailsID();

    default ActivityDetailsDataTable getActivityDetailsOrNew(String ID) {
        ActivityDetailsDataTable activityDetailsDataTable = getActivityDetailsByID(ID);
        if(activityDetailsDataTable == null) activityDetailsDataTable = new ActivityDetailsDataTable();
        return activityDetailsDataTable;
    }
}
