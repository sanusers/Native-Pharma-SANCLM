package saneforce.santrip.activity.Quiz;

import java.util.ArrayList;

public class QuizOptionModelClass {

    int QuestionId;
    String[]  OptionList ;
    String[]  OptionCodeList ;
    String SelectedOption;
    String SelctionCode;


    boolean ischecked;
    String optionName;



    public QuizOptionModelClass(boolean ischecked, String optionName) {
        this.ischecked = ischecked;
        this.optionName = optionName;
    }

    public QuizOptionModelClass(int questionId, String selectedOption, String selctionCode) {
        QuestionId = questionId;
        SelectedOption = selectedOption;
        SelctionCode = selctionCode;
    }

    public String[]  getOptionCodeList() {

        return OptionCodeList;
    }

    public void setOptionCodeList(String[]  optionCodeList) {
        OptionCodeList = optionCodeList;
    }

    public String getSelctionCode() {
        return SelctionCode;
    }

    public void setSelctionCode(String selctionCode) {
        SelctionCode = selctionCode;
    }

    public QuizOptionModelClass(int questionId, String[]  optionList, String[]  optionCodeList, String selectedOption, String selctionCode) {
        QuestionId = questionId;
        OptionList = optionList;
        OptionCodeList = optionCodeList;
        SelectedOption = selectedOption;
        SelctionCode = selctionCode;
    }

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        QuestionId = questionId;
    }

    public String[]  getOptionList() {
        return OptionList;
    }

    public void setOptionList(String[]  optionList) {
        OptionList = optionList;
    }

    public String getSelectedOption() {
        return SelectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        SelectedOption = selectedOption;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
