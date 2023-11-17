package saneforce.sanclm.activity.myresource;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static java.util.Locale.filter;
import static saneforce.sanclm.commonClasses.UtilityClass.hideKeyboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.TimeUtils;

public class MyResource_Activity extends AppCompatActivity implements LocationListener {
    ImageView back_btn;
    RecyclerView resource_id;
    Resource_adapter resourceAdapter;
    public ArrayList<Resourcemodel_class> listed_data = new ArrayList<>();
    public static ArrayList<Resourcemodel_class> listresource = new ArrayList<>();
    HashMap<String, Integer> idCounts = new HashMap<>();

    public static ArrayList<Resourcemodel_class> search_list = new ArrayList<>();
    public static ArrayList<String> count_list = new ArrayList<>();
    public static   ArrayList<String> visitcount_list = new ArrayList<>();
    ArrayList<String> visit_list = new ArrayList<>();
    ArrayList<String> visitlist12 = new ArrayList<>();
    public static DrawerLayout drawerLayout;
    public static RecyclerView appRecyclerView;
    public static String datalist;
    public static EditText et_Custsearch;
    public static ImageView close_sideview;
    public static TextView headtext_id;
    public static String values1;
    public static String Key;
    LoginResponse loginResponse;
    double str1, str2;

    //  TextView hq_head;
    protected LocationManager mLocationManager;
    Location gps_loc, network_loc, final_loc;
    public Criteria criteria;
    public String bestProvider;
    LinearLayout backArrow,hq_view;
    String Doc_count = "", Che_count = "", Strck_count = "", Unlist_count = "", Cip_count = "", Hosp_count = "";
    SQLite sqLite;
    ArrayList<JSONObject> Coustum_list = new ArrayList<>();
    public static ArrayList<String> list = new ArrayList<>();
    TextView hq_head;
    String navigateFrom = "";    //view_screen-
    public  static String Valcount="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_resource);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        hideKeyboard(MyResource_Activity.this);
        back_btn = findViewById(R.id.back_btn);
        resource_id = findViewById(R.id.resource_id);
        close_sideview = findViewById(R.id.close_sideview);
        headtext_id = findViewById(R.id.headtext_id);
        et_Custsearch = findViewById(R.id.et_Custsearch);
        appRecyclerView = findViewById(R.id.app_recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        backArrow = findViewById(R.id.backArrow);
        hq_head = findViewById(R.id.hq_head);
        hq_view = findViewById(R.id.hq_view);


        appRecyclerView.setVisibility(View.VISIBLE);
        sqLite = new SQLite(this);


        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navigateFrom = getIntent().getExtras().getString("Origin");
        }

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();


        backArrow.setOnClickListener(v -> {
            Intent l = new Intent(MyResource_Activity.this, HomeDashBoard.class);
            startActivity(l);
        });

        if(loginResponse.getDesig_Code().equals("MR")){
            hq_view.setVisibility(View.GONE);
        }else{
            if(loginResponse.getDesig_Code().equals("MGR")){
                hq_view.setVisibility(View.VISIBLE);
                hq_head.setText(SharedPref.getHqName(MyResource_Activity.this));
            }
        }

        Resource_list();


        close_sideview.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.END);
            et_Custsearch.getText().clear();
            hideKeyboard(MyResource_Activity.this);
        });

        doSearch();


    }


    public void Resource_list() {
        try{
            JSONArray jsonDoc = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this));

            String Doc_code="",Chm_code="",Stk_code="",Cip_code="",Hosp_code="",Unlist_code="";
            String doctor= String.valueOf(jsonDoc);
            if (!doctor.equals("") || !doctor.equals("null")) {
                count_list.clear();

                if (jsonDoc.length() > 0) {
                    for (int i = 0; i < jsonDoc.length(); i++) {
                        JSONObject jsonObject = jsonDoc.getJSONObject(i);

                        if (!Doc_code.equals(jsonObject.getString("Code"))) {
                            Doc_code = jsonObject.getString("Code");
                            count_list.add(Doc_code);
                            Doc_count= String.valueOf(count_list.size());
                            Log.d("doctor_23", String.valueOf(count_list));
                        }
                    }
                }else{
                    Doc_count="0";
                }
            }

            JSONArray jsonChm = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(this));
            String chemist= String.valueOf(jsonChm);
            if (!chemist.equals("") || !chemist.equals("null")) {
                count_list.clear();

                if (jsonChm.length() > 0) {
                    for (int i = 0; i < jsonChm.length(); i++) {
                        JSONObject jsonObject = jsonChm.getJSONObject(i);

                        if (!Chm_code.equals(jsonObject.getString("Code"))) {
                            Chm_code = jsonObject.getString("Code");
                            count_list.add(Chm_code);
                            Che_count= String.valueOf(count_list.size());
                        }
                    }
                }else{
                    Che_count="0";
                }
            }

            JSONArray jsonstock = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(this));
            String stockist= String.valueOf(jsonstock);
            if (!stockist.equals("") || !stockist.equals("null")) {
                count_list.clear();
                if (jsonstock.length() > 0) {
                    for (int i = 0; i < jsonstock.length(); i++) {
                        JSONObject jsonObject = jsonstock.getJSONObject(i);

                        if (!Stk_code.equals(jsonObject.getString("Code"))) {
                            Stk_code = jsonObject.getString("Code");
                            count_list.add(Stk_code);
                            Strck_count= String.valueOf(count_list.size());
                        }
                    }
                }else{
                    Strck_count="0";
                }
            }


            JSONArray jsonunlisted = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(this));
            String unlisted= String.valueOf(jsonunlisted);
            if (!unlisted.equals("") || !unlisted.equals("null")) {
                count_list.clear();

                if (jsonunlisted.length() > 0) {
                    for (int i = 0; i < jsonunlisted.length(); i++) {
                        JSONObject jsonObject = jsonunlisted.getJSONObject(i);

                        if (!Unlist_code.equals(jsonObject.getString("Code"))) {
                            Stk_code = jsonObject.getString("Code");
                            count_list.add(Stk_code);
                            Unlist_count= String.valueOf(count_list.size());
                        }
                    }
                }else{
                    Unlist_count="0";
                }
            }

            JSONArray jsoncip = sqLite.getMasterSyncDataByKey(Constants.CIP + SharedPref.getHqCode(this));
            String cip= String.valueOf(jsoncip);
            if (!cip.equals("") || !cip.equals("null")) {
                count_list.clear();

                if (jsoncip.length() > 0) {
                    for (int i = 0; i < jsoncip.length(); i++) {
                        JSONObject jsonObject = jsoncip.getJSONObject(i);

                        if (!Cip_code.equals(jsonObject.getString("Code"))) {
                            Cip_code= jsonObject.getString("Code");
                            count_list.add(Cip_code);
                            Cip_count= String.valueOf(count_list.size());
                        }
                    }
                }else{
                    Cip_count="0";
                }
            }

            JSONArray jsonhosp = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + SharedPref.getHqCode(this));
            String hosp= String.valueOf(jsonhosp);
            if (!hosp.equals("") || !hosp.equals("null")) {
                count_list.clear();

                if (jsonhosp.length() > 0) {
                    for (int i = 0; i < jsonhosp.length(); i++) {
                        JSONObject jsonObject = jsonhosp.getJSONObject(i);

                        if (!Hosp_code.equals(jsonObject.getString("Code"))) {
                            Hosp_code= jsonObject.getString("Code");
                            count_list.add(Hosp_code);
                            Hosp_count= String.valueOf(count_list.size());
                        }
                    }
                }else{
                    Hosp_count="0";
                }
            }

            Docvisit();

            listed_data.clear();
            listed_data.add(new Resourcemodel_class(loginResponse.getDrCap(),Doc_count ,"1"));
            listed_data.add(new Resourcemodel_class(loginResponse.getChmCap(), Che_count,"2"));
            listed_data.add(new Resourcemodel_class(loginResponse.getStkCap(), Strck_count,"3"));
            listed_data.add(new Resourcemodel_class(loginResponse.getNLCap(), Unlist_count,"4"));
            listed_data.add(new Resourcemodel_class("Hospital", Hosp_count,"5"));
            listed_data.add(new Resourcemodel_class("Cip", Cip_count,"6"));
            listed_data.add(new Resourcemodel_class("Input", String.valueOf(sqLite.getMasterSyncDataByKey(Constants.INPUT).length()),"7"));
            listed_data.add(new Resourcemodel_class("Product", String.valueOf(sqLite.getMasterSyncDataByKey(Constants.PRODUCT).length()),"8"));
            listed_data.add(new Resourcemodel_class("Culster", String.valueOf(sqLite.getMasterSyncDataByKey(Constants.CLUSTER+ SharedPref.getHqCode(this)).length()),"9"));
            listed_data.add(new Resourcemodel_class("Doctor Visit",values1,"10"));

            resourceAdapter = new Resource_adapter(MyResource_Activity.this, listed_data);
            resource_id.setItemAnimator(new DefaultItemAnimator());
            resource_id.setLayoutManager(new GridLayoutManager(MyResource_Activity.this, 4, GridLayoutManager.VERTICAL, false));
            resource_id.setAdapter(resourceAdapter);



        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public void Docvisit(){
        try {
            idCounts.clear();
            count_list.clear();
            visitcount_list.clear();

            JSONArray jsonvst_ctl = sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL);
            JSONArray jsonvst_Doc = sqLite.getMasterSyncDataByKey(Constants.DOCTOR+SharedPref.getHqCode(this));
            // Initialize a HashMap to store counts of custom_id1 values
           String viewlist= TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, CommonUtilsMethods.getCurrentInstance());
            if (jsonvst_ctl.length() > 0) {
                for (int i = 0; i < jsonvst_ctl.length(); i++) {
                    JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
                    String custom_id1 = jsonObject.getString("CustCode");
                    String Mnth = jsonObject.getString("Mnth");
                    if(viewlist.equals(Mnth)){
                        // Check if the custom_id1 value is already in the HashMap
                        if (idCounts.containsKey(custom_id1)) {
                            // If it's already in the HashMap, increment the count
                            idCounts.put(custom_id1, idCounts.get(custom_id1) + 1);
                        } else {
                            // If it's not in the HashMap, add it with a count of 1
                            idCounts.put(custom_id1, 1);
                        }
                    }

                }

                // Now, you have a HashMap with custom_id1 as keys and their counts as values
                // You can iterate through the HashMap to get the counts of each custom_id1
                for (String id : idCounts.keySet()) {
                    int count = idCounts.get(id);

                    count_list.add(id);
                    visitcount_list.add(String.valueOf(count));
                    values1= String.valueOf(count_list.size());
                    System.out.println("Tlvst: " + count_list + ", Count: " + visitcount_list);
                }

            }else{
                values1="0";
            }





        }catch (Exception a){
            a.printStackTrace();
        }

    }
    private void doSearch() {
        et_Custsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = et_Custsearch.getText().toString().toLowerCase(Locale.getDefault());
                filter(text);
                Log.d("Printlog", "example log");
            }
        });
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        listresource.clear();

        if (charText.length() == 0) {
            listresource.addAll(search_list);
        } else {
            for (int i = 0; i < search_list.size(); i++) {

                final String text = search_list.get(i).getDcr_name().toLowerCase();
                if (text.contains(charText)) {
                    listresource.add(search_list.get(i));
                }
            }
        }
        Res_sidescreenAdapter appAdapter_0 = new Res_sidescreenAdapter(MyResource_Activity.this, listresource, Valcount);
        appRecyclerView.setAdapter(appAdapter_0);
        appRecyclerView.setLayoutManager(new LinearLayoutManager(MyResource_Activity.this));
        appAdapter_0.notifyDataSetChanged();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
