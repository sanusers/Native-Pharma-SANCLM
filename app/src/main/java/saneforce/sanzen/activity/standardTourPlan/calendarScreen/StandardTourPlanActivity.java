package saneforce.sanzen.activity.standardTourPlan.calendarScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
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
import saneforce.sanzen.commonClasses.STPDaySorter;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityStandardTourPlanBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

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
    private Set<String> selectedDocCodeListSize, selectedChmCodeListSize, selectedClusterCodeListSize;
    private String hqCode, drCap, chmCap, stkCap, unDrCap, cipCap, hosCap, clusterCap, drNeed, chmNeed, stkNeed, unDrNeed, cipNeed, hosNeed, dayCaptions, dayIDs, stpFlag, rejectReason, stpType;
    private RoomDB roomDB;
    public static String stpCap;
    private MasterDataDao masterDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private GPSTrack gpsTrack;
    private CommonUtilsMethods commonUtilsMethods;
    public static HashMap<String, List<DCRModel>> selectedDcrMap;
    private ApiInterface apiInterface;
    private int totalDaysCount = 0;
    private JSONObject deleteJsonObject;
    private ArrayList<DCRFillteredModelClass> stpDataModels = new ArrayList<>();
    private String swapCode, swapName;
    private JSONArray swapJsonArray;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            activityStandardTourPlanBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stpFlag == null || stpFlag.isEmpty()) {
            stpFlag = "3";
            if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                stpFlag = "0";
            }
        }
        checkApprovalButtonStatus();

        if (stpFlag != null && !stpFlag.isEmpty()) {
            if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                if (stpFlag.equalsIgnoreCase("3")) {
                    activityStandardTourPlanBinding.llRejection.setVisibility(View.VISIBLE);
                    activityStandardTourPlanBinding.tvRejectReason.setText(rejectReason);
                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.rejected));
                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.red_60));
                    activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.rejected));
                } else {
                    activityStandardTourPlanBinding.llRejection.setVisibility(View.GONE);
                    if (stpFlag.equalsIgnoreCase("2")) {
                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.approved));
                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.green_60));
                        activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.approved));
                        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                    } else if (stpFlag.equalsIgnoreCase("1")) {
                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
                        activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.waiting_for_approval));
                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.yellow_45));
                        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                    } else if (stpFlag.equalsIgnoreCase("0")) {
                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.planning));
                        activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.planning));
                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.dark_purple));
                    }
                }
            } else {
                if (stpFlag.equalsIgnoreCase("1")) {
                    activityStandardTourPlanBinding.llRejection.setVisibility(View.VISIBLE);
                    activityStandardTourPlanBinding.tvRejectReason.setText(rejectReason);
                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.rejected));
                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.red_60));
                    activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.rejected));
                } else {
                    activityStandardTourPlanBinding.llRejection.setVisibility(View.GONE);
                    if (stpFlag.equalsIgnoreCase("0")) {
                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.approved));
                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.green_60));
                        activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.approved));
                        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                    } else if (stpFlag.equalsIgnoreCase("2")) {
                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
                        activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.waiting_for_approval));
                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.yellow_45));
                        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                    } else if (stpFlag.equalsIgnoreCase("3")) {
                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.planning));
                        activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.planning));
                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.dark_purple));
                    }
                }
            }
        } else {
            if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                stpFlag = "0";
                SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.planning));
                activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.planning));
                activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.dark_purple));
            } else {
                stpFlag = "3";
                SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.planning));
                activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.planning));
                activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.dark_purple));
            }
        }

        String stpStatus = SharedPref.getStpStatus(StandardTourPlanActivity.this);
        activityStandardTourPlanBinding.tvStpStatus.setText(stpStatus.isEmpty() ? getString(R.string.planning) : stpStatus);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, new IntentFilter("com.saneforce.SYNC_COMPLETED"));
    }

    private void checkApprovalButtonStatus() {
        if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
            activityStandardTourPlanBinding.sendToApproval.setEnabled(selectedDcrMap != null && checkAllDocsSelected() && (stpOfflineDataDao.getTotalFilledCount() >= totalDaysCount) && (totalDaysCount > 0) && (stpFlag.equalsIgnoreCase("0") || stpFlag.equalsIgnoreCase("3")));
        } else {
            activityStandardTourPlanBinding.sendToApproval.setEnabled(selectedDcrMap != null && checkAllDocsSelected() && (stpOfflineDataDao.getTotalFilledCount() >= totalDaysCount) && (totalDaysCount > 0) && (stpFlag.equalsIgnoreCase("1") || stpFlag.equalsIgnoreCase("3")));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStandardTourPlanBinding = ActivityStandardTourPlanBinding.inflate(getLayoutInflater());
        setContentView(activityStandardTourPlanBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getRequiredData();

        if (!stpType.equalsIgnoreCase("1")) {
            activityStandardTourPlanBinding.visitDetails.setVisibility(View.VISIBLE);
            activityStandardTourPlanBinding.tvDdTotalVisits.setVisibility(View.VISIBLE);
            activityStandardTourPlanBinding.tvDdPlannedVisits.setVisibility(View.VISIBLE);
            activityStandardTourPlanBinding.checkUnplannedVisits.setText(getString(R.string.check_unplanned_visits));
        } else {
            activityStandardTourPlanBinding.visitDetails.setVisibility(View.GONE);
            activityStandardTourPlanBinding.tvDdTotalVisits.setVisibility(View.GONE);
            activityStandardTourPlanBinding.tvDdPlannedVisits.setVisibility(View.GONE);
            activityStandardTourPlanBinding.checkUnplannedVisits.setText(getString(R.string.check_unplanned_doctors));
        }

        populateAdapters();
        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
        activityStandardTourPlanBinding.backArrow.setOnClickListener(view -> {
            SharedPref.setTpSKIPDate(StandardTourPlanActivity.this, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
            getOnBackPressedDispatcher().onBackPressed();
        });

        activityStandardTourPlanBinding.checkUnplannedVisits.setOnClickListener(view -> startActivity(new Intent(StandardTourPlanActivity.this, UnplannedVisitActivity.class)));

        activityStandardTourPlanBinding.sendToApproval.setOnClickListener(view -> {
            if (UtilityClass.isNetworkAvailable(this)) {
                if (!stpOfflineDataDao.isNonSyncAvailable()) {
                    sendToApproval();
                } else {
                    activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                    saveAllSTPData();
                }
            } else {
                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
            }
        });

        String stpStatus = SharedPref.getStpStatus(StandardTourPlanActivity.this);
        activityStandardTourPlanBinding.tvStpStatus.setText(stpStatus.isEmpty() ? getString(R.string.planning) : stpStatus);

        activityStandardTourPlanBinding.tvSync.setOnClickListener(view -> {
            syncSTP();
        });
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
        stpCap = SharedPref.getStpCaption(this);
        stpType = SharedPref.getStpType(this); // 0 -> divisionwise, 1 -> userwise
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
        selectedDocCodeListSize = new HashSet<>();
        selectedChmCodeListSize = new HashSet<>();
        selectedClusterCodeListSize = new HashSet<>();
        selectedDcrMap = new HashMap<>();
        allSelectedDocXCatMap = new HashMap<>();
        allSelectedDocList = new ArrayList<>();

        if (!stpCap.isEmpty()) {
            activityStandardTourPlanBinding.title.setText(stpCap);
        }
        getData();

        String stpStatus = SharedPref.getStpStatus(StandardTourPlanActivity.this);
        activityStandardTourPlanBinding.tvStpStatus.setText(stpStatus.isEmpty() ? getString(R.string.planning) : stpStatus);

    }

    private void getData() {
        getSTPSetup();
//        saveSTPDataToLocal(update);
        getLocalSTPData();
        getClusterData();
        getCategoryData();
        getDcrData();
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
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.optString("Code");
                    if (!code.isEmpty() && !totalCategoryCodeList.contains(code)) {
                        totalCategoryCodeList.add(code);
                        String name = (jsonObject.optString("Name"));
                        String docCatName = (jsonObject.optString("Doc_Cat_Name"));
                        String visitFrequency = (jsonObject.optString("No_of_visit"));
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
            if (jsonculst.length() > 0) {
                for (int i = 0; i < jsonculst.length(); i++) {
                    JSONObject jsonObject = jsonculst.getJSONObject(i);
                    String clusterCode = jsonObject.optString("Code");
                    if (!clusterCode.isEmpty() && !totalClusterCodeList.contains(clusterCode)) {
                        totalClusterCodeList.add(clusterCode);
                        String custom_name = (jsonObject.optString("Name"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSelectedCount(String codes) {
        if (codes == null || codes.trim().isEmpty())
            return 0;

        String[] arr = codes.split(",");

        int count = 0;
        for (String s : arr) {
            if (!s.trim().isEmpty()) count++;
        }
        return count;
    }

    private void saveSTPDataToLocal() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
            JSONArray jsonDoc_mas = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getSfCode(StandardTourPlanActivity.this)).getMasterSyncDataJsonArray();
            JSONArray jsonChm_mas = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + SharedPref.getSfCode(StandardTourPlanActivity.this)).getMasterSyncDataJsonArray();
            JSONArray jsonCluster = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getSfCode(StandardTourPlanActivity.this)).getMasterSyncDataJsonArray();
            if (jsonArray.length() > 0) {
                if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {

                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String dayID = jsonObject.optString("Day_Plan_ShortName");
                            String dayCaption = jsonObject.optString("Day_Plan_Name");
                            String dayPlanCode = jsonObject.optString("Day_Plan_Code");
                            String clusterCode = jsonObject.optString("Patch_Code");
                            String clusterName = jsonObject.optString("Patch_Name");
                            String doctorCode = jsonObject.optString("Dr_Code");
                            String doctorName = jsonObject.optString("Dr_Name");
                            String chemistCode = jsonObject.optString("Chem_Code");
                            String chemistName = jsonObject.optString("Chem_Name");
                            String doctorSize = String.valueOf(getSelectedCount(jsonObject.optString("Dr_Code")));
                            String chemistSize = String.valueOf(getSelectedCount(jsonObject.optString("Chem_Code")));
                            String clusterSize = String.valueOf(getSelectedCount(jsonObject.optString("Patch_Code")));
                            String doctorSpeciality = jsonObject.optString("Speciality_Name");
                            String doctorCategory = jsonObject.optString("CategoryName");
                            String doctorCategoryCode = jsonObject.optString("CategoryCode");
                            String doctorClass = jsonObject.optString("Class_Name");
                            String dateTime = jsonObject.optString("Created_Date");
                            String activeFlag = jsonObject.optString("Active_Flag");
                            Log.d("STP master data", "saveSTPDataToLocal1: " + jsonObject);

                            if (stpFlag == null || !stpFlag.isEmpty()) {
                                stpFlag = jsonObject.optString("Active_Flag", "0");
                                rejectReason = jsonObject.optString("Stp_Reject_Reason");
                                if (stpFlag.equalsIgnoreCase("3")) {
                                    activityStandardTourPlanBinding.llRejection.setVisibility(View.VISIBLE);
                                    activityStandardTourPlanBinding.tvRejectReason.setText(rejectReason);
                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.rejected));
                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.red_60));
                                } else {
                                    activityStandardTourPlanBinding.llRejection.setVisibility(View.GONE);
                                    if (stpFlag.equalsIgnoreCase("2")) {
                                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.approved));
                                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.green_60));
                                        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                                    } else if (stpFlag.equalsIgnoreCase("1")) {
                                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
                                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.yellow_45));
                                        activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                                    } else if (stpFlag.equalsIgnoreCase("0")) {
                                        SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.planning));
                                        activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.dark_purple));
                                    }
                                }
                            }

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
                            jsonSave.put("Planned_Territory_Count", clusterSize + " (" + jsonCluster.length() + ")");
                            jsonSave.put("Planned_Doctor_Count", doctorSize + " (" + jsonDoc_mas.length() + ")");
                            jsonSave.put("Planned_Chemist_Count", chemistSize + " (" + jsonChm_mas.length() + ")");
                            jsonSave.put("Planned_Hospital_Count", "0" + " (" + "0" + ")");
                            jsonSave.put("Speciality_Name", doctorSpeciality);
                            jsonSave.put("Category_Name", doctorCategory);
                            jsonSave.put("Class_Name", doctorClass);
                            jsonSave.put("Plan_Name", dayCaption);
                            jsonSave.put("Plan_SName", dayID);
                            jsonSave.put("Plan_Code", dayPlanCode);
                            jsonSave.put("StpFlag", activeFlag);
                            jsonSave.put("tableName", "save_stp");
                            jsonSave.put("ReqDt", dateTime);
                            Log.d("STP save data", "saveSTPDataToLocal2: " + jsonSave);
                            int stpFlag = 0;
                            try {
                                stpFlag = Integer.parseInt(activeFlag);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, SharedPref.getSfCode(StandardTourPlanActivity.this), dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, doctorSpeciality, doctorCategory, doctorClass, doctorCategoryCode, jsonObject.toString(), stpFlag, "0"));
                        }
                      /*  for (int a = 0; a < jsonArray.length(); a++) {

                            JSONObject row = jsonArray.getJSONObject(a);

                            String dayShort = row.optString("Day_Plan_ShortName");
                            String dayName = row.optString("Day_Plan_Name");

                            String key = dayShort;

                            JsonObject planObj;
                            if (!planMap.containsKey(key)) {
                                planObj = new JsonObject();
                                planObj.addProperty("Plan_Code", dayShort);
                                planObj.addProperty("Plan_Name", dayName);
                                planObj.addProperty("Plan_SName", dayShort);
                                planObj.add("Territory", new JsonArray());
                                planObj.add("Doctor", new JsonArray());
                                planObj.add("Chemist", new JsonArray());
                                planObj.add("Hospital", new JsonArray());
                                planMap.put(key, planObj);
                            } else {
                                planObj = planMap.get(key);
                            }

                            String[] terrCodes = row.optString("Patch_Code").split(",");
                            String[] terrNames = row.optString("Patch_Name").split(",");

                            for (int t = 0; t < terrCodes.length; t++) {
                                if (terrCodes[t].trim().isEmpty()) continue;

                                JsonObject terrObj = new JsonObject();
                                terrObj.addProperty("Code", terrCodes[t].trim());
                                terrObj.addProperty("Name", terrNames.length > t ? terrNames[t].trim() : "");
                                terrObj.addProperty("Speciality_Name", "");
                                terrObj.addProperty("Category_Name", "");
                                terrObj.addProperty("Class_Name", "");

                                planObj.getAsJsonArray("Territory").add(terrObj);
                            }

                            String[] drCodes = row.optString("Dr_Code").split(",");
                            String[] drNames = row.optString("Dr_Name").split(",");


                            for (int d = 0; d < drCodes.length; d++) {
                                if (drCodes[d].trim().isEmpty()) continue;

                                JsonObject docObj = new JsonObject();
                                docObj.addProperty("Code", drCodes[d].trim());
                                docObj.addProperty("Name", drNames.length > d ? drNames[d].trim() : "");

                                docObj.addProperty("Territory_Name", terrNames.length > d ? terrNames[d].trim() : "");

                                docObj.addProperty("Territory_Code", terrCodes.length > d ? terrCodes[d].trim() : "");

                                docObj.addProperty("Speciality_Name", row.optString("Speciality_Name"));
                                docObj.addProperty("Category_Name", row.optString("CategoryName"));
                                docObj.addProperty("Class_Name", "Class_Name");

                                planObj.getAsJsonArray("Doctor").add(docObj);
                            }


                            String[] chCodes = row.optString("Chem_Code").split(",");
                            String[] chNames = row.optString("Chem_Name").split(",");

                            for (int c = 0; c < chCodes.length; c++) {
                                if (chCodes[c].trim().isEmpty()) continue;

                                JsonObject chmObj = new JsonObject();
                                chmObj.addProperty("Code", chCodes[c].trim());
                                chmObj.addProperty("Name", chNames.length > c ? chNames[c].trim() : "");
                                chmObj.addProperty("Territory_Name", terrNames.length > 0 ? terrNames[0].trim() : "");
                                chmObj.addProperty("Speciality_Name", "");
                                chmObj.addProperty("Category_Name", "");
                                chmObj.addProperty("Class_Name", "");
                                chmObj.addProperty("Territory_Code", terrCodes.length > 0 ? terrCodes[0].trim() : "");

                                planObj.getAsJsonArray("Chemist").add(chmObj);
                            }


                        }

                        JsonArray stpDetailsArr = new JsonArray();
                        for (JsonObject obj : planMap.values()) {
                            stpDetailsArr.add(obj);
                        }
                        JsonArray categorySummaryArr = new JsonArray();

                        for (DocCategoryModel cat : docCategoryModelMap.values()) {

                            JsonObject catObj = new JsonObject();
                            catObj.addProperty("Category_Code", String.valueOf(cat.getCategoryID()));
                            catObj.addProperty("Category_Name", cat.getCategoryName());
                            catObj.addProperty("No_of_Visit", String.valueOf(cat.getVisitCount()));
                            catObj.addProperty("Planned", selectedCategoryCodeList.size() + " (" + totalCategoryCodeList.size() + ")");
                            catObj.addProperty("Total", selectedCategoryCodeList.size() + " (" + totalCategoryCodeList.size() + ")");

                            categorySummaryArr.add(catObj);
                        }

                        jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("division_code", CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this)));
                        jsonObject.put("StpFlag", SharedPref.getStpApprNeed(this).equalsIgnoreCase("1") ? "0" : "2");
                        jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                        jsonObject.put("Planned_Territory_Count", getSelectedCount(selectedClusterCodeList.toString()) + " (" + jsonCluster.length() + ")");
                        jsonObject.put("Planned_Doctor_Count", getSelectedCount(selectedDocCodeList.toString()) + " (" + jsonDoc_mas.length() + ")");
                        jsonObject.put("Planned_Chemist_Count", getSelectedCount(selectedChmCodeList.toString()) + " (" + jsonChm_mas.length() + ")");
                        jsonObject.put("Planned_Hospital_Count", "0" + " (" + "0" + ")");
                        jsonObject.put("tableName", "save_stp");
                        JSONArray categorySummaryJsonArr = new JSONArray(categorySummaryArr.toString());

                        jsonObject.put("STP_Category_Summary", categorySummaryJsonArr);

                        JSONArray stpDetailsJsonArr = new JSONArray(stpDetailsArr.toString());

                        jsonObject.put("STP_Details", stpDetailsJsonArr);

                        Log.d("STP_JSON", jsonObject.toString());*/

//                        if (stpFlag == null || !stpFlag.isEmpty()) {
//                            stpFlag = jsonObject.optString("Active_Flag", "0");
//                            rejectReason = jsonObject.optString("Stp_Reject_Reason");
//                            if (stpFlag.equalsIgnoreCase("3")) {
//                                activityStandardTourPlanBinding.llRejection.setVisibility(View.VISIBLE);
//                                activityStandardTourPlanBinding.tvRejectReason.setText(rejectReason);
//                                SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.rejected));
//                                activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.red_60));
//                            } else {
//                                activityStandardTourPlanBinding.llRejection.setVisibility(View.GONE);
//                                if (stpFlag.equalsIgnoreCase("2")) {
//                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.approved));
//                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.green_60));
//                                    activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
//                                } else if (stpFlag.equalsIgnoreCase("1")) {
//                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
//                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.yellow_45));
//                                    activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
//                                } else if (stpFlag.equalsIgnoreCase("0")) {
//                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.planning));
//                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.dark_purple));
//                                }
//                            }
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dayID = jsonObject.optString("Day_Plan_ShortName");
                        String dayCaption = jsonObject.optString("Day_Plan_Name");
                        String dayPlanCode = jsonObject.optString("Day_Plan_Code");
                        String clusterCode = jsonObject.optString("Patch_Code");
                        String clusterName = jsonObject.optString("Patch_Name");
                        String doctorCode = jsonObject.optString("Dr_Code");
                        String doctorName = jsonObject.optString("Dr_Name");
                        String chemistCode = jsonObject.optString("Chem_Code");
                        String chemistName = jsonObject.optString("Chem_Name");
                        String dateTime = jsonObject.optString("Created_Date");
                        String activeFlag = jsonObject.optString("Active_Flag");
                        Log.d("STP master data", "saveSTPDataToLocal: " + jsonObject);

                        if (stpFlag == null || !stpFlag.isEmpty()) {
                            stpFlag = jsonObject.optString("Active_Flag", "3");
                            rejectReason = jsonObject.optString("Stp_Reject_Reason");
                            if (stpFlag.equalsIgnoreCase("1")) {
                                activityStandardTourPlanBinding.llRejection.setVisibility(View.VISIBLE);
                                activityStandardTourPlanBinding.tvRejectReason.setText(rejectReason);
                                SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.rejected));
                                activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.red_60));
                            } else {
                                activityStandardTourPlanBinding.llRejection.setVisibility(View.GONE);
                                if (stpFlag.equalsIgnoreCase("0")) {
                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.approved));
                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.green_60));
                                    activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                                } else if (stpFlag.equalsIgnoreCase("2")) {
                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.yellow_45));
                                    activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                                } else if (stpFlag.equalsIgnoreCase("3")) {
                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.planning));
                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.dark_purple));
                                }
                            }
                        }

                        new JSONObject();
                        JSONObject jsonSave;
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
                        jsonSave.put("ReqDt", dateTime);
                        Log.d("STP save data", "saveSTPDataToLocal: " + jsonSave);
                        int stpFlag = 3;
                        try {
                            stpFlag = Integer.parseInt(activeFlag);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, SharedPref.getSfCode(StandardTourPlanActivity.this), dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, jsonObject.toString(), stpFlag, "0"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocalSTPData() {
        try {
            List<STPOfflineDataTable> stpOfflineDataTableList = stpOfflineDataDao.getAllSTPData();
            for (STPOfflineDataTable stpOfflineDataTable : stpOfflineDataTableList) {
                if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                    String clusterCode = stpOfflineDataTable.getClusterCode();
                    String doctorCode = stpOfflineDataTable.getDoctorCode();
                    String chemistCode = stpOfflineDataTable.getChemistCode();
                    JSONObject jsonObject = new JSONObject(stpOfflineDataTable.getStpData());
                    stpFlag = jsonObject.optString("Active_Flag", "0");
                    rejectReason = jsonObject.optString("Stp_Reject_Reason", "");
                    selectedClusterCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(clusterCode)).split(",")));
                    selectedDocCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(doctorCode)).split(",")));
                    allSelectedDocList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(doctorCode)).split(",")));
                    selectedChmCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(chemistCode)).split(",")));
                    selectedDocCodeListSize.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(String.valueOf(doctorCode.length()))).split(",")));
                    selectedChmCodeListSize.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(String.valueOf(chemistCode.length()))).split(",")));
                    selectedClusterCodeListSize.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(String.valueOf(clusterCode.length()))).split(",")));
                    selectedClusterCodeList.remove("");
                    selectedDocCodeList.remove("");
                    allSelectedDocList.remove("");
                    selectedChmCodeList.remove("");
                    selectedDocCodeListSize.remove("");
                    selectedChmCodeListSize.remove("");
                    selectedClusterCodeListSize.remove("");
                    selectedClusterCodeList.remove(null);
                    selectedDocCodeList.remove(null);
                    allSelectedDocList.remove(null);
                    selectedChmCodeList.remove(null);
                    selectedDocCodeListSize.remove(null);
                    selectedChmCodeListSize.remove(null);
                    selectedClusterCodeListSize.remove(null);
                } else {
                    String clusterCode = stpOfflineDataTable.getClusterCode();
                    String doctorCode = stpOfflineDataTable.getDoctorCode();
                    String chemistCode = stpOfflineDataTable.getChemistCode();
                    JSONObject jsonObject = new JSONObject(stpOfflineDataTable.getStpData());
                    stpFlag = jsonObject.optString("Active_Flag", "3");
                    rejectReason = jsonObject.optString("Stp_Reject_Reason", "");
                    selectedClusterCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(clusterCode)).split(",")));
                    selectedDocCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(doctorCode)).split(",")));
                    allSelectedDocList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(doctorCode)).split(",")));
                    selectedChmCodeList.addAll(Arrays.asList((CommonUtilsMethods.removeLastComma(chemistCode)).split(",")));
                    selectedClusterCodeList.remove("");
                    selectedDocCodeList.remove("");
                    allSelectedDocList.remove("");
                    selectedChmCodeList.remove("");
                    selectedClusterCodeList.remove(null);
                    selectedDocCodeList.remove(null);
                    allSelectedDocList.remove(null);
                    selectedChmCodeList.remove(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
           /* if(drNeed.equalsIgnoreCase("0")) {
                dcrNameList.add(Constants.DOCTOR);
            }*/
            if (drNeed.equalsIgnoreCase("0")) {
                dcrNameList.add(Constants.DOCTOR_MAS);
            }
         /*   if(chmNeed.equalsIgnoreCase("0")) {
                dcrNameList.add(Constants.CHEMIST);
            }*/
            if (chmNeed.equalsIgnoreCase("0")) {
                dcrNameList.add(Constants.CHEMIST_MAS);
            }
            if (stkNeed.equalsIgnoreCase("0")) {
//                dcrNameList.add(Constants.STOCKIEST);
            }
            if (unDrNeed.equalsIgnoreCase("0")) {
//                dcrNameList.add(Constants.UNLISTED_DOCTOR);
            }
            if (cipNeed.equalsIgnoreCase("0")) {
//            dcrNameList.add(Constants.CIP);
            }
            if (hosNeed.equalsIgnoreCase("0")) {
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
                ArrayList<String> codes = new ArrayList<>();
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(dcrName + hqCode).getMasterSyncDataJsonArray();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        try {
                            String code = "-", name = "-", category = "-", categoryCode = "-", speciality = "-", townName = "-", townCode = "-";
                            int visitCount = 0;

                            code = jsonObject.optString("Code");
                            if (!codes.contains(code)) {
                                codes.add(code);
                                name = jsonObject.optString("Name");
                                townName = jsonObject.optString("Town_Name");
                                townCode = jsonObject.optString("Town_Code");

                                /*if(dcrName.equalsIgnoreCase(Constants.DOCTOR)
//                                    || dcrName.equalsIgnoreCase(Constants.UNLISTED_DOCTOR)
                                ) */
                                if (dcrName.equalsIgnoreCase(Constants.DOCTOR_MAS)
//                                    || dcrName.equalsIgnoreCase(Constants.UNLISTED_DOCTOR)
                                ) {
                                    category = jsonObject.optString("Category");
                                    categoryCode = jsonObject.optString("CategoryCode");
                                    speciality = jsonObject.optString("Specialty");
//                                    if(dcrName.equalsIgnoreCase(Constants.DOCTOR)) {
                                    if (dcrName.equalsIgnoreCase(Constants.DOCTOR_MAS)) {
                                        String vstCount = jsonObject.optString("Tlvst");
                                        if (!vstCount.equalsIgnoreCase("null") && !vstCount.isEmpty()) {
                                            visitCount = Integer.parseInt(vstCount);
                                        }
                                    }
                                    if (allSelectedDocList.contains(code)) {
                                        if (!allSelectedDocXCatMap.containsKey(categoryCode)) {
                                            allSelectedDocXCatMap.put(categoryCode, new ArrayList<>());
                                        }
                                        List<String> docCodes = allSelectedDocXCatMap.get(categoryCode);
                                        if (docCodes == null) {
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
                                if (!code.isEmpty()) {
                                    switch (dcrName) {
//                                        case Constants.DOCTOR:
                                        case Constants.DOCTOR_MAS:
                                            if (!totalDocCodeList.contains(code)) {
                                                totalDocCodeList.add(code);
                                                if (docCategoryModelMap.containsKey(categoryCode)) {
                                                    DocCategoryModel docCategoryModel = docCategoryModelMap.get(categoryCode);
                                                    if (docCategoryModel != null) {
                                                        docCategoryModel.incrementDocCount();
                                                        docCategoryModelMap.put(categoryCode, docCategoryModel);
                                                    }
                                                }
                                              /*  if(!selectedDcrMap.containsKey(Constants.DOCTOR)) {
                                                    selectedDcrMap.put(Constants.DOCTOR, new ArrayList<>());
                                                }*/
                                                if (!selectedDcrMap.containsKey(Constants.DOCTOR_MAS)) {
                                                    selectedDcrMap.put(Constants.DOCTOR_MAS, new ArrayList<>());
                                                }
//                                                List<DCRModel> docModelList = selectedDcrMap.get(Constants.DOCTOR);
                                                List<DCRModel> docModelList = selectedDcrMap.get(Constants.DOCTOR_MAS);
                                                if (docModelList == null) {
                                                    docModelList = new ArrayList<>();
                                                }
                                                docModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
//                                                selectedDcrMap.put(Constants.DOCTOR, docModelList);
                                                selectedDcrMap.put(Constants.DOCTOR_MAS, docModelList);
                                            }
                                            break;
//                                        case Constants.CHEMIST:
                                        case Constants.CHEMIST_MAS:
                                            if (!totalChmCodeList.contains(code)) {
                                                totalChmCodeList.add(code);
                                        /*        if(!selectedDcrMap.containsKey(Constants.CHEMIST)) {
                                                    selectedDcrMap.put(Constants.CHEMIST, new ArrayList<>());
                                                }*/
                                                if (!selectedDcrMap.containsKey(Constants.CHEMIST_MAS)) {
                                                    selectedDcrMap.put(Constants.CHEMIST_MAS, new ArrayList<>());
                                                }
//                                                List<DCRModel> chmModelList = selectedDcrMap.get(Constants.CHEMIST);
                                                List<DCRModel> chmModelList = selectedDcrMap.get(Constants.CHEMIST_MAS);
                                                if (chmModelList == null) {
                                                    chmModelList = new ArrayList<>();
                                                }
                                                chmModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
//                                                selectedDcrMap.put(Constants.CHEMIST, chmModelList);
                                                selectedDcrMap.put(Constants.CHEMIST_MAS, chmModelList);
                                            }
                                            break;
                                        case Constants.STOCKIEST_MAS:
                                            if (!totalStkCodeList.contains(code)) {
                                                totalStkCodeList.add(code);
                                                if (!selectedDcrMap.containsKey(Constants.STOCKIEST_MAS)) {
                                                    selectedDcrMap.put(Constants.STOCKIEST_MAS, new ArrayList<>());
                                                }
                                                List<DCRModel> stkModelList = selectedDcrMap.get(Constants.STOCKIEST_MAS);
                                                if (stkModelList == null) {
                                                    stkModelList = new ArrayList<>();
                                                }
                                                stkModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                                selectedDcrMap.put(Constants.STOCKIEST_MAS, stkModelList);
                                            }
                                            break;
                                        case Constants.UNLISTED_DOCTOR_MAS:
                                            if (!totalUnDrCodeList.contains(code)) {
                                                totalUnDrCodeList.add(code);
                                                if (!selectedDcrMap.containsKey(Constants.UNLISTED_DOCTOR_MAS)) {
                                                    selectedDcrMap.put(Constants.UNLISTED_DOCTOR_MAS, new ArrayList<>());
                                                }
                                                List<DCRModel> unDrModelList = selectedDcrMap.get(Constants.UNLISTED_DOCTOR_MAS);
                                                if (unDrModelList == null) {
                                                    unDrModelList = new ArrayList<>();
                                                }
                                                unDrModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                                selectedDcrMap.put(Constants.UNLISTED_DOCTOR_MAS, unDrModelList);
                                            }
                                            break;
                                        case Constants.CIP:
                                            if (!totalCipCodeList.contains(code)) {
                                                totalCipCodeList.add(code);
                                                if (!selectedDcrMap.containsKey(Constants.CIP)) {
                                                    selectedDcrMap.put(Constants.CIP, new ArrayList<>());
                                                }
                                                List<DCRModel> cipModelList = selectedDcrMap.get(Constants.CIP);
                                                if (cipModelList == null) {
                                                    cipModelList = new ArrayList<>();
                                                }
                                                cipModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                                selectedDcrMap.put(Constants.CIP, cipModelList);
                                            }
                                            break;
                                        case Constants.HOSPITAL:
                                            if (!totalHosCodeList.contains(code)) {
                                                totalHosCodeList.add(code);
                                                if (!selectedDcrMap.containsKey(Constants.HOSPITAL)) {
                                                    selectedDcrMap.put(Constants.HOSPITAL, new ArrayList<>());
                                                }
                                                List<DCRModel> hosModelList = selectedDcrMap.get(Constants.HOSPITAL);
                                                if (hosModelList == null) {
                                                    hosModelList = new ArrayList<>();
                                                }
                                                hosModelList.add(new DCRModel(name, code, category, speciality, townName, townCode, visitCount, "-", "", false));
                                                selectedDcrMap.put(Constants.HOSPITAL, hosModelList);
                                            }
                                            break;
                                    }
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
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                dayCaptions = jsonObject.optString("Plan_Name", "");
                dayIDs = jsonObject.optString("Plan_SName", "");
//                stpCap = jsonObject.optString("STP_Name", StandardTourPlanActivity.this.getString(R.string.standard_tour_plan));
//                if(!stpCap.isEmpty()) {
//                    activityStandardTourPlanBinding.title.setText(stpCap);
//                }
            }
            if (dayIDs == null || dayIDs.isEmpty()) {
                //commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, "Kindly sync Standard Tour Plan Setup!");
                CommonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, stpCap + " " + getString(R.string.is_empty));
//                startActivity(new Intent(StandardTourPlanActivity.this, MasterSyncActivity.class));
//                finish();
            } else {
                List<String> dayIDs = stpOfflineDataDao.getAllSTPDayID();
                for (String dayID : dayIDs) {
                    if (!this.dayIDs.contains(dayID)) {
                        stpOfflineDataDao.deleteByDayID(dayID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populatePlanForAdapter() {
        planForModelList = new ArrayList<>();
        planForModelList.add(new PlanForModel(clusterCap, R.drawable.tp_cluster_location_ic, totalClusterCodeList.size(), selectedClusterCodeList.size()));
        planForModelList.add(new PlanForModel(drCap, R.drawable.doctor_img, totalDocCodeList.size(), selectedDocCodeList.size()));
        if (!stpType.equalsIgnoreCase("1")) {
            planForModelList.add(new PlanForModel(chmCap, R.drawable.chemist_img, totalChmCodeList.size(), selectedChmCodeList.size()));
        }
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
        activityStandardTourPlanBinding.tvDocCategory.setText(drCap + " " + getString(R.string.category));
        doctorCategoryXVisitFrequencyModelList = new ArrayList<>();
        for (String key : docCategoryModelMap.keySet()) {
            DocCategoryModel docCategoryModel = docCategoryModelMap.get(key);
            if (docCategoryModel != null) {
                doctorCategoryXVisitFrequencyModelList.add(new DoctorCategoryXVisitFrequencyModel(docCategoryModel.getCategoryName(), docCategoryModel.getVisitCount()));
            }
        }

        docCategoryXVisitAdapter = new DocCategoryXVisitAdapter(this, doctorCategoryXVisitFrequencyModelList);
        RecyclerView.LayoutManager docCategoryXVisitLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvDocCatXVisit.setLayoutManager(docCategoryXVisitLayoutManager);
        activityStandardTourPlanBinding.rvDocCatXVisit.setAdapter(docCategoryXVisitAdapter);
    }

    private void populateDocDataAdapter() {
        activityStandardTourPlanBinding.tvDdDocCategory.setText(drCap + " " + getString(R.string.category));
        activityStandardTourPlanBinding.tvDdTotalDoctors.setText(getString(R.string.total) + " " + drCap);
        activityStandardTourPlanBinding.tvDdPlannedDoctors.setText(getString(R.string.planned) + " " + drCap);
        activityStandardTourPlanBinding.tvDdTotalVisits.setText(getString(R.string.total) + " " + getString(R.string.visit));
        activityStandardTourPlanBinding.tvDdPlannedVisits.setText(getString(R.string.planned) + " " + getString(R.string.visit));
        docDataModelList = new ArrayList<>();
        for (String key : docCategoryModelMap.keySet()) {
            DocCategoryModel docCategoryModel = docCategoryModelMap.get(key);
            if (docCategoryModel != null) {
                List<String> allDocCodes = allSelectedDocXCatMap.get(key);
                if (allDocCodes == null) allDocCodes = new ArrayList<>();
                Set<String> uniqueDocCodes = new HashSet<>(allDocCodes);
                docDataModelList.add(new DocDataModel(docCategoryModel.getCategoryName(), docCategoryModel.getDocCount(), (docCategoryModel.getDocCount() * docCategoryModel.getVisitCount()), uniqueDocCodes.size(), allDocCodes.size()));
            }
        }

        docDataAdapter = new DocDataAdapter(this, docDataModelList, stpType);
        RecyclerView.LayoutManager docDataLayoutManager = new LinearLayoutManager(this);
        activityStandardTourPlanBinding.rvDocData.setLayoutManager(docDataLayoutManager);
        activityStandardTourPlanBinding.rvDocData.setAdapter(docDataAdapter);
    }

    private void populateCalendarAdapter() {
        if (dayIDs != null && !dayIDs.isEmpty()) {
            totalDaysCount = 0;
            String[] dayIDValues = dayIDs.split("/");
            String[] dayCaptionValues = dayCaptions.split("/");
//            String[] dayIDValues = (dayIDs + "FR1/FR2/FR3/FR4/").split("/");
//            String[] dayCaptionValues = (dayCaptions + "Friday 1/Friday 2/Friday 3/Friday 4/").split("/");
            calendarMap = new LinkedHashMap<>();

            for (int index = 0; index < dayIDValues.length; index++) {
                String dayID = dayIDValues[index];
                if (!dayID.isEmpty()) {
                    totalDaysCount++;
                }
                if (!stpType.equalsIgnoreCase("1")) {
                    if (dayID.toLowerCase().contains("mo")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("monday");
                        if (calendarModelList == null) {
                            calendarModelList = new ArrayList<>();
                        }
                        calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                        calendarMap.put("monday", calendarModelList);
                    } else if (dayID.toLowerCase().contains("tu")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("tuesday");
                        if (calendarModelList == null) {
                            calendarModelList = new ArrayList<>();
                        }
                        calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                        calendarMap.put("tuesday", calendarModelList);
                    } else if (dayID.toLowerCase().contains("we")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("wednesday");
                        if (calendarModelList == null) {
                            calendarModelList = new ArrayList<>();
                        }
                        calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                        calendarMap.put("wednesday", calendarModelList);
                    } else if (dayID.toLowerCase().contains("th")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("thursday");
                        if (calendarModelList == null) {
                            calendarModelList = new ArrayList<>();
                        }
                        calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                        calendarMap.put("thursday", calendarModelList);
                    } else if (dayID.toLowerCase().contains("fr")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("friday");
                        if (calendarModelList == null) {
                            calendarModelList = new ArrayList<>();
                        }
                        calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                        calendarMap.put("friday", calendarModelList);
                    } else if (dayID.toLowerCase().contains("sa")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("saturday");
                        if (calendarModelList == null) {
                            calendarModelList = new ArrayList<>();
                        }
                        calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                        calendarMap.put("saturday", calendarModelList);
                    } else if (dayID.toLowerCase().contains("su")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("sunday");
                        if (calendarModelList == null) {
                            calendarModelList = new ArrayList<>();
                        }
                        calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                        calendarMap.put("sunday", calendarModelList);
                    }
                } else {
                    List<CalendarModel> calendarModelList = calendarMap.get(String.valueOf((int) (index / 4)));
                    if (calendarModelList == null) {
                        calendarModelList = new ArrayList<>();
                    }
                    calendarModelList.add(new CalendarModel(dayCaptionValues[index], dayID, true, null));
                    List<SelectedDCRModel> selectedDcrModelList = getSelectedDCRDataList(dayID, dayCaptionValues[index]);
                    calendarModelList.get((int) (index % 4)).setDcrModelList(selectedDcrModelList);
                    calendarMap.put(String.valueOf((int) (index / 4)), calendarModelList);
                }
            }

            if (!stpType.equalsIgnoreCase("1")) {
                for (String key : calendarMap.keySet()) {
                    if (key.equalsIgnoreCase("monday")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("monday");
                        if (calendarModelList != null && !calendarModelList.isEmpty()) {
                            String caption = "";
                            for (int index = 1; index < 5; index++) {
                                String dayID = "MO" + index;
                                boolean isDayFound = false;
                                for (CalendarModel calendarModel : calendarModelList) {
                                    if (dayID.equalsIgnoreCase(calendarModel.getId())) {
                                        isDayFound = true;
                                        if (caption.isEmpty()) {
                                            caption = calendarModel.getCaption();
                                        }
                                        break;
                                    }
                                }
                                if (!isDayFound) {
                                    String newCaption = changeCaptionNumber(caption, index);
                                    calendarModelList.add(index - 1, new CalendarModel(newCaption, dayID, false, null));
                                } else {
                                    List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                                    selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                                    calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                                }
                            }
                            calendarMap.put("monday", calendarModelList);
                        }
                    } else if (key.equalsIgnoreCase("tuesday")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("tuesday");
                        if (calendarModelList != null && !calendarModelList.isEmpty()) {
                            String caption = "";
                            for (int index = 1; index < 5; index++) {
                                String dayID = "TU" + index;
                                boolean isDayFound = false;
                                for (CalendarModel calendarModel : calendarModelList) {
                                    if (dayID.equalsIgnoreCase(calendarModel.getId())) {
                                        isDayFound = true;
                                        if (caption.isEmpty()) {
                                            caption = calendarModel.getCaption();
                                        }
                                        break;
                                    }
                                }
                                if (!isDayFound) {
                                    String newCaption = changeCaptionNumber(caption, index);
                                    calendarModelList.add(index - 1, new CalendarModel(newCaption, dayID, false, null));
                                } else {
                                    List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                                    selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                                    calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                                }
                            }
                            calendarMap.put("tuesday", calendarModelList);
                        }
                    } else if (key.equalsIgnoreCase("wednesday")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("wednesday");
                        if (calendarModelList != null && !calendarModelList.isEmpty()) {
                            String caption = "";
                            for (int index = 1; index < 5; index++) {
                                String dayID = "WE" + index;
                                boolean isDayFound = false;
                                for (CalendarModel calendarModel : calendarModelList) {
                                    if (dayID.equalsIgnoreCase(calendarModel.getId())) {
                                        isDayFound = true;
                                        if (caption.isEmpty()) {
                                            caption = calendarModel.getCaption();
                                        }
                                        break;
                                    }
                                }
                                if (!isDayFound) {
                                    String newCaption = changeCaptionNumber(caption, index);
                                    calendarModelList.add(index - 1, new CalendarModel(newCaption, dayID, false, null));
                                } else {
                                    List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                                    selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                                    calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                                }
                            }
                            calendarMap.put("wednesday", calendarModelList);
                        }
                    } else if (key.equalsIgnoreCase("thursday")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("thursday");
                        if (calendarModelList != null && !calendarModelList.isEmpty()) {
                            String caption = "";
                            for (int index = 1; index < 5; index++) {
                                String dayID = "TH" + index;
                                boolean isDayFound = false;
                                for (CalendarModel calendarModel : calendarModelList) {
                                    if (dayID.equalsIgnoreCase(calendarModel.getId())) {
                                        isDayFound = true;
                                        if (caption.isEmpty()) {
                                            caption = calendarModel.getCaption();
                                        }
                                        break;
                                    }
                                }
                                if (!isDayFound) {
                                    String newCaption = changeCaptionNumber(caption, index);
                                    calendarModelList.add(index - 1, new CalendarModel(newCaption, dayID, false, null));
                                } else {
                                    List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                                    selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                                    calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                                }
                            }
                            calendarMap.put("thursday", calendarModelList);
                        }
                    } else if (key.equalsIgnoreCase("friday")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("friday");
                        if (calendarModelList != null && !calendarModelList.isEmpty()) {
                            String caption = "";
                            for (int index = 1; index < 5; index++) {
                                String dayID = "FR" + index;
                                boolean isDayFound = false;
                                for (CalendarModel calendarModel : calendarModelList) {
                                    if (dayID.equalsIgnoreCase(calendarModel.getId())) {
                                        isDayFound = true;
                                        if (caption.isEmpty()) {
                                            caption = calendarModel.getCaption();
                                        }
                                        break;
                                    }
                                }
                                if (!isDayFound) {
                                    String newCaption = changeCaptionNumber(caption, index);
                                    calendarModelList.add(index - 1, new CalendarModel(newCaption, dayID, false, null));
                                } else {
                                    List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                                    selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                                    calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                                }
                            }
                            calendarMap.put("friday", calendarModelList);
                        }
                    } else if (key.equalsIgnoreCase("saturday")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("saturday");
                        if (calendarModelList != null && !calendarModelList.isEmpty()) {
                            String caption = "";
                            for (int index = 1; index < 5; index++) {
                                String dayID = "SA" + index;
                                boolean isDayFound = false;
                                for (CalendarModel calendarModel : calendarModelList) {
                                    if (dayID.equalsIgnoreCase(calendarModel.getId())) {
                                        isDayFound = true;
                                        if (caption.isEmpty()) {
                                            caption = calendarModel.getCaption();
                                        }
                                        break;
                                    }
                                }
                                if (!isDayFound) {
                                    String newCaption = changeCaptionNumber(caption, index);
                                    calendarModelList.add(index - 1, new CalendarModel(newCaption, dayID, false, null));
                                } else {
                                    List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                                    selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                                    calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                                }
                            }
                            calendarMap.put("saturday", calendarModelList);
                        }
                    } else if (key.equalsIgnoreCase("sunday")) {
                        List<CalendarModel> calendarModelList = calendarMap.get("sunday");
                        if (calendarModelList != null && !calendarModelList.isEmpty()) {
                            String caption = "";
                            for (int index = 1; index < 5; index++) {
                                String dayID = "SU" + index;
                                boolean isDayFound = false;
                                for (CalendarModel calendarModel : calendarModelList) {
                                    if (dayID.equalsIgnoreCase(calendarModel.getId())) {
                                        isDayFound = true;
                                        if (caption.isEmpty()) {
                                            caption = calendarModel.getCaption();
                                        }
                                        break;
                                    }
                                }
                                if (!isDayFound) {
                                    String newCaption = changeCaptionNumber(caption, index);
                                    calendarModelList.add(index - 1, new CalendarModel(newCaption, dayID, false, null));
                                } else {
                                    List<SelectedDCRModel> selectedDcrModelList = new ArrayList<>();
                                    selectedDcrModelList = getSelectedDCRDataList(dayID, calendarModelList.get(index - 1).getCaption());
                                    calendarModelList.get(index - 1).setDcrModelList(selectedDcrModelList);
                                }
                            }
                            calendarMap.put("sunday", calendarModelList);
                        }
                    }
                }
            }

            calendarAdapter = new CalendarAdapter(this, calendarMap, new ArrayList<>(calendarMap.keySet()), calendarDayClickListener, calendarDayMenuClickListener);
            RecyclerView.LayoutManager calendarLayoutManager = new LinearLayoutManager(this);
            activityStandardTourPlanBinding.rvCalendar.setLayoutManager(calendarLayoutManager);
            activityStandardTourPlanBinding.rvCalendar.setAdapter(calendarAdapter);
        } else {
            CommonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, stpCap + " " + getString(R.string.is_empty));
            activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
        }
    }

    private String changeCaptionNumber(String str, int value) {
        try {
            int index = -1;
            for (int i = 0; i < str.length(); i++) {
                if (Character.isDigit(str.charAt(i))) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                return str.substring(0, index) + value + str.substring(index + 1);
            } else {
                return str;
            }
        } catch (Exception e) {
            Log.e("STP", "findNumberIndex: " + e.getMessage());
            e.printStackTrace();
        }
        return str;
    }

    private List<SelectedDCRModel> getSelectedDCRDataList(String dayID, String caption) {
        List<SelectedDCRModel> selectedDCRModels = new ArrayList<>();
        boolean isDayAvailable = stpOfflineDataDao.isDayAvailable(dayID);
        if (isDayAvailable) {
            STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(dayID, SharedPref.getSfCode(StandardTourPlanActivity.this));
            String[] docList = CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(",");
            docList = Arrays.stream(docList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
            selectedDCRModels.add(new SelectedDCRModel(R.drawable.doctor_img, 1, Arrays.toString(docList), docList.length));
//            List<DCRModel> selectedDocList = selectedDcrMap.get(Constants.DOCTOR);
            List<DCRModel> selectedDocList = selectedDcrMap.get(Constants.DOCTOR_MAS);
            if (selectedDocList != null && !selectedDocList.isEmpty()) {
                for (int index = 0; index < selectedDocList.size(); index++) {
                    DCRModel dcrModel = selectedDocList.get(index);
                    if (stpOfflineDataTable.getDoctorCode().contains(dcrModel.getCode())) {
                        String plannedForName = dcrModel.getPlannedForName().replace("-", "") + caption + ",";
                        String plannedForCode = dcrModel.getPlannedForCode().replace("-", "") + dayID + ",";
                        dcrModel.setPlannedForName(plannedForName);
                        dcrModel.setPlannedForCode(plannedForCode);
                        selectedDocList.set(index, dcrModel);
                    }
                }
            }
//            selectedDcrMap.put(Constants.DOCTOR, selectedDocList);
            selectedDcrMap.put(Constants.DOCTOR_MAS, selectedDocList);
            if (!stpType.equalsIgnoreCase("1")) {
                String[] chmList = CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getChemistCode()).split(",");
                chmList = Arrays.stream(chmList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
                selectedDCRModels.add(new SelectedDCRModel(R.drawable.chemist_img, 2, Arrays.toString(chmList), chmList.length));
//            List<DCRModel> selectedChmList = selectedDcrMap.get(Constants.CHEMIST);
                List<DCRModel> selectedChmList = selectedDcrMap.get(Constants.CHEMIST_MAS);
                if (selectedChmList != null && !selectedChmList.isEmpty()) {
                    for (int index = 0; index < selectedChmList.size(); index++) {
                        DCRModel dcrModel = selectedChmList.get(index);
                        if (stpOfflineDataTable.getChemistCode().contains(dcrModel.getCode())) {
                            String plannedForName = dcrModel.getPlannedForName().replace("-", "") + caption + ",";
                            String plannedForCode = dcrModel.getPlannedForCode().replace("-", "") + dayID + ",";
                            dcrModel.setPlannedForName(plannedForName);
                            dcrModel.setPlannedForCode(plannedForCode);
                            selectedChmList.set(index, dcrModel);
                        }
                    }
                }
//            selectedDcrMap.put(Constants.CHEMIST, selectedChmList);
                selectedDcrMap.put(Constants.CHEMIST_MAS, selectedChmList);
            }
        }
        return selectedDCRModels;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult activityResult) {
            try {
                if (activityResult.getResultCode() == Activity.RESULT_OK) {
                    getRequiredData();
                    populateAdapters();
                } else {
                    Log.d("STP", "onActivityResult: nothing changed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkApprovalButtonStatus();
        }
    });

    private final CalendarAdapter.CalendarDayClickListener calendarDayClickListener = (calendarModel, mode) -> {
//        if(!stpFlag.equalsIgnoreCase("0") && !stpFlag.equalsIgnoreCase("2")) {
        Intent intent = new Intent(StandardTourPlanActivity.this, AddListActivity.class);
        intent.putExtra("MODE", String.valueOf(mode));
        intent.putExtra("DAY_ID", calendarModel.getId());
        intent.putExtra("DAY_CAPTION", calendarModel.getCaption());
        activityResultLauncher.launch(intent);
//        }else if (stpFlag.equalsIgnoreCase("0")){
//            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.already_approved));
//        }else if (stpFlag.equalsIgnoreCase("2")){
//            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
//        }
    };

    private final CalendarAdapter.CalendarDayMenuClickListener calendarDayMenuClickListener = (calendarModel, menuItem) -> {
        if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
            if (!stpFlag.equalsIgnoreCase("1") && !stpFlag.equalsIgnoreCase("2")) {
                if (menuItem.getItemId() == R.id.menuEdit) {
                    Log.d("STP Item", "Edit");
                    Intent intent = new Intent(StandardTourPlanActivity.this, AddListActivity.class);
                    intent.putExtra("MODE", String.valueOf(CalendarAdapter.Mode.EDIT));
                    intent.putExtra("DAY_ID", calendarModel.getId());
                    intent.putExtra("DAY_CAPTION", calendarModel.getCaption());
                    activityResultLauncher.launch(intent);
                } else if (menuItem.getItemId() == R.id.menuDelete) {
                    Log.d("STP Item", "Delete");
                    showDeleteDialog(calendarModel.getId(), calendarModel.getCaption());
                } else if (menuItem.getItemId() == R.id.menuSwap) {
                    Log.d("STP Item", "Swap");
                    showSwapDialog(calendarModel.getId(), calendarModel.getCaption());
                }
            } else if (stpFlag.equalsIgnoreCase("1")) {
                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
            } else if (stpFlag.equalsIgnoreCase("2")) {
                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.already_approved));
            }
        } else {
            if (!stpFlag.equalsIgnoreCase("0") && !stpFlag.equalsIgnoreCase("2")) {
                if (menuItem.getItemId() == R.id.menuEdit) {
                    Log.d("STP Item", "Edit");
                    Intent intent = new Intent(StandardTourPlanActivity.this, AddListActivity.class);
                    intent.putExtra("MODE", String.valueOf(CalendarAdapter.Mode.EDIT));
                    intent.putExtra("DAY_ID", calendarModel.getId());
                    intent.putExtra("DAY_CAPTION", calendarModel.getCaption());
                    activityResultLauncher.launch(intent);
                } else if (menuItem.getItemId() == R.id.menuDelete) {
                    Log.d("STP Item", "Delete");
                    showDeleteDialog(calendarModel.getId(), calendarModel.getCaption());
                } else if (menuItem.getItemId() == R.id.menuSwap) {
                    Log.d("STP Item", "Swap");
                    showSwapDialog(calendarModel.getId(), calendarModel.getCaption());
                }
            } else if (stpFlag.equalsIgnoreCase("0")) {
                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.already_approved));
            } else if (stpFlag.equalsIgnoreCase("2")) {
                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
            }
        }
    };

    private void showSwapDialog(String id, String caption) {
        try {
            Dialog dialogFilter;
            ImageView img_close;
            Button btn_proceed, btn_cancel;
            ListView lv_to;
            TextView tvTo, tvFrom;

            dialogFilter = new Dialog(this);
            dialogFilter.setContentView(R.layout.popup_stp_swap);
            Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogFilter.setCancelable(false);
            dialogFilter.show();
            img_close = dialogFilter.findViewById(R.id.img_close);
            btn_proceed = dialogFilter.findViewById(R.id.btn_proceed);
            btn_cancel = dialogFilter.findViewById(R.id.btn_cancel);
            tvTo = dialogFilter.findViewById(R.id.constraint_to);
            tvFrom = dialogFilter.findViewById(R.id.constraint_from);
            lv_to = dialogFilter.findViewById(R.id.lv_to);

            tvFrom.setText(caption);

            ConstraintLayout constraintLayout = dialogFilter.findViewById(R.id.constraint_btns);
            img_close.setOnClickListener(view12 -> dismissSwap(dialogFilter));
            btn_cancel.setOnClickListener(view12 -> dismissSwap(dialogFilter));
            swapCode = "";
            swapName = "";

            tvTo.setOnClickListener(view -> {
                if (lv_to.getVisibility() == View.VISIBLE) {
                    lv_to.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    getSTPData(id);
                    FillteredAdapter arrayAdapter = new FillteredAdapter(this, stpDataModels, clickedItem -> {
                        swapCode = clickedItem.getCode();
                        swapName = clickedItem.getName();
                        tvTo.setText(clickedItem.getName());
                        lv_to.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_to.setAdapter(arrayAdapter);
                    lv_to.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            });

            btn_proceed.setOnClickListener(view -> {
                if (swapCode != null && !swapCode.isEmpty()) {
                    showSwapConfirmDialog(id, caption, swapCode, swapName);
                    dialogFilter.dismiss();
                } else {
                    CommonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.select_swap_to));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissSwap(Dialog dialogFilter) {
        swapCode = "";
        swapName = "";
        dialogFilter.dismiss();
    }

    private void showSwapConfirmDialog(String fromID, String fromName, String toID, String toName) {
        Dialog dialogOptionSelection = new Dialog(this);
        dialogOptionSelection.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogOptionSelection.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptionSelection.setCancelable(false);
        ImageView iv_close = dialogOptionSelection.findViewById(R.id.img_close);
        EditText ed_remarks = dialogOptionSelection.findViewById(R.id.ed_remark);
        TextView heading = dialogOptionSelection.findViewById(R.id.tv_head);
        TextView content = dialogOptionSelection.findViewById(R.id.content);
        Button btn_clear = dialogOptionSelection.findViewById(R.id.btn_clear);
        Button btn_save = dialogOptionSelection.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(getString(R.string.swap));
        btn_clear.setText(getString(R.string.cancel));
        content.setText(getString(R.string.are_you_sure) + " " + getString(R.string.want_to_swap) + " " + getString(R.string.from) + " " + fromName + " " + getString(R.string.to) + " " + toName);
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(view -> {
            dialogOptionSelection.dismiss();
            if (UtilityClass.isNetworkAvailable(this)) {
                createSwapJson(fromID, fromName, toID, toName);
                callSwapAPI(fromID, fromName, toID, toName);
            } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
            }
        });
        btn_clear.setOnClickListener(view -> {
            dialogOptionSelection.dismiss();
        });
        iv_close.setOnClickListener(view -> dialogOptionSelection.dismiss());
        dialogOptionSelection.show();
    }

    private void callSwapAPI(String fromID, String fromName, String toID, String toName) {
        try {
            activityStandardTourPlanBinding.flProgress.setVisibility(View.VISIBLE);
            activityStandardTourPlanBinding.tvProgressTitle.setText(getString(R.string.swapping));
            Log.e("swap:Object", swapJsonArray.toString());
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "swap/stp");
            Call<JsonElement> saveMyDayPlan = apiInterface.getJSONElement(SharedPref.getCallApiUrl(StandardTourPlanActivity.this), mapString, swapJsonArray.toString());
            saveMyDayPlan.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.d("swap:Code", response.code() + " - " + response);
                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.swap_between) + " " + fromName + " " + getString(R.string.and) + " " + toName + " " + getString(R.string.was_successful));
                            }
                        } catch (Exception e) {
                            Log.e("STP SWAP", "onResponse: " + e.getMessage());
                        }
                        syncSTP();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                    Log.e("VALUES", Arrays.toString(t.getStackTrace()));
                    commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
        }
    }

    private void syncSTP() {
        try {
            activityStandardTourPlanBinding.flProgress.setVisibility(View.VISIBLE);
            activityStandardTourPlanBinding.tvProgressTitle.setText(getString(R.string.syncing));
            apiInterface = RetrofitClient.getRetrofit(StandardTourPlanActivity.this, SharedPref.getCallApiUrl(StandardTourPlanActivity.this));
            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
            jsonObject.put("tableName", "getstp_details");
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SharedPref.getHqCode(this));
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));

            Log.v("STP", "--json-- " + jsonObject);

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "get/stp");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    boolean success = false;
                    JSONArray jsonArray = new JSONArray();
                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        Log.e("test mydayplan", "response : " + Objects.requireNonNull(response.body()));
                        try {
                            JsonElement jsonElement = response.body();
                            if (!jsonElement.isJsonNull()) {
                                if (jsonElement.isJsonArray()) {
                                    JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                    jsonArray = new JSONArray(jsonArray1.toString());
                                    success = true;
                                } else if (jsonElement.isJsonObject()) {
                                    JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                    JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                    if (!jsonObject2.has("success")) {
                                        jsonArray.put(jsonObject2);
                                        success = true;
                                    } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                        masterDataDao.saveMasterSyncStatus(Constants.STANDARD_TOUR_PLAN, 1);
                                    }
                                }

                                if (success) {
                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STANDARD_TOUR_PLAN, jsonArray.toString(), 2));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.deleteAllData("0");
                        saveSTPDataToLocal();
                        getRequiredData();
                        populateAdapters();
                        checkApprovalButtonStatus();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    Log.e("STP", "onFailure: ");
                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                    commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
                    t.printStackTrace();
                }
            });

        } catch (JSONException a) {
            activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
            a.printStackTrace();
        }
    }

    private void createSwapJson(String fromID, String fromName, String toID, String toName) {
        try {
            STPOfflineDataTable fromStpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDay(fromID, SharedPref.getSfCode(StandardTourPlanActivity.this));
            STPOfflineDataTable toStpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDay(toID, SharedPref.getSfCode(StandardTourPlanActivity.this));

            String dateTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1);
            swapJsonArray = new JSONArray();
            JSONObject fromObj = CommonUtilsMethods.CommonObjectParameter(this);
            fromObj.put("tableName", "stp_swap");
            fromObj.put("sfcode", SharedPref.getSfCode(this));
            fromObj.put("division_code", SharedPref.getDivisionCode(this));
            fromObj.put("Rsf", SharedPref.getSfCode(this));
            fromObj.put("town_code", fromStpOfflineDataTable.getClusterCode());
            fromObj.put("town_name", fromStpOfflineDataTable.getClusterName());
            fromObj.put("Doctor_Id", fromStpOfflineDataTable.getDoctorCode());
            fromObj.put("Doctor_Name", fromStpOfflineDataTable.getDoctorName());
            fromObj.put("Chemist_Id", fromStpOfflineDataTable.getChemistCode());
            fromObj.put("Chemist_Name", fromStpOfflineDataTable.getChemistName());
            fromObj.put("Plan_Code", "" + toStpOfflineDataTable.getDayID().charAt(toStpOfflineDataTable.getDayID().length() - 1));
            fromObj.put("Plan_Name", toStpOfflineDataTable.getDayCaption());
            fromObj.put("Plan_SName", toStpOfflineDataTable.getDayID());
            fromObj.put("Creation_time", dateTime);
            if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                fromObj.put("StpFlag", "0");
            } else {
                fromObj.put("StpFlag", "3");
            }
            JSONObject toObj = CommonUtilsMethods.CommonObjectParameter(this);
            toObj.put("tableName", "stp_swap");
            toObj.put("sfcode", SharedPref.getSfCode(this));
            toObj.put("division_code", SharedPref.getDivisionCode(this));
            toObj.put("Rsf", SharedPref.getSfCode(this));
            toObj.put("town_code", toStpOfflineDataTable.getClusterCode());
            toObj.put("town_name", toStpOfflineDataTable.getClusterName());
            toObj.put("Doctor_Id", toStpOfflineDataTable.getDoctorCode());
            toObj.put("Doctor_Name", toStpOfflineDataTable.getDoctorName());
            toObj.put("Chemist_Id", toStpOfflineDataTable.getChemistCode());
            toObj.put("Chemist_Name", toStpOfflineDataTable.getChemistName());
            toObj.put("Plan_Code", "" + fromStpOfflineDataTable.getDayID().charAt(fromStpOfflineDataTable.getDayID().length() - 1));
            toObj.put("Plan_Name", fromStpOfflineDataTable.getDayCaption());
            toObj.put("Plan_SName", fromStpOfflineDataTable.getDayID());
            toObj.put("Creation_time", dateTime);
            if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                fromObj.put("StpFlag", "0");
            } else {
                fromObj.put("StpFlag", "3");
            }

            swapJsonArray.put(fromObj);
            swapJsonArray.put(toObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSTPData(String id) {
        try {
            List<STPOfflineDataTable> stpData = stpOfflineDataDao.getAllSTPData();
            stpDataModels.clear();
            for (STPOfflineDataTable stpOfflineDataTable : stpData) {
                if (!stpOfflineDataTable.getDayID().equals(id)) {
                    stpDataModels.add(new DCRFillteredModelClass(stpOfflineDataTable.getDayCaption(), stpOfflineDataTable.getDayID()));
                }
            }
            if (!SharedPref.getStpType(StandardTourPlanActivity.this).equalsIgnoreCase("1")) {
                STPDaySorter.sortDays(stpDataModels, DCRFillteredModelClass::getCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteDialog(String dayID, String caption) {
        Dialog dialogOptionSelection = new Dialog(this);
        dialogOptionSelection.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogOptionSelection.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptionSelection.setCancelable(false);
        ImageView iv_close = dialogOptionSelection.findViewById(R.id.img_close);
        EditText ed_remarks = dialogOptionSelection.findViewById(R.id.ed_remark);
        TextView heading = dialogOptionSelection.findViewById(R.id.tv_head);
        TextView content = dialogOptionSelection.findViewById(R.id.content);
        Button btn_clear = dialogOptionSelection.findViewById(R.id.btn_clear);
        Button btn_save = dialogOptionSelection.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(getString(R.string.delete));
        btn_clear.setText(getString(R.string.cancel));
        content.setText(R.string.are_you_sure_to_delete);
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(view -> {
            if (UtilityClass.isNetworkAvailable(this)) {
                createDeleteJson(dayID);
                callDeleteAPI(dayID, caption);
            } else {
//                if(stpOfflineDataDao.getSTPDataOfDayOrNew(dayID).getSyncStatus().equalsIgnoreCase("1")) {
//                    stpOfflineDataDao.deleteByDayID(dayID);
//                    getRequiredData();
//                    populateAdapters();
//                    checkApprovalButtonStatus();
//                } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
//                }
            }
            dialogOptionSelection.dismiss();
        });
        btn_clear.setOnClickListener(view -> {
            dialogOptionSelection.dismiss();
        });
        iv_close.setOnClickListener(view -> dialogOptionSelection.dismiss());
        dialogOptionSelection.show();
    }

    private void callDeleteAPI(String dayID, String caption) {
        try {
            activityStandardTourPlanBinding.flProgress.setVisibility(View.VISIBLE);
            activityStandardTourPlanBinding.tvProgressTitle.setText(getString(R.string.deleting));
            Log.e("delete:Object", deleteJsonObject.toString());
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "delete/stp");
            Call<JsonElement> saveMyDayPlan = apiInterface.getJSONElement(SharedPref.getCallApiUrl(StandardTourPlanActivity.this), mapString, deleteJsonObject.toString());
            saveMyDayPlan.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.d("delete:Code", response.code() + " - " + response);
                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, caption + " " + getString(R.string.deleted_successfully));
                            }
                        } catch (Exception e) {
                            Log.e("STP Delete", "onResponse: " + e.getMessage());
                        }
                        syncSTP();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    Log.e("VALUES", Arrays.toString(t.getStackTrace()));
                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                    commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
        }
    }

    private void createDeleteJson(String dayID) {
        try {
            deleteJsonObject = CommonUtilsMethods.CommonObjectParameter(this);
            deleteJsonObject.put("tableName", "stp_delete");
            deleteJsonObject.put("Plan_Code", dayID);
            deleteJsonObject.put("sfcode", SharedPref.getSfCode(this));
            deleteJsonObject.put("division_code", SharedPref.getDivisionCode(this));
            deleteJsonObject.put("Rsf", SharedPref.getSfCode(this));
            deleteJsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkAllDocsSelected() {
//        List<DCRModel> selectedDocList = selectedDcrMap.get(Constants.DOCTOR);
        List<DCRModel> selectedDocList = selectedDcrMap.get(Constants.DOCTOR_MAS);
        if (selectedDocList != null && !selectedDocList.isEmpty()) {
            for (DCRModel dcrModel : selectedDocList) {
                String[] docList = CommonUtilsMethods.removeLastComma(dcrModel.getPlannedForCode()).split(",");
                docList = Arrays.stream(docList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
                if (stpType.equalsIgnoreCase("1")) {
                    if (docList.length == 0) {
                        return false;
                    }
                } else {
                    if (docList.length < dcrModel.getVisitFrequency()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void saveAllSTPData() {
        if (UtilityClass.isNetworkAvailable(this)) {
            try {
                List<STPOfflineDataTable> stpOfflineDataTableList = stpOfflineDataDao.getAllNonSyncSTPData();
                if (stpOfflineDataTableList != null && !stpOfflineDataTableList.isEmpty()) {
                    activityStandardTourPlanBinding.flProgress.setVisibility(View.VISIBLE);
                    activityStandardTourPlanBinding.tvProgressTitle.setText(getString(R.string.syncing_offline_data));
                    final int[] apiCount = {0};
                    for (STPOfflineDataTable stpOfflineDataTable : stpOfflineDataTableList) {
                        String dayID = stpOfflineDataTable.getDayID(), dayCaption = stpOfflineDataTable.getDayCaption(), strClusterID = stpOfflineDataTable.getClusterCode(), strClusterName = stpOfflineDataTable.getClusterName(), docCodes = stpOfflineDataTable.getDoctorCode(), docNames = stpOfflineDataTable.getDoctorName(), chmCodes = stpOfflineDataTable.getChemistCode(), chmNames = stpOfflineDataTable.getChemistName(), docSpeciality = stpOfflineDataTable.getDoctorSpeciality(), docCategory = stpOfflineDataTable.getDoctorCategory(), docCategoryCode = stpOfflineDataTable.getDoctorCategoryCode(), docClass = stpOfflineDataTable.getDoctorClass(), jsonObject = stpOfflineDataTable.getStpData();
                        int stpFlag = stpOfflineDataTable.getStatus();
                        apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "save/stp");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(StandardTourPlanActivity.this), mapString, jsonObject);
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.v("stp save", "--res--" + response.body());
                                apiCount[0]++;
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        JSONObject jsonObject1 = new JSONObject(response.body().toString());
                                        if (jsonObject1.optString("success").equals("true")) {
                                            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, dayCaption + " " + getString(R.string.saved_successfully));
                                            if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                                                stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, SharedPref.getSfCode(StandardTourPlanActivity.this), dayCaption, strClusterID, strClusterName, docCodes, docNames, chmCodes, chmNames, docSpeciality, docCategory, docClass, docCategoryCode, jsonObject, stpFlag, "0"));
                                            } else {
                                                stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, SharedPref.getSfCode(StandardTourPlanActivity.this), dayCaption, strClusterID, strClusterName, docCodes, docNames, chmCodes, chmNames, jsonObject, stpFlag, "0"));
                                            }
                                        } else {
                                            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.stp_saved_locally));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.stp_saved_locally));
                                }
                                if (apiCount[0] == stpOfflineDataTableList.size()) {
                                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                                    activityStandardTourPlanBinding.sendToApproval.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                t.printStackTrace();
                                apiCount[0]++;
                                if (apiCount[0] == stpOfflineDataTableList.size()) {
                                    activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                                    activityStandardTourPlanBinding.sendToApproval.setEnabled(true);
                                }
                                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.stp_saved_locally));
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                activityStandardTourPlanBinding.flProgress.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.no_network));
            }
        }
    }

    private void sendToApproval() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {

       /*         JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
                String doctorSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Dr_Code")));
                String chemistSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Chem_Code")));
                String clusterSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Patch_Code")));
                JSONArray jsonDoc_mas = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getSfCode(StandardTourPlanActivity.this)).getMasterSyncDataJsonArray();
                JSONArray jsonChm_mas = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + SharedPref.getSfCode(StandardTourPlanActivity.this)).getMasterSyncDataJsonArray();
                JSONArray jsonCluster = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getSfCode(StandardTourPlanActivity.this)).getMasterSyncDataJsonArray();
                Map<String, JsonObject> planMap = new LinkedHashMap<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject row = jsonArray.getJSONObject(i);

                    String dayShort = row.optString("Day_Plan_ShortName");
                    String dayName  = row.optString("Day_Plan_Name");

                    String key = dayShort;

                    JsonObject planObj;
                    if (!planMap.containsKey(key)) {
                        planObj = new JsonObject();
                        planObj.addProperty("Plan_Code", dayShort);
                        planObj.addProperty("Plan_Name", dayName);
                        planObj.addProperty("Plan_SName",dayShort);
                        planObj.add("Territory", new JsonArray());
                        planObj.add("Doctor", new JsonArray());
                        planObj.add("Chemist", new JsonArray());
                        planObj.add("Hospital", new JsonArray());
                        planMap.put(key, planObj);
                    } else {
                        planObj = planMap.get(key);
                    }

                    String[] terrCodes = row.optString("Patch_Code").split(",");
                    String[] terrNames = row.optString("Patch_Name").split(",");

                    for (int t = 0; t < terrCodes.length; t++) {
                        if (terrCodes[t].trim().isEmpty()) continue;

                        JsonObject terrObj = new JsonObject();
                        terrObj.addProperty("Code", terrCodes[t].trim());
                        terrObj.addProperty("Name", terrNames.length > t ? terrNames[t].trim() : "");
                        terrObj.addProperty("Speciality_Name", "");
                        terrObj.addProperty("Category_Name", "");
                        terrObj.addProperty("Class_Name", "");

                        planObj.getAsJsonArray("Territory").add(terrObj);
                    }

                    String[] drCodes = row.optString("Dr_Code").split(",");
                    String[] drNames = row.optString("Dr_Name").split(",");

                 *//*   for (int d = 0; d < drCodes.length; d++) {
                        if (drCodes[d].trim().isEmpty()) continue;

                        JsonObject docObj = new JsonObject();
                        docObj.addProperty("Code", drCodes[d].trim());
                        docObj.addProperty("Name", drNames.length > d ? drNames[d].trim() : "");
                        docObj.addProperty("Territory_Name", terrNames.length > 0 ? terrNames[0].trim() : "");
                        docObj.addProperty("Speciality_Name", row.optString("Speciality_Name"));
                        docObj.addProperty("Category_Name", row.optString("CategoryName"));
                        docObj.addProperty("Class_Name", "Nil");
                        docObj.addProperty("Territory_Code", terrCodes.length > 0 ? terrCodes[0].trim() : "");

                        planObj.getAsJsonArray("Doctor").add(docObj);
                    }*//*
                    for (int d = 0; d < drCodes.length; d++) {
                        if (drCodes[d].trim().isEmpty()) continue;

                        JsonObject docObj = new JsonObject();
                        docObj.addProperty("Code", drCodes[d].trim());
                        docObj.addProperty("Name", drNames.length > d ? drNames[d].trim() : "");

                        docObj.addProperty(
                                "Territory_Name",
                                terrNames.length > d ? terrNames[d].trim() : ""
                        );

                        docObj.addProperty(
                                "Territory_Code",
                                terrCodes.length > d ? terrCodes[d].trim() : ""
                        );

                        docObj.addProperty("Speciality_Name", row.optString("Speciality_Name"));
                        docObj.addProperty("Category_Name", row.optString("CategoryName"));
                        docObj.addProperty("Class_Name", "Class_Name");

                        planObj.getAsJsonArray("Doctor").add(docObj);
                    }


                    String[] chCodes = row.optString("Chem_Code").split(",");
                    String[] chNames = row.optString("Chem_Name").split(",");

                    for (int c = 0; c < chCodes.length; c++) {
                        if (chCodes[c].trim().isEmpty()) continue;

                        JsonObject chmObj = new JsonObject();
                        chmObj.addProperty("Code", chCodes[c].trim());
                        chmObj.addProperty("Name", chNames.length > c ? chNames[c].trim() : "");
                        chmObj.addProperty("Territory_Name", terrNames.length > 0 ? terrNames[0].trim() : "");
                        chmObj.addProperty("Speciality_Name", "");
                        chmObj.addProperty("Category_Name", "");
                        chmObj.addProperty("Class_Name", "");
                        chmObj.addProperty("Territory_Code", terrCodes.length > 0 ? terrCodes[0].trim() : "");

                        planObj.getAsJsonArray("Chemist").add(chmObj);
                    }
                }

                JsonArray stpDetailsArr = new JsonArray();
                for (JsonObject obj : planMap.values()) {
                    stpDetailsArr.add(obj);
                }
                JsonArray categorySummaryArr = new JsonArray();

                for (DocCategoryModel cat : docCategoryModelMap.values()) {

                    JsonObject catObj = new JsonObject();
                    catObj.addProperty("Category_Code", String.valueOf(cat.getCategoryID()));
                    catObj.addProperty("Category_Name", cat.getCategoryName());
                    catObj.addProperty("No_of_Visit", String.valueOf(cat.getVisitCount()));
                    catObj.addProperty("Planned", selectedCategoryCodeList.size()+" ("+totalCategoryCodeList.size()+")");
                    catObj.addProperty("Total", selectedCategoryCodeList.size()+" ("+totalCategoryCodeList.size()+")");

                    categorySummaryArr.add(catObj);
                }

                jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this)));
                jsonObject.put("StpFlag", SharedPref.getStpApprNeed(this).equalsIgnoreCase("1") ? "0" : "2");
                jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                jsonObject.put("Planned_Territory_Count",selectedClusterCodeList.size()+" ("+jsonCluster.length()+")");
                jsonObject.put("Planned_Doctor_Count",selectedDocCodeList.size()+" ("+jsonDoc_mas.length()+")");
                jsonObject.put("Planned_Chemist_Count",selectedChmCodeList.size()+" ("+jsonChm_mas.length()+")");
                jsonObject.put("Planned_Hospital_Count","0"+" ("+"0"+")");
                jsonObject.put("tableName", "submit_stp");
                JSONArray categorySummaryJsonArr = new JSONArray(categorySummaryArr.toString());

                jsonObject.put("STP_Category_Summary", categorySummaryJsonArr);

                JSONArray stpDetailsJsonArr = new JSONArray(stpDetailsArr.toString());

                jsonObject.put("STP_Details", stpDetailsJsonArr);

                Log.d("STP_JSON", jsonObject.toString());*/
                jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this)));
                jsonObject.put("Rsf", SharedPref.getHqCode(this));
                jsonObject.put("StpFlag", SharedPref.getStpApprNeed(this).equalsIgnoreCase("0") ? "1" : "2");
                jsonObject.put("tableName", "submit_stp");
                jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                Log.v("json_save_stp", jsonObject.toString());

            } else {
                jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this)));
                jsonObject.put("Rsf", SharedPref.getHqCode(this));
                jsonObject.put("StpFlag", SharedPref.getStpApprNeed(this).equalsIgnoreCase("1") ? "0" : "2");
                jsonObject.put("tableName", "submit_stp");
                jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                Log.v("json_save_stp", jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (UtilityClass.isNetworkAvailable(this)) {
                apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/stp");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(StandardTourPlanActivity.this), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        Log.v("stp save", "--res--" + response.body());
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject1 = new JSONObject(response.body().toString());
                                if (jsonObject1.optString("success", "false").equals("true")) {
                                    commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.send_approved_successfully));
                                    activityStandardTourPlanBinding.sendToApproval.setEnabled(false);
                                    SharedPref.setStpStatus(StandardTourPlanActivity.this, getString(R.string.waiting_for_approval));
                                    activityStandardTourPlanBinding.tvStpStatus.setTextColor(getColor(R.color.yellow_45));
                                    activityStandardTourPlanBinding.tvStpStatus.setText(getString(R.string.waiting_for_approval));
                                    activityStandardTourPlanBinding.llRejection.setVisibility(View.GONE);
                                    if (SharedPref.getOneBuild(StandardTourPlanActivity.this).equalsIgnoreCase("0")) {
                                        stpFlag = "1";
                                    } else {
                                        stpFlag = "2";
                                    }
                                    syncSTP();
                                } else {
                                    commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.failed_to_send_approval));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.failed_to_send_approval));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.failed_to_send_approval));
                    }
                });
            } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
            }
        } catch (Exception e) {
            e.printStackTrace();
            commonUtilsMethods.showToastMessage(StandardTourPlanActivity.this, getString(R.string.failed_to_send_approval));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if (type != null && type.matches("(?i)DR|CH|ST|UL|HOS|CIP|SE|TM|FSD|AMS")) {
                startActivity(new Intent(StandardTourPlanActivity.this, StandardTourPlanActivity.class));
                finish();
            }
        }
    };

}