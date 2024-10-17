package saneforce.sanzen.activity.standardTourPlan.calendarScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.AddListActivity;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.CalendarAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.DocCategoryXVisitAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.DocDataAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.PlanForAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.CalendarModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DocCategoryModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DocDataModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DoctorCategoryXVisitFrequencyModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.PlanForModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.SelectedDCRModel;
import saneforce.sanzen.activity.standardTourPlan.unplannedVisitScreen.UnplannedVisitActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.databinding.ActivityStandardTourPlanBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;

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
    private HashMap<String, DocCategoryModel> docCategoryModelMap;
    private HashMap<String, List<String>> allSelectedDocXCatMap;
    private List<String> allSelectedDocList;
    private Set<String> totalCategoryCodeList, totalClusterCodeList, totalDocCodeList, totalChmCodeList, totalStkCodeList, totalUnDrCodeList, totalCipCodeList, totalHosCodeList;
    private Set<String> selectedCategoryCodeList, selectedClusterCodeList, selectedDocCodeList, selectedChmCodeList, selectedStkCodeList, selectedUnDrCodeList, selectedCipCodeList, selectedHosCodeList;
    private String hqCode, drCap, chmCap, stkCap, unDrCap, cipCap, hosCap, clusterCap, stpCap, drNeed, chmNeed, stkNeed, unDrNeed, cipNeed, hosNeed, dayCaptions, dayIDs;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private GPSTrack gpsTrack;
    private CommonUtilsMethods commonUtilsMethods;
    public static HashMap<String, List<DCRModel>> selectedDcrMap;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(selectedDcrMap != null && checkAllDocsSelected()) {
            activityStandardTourPlanBinding.sendToApproval.setEnabled(true);
        } else{
            activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStandardTourPlanBinding = ActivityStandardTourPlanBinding.inflate(getLayoutInflater());
        setContentView(activityStandardTourPlanBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getRequiredData();
        populateAdapters();
        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
        activityStandardTourPlanBinding.backArrow.setOnClickListener(v -> {
            super.onBackPressed();
        });

        activityStandardTourPlanBinding.checkUnplannedVisits.setOnClickListener( view -> startActivity(new Intent(StandardTourPlanActivity.this, UnplannedVisitActivity.class)));

    }

    private void getRequiredData() {
        hqCode = SharedPref.getHqCode(this);
        drCap = SharedPref.getDrCap(this);
        chmCap = SharedPref.getChmCap(this);
        stkCap = SharedPref.getStkCap(this);
        unDrCap = SharedPref.getUNLcap(this);
        cipCap = SharedPref.getCipCaption(this);
        hosCap = SharedPref.getHospCaption(this);
        clusterCap = SharedPref.getClusterCap(this);
        drNeed = SharedPref.getDrNeed(this);
        chmNeed = SharedPref.getChmNeed(this);
        stkNeed = SharedPref.getStkNeed(this);
        unDrNeed = SharedPref.getUnlNeed(this);
        cipNeed = SharedPref.getCipNeed(this);
        hosNeed = SharedPref.getHospNeed(this);
//        stpCap = SharedPref.getSTPCap(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        selectedCategoryCodeList = new HashSet<>();
        selectedClusterCodeList = new HashSet<>();
        selectedDocCodeList = new HashSet<>();
        selectedChmCodeList = new HashSet<>();
        selectedStkCodeList = new HashSet<>();
        selectedUnDrCodeList = new HashSet<>();
        selectedCipCodeList = new HashSet<>();
        selectedHosCodeList = new HashSet<>();
        selectedDcrMap = new HashMap<>();
        allSelectedDocXCatMap = new HashMap<>();
        allSelectedDocList = new ArrayList<>();

        getData();
    }

    private void getData() {
        saveSTPDataToLocal();
        getLocalSTPData();
        getClusterData();
        getCategoryData();
        getDcrData();
        getSTPSetup();
    }

    private void populateAdapters() {
        populatePlanForAdapter();
        populateDocCatXVisitAdapter();
        populateDocDataAdapter();
        populateCalendarAdapter();
    }

    private void getCategoryData() {
        docCategoryModelMap = new HashMap<>();
        totalCategoryCodeList = new HashSet<>();
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
            if(jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("Code");
                    if(!code.isEmpty() && !totalCategoryCodeList.contains(code)) {
                        totalCategoryCodeList.add(code);
                        String name = (jsonObject.getString("Name"));
                        String docCatName = (jsonObject.getString("Doc_Cat_Name"));
                        String visitFrequency = (jsonObject.getString("No_of_visit"));
                        docCategoryModelMap.put(code, new DocCategoryModel(Integer.parseInt(code), name, docCatName, Integer.parseInt(visitFrequency), 0));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClusterData() {
        totalClusterCodeList = new HashSet<>();
        try {
            JSONArray jsonculst = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            if(jsonculst.length()>0) {
                for (int i = 0; i<jsonculst.length(); i++) {
                    JSONObject jsonObject = jsonculst.getJSONObject(i);
                    String clusterCode = jsonObject.getString("Code");
                    if(!clusterCode.isEmpty() && !totalClusterCodeList.contains(clusterCode)) {
                        totalClusterCodeList.add(clusterCode);
                        String custom_name = (jsonObject.getString("Name"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSTPDataToLocal() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
            if(jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dayID = jsonObject.getString("Day_Plan_ShortName");
                    String dayCaption = jsonObject.getString("Day_Plan_Name");
                    String dayPlanCode = jsonObject.getString("Day_Plan_Code");
                    String clusterCode = jsonObject.getString("Patch_Code");
                    String clusterName = jsonObject.getString("Patch_Name");
                    String doctorCode = jsonObject.getString("Dr_Code");
                    String doctorName = jsonObject.getString("Dr_Name");
                    String chemistCode = jsonObject.getString("Chem_Code");
                    String chemistName = jsonObject.getString("Chem_Name");
                    String dateTime = jsonObject.getString("Created_Date");
                    String activeFlag = jsonObject.getString("Active_Flag");
                    Log.d("STP master data", "saveSTPDataToLocal: " + jsonObject);

                    JSONObject jsonSave = new JSONObject();
                    jsonSave = CommonUtilsMethods.CommonObjectParameter(this);
                    jsonSave.put("sfcode", SharedPref.getSfCode(this));
                    jsonSave.put("DivCode", SharedPref.getDivisionCode(this));
                    jsonSave.put("Rsf", SharedPref.getHqCode(this));
                    jsonSave.put("town_code", clusterCode);
                    jsonSave.put("town_name", clusterName);
                    jsonSave.put("Doctor_Id", doctorCode);
                    jsonSave.put("Doctor_Name", doctorName);
                    jsonSave.put("Chemist_Id", chemistCode);
                    jsonSave.put("Chemist_Name", chemistName);
                    jsonSave.put("Plan_Name", dayCaption);
                    jsonSave.put("Plan_SName", dayID);
                    jsonSave.put("Plan_Code", dayPlanCode);
                    jsonSave.put("StpFlag", activeFlag);
                    jsonSave.put("tableName", "save_stp");
                    Log.d("STP save data", "saveSTPDataToLocal: " + jsonSave);
                    jsonSave.put("ReqDt", dateTime);
                    stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, jsonObject.toString(), "0"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocalSTPData() {
        List<STPOfflineDataTable> stpOfflineDataTableList = stpOfflineDataDao.getAllSTPData();
        for (STPOfflineDataTable stpOfflineDataTable : stpOfflineDataTableList) {
            String clusterCode = stpOfflineDataTable.getClusterCode();
            String doctorCode = stpOfflineDataTable.getDoctorCode();
            String chemistCode = stpOfflineDataTable.getChemistCode();
            selectedClusterCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(clusterCode)).split(",")));
            selectedDocCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(doctorCode)).split(",")));
            allSelectedDocList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(doctorCode)).split(",")));
            selectedChmCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(chemistCode)).split(",")));
        }
    }

    private void getDcrData() {
        totalDocCodeList = new HashSet<>();
        totalChmCodeList = new HashSet<>();
        totalStkCodeList = new HashSet<>();
        totalUnDrCodeList = new HashSet<>();
        totalCipCodeList = new HashSet<>();
        totalHosCodeList = new HashSet<>();

        try {
            List<String> dcrNameList = new ArrayList<>();
            if(drNeed.equalsIgnoreCase("0")) {
                dcrNameList.add(Constants.DOCTOR);
            }
            if(chmNeed.equalsIgnoreCase("0")) {
                dcrNameList.add(Constants.CHEMIST);
            }
            if(stkNeed.equalsIgnoreCase("0")) {
//                dcrNameList.add(Constants.STOCKIEST);
            }
            if(unDrNeed.equalsIgnoreCase("0")) {
//                dcrNameList.add(Constants.UNLISTED_DOCTOR);
            }
            if(cipNeed.equalsIgnoreCase("0")) {
//            dcrNameList.add(Constants.CIP);
            }
            if(hosNeed.equalsIgnoreCase("0")) {
//            dcrNameList.add(Constants.HOSPITAL);
            }

//            HashMap<String, String> chmCatMap = new HashMap<>();
//            JSONArray jsonChmCatArray = masterDataDao.getMasterDataTableOrNew("ChemistCategory").getMasterSyncDataJsonArray();
//            for (int i = 0; i<jsonChmCatArray.length(); i++) {
//                JSONObject jsonObject = jsonChmCatArray.getJSONObject(i);
//                try {
//                    String code = jsonObject.optString("Code");
//                    String name = jsonObject.optString("Name");
//                    chmCatMap.put(code, name);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

            for (String dcrName : dcrNameList) {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(dcrName + hqCode).getMasterSyncDataJsonArray();
                if(jsonArray.length()>0) {
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        try {
                            String code = "-", name = "-", category = "-", categoryCode = "-", speciality = "-", townName = "-", townCode = "-";
                            int visitCount = 0;

                            code = jsonObject.optString("Code");
                            name = jsonObject.optString("Name");
                            townName = jsonObject.optString("Town_Name");
                            townCode = jsonObject.optString("Town_Code");
                            if(dcrName.equalsIgnoreCase(Constants.DOCTOR)
//                                    || dcrName.equalsIgnoreCase(Constants.UNLISTED_DOCTOR)
                            ) {
                                category = jsonObject.optString("Category");
                                categoryCode = jsonObject.optString("CategoryCode");
                                speciality = jsonObject.optString("Specialty");
                                if(dcrName.equalsIgnoreCase(Constants.DOCTOR)) {
                                    String vstCount = jsonObject.optString("Tlvst");
                                    if(!vstCount.equalsIgnoreCase("null") && !vstCount.isEmpty()) {
                                        visitCount = Integer.parseInt(vstCount);
                                    }
                                }
                                if(allSelectedDocList.contains(code)) {
                                    if(!allSelectedDocXCatMap.containsKey(categoryCode)) {
                                        allSelectedDocXCatMap.put(categoryCode, new ArrayList<>());
                                    }
                                    List<String> docCodes = allSelectedDocXCatMap.get(categoryCode);
                                    if(docCodes == null) {
                                        docCodes = new ArrayList<>();
                                    }
                                    int docSelectedFreq = Collections.frequency(allSelectedDocList, code);
                                    docCodes.addAll(Collections.nCopies(docSelectedFreq, code));
                                    allSelectedDocXCatMap.put(categoryCode, docCodes);
                                }
                            }
//                            else if(dcrName.equalsIgnoreCase(Constants.CHEMIST)) {
//                                categoryCode = jsonObject.optString("Chm_cat");
//                                category = chmCatMap.getOrDefault(categoryCode, "");
//                            }
                            if(!code.isEmpty()) {
                                switch (dcrName){
                                    case Constants.DOCTOR:
                                        if(!totalDocCodeList.contains(code)) {
                                            totalDocCodeList.add(code);
                                            if(docCategoryModelMap.containsKey(categoryCode)) {
                                                DocCategoryModel docCategoryModel = docCategoryModelMap.get(categoryCode);
                                                if(docCategoryModel != null) {
                                                    docCategoryModel.incrementDocCount();
                                                    docCategoryModelMap.put(categoryCode, docCategoryModel);
                                                }
                                            }
                                            if(!selectedDcrMap.containsKey(Constants.DOCTOR)) {
                                                selectedDcrMap.put(Constants.DOCTOR, new ArrayList<>());
                                            }
                                            List<DCRModel> docModelList = selectedDcrMap.get(Constants.DOCTOR);
                                            if(docModelList == null) {
                                                docModelList = new ArrayList<>();
                                            }
                                            docModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                            selectedDcrMap.put(Constants.DOCTOR, docModelList);
                                        }
                                        break;
                                    case Constants.CHEMIST:
                                        if(!totalChmCodeList.contains(code)) {
                                            totalChmCodeList.add(code);
                                            if(!selectedDcrMap.containsKey(Constants.CHEMIST)) {
                                                selectedDcrMap.put(Constants.CHEMIST, new ArrayList<>());
                                            }
                                            List<DCRModel> chmModelList = selectedDcrMap.get(Constants.CHEMIST);
                                            if(chmModelList == null) {
                                                chmModelList = new ArrayList<>();
                                            }
                                            chmModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                            selectedDcrMap.put(Constants.CHEMIST, chmModelList);
                                        }
                                        break;
                                    case Constants.STOCKIEST:
                                        if(!totalStkCodeList.contains(code)) {
                                            totalStkCodeList.add(code);
                                            if(!selectedDcrMap.containsKey(Constants.STOCKIEST)) {
                                                selectedDcrMap.put(Constants.STOCKIEST, new ArrayList<>());
                                            }
                                            List<DCRModel> stkModelList = selectedDcrMap.get(Constants.STOCKIEST);
                                            if(stkModelList == null) {
                                                stkModelList = new ArrayList<>();
                                            }
                                            stkModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                            selectedDcrMap.put(Constants.STOCKIEST, stkModelList);
                                        }
                                        break;
                                    case Constants.UNLISTED_DOCTOR:
                                        if(!totalUnDrCodeList.contains(code)) {
                                            totalUnDrCodeList.add(code);
                                            if(!selectedDcrMap.containsKey(Constants.UNLISTED_DOCTOR)) {
                                                selectedDcrMap.put(Constants.UNLISTED_DOCTOR, new ArrayList<>());
                                            }
                                            List<DCRModel> unDrModelList = selectedDcrMap.get(Constants.UNLISTED_DOCTOR);
                                            if(unDrModelList == null) {
                                                unDrModelList = new ArrayList<>();
                                            }
                                            unDrModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                            selectedDcrMap.put(Constants.UNLISTED_DOCTOR, unDrModelList);
                                        }
                                        break;
                                    case Constants.CIP:
                                        if(!totalCipCodeList.contains(code)) {
                                            totalCipCodeList.add(code);
                                            if(!selectedDcrMap.containsKey(Constants.CIP)) {
                                                selectedDcrMap.put(Constants.CIP, new ArrayList<>());
                                            }
                                            List<DCRModel> cipModelList = selectedDcrMap.get(Constants.CIP);
                                            if(cipModelList == null) {
                                                cipModelList = new ArrayList<>();
                                            }
                                            cipModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                            selectedDcrMap.put(Constants.CIP, cipModelList);
                                        }
                                        break;
                                    case Constants.HOSPITAL:
                                        if(!totalHosCodeList.contains(code)) {
                                            totalHosCodeList.add(code);
                                            if(!selectedDcrMap.containsKey(Constants.HOSPITAL)) {
                                                selectedDcrMap.put(Constants.HOSPITAL, new ArrayList<>());
                                            }
                                            List<DCRModel> hosModelList = selectedDcrMap.get(Constants.HOSPITAL);
                                            if(hosModelList == null) {
                                                hosModelList = new ArrayList<>();
                                            }
                                            hosModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                            selectedDcrMap.put(Constants.HOSPITAL, hosModelList);
                                        }
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSTPSetup() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STP_SETUP).getMasterSyncDataJsonArray();
            if(jsonArray != null && jsonArray.length()>0) {
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                dayCaptions = jsonObject.optString("Plan_Name");
                dayIDs = jsonObject.optString("Plan_SName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populatePlanForAdapter() {
        planForModelList = new ArrayList<>();
        planForModelList.add(new PlanForModel(clusterCap, R.drawable.tp_cluster_location_ic, totalClusterCodeList.size(), selectedClusterCodeList.size()));
        planForModelList.add(new PlanForModel(drCap, R.drawable.doctor_img, totalDocCodeList.size(), selectedDocCodeList.size()));
        planForModelList.add(new PlanForModel(chmCap, R.drawable.chemist_img, totalChmCodeList.size(), selectedChmCodeList.size()));
//        planForModelList.add(new PlanForModel(stkCap, R.drawable.map_stockist_img, totalStkCodeList.size(), selectedStkCodeList.size()));
//        planForModelList.add(new PlanForModel(unDrCap, R.drawable.map_unlistdr_img, totalUnDrCodeList.size(), selectedUnDrCodeList.size()));
//        planForModelList.add(new PlanForModel(cipCap, R.drawable.cip_img, totalCipCodeList.size(), selectedCipCodeList.size()));
//        planForModelList.add(new PlanForModel(hosCap, R.drawable.tp_hospital_icon, totalHosCodeList.size(), selectedHosCodeList.size()));

        planForAdapter = new PlanForAdapter(this, planForModelList);
        RecyclerView.LayoutManager planForLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvPlanFor.setLayoutManager(planForLayoutManager);
        activityStandardTourPlanBinding.rvPlanFor.setAdapter(planForAdapter);
    }

    private void populateDocCatXVisitAdapter() {
        activityStandardTourPlanBinding.tvDocCategory.setText(drCap + " Category");
        doctorCategoryXVisitFrequencyModelList = new ArrayList<>();
        for (String key : docCategoryModelMap.keySet()) {
            DocCategoryModel docCategoryModel = docCategoryModelMap.get(key);
            if(docCategoryModel != null) {
                doctorCategoryXVisitFrequencyModelList.add(new DoctorCategoryXVisitFrequencyModel(docCategoryModel.getCategoryName(), docCategoryModel.getVisitCount()));
            }
        }

        docCategoryXVisitAdapter = new DocCategoryXVisitAdapter(this, doctorCategoryXVisitFrequencyModelList);
        RecyclerView.LayoutManager docCategoryXVisitLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvDocCatXVisit.setLayoutManager(docCategoryXVisitLayoutManager);
        activityStandardTourPlanBinding.rvDocCatXVisit.setAdapter(docCategoryXVisitAdapter);
    }

    private void populateDocDataAdapter() {
        activityStandardTourPlanBinding.tvDdDocCategory.setText(drCap + " Category");
        activityStandardTourPlanBinding.tvDdTotalDoctors.setText("Total " + drCap);
        activityStandardTourPlanBinding.tvDdPlannedDoctors.setText("Planned " + drCap);
        docDataModelList = new ArrayList<>();
        for (String key : docCategoryModelMap.keySet()) {
            DocCategoryModel docCategoryModel = docCategoryModelMap.get(key);
            if(docCategoryModel != null) {
                List<String> allDocCodes = allSelectedDocXCatMap.get(key);
                if(allDocCodes == null) allDocCodes = new ArrayList<>();
                Set<String> uniqueDocCodes = new HashSet<>(allDocCodes);
                docDataModelList.add(new DocDataModel(docCategoryModel.getCategoryName(), docCategoryModel.getDocCount(), (docCategoryModel.getDocCount() * docCategoryModel.getVisitCount()), uniqueDocCodes.size(), allDocCodes.size()));
            }
        }

        docDataAdapter = new DocDataAdapter(this, docDataModelList);
        RecyclerView.LayoutManager docDataLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvDocData.setLayoutManager(docDataLayoutManager);
        activityStandardTourPlanBinding.rvDocData.setAdapter(docDataAdapter);
    }

    private void populateCalendarAdapter() {
        String[] dayIDValues = dayIDs.split("/");
        String[] dayCaptionValues = dayCaptions.split("/");
        calendarMap = new LinkedHashMap<>();

        for (int index = 0; index<dayIDValues.length; index++) {
            String dayID = dayIDValues[index];
            if(dayID.toLowerCase().contains("mo")) {
                List<CalendarModel> calendarModelList = calendarMap.get("monday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                calendarMap.put("monday", calendarModelList);
            }else if(dayID.toLowerCase().contains("tu")) {
                List<CalendarModel> calendarModelList = calendarMap.get("tuesday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                calendarMap.put("tuesday", calendarModelList);
            }else if(dayID.toLowerCase().contains("we")) {
                List<CalendarModel> calendarModelList = calendarMap.get("wednesday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                calendarMap.put("wednesday", calendarModelList);
            }else if(dayID.toLowerCase().contains("th")) {
                List<CalendarModel> calendarModelList = calendarMap.get("thursday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                calendarMap.put("thursday", calendarModelList);
            }else if(dayID.toLowerCase().contains("fr")) {
                List<CalendarModel> calendarModelList = calendarMap.get("friday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                calendarMap.put("friday", calendarModelList);
            }else if(dayID.toLowerCase().contains("sa")) {
                List<CalendarModel> calendarModelList = calendarMap.get("saturday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                calendarMap.put("saturday", calendarModelList);
            }else if(dayID.toLowerCase().contains("su")) {
                List<CalendarModel> calendarModelList = calendarMap.get("sunday");
                if(calendarModelList == null) {
                    calendarModelList = new ArrayList<>();
                }
                calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
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
                            calendarModelList.add(index - 1, new CalendarModel((caption + index), dayID, false, null));
                        }else {
                            List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                            selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                            calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                        }
                    }
                    calendarMap.put("monday", calendarModelList);
                }
            }else if(key.equalsIgnoreCase("tuesday")) {
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
                            calendarModelList.add(index - 1, new CalendarModel((caption + index), dayID, false, null));
                        }else {
                            List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                            selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                            calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                        }
                    }
                    calendarMap.put("tuesday", calendarModelList);
                }
            }else if(key.equalsIgnoreCase("wednesday")) {
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
                            calendarModelList.add(index - 1, new CalendarModel((caption + index), dayID, false, null));
                        }else {
                            List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                            selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                            calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                        }
                    }
                    calendarMap.put("wednesday", calendarModelList);
                }
            }else if(key.equalsIgnoreCase("thursday")) {
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
                            calendarModelList.add(index - 1, new CalendarModel((caption + index), dayID, false, null));
                        }else {
                            List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                            selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                            calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                        }
                    }
                    calendarMap.put("thursday", calendarModelList);
                }
            }else if(key.equalsIgnoreCase("friday")) {
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
                            calendarModelList.add(index - 1, new CalendarModel((caption + index), dayID, false, null));
                        }else {
                            List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                            selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                            calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                        }
                    }
                    calendarMap.put("friday", calendarModelList);
                }
            }else if(key.equalsIgnoreCase("saturday")) {
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
                            calendarModelList.add(index - 1, new CalendarModel((caption + index), dayID, false, null));
                        }else {
                            List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                            selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                            calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                        }
                    }
                    calendarMap.put("saturday", calendarModelList);
                }
            }else if(key.equalsIgnoreCase("sunday")) {
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
                            calendarModelList.add(index - 1, new CalendarModel((caption + index), dayID, false, null));
                        }else {
                            List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                            selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                            calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                        }
                    }
                    calendarMap.put("sunday", calendarModelList);
                }
            }
        }

        calendarAdapter = new CalendarAdapter(this, calendarMap, new ArrayList<>(calendarMap.keySet()), calendarDayClickListener, calendarDayMenuClickListener);
        RecyclerView.LayoutManager calendarLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvCalendar.setLayoutManager(calendarLayoutManager);
        activityStandardTourPlanBinding.rvCalendar.setAdapter(calendarAdapter);
    }

    private List<SelectedDCRModel> getSelectedDCRDataList(String dayID, String caption) {
        List<SelectedDCRModel> selectedDCRModels = new ArrayList<>();
        boolean isDayAvailable = stpOfflineDataDao.isDayAvailable(dayID);
        if(isDayAvailable) {
            STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(dayID);
            String[] docList = CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(",");
            String[] chmList = CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getChemistCode()).split(",");
            docList = Arrays.stream(docList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
            chmList = Arrays.stream(chmList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
            selectedDCRModels.add(new SelectedDCRModel(R.drawable.doctor_img, 1, Arrays.toString(docList), docList.length));
            selectedDCRModels.add(new SelectedDCRModel(R.drawable.chemist_img, 2, Arrays.toString(chmList), chmList.length));
            List<DCRModel> selectedDocList = selectedDcrMap.get(Constants.DOCTOR);
            if(selectedDocList != null && !selectedDocList.isEmpty()) {
                for (int index = 0; index<selectedDocList.size(); index++) {
                    DCRModel dcrModel = selectedDocList.get(index);
                    if(stpOfflineDataTable.getDoctorCode().contains(dcrModel.getCode())) {
                        String plannedForName = dcrModel.getPlannedForName().replace("-", "") + caption + ",";
                        String plannedForCode = dcrModel.getPlannedForCode().replace("-", "") + dayID + ",";
                        dcrModel.setPlannedForName(plannedForName);
                        dcrModel.setPlannedForCode(plannedForCode);
                        selectedDocList.set(index, dcrModel);
                    }
                }
            }
            selectedDcrMap.put(Constants.DOCTOR, selectedDocList);
            List<DCRModel> selectedChmList = selectedDcrMap.get(Constants.CHEMIST);
            if(selectedChmList != null && !selectedChmList.isEmpty()) {
                for (int index = 0; index<selectedChmList.size(); index++) {
                    DCRModel dcrModel = selectedChmList.get(index);
                    if(stpOfflineDataTable.getChemistCode().contains(dcrModel.getCode())) {
                        String plannedForName = dcrModel.getPlannedForName().replace("-", "") + caption + ",";
                        String plannedForCode = dcrModel.getPlannedForCode().replace("-", "") + dayID + ",";
                        dcrModel.setPlannedForName(plannedForName);
                        dcrModel.setPlannedForCode(plannedForCode);
                        selectedChmList.set(index, dcrModel);
                    }
                }
            }
            selectedDcrMap.put(Constants.CHEMIST, selectedChmList);
        }
        return selectedDCRModels;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult activityResult) {
            try {
                if(activityResult.getResultCode() == Activity.RESULT_OK) {
                    getRequiredData();
                    populateAdapters();
                }else {
                    Log.d("STP", "onActivityResult: nothing changed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    private final CalendarAdapter.CalendarDayClickListener calendarDayClickListener = (calendarModel, mode) -> {
        Intent intent = new Intent(StandardTourPlanActivity.this, AddListActivity.class);
        intent.putExtra("MODE", String.valueOf(mode));
        intent.putExtra("DAY_ID", calendarModel.getId());
        intent.putExtra("DAY_CAPTION", calendarModel.getCaption());
        activityResultLauncher.launch(intent);
    };

    private final CalendarAdapter.CalendarDayMenuClickListener calendarDayMenuClickListener = (calendarModel, menuItem) -> {
        if(menuItem.getItemId() == R.id.menuEdit) {
            Log.d("STP Item", "Edit");
            Intent intent = new Intent(StandardTourPlanActivity.this, AddListActivity.class);
            intent.putExtra("MODE", String.valueOf(CalendarAdapter.Mode.EDIT));
            intent.putExtra("DAY_ID", calendarModel.getId());
            intent.putExtra("DAY_CAPTION", calendarModel.getCaption());
            activityResultLauncher.launch(intent);
        }else if(menuItem.getItemId() == R.id.menuDelete) {
            Log.d("STP Item", "Delete");
        }else if(menuItem.getItemId() == R.id.menuSwap) {
            Log.d("STP Item", "Swap");
        }
    };

    private boolean checkAllDocsSelected() {
        List<DCRModel> selectedDocList = selectedDcrMap.get(Constants.DOCTOR);
        if(selectedDocList != null && !selectedDocList.isEmpty()) {
            for (DCRModel dcrModel : selectedDocList) {
                String[] docList = CommonUtilsMethods.removeLastComma(dcrModel.getPlannedForCode()).split(",");
                docList = Arrays.stream(docList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
                if(docList.length<dcrModel.getVisitFrequency()) {
                    return false;
                }
            }
        }
        return true;
    }
}