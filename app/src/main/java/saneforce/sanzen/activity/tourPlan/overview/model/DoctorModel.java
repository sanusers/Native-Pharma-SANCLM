package saneforce.sanzen.activity.tourPlan.overview.model;

public class DoctorModel extends DCRModel{
    private final String categoryCode, categoryName, specialityCode, specialityName, totalVisit;

    public DoctorModel(String name, String code, String clusterCode, String clusterName, String categoryCode, String categoryName, String specialityCode, String specialityName, String totalVisit) {
        super(name, code, clusterCode, clusterName);
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.specialityCode = specialityCode;
        this.specialityName = specialityName;
        this.totalVisit = totalVisit;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public String getTotalVisit() {
        return totalVisit;
    }
}
