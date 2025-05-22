package saneforce.sanzen.activity.call.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CallCommonCheckedList implements Parcelable {
    boolean checkedItem;
    String name;
    String category;
    String ActualStock;
    String CurrentStock;

    protected CallCommonCheckedList(Parcel in) {
        checkedItem = in.readByte() != 0;
        name = in.readString();
        category = in.readString();
        ActualStock = in.readString();
        CurrentStock = in.readString();
        StockCode = in.readString();
        categoryExtra = in.readString();
        code = in.readString();
        stock_balance = in.readString();
        totalVisit = in.readString();
        town_code = in.readString();
        town_name = in.readString();
    }

    public static final Creator<CallCommonCheckedList> CREATOR = new Creator<CallCommonCheckedList>() {
        @Override
        public CallCommonCheckedList createFromParcel(Parcel in) {
            return new CallCommonCheckedList(in);
        }

        @Override
        public CallCommonCheckedList[] newArray(int size) {
            return new CallCommonCheckedList[size];
        }
    };

    public String getActualStock() {
        return ActualStock;
    }

    public void setActualStock(String actualStock) {
        ActualStock = actualStock;
    }

    public String getCurrentStock() {
        return CurrentStock;
    }

    public void setCurrentStock(String currentStock) {
        CurrentStock = currentStock;
    }

    public String getStockCode() {
        return StockCode;
    }

    public void setStockCode(String stockCode) {
        StockCode = stockCode;
    }

    String StockCode;

    public CallCommonCheckedList(String stockCode,String actualStock, String currentStock) {
        ActualStock = actualStock;
        CurrentStock = currentStock;
        StockCode = stockCode;
    }

    public String getCategoryExtra() {
        return categoryExtra;
    }

    public void setCategoryExtra(String categoryExtra) {
        this.categoryExtra = categoryExtra;
    }

    String categoryExtra;
    String code;
    String stock_balance;
    String totalVisit;

    public String getTotalVisit() {
        return totalVisit;
    }

    public void setTotalVisit(String totalVisit) {
        this.totalVisit = totalVisit;
    }

    public String getStock_balance() {
        return stock_balance;
    }

    public void setStock_balance(String stock_balance) {
        this.stock_balance = stock_balance;
    }

    String town_code;
    String town_name;
    public String pack;
    String NSR_Price,Sample_Price,Target_Price,Distributor_Price,Product_Detail_Name,value;

    public String getNSR_Price() {
        return NSR_Price;
    }

    public void setNSR_Price(String NSR_Price) {
        this.NSR_Price = NSR_Price;
    }

    public String getSample_Price() {
        return Sample_Price;
    }

    public void setSample_Price(String sample_Price) {
        Sample_Price = sample_Price;
    }

    public String getTarget_Price() {
        return Target_Price;
    }

    public void setTarget_Price(String target_Price) {
        Target_Price = target_Price;
    }

    public String getDistributor_Price() {
        return Distributor_Price;
    }

    public void setDistributor_Price(String distributor_Price) {
        Distributor_Price = distributor_Price;
    }

    public String getProduct_Detail_Name() {
        return Product_Detail_Name;
    }

    public void setProduct_Detail_Name(String product_Detail_Name) {
        Product_Detail_Name = product_Detail_Name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRetailor_Price() {
        return Retailor_Price;
    }

    public void setRetailor_Price(String retailor_Price) {
        Retailor_Price = retailor_Price;
    }

    public String getProduct_Sale_Unit() {
        return Product_Sale_Unit;
    }

    public void setProduct_Sale_Unit(String product_Sale_Unit) {
        Product_Sale_Unit = product_Sale_Unit;
    }

    public String getMRP_Price() {
        return MRP_Price;
    }

    public void setMRP_Price(String MRP_Price) {
        this.MRP_Price = MRP_Price;
    }

    String Retailor_Price,Product_Sale_Unit,MRP_Price;

    public String getDetailcode() {
        return detailcode;
    }

    public void setDetailcode(String detailcode) {
        this.detailcode = detailcode;
    }

    public String detailcode;
    // Doctor-specific prefilled data
    public boolean isSelected = false;
    public double selectedMRP = 0;

    public double getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(double selectedValue) {
        this.selectedValue = selectedValue;
    }

    public double selectedValue = 0;
    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int qty;
    // Optional: fields for autofill from doctor product data
    public double selectedQty = 0;

    public double getSelectedMRP() {
        return selectedMRP;
    }

    public void setSelectedMRP(double selectedMRP) {
        this.selectedMRP = selectedMRP;
    }

    public double getSelectedQty() {
        return selectedQty;
    }

    public void setSelectedQty(double selectedQty) {
        this.selectedQty = selectedQty;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String rate;

    public String getTown_code() {
        return town_code;
    }

    public void setTown_code(String town_code) {
        this.town_code = town_code;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public CallCommonCheckedList(String name) {
        this.name = name;
    }

    public CallCommonCheckedList(boolean checkedItem, String name, String category) {
        this.checkedItem = checkedItem;
        this.name = name;
        this.category = category;
    }

    public CallCommonCheckedList(String name, String code, String stock_balance, boolean checkedItem, String category, String categoryExtra) {
        this.checkedItem = checkedItem;
        this.name = name;
        this.code = code;
        this.stock_balance = stock_balance;
        this.category = category;
        this.categoryExtra = categoryExtra;
    }
    public CallCommonCheckedList(String name, String pack, String rate,String code,String detailcode,int qty,String prdsale,String mrp,String retailor,String distributor,
                                 String nsr,String Sample,String target,String value) {
        this.name = name;
        this.pack = pack;
        this.rate = rate;
        this.code = code;
        this.detailcode=detailcode;
        this.qty=qty;
        this.Product_Sale_Unit=prdsale;
        this.MRP_Price=mrp;
        this.Retailor_Price=retailor;
        this.Distributor_Price=distributor;
        this.NSR_Price=nsr;
        this.Sample_Price=Sample;
        this.Target_Price=target;
        this.value=value;

    }

    public CallCommonCheckedList(String name, String code,boolean checkedItem) {
        this.checkedItem = checkedItem;
        this.code = code;
        this.name = name;
    }


    public CallCommonCheckedList(String name, String code,String stock_balance,boolean checkedItem) {
        this.checkedItem = checkedItem;
        this.code = code;
        this.stock_balance = stock_balance;
        this.name = name;
    }
    public CallCommonCheckedList(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public CallCommonCheckedList(String name, String code, String town_name, String town_code, boolean checkedItem,String totalVisit) {
        this.checkedItem = checkedItem;
        this.code = code;
        this.name = name;
        this.town_code = town_code;
        this.town_name = town_name;
        this.totalVisit = totalVisit;
    }

    public CallCommonCheckedList(String name,boolean checkedItem) {
        this.checkedItem = checkedItem;
        this.name = name;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isCheckedItem() {
        return checkedItem;
    }

    public void setCheckedItem(boolean checkedItem) {
        this.checkedItem = checkedItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (checkedItem ? 1 : 0));
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(ActualStock);
        dest.writeString(CurrentStock);
        dest.writeString(StockCode);
        dest.writeString(categoryExtra);
        dest.writeString(code);
        dest.writeString(stock_balance);
        dest.writeString(totalVisit);
        dest.writeString(town_code);
        dest.writeString(town_name);
    }
}
