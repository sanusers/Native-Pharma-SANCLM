package saneforce.sanclm.activity.homeScreen.fragment.worktype;



import static saneforce.sanclm.activity.homeScreen.HomeDashBoard.text_date;
import static saneforce.sanclm.activity.homeScreen.HomeDashBoard.txt_cl_done;

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
import android.widget.AdapterView;
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

    WorkplanFragmentBinding binding;

    SQLite sqLite;
    ArrayList<JSONObject> worktype_list1 = new ArrayList<>();
    ArrayList<Multicheckclass_clust> multiple_clusterlist = new ArrayList<>();
    ArrayList<JSONObject> Headquteslist = new ArrayList<>();
    ArrayList<JSONObject> cluster = new ArrayList<>();
    public  static String chk_cluster ="";
    public static ArrayList<Multicheckclass_clust> listSelectedclust = new ArrayList<>();


    JSONObject SelectedWorktype;

    JSONObject SelectedHeadQuates;
    JSONObject SelectedCluster;
    ApiInterface api_interface;

    LoginResponse loginResponse;

    String strClustID = "", strClustName = "";

    public  static String mTowncode="",mTownname="",mWTCode="",mWTname="",mFwFlg="",mHQcode = "",mHQname = "",mremarks="";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WorkplanFragmentBinding.inflate(inflater);
        View view = binding.getRoot();

        sqLite = new SQLite(getActivity());
        loginResponse= new LoginResponse();
        loginResponse= sqLite.getLoginData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.progressHq.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            binding.progressSumit.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
        }
        api_interface = RetrofitClient.getRetrofit(getContext(), SharedPref.getCallApiUrl(getContext()));

        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
            binding.rlheadquates.setVisibility(View.VISIBLE);
        } else {
            binding.rlheadquates.setVisibility(View.GONE);
        }
        setmydaypalndata();
        getLocaldata();

        binding.btnsumit.setOnClickListener(this);
        binding.rlworktype.setOnClickListener(this);
        binding.rlcluster.setOnClickListener(this);
        binding.rlheadquates.setOnClickListener(this);




        return view;
    }


    public void ShowWorkTypeAlert() {

        HomeDashBoard.et_search.setText("");
        HomeDashBoard.txt_wt_plan.setText("Select WorkType");
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        HomeDashBoard.wk_recyclerr_view.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
        WorkplanListAdapter WT_ListAdapter = new WorkplanListAdapter(getActivity(), worktype_list1, "1");
        HomeDashBoard.wk_listview.setAdapter(WT_ListAdapter);
        HomeDashBoard.wk_listview.setOnItemClickListener((parent, view, position, id) -> {
            HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
            SelectedWorktype = WT_ListAdapter.getlisted().get(position);
            try {
                binding.txtWorktype.setText(SelectedWorktype.getString("Name"));

                mFwFlg=SelectedWorktype.getString("FWFlg");
                mWTCode = SelectedWorktype.getString("Code");
                mWTname = SelectedWorktype.getString("Name");

                if (SelectedWorktype.getString("FWFlg").equalsIgnoreCase("F")) {
                    binding.rlcluster.setVisibility(View.VISIBLE);
                    if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                        binding.rlheadquates.setVisibility(View.VISIBLE);
                    }

                } else {
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


    public void showclusteralter() {

        HomeDashBoard.et_search.setText("");
        HomeDashBoard.wk_recyclerr_view.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter CL_ListAdapter = new WorkplanListAdapter(getActivity(), cluster, "2");
        HomeDashBoard.wk_listview.setAdapter(CL_ListAdapter);


        HomeDashBoard.txt_wt_plan.setText("Select Cluster");
        HomeDashBoard.wk_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
                SelectedCluster = CL_ListAdapter.getlisted().get(position);
                try {
                    binding.txtCluster.setText(SelectedCluster.getString("Name"));

                    mTowncode=SelectedCluster.getString("Code");
                    mTownname=SelectedCluster.getString("Name");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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




    public void showmulticlusteralter() {

        HomeDashBoard.et_search.setText("");
        txt_cl_done.setVisibility(View.VISIBLE);
        HomeDashBoard.wk_recyclerr_view.setVisibility(View.VISIBLE);
        HomeDashBoard.wk_listview.setVisibility(View.GONE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        HomeDashBoard.txt_wt_plan.setText("Select Cluster");

        MultiClusterAdapter multiClusterAdapter=new MultiClusterAdapter(getActivity(), multiple_clusterlist, new OnClusterClicklistener() {
            @Override
            public void classCampaignItem_addClass(Multicheckclass_clust classGroup) {
                listSelectedclust.add(classGroup);

            }
            @Override
            public void classCampaignItem_removeClass(Multicheckclass_clust classGroup) {
                listSelectedclust.add(classGroup);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        HomeDashBoard.wk_recyclerr_view.setLayoutManager(linearLayoutManager);
        HomeDashBoard.wk_recyclerr_view.setAdapter(multiClusterAdapter);

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



        txt_cl_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);

                if (listSelectedclust.size() > 0) {
                    String selectedUsers = "", selctectedId = "";
                    strClustName = "";
                    strClustID = "";
                    for (Multicheckclass_clust multicheckclassClust : multiple_clusterlist) {
                        if (multicheckclassClust.isChecked()) {
                            selectedUsers = selectedUsers + multicheckclassClust.getStrname() + ",";
                            selctectedId = selctectedId + multicheckclassClust.getStrid() + ",";
                            strClustID = selctectedId;
                            strClustName = selectedUsers;
                            binding.txtCluster.setText(strClustName);
                        }
                    }

                    mTowncode=strClustID;
                    mTownname=strClustName;
                }
            }
        });
    }
    public void showheadquates() {
        txt_cl_done.setVisibility(View.GONE);
        HomeDashBoard.wk_recyclerr_view.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.et_search.setText("");
        HomeDashBoard.txt_wt_plan.setText("Select Headquates");
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter HQ_ListAdapter = new WorkplanListAdapter(getActivity(), Headquteslist, "3");
        HomeDashBoard.wk_listview.setAdapter(HQ_ListAdapter);

        HomeDashBoard.wk_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedHeadQuates = HQ_ListAdapter.getlisted().get(position);
                HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);

                try {
                    binding.txtCluster.setText("");
                    binding.txtheadquaters.setText(SelectedHeadQuates.getString("name"));
                    mHQcode = SelectedHeadQuates.getString("id");
                    mHQname = SelectedHeadQuates.getString("name");
                    if(UtilityClass.isNetworkAvailable(getContext())){
                        getdata(SelectedHeadQuates.getString("id"));
                    }else{
                        Toast.makeText(getActivity(), "No internet connectivity", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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


    void getLocaldata() {
        worktype_list1.clear();
        cluster.clear();
        multiple_clusterlist.clear();

        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                worktype_list1.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + SharedPref.getHqCode(requireContext()));
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);


                if ((("," + chk_cluster + ",").indexOf("," + jsonObject.getString("Code") + ",")) > -1) {
                    multiple_clusterlist.add(new Multicheckclass_clust(jsonObject.getString("Code"),jsonObject.getString("Name"),"",true));
                }else {
                    multiple_clusterlist.add(new Multicheckclass_clust(jsonObject.getString("Code"),jsonObject.getString("Name"),"",false));

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
                Headquteslist.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlworktype:
                if(HomeDashBoard.text_date.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), "Select Date", Toast.LENGTH_SHORT).show();
                }else {
                    ShowWorkTypeAlert();
                }

                break;

            case R.id.rlcluster:

                if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    Toast.makeText(getActivity(), "Select Headquates", Toast.LENGTH_SHORT).show();

                } else if (loginResponse.getDesig().equalsIgnoreCase("MR")) {
                    if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Select Worktype", Toast.LENGTH_SHORT).show();
                    } else {
                        showmulticlusteralter();
                    }
                } else {
                    showmulticlusteralter();
                }

                break;
            case R.id.btnsumit:


                if (binding.txtWorktype.getText().toString().startsWith("Field")) {

                    if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("") && !loginResponse.getDesig().equalsIgnoreCase("MR")) {
                        Toast.makeText(getActivity(), "Select Headquates", Toast.LENGTH_SHORT).show();

                    } else if (binding.txtCluster.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Select Cluster", Toast.LENGTH_SHORT).show();
                    } else {
                        MydayplanSumit();
                    }
                } else if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Select Worktype", Toast.LENGTH_SHORT).show();
                } else {
                    MydayplanSumit();
                }
                break;
            case R.id.rlheadquates:
                if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Select Worktype", Toast.LENGTH_SHORT).show();
                } else {
                    showheadquates();
                }
                break;

        }
    }


    public void MydayplanSumit() {
        binding.progressSumit.setVisibility(View.VISIBLE);
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date currentDate = new Date();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "dayplan");
            jsonObject.put("sfcode", loginResponse.getSF_Code());
            jsonObject.put("division_code",loginResponse.getDivision_Code());
            if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                jsonObject.put("Rsf", mHQcode);
            } else {
                jsonObject.put("Rsf", loginResponse.getSF_Code());
            }
            jsonObject.put("sf_type",loginResponse.getSf_type());
            jsonObject.put("Designation", loginResponse.getDesig());
            jsonObject.put("state_code", loginResponse.getState_Code());
            jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
            jsonObject.put("town_code", mTowncode);
            jsonObject.put("Town_name", mTownname);
            jsonObject.put("WT_code", mWTCode);
            jsonObject.put("WTName", mWTname);
            jsonObject.put("FwFlg", mFwFlg);
            jsonObject.put("Remarks", binding.txtdayremark.getText().toString());
            jsonObject.put("location", "");
            jsonObject.put("InsMode", "0");
            jsonObject.put("Appver", getResources().getString(R.string.app_version));
            jsonObject.put("Mod", "");
            jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27,TimeUtils.FORMAT_15,text_date.getText().toString()));
            jsonObject.put("TpVwFlg", "");
            jsonObject.put("TP_cluster", "");
            jsonObject.put("TP_worktype", "");


            Call<JsonObject> saveMydayPlan = api_interface.saveMydayPlan(jsonObject.toString());
            saveMydayPlan.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("todaycallList:Code", response.code() + " - " + response);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(response.body().toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), json.getString("Msg"), Toast.LENGTH_SHORT).show();
                                if (!loginResponse.getDesig().equalsIgnoreCase("MR"))
                                    SharedPref.saveHq(getContext(),mHQname,mHQcode);
                                syncmydayplan();
                            }else{
                                Toast.makeText(getActivity(), json.getString("Msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }
                        binding.progressSumit.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    binding.progressSumit.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Mydayplan  failure", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (JSONException a) {
            throw new RuntimeException();
        }

    }


    public void getdata(String hdcode) {
        binding.progressHq.setVisibility(View.VISIBLE);
        List<MasterSyncItemModel> list = new ArrayList<>();
        list.clear();
        list.add(new MasterSyncItemModel("Doctor", 0, "Doctor", "getdoctors", Constants.DOCTOR + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", 0, "Doctor", "getchemist", Constants.CHEMIST + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", 0, "Doctor", "getstockist", Constants.STOCKIEST + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", 0, "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hdcode, 0, false));
        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Cluster", 0, "Doctor", "getterritory", Constants.CLUSTER + hdcode, 0, false));
        //   list.add(new MasterSyncItemModel("Subordinate", 0, "Subordinate", "getsubordinate", Constants.SUBORDINATE, 0, false));


        for (int i = 0; i < list.size(); i++) {
            syncmaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hdcode);
        }
    }

    public void syncmaster(String masterFor, String remoteTableName, String LocalTableKeyName, String hdcode) {

        try {
                String baseUrl = SharedPref.getBaseWebUrl(getActivity());
                String pathUrl = SharedPref.getPhpPathUrl(getActivity());
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                api_interface = RetrofitClient.getRetrofit(getActivity(), baseUrl + replacedUrl);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", loginResponse.getSF_Code());
                jsonObject.put("division_code", loginResponse.getDivision_Code());
                jsonObject.put("Rsf", hdcode);
                jsonObject.put("sf_type", loginResponse.getSf_type());
                jsonObject.put("Designation", loginResponse.getDesig());
                jsonObject.put("state_code",  loginResponse.getState_Code());
                jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());

                Call<JsonElement> call = api_interface.getDrMaster(jsonObject.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();

                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + masterFor + " -- " + remoteTableName + " : " + response.body().toString());
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
                                                getdatabasedHeadQuates(hdcode);
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

    }


    private void getdatabasedHeadQuates(String hdcode) {

        cluster.clear();
        multiple_clusterlist.clear();
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hdcode);
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                multiple_clusterlist.add(new Multicheckclass_clust(jsonObject.getString("Code"),jsonObject.getString("Name"),"",false));

                cluster.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }



    public void setmydaypalndata(){

        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.MY_DAY_PLAN);

            if(workTypeArray.length()>0){

                JSONObject jsonObject = workTypeArray.getJSONObject(0);

                String TPDt = jsonObject.getString("TPDt");
                JSONObject jsonObject1 = new JSONObject(TPDt);
                String daypaln_Date = jsonObject1.getString("date");
                String currnetdate = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_15);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date1 = sdf.parse(daypaln_Date);
                Date date2 = sdf.parse(currnetdate);
                if (date1.equals(date2)) {
                    mTowncode = jsonObject.getString("Pl");
                    mTownname = jsonObject.getString("PlNm");
                    mWTCode = jsonObject.getString("WT");
                    mWTname = jsonObject.getString("WTNm");
                    mFwFlg = jsonObject.getString("FWFlg");
                    mHQcode = jsonObject.getString("SFMem");
                    mHQname = jsonObject.getString("HQNm");
                    mremarks = jsonObject.getString("Rem");
                    chk_cluster= jsonObject.getString("Pl");
                    if(!mFwFlg.equalsIgnoreCase("F")){
                        binding.rlheadquates.setVisibility(View.GONE);
                        binding.rlcluster.setVisibility(View.GONE);
                        binding.txtWorktype.setText(mWTname);
                        binding.txtCluster.setText("");
                        binding.txtheadquaters.setText("");
                        binding.txtdayremark.setText(mremarks);
                    }else {
                        if (!loginResponse.getDesig().equalsIgnoreCase("MR")) {
                            binding.rlheadquates.setVisibility(View.VISIBLE);
                        } else {
                            binding.rlheadquates.setVisibility(View.GONE);
                        }
                        binding.rlcluster.setVisibility(View.VISIBLE);
                        binding.txtWorktype.setText(mWTname);
                        binding.txtCluster.setText(mTownname);
                        binding.txtheadquaters.setText(mHQname);
                        binding.txtdayremark.setText(mremarks);

                    }

                    String dateOnlyString = sdf.format(date1);
                    String selecteddate=TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4,TimeUtils.FORMAT_27,dateOnlyString);
                    HomeDashBoard.text_date.setText(selecteddate);
                }else {
                    sqLite.saveMasterSyncData(Constants.MY_DAY_PLAN,"[]",0);
                    binding.txtWorktype.setText("");
                    binding.txtCluster.setText("");
                    binding.txtheadquaters.setText("");
                    binding.txtdayremark.setText("");
                    HomeDashBoard.text_date.setText("");
                }

            }else {
                binding.txtWorktype.setText("");
                binding.txtCluster.setText("");
                binding.txtheadquaters.setText("");

            }

        } catch (Exception a) {
            throw new RuntimeException(a);
        }
    }


    public void syncmydayplan(){

        try {
            String baseUrl = SharedPref.getBaseWebUrl(getActivity());
            String pathUrl = SharedPref.getPhpPathUrl(getActivity());
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
            api_interface = RetrofitClient.getRetrofit(getActivity(), baseUrl + replacedUrl);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "gettodaytpnew");
            jsonObject.put("sfcode", loginResponse.getSF_Code());
            jsonObject.put("division_code", loginResponse.getDivision_Code());
            jsonObject.put("Rsf", SharedPref.getHqCode(getContext()));
            jsonObject.put("sf_type", loginResponse.getSf_type());
            jsonObject.put("Designation", loginResponse.getDesig());
            jsonObject.put("state_code", loginResponse.getState_Code());
            jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
            jsonObject.put("ReqDt",TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));

            Call<JsonElement> call = api_interface.getDrMaster(jsonObject.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    boolean success = false;
                    JSONArray jsonArray = new JSONArray();

                    if (response.isSuccessful()) {
                        Log.e("test", "response : " + response.body().toString());
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
                public void onFailure(Call<JsonElement> call, Throwable t) {

                }
            });

        }catch (JSONException a){
            a.printStackTrace();
        }
    }
}
