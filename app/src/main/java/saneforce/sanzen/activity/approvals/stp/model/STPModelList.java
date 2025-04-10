package saneforce.sanzen.activity.approvals.stp.model;

public class STPModelList {
    private final String name, code, divCode;

    public STPModelList(String name, String code, String divCode) {
        this.name = name;
        this.code = code;
        this.divCode = divCode;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDivCode() {
        return divCode;
    }
}
