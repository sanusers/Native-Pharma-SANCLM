package saneforce.sanclm.activity.homeScreen.call.pojo.rcpa;

public class RCPAAddedCompList {
    String prd_name, Chem_names;
    String comp_name, comp_brand, qty, rate, value, remarks;

    public RCPAAddedCompList(String chem_names, String prd_name, String comp_name, String comp_brand, String qty, String rate, String value, String remarks) {
        this.Chem_names = chem_names;
        this.prd_name = prd_name;
        this.comp_name = comp_name;
        this.comp_brand = comp_brand;
        this.qty = qty;
        this.rate = rate;
        this.value = value;
        this.remarks = remarks;
    }

    public RCPAAddedCompList(String comp_name, String comp_brand, String qty, String rate, String value, String remarks) {
        this.comp_name = comp_name;
        this.comp_brand = comp_brand;
        this.qty = qty;
        this.rate = rate;
        this.value = value;
        this.remarks = remarks;
    }

    public String getPrd_name() {
        return prd_name;
    }

    public void setPrd_name(String prd_name) {
        this.prd_name = prd_name;
    }

    public String getChem_names() {
        return Chem_names;
    }

    public void setChem_names(String chem_names) {
        Chem_names = chem_names;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public String getComp_brand() {
        return comp_brand;
    }

    public void setComp_brand(String comp_brand) {
        this.comp_brand = comp_brand;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
