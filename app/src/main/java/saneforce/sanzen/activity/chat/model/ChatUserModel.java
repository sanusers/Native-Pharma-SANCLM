package saneforce.sanzen.activity.chat.model;

public class ChatUserModel {
    private final String name, code, sfName, divisionCode, sfTypeCode, sfTypeName, designation;

    public ChatUserModel(String name, String code, String sfName, String divisionCode, String sfTypeCode, String sfTypeName, String designation) {
        this.name = name;
        this.code = code;
        this.sfName = sfName;
        this.divisionCode = divisionCode;
        this.sfTypeCode = sfTypeCode;
        this.sfTypeName = sfTypeName;
        this.designation = designation;
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

}
