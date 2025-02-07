package saneforce.sanzen.activity.survey.model.masterModel;

public class DoctorModel extends MasterModel{
    private final String categoryCode, specialtyCode, classCode;

    public DoctorModel(String code, String name, String categoryCode, String specialtyCode, String classCode) {
        super(code, name);
        this.categoryCode = categoryCode;
        this.specialtyCode = specialtyCode;
        this.classCode = classCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getSpecialtyCode() {
        return specialtyCode;
    }

    public String getClassCode() {
        return classCode;
    }

}
