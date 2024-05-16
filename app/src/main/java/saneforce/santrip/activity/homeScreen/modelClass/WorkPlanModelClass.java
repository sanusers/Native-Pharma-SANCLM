package saneforce.santrip.activity.homeScreen.modelClass;

public class WorkPlanModelClass {
    private final int id;
    private String date;
    private String wtName;
    private String wtCode;
    private String jsonValues;
    private String wtStatus;
    private int syncStatus;

    public WorkPlanModelClass(int id, String date, String wtName, String wtCode, String jsonValues, String wtStatus, int syncStatus) {
        this.id = id;
        this.date = date;
        this.wtName = wtName;
        this.wtCode = wtCode;
        this.jsonValues = jsonValues;
        this.wtStatus = wtStatus;
        this.syncStatus = syncStatus;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWtName() {
        return wtName;
    }

    public void setWtName(String wtName) {
        this.wtName = wtName;
    }

    public String getWtCode() {
        return wtCode;
    }

    public void setWtCode(String wtCode) {
        this.wtCode = wtCode;
    }

    public String getJsonValues() {
        return jsonValues;
    }

    public void setJsonValues(String jsonValues) {
        this.jsonValues = jsonValues;
    }

    public String getWtStatus() {
        return wtStatus;
    }

    public void setWtStatus(String wtStatus) {
        this.wtStatus = wtStatus;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
