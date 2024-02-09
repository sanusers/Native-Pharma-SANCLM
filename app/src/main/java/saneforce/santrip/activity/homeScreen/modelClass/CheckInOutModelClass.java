package saneforce.santrip.activity.homeScreen.modelClass;

public class CheckInOutModelClass {
    private String dates;
    private String CheckInTime;
    private String CheckOutTime;
    private String jsonInValues;
    private String jsonOutValues;
    private String CheckCount;

    public CheckInOutModelClass(String dates, String checkInTime, String checkOutTime, String jsonInValues, String jsonOutValues, String checkCount) {
        this.dates = dates;
        CheckInTime = checkInTime;
        CheckOutTime = checkOutTime;
        this.jsonInValues = jsonInValues;
        this.jsonOutValues = jsonOutValues;
        this.CheckCount = checkCount;
    }

    public String getCheckCount() {
        return CheckCount;
    }

    public void setCheckCount(String checkCount) {
        CheckCount = checkCount;
    }

    public String getJsonInValues() {
        return jsonInValues;
    }

    public void setJsonInValues(String jsonInValues) {
        this.jsonInValues = jsonInValues;
    }

    public String getJsonOutValues() {
        return jsonOutValues;
    }

    public void setJsonOutValues(String jsonOutValues) {
        this.jsonOutValues = jsonOutValues;
    }

    public String getCheckInTime() {
        return CheckInTime;
    }

    public void setCheckInTime(String checkInTime) {
        CheckInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return CheckOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        CheckOutTime = checkOutTime;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }


}
