package saneforce.sanzen.activity.approvals.dcr.pojo;

public class DCRApprovalList {
    String trans_slNo;
    String sf_name;
    String activity_date;
    String plan_name;
    String workType_name;
    String SfCode;
    String fieldWork_indicator;
    String jointwrk;
    String pob;
    String callfd;
    String remarks;
    String position;
    boolean isSelected;
    String activity_date_sub;
    String submission_date_sub;
    String other_wt;

    String Additional_Temp_Details;

    public String getActivity_date_sub() {
        return activity_date_sub;
    }

    public void setActivity_date_sub(String activity_date_sub) {
        this.activity_date_sub = activity_date_sub;
    }

    public String getSubmission_date_sub() {
        return submission_date_sub;
    }

    public void setSubmission_date_sub(String submission_date_sub) {
        this.submission_date_sub = submission_date_sub;
    }

    public String getOther_wt() {
        return other_wt;
    }

    public void setOther_wt(String other_wt) {
        this.other_wt = other_wt;
    }

    public DCRApprovalList(String sf_name) {
        this.sf_name = sf_name;
    }

//    public DCRApprovalList(String trans_slNo, String sf_name, String activity_date, String plan_name, String workType_name, String SfCode, String fieldWork_indicator, String jointwrk, String pob, String callfd, String remarks) {
//        this.trans_slNo = trans_slNo;
//        this.sf_name = sf_name;
//        this.activity_date = activity_date;
//        this.plan_name = plan_name;
//        this.workType_name = workType_name;
//        this.SfCode = SfCode;
//        this.fieldWork_indicator = fieldWork_indicator;
//        this.jointwrk = jointwrk;
//        this.pob = pob;
//        this.callfd = callfd;
//        this.remarks = remarks;
//
//    }

    public DCRApprovalList(String trans_slNo, String sf_name, String activity_date, String plan_name, String workType_name, String SfCode, String fieldWork_indicator,String submission_date_sub,String other_wt,String remarks,String additional_Temp_Details) {
        this.trans_slNo = trans_slNo;
        this.sf_name = sf_name;
        this.activity_date = activity_date;
        this.plan_name = plan_name;
        this.workType_name = workType_name;
        this.SfCode = SfCode;
        this.fieldWork_indicator = fieldWork_indicator;
        this.submission_date_sub = submission_date_sub;
        this.other_wt = other_wt;
        this.remarks = remarks;
        this.Additional_Temp_Details = additional_Temp_Details;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getJointwrk() {
        return jointwrk;
    }

    public void setJointwrk(String jointwrk) {
        this.jointwrk = jointwrk;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getCallfd() {
        return callfd;
    }

    public void setCallfd(String callfd) {
        this.callfd = callfd;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTrans_slNo() {
        return trans_slNo;
    }

    public void setTrans_slNo(String trans_slNo) {
        this.trans_slNo = trans_slNo;
    }

    public String getSf_name() {
        return sf_name;
    }

    public void setSf_name(String sf_name) {
        this.sf_name = sf_name;
    }

    public String getActivity_date() {
        return activity_date;
    }

    public void setActivity_date(String activity_date) {
        this.activity_date = activity_date;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getWorkType_name() {
        return workType_name;
    }

    public void setWorkType_name(String workType_name) {
        this.workType_name = workType_name;
    }

    public String getSfCode() {
        return SfCode;
    }

    public void setSfCode(String sfCode) {
        this.SfCode = sfCode;
    }

    public String getFieldWork_indicator() {
        return fieldWork_indicator;
    }

    public void setFieldWork_indicator(String fieldWork_indicator) {
        this.fieldWork_indicator = fieldWork_indicator;
    }

    public String getAdditional_Temp_Details() {
        return Additional_Temp_Details;
    }

    public void setAdditional_Temp_Details(String additional_Temp_Details) {
        Additional_Temp_Details = additional_Temp_Details;
    }
}
