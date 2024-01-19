package saneforce.santrip.activity.homeScreen.modelClass;

import java.util.List;

public class GroupModelClass {
    private String groupName;
    private List<CallsModalClass> childItems;


    public GroupModelClass(String groupName, List<CallsModalClass> childItems) {
        this.groupName = groupName;
        this.childItems = childItems;
    }

    public GroupModelClass(String groupName) {
        this.groupName = groupName;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<CallsModalClass> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<CallsModalClass> childItems) {
        this.childItems = childItems;
    }
}
