package saneforce.sanclm.activity.homeScreen.call.pojo.rcpa;

import saneforce.sanclm.response.SetupResponse;

public class RCPAAddedProdList {
    String prd_name;
    String che_codes;
    String prd_code;
    String chem_names;
    String totalPrdValue;

    public String getTotalPrdValue() {
        return totalPrdValue;
    }

    public void setTotalPrdValue(String totalPrdValue) {
        this.totalPrdValue = totalPrdValue;
    }

    public String getChe_codes() {
        return che_codes;
    }

    public void setChe_codes(String che_codes) {
        this.che_codes = che_codes;
    }

    public String getPrd_code() {
        return prd_code;
    }

    public void setPrd_code(String prd_code) {
        this.prd_code = prd_code;
    }

    String qty;
    String rate;
    String value;

    public RCPAAddedProdList(String chem_names, String chem_codes, String prd_name, String prd_codes, String qty, String rate, String value, String totalPrdValue) {
        this.prd_name = prd_name;
        this.prd_code = prd_codes;
        this.qty = qty;
        this.rate = rate;
        this.value = value;
        this.chem_names = chem_names;
        this.che_codes = chem_codes;
        this.totalPrdValue = totalPrdValue;
    }
    public RCPAAddedProdList(String prd_name) {
        this.prd_name = prd_name;
    }

    public String getChem_names() {
        return chem_names;
    }

    public void setChem_names(String chem_names) {
        this.chem_names = chem_names;
    }

    public String getPrd_name() {
        return prd_name;
    }

    public void setPrd_name(String prd_name) {
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
