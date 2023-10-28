package saneforce.sanclm.activity.myresource;

public class Resourcemodel_class {

    String listed_data,listed_count;

    String Dcr_name,Res_custname,Res_Category,Res_rx,Res_Specialty,Latitude,Longtitude,Res_id,Custoum_name;

    public Resourcemodel_class(String listed_data, String listed_count) {
        this.listed_data = listed_data;
        this.listed_count = listed_count;
    }

    public String getDcr_name() {
        return Dcr_name;
    }

    public void setDcr_name(String dcr_name) {
        Dcr_name = dcr_name;
    }

    public String getRes_custname() {
        return Res_custname;
    }

    public void setRes_custname(String res_custname) {
        Res_custname = res_custname;
    }

    public String getRes_Category() {
        return Res_Category;
    }

    public void setRes_Category(String res_Category) {
        Res_Category = res_Category;
    }

    public String getRes_rx() {
        return Res_rx;
    }

    public void setRes_rx(String res_rx) {
        Res_rx = res_rx;
    }

    public String getRes_Specialty() {
        return Res_Specialty;
    }

    public void setRes_Specialty(String res_Specialty) {
        Res_Specialty = res_Specialty;
    }



    public Resourcemodel_class(String dcr_name, String res_custname, String res_Category, String res_rx, String res_Specialty, String Latitude, String Longtitude, String Res_id, String Custoum_name) {
        Dcr_name = dcr_name;
        Res_custname = res_custname;
        Res_Category = res_Category;
        Res_rx = res_rx;
        Res_Specialty = res_Specialty;
        this.Latitude = Latitude;
        this.Longtitude = Longtitude;
        this.Res_id = Res_id;
        this.Custoum_name = Custoum_name;
    }




//    public Resourcemodel_class(String strlat, String strlong, String straddr, String strname, String str_townname, String imgs) {
//        this.strlat = strlat;
//        this.strlong = strlong;
//        this.straddr = straddr;
//        this.strname = strname;
//        this.str_townname = str_townname;
//        this.imgs = imgs;
//    }

    public String getListed_data() {
        return listed_data;
    }

    public void setListed_data(String listed_data) {
        this.listed_data = listed_data;
    }

    public String getListed_count() {
        return listed_count;
    }

    public void setListed_count(String listed_count) {
        this.listed_count = listed_count;
    }


    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(String longtitude) {
        Longtitude = longtitude;
    }
    public String getRes_id() {
        return Res_id;
    }

    public void setRes_id(String res_id) {
        Res_id = res_id;
    }
    public String getCustoum_name() {
        return Custoum_name;
    }

    public void setCustoum_name(String custoum_name) {
        Custoum_name = custoum_name;
    }

}
