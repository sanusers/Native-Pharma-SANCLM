package saneforce.santrip.activity.Quiz;

public class QuizModelClass {

    String QuestionName;
    String QuestionCode;
    String Option ;
    String optionCode;
    String AnswerCode;

    String SelctedAnswerName;
    String SelctedAnswerCode;




    public QuizModelClass(String questionName,String questionCode, String option, String optionCode, String answerCode, String selctedAnswerName, String selctedAnswerCode) {
        QuestionName = questionName;
        Option = option;
        this.optionCode = optionCode;
        AnswerCode = answerCode;
        SelctedAnswerName = selctedAnswerName;
        SelctedAnswerCode = selctedAnswerCode;
        QuestionCode = questionCode;
    }

    public String getQuestionCode() {
        return QuestionCode;
    }

    public void setQuestionCode(String questionCode) {
        QuestionCode = questionCode;
    }

    public String getQuestionName() {
        return QuestionName;
    }

    public void setQuestionName(String questionName) {
        QuestionName = questionName;
    }

    public String getOption() {
        return Option;
    }

    public void setOption(String option) {
        Option = option;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }

    public String getAnswerCode() {
        return AnswerCode;
    }

    public void setAnswerCode(String answerCode) {
        AnswerCode = answerCode;
    }

    public String getSelctedAnswerName() {
        return SelctedAnswerName;
    }

    public void setSelctedAnswerName(String selctedAnswerName) {
        SelctedAnswerName = selctedAnswerName;
    }

    public String getSelctedAnswerCode() {
        return SelctedAnswerCode;
    }

    public void setSelctedAnswerCode(String selctedAnswerCode) {
        SelctedAnswerCode = selctedAnswerCode;
    }
}
