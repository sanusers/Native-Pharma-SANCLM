package saneforce.sanzen.activity.tourPlan.overview.model;

public abstract class MasterModel {
    private final String name, code;

    public MasterModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
