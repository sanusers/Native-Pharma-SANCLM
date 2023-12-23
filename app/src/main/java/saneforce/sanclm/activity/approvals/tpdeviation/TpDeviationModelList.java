package saneforce.sanclm.activity.approvals.tpdeviation;

public class TpDeviationModelList {
    String SfName;
    String SfCode;
    String slNo;
    String Date;
    String DeviationRemarks;

    public TpDeviationModelList(String sfName, String sfCode, String SlNo, String date, String deviationRemarks) {
        slNo = SlNo;
        SfCode = sfCode;
        SfName = sfName;
        Date = date;
        DeviationRemarks = deviationRemarks;
    }

    public String getSfCode() {
        return SfCode;
    }

    public void setSfCode(String sfCode) {
        SfCode = sfCode;
    }

    public String getSlNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getSfName() {
        return SfName;
    }

    public void setSfName(String sfName) {
        SfName = sfName;
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


}
