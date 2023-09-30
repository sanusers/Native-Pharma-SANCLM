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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.MapDcrSelectionBinding;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class TagCustSelectionList extends AppCompatActivity {

    MapDcrSelectionBinding binding;
    List<String> HqNameList = new ArrayList<>();
    List<String> HqCodeList = new ArrayList<>();
    CustListAdapter custListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    SQLiteHandler sqLiteHandler;
    String getCustListDB, SelectedTab, SfType, SfCode, SfName, SelectedHqCode, getHqList, TodayPlanSfCode;
    Cursor mCursor;

    @Override
    public void onBackPressed() {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        commonUtilsMethods = new CommonUtilsMethods(this);
        sqLiteHandler = new SQLiteHandler(this);
        commonUtilsMethods.FullScreencall();
        SelectedTab = SharedPref.getMapSelectedTab(TagCustSelectionList.this);
        SfType = SharedPref.getSfType(TagCustSelectionList.this);
        SfCode = SharedPref.getSfCode(TagCustSelectionList.this);
        SfName = SharedPref.getSfName(TagCustSelectionList.this);
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(TagCustSelectionList.this);
        sqLiteHandler.open();

        if (SfType.equalsIgnoreCase("1")) {
            AddCustList(SfCode);
            binding.txtSelectedHq.setText(SfName);
            binding.txtSelectedHq.setEnabled(false);
        } else {
            binding.txtSelectedHq.setEnabled(true);
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
            startActivity(intent);
        });

        binding.hqListView.setOnItemClickListener((adapterView, view, i, l) -> {
            SelectedHqCode = HqCodeList.get(i);
            binding.txtSelectedHq.setText(HqNameList.get(i));
            binding.hqListView.setVisibility(View.GONE);
            AddCustList(SelectedHqCode);
        });
    }

    private void SetHqAdapter() {
        HqNameList.clear();
        HqCodeList.clear();
        try {
            Cursor curHqList = sqLiteHandler.select_master_list("Subordinate");
            if (curHqList.getCount() > 0) {
                while (curHqList.moveToNext()) {
                    getHqList = curHqList.getString(1);
                }
            }

            JSONArray jsonArray = new JSONArray(getHqList);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonHQList = jsonArray.getJSONObject(i);
                if (TodayPlanSfCode.equalsIgnoreCase(jsonHQList.getString("id"))) {
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
            binding.txtSelectedHq.setText(HqNameList.get(0));
            SelectedHqCode = HqCodeList.get(0);
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
                    mCursor = sqLiteHandler.select_master_list("Doctor_"+selectedHqCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"),jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"),
                                jsonObject.getString("Town_Name"),jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap")));
                   ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ }
                } catch (Exception e) {
                    Log.v("dr_tag", "---error--" + e);
                }

                break;
            case "C":
                try {
                    binding.tagSelection.setText(SharedPref.getCaptionChemist(TagCustSelectionList.this));
                    mCursor = sqLiteHandler.select_master_list("Chemist_"+selectedHqCode);
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
                    binding.tagSelection.setText(SharedPref.getCaptionStockist(TagCustSelectionList.this));
                    mCursor = sqLiteHandler.select_master_list("Stockiest_"+selectedHqCode);
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
                    binding.tagSelection.setText(SharedPref.getCaptionUnDr(TagCustSelectionList.this));
                    mCursor = sqLiteHandler.select_master_list("Unlisted_Doctor_"+selectedHqCode);
                    if (mCursor.getCount() > 0) {
                        while (mCursor.moveToNext()) {
                            getCustListDB = mCursor.getString(1);
                        }
                    }

                    JSONArray jsonArray = new JSONArray(getCustListDB);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"),jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"),
                                jsonObject.getString("Town_Name"),jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap")));
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