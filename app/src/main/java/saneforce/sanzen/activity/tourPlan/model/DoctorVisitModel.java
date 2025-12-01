package saneforce.sanzen.activity.tourPlan.model;

import java.util.List;

public class DoctorVisitModel {
    private String code, name, category;
    private List<String> plannedDates;
    private int totalVisit = 0, plannedVisit = 0;

    public DoctorVisitModel(String code, String name, String category, List<String> plannedDates, int totalVisit, int plannedVisit) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.plannedDates = plannedDates;
        this.totalVisit = totalVisit;
        this.plannedVisit = plannedVisit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getPlannedDates() {
        return plannedDates;
    }

    public void setPlannedDates(List<String> plannedDates) {
        this.plannedDates = plannedDates;
    }

    public int getTotalVisit() {
        return totalVisit;
    }

    public void setTotalVisit(int totalVisit) {
        this.totalVisit = totalVisit;
    }

    public int getPlannedVisit() {
        return plannedVisit;
    }

    public void setPlannedVisit(int plannedVisit) {
        this.plannedVisit = plannedVisit;
    }
}
