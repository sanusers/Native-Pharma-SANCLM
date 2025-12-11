package saneforce.sanzen.activity.tourPlan.overview;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.activity.tourPlan.overview.adapter.CategoryDataAdapter;
import saneforce.sanzen.activity.tourPlan.overview.adapter.ClusterDataAdapter;
import saneforce.sanzen.activity.tourPlan.overview.adapter.SideAdapter;
import saneforce.sanzen.activity.tourPlan.overview.model.CategoryWiseModel;
import saneforce.sanzen.activity.tourPlan.overview.model.ClusterModel;
import saneforce.sanzen.activity.tourPlan.overview.model.ClusterWiseModel;
import saneforce.sanzen.activity.tourPlan.overview.model.ContentModel;
import saneforce.sanzen.activity.tourPlan.overview.model.DCRModel;
import saneforce.sanzen.activity.tourPlan.overview.model.DoctorCategoryModel;
import saneforce.sanzen.activity.tourPlan.overview.model.DoctorModel;
import saneforce.sanzen.activity.tourPlan.overview.model.HeaderModel;
import saneforce.sanzen.activity.tourPlan.overview.model.MasterModel;
import saneforce.sanzen.activity.tourPlan.overview.model.WorkTypeModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityTourPlanOverviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class TourPlanOverviewActivity extends AppCompatActivity {
    private ActivityTourPlanOverviewBinding binding;
    private String title = "", monthYear = "", hqCode = "", clusterCap, drCap, chmCap;
    private boolean isOneBuild = false, drNeed = false, chmNeed = false;
    private ArrayList<ModelClass> modelClassList = new ArrayList<>();
    private ArrayList<OneBuildModelClass> oneBuildModelClassList = new ArrayList<>();
    private Map<String, WorkTypeModel> workTypeMaster = new HashMap<>();
    private Map<String, ClusterModel> clusterMaster = new HashMap<>();
    private Map<String, MasterModel> jointWorkMaster = new HashMap<>();
    private Map<String, DoctorModel> doctorMaster = new HashMap<>();
    private Map<String, DCRModel> chemistMaster = new HashMap<>();
    private Map<String, DoctorCategoryModel> categoryMaster = new HashMap<>();
    private Map<String, List<String>> doctorCategoryMaster = new HashMap<>();
    private Map<String, List<String>> doctorClusterMaster = new HashMap<>();
    private Map<String, List<String>> chemistClusterMaster = new HashMap<>();
    private List<String> plannedDates = new ArrayList<>();
    private Map<String, List<String>> workTypePlanned = new HashMap<>();
    private Map<String, List<String>> clusterPlanned = new HashMap<>();
    private Map<String, List<String>> jointWorkPlanned = new HashMap<>();
    private Map<String, List<String>> clusterCategoryPlanned = new HashMap<>();
    private Map<String, List<String>> clusterDatexCategoryPlanned = new HashMap<>();
    private Map<String, List<String>> doctorPlanned = new HashMap<>();
    private Map<String, Set<String>> doctorCategoryPlanned = new HashMap<>();
    private Map<String, Set<String>> doctorClusterPlanned = new HashMap<>();
    private Map<String, List<String>> chemistPlanned = new HashMap<>();
    private Map<String, Set<String>> chemistClusterPlanned = new HashMap<>();
    private Map<String, CategoryWiseModel> drCategoryWisePlan = new HashMap<>();
    private Map<String, ClusterWiseModel> drClusterWisePlan = new HashMap<>();
    private Map<String, ClusterWiseModel> chmClusterWisePlan = new HashMap<>();
    private int totalDays = 0;
    private CategoryDataAdapter drCategoryDataAdapter = new CategoryDataAdapter();
    private ClusterDataAdapter drClusterDataAdapter = new ClusterDataAdapter();
    private ClusterDataAdapter chmClusterDataAdapter = new ClusterDataAdapter();

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint({"MissingSuperCall", "GestureBackNavigation"})
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
        clusterCap = SharedPref.getClusterCap(TourPlanOverviewActivity.this);
        drCap = SharedPref.getDrCap(TourPlanOverviewActivity.this);
        chmCap = SharedPref.getChmCap(TourPlanOverviewActivity.this);
        binding.tpOverviewDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        binding.tpOverviewDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                binding.tpOverviewDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                UtilityClass.hideKeyboard(TourPlanOverviewActivity.this);
            }
        });

        binding.tvClusterCap.setText(clusterCap);
        binding.tvDrCluster.setText(clusterCap);
        binding.tvDrClusterWise.setText(clusterCap + " Wise");
        binding.tvChmCluster.setText(clusterCap);
        binding.tvDoctorCap.setText(drCap);
        binding.tvChemistCap.setText(chmCap);
        binding.tvDrCategory.setText(drCap + " Category");
        binding.tvDrTotalDoctors.setText("Total " + drCap);
        binding.tvDrPlannedDoctors.setText("Planned " + drCap);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                monthYear = bundle.getString("month");
                title += " (" + monthYear + ")";
                binding.tvTitle.setText(title);
                isOneBuild = bundle.getBoolean("is_one_build");
                drNeed = bundle.getBoolean("dr_need");
                chmNeed = bundle.getBoolean("chm_need");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    try {
                        modelClassList = bundle.getParcelableArrayList("data", ModelClass.class);
                        oneBuildModelClassList = bundle.getParcelableArrayList("one_build_data", OneBuildModelClass.class);
                    } catch (Exception e) {
                        modelClassList = new ArrayList<>();
                        oneBuildModelClassList = new ArrayList<>();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        modelClassList = (ArrayList<ModelClass>) (ArrayList<?>) bundle.getParcelableArrayList("data");
                        oneBuildModelClassList = (ArrayList<OneBuildModelClass>) (ArrayList<?>) bundle.getParcelableArrayList("one_build_data");
                    } catch (Exception e) {
                        modelClassList = new ArrayList<>();
                        oneBuildModelClassList = new ArrayList<>();
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getMasterData();
        prepareData();
        setData();
        setClickListeners();
    }


    public enum NavType {
        WORK_CATEGORY,
        WORK_TYPE,
        CLUSTER,
        JOINT_WORK,
        DOCTOR,
        CHEMIST
    }

    private void setClickListeners() {
        binding.tpDataNavigation.llClose.setOnClickListener(view -> {
            binding.tpOverviewDrawer.closeDrawer(GravityCompat.END);
        });

        binding.tvHq.setOnClickListener(view -> {
            List<Object> headerModelList = getClusterCategoryList("HQ");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, headerModelList);
        });

        binding.tvEx.setOnClickListener(view -> {
            List<Object> headerModelList = getClusterCategoryList("EX");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, headerModelList);
        });

        binding.tvOs.setOnClickListener(view -> {
            List<Object> headerModelList = getClusterCategoryList("OS");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, headerModelList);
        });

        binding.tvOsEx.setOnClickListener(view -> {
            List<Object> headerModelList = getClusterCategoryList("OS-EX");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, headerModelList);
        });

        binding.tvFieldWork.setOnClickListener(view -> {
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, new ArrayList<>());
        });

        binding.tvNonFieldWork.setOnClickListener(view -> {
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, new ArrayList<>());
        });

        binding.tvHoliday.setOnClickListener(view -> {
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, new ArrayList<>());
        });

        binding.tvWeeklyOff.setOnClickListener(view -> {
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, new ArrayList<>());
        });

        binding.tvLeave.setOnClickListener(view -> {
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, new ArrayList<>());
        });

        binding.clusterDetails.setOnClickListener(view -> {
            openDrawer(clusterCap, NavType.CLUSTER, new ArrayList<>());
        });

        binding.jointWorkDetails.setOnClickListener(view -> {
            openDrawer(getString(R.string.joint_work), NavType.JOINT_WORK, new ArrayList<>());
        });

        binding.rlDrHead.setOnClickListener(view -> {
            if (binding.rlDrData.getVisibility() == View.VISIBLE){
                binding.rlDrData.setVisibility(View.GONE);
                binding.ivDoctorArrow.setImageResource(R.drawable.down_arrow);
                if (binding.rlChmData.getVisibility() == View.VISIBLE) {
                    binding.ivChemistArrow.setImageResource(R.drawable.up_arrow);
                } else {
                    binding.ivChemistArrow.setImageResource(R.drawable.down_arrow);
                }
            } else {
                binding.rlDrData.setVisibility(View.VISIBLE);
                binding.ivDoctorArrow.setImageResource(R.drawable.up_arrow);
                binding.ivChemistArrow.setImageResource(R.drawable.down_arrow);
                binding.rlChmData.setVisibility(View.GONE);
            }
        });

        binding.rlChmHead.setOnClickListener(view -> {
            if (binding.rlChmData.getVisibility() == View.VISIBLE){
                binding.rlChmData.setVisibility(View.GONE);
                binding.ivChemistArrow.setImageResource(R.drawable.down_arrow);
                if (binding.rlDrData.getVisibility() == View.VISIBLE) {
                    binding.ivDoctorArrow.setImageResource(R.drawable.up_arrow);
                } else {
                    binding.ivDoctorArrow.setImageResource(R.drawable.down_arrow);
                }
            } else {
                binding.rlChmData.setVisibility(View.VISIBLE);
                binding.ivChemistArrow.setImageResource(R.drawable.up_arrow);
                binding.ivDoctorArrow.setImageResource(R.drawable.down_arrow);
                binding.rlDrData.setVisibility(View.GONE);
            }
        });

        binding.tvDrCategoryWise.setOnClickListener(view -> {
            binding.tvDrCategoryWise.setBackground(getDrawable(R.drawable.bg_green));
            binding.tvDrCategoryWise.setTextColor(getResources().getColor(R.color.white));
            binding.tvDrClusterWise.setBackground(null);
            binding.tvDrClusterWise.setTextColor(getResources().getColor(R.color.dark_purple));
            binding.drCategoryData.setVisibility(View.VISIBLE);
            binding.drClusterData.setVisibility(View.GONE);
        });

        binding.tvDrClusterWise.setOnClickListener(view -> {
            binding.tvDrClusterWise.setBackground(getDrawable(R.drawable.bg_green));
            binding.tvDrClusterWise.setTextColor(getResources().getColor(R.color.white));
            binding.tvDrCategoryWise.setBackground(null);
            binding.tvDrCategoryWise.setTextColor(getResources().getColor(R.color.dark_purple));
            binding.drClusterData.setVisibility(View.VISIBLE);
            binding.drCategoryData.setVisibility(View.GONE);
        });
    }

    @NonNull
    private List<Object> getClusterCategoryList(String flag) {
        List<Object> dataList = new ArrayList<>();
        HeaderModel headerModel = new HeaderModel(getString(R.string.hq));
        dataList.add(headerModel);
        if (clusterCategoryPlanned.get(flag) != null) {
            for (String date : clusterCategoryPlanned.get(flag)) {
                ContentModel contentModel = new ContentModel(date + ", " + monthYear, "", "");
                if (clusterDatexCategoryPlanned.get(date) != null && clusterDatexCategoryPlanned.get(date).contains(flag)) {
                    contentModel.setSideContent("*");
                }
                dataList.add(contentModel);
            }
        }
        return dataList;
    }

    private void openDrawer(String title, NavType navType, List<Object> headerModelList) {
        binding.tpOverviewDrawer.openDrawer(GravityCompat.END);
        binding.tpDataNavigation.tvTitle.setText(title);
        switch (navType) {
            case WORK_CATEGORY:
                binding.tpDataNavigation.rlNote.setVisibility(View.VISIBLE);
                SideAdapter sideAdapter = new SideAdapter(headerModelList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TourPlanOverviewActivity.this);
                binding.tpDataNavigation.rvData.setLayoutManager(mLayoutManager);
                binding.tpDataNavigation.rvData.setAdapter(sideAdapter);
                break;
            case WORK_TYPE:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                break;
            case CLUSTER:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                break;
            case JOINT_WORK:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                break;
            case DOCTOR:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                break;
            case CHEMIST:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                break;
        }
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
        try {
            binding.tvClTotal.setText(String.valueOf(clusterMaster.size()));
            binding.tvClPlanned.setText(String.valueOf(clusterPlanned.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            binding.tvJwPlannedDays.setText(String.valueOf(jointWorkPlanned.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (drNeed) {
            try {
                binding.tvDrTotal.setText(String.valueOf(doctorMaster.size()));
                binding.tvDrPlanned.setText(String.valueOf(doctorPlanned.size()));
                try {
                    List<ClusterWiseModel> clusterWiseModelList = new ArrayList<>(drClusterWisePlan.values());
                    Collections.sort(clusterWiseModelList, Comparator.comparing(ClusterWiseModel::getName));
                    drClusterDataAdapter = new ClusterDataAdapter(this, clusterWiseModelList, true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    binding.rvDrClusterData.setLayoutManager(layoutManager);
                    binding.rvDrClusterData.setAdapter(drClusterDataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    List<CategoryWiseModel> categoryWiseModelList = new ArrayList<>(drCategoryWisePlan.values());
                    Collections.sort(categoryWiseModelList, Comparator.comparing(CategoryWiseModel::getName));
                    drCategoryDataAdapter = new CategoryDataAdapter(this, categoryWiseModelList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    binding.rvDrCategoryData.setLayoutManager(layoutManager);
                    binding.rvDrCategoryData.setAdapter(drCategoryDataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (chmNeed) {
            try {
                binding.tvChmTotal.setText(String.valueOf(chemistMaster.size()));
                binding.tvChmPlanned.setText(String.valueOf(chemistPlanned.size()));
                try {
                    List<ClusterWiseModel> clusterWiseModelList = new ArrayList<>(chmClusterWisePlan.values());
                    Collections.sort(clusterWiseModelList, Comparator.comparing(ClusterWiseModel::getName));
                    chmClusterDataAdapter = new ClusterDataAdapter(this, clusterWiseModelList, false);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    binding.rvChmClusterData.setLayoutManager(layoutManager);
                    binding.rvChmClusterData.setAdapter(chmClusterDataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getMasterData() {
        hqCode = SharedPref.getHqCode(TourPlanOverviewActivity.this);
        RoomDB roomDB = RoomDB.getDatabase(TourPlanOverviewActivity.this);
        MasterDataDao masterDataDao = roomDB.masterDataDao();
        drCategoryWisePlan = new HashMap<>();
        drClusterWisePlan = new HashMap<>();
        chmClusterWisePlan = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                clusterMaster.put(jsonObject.optString("Code"), new ClusterModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Territory_Cat")));
                drClusterWisePlan.put(jsonObject.optString("Code"), new ClusterWiseModel(jsonObject.optString("Code"), jsonObject.optString("Name"), new HashMap<>(), new HashMap<>()));
                chmClusterWisePlan.put(jsonObject.optString("Code"), new ClusterWiseModel(jsonObject.optString("Code"), jsonObject.optString("Name"), new HashMap<>(), new HashMap<>()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                jointWorkMaster.put(jsonObject.optString("Code"), new MasterModel(jsonObject.optString("Code"), jsonObject.optString("Name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                workTypeMaster.put(jsonObject.optString("Code"), new WorkTypeModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("FWFlg"), jsonObject.optString("TerrSlFlg")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (drNeed) {
            binding.doctorDetails.setVisibility(View.VISIBLE);
            try {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    categoryMaster.put(jsonObject.optString("Code"), new DoctorCategoryModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Doc_Cat_Name"), jsonObject.optString("No_of_visit")));
                    drCategoryWisePlan.put(jsonObject.optString("Code"), new CategoryWiseModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("No_of_visit")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                doctorCategoryMaster = new HashMap<>();
                doctorClusterMaster = new HashMap<>();
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + hqCode).getMasterSyncDataJsonArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    doctorMaster.put(jsonObject.optString("Code"), new DoctorModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name"), jsonObject.optString("CategoryCode"), jsonObject.optString("Category"), jsonObject.optString("Tlvst")));
                    try {
                        addData(doctorCategoryMaster, jsonObject.optString("CategoryCode"), jsonObject.optString("Code"));
                        addData(doctorClusterMaster, jsonObject.optString("Town_Code"), jsonObject.optString("Code"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        ClusterWiseModel clusterWiseModel = drClusterWisePlan.get(jsonObject.optString("Town_Code"));
                        if (clusterWiseModel == null) {
                            clusterWiseModel = new ClusterWiseModel(jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name"), new HashMap<>(), new HashMap<>());
                        }
                        clusterWiseModel.addTotal(new MasterModel(jsonObject.optString("Code"), jsonObject.optString("Name")));
                        drClusterWisePlan.put(jsonObject.optString("Town_Code"), clusterWiseModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        CategoryWiseModel categoryWiseModel = drCategoryWisePlan.get(jsonObject.optString("CategoryCode"));
                        if (categoryWiseModel == null) {
                            categoryWiseModel = new CategoryWiseModel(jsonObject.optString("CategoryCode"), jsonObject.optString("Category"), jsonObject.optString("Tlvst"));
                        }
                        categoryWiseModel.addTotalDoctors(new MasterModel(jsonObject.optString("Code"), jsonObject.optString("Name")));
                        drCategoryWisePlan.put(jsonObject.optString("CategoryCode"), categoryWiseModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.doctorDetails.setVisibility(View.GONE);
        }

        if (chmNeed) {
            binding.chemistDetails.setVisibility(View.VISIBLE);
            try {
                chemistClusterMaster = new HashMap<>();
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + hqCode).getMasterSyncDataJsonArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    chemistMaster.put(jsonObject.optString("Code"), new DCRModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name")));
                    try {
                        addData(chemistClusterMaster, jsonObject.optString("Town_Code"), jsonObject.optString("Code"));
                        ClusterWiseModel clusterWiseModel = chmClusterWisePlan.get(jsonObject.optString("Town_Code"));
                        if (clusterWiseModel == null) {
                            clusterWiseModel = new ClusterWiseModel(jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name"), new HashMap<>(), new HashMap<>());
                        }
                        clusterWiseModel.addTotal(new MasterModel(jsonObject.optString("Code"), jsonObject.optString("Name")));
                        chmClusterWisePlan.put(jsonObject.optString("Town_Code"), clusterWiseModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.chemistDetails.setVisibility(View.GONE);
        }

        Log.d("Overview", "getMasterData: " + clusterMaster.size() + " " + jointWorkMaster.size() + " " + workTypeMaster.size() + " " + doctorMaster.size() + " " + doctorCategoryMaster.size() + " " + chemistMaster.size());
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
        clusterCategoryPlanned = new HashMap<>();
        clusterCategoryPlanned.put("HQ", new ArrayList<>());
        clusterCategoryPlanned.put("OS", new ArrayList<>());
        clusterCategoryPlanned.put("EX", new ArrayList<>());
        clusterCategoryPlanned.put("OS-EX", new ArrayList<>());
        clusterDatexCategoryPlanned = new HashMap<>();
        clusterPlanned = new HashMap<>();
        jointWorkPlanned = new HashMap<>();
        doctorCategoryPlanned = new HashMap<>();
        doctorClusterPlanned = new HashMap<>();
        chemistClusterPlanned = new HashMap<>();

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
                            if (session.getWorkType().getFWFlg() == null || session.getWorkType().getFWFlg().isEmpty())
                                continue;
                            if (!isPlanned) isPlanned = true;
                            if (session.getTerritories() != null && !session.getTerritories().isEmpty()) {
                                for (OneBuildModelClass.SessionList.SubClass cluster : session.getTerritories()) {
                                    if (cluster != null) {
                                        addClusterData(clusterPlanned, cluster.getCode(), dayNo);
                                    }
                                }
                            }
                            if (session.getJointWorks() != null && !session.getJointWorks().isEmpty()) {
                                for (OneBuildModelClass.SessionList.SubClass jointWork : session.getJointWorks()) {
                                    if (jointWork != null) {
                                        addJWData(jointWorkPlanned, dayNo, jointWork.getCode());
                                    }
                                }
                            }
                            if ("F".equalsIgnoreCase(session.getWorkType().getFWFlg())) {
                                addData(workTypePlanned, "F", dayNo);
                                if (drNeed && session.getDoctors() != null && !session.getDoctors().isEmpty()) {
                                    for (OneBuildModelClass.SessionList.SubClass doctor : session.getDoctors()) {
                                        if (doctor != null) {
                                            String drCode = doctor.getCode();
                                            addData(doctorPlanned, drCode, dayNo);
                                            try {
                                                if (doctorMaster.containsKey(drCode) && doctorMaster.get(drCode) != null) {
                                                    String drClusterCode = doctorMaster.get(drCode).getClusterCode();
                                                    String drClusterName = doctorMaster.get(drCode).getClusterName();
                                                    String drCategoryCode = doctorMaster.get(drCode).getCategoryCode();
                                                    try {
                                                        addDataSet(doctorClusterPlanned, drClusterCode, drCode);
                                                        addDataSet(doctorCategoryPlanned, drCategoryCode, drCode);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        ClusterWiseModel clusterWiseModel = drClusterWisePlan.get(drClusterCode);
                                                        if (clusterWiseModel == null) {
                                                            clusterWiseModel = new ClusterWiseModel(drClusterCode, drClusterName, new HashMap<>(), new HashMap<>());
                                                        }
                                                        clusterWiseModel.addPlanned(new MasterModel(drCode, doctor.getName()));
                                                        drClusterWisePlan.put(drClusterCode, clusterWiseModel);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        CategoryWiseModel categoryWiseModel = drCategoryWisePlan.get(drCategoryCode);
                                                        if (categoryWiseModel == null) {
                                                            categoryWiseModel = new CategoryWiseModel(drClusterCode, drClusterName, "0");
                                                        }
                                                        categoryWiseModel.addPlanned(new MasterModel(drCode, doctor.getName()), dayNo);
                                                        drCategoryWisePlan.put(drCategoryCode, categoryWiseModel);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                if (chmNeed && session.getChemists() != null && !session.getChemists().isEmpty()) {
                                    for (OneBuildModelClass.SessionList.SubClass chemist : session.getChemists()) {
                                        if (chemist != null) {
                                            String chmCode = chemist.getCode();
                                            addData(chemistPlanned, chmCode, dayNo);
                                            try {
                                                if (chemistMaster.containsKey(chmCode) && chemistMaster.get(chmCode) != null) {
                                                    String chmClusterCode = chemistMaster.get(chmCode).getClusterCode();
                                                    String chmClusterName = chemistMaster.get(chmCode).getClusterName();
                                                    try {
                                                        addDataSet(chemistClusterPlanned, chmClusterCode, chmCode);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        ClusterWiseModel clusterWiseModel = chmClusterWisePlan.get(chmClusterCode);
                                                        if (clusterWiseModel == null) {
                                                            clusterWiseModel = new ClusterWiseModel(chmClusterCode, chmClusterName, new HashMap<>(), new HashMap<>());
                                                        }
                                                        clusterWiseModel.addPlanned(new MasterModel(chmCode, chemist.getName()));
                                                        chmClusterWisePlan.put(chmClusterCode, clusterWiseModel);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
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

    private void addDataSet(Map<String, Set<String>> dataMap, String code, String data) {
        try {
            Set<String> datas = dataMap.get(code);
            if (datas == null || datas.isEmpty()) {
                datas = new HashSet<>();
            }
            if (!datas.contains(data)) {
                datas.add(data);
            }
            dataMap.put(code, datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addData(Map<String, List<String>> dataMap, String flag, String data) {
        try {
            List<String> datas = dataMap.get(flag);
            if (datas == null || datas.isEmpty()) {
                datas = new ArrayList<>();
            }
            if (!datas.contains(data)) {
                datas.add(data);
            }
            dataMap.put(flag, datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addJWData(Map<String, List<String>> dataMap, String date, String data) {
        try {
            List<String> datas = dataMap.get(date);
            if (datas == null || datas.isEmpty()) {
                datas = new ArrayList<>();
            }
            if (!datas.contains(data)) {
                datas.add(data);
            }
            dataMap.put(date, datas);
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
                clusterCategory = getClusterCategory(clusterCategoryCode);
                addData(clusterCategoryPlanned, clusterCategory, data);
                addData(clusterDatexCategoryPlanned, data, clusterCategory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getClusterCategory(String clusterCategoryCode) {
        String clusterCategory = "";
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
        return clusterCategory;
    }

}