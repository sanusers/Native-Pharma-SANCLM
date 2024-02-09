package saneforce.santrip.activity.homeScreen.fragment.worktype;


import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.CheckInOutNeed;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.DivCode;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.EmpId;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.SfCode;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.SfEmpId;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.SfName;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.SetupOutBoxAdapter;
import static saneforce.santrip.commonClasses.Constants.APP_MODE;
import static saneforce.santrip.commonClasses.Constants.APP_VERSION;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.Toast;

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
import saneforce.santrip.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.WorkplanFragmentBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;


public class WorkPlanFragment extends Fragment implements View.OnClickListener {

    public static String chk_cluster = "";
    public static ArrayList<Multicheckclass_clust> listSelectedCluster = new ArrayList<>();
    public static String mTowncode1 = "", mTownname1 = "", mWTCode1 = "", mWTName1 = "", mFwFlg1 = "", mHQCode1 = "", mHQName1 = "", mRemarks1 = "", mTowncode2 = "", mTownname2 = "", mWTCode2 = "", mWTName2 = "", mFwFlg2 = "", mHQCode2 = "", mHQName2 = "", mHQCode = "", mTowncode = "", mTownname = "", mWTCode = "", mWTName = "", mFwFlg = "", mHQName = "";
    @SuppressLint("StaticFieldLeak")
    public static WorkplanFragmentBinding binding;
    SQLite sqLite;
    String CheckInOutStatus;

    ArrayList<JSONObject> workType_list1 = new ArrayList<>();
    ArrayList<Multicheckclass_clust> multiple_cluster_list = new ArrayList<>();
    ArrayList<JSONObject> HQList = new ArrayList<>();
    ArrayList<JSONObject> cluster = new ArrayList<>();
    JSONObject SelectedWorkType;
    JSONObject SelectedHQ;
    JSONObject SelectedCluster;
    ApiInterface api_interface;
    LoginResponse loginResponse;
    String strClusterID = "", strClusterName = "";

    String worktypeflag = "1", IsFeildWorkFlag = "F0";

    String mSubmitflag = "S0";
    CommonUtilsMethods commonUtilsMethods;
    double latitude, longitude;
    GPSTrack gpsTrack;
    Dialog dialogAfterCheckOut;
    TextView tvDateTimeAfter, tvLat, tvLong, tvAddress, tvHeading;
    Button btnClose;
    String address;
    JSONObject jsonCheck;


    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WorkplanFragmentBinding.inflate(inflater);
        View view = binding.getRoot();
        Log.v("fragment", "workPlan");
        sqLite = new SQLite(getActivity());
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        if (CheckInOutNeed.equalsIgnoreCase("1")) {
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

        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
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

        if (!SharedPref.getCheckDateTodayPlan(requireContext()).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
            if (UtilityClass.isNetworkAvailable(requireContext())) {
                syncMyDayPlan();
                SharedPref.setCheckDateTodayPlan(requireContext(), CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
            } else {
                setUpMyDayplan();
            }
        } else {
            setUpMyDayplan();
        }


        getLocalData();
        return view;
    }


    @SuppressLint("SetTextI18n")
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
                if (worktypeflag.equalsIgnoreCase("1")) {
                    mFwFlg1 = SelectedWorkType.getString("FWFlg");
                    mWTCode1 = SelectedWorkType.getString("Code");
                    mWTName1 = SelectedWorkType.getString("Name");

                    if (SelectedWorkType.getString("FWFlg").equalsIgnoreCase("F")) {
                        IsFeildWorkFlag = "F1";
                        rlculster.setVisibility(View.VISIBLE);
                        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
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

                    if (SelectedWorkType.getString("FWFlg").equalsIgnoreCase("F")) {
                        IsFeildWorkFlag = "F2";
                        rlculster.setVisibility(View.VISIBLE);
                        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
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
                WT_ListAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


//    @SuppressLint("SetTextI18n")
//    public void showClusterAlter() {
//
//        HomeDashBoard.binding.llNav.etSearch.setText("");
//        HomeDashBoard.wk_recycler_view.setVisibility(View.GONE);
//        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
//        drawerLayout.openDrawer(GravityCompat.END);
//        WorkplanListAdapter CL_ListAdapter = new WorkplanListAdapter(getActivity(), cluster, "2");
//        HomeDashBoard.wk_listview.setAdapter(CL_ListAdapter);
//
//
//        HomeDashBoard.txt_wt_plan.setText("Select Cluster");
//        HomeDashBoard.wk_listview.setOnItemClickListener((parent, view, position, id) -> {
//            drawerLayout.closeDrawer(GravityCompat.END);
//            SelectedCluster = CL_ListAdapter.getlisted().get(position);
//            try {
//                binding.txtCluster.setText(SelectedCluster.getString("Name"));
//
//                mTowncode = SelectedCluster.getString("Code");
//                mTownname = SelectedCluster.getString("Name");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        });
//        HomeDashBoard.et_search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//
//                String searchString = s.toString();
//                CL_ListAdapter.getFilter().filter(searchString);
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//    }


    @SuppressLint("SetTextI18n")
    public void showMultiClusterAlter() {
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
                if (worktypeflag.equalsIgnoreCase("1")) {
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
                TextCL.setText("");
                TextHQ.setText(SelectedHQ.getString("name"));
                if (worktypeflag.equalsIgnoreCase("1")) {
                    mHQCode1 = SelectedHQ.getString("id");
                    mHQName1 = SelectedHQ.getString("name");
                    getData(SelectedHQ.getString("id"));

                } else {
                    mHQCode2 = SelectedHQ.getString("id");
                    mHQName2 = SelectedHQ.getString("name");
                    getData(SelectedHQ.getString("id"));
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

        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (worktypeflag.equalsIgnoreCase("1")) {
                    if (!(mWTCode2).equalsIgnoreCase(jsonObject.getString("Code"))) {
                        workType_list1.add(jsonObject);
                    }
                } else {
                    if (!(mWTCode1).equalsIgnoreCase(jsonObject.getString("Code"))) {
                        workType_list1.add(jsonObject);
                    }
                }

//                workType_list1.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + SharedPref.getHqCode(requireContext()));
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);


                if (("," + chk_cluster + ",").contains("," + jsonObject.getString("Code") + ",")) {
                    multiple_cluster_list.add(new Multicheckclass_clust(jsonObject.getString("Code"), jsonObject.getString("Name"), "", true));
                } else {
                    multiple_cluster_list.add(new Multicheckclass_clust(jsonObject.getString("Code"), jsonObject.getString("Name"), "", false));

                }
                cluster.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                HQList.add(jsonObject);
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
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_date), 100);
                } else {
                    ShowWorkTypeAlert(binding.txtWorktype1, binding.rlcluster1, binding.rlheadquates1);
                }

                break;

            case R.id.rlcluster1:

                if (binding.txtheadquaters1.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_hq), 100);
                } else if (loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_worktype), 100);
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

                if (binding.txtheadquaters2.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_hq), 100);
                } else if (loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_worktype), 100);
                    } else {
                        showMultiClusterAlter();
                    }
                } else {
                    showMultiClusterAlter();
                }

                break;

            case R.id.rlheadquates1:
                if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_worktype), 100);
                } else {
                    showHQ(binding.txtheadquaters1, binding.txtCluster1);
                }
                break;

            case R.id.rlheadquates2:

                if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_worktype), 100);
                } else {
                    showHQ(binding.txtheadquaters2, binding.txtCluster2);
                }

                break;
            case R.id.txtSave:

                if (UtilityClass.isNetworkAvailable(getActivity())) {
                    if (worktypeflag.equalsIgnoreCase("1")) {
                        if (binding.txtWorktype1.getText().toString().startsWith("Field")) {
                            if (binding.txtheadquaters1.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                                commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_hq), 100);
                            } else if (binding.txtCluster1.getText().toString().equalsIgnoreCase("")) {
                                commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.select_cluster), 100);
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
                                MyDayPlanSubmit();

                            }
                        } else if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(), "Select WorkType", Toast.LENGTH_SHORT).show();
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
                            MyDayPlanSubmit();
                        }
                    } else {
                        if (binding.txtWorktype2.getText().toString().startsWith("Field")) {
                            if (binding.txtheadquaters2.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                                Toast.makeText(getActivity(), "Select Headquarters", Toast.LENGTH_SHORT).show();


                            } else if (binding.txtCluster2.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(getActivity(), "Select Cluster", Toast.LENGTH_SHORT).show();
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
                                MyDayPlanSubmit();
                            }
                        } else if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(), "Select WorkType", Toast.LENGTH_SHORT).show();
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
                            MyDayPlanSubmit();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "No Internet connectivity!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txtAddPlan:
                mSubmitflag = "S0";
                worktypeflag = "2";
                binding.llDelete.setVisibility(View.VISIBLE);
                binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                binding.txtSave.setTextColor(getResources().getColor(R.color.black));
                binding.txtSave.setEnabled(true);
                binding.cardPlan2.setVisibility(View.VISIBLE);
                getLocalData();
                break;

            case R.id.btnsumit:
                if (CheckInOutNeed.equalsIgnoreCase("1") && !SharedPref.getCheckInTime(requireContext()).isEmpty()) {
                    gpsTrack = new GPSTrack(requireContext());
                    latitude = gpsTrack.getLatitude();
                    longitude = gpsTrack.getLongitude();
                    if (UtilityClass.isNetworkAvailable(requireContext())) {
                        address = CommonUtilsMethods.gettingAddress(requireActivity(), latitude, longitude, false);
                    } else {
                        address = "No Address Found";
                    }
                    jsonCheck = new JSONObject();
                    try {
                        jsonCheck.put("tableName", "savetp_attendance");
                        jsonCheck.put("sfcode", SfCode);
                        jsonCheck.put("division_code", DivCode);
                        jsonCheck.put("lat", latitude);
                        jsonCheck.put("long", longitude);
                        jsonCheck.put("address", address);
                        jsonCheck.put("update", "1");
                        jsonCheck.put("Appver", APP_VERSION);
                        jsonCheck.put("Mod", APP_MODE);
                        jsonCheck.put("sf_emp_id", SfEmpId);
                        jsonCheck.put("sfname", SfName);
                        jsonCheck.put("Employee_Id", EmpId);
                        jsonCheck.put("Check_In", SharedPref.getCheckInTime(requireContext()));
                        jsonCheck.put("Check_Out", CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
                        jsonCheck.put("DateTime", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd") + " " + CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
                        Log.v("CheckInOut", "--json--" + jsonCheck);
                    } catch (JSONException ignored) {
                    }
                   /* if (UtilityClass.isNetworkAvailable(requireContext())) {
                        CallCheckOutAPI();
                    } else {*/
                    sqLite.saveCheckOut(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
                    SharedPref.setCheckTodayCheckInOut(requireContext(), "");
                    SharedPref.setCheckInTime(requireContext(), "");
                    SharedPref.setCheckDateTodayPlan(requireContext(), "");
                    SetupOutBoxAdapter(requireActivity(), sqLite, requireContext());
                    CallDialogAfterCheckOut();
                    // }
                } else {
                    commonUtilsMethods.ShowToast(requireContext(), getString(R.string.submit_checkin), 100);
                }

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
                worktypeflag = "1";
                mSubmitflag = "S1";
                binding.cardPlan2.setVisibility(View.GONE);
                break;
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
                            CallDialogAfterCheckOut();
                            SharedPref.setCheckInTime(requireContext(), "");
                            SharedPref.setCheckTodayCheckInOut(requireContext(), "");
                        } else {
                            commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.toast_leave_posted), 100);
                        }

                    } catch (Exception ignored) {

                    }
                } else {
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.contact_admin), 100);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.toast_response_failed), 100);
            }
        });
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
        if (worktypeflag.equalsIgnoreCase("1")) {
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

        binding.progressSumit.setVisibility(View.VISIBLE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "dayplan");
            jsonObject.put("sfcode", loginResponse.getSF_Code());
            jsonObject.put("division_code", loginResponse.getDivision_Code());
            if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                jsonObject.put("Rsf", mHQCode1);
                jsonObject.put("Rsf2", mHQCode2);
            } else {
                jsonObject.put("Rsf", loginResponse.getSF_Code());
            }
            jsonObject.put("sf_type", loginResponse.getSf_type());
            jsonObject.put("Designation", loginResponse.getDesig());
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
            jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, HomeDashBoard.binding.textDate.getText().toString()));
            jsonObject.put("TpVwFlg", "");
            jsonObject.put("TP_cluster", "");
            jsonObject.put("TP_worktype", "");
            Log.e("VALUES", jsonObject.toString());

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
                                commonUtilsMethods.ShowToast(requireContext(), json.getString("Msg"), 100);

                                if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                                    SharedPref.saveHq(requireContext(), mHQName, mHQCode);
                                    syncMyDayPlan();
                                    SharedPref.setTodayDayPlanSfCode(requireContext(), mHQCode);
                                    SharedPref.setTodayDayPlanSfName(requireContext(), mHQName);
                                } else {
                                    SharedPref.setTodayDayPlanSfCode(requireContext(), loginResponse.getSF_Code());
                                    SharedPref.setTodayDayPlanSfName(requireContext(), loginResponse.getSF_Name());
                                }
                                SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode);
                            } else {
                                commonUtilsMethods.ShowToast(requireContext(), json.getString("Msg"), 100);
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    Log.e("VALUES", String.valueOf(t));
//                    binding.progressSumit.setVisibility(View.GONE);
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.toast_response_failed), 100);
                }
            });

          /*  saveMyDayPlan.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                    Log.d("todayCallList:Code", response.code() + " - " + response);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), json.getString("Msg"), Toast.LENGTH_SHORT).show();

                                if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                                    SharedPref.saveHq(requireContext(), mHQName, mHQCode);
                                    syncMyDayPlan();
                                    SharedPref.setTodayDayPlanSfCode(requireContext(), mHQCode);
                                    SharedPref.setTodayDayPlanSfName(requireContext(), mHQName);
                                } else {
                                    SharedPref.setTodayDayPlanSfCode(requireContext(), loginResponse.getSF_Code());
                                    SharedPref.setTodayDayPlanSfName(requireContext(), loginResponse.getSF_Name());
                                }
                                SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode);
                            } else {
                                Toast.makeText(getActivity(), json.getString("Msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ignored) {

                        }
//                        binding.progressSumit.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {

                    Log.e("VALUES",""+t);
//                    binding.progressSumit.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "MyDayPlan  failure", Toast.LENGTH_SHORT).show();
                }
            });
*/

        } catch (JSONException a) {
            throw new RuntimeException();
        }

    }


    public void getData(String hqCode) {
        if (worktypeflag.equalsIgnoreCase("1")) {
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
                jsonObject.put("sfcode", loginResponse.getSF_Code());
                jsonObject.put("division_code", loginResponse.getDivision_Code());
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", loginResponse.getSf_type());
                jsonObject.put("Designation", loginResponse.getDesig());
                jsonObject.put("state_code", loginResponse.getState_Code());
                jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());

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
                                                sqLite.saveMasterSyncStatus(LocalTableKeyName, 1);
                                            }
                                        }

                                        if (success) {
                                            sqLite.saveMasterSyncData(LocalTableKeyName, jsonArray.toString(), 0);

                                            if (LocalTableKeyName.startsWith(Constants.CLUSTER)) {
                                                if (worktypeflag.equalsIgnoreCase("1")) {
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
            commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.no_network), 100);

        }
    }


    private void getDatabaseHeadQuarters(String hqCode) {

        cluster.clear();
        multiple_cluster_list.clear();
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hqCode);
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
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.MY_DAY_PLAN);
            if (workTypeArray.length() > 0) {
                JSONObject jsonObject = workTypeArray.getJSONObject(0);
                String TPDt = jsonObject.getString("TPDt");
                JSONObject jsonObject1 = new JSONObject(TPDt);
                String dayPlan_Date = jsonObject1.getString("date");
                String CurrentDate = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_15);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date1 = sdf.parse(dayPlan_Date);
                Date date2 = sdf.parse(CurrentDate);
                if (Objects.requireNonNull(date1).equals(date2)) {


                    mTowncode1 = jsonObject.getString("Pl");
                    mTownname1 = jsonObject.getString("PlNm");
                    mWTCode1 = jsonObject.getString("WT");
                    mWTName1 = jsonObject.getString("WTNm");
                    mFwFlg1 = jsonObject.getString("FWFlg");
                    mHQCode1 = jsonObject.getString("SFMem");
                    mHQName1 = jsonObject.getString("HQNm");
                    mRemarks1 = jsonObject.getString("Rem");
                    chk_cluster = jsonObject.getString("Pl");

//                    mTowncode2 = jsonObject.getString("Pl2");
//                    mTownname2 = jsonObject.getString("PlNm2");
//                    mWTCode2 = jsonObject.getString("WT2");
//                    mWTName2= jsonObject.getString("WTNm2");
//                    mFwFlg2 = jsonObject.getString("FWFlg2");
//                    mHQCode2 = jsonObject.getString("SFMem2");
//                    mHQName2 = jsonObject.getString("HQNm2");
//                    mRemarks1 = jsonObject.getString("Rem2");


                    if (!mFwFlg1.equalsIgnoreCase("F")) {
                        binding.rlheadquates1.setVisibility(View.GONE);
                        binding.rlcluster1.setVisibility(View.GONE);
                        binding.txtWorktype1.setText(mWTName1);
                        binding.txtCluster1.setText("");
                        binding.txtheadquaters1.setText("");
                        SharedPref.setTodayDayPlanSfCode(requireContext(), "");
                        SharedPref.setTodayDayPlanSfName(requireContext(), "");
                        SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                    } else {
                        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                            binding.rlheadquates1.setVisibility(View.VISIBLE);
                            SharedPref.saveHq(requireContext(), mHQName1, mHQCode1);
                            SharedPref.setTodayDayPlanSfCode(requireContext(), mHQCode1);
                            SharedPref.setTodayDayPlanSfName(requireContext(), mHQName1);
                        } else {
                            binding.rlheadquates1.setVisibility(View.GONE);
                            SharedPref.setTodayDayPlanSfCode(requireContext(), loginResponse.getSF_Code());
                            SharedPref.setTodayDayPlanSfName(requireContext(), loginResponse.getSF_Name());
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


                    // seccond session
//
//                    if(!mFwFlg2.equalsIgnoreCase("")){
//                        binding.cardPlan2.setVisibility(View.VISIBLE);
//
//                        if (!mFwFlg2.equalsIgnoreCase("F")) {
//                            binding.rlheadquates2.setVisibility(View.GONE);
//                            binding.rlcluster2.setVisibility(View.GONE);
//                            binding.txtWorktype2.setText(mWTName1);
//                            binding.txtCluster2.setText("");
//                            binding.txtheadquaters2.setText("");
//                        SharedPref.setTodayDayPlanSfCode(requireContext(), "");
//                        SharedPref.setTodayDayPlanSfName(requireContext(), "");
//                        SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
//
//                        } else {
//                            if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
//                                binding.rlheadquates2.setVisibility(View.VISIBLE);
//                    SharedPref.saveHq(requireContext(), mHQName1, mHQCode2);
//                    SharedPref.setTodayDayPlanSfCode(requireContext(), mHQCode2);
//                    SharedPref.setTodayDayPlanSfName(requireContext(), mHQName2);
//                            } else {
//                                binding.rlheadquates2.setVisibility(View.GONE);
//                    SharedPref.setTodayDayPlanSfCode(requireContext(), loginResponse.getSF_Code());
//                    SharedPref.setTodayDayPlanSfName(requireContext(), loginResponse.getSF_Name());
//                            }
//
//                            binding.rlcluster2.setVisibility(View.VISIBLE);
//                            binding.txtWorktype2.setText(mWTName1);
//                            binding.txtCluster2.setText(mTownname1);
//                            binding.txtheadquaters2.setText(mHQName1);
//                        }
//
//                        binding.cardPlan2.setCardBackgroundColor(getResources().getColor(R.color.gray_45));
//                        binding.llPlan2.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
//                        binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
//                        binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
//                        if(mFwFlg2.equalsIgnoreCase("F")){
//                            binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
//                        }else {
//                            binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
//                        }
//                    }else {
//                        binding.cardPlan2.setVisibility(View.GONE);
//                    }


                    String dateOnlyString = sdf.format(date1);
                    String selectedDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_27, dateOnlyString);
                    HomeDashBoard.binding.textDate.setText(selectedDate);

                } else {

                    binding.cardPlan2.setVisibility(View.GONE);
                    sqLite.saveMasterSyncData(Constants.MY_DAY_PLAN, "[]", 0);
                    binding.txtWorktype1.setText("");
                    binding.txtCluster1.setText("");
                    binding.txtheadquaters1.setText("");
                    HomeDashBoard.binding.textDate.setText("");
                    binding.txtWorktype2.setText("");
                    binding.txtCluster2.setText("");
                    binding.txtheadquaters2.setText("");
                    HomeDashBoard.binding.textDate.setText("");
                    SharedPref.setTodayDayPlanSfCode(requireContext(), "");
                    SharedPref.setTodayDayPlanSfName(requireContext(), "");
                    SharedPref.setTodayDayPlanClusterCode(requireContext(), "");

                }

            } else {
                binding.cardPlan2.setVisibility(View.GONE);
                HomeDashBoard.binding.textDate.setText(CommonUtilsMethods.getCurrentInstance("MMMM d, yyyy"));
                SharedPref.setTodayDayPlanSfCode(requireContext(), "");
                SharedPref.setTodayDayPlanSfName(requireContext(), "");
                SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                binding.txtWorktype1.setText("");
                binding.txtCluster1.setText("");
                binding.txtheadquaters1.setText("");
            }

        } catch (Exception a) {
            a.printStackTrace();
        }
    }


    public void syncMyDayPlan() {

        try {
            api_interface = RetrofitClient.getRetrofit(getActivity(), SharedPref.getCallApiUrl(requireContext()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "gettodaytpnew");
            jsonObject.put("sfcode", loginResponse.getSF_Code());
            jsonObject.put("division_code", loginResponse.getDivision_Code());
            jsonObject.put("Rsf", SharedPref.getHqCode(requireContext()));
            jsonObject.put("sf_type", loginResponse.getSf_type());
            jsonObject.put("Designation", loginResponse.getDesig());
            jsonObject.put("state_code", loginResponse.getState_Code());
            jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "table/dcrmasterdata");
            Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    boolean success = false;
                    JSONArray jsonArray = new JSONArray();

                    if (response.isSuccessful()) {
                        Log.e("test", "response : " + Objects.requireNonNull(response.body()));
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
                                        sqLite.saveMasterSyncStatus(Constants.MY_DAY_PLAN, 1);
                                    }
                                }

                                if (success) {
                                    sqLite.saveMasterSyncData(Constants.MY_DAY_PLAN, jsonArray.toString(), 0);
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

        } catch (JSONException a) {
            a.printStackTrace();
        }
    }


    void AletboxRemarks() {

        Dialog dialogReject = new Dialog(requireActivity());
        dialogReject.setContentView(R.layout.alertbox_dayplan_remarks);
        Objects.requireNonNull(dialogReject.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReject.setCancelable(false);

        ImageView iv_close = dialogReject.findViewById(R.id.img_close);
        EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
        Button btn_cancel = dialogReject.findViewById(R.id.btnskip);
        Button btn_save = dialogReject.findViewById(R.id.btn_save);
        ed_reason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_reason)});

        btn_cancel.setOnClickListener(view1 -> {
            mSubmitflag = "S2";
            ed_reason.setText("");
            dialogReject.dismiss();
        });

        iv_close.setOnClickListener(view12 -> {
            mSubmitflag = "S1";
            ed_reason.setText("");
            dialogReject.dismiss();
        });
        btn_save.setOnClickListener(view12 -> {
            mSubmitflag = "S2";
            mRemarks1 = ed_reason.getText().toString();
            dialogReject.dismiss();
        });
        dialogReject.show();


    }
}