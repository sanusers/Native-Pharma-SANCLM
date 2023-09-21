package saneforce.sanclm.Activities.Call.Pojo.RCPA;

public class RCPAAddedProdList {
    String prd_name;

    public String getChem_names() {
        return chem_names;
    }

    public void setChem_names(String chem_names) {
        this.chem_names = chem_names;
    }

    String chem_names;

    public RCPAAddedProdList(String prd_name, String qty, String rate, String value, String chem_names) {
        this.prd_name = prd_name;
        this.qty = qty;
        this.rate = rate;
        this.value = value;
        this.chem_names = chem_names;
    }

    String qty;
    String rate;
    String value;

    public String getPrd_name() {
        return prd_name;
    }

    public void setPrd_name(String prd_name) {
        this.prd_name = prd_name;
    }

    public RCPAAddedProdList(String prd_name) {
        this.prd_name = prd_name;
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
}
