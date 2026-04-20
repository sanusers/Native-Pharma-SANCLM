package saneforce.sanzen.activity.tourPlan.session;

import java.util.ArrayList;

import saneforce.sanzen.activity.tourPlan.model.EditModelClass;

public interface SessionItemInterface {
    void itemClicked(ArrayList<EditModelClass> list, EditModelClass item, int checkedCount);
}
