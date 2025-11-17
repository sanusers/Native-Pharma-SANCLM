package saneforce.sanzen.activity.chat.model;

import java.util.Locale;

public class ChatUserModel {
    private final String name, code, sfName, divisionCode, sfTypeCode, sfTypeName, designation, hq;
    private String date, message;
    private final int rank;
    private boolean isSelected;

    public ChatUserModel(String name, String code, String sfName, String divisionCode, String sfTypeCode, String sfTypeName, String designation, String hq) {
        this.name = name;
        this.code = code;
        this.sfName = sfName;
        this.divisionCode = divisionCode;
        this.sfTypeCode = sfTypeCode;
        this.sfTypeName = sfTypeName;
        this.designation = designation;
        this.hq = hq;
        this.rank = getRank(sfName);
        this.date = "";
        this.message = "";
    }

    private int getRank(String designation) {
        switch (designation.toUpperCase(Locale.ROOT)) {
            case "ADMIN":
                return 1;
            case "HR":
                return 2;
            case "MGR":
                return 3;
            case "MR":
                return 4;
            default:
                return Integer.MAX_VALUE;
        }
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSfName() {
        return sfName;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public String getSfTypeCode() {
        return sfTypeCode;
    }

    public String getSfTypeName() {
        return sfTypeName;
    }

    public String getDesignation() {
        return designation;
    }

    public String getHq() {
        return hq;
    }

    public int getRank() {
        return rank;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
