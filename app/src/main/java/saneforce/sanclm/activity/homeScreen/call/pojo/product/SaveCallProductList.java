package saneforce.sanclm.activity.homeScreen.call.pojo.product;

public class SaveCallProductList {
    String name;
    String stock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

    boolean isCliked;
    String category;
    String from_call;

    String sample_qty;
    String rx_qty;
    String rcpa_qty;

    public SaveCallProductList(String name, String stock, String sample_qty, String rx_qty, String rcpa_qty, boolean isCliked) {
        this.name = name;
        this.stock = stock;
        this.sample_qty = sample_qty;
        this.rx_qty = rx_qty;
        this.rcpa_qty = rcpa_qty;
        this.isCliked = isCliked;
    }
}
