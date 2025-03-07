package saneforce.sanzen.activity.presentation.customerSelection.model;

public class CustomerDataModel implements Comparable<CustomerDataModel>{
    private final String name, code, clusterName, clusterCode, categoryName, categoryCode, specialityName, specialityCode, className, classCode;
    private boolean isSelected = false;

    public CustomerDataModel(String name, String code) {
        this.name = name;
        this.code = code;
        this.clusterName = "";
        this.clusterCode = "";
        this.categoryName = "";
        this.categoryCode = "";
        this.specialityName = "";
        this.specialityCode = "";
        this.className = "";
        this.classCode = "";
    }

    public CustomerDataModel(String name, String code, String clusterName, String clusterCode, String categoryName, String categoryCode, String specialityName, String specialityCode, String className, String classCode) {
        this.name = name;
        this.code = code;
        this.clusterName = clusterName;
        this.clusterCode = clusterCode;
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
        this.specialityName = specialityName;
        this.specialityCode = specialityCode;
        this.className = className;
        this.classCode = classCode;
    }

    public CustomerDataModel(String name, String code, String clusterName, String clusterCode, String categoryName, String categoryCode, String specialityName, String specialityCode, String className, String classCode, boolean isSelected) {
        this.name = name;
        this.code = code;
        this.clusterName = clusterName;
        this.clusterCode = clusterCode;
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
        this.specialityName = specialityName;
        this.specialityCode = specialityCode;
        this.className = className;
        this.classCode = classCode;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public String getClassName() {
        return className;
    }

    public String getClassCode() {
        return classCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int compareTo(CustomerDataModel o) {
        return this.name.compareTo(o.name);
    }
}
