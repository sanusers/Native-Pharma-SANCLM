package saneforce.sanclm.activity.map.custSelection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.masterSync.MasterSyncActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.MapDcrSelectionBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class TagCustSelectionList extends AppCompatActivity {

    MapDcrSelectionBinding binding;
    List<String> HqNameList = new ArrayList<>();
    List<String> HqCodeList = new ArrayList<>();
    CustListAdapter custListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    SQLite sqLite;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    SQLiteHandler sqLiteHandler;
    ApiInterface apiInterface;
    String getCustListDB, SelectedTab, SfType, SfCode, SfName, SelectedHqCode, getHqList, TodayPlanSfCode;
    Cursor mCursor;
    int pos;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (SharedPref.getTaggedSuccessfully(TagCustSelectionList.this).equalsIgnoreCase("true")) {
                CustList mm = custListArrayList.get(Integer.parseInt(SharedPref.getCustomerPosition(TagCustSelectionList.this)));
                int yy = Integer.parseInt(mm.getTag()) + 1;
                mm.setTag(String.valueOf(yy));
                custListAdapter.notifyDataSetChanged();
                SharedPref.setTaggedSuccessfully(TagCustSelectionList.this, "false");
            }
        } catch (Exception e) {

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        commonUtilsMethods = new CommonUtilsMethods(this);
        String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
        String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
        String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);
        sqLiteHandler = new SQLiteHandler(this);
        sqLiteHandler.open();
        sqLite = new SQLite(getApplicationContext());
        commonUtilsMethods.FullScreencall();
        SelectedTab = SharedPref.getMapSelectedTab(TagCustSelectionList.this);
        SfType = SharedPref.getSfType(TagCustSelectionList.this);
        SfCode = SharedPref.getSfCode(TagCustSelectionList.this);
        SfName = SharedPref.getSfName(TagCustSelectionList.this);
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(TagCustSelectionList.this);


      /*  Bundle extra = getIntent().getExtras();
        if (extra != null) {
            pos = extra.getInt("cus_pos");
        }*/


        if (SfType.equalsIgnoreCase("1")) {
            AddCustList(SfCode);
            binding.txtSelectedHq.setText(SfName);
            binding.txtSelectedHq.setEnabled(false);
        } else {
            binding.txtSelectedHq.setEnabled(false);
            binding.txtSelectedHq.setEnabled(true);
            if (SharedPref.getTpBasedDcr(TagCustSelectionList.this).equalsIgnoreCase("0")) {
                binding.txtSelectedHq.setEnabled(false);
            }
            SetHqAdapter();
        }


        binding.txtSelectedHq.setOnClickListener(view -> {
            if (binding.hqListView.getVisibility() == View.VISIBLE) {
                binding.hqListView.setVisibility(View.GONE);
            } else {
                binding.hqListView.setVisibility(View.VISIBLE);
            }
        });

        binding.searchCust.addTextChangedListener(new TextWatcher() {
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

        binding.ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(TagCustSelectionList.this, MapsActivity.class);
            intent.putExtra("from", "not_tagging");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.hqListView.setOnItemClickListener((adapterView, view, i, l) -> {
            binding.txtSelectedHq.setText(HqNameList.get(i));
            binding.hqListView.setVisibility(View.GONE);
            SelectedHqCode = HqCodeList.get(i);
            SharedPref.setSelectedHqCode(TagCustSelectionList.this, SelectedHqCode);
            SharedPref.setSelectedHqName(TagCustSelectionList.this, HqNameList.get(i));
            AddCustList(SelectedHqCode);
        });
    }

    private void SetHqAdapter() {

        HqNameList.clear();
        HqCodeList.clear();
        SelectedHqCode = SharedPref.getSelectedHqCode(TagCustSelectionList.this);
        Log.v("dddd", "-1sds-" + SelectedHqCode);
        try {
          /*  Cursor curHqList = sqLiteHandler.select_master_list("Subordinate");
            if (curHqList.getCount() > 0) {
                while (curHqList.moveToNext()) {
                    getHqList = curHqList.getString(1);
                }
            }

            JSONArray jsonArray = new JSONArray(getHqList);*/

            JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Subordinate");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonHQList = jsonArray.getJSONObject(i);
                if (SelectedHqCode.equalsIgnoreCase(jsonHQList.getString("id"))) {
                    if (HqCodeList.size() != 0) {
                        HqNameList.add(jsonHQList.getString("name"));
                        HqCodeList.add(jsonHQList.getString("id"));
                    } else {
                        HqNameList.add(jsonHQList.getString("name"));
                        HqCodeList.add(jsonHQList.getString("id"));
                    }
                } else {
                    HqNameList.add(jsonHQList.getString("name"));
                    HqCodeList.add(jsonHQList.getString("id"));
                }
            }

            binding.hqListView.setAdapter(new ArrayAdapter<>(TagCustSelectionList.this, R.layout.listview_items, HqNameList));
            binding.txtSelectedHq.setText(SharedPref.getSelectedHqName(TagCustSelectionList.this));
            if (SelectedHqCode.isEmpty()) {
                SelectedHqCode = HqCodeList.get(0);
            }
            // SelectedHqCode = HqCodeList.get(0);
            AddCustList(SelectedHqCode);

        } catch (Exception e) {

        }
    }

    private void AddCustList(String selectedHqCode) {
        custListArrayList.clear();
        Log.v("map_selected_tab", "---" + SharedPref.getMapSelectedTab(TagCustSelectionList.this));
        Log.v("selected_hq", "---" + SelectedHqCode);

        switch (SelectedTab) {
            case "D":
                try {
                    binding.tagSelection.setText(SharedPref.getCaptionDr(TagCustSelectionList.this));
                  /*  mCursor = sqLiteHandler.select_master_list("Doctor_" + selectedHqCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Doctor_" + selectedHqCode);
                    Log.v("jsonArray", "--" + jsonArray.length() + "---" + jsonArray);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Doctor", "getdoctors", SfCode, SharedPref.getDivisionCode(TagCustSelectionList.this), selectedHqCode, SfType, SharedPref.getDesignationName(TagCustSelectionList.this), SharedPref.getStateCode(TagCustSelectionList.this), SharedPref.getSubdivCode(TagCustSelectionList.this));
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"),SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                    }
                } catch (Exception e) {
                    Log.v("dr_tag", "---error--" + e);
                }

                break;
            case "C":
                try {
                    binding.tagSelection.setText(SharedPref.getCaptionChemist(TagCustSelectionList.this));
                  /*  mCursor = sqLiteHandler.select_master_list("Chemist_" + selectedHqCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Chemist_" + selectedHqCode);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Chemist", "getchemist", SfCode, SharedPref.getDivisionCode(TagCustSelectionList.this), selectedHqCode, SfType, SharedPref.getDesignationName(TagCustSelectionList.this), SharedPref.getStateCode(TagCustSelectionList.this), SharedPref.getSubdivCode(TagCustSelectionList.this));
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"),SelectedTab, "Category", "Specialty",
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                    }
                } catch (Exception e) {

                }
                break;
            case "S":
                try {
                    binding.tagSelection.setText(SharedPref.getCaptionStockist(TagCustSelectionList.this));
               /*     mCursor = sqLiteHandler.select_master_list("Stockiest_" + selectedHqCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }
                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Stockiest_" + selectedHqCode);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Stockiest", "getstockist", SfCode, SharedPref.getDivisionCode(TagCustSelectionList.this), selectedHqCode, SfType, SharedPref.getDesignationName(TagCustSelectionList.this), SharedPref.getStateCode(TagCustSelectionList.this), SharedPref.getSubdivCode(TagCustSelectionList.this));
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"),SelectedTab, "Category", "Specialty",
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                    }
                } catch (Exception e) {

                }
                break;
            case "U":
                try {
                    binding.tagSelection.setText(SharedPref.getCaptionUnDr(TagCustSelectionList.this));
                  /*  mCursor = sqLiteHandler.select_master_list("Unlisted_Doctor_" + selectedHqCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);*/
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Unlisted_Doctor_" + selectedHqCode);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Unlisted_Doctor", "getunlisteddr", SfCode, SharedPref.getDivisionCode(TagCustSelectionList.this), selectedHqCode, SfType, SharedPref.getDesignationName(TagCustSelectionList.this), SharedPref.getStateCode(TagCustSelectionList.this), SharedPref.getSubdivCode(TagCustSelectionList.this));
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"),SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"),
                                jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                    }
                } catch (Exception e) {

                }
                break;
        }

        custListAdapter = new CustListAdapter(TagCustSelectionList.this, TagCustSelectionList.this, custListArrayList);
        binding.rvCustList.setItemAnimator(new DefaultItemAnimator());
        binding.rvCustList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        binding.rvCustList.setAdapter(custListAdapter);
        custListAdapter.notifyDataSetChanged();
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