package saneforce.sanzen.activity.tourPlan.overview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.activity.tourPlan.overview.model.ClusterModel;
import saneforce.sanzen.activity.tourPlan.overview.model.DCRModel;
import saneforce.sanzen.activity.tourPlan.overview.model.DoctorCategoryModel;
import saneforce.sanzen.activity.tourPlan.overview.model.DoctorModel;
import saneforce.sanzen.activity.tourPlan.overview.model.WorkTypeModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityTourPlanOverviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class TourPlanOverviewActivity extends AppCompatActivity {
    private ActivityTourPlanOverviewBinding binding;
    private String title = "", hqCode = "";
    private boolean isOneBuild = false, drNeed = false, chmNeed = false;
    private ArrayList<ModelClass> modelClassList = new ArrayList<>();
    private ArrayList<OneBuildModelClass> oneBuildModelClassList = new ArrayList<>();
    private Map<String, WorkTypeModel> workTypeMaster = new HashMap<>();
    private Map<String, ClusterModel> clusterMaster = new HashMap<>();
    private Map<String, DoctorModel> doctorMaster = new HashMap<>();
    private Map<String, DCRModel> chemistMaster = new HashMap<>();
    private Map<String, DoctorCategoryModel> doctorCategoryMaster = new HashMap<>();
    private List<String> plannedDates = new ArrayList<>();
    private Map<String, List<String>> workTypePlanned = new HashMap<>();
    private Map<String, List<String>> clusterPlanned = new HashMap<>();
    private Map<String, List<String>> clusterCategoryPlanned = new HashMap<>();
    private int totalDays = 0;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourPlanOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backArrow.setOnClickListener(view -> finish());
        title = getString(R.string.tour_plan_overview);

        binding.tpDataNavigation.llClose.setOnClickListener(view -> {
            binding.tpDrawer.closeDrawer(GravityCompat.END);
        });

        getMasterData();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                String month = bundle.getString("month");
                title += " (" + month + ")";
                binding.tvTitle.setText(title);
                isOneBuild = bundle.getBoolean("is_one_build");
                drNeed = bundle.getBoolean("dr_need");
                chmNeed = bundle.getBoolean("chm_need");
                modelClassList = bundle.getParcelableArrayList("data", ModelClass.class);
                oneBuildModelClassList = bundle.getParcelableArrayList("one_build_data", OneBuildModelClass.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        prepareData();
        setData();
    }

    private void setData() {
        try {
            binding.plannedDays.setText(getString(R.string.total_planned_days) + plannedDates.size() + " / " + totalDays);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            binding.tvFieldWork.setText(String.valueOf(workTypePlanned.get("F").size()));
            binding.tvNonFieldWork.setText(String.valueOf(workTypePlanned.get("N").size()));
            binding.tvLeave.setText(String.valueOf(workTypePlanned.get("L").size()));
            binding.tvWeeklyOff.setText(String.valueOf(workTypePlanned.get("W").size()));
            binding.tvHoliday.setText(String.valueOf(workTypePlanned.get("H").size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            binding.tvHq.setText(String.valueOf(clusterCategoryPlanned.get("HQ").size()));
            binding.tvOs.setText(String.valueOf(clusterCategoryPlanned.get("OS").size()));
            binding.tvEx.setText(String.valueOf(clusterCategoryPlanned.get("EX").size()));
            binding.tvOsEx.setText(String.valueOf(clusterCategoryPlanned.get("OS-EX").size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMasterData() {
        hqCode = SharedPref.getHqCode(TourPlanOverviewActivity.this);
        RoomDB roomDB = RoomDB.getDatabase(TourPlanOverviewActivity.this);
        MasterDataDao masterDataDao = roomDB.masterDataDao();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                clusterMaster.put(jsonObject.optString("Code") ,new ClusterModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Territory_Cat")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                workTypeMaster.put(jsonObject.optString("Code") ,new WorkTypeModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("FWFlg"), jsonObject.optString("TerrSlFlg")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (drNeed) {
            try {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + hqCode).getMasterSyncDataJsonArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    doctorMaster.put(jsonObject.optString("Code"), new DoctorModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name"), jsonObject.optString("CategoryCode"), jsonObject.optString("Category"), jsonObject.optString("Tlvst")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    doctorCategoryMaster.put(jsonObject.optString("Code"), new DoctorCategoryModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Doc_Cat_Name"), jsonObject.optString("No_of_visit")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (chmNeed) {
            try {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + hqCode).getMasterSyncDataJsonArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    chemistMaster.put(jsonObject.optString("Code"), new DCRModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareData() {
        totalDays = 0;
        plannedDates = new ArrayList<>();
        workTypePlanned = new HashMap<>();
        workTypePlanned.put("F", new ArrayList<>());
        workTypePlanned.put("N", new ArrayList<>());
        workTypePlanned.put("W", new ArrayList<>());
        workTypePlanned.put("H", new ArrayList<>());
        workTypePlanned.put("L", new ArrayList<>());
        clusterCategoryPlanned.put("HQ", new ArrayList<>());
        clusterCategoryPlanned.put("OS", new ArrayList<>());
        clusterCategoryPlanned.put("EX", new ArrayList<>());
        clusterCategoryPlanned.put("OS-EX", new ArrayList<>());
        clusterPlanned = new HashMap<>();
        if (isOneBuild) {
            for (OneBuildModelClass day : oneBuildModelClassList) {
                try {
                    String dayNo = day.getDayNo();
                    if (dayNo != null && !dayNo.isEmpty()) {
                        totalDays++;
                    }
                    if (day.getSessionList() == null || day.getSessionList().isEmpty()) continue;
                    boolean isPlanned = false;
                    for (OneBuildModelClass.SessionList session : day.getSessionList()) {
                        try {
                            if (session == null || session.getWorkType() == null) continue;
                            if (session.getWorkType().getFWFlg() == null || session.getWorkType().getFWFlg().isEmpty()) continue;
                            if (!isPlanned) isPlanned = true;
                            if (session.getTerritories() != null && !session.getTerritories().isEmpty()) {
                                for (OneBuildModelClass.SessionList.SubClass cluster : session.getTerritories()) {
                                    if (cluster != null) {
                                        addClusterData(clusterPlanned, cluster.getCode(), dayNo);
                                    }
                                }
                            }
                            if ("F".equalsIgnoreCase(session.getWorkType().getFWFlg())) {
                                addData(workTypePlanned, "F", dayNo);
                                if (session.getDoctors() != null && !session.getDoctors().isEmpty()) {
                                    for (OneBuildModelClass.SessionList.SubClass doctorSub : session.getDoctors()) {
                                        if (doctorSub != null) {

                                        }
                                    }
                                }
                            } else if ("W".equalsIgnoreCase(session.getWorkType().getFWFlg())) {
                                addData(workTypePlanned, "W", dayNo);
                            } else if ("H".equalsIgnoreCase(session.getWorkType().getFWFlg())) {
                                addData(workTypePlanned, "H", dayNo);
                            } else if ("L".equalsIgnoreCase(session.getWorkType().getFWFlg())) {
                                addData(workTypePlanned, "L", dayNo);
                            } else {
                                addData(workTypePlanned, "N", dayNo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (isPlanned) {
                        plannedDates.add(dayNo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

        }
    }

    private void addData(Map<String, List<String>> dataMap, String flag, String data) {
        try {
            List<String> dates = dataMap.get(flag);
            if (dates == null || dates.isEmpty()) {
                dates = new ArrayList<>();
            }
            if (!dates.contains(data)) {
                dates.add(data);
            }
            dataMap.put(flag, dates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addClusterData(Map<String, List<String>> dataMap, String clusterCode, String data) {
        try {
            List<String> dates = dataMap.get(clusterCode);
            if (dates == null || dates.isEmpty()) {
                dates = new ArrayList<>();
            }
            if (!dates.contains(data)) {
                dates.add(data);
            }
            dataMap.put(clusterCode, dates);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ClusterModel clusterModel = clusterMaster.get(clusterCode);
            if (clusterModel != null) {
                String clusterCategory = "";
                String clusterCategoryCode = clusterModel.getCategoryCode();
                switch (clusterCategoryCode) {
                    case "1":
                        clusterCategory = "HQ";
                        break;
                    case "2":
                        clusterCategory = "EX";
                        break;
                    case "3":
                        clusterCategory = "OS";
                        break;
                    case "4":
                        clusterCategory = "OS-EX";
                        break;
                }
                addData(clusterCategoryPlanned, clusterCategory, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}