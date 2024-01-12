package saneforce.santrip.activity.homeScreen.call.pojo.additionalCalls;

public class AddSampleAdditionalCall {
    String Cust_name;
    String prd_name;
    String prd_code;
    String balance_stock;
    String sample_qty;
    String Cust_code;
    String category;
    String last_stock;

    public AddSampleAdditionalCall(String cust_name, String cust_code, String prd_name, String prd_code, String balance_stock, String last_stock, String sample_qty, String category) {
        this.Cust_name = cust_name;
        this.Cust_code = cust_code;
        this.prd_name = prd_name;
        this.prd_code = prd_code;
        this.balance_stock = balance_stock;
        this.sample_qty = sample_qty;
        this.category = category;
        this.last_stock = last_stock;
    }

    public AddSampleAdditionalCall(String cust_name, String cust_code, String prd_name, String sample_qty) {
        this.Cust_name = cust_name;
        this.Cust_code = cust_code;
        this.prd_name = prd_name;
        this.sample_qty = sample_qty;
    }

    public String getLast_stock() {
        return last_stock;
    }

    public void setLast_stock(String last_stock) {
        this.last_stock = last_stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getBalance_stock() {
        return balance_stock;
    }

    public void setBalance_stock(String balance_stock) {
        this.balance_stock = balance_stock;
    }

    public String getSample_qty() {
        return sample_qty;
    }

    public void setSample_qty(String sample_qty) {
        this.sample_qty = sample_qty;
    }
}