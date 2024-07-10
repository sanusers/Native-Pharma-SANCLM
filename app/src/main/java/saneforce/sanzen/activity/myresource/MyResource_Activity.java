package saneforce.sanzen.activity.myresource;

import static saneforce.sanzen.commonClasses.UtilityClass.hideKeyboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityMyResourceBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class MyResource_Activity extends AppCompatActivity {

    public static ArrayList<Resourcemodel_class> listresource = new ArrayList<>();
    public static ArrayList<Resourcemodel_class> search_list = new ArrayList<>();
    public static ArrayList<String> count_list = new ArrayList<>();
    public static ArrayList<String> visitcount_list = new ArrayList<>();
    public static RecyclerView appRecyclerView;
    public static String datalist = "";
    public static EditText et_Custsearch;
    public static ImageView close_sideview;
    public static TextView headtext_id;
    public static String values1;

    public static String Key;
    public static ArrayList<String> list = new ArrayList<>();

    ArrayList<String> inputcount = new ArrayList<>();
    ArrayList<String> productcount = new ArrayList<>();
    public static String Valcount = "";
    public ArrayList<Resourcemodel_class> listed_data = new ArrayList<>();
    Resource_adapter resourceAdapter;
    HashMap<String, Integer> idCounts = new HashMap<>();
    String Doc_count = "", Che_count = "", Strck_count = "", Unlist_count = "", Cip_count = "", Hosp_count = "";
    String navigateFrom = "", input_count = "", product_count = "";
    public static ActivityMyResourceBinding binding;
    LocalDate date_n;
    private RoomDB roomDB;

    private MasterDataDao masterDataDao;
    boolean isLeaveEntitlementRequested;
    boolean isInputRequested, isProductRequested;
    int inputCount = 0;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyResourceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        hideKeyboard(MyResource_Activity.this);
        close_sideview = findViewById(R.id.close_sideview);
        headtext_id = findViewById(R.id.headtext_id);
        et_Custsearch = findViewById(R.id.et_Custsearch);
        appRecyclerView = findViewById(R.id.app_recycler_view);
        appRecyclerView.setVisibility(View.VISIBLE);

        roomDB = RoomDB.getDatabase(getApplicationContext());

        masterDataDao = roomDB.masterDataDao();
        setUp();

        isLeaveEntitlementRequested = SharedPref.getLeaveEntitlementNeed(this).equals("0");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navigateFrom = getIntent().getExtras().getString("Origin");
        }
        binding.backArrow.setOnClickListener(v -> {
            UtilityClass.hideKeyboard(this);
            getOnBackPressedDispatcher().onBackPressed();
        });
        Log.d("div_name", SharedPref.getDesigCode(this) + "--" + SharedPref.getDesig(this));
        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
            binding.hqView.setVisibility(View.VISIBLE);
            binding.hqHead.setText(SharedPref.getHqName(MyResource_Activity.this));
        }

        binding.hqView.setOnClickListener(v -> {
            syn_hq();
        });
        Resource_list(SharedPref.getHqCode(this));

        close_sideview.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.END);
            et_Custsearch.getText().clear();
            UtilityClass.hideKeyboard(this);
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


    public void Resource_list(String synhqval1) {
        try {
            JSONArray jsonDoc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + synhqval1).getMasterSyncDataJsonArray();

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

            JSONArray jsonChm = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + synhqval1).getMasterSyncDataJsonArray();
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
            JSONArray jsonstock = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + synhqval1).getMasterSyncDataJsonArray();

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
            JSONArray jsonunlisted = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + synhqval1).getMasterSyncDataJsonArray();
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


            JSONArray jsoncip = masterDataDao.getMasterDataTableOrNew(Constants.CIP + synhqval1).getMasterSyncDataJsonArray();
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
            JSONArray jsonhosp = masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + synhqval1).getMasterSyncDataJsonArray();
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
            if (SharedPref.getDrNeed(this).equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(SharedPref.getDrCap(this), Doc_count, "1"));
            if (SharedPref.getChmNeed(this).equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(SharedPref.getChmCap(this), Che_count, "2"));
            if (SharedPref.getStkNeed(this).equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(SharedPref.getStkCap(this), Strck_count, "3"));
            if (SharedPref.getUnlNeed(this).equalsIgnoreCase("0"))
                listed_data.add(new Resourcemodel_class(SharedPref.getUNLcap(this), Unlist_count, "4"));
//            if (SharedPref.getHospNeed(this).equalsIgnoreCase("0"))
//                listed_data.add(new Resourcemodel_class(SharedPref.getHospCaption(this), Hosp_count, "5"));
//            if (SharedPref.getCipINeed(this).equalsIgnoreCase("0"))
//                listed_data.add(new Resourcemodel_class(SharedPref.getCipCaption(this), Cip_count, "6"));


            try {
                inputcount.clear();
                productcount.clear();
                String input_val = "", product_val = "";
                JSONArray jsoninput = masterDataDao.getMasterDataTableOrNew(Constants.INPUT).getMasterSyncDataJsonArray();
                for (int i = 0; i < jsoninput.length(); i++) {
                    JSONObject jsonObject = jsoninput.getJSONObject(i);
                    if (!input_val.equals(jsonObject.getString("Code")) && (!jsonObject.getString("Code").equalsIgnoreCase("-1"))) {
                        inputcount.add(jsonObject.getString("Code"));
                    }
                }
                JSONArray jsonproduct = masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT).getMasterSyncDataJsonArray();


                for (int i = 0; i < jsonproduct.length(); i++) {
                    JSONObject jsonObject = jsonproduct.getJSONObject(i);
                    if (!product_val.equals(jsonObject.getString("Code")) && (!jsonObject.getString("Code").equalsIgnoreCase("-1"))) {

                        productcount.add(jsonObject.getString("Code"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String dcrCaption = SharedPref.getDrCap(this);
            if (dcrCaption == null || dcrCaption.isEmpty()) {
                dcrCaption = "Doctor";
            } else {
                dcrCaption = SharedPref.getDrCap(this);
            }
            input_count = String.valueOf(inputcount.size());
            product_count = String.valueOf(productcount.size());
            inputCount = 0;
            JSONArray input = masterDataDao.getMasterDataTableOrNew(Constants.INPUT).getMasterSyncDataJsonArray();
            for (int i = 0; i < input.length(); i++) {
                JSONObject jsonObject = input.getJSONObject(i);
                int code = jsonObject.getInt("Code");
                if (code >= 0) {
                    inputCount++;
                }
            }
            if (isInputRequested) {
                listed_data.add(new Resourcemodel_class("Input", String.valueOf(inputCount), "7"));
            }
            if (isProductRequested) {
                listed_data.add(new Resourcemodel_class("Product", String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT).getMasterSyncDataJsonArray().length()), "8"));
            }
            listed_data.add(new Resourcemodel_class(SharedPref.getClusterCap(this), String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + synhqval1).getMasterSyncDataJsonArray().length()), "9"));
            listed_data.add(new Resourcemodel_class("Holiday / Weekly off", masterDataDao.getMasterDataTableOrNew(Constants.HOLIDAY).getMasterSyncDataJsonArray().length() + " / " + masterDataDao.getMasterDataTableOrNew(Constants.WEEKLY_OFF).getMasterSyncDataJsonArray().length(), "10"));
            listed_data.add(new Resourcemodel_class("Category", String.format("%s / %s", masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray().length(), masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray().length()), "11"));
            listed_data.add(new Resourcemodel_class("WorkType", String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray().length()), "12"));
            if (isLeaveEntitlementRequested) {
                listed_data.add(new Resourcemodel_class("LeaveStatus", String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray().length()), "13"));
            }
            if (SharedPref.getVstNd(getApplicationContext()).equalsIgnoreCase("0") && SharedPref.getSfType(this).equalsIgnoreCase("1")) {
//                listed_data.add(new Resourcemodel_class("Doctor Visit", values1, "10"));
                listed_data.add(new Resourcemodel_class(dcrCaption + " " + "Visit", "", "14"));
            }
//            listed_data.add(new Resourcemodel_class("Calls Status",  String.valueOf(masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray().length()), "12"));
            listed_data.add(new Resourcemodel_class("Calls Summary", "", "15"));
            listed_data.add(new Resourcemodel_class("Date Summary", "", "16"));
            Log.d("counts_data", Doc_count + "--" + Che_count + "--" + Strck_count + "--" + Unlist_count + "---" + Cip_count + "--" + Hosp_count);

            resourceAdapter = new Resource_adapter(MyResource_Activity.this, listed_data, synhqval1);//13
            binding.resourceId.setItemAnimator(new DefaultItemAnimator());
            binding.resourceId.setLayoutManager(new GridLayoutManager(MyResource_Activity.this, 4, GridLayoutManager.VERTICAL, false));
            binding.resourceId.setAdapter(resourceAdapter);
            resourceAdapter.notifyDataSetChanged();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Docvisit() {
        try {
            idCounts.clear();
            count_list.clear();
            visitcount_list.clear();

            JSONArray jsonvst_ctl = masterDataDao.getMasterDataTableOrNew(Constants.VISIT_CONTROL).getMasterSyncDataJsonArray();
            JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
            // Initialize a HashMap to store counts of custom_id1 values
            String viewlist = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_31, (CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")));
            if (jsonvst_ctl.length() > 0) {
                for (int i = 0; i < jsonvst_ctl.length(); i++) {
                    JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
                    String custom_id1 = jsonObject.getString("CustCode");
                    String Mnth = jsonObject.getString("Mnth");
                    if (!custom_id1.isEmpty() && viewlist.equals(Mnth)) {
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

    @SuppressLint("WrongConstant")
    public void syn_hq() {
        List<String> SyHqList = new ArrayList<>();
        SyHqList = SharedPref.getsyn_hqcode(this);
        try {
            listresource.clear();
            //add

            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            headtext_id.setText("Headquarters");

            ArrayList<String> list = new ArrayList<>();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (SyHqList.contains(jsonObject.getString("id")) && (!list.contains(jsonObject.getString("id")))) {
                        list.add(jsonObject.getString("id"));
                        listresource.add(new Resourcemodel_class(jsonObject.getString("id"), jsonObject.getString("name"), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                    }

                    Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(this, listresource, "1", "");
                    appAdapter3.setOnItemClickListener(new Res_sidescreenAdapter.OnItemClickListener() {
                        public void onItemClick(Resourcemodel_class item) {
                            binding.hqHead.setText(item.getDcr_name());
                            et_Custsearch.getText().clear();
                            Resource_list(item.getDcr_code());
                        }
                    });
                    appRecyclerView.setAdapter(appAdapter3);
                    appRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    appAdapter3.notifyDataSetChanged();
                }
            }

            search_list.addAll(listresource);
            binding.drawerLayout.openDrawer(Gravity.END);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filter(String text) {
        ArrayList<Resourcemodel_class> filterdNames = new ArrayList<>();
        for (Resourcemodel_class s : listresource) {
            if (s.getDcr_name().toLowerCase().contains(text.toLowerCase()) || s.getRes_custname().toLowerCase().contains(text.toLowerCase()) || s.getRes_Specialty().toLowerCase().contains(text.toLowerCase()) || s.getRes_Category().toLowerCase().contains(text.toLowerCase()) || s.getWorkType().toLowerCase().contains(text.toLowerCase()) || s.getLeaveTypes().toLowerCase().contains(text.toLowerCase())) {//getRes_Category
                filterdNames.add(s);
            }
        }
        Res_sidescreenAdapter appAdapter_0 = new Res_sidescreenAdapter(MyResource_Activity.this, listresource, Valcount, "");
        appAdapter_0.filterList(filterdNames);
        appAdapter_0.setOnItemClickListener(item -> {
            UtilityClass.hideKeyboard(this);
            et_Custsearch.getText().clear();
            binding.hqHead.setText(item.getDcr_name());
            Resource_list(item.getDcr_code());
        });
        appRecyclerView.setAdapter(appAdapter_0);
        appRecyclerView.setLayoutManager(new LinearLayoutManager(MyResource_Activity.this));
    }

    private void setUp() {
        isInputRequested = SharedPref.getDiNeed(this).equals("0") || SharedPref.getCiNeed(this).equals("0") || SharedPref.getSiNeed(this).equals("0") || SharedPref.getNiNeed(this).equals("0");
        isProductRequested = SharedPref.getDpNeed(this).equals("0") || SharedPref.getCpNeed(this).equals("0") || SharedPref.getSpNeed(this).equals("0") || SharedPref.getNpNeed(this).equals("0");
    }

}
