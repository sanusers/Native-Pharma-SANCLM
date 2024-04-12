package saneforce.santrip.activity.tourPlan.session;

import saneforce.santrip.activity.tourPlan.model.ModelClass;

public interface SessionInterface {

    void deleteClicked(ModelClass modelClass, int position);

    void fieldWorkSelected (ModelClass modelClass, int position);

    void hqChanged(ModelClass modelClass, int position,boolean changed);

    void clusterChanged(ModelClass modelClass,int position);


}
