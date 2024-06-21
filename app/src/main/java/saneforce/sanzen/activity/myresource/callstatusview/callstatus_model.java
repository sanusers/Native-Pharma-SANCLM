package saneforce.sanzen.activity.myresource.callstatusview;

public class callstatus_model {


    String CustCode, CustType, FW_Indicator, Dcr_dt, month_name, CustName, town_code, town_name, Dcr_flag, SF_Code, Trans_SlNo, AMSLNo, Mnth, chkflk, Dcrname, workType,time;

    public callstatus_model(String custCode, String custType, String FW_Indicator, String dcr_dt, String month_name, String custName, String town_code, String town_name,
                            String dcr_flag, String SF_Code, String trans_SlNo, String AMSLNo, String Mnth, String chkflk, String Dcrname, String workType,String time) {
        CustCode = custCode;
        CustType = custType;
        this.FW_Indicator = FW_Indicator;
        Dcr_dt = dcr_dt;
        this.month_name = month_name;
        CustName = custName;
        this.town_code = town_code;
        this.town_name = town_name;
        Dcr_flag = dcr_flag;
        this.SF_Code = SF_Code;
        Trans_SlNo = trans_SlNo;
        this.AMSLNo = AMSLNo;
        this.Mnth = Mnth;
        this.chkflk = chkflk;
        this.Dcrname = Dcrname;
        this.workType = workType;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getCustCode() {
        return CustCode;
    }

    public void setCustCode(String custCode) {
        CustCode = custCode;
    }

    public String getCustType() {
        return CustType;
    }

    public void setCustType(String custType) {
        CustType = custType;
    }

    public String getFW_Indicator() {
        return FW_Indicator;
    }

    public void setFW_Indicator(String FW_Indicator) {
        this.FW_Indicator = FW_Indicator;
    }

    public String getDcr_dt() {
        return Dcr_dt;
    }

    public void setDcr_dt(String dcr_dt) {
        Dcr_dt = dcr_dt;
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getTown_code() {
        return town_code;
    }

    public void setTown_code(String town_code) {
        this.town_code = town_code;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public String getDcr_flag() {
        return Dcr_flag;
    }

    public void setDcr_flag(String dcr_flag) {
        Dcr_flag = dcr_flag;
    }

    public String getSF_Code() {
        return SF_Code;
    }

    public void setSF_Code(String SF_Code) {
        this.SF_Code = SF_Code;
    }

    public String getTrans_SlNo() {
        return Trans_SlNo;
    }

    public void setTrans_SlNo(String trans_SlNo) {
        Trans_SlNo = trans_SlNo;
    }

    public String getAMSLNo() {
        return AMSLNo;
    }

    public void setAMSLNo(String AMSLNo) {
        this.AMSLNo = AMSLNo;
    }
    //        "CustCode":"",
//    "CustType":"0",
//    "FW_Indicator":"N",
//    "Dcr_dt":"2024-01-01",
//    "month_name":"January",
//    "Mnth":1,
//    "Yr":2024,
//    "CustName":"",
//    "town_code":"",
//    "town_name":"",
//    "Dcr_flag":1,
//    "SF_Code":"MGR0941",
//    "Trans_SlNo":"DP3-697",
//    "AMSLNo":""


    public String getMnth() {
        return Mnth;
    }

    public void setMnth(String mnth) {
        Mnth = mnth;
    }

    public String getChkflk() {
        return chkflk;
    }

    public void setChkflk(String chkflk) {
        this.chkflk = chkflk;
    }

    public String getDcrname() {
        return Dcrname;
    }

    public void setDcrname(String dcrname) {
        Dcrname = dcrname;
    }
}
