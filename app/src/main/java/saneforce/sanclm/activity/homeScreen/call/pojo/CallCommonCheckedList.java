package saneforce.sanclm.activity.homeScreen.call.pojo;

public class CallCommonCheckedList {
    boolean checkedItem;
    String name;
    String category;
    String code;

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
