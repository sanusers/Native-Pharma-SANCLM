package saneforce.sanclm.activity.tourPlan.session;

import saneforce.sanclm.activity.tourPlan.model.ModelClass;

public interface SessionInterface {

    public void deleteClicked(ModelClass modelClass, int position);

    public void fieldWorkSelected (ModelClass modelClass, int position);

    public void hqChanged(ModelClass modelClass, int position,boolean changed);

    public void clusterChanged(ModelClass modelClass,int position);


}
