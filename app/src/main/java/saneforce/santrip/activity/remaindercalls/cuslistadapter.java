package saneforce.santrip.activity.remaindercalls;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.activity.myresource.Resource_profiling;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.roomdatabase.DCRDocDataTableDetails.DCRDocDataDao;
import saneforce.santrip.roomdatabase.DCRDocDataTableDetails.DCRDocDataTable;
import saneforce.santrip.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class cuslistadapter extends RecyclerView.Adapter<cuslistadapter.ViewHolder> {
    Context context;
    ApiInterface api_interface;
    LoginResponse loginResponse;
//    SQLite sqLite;
    String SfType = "", Vals = "";
    String pos;


    ProgressDialog progressDialog = null;
    ArrayList<remainder_modelclass> JwList;

    ArrayList<remainder_modelclass> listeduser1;
    private RoomDB roomDB;
    private LoginDataDao loginDataDao;
    private DCRDocDataDao dcrDocDataDao;

    public cuslistadapter(Context context, ArrayList<remainder_modelclass> listeduser1, String pos) {
        this.context = context;
        this.listeduser1 = listeduser1;
        this.pos = pos;

//        sqLite = new SQLite(context);
        roomDB = RoomDB.getDatabase(context);
        loginDataDao = roomDB.loginDataDao();
        dcrDocDataDao = roomDB.dcrDocDataDao();
        loginResponse = loginDataDao.getLoginData().getLoginResponse();
//        loginResponse = sqLite.getLoginData();
        SfType = loginResponse.getSf_type();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        remainder_modelclass app_adapt = listeduser1.get(position);

        holder.itemTitle.setText(app_adapt.getDoc_name());

        holder.itemTitle.setOnClickListener(view -> {

            Log.d("itemTitle", app_adapt.getDoc_name());
            if (!Resource_profiling.profil_val.equals("")) {

                Resource_profiling.drawer_Layout12.closeDrawer(GravityCompat.END);

                if (pos.equals("Q")) {
                    Resource_profiling.Qual_code = app_adapt.getDoc_code();
                    Resource_profiling.Qualification.setText(app_adapt.getDoc_name());
                } else if (pos.equals("S")) {
                    Resource_profiling.spec_code = app_adapt.getDoc_code();
                    Resource_profiling.Speciality.setText(app_adapt.getDoc_name());
                } else if (pos.equals("C")) {
                    Resource_profiling.cate_code = app_adapt.getDoc_code();
                    Resource_profiling.Category.setText(app_adapt.getDoc_name());
                }


            } else {


                RemaindercallsActivity.townname.setText(app_adapt.getDoc_name());
                RemaindercallsActivity.REm_hq_code = app_adapt.getDoc_code();
                String hqcode = SharedPref.getDcrdoc_hqcode(context);
                Log.d("hqlist_data", hqcode);
                RemaindercallsActivity.slt_hq.add(hqcode);
                if (RemaindercallsActivity.slt_hq.size() != 0) {
                    for (int v = 0; v < RemaindercallsActivity.slt_hq.size(); v++) {
                        if (RemaindercallsActivity.slt_hq.get(v).equals("Doctor_" + app_adapt.getDoc_code())) {
                            show_hq("Doctor_" + app_adapt.getDoc_code());
                            Vals = "A";
                        }
                    }
                }
                if (Vals.equals("")) {
                    getData(app_adapt.getDoc_code());


                }


                RemaindercallsActivity.drawer_Layout12.closeDrawer(GravityCompat.END);

            }
        });


    }


    @Override
    public int getItemCount() {
        return listeduser1.size();
    }

    public void filter_hqList(ArrayList<remainder_modelclass> filterdHqname) {
        this.listeduser1 = filterdHqname;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox ch_cluster;
        TextView itemTitle;
        ImageView close_sideview;
        LinearLayout linearlist;

        //        //ch_cluster,itemTitle
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = (itemView).findViewById(R.id.itemTitle);

        }
    }

    public void show_hq(String hqcode) {
        try {
            RemaindercallsActivity.listeduser.clear();
            String Town_Name = "";
//            JSONArray jsonvst_Doc = sqLite.getDcr_datas(hqcode);
            JSONArray jsonvst_Doc = dcrDocDataDao.getDCRDocData(hqcode).getDCRDocDataJSONArray();
            Log.d("check_json", jsonvst_Doc.toString());
            if (!jsonvst_Doc.equals("[]") || !jsonvst_Doc.equals("null") && loginResponse.getRemainder_geo().equals("0")) {
                for (int i = 0; i < jsonvst_Doc.length(); i++) {
                    JSONObject jsonObject = jsonvst_Doc.getJSONObject(i);
                    if (loginResponse.getGeoChk().equals("1")) {//"Lat":"", "Long":"",
                        if (!jsonObject.getString("Lat").equals("") && !jsonObject.getString("Long").equals("")) {

                            String Code = jsonObject.getString("Code");
                            String Name = jsonObject.getString("Name");
                            String Category = jsonObject.getString("Category");
                            String Specialty = jsonObject.getString("Specialty");
                            Town_Name = jsonObject.getString("Town_Name");

                            String CategoryCode = jsonObject.getString("CategoryCode");
                            String SpecialtyCode = jsonObject.getString("SpecialtyCode");
                            String Town_Code = jsonObject.getString("Town_Code");

                            remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name, Category, Specialty, Town_Name, CategoryCode, SpecialtyCode, Town_Code, SfType);
                            RemaindercallsActivity.listeduser.add(doc_VALUES);
                        }
                    } else {

                        String Code = jsonObject.getString("Code");
                        String Name = jsonObject.getString("Name");
                        String Category = jsonObject.getString("Category");
                        String Specialty = jsonObject.getString("Specialty");
                        Town_Name = jsonObject.getString("Town_Name");

                        String CategoryCode = jsonObject.getString("CategoryCode");
                        String SpecialtyCode = jsonObject.getString("SpecialtyCode");
                        String Town_Code = jsonObject.getString("Town_Code");

                        remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name, Category, Specialty, Town_Name, CategoryCode, SpecialtyCode, Town_Code, SfType);
                        RemaindercallsActivity.listeduser.add(doc_VALUES);
                    }


                }


            }

            RemaindercallsActivity.remaindercallsAdapter = new remaindercalls_adapter(context, RemaindercallsActivity.listeduser);
            RemaindercallsActivity.remaindercallsAdapter.filterList(RemaindercallsActivity.listeduser);
            RemaindercallsActivity.remainded_view.setItemAnimator(new DefaultItemAnimator());
            RemaindercallsActivity.remainded_view.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
            RemaindercallsActivity.remainded_view.setAdapter(RemaindercallsActivity.remaindercallsAdapter);

            RemaindercallsActivity.remaindercallsAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void getData(String hq_code) {
        ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();

        List<MasterSyncItemModel> list = new ArrayList<>();


        list.add(new MasterSyncItemModel("Doctor", 0, "Doctor", "getdoctors", Constants.DOCTOR + hq_code, 0, false));

        for (int i = 0; i < list.size(); i++) {
            syncMaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hq_code);
            Log.d("check_syndata", list.get(i).getMasterOf() + "====" + list.get(i).getRemoteTableName() + "===" + list.get(i).getLocalTableKeyName());
        }


    }

    public void syncMaster(String masterFor, String remoteTableName, String LocalTableKeyName, String hq_code) {
        Log.d("listed", hq_code);
        if (UtilityClass.isNetworkAvailable(context)) {
            progressDialog = CommonUtilsMethods.createProgressDialog(context);
            try {
                String baseUrl = SharedPref.getBaseWebUrl(context);
                String pathUrl = SharedPref.getPhpPathUrl(context);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");

                api_interface = RetrofitClient.getRetrofit(context, baseUrl + replacedUrl);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", loginResponse.getSF_Code());
                jsonObject.put("division_code", loginResponse.getDivision_Code());
                jsonObject.put("Rsf", hq_code);
                jsonObject.put("sf_type", loginResponse.getSf_type());
                jsonObject.put("Designation", loginResponse.getDesig());
                jsonObject.put("state_code", loginResponse.getState_Code());
                jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());

                Log.d("jsonObject", String.valueOf(jsonObject));

                Map<String, String> mapString = new HashMap<>();

                if (masterFor.equalsIgnoreCase("Doctor")) {
                    mapString.put("axn", "table/dcrmasterdata");
                } else if (masterFor.equalsIgnoreCase("Subordinate")) {
                    mapString.put("axn", "table/subordinates");
                }
                Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            try {
                                RemaindercallsActivity.listeduser.clear();
                                Log.e("test", "response : " + Objects.requireNonNull(response.body()) + "--");
                                JsonElement jsonElement = response.body();


                                Log.d("json_check", jsonElement.toString());
                                progressDialog.dismiss();

                                if (!jsonElement.isJsonNull() || !jsonElement.equals("[]")) {

                                    if (jsonElement.isJsonArray()) {
                                        JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                        Log.d("check_jsonval", jsonArray1.toString());
                                        if (masterFor.equals("Doctor")) {
                                            progressDialog.dismiss();

                                            change_hq(jsonArray1, LocalTableKeyName);
                                        }
                                        if (masterFor.equals("Subordinate")) {//Subordinate
                                            Log.d("data_jw", "1243456567jw");
                                            progressDialog.dismiss();
                                            Show_Subordinate(jsonArray1, LocalTableKeyName);
                                        }
                                    }
                                } else {
                                    progressDialog.dismiss();
                                }
                                if (jsonElement.toString().equals("[]")) {
                                    RemaindercallsActivity.listeduser.clear();
                                    Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                                RemaindercallsActivity.remaindercallsAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        progressDialog.dismiss();
                    }
                });


            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    public void Show_Subordinate(JsonArray json, String hq_code) {
        try {
            RemaindercallsActivity.sub_list.clear();
            JSONArray jsonvst_Doc = new JSONArray(json.toString());
//            sqLite.insert_docvalues("Joint_Work_" + hq_code, jsonvst_Doc.toString());
            dcrDocDataDao.insertDCRDocValues(new DCRDocDataTable("Joint_Work_" + hq_code, jsonvst_Doc.toString()));

            SharedPref.setDcr_dochqcode(context, hq_code);

            if (jsonvst_Doc.length() > 0) {
                for (int i = 0; i < jsonvst_Doc.length(); i++) {
                    JSONObject jsonObject = jsonvst_Doc.getJSONObject(i);
                    String Code = jsonObject.getString("Code");
                    String Name = jsonObject.getString("Name");

                    remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name);
                    RemaindercallsActivity.sub_list.add(doc_VALUES);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void change_hq(JsonArray json, String hq_code) {
        try {
            RemaindercallsActivity.listeduser.clear();
            JSONArray jsonArray1 = new JSONArray(json.toString());
            SharedPref.setDcr_dochqcode(context, hq_code);

//            sqLite.insert_docvalues("Doctor_" + hq_code, jsonArray1.toString());
            dcrDocDataDao.insertDCRDocValues(new DCRDocDataTable("Doctor_" + hq_code, jsonArray1.toString()));
            String Town_Name = "";
            if (jsonArray1.length() > 0) {
                if (!jsonArray1.equals("[]") || !jsonArray1.equals("null") && loginResponse.getRemainder_geo().equals("0")) {
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject = jsonArray1.getJSONObject(i);
                        if (loginResponse.getGeoChk().equals("1")) {//"Lat":"", "Long":"",
                            if (!jsonObject.getString("Lat").equals("") && !jsonObject.getString("Long").equals("")) {

                                String Code = jsonObject.getString("Code");
                                String Name = jsonObject.getString("Name");
                                String Category = jsonObject.getString("Category");
                                String Specialty = jsonObject.getString("Specialty");
                                Town_Name = jsonObject.getString("Town_Name");

                                String CategoryCode = jsonObject.getString("CategoryCode");
                                String SpecialtyCode = jsonObject.getString("SpecialtyCode");
                                String Town_Code = jsonObject.getString("Town_Code");

                                remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name, Category, Specialty, Town_Name, CategoryCode, SpecialtyCode, Town_Code, SfType);
                                RemaindercallsActivity.listeduser.add(doc_VALUES);
                            }
                        } else {

                            String Code = jsonObject.getString("Code");
                            String Name = jsonObject.getString("Name");
                            String Category = jsonObject.getString("Category");
                            String Specialty = jsonObject.getString("Specialty");
                            Town_Name = jsonObject.getString("Town_Name");

                            String CategoryCode = jsonObject.getString("CategoryCode");
                            String SpecialtyCode = jsonObject.getString("SpecialtyCode");
                            String Town_Code = jsonObject.getString("Town_Code");

                            remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name, Category, Specialty, Town_Name, CategoryCode, SpecialtyCode, Town_Code, SfType);
                            RemaindercallsActivity.listeduser.add(doc_VALUES);
                        }


                    }


                }


                RemaindercallsActivity.remaindercallsAdapter = new remaindercalls_adapter(context, RemaindercallsActivity.listeduser);
                RemaindercallsActivity.remaindercallsAdapter.filterList(RemaindercallsActivity.listeduser);
                RemaindercallsActivity.remainded_view.setItemAnimator(new DefaultItemAnimator());
                RemaindercallsActivity.remainded_view.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
                RemaindercallsActivity.remainded_view.setAdapter(RemaindercallsActivity.remaindercallsAdapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
