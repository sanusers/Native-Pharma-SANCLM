package saneforce.sanclm.activity.homeScreen.fragment.worktype;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.WorkplanFragmentBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class WorkPlanFragment extends Fragment implements View.OnClickListener {

    WorkplanFragmentBinding binding;

    SQLite sqLite;
    ArrayList<JSONObject>  worktype_list1=new ArrayList<>();
    ArrayList<JSONObject>  Headquteslist=new ArrayList<>();
    ArrayList<JSONObject>  cluster=new ArrayList<>();
    JSONObject SelectedWorktype;

    JSONObject SelectedHeadQuates;
    JSONObject SelectedCluster;
    ApiInterface api_interface;
    String hdcode="";


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WorkplanFragmentBinding.inflate(inflater);
        View view = binding.getRoot();

         api_interface = RetrofitClient.getRetrofit(getContext(), SharedPref.getCallApiUrl(getContext()));
         getLocaldata();
         binding.btnsumit.setOnClickListener(this);
         binding.rlworktype.setOnClickListener(this);
         binding.rlcluster.setOnClickListener(this);
         binding.rlheadquates.setOnClickListener(this);

         if(SharedPref.getDesignationName(getContext()).equalsIgnoreCase("MGR")){
             binding.rlheadquates.setVisibility(View.VISIBLE);
         }else {
             binding.rlheadquates.setVisibility(View.GONE);
         }

        return view;
    }


    public void ShowWorkTypeAlert() {

        HomeDashBoard.cl_listview.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.hq_listview.setVisibility(View.GONE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);

        WorktypeAdapter  worktypeAdapter = new WorktypeAdapter(getActivity(),worktype_list1,"1");
        HomeDashBoard.wk_listview.setAdapter(worktypeAdapter);


        HomeDashBoard.wk_listview.setOnItemClickListener((parent, view, position, id) -> {
            HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
               SelectedWorktype = worktype_list1.get(position);
            try {
                binding.txtWorktype.setText(SelectedWorktype.getString("Name"));
                if(SelectedWorktype.getString("FWFlg").equalsIgnoreCase("F")){
                    binding.rlcluster.setVisibility(View.VISIBLE);
                    if(SharedPref.getDesignationName(getContext()).equalsIgnoreCase("MGR")){
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

     }


    public void showclusteralter(){

        HomeDashBoard.cl_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.wk_listview.setVisibility(View.GONE);
        HomeDashBoard.hq_listview.setVisibility(View.GONE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        WorktypeAdapter  worktypeAdapter = new WorktypeAdapter(getActivity(),cluster,"1");
        HomeDashBoard.cl_listview.setAdapter(worktypeAdapter);;

        HomeDashBoard.cl_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
              SelectedCluster = cluster.get(position);
                try {
                    binding.txtCluster.setText(SelectedCluster.getString("Name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void showheadquates(){
        HomeDashBoard.cl_listview.setVisibility(View.GONE);
        HomeDashBoard.wk_listview.setVisibility(View.GONE);
        HomeDashBoard.hq_listview.setVisibility(View.VISIBLE);
        HomeDashBoard.drawerLayout.openDrawer(GravityCompat.END);
        WorktypeAdapter  worktypeAdapter = new WorktypeAdapter(getActivity(),Headquteslist,"3");
        HomeDashBoard.hq_listview.setAdapter(worktypeAdapter);;

        HomeDashBoard.hq_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedHeadQuates = Headquteslist.get(position);
                HomeDashBoard.drawerLayout.closeDrawer(GravityCompat.END);
                try {
                    binding.txtheadquaters.setText(SelectedHeadQuates.getString("name"));
                    hdcode=SelectedHeadQuates.getString("id");
                    getdata(SelectedHeadQuates.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    void getLocaldata(){
        worktype_list1.clear();
        cluster.clear();
        sqLite=new SQLite(getActivity());
        try {JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            for (int i=0;i<workTypeArray.length();i++){
            JSONObject jsonObject = workTypeArray.getJSONObject(i);
             worktype_list1.add(jsonObject);
        }}catch (Exception a){
            a.printStackTrace();
        }
        try {JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER+ SharedPref.getHqCode(requireContext()));
            for (int i=0;i<workTypeArray.length();i++){
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                cluster.add(jsonObject);
            }}catch (Exception a){
            a.printStackTrace();
        }
        try {JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
            for (int i=0;i<workTypeArray.length();i++){
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                Headquteslist.add(jsonObject);
            }}catch (Exception a){
            a.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlworktype:
                ShowWorkTypeAlert();
                break;

            case R.id.rlcluster:

                if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("")&& SharedPref.getDesignationName(getActivity()).equalsIgnoreCase("MGR")) {
                    Toast.makeText(getActivity(), "Select Headquates", Toast.LENGTH_SHORT).show();

                } else if(!SharedPref.getDesignationName(getActivity()).equalsIgnoreCase("MGR")){
                    if (binding.txtWorktype.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Select Worktype", Toast.LENGTH_SHORT).show();
                    } else {
                        showclusteralter();
                    }
                }else {
                    showclusteralter();
                }

                break;
            case R.id.btnsumit:



                if (binding.txtWorktype.getText().toString().startsWith("Field")) {

                    if (binding.txtheadquaters.getText().toString().equalsIgnoreCase("") && SharedPref.getDesignationName(getActivity()).equalsIgnoreCase("MGR")) {
                        Toast.makeText(getActivity(), "Select Headquates", Toast.LENGTH_SHORT).show();

                    } else if (binding.txtCluster.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Select Cluster", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Sucess", Toast.LENGTH_SHORT).show();
                        MydayplanSumit();
                    }
                } else if(binding.txtWorktype.getText().toString().equalsIgnoreCase("")){

                    Toast.makeText(getActivity(), "Select Worktype", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(getActivity(), "Sucess", Toast.LENGTH_SHORT).show();
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

    }}


   public void MydayplanSumit(){
       try {
           String towncode="",townname="",WT_code="",WTName="",FwFlg="";

         if( SelectedCluster.length()>0){
              towncode =SelectedCluster.getString("Code");
              townname =SelectedCluster.getString("Name");
         }

           if( SelectedWorktype.length()>0){
               WT_code =SelectedWorktype.getString("Code");
               WTName =SelectedWorktype.getString("Name");
               FwFlg=  SelectedWorktype.getString("FWFlg");
           }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date currentDate = new Date();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "dayplan");
            jsonObject.put("sfcode", SharedPref.getSfCode(getActivity()));
            jsonObject.put("division_code", SharedPref.getDivisionCode(getActivity()));
            if(SharedPref.getDesignationName(getActivity()).equalsIgnoreCase("MGR")){
                jsonObject.put("Rsf", hdcode);
            }else {
                jsonObject.put("Rsf", SharedPref.getSfCode(getActivity()));
            }
            jsonObject.put("Rsf", SharedPref.getHqCode(getActivity()));
            jsonObject.put("sf_type", SharedPref.getSfType(getActivity()));
            jsonObject.put("Designation", SharedPref.getDesignationName(getActivity()));
            jsonObject.put("state_code", SharedPref.getStateCode(getActivity()));
            jsonObject.put("subdivision_code", SharedPref.getSubdivCode(getActivity()));
            jsonObject.put("town_code", towncode);
            jsonObject.put("Town_name", townname);
            jsonObject.put("WT_code",WT_code);
            jsonObject.put("WTName", WTName);
            jsonObject.put("FwFlg", FwFlg);
            jsonObject.put("Remarks","");
            jsonObject.put("location", "");
            jsonObject.put("InsMode", "0");
            jsonObject.put("Appver",  R.string.app_version);
            jsonObject.put("Mod", "");
            jsonObject.put("TPDt", sdf.format(currentDate));
            jsonObject.put("TpVwFlg", "");
            jsonObject.put("TP_cluster","");
            jsonObject.put("TP_worktype", "");

            Log.d("todaycallList:Code", jsonObject.toString());

            Call<JsonObject> saveMydayPlan = api_interface.saveMydayPlan(jsonObject.toString());
            saveMydayPlan.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("todaycallList:Code", response.code() + " - " + response);
                    if (response.isSuccessful()) {

                        try {
                            JSONObject json = new JSONObject(response.body().toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                 Toast.makeText(getActivity(), "Mydayplan  Successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }
                }}

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getActivity(), "Mydayplan  failure", Toast.LENGTH_SHORT).show();
                }
            });



        }catch (JSONException a){
           throw new RuntimeException();
        }

    }


    public  void getdata(String hdcode){


        List<MasterSyncItemModel> list=new ArrayList<>();
        list.clear();
        list.add(new MasterSyncItemModel("Doctor", 0, "Doctor", "getdoctors", Constants.DOCTOR +hdcode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", 0, "Doctor", "getchemist", Constants.CHEMIST + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", 0, "Doctor", "getstockist", Constants.STOCKIEST + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", 0, "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hdcode, 0, false));
        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hdcode, 0, false));
        list.add(new MasterSyncItemModel("Cluster", 0, "Doctor", "getterritory", Constants.CLUSTER + hdcode, 0, false));
     //   list.add(new MasterSyncItemModel("Subordinate", 0, "Subordinate", "getsubordinate", Constants.SUBORDINATE, 0, false));


        for (int i = 0; i < list.size(); i++) {
            syncmaster(list.get(i).getMasterFor(),list.get(i).getRemoteTableName(),list.get(i). getLocalTableKeyName(),hdcode);
        }
    }

    public void syncmaster(String masterFor, String remoteTableName,String LocalTableKeyName,String hdcode){
         if (UtilityClass.isNetworkAvailable(getActivity())){
            try {
                String baseUrl = SharedPref.getBaseWebUrl(getActivity());
                String pathUrl = SharedPref.getPhpPathUrl(getActivity());
                String replacedUrl = pathUrl.replaceAll("\\?.*","/");
                 api_interface = RetrofitClient.getRetrofit(getActivity(), baseUrl+replacedUrl);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", SharedPref.getSfCode(getActivity()));
                jsonObject.put("division_code", SharedPref.getDivisionCode(getActivity()));
                jsonObject.put("Rsf", hdcode);
                jsonObject.put("sf_type", SharedPref.getSfType(getActivity()));
                jsonObject.put("Designation", SharedPref.getDesignationName(getActivity()));
                jsonObject.put("state_code", SharedPref.getStateCode(getActivity()));
                jsonObject.put("subdivision_code",SharedPref.getSubdivCode(getActivity()));


                Call<JsonElement> call = api_interface.getDrMaster(jsonObject.toString());

                if (call != null){
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
                                            sqLite.saveMasterSyncData(LocalTableKeyName, jsonArray.toString(),0);

                                            if(LocalTableKeyName.startsWith(Constants.CLUSTER)){
                                                getdatabasedHeadQuates(hdcode);
                                            }
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }}
                        @Override
                        public void onFailure (@NonNull Call<JsonElement> call, @NonNull Throwable t) {


                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(), "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }


     private  void getdatabasedHeadQuates(String hdcode){

         cluster.clear();
         try {JSONArray workTypeArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER+ hdcode);
             for (int i=0;i<workTypeArray.length();i++){
                 JSONObject jsonObject = workTypeArray.getJSONObject(i);
                 cluster.add(jsonObject);
             }}catch (Exception a){
             a.printStackTrace();
         }
    }
}