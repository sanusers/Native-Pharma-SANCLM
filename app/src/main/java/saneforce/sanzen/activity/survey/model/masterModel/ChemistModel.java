package saneforce.sanzen.activity.survey.model.masterModel;

public class ChemistModel extends MasterModel{
    private final String categoryCode;

    public ChemistModel(String code, String name, String categoryCode) {
        super(code, name);
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

}
