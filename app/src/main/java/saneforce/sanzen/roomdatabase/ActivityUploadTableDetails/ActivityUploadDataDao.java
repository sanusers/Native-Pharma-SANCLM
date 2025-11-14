package saneforce.sanzen.roomdatabase.ActivityUploadTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.ActivityUploadModelClass;

@Dao
public interface ActivityUploadDataDao {

    @Insert
    void insert(ActivityUploadDataTable activityUploadDataTable);

    @Update
    void update(ActivityUploadDataTable activityUploadDataTable);

    @Delete
    void delete(ActivityUploadDataTable activityUploadDataTable);

    @Query("DELETE FROM `ACTIVITY_UPLOAD_TABLE`")
    void deleteAllData();

    @Query("DELETE FROM `ACTIVITY_UPLOAD_TABLE` WHERE `ACTIVITY_DATE` = :date")
    void deleteAllData(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveActivityUploadData(ActivityUploadDataTable activityUploadDataTable);

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_UPLOAD_TABLE")
    boolean isActivityUploadAvailable();

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_UPLOAD_TABLE WHERE `SYNC_STATUS` != 'Duplicate Call' AND `ACTIVITY_DATE` = :date")
    boolean isActivityUploadAvailable(String date);

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_UPLOAD_TABLE WHERE `SYNC_STATUS` != 'Duplicate Call'")
    boolean isNonSyncActivityUploadAvailable();

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_UPLOAD_TABLE WHERE `ACTIVITY_DATE` = :date")
    boolean isAvailableActivityOnDate(String date);

    @Query("SELECT EXISTS(SELECT 1 FROM `ACTIVITY_UPLOAD_TABLE` WHERE `SYNC_STATUS` = :status)")
    boolean isActivityAvailableByStatus(String status);

    @Query("SELECT * FROM `ACTIVITY_UPLOAD_TABLE` WHERE `ACTIVITY_ID` = :id")
    ActivityUploadDataTable getActivityUploadData(int id);

    @Query("SELECT `JSON_DATA` FROM `ACTIVITY_UPLOAD_TABLE` WHERE `ACTIVITY_DATE` = :date AND `NAME` = :name AND `ACTIVITY_DATE` = :id")
    String getActivityJson(int id, String name, String date);

    @Query("DELETE FROM `ACTIVITY_UPLOAD_TABLE` WHERE `id` = :id AND `ACTIVITY_ID` = :activityID")
    void deleteUploadActivity(int id, int activityID);

    @Query("DELETE FROM `ACTIVITY_UPLOAD_TABLE` WHERE `ACTIVITY_ID` = :activityID")
    void deleteUploadActivity(int activityID);

    @Query("DELETE FROM `ACTIVITY_UPLOAD_TABLE` WHERE `ACTIVITY_ID` = :activityID AND `ACTIVITY_DATE` = :activityDate")
    void deleteUploadActivity(int activityID, String activityDate);

    @Query("SELECT * FROM `ACTIVITY_UPLOAD_TABLE` WHERE `ACTIVITY_DATE` = :date")
    List<ActivityUploadDataTable> getOutBoxActivityUploadList(String date);

    @Query("SELECT * FROM `ACTIVITY_UPLOAD_TABLE`")
    List<ActivityUploadDataTable> getAllOutBoxActivityList();

    @Query("SELECT `ACTIVITY_DATE` FROM `ACTIVITY_UPLOAD_TABLE`")
    List<String> getAllActivityUploadDates();

    default ArrayList<ActivityUploadModelClass> getActivityUploadList(String date) {
        ArrayList<ActivityUploadModelClass> activityUploadModelClassList = new ArrayList<>();
        List<ActivityUploadDataTable> list = getOutBoxActivityUploadList(date);
        for(ActivityUploadDataTable activityUploadDataTable : list) {
            activityUploadModelClassList.add(new ActivityUploadModelClass(activityUploadDataTable.getId(), activityUploadDataTable.getActivityID(), activityUploadDataTable.getActivityDate(), activityUploadDataTable.getActivityTime(), activityUploadDataTable.getSlNo(), activityUploadDataTable.getName(), activityUploadDataTable.getImageName(), activityUploadDataTable.getFilePath(), activityUploadDataTable.getJsonData(), activityUploadDataTable.getSyncCount(), activityUploadDataTable.getSyncStatus()));
        }
        return activityUploadModelClassList;
    }
}
