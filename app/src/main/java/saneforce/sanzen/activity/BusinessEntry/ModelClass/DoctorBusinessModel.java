package saneforce.sanzen.activity.BusinessEntry.ModelClass;


public class DoctorBusinessModel {
    public String getHeaderno() {
        return Headerno;
    }

    public void setHeaderno(String headerno) {
        Headerno = headerno;
    }

    public String getDrcode() {
        return drcode;
    }

    public void setDrcode(String drcode) {
        this.drcode = drcode;
    }

    public String getDrname() {
        return drname;
    }

    public void setDrname(String drname) {
        this.drname = drname;
    }

    public String getActiveflag() {
        return activeflag;
    }

    public void setActiveflag(String activeflag) {
        this.activeflag = activeflag;
    }

    String Headerno,drcode,drname,activeflag;

    public String getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(String jsonArray) {
        this.jsonArray = jsonArray;
    }
    public DoctorBusinessModel() {}
    String jsonArray;
    public DoctorBusinessModel(String headerno, String code, String name,String activeflag, String jsonArray) {
        this.Headerno = headerno;
        this.drcode = code;
        this.drname = name;
        this.activeflag = activeflag;
        this.jsonArray = jsonArray;
    }
}
