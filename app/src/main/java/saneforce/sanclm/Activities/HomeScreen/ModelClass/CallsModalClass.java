package saneforce.sanclm.Activities.HomeScreen.ModelClass;

public class CallsModalClass {



    public CallsModalClass(String docName, String callsDateTime, String docNameID) {
        DocName = docName;
        CallsDateTime = callsDateTime;
        DocNameID = docNameID;
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
    String  CallsDateTime;
    String  DocNameID;

}
