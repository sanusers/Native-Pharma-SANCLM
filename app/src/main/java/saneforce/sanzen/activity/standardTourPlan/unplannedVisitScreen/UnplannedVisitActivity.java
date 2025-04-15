package saneforce.sanzen.activity.standardTourPlan.unplannedVisitScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter.DCRSelectionAdapter;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.model.ClusterModel;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.model.NoDataModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.StandardTourPlanActivity;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityUnplannedVisitBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.storage.SharedPref;

public class UnplannedVisitActivity extends AppCompatActivity {

    private ActivityUnplannedVisitBinding activityUnplannedVisitBinding;
    private String hqCode, drCap, chmCap, stkCap, unDrCap, cipCap, hosCap, drNeed, chmNeed, stkNeed, unDrNeed, cipNeed, hosNeed, selectedDCR;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private CommonUtilsMethods commonUtilsMethods;
    private List<Object> dataList;
    private DCRSelectionAdapter dcrSelectionAdapter;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUnplannedVisitBinding = ActivityUnplannedVisitBinding.inflate(getLayoutInflater());
        setContentView(activityUnplannedVisitBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getRequiredData();

        activityUnplannedVisitBinding.backArrow.setOnClickListener(v -> super.onBackPressed());

        activityUnplannedVisitBinding.tagTvDoctor.setOnClickListener(v -> {
            selectedDCR = Constants.DOCTOR;
            updateDCRSelectionUI();
            populateDcrData();
        });

        activityUnplannedVisitBinding.tagTvChemist.setOnClickListener(v -> {
            selectedDCR = Constants.CHEMIST;
            updateDCRSelectionUI();
            populateDcrData();
        });

        activityUnplannedVisitBinding.tagTvStockist.setOnClickListener(v -> {
            selectedDCR = Constants.STOCKIEST;
            updateDCRSelectionUI();
            populateDcrData();
        });

        activityUnplannedVisitBinding.tagTvUndr.setOnClickListener(v -> {
            selectedDCR = Constants.UNLISTED_DOCTOR;
            updateDCRSelectionUI();
            populateDcrData();
        });

        activityUnplannedVisitBinding.tagTvCip.setOnClickListener(v -> {
            selectedDCR = Constants.CIP;
            updateDCRSelectionUI();
            populateDcrData();
        });

        activityUnplannedVisitBinding.tagTvHospital.setOnClickListener(v -> {
            selectedDCR = Constants.HOSPITAL;
            updateDCRSelectionUI();
            populateDcrData();
        });

        activityUnplannedVisitBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if(searchString.isEmpty()) UtilityClass.hideKeyboard(UnplannedVisitActivity.this);
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
        drNeed = SharedPref.getDrNeed(this);
        chmNeed = SharedPref.getChmNeed(this);
        stkNeed = SharedPref.getStkNeed(this);
        unDrNeed = SharedPref.getUnlNeed(this);
        cipNeed = SharedPref.getCipNeed(this);
        hosNeed = SharedPref.getHospNeed(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);

        activityUnplannedVisitBinding.tagTvDoctor.setText(drCap);
        activityUnplannedVisitBinding.tagTvChemist.setText(chmCap);
        activityUnplannedVisitBinding.tagTvStockist.setText(stkCap);
        activityUnplannedVisitBinding.tagTvUndr.setText(unDrCap);
        activityUnplannedVisitBinding.tagTvCip.setText(cipCap);
        activityUnplannedVisitBinding.tagTvHospital.setText(hosCap);

        selectedDCR = Constants.DOCTOR;

        if(drNeed.equalsIgnoreCase("0")) {
            activityUnplannedVisitBinding.tagTvDoctor.setVisibility(View.VISIBLE);
        }else {
            activityUnplannedVisitBinding.tagTvDoctor.setVisibility(View.GONE);
        }
        if(chmNeed.equalsIgnoreCase("0")) {
            activityUnplannedVisitBinding.tagTvChemist.setVisibility(View.VISIBLE);
        }else {
            activityUnplannedVisitBinding.tagTvChemist.setVisibility(View.GONE);
        }
//        if(stkNeed.equalsIgnoreCase("0")) {
//            activityUnplannedVisitBinding.tagTvStockist.setVisibility(View.VISIBLE);
//        }else {
        activityUnplannedVisitBinding.tagTvStockist.setVisibility(View.GONE);
//        }
//        if(unDrNeed.equalsIgnoreCase("0")) {
//            activityUnplannedVisitBinding.tagTvUndr.setVisibility(View.VISIBLE);
//        }else {
        activityUnplannedVisitBinding.tagTvUndr.setVisibility(View.GONE);
//        }
//        if(cipNeed.equalsIgnoreCase("0")) {
//            activityUnplannedVisitBinding.tagTvCip.setVisibility(View.VISIBLE);
//        }else {
        activityUnplannedVisitBinding.tagTvCip.setVisibility(View.GONE);
//        }
//        if(hosNeed.equalsIgnoreCase("0")) {
//            activityUnplannedVisitBinding.tagTvHospital.setVisibility(View.VISIBLE);
//        }else {
        activityUnplannedVisitBinding.tagTvHospital.setVisibility(View.GONE);
//        }
    }

    private void updateDCRSelectionUI() {
        activityUnplannedVisitBinding.tagTvDoctor.setBackground(null);
        activityUnplannedVisitBinding.tagTvChemist.setBackground(null);
        activityUnplannedVisitBinding.tagTvStockist.setBackground(null);
        activityUnplannedVisitBinding.tagTvUndr.setBackground(null);
        activityUnplannedVisitBinding.tagTvCip.setBackground(null);
        activityUnplannedVisitBinding.tagTvHospital.setBackground(null);

        activityUnplannedVisitBinding.tagTvDoctor.setTextColor(getColor(R.color.dark_purple));
        activityUnplannedVisitBinding.tagTvChemist.setTextColor(getColor(R.color.dark_purple));
        activityUnplannedVisitBinding.tagTvStockist.setTextColor(getColor(R.color.dark_purple));
        activityUnplannedVisitBinding.tagTvUndr.setTextColor(getColor(R.color.dark_purple));
        activityUnplannedVisitBinding.tagTvCip.setTextColor(getColor(R.color.dark_purple));
        activityUnplannedVisitBinding.tagTvHospital.setTextColor(getColor(R.color.dark_purple));

        switch (selectedDCR){
            case Constants.DOCTOR:
                activityUnplannedVisitBinding.tagTvDoctor.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityUnplannedVisitBinding.tagTvDoctor.setTextColor(getColor(R.color.white));
                break;
            case Constants.CHEMIST:
                activityUnplannedVisitBinding.tagTvChemist.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityUnplannedVisitBinding.tagTvChemist.setTextColor(getColor(R.color.white));
                break;
            case Constants.STOCKIEST:
                activityUnplannedVisitBinding.tagTvStockist.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityUnplannedVisitBinding.tagTvStockist.setTextColor(getColor(R.color.white));
                break;
            case Constants.UNLISTED_DOCTOR:
                activityUnplannedVisitBinding.tagTvUndr.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityUnplannedVisitBinding.tagTvUndr.setTextColor(getColor(R.color.white));
                break;
            case Constants.CIP:
                activityUnplannedVisitBinding.tagTvCip.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityUnplannedVisitBinding.tagTvCip.setTextColor(getColor(R.color.white));
                break;
            case Constants.HOSPITAL:
                activityUnplannedVisitBinding.tagTvHospital.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_light_purple));
                activityUnplannedVisitBinding.tagTvHospital.setTextColor(getColor(R.color.white));
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
                if(!clusterXDcrMap.containsKey(dcrModel.getTownCode())) {
                    clusterXDcrMap.put(dcrModel.getTownCode(), new ArrayList<>());
                    clusterMap.put(dcrModel.getTownCode(), dcrModel.getTownName());
                }
                List<DCRModel> dcrModels = clusterXDcrMap.get(dcrModel.getTownCode());
                if(dcrModels == null) {
                    dcrModels = new ArrayList<>();
                }
                Set<String> plannedFor = new HashSet<>(Arrays.asList(CommonUtilsMethods.removeLastComma(dcrModel.getPlannedForCode()).split(",")));
                plannedFor.remove("");
                plannedFor.remove(null);
                if(selectedDCR.equalsIgnoreCase(Constants.DOCTOR) && dcrModel.getVisitFrequency() != plannedFor.size()) {
                    dcrModels.add(dcrModel);
                } else if(selectedDCR.equalsIgnoreCase(Constants.CHEMIST) && (dcrModel.getPlannedForCode().equalsIgnoreCase("") || dcrModel.getPlannedForCode().equalsIgnoreCase("-"))){
                    dcrModels.add(dcrModel);
                }
                clusterXDcrMap.put(dcrModel.getTownCode(), dcrModels);
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
                dataList.addAll(dcrModels);
            }else {
                if(selectedDCR.equalsIgnoreCase(Constants.DOCTOR)) {
                    dataList.add(new NoDataModel("All " + drCap + " are selected"));
                } else if(selectedDCR.equalsIgnoreCase(Constants.CHEMIST)) {
                    dataList.add(new NoDataModel("All " + chmCap + " are selected"));
                }
            }
        }

        if(dataList.isEmpty()) {
            activityUnplannedVisitBinding.tvNoData.setText("No Data To View");
            activityUnplannedVisitBinding.noData.setVisibility(View.VISIBLE);
            activityUnplannedVisitBinding.llDcrSelection.setVisibility(View.GONE);
        }else {
            activityUnplannedVisitBinding.noData.setVisibility(View.GONE);
            activityUnplannedVisitBinding.llDcrSelection.setVisibility(View.VISIBLE);
            dcrSelectionAdapter = new DCRSelectionAdapter(this, dataList, selectedDCR);
            RecyclerView.LayoutManager dcrSelectionLayoutManager = new LinearLayoutManager(this);
            activityUnplannedVisitBinding.rvDcrSelection.setLayoutManager(dcrSelectionLayoutManager);
            activityUnplannedVisitBinding.rvDcrSelection.setAdapter(dcrSelectionAdapter);

            switch (selectedDCR){
                case Constants.DOCTOR:
                    activityUnplannedVisitBinding.tvDcrSpec.setVisibility(View.VISIBLE);
                    activityUnplannedVisitBinding.tvDcrCatXVisit.setVisibility(View.VISIBLE);
                    activityUnplannedVisitBinding.tvDcrPlannedFor.setVisibility(View.VISIBLE);
                    break;
                case Constants.CHEMIST:
                    activityUnplannedVisitBinding.tvDcrSpec.setVisibility(View.GONE);
                    activityUnplannedVisitBinding.tvDcrCatXVisit.setVisibility(View.GONE);
                    activityUnplannedVisitBinding.tvDcrPlannedFor.setVisibility(View.GONE);
                    break;
            }
        }
    }

}