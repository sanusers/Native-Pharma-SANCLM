package saneforce.sanzen.activity.tourPlan.overview.model;

public class DoctorCategoryModel extends MasterModel{
    private final String docCatName, noOfVisit;

    public DoctorCategoryModel(String code, String name, String docCatName, String noOfVisit) {
        super(code, name);
        this.docCatName = docCatName;
        this.noOfVisit = noOfVisit;
    }

    public String getDocCatName() {
        return docCatName;
    }

    public String getNoOfVisit() {
        return noOfVisit;
    }

}
