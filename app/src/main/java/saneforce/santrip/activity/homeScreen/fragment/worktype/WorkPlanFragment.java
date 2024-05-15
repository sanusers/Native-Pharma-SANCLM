package saneforce.santrip.activity.homeScreen.fragment.worktype;


import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.SetupOutBoxAdapter;
import static saneforce.santrip.commonClasses.Constants.APP_MODE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.homeScreen.fragment.OutboxFragment;
import saneforce.santrip.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.commonClasses.MyDayPlanEntriesNeeded;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.WorkplanFragmentBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.santrip.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataTable;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;


public class WorkPlanFragment extends Fragment implements View.OnClickListener {

    public static String chk_cluster = "";
    public static ArrayList<Multicheckclass_clust> listSelectedCluster = new ArrayList<>();
    public static String mTowncode1 = "", mTownname1 = "", mWTCode1 = "", mWTName1 = "", mFwFlg1 = "", mHQCode1 = "", mHQName1 = "", mRemarks1 = "", mTowncode2 = "", mTownname2 = "", mWTCode2 = "", mWTName2 = "", mFwFlg2 = "", mHQCode2 = "", mHQName2 = "", mHQCode = "", mTowncode = "", mTownname = "", mWTCode = "", mWTName = "", mFwFlg = "", mHQName = "", mFinalRemarks = "";
    @SuppressLint("StaticFieldLeak")
    public static WorkplanFragmentBinding binding;
    ProgressDialog progressDialog;
    String CheckInOutStatus, FinalSubmitStatus;
    JSONObject jsonObject = new JSONObject();

    ArrayList<JSONObject> workType_list1 = new ArrayList<>();
    ArrayList<Multicheckclass_clust> multiple_cluster_list = new ArrayList<>();
    ArrayList<JSONObject> HQList = new ArrayList<>();
    ArrayList<JSONObject> cluster = new ArrayList<>();
    JSONObject SelectedWorkType;
    JSONObject SelectedHQ;
    ApiInterface api_interface;

    String strClusterID = "", strClusterName = "";

    String DayPlanCount = "1", IsFeildWorkFlag = "F0";

    String mSubmitflag = "S0";
    CommonUtilsMethods commonUtilsMethods;
    double latitude, longitude;
    GPSTrack gpsTrack;
    Dialog dialogAfterCheckOut;
    TextView tvDateTimeAfter, tvLat, tvLong, tvAddress, tvHeading;
    Button btnClose;
    String address;
    JSONObject jsonCheck;
    JSONObject finalSubmitJSONObject;
    boolean NeedClusterFlag1 = false, NeedClusterFlag2 = false;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;


    @Override
    public void onResume() {
        super.onResume();
        Log.d("ACTIVITY_STATUS","OnResume");
//        if (!SharedPref.getCheckDateTodayPlan(requireContext()).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
//            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, "[]", 0));
//        if(HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
//            if(UtilityClass.isNetworkAvailable(requireContext())) {
//                syncMyDayPlan();
//                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
//            }else {
//                setUpMyDayplan();
//            }
//        }
//        } else {
//            setUpMyDayplan();
//        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WorkplanFragmentBinding.inflate(inflater);
        View view = binding.getRoot();
        Log.d("ACTIVITY_STATUS","oncreateview");
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();
        callOfflineWorkTypeDataDao = roomDB.callOfflineWorkTypeDataDao();
        gpsTrack = new GPSTrack(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
            binding.btnsumit.setText(requireContext().getString(R.string.final_submit_check_out));
        } else {
            binding.btnsumit.setText(requireContext().getString(R.string.final_submit));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.progressHq1.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            binding.progressHq2.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            binding.progressSumit1.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
        }

        api_interface = RetrofitClient.getRetrofit(getContext(), SharedPref.getCallApiUrl(requireContext()));

        if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
            binding.rlheadquates1.setVisibility(View.VISIBLE);
            binding.rlheadquates2.setVisibility(View.VISIBLE);
        } else {
            binding.rlheadquates1.setVisibility(View.GONE);
            binding.rlheadquates2.setVisibility(View.GONE);
        }

        binding.btnsumit.setOnClickListener(this);
        binding.rlworktype1.setOnClickListener(this);
        binding.rlcluster1.setOnClickListener(this);
        binding.rlheadquates1.setOnClickListener(this);
        binding.txtAddPlan.setOnClickListener(this);
        binding.txtSave.setOnClickListener(this);
        binding.rlworktype2.setOnClickListener(this);
        binding.rlcluster2.setOnClickListener(this);
        binding.rlheadquates2.setOnClickListener(this);
        binding.llDelete.setOnClickListener(this);
        boolean toSync = false;
        if(HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
            try {
                JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.MY_DAY_PLAN).getMasterSyncDataJsonArray();
                if(workTypeArray.length() > 0) {
                    JSONObject FirstSeasonDayPlanObject = workTypeArray.getJSONObject(0);
                    String DayPlanDate1 = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_4, FirstSeasonDayPlanObject.getJSONObject("TPDt").getString("date"));
                    String CurrentDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                    if(!DayPlanDate1.equalsIgnoreCase(CurrentDate)) {
                        toSync = true;
                    }
                    Log.e("WorkPlanFragment", "onCreateView: " + DayPlanDate1 + " " + CurrentDate + " " + toSync);
                } else {
                    toSync = true;
                }
            } catch (Exception e) {
                Log.e("WorkPlanFragment", "onCreateView: " + e.getMessage());
                e.printStackTrace();
            }
        }
//        if (!SharedPref.getCheckDateTodayPlan(requireContext()).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
//            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, "[]", 0));
        if(toSync) {
            if(UtilityClass.isNetworkAvailable(requireContext())) {
                syncMyDayPlan();
                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
            }else {
                setUpMyDayplan();
            }
        }else {
            setUpMyDayplan();
        }
//        } else {
//            setUpMyDayplan();
//        }

        getLocalData();


        return view;
    }

    public void ShowWorkTypeAlert(TextView mTxtWorktype, RelativeLayout rlculster, RelativeLayout rlHQ) {
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.tvSearchheader.setText(requireContext().getString(R.string.worktype));
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.VISIBLE);
        WorkplanListAdapter WT_ListAdapter = new WorkplanListAdapter(getActivity(), workType_list1, "1");
        HomeDashBoard.binding.llNav.wkListView.setAdapter(WT_ListAdapter);

        HomeDashBoard.binding.llNav.wkListView.setOnItemClickListener((parent, view, position, id) -> {
            HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            SelectedWorkType = WT_ListAdapter.getlisted().get(position);
            try {
                mTxtWorktype.setText(SelectedWorkType.getString("Name"));
                if (DayPlanCount.equalsIgnoreCase("1")) {
                    mFwFlg1 = SelectedWorkType.getString("FWFlg");
                    mWTCode1 = SelectedWorkType.getString("Code");
                    mWTName1 = SelectedWorkType.getString("Name");

                    if (SelectedWorkType.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                        IsFeildWorkFlag = "F1";
                        rlculster.setVisibility(View.VISIBLE);
                        if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                            rlHQ.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mTowncode1 = "";
                        mTownname1 = "";
                        mHQCode1 = "";
                        mHQName1 = "";
                        chk_cluster = "";
                        rlculster.setVisibility(View.GONE);
                        rlHQ.setVisibility(View.GONE);
                    }
                } else {
                    mFwFlg2 = SelectedWorkType.getString("FWFlg");
                    mWTCode2 = SelectedWorkType.getString("Code");
                    mWTName2 = SelectedWorkType.getString("Name");
                    if (SelectedWorkType.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                        NeedClusterFlag2 = true;
                        IsFeildWorkFlag = "F2";
                        rlculster.setVisibility(View.VISIBLE);
                        if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                            rlHQ.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mTowncode2 = "";
                        mTownname2 = "";
                        mHQCode2 = "";
                        mHQName2 = "";
                        chk_cluster = "";
                        rlculster.setVisibility(View.GONE);
                        rlHQ.setVisibility(View.GONE);
                    }

                }

                HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String searchString = s.toString();
                        WT_ListAdapter.getFilter().filter(searchString);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void showMultiClusterAlter() {
        listSelectedCluster.clear();
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.GONE);
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        HomeDashBoard.binding.llNav.tvSearchheader.setText("Cluster");

        MultiClusterAdapter multiClusterAdapter = new MultiClusterAdapter(getActivity(), multiple_cluster_list, new OnClusterClicklistener() {
            @Override
            public void classCampaignItem_addClass(Multicheckclass_clust classGroup) {
                listSelectedCluster.add(classGroup);

            }

            @Override
            public void classCampaignItem_removeClass(Multicheckclass_clust classGroup) {
                listSelectedCluster.add(classGroup);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        HomeDashBoard.binding.llNav.wkRecyelerView.setLayoutManager(linearLayoutManager);
        HomeDashBoard.binding.llNav.wkRecyelerView.setAdapter(multiClusterAdapter);

        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString();
                multiClusterAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        HomeDashBoard.binding.llNav.txtClDone.setOnClickListener(v -> {
            HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            if (listSelectedCluster.size() > 0) {
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
                if (DayPlanCount.equalsIgnoreCase("1")) {
                    mTowncode1 = strClusterID;
                    mTownname1 = strClusterName;
                    binding.txtCluster1.setText(strClusterName);
                } else {
                    mTowncode2 = strClusterID;
                    mTownname2 = strClusterName;
                    binding.txtCluster2.setText(strClusterName);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showHQ(TextView TextHQ, TextView TextCL) {
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.VISIBLE);

        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.tvSearchheader.setText("HeadQuarters");


        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter HQ_ListAdapter = new WorkplanListAdapter(getActivity(), HQList, "3");
        HomeDashBoard.binding.llNav.wkListView.setAdapter(HQ_ListAdapter);

        HomeDashBoard.binding.llNav.wkListView.setOnItemClickListener((parent, view, position, id) -> {
            SelectedHQ = HQ_ListAdapter.getlisted().get(position);
            HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            try {

                if (UtilityClass.isNetworkAvailable(requireContext())) {

                    TextCL.setText("");
                    TextHQ.setText(SelectedHQ.getString("name"));
                    if (DayPlanCount.equalsIgnoreCase("1")) {
                        mHQCode1 = SelectedHQ.getString("id");
                        mHQName1 = SelectedHQ.getString("name");
                        getData(SelectedHQ.getString("id"));

                    } else {
                        mHQCode2 = SelectedHQ.getString("id");
                        mHQName2 = SelectedHQ.getString("name");
                        getData(SelectedHQ.getString("id"));
                    }
                } else {
                    TextHQ.setText("");
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString();
                HQ_ListAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    void getLocalData() {
        workType_list1.clear();
        cluster.clear();
        multiple_cluster_list.clear();
        HQList.clear();

        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject object = workTypeArray.getJSONObject(i);

                if (SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {

                    if (DayPlanCount.equalsIgnoreCase("1")) {
                        if (!(mWTCode2).equalsIgnoreCase(object.getString("Code"))) {
                            workType_list1.add(object);
                        }
                    } else {
                        if (!(mWTCode1).equalsIgnoreCase(object.getString("Code"))) {
                            workType_list1.add(object);
                        }
                    }
                } else {

                    workType_list1.add(object);
                }
            }


            JSONArray workTypeArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getHqCode(requireContext())).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray2.length(); i++) {
                JSONObject Object1 = workTypeArray2.getJSONObject(i);


                if (("," + chk_cluster + ",").contains("," + Object1.getString("Code") + ",")) {
                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", true));
                } else {
                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", false));

                }
                cluster.add(Object1);
            }

            if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                JSONArray workTypeArray3 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                for (int i = 0; i < workTypeArray3.length(); i++) {
                    JSONObject jsonObject = workTypeArray3.getJSONObject(i);

                    if (DayPlanCount.equalsIgnoreCase("1")) {
                        if (!(mHQCode2).equalsIgnoreCase(jsonObject.getString("id"))) {
                            HQList.add(jsonObject);
                        }
                    } else {
                        if (!(mHQCode1).equalsIgnoreCase(jsonObject.getString("id"))) {
                            HQList.add(jsonObject);
                        }
                    }

                }
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlworktype1:
                if (HomeDashBoard.binding.textDate.getText().toString().equalsIgnoreCase("")) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_date));
                } else {
                    ShowWorkTypeAlert(binding.txtWorktype1, binding.rlcluster1, binding.rlheadquates1);
                }

                break;

            case R.id.rlcluster1:
                if (binding.txtheadquaters1.getText().toString().equalsIgnoreCase("") && !SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
                } else if (SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                    if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                    } else {
                        showMultiClusterAlter();
                    }
                } else {
                    showMultiClusterAlter();
                }

                break;


            case R.id.rlworktype2:
                ShowWorkTypeAlert(binding.txtWorktype2, binding.rlcluster2, binding.rlheadquates2);
                break;

            case R.id.rlcluster2:

                if (binding.txtheadquaters2.getText().toString().equalsIgnoreCase("") && !SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
                } else if (SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                    if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                    } else {
                        showMultiClusterAlter();
                    }
                } else {
                    showMultiClusterAlter();
                }

                break;

            case R.id.rlheadquates1:
                if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                } else {
                    showHQ(binding.txtheadquaters1, binding.txtCluster1);
                }
                break;

            case R.id.rlheadquates2:
                if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                } else {
                    showHQ(binding.txtheadquaters2, binding.txtCluster2);
                }
                break;
            case R.id.txtSave:
                if (DayPlanCount.equalsIgnoreCase("1")) {
                    if (NeedClusterFlag1) {
                        if (binding.txtheadquaters1.getText().toString().equalsIgnoreCase("") && !SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
                        } else if (binding.txtCluster1.getText().toString().equalsIgnoreCase("")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_cluster));
                        } else {
                            binding.llPlan1.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
                            binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            if (mFwFlg1.equalsIgnoreCase("F")) {
                                binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
                            } else {
                                binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            }

                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
                            binding.rlworktype1.setEnabled(false);
                            binding.rlcluster1.setEnabled(false);
                            binding.rlheadquates1.setEnabled(false);
                            binding.txtAddPlan.setEnabled(true);
                            binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtSave.setEnabled(false);
                            mSubmitflag = "S1";
                            CreateJson();
                            if (UtilityClass.isNetworkAvailable(requireContext())) {
                                MyDayPlanSubmit();
                            } else {
                                SaveWTLocal("1");
                            }
                        }
                    } else if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                    } else {
                        binding.llPlan1.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
                        binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        if (mFwFlg1.equalsIgnoreCase("F")) {
                            binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
                        } else {
                            binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        }

                        binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
                        binding.rlworktype1.setEnabled(false);
                        binding.rlcluster1.setEnabled(false);
                        binding.rlheadquates1.setEnabled(false);
                        binding.txtAddPlan.setEnabled(true);
                        binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
                        binding.txtSave.setEnabled(false);
                        mSubmitflag = "S1";
                        CreateJson();
                        if (UtilityClass.isNetworkAvailable(requireContext())) {
                            MyDayPlanSubmit();
                        } else {
                            SaveWTLocal("1");
                        }
                    }
                } else {
                    if (NeedClusterFlag1) {
                        if (binding.txtheadquaters2.getText().toString().equalsIgnoreCase("") && !SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
                        } else if (binding.txtCluster2.getText().toString().equalsIgnoreCase("")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_cluster));
                        } else {
                            binding.cardPlan2.setCardBackgroundColor(getResources().getColor(R.color.gray_45));
                            binding.llPlan2.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
                            binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            if (mFwFlg2.equalsIgnoreCase("F")) {
                                binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
                            } else {
                                binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            }
                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtAddPlan.setEnabled(false);
                            binding.rlworktype2.setEnabled(false);
                            binding.rlcluster2.setEnabled(false);
                            binding.rlheadquates2.setEnabled(false);
                            binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtSave.setEnabled(false);
                            mSubmitflag = "S1";
                            binding.llDelete.setVisibility(View.GONE);
                            CreateJson();
                            if (UtilityClass.isNetworkAvailable(requireContext())) {
                                MyDayPlanSubmit();
                            } else {
                                SaveWTLocal("2");
                            }
                        }
                    } else if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                    } else {
                        binding.cardPlan2.setCardBackgroundColor(getResources().getColor(R.color.gray_45));
                        binding.llPlan2.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
                        binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        if (mFwFlg2.equalsIgnoreCase("F")) {
                            binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
                        } else {
                            binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        }
                        binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                        binding.txtAddPlan.setEnabled(false);
                        binding.rlworktype2.setEnabled(false);
                        binding.rlcluster2.setEnabled(false);
                        binding.rlheadquates2.setEnabled(false);
                        binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
                        binding.txtSave.setEnabled(false);
                        mSubmitflag = "S1";
                        binding.llDelete.setVisibility(View.GONE);
                        CreateJson();
                        if (UtilityClass.isNetworkAvailable(requireContext())) {
                            MyDayPlanSubmit();
                        } else {
                            SaveWTLocal("2");
                        }
                    }
                }
                break;

            case R.id.txtAddPlan:
                mSubmitflag = "S0";
                DayPlanCount = "2";
                binding.llDelete.setVisibility(View.VISIBLE);
                binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                binding.txtSave.setTextColor(getResources().getColor(R.color.black));
                binding.txtSave.setEnabled(true);
                binding.cardPlan2.setVisibility(View.VISIBLE);
                getLocalData();
                break;

            case R.id.btnsumit:
//                if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
//                    if (!SharedPref.getCheckInTime(requireContext()).isEmpty()) {
                if(HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
                    remarksAlertBox();
                }
//                    } else {
//                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_checkin));
//                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, HomeDashBoard.dateSync.toString(), 0));
//                        MyDayPlanEntriesNeeded.deleteDate(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
//                        HomeDashBoard.checkAndSetEntryDate(requireContext());
//                    }
//                }

//                if (mSubmitflag.equalsIgnoreCase("S1")) {
//                    AletboxRemarks();
//                } else if (mSubmitflag.equalsIgnoreCase("S2")) {
//                    MyDayPlanSubmit();
//                } else {
//                    Toast.makeText(getActivity(), "Save Workday Plan", Toast.LENGTH_SHORT).show();
//                }
                break;


            case R.id.ll_delete:
                binding.txtSave.setEnabled(false);
                binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
                binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
                binding.txtWorktype2.setText("");
                binding.txtCluster2.setText("");
                binding.txtheadquaters2.setText("");
                DayPlanCount = "1";
                mSubmitflag = "S1";
                binding.cardPlan2.setVisibility(View.GONE);
                break;
        }

    }

    private void SaveWTLocal(String isWhich) {
        try {
            SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode1);
            JSONArray MydayPlanDataList = new JSONArray();
            JSONObject FisrstSeasonObject = new JSONObject();
            JSONObject SecondSeasonObject = new JSONObject();
            JSONObject jsonObjectwt = new JSONObject();

            if (isWhich.equalsIgnoreCase("1")) {
                callOfflineWorkTypeDataDao.insert(new CallOfflineWorkTypeDataTable(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), mWTCode1, mWTName1, jsonObject.toString(), "", 0));
                if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                    SharedPref.saveHq(requireContext(), mHQName1, mHQCode1);
                } else {
                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()),  SharedPref.getSfCode(requireContext()));
                }
                SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode1);
            } else {
                callOfflineWorkTypeDataDao.insert(new CallOfflineWorkTypeDataTable(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), mWTCode2, mWTName2, jsonObject.toString(), "", 0));
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.save_wt_locally));
                OutboxFragment.SetupOutBoxAdapter(requireActivity(), requireContext());
                if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                    SharedPref.saveHq(requireContext(), mHQName2, mHQCode2);
                } else {
                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()),  SharedPref.getSfCode(requireContext()));
                }
                SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode2);
            }

            boolean isTodayAvailable = false;
            JSONArray jsonData = masterDataDao.getMasterDataTableOrNew(Constants.MY_DAY_PLAN).getMasterSyncDataJsonArray();

            if (jsonData.length() > 0) {
                for (int i = 0; i < jsonData.length(); i++) {
                    jsonObjectwt = jsonData.getJSONObject(i);
                    if (jsonObjectwt.getJSONObject("TPDt").getString("date").substring(0, 11).equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"))) {
                        isTodayAvailable = true;
                        break;
                    }
                }
            }

            if (isTodayAvailable) {
                jsonObjectwt = new JSONObject();
                jsonObjectwt.put("SFCode",SharedPref.getSfCode(requireContext()));
                JSONObject TPDtSecondSeasonObject = new JSONObject();
                TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
                jsonObjectwt.put("TPDt", TPDtSecondSeasonObject);
                jsonObjectwt.put("WT", mWTCode2);
                jsonObjectwt.put("WTNm", mWTName2);
                jsonObjectwt.put("FWFlg", mFwFlg2);
                jsonObjectwt.put("SFMem", mHQCode2);
                jsonObjectwt.put("HQNm", mHQName2);
                jsonObjectwt.put("Pl", mTowncode2);
                jsonObjectwt.put("PlNm", mTownname2);
                jsonObjectwt.put("Rem", "");
                jsonObjectwt.put("TpVwFlg", "2");
                jsonObjectwt.put("TP_Doctor", "");
                jsonObjectwt.put("TP_cluster", "");
                jsonObjectwt.put("TP_worktype", "");
                jsonData.put(SecondSeasonObject);
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, jsonData.toString(), 0));
            } else {
                FisrstSeasonObject.put("SFCode", SharedPref.getSfCode(requireContext()));
                JSONObject TPDtFisrstSeasonObject = new JSONObject();
                TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
                FisrstSeasonObject.put("TPDt", TPDtFisrstSeasonObject);
                FisrstSeasonObject.put("WT", mWTCode1);
                FisrstSeasonObject.put("WTNm", mWTName1);
                FisrstSeasonObject.put("FWFlg", mFwFlg1);
                FisrstSeasonObject.put("SFMem", mHQCode1);
                FisrstSeasonObject.put("HQNm", mHQName1);
                FisrstSeasonObject.put("Pl", mTowncode1);
                FisrstSeasonObject.put("PlNm", mTownname1);
                FisrstSeasonObject.put("Rem", "");
                FisrstSeasonObject.put("TpVwFlg", "2");
                FisrstSeasonObject.put("TP_Doctor", "");
                FisrstSeasonObject.put("TP_cluster", "");
                FisrstSeasonObject.put("TP_worktype", "");
                MydayPlanDataList.put(FisrstSeasonObject);

                SecondSeasonObject.put("SFCode",SharedPref.getSfCode(requireContext()));
                JSONObject TPDtSecondSeasonObject = new JSONObject();
                TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
                SecondSeasonObject.put("TPDt", TPDtSecondSeasonObject);
                SecondSeasonObject.put("WT", mWTCode2);
                SecondSeasonObject.put("WTNm", mWTName2);
                SecondSeasonObject.put("FWFlg", mFwFlg2);
                SecondSeasonObject.put("SFMem", mHQCode2);
                SecondSeasonObject.put("HQNm", mHQName2);
                SecondSeasonObject.put("Pl", mTowncode2);
                SecondSeasonObject.put("PlNm", mTownname2);
                SecondSeasonObject.put("Rem", "");
                SecondSeasonObject.put("TpVwFlg", "2");
                SecondSeasonObject.put("TP_Doctor", "");
                SecondSeasonObject.put("TP_cluster", "");
                SecondSeasonObject.put("TP_worktype", "");
                MydayPlanDataList.put(SecondSeasonObject);
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, MydayPlanDataList.toString(), 0));
            }


            OutboxFragment.SetupOutBoxAdapter(requireActivity(), requireContext());
            SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.save_wt_locally));
        } catch (Exception ignored) {

        }
    }

    private void CreateJson() {
        try {
            if (DayPlanCount.equalsIgnoreCase("1")) {
                mHQCode = mHQCode1;
                mTowncode = mTowncode1;
                mHQName = mHQName1;
                mFwFlg = mFwFlg1;
            } else {
                if (IsFeildWorkFlag.equalsIgnoreCase("F1")) {
                    mHQCode = mHQCode1;
                    mTowncode = mTowncode1;
                    mHQName = mHQName1;

                } else if (IsFeildWorkFlag.equalsIgnoreCase("F2")) {
                    mHQCode = mHQCode2;
                    mTowncode = mTowncode2;
                    mHQName = mHQName2;
                } else {
                    mHQCode = "";
                    mTowncode = "";
                    mHQName = "";
                }
            }

            jsonObject = new JSONObject();
            jsonObject.put("tableName", "dayplan");
            jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
            if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                jsonObject.put("Rsf", mHQCode1);
                jsonObject.put("Rsf2", mHQCode2);
            } else {
                jsonObject.put("Rsf", SharedPref.getSfCode(requireContext()));
            }
            jsonObject.put("sf_type",SharedPref.getSfType(requireContext()));
            jsonObject.put("Designation", SharedPref.getDesig(requireContext()));
            jsonObject.put("state_code", SharedPref.getStateCode(requireContext()));
            jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(requireContext()));
            jsonObject.put("town_code", mTowncode1);
            jsonObject.put("Town_name", mTownname1);
            jsonObject.put("WT_code", mWTCode1);
            jsonObject.put("WTName", mWTName1);
            jsonObject.put("FwFlg", mFwFlg1);

            jsonObject.put("town_code2", mTowncode2);
            jsonObject.put("Town_name2", mTownname2);
            jsonObject.put("WT_code2", mWTCode2);
            jsonObject.put("WTName2", mWTName2);
            jsonObject.put("FwFlg2", mFwFlg2);

            jsonObject.put("Remarks", mRemarks1);
            jsonObject.put("location", gpsTrack.getLatitude()+":"+gpsTrack.getLongitude());
            jsonObject.put("address",CommonUtilsMethods.gettingAddress(getActivity(), gpsTrack.getLatitude(), gpsTrack.getLongitude(), false));
            jsonObject.put("InsMode", "0");
            jsonObject.put("Appver", getResources().getString(R.string.app_version));
            jsonObject.put("Mod", getResources().getString(R.string.app_mode));
            jsonObject.put("SubmittedDate", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
            jsonObject.put("TpVwFlg", "0");
            jsonObject.put("TP_cluster", "");
            jsonObject.put("TP_worktype", "");
            jsonObject.put("day_flag", "1");


        } catch (Exception ignored) {

        }
    }

    private void CallCheckOutAPI() {
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/activity");
        Call<JsonElement> callCheckInOut = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonCheck.toString());
        callCheckInOut.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("CheckInOut", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            CheckInOutStatus = obj.getString("msg");
                        }

                        if (CheckInOutStatus.equalsIgnoreCase("1")) {
                            SharedPref.setCheckInTime(requireContext(), "");
                            SharedPref.setCheckTodayCheckInOut(requireContext(), "");
                            CallDialogAfterCheckOut();
                        } else {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.toast_leave_posted));
                        }
                        progressDialog.dismiss();
                    } catch (Exception ignored) {
                        progressDialog.dismiss();
                    }
                } else {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.contact_admin_out));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.toast_response_failed));
                progressDialog.dismiss();
            }
        });
    }

    private void CallFinalSubmitAPI() {
        try {
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/daysubmit");
            Call<JsonElement> callFinalSubmit = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, finalSubmitJSONObject.toString());
            callFinalSubmit.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    assert response.body() != null;
                    Log.v("FinalSubmit", response.body() + "--" + response.isSuccessful());
                    if(response.isSuccessful()) {
                        try {
                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, "[]", 0));
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            HomeDashBoard.canMoveNextDate = false;
                            FinalSubmitStatus = jsonObject.getString("Msg");
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.day_submitted_successfully));
                            progressDialog.dismiss();
                        } catch (Exception ignored) {
                            progressDialog.dismiss();
                        }
                    }else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.cannot_submit_work_plan));
                        progressDialog.dismiss();
                    }
                    HomeDashBoard.checkAndSetEntryDate(requireContext());
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.toast_response_failed));
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CallDialogAfterCheckOut() {
        dialogAfterCheckOut = new Dialog(requireContext());
        dialogAfterCheckOut.setContentView(R.layout.dialog_checkindata);
        dialogAfterCheckOut.setCancelable(false);
        Objects.requireNonNull(dialogAfterCheckOut.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnClose = dialogAfterCheckOut.findViewById(R.id.btn_close);
        tvHeading = dialogAfterCheckOut.findViewById(R.id.txt_heading);
        tvDateTimeAfter = dialogAfterCheckOut.findViewById(R.id.txt_date_time);
        tvAddress = dialogAfterCheckOut.findViewById(R.id.txt_address);
        tvLat = dialogAfterCheckOut.findViewById(R.id.txt_lat);
        tvLong = dialogAfterCheckOut.findViewById(R.id.txt_long);

        tvHeading.setText(getResources().getString(R.string.check_out));

        tvDateTimeAfter.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
        tvLat.setText(String.valueOf(latitude));
        tvLong.setText(String.valueOf(longitude));
        tvAddress.setText(address);

        btnClose.setOnClickListener(v -> {
            dialogAfterCheckOut.dismiss();
            try {
                HomeDashBoard.dialogCheckInOut.show();
            } catch (Exception ignored) {

            }
        });

        dialogAfterCheckOut.show();
    }


    public void MyDayPlanSubmit() {
        try {
        /*    if (DayPlanCount.equalsIgnoreCase("1")) {
                mHQCode = mHQCode1;
                mTowncode = mTowncode1;
                mHQName = mHQName1;
                mFwFlg = mFwFlg1;
            } else {
                if (IsFeildWorkFlag.equalsIgnoreCase("F1")) {
                    mHQCode = mHQCode1;
                    mTowncode = mTowncode1;
                    mHQName = mHQName1;

                } else if (IsFeildWorkFlag.equalsIgnoreCase("F2")) {
                    mHQCode = mHQCode2;
                    mTowncode = mTowncode2;
                    mHQName = mHQName2;
                } else {
                    mHQCode = "";
                    mTowncode = "";
                    mHQName = "";
                }
            }*/

            binding.progressSumit.setVisibility(View.VISIBLE);

        /*    JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "dayplan");
            jsonObject.put("sfcode", loginResponse.getSF_Code());
            jsonObject.put("division_code", loginResponse.getDivision_Code());
            if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                jsonObject.put("Rsf", mHQCode1);
                jsonObject.put("Rsf2", mHQCode2);
            } else {
                jsonObject.put("Rsf", loginResponse.getSF_Code());
            }
            jsonObject.put("sf_type", loginResponse.getSf_type());
            jsonObject.put("Designation", SharedPref.getDesig(requireContext()));
            jsonObject.put("state_code", loginResponse.getState_Code());
            jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
            jsonObject.put("town_code", mTowncode1);
            jsonObject.put("Town_name", mTownname1);
            jsonObject.put("WT_code", mWTCode1);
            jsonObject.put("WTName", mWTName1);
            jsonObject.put("FwFlg", mFwFlg1);

            jsonObject.put("town_code2", mTowncode2);
            jsonObject.put("Town_name2", mTownname2);
            jsonObject.put("WT_code2", mWTCode2);
            jsonObject.put("WTName2", mWTName2);
            jsonObject.put("FwFlg2", mFwFlg2);

            jsonObject.put("Remarks", mRemarks1);
            jsonObject.put("location", "");
            jsonObject.put("location2", "");
            jsonObject.put("InsMode", "0");
            jsonObject.put("Appver", getResources().getString(R.string.app_version));
            jsonObject.put("Mod", "");
            jsonObject.put("SubmittedDate", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
            jsonObject.put("TpVwFlg", "0");
            jsonObject.put("TP_cluster", "");
            jsonObject.put("TP_worktype", "");*/

            Log.e("todayCallList:Object",jsonObject.toString());

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "edetsave/dayplan");
            Call<JsonElement> saveMyDayPlan = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());

            saveMyDayPlan.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.d("todayCallList:Code", response.code() + " - " + response);
                    if (response.isSuccessful()) {
                        binding.progressSumit.setVisibility(View.GONE);
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                                if (!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                                    SharedPref.saveHq(requireContext(), mHQName, mHQCode);
                                } else {
                                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()),  SharedPref.getSfCode(requireContext()));
                                }

                                SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode);
                                if(mFwFlg1.equalsIgnoreCase("F")||mFwFlg2.equalsIgnoreCase("F")){
                                    SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(),true,true);
                                }else {
                                    SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(),true,false);

                                }

                                JSONArray MydayPlanDataList = new JSONArray();
                                JSONObject FisrstSeasonObject = new JSONObject();
                                JSONObject SecondSeasonObject = new JSONObject();

                                FisrstSeasonObject.put("SFCode",SharedPref.getSfCode(requireContext()));
                                JSONObject TPDtFisrstSeasonObject = new JSONObject();
                                TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
                                FisrstSeasonObject.put("TPDt", TPDtFisrstSeasonObject);
                                FisrstSeasonObject.put("WT", mWTCode1);
                                FisrstSeasonObject.put("WTNm", mWTName1);
                                FisrstSeasonObject.put("FWFlg", mFwFlg1);
                                FisrstSeasonObject.put("SFMem", mHQCode1);
                                FisrstSeasonObject.put("HQNm", mHQName1);
                                FisrstSeasonObject.put("Pl", mTowncode1);
                                FisrstSeasonObject.put("PlNm", mTownname1);
                                FisrstSeasonObject.put("Rem", "");
                                FisrstSeasonObject.put("TpVwFlg", "2");
                                FisrstSeasonObject.put("TP_Doctor", "");
                                FisrstSeasonObject.put("TP_cluster", "");
                                FisrstSeasonObject.put("TP_worktype", "");
                                MydayPlanDataList.put(FisrstSeasonObject);
                                if (DayPlanCount.equalsIgnoreCase("2")) {
                                    SecondSeasonObject.put("SFCode",SharedPref.getSfCode(requireContext()));
                                    JSONObject TPDtSecondSeasonObject = new JSONObject();
                                    TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
                                    SecondSeasonObject.put("TPDt", TPDtSecondSeasonObject);
                                    SecondSeasonObject.put("WT", mWTCode2);
                                    SecondSeasonObject.put("WTNm", mWTName2);
                                    SecondSeasonObject.put("FWFlg", mFwFlg2);
                                    SecondSeasonObject.put("SFMem", mHQCode2);
                                    SecondSeasonObject.put("HQNm", mHQName2);
                                    SecondSeasonObject.put("Pl", mTowncode2);
                                    SecondSeasonObject.put("PlNm", mTownname2);
                                    SecondSeasonObject.put("Rem", "");
                                    SecondSeasonObject.put("TpVwFlg", "2");
                                    SecondSeasonObject.put("TP_Doctor", "");
                                    SecondSeasonObject.put("TP_cluster", "");
                                    SecondSeasonObject.put("TP_worktype", "");
                                    MydayPlanDataList.put(SecondSeasonObject);
                                }
                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, MydayPlanDataList.toString(), 0));
                            } else {
                                setUpMyDayplan();
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    binding.progressSumit.setVisibility(View.GONE);
                    setUpMyDayplan();
                    Log.e("VALUES", String.valueOf(t));
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.toast_response_failed));
                }
            });
        } catch (Exception ignored) {
        }
    }


    public void getData(String hqCode) {
        if (DayPlanCount.equalsIgnoreCase("1")) {
            binding.progressHq1.setVisibility(View.VISIBLE);
        } else {
            binding.progressHq2.setVisibility(View.VISIBLE);
        }

        List<MasterSyncItemModel> list = new ArrayList<>();
        list.add(new MasterSyncItemModel("Doctor", 0, "Doctor", "getdoctors", Constants.DOCTOR + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", 0, "Doctor", "getchemist", Constants.CHEMIST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", 0, "Doctor", "getstockist", Constants.STOCKIEST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", 0, "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hqCode, 0, false));
        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Cluster", 0, "Doctor", "getterritory", Constants.CLUSTER + hqCode, 0, false));


        for (int i = 0; i < list.size(); i++) {
            syncMaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hqCode);
        }
    }

    public void syncMaster(String masterFor, String remoteTableName, String LocalTableKeyName, String hqCode) {
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(requireContext());
                String pathUrl = SharedPref.getPhpPathUrl(requireContext());
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                api_interface = RetrofitClient.getRetrofit(getActivity(), baseUrl + replacedUrl);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode",SharedPref.getSfCode(requireContext()));
                jsonObject.put("division_code",SharedPref.getDivisionCode(requireContext()));
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", SharedPref.getSfType(requireContext()));
                jsonObject.put("Designation", SharedPref.getDesig(requireContext()));
                jsonObject.put("state_code", SharedPref.getStateCode(requireContext()));
                jsonObject.put("subdivision_code",SharedPref.getSubdivisionCode(requireContext()));

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();

                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + masterFor + " -- " + remoteTableName + " : " + Objects.requireNonNull(response.body()));
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
                                                masterDataDao.saveMasterSyncStatus(LocalTableKeyName, 1);
                                            }
                                        }

                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(LocalTableKeyName, jsonArray.toString(), 0));

                                            if (LocalTableKeyName.startsWith(Constants.CLUSTER)) {
                                                if (DayPlanCount.equalsIgnoreCase("1")) {
                                                    binding.progressHq1.setVisibility(View.GONE);
                                                } else {
                                                    binding.progressHq2.setVisibility(View.GONE);
                                                }
                                                getDatabaseHeadQuarters(hqCode);
                                            }
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {


                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
            if (DayPlanCount.equalsIgnoreCase("1")) {
                binding.progressHq1.setVisibility(View.GONE);
            } else {
                binding.progressHq2.setVisibility(View.GONE);
            }
        }
    }


    private void getDatabaseHeadQuarters(String hqCode) {

        cluster.clear();
        multiple_cluster_list.clear();
        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                multiple_cluster_list.add(new Multicheckclass_clust(jsonObject.getString("Code"), jsonObject.getString("Name"), "", false));

                cluster.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }


    public void setUpMyDayplan() {

        try {
            binding.progressWt1.setVisibility(View.VISIBLE);
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.MY_DAY_PLAN).getMasterSyncDataJsonArray();
            JSONArray worktypedata = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(),false,false);
            if (workTypeArray.length() > 0) {
                if(HomeDashBoard.selectedDate != null) {
                    SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                    if(workTypeArray.length() == 2) {
                        binding.cardPlan2.setVisibility(View.VISIBLE);
                        binding.llDelete.setVisibility(View.GONE);
                    }else {
                        binding.cardPlan2.setVisibility(View.GONE);
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    JSONObject FirstSeasonDayPlanObject = workTypeArray.getJSONObject(0);
                    String DayplanDate1 = FirstSeasonDayPlanObject.getJSONObject("TPDt").getString("date");
                    String CurrentDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));

                    Date FirstPlanDate = sdf.parse(DayplanDate1);
                    Date CurentDate = sdf.parse(CurrentDate);
                    String TerritoryFlag1 = "Y", TerritoryFlag2 = "Y";

                    if(Objects.requireNonNull(FirstPlanDate).equals(CurentDate)) {
                        mTowncode1 = FirstSeasonDayPlanObject.getString("Pl");
                        mTownname1 = FirstSeasonDayPlanObject.getString("PlNm");
                        mWTCode1 = FirstSeasonDayPlanObject.getString("WT");
                        mWTName1 = FirstSeasonDayPlanObject.getString("WTNm");
                        mFwFlg1 = FirstSeasonDayPlanObject.getString("FWFlg");
                        mHQCode1 = FirstSeasonDayPlanObject.getString("SFMem");
                        mHQName1 = FirstSeasonDayPlanObject.getString("HQNm");
                        mRemarks1 = FirstSeasonDayPlanObject.getString("Rem");
                        chk_cluster = FirstSeasonDayPlanObject.getString("Pl");


                        if(worktypedata.length()>0) {
                            for (int i = 0; i<worktypedata.length(); i++) {
                                JSONObject mJsonObject = worktypedata.getJSONObject(i);
                                if(mJsonObject.getString("Code").equalsIgnoreCase(mWTCode1)) {
                                    TerritoryFlag1 = mJsonObject.getString("TerrSlFlg");
                                }
                            }
                        }

                        if(!HQList.isEmpty()) {
                            for(JSONObject hqJsonObject: HQList){
                                if((mHQCode1).equalsIgnoreCase(hqJsonObject.getString("id"))){
                                    mHQName1 = hqJsonObject.getString("name");
                                    break;
                                }
                            }
                        }

                        if(TerritoryFlag1.equalsIgnoreCase("N")) {
                            binding.rlheadquates1.setVisibility(View.GONE);
                            binding.rlcluster1.setVisibility(View.GONE);
                            binding.txtWorktype1.setText(mWTName1);
                            binding.txtCluster1.setText("");
                            binding.txtheadquaters1.setText("");
                            SharedPref.saveHq(requireContext(), "", "");
                            SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                        }else if(TerritoryFlag1.equalsIgnoreCase("Y")) {
                            if(!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                                binding.rlheadquates1.setVisibility(View.VISIBLE);
                                SharedPref.saveHq(requireContext(), mHQName1, mHQCode1);

                            }else {
                                binding.rlheadquates1.setVisibility(View.GONE);
                                SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));

                            }

                            binding.rlcluster1.setVisibility(View.VISIBLE);
                            binding.txtWorktype1.setText(mWTName1);
                            binding.txtCluster1.setText(mTownname1);
                            binding.txtheadquaters1.setText(mHQName1);
                            SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode1);

                        }

                        binding.llPlan1.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
                        binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        if(mFwFlg1.equalsIgnoreCase("F")) {

                            binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
                        }else {
                            binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        }
                        binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
                        binding.rlworktype1.setEnabled(false);
                        binding.rlcluster1.setEnabled(false);
                        binding.rlheadquates1.setEnabled(false);
                        binding.txtAddPlan.setEnabled(true);
                        binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
                        binding.txtSave.setEnabled(false);
                        mSubmitflag = "S1";

                        String dateOnlyString = sdf.format(FirstPlanDate);
                        String selectedDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_27, dateOnlyString);
                        HomeDashBoard.binding.textDate.setText(selectedDate);

                    }
//                else {
//                    HomeDashBoard.binding.textDate.setText(CommonUtilsMethods.getCurrentInstance("MMMM d, yyyy"));
//                    SharedPref.saveHq(requireContext(), "", "");
//                    SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
//                    binding.txtWorktype1.setText("");
//                    binding.txtCluster1.setText("");
//                    binding.txtheadquaters1.setText("");
//                }

                    binding.llPlan2.setBackground(null);
                    binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlworktype2.setEnabled(true);
                    binding.rlcluster2.setEnabled(true);
                    binding.rlheadquates2.setEnabled(true);
                    binding.txtWorktype2.setText("");
                    binding.txtCluster2.setText("");
                    binding.txtheadquaters2.setText("");

                    if(workTypeArray.length() == 2) {
                        JSONObject SecondSeasonDayPlanObject = workTypeArray.getJSONObject(1);
                        String TPDt2 = SecondSeasonDayPlanObject.getString("TPDt");
                        JSONObject jsonObject12 = new JSONObject(TPDt2);
                        String dayPlan_Date2 = jsonObject12.getString("date");
                        Date SecondPlanDate = sdf.parse(dayPlan_Date2);

                        if(Objects.requireNonNull(SecondPlanDate).equals(CurentDate)) {
                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtAddPlan.setEnabled(false);
                            mTowncode2 = SecondSeasonDayPlanObject.getString("Pl");
                            mTownname2 = SecondSeasonDayPlanObject.getString("PlNm");
                            mWTCode2 = SecondSeasonDayPlanObject.getString("WT");
                            mWTName2 = SecondSeasonDayPlanObject.getString("WTNm");
                            mFwFlg2 = SecondSeasonDayPlanObject.getString("FWFlg");
                            mHQCode2 = SecondSeasonDayPlanObject.getString("SFMem");
                            mHQName2 = SecondSeasonDayPlanObject.getString("HQNm");
                            //   mRemarks1 = SecondSeasonDayPlanObject.getString("Rem");


                            if(worktypedata.length()>0) {
                                for (int i = 0; i<worktypedata.length(); i++) {
                                    JSONObject mJsonObject = worktypedata.getJSONObject(i);
                                    if(mJsonObject.getString("Code").equalsIgnoreCase(mWTCode2)) {
                                        TerritoryFlag2 = mJsonObject.getString("TerrSlFlg");
                                    }
                                }
                            }

                            if(!HQList.isEmpty()) {
                                for(JSONObject hqJsonObject: HQList){
                                    if((mHQCode2).equalsIgnoreCase(hqJsonObject.getString("id"))){
                                        mHQName2 = hqJsonObject.getString("name");
                                        break;
                                    }
                                }
                            }

                            if(TerritoryFlag2.equalsIgnoreCase("N")) {
                                binding.rlheadquates2.setVisibility(View.GONE);
                                binding.rlcluster2.setVisibility(View.GONE);
                                binding.txtWorktype2.setText(mWTName2);
                                binding.txtCluster2.setText("");
                                binding.txtheadquaters2.setText("");

                            }else if(TerritoryFlag2.equalsIgnoreCase("Y")) {
                                if(!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                                    binding.rlheadquates2.setVisibility(View.VISIBLE);
                                    SharedPref.saveHq(requireContext(), mHQName2, mHQCode2);
                                }else {
                                    binding.rlheadquates2.setVisibility(View.GONE);
                                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                                }
                                binding.rlcluster2.setVisibility(View.VISIBLE);
                                binding.txtWorktype2.setText(mWTName2);
                                binding.txtCluster2.setText(mTownname2);
                                binding.txtheadquaters2.setText(mHQName2);
                                SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode2);
                            }
                            binding.cardPlan2.setCardBackgroundColor(getResources().getColor(R.color.gray_45));
                            binding.llPlan2.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
                            binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            if(mFwFlg2.equalsIgnoreCase("F")) {

                                binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
                            }else {
                                binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                            }
                            binding.rlworktype2.setEnabled(false);
                            binding.rlcluster2.setEnabled(false);
                            binding.rlheadquates2.setEnabled(false);
                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtAddPlan.setEnabled(false);
                        }else {
                            binding.cardPlan2.setVisibility(View.GONE);
                        }


                    }else {
                        binding.cardPlan2.setVisibility(View.GONE);
                    }


                    if(mFwFlg1.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("F")) {
                        SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), true, true);
                    }else {
                        SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), true, false);
                    }
                }
            }else {
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, "[]", 0));
                binding.txtWorktype1.setText("");
                binding.txtCluster1.setText("");
                binding.txtheadquaters1.setText("");
//                    HomeDashBoard.binding.textDate.setText("");
                binding.txtWorktype2.setText("");
                binding.txtCluster2.setText("");
                binding.txtheadquaters2.setText("");
                binding.cardPlan2.setVisibility(View.GONE);
//                    HomeDashBoard.binding.textDate.setText("");
//                    SharedPref.setSelectedDateCal(requireContext(), "");
                if(!SharedPref.getDesig(requireContext()).equalsIgnoreCase("MR")) {
                    SharedPref.saveHq(requireContext(), SharedPref.getHqName(requireContext()), "");
                    SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                }else {
                    SharedPref.saveHq(requireContext(), SharedPref.getHqName(requireContext()), SharedPref.getSfCode(requireContext()));
                    SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                }
                SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), false, false);

                binding.rlworktype1.setEnabled(true);
                binding.rlcluster1.setEnabled(true);
                binding.rlheadquates1.setEnabled(true);
                binding.rlworktype2.setEnabled(true);
                binding.rlcluster2.setEnabled(true);
                binding.rlheadquates2.setEnabled(true);
                binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                binding.txtAddPlan.setEnabled(false);
                binding.txtSave.setTextColor(getResources().getColor(R.color.black));
                binding.txtSave.setEnabled(true);
                binding.llPlan1.setBackground(null);
                binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.llPlan2.setBackground(null);
                binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.cardPlan2.setVisibility(View.GONE);
            }

        } catch (Exception a) {
            a.printStackTrace();
        }
        binding.progressWt1.setVisibility(View.GONE);
    }


    public void syncMyDayPlan() {
        if(HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
            try {
                api_interface = RetrofitClient.getRetrofit(getActivity(), SharedPref.getCallApiUrl(requireContext()));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", "gettodaydcr");
                jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
                jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
                jsonObject.put("Rsf", SharedPref.getHqCode(requireContext()));
                jsonObject.put("sf_type", SharedPref.getSfType(requireContext()));
                jsonObject.put("Designation", SharedPref.getDesig(requireContext()));
                jsonObject.put("state_code", SharedPref.getStateCode(requireContext()));
                jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(requireContext()));
                jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));

                Log.v("Mydayplan", "--json-- " + jsonObject);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();

                        if(response.isSuccessful()) {
                            Log.e("test mydayplan", "response : " + Objects.requireNonNull(response.body()));
                            try {
                                JsonElement jsonElement = response.body();
                                if(!jsonElement.isJsonNull()) {
                                    if(jsonElement.isJsonArray()) {
                                        JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                        jsonArray = new JSONArray(jsonArray1.toString());
                                        success = true;
                                    }else if(jsonElement.isJsonObject()) {
                                        JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                        JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                        if(!jsonObject2.has("success")) {
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        }else if(jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(Constants.MY_DAY_PLAN, 1);
                                        }
                                    }

                                    if(success) {
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, jsonArray.toString(), 0));
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            setUpMyDayplan();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        Log.e("Mydayplan", "onFailure: ");
                        t.printStackTrace();
                    }
                });

            } catch (JSONException a) {
                a.printStackTrace();
            }
        }
    }

    private void finalSubmit(String remark){
        gpsTrack = new GPSTrack(requireContext());
        latitude = gpsTrack.getLatitude();
        longitude = gpsTrack.getLongitude();
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            address = CommonUtilsMethods.gettingAddress(requireActivity(), latitude, longitude, false);
        } else {
            address = "No Address Found";
        }
        jsonCheck = new JSONObject();
        finalSubmitJSONObject = new JSONObject();
        try {
            jsonCheck.put("tableName", "savetp_attendance");
            jsonCheck.put("sfcode", SharedPref.getSfCode(requireContext()));
            jsonCheck.put("division_code", SharedPref.getDivisionCode(requireContext()));
            jsonCheck.put("lat", latitude);
            jsonCheck.put("long", longitude);
            jsonCheck.put("address", address);
            jsonCheck.put("update", "1");
            jsonCheck.put("Appver", getResources().getString(R.string.app_version));
            jsonCheck.put("Mod", APP_MODE);
            jsonCheck.put("sf_emp_id",  SharedPref.getSfEmpId(requireContext()));
            jsonCheck.put("sfname", SharedPref.getSfName(requireContext()));
            jsonCheck.put("Employee_Id", "");
            jsonCheck.put("Check_In", SharedPref.getCheckInTime(requireContext()));
            jsonCheck.put("Check_Out", CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
            jsonCheck.put("DateTime", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd") + " " + CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
            Log.v("CheckInOut", "--json--" + jsonCheck);

            finalSubmitJSONObject.put("tableName", "final_day");
            finalSubmitJSONObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            finalSubmitJSONObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
            finalSubmitJSONObject.put("Rsf", SharedPref.getHqCode(requireContext()));
            finalSubmitJSONObject.put("sf_type", SharedPref.getSfType(requireContext()));
            finalSubmitJSONObject.put("Designation", SharedPref.getDesig(requireContext()));
            finalSubmitJSONObject.put("state_code", SharedPref.getStateCode(requireContext()));
            finalSubmitJSONObject.put("subdivision_code", SharedPref.getSubdivisionCode(requireContext()));
            finalSubmitJSONObject.put("day_flag", "1");
            finalSubmitJSONObject.put("Appver", getResources().getString(R.string.app_version));
            finalSubmitJSONObject.put("Mod", APP_MODE);
            finalSubmitJSONObject.put("Activity_Dt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
            finalSubmitJSONObject.put("current_Dt", CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_1));
            finalSubmitJSONObject.put("day_remarks", remark);
            Log.v("Final Submit", "--json-- " + finalSubmitJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            progressDialog = CommonUtilsMethods.createProgressDialog(requireContext());
            CallFinalSubmitAPI();
//                            MyDayPlanEntriesNeeded.deleteDate(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
//                            HomeDashBoard.checkAndSetEntryDate(requireContext());
//                            CallCheckOutAPI();
        } else {
            offlineCheckInOutDataDao.saveCheckOut(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
            SharedPref.setCheckTodayCheckInOut(requireContext(), "");
            SharedPref.setCheckInTime(requireContext(), "");
            SharedPref.setCheckDateTodayPlan(requireContext(), "");
//            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, HomeDashBoard.dateSync.toString(), 0));
            MyDayPlanEntriesNeeded.deleteDate(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
            HomeDashBoard.checkAndSetEntryDate(requireContext());
            SetupOutBoxAdapter(requireActivity(), requireContext());
            CallDialogAfterCheckOut();
        }
    }

    private void remarksAlertBox() {
        Dialog dialogRemarks = new Dialog(requireActivity());
        dialogRemarks.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogRemarks.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogRemarks.setCancelable(false);
        ImageView iv_close = dialogRemarks.findViewById(R.id.img_close);
        dialogAcknowledge(dialogRemarks);
        dialogRemarks.show();

        iv_close.setOnClickListener(view -> {
            dialogRemarks.dismiss();
        });

    }

    private void dialogFinalSubmit(Dialog dialogRemarks) {
        EditText ed_remarks = dialogRemarks.findViewById(R.id.ed_remark);
        TextView heading = dialogRemarks.findViewById(R.id.tv_head);
        TextView content = dialogRemarks.findViewById(R.id.content);
        Button btn_clear = dialogRemarks.findViewById(R.id.btn_clear);
        Button btn_save = dialogRemarks.findViewById(R.id.btn_save);
        btn_clear.setText(requireContext().getString(R.string.clear));
        btn_save.setText(requireContext().getString(R.string.save));
        content.setVisibility(View.GONE);
        heading.setText(requireContext().getString(R.string.remarks));
        ed_remarks.setVisibility(View.VISIBLE);
        btn_save.setOnClickListener(view -> {
            String remarks = ed_remarks.getText().toString().trim();
            if(!remarks.isEmpty() && remarks.length()>2) {
                dialogRemarks.dismiss();
                remarks = remarks.replaceAll("'", "");
                Log.e("Remarks", "remark : " + remarks);
                finalSubmit(remarks);
            } else {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.please_enter_the_remarks));
            }
        });
        btn_clear.setOnClickListener(view -> {
            ed_remarks.setText("");
        });
    }

    private void dialogAcknowledge(Dialog dialogRemarks) {
        EditText ed_remarks = dialogRemarks.findViewById(R.id.ed_remark);
        TextView heading = dialogRemarks.findViewById(R.id.tv_head);
        TextView content = dialogRemarks.findViewById(R.id.content);
        Button btn_clear = dialogRemarks.findViewById(R.id.btn_clear);
        Button btn_save = dialogRemarks.findViewById(R.id.btn_save);
        heading.setText(requireContext().getString(R.string.are_you_sure));
        btn_save.setText(requireContext().getString(R.string.yes));
        btn_clear.setText(requireContext().getString(R.string.no));
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(view -> {
            dialogFinalSubmit(dialogRemarks);
        });
        btn_clear.setOnClickListener(view -> {
            dialogRemarks.dismiss();
        });
    }
}
