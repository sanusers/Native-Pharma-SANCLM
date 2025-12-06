package saneforce.sanzen.activity.tourPlan.overview.model;

public class DoctorModel extends DCRModel{
    private final String categoryCode, categoryName, totalVisit;

    public DoctorModel(String name, String code, String clusterCode, String clusterName, String categoryCode, String categoryName, String totalVisit) {
        super(name, code, clusterCode, clusterName);
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.totalVisit = totalVisit;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getTotalVisit() {
        return totalVisit;
    }
}
