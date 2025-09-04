package saneforce.sanzen.activity.reports.missedReport;

public class DoctorVisitItem {
    private String name;
    private String code;
    private String territory;
    private String qualification;
    private String category;
    private String speciality;
    private String className;



    public DoctorVisitItem(String name, String territory,String code, String qualification, String category, String speciality, String className) {
        this.name = name;
        this.code = code;
        this.territory = territory;
        this.qualification = qualification;
        this.category = category;
        this.speciality = speciality;
        this.className = className;
    }

    public String getName() {
        return name;
    }
    public String getcode() {
        return code;
    }

    public String getTerritory() {
        return territory;
    }

    public String getQualification() {
        return qualification;
    }

    public String getCategory() {
        return category;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getClassName() {
        return className;
    }
}
