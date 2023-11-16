package saneforce.sanclm.activity.homeScreen.fragment.worktype;

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
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.WorkplanFragmentBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class WorkPlanFragment extends Fragment implements View.OnClickListener {

    WorkplanFragmentBinding binding;

    SQLite sqLite;
    ArrayList<JSONObject> worktype_list1 = new ArrayList<>();
    ArrayList<JSONObject> Headquteslist = new ArrayList<>();
    ArrayList<JSONObject> cluster = new ArrayList<>();
    JSONObject SelectedWorktype;

    JSONObject SelectedHeadQuates;
    JSONObject SelectedCluster;
    ApiInterface api_interface;
    String hdcode = "";

    LoginResponse loginResponse;

    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WorkplanFragmentBinding.inflate(inflater);
        View view = binding.getRoot();

        sqLite = new SQLite(getActivity());
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            binding.progressHq.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
        }
        api_interface = RetrofitClient.getRetrofit(getContext(), SharedPref.getCallApiUrl(getContext()));
        getLocaldata();
        binding.btnsumit.setOnClickListener(this);
        binding.rlworktype.setOnClickListener(this);
        binding.rlcluster.setOnClickListener(this);
        binding.rlheadquates.setOnClickListener(this);

        if (loginResponse.getDesig().equalsIgnoreCase("MGR")) {
            binding.rlheadquates.setVisibility(View.VISIBLE);
        } else {
            binding.rlheadquates.setVisibility(View.GONE);
        }


        return view;
    }


    public void ShowWorkTypeAlert () {
        HomeDashBoard.et_search.setText("");
        HomeDashBoard.txt_wt_plan.setText("Select WorkType");
        HomeDashBoard.cl_listview.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.hq_listview.setVisibility(View.GONE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);


        WorkplanListAdapter WT_ListAdapter = new WorkplanListAdapter(getActivity(), worktype_list1, "1");
        HomeDashBoard.wk_listview.setAdapter(WT_ListAdapter);

        HomeDashBoard.wk_listview.setOnItemClickListener((parent, view, position, id) -> {
            HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
            SelectedWorktype = WT_ListAdapter.getlisted().get(position);
            try {
                binding.txtWorktype.setText(SelectedWorktype.getString("Name"));
                if (SelectedWorktype.getString("FWFlg").equalsIgnoreCase("F")) {
                    binding.rlcluster.setVisibility(View.VISIBLE);
                    if (loginResponse.getDesig().equalsIgnoreCase("MGR")) {
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
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {

                String searchString = s.toString();
                WT_ListAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged (Editable s) {

            }
        });


    }


    public void showclusteralter () {

        HomeDashBoard.et_search.setText("");

        HomeDashBoard.cl_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.wk_listview.setVisibility(View.GONE);
        HomeDashBoard.hq_listview.setVisibility(View.GONE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter CL_ListAdapter = new WorkplanListAdapter(getActivity(), cluster, "2");
        HomeDashBoard.cl_listview.setAdapter(CL_ListAdapter);


        HomeDashBoard.txt_wt_plan.setText("Select Cluster");
        HomeDashBoard.cl_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
                SelectedCluster = CL_ListAdapter.getlisted().get(position);
                try {
                    binding.txtCluster.setText(SelectedCluster.getString("Name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        HomeDashBoard.et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {


                String searchString = s.toString();
                CL_ListAdapter.getFilter().filter(searchString);


            }

            @Override
            public void afterTextChanged (Editable s) {

            }
        });

    }


    public void showheadquates () {
        HomeDashBoard.et_search.setText("");
        HomeDashBoard.txt_wt_plan.setText("Select Headquates");
        HomeDashBoard.cl_listview.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.GONE);
        HomeDashBoard.hq_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter HQ_ListAdapter = new WorkplanListAdapter(getActivity(), Headquteslist, "3");
        HomeDashBoard.hq_listview.setAdapter(HQ_ListAdapter);

        HomeDashBoard.hq_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                SelectedHeadQuates = HQ_ListAdapter.getlisted().get(position);
                HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
                try {
                    binding.txtCluster.setText("");
                    binding.txtheadquaters.setText(SelectedHeadQuates.getString("name"));
                    hdcode = SelectedHeadQuates.getString("id");

                    getdata(SelectedHeadQuates.getString("id"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        HomeDashBoard.et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {

                String searchString = s.toString();
                HQ_ListAdapter.getFilter().filter(searchString);

            }

            @Override
            public void afterTextChanged (Editable s) {

            }
        });
    }


    void getLocaldata () {
        worktype_list1.clear();
        cluster.clear();

        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i = 0; i<workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                worktype_list1.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + SharedPref.getHqCode(requireContext()));
            for (int i = 0; i<workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                cluster.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
            for (int i = 0; i<workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                Headquteslist.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    @Override
    public void onClick (View v) {

        switch (v.getId()){

            case R.id.rlworktype:
                ShowWorkTypeAlert();
                break;

            case R.id.rlcluster:

                if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("") && loginResponse.getDesig().equalsIgnoreCase("MGR")) {
                    Toast.makeText(getActivity(), "Select Headquates", Toast.LENGTH_SHORT).show();

                } else if (!loginResponse.getDesig().equalsIgnoreCase("MGR")) {
                    if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Select Worktype", Toast.LENGTH_SHORT).show();
                    } else {
                        showclusteralter();
                    }
                } else {
                    showclusteralter();
                }

                break;
            case R.id.btnsumit:


                if (binding.txtWorktype.getText().toString().startsWith("Field")) {

                    if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("") && loginResponse.getDesig().equalsIgnoreCase("MGR")) {
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


    public void MydayplanSumit () {
        try {
            String towncode = "", townname = "", WT_code = "", WTName = "", FwFlg = "";

            if (SelectedCluster.length()>0) {
                towncode = SelectedCluster.getString("Code");
                townname = SelectedCluster.getString("Name");
            }

            if (SelectedWorktype.length()>0) {
                WT_code = SelectedWorktype.getString("Code");
                WTName = SelectedWorktype.getString("Name");
                FwFlg = SelectedWorktype.getString("FWFlg");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date currentDate = new Date();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "dayplan");
            jsonObject.put("sfcode", loginResponse.getSF_Code());
            jsonObject.put("division_code", loginResponse.getDivision_Code());
            if (loginResponse.getDesig().equalsIgnoreCase("MGR")) {
                jsonObject.put("Rsf", hdcode);
            } else {
                jsonObject.put("Rsf", loginResponse.getSF_Code());
            }
            jsonObject.put("sf_type", loginResponse.getSf_type());
            jsonObject.put("Designation", loginResponse.getDesig());
            jsonObject.put("state_code", loginResponse.getState_Code());
            jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
            jsonObject.put("town_code", towncode);
            jsonObject.put("Town_name", townname);
            jsonObject.put("WT_code", WT_code);
            jsonObject.put("WTName", WTName);
            jsonObject.put("FwFlg", FwFlg);
            jsonObject.put("Remarks", binding.txtdayremark.getText().toString());
            jsonObject.put("location", "");
            jsonObject.put("InsMode", "0");
            jsonObject.put("Appver", R.string.app_version);
            jsonObject.put("Mod", "");
            jsonObject.put("TPDt", sdf.format(currentDate));
            jsonObject.put("TpVwFlg", "");
            jsonObject.put("TP_cluster", "");
            jsonObject.put("TP_worktype", "");

            Log.d("todaycallList:Code", jsonObject.toString());

            Call<JsonObject> saveMydayPlan = api_interface.saveMydayPlan(jsonObject.toString());
            saveMydayPlan.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse (Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("todaycallList:Code", response.code() + " - " + response);
                    if (response.isSuccessful()) {

                        try {
                            JSONObject json = new JSONObject(response.body().toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), "Mydayplan  Successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }
                    }
                }

                @Override
                public void onFailure (Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getActivity(), "Mydayplan  failure", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (JSONException a) {
            throw new RuntimeException();
        }

    }


    public void getdata (String hdcode) {
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

        for (int i = 0; i<list.size(); i++) {
            syncmaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hdcode);
        }
    }

    public void syncmaster (String masterFor, String remoteTableName, String LocalTableKeyName, String hdcode) {
        if (UtilityClass.isNetworkAvailable(getActivity())) {
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
                jsonObject.put("state_code", loginResponse.getState_Code());
                jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());


                Call<JsonElement> call = api_interface.getDrMaster(jsonObject.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse (@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
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
                        public void onFailure (@NonNull Call<JsonElement> call, @NonNull Throwable t) {


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


    private void getdatabasedHeadQuates (String hdcode) {

        cluster.clear();
        try {
            JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hdcode);
            for (int i = 0; i<workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                cluster.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }
}