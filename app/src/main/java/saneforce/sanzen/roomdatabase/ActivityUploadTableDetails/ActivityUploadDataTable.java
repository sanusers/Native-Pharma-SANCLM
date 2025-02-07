package saneforce.sanzen.roomdatabase.ActivityUploadTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataTable;

@Entity(tableName = "activity_upload_table"
//        , foreignKeys = {
//        @ForeignKey(entity = ActivityOfflineDataTable.class, parentColumns = "id", childColumns = "activity_id", onDelete = ForeignKey.CASCADE)
//}, indices = {@Index(value = {"activity_id"})}
)
public class ActivityUploadDataTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "activity_id")
    private int activityID;

    @ColumnInfo(name = "activity_date")
    private String activityDate = "";

    @ColumnInfo(name = "activity_time")
    private String activityTime = "";

    @ColumnInfo(name = "sl_no")
    private String slNo = "";

    @ColumnInfo(name = "name")
    private String name = "";

    @ColumnInfo(name = "image_name")
    private String imageName = "";

    @ColumnInfo(name = "file_path")
    private String filePath = "";

    @ColumnInfo(name = "json_data")
    private String jsonData = "";

    @ColumnInfo(name = "sync_count")
    private int syncCount = 0;

    @ColumnInfo(name = "sync_status")
    private String syncStatus = "";

    @Ignore
    public ActivityUploadDataTable() {
    }

    public ActivityUploadDataTable(int activityID, String slNo, String name, String activityDate, String activityTime, String imageName, String filePath, String jsonData, int syncCount, String syncStatus) {
        this.activityID = activityID;
        this.slNo = slNo;
        this.name = name;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.imageName = imageName;
        this.filePath = filePath;
        this.jsonData = jsonData;
        this.syncCount = syncCount;
        this.syncStatus = syncStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public JSONArray getActivityDataJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            if(jsonData != null && !jsonData.isEmpty()) jsonArray = new JSONArray(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getSlNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSyncCount() {
        return syncCount;
    }

    public void setSyncCount(int syncCount) {
        this.syncCount = syncCount;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }
}
