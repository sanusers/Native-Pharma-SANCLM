package saneforce.sanclm.Activities.Call.Pojo;

public class CallCommonCheckedList {
    public CallCommonCheckedList(String name) {
        this.name = name;
    }

    public CallCommonCheckedList(String name, boolean checkedItem, String category) {
        this.checkedItem = checkedItem;
        this.name = name;
        this.category = category;
    }

    public CallCommonCheckedList(String name, boolean checkedItem) {
        this.checkedItem = checkedItem;
        this.name = name;
    }

    boolean checkedItem;

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

    String name;

    String category;
}
