package saneforce.sanzen.activity.approvals.tpdeviation;

public class TpDeviationModelList {
    String SfName;
    String SfCode;
    String slNo;
    String Date;
    String DeviationRemarks;
    String workTypeName;
    String HQName;
    String clusterName;
    String requestedDate;

    public TpDeviationModelList(String sfName, String sfCode, String SlNo, String date, String deviationRemarks, String workTypeName, String HQName, String clusterName, String requestedDate) {
        slNo = SlNo;
        SfCode = sfCode;
        SfName = sfName;
        Date = date;
        DeviationRemarks = deviationRemarks;
        this.workTypeName = workTypeName;
        this.HQName = HQName;
        this.clusterName = clusterName;
        this.requestedDate = requestedDate;
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

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
    }

    public String getHQName() {
        return HQName;
    }

    public void setHQName(String HQName) {
        this.HQName = HQName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }
}
