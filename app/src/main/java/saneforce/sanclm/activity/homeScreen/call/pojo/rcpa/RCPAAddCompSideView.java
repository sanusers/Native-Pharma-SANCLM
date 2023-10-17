package saneforce.sanclm.activity.homeScreen.call.pojo.rcpa;

public class RCPAAddCompSideView {
    String prd_name;

    public RCPAAddCompSideView(String prd_name, String qty, String rate, String value) {
        this.prd_name = prd_name;
        this.qty = qty;
        this.rate = rate;
        this.value = value;
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

    public RCPAAddCompSideView(String prd_name) {
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
