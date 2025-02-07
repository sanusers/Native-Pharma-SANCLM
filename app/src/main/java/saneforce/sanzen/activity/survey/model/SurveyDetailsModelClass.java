package saneforce.sanzen.activity.survey.model;

public class SurveyDetailsModelClass {
    private final String questionID, surveyId, drCat, drSpl, drCls, hosCls, chmCat, stkState, stkHQ, surveyType, questionCodeID, questionType, answerLength, mandatory, question, answer, activeFlag;

    public SurveyDetailsModelClass(String questionID, String surveyId, String drCat, String drSpl, String drCls, String hosCls, String chmCat, String stkState, String stkHQ, String surveyType, String questionCodeID, String questionType, String answerLength, String mandatory, String question, String answer, String activeFlag) {
        this.questionID = questionID;
        this.surveyId = surveyId;
        this.drCat = drCat;
        this.drSpl = drSpl;
        this.drCls = drCls;
        this.hosCls = hosCls;
        this.chmCat = chmCat;
        this.stkState = stkState;
        this.stkHQ = stkHQ;
        this.surveyType = surveyType;
        this.questionCodeID = questionCodeID;
        this.questionType = questionType;
        this.answerLength = answerLength;
        this.mandatory = mandatory;
        this.question = question;
        this.answer = answer;
        this.activeFlag = activeFlag;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getSurveyId() {
        return surveyId;
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

    public String getSurveyType() {
        return surveyType;
    }

    public String getQuestionCodeID() {
        return questionCodeID;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getAnswerLength() {
        return answerLength;
    }

    public String getMandatory() {
        return mandatory;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getActiveFlag() {
        return activeFlag;
    }
}
