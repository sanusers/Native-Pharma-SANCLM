package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

public class DCRModel {
    private final String name;
    private final String code;
    private final String category;
    private final String speciality;
    private final String townName;
    private final String townCode;
    private final int visitFrequency;
    private String plannedForName;
    private String plannedForCode;
    private boolean isSelected;

    public DCRModel(String name, String code, String category, String speciality, String townName, String townCode, int visitFrequency, String plannedForName, String plannedForCode, boolean isSelected) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.speciality = speciality;
        this.townName = townName;
        this.townCode = townCode;
        this.visitFrequency = visitFrequency;
        this.plannedForName = plannedForName;
        this.plannedForCode = plannedForCode;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getTownName() {
        return townName;
    }

    public String getTownCode() {
        return townCode;
    }

    public int getVisitFrequency() {
        return visitFrequency;
    }

    public String getPlannedForName() {
        return plannedForName;
    }

    public void setPlannedForName(String plannedForName) {
        this.plannedForName = plannedForName;
    }

    public String getPlannedForCode() {
        return plannedForCode;
    }

    public void setPlannedForCode(String plannedForCode) {
        this.plannedForCode = plannedForCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
