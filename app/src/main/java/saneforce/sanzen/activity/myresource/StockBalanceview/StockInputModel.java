package saneforce.sanzen.activity.myresource.StockBalanceview;

public class StockInputModel {
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getBalance_Stock() {
        return Balance_Stock;
    }

    public void setBalance_Stock(String balance_Stock) {
        Balance_Stock = balance_Stock;
    }

    String Name,Code,Balance_Stock;
    public StockInputModel (String code,String name,String balance){
        this.Name=name;
        this.Code=code;
        this.Balance_Stock=balance;
    }
}
