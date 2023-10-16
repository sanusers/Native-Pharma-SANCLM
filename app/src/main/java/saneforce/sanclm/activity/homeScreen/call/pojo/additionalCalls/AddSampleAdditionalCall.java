package saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls;

public class AddSampleAdditionalCall {
    public String getCust_name() {
        return Cust_name;
    }

    public void setCust_name(String cust_name) {
        Cust_name = cust_name;
    }

    String Cust_name,prd_name,prd_stock,sample_qty,rx_qty;

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

    public String getRx_qty() {
        return rx_qty;
    }

    public void setRx_qty(String rx_qty) {
        this.rx_qty = rx_qty;
    }

    public AddSampleAdditionalCall(String cust_name,String prd_name, String prd_stock, String sample_qty) {
        this.Cust_name = cust_name;
        this.prd_name = prd_name;
        this.prd_stock = prd_stock;
        this.sample_qty = sample_qty;
    }
}
