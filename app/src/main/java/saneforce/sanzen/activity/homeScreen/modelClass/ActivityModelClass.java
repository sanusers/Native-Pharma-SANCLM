package saneforce.sanzen.activity.homeScreen.modelClass;

public class ActivityModelClass {
    private int id;
    private String activityDate;
    private String activityTime;
    private String slNo;
    private String name;
    private String jsonData;
    private int syncCount;
    private String syncStatus;

    public ActivityModelClass(int id, String activityDate, String activityTime, String slNo, String name, String jsonData, int syncCount, String syncStatus) {
        this.id = id;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.slNo = slNo;
        this.name = name;
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

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
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
}
