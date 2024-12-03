package saneforce.sanzen.activity.homeScreen.modelClass;

import androidx.room.ColumnInfo;

public class ActivityUploadModelClass {
    private String id;
    private String activityDate;
    private String activityTime;
    private String slNo;
    private String name;
    private String imageName;
    private String filePath;
    private String jsonData;
    private String status;
    private int syncStatus;

    public ActivityUploadModelClass(String id, String activityDate, String activityTime, String slNo, String name, String imageName, String filePath, String jsonData, String status, int syncStatus) {
        this.id = id;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.slNo = slNo;
        this.name = name;
        this.imageName = imageName;
        this.filePath = filePath;
        this.jsonData = jsonData;
        this.status = status;
        this.syncStatus = syncStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
