package saneforce.santrip.activity.activityModule;

public class ActivityDetailsModelClass {


    private String fieldName;
    private String controlId;
    private String creationId;
    private String input;
    private String mandatory;
    private String controlPara;
    private String groupCreationId;


    public ActivityDetailsModelClass(String fieldName, String controlId, String creationId, String input, String mandatory, String controlPara, String groupCreationId) {
        this.fieldName = fieldName;
        this.controlId = controlId;
        this.creationId = creationId;
        this.input = input;
        this.mandatory = mandatory;
        this.controlPara = controlPara;
        this.groupCreationId = groupCreationId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
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
}
