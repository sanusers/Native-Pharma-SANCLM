package saneforce.sanzen.activity.reports.dayReport.model;

import com.google.gson.JsonArray;

public class MenuModel {
    public String Menu_Name;
    public String Menu_Icon;
    JsonArray Menu_Sub_Details;

    public MenuModel(String menu_Name,String menu_Icon,JsonArray menu_Sub_Detail) {
        this.Menu_Name = menu_Name;
        this.Menu_Icon = menu_Icon;
        this.Menu_Sub_Details = menu_Sub_Detail;
    }

    public JsonArray getMenu_Sub_Details() {
        return Menu_Sub_Details;
    }

    public void setMenu_Sub_Details(JsonArray menu_Sub_Details) {
        Menu_Sub_Details = menu_Sub_Details;
    }

    public String getMenu_Icon() {
        return Menu_Icon;
    }
    public void setMenu_Icon(String menu_Icon){
        Menu_Icon = menu_Icon;
    }

    public String getMenu_Name() {
        return Menu_Name;
    }
    public void setMenu_Name(String menu_Name){
        Menu_Name = menu_Name;
    }
}
