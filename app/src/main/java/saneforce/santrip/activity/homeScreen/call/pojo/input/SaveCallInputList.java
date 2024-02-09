package saneforce.santrip.activity.homeScreen.call.pojo.input;

public class SaveCallInputList {
    String input_name;
    String inp_qty;
    String inp_code;
    String balance_inp_stk;
    String last_inp_stk;
    String dataFrom;


    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public SaveCallInputList(String inp_code, String input_name, String inp_qty) {
        this.input_name = input_name;
        this.inp_code = inp_code;
        this.inp_qty = inp_qty;
    }


    public SaveCallInputList(String input_name, String inp_code, String inp_qty, String balance_inp_stk, String last_inp_stk) {
        this.input_name = input_name;
        this.inp_code = inp_code;
        this.inp_qty = inp_qty;
        this.balance_inp_stk = balance_inp_stk;
        this.last_inp_stk = last_inp_stk;
    }

    public String getInp_code() {
        return inp_code;
    }

    public void setInp_code(String inp_code) {
        this.inp_code = inp_code;
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

    public String getBalance_inp_stk() {
        return balance_inp_stk;
    }

    public void setBalance_inp_stk(String balance_inp_stk) {
        this.balance_inp_stk = balance_inp_stk;
    }

    public String getLast_inp_stk() {
        return last_inp_stk;
    }

    public void setLast_inp_stk(String last_inp_stk) {
        this.last_inp_stk = last_inp_stk;
    }

}