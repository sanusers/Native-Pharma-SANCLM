package saneforce.santrip.activity.homeScreen.modelClass;

import java.util.ArrayList;
import java.util.List;

public class ChildListModelClass {
    private String childName;
    private String groupName;
    private int childId;
    private ArrayList<OutBoxCallList> outBoxCallLists;
    private ArrayList<EcModelClass> ecModelClasses;

    public ArrayList<EcModelClass> getEcModelClasses() {
        return ecModelClasses;
    }

    public void setEcModelClasses(ArrayList<EcModelClass> ecModelClasses) {
        this.ecModelClasses = ecModelClasses;
    }

    private boolean isAvailableList;
    private boolean isExpanded;
    private int Counts;
    private String otherContents;

    public String getOtherContents() {
        return otherContents;
    }

    public void setOtherContents(String otherContents) {
        this.otherContents = otherContents;
    }

    public int getCounts() {
        return Counts;
    }

    public void setCounts(int counts) {
        Counts = counts;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isAvailableList() {
        return isAvailableList;
    }

    public void setAvailableList(boolean availableList) {
        isAvailableList = availableList;
    }

    public ArrayList<OutBoxCallList> getOutBoxCallLists() {
        return outBoxCallLists;
    }

    public void setOutBoxCallLists(ArrayList<OutBoxCallList> outBoxCallLists) {
        this.outBoxCallLists = outBoxCallLists;
    }

    public ChildListModelClass(String childName, int childId, boolean isExpanded, boolean isAvailableList, ArrayList<OutBoxCallList> outBoxCallLists, String dummy) {
        this.childName = childName;
        this.childId = childId;
        this.isExpanded = isExpanded;
        this.isAvailableList = isAvailableList;
        this.outBoxCallLists = outBoxCallLists;
    }

    public ChildListModelClass(String childName, int childId, boolean isExpanded, boolean isAvailableList, ArrayList<EcModelClass> ecModelClasses) {
        this.childName = childName;
        this.childId = childId;
        this.isExpanded = isExpanded;
        this.isAvailableList = isAvailableList;
        this.ecModelClasses = ecModelClasses;
    }

    public ChildListModelClass(String childName, int childId, boolean isAvailableList, String otherContents) {
        this.childName = childName;
        this.childId = childId;
        this.isAvailableList = isAvailableList;
        this.otherContents = otherContents;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public ChildListModelClass(String childName, int childId) {
        this.childName = childName;
        this.childId = childId;
    }
}
