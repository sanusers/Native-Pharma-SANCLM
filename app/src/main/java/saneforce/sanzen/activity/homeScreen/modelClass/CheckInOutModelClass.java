package saneforce.sanzen.activity.homeScreen.modelClass;

public class CheckInOutModelClass {
    private final int id;
    private String dates;
    private String CheckInTime;
    private String CheckOutTime;
    private String jsonInValues;
    private String jsonOutValues;
    private int CheckCount;
    private int CheckStatus;

    public CheckInOutModelClass(int id, String dates, String checkInTime, String checkOutTime, String jsonInValues, String jsonOutValues, int checkCount, int checkStatus) {
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

    public int getCheckCount() {
        return CheckCount;
    }

    public void setCheckCount(int checkCount) {
        CheckCount = checkCount;
    }

    public int getId() {
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
