package saneforce.santrip.activity.homeScreen.modelClass;

public class CallsModalClass {


    public CallsModalClass(String docName, String callsDateTime, String docNameID) {
        DocName = docName;
        CallsDateTime = callsDateTime;
        DocNameID = docNameID;
    }

    public CallsModalClass(String TranslNo,String ADetSLNo, String docName, String doccode,String callsDateTime, String docNameID) {
        this.Trans_Slno = TranslNo;
        this.ADetSLNo = ADetSLNo;
        DocName = docName;
        DocCode = doccode;
        CallsDateTime = callsDateTime;
        DocNameID = docNameID;
    }

    public String getDocCode() {
        return DocCode;
    }

    public void setDocCode(String docCode) {
        DocCode = docCode;
    }

    public String getDocName() {
        return DocName;
    }

    public void setDocName(String docName) {
        DocName = docName;
    }

    public String getCallsDateTime() {
        return CallsDateTime;
    }

    public void setCallsDateTime(String callsDateTime) {
        CallsDateTime = callsDateTime;
    }

    public String getDocNameID() {
        return DocNameID;
    }

    public void setDocNameID(String docNameID) {
        DocNameID = docNameID;
    }

    String DocName;
    String DocCode;
    String  CallsDateTime;
    String  DocNameID;
    String Trans_Slno;
    String ADetSLNo;

    public String getTrans_Slno() {
        return Trans_Slno;
    }

    public void setTrans_Slno(String trans_Slno) {
        Trans_Slno = trans_Slno;
    }

    public String getADetSLNo() {
        return ADetSLNo;
    }

    public void setADetSLNo(String ADetSLNo) {
        this.ADetSLNo = ADetSLNo;
    }
}
