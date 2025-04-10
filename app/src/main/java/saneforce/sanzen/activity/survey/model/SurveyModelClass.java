package saneforce.sanzen.activity.survey.model;

public class SurveyModelClass {
    private final String surveyID, surveyName, drCat, drSpl, drCls, hosCls, chmCat, stkState, stkHQ, fromDate, toDate;

//    public SurveyModelClass(String surveyID, String surveyName, String fromDate, String toDate) {
//        this.surveyID = surveyID;
//        this.surveyName = surveyName;
//        this.fromDate = fromDate;
//        this.toDate = toDate;
//    }

    public SurveyModelClass(String surveyID, String surveyName, String drCat, String drSpl, String drCls, String hosCls, String chmCat, String stkState, String stkHQ, String fromDate, String toDate) {
        this.surveyID = surveyID;
        this.surveyName = surveyName;
        this.drCat = drCat;
        this.drSpl = drSpl;
        this.drCls = drCls;
        this.hosCls = hosCls;
        this.chmCat = chmCat;
        this.stkState = stkState;
        this.stkHQ = stkHQ;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getSurveyID() {
        return surveyID;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public String getDrCat() {
        return drCat;
    }

    public String getDrSpl() {
        return drSpl;
    }

    public String getDrCls() {
        return drCls;
    }

    public String getHosCls() {
        return hosCls;
    }

    public String getChmCat() {
        return chmCat;
    }

    public String getStkState() {
        return stkState;
    }

    public String getStkHQ() {
        return stkHQ;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }
}
