package saneforce.sanclm.activity.approvals.tpdeviation;

public class TpDeviationModelList {
    String wtName;
    String Cluster;
    String Hq;
    String Remarks;
    String DeviationRemarks;

    public TpDeviationModelList(String wtName, String cluster, String hq, String remarks, String deviationRemarks) {
        this.wtName = wtName;
        Cluster = cluster;
        Hq = hq;
        Remarks = remarks;
        DeviationRemarks = deviationRemarks;
    }

    public String getWtName() {
        return wtName;
    }

    public void setWtName(String wtName) {
        this.wtName = wtName;
    }

    public String getCluster() {
        return Cluster;
    }

    public void setCluster(String cluster) {
        Cluster = cluster;
    }

    public String getHq() {
        return Hq;
    }

    public void setHq(String hq) {
        Hq = hq;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getDeviationRemarks() {
        return DeviationRemarks;
    }

    public void setDeviationRemarks(String deviationRemarks) {
        DeviationRemarks = deviationRemarks;
    }
}
