package saneforce.sanzen.activity.activityModule;

public class ActivityModelClass {

    private String slNo;
    private String activityName;
    private String activityFor;
    private String activeFlag;
    private String activityDesig;
    private String activityAvailable;


    public ActivityModelClass(String slNo, String activityName, String activityFor, String activeFlag, String activityDesig, String activityAvailable) {
        this.slNo = slNo;
        this.activityName = activityName;
        this.activityFor = activityFor;
        this.activeFlag = activeFlag;
        this.activityDesig = activityDesig;
        this.activityAvailable = activityAvailable;
    }

    public String getSlNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityFor() {
        return activityFor;
    }

    public void setActivityFor(String activityFor) {
        this.activityFor = activityFor;
    }

    public String getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getActivityDesig() {
        return activityDesig;
    }

    public void setActivityDesig(String activityDesig) {
        this.activityDesig = activityDesig;
    }

    public String getActivityAvailable() {
        return activityAvailable;
    }

    public void setActivityAvailable(String activityAvailable) {
        this.activityAvailable = activityAvailable;
    }
}