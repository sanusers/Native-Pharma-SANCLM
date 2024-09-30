package saneforce.sanzen.activity.standardTourPlan.calendarScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.AddListActivity;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.CalendarAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.DocCategoryXVisitAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.DocDataAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.PlanForAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.CalendarModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DocDataModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DoctorCategoryXVisitFrequencyModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.PlanForModel;
import saneforce.sanzen.databinding.ActivityStandardTourPlanBinding;

public class StandardTourPlanActivity extends AppCompatActivity {

    private ActivityStandardTourPlanBinding activityStandardTourPlanBinding;
    private List<DoctorCategoryXVisitFrequencyModel> doctorCategoryXVisitFrequencyModelList;
    private DocCategoryXVisitAdapter docCategoryXVisitAdapter;
    private List<PlanForModel> planForModelList;
    private PlanForAdapter planForAdapter;
    private List<DocDataModel> docDataModelList;
    private DocDataAdapter docDataAdapter;
    private LinkedHashMap<String, List<CalendarModel>> calendarMap;
    private CalendarAdapter calendarAdapter;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStandardTourPlanBinding = ActivityStandardTourPlanBinding.inflate(getLayoutInflater());
        setContentView(activityStandardTourPlanBinding.getRoot());

        populateAdapters();
        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
        activityStandardTourPlanBinding.backArrow.setOnClickListener(v -> {
            super.onBackPressed();
        });
    }

    private void populateAdapters() {
        populatePlanForAdapter();
        populateDocCatXVisitAdapter();
        populateDocDataAdapter();
        populateCalendarAdapter();
    }

    private void populatePlanForAdapter() {
        planForModelList = new ArrayList<>();
        planForModelList.add(new PlanForModel("Cluster", R.drawable.tp_cluster_location_ic, 4, 2));
        planForModelList.add(new PlanForModel("Doctor", R.drawable.doctor_img, 60, 12));
        planForModelList.add(new PlanForModel("Chemist", R.drawable.chemist_img, 20, 2));

        planForAdapter = new PlanForAdapter(this, planForModelList);
        RecyclerView.LayoutManager planForLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvPlanFor.setLayoutManager(planForLayoutManager);
        activityStandardTourPlanBinding.rvPlanFor.setAdapter(planForAdapter);
    }

    private void populateDocCatXVisitAdapter() {
        doctorCategoryXVisitFrequencyModelList = new ArrayList<>();
        doctorCategoryXVisitFrequencyModelList.add(new DoctorCategoryXVisitFrequencyModel("A", 2));
        doctorCategoryXVisitFrequencyModelList.add(new DoctorCategoryXVisitFrequencyModel("B", 1));
        doctorCategoryXVisitFrequencyModelList.add(new DoctorCategoryXVisitFrequencyModel("C", 3));

        docCategoryXVisitAdapter = new DocCategoryXVisitAdapter(this, doctorCategoryXVisitFrequencyModelList);
        RecyclerView.LayoutManager docCategoryXVisitLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvDocCatXVisit.setLayoutManager(docCategoryXVisitLayoutManager);
        activityStandardTourPlanBinding.rvDocCatXVisit.setAdapter(docCategoryXVisitAdapter);
    }

    private void populateDocDataAdapter() {
        docDataModelList = new ArrayList<>();
        docDataModelList.add(new DocDataModel("A", 10, 20, 5, 4));
        docDataModelList.add(new DocDataModel("B", 12, 12, 10, 10));
        docDataModelList.add(new DocDataModel("C", 15, 15, 8, 6));

        docDataAdapter = new DocDataAdapter(this, docDataModelList);
        RecyclerView.LayoutManager docDataLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvDocData.setLayoutManager(docDataLayoutManager);
        activityStandardTourPlanBinding.rvDocData.setAdapter(docDataAdapter);
    }

    private void populateCalendarAdapter() {
        String dayIDValues = "MO1/MO2/MO3/MO4/TU1/TU2/TU3/TU4/WE1/WE2/WE4/TH1/TH2/TH3/TH4/FR1/FR2/FR3/FR4/SA1/SA2/SA3/SA4/";
        String[] dayIDs = dayIDValues.split("/");
        String[] dayCaptions = "Monday 1/Monday 2/Monday 3/Monday 4/Tuesday 1/Tuesday 2/Tuesday 3/Tuesday 4/Wednesday 1/Wednesday 2/Wednesday 4/Thursday 1/Thursday 2/Thursday 3/Thursday 4/Friday 1/Friday 2/Friday 3/Friday 4/Saturday 1/Saturday 2/Saturday 3/Saturday 4/".split("/");
        calendarMap = new LinkedHashMap<>();

        for (int index = 0; index<dayIDs.length; index++) {
            String dayID = dayIDs[index];
            if(dayID.toLowerCase().contains("mo")) {
                List<CalendarModel> calendarModelList = calendarMap.get("monday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptions[index], dayID, true, null));
                calendarMap.put("monday", calendarModelList);
            }else if(dayID.toLowerCase().contains("tu")) {
                List<CalendarModel> calendarModelList = calendarMap.get("tuesday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptions[index], dayID, true, null));
                calendarMap.put("tuesday", calendarModelList);
            }else if(dayID.toLowerCase().contains("we")) {
                List<CalendarModel> calendarModelList = calendarMap.get("wednesday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptions[index], dayID, true, null));
                calendarMap.put("wednesday", calendarModelList);
            }else if(dayID.toLowerCase().contains("th")) {
                List<CalendarModel> calendarModelList = calendarMap.get("thursday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptions[index], dayID, true, null));
                calendarMap.put("thursday", calendarModelList);
            }else if(dayID.toLowerCase().contains("fr")) {
                List<CalendarModel> calendarModelList = calendarMap.get("friday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptions[index], dayID, true, null));
                calendarMap.put("friday", calendarModelList);
            }else if(dayID.toLowerCase().contains("sa")) {
                List<CalendarModel> calendarModelList = calendarMap.get("saturday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptions[index], dayID, true, null));
                calendarMap.put("saturday", calendarModelList);
            }else if(dayID.toLowerCase().contains("su")) {
                List<CalendarModel> calendarModelList = calendarMap.get("sunday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptions[index], dayID, true, null));
                calendarMap.put("sunday", calendarModelList);
            }
        }

        for (String key : calendarMap.keySet()) {
            if(key.equalsIgnoreCase("monday")) {
                List<CalendarModel> calendarModelList = calendarMap.get("monday");
                if(calendarModelList != null && !calendarModelList.isEmpty()) {
                    String caption = "";
                    for (int index = 1; index<5; index++) {
                        String dayID = "MO" + index;
                        boolean isDayFound = false;
                        for (CalendarModel calendarModel : calendarModelList) {
                            if(dayID.equalsIgnoreCase(calendarModel.getId())) {
                                isDayFound = true;
                                if(caption.isEmpty()) {
                                    caption = calendarModel.getCaption();
                                    caption = caption.substring(0, caption.length() - 1);
                                }
                                break;
                            }
                        }
                        if(!isDayFound) {
                            calendarModelList.add(index-1, new CalendarModel((caption + index), dayID, false, null));
                        }
                        if(index == 1) {
                            List<DCRModel> dcrModelList = new ArrayList<>();
                            dcrModelList.add(new DCRModel(R.drawable.doctor_img, 1, 10));
                            dcrModelList.add(new DCRModel(R.drawable.chemist_img, 2, 8));
                            calendarModelList.get(0).setDcrModelList(dcrModelList);
                        }
                    }
                    calendarMap.put("monday", calendarModelList);
                }
            } else if(key.equalsIgnoreCase("tuesday")) {
                List<CalendarModel> calendarModelList = calendarMap.get("tuesday");
                if(calendarModelList != null && !calendarModelList.isEmpty()) {
                    String caption = "";
                    for (int index = 1; index<5; index++) {
                        String dayID = "TU" + index;
                        boolean isDayFound = false;
                        for (CalendarModel calendarModel : calendarModelList) {
                            if(dayID.equalsIgnoreCase(calendarModel.getId())) {
                                isDayFound = true;
                                if(caption.isEmpty()) {
                                    caption = calendarModel.getCaption();
                                    caption = caption.substring(0, caption.length() - 1);
                                }
                                break;
                            }
                        }
                        if(!isDayFound) {
                            calendarModelList.add(index-1, new CalendarModel((caption + index), dayID, false, null));
                        }
                    }
                    calendarMap.put("tuesday", calendarModelList);
                }
            } else if(key.equalsIgnoreCase("wednesday")) {
                List<CalendarModel> calendarModelList = calendarMap.get("wednesday");
                if(calendarModelList != null && !calendarModelList.isEmpty()) {
                    String caption = "";
                    for (int index = 1; index<5; index++) {
                        String dayID = "WE" + index;
                        boolean isDayFound = false;
                        for (CalendarModel calendarModel : calendarModelList) {
                            if(dayID.equalsIgnoreCase(calendarModel.getId())) {
                                isDayFound = true;
                                if(caption.isEmpty()) {
                                    caption = calendarModel.getCaption();
                                    caption = caption.substring(0, caption.length() - 1);
                                }
                                break;
                            }
                        }
                        if(!isDayFound) {
                            calendarModelList.add(index-1, new CalendarModel((caption + index), dayID, false, null));
                        }
                    }
                    calendarMap.put("wednesday", calendarModelList);
                }
            } else if(key.equalsIgnoreCase("thursday")) {
                List<CalendarModel> calendarModelList = calendarMap.get("thursday");
                if(calendarModelList != null && !calendarModelList.isEmpty()) {
                    String caption = "";
                    for (int index = 1; index<5; index++) {
                        String dayID = "TH" + index;
                        boolean isDayFound = false;
                        for (CalendarModel calendarModel : calendarModelList) {
                            if(dayID.equalsIgnoreCase(calendarModel.getId())) {
                                isDayFound = true;
                                if(caption.isEmpty()) {
                                    caption = calendarModel.getCaption();
                                    caption = caption.substring(0, caption.length() - 1);
                                }
                                break;
                            }
                        }
                        if(!isDayFound) {
                            calendarModelList.add(index-1, new CalendarModel((caption + index), dayID, false, null));
                        }
                        if(index == 2) {
                            List<DCRModel> dcrModelList = new ArrayList<>();
                            dcrModelList.add(new DCRModel(R.drawable.doctor_img, 1, 15));
                            dcrModelList.add(new DCRModel(R.drawable.chemist_img, 2, 12));
                            dcrModelList.add(new DCRModel(R.drawable.map_stockist_img, 3, 3));
                            calendarModelList.get(1).setDcrModelList(dcrModelList);
                        }
                    }
                    calendarMap.put("thursday", calendarModelList);
                }
            } else if(key.equalsIgnoreCase("friday")) {
                List<CalendarModel> calendarModelList = calendarMap.get("friday");
                if(calendarModelList != null && !calendarModelList.isEmpty()) {
                    String caption = "";
                    for (int index = 1; index<5; index++) {
                        String dayID = "FR" + index;
                        boolean isDayFound = false;
                        for (CalendarModel calendarModel : calendarModelList) {
                            if(dayID.equalsIgnoreCase(calendarModel.getId())) {
                                isDayFound = true;
                                if(caption.isEmpty()) {
                                    caption = calendarModel.getCaption();
                                    caption = caption.substring(0, caption.length() - 1);
                                }
                                break;
                            }
                        }
                        if(!isDayFound) {
                            calendarModelList.add(index-1, new CalendarModel((caption + index), dayID, false, null));
                        }
                    }
                    calendarMap.put("friday", calendarModelList);
                }
            } else if(key.equalsIgnoreCase("saturday")) {
                List<CalendarModel> calendarModelList = calendarMap.get("saturday");
                if(calendarModelList != null && !calendarModelList.isEmpty()) {
                    String caption = "";
                    for (int index = 1; index<5; index++) {
                        String dayID = "SA" + index;
                        boolean isDayFound = false;
                        for (CalendarModel calendarModel : calendarModelList) {
                            if(dayID.equalsIgnoreCase(calendarModel.getId())) {
                                isDayFound = true;
                                if(caption.isEmpty()) {
                                    caption = calendarModel.getCaption();
                                    caption = caption.substring(0, caption.length() - 1);
                                }
                                break;
                            }
                        }
                        if(!isDayFound) {
                            calendarModelList.add(index-1, new CalendarModel((caption + index), dayID, false, null));
                        }
                    }
                    calendarMap.put("saturday", calendarModelList);
                }
            } else if(key.equalsIgnoreCase("sunday")) {
                List<CalendarModel> calendarModelList = calendarMap.get("sunday");
                if(calendarModelList != null && !calendarModelList.isEmpty()) {
                    String caption = "";
                    for (int index = 1; index<5; index++) {
                        String dayID = "SU" + index;
                        boolean isDayFound = false;
                        for (CalendarModel calendarModel : calendarModelList) {
                            if(dayID.equalsIgnoreCase(calendarModel.getId())) {
                                isDayFound = true;
                                if(caption.isEmpty()) {
                                    caption = calendarModel.getCaption();
                                    caption = caption.substring(0, caption.length() - 1);
                                }
                                break;
                            }
                        }
                        if(!isDayFound) {
                            calendarModelList.add(index-1, new CalendarModel((caption + index), dayID, false, null));
                        }
                    }
                    calendarMap.put("sunday", calendarModelList);
                }
            }
        }

        calendarAdapter = new CalendarAdapter(this, calendarMap, new ArrayList<>(calendarMap.keySet()), calendarDayClickListener);
        RecyclerView.LayoutManager calendarLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvCalendar.setLayoutManager(calendarLayoutManager);
        activityStandardTourPlanBinding.rvCalendar.setAdapter(calendarAdapter);
    }

    private CalendarAdapter.CalendarDayClickListener calendarDayClickListener = new CalendarAdapter.CalendarDayClickListener() {
        @Override
        public void onClick(CalendarModel calendarModel) {
            startActivity(new Intent(StandardTourPlanActivity.this, AddListActivity.class));
        }
    };

}