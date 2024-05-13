package saneforce.santrip.roomdatabase.LoginTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "login_table")
public class LoginDataTable {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "login_data")
    private String loginData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = 0;
    }

    public String getLoginData() {
        return loginData;
    }

    public void setLoginData(String loginData) {
        this.loginData = loginData;
    }

    public LoginDataTable(){}

    @Ignore
    public LoginDataTable(String loginData){
        this.loginData = loginData;
    }

}
