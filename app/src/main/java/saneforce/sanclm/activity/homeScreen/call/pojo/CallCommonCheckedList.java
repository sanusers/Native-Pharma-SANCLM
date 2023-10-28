package saneforce.sanclm.activity.homeScreen.call.pojo;

public class CallCommonCheckedList {
    boolean checkedItem;
    String name;
    String category;
    String code;
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

    public CallCommonCheckedList(String name, String code, boolean checkedItem, String category) {
        this.checkedItem = checkedItem;
        this.name = name;
        this.code = code;
        this.category = category;
    }

    public CallCommonCheckedList(String name, String code,boolean checkedItem) {
        this.checkedItem = checkedItem;
        this.code = code;
        this.name = name;
    }

    public CallCommonCheckedList(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public CallCommonCheckedList(String name, String code, String town_name, String town_code, boolean checkedItem) {
        this.checkedItem = checkedItem;
        this.code = code;
        this.name = name;
        this.town_code = town_code;
        this.town_name = town_name;
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
}
