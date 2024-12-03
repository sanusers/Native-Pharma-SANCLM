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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveActivityUploadData(ActivityUploadDataTable activityUploadDataTable);

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_UPLOAD_TABLE")
    boolean isActivityUploadAvailable();

    @Query("SELECT COUNT(1) > 0 FROM ACTIVITY_UPLOAD_TABLE WHERE `ACTIVITY_DATE` = :date")
    boolean isAvailableActivityOnDate(String date);

    @Query("SELECT EXISTS(SELECT 1 FROM `ACTIVITY_UPLOAD_TABLE` WHERE `STATUS` = :status)")
    boolean isActivityAvailableByStatus(String status);

    @Query("SELECT * FROM `ACTIVITY_UPLOAD_TABLE` WHERE `NAME` = :name AND `ACTIVITY_DATE` = :date AND `ACTIVITY_DATE` = :id")
    ActivityUploadDataTable getActivityUploadData(String id, String name, String date);

    @Query("SELECT `JSON_DATA` FROM `ACTIVITY_UPLOAD_TABLE` WHERE `ACTIVITY_DATE` = :date AND `NAME` = :name AND `ACTIVITY_DATE` = :id")
    String getActivityJson(String id, String name, String date);

    @Query("DELETE FROM `ACTIVITY_UPLOAD_TABLE` WHERE `NAME` = :name AND `ACTIVITY_DATE` = :date AND `ACTIVITY_DATE` = :id")
    void deleteUploadActivity(String id, String name, String date);

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
            activityUploadModelClassList.add(new ActivityUploadModelClass(activityUploadDataTable.getId(), activityUploadDataTable.getActivityDate(), activityUploadDataTable.getActivityTime(), activityUploadDataTable.getSlNo(), activityUploadDataTable.getName(), activityUploadDataTable.getImageName(), activityUploadDataTable.getFilePath(), activityUploadDataTable.getJsonData(), activityUploadDataTable.getStatus(), activityUploadDataTable.getSyncStatus()));
        }
        return activityUploadModelClassList;
    }
}
