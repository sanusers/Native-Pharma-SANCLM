package saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls;


public class AddInputAdditionalCall {

    String cust_name, cust_code;
    String input_name;
    String input_code;
    String balance_stock;
    String inp_qty;

    public String getLast_stock() {
        return last_stock;
    }

    public void setLast_stock(String last_stock) {
        this.last_stock = last_stock;
    }

    String last_stock;

    public AddInputAdditionalCall(String cust_name, String cust_code, String input_name, String input_code, String balance_stock, String last_stock, String input_qty) {
        this.cust_name = cust_name;
        this.cust_code = cust_code;
        this.input_name = input_name;
        this.input_code = input_code;
        this.balance_stock = balance_stock;
        this.inp_qty = input_qty;
        this.last_stock = last_stock;
    }

    public AddInputAdditionalCall(String cust_name, String cust_code, String input_name, String input_qty) {
        this.cust_name = cust_name;
        this.cust_code = cust_code;
        this.input_name = input_name;
        this.inp_qty = input_qty;
    }

    public String getCust_code() {
        return cust_code;
    }

    public void setCust_code(String cust_code) {
        this.cust_code = cust_code;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getInput_code() {
        return input_code;
    }

    public void setInput_code(String input_code) {
        this.input_code = input_code;
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

    public String getBalance_stock() {
        return balance_stock;
    }

    public void setBalance_stock(String balance_stock) {
        this.balance_stock = balance_stock;
    }
}
