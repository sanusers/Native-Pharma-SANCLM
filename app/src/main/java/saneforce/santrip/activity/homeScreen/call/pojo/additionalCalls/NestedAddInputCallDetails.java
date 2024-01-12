package saneforce.santrip.activity.homeScreen.call.pojo.additionalCalls;

public class NestedAddInputCallDetails {

    public NestedAddInputCallDetails(String cust_name,String inp_name, String inp_qty) {
        this.cust_name = cust_name;
        this.inp_name = inp_name;
        this.inp_qty = inp_qty;
    }


    String inp_name;
    String cust_name;

    public String getInp_name() {
        return inp_name;
    }

    public void setInp_name(String inp_name) {
        this.inp_name = inp_name;
    }

    public String getInp_qty() {
        return inp_qty;
    }

    public void setInp_qty(String inp_qty) {
        this.inp_qty = inp_qty;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    String inp_qty;


}
