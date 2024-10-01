package saneforce.sanzen.activity.standardTourPlan.addListScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.MultiClusterAdapter;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.OnClusterClicklistener;
import saneforce.sanzen.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DocCategoryModel;
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
    GPSTrack gpsTrack;
    CommonUtilsMethods commonUtilsMethods;

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
        getRequiredData();

        activityAddListBinding.backArrow.setOnClickListener(v -> {
            super.onBackPressed();
        });

        activityAddListBinding.btnCancel.setOnClickListener(v -> {
            super.onBackPressed();
        });

        activityAddListBinding.btnSave.setOnClickListener(v -> {

        });

        activityAddListBinding.selectedClusters.setOnClickListener(v -> {
            showMultiClusterAlter();
        });

        activityAddListBinding.tagTvDoctor.setOnClickListener(v -> {

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
//        stpCap = SharedPref.getSTPCap(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        gpsTrack = new GPSTrack(this);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        strClusterID = "";
        strClusterName = "";
        if(SharedPref.getWrkAreaName(this).isEmpty()) {
            activityAddListBinding.selectedClusters.setText("Select Cluster");
        }else {
            activityAddListBinding.selectedClusters.setText("Select " + SharedPref.getWrkAreaName(this));
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
    }

    public void showMultiClusterAlter() {
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


//    private void getDcrData() {
//        totalDocCodeList = new HashSet<>();
//        totalChmCodeList = new HashSet<>();
//        totalStkCodeList = new HashSet<>();
//        totalUnDrCodeList = new HashSet<>();
//        totalCipCodeList = new HashSet<>();
//        totalHosCodeList = new HashSet<>();
//
//        try {
//            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(selectedDCR + hqCode).getMasterSyncDataJsonArray();
//            if(jsonArray.length()>0) {
//                for (int i = 0; i<jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    try {
//                        String dcrCode = jsonObject.optString("Code");
//                        String category = jsonObject.optString("Category");
//                        String categoryCode = jsonObject.optString("CategoryCode");
//                        String visitCount = jsonObject.optString("Tlvst");
//                        if(!dcrCode.isEmpty()) {
//                            switch (selectedDCR){
//                                case Constants.DOCTOR:
//                                    if(!totalDocCodeList.contains(dcrCode)) {
//                                        totalDocCodeList.add(dcrCode);
//                                        if(docCategoryModelMap.containsKey(categoryCode)) {
//                                            DocCategoryModel docCategoryModel = docCategoryModelMap.get(categoryCode);
//                                            if(docCategoryModel != null) {
//                                                docCategoryModel.incrementDocCount();
//                                                docCategoryModelMap.put(categoryCode, docCategoryModel);
//                                            }
//                                        }
//                                    }
//                                    break;
//                                case Constants.CHEMIST:
//                                    totalChmCodeList.add(dcrCode);
//                                    break;
//                                case Constants.STOCKIEST:
//                                    totalStkCodeList.add(dcrCode);
//                                    break;
//                                case Constants.UNLISTED_DOCTOR:
//                                    totalUnDrCodeList.add(dcrCode);
//                                    break;
//                                case Constants.CIP:
//                                    totalCipCodeList.add(dcrCode);
//                                    break;
//                                case Constants.HOSPITAL:
//                                    totalHosCodeList.add(dcrCode);
//                                    break;
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}