package saneforce.sanzen.activity.tourPlan.overview.model;

public class DCRModel extends MasterModel{
    private final String clusterName, clusterCode;

    public DCRModel(String code, String name, String clusterCode, String clusterName) {
        super(code, name);
        this.clusterCode = clusterCode;
        this.clusterName = clusterName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getClusterCode() {
        return clusterCode;
    }
}
