package saneforce.sanzen.activity.tourPlan.overview.model;

public class ClusterModel extends MasterModel {
    private final String categoryCode;
    public ClusterModel(String code, String name, String categoryCode) {
        super(code, name);
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }
}
