package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

public class DocDataModel {
    private final String category;
    private final int totalDoctors;
    private final int totalVisits;
    private int plannedDoctors;
    private int plannedVisits;

    public DocDataModel(String category, int totalDoctors, int totalVisits, int plannedDoctors, int plannedVisits) {
        this.category = category;
        this.totalDoctors = totalDoctors;
        this.totalVisits = totalVisits;
        this.plannedDoctors = plannedDoctors;
        this.plannedVisits = plannedVisits;
    }

    public String getCategory() {
        return category;
    }

    public int getTotalDoctors() {
        return totalDoctors;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    public int getPlannedDoctors() {
        return plannedDoctors;
    }

    public void setPlannedDoctors(int plannedDoctors) {
        this.plannedDoctors = plannedDoctors;
    }

    public int getPlannedVisits() {
        return plannedVisits;
    }

    public void setPlannedVisits(int plannedVisits) {
        this.plannedVisits = plannedVisits;
    }

}
