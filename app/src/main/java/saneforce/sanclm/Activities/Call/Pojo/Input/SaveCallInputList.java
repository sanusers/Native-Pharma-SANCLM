package saneforce.sanclm.Activities.Call.Pojo.Input;

public class SaveCallInputList {

    String input_name;

    String inp_qty;

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

    public String getInput_stk() {
        return input_stk;
    }

    public void setInput_stk(String input_stk) {
        this.input_stk = input_stk;
    }

    public SaveCallInputList(String input_name, String inp_qty, String input_stk) {
        this.input_name = input_name;
        this.inp_qty = inp_qty;
        this.input_stk = input_stk;
    }

    String input_stk;
}
