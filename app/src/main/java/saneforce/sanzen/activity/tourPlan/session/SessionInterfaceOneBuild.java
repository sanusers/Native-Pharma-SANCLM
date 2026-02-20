package saneforce.sanzen.activity.tourPlan.session;

import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;

public interface SessionInterfaceOneBuild {

    void deleteClickedOneBuild(OneBuildModelClass oneBuildModelClass, int position);

    void fieldWorkSelectedOneBuild (OneBuildModelClass oneBuildModelClass, int position);

    void hqChangedOneBuild(OneBuildModelClass oneBuildModelClass, int position,boolean changed);

    void clusterChangedOneBuild(OneBuildModelClass oneBuildModelClass,int position);

//    void workDayChangedOneBuild(OneBuildModelClass oneBuildModelClass, int position);

    void workDayChangedOneBuild(OneBuildModelClass oneBuildModelClass, String code, String name, int position);
}
