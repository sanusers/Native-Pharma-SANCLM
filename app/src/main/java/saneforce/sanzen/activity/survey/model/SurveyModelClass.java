package saneforce.sanzen.activity.survey.model;

public class SurveyModelClass {
    private final String surveyID, surveyName, fromDate, toDate;

    public SurveyModelClass(String surveyID, String surveyName, String fromDate, String toDate) {
        this.surveyID = surveyID;
        this.surveyName = surveyName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getSurveyID() {
        return surveyID;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }
}
