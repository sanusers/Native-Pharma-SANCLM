package saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

@Entity(tableName = "activity_offline_table")
public class ActivityOfflineDataTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "activity_date")
    private String activityDate = "";

    @ColumnInfo(name = "activity_time")
    private String activityTime = "";

    @ColumnInfo(name = "sl_no")
    private String slNo = "";

    @ColumnInfo(name = "name")
    private String name = "";

    @ColumnInfo(name = "dr_code")
    private String drCode = "";

    @ColumnInfo(name = "json_data")
    private String jsonData = "";

    @ColumnInfo(name = "sync_count")
    private int syncCount = 0;

    @ColumnInfo(name = "sync_status")
    private String syncStatus = "";

    @Ignore
    public ActivityOfflineDataTable() {
    }

    @Ignore
    public ActivityOfflineDataTable(String slNo, String name, String activityDate, String activityTime, String jsonData, int syncCount, String syncStatus) {
        this.slNo = slNo;
        this.name = name;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.jsonData = jsonData;
        this.syncCount = syncCount;
        this.syncStatus = syncStatus;
    }

    public ActivityOfflineDataTable(String slNo, String name, String drCode, String activityDate, String activityTime, String jsonData, int syncCount, String syncStatus) {
        this.slNo = slNo;
        this.name = name;
        this.drCode = drCode;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
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

    public int getSyncCount() {
        return syncCount;
    }

    public void setSyncCount(int syncCount) {
        this.syncCount = syncCount;
    }

    public String getDrCode() {
        return drCode;
    }

    public void setDrCode(String drCode) {
        this.drCode = drCode;
    }
}
