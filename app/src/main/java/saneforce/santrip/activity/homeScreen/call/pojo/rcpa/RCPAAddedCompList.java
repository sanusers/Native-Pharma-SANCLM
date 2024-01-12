package saneforce.santrip.activity.homeScreen.call.pojo.rcpa;

public class RCPAAddedCompList {
    String prd_name, prd_code, Chem_names, Chem_Code;
    String comp_company_name;
    String comp_company_code;
    String comp_product;
    String comp_product_code;
    String qty;
    String rate;
    String value;
    String remarks;
    boolean isSelected;
    String totalPrdValue;

    public RCPAAddedCompList(String prd_name, String prd_code, String chem_names, String chem_Code, String rate, String totalPrdValue) {
        this.prd_name = prd_name;
        this.prd_code = prd_code;
        Chem_names = chem_names;
        Chem_Code = chem_Code;
        this.rate = rate;
        this.totalPrdValue = totalPrdValue;
    }

    public RCPAAddedCompList(String prd_name, String prd_code, String chem_names, String chem_Code, String comp_company_name, String comp_code, String comp_product, String comp_product_code, String rate, boolean isSelected, String totalPrdValue) {
        this.prd_name = prd_name;
        this.prd_code = prd_code;
        this.Chem_names = chem_names;
        this.Chem_Code = chem_Code;
        this.comp_company_name = comp_company_name;
        this.comp_company_code = comp_code;
        this.comp_product = comp_product;
        this.comp_product_code = comp_product_code;
        this.rate = rate;
        this.isSelected = isSelected;
        this.totalPrdValue = totalPrdValue;
    }
    public RCPAAddedCompList(String chem_names, String chem_code, String prd_name, String prd_code, String comp_company_name, String comp_code, String comp_product, String comp_product_code, String qty, String rate, String value, String remarks, String totalPrdValue) {
        this.Chem_names = chem_names;
        this.Chem_Code = chem_code;
        this.prd_name = prd_name;
        this.prd_code = prd_code;
        this.comp_company_name = comp_company_name;
        this.comp_company_code = comp_code;
        this.comp_product = comp_product;
        this.comp_product_code = comp_product_code;
        this.qty = qty;
        this.rate = rate;
        this.value = value;
        this.remarks = remarks;
        this.totalPrdValue = totalPrdValue;
    }

    public String getComp_company_code() {
        return comp_company_code;
    }

    public void setComp_company_code(String comp_company_code) {
        this.comp_company_code = comp_company_code;
    }

    public String getComp_product_code() {
        return comp_product_code;
    }

    public void setComp_product_code(String comp_product_code) {
        this.comp_product_code = comp_product_code;
    }

    public String getTotalPrdValue() {
        return totalPrdValue;
    }

    public void setTotalPrdValue(String totalPrdValue) {
        this.totalPrdValue = totalPrdValue;
    }

    public String getPrd_code() {
        return prd_code;
    }

    public void setPrd_code(String prd_code) {
        this.prd_code = prd_code;
    }

    public String getChem_Code() {
        return Chem_Code;
    }

    public void setChem_Code(String chem_Code) {
        Chem_Code = chem_Code;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    public String getComp_company_name() {
        return comp_company_name;
    }

    public void setComp_company_name(String comp_company_name) {
        this.comp_company_name = comp_company_name;
    }

    public String getComp_product() {
        return comp_product;
    }

    public void setComp_product(String comp_product) {
        this.comp_product = comp_product;
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
