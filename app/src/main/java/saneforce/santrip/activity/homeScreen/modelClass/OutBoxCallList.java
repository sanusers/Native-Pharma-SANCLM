package saneforce.santrip.activity.homeScreen.modelClass;

import java.util.ArrayList;

public class OutBoxCallList {
    private String CusName;
    private String CusCode;
    private String jsonData;
    private int SyncCount;

    public int getSyncCount() {
        return SyncCount;
    }

    public void setSyncCount(int syncCount) {
        SyncCount = syncCount;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    private String CusType;
    private String In;
    private String Out;
    private String dates;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIn() {
        return In;
    }

    public void setIn(String in) {
        In = in;
    }

    public String getOut() {
        return Out;
    }

    public void setOut(String out) {
        Out = out;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public OutBoxCallList(String cusName, String cusCode, String date,String In,String Out, String jsonData, String cusType,String status,int syncCount) {
        CusName = cusName;
        CusCode = cusCode;
        this.dates = date;
        this.In = In;
        this.Out = Out;
        this.jsonData = jsonData;
        CusType = cusType;
        this.status = status;
        this.SyncCount = syncCount;
    }

    public String getCusName() {
        return CusName;
    }

    public void setCusName(String cusName) {
        CusName = cusName;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }


    public String getCusType() {
        return CusType;
    }

    public void setCusType(String cusType) {
        CusType = cusType;
    }
}
