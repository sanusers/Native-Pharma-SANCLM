package saneforce.santrip.activity.myresource;

import static saneforce.santrip.commonClasses.UtilityClass.hideKeyboard;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import saneforce.santrip.R;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;

public class MyResource_Activity extends AppCompatActivity implements LocationListener {
    public static ArrayList<Resourcemodel_class> listresource = new ArrayList<>();
    public static ArrayList<Resourcemodel_class> search_list = new ArrayList<>();
    public static ArrayList<String> count_list = new ArrayList<>();
    public static ArrayList<String> visitcount_list = new ArrayList<>();
    public static DrawerLayout drawerLayout;
    public static RecyclerView appRecyclerView;
    public static String datalist;
    public static EditText et_Custsearch;
    public static ImageView close_sideview;
    public static TextView headtext_id;
    public static String values1;
    public static String Key;
    public static ArrayList<String> list = new ArrayList<>();
    public static String Valcount = "";
    public ArrayList<Resourcemodel_class> listed_data = new ArrayList<>();

    protected LocationManager mLocationManager;
    ImageView back_btn;
    RecyclerView resource_id;
    Resource_adapter resourceAdapter;
    DrawerLayout layout_scrn;
    Res_sidescreenAdapter res_sidescreenAdapter;
    HashMap<String, Integer> idCounts = new HashMap<>();

    LoginResponse loginResponse;

    LinearLayout backArrow, hq_view;
    String Doc_count = "", Che_count = "", Strck_count = "", Unlist_count = "", Cip_count = "", Hosp_count = "";
    SQLite sqLite;

    TextView hq_head;
    String navigateFrom = "";
    private RoomDB roomDB;
    private LoginDataDao loginDataDao;
    private MasterDataDao masterDataDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_resource);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        drawerLayout = findViewById(R.id.drawer_layout);

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
        layout_scrn = findViewById(R.id.layout_scrn);


//        layout_scrn.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        layout_scrn.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                layout_scrn.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                drawerLayout.setBackgroundResource(R.drawable.cross_img);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                close_sideview.setBackgroundResource(R.drawable.bars_sort_img);
            }
        });


        appRecyclerView.setVisibility(View.VISIBLE);
        sqLite = new SQLite(this);

        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        roomDB = RoomDB.getDatabase(getApplicationContext());
        loginDataDao = roomDB.loginDataDao();
        masterDataDao = roomDB.masterDataDao();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navigateFrom = getIntent().getExtras().getString("Origin");
        }
        loginResponse = loginDataDao.getLoginData().getLoginResponse();
//        loginResponse = sqLite.getLoginData();


        backArrow.setOnClickListener(v -> {
           getOnBackPressedDispatcher().onBackPressed();
//            Intent l = new Intent(MyResource_Activity.this, HomeDashBoard.class);
//            startActivity(l);

//            finish();
        });

        if (loginResponse.getDesig_Code().equals("MR")) {
            hq_view.setVisibility(View.GONE);
        } else {
            if (loginResponse.getDesig_Code().equals("MGR")) {
                hq_view.setVisibility(View.VISIBLE);
                hq_head.setText(SharedPref.getHqName(MyResource_Activity.this));
            }
        }

        Resource_list();


        close_sideview.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.END);
            et_Custsearch.getText().clear();
            hideKeyboard(MyResource_Activity.this);
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
                filter(editable.toString());
            }
        });
    }

    public void Resource_list() {
        try {
            JSONArray jsonDoc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
//            JSONArray jsonDoc = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this));

            String Doc_code = "", Chm_code = "", Stk_code = "", Cip_code = "", Hosp_code = "", Unlist_code = "";
            String doctor = String.valueOf(jsonDoc);
            if (!doctor.equals("") || !doctor.equals("null")) {
                count_list.clear();

                if (jsonDoc.length() > 0) {
                    for (int i = 0; i < jsonDoc.length(); i++) {
                        JSONObject jsonObject = jsonDoc.getJSONObject(i);

                        if (!Doc_code.equals(jsonObject.getString("Code"))) {
                            Doc_code = jsonObject.getString("Code");
                            count_list.add(Doc_code);
                            Doc_count = String.valueOf(count_list.size());

                        }
                    }
                } else {
                    Doc_count = "0";
                }
            }

            JSONArray jsonChm = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
//            JSONArray jsonChm = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(this));
            String chemist = String.valueOf(jsonChm);
            if (!chemist.equals("") || !chemist.equals("null")) {
                count_list.clear();

                if (jsonChm.length() > 0) {
                    for (int i = 0; i < jsonChm.length(); i++) {
                        JSONObject jsonObject = jsonChm.getJSONObject(i);

                        if (!Chm_code.equals(jsonObject.getString("Code"))) {
                            Chm_code = jsonObject.getString("Code");
                            count_list.add(Chm_code);
                            Che_count = String.valueOf(count_list.size());
                        }
                    }
                } else {
                    Che_count = "0";
                }
            }

            JSONArray jsonstock = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
//            JSONArray jsonstock = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(this));
            String stockist = String.valueOf(jsonstock);
            if (!stockist.equals("") || !stockist.equals("null")) {
                count_list.clear();
                if (jsonstock.length() > 0) {
                    for (int i = 0; i < jsonstock.length(); i++) {
                        JSONObject jsonObject = jsonstock.getJSONObject(i);

                        if (!Stk_code.equals(jsonObject.getString("Code"))) {
                            Stk_code = jsonObject.getString("Code");
                            count_list.add(Stk_code);
                            Strck_count = String.valueOf(count_list.size());
                        }
                    }
                } else {
                    Strck_count = "0";
                }
            }


            JSONArray jsonunlisted = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
//            JSONArray jsonunlisted = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(this));
            String unlisted = String.valueOf(jsonunlisted);
            if (!unlisted.equals("") || !unlisted.equals("null")) {
                count_list.clear();

                if (jsonunlisted.length() > 0) {
                    for (int i = 0; i < jsonunlisted.length(); i++) {
                        JSONObject jsonObject = jsonunlisted.getJSONObject(i);

                        if (!Unlist_code.equals(jsonObject.getString("Code"))) {
                            Stk_code = jsonObject.getString("Code");
                            count_list.add(Stk_code);
                            Unlist_count = String.valueOf(count_list.size());
                        }
                    }
                } else {
                    Unlist_count = "0";
                }
            }

            JSONArray jsoncip = masterDataDao.getMasterDataTableOrNew(Constants.CIP + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
//            JSONArray jsoncip = sqLite.getMasterSyncDataByKey(Constants.CIP + SharedPref.getHqCode(this));
            String cip = String.valueOf(jsoncip);
            if (!cip.equals("") || !cip.equals("null")) {
                count_list.clear();

                if (jsoncip.length() > 0) {
                    for (int i = 0; i < jsoncip.length(); i++) {
                        JSONObject jsonObject = jsoncip.getJSONObject(i);

                        if (!Cip_code.equals(jsonObject.getString("Code"))) {
                            Cip_code = jsonObject.getString("Code");
                            count_list.add(Cip_code);
                            Cip_count = String.valueOf(count_list.size());
                        }
                    }
                } else {
                    Cip_count = "0";
                }
            }

            JSONArray jsonhosp = masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
//            JSONArray jsonhosp = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + SharedPref.getHqCode(this));
            String hosp = String.valueOf(jsonhosp);
            if (!hosp.equals("") || !hosp.equals("null")) {
                count_list.clear();

                if (jsonhosp.length() > 0) {
                    for (int i = 0; i < jsonhosp.length(); i++) {
                        JSONObject jsonObject = jsonhosp.getJSONObject(i);

                        if (!Hosp_code.equals(jsonObject.getString("Code"))) {
                            Hosp_code = jsonObject.getString("Code");
                            count_list.add(Hosp_code);
                            Hosp_count = String.valueOf(count_list.size());
                        }
                    }
                } else {
                    Hosp_count = "0";
                }
            }

            Docvisit();

            listed_data.clear();
            if (loginResponse.getDrNeed().equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(loginResponse.getDrCap(), Doc_count, "1"));
            if (loginResponse.getChmNeed().equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(loginResponse.getChmCap(), Che_count, "2"));
            if (loginResponse.getStkNeed().equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(loginResponse.getStkCap(), Strck_count, "3"));
            if (loginResponse.getUNLNeed().equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(loginResponse.getNLCap(), Unlist_count, "4"));
            if (loginResponse.getHosp_need().equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(loginResponse.getHosp_caption(), Hosp_count, "5"));
            if (loginResponse.getCip_need().equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(loginResponse.getCIP_Caption(), Cip_count, "6"));
//            listed_data.add(new Resourcemodel_class("Input", String.valueOf(sqLite.getMasterSyncDataByKey(Constants.INPUT).length()), "7"));
//            listed_data.add(new Resourcemodel_class("Product", String.valueOf(sqLite.getMasterSyncDataByKey(Constants.PRODUCT).length()), "8"));
//            listed_data.add(new Resourcemodel_class("Cluster", String.valueOf(sqLite.getMasterSyncDataByKey(Constants.CLUSTER + SharedPref.getHqCode(this)).length()), "9"));
            listed_data.add(new Resourcemodel_class("Input", String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.INPUT).getMasterSyncDataJsonArray().length()), "7"));
            listed_data.add(new Resourcemodel_class("Product", String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT).getMasterSyncDataJsonArray().length()), "8"));
            listed_data.add(new Resourcemodel_class("Cluster", String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray().length()), "9"));
            listed_data.add(new Resourcemodel_class("Doctor Visit", values1, "10"));
//               frmlisted_data.add(new Formsmodel_class("Holiday / Weekly off", R.drawable.vacation));
            listed_data.add(new Resourcemodel_class("Holiday / Weekly off", "", "11"));

            resourceAdapter = new Resource_adapter(MyResource_Activity.this, listed_data);
            resource_id.setItemAnimator(new DefaultItemAnimator());
            resource_id.setLayoutManager(new GridLayoutManager(MyResource_Activity.this, 4, GridLayoutManager.VERTICAL, false));
            resource_id.setAdapter(resourceAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void Docvisit() {
        try {
            idCounts.clear();
            count_list.clear();
            visitcount_list.clear();

//            JSONArray jsonvst_ctl = sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL);
//            JSONArray jsonvst_Doc = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this));
            JSONArray jsonvst_ctl = masterDataDao.getMasterDataTableOrNew(Constants.VISIT_CONTROL).getMasterSyncDataJsonArray();
            JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
            // Initialize a HashMap to store counts of custom_id1 values
            String viewlist = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, (CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")));
            if (jsonvst_ctl.length() > 0) {
                for (int i = 0; i < jsonvst_ctl.length(); i++) {
                    JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
                    String custom_id1 = jsonObject.getString("CustCode");
                    String Mnth = jsonObject.getString("Mnth");
                    if (viewlist.equals(Mnth)) {
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
                    values1 = String.valueOf(count_list.size());
                    System.out.println("Tlvst: " + count_list + ", Count: " + visitcount_list);
                }
            }

            if (idCounts.isEmpty()) {
                values1 = "0";
            }

        } catch (Exception a) {
            a.printStackTrace();
        }

    }


//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//
//        listresource.clear();
//
//        if (charText.length() == 0) {
//            listresource.addAll(search_list);
//        } else {
//            for (int i = 0; i < search_list.size(); i++) {
//
//                final String text = search_list.get(i).getDcr_name().toLowerCase();
//                if (text.contains(charText)) {
//                    listresource.add(search_list.get(i));
//                }
//            }
//        }
//        Res_sidescreenAdapter appAdapter_0 = new Res_sidescreenAdapter(MyResource_Activity.this, listresource, Valcount);
//        appRecyclerView.setAdapter(appAdapter_0);
//        appRecyclerView.setLayoutManager(new LinearLayoutManager(MyResource_Activity.this));
//        appAdapter_0.notifyDataSetChanged();
//    }

//    public static ArrayList<Resourcemodel_class> listresource = new ArrayList<>();

    private void filter(String text) {
        ArrayList<Resourcemodel_class> filterdNames = new ArrayList<>();
        for (Resourcemodel_class s : listresource) {
            if (s.getDcr_name().toLowerCase().contains(text.toLowerCase()) || s.getRes_custname().toLowerCase().contains(text.toLowerCase()) || s.getRes_Specialty().toLowerCase().contains(text.toLowerCase()) || s.getRes_Category().toLowerCase().contains(text.toLowerCase())) {//getRes_Category
                filterdNames.add(s);
            }
        }
        Res_sidescreenAdapter appAdapter_0 = new Res_sidescreenAdapter(MyResource_Activity.this, listresource, Valcount);
        appAdapter_0.filterList(filterdNames);
        appRecyclerView.setAdapter(appAdapter_0);
        appRecyclerView.setLayoutManager(new LinearLayoutManager(MyResource_Activity.this));



    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
