package saneforce.santrip.activity.homeScreen.modelClass;

public class CheckInOutModelClass {
    private final String id;
    private String dates;
    private String CheckInTime;
    private String CheckOutTime;
    private String jsonInValues;
    private String jsonOutValues;
    private String CheckCount;
    private int CheckStatus;

    public CheckInOutModelClass(String id, String dates, String checkInTime, String checkOutTime, String jsonInValues, String jsonOutValues, String checkCount, int checkStatus) {
        this.id = id;
        this.dates = dates;
        CheckInTime = checkInTime;
        CheckOutTime = checkOutTime;
        this.jsonInValues = jsonInValues;
        this.jsonOutValues = jsonOutValues;
        this.CheckCount = checkCount;
        this.CheckStatus = checkStatus;
    }

    public int getCheckStatus() {
        return CheckStatus;
    }

    public void setCheckStatus(int checkStatus) {
        CheckStatus = checkStatus;
    }

    public String getCheckCount() {
        return CheckCount;
    }

    public void setCheckCount(String checkCount) {
        CheckCount = checkCount;
    }

    public String getId() {
        return id;
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
