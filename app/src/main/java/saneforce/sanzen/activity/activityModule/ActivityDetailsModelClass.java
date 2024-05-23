package saneforce.sanzen.activity.activityModule;

public class ActivityDetailsModelClass {




    private String LabelTitleName;
    private String AnswerTxt;
    private String AnswerTxt2;
    private String controlId;
    private int Position;
    private String Codes ;

    private String fieldName;

    private String creationId;
    private String input;
    private String mandatory;
    private String controlPara;
    private String groupCreationId;

    private String slno;


    public ActivityDetailsModelClass(String fieldName, String controlId, String creationId, String input, String mandatory, String controlPara, String groupCreationId, String slno) {
        this.fieldName = fieldName;
        this.controlId = controlId;
        this.creationId = creationId;
        this.input = input;
        this.mandatory = mandatory;
        this.controlPara = controlPara;
        this.groupCreationId = groupCreationId;
        this.slno = slno;

    }



    public ActivityDetailsModelClass(int position, String fieldName,String answerTxt, String answerTxt2, String controlId, String creationId, String input, String mandatory, String controlPara, String groupCreationId,String codes,String slno) {

        this. AnswerTxt = answerTxt;
        this. AnswerTxt2 = answerTxt2;
        this. Position = position;
        this.fieldName = fieldName;
        this.controlId = controlId;
        this.creationId = creationId;
        this.input = input;
        this.mandatory = mandatory;
        this.controlPara = controlPara;
        this.groupCreationId = groupCreationId;
        this.Codes = codes;
        this.slno = slno;
    }

    public String getLabelTitleName() {
        return LabelTitleName;
    }

    public void setLabelTitleName(String labelTitleName) {
        LabelTitleName = labelTitleName;
    }

    public String getAnswerTxt() {
        return AnswerTxt;
    }

    public void setAnswerTxt(String answerTxt) {
        AnswerTxt = answerTxt;
    }

    public String getAnswerTxt2() {
        return AnswerTxt2;
    }

    public void setAnswerTxt2(String answerTxt2) {
        AnswerTxt2 = answerTxt2;
    }

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public String getCreationId() {
        return creationId;
    }

    public void setCreationId(String creationId) {
        this.creationId = creationId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getControlPara() {
        return controlPara;
    }

    public void setControlPara(String controlPara) {
        this.controlPara = controlPara;
    }

    public String getGroupCreationId() {
        return groupCreationId;
    }

    public void setGroupCreationId(String groupCreationId) {
        this.groupCreationId = groupCreationId;
    }

    public String getCodes() {
        return Codes;
    }

    public void setCodes(String codes) {
        Codes = codes;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }
}
