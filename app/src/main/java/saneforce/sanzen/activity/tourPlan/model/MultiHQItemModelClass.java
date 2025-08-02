package saneforce.sanzen.activity.tourPlan.model;

public class MultiHQItemModelClass {
    private final String name, code, hqCode, clusterCode, clusterName;
    private boolean isChecked;

    public MultiHQItemModelClass(String name, String code, String hqCode, String clusterCode, String clusterName, boolean isChecked) {
        this.name = name;
        this.code = code;
        this.hqCode = hqCode;
        this.clusterCode = clusterCode;
        this.clusterName = clusterName;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getHqCode() {
        return hqCode;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
