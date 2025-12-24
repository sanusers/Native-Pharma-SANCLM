package saneforce.sanzen.activity.tourPlan.overview.model;

import java.util.HashMap;
import java.util.Map;

public class ClusterWiseModel extends MasterModel {
    private Map<String, String> total, planned;

    public ClusterWiseModel(String code, String name, Map<String, String> total, Map<String, String> planned) {
        super(code, name);
        this.total = total;
        this.planned = planned;
    }

    public void addTotal(MasterModel masterModel) {
        if (total == null) total = new HashMap<>();
        total.put(masterModel.getCode(), masterModel.getName());
    }

    public void addPlanned(MasterModel masterModel) {
        if (planned == null) planned = new HashMap<>();
        planned.put(masterModel.getCode(), masterModel.getName());
    }

    public Map<String, String> getTotal() {
        return total;
    }

    public Map<String, String> getPlanned() {
        return planned;
    }

    public Map<String, String> getUnplanned() {
        Map<String, String> unplanned = new HashMap<>();
        for (String drCode : total.keySet()) {
            if (!planned.containsKey(drCode)) {
                unplanned.put(drCode, total.get(drCode));
            }
        }
        return unplanned;
    }
}
