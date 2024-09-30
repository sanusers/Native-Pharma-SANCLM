package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

public class PlanForModel {
    private final String caption;
    private final int imageID;
    private final int total;
    private int selected;

    public PlanForModel(String caption, int imageID, int total, int selected) {
        this.caption = caption;
        this.imageID = imageID;
        this.total = total;
        this.selected = selected;
    }

    public String getCaption() {
        return caption;
    }

    public int getImageID() {
        return imageID;
    }

    public int getTotal() {
        return total;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
}
