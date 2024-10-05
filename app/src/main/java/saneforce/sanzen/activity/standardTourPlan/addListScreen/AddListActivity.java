package saneforce.sanzen.activity.standardTourPlan.addListScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.MultiClusterAdapter;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.OnClusterClicklistener;
import saneforce.sanzen.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter.DCRSelectionAdapter;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter.SelectedDCRAdapter;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.StandardTourPlanActivity;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityAddListBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class AddListActivity extends AppCompatActivity {

    private ActivityAddListBinding activityAddListBinding;
    private ArrayList<Multicheckclass_clust> selectedClusterList = new ArrayList<>();
    private ArrayList<Multicheckclass_clust> multiple_cluster_list = new ArrayList<>();
    private String hqCode, strClusterName, strClusterID, dayID, dayCaption, drCap, chmCap, stkCap, unDrCap, cipCap, hosCap, clusterCap, stpCap, selectedDCR;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private GPSTrack gpsTrack;
    private CommonUtilsMethods commonUtilsMethods;
    private List<Object> dataList;
    private DCRSelectionAdapter dcrSelectionAdapter;
    private HashMap<String, List<DCRModel>> selectedDCRMap;
    private List<Object> selectedDataList;
    private SelectedDCRAdapter selectedDCRAdapter;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddListBinding = ActivityAddListBinding.inflate(getLayoutInflater());
        setContentView(activityAddListBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getRequiredData();

        activityAddListBinding.backArrow.setOnClickListener(v -> {
            super.onBackPressed();
        });

        activityAddListBinding.btnCancel.setOnClickListener(v -> {
            super.onBackPressed();
        });

        activityAddListBinding.btnSave.setOnClickListener(v -> {
            saveSelectedDCR();
        });

        activityAddListBinding.selectedClusters.setOnClickListener(v -> {
            showMultiClusterAlter();
        });

        activityAddListBinding.tagTvDoctor.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.DOCTOR;
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvChemist.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.CHEMIST;
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvStockist.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.STOCKIEST;
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvUndr.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.UNLISTED_DOCTOR;
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvCip.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.CIP;
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvHospital.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.HOSPITAL;
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.btnClear.setOnClickListener(v -> {
            List<DCRModel> dcrModels = selectedDCRMap.get(selectedDCR);
            if(dcrModels != null && !dcrModels.isEmpty()) {
                clearSelection();
            }else {
                commonUtilsMethods.showToastMessage(this, "Nothing selected to clear");
            }
        });

        activityAddListBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if(searchString.isEmpty()) UtilityClass.hideKeyboard(AddListActivity.this);
                dcrSelectionAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
//        stpCap = SharedPref.getSTPCap(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        strClusterID = "";
        strClusterName = "";
        if(clusterCap.isEmpty()) {
            activityAddListBinding.selectedClusters.setText("Select Cluster");
        }else {
            activityAddListBinding.selectedClusters.setText("Select " + clusterCap);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            dayID = bundle.getString("DAY_ID", "");
            dayCaption = bundle.getString("DAY_CAPTION", "");

            activityAddListBinding.title.setText(getString(R.string.standard_tour_plan) + " (" + dayCaption + ")");
        }

        activityAddListBinding.tagTvDoctor.setText(drCap);
        activityAddListBinding.tagTvChemist.setText(chmCap);
        activityAddListBinding.tagTvStockist.setText(stkCap);
        activityAddListBinding.tagTvUndr.setText(unDrCap);
        activityAddListBinding.tagTvCip.setText(cipCap);
        activityAddListBinding.tagTvHospital.setText(hosCap);

        selectedDCR = Constants.DOCTOR;
        selectedDCRMap = new HashMap<>();
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

        switch (selectedDCR){
            case Constants.DOCTOR:
                activityAddListBinding.tagTvDoctor.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvDoctor.setTextColor(getColor(R.color.white));
                break;
            case Constants.CHEMIST:
                activityAddListBinding.tagTvChemist.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvChemist.setTextColor(getColor(R.color.white));
                break;
            case Constants.STOCKIEST:
                activityAddListBinding.tagTvStockist.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityAddListBinding.tagTvStockist.setTextColor(getColor(R.color.white));
                break;
            case Constants.UNLISTED_DOCTOR:
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

    private void populateDcrData() {
        List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
        HashMap<String, List<DCRModel>> clusterXDcrMap = new HashMap<>();
        HashMap<String, String> clusterMap = new HashMap<>();
        dataList = new ArrayList<>();

        if(dcrModelList != null) {
            for (DCRModel dcrModel : dcrModelList) {
                if(strClusterID.toLowerCase().contains(dcrModel.getTownCode().toLowerCase())) {
                    if(!clusterXDcrMap.containsKey(dcrModel.getTownCode())) {
                        clusterXDcrMap.put(dcrModel.getTownCode(), new ArrayList<>());
                        clusterMap.put(dcrModel.getTownCode(), dcrModel.getTownName());
                    }
                    List<DCRModel> dcrModels = clusterXDcrMap.get(dcrModel.getTownCode());
                    if(dcrModels == null) {
                        dcrModels = new ArrayList<>();
                    }
                    dcrModels.add(dcrModel);
                    clusterXDcrMap.put(dcrModel.getTownCode(), dcrModels);
                }
            }
        }

        List<Map.Entry<String, String>> clusterEntries = new ArrayList<>(clusterMap.entrySet());
        Collections.sort(clusterEntries, Map.Entry.comparingByValue());

        for (Map.Entry<String, String> entry : clusterEntries) {
            String clusterCode = entry.getKey();
            dataList.add(new ClusterModel(clusterCode, clusterMap.get(clusterCode)));
            List<DCRModel> dcrModels = clusterXDcrMap.get(clusterCode);
            if(dcrModels != null && !dcrModels.isEmpty()) {
                dcrModels.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            }
            dataList.addAll(dcrModels);
        }

        if(dataList.isEmpty()) {
            if(activityAddListBinding.selectedClusters.getText().toString().isEmpty() || activityAddListBinding.selectedClusters.getText().toString().trim().equalsIgnoreCase("Select Cluster")) {
                activityAddListBinding.tvNoData.setText("Select Cluster to view list");
            }else {
                activityAddListBinding.tvNoData.setText("No Data To View");
            }
            activityAddListBinding.noData.setVisibility(View.VISIBLE);
            activityAddListBinding.llDcrSelection.setVisibility(View.GONE);
            activityAddListBinding.cvRightPane.setVisibility(View.GONE);
        }else {
            activityAddListBinding.noData.setVisibility(View.GONE);
            activityAddListBinding.llDcrSelection.setVisibility(View.VISIBLE);
            activityAddListBinding.cvRightPane.setVisibility(View.VISIBLE);
            dcrSelectionAdapter = new DCRSelectionAdapter(this, dataList, checkBoxClickListener, selectedDCR);
            RecyclerView.LayoutManager dcrSelectionLayoutManager = new LinearLayoutManager(this);
            activityAddListBinding.rvDcrSelection.setLayoutManager(dcrSelectionLayoutManager);
            activityAddListBinding.rvDcrSelection.setAdapter(dcrSelectionAdapter);

            switch (selectedDCR){
                case Constants.DOCTOR:
                    activityAddListBinding.tvDcrSpec.setVisibility(View.VISIBLE);
                    activityAddListBinding.tvDcrCatXVisit.setVisibility(View.VISIBLE);
                    break;
                case Constants.CHEMIST:
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
            if(!selectedDCRMap.containsKey(selectedDCR)) {
                selectedDCRMap.put(selectedDCR, new ArrayList<>());
            }
            List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
            if(selectedDCRModels == null) {
                selectedDCRModels = new ArrayList<>();
            }
            selectedDCRModels.add(dcrModel);
            selectedDCRMap.put(selectedDCR, selectedDCRModels);
            updateSelectedDCRList();
        }

        @Override
        public void onDeSelected(DCRModel dcrModel, String selectedDCR) {
            if(selectedDCRMap.containsKey(selectedDCR)) {
                List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
                if(selectedDCRModels != null) {
                    selectedDCRModels.remove(dcrModel);
                    selectedDCRMap.put(selectedDCR, selectedDCRModels);
                    updateSelectedDCRList();
                }
            }
        }
    };

    private void populateSelectedDcr() {
        selectedDataList = new ArrayList<>();

        switch (selectedDCR){
            case Constants.DOCTOR:
                activityAddListBinding.tvSelectedDcr.setText("Selected " + drCap);
                break;
            case Constants.CHEMIST:
                activityAddListBinding.tvSelectedDcr.setText("Selected " + chmCap);
                break;
            case Constants.STOCKIEST:
                activityAddListBinding.tvSelectedDcr.setText("Selected " + stkCap);
                break;
            case Constants.UNLISTED_DOCTOR:
                activityAddListBinding.tvSelectedDcr.setText("Selected " + unDrCap);
                break;
            case Constants.CIP:
                activityAddListBinding.tvSelectedDcr.setText("Selected " + cipCap);
                break;
            case Constants.HOSPITAL:
                activityAddListBinding.tvSelectedDcr.setText("Selected " + hosCap);
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
        HashMap<String, List<DCRModel>> selectedClusterXDcrMap = new HashMap<>();
        HashMap<String, String> selectedDCRClusterMap = new HashMap<>();
        selectedDataList = new ArrayList<>();
        List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
        if(selectedDCRModels != null) {
            if(!selectedDCRModels.isEmpty()) {
                activityAddListBinding.btnClear.setVisibility(View.VISIBLE);
                for (DCRModel dcrModel : selectedDCRModels) {
                    if(!selectedDCRClusterMap.containsKey(dcrModel.getTownCode())) {
                        selectedDCRClusterMap.put(dcrModel.getTownCode(), dcrModel.getTownName());
                        selectedClusterXDcrMap.put(dcrModel.getTownCode(), new ArrayList<>());
                    }
                    List<DCRModel> dcrModels = selectedClusterXDcrMap.get(dcrModel.getTownCode());
                    if(dcrModels == null) {
                        dcrModels = new ArrayList<>();
                    }
                    dcrModels.add(dcrModel);
                    selectedClusterXDcrMap.put(dcrModel.getTownCode(), dcrModels);
                }
            }else {
                activityAddListBinding.btnClear.setVisibility(View.GONE);
            }
        }

        List<Map.Entry<String, String>> clusterEntries = new ArrayList<>(selectedDCRClusterMap.entrySet());
        Collections.sort(clusterEntries, Map.Entry.comparingByValue());

        for (Map.Entry<String, String> entry : clusterEntries) {
            String clusterCode = entry.getKey();
            selectedDataList.add(new ClusterModel(clusterCode, selectedDCRClusterMap.get(clusterCode)));
            List<DCRModel> dcrModels = selectedClusterXDcrMap.get(clusterCode);
            if(dcrModels != null && !dcrModels.isEmpty()) {
                dcrModels.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            }
            selectedDataList.addAll(dcrModels);
        }

        activityAddListBinding.tvSelectedDcrCount.setText("(" + (selectedDataList.size() - selectedDCRClusterMap.size()) + ")");
        selectedDCRAdapter.updateList(selectedDataList);
    }

    private final SelectedDCRAdapter.DeleteClickListener deleteClickListener = (dcrModel, selectedDCR) -> {

        List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
        if(dcrModelList != null && !dcrModelList.isEmpty()) {
            for (int index = 0; index<dcrModelList.size(); index++) {
                DCRModel oldDcrModel = dcrModelList.get(index);
                if(oldDcrModel.getCode().equals(dcrModel.getCode())) {
                    oldDcrModel.setSelected(false);
                    dcrModelList.set(index, oldDcrModel);
                    break;
                }
            }
            populateDcrData();
        }

        if(selectedDCRMap.containsKey(selectedDCR)) {
            List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
            if(selectedDCRModels != null) {
                selectedDCRModels.remove(dcrModel);
                selectedDCRMap.put(selectedDCR, selectedDCRModels);
                updateSelectedDCRList();
            }
        }

        if(activityAddListBinding.etSearch.getText() != null && !activityAddListBinding.etSearch.getText().toString().trim().isEmpty()) {
            activityAddListBinding.etSearch.setText("");
        }

    };

    private void clearSelection() {
        List<DCRModel> dcrModels = selectedDCRMap.get(selectedDCR);
        List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
        if(dcrModelList != null && !dcrModelList.isEmpty() && dcrModels != null && !dcrModels.isEmpty()) {
            for (DCRModel dcrModel : dcrModelList) {
                for (DCRModel selectedDCRModel : dcrModels) {
                    if(selectedDCRModel.getCode().equals(dcrModel.getCode())) {
                        dcrModel.setSelected(false);
                        dcrModels.remove(selectedDCRModel);
                        break;
                    }
                }
            }
            selectedDCRMap.put(selectedDCR, new ArrayList<>());
            populateDcrData();
        }
    }

    private void showMultiClusterAlter() {
        selectedClusterList.clear();
        activityAddListBinding.stpAddListNavigation.etSearch.setText("");
        activityAddListBinding.stpAddListNavigation.txtClDone.setVisibility(View.VISIBLE);
        activityAddListBinding.stpAddListNavigation.wkRecyelerView.setVisibility(View.VISIBLE);
        activityAddListBinding.stpAddListNavigation.wkListView.setVisibility(View.GONE);
        activityAddListBinding.stpDrawer.openDrawer(GravityCompat.END);
        SharedPref.getWrkAreaName(this);
        if(SharedPref.getWrkAreaName(this).isEmpty()) {
            activityAddListBinding.stpAddListNavigation.tvSearchheader.setText("Cluster");
        }else {
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
                if(searchString.isEmpty()) UtilityClass.hideKeyboard(AddListActivity.this);
                multiClusterAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityAddListBinding.stpAddListNavigation.txtClDone.setOnClickListener(v -> {
            activityAddListBinding.stpDrawer.closeDrawer(GravityCompat.END);
            UtilityClass.hideKeyboard(this);
            if(!selectedClusterList.isEmpty()) {
                String selectedUsers = "", selectedId = "";
                strClusterName = "";
                strClusterID = "";
                for (Multicheckclass_clust multiCheckClassCluster : multiple_cluster_list) {
                    if(multiCheckClassCluster.isChecked()) {
                        selectedUsers = selectedUsers + multiCheckClassCluster.getStrname() + ",";
                        selectedId = selectedId + multiCheckClassCluster.getStrid() + ",";
                        strClusterID = selectedId;
                        strClusterName = selectedUsers;
                    }
                }
            }else {
                strClusterName = "";
                strClusterID = "";
            }
            activityAddListBinding.selectedClusters.setText(strClusterName);
            populateDcrData();
        });

        activityAddListBinding.stpAddListNavigation.cancelImg.setOnClickListener(view -> {
            activityAddListBinding.stpDrawer.closeDrawer(GravityCompat.END);
        });
    }

    private void updateClusterList() {
        try {
            multiple_cluster_list.clear();
            JSONArray workTypeArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i<workTypeArray2.length(); i++) {
                JSONObject Object1 = workTypeArray2.getJSONObject(i);
                if(("," + strClusterID + ",").contains("," + Object1.getString("Code") + ",")) {
                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", true));
                }else {
                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", false));
                }
            }
        } catch (Exception e) {
            Log.e("Work plan", "updateClusterList: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveSelectedDCR() {
        if(selectedDCRMap != null && !selectedDCRMap.isEmpty()) {
            for (String selectedDCR: selectedDCRMap.keySet()) {
                List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
                List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
                if(dcrModelList != null && !dcrModelList.isEmpty() && selectedDCRModels != null && !selectedDCRModels.isEmpty()) {
                    for (int index = 0; index<dcrModelList.size(); index++) {
                        DCRModel dcrModel = dcrModelList.get(index);
                        for (DCRModel selectedDcrModel : selectedDCRModels) {
                            if(dcrModel.getCode().equals(selectedDcrModel.getCode()) && selectedDcrModel.isSelected()) {
                                dcrModel.setSelected(false);
                                if(dcrModel.getPlannedForName().equals("-")) dcrModel.setPlannedForName("");
                                dcrModel.setPlannedForName(dcrModel.getPlannedForName() + dayCaption + ",");
                                dcrModel.setPlannedForCode(dcrModel.getPlannedForCode() + dayID + ",");
                                break;
                            }
                        }
                    }
                }
            }
        }
        finish();
    }

}