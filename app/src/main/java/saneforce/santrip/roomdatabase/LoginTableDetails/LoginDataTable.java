package saneforce.santrip.roomdatabase.LoginTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import saneforce.santrip.response.LoginResponse;

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
    public LoginResponse getLoginResponse() {
        LoginResponse loginResponse = new LoginResponse();
        if(!loginData.equals("")) {
            Type type = new TypeToken<LoginResponse>() {
            }.getType();
            loginResponse = new Gson().fromJson(loginData, type);
            return loginResponse;
        }
        return loginResponse;
    }
}
