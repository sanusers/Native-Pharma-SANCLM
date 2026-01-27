package saneforce.sanzen.activity.tourPlan.model;

public class DoctorDataModel {
    private final String name, code;
    private String cluster, category, classs, speciality, qualification;
    private String clusterCode, categoryCode, classsCode, specialityCode, qualificationCode;
    private int visitCount = 0;

    public DoctorDataModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public DoctorDataModel(String name, String code, String cluster, String category, String classs, String speciality, String qualification, int visitCount) {
        this.name = name;
        this.code = code;
        this.cluster = cluster;
        this.category = category;
        this.classs = classs;
        this.speciality = speciality;
        this.qualification = qualification;
        this.visitCount = visitCount;
    }

    public DoctorDataModel(String name, String code, String cluster, String clusterCode, String category, String categoryCode, String classs, String classsCode, String speciality, String specialityCode, String qualification, String qualificationCode, int visitCount) {
        this.name = name;
        this.code = code;
        this.cluster = cluster;
        this.category = category;
        this.classs = classs;
        this.speciality = speciality;
        this.qualification = qualification;
        this.clusterCode = clusterCode;
        this.categoryCode = categoryCode;
        this.classsCode = classsCode;
        this.specialityCode = specialityCode;
        this.qualificationCode = qualificationCode;
        this.visitCount = visitCount;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getCluster() {
        return cluster;
    }

    public String getCategory() {
        return category;
    }

    public String getClasss() {
        return classs;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getQualification() {
        return qualification;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getClasssCode() {
        return classsCode;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public String getQualificationCode() {
        return qualificationCode;
    }
}
