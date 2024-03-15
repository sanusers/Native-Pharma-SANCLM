package saneforce.santrip.activity.remaindercalls;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class RemaindercallsActivity extends AppCompatActivity {
    ImageView back_btn;
    SQLite sqLite;
    String SfType = "", Sf_code = "";
    LoginResponse loginResponse;
    TextView search_bar, headtext_id;
    public static TextView townname;
    public static EditText et_Custsearch;
    public static RecyclerView remainded_view;
    public static RecyclerView app_recycler_view;
    public static cuslistadapter hqlistadapter;
    public static DrawerLayout drawer_Layout12;
    public static ImageView close_sideview;
    NavigationView nav_view1;
    public static remaindercalls_adapter remaindercallsAdapter;
    public static String REm_hq_code;
    public static ArrayList<remainder_modelclass> listeduser = new ArrayList<>();
    public static ArrayList<remainder_modelclass> sub_list = new ArrayList<>();
    public static ArrayList<String> slt_hq = new ArrayList<>();
    ArrayList<remainder_modelclass> hq_view = new ArrayList<>();
    public static String vals_rm = "";
    ProgressDialog progressDialog = null;
    ApiInterface api_interface;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_remaindercalls);
        back_btn = findViewById(R.id.back_btn);
        remainded_view = findViewById(R.id.remainded_view);
        search_bar = findViewById(R.id.search_bar1);
        townname = findViewById(R.id.townname);
        drawer_Layout12 = findViewById(R.id.drawer_layout);
        nav_view1 = findViewById(R.id.nav_view1);
        app_recycler_view = findViewById(R.id.app_recycler_view);
        headtext_id = findViewById(R.id.headtext_id);
        close_sideview = findViewById(R.id.close_sideview);
        et_Custsearch = findViewById(R.id.et_Custsearch);

        sqLite = new SQLite(this);
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();
        SfType = SharedPref.getSfType(this);
        REm_hq_code = SharedPref.getSfCode(this);

        app_recycler_view.setVisibility(View.VISIBLE);

        back_btn.setOnClickListener(v -> {
            finish();
        });

        close_sideview.setOnClickListener(view -> {
            RemaindercallsActivity.drawer_Layout12.closeDrawer(GravityCompat.END);
        });

        townname.setText(SharedPref.getHqName(this));

        if (loginResponse.getGeoChk().equals("1")) {
//            getData(SharedPref.getHqCode(this));
        }


        if (SharedPref.getDivisionCode(this).equals("MR")) {

        } else {
            townname.setOnClickListener(v -> {
                show_hq();
                drawer_Layout12.openDrawer(GravityCompat.END);

            });
        }

        headtext_id.setText("HeadQuarter");
        Show_Subordinate();


        show_docdata();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        et_Custsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter_hq(editable.toString());
            }
        });

    }

    public void getData(String hq_code) {
        ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();

        List<MasterSyncItemModel> list = new ArrayList<>();


        list.add(new MasterSyncItemModel("Doctor", 0, "Doctor", "getdoctors", Constants.DOCTOR + hq_code, 0, false));
//
//        for (int i = 0; i < list.size(); i++) {
//            syncMaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hq_code);
//            Log.d("check_syndata", list.get(i).getMasterOf() + "====" + list.get(i).getRemoteTableName() + "===" + list.get(i).getLocalTableKeyName());
//        }

        for (int i = 0; i < list.size(); i++) {
            syncMaster(list.get(i), hq_code);
        }

    }


    public void show_docdata() {
        try {
            listeduser.clear();
            JSONArray jsonvst_Doc = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this));

            String Town_Name = "";

            Log.d("jsonlist", jsonvst_Doc.toString());//

            sqLite.insert_docvalues("Doctor_" + SharedPref.getHqCode(this), jsonvst_Doc.toString());


            SharedPref.setDcr_dochqcode(this, "Doctor_" + SharedPref.getHqCode(this));


            if (jsonvst_Doc.length() > 0) {
                for (int i = 0; i < jsonvst_Doc.length(); i++) {
                    JSONObject jsonObject = jsonvst_Doc.getJSONObject(i);
                    //"lat":"13.029991513522175",
                    //"long":"80.24135816842318",


                    if (SharedPref.getGeoNeed(this).equals("1")) {//"Lat":"", "Long":"",
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
                            listeduser.add(doc_VALUES);
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
                        listeduser.add(doc_VALUES);
                    }


                }
                Log.d("listsize_SfType", String.valueOf(SfType));
                remaindercallsAdapter = new remaindercalls_adapter(this, listeduser);
                remainded_view.setItemAnimator(new DefaultItemAnimator());
                remainded_view.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
                remainded_view.setAdapter(remaindercallsAdapter);
                remaindercallsAdapter.notifyDataSetChanged();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Show_Subordinate() {
        try {
            sub_list.clear();
//            jsonArray = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + TodayPlanSfCode);
            JSONArray jsonvst_Doc = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + SharedPref.getHqCode(this));
            sqLite.insert_docvalues("Joint_Work_" + SharedPref.getHqCode(this), jsonvst_Doc.toString());

            SharedPref.setDcr_dochqcode(this, "Joint_Work_" + SharedPref.getHqCode(this));

            if (jsonvst_Doc.length() > 0) {
                for (int i = 0; i < jsonvst_Doc.length(); i++) {
                    JSONObject jsonObject = jsonvst_Doc.getJSONObject(i);
                    String Code = jsonObject.getString("Code");
                    String Name = jsonObject.getString("Name");

                    remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name);
                    sub_list.add(doc_VALUES);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filter(String text) {
        ArrayList<remainder_modelclass> filterdNames = new ArrayList<>();
        for (remainder_modelclass s : listeduser) {
            if (s.getDoc_name().toLowerCase().contains(text.toLowerCase()) || s.getDoc_town().toLowerCase().contains(text.toLowerCase()) || s.getDoc_spec().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        remaindercallsAdapter.filterList(filterdNames);
    }

    private void filter_hq(String text) {
        ArrayList<remainder_modelclass> filterd_hqname = new ArrayList<>();
        for (remainder_modelclass s : hq_view) {
            if (s.getDoc_name().toLowerCase().contains(text.toLowerCase())) {
                filterd_hqname.add(s);
            }
        }
        hqlistadapter.filter_hqList(filterd_hqname);
    }

    public void show_hq() {
        try {
            hq_view.clear();
            JSONArray jsonvst_Doc = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
            if (jsonvst_Doc.length() > 0) {
                for (int i = 0; i < jsonvst_Doc.length(); i++) {
                    JSONObject jsonObject = jsonvst_Doc.getJSONObject(i);
                    String Code = jsonObject.getString("id");
                    String Name = jsonObject.getString("name");

                    Log.d("listsize", Name + "___" + Code);

                    remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name, "");
                    hq_view.add(doc_VALUES);
                }
//                Log.d("listsize", String.valueOf(hq_view));

                hqlistadapter = new cuslistadapter(RemaindercallsActivity.this, hq_view, "");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                app_recycler_view.setLayoutManager(mLayoutManager);
                app_recycler_view.setItemAnimator(new DefaultItemAnimator());
                app_recycler_view.setAdapter(hqlistadapter);
                hqlistadapter.notifyDataSetChanged();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncMaster(MasterSyncItemModel masterSyncItemModel, String hqCode) {
        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this).replace(",", "").trim());
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", SharedPref.getSfType(this));
                jsonObject.put("Designation", SharedPref.getDesig(this));
                jsonObject.put("state_code",SharedPref.getStateCode(this));
                jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));


                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Log.e("test", "master sync obj in TP : " + jsonObject + "--" + hqCode);
                Call<JsonElement> call = null;
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Doctor")) {
                    call = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());

                }

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                            boolean success = false;
                            if (response.isSuccessful()) {
                                try {
                                    JsonElement jsonElement = response.body();


                                    JSONArray jsonArray = new JSONArray();
                                    assert jsonElement != null;
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                            if (!jsonObject1.has("success")) { // json object with "success" : "fail" will be received only when api call is failed ,"success will not be received when api call is success
                                                jsonArray.put(jsonObject1);
                                                success = true;
                                            } else if (jsonObject1.has("success") && !jsonObject1.getBoolean("success")) {
                                                sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        Log.d("syncheck", masterSyncItemModel.getLocalTableKeyName() + "--" + success + "--" + jsonArray.toString());

                                        if (success) {

                                            sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0);
                                        }
                                    } else {

                                        sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }


                                    show_docdata();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }


}