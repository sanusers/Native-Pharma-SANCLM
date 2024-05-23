package saneforce.sanzen.activity.tourPlan.calendar;

import saneforce.sanzen.activity.tourPlan.model.ModelClass;

public interface OnDayClickInterface {

    void onDayClicked(int position, String date, ModelClass modelClass);
}
