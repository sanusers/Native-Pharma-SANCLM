package saneforce.sanzen.activity.standardTourPlan.addListScreen;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DocCategoryModel;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.MultiClusterAdapter;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.OnClusterClicklistener;
import saneforce.sanzen.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter.DCRSelectionAdapter;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter.SelectedDCRAdapter;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.model.ClusterModel;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.model.NoDataModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.StandardTourPlanActivity;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter.CalendarAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityAddListBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class AddListActivity extends AppCompatActivity {
    private ActivityAddListBinding activityAddListBinding;
    private final ArrayList<Multicheckclass_clust> selectedClusterList = new ArrayList<>();
    private final ArrayList<Multicheckclass_clust> multiple_cluster_list = new ArrayList<>();
    private String hqCode, strClusterName, strClusterID, mode, dayID, dayCaption, drCap, chmCap, stkCap, unDrCap, cipCap, hosCap, clusterCap, stpCap, selectedDCR, drNeed, chmNeed, stkNeed, unDrNeed, cipNeed, hosNeed, selectedDCRCap;
    private ApiInterface apiInterface;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private GPSTrack gpsTrack;
    private CommonUtilsMethods commonUtilsMethods;
    private List<Object> dataList;
    private DCRSelectionAdapter dcrSelectionAdapter;
    private HashMap<String, List<DCRModel>> selectedDCRMap;
    private List<Object> selectedDataList;
    private SelectedDCRAdapter selectedDCRAdapter;
    private StringBuilder selectedClusterName, selectedClusterCode, selectedDoctorName, selectedDoctorCode, selectedChemistName, selectedChemistCode, selectedDocSpeciality, selectedDocCategory, selectedDocClass, selectedDocCategoryCode;
    private JSONObject jsonObject;
    private List<String> localDocCodeList, localChmCodeList;
    private Set<String> populatedDCRList;
    private boolean isRouteSelected = false;
    private HashMap<String, List<DCRModel>> storedSelectedDcrMap;

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
            activityAddListBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddListBinding = ActivityAddListBinding.inflate(getLayoutInflater());
        setContentView(activityAddListBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getRequiredData();

        activityAddListBinding.backArrow.setOnClickListener(view -> {
            strClusterID = "";
            strClusterName = "";
            clusterChangeClearDCRSelection();
            StandardTourPlanActivity.selectedDcrMap = storedSelectedDcrMap;
            super.onBackPressed();
        });

        activityAddListBinding.btnCancel.setOnClickListener(view -> {
            strClusterID = "";
            strClusterName = "";
            clusterChangeClearDCRSelection();
            StandardTourPlanActivity.selectedDcrMap = storedSelectedDcrMap;
            super.onBackPressed();
        });

        activityAddListBinding.btnSave.setOnClickListener(view -> {
            if (strClusterName.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select) + " " + clusterCap);
            }
            /*else if(selectedDCRMap.get(Constants.DOCTOR) != null && selectedDCRMap.get(Constants.DOCTOR).isEmpty()
                    && selectedDCRMap.get(Constants.CHEMIST) != null && selectedDCRMap.get(Constants.CHEMIST).isEmpty()) {
                commonUtilsMethods.showToastMessage(this, "Please select any " + drCap + " or " + chmCap);
            }*/
            else if (selectedDCRMap.get(Constants.DOCTOR_MAS) != null && selectedDCRMap.get(Constants.DOCTOR_MAS).isEmpty()
                    && selectedDCRMap.get(Constants.CHEMIST_MAS) != null && selectedDCRMap.get(Constants.CHEMIST_MAS).isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_any) + drCap + " or " + chmCap);
//                String message = getString(R.string.please_select_any) + " " + drCap + " "
//                        + getString(R.string.or_text) + " " + chmCap;
//                commonUtilsMethods.showToastMessage(this, message);
            } else if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.approved))) {
                // commonUtilsMethods.showToastMessage(this, "Cannot Save, Already Approved");
                commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_save_already_approved));
            } else if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.waiting_for_approval))) {
//                commonUtilsMethods.showToastMessage(this, "Cannot Save, Waiting For Approval");
                commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_save_waiting_for_approval));
            } else {
                saveSelectedDCR();
            }
        });

        activityAddListBinding.selectedClusters.setOnClickListener(view -> {
            isRouteSelected = true;
            if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.approved))) {
//                commonUtilsMethods.showToastMessage(this, "Cannot Clear, Already Approved");
                commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_clear_already_approved));
            } else if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.waiting_for_approval))) {
//                commonUtilsMethods.showToastMessage(this, "Cannot Clear, Waiting For Approval");
                commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_clear_waiting_for_approval));
            } else {
                showMultiCluster();
            }
        });

        activityAddListBinding.tagTvDoctor.setOnClickListener(view -> {
            if (strClusterName.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select) + " " + clusterCap);
            } else {
//                selectedDCR = Constants.DOCTOR;
                selectedDCR = Constants.DOCTOR_MAS;
                selectedDCRCap = drCap;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvChemist.setOnClickListener(view -> {
            if (strClusterName.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select) + " " + clusterCap);
            } else {
//                selectedDCR = Constants.CHEMIST;
                selectedDCR = Constants.CHEMIST_MAS;
                selectedDCRCap = chmCap;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvStockist.setOnClickListener(view -> {
            if (strClusterName.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select) + " " + clusterCap);
            } else {
                selectedDCR = Constants.STOCKIEST_MAS;
                selectedDCRCap = stkCap;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvUndr.setOnClickListener(view -> {
            if (strClusterName.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select) + " " + clusterCap);
            } else {
                selectedDCR = Constants.UNLISTED_DOCTOR_MAS;
                selectedDCRCap = unDrCap;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvCip.setOnClickListener(view -> {
            if (strClusterName.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select) + " " + clusterCap);
            } else {
                selectedDCR = cipCap;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvHospital.setOnClickListener(view -> {
            if (strClusterName.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select) + " " + clusterCap);
            } else {
                selectedDCR = Constants.HOSPITAL;
                selectedDCRCap = hosCap;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.btnClear.setOnClickListener(view -> {
            if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.approved))) {
                //  commonUtilsMethods.showToastMessage(this, "Cannot Clear, Already Approved");
                commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_clear_already_approved));
            } else if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.waiting_for_approval))) {
                //commonUtilsMethods.showToastMessage(this, "Cannot Clear, Waiting For Approval");
                commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_clear_waiting_for_approval));
            } else {
                List<DCRModel> dcrModels = selectedDCRMap.get(selectedDCR);
                if (dcrModels != null && !dcrModels.isEmpty()) {
                    clearSelection(selectedDCR, false);
                } else {
//                    commonUtilsMethods.showToastMessage(this, "Nothing selected to clear");
                    commonUtilsMethods.showToastMessage(this, getString(R.string.nothing_selected_to_clear));
                }
            }
        });

        activityAddListBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(AddListActivity.this);
                if (dcrSelectionAdapter != null) {
                    dcrSelectionAdapter.getFilter().filter(searchString);
                } else if (strClusterID.isEmpty()) {
                    UtilityClass.hideKeyboard(AddListActivity.this);
                    if (!isRouteSelected) {
                        commonUtilsMethods.showToastMessage(AddListActivity.this, getString(R.string.please_select) + " " + clusterCap);
                        isRouteSelected = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityAddListBinding.stpDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        activityAddListBinding.stpDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                activityAddListBinding.stpDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                UtilityClass.hideKeyboard(AddListActivity.this);
            }
        });

//        selectedDCR = Constants.CHEMIST;
        selectedDCR = Constants.CHEMIST_MAS;
        populateDcrData();

//        selectedDCR = Constants.DOCTOR;
        selectedDCR = Constants.DOCTOR_MAS;
        populateDcrData();
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
        stpCap = StandardTourPlanActivity.stpCap;

        if (stpCap == null || stpCap.isEmpty()) {
            stpCap = getString(R.string.standard_tour_plan);
        }

        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        strClusterID = "";
        strClusterName = "";
        jsonObject = new JSONObject();
        if (clusterCap.isEmpty()) {
            activityAddListBinding.clusterTitle.setText("Cluster");
            activityAddListBinding.selectedClusters.setText("Select Cluster");
        } else {
            activityAddListBinding.clusterTitle.setText(clusterCap);
            activityAddListBinding.selectedClusters.setText("Select " + clusterCap);
        }
        localDocCodeList = new ArrayList<>();
        localChmCodeList = new ArrayList<>();
        populatedDCRList = new HashSet<>();

//        selectedDCR = Constants.DOCTOR;
        selectedDCR = Constants.DOCTOR_MAS;
        selectedDCRCap = drCap;
        selectedDCRMap = new HashMap<>();

        storedSelectedDcrMap = new HashMap<>();
        if (StandardTourPlanActivity.selectedDcrMap != null) {
            for (Map.Entry<String, List<DCRModel>> entry : StandardTourPlanActivity.selectedDcrMap.entrySet()) {
                if (entry != null && entry.getValue() != null) {
                    List<DCRModel> newList = new ArrayList<>();
                    for (DCRModel model : entry.getValue()) {
                        newList.add(new DCRModel(model));
                    }
                    storedSelectedDcrMap.put(entry.getKey(), newList);
                }
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mode = bundle.getString("MODE", "");
            dayID = bundle.getString("DAY_ID", "");
            dayCaption = bundle.getString("DAY_CAPTION", "");

            if (mode.equalsIgnoreCase(String.valueOf(CalendarAdapter.Mode.NEW))) {
                activityAddListBinding.title.setText(stpCap + " (" + dayCaption + ")");
                activityAddListBinding.btnSave.setText(getString(R.string.save));
            } else {
                activityAddListBinding.title.setText("Edit " + stpCap + " (" + dayCaption + ")");
                activityAddListBinding.btnSave.setText(getString(R.string.update));
                getLocalData();
            }
        }

        activityAddListBinding.tagTvDoctor.setText(drCap);
        activityAddListBinding.tagTvChemist.setText(chmCap);
        activityAddListBinding.tagTvStockist.setText(stkCap);
        activityAddListBinding.tagTvUndr.setText(unDrCap);
        activityAddListBinding.tagTvCip.setText(cipCap);
        activityAddListBinding.tagTvHospital.setText(hosCap);

        if (drNeed.equalsIgnoreCase("0")) {
            activityAddListBinding.tagTvDoctor.setVisibility(View.VISIBLE);
        } else {
            activityAddListBinding.tagTvDoctor.setVisibility(View.GONE);
        }
        if (chmNeed.equalsIgnoreCase("0")) {
            activityAddListBinding.tagTvChemist.setVisibility(View.VISIBLE);
        } else {
            activityAddListBinding.tagTvChemist.setVisibility(View.GONE);
        }
//        if(stkNeed.equalsIgnoreCase("0")) {
//            activityAddListBinding.tagTvStockist.setVisibility(View.VISIBLE);
//        }else {
        activityAddListBinding.tagTvStockist.setVisibility(View.GONE);
//        }
//        if(unDrNeed.equalsIgnoreCase("0")) {
//            activityAddListBinding.tagTvUndr.setVisibility(View.VISIBLE);
//        }else {
        activityAddListBinding.tagTvUndr.setVisibility(View.GONE);
//        }
//        if(cipNeed.equalsIgnoreCase("0")) {
//            activityAddListBinding.tagTvCip.setVisibility(View.VISIBLE);
//        }else {
        activityAddListBinding.tagTvCip.setVisibility(View.GONE);
//        }
//        if(hosNeed.equalsIgnoreCase("0")) {
//            activityAddListBinding.tagTvHospital.setVisibility(View.VISIBLE);
//        }else {
        activityAddListBinding.tagTvHospital.setVisibility(View.GONE);
//        }

        selectedClusterName = new StringBuilder();
        selectedClusterCode = new StringBuilder();
        selectedDoctorName = new StringBuilder();
        selectedDoctorCode = new StringBuilder();
        selectedChemistName = new StringBuilder();
        selectedChemistCode = new StringBuilder();
        selectedDocSpeciality = new StringBuilder();
        selectedDocCategory = new StringBuilder();
        selectedDocClass = new StringBuilder();
        selectedDocCategoryCode = new StringBuilder();
    }

    private void getLocalData() {
        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDay(dayID);
        strClusterID = stpOfflineDataTable.getClusterCode();
        strClusterName = stpOfflineDataTable.getClusterName();

        activityAddListBinding.selectedClusters.setText(strClusterName);
        updateClusterList();

        String[] docList = CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(",");
        docList = Arrays.stream(docList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
        localDocCodeList.addAll(Arrays.asList(docList));
        String[] chmList = CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getChemistCode()).split(",");
        chmList = Arrays.stream(chmList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
        localChmCodeList.addAll(Arrays.asList(chmList));

        setDCRSelectedForEdit();
    }

    private void updateDCRSelectionUI() {
        activityAddListBinding.tagTvDoctor.setBackground(null);
        activityAddListBinding.tagTvChemist.setBackground(null);
        activityAddListBinding.tagTvStockist.setBackground(null);
        activityAddListBinding.tagTvUndr.setBackground(null);
        activityAddListBinding.tagTvCip.setBackground(null);
        activityAddListBinding.tagTvHospital.setBackground(null);

        activityAddListBinding.tagTvDoctor.setTextColor(getColor(R.color.dark_purple));
        activityAddListBinding.tagTvChemist.setTextColor(getColor(R.color.dark_purple));
        activityAddListBinding.tagTvStockist.setTextColor(getColor(R.color.dark_purple));
        activityAddListBinding.tagTvUndr.setTextColor(getColor(R.color.dark_purple));
        activityAddListBinding.tagTvCip.setTextColor(getColor(R.color.dark_purple));
        activityAddListBinding.tagTvHospital.setTextColor(getColor(R.color.dark_purple));

        switch (selectedDCR) {
//            case Constants.DOCTOR:
            case Constants.DOCTOR_MAS:
                activityAddListBinding.tagTvDoctor.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvDoctor.setTextColor(getColor(R.color.white));
                break;
//            case Constants.CHEMIST:
            case Constants.CHEMIST_MAS:
                activityAddListBinding.tagTvChemist.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvChemist.setTextColor(getColor(R.color.white));
                break;
            case Constants.STOCKIEST_MAS:
                activityAddListBinding.tagTvStockist.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvStockist.setTextColor(getColor(R.color.white));
                break;
            case Constants.UNLISTED_DOCTOR_MAS:
                activityAddListBinding.tagTvUndr.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvUndr.setTextColor(getColor(R.color.white));
                break;
            case Constants.CIP:
                activityAddListBinding.tagTvCip.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvCip.setTextColor(getColor(R.color.white));
                break;
            case Constants.HOSPITAL:
                activityAddListBinding.tagTvHospital.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvHospital.setTextColor(getColor(R.color.white));
                break;
        }
    }

    private void setDCRSelectedForEdit() {
        List<String> dcrTAGList = new ArrayList<>();
//        dcrTAGList.add(Constants.DOCTOR);
        dcrTAGList.add(Constants.DOCTOR_MAS);
//        dcrTAGList.add(Constants.CHEMIST);
        dcrTAGList.add(Constants.CHEMIST_MAS);
        for (String selectedDCR : dcrTAGList) {
            List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
            if (dcrModelList != null) {
                for (DCRModel dcrModel : dcrModelList) {
                    if (mode.equalsIgnoreCase(String.valueOf(CalendarAdapter.Mode.EDIT))) {
//                        if(selectedDCR.equalsIgnoreCase(Constants.DOCTOR) && localDocCodeList != null && localDocCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.DOCTOR)) {
                        if (selectedDCR.equalsIgnoreCase(Constants.DOCTOR_MAS) && localDocCodeList != null && localDocCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.DOCTOR_MAS)) {
                            dcrModel.setSelected(true);
//                        }else if(selectedDCR.equalsIgnoreCase(Constants.CHEMIST) && localChmCodeList != null && localChmCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.CHEMIST)) {
                        } else if (selectedDCR.equalsIgnoreCase(Constants.CHEMIST_MAS) && localChmCodeList != null && localChmCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.CHEMIST_MAS)) {
                            dcrModel.setSelected(true);
                        }
                    }
                    if (dcrModel.isSelected()) {
                        checkBoxClickListener.onSelected(dcrModel, selectedDCR);
                    }
                }
            }
        }
    }

    private void populateDcrData() {
        List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
        HashMap<String, List<DCRModel>> clusterXDcrMap = new HashMap<>();
        HashMap<String, String> clusterMap = new HashMap<>();
        selectedDCRMap.put(selectedDCR, new ArrayList<>());
        dataList = new ArrayList<>();

        String[] clusterCodes = CommonUtilsMethods.removeLastComma(strClusterID).split(",");
        String[] clusterNames = CommonUtilsMethods.removeLastComma(strClusterName).split(",");
        for (int index = 0; index < clusterCodes.length; index++) {
            if (!clusterCodes[index].isEmpty() && !clusterNames[index].isEmpty()) {
                clusterMap.put(clusterCodes[index], clusterNames[index]);
            }
        }

        if (dcrModelList != null) {
            for (DCRModel dcrModel : dcrModelList) {
                if (strClusterID.toLowerCase().contains(dcrModel.getTownCode().toLowerCase())) {
                    if (!clusterXDcrMap.containsKey(dcrModel.getTownCode())) {
                        clusterXDcrMap.put(dcrModel.getTownCode(), new ArrayList<>());
                    }
                    List<DCRModel> dcrModels = clusterXDcrMap.get(dcrModel.getTownCode());
                    if (dcrModels == null) {
                        dcrModels = new ArrayList<>();
                    }
                    dcrModels.add(dcrModel);
                    if (mode.equalsIgnoreCase(String.valueOf(CalendarAdapter.Mode.EDIT))) {
//                        if(selectedDCR.equalsIgnoreCase(Constants.DOCTOR) && localDocCodeList != null && localDocCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.DOCTOR)) {
                        if (selectedDCR.equalsIgnoreCase(Constants.DOCTOR_MAS) && localDocCodeList != null && localDocCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.DOCTOR_MAS)) {
                            dcrModel.setSelected(true);
//                        }else if(selectedDCR.equalsIgnoreCase(Constants.CHEMIST) && localChmCodeList != null && localChmCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.CHEMIST)) {
                        } else if (selectedDCR.equalsIgnoreCase(Constants.CHEMIST_MAS) && localChmCodeList != null && localChmCodeList.contains(dcrModel.getCode()) && populatedDCRList != null && !populatedDCRList.contains(Constants.CHEMIST_MAS)) {
                            dcrModel.setSelected(true);
                        }
                    }
                    if (dcrModel.isSelected()) {
                        checkBoxClickListener.onSelected(dcrModel, selectedDCR);
                    }
                    clusterXDcrMap.put(dcrModel.getTownCode(), dcrModels);
                }
            }

            if (mode.equalsIgnoreCase(String.valueOf(CalendarAdapter.Mode.EDIT)) && !strClusterName.isEmpty()) {
//                if(selectedDCR.equalsIgnoreCase(Constants.DOCTOR) && localDocCodeList != null) {
                if (selectedDCR.equalsIgnoreCase(Constants.DOCTOR_MAS) && localDocCodeList != null) {
                    localDocCodeList = new ArrayList<>();
//                    populatedDCRList.add(Constants.DOCTOR);
                    populatedDCRList.add(Constants.DOCTOR_MAS);
//                }else if(selectedDCR.equalsIgnoreCase(Constants.CHEMIST) && localChmCodeList != null) {
                } else if (selectedDCR.equalsIgnoreCase(Constants.CHEMIST_MAS) && localChmCodeList != null) {
                    localChmCodeList = new ArrayList<>();
//                    populatedDCRList.add(Constants.CHEMIST);
                    populatedDCRList.add(Constants.CHEMIST_MAS);
                }
            }
        }

        List<Map.Entry<String, String>> clusterEntries = new ArrayList<>(clusterMap.entrySet());
        Collections.sort(clusterEntries, Map.Entry.comparingByValue());

        for (Map.Entry<String, String> entry : clusterEntries) {
            String clusterCode = entry.getKey();
            dataList.add(new ClusterModel(clusterCode, clusterMap.get(clusterCode)));
            List<DCRModel> dcrModels = clusterXDcrMap.get(clusterCode);
            if (dcrModels != null && !dcrModels.isEmpty()) {
                dcrModels.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
                dataList.addAll(dcrModels);
            } else {
                dataList.add(new NoDataModel(getString(R.string.no) + " " + selectedDCRCap + " " + getString(R.string.found)));
            }
        }

        if (dataList.isEmpty()) {
            if (activityAddListBinding.selectedClusters.getText().toString().isEmpty() || activityAddListBinding.selectedClusters.getText().toString().trim().equalsIgnoreCase("Select Cluster")) {
                activityAddListBinding.tvNoData.setText(getString(R.string.select) + clusterCap + getString(R.string.to_view_list));
            } else {
                activityAddListBinding.tvNoData.setText(getString(R.string.no_data_to_view));
            }
            activityAddListBinding.noData.setVisibility(View.VISIBLE);
            activityAddListBinding.llDcrSelection.setVisibility(View.GONE);
            activityAddListBinding.cvRightPane.setVisibility(View.GONE);
        } else {
            activityAddListBinding.noData.setVisibility(View.GONE);
            activityAddListBinding.llDcrSelection.setVisibility(View.VISIBLE);
            activityAddListBinding.cvRightPane.setVisibility(View.VISIBLE);
            dcrSelectionAdapter = new DCRSelectionAdapter(this, dataList, checkBoxClickListener, selectedDCR, mode, dayCaption, dayID);
            RecyclerView.LayoutManager dcrSelectionLayoutManager = new LinearLayoutManager(this);
            activityAddListBinding.rvDcrSelection.setLayoutManager(dcrSelectionLayoutManager);
            activityAddListBinding.rvDcrSelection.setAdapter(dcrSelectionAdapter);

            switch (selectedDCR) {
//                case Constants.DOCTOR:
                case Constants.DOCTOR_MAS:
                    activityAddListBinding.tvDcrSpec.setVisibility(View.VISIBLE);
                    activityAddListBinding.tvDcrCatXVisit.setVisibility(View.VISIBLE);
                    break;
//                case Constants.CHEMIST:
                case Constants.CHEMIST_MAS:
                    activityAddListBinding.tvDcrSpec.setVisibility(View.GONE);
                    activityAddListBinding.tvDcrCatXVisit.setVisibility(View.GONE);
                    break;
            }

            populateSelectedDcr();
        }
    }

    private final DCRSelectionAdapter.CheckBoxClickListener checkBoxClickListener = new DCRSelectionAdapter.CheckBoxClickListener() {
        @Override
        public void onSelected(DCRModel dcrModel, String selectedDCR) {
            if (!selectedDCRMap.containsKey(selectedDCR)) {
                selectedDCRMap.put(selectedDCR, new ArrayList<>());
            }
            List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
            if (selectedDCRModels == null) {
                selectedDCRModels = new ArrayList<>();
            }
            selectedDCRModels.add(dcrModel);
            selectedDCRMap.put(selectedDCR, selectedDCRModels);
            updateSelectedDCRList();
        }

        @Override
        public void onDeSelected(DCRModel dcrModel, String selectedDCR) {
            if (selectedDCRMap.containsKey(selectedDCR)) {
                List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
                if (selectedDCRModels != null) {
                    selectedDCRModels.remove(dcrModel);
                    selectedDCRMap.put(selectedDCR, selectedDCRModels);
                    updateSelectedDCRList();
                }
            }
        }
    };

    private void populateSelectedDcr() {
        selectedDataList = new ArrayList<>();

        switch (selectedDCR) {
//            case Constants.DOCTOR:
            case Constants.DOCTOR_MAS:
                activityAddListBinding.tvSelectedDcr.setText(getString(R.string.selected) + drCap);
                break;
//            case Constants.CHEMIST:
            case Constants.CHEMIST_MAS:
                activityAddListBinding.tvSelectedDcr.setText(getString(R.string.selected) + chmCap);
                break;
            case Constants.STOCKIEST_MAS:
                activityAddListBinding.tvSelectedDcr.setText(getString(R.string.selected) + stkCap);
                break;
            case Constants.UNLISTED_DOCTOR_MAS:
                activityAddListBinding.tvSelectedDcr.setText(getString(R.string.selected) + unDrCap);
                break;
            case Constants.CIP:
                activityAddListBinding.tvSelectedDcr.setText(getString(R.string.selected) + cipCap);
                break;
            case Constants.HOSPITAL:
                activityAddListBinding.tvSelectedDcr.setText(getString(R.string.selected) + hosCap);
                break;

        }
        activityAddListBinding.tvSelectedDcrCount.setText("(0)");

        selectedDCRAdapter = new SelectedDCRAdapter(this, selectedDataList, selectedDCR, deleteClickListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        activityAddListBinding.rvSelectedDcrList.setAdapter(selectedDCRAdapter);
        activityAddListBinding.rvSelectedDcrList.setLayoutManager(layoutManager);

        updateSelectedDCRList();
    }

    private void updateSelectedDCRList() {
        if (selectedDCRAdapter != null) {
            HashMap<String, List<DCRModel>> selectedClusterXDcrMap = new HashMap<>();
            HashMap<String, String> selectedDCRClusterMap = new HashMap<>();
            selectedDataList = new ArrayList<>();
            List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
            if (selectedDCRModels != null) {
                if (!selectedDCRModels.isEmpty()) {
                    activityAddListBinding.btnClear.setText(getString(R.string.clear_selected) + selectedDCRCap);
                    activityAddListBinding.btnClear.setVisibility(View.VISIBLE);
                    for (DCRModel dcrModel : selectedDCRModels) {
                        if (!selectedDCRClusterMap.containsKey(dcrModel.getTownCode())) {
                            selectedDCRClusterMap.put(dcrModel.getTownCode(), dcrModel.getTownName());
                            selectedClusterXDcrMap.put(dcrModel.getTownCode(), new ArrayList<>());
                        }
                        List<DCRModel> dcrModels = selectedClusterXDcrMap.get(dcrModel.getTownCode());
                        if (dcrModels == null) {
                            dcrModels = new ArrayList<>();
                        }
                        dcrModels.add(dcrModel);
                        selectedClusterXDcrMap.put(dcrModel.getTownCode(), dcrModels);
                    }
                } else {
                    activityAddListBinding.btnClear.setVisibility(View.GONE);
                }
            }

            List<Map.Entry<String, String>> clusterEntries = new ArrayList<>(selectedDCRClusterMap.entrySet());
            Collections.sort(clusterEntries, Map.Entry.comparingByValue());

            for (Map.Entry<String, String> entry : clusterEntries) {
                String clusterCode = entry.getKey();
                selectedDataList.add(new ClusterModel(clusterCode, selectedDCRClusterMap.get(clusterCode)));
                List<DCRModel> dcrModels = selectedClusterXDcrMap.get(clusterCode);
                if (dcrModels != null && !dcrModels.isEmpty()) {
                    dcrModels.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
                }
                selectedDataList.addAll(dcrModels);
            }

            activityAddListBinding.tvSelectedDcrCount.setText("(" + (selectedDataList.size() - selectedDCRClusterMap.size()) + ")");
            selectedDCRAdapter.updateList(selectedDataList);
        }
    }

    private final SelectedDCRAdapter.DeleteClickListener deleteClickListener = (dcrModel, selectedDCR) -> {

        if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.approved))) {
            //  commonUtilsMethods.showToastMessage(this, "Cannot Delete, Already Approved");
            commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_delete_already_approved));
        } else if (SharedPref.getStpStatus(this).equalsIgnoreCase(getString(R.string.waiting_for_approval))) {
            //commonUtilsMethods.showToastMessage(this, "Cannot Delete, Waiting For Approval");
            commonUtilsMethods.showToastMessage(this, getString(R.string.cannot_delete_waiting_for_approval));
        } else {
            List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
            if (dcrModelList != null && !dcrModelList.isEmpty()) {
                for (int index = 0; index < dcrModelList.size(); index++) {
                    DCRModel oldDcrModel = dcrModelList.get(index);
                    if (oldDcrModel.getCode().equals(dcrModel.getCode())) {
                        oldDcrModel.setSelected(false);
                        dcrModelList.set(index, oldDcrModel);
                        break;
                    }
                }
                populateDcrData();
            }

            if (selectedDCRMap.containsKey(selectedDCR)) {
                List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
                if (selectedDCRModels != null) {
                    selectedDCRModels.remove(dcrModel);
                    selectedDCRMap.put(selectedDCR, selectedDCRModels);
                    updateSelectedDCRList();
                }
            }

            if (activityAddListBinding.etSearch.getText() != null && !activityAddListBinding.etSearch.getText().toString().trim().isEmpty()) {
                activityAddListBinding.etSearch.setText("");
            }
        }

    };

    private void clusterChangeClearDCRSelection() {
        List<String> dcrTAGList = new ArrayList<>();
//        dcrTAGList.add(Constants.DOCTOR);
        dcrTAGList.add(Constants.DOCTOR_MAS);
//        dcrTAGList.add(Constants.CHEMIST);
        dcrTAGList.add(Constants.CHEMIST_MAS);
//        dcrTAGList.add(Constants.STOCKIEST);
//        dcrTAGList.add(Constants.UNLISTED_DOCTOR);
//        dcrTAGList.add(Constants.CIP);
//        dcrTAGList.add(Constants.HOSPITAL);
        for (String dcr : dcrTAGList) {
            clearSelection(dcr, true);
        }
    }

    private void clearSelection(String dcr, boolean clusterChange) {
        List<DCRModel> dcrModels = selectedDCRMap.get(dcr);
        List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(dcr);
        if (dcrModelList != null && !dcrModelList.isEmpty() && dcrModels != null && !dcrModels.isEmpty()) {
            for (DCRModel dcrModel : dcrModelList) {
                for (DCRModel selectedDCRModel : dcrModels) {
                    if (selectedDCRModel.getCode().equals(dcrModel.getCode())) {
                        if (!clusterChange || !strClusterID.contains(dcrModel.getTownCode())) {
                            dcrModel.setSelected(false);
                            String plannedForName = dcrModel.getPlannedForName().replaceAll(dayCaption + ",", "");
//                            dcrModel.setPlannedForName(plannedForName.isEmpty()? "-" : plannedForName);
                            dcrModels.remove(selectedDCRModel);
                        }
                        break;
                    }
                }
            }
            selectedDCRMap.put(dcr, dcrModels);
        }
        activityAddListBinding.btnClear.setVisibility(View.GONE);
        populateDcrData();
    }

    private void showMultiCluster() {
        selectedClusterList.clear();
        activityAddListBinding.etSearch.setText("");
        activityAddListBinding.stpAddListNavigation.etSearch.setText("");
        activityAddListBinding.stpAddListNavigation.txtClDone.setVisibility(View.VISIBLE);
        activityAddListBinding.stpAddListNavigation.wkRecyelerView.setVisibility(View.VISIBLE);
        activityAddListBinding.stpAddListNavigation.wkListView.setVisibility(View.GONE);
        activityAddListBinding.stpDrawer.openDrawer(GravityCompat.END);
        SharedPref.getWrkAreaName(this);
        if (SharedPref.getWrkAreaName(this).isEmpty()) {
            activityAddListBinding.stpAddListNavigation.tvSearchheader.setText("Cluster");
        } else {
            activityAddListBinding.stpAddListNavigation.tvSearchheader.setText(SharedPref.getWrkAreaName(this));
        }

        updateClusterList();
        MultiClusterAdapter multiClusterAdapter = new MultiClusterAdapter(this, multiple_cluster_list, new OnClusterClicklistener() {
            @Override
            public void classCampaignItem_addClass(Multicheckclass_clust classGroup) {
                selectedClusterList.add(classGroup);
            }

            @Override
            public void classCampaignItem_removeClass(Multicheckclass_clust classGroup) {
                selectedClusterList.remove(classGroup);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        activityAddListBinding.stpAddListNavigation.wkRecyelerView.setLayoutManager(linearLayoutManager);
        activityAddListBinding.stpAddListNavigation.wkRecyelerView.setAdapter(multiClusterAdapter);

        activityAddListBinding.stpAddListNavigation.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(AddListActivity.this);
                multiClusterAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityAddListBinding.stpAddListNavigation.txtClDone.setOnClickListener(view -> {
            activityAddListBinding.stpDrawer.closeDrawer(GravityCompat.END);
            UtilityClass.hideKeyboard(this);
            if (!selectedClusterList.isEmpty()) {
                String selectedUsers = "", selectedId = "";
                strClusterName = "";
                strClusterID = "";
                for (Multicheckclass_clust multiCheckClassCluster : multiple_cluster_list) {
                    if (multiCheckClassCluster.isChecked()) {
                        selectedUsers = selectedUsers + multiCheckClassCluster.getStrname() + ",";
                        selectedId = selectedId + multiCheckClassCluster.getStrid() + ",";
                        strClusterID = selectedId;
                        strClusterName = selectedUsers;
                    }
                }
            } else {
                strClusterName = "";
                strClusterID = "";
            }
//            selectedDCRMap = new HashMap<>();
            activityAddListBinding.selectedClusters.setText(strClusterName);
            clusterChangeClearDCRSelection();
        });

        activityAddListBinding.stpAddListNavigation.cancelImg.setOnClickListener(view -> {
            activityAddListBinding.stpDrawer.closeDrawer(GravityCompat.END);
        });
    }

    private void updateClusterList() {
        try {
            multiple_cluster_list.clear();
            selectedClusterList.clear();
            JSONArray workTypeArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray2.length(); i++) {
                JSONObject Object1 = workTypeArray2.getJSONObject(i);
                if (("," + strClusterID + ",").contains("," + Object1.getString("Code") + ",")) {
                    Multicheckclass_clust multicheckclassClust = new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", true);
                    multiple_cluster_list.add(multicheckclassClust);
                    selectedClusterList.add(multicheckclassClust);
                } else {
                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", false));
                }
            }
        } catch (Exception e) {
            Log.e("Work plan", "updateClusterList: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addTerritories(JsonObject planObj) {

        String[] terrCodes = selectedClusterCode.toString().split(",");
        String[] terrNames = selectedClusterName.toString().split(",");

        for (int i = 0; i < terrCodes.length; i++) {
            if (terrCodes[i].trim().isEmpty()) continue;

            JsonObject terrObj = new JsonObject();
            terrObj.addProperty("Code", terrCodes[i].trim());
            terrObj.addProperty("Name", terrNames.length > i ? terrNames[i].trim() : "");
            terrObj.addProperty("Speciality_Name", "");
            terrObj.addProperty("Category_Name", "");
            terrObj.addProperty("Class_Name", "");

            planObj.getAsJsonArray("Territory").add(terrObj);
        }
    }

    private void addDoctors(JsonObject planObj) {

        // --- Selected values ---
        String[] drCodes = selectedDoctorCode.toString().split(",");
        String[] drNames = selectedDoctorName.toString().split(",");
        String[] terrCodes = selectedClusterCode.toString().split(",");
        String[] terrNames = selectedClusterName.toString().split(",");

        // --- Load masters ---
        JSONArray jsonDocMas = masterDataDao
                .getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getSfCode(this))
                .getMasterSyncDataJsonArray();

        JSONArray jsonClass = masterDataDao
                .getMasterDataTableOrNew(Constants.CLASS)
                .getMasterSyncDataJsonArray();

        // --- Build Class map ---
        Map<String, String> classMap = new HashMap<>();
        if (jsonClass != null) {
            for (int i = 0; i < jsonClass.length(); i++) {
                JSONObject cls = jsonClass.optJSONObject(i);
                if (cls == null) continue;

                classMap.put(
                        cls.optString("Class_Code"),
                        cls.optString("Class_Name")
                );
            }
        }

        // --- Loop doctors ---
        for (int i = 0; i < drCodes.length; i++) {

            String drCode = drCodes[i].trim();
            if (drCode.isEmpty()) continue;

            String classCode = "";
            String className = "";

            // 🔎 Find doctor's Class_Code
            if (jsonDocMas != null) {
                for (int j = 0; j < jsonDocMas.length(); j++) {
                    JSONObject doc = jsonDocMas.optJSONObject(j);
                    if (doc == null) continue;

                    if (drCode.equals(doc.optString("Code"))) {
                        classCode = doc.optString("Doc_Class_ShortName");
                        break;
                    }
                }
            }

            // 🎯 Get Class_Name


            // --- Build doctor JSON ---
            JsonObject docObj = new JsonObject();
            docObj.addProperty("Code", drCode);
            docObj.addProperty("Name", drNames.length > i ? drNames[i].trim() : "");
            docObj.addProperty("Territory_Name", terrNames.length > 0 ? terrNames[0] : "");
            docObj.addProperty("Territory_Code", terrCodes.length > 0 ? terrCodes[0] : "");
            docObj.addProperty("Speciality_Name", selectedDocSpeciality.toString().replace(",", ""));
            docObj.addProperty("Category_Name", selectedDocCategory.toString().replace(",", ""));
            docObj.addProperty("Class_Name", classCode);

            planObj.getAsJsonArray("Doctor").add(docObj);
        }
    }


    //    private void addDoctors(JsonObject planObj, JSONObject row) {
//
//        String[] drCodes = selectedDoctorCode.toString().split(",");
//        String[] drNames = selectedDoctorName.toString().split(",");
//        String[] terrCodes = selectedClusterCode.toString().split(",");
//        String[] terrNames = selectedClusterName.toString().split(",");
//
//        for (int i = 0; i < drCodes.length; i++) {
//            if (drCodes[i].trim().isEmpty()) continue;
//
//            JsonObject docObj = new JsonObject();
//            docObj.addProperty("Code", drCodes[i].trim());
//            docObj.addProperty("Name", drNames.length > i ? drNames[i].trim() : "");
//            docObj.addProperty("Territory_Name", terrNames.length > 0 ? terrNames[0] : "");
//            docObj.addProperty("Territory_Code", terrCodes.length > 0 ? terrCodes[0] : "");
//            docObj.addProperty("Speciality_Name", selectedDocSpeciality.toString().replace(",",""));
//            docObj.addProperty("Category_Name", selectedDocCategory.toString().replace(",",""));
//            docObj.addProperty("Class_Name", className != null ? className : "");
//
//            planObj.getAsJsonArray("Doctor").add(docObj);
//        }
//    }
    private void addChemists(JsonObject planObj) {

        if (selectedChemistCode == null || selectedChemistName == null) return;

        String[] chCodes = selectedChemistCode.toString().split(",");
        String[] chNames = selectedChemistName.toString().split(",");

        String[] terrCodes = selectedClusterCode != null
                ? selectedClusterCode.toString().split(",")
                : new String[0];

        String[] terrNames = selectedClusterName != null
                ? selectedClusterName.toString().split(",")
                : new String[0];

        // To avoid duplicate chemists
        Set<String> addedChemistCodes = new HashSet<>();

        for (int i = 0; i < chCodes.length; i++) {

            String code = chCodes[i].trim();
            if (code.isEmpty()) continue;
            if (!addedChemistCodes.add(code)) continue;
            JsonObject chmObj = new JsonObject();
            chmObj.addProperty("Code", code);
            chmObj.addProperty("Name", chNames.length > i ? chNames[i].trim() : "");

            chmObj.addProperty(
                    "Territory_Name",
                    terrNames.length > 0 ? terrNames[0].trim() : ""
            );

            chmObj.addProperty(
                    "Territory_Code",
                    terrCodes.length > 0 ? terrCodes[0].trim() : ""
            );

            chmObj.addProperty("Speciality_Name", "");
            chmObj.addProperty("Category_Name", "");
            chmObj.addProperty("Class_Name", "");

            planObj.getAsJsonArray("Chemist").add(chmObj);
        }
    }

    private void addHospitals(JsonObject planObj) {

        if (!planObj.has("Hospital")) {
            planObj.add("Hospital", new JsonArray());
        }

    }

    private String getCategoryCodeFromDocMaster(JSONArray jsonDocMas, String doctorCode) {

        if (jsonDocMas == null || doctorCode == null) return "";

        for (int i = 0; i < jsonDocMas.length(); i++) {
            try {
                JSONObject docObj = jsonDocMas.getJSONObject(i);

                if (doctorCode.equals(docObj.optString("DoctorCode"))) {
                    return docObj.optString("CategoryCode", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private String getNoOfVisitFromCategory(JSONArray jsonCate, String categoryCode) {

        if (jsonCate == null || categoryCode == null) return "0";

        for (int i = 0; i < jsonCate.length(); i++) {
            try {
                JSONObject cateObj = jsonCate.getJSONObject(i);

                if (categoryCode.equals(cateObj.optString("CategoryCode"))) {
                    return cateObj.optString("No_of_visit", "0");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return "0";
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

    private void saveSelectedDCR() {
        if (selectedDCRMap != null && !selectedDCRMap.isEmpty()) {
            selectedClusterName = new StringBuilder();
            selectedClusterCode = new StringBuilder();
            selectedDoctorName = new StringBuilder();
            selectedDoctorCode = new StringBuilder();
            selectedChemistName = new StringBuilder();
            selectedChemistCode = new StringBuilder();
            selectedDocSpeciality = new StringBuilder();
            selectedDocCategory = new StringBuilder();
            selectedDocClass = new StringBuilder();
            selectedDocCategoryCode = new StringBuilder();
            for (String selectedDCR : selectedDCRMap.keySet()) {
                List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
                List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
                if (dcrModelList != null && !dcrModelList.isEmpty() && selectedDCRModels != null && !selectedDCRModels.isEmpty()) {
                    for (int index = 0; index < dcrModelList.size(); index++) {
                        DCRModel dcrModel = dcrModelList.get(index);
                        for (DCRModel selectedDcrModel : selectedDCRModels) {
                            if (dcrModel.getCode().equals(selectedDcrModel.getCode()) && selectedDcrModel.isSelected()) {
                                if (!selectedClusterCode.toString().contains(selectedDcrModel.getTownCode())) {
                                    selectedClusterCode.append(selectedDcrModel.getTownCode()).append(",");
                                    selectedClusterName.append(selectedDcrModel.getTownName()).append(",");
                                }
                                switch (selectedDCR) {
//                                    case Constants.DOCTOR:
                                    case Constants.DOCTOR_MAS:
                                        if (SharedPref.getOneBuild(AddListActivity.this).equalsIgnoreCase("0")) {
                                            selectedDoctorCode.append(selectedDcrModel.getCode()).append(",");
                                            selectedDoctorName.append(selectedDcrModel.getName()).append(",");
                                            selectedDocSpeciality.append(selectedDcrModel.getSpeciality()).append(",");
                                            selectedDocCategory.append(selectedDcrModel.getCategory()).append(",");
                                            selectedDocClass.append(selectedDcrModel.getClass()).append(",");
                                            selectedDocCategoryCode.append(selectedDcrModel.getCode()).append(",");

                                        } else {
                                            selectedDoctorCode.append(selectedDcrModel.getCode()).append(",");
                                            selectedDoctorName.append(selectedDcrModel.getName()).append(",");
                                        }
                                        break;
//                                    case Constants.CHEMIST:
                                    case Constants.CHEMIST_MAS:
                                        selectedChemistCode.append(selectedDcrModel.getCode()).append(",");
                                        selectedChemistName.append(selectedDcrModel.getName()).append(",");
                                        break;
                                }
                                dcrModel.setSelected(false);
                                if (dcrModel.getPlannedForName().equals("-"))
                                    dcrModel.setPlannedForName("");
                                if (!dcrModel.getPlannedForCode().contains(dayID)) {
                                    dcrModel.setPlannedForName(dcrModel.getPlannedForName() + dayCaption + ",");
                                    dcrModel.setPlannedForCode(dcrModel.getPlannedForCode() + dayID + ",");
                                } else {
                                    if (!dcrModel.isSelected()) {
                                        dcrModel.setPlannedForName(dcrModel.getPlannedForName().replaceAll(dayCaption + ",", ""));
                                        dcrModel.setPlannedForCode(dcrModel.getPlannedForCode().replaceAll(dayID + ",", ""));
                                    }
                                }
                                break;
                            } else {
                                Log.d("data", "saveSelectedDCR: " + selectedDcrModel.getPlannedForName());
                            }
                        }
                    }
                }
            }
            if (SharedPref.getOneBuild(AddListActivity.this).equalsIgnoreCase("0")) {

//                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
                JSONArray jsonDoc_mas = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getSfCode(AddListActivity.this)).getMasterSyncDataJsonArray();
                JSONArray jsonChm_mas = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + SharedPref.getSfCode(AddListActivity.this)).getMasterSyncDataJsonArray();
                JSONArray jsonCluster = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getSfCode(AddListActivity.this)).getMasterSyncDataJsonArray();
                JSONArray jsonCate = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();

//                JSONObject jsonObject = new JSONObject();
//                if (jsonObject.length() > 0) {

                    try {
                        Map<String, JsonObject> planMap = new LinkedHashMap<>();

//                        for (int a = 0; a < jsonObject.length(); a++) {
//
//                            JSONObject row = jsonObject.getJSONObject(String.valueOf(a));

                            String key = dayID;
                            JsonObject planObj;

                            if (!planMap.containsKey(key)) {
                                planObj = new JsonObject();
                                planObj.addProperty("Plan_Code", key);
                                planObj.addProperty("Plan_Name", dayCaption);
                                planObj.addProperty("Plan_SName", key);
                                planObj.add("Territory", new JsonArray());
                                planObj.add("Doctor", new JsonArray());
                                planObj.add("Chemist", new JsonArray());
                                planObj.add("Hospital", new JsonArray());
                                planMap.put(key, planObj);
                                addTerritories(planObj);
                                addDoctors(planObj);
                                addChemists(planObj);
                                addHospitals(planObj);
                            }
//                        }

                        JsonArray stpDetailsArr = new JsonArray();
                        for (JsonObject obj : planMap.values()) {
                            stpDetailsArr.add(obj);
                        }
                        JsonArray categorySummaryArr = new JsonArray();


//                        for (DocCategoryModel cat : dataList) {

                        JsonObject catObj = new JsonObject();
//                            catObj.addProperty("Category_Code", jsonDoc_mas.optString("CategoryCode", "").toString().replace(",",""));
                        String categoryCode = getCategoryCodeFromDocMaster(jsonDoc_mas, "");
                        String noOfVisit = getNoOfVisitFromCategory(jsonCate, "");
                        catObj.addProperty("CategoryCode", categoryCode.replace(",", ""));
                        catObj.addProperty("Category_Name", selectedDocCategory.toString().replace(",", ""));
                        catObj.addProperty("No_of_Visit", noOfVisit.replace(",", ""));
                        catObj.addProperty("Planned", selectedDocCategory.length() + " (" + selectedDocCategory.length() + ")");
                        catObj.addProperty("Total", selectedDocCategory.length() + " (" + selectedDocCategory.length() + ")");
                        categorySummaryArr.add(catObj);
//                        }


                        jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("division_code", CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this)));
                        jsonObject.put("StpFlag", "0");
                        jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                        jsonObject.put("Planned_Territory_Count", getSelectedCount(String.valueOf(selectedClusterCode)) + " (" + jsonCluster.length() + ")");
                        jsonObject.put("Planned_Doctor_Count", getSelectedCount(String.valueOf(selectedDoctorCode)) + " (" + jsonDoc_mas.length() + ")");
                        jsonObject.put("Planned_Chemist_Count", getSelectedCount(String.valueOf(selectedChemistCode)) + " (" + jsonChm_mas.length() + ")");
                        jsonObject.put("Planned_Hospital_Count", "0" + " (" + "0" + ")");
                        jsonObject.put("tableName", "save_stp");
                        JSONArray categorySummaryJsonArr = new JSONArray(categorySummaryArr.toString());

                        jsonObject.put("STP_Category_Summary", categorySummaryJsonArr);

                        JSONArray stpDetailsJsonArr = new JSONArray(stpDetailsArr.toString());

                        jsonObject.put("STP_Details", stpDetailsJsonArr);

                        Log.d("STP_JSON", jsonObject.toString());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                }
            } else {
                try {
                    jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                    jsonObject.put("sfcode", SharedPref.getSfCode(this));
                    jsonObject.put("DivCode", SharedPref.getDivisionCode(this));
                    jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                    jsonObject.put("Rsf", SharedPref.getHqCode(this));
                    jsonObject.put("town_code", strClusterID);
                    jsonObject.put("town_name", strClusterName);
                    jsonObject.put("Doctor_Id", selectedDoctorCode.toString());
                    jsonObject.put("Doctor_Name", selectedDoctorName.toString());
                    jsonObject.put("Chemist_Id", selectedChemistCode.toString());
                    jsonObject.put("Chemist_Name", selectedChemistName.toString());
                    jsonObject.put("Plan_Name", dayCaption);
                    jsonObject.put("Plan_SName", dayID);
                    jsonObject.put("Plan_Code", "" + dayID.charAt(dayID.length() - 1));
                    jsonObject.put("StpFlag", "3");
                    jsonObject.put("tableName", "save_stp");
                    jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                    Log.v("json_save_stp", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (SharedPref.getOneBuild(AddListActivity.this).equalsIgnoreCase("0")) {
                stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, strClusterID, strClusterName, selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), selectedDocSpeciality.toString(), selectedDocCategory.toString(), selectedDocClass.toString(), selectedDocCategoryCode.toString(), jsonObject.toString(), 3, "1"));
            } else {
                stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, strClusterID, strClusterName, selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), jsonObject.toString(), 3, "1"));

            }

            if (UtilityClass.isNetworkAvailable(this)) {
                APICallSaveSTP();
            } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.stp_saved_locally));
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    private void APICallSaveSTP() {
        if (UtilityClass.isNetworkAvailable(this)) {
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/stp");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.v("stp save", "--res--" + response.body());
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject jsonObject1 = new JSONObject(response.body().toString());
                            if (jsonObject1.optString("success").equals("true")) {
                                commonUtilsMethods.showToastMessage(AddListActivity.this, dayCaption + " " + getString(R.string.saved_successfully));
                                if (SharedPref.getOneBuild(AddListActivity.this).equalsIgnoreCase("0")) {
                                    stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, strClusterID, strClusterName, selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), selectedDocSpeciality.toString(), selectedDocCategory.toString(), selectedDocClass.toString(), selectedDocCategoryCode.toString(), jsonObject.toString(), 3, "0"));

                                } else {
                                    stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, strClusterID, strClusterName, selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), jsonObject.toString(), 3, "0"));

                                }
                            } else {
                                commonUtilsMethods.showToastMessage(AddListActivity.this, getString(R.string.stp_saved_locally));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        commonUtilsMethods.showToastMessage(AddListActivity.this, getString(R.string.stp_saved_locally));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    commonUtilsMethods.showToastMessage(AddListActivity.this, getString(R.string.stp_saved_locally));
                }
            });
        } else {
            commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, new IntentFilter("com.saneforce.SYNC_COMPLETED"));
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
                startActivity(new Intent(AddListActivity.this, StandardTourPlanActivity.class));
                finish();
            }
        }
    };

}