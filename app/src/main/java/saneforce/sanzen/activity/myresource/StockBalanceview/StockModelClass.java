package saneforce.sanzen.activity.myresource.StockBalanceview;

public class StockModelClass {
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

    public String getPack() {
        return Pack;
    }

    public void setPack(String pack) {
        Pack = pack;
    }

    public String getBalance_Stock() {
        return Balance_Stock;
    }

    public void setBalance_Stock(String balance_Stock) {
        Balance_Stock = balance_Stock;
    }

    String Name,Code,Pack,Balance_Stock;
    public StockModelClass(String code, String name,String Pack,String balancestock){
        this.Code=code;
        this.Name=name;
        this.Pack=Pack;
        this.Balance_Stock=balancestock;
    }
}
