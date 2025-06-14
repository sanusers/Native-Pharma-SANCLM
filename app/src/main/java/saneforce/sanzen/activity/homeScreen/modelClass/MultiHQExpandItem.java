package saneforce.sanzen.activity.homeScreen.modelClass;

import java.util.ArrayList;

public class MultiHQExpandItem {
    private final String name, code;
    private boolean isExpanded;
    private final ArrayList<MultiHQClusterItem> clusterList;

    public MultiHQExpandItem(String name, String code, ArrayList<MultiHQClusterItem> clusterList, boolean isExpanded) {
        this.name = name;
        this.code = code;
        this.clusterList = clusterList;
        this.isExpanded = isExpanded;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public ArrayList<MultiHQClusterItem> getClusterList() {
        return clusterList;
    }
}
