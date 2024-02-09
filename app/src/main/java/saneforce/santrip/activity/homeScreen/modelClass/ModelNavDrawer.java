package saneforce.santrip.activity.homeScreen.modelClass;

public class ModelNavDrawer
{
    Integer drawable;
    String text;
    int groupId;
    int itemId;
    int orderNo;

    public ModelNavDrawer(Integer drawable,String text) {
        this.text = text;
        this.drawable = drawable;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }


    public Integer getDrawable() {
        return drawable;
    }

    public void setDrawable(Integer drawable) {
        this.drawable = drawable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
