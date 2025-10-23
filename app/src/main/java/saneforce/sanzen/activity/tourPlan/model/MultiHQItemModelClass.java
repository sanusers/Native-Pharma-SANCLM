package saneforce.sanzen.activity.tourPlan.model;

public class MultiHQItemModelClass {
    private final String name;
    private String code, hqCode, clusterCode, clusterName;
    private boolean isChecked, isPlaceholder;

    public MultiHQItemModelClass(String name, String code, String hqCode, String clusterCode, String clusterName, boolean isChecked) {
        this.name = name;
        this.code = code;
        this.hqCode = hqCode;
        this.clusterCode = clusterCode;
        this.clusterName = clusterName;
        this.isChecked = isChecked;
    }

    public MultiHQItemModelClass(String name, boolean isPlaceholder) {
        this.name = name;
        this.isPlaceholder = isPlaceholder;
    }

    public MultiHQItemModelClass(MultiHQItemModelClass multiHQItemModelClass) {
        this.name = multiHQItemModelClass.name;
        this.code = multiHQItemModelClass.code;
        this.hqCode = multiHQItemModelClass.hqCode;
        this.clusterCode = multiHQItemModelClass.clusterCode;
        this.clusterName = multiHQItemModelClass.clusterName;
        this.isChecked = multiHQItemModelClass.isChecked;
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

    public boolean isPlaceholder() {
        return isPlaceholder;
    }

    public void setPlaceholder(boolean placeholder) {
        isPlaceholder = placeholder;
    }
}
