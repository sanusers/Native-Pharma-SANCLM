package saneforce.sanclm.activity.approvals.dcr.pojo;



public class DcrDetailModelList {
    String name;
    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String typeCust;
    String type;
    String pob;
    String sdp_name;
    String remark,call_feedback;
    String hq_name;
    String modTime,visitTime;

    public String getModTime() {
        return modTime;
    }

    public void setModTime(String modTime) {
        this.modTime = modTime;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getHq_name() {
        return hq_name;
    }

    public void setHq_name(String hq_name) {
        this.hq_name = hq_name;
    }

    public int getSelected_pos() {
        return selected_pos;
    }

    public void setSelected_pos(int selected_pos) {
        this.selected_pos = selected_pos;
    }

    int selected_pos;

    String jointWork;

    public String getJointWork() {
        return jointWork;
    }

    public void setJointWork(String jointWork) {
        this.jointWork = jointWork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeCust() {
        return typeCust;
    }

    public void setTypeCust(String typeCust) {
        this.typeCust = typeCust;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getSdp_name() {
        return sdp_name;
    }

    public void setSdp_name(String sdp_name) {
        this.sdp_name = sdp_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCall_feedback() {
        return call_feedback;
    }

    public void setCall_feedback(String call_feedback) {
        this.call_feedback = call_feedback;
    }

    public DcrDetailModelList(String name) {
        this.name = name;
    }

    public DcrDetailModelList(String hqName,String name, String code,String typeCust, String type, String sdp_name, String pob, String remark, String jointwork, String call_feedback,String visitTime,String modTime) {
        this.hq_name = hqName;
        this.name = name;
        this.code = code;
        this.typeCust = typeCust;
        this.type = type;
        this.sdp_name = sdp_name;
        this.pob = pob;
        this.remark = remark;
        this.jointWork = jointwork;
        this.call_feedback = call_feedback;
        this.visitTime = visitTime;
        this.modTime = modTime;
    }

    public DcrDetailModelList(String name, String typeCust, String type, String sdp_name, String pob, String remark, String jointwork, String call_feedback,int selected_pos) {
        this.name = name;
        this.typeCust = typeCust;
        this.type = type;
        this.sdp_name = sdp_name;
        this.pob = pob;
        this.remark = remark;
        this.jointWork = jointwork;
        this.call_feedback = call_feedback;
        this.selected_pos = selected_pos;
    }
}
