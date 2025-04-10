package saneforce.sanzen.activity.survey.model.masterModel;

public class HospitalModel extends MasterModel {
    private final String classCode;

    public HospitalModel(String code, String name, String classCode) {
        super(code, name);
        this.classCode = classCode;
    }

    public String getClassCode() {
        return classCode;
    }

}
