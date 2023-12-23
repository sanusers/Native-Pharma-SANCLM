package saneforce.sanclm.activity.approvals.tpdeviation;

public class TpDeviationModelList {
    String HqName;
    String Date;

    public String getHqName() {
        return HqName;
    }

    public void setHqName(String hqName) {
        HqName = hqName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDeviationRemarks() {
        return DeviationRemarks;
    }

    public void setDeviationRemarks(String deviationRemarks) {
        DeviationRemarks = deviationRemarks;
    }

    public TpDeviationModelList(String hqName, String date, String deviationRemarks) {
        HqName = hqName;
        Date = date;
        DeviationRemarks = deviationRemarks;
    }

    String DeviationRemarks;


}
