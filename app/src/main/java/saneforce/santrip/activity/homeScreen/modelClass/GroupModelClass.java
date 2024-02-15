package saneforce.santrip.activity.homeScreen.modelClass;

import java.util.ArrayList;
import java.util.List;

public class GroupModelClass {
    private String groupName;
    private boolean isExpanded;
    private int synced;

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    private ArrayList<ChildListModelClass> childItems;

    public ArrayList<ChildListModelClass> getChildItems() {
        return childItems;
    }

    public void setChildItems(ArrayList<ChildListModelClass> childItems) {
        this.childItems = childItems;
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupModelClass(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



    public GroupModelClass(String groupName, ArrayList<ChildListModelClass> childItems, boolean isExpanded,int synced) {
        this.groupName = groupName;
        this.childItems = childItems;
        this.isExpanded = isExpanded;
        this.synced = synced;
    }

    public GroupModelClass() {
    }

}
