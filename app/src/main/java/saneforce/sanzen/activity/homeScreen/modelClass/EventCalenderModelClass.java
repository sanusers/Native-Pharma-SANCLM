package saneforce.sanzen.activity.homeScreen.modelClass;

public class EventCalenderModelClass {


    private String dateID;
    private String EventFlag;
    private String Month;
    private String Year;
    private boolean isSelected;


    public EventCalenderModelClass(String dateID, String worktypeFlog, String month, String year) {
        this.dateID = dateID;
        this.EventFlag = worktypeFlog;
        Month = month;
        Year = year;
    }

    public String getDateID() {
        return dateID;
    }

    public void setDateID(String dateID) {
        this.dateID = dateID;
    }

    public String getWorkTypeFlag() {
        return EventFlag;
    }

    public void setWorkTypeFlag(String worktypeFlog) {
        this.EventFlag = worktypeFlog;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }
}
