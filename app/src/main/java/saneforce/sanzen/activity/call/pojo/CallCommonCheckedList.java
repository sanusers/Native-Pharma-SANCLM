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

    String town_code,town_name;

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
