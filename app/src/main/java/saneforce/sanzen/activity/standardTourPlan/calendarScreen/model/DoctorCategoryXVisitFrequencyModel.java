package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

public class DoctorCategoryXVisitFrequencyModel {
    private final String category;
    private final int visitFrequency;

    public DoctorCategoryXVisitFrequencyModel(String category, int visitFrequency) {
        this.category = category;
        this.visitFrequency = visitFrequency;
    }

    public String getCategory() {
        return category;
    }

    public int getVisitFrequency() {
        return visitFrequency;
    }

}
