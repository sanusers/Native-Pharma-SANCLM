package saneforce.sanzen.activity.survey.model.masterModel;

public abstract class MasterModel {
    private final String code, name;

    public MasterModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
