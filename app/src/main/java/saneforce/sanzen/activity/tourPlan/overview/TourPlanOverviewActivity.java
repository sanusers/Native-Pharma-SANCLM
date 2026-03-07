package saneforce.sanzen.activity.tourPlan.overview;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.activity.tourPlan.overview.adapter.CategoryDataAdapter;
import saneforce.sanzen.activity.tourPlan.overview.adapter.CategoryDataAdapter.CategoryClickListener;
import saneforce.sanzen.activity.tourPlan.overview.adapter.ClusterDataAdapter;
import saneforce.sanzen.activity.tourPlan.overview.adapter.ClusterDataAdapter.ClusterClickListener;
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
import saneforce.sanzen.activity.tourPlan.overview.model.VisitModel;
import saneforce.sanzen.activity.tourPlan.overview.model.WorkTypeModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityTourPlanOverviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class TourPlanOverviewActivity extends AppCompatActivity {
    private ActivityTourPlanOverviewBinding binding;
    private String title = "", monthYear = "", hqCode = "", clusterCap, drCap, chmCap;
    private boolean isOneBuild = false, drNeed = false, chmNeed = false, visitFrequencyNeed = false;
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
    private SideAdapter sideAdapter;
    int position;

    ContentModel model;

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
        binding.tpOverviewDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
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
        binding.tvDrClusterWise.setText(clusterCap + getString(R.string.wise));
        binding.tvChmCluster.setText(clusterCap);
        binding.tvDoctorCap.setText(drCap);
        binding.tvChemistCap.setText(chmCap);
        binding.tvDrCategory.setText(drCap + " " + getString(R.string.category));
        binding.tvDrTotalDoctors.setText(getString(R.string.total) + " " + drCap);
        binding.tvDrPlannedDoctors.setText(getString(R.string.planned) + " " + drCap);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                monthYear = bundle.getString("month");
                title += " (" + monthYear + ")";
                binding.tvTitle.setText(title);
                isOneBuild = bundle.getBoolean("is_one_build");
                visitFrequencyNeed = bundle.getBoolean("visit_frequency_need");
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
        CHEMIST,
        DOCTOR_CATEGORY,
        CUSTOMER_CLUSTER
    }

    private void setClickListeners() {
        binding.tpDataNavigation.llClose.setOnClickListener(view -> {
            binding.tpOverviewDrawer.closeDrawer(GravityCompat.END);
        });

        binding.tpDataNavigation.goToTp.setOnClickListener(view -> {
            binding.tpOverviewDrawer.closeDrawer(GravityCompat.END);
            finish();
        });

        binding.tvHq.setOnClickListener(view -> {
            List<Object> dataList = getClusterCategoryList("HQ");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, dataList);
        });

        binding.tvEx.setOnClickListener(view -> {
            List<Object> dataList = getClusterCategoryList("EX");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, dataList);
        });

        binding.tvOs.setOnClickListener(view -> {
            List<Object> dataList = getClusterCategoryList("OS");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, dataList);
        });

        binding.tvOsEx.setOnClickListener(view -> {
            List<Object> dataList = getClusterCategoryList("OS-EX");
            openDrawer(getString(R.string.work_category_in_days), NavType.WORK_CATEGORY, dataList);
        });

        binding.tvFieldWork.setOnClickListener(view -> {
            List<Object> dataList = getWorkTypeList("F");
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, dataList);
        });

        binding.tvNonFieldWork.setOnClickListener(view -> {
            List<Object> dataList = getWorkTypeList("N");
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, dataList);
        });

        binding.tvHoliday.setOnClickListener(view -> {
            List<Object> dataList = getWorkTypeList("H");
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, dataList);
        });

        binding.tvWeeklyOff.setOnClickListener(view -> {
            List<Object> dataList = getWorkTypeList("W");
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, dataList);
        });

        binding.tvLeave.setOnClickListener(view -> {
            List<Object> dataList = getWorkTypeList("L");
            openDrawer(getString(R.string.work_type_in_days), NavType.WORK_TYPE, dataList);
        });

        binding.clusterDetails.setOnClickListener(view -> {
            List<Object> dataList = getClusterList();
            openDrawer(clusterCap, NavType.CLUSTER, dataList);
        });

        binding.jointWorkDetails.setOnClickListener(view -> {
            List<Object> dataList = getJointWorkList();
            openDrawer(getString(R.string.joint_work), NavType.JOINT_WORK, dataList);
        });

//        binding.rlDrHead.setOnClickListener(view -> {
//            if (binding.rlDrData.getVisibility() == View.VISIBLE) {
//                binding.rlDrData.setVisibility(View.GONE);
//                binding.ivDoctorArrow.setImageResource(R.drawable.down_arrow);
//                if (binding.rlChmData.getVisibility() == View.VISIBLE) {
//                    binding.ivChemistArrow.setImageResource(R.drawable.up_arrow);
//                } else {
//                    binding.ivChemistArrow.setImageResource(R.drawable.down_arrow);
//                }
//            } else {
//                binding.rlDrData.setVisibility(View.VISIBLE);
//                binding.ivDoctorArrow.setImageResource(R.drawable.up_arrow);
//                binding.ivChemistArrow.setImageResource(R.drawable.down_arrow);
//                binding.rlChmData.setVisibility(View.GONE);
//            }
//        });
//
//        binding.rlChmHead.setOnClickListener(view -> {
//            if (binding.rlChmData.getVisibility() == View.VISIBLE) {
//                binding.rlChmData.setVisibility(View.GONE);
//                binding.ivChemistArrow.setImageResource(R.drawable.down_arrow);
//                if (binding.rlDrData.getVisibility() == View.VISIBLE) {
//                    binding.ivDoctorArrow.setImageResource(R.drawable.up_arrow);
//                } else {
//                    binding.ivDoctorArrow.setImageResource(R.drawable.down_arrow);
//                }
//            } else {
//                binding.rlChmData.setVisibility(View.VISIBLE);
//                binding.ivChemistArrow.setImageResource(R.drawable.up_arrow);
//                binding.ivDoctorArrow.setImageResource(R.drawable.down_arrow);
//                binding.rlDrData.setVisibility(View.GONE);
//                binding.rvChmClusterData.post(() -> adjustRecyclerViewHeight(binding.rvChmClusterData, binding.rvChmClusterData.getAdapter().getItemCount()));
//            }
//        });

        binding.tvDrCategoryWise.setOnClickListener(view -> {
            binding.tvDrCategoryWise.setBackground(getDrawable(R.drawable.bg_green));
            binding.tvDrCategoryWise.setTextColor(getResources().getColor(R.color.white));
            binding.tvDrClusterWise.setBackground(null);
            binding.tvDrClusterWise.setTextColor(getResources().getColor(R.color.dark_purple));
            binding.drCategoryData.setVisibility(View.VISIBLE);
            binding.drClusterData.setVisibility(View.GONE);
            binding.rvDrCategoryData.post(() -> adjustRecyclerViewHeight(binding.rvDrCategoryData, binding.rvDrCategoryData.getAdapter().getItemCount()));
        });

        binding.tvDrClusterWise.setOnClickListener(view -> {
            binding.tvDrClusterWise.setBackground(getDrawable(R.drawable.bg_green));
            binding.tvDrClusterWise.setTextColor(getResources().getColor(R.color.white));
            binding.tvDrCategoryWise.setBackground(null);
            binding.tvDrCategoryWise.setTextColor(getResources().getColor(R.color.dark_purple));
            binding.drClusterData.setVisibility(View.VISIBLE);
            binding.drCategoryData.setVisibility(View.GONE);
            binding.rvDrClusterData.post(() -> adjustRecyclerViewHeight(binding.rvDrClusterData, binding.rvDrClusterData.getAdapter().getItemCount()));
        });
    }

    @NonNull
    private List<Object> getClusterCategoryList(String flag) {
        List<Object> dataList = new ArrayList<>();
        try {
            HeaderModel headerModel = new HeaderModel(flag);
            dataList.add(headerModel);
            if (clusterCategoryPlanned.get(flag) != null) {
                int count = 0;
                for (String date : clusterCategoryPlanned.get(flag)) {
                    count++;
                    ContentModel contentModel = new ContentModel(TimeUtils.formatFullDate(date, monthYear), "", "");
                    if ((clusterDatexCategoryPlanned.get(date) != null) && clusterDatexCategoryPlanned.get(date).contains(flag) && (clusterDatexCategoryPlanned.get(date).size() > 1)) {
                        contentModel.setSideContent("*");
                    }
                    dataList.add(contentModel);
                }
                headerModel.setTitle(flag + " (" + count + " days)");
            }
            if (dataList.size() == 1) {
                ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + flag + " " + getString(R.string.planned), "", "");
                dataList.add(contentModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    @NonNull
    private List<Object> getWorkTypeList(String flag) {
        List<Object> dataList = new ArrayList<>();
        try {
            String title = "";
            switch (flag) {
                case "F":
                    title = getString(R.string.field_work);
                    break;
                case "N":
                    title = getString(R.string.non_field_work);
                    break;
                case "H":
                    title = getString(R.string.holiday);
                    break;
                case "W":
                    title = getString(R.string.weekly_off);
                    break;
                case "L":
                    title = getString(R.string.leave);
                    break;
            }
            HeaderModel headerModel = new HeaderModel(title);
            dataList.add(headerModel);
            if (workTypePlanned.get(flag) != null) {
                int count = 0;
                for (String date : workTypePlanned.get(flag)) {
                    count++;
                    ContentModel contentModel = new ContentModel(TimeUtils.formatFullDate(date, monthYear), "", "");
//                if (clusterDatexCategoryPlanned.get(date) != null && clusterDatexCategoryPlanned.get(date).contains(flag)) {
//                    contentModel.setSideContent("*");
//                }
                    dataList.add(contentModel);
                }
                headerModel.setTitle(title + " (" + count + " days)");
            }
            if (dataList.size() == 1) {
                ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + title + " " + getString(R.string.planned), "", "");
                dataList.add(contentModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    @NonNull
    private List<Object> getClusterList() {
        List<Object> dataList = new ArrayList<>();
        try {
            HeaderModel plannedHeaderModel = new HeaderModel(getString(R.string.planned));
            HeaderModel unplannedHeaderModel = new HeaderModel(getString(R.string.unplanned));
            List<Object> plannedDataList = new ArrayList<>();
            List<Object> unplannedDataList = new ArrayList<>();
            int plannedCount = 0, unplannedCount = 0;
            if (clusterMaster != null && !clusterMaster.isEmpty()) {
                for (String clusterCode : clusterMaster.keySet()) {
                    ClusterModel clusterModel = clusterMaster.get(clusterCode);
                    if (clusterModel != null) {
                        if (clusterPlanned.containsKey(clusterCode)) {
                            List<String> dateList = clusterPlanned.get(clusterCode);
                            StringBuilder datesBuilder = new StringBuilder();
                            if (dateList != null && !dateList.isEmpty()) {
                                for (String date : dateList) {
                                    String formattedDate = TimeUtils.getOrdinal(Integer.parseInt(date));
                                    datesBuilder.append(formattedDate);
                                    datesBuilder.append(", ");
                                }
                                String dates = CommonUtilsMethods.removeLastComma(datesBuilder.toString().trim()) + getShortMonth();
                                ContentModel contentModel = new ContentModel(clusterModel.getName(), dates, "");
                                plannedCount++;
                                plannedDataList.add(contentModel);
                            }
                        } else {
                            ContentModel contentModel = new ContentModel(clusterModel.getName(), "", "");
                            unplannedCount++;
                            unplannedDataList.add(contentModel);
                        }
                    }
                }
            }
            if (plannedDataList.isEmpty()) {
                ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + clusterCap + " " + getString(R.string.planned), "", "");
                plannedDataList.add(contentModel);
            }
            if (unplannedDataList.isEmpty()) {
                ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + clusterCap + " " + getString(R.string.unplanned), "", "");
                unplannedDataList.add(contentModel);
            }
            plannedHeaderModel.setTitle(getString(R.string.planned) + " (" + plannedCount + ")");
            dataList.add(plannedHeaderModel);
            dataList.addAll(plannedDataList);
            unplannedHeaderModel.setTitle(getString(R.string.unplanned) + " (" + unplannedCount + ")");
            dataList.add(unplannedHeaderModel);
            dataList.addAll(unplannedDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    @NonNull
    private String getShortMonth() {
        String[] parts = monthYear.split(" ");
        String shortMonth = parts[0].substring(0, 3).toLowerCase();
        return " " + shortMonth;
    }

    @NonNull
    private List<Object> getJointWorkList() {
        List<Object> dataList = new ArrayList<>();
        try {
            HeaderModel headerModel = new HeaderModel(getString(R.string.planned));
            dataList.add(headerModel);
            if (jointWorkPlanned != null && !jointWorkPlanned.isEmpty()) {
                int count = 0;
                for (String date : jointWorkPlanned.keySet()) {
                    count++;
                    List<String> plannedCodes = jointWorkPlanned.get(date);
                    if (plannedCodes != null && !plannedCodes.isEmpty()) {
                        StringBuilder names = new StringBuilder();
                        for (String code : plannedCodes) {
                            MasterModel jointWorkModel = jointWorkMaster.get(code);
                            if (jointWorkModel != null) {
                                names.append(jointWorkModel.getName());
                                names.append(", ");
                            }
                        }
                        ContentModel contentModel = new ContentModel(TimeUtils.formatFullDate(date, monthYear), CommonUtilsMethods.removeLastComma(names.toString().trim()), "");
                        dataList.add(contentModel);
                    }
                }
                headerModel.setTitle(getString(R.string.planned) + " (" + count + " days)");
            }
            if (dataList.size() == 1) {
                ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + getString(R.string.joint_work) + " " + getString(R.string.planned), "", "");
                dataList.add(contentModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private void openDrawer(String title, NavType navType, List<Object> dataList) {
        binding.tpOverviewDrawer.openDrawer(GravityCompat.END);
        binding.tpDataNavigation.tvTitle.setText(title);
        binding.tpDataNavigation.tvSubTitle.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TourPlanOverviewActivity.this);
        binding.tpDataNavigation.rvData.setLayoutManager(mLayoutManager);
        if (dataList != null) {
            sideAdapter = new SideAdapter(TourPlanOverviewActivity.this, dataList, navType,(model,position) -> {
                binding.tpOverviewDrawer.closeDrawer(GravityCompat.END);
                finish();
            });
            binding.tpDataNavigation.rvData.setAdapter(sideAdapter);
        }
        switch (navType) {
            case WORK_CATEGORY:
                binding.tpDataNavigation.rlNote.setVisibility(View.VISIBLE);
                binding.tpDataNavigation.goToTp.setVisibility(View.GONE);
                break;
            case WORK_TYPE:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                binding.tpDataNavigation.goToTp.setVisibility(View.VISIBLE);
                break;
            case CLUSTER:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                binding.tpDataNavigation.goToTp.setVisibility(View.VISIBLE);
                break;
            case JOINT_WORK:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                binding.tpDataNavigation.goToTp.setVisibility(View.VISIBLE);
                break;
            case DOCTOR:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                binding.tpDataNavigation.goToTp.setVisibility(View.VISIBLE);
                break;
            case CHEMIST:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                binding.tpDataNavigation.goToTp.setVisibility(View.VISIBLE);
                break;
            case CUSTOMER_CLUSTER:
                binding.tpDataNavigation.rlNote.setVisibility(View.GONE);
                binding.tpDataNavigation.goToTp.setVisibility(View.GONE);
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
                    drClusterDataAdapter = new ClusterDataAdapter(this, clusterWiseModelList, true, clusterClickListener);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    binding.rvDrClusterData.setLayoutManager(layoutManager);
                    binding.rvDrClusterData.setAdapter(drClusterDataAdapter);
                    binding.rvDrClusterData.post(() -> adjustRecyclerViewHeight(binding.rvDrClusterData, clusterWiseModelList.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (visitFrequencyNeed) {
                    binding.llDrSelection.setVisibility(View.VISIBLE);
                    binding.drCategoryData.setVisibility(View.VISIBLE);
                    binding.drClusterData.setVisibility(View.GONE);
                    try {
                        List<CategoryWiseModel> categoryWiseModelList = new ArrayList<>(drCategoryWisePlan.values());
                        Collections.sort(categoryWiseModelList, Comparator.comparing(CategoryWiseModel::getName));
                        drCategoryDataAdapter = new CategoryDataAdapter(this, categoryWiseModelList, categoryClickListener);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                        binding.rvDrCategoryData.setLayoutManager(layoutManager);
                        binding.rvDrCategoryData.setAdapter(drCategoryDataAdapter);
                        binding.rvDrCategoryData.post(() -> adjustRecyclerViewHeight(binding.rvDrCategoryData, categoryWiseModelList.size()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    binding.llDrSelection.setVisibility(View.GONE);
                    binding.drCategoryData.setVisibility(View.GONE);
                    binding.drClusterData.setVisibility(View.VISIBLE);
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
                    chmClusterDataAdapter = new ClusterDataAdapter(this, clusterWiseModelList, false, clusterClickListener);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    binding.rvChmClusterData.setLayoutManager(layoutManager);
                    binding.rvChmClusterData.setAdapter(chmClusterDataAdapter);
                    binding.rvChmClusterData.post(() -> adjustRecyclerViewHeight(binding.rvChmClusterData, clusterWiseModelList.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void adjustRecyclerViewHeight(RecyclerView recyclerView, int itemCount) {
        if (itemCount <= 5) {
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            recyclerView.setLayoutParams(params);
            return;
        }
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(0);
        if (holder == null) return;
        int itemHeight = holder.itemView.getHeight();
        int maxHeight = itemHeight * 5;
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = maxHeight;
        recyclerView.setLayoutParams(params);
    }

    private final ClusterClickListener clusterClickListener = new ClusterClickListener() {
        @Override
        public void onClusterClick(boolean isDr, ClusterWiseModel clusterWiseModel) {
            binding.tpDataNavigation.tvSubTitle.setText(clusterWiseModel.getName());
            try {
                if (clusterPlanned.containsKey(clusterWiseModel.getCode())) {
                    List<String> dateList = clusterPlanned.get(clusterWiseModel.getCode());
                    StringBuilder datesBuilder = new StringBuilder();
                    if (dateList != null && !dateList.isEmpty()) {
                        for (String date : dateList) {
                            String formattedDate = TimeUtils.getOrdinal(Integer.parseInt(date));
                            datesBuilder.append(formattedDate);
                            datesBuilder.append(", ");
                        }
                        String dates = CommonUtilsMethods.removeLastComma(datesBuilder.toString().trim()) + getShortMonth();
                        binding.tpDataNavigation.tvSubTitle.setText(CommonUtilsMethods.applyOrdinalSuperscript(clusterWiseModel.getName() + " - " + dates));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Object> dataList = new ArrayList<>();
            if (isDr) {
                try {
                    HeaderModel plannedHeaderModel = new HeaderModel(getString(R.string.planned));
                    HeaderModel unplannedHeaderModel = new HeaderModel(getString(R.string.unplanned));
                    List<Object> plannedDataList = new ArrayList<>();
                    List<Object> unplannedDataList = new ArrayList<>();
                    int plannedCount = 0, unplannedCount = 0;
                    if (doctorClusterMaster.containsKey(clusterWiseModel.getCode()) && doctorClusterMaster.get(clusterWiseModel.getCode()) != null && !doctorClusterMaster.get(clusterWiseModel.getCode()).isEmpty()) {
                        for (String drCode : doctorClusterMaster.get(clusterWiseModel.getCode())) {
                            DoctorModel doctorModel = doctorMaster.get(drCode);
                            if (doctorModel != null) {
                                ContentModel contentModel = new ContentModel(doctorModel.getName(), doctorModel.getSpecialityName(), "");
                                if (clusterWiseModel.getPlanned().containsKey(drCode)) {
                                    plannedCount++;
                                    plannedDataList.add(contentModel);
                                } else {
                                    unplannedCount++;
                                    unplannedDataList.add(contentModel);
                                }
                            }
                        }
                    }
                    if (plannedDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + drCap + " " + getString(R.string.planned), "", "");
                        plannedDataList.add(contentModel);
                    }
                    if (unplannedDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + drCap + " " + getString(R.string.unplanned), "", "");
                        unplannedDataList.add(contentModel);
                    }
                    plannedHeaderModel.setTitle(getString(R.string.planned) + " " + drCap + " (" + plannedCount + ")");
                    dataList.add(plannedHeaderModel);
                    dataList.addAll(plannedDataList);
                    unplannedHeaderModel.setTitle(getString(R.string.unplanned) + " " + drCap + " (" + unplannedCount + ")");
                    dataList.add(unplannedHeaderModel);
                    dataList.addAll(unplannedDataList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                openDrawer(drCap, NavType.CUSTOMER_CLUSTER, dataList);
            } else {
                try {
                    HeaderModel plannedHeaderModel = new HeaderModel(getString(R.string.planned));
                    HeaderModel unplannedHeaderModel = new HeaderModel(getString(R.string.unplanned));
                    List<Object> plannedDataList = new ArrayList<>();
                    List<Object> unplannedDataList = new ArrayList<>();
                    int plannedCount = 0, unplannedCount = 0;
                    if (chemistClusterMaster.containsKey(clusterWiseModel.getCode()) && chemistClusterMaster.get(clusterWiseModel.getCode()) != null && !chemistClusterMaster.get(clusterWiseModel.getCode()).isEmpty()) {
                        for (String chmCode : chemistClusterMaster.get(clusterWiseModel.getCode())) {
                            DCRModel dcrModel = chemistMaster.get(chmCode);
                            if (dcrModel != null) {
                                ContentModel contentModel = new ContentModel(dcrModel.getName(), "", "");
                                if (clusterWiseModel.getPlanned().containsKey(chmCode)) {
                                    plannedCount++;
                                    plannedDataList.add(contentModel);
                                } else {
                                    unplannedCount++;
                                    unplannedDataList.add(contentModel);
                                }
                            }
                        }
                    }
                    if (plannedDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + chmCap + " " + getString(R.string.planned), "", "");
                        plannedDataList.add(contentModel);
                    }
                    if (unplannedDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + chmCap + " " + getString(R.string.unplanned), "", "");
                        unplannedDataList.add(contentModel);
                    }
                    plannedHeaderModel.setTitle(getString(R.string.planned) + " " + chmCap + " (" + plannedCount + ")");
                    dataList.add(plannedHeaderModel);
                    dataList.addAll(plannedDataList);
                    unplannedHeaderModel.setTitle(getString(R.string.unplanned) + " " + chmCap + " (" + unplannedCount + ")");
                    dataList.add(unplannedHeaderModel);
                    dataList.addAll(unplannedDataList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                openDrawer(chmCap, NavType.CUSTOMER_CLUSTER, dataList);
            }
            binding.tpDataNavigation.tvSubTitle.setVisibility(View.VISIBLE);
        }
    };

    private final CategoryClickListener categoryClickListener = new CategoryClickListener() {
        @Override
        public void onCategoryClick(CategoryDataAdapter.CategoryClickType categoryClickType, CategoryWiseModel categoryWiseModel) {
            binding.tpDataNavigation.tvSubTitle.setText(categoryWiseModel.getName() + " (" + categoryWiseModel.getFrequency() + ")");
            List<Object> dataList = new ArrayList<>();
            try {
                HeaderModel plannedHeaderModel = new HeaderModel(getString(R.string.planned));
                HeaderModel unplannedHeaderModel = new HeaderModel(getString(R.string.unplanned));
                List<Object> plannedDataList = new ArrayList<>();
                List<Object> unplannedDataList = new ArrayList<>();
                List<Object> unplannedVisitsDataList = new ArrayList<>();
                int plannedCount = 0, unplannedCount = 0;
                if (doctorCategoryMaster.containsKey(categoryWiseModel.getCode()) && doctorCategoryMaster.get(categoryWiseModel.getCode()) != null && !doctorCategoryMaster.get(categoryWiseModel.getCode()).isEmpty()) {
                    for (String drCode : doctorCategoryMaster.get(categoryWiseModel.getCode())) {
                        DoctorModel doctorModel = doctorMaster.get(drCode);
                        if (doctorModel != null) {
                            ContentModel contentModel = new ContentModel(doctorModel.getName(), doctorModel.getClusterName() + " | " + doctorModel.getSpecialityName(), "");
                            if (categoryWiseModel.getPlannedDoctors().containsKey(drCode)) {
                                plannedCount++;
                                if (categoryClickType != CategoryDataAdapter.CategoryClickType.PLANNED_DOCTORS) {
                                    VisitModel visitModel = categoryWiseModel.getPlannedVisit().get(drCode);
                                    if (visitModel != null) {
                                        Set<String> plannedDates = visitModel.getDates();
                                        StringBuilder datesBuilder = new StringBuilder();
                                        for (String date : plannedDates) {
                                            datesBuilder.append(TimeUtils.getOrdinal(Integer.parseInt(date)));
                                            datesBuilder.append(", ");
                                        }
                                        String dates = CommonUtilsMethods.removeLastComma(datesBuilder.toString().trim()) + getShortMonth();
                                        contentModel.setSideContent(dates);
                                        if (categoryClickType == CategoryDataAdapter.CategoryClickType.UNPLANNED_VISITS) {
                                            int plannedDatesSize = (datesBuilder.toString().trim().split(", ").length);
                                            if (categoryWiseModel.getFrequency() > plannedDatesSize) {
                                                contentModel.setSideContent(plannedDatesSize + "/" + categoryWiseModel.getFrequency());
                                                unplannedVisitsDataList.add(contentModel);
                                            }
                                        }
                                    }
                                }
                                plannedDataList.add(contentModel);
                            } else {
                                unplannedCount++;
                                unplannedDataList.add(contentModel);
                                if (categoryClickType == CategoryDataAdapter.CategoryClickType.UNPLANNED_VISITS) {
                                    contentModel.setSideContent("0/" + categoryWiseModel.getFrequency());
                                    unplannedVisitsDataList.add(contentModel);
                                }
                            }
                        }
                    }
                }
                if (categoryClickType == CategoryDataAdapter.CategoryClickType.PLANNED_DOCTORS) {
                    if (plannedDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + drCap + " " + getString(R.string.planned), "", "");
                        plannedDataList.add(contentModel);
                    }
                    if (unplannedDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + drCap + " " + getString(R.string.unplanned), "", "");
                        unplannedDataList.add(contentModel);
                    }
                    plannedHeaderModel.setTitle(getString(R.string.planned) + " " + drCap + " (" + plannedCount + ")");
                    dataList.add(plannedHeaderModel);
                    dataList.addAll(plannedDataList);
                    unplannedHeaderModel.setTitle(getString(R.string.unplanned) + " " + drCap + " (" + unplannedCount + ")");
                    dataList.add(unplannedHeaderModel);
                    dataList.addAll(unplannedDataList);
                }
                if (categoryClickType == CategoryDataAdapter.CategoryClickType.PLANNED_VISITS) {
                    if (plannedDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + drCap + " " + getString(R.string.planned_visits), "", "");
                        plannedDataList.add(contentModel);
                    }
                    plannedHeaderModel.setTitle(getString(R.string.planned_visits) + " (" + plannedCount + ")");
                    dataList.add(plannedHeaderModel);
                    dataList.addAll(plannedDataList);
                }
                if (categoryClickType == CategoryDataAdapter.CategoryClickType.UNPLANNED_VISITS) {
                    if (unplannedVisitsDataList.isEmpty()) {
                        ContentModel contentModel = new ContentModel(getString(R.string.no) + " " + drCap + " " + getString(R.string.unplanned_visits), "", "");
                        unplannedVisitsDataList.add(contentModel);
                    }
                    unplannedHeaderModel.setTitle(getString(R.string.unplanned_visits) + " (" + unplannedCount + ")");
                    dataList.add(unplannedHeaderModel);
                    dataList.addAll(unplannedVisitsDataList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            openDrawer(drCap, NavType.CUSTOMER_CLUSTER, dataList);
            binding.tpDataNavigation.tvSubTitle.setVisibility(View.VISIBLE);
        }
    };

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
                    DoctorModel doctorModel = new DoctorModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name"), jsonObject.optString("CategoryCode"), jsonObject.optString("Category"), jsonObject.optString("SpecialtyCode"), jsonObject.optString("Specialty"), jsonObject.optString("Tlvst"));
                    doctorMaster.put(jsonObject.optString("Code"), doctorModel);
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
                    DCRModel dcrModel = new DCRModel(jsonObject.optString("Code"), jsonObject.optString("Name"), jsonObject.optString("Town_Code"), jsonObject.optString("Town_Name"));
                    chemistMaster.put(jsonObject.optString("Code"), dcrModel);
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
                            } else if (!session.getWorkType().getFWFlg().isEmpty()){
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

    private void addData(Map<String, List<DoctorModel>> dataMap, String flag, DoctorModel data) {
        try {
            List<DoctorModel> datas = dataMap.get(flag);
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

    private void addData(Map<String, List<DCRModel>> dataMap, String flag, DCRModel data) {
        try {
            List<DCRModel> datas = dataMap.get(flag);
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