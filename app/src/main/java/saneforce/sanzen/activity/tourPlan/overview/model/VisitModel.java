package saneforce.sanzen.activity.tourPlan.overview.model;

import java.util.HashSet;
import java.util.Set;

public class VisitModel extends MasterModel{
    private Set<String> dates;

    public VisitModel(String code, String name) {
        super(code, name);
        this.dates = new HashSet<>();
    }

    public Set<String> getDates() {
        if (dates == null) dates = new HashSet<>();
        return dates;
    }

    public void addDates(String date) {
        if (dates == null) {
            dates = new HashSet<>();
        }
        dates.add(date);
    }
}