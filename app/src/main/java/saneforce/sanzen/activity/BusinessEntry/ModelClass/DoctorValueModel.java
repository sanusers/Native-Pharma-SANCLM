package saneforce.sanzen.activity.BusinessEntry.ModelClass;

public class DoctorValueModel {
    public String getListedDrCode() {
        return ListedDrCode;
    }

    public void setListedDrCode(String listedDrCode) {
        ListedDrCode = listedDrCode;
    }

    public String getDetail_No() {
        return Detail_No;
    }

    public void setDetail_No(String detail_No) {
        Detail_No = detail_No;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String ListedDrCode,Detail_No,value;
}
