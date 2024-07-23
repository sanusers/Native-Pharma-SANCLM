package saneforce.sanzen.activity.remaindercalls;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityRemaindercallsBinding;

import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.DCRDocDataTableDetails.DCRDocDataDao;
import saneforce.sanzen.roomdatabase.DCRDocDataTableDetails.DCRDocDataTable;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class RemaindercallsActivity extends AppCompatActivity {

    String SfType = "";

    TextView headtext_id;
    RelativeLayout hq_name1;
    public static EditText et_Custsearch;
    public static RecyclerView app_recycler_view;
    public static cuslistadapter hqlistadapter;

    public static ImageView close_sideview;
    NavigationView nav_view1;
    public static remaindercalls_adapter remaindercallsAdapter;
    public static String REm_hq_code;
    public static ArrayList<remainder_modelclass> listeduser = new ArrayList<>();
    public static ArrayList<remainder_modelclass> sub_list = new ArrayList<>();
    public static ArrayList<String> slt_hq = new ArrayList<>();
    ArrayList<remainder_modelclass> hq_view = new ArrayList<>();
    ArrayList<remainder_modelclass> filterd_hqname = new ArrayList<>();
    public static String vals_rm = "";
    ApiInterface api_interface;
    private RoomDB roomDB;
    private DCRDocDataDao dcrDocDataDao;
    private MasterDataDao masterDataDao;
  public  static   ActivityRemaindercallsBinding remcallbinding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remcallbinding = ActivityRemaindercallsBinding.inflate(getLayoutInflater());
        setContentView(remcallbinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        roomDB = RoomDB.getDatabase(this);
        dcrDocDataDao = roomDB.dcrDocDataDao();
        masterDataDao = roomDB.masterDataDao();
        nav_view1 = findViewById(R.id.nav_view1);
        app_recycler_view = findViewById(R.id.app_recycler_view);
        headtext_id = findViewById(R.id.headtext_id);
        close_sideview = findViewById(R.id.close_sideview);
        et_Custsearch = findViewById(R.id.et_Custsearch);
        hq_name1 = findViewById(R.id.hq_name1);
        SfType = SharedPref.getSfType(this);
        REm_hq_code = SharedPref.getSfCode(this);

        app_recycler_view.setVisibility(View.VISIBLE);
        remcallbinding.title.setText(SharedPref.getRemainderCallCap(this));

        remcallbinding.backArrow.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
//        remcallbinding.backArrow.setOnClickListener(view -> {
//            startActivity(new Intent(this, HomeDashBoard.class));
//
//        });

        close_sideview.setOnClickListener(view -> {
            RemaindercallsActivity.remcallbinding.drawerLayout.closeDrawer(GravityCompat.END);
            UtilityClass.hideKeyboard(this);
        });

        remcallbinding.townname.setText(SharedPref.getHqName(this));


            remcallbinding.townname.setOnClickListener(v -> {
                if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                    show_hq();
                    remcallbinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                }
            });


        headtext_id.setText("HeadQuarter");

        show_docdata();
        remcallbinding.searchBar1.addTextChangedListener(new TextWatcher() {
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


    public void show_docdata() {
        try {
            listeduser.clear();
            JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();

            String Town_Name = "";

            Log.d("jsonlist", "Doctor_" + SharedPref.getHqCode(this) + "--" + jsonvst_Doc.toString());
            Log.d("jsonlist", jsonvst_Doc.toString());//

            dcrDocDataDao.insertDCRDocValues(new DCRDocDataTable("Doctor_" + SharedPref.getHqCode(this), jsonvst_Doc.toString()));
            SharedPref.setDcr_dochqcode(this, "Doctor_" + SharedPref.getHqCode(this));
            if (jsonvst_Doc.length() > 0) {
                for (int i = 0; i < jsonvst_Doc.length(); i++) {
                    JSONObject jsonObject = jsonvst_Doc.getJSONObject(i);
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
                remcallbinding.remaindedView.setItemAnimator(new DefaultItemAnimator());
                remcallbinding.remaindedView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
                remcallbinding.remaindedView.setAdapter(remaindercallsAdapter);
                remaindercallsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Show_Subordinate() {
        try {
            sub_list.clear();
            JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
            dcrDocDataDao.insertDCRDocValues(new DCRDocDataTable("Joint_Work_" + SharedPref.getHqCode(this), jsonvst_Doc.toString()));

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
        filterd_hqname.clear();
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
            JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            if (jsonvst_Doc.length() > 0) {
                for (int i = 0; i < jsonvst_Doc.length(); i++) {
                    JSONObject jsonObject = jsonvst_Doc.getJSONObject(i);
                    String Code = jsonObject.getString("id");
                    String Name = jsonObject.getString("name");

                    Log.d("listsize", Name + "___" + Code);

                    remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name, "");
                    hq_view.add(doc_VALUES);
                }
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

}