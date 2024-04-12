package saneforce.santrip.activity.myresource;

public class Resourcemodel_class {

    String listed_data, listed_count;

    String Dcr_name, Res_custname, Res_Category, Res_rx, Res_Specialty, Latitude, Longtitude, Res_id, Custoum_name, val_pos;

    String cust_name, town_name, visit_date, max_count, cust_id, custcode;
    int visit_count;

    boolean isVisibleView;

    public Resourcemodel_class(String listed_data, String listed_count, String val_pos) {
        this.listed_data = listed_data;
        this.listed_count = listed_count;
        this.val_pos = val_pos;
    }

    public Resourcemodel_class(String cust_name, String cust_id, String visit_date, String max_count) {
        this.cust_name = cust_name;
        this.cust_id = cust_id;
        this.visit_date = visit_date;
        this.max_count = max_count;
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

    public Resourcemodel_class(String dcr_name, String res_custname, String res_Category, String res_rx, String res_Specialty, String Latitude, String Longtitude, String Res_id, String Custoum_name,boolean isVisibleView) {
        Dcr_name = dcr_name;
        Res_custname = res_custname;
        Res_Category = res_Category;
        Res_rx = res_rx;
        Res_Specialty = res_Specialty;
        this.Latitude = Latitude;
        this.Longtitude = Longtitude;
        this.Res_id = Res_id;
        this.Custoum_name = Custoum_name;
        this.isVisibleView = isVisibleView;
    }


    public Resourcemodel_class(int visit_count, String custcode) {
        this.visit_count = visit_count;
        this.custcode = custcode;
    }

    public boolean isVisibleView() {
        return isVisibleView;
    }

    public void setVisibleView(boolean visibleView) {
        isVisibleView = visibleView;
    }

    public String getVal_pos() {
        return val_pos;
    }

    public void setVal_pos(String val_pos) {
        this.val_pos = val_pos;
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


    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public int getVisit_count() {
        return visit_count;
    }

    public void setVisit_count(int visit_count) {
        this.visit_count = visit_count;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getMax_count() {
        return max_count;
    }

    public void setMax_count(String max_count) {
        this.max_count = max_count;
    }


    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }


    public String getCustcode() {
        return custcode;
    }

    public void setCustcode(String custcode) {
        this.custcode = custcode;
    }
}
