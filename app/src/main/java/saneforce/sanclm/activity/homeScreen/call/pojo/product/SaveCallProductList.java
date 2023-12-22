package saneforce.sanclm.activity.homeScreen.call.pojo.product;

public class SaveCallProductList {
    String name;
    String balance_sam_stk;
    String last_stock;
    String code;
    boolean isClicked;
    String category;
    String from_call;
    String sample_qty;
    String rx_qty;
    String rcpa_qty;
    String promoted;
    String rate;

    public SaveCallProductList(String name, String code, String rate) {
        this.name = name;
        this.code = code;
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public SaveCallProductList(String code, String name, String sample_qty, String rx_qty, String rcpa_qty, String promoted) {
        this.name = name;
        this.code = code;
        this.sample_qty = sample_qty;
        this.rx_qty = rx_qty;
        this.rcpa_qty = rcpa_qty;
        this.promoted = promoted;
    }

    public SaveCallProductList(String name, String code, String category, String balance_sam_stk, String last_stock, String sample_qty, String rx_qty, String rcpa_qty, String promoted, boolean isClicked) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.balance_sam_stk = balance_sam_stk;
        this.last_stock = last_stock;
        this.sample_qty = sample_qty;
        this.rx_qty = rx_qty;
        this.rcpa_qty = rcpa_qty;
        this.promoted = promoted;
        this.isClicked = isClicked;
    }

    public String getPromoted() {
        return promoted;
    }

    public void setPromoted(String promoted) {
        this.promoted = promoted;
    }

    public String getLast_stock() {
        return last_stock;
    }

    public void setLast_stock(String last_stock) {
        this.last_stock = last_stock;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance_sam_stk() {
        return balance_sam_stk;
    }

    public void setBalance_sam_stk(String balance_sam_stk) {
        this.balance_sam_stk = balance_sam_stk;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFrom_call() {
        return from_call;
    }

    public void setFrom_call(String from_call) {
        this.from_call = from_call;
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

    public String getRcpa_qty() {
        return rcpa_qty;
    }

    public void setRcpa_qty(String rcpa_qty) {
        this.rcpa_qty = rcpa_qty;
    }
}
