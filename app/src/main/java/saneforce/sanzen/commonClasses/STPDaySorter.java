package saneforce.sanzen.commonClasses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.activity.approvals.stp.model.STPDetailedModel;
import saneforce.sanzen.activity.approvals.stp.model.STPModelList;
import saneforce.sanzen.activity.tourPlan.model.EditModelClass;

public class STPDaySorter {

    private final Map<String, Integer> DAY_OF_WEEK_MAP = new HashMap<>();

    public STPDaySorter() {
        List<String> DAYS_OF_WEEK = Arrays.asList("MO", "TU", "WE", "TH", "FR", "SA", "SU");
        for (int i = 0; i<DAYS_OF_WEEK.size(); i++) {
            DAY_OF_WEEK_MAP.put(DAYS_OF_WEEK.get(i), i + 1);
        }
    }

    public void sortDaysWithDetails(List<STPDetailedModel> unsortedData) {
        unsortedData.sort((obj1, obj2) -> {
            String day1 = obj1.getDayPlanShortName();
            String day2 = obj2.getDayPlanShortName();

            String[] day1Parts = {day1.substring(0, day1.length()-1), day1.substring(day1.length()-1)};
            String[] day2Parts = {day2.substring(0, day2.length()-1), day2.substring(day2.length()-1)};

            String day1Name = day1Parts[0];
            String day2Name = day2Parts[0];

            int day1Order = DAY_OF_WEEK_MAP.getOrDefault(day1Name, 0);
            int day2Order = DAY_OF_WEEK_MAP.getOrDefault(day2Name, 0);

            if(day1Order != day2Order) {
                return Integer.compare(day1Order, day2Order);
            }else {
                int day1Suffix = Integer.parseInt(day1Parts[1]);
                int day2Suffix = Integer.parseInt(day2Parts[1]);
                return Integer.compare(day1Suffix, day2Suffix);
            }
        });
    }

    public void sortDays(List<STPModelList> unsortedData) {
        unsortedData.sort((obj1, obj2) -> {
            String day1 = obj1.getCode();
            String day2 = obj2.getCode();

            String[] day1Parts = {day1.substring(0, day1.length()-1), day1.substring(day1.length()-1)};
            String[] day2Parts = {day2.substring(0, day2.length()-1), day2.substring(day2.length()-1)};

            String day1Name = day1Parts[0];
            String day2Name = day2Parts[0];

            int day1Order = DAY_OF_WEEK_MAP.getOrDefault(day1Name, 0);
            int day2Order = DAY_OF_WEEK_MAP.getOrDefault(day2Name, 0);

            if(day1Order != day2Order) {
                return Integer.compare(day1Order, day2Order);
            }else {
                int day1Suffix = Integer.parseInt(day1Parts[1]);
                int day2Suffix = Integer.parseInt(day2Parts[1]);
                return Integer.compare(day1Suffix, day2Suffix);
            }
        });
    }

    public void sortDaysTP(List<EditModelClass> unsortedData) {
        unsortedData.sort((obj1, obj2) -> {
            String day1 = obj1.getCode();
            String day2 = obj2.getCode();

            String[] day1Parts = {day1.substring(0, day1.length()-1), day1.substring(day1.length()-1)};
            String[] day2Parts = {day2.substring(0, day2.length()-1), day2.substring(day2.length()-1)};

            String day1Name = day1Parts[0];
            String day2Name = day2Parts[0];

            int day1Order = DAY_OF_WEEK_MAP.getOrDefault(day1Name, 0);
            int day2Order = DAY_OF_WEEK_MAP.getOrDefault(day2Name, 0);

            if(day1Order != day2Order) {
                return Integer.compare(day1Order, day2Order);
            }else {
                int day1Suffix = Integer.parseInt(day1Parts[1]);
                int day2Suffix = Integer.parseInt(day2Parts[1]);
                return Integer.compare(day1Suffix, day2Suffix);
            }
        });
    }
}
