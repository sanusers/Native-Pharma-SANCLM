package saneforce.sanclm.activity.homeScreen.modelClass;

public class CallStatusModelClass {


    private String month;
    private String year;
    private String date;
    private String worktype;

    public CallStatusModelClass(String month, String year, String date, String worktype) {
        this.month = month;
        this.year = year;
        this.date = date;
        this.worktype = worktype;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWorktype() {
        return worktype;
    }

    public void setWorktype(String worktype) {
        this.worktype = worktype;
    }
}
