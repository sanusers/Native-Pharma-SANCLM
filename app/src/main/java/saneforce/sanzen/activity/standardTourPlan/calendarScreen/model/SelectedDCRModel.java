package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

public class SelectedDCRModel {
    private final int imgID;
    private final int dcrType;
    private String dcrCodes;
    private int count;

    public SelectedDCRModel(int imgID, int dcrType, String dcrCodes, int count) {
        this.imgID = imgID;
        this.dcrType = dcrType;
        this.dcrCodes = dcrCodes;
        this.count = count;
    }

    public int getImgID() {
        return imgID;
    }

    public int getDcrType() {
        return dcrType;
    }

    public String getDcrCodes() {
        return dcrCodes;
    }

    public void setDcrCodes(String dcrCodes) {
        this.dcrCodes = dcrCodes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
