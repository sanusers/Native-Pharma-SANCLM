package saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls;

public class AddSampleAdditionalCall {
    String Cust_name;
    String prd_name;
    String prd_code;
    String prd_stock;
    String sample_qty;
    String Cust_code;

    public AddSampleAdditionalCall(String cust_name, String cust_code, String prd_name, String prd_code, String prd_stock, String sample_qty) {
        this.Cust_name = cust_name;
        this.Cust_code = cust_code;
        this.prd_name = prd_name;
        this.prd_code = prd_code;
        this.prd_stock = prd_stock;
        this.sample_qty = sample_qty;
    }

    public AddSampleAdditionalCall(String cust_name, String cust_code, String prd_name, String sample_qty) {
        this.Cust_name = cust_name;
        this.Cust_code = cust_code;
        this.prd_name = prd_name;
        this.sample_qty = sample_qty;
    }

    public String getCust_name() {
        return Cust_name;
    }

    public void setCust_name(String cust_name) {
        Cust_name = cust_name;
    }

    public String getPrd_code() {
        return prd_code;
    }

    public void setPrd_code(String prd_code) {
        this.prd_code = prd_code;
    }

    public String getCust_code() {
        return Cust_code;
    }

    public void setCust_code(String cust_code) {
        Cust_code = cust_code;
    }

    public String getPrd_name() {
        return prd_name;
    }

    public void setPrd_name(String prd_name) {
        this.prd_name = prd_name;
    }

    public String getPrd_stock() {
        return prd_stock;
    }

    public void setPrd_stock(String prd_stock) {
        this.prd_stock = prd_stock;
    }

    public String getSample_qty() {
        return sample_qty;
    }

    public void setSample_qty(String sample_qty) {
        this.sample_qty = sample_qty;
    }
}
