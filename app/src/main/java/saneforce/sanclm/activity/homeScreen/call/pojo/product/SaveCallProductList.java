package saneforce.sanclm.activity.homeScreen.call.pojo.product;

public class SaveCallProductList {
    String name;
    String balance_sam_stk;
    String sam_stk;
    String code;
    boolean isCliked;
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

    public SaveCallProductList(String name, String code, String category, String balance_sam_stk, String sam_stk, String sample_qty, String rx_qty, String rcpa_qty, String promoted, boolean isCliked) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.balance_sam_stk = balance_sam_stk;
        this.sam_stk = sam_stk;
        this.sample_qty = sample_qty;
        this.rx_qty = rx_qty;
        this.rcpa_qty = rcpa_qty;
        this.promoted = promoted;
        this.isCliked = isCliked;
    }

    public String getPromoted() {
        return promoted;
    }

    public void setPromoted(String promoted) {
        this.promoted = promoted;
    }

    public String getSam_stk() {
        return sam_stk;
    }

    public void setSam_stk(String sam_stk) {
        this.sam_stk = sam_stk;
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

    public boolean isCliked() {
        return isCliked;
    }

    public void setCliked(boolean cliked) {
        isCliked = cliked;
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
