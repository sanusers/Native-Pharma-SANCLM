package saneforce.sanzen.activity.homeScreen.modelClass;

public class CallsModalClass {
    String custName;
    String custCode;
    String custType;
    String CallsDateTime;
    String dcrDate;
    String DocNameID;
    String Trans_Slno;
    String ADetSLNo;
    String Product;
    String Input;

    public CallsModalClass(String custName, String callsDateTime, String docNameID) {
        this.custName = custName;
        CallsDateTime = callsDateTime;
        DocNameID = docNameID;
    }

    public CallsModalClass(String TranslNo, String ADetSLNo, String custName, String custCode, String custType, String callsDateTime, String dcrDate, String docNameID, String product, String input) {
        this.Trans_Slno = TranslNo;
        this.ADetSLNo = ADetSLNo;
        this.custName = custName;
        this.custCode = custCode;
        this.custType = custType;
        CallsDateTime = callsDateTime;
        this.dcrDate = dcrDate;
        DocNameID = docNameID;
        Product = product;
        Input = input;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
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

    public String getDcrDate() {
        return dcrDate;
    }

    public void setDcrDate(String dcrDate) {
        this.dcrDate = dcrDate;
    }

}
