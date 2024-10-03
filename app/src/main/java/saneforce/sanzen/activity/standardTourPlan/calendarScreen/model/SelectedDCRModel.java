package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

public class SelectedDCRModel {
    private final int imgID;
    private final int dcrType;
    private int count;

    public SelectedDCRModel(int imgID, int dcrType, int count) {
        this.imgID = imgID;
        this.dcrType = dcrType;
        this.count = count;
    }

    public int getImgID() {
        return imgID;
    }

    public int getDcrType() {
        return dcrType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
