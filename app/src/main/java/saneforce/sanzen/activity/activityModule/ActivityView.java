package saneforce.sanzen.activity.activityModule;

import saneforce.sanzen.activity.activityModule.adapter.ActivityAdapter;
import saneforce.sanzen.activity.activityModule.model.ActivityModelClass;

public interface ActivityView {
    public void ChooseActivity(ActivityModelClass classGroup, ActivityAdapter.Viewholder holder, int position);


}
