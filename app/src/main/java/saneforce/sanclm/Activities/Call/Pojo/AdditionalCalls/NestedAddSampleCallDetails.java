package saneforce.sanclm.Activities.Call.Pojo.AdditionalCalls;

public class NestedAddSampleCallDetails {
    String sam_name;
    String cust_name;
    String sam_qty, rx_qty;

    public NestedAddSampleCallDetails(String cust_name, String sam_name, String sam_qty, String rx_qty) {
        this.cust_name = cust_name;
        this.sam_name = sam_name;
        this.sam_qty = sam_qty;
        this.rx_qty = rx_qty;
    }

    public String getSam_name() {
        return sam_name;
    }

    public void setSam_name(String sam_name) {
        this.sam_name = sam_name;
    }

    public String getSam_qty() {
        return sam_qty;
    }

    public void setSam_qty(String sam_qty) {
        this.sam_qty = sam_qty;
    }

    public String getRx_qty() {
        return rx_qty;
    }

    public void setRx_qty(String rx_qty) {
        this.rx_qty = rx_qty;
    }


    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }
}
