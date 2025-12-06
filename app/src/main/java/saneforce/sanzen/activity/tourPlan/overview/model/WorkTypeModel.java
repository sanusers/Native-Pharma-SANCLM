package saneforce.sanzen.activity.tourPlan.overview.model;

public class WorkTypeModel extends MasterModel{
    private final String fwFlag, territorySlFlag;

    public WorkTypeModel(String code, String name, String fwFlag, String territorySlFlag) {
        super(code, name);
        this.fwFlag = fwFlag;
        this.territorySlFlag = territorySlFlag;
    }

    public String getFwFlag() {
        return fwFlag;
    }

    public String getTerritorySlFlag() {
        return territorySlFlag;
    }
}
