package saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls;

public class AddInputAdditionalCall {

    String cust_name;
    String input_name, stock, inp_qty;


    public AddInputAdditionalCall(String cust_name, String input_name, String stock, String input_qty) {
        this.cust_name = cust_name;
        this.input_name = input_name;
        this.stock = stock;
        this.inp_qty = input_qty;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getInput_name() {
        return input_name;
    }

    public void setInput_name(String input_name) {
        this.input_name = input_name;
    }

    public String getInp_qty() {
        return inp_qty;
    }

    public void setInp_qty(String inp_qty) {
        this.inp_qty = inp_qty;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
