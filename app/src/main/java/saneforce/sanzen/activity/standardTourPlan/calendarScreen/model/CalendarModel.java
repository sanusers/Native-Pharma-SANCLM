package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

import java.util.List;

public class CalendarModel {
    private final String caption;
    private final String id;
    private final boolean isVisible;
    private List<DCRModel> dcrModelList;

    public CalendarModel(String caption, String id, boolean isVisible, List<DCRModel> dcrModelList) {
        this.caption = caption;
        this.id = id;
        this.isVisible = isVisible;
        this.dcrModelList = dcrModelList;
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

    public void setDcrModelList(List<DCRModel> dcrModelList) {
        this.dcrModelList = dcrModelList;
    }

    public List<DCRModel> getDcrModelList() {
        return dcrModelList;
    }
}
