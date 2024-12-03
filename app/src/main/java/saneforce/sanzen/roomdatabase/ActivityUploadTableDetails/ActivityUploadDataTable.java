package saneforce.sanzen.roomdatabase.ActivityUploadTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

@Entity(tableName = "activity_upload_table")
public class ActivityUploadDataTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

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

    @ColumnInfo(name = "status")
    private String status = "";

    @ColumnInfo(name = "sync_status")
    private int syncStatus = 0;

    @Ignore
    public ActivityUploadDataTable() {
    }

    public ActivityUploadDataTable(String slNo, String name, String activityDate, String activityTime, String imageName, String filePath, String jsonData, String status, int syncStatus) {
        this.name = name;
        this.slNo = slNo;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.imageName = imageName;
        this.filePath = filePath;
        this.jsonData = jsonData;
        this.status = status;
        this.syncStatus = syncStatus;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
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
}
