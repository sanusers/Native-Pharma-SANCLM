package saneforce.sanzen.activity.homeScreen.modelClass;

public class DaySubmitModelClass {
    private String date;
    private String jsonValues;
    private int syncStatus;

    public DaySubmitModelClass(String date, String jsonValues, int syncStatus) {
        this.date = date;
        this.jsonValues = jsonValues;
        this.syncStatus = syncStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJsonValues() {
        return jsonValues;
    }

    public void setJsonValues(String jsonValues) {
        this.jsonValues = jsonValues;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
