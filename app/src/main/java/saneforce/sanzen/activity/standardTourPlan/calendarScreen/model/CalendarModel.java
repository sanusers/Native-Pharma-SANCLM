package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

import java.util.List;

public class CalendarModel {
    private final String caption;
    private final String id;
    private final boolean isVisible;
    private List<SelectedDCRModel> selectedDcrModelList;

    public CalendarModel(String caption, String id, boolean isVisible, List<SelectedDCRModel> selectedDcrModelList) {
        this.caption = caption;
        this.id = id;
        this.isVisible = isVisible;
        this.selectedDcrModelList = selectedDcrModelList;
    }

    public String getCaption() {
        return caption;
    }

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setDcrModelList(List<SelectedDCRModel> selectedDcrModelList) {
        this.selectedDcrModelList = selectedDcrModelList;
    }

    public List<SelectedDCRModel> getDcrModelList() {
        return selectedDcrModelList;
    }
}
