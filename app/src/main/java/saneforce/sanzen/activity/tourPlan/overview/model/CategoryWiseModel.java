package saneforce.sanzen.activity.tourPlan.overview.model;

import java.util.HashMap;
import java.util.Map;

public class CategoryWiseModel extends MasterModel {
    private int frequency = 0;
    private Map<String, String> totalDoctors, plannedDoctors;
    private Map<String, VisitModel> plannedVisit;

    public CategoryWiseModel(String code, String name, String frequency) {
        super(code, name);
        try {
            totalDoctors = new HashMap<>();
            plannedDoctors = new HashMap<>();
            plannedVisit = new HashMap<>();
            this.frequency = Integer.parseInt(frequency);
        } catch (Exception e) {
            e.printStackTrace();
            this.frequency = 0;
        }
    }

    public void addTotalDoctors(MasterModel masterModel) {
        if (totalDoctors == null) totalDoctors = new HashMap<>();
        totalDoctors.put(masterModel.getCode(), masterModel.getName());
    }

    public void addPlanned(MasterModel masterModel, String date) {
        if (plannedDoctors == null) plannedDoctors = new HashMap<>();
        plannedDoctors.put(masterModel.getCode(), masterModel.getName());
        if (plannedVisit == null) plannedVisit = new HashMap<>();
        VisitModel visitModel = plannedVisit.get(masterModel.getCode());
        if (visitModel == null) visitModel = new VisitModel(masterModel.getCode(), masterModel.getName());
        visitModel.addDates(date);
        plannedVisit.put(masterModel.getCode(), visitModel);
    }

    public int getFrequency() {
        return frequency;
    }

    public Map<String, String> getTotalDoctors() {
        return totalDoctors;
    }

    public Map<String, String> getPlannedDoctors() {
        return plannedDoctors;
    }

    public Map<String, VisitModel> getPlannedVisit() {
        return plannedVisit;
    }

    public Map<String, String> getUnplanned() {
        Map<String, String> unplanned = new HashMap<>();
        for (String drCode : totalDoctors.keySet()) {
            if (!plannedDoctors.containsKey(drCode)) {
                unplanned.put(drCode, totalDoctors.get(drCode));
            }
        }
        return unplanned;
    }

}
