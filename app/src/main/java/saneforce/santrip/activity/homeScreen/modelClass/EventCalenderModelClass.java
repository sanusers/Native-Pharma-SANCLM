package saneforce.santrip.activity.homeScreen.modelClass;

public class EventCalenderModelClass {


    private String dateID;
    private String EventFlog;
    private String Month;
    private String Year;
    private boolean isSelected;


    public EventCalenderModelClass(String dateID, String worktypeFlog, String month, String year) {
        this.dateID = dateID;
        this.EventFlog = worktypeFlog;
        Month = month;
        Year = year;
    }

    public String getDateID() {
        return dateID;
    }

    public void setDateID(String dateID) {
        this.dateID = dateID;
    }

    public String getWorktypeFlog() {
        return EventFlog;
    }

    public void setWorktypeFlog(String worktypeFlog) {
        this.EventFlog = worktypeFlog;
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
