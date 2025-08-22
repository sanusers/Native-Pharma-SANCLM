package saneforce.sanzen.activity.tourPlan.model;

import java.util.ArrayList;

public class MultiHQHeaderModelClass {
    private final String name, code;
    private boolean isExpanded;
    private ArrayList<MultiHQItemModelClass> itemsList;

    public MultiHQHeaderModelClass(String name, String code, ArrayList<MultiHQItemModelClass> itemsList, boolean isExpanded) {
        this.name = name;
        this.code = code;
        this.itemsList = itemsList;
        this.isExpanded = isExpanded;
    }

    public MultiHQHeaderModelClass(MultiHQHeaderModelClass multiHQHeaderModelClass){
        this.name = multiHQHeaderModelClass.name;
        this.code = multiHQHeaderModelClass.code;
        ArrayList<MultiHQItemModelClass> itemsList = new ArrayList<>();
        for (MultiHQItemModelClass multiHQItemModelClass : multiHQHeaderModelClass.getItemsList()) {
            itemsList.add(new MultiHQItemModelClass(multiHQItemModelClass));
        }
        this.itemsList = itemsList;
        this.isExpanded = multiHQHeaderModelClass.isExpanded;
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

    public ArrayList<MultiHQItemModelClass> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<MultiHQItemModelClass> itemsList) {
        this.itemsList = itemsList;
    }
}
