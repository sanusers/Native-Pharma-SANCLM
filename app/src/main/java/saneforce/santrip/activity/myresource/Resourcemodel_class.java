package saneforce.santrip.activity.myresource;

public class Resourcemodel_class {

    String listed_data, listed_count;

    String Dcr_code, Dcr_name, Res_custname, Res_Category, Res_rx, Res_Specialty, Latitude, Longtitude, Res_id, Custoum_name, val_pos, Res_Qualifiey, Res_adds, Res_Dob, Res_Dow, Res_mob,
            Res_phn, Res_Email, Res_listeddoc_sex, Res_Categorycode, Res_Specialtycode, Town_code, Town_name,Pos_name;

    String cust_name, visit_date, max_count, cust_id, custcode,workType,TP_DCR,type;
    int visit_count;

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


    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getTP_DCR() {
        return TP_DCR;
    }

    public void setTP_DCR(String TP_DCR) {
        this.TP_DCR = TP_DCR;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Resourcemodel_class(String dcr_code, String dcr_name, String res_custname, String res_Category, String res_Categorycode, String res_rx, String res_Specialtycode, String res_Specialty, String Latitude, String Longtitude, String Res_id,
                               String Custoum_name, String res_Qualifiey, String Res_adds, String Res_Dob, String Res_Dow, String Res_mob, String Res_phn, String Res_Email, String Res_listeddoc_sex, String Town_code, String Town_name, String Pos_name, String workType, String TP_DCR, String type) {
        Dcr_code = dcr_code;
        Dcr_name = dcr_name;
        Res_custname = res_custname;
        Res_Category = res_Category;
        Res_Categorycode = res_Categorycode;
        Res_Specialtycode = res_Specialtycode;
        Res_rx = res_rx;
        Res_Specialty = res_Specialty;
        this.Latitude = Latitude;
        this.Longtitude = Longtitude;
        this.Res_id = Res_id;
        this.Custoum_name = Custoum_name;
        this.Res_Qualifiey = res_Qualifiey;
        this.Res_adds = Res_adds;
        this.Res_Dob = Res_Dob;
        this.Res_Dow = Res_Dow;
        this.Res_mob = Res_mob;
        this.Res_phn = Res_phn;
        this.Res_Email = Res_Email;
        this.Res_listeddoc_sex = Res_listeddoc_sex;
        this.Town_code = Town_code;
        this.Town_name = Town_name;
        this.Pos_name = Pos_name;
        this.workType = workType;
        this.TP_DCR = TP_DCR;
        this.type = type;

    }

    public Resourcemodel_class(int visit_count, String custcode) {
        this.visit_count = visit_count;
        this.custcode = custcode;
    }

    public String getDcr_code() {
        return Dcr_code;
    }

    public void setDcr_code(String dcr_code) {
        Dcr_code = dcr_code;
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

    public String getRes_Categorycode() {
        return Res_Categorycode;
    }

    public void setRes_Categorycode(String res_Categorycode) {
        Res_Categorycode = res_Categorycode;
    }

    public String getRes_Specialtycode() {
        return Res_Specialtycode;
    }

    public void setRes_Specialtycode(String res_Specialtycode) {
        Res_Specialtycode = res_Specialtycode;
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


    public String getRes_Qualifiey() {
        return Res_Qualifiey;
    }

    public void setRes_Qualifiey(String res_Qualifiey) {
        Res_Qualifiey = res_Qualifiey;
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


    public String getRes_adds() {
        return Res_adds;
    }

    public void setRes_adds(String res_adds) {
        Res_adds = res_adds;
    }

    public String getRes_Dob() {
        return Res_Dob;
    }

    public void setRes_Dob(String res_Dob) {
        Res_Dob = res_Dob;
    }

    public String getRes_Dow() {
        return Res_Dow;
    }

    public void setRes_Dow(String res_Dow) {
        Res_Dow = res_Dow;
    }

    public String getRes_mob() {
        return Res_mob;
    }

    public void setRes_mob(String res_mob) {
        Res_mob = res_mob;
    }

    public String getRes_phn() {
        return Res_phn;
    }

    public void setRes_phn(String res_phn) {
        Res_phn = res_phn;
    }

    public String getRes_Email() {
        return Res_Email;
    }

    public void setRes_Email(String res_Email) {
        Res_Email = res_Email;
    }

    public String getRes_listeddoc_sex() {
        return Res_listeddoc_sex;
    }

    public void setRes_listeddoc_sex(String res_listeddoc_sex) {
        Res_listeddoc_sex = res_listeddoc_sex;
    }

    public String getTown_code() {
        return Town_code;
    }

    public void setTown_code(String town_code) {
        Town_code = town_code;
    }

    public String getPos_name() {
        return Pos_name;
    }

    public String getTown_name() {
        return Town_name;
    }

    public void setTown_name(String town_name) {
        Town_name = town_name;
    }

    public void setPos_name(String pos_name) {
        Pos_name = pos_name;
    }
}
