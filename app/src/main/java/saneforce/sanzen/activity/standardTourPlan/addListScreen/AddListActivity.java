package saneforce.sanzen.activity.standardTourPlan.addListScreen;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private String hqCode, strClusterName, strClusterID, mode, dayID, dayCaption, drCap, chmCap, stkCap, unDrCap, cipCap, hosCap, clusterCap, stpCap, selectedDCR, drNeed, chmNeed, stkNeed, unDrNeed, cipNeed, hosNeed;
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
    private StringBuilder selectedClusterName, selectedClusterCode, selectedDoctorName, selectedDoctorCode, selectedChemistName, selectedChemistCode;
    private JSONObject jsonObject;
    private List<String> localDocCodeList, localChmCodeList;

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
            strClusterID = "";
            strClusterName = "";
            clusterChangeClearDCRSelection();
            super.onBackPressed();
        });

        activityAddListBinding.btnCancel.setOnClickListener(v -> {
            strClusterID = "";
            strClusterName = "";
            clusterChangeClearDCRSelection();
            super.onBackPressed();
        });

        activityAddListBinding.btnSave.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                saveSelectedDCR();
            }
        });

        activityAddListBinding.selectedClusters.setOnClickListener(v -> showMultiCluster());

        activityAddListBinding.tagTvDoctor.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.DOCTOR;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvChemist.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.CHEMIST;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvStockist.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.STOCKIEST;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvUndr.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.UNLISTED_DOCTOR;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvCip.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.CIP;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.tagTvHospital.setOnClickListener(v -> {
            if(selectedClusterList.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_select_cluster));
            }else {
                selectedDCR = Constants.HOSPITAL;
                activityAddListBinding.etSearch.setText("");
                updateDCRSelectionUI();
                populateDcrData();
            }
        });

        activityAddListBinding.btnClear.setOnClickListener(v -> {
            List<DCRModel> dcrModels = selectedDCRMap.get(selectedDCR);
            if(dcrModels != null && !dcrModels.isEmpty()) {
                clearSelection(selectedDCR);
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
//        stpCap = SharedPref.getSTPCap(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        strClusterID = "";
        strClusterName = "";
        jsonObject = new JSONObject();
        if(clusterCap.isEmpty()) {
            activityAddListBinding.selectedClusters.setText("Select Cluster");
        }else {
            activityAddListBinding.selectedClusters.setText("Select " + clusterCap);
        }
        localDocCodeList = new ArrayList<>();
        localChmCodeList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mode = bundle.getString("MODE", "");
            dayID = bundle.getString("DAY_ID", "");
            dayCaption = bundle.getString("DAY_CAPTION", "");

            if(mode.equalsIgnoreCase("NEW")) {
                activityAddListBinding.title.setText(getString(R.string.standard_tour_plan) + " (" + dayCaption + ")");
            } else {
                activityAddListBinding.title.setText( "Edit " + getString(R.string.standard_tour_plan) + " (" + dayCaption + ")");
                getLocalData();
            }
        }

        activityAddListBinding.tagTvDoctor.setText(drCap);
        activityAddListBinding.tagTvChemist.setText(chmCap);
        activityAddListBinding.tagTvStockist.setText(stkCap);
        activityAddListBinding.tagTvUndr.setText(unDrCap);
        activityAddListBinding.tagTvCip.setText(cipCap);
        activityAddListBinding.tagTvHospital.setText(hosCap);

        selectedDCR = Constants.DOCTOR;
        selectedDCRMap = new HashMap<>();

        if(drNeed.equalsIgnoreCase("0")) {
            activityAddListBinding.tagTvDoctor.setVisibility(View.VISIBLE);
        }else {
            activityAddListBinding.tagTvDoctor.setVisibility(View.GONE);
        }
        if(chmNeed.equalsIgnoreCase("0")) {
            activityAddListBinding.tagTvChemist.setVisibility(View.VISIBLE);
        }else {
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
        selectedDCRMap = new HashMap<>();
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
                    if(mode.equalsIgnoreCase("EDIT")) {
                        if(selectedDCR.equalsIgnoreCase(Constants.DOCTOR) && localDocCodeList != null && localDocCodeList.contains(dcrModel.getCode())) {
                            dcrModel.setSelected(true);
                        } else if(selectedDCR.equalsIgnoreCase(Constants.CHEMIST) && localChmCodeList != null && localChmCodeList.contains(dcrModel.getCode())) {
                            dcrModel.setSelected(true);
                        }
                    }
                    if(dcrModel.isSelected()) {
                        checkBoxClickListener.onSelected(dcrModel, selectedDCR);
                    }
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
        if(selectedDCRAdapter != null) {
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

    private void clusterChangeClearDCRSelection() {
        List<String> dcrTAGList = new ArrayList<>();
        dcrTAGList.add(Constants.DOCTOR);
        dcrTAGList.add(Constants.CHEMIST);
//        dcrTAGList.add(Constants.STOCKIEST);
//        dcrTAGList.add(Constants.UNLISTED_DOCTOR);
//        dcrTAGList.add(Constants.CIP);
//        dcrTAGList.add(Constants.HOSPITAL);
        for (String dcr : dcrTAGList) {
            clearSelection(dcr);
        }
    }

    private void clearSelection(String dcr) {
        List<DCRModel> dcrModels = selectedDCRMap.get(dcr);
        List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(dcr);
        if(dcrModelList != null && !dcrModelList.isEmpty() && dcrModels != null && !dcrModels.isEmpty()) {
            for (DCRModel dcrModel : dcrModelList) {
                for (DCRModel selectedDCRModel : dcrModels) {
                    if(selectedDCRModel.getCode().equals(dcrModel.getCode())) {
                        if(!strClusterID.contains(dcrModel.getTownCode())) {
                            dcrModel.setSelected(false);
                            dcrModels.remove(selectedDCRModel);
                        }
                        break;
                    }
                }
            }
            selectedDCRMap.put(dcr, new ArrayList<>());
        }
        populateDcrData();
    }

    private void showMultiCluster() {
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
            selectedDCRMap = new HashMap<>();
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
            selectedClusterName = new StringBuilder();
            selectedClusterCode = new StringBuilder();
            selectedDoctorName = new StringBuilder();
            selectedDoctorCode = new StringBuilder();
            selectedChemistName = new StringBuilder();
            selectedChemistCode = new StringBuilder();
            for (String selectedDCR : selectedDCRMap.keySet()) {
                List<DCRModel> dcrModelList = StandardTourPlanActivity.selectedDcrMap.get(selectedDCR);
                List<DCRModel> selectedDCRModels = selectedDCRMap.get(selectedDCR);
                if(dcrModelList != null && !dcrModelList.isEmpty() && selectedDCRModels != null && !selectedDCRModels.isEmpty()) {
                    for (int index = 0; index<dcrModelList.size(); index++) {
                        DCRModel dcrModel = dcrModelList.get(index);
                        for (DCRModel selectedDcrModel : selectedDCRModels) {
                            if(dcrModel.getCode().equals(selectedDcrModel.getCode()) && selectedDcrModel.isSelected()) {
                                if(!selectedClusterCode.toString().contains(selectedDcrModel.getTownCode())) {
                                    selectedClusterCode.append(selectedDcrModel.getTownCode()).append(",");
                                    selectedClusterName.append(selectedDcrModel.getTownName()).append(",");
                                }
                                switch (selectedDCR){
                                    case Constants.DOCTOR:
                                        selectedDoctorCode.append(selectedDcrModel.getCode()).append(",");
                                        selectedDoctorName.append(selectedDcrModel.getName()).append(",");
                                        break;
                                    case Constants.CHEMIST:
                                        selectedChemistCode.append(selectedDcrModel.getCode()).append(",");
                                        selectedChemistName.append(selectedDcrModel.getName()).append(",");
                                        break;
                                }
                                dcrModel.setSelected(false);
                                if(dcrModel.getPlannedForName().equals("-"))
                                    dcrModel.setPlannedForName("");
                                dcrModel.setPlannedForName(dcrModel.getPlannedForName() + dayCaption + ",");
                                dcrModel.setPlannedForCode(dcrModel.getPlannedForCode() + dayID + ",");
                                break;
                            }
                        }
                    }
                }
            }
            try {
                jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("DivCode", SharedPref.getDivisionCode(this));
                jsonObject.put("Rsf", SharedPref.getHqCode(this));
                jsonObject.put("town_code", selectedClusterCode.toString());
                jsonObject.put("town_name", selectedClusterName.toString());
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

            stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, selectedClusterCode.toString(), selectedClusterName.toString(), selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), jsonObject.toString(), "1"));

            if(UtilityClass.isNetworkAvailable(this)) {
                APICallSaveSTP();
            }else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.stp_saved_locally));
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    private void APICallSaveSTP() {
        if(UtilityClass.isNetworkAvailable(this)) {
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/stp");
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.v("stp save", "--res--" + response.body());
                    try {
                        if(response.isSuccessful() && response.body() != null) {
                            JSONObject jsonObject1 = new JSONObject(response.body().toString());
                            if(jsonObject1.optString("success").equals("true")) {
                                commonUtilsMethods.showToastMessage(AddListActivity.this, dayCaption + " " +getString(R.string.saved_successfully));
                                stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, selectedClusterCode.toString(), selectedClusterName.toString(), selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), jsonObject.toString(), "0"));
                            }else {
                                stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, selectedClusterCode.toString(), selectedClusterName.toString(), selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), jsonObject.toString(), "1"));
                                commonUtilsMethods.showToastMessage(AddListActivity.this, getString(R.string.stp_saved_locally));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, selectedClusterCode.toString(), selectedClusterName.toString(), selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), jsonObject.toString(), "1"));
                        commonUtilsMethods.showToastMessage(AddListActivity.this, getString(R.string.stp_saved_locally));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    commonUtilsMethods.showToastMessage(AddListActivity.this, getString(R.string.stp_saved_locally));
                    stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, dayCaption, selectedClusterCode.toString(), selectedClusterName.toString(), selectedDoctorCode.toString(), selectedDoctorName.toString(), selectedChemistCode.toString(), selectedChemistName.toString(), jsonObject.toString(), "1"));
                }
            });
        }else {
            commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
        }
    }

}