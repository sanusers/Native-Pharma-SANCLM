package saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.ActivityModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataTable;

@Dao
public interface ActivityOfflineDataDao {

    @Insert
    Long insert(ActivityOfflineDataTable activityOfflineDataTable);

    @Update
    void update(ActivityOfflineDataTable activityOfflineDataTable);

    @Delete
    void delete(ActivityOfflineDataTable activityOfflineDataTable);

    @Query("DELETE FROM `ACTIVITY_OFFLINE_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long saveActivityOfflineData(ActivityOfflineDataTable activityOfflineDataTable);

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_OFFLINE_TABLE")
    boolean isActivityAvailable();

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_OFFLINE_TABLE WHERE `ACTIVITY_DATE` = :date")
    boolean isAvailableActivityOnDate(String date);

    @Query("SELECT EXISTS(SELECT 1 FROM `ACTIVITY_OFFLINE_TABLE` WHERE `SYNC_STATUS` = :status)")
    boolean isActivityAvailableByStatus(String status);

    @Query("SELECT * FROM `ACTIVITY_OFFLINE_TABLE` WHERE `ACTIVITY_DATE` = :id")
    ActivityOfflineDataTable getActivityOfflineData(int id);

    @Query("SELECT `JSON_DATA` FROM `ACTIVITY_OFFLINE_TABLE` WHERE `ACTIVITY_DATE` = :date AND `NAME` = :name AND `ACTIVITY_DATE` = :id")
    String getActivityJson(int id, String name, String date);

    @Query("DELETE FROM `ACTIVITY_OFFLINE_TABLE` WHERE `ID` = :id")
    void deleteOfflineActivity(int id);

    @Query("SELECT * FROM `ACTIVITY_OFFLINE_TABLE` WHERE `ACTIVITY_DATE` = :date")
    List<ActivityOfflineDataTable> getOutBoxActivityList(String date);

    @Query("SELECT * FROM `ACTIVITY_OFFLINE_TABLE`")
    List<ActivityOfflineDataTable> getAllOutBoxActivityList();

    @Query("SELECT `ACTIVITY_DATE` FROM `ACTIVITY_OFFLINE_TABLE`")
    List<String> getAllActivityOfflineDates();
    
    default ArrayList<ActivityModelClass> getActivityList(String date) {
        ArrayList<ActivityModelClass> activityModelClassList = new ArrayList<>();
        List<ActivityOfflineDataTable> list = getOutBoxActivityList(date);
        for(ActivityOfflineDataTable activityOfflineDataTable : list) {
            activityModelClassList.add(new ActivityModelClass(activityOfflineDataTable.getId(), activityOfflineDataTable.getActivityDate(), activityOfflineDataTable.getActivityTime(), activityOfflineDataTable.getSlNo(), activityOfflineDataTable.getName(), activityOfflineDataTable.getJsonData(), activityOfflineDataTable.getSyncCount(), activityOfflineDataTable.getSyncStatus()));
        }
        return activityModelClassList;
    }
}
