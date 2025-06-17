package saneforce.sanzen.activity.homeScreen.modelClass;

import androidx.camera.core.processing.SurfaceProcessorNode;

public class CallsModalClass {


    public CallsModalClass(String docName, String callsDateTime, String docNameID) {
        DocName = docName;
        CallsDateTime = callsDateTime;
        DocNameID = docNameID;
    }

    public CallsModalClass(String TranslNo,String ADetSLNo, String docName, String doccode,String callsDateTime, String docNameID, String product, String input) {
        this.Trans_Slno = TranslNo;
        this.ADetSLNo = ADetSLNo;
        DocName = docName;
        DocCode = doccode;
        CallsDateTime = callsDateTime;
        DocNameID = docNameID;
        Product = product;
        Input = input;
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
    String Product;
    String Input;

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

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getInput() {
        return Input;
    }

    public void setInput(String input) {
        Input = input;
    }
}
