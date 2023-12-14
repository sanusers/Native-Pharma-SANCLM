package saneforce.sanclm.activity.homeScreen.fragment.worktype;


import static saneforce.sanclm.activity.homeScreen.HomeDashBoard.drawerLayout;
import static saneforce.sanclm.activity.homeScreen.HomeDashBoard.text_date;
import static saneforce.sanclm.activity.homeScreen.HomeDashBoard.txt_cl_done;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.WorkplanFragmentBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.TimeUtils;


public class WorkPlanFragment extends Fragment implements View.OnClickListener {

    public static String chk_cluster = "";
    public static ArrayList<Multicheckclass_clust> listSelectedCluster = new ArrayList<>();
    public static String mTowncode = "", mTownname = "", mWTCode = "", mWTName = "", mFwFlg = "", mHQCode = "", mHQName = "", mRemarks = "";
    WorkplanFragmentBinding binding;
    SQLite sqLite;
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

    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WorkplanFragmentBinding.inflate(inflater);
        View view = binding.getRoot();
        Log.v("fragment", "workPlan");
        sqLite = new SQLite(getActivity());
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.progressHq.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            binding.progressSumit.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
        }
        api_interface = RetrofitClient.getRetrofit(getContext(), SharedPref.getCallApiUrl(requireContext()));

        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
            binding.rlheadquates.setVisibility(View.VISIBLE);
        } else {
            binding.rlheadquates.setVisibility(View.GONE);
        }


        binding.btnsumit.setOnClickListener(this);
        binding.rlworktype.setOnClickListener(this);
        binding.rlcluster.setOnClickListener(this);
        binding.rlheadquates.setOnClickListener(this);

        setUpMyDayplan();
        getLocalData();


        return view;
    }


    @SuppressLint("SetTextI18n")
    public void ShowWorkTypeAlert() {

        HomeDashBoard.et_search.setText("");
        HomeDashBoard.txt_wt_plan.setText("Select WorkType");
        drawerLayout.openDrawer(GravityCompat.END);
        HomeDashBoard.wk_recycler_view.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);

        WorkplanListAdapter WT_ListAdapter = new WorkplanListAdapter(getActivity(), workType_list1, "1");
        HomeDashBoard.wk_listview.setAdapter(WT_ListAdapter);
        HomeDashBoard.wk_listview.setOnItemClickListener((parent, view, position, id) -> {
            drawerLayout.closeDrawer(GravityCompat.END);
            SelectedWorkType = WT_ListAdapter.getlisted().get(position);
            try {
                binding.txtWorktype.setText(SelectedWorkType.getString("Name"));

                mFwFlg = SelectedWorkType.getString("FWFlg");
                mWTCode = SelectedWorkType.getString("Code");
                mWTName = SelectedWorkType.getString("Name");

                if (SelectedWorkType.getString("FWFlg").equalsIgnoreCase("F")) {
                    binding.rlcluster.setVisibility(View.VISIBLE);
                    if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                        binding.rlheadquates.setVisibility(View.VISIBLE);
                    }

                } else {
                    mTowncode = "";
                    mTownname = "";
                    mHQCode = "";
                    mHQName = "";
                    chk_cluster = "";
                    binding.rlcluster.setVisibility(View.GONE);
                    binding.rlheadquates.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        HomeDashBoard.et_search.addTextChangedListener(new TextWatcher() {
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


    @SuppressLint("SetTextI18n")
    public void showClusterAlter() {

        HomeDashBoard.et_search.setText("");
        HomeDashBoard.wk_recycler_view.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
        drawerLayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter CL_ListAdapter = new WorkplanListAdapter(getActivity(), cluster, "2");
        HomeDashBoard.wk_listview.setAdapter(CL_ListAdapter);


        HomeDashBoard.txt_wt_plan.setText("Select Cluster");
        HomeDashBoard.wk_listview.setOnItemClickListener((parent, view, position, id) -> {
            drawerLayout.closeDrawer(GravityCompat.END);
            SelectedCluster = CL_ListAdapter.getlisted().get(position);
            try {
                binding.txtCluster.setText(SelectedCluster.getString("Name"));

                mTowncode = SelectedCluster.getString("Code");
                mTownname = SelectedCluster.getString("Name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        HomeDashBoard.et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String searchString = s.toString();
                CL_ListAdapter.getFilter().filter(searchString);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @SuppressLint("SetTextI18n")
    public void showMultiClusterAlter() {

        HomeDashBoard.et_search.setText("");
        txt_cl_done.setVisibility(View.VISIBLE);
        HomeDashBoard.wk_recycler_view.setVisibility(View.VISIBLE);
        HomeDashBoard.wk_listview.setVisibility(View.GONE);
        drawerLayout.openDrawer(GravityCompat.END);
        HomeDashBoard.txt_wt_plan.setText("Select Cluster");

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
        HomeDashBoard.wk_recycler_view.setLayoutManager(linearLayoutManager);
        HomeDashBoard.wk_recycler_view.setAdapter(multiClusterAdapter);

        HomeDashBoard.et_search.addTextChangedListener(new TextWatcher() {
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


        txt_cl_done.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.END);

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
                        binding.txtCluster.setText(strClusterName);
                    }

                }
                mTowncode = strClusterID;
                mTownname = strClusterName;

            }


        });
    }

    @SuppressLint("SetTextI18n")
    public void showHQ() {
        txt_cl_done.setVisibility(View.GONE);
        HomeDashBoard.wk_recycler_view.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.et_search.setText("");
        HomeDashBoard.txt_wt_plan.setText("Select HeadQuarters");

        drawerLayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter HQ_ListAdapter = new WorkplanListAdapter(getActivity(), HQList, "3");
        HomeDashBoard.wk_listview.setAdapter(HQ_ListAdapter);

        HomeDashBoard.wk_listview.setOnItemClickListener((parent, view, position, id) -> {
            SelectedHQ = HQ_ListAdapter.getlisted().get(position);
            drawerLayout.closeDrawer(GravityCompat.END);
            try {
                binding.txtCluster.setText("");
                binding.txtheadquaters.setText(SelectedHQ.getString("name"));
                mHQCode = SelectedHQ.getString("id");
                mHQName = SelectedHQ.getString("name");
                getData(SelectedHQ.getString("id"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        HomeDashBoard.et_search.addTextChangedListener(new TextWatcher() {
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
                workType_list1.add(jsonObject);
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

            case R.id.rlworktype:
                if (HomeDashBoard.text_date.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Select Date", Toast.LENGTH_SHORT).show();
                } else {
                    ShowWorkTypeAlert();
                }

                break;

            case R.id.rlcluster:

                if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    Toast.makeText(getActivity(), "Select Headquarters", Toast.LENGTH_SHORT).show();

                } else if (loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Select WorkType", Toast.LENGTH_SHORT).show();
                    } else {
                        showMultiClusterAlter();
                    }
                } else {
                    showMultiClusterAlter();
                }

                break;
            case R.id.btnsumit:


                if (binding.txtWorktype.getText().toString().startsWith("Field")) {

                    if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                        Toast.makeText(getActivity(), "Select Headquarters", Toast.LENGTH_SHORT).show();

                    } else if (binding.txtCluster.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Select Cluster", Toast.LENGTH_SHORT).show();
                    } else {
                        MyDayPlanSubmit();
                    }
                } else if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Select WorkType", Toast.LENGTH_SHORT).show();
                } else {
                    MyDayPlanSubmit();
                }
                break;
            case R.id.rlheadquates:
                if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Select WorkType", Toast.LENGTH_SHORT).show();
                } else {
                    showHQ();
                }
                break;

        }
    }


    public void MyDayPlanSubmit() {
        binding.progressSumit.setVisibility(View.VISIBLE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "dayplan");
            jsonObject.put("sfcode", loginResponse.getSF_Code());
            jsonObject.put("division_code", loginResponse.getDivision_Code());
            if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                jsonObject.put("Rsf", mHQCode);
            } else {
                jsonObject.put("Rsf", loginResponse.getSF_Code());
            }
            jsonObject.put("sf_type", loginResponse.getSf_type());
            jsonObject.put("Designation", loginResponse.getDesig());
            jsonObject.put("state_code", loginResponse.getState_Code());
            jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
            jsonObject.put("town_code", mTowncode);
            jsonObject.put("Town_name", mTownname);
            jsonObject.put("WT_code", mWTCode);
            jsonObject.put("WTName", mWTName);
            jsonObject.put("FwFlg", mFwFlg);
            jsonObject.put("Remarks", binding.txtdayremark.getText().toString());
            jsonObject.put("location", "");
            jsonObject.put("InsMode", "0");
            jsonObject.put("Appver", getResources().getString(R.string.app_version));
            jsonObject.put("Mod", "");
            jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_15, text_date.getText().toString()));
            jsonObject.put("TpVwFlg", "");
            jsonObject.put("TP_cluster", "");
            jsonObject.put("TP_worktype", "");

            Call<JsonObject> saveMyDayPlan = api_interface.saveMydayPlan(jsonObject.toString());
            saveMyDayPlan.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
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
                        binding.progressSumit.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    binding.progressSumit.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "MyDayPlan  failure", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (JSONException a) {
            throw new RuntimeException();
        }

    }


    public void getData(String hqCode) {
        binding.progressHq.setVisibility(View.VISIBLE);
        List<MasterSyncItemModel> list = new ArrayList<>();
        list.add(new MasterSyncItemModel("Doctor", 0, "Doctor", "getdoctors", Constants.DOCTOR + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", 0, "Doctor", "getchemist", Constants.CHEMIST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", 0, "Doctor", "getstockist", Constants.STOCKIEST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", 0, "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hqCode, 0, false));
        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Cluster", 0, "Doctor", "getterritory", Constants.CLUSTER + hqCode, 0, false));
        //   list.add(new MasterSyncItemModel("Subordinate", 0, "Subordinate", "getsubordinate", Constants.SUBORDINATE, 0, false));


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

                Call<JsonElement> call = api_interface.getDrMaster(jsonObject.toString());

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
                                                binding.progressHq.setVisibility(View.GONE);
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
            Toast.makeText(getActivity(), "No internet connectivity", Toast.LENGTH_SHORT).show();
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
                    mTowncode = jsonObject.getString("Pl");
                    mTownname = jsonObject.getString("PlNm");
                    mWTCode = jsonObject.getString("WT");
                    mWTName = jsonObject.getString("WTNm");
                    mFwFlg = jsonObject.getString("FWFlg");
                    mHQCode = jsonObject.getString("SFMem");
                    mHQName = jsonObject.getString("HQNm");
                    mRemarks = jsonObject.getString("Rem");
                    chk_cluster = jsonObject.getString("Pl");
                    Log.v("clusterNames", "---" + mTowncode);
                    if (!mFwFlg.equalsIgnoreCase("F")) {
                        binding.rlheadquates.setVisibility(View.GONE);
                        binding.rlcluster.setVisibility(View.GONE);
                        binding.txtWorktype.setText(mWTName);
                        binding.txtCluster.setText("");

                        binding.txtheadquaters.setText("");
                        binding.txtdayremark.setText(mRemarks);

                    } else {

                        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                            binding.rlheadquates.setVisibility(View.VISIBLE);
                        } else {
                            binding.rlheadquates.setVisibility(View.GONE);
                        }

                        binding.rlcluster.setVisibility(View.VISIBLE);


                        binding.txtWorktype.setText(mWTName);
                        binding.txtCluster.setText(mTownname);
                        binding.txtheadquaters.setText(mHQName);
                        binding.txtdayremark.setText(mRemarks);

                    }


                    String dateOnlyString = sdf.format(date1);
                    String selectedDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_27, dateOnlyString);
                    HomeDashBoard.text_date.setText(selectedDate);
                } else {
                    sqLite.saveMasterSyncData(Constants.MY_DAY_PLAN, "[]", 0);
                    binding.txtWorktype.setText("");
                    binding.txtCluster.setText("");
                    binding.txtheadquaters.setText("");
                    binding.txtdayremark.setText("");
                    HomeDashBoard.text_date.setText("");
                }

            } else {
                binding.txtWorktype.setText("");
                binding.txtCluster.setText("");
                binding.txtheadquaters.setText("");

            }

        } catch (Exception a) {
            throw new RuntimeException(a);
        }
    }


    public void syncMyDayPlan() {

        try {
            String baseUrl = SharedPref.getBaseWebUrl(requireContext());
            String pathUrl = SharedPref.getPhpPathUrl(requireContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
            api_interface = RetrofitClient.getRetrofit(getActivity(), baseUrl + replacedUrl);

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

            Call<JsonElement> call = api_interface.getDrMaster(jsonObject.toString());
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
}

