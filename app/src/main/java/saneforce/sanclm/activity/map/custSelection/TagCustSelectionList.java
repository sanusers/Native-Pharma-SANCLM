package saneforce.sanclm.activity.map.custSelection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class TagCustSelectionList extends AppCompatActivity {
    ImageView img_back;
    RecyclerView rv_cust_list;
    CustListAdapter custListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    EditText editTextSearch_cust;
    ArrayList<CustList> custListArrayList = new ArrayList<>();

    SQLiteHandler sqLiteHandler;
    String getCustListDB, SelectedTab, SfType, SfCode;
    Cursor mCursor;

    @Override
    public void onBackPressed() {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dcr_selection);

        commonUtilsMethods = new CommonUtilsMethods(this);
        sqLiteHandler = new SQLiteHandler(this);
        commonUtilsMethods.FullScreencall();
        SelectedTab = SharedPref.getMapSelectedTab(TagCustSelectionList.this);
        SfType = SharedPref.getSfType(TagCustSelectionList.this);
        SfCode = SharedPref.getSfCode(TagCustSelectionList.this);

        img_back = findViewById(R.id.iv_back);
        rv_cust_list = findViewById(R.id.rv_cust_list);
        editTextSearch_cust = findViewById(R.id.search_cust);

        AddCustList();

        editTextSearch_cust.addTextChangedListener(new TextWatcher() {
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

        img_back.setOnClickListener(view -> {
            Intent intent = new Intent(TagCustSelectionList.this, MapsActivity.class);
            intent.putExtra("from", "not_tagging");
            startActivity(intent);
        });
    }

    private void AddCustList() {
        sqLiteHandler.open();
        custListArrayList.clear();
        Log.v("map_selected_tab", "---" + SharedPref.getMapSelectedTab(TagCustSelectionList.this));

        switch (SelectedTab) {
            case "D":
                try {
                    if (SfType.equalsIgnoreCase("1")) {
                        mCursor = sqLiteHandler.select_doctor_list(SfCode);
                    } else {

                    }
                    //   mCursor = sqLiteHandler.select_master_list("Doctor");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Category"), jsonObject.getString("Specialty"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap")));
                    }
                } catch (Exception e) {

                }

                break;
            case "C":
                try {
                    mCursor = sqLiteHandler.select_master_list("Chemist");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), "Category", "Specialty",
                                jsonObject.getString("Town_Name"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap")));
                    }
                } catch (Exception e) {

                }
                break;
            case "S":
                try {
                    mCursor = sqLiteHandler.select_master_list("Stockiest");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }
                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), "Category", "Specialty",
                                jsonObject.getString("Town_Name"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap")));
                    }
                } catch (Exception e) {

                }
                break;
            case "U":
                try {
                    mCursor = sqLiteHandler.select_master_list("Unlisted_Doctor");
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap")));
                    }
                } catch (Exception e) {

                }

                break;
        }

    /*    if (SharedPref.getMapSelectedTab(TagCustSelectionList.this).equalsIgnoreCase("D")) {
            mCursor = sqLiteHandler.select_master_list("Doctor");
        } else if (SharedPref.getMapSelectedTab(TagCustSelectionList.this).equalsIgnoreCase("C")) {
            mCursor = sqLiteHandler.select_master_list("Chemist");
        } else if (SharedPref.getMapSelectedTab(TagCustSelectionList.this).equalsIgnoreCase("S")) {
            mCursor = sqLiteHandler.select_master_list("Stockiest");
        } else if (SharedPref.getMapSelectedTab(TagCustSelectionList.this).equalsIgnoreCase("U")) {
            mCursor = sqLiteHandler.select_master_list("Unlisted_Doctor");
        }
*/
       /* if (mCursor.getCount() > 0) {
            while (mCursor.moveToNext()) {
                getCustListDB = mCursor.getString(1);
            }
        }

        try {
            JSONArray jsonArray = new JSONArray(getCustListDB);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Category"), jsonObject.getString("Specialty"),
                        jsonObject.getString("Town_Name"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap")));
            }
        } catch (Exception e) {

        }*/

    /*    custListArrayList.clear();
        custListArrayList.add(new CustList("Mohammed Ameer BashaKhan", "Category", "Cardio Surgion", "Madurai", "0/1"));
        custListArrayList.add(new CustList("Baskar Kumar Reddy", "Category", "Neurolgist", "Chennai", "1/1"));
        custListArrayList.add(new CustList("Aasik", "Category", "MBBS", "Trichy", "0/1"));
        custListArrayList.add(new CustList("Umar Kathab Manzoor Ali", "Category", "Ortho Specialist", "Kanyakumari", "0/1"));
        custListArrayList.add(new CustList("Venkatesh", "Category", "Dermotologist", "Sivagangai", "1/1"));
        custListArrayList.add(new CustList("Akash", "Category", "MBBS", "Madurai", "0/1"));
        custListArrayList.add(new CustList("Aravindh", "Category", "Ortho", "Vellore", "1/1"));
        custListArrayList.add(new CustList("Surya Vignesh Kumar ", "Category", "Dermotologist", "Kerala", "0/1"));
        custListArrayList.add(new CustList("Jahir Basha", "Category", "Gynocologist", "Andhra", "1/1"));
        custListArrayList.add(new CustList("Vamshi Kannan", "Category", "Neurolgist", "Jammu", "0/1"));
        custListArrayList.add(new CustList("Madhan", "Category", "Cardiogilist", "Kanyakumari", "1/1"));
*/
        custListAdapter = new CustListAdapter(TagCustSelectionList.this, TagCustSelectionList.this, custListArrayList);
        rv_cust_list.setItemAnimator(new DefaultItemAnimator());
        rv_cust_list.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        rv_cust_list.setAdapter(custListAdapter);
    }

    private void filter(String text) {
        ArrayList<CustList> filterdNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        custListAdapter.filterList(filterdNames);
    }
}