/*
package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.deviation;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.tpDataObj;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.workDayCode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;

public class ListedDoctorFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static ListView filterList;
    public static ConstraintLayout constraintFilter;
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    ArrayList<CustList> filteredNames = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close, img_del, img_drop_down;
    TextView tv_hqName, tv_add_condition, tv_filterCount, noDoctor;
    Button btn_apply, btn_clear;
    String specialityCode = "", categoryCode = "", territoryCode = "", classCode = "";
    String specialityName = "", categoryName = "", territoryName = "", className = "";

    ListView lv_spec, lv_cate, lv_terr, lv_class;
    JSONArray jsonArray;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    ArrayList<String> listOfItems = new ArrayList<>();
    TextView tvSpec, tvCate, tvTerritory, tvClass;
    JSONObject jsonObject;
    ApiInterface apiInterface;
    ConstraintLayout constraintLayout;
    int count = 0;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private String STPNeed, STPBasedMTP, STPBasedDCR, TPNeed, TPMandatory, TPBasedDCR, TPDCRDeviation;
    private final DcrCallTabLayoutActivity.HQChangeListener hqChangeListener;

    public ListedDoctorFragment(DcrCallTabLayoutActivity.HQChangeListener hqChangeListener) {
        this.hqChangeListener = hqChangeListener;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "doctor");
        View v = inflater.inflate(R.layout.fragment_listed_doctor, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        img_drop_down = v.findViewById(R.id.img_drop_down);
        filterList = v.findViewById(R.id.filter_list_view);
        constraintFilter = v.findViewById(R.id.constraint_filter_selection_list);
        tv_filterCount = v.findViewById(R.id.tv_filter_count);
        noDoctor = v.findViewById(R.id.no_doctor);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        getRequiredData();

        custListArrayList.clear();
        SetupAdapter();

        iv_filter.setOnClickListener(view -> {
            CustomizeFiltered();
        });
        ed_search.addTextChangedListener(new TextWatcher() {
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

//        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
//            if (!SharedPref.getMultiHQCode(requireContext()).isEmpty()) {
//                String[] hqCodes = SharedPref.getMultiHQCode(requireContext()).split(",");
//                if (hqCodes.length > 1) {
//                    img_drop_down.setVisibility(View.VISIBLE);
//                } else {
//                    img_drop_down.setVisibility(View.GONE);
//                }
//            }
//            if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
//                if ((WorkPlanFragment.mHQCode1 != null && !WorkPlanFragment.mHQCode1.isEmpty() && WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F"))
//                        && (WorkPlanFragment.mHQCode2 != null && !WorkPlanFragment.mHQCode2.isEmpty() && WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))) {
//                    img_drop_down.setVisibility(View.VISIBLE);
//                } else {
//                    img_drop_down.setVisibility(View.GONE);
//                }
//                tv_hqName.setOnClickListener(view -> {
//                    try {
//                        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
//                        ArrayList<String> list = new ArrayList<>();
//
//                        if (jsonArray.length() > 0) {
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                if ((WorkPlanFragment.mHQCode1 != null && WorkPlanFragment.mHQCode1.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F"))
//                                        || (WorkPlanFragment.mHQCode2 != null && WorkPlanFragment.mHQCode2.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))) {
//                                    list.add(jsonObject.getString("name"));
//                                }
//                            }
//                        }
//
//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
//                        View dialogView = inflater.inflate(R.layout.dialog_listview, null);
//                        alertDialog.setView(dialogView);
//                        TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
//                        ListView listView = dialogView.findViewById(R.id.listView);
//                        SearchView searchView = dialogView.findViewById(R.id.searchET);
//
//                        headerTxt.setText(getResources().getText(R.string.select_hq));
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, list);
//                        listView.setAdapter(adapter);
//                        AlertDialog dialog = alertDialog.create();
//
//                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                            @Override
//                            public boolean onQueryTextSubmit(String s) {
//                                adapter.getFilter().filter(s);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onQueryTextChange(String s) {
//                                adapter.getFilter().filter(s);
//                                return false;
//                            }
//                        });
//
//                        listView.setOnItemClickListener((adapterView, view1, position, l) -> {
//                            String selectedHq = listView.getItemAtPosition(position).toString();
//                            tv_hqName.setText(selectedHq);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                try {
//                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                    if (jsonObject.getString("name").equalsIgnoreCase(selectedHq)) {
//                                        DcrCallTabLayoutActivity.TodayPlanSfCode = jsonObject.getString("id");
//                                        DcrCallTabLayoutActivity.TodayPlanSfName = jsonObject.getString("name");
//                                        SharedPref.saveHq(requireContext(), DcrCallTabLayoutActivity.TodayPlanSfName, DcrCallTabLayoutActivity.TodayPlanSfCode);
//                                        break;
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            hqChangeListener.onHQChange();
//                            dialog.dismiss();
//                        });
//
//                        alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
//
//                        dialog.show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    UtilityClass.hideKeyboard(requireActivity());
//                });
//            } else {
//                tv_hqName.setOnClickListener(view -> {
//                    try {
//                        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
//                        ArrayList<String> list = new ArrayList<>();
//
//                        if (jsonArray.length() > 0) {
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                if (SharedPref.getMultiHQCode(requireContext()).contains(jsonObject.optString("id"))) {
//                                    list.add(jsonObject.optString("name"));
//                                }
//                            }
//                        }
//
//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
//                        View dialogView = inflater.inflate(R.layout.dialog_listview, null);
//                        alertDialog.setView(dialogView);
//                        TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
//                        ListView listView = dialogView.findViewById(R.id.listView);
//                        SearchView searchView = dialogView.findViewById(R.id.searchET);
//
//                        headerTxt.setText(getResources().getText(R.string.select_hq));
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, list);
//                        listView.setAdapter(adapter);
//                        AlertDialog dialog = alertDialog.create();
//
//                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                            @Override
//                            public boolean onQueryTextSubmit(String s) {
//                                adapter.getFilter().filter(s);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onQueryTextChange(String newText) {
//                                List<String> filteredList = new ArrayList<>();
//
//                                if (newText == null || newText.trim().isEmpty()) {
//                                    filteredList.addAll(list);
//                                } else {
//                                    for (String item : list) {
//                                        if (item.toLowerCase().contains(newText.toLowerCase())) {
//                                            filteredList.add(item);
//                                        }
//                                    }
//                                }
//
//                                ArrayAdapter<String> tempAdapter = new ArrayAdapter<>(
//                                        requireContext(),
//                                        android.R.layout.simple_list_item_1,
//                                        filteredList
//                                );
//                                listView.setAdapter(tempAdapter);
//
//                                return true;
//                            }
//                        });
//
//                        listView.setOnItemClickListener((adapterView, view1, position, l) -> {
//                            String selectedHq = listView.getItemAtPosition(position).toString();
//                            tv_hqName.setText(selectedHq);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                try {
//                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                    if (jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
//                                        DcrCallTabLayoutActivity.TodayPlanSfCode = jsonObject.optString("id");
//                                        DcrCallTabLayoutActivity.TodayPlanSfName = jsonObject.optString("name");
//                                        SharedPref.saveHq(requireContext(), DcrCallTabLayoutActivity.TodayPlanSfName, DcrCallTabLayoutActivity.TodayPlanSfCode);
//                                        break;
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
////                        DcrCallTabLayoutActivity.prepareClusterList(requireActivity());
////                        SetupAdapter();
//                            hqChangeListener.onHQChange();
//                            dialog.dismiss();
//                        });
//
//                        alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
//
//                        dialog.show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    UtilityClass.hideKeyboard(requireActivity());
//                });
//            }
//        }
        HQSelector.setupHQSelector(
                this,
                tv_hqName,
                img_drop_down,
                masterDataDao,
                getLayoutInflater(),
                hqChangeListener::onHQChange
        );

        return v;
    }

    private void getRequiredData() {
        STPNeed = SharedPref.getStpNeed(requireContext());
        STPBasedMTP = SharedPref.getStpBasedMtp(requireContext());
        STPBasedDCR = SharedPref.getStpBasedDcr(requireContext());
        TPNeed = SharedPref.getTpNeed(requireContext());
        TPMandatory = SharedPref.getTpMandatoryNeed(requireContext());
        TPBasedDCR = SharedPref.getTpbasedDcr(requireContext());
        TPDCRDeviation = SharedPref.getTpdcrDeviation(requireContext());
    }

    public void CustomizeFiltered() {

        dialogFilter = new Dialog(requireContext());
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        dialogFilter.show();
        ed_search = dialogFilter.findViewById(R.id.search_cust);
        img_close = dialogFilter.findViewById(R.id.img_close);
        img_del = dialogFilter.findViewById(R.id.img_del);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        tv_add_condition = dialogFilter.findViewById(R.id.btn_add_condition);
        tvSpec = dialogFilter.findViewById(R.id.constraint_speciality);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);
        tvCate = dialogFilter.findViewById(R.id.constraint_category);
        tvClass = dialogFilter.findViewById(R.id.constraint_class);

        lv_spec = dialogFilter.findViewById(R.id.lv_spec);
        lv_cate = dialogFilter.findViewById(R.id.lv_category);
        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        lv_class = dialogFilter.findViewById(R.id.lv_class);
        tvSpec.setVisibility(View.VISIBLE);
        tvCate.setVisibility(View.VISIBLE);
        tv_add_condition.setVisibility(View.VISIBLE);
        img_del.setVisibility(View.GONE);

        if (!territoryCode.isEmpty()) {
            tvTerritory.setVisibility(View.VISIBLE);
            img_del.setVisibility(View.VISIBLE);
        } else {
            tvTerritory.setVisibility(View.GONE);
            img_del.setVisibility(View.GONE);
        }

        if (!classCode.isEmpty()) {
            if (territoryCode.isEmpty()) {
                tvTerritory.setVisibility(View.INVISIBLE);
            } else {
                tv_add_condition.setVisibility(View.GONE);
            }
            tvClass.setVisibility(View.VISIBLE);
        } else {
            tvClass.setVisibility(View.GONE);
        }

        constraintLayout = dialogFilter.findViewById(R.id.constraint_btns);
        img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

        btn_clear.setOnClickListener(view15 -> {
            specialityCode = "";
            territoryCode = "";
            categoryCode = "";
            classCode = "";
            specialityName = "";
            territoryName = "";
            categoryName = "";
            className = "";
            tvSpec.setText("");
            tvTerritory.setText("");
            tvCate.setText("");
            tvClass.setText("");
            tvSpec.setHint(R.string.speciality);
            tvTerritory.setHint(R.string.territory);
            tvCate.setHint(R.string.category);
            tvClass.setHint(R.string.class_filter);
        });

        tvSpec.setText(specialityName);
        tvTerritory.setText(territoryName);
        tvCate.setText(categoryName);
        tvClass.setText(className);

        tv_add_condition.setOnClickListener(view13 -> {
            if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                tvClass.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.GONE);
            } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE) {
                tvTerritory.setVisibility(View.VISIBLE);
                img_del.setVisibility(View.VISIBLE);
            }
        });

        img_del.setOnClickListener(view14 -> {
            if (!classCode.isEmpty()) {
                classCode = "";
                className = "";
            } else if (!territoryCode.isEmpty()) {
                territoryCode = "";
                territoryName = "";
            }
            if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvClass.getVisibility() == View.INVISIBLE) {
                tvTerritory.setVisibility(View.GONE);
                img_del.setVisibility(View.GONE);
                tvTerritory.setHint(R.string.territory);
            } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                tvClass.setVisibility(View.INVISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
                tvClass.setHint(R.string.class_filter);
            }
        });

        tvSpec.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if (lv_spec.getVisibility() == View.VISIBLE) {
                ed_search.setVisibility(View.GONE);
                lv_spec.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Speciality");

                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    specialityCode = clickedItem.getCode();
                    specialityName = clickedItem.getName();
                    tvSpec.setText(clickedItem.getName());
                    lv_spec.setVisibility(View.GONE);
//                    ed_search.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_spec.setAdapter(arrayAdapter);
                lv_spec.setVisibility(View.VISIBLE);
//                ed_search.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvCate.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_spec.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if (lv_cate.getVisibility() == View.VISIBLE) {
                lv_cate.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Category");
                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    categoryCode = clickedItem.getCode();
                    categoryName = clickedItem.getName();
                    tvCate.setText(clickedItem.getName());
                    lv_cate.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_cate.setAdapter(arrayAdapter);
                lv_cate.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvTerritory.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_spec.setVisibility(View.GONE);
            if (lv_terr.getVisibility() == View.VISIBLE) {
                lv_terr.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Territory");
                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    territoryCode = clickedItem.getCode();
                    territoryName = clickedItem.getName();
                    tvTerritory.setText(clickedItem.getName());
                    lv_terr.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_terr.setAdapter(arrayAdapter);
                lv_terr.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvClass.setOnClickListener(view -> {
            lv_spec.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if (lv_class.getVisibility() == View.VISIBLE) {
                lv_class.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Class");
                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    classCode = clickedItem.getCode();
                    className = clickedItem.getName();
                    tvClass.setText(clickedItem.getName());
                    lv_class.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_class.setAdapter(arrayAdapter);
                lv_class.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        btn_apply.setOnClickListener(view1 -> {
            Filtered();
        });

    }

    private void getFilterList(String requiredList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (requiredList.equalsIgnoreCase("Speciality")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Category")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Territory")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Class")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
            }
            filterSelectionList.clear();
            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(jsonObject.optString("Name"), jsonObject.optString("Code")));
            }

        } catch (Exception ignored) {

        }
    }

    public void SetupAdapter() {
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        custListArrayList.clear();
        if (SharedPref.getGeotagNeed(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
            try {
                JSONArray masterJsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                JSONArray masterJsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_GEO + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                HashMap<String, JSONObject> docObj = new HashMap<>();
                for (int i = 0; i < masterJsonArray1.length(); i++) {
                    JSONObject jsonObject = masterJsonArray1.getJSONObject(i);
                    String code = jsonObject.optString("Code");
                    if (!code.isEmpty()) {
                        docObj.put(code, jsonObject);
                    } else {
                        Log.d("Merge", "Skipping DOCTOR_MAS object with empty 'Code': " + jsonObject.toString());
                    }
                }
                for (int i = 0; i < masterJsonArray2.length(); i++) {
                    JSONObject jsonObject_geo = masterJsonArray2.getJSONObject(i);
                    String code = jsonObject_geo.optString("Code");
                    if (code.isEmpty()) {
                        Log.w("Merge", "Skipping GEO object with empty 'Code': " + jsonObject_geo.toString());
                        continue;
                    }
                    if (docObj.containsKey(code)) {
                        JSONObject existingObject = docObj.get(code);
                        for (java.util.Iterator<String> it = jsonObject_geo.keys(); it.hasNext(); ) {
                            String key = it.next();
                            try {
                                assert existingObject != null;
                                existingObject.put(key, jsonObject_geo.get(key));
                            } catch (JSONException e) {
                                Log.e("MergeError", "Error merging key " + key + " for code " + code + ": " + e.getMessage());
                            }
                        }
                    }
                }
                List<JSONObject> sortedList = new ArrayList<>(docObj.values());


                Collections.sort(sortedList, (o1, o2) -> {
                    String name1 = o1.optString("Name", "");
                    String name2 = o2.optString("Name", "");
                    return name1.compareToIgnoreCase(name2);
                });


                jsonArray = new JSONArray(docObj.values());
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    try {
                        if (SharedPref.getGeotagNeed(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            if (!jsonObject.optString("lat").isEmpty() && !jsonObject.optString("long").isEmpty()) {
                                if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
                                    Log.v("DrCall", "111");
                                    float[] distance = new float[2];
                                    Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                    if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                        if (jsonObject.optString("cust_status").equalsIgnoreCase("0")) {
                                            custListArrayList = SaveData(jsonObject, i, true);
                                        }
                                    }
                                } else {
                                    Log.v("DrCall", "222");
                                    float[] distance = new float[2];
                                    Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                    if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                        custListArrayList = SaveData(jsonObject, i, true);
                                    }
                                }
                            }
                        } else {
                            Log.v("DrCall", "333");

                            // This not need TbBased DCR
//                        if (SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
//                            Log.v("DrCall", "444");
//                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
//                                custListArrayList = SaveData(jsonObject, i);
//                            }
//                        } else {
//                            Log.v("DrCall", "555");
//                            custListArrayList = SaveData(jsonObject, i);
//                        }

                            custListArrayList = SaveData(jsonObject, i, false);
                        }


                    } catch (Exception e) {
                        Log.v("DrCall", "dr--error-1-" + e);
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < count; i++) {
//                    for (int j = i + 1; j < count; j++) {
//                        if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
//                            custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getclass(), custListArrayList.get(i).getClassCode(), custListArrayList.get(i).isClusterAvailable()));
//                            custListArrayList.remove(j--);
//                            count--;
//                        } else {
                    custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getclass(), custListArrayList.get(i).getClassCode(), custListArrayList.get(i).isClusterAvailable()));
//                        }
//                    }
                }
            } catch (Exception e) {
                Log.v("DrCall", "-dr--error-2-" + e);
            }
            Log.v("call", "-dr--size--" + custListArrayList.size());

        } else {
            try {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

                Log.d("hqSfcoe", DcrCallTabLayoutActivity.TodayPlanSfCode);

                if (jsonArray.length() == 0) {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_data_found) + "  " + context.getString(R.string.do_master_sync));
                }

                Log.v("DrCall", "-dr_full_length-" + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    try {
                        if (SharedPref.getGeotagNeed(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            if (!jsonObject.optString("Lat").isEmpty() && !jsonObject.optString("Long").isEmpty()) {
                                if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
                                    Log.v("DrCall", "111");
                                    float[] distance = new float[2];
                                    Location.distanceBetween(Double.parseDouble(jsonObject.optString("Lat")), Double.parseDouble(jsonObject.optString("Long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                    if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                        if (jsonObject.optString("cust_status").equalsIgnoreCase("0")) {
                                            custListArrayList = SaveData(jsonObject, i, true);
                                        }
                                    }
                                } else {
                                    Log.v("DrCall", "222");
                                    float[] distance = new float[2];
                                    Location.distanceBetween(Double.parseDouble(jsonObject.optString("Lat")), Double.parseDouble(jsonObject.optString("Long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                    if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                        custListArrayList = SaveData(jsonObject, i, true);
                                    }
                                }
                            }
                        } else {
                            Log.v("DrCall", "333");

                            // This not need TbBased DCR
//                        if (SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
//                            Log.v("DrCall", "444");
//                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
//                                custListArrayList = SaveData(jsonObject, i);
//                            }
//                        } else {
//                            Log.v("DrCall", "555");
//                            custListArrayList = SaveData(jsonObject, i);
//                        }

                            custListArrayList = SaveData(jsonObject, i, false);
                        }
                    } catch (Exception e) {
                        Log.v("DrCall", "dr--error-1-" + e);
                    }
               */
/* switch (RequiredFiltered) {
                    case "Speciality":
                        if (filteredStr.equalsIgnoreCase(jsonObject.optString("Specialty"))) {
                            AssignData(i);
                        }
                        break;
                    case "Category":
                        if (filteredStr.equalsIgnoreCase(jsonObject.optString("Category"))) {
                            AssignData(i);
                        }
                        break;
                    case "Territory":
                        if (filteredStr.equalsIgnoreCase(jsonObject.optString("Town_Name"))) {
                            AssignData(i);
                        }
                        break;
                    case "":
                        AssignData(i);
                        break;
                }*//*

                }

                int count = custListArrayList.size();
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                            custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).getclass(), custListArrayList.get(i).getClassCode(), custListArrayList.get(i).isClusterAvailable()));
                            custListArrayList.remove(j--);
                            count--;
                        } else {
                            custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).getclass(), custListArrayList.get(i).getClassCode(), custListArrayList.get(i).isClusterAvailable()));
                        }
                    }
                }
            } catch (Exception e) {
                Log.v("DrCall", "-dr--error-2-" + e);
                e.printStackTrace();
            }
            Log.v("call", "-dr--size--" + custListArrayList.size());
        }
        FilltercustArraList.clear();
        FilltercustArraList.addAll(custListArrayList);

        if (FilltercustArraList.isEmpty()) {
            noDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getDrCap(requireContext()), getString(R.string.found)));
            noDoctor.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noDoctor.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, SharedPref.getCustSrtNd(requireContext()), "1");
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
            rv_list.setAdapter(adapterDCRCallSelection);
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i, boolean isFenced) {
        try {
            String brands = getBrands(jsonObject.optString("MappProds"));
            if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) && !stpOfflineDataDao.isNotApproved()))
                    && !deviation.equalsIgnoreCase("1")) {
                if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                    STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                    List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
                    Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
                    if (!drList.isEmpty()) {
                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.optString("Code")))) {
                            prepareData(jsonObject, i, brands, false);
                        }
                    }
                } else if (tpDataObj != null) {
//                    Type type = new TypeToken<ModelClass>() {
//                    }.getType();
//                    ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
//                    int fwSession = -1;
//                    if(!modelClass.getSessionList().isEmpty() && modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(0).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)){
//                        fwSession = 0;
//                    } else if((modelClass.getSessionList().size() > 1) && modelClass.getSessionList().get(1).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(1).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)) {
//                        fwSession = 1;
//                    }
//                    if(fwSession != -1) {
//                        for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(fwSession).getListedDr()) {
//                            drList.add(subClass.getCode());
//                        }
//                        Log.i("TP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
//                        if(!drList.isEmpty()) {
//                            if(SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.optString("Code")))) {
//                                prepareData(jsonObject, i, brands, false);
//                            }
//                        } else {
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
//                            } else {
//                                prepareData(jsonObject, i, brands, true)
                    }
//                        }
//                    } else {
//                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
//                            prepareData(jsonObject, i, brands, false);
//                        } else {
//                            prepareData(jsonObject, i, brands, true);
//                        }
//                    }
                } else {
                    Log.d("TAG", "SaveData: 111");
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
                    } else {
                        prepareData(jsonObject, i, brands, true);
                    }
                    Log.d("TAG", "SaveData: 2");
                }
                Log.d("TAG", "SaveData: 3");
            } else if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1"))))
                    && deviation.equalsIgnoreCase("1")) {
//                STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
//                List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
//                Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
//                if(!drList.isEmpty()) {
//                    if(SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.optString("Code")))) {
//                        prepareData(jsonObject, i, brands, false);
//                    }
//                } else
                if (isFenced) {
                    if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                        List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
                        Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
                        if (!drList.isEmpty()) {
                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.optString("Code")))) {
                                prepareData(jsonObject, i, brands, false);
                            }
                        }
                    } else if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
                    }
                } else {
                    if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                        List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
                        Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
                        if (!drList.isEmpty()) {
                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.optString("Code")))) {
                                prepareData(jsonObject, i, brands, false);
                            }
                        }
                    } else if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
                    } else {
                        prepareData(jsonObject, i, brands, true);
                    }
                }
            } else if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
                prepareData(jsonObject, i, brands, false);
            } else {
                prepareData(jsonObject, i, brands, true);
            }
            Log.v("DrCall", "--brands---" + brands);
        } catch (Exception e) {
            Log.v("DrCall", "--1111---" + e.toString());
            e.printStackTrace();
        }
        return custListArrayList;
    }

    private void prepareData(JSONObject jsonObject, int i, String brands, boolean isClusterAvailable) {
        if (SharedPref.getGeotagNeed(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
            if (jsonObject.has("Product_Code")) {
                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("SpecialtyCode"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagedCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("HosAddr"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), jsonObject.optString("Product_Code"), brands, jsonObject.optString("MProd"), jsonObject.optString("Tlvst"), jsonObject.optString("Doc_Class_ShortName"), jsonObject.optString("Doc_ClsCode"), isClusterAvailable));
            } else {
                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("SpecialtyCode"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagedCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("HosAddr"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), "", brands, jsonObject.optString("MProd"), jsonObject.optString("Tlvst"), jsonObject.optString("Doc_Class_ShortName"), jsonObject.optString("Doc_ClsCode"), isClusterAvailable));
            }
        } else {
            if (jsonObject.has("Product_Code")) {
                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("SpecialtyCode"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), "", "", String.valueOf(i), "", "", jsonObject.optString("HosAddr"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), jsonObject.optString("Product_Code"), brands, jsonObject.optString("MProd"), jsonObject.optString("Tlvst"), jsonObject.optString("Doc_Class_ShortName"), jsonObject.optString("Doc_ClsCode"), isClusterAvailable));
            } else {
                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("SpecialtyCode"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), "", "", String.valueOf(i), "", "", jsonObject.optString("HosAddr"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), "", brands, jsonObject.optString("MProd"), jsonObject.optString("Tlvst"), jsonObject.optString("Doc_Class_ShortName"), jsonObject.optString("Doc_ClsCode"), isClusterAvailable));

            }

        }
    }

    public String getBrands(String mappProds) {
        String subString = "";
        String[] StrArray = mappProds.split(",");
        StringBuilder ss1 = new StringBuilder();

        for (String value : StrArray) {
            int iEnd = value.indexOf("-");
            if (iEnd != -1) {
                ss1.append(value.substring(0, iEnd));
                ss1.append(",");
            }
        }

        subString = ss1.toString();
        return subString;
    }

  */
/*  private void AssignData(int i) {
        try {
            Log.v("CheckSelCall", "--dr-" + GeoTagApproval + "--" + DrGeoTag + "----" + TpBasedDcr);
            if (DrGeoTag.equalsIgnoreCase("1")) {
                if (!jsonObject.optString("Lat").isEmpty() && !jsonObject.optString("Long").isEmpty()) {
                    if (GeoTagApproval.equalsIgnoreCase("0")) {
                        Log.v("CheckSelCall", "111");
                        float[] distance = new float[2];
                        Location.distanceBetween(Double.parseDouble(jsonObject.optString("Lat")), Double.parseDouble(jsonObject.optString("Long")), lat, lng, distance);
                        if (distance[0] < limitKm * 1000.0) {
                            if (jsonObject.optString("cust_status").equalsIgnoreCase("0")) {
                                if (jsonObject.has("Product_Code")) {
                                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), jsonObject.optString("Product_Code")));
                                } else {
                                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), ""));
                                }
                            }
                        }
                    } else {
                        Log.v("CheckSelCall", "222");
                        float[] distance = new float[2];
                        Location.distanceBetween(Double.parseDouble(jsonObject.optString("Lat")), Double.parseDouble(jsonObject.optString("Long")), lat, lng, distance);
                        if (distance[0] < limitKm * 1000.0) {
                            if (jsonObject.has("Product_Code")) {
                                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), jsonObject.optString("Product_Code")));
                            } else {
                                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), ""));
                            }
                        }
                    }
                }
            } else {
                Log.v("CheckSelCall", "333");
                if (TpBasedDcr.equalsIgnoreCase("0")) {
                    Log.v("CheckSelCall", "444");
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).equalsIgnoreCase(jsonObject.optString("Town_Code"))) {
                        if (jsonObject.has("Product_Code")) {
                            custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), jsonObject.optString("Product_Code")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), ""));
                        }
                    }
                } else {
                    Log.v("CheckSelCall", "555");
                    if (jsonObject.has("Product_Code")) {
                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), jsonObject.optString("Product_Code")));
                    } else {
                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "1", jsonObject.optString("Category"), jsonObject.optString("CategoryCode"), jsonObject.optString("Specialty"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("Lat"), jsonObject.optString("Long"), jsonObject.optString("Addrs"), jsonObject.optString("DOB"), jsonObject.optString("DOW"), jsonObject.optString("DrEmail"), jsonObject.optString("Mobile"), jsonObject.optString("Phone"), jsonObject.optString("DrDesig"), ""));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("call", "dr--error--" + e);
        }
    }*//*


    private void filter(String text) {
        filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filteredNames);
    }

    public void Filtered() {
        ArrayList<CustList> filterCusList = new ArrayList<>();
        if (!filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        } else {
            filterCusList.addAll(custListArrayList);
        }
        FilltercustArraList.clear();
        if (specialityCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("") && territoryCode.equalsIgnoreCase("") && classCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(filterCusList);
            tv_filterCount.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else {
            for (CustList mList : filterCusList) {
                if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && categoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && territoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && specialityCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else {
                    if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                            && specialityCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getClassCode().equalsIgnoreCase(classCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    }
                }
            }
            tv_filterCount.setText(String.valueOf(FilltercustArraList.size()));
        }

        if (FilltercustArraList.isEmpty()) {
            noDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getDrCap(requireContext()), getString(R.string.found)));
            noDoctor.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noDoctor.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection.filterList(FilltercustArraList);
        }
        dialogFilter.dismiss();
    }

}*/
package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.deviation;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.tpDataObj;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.workDayCode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;

public class ListedDoctorFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static ListView filterList;
    public static ConstraintLayout constraintFilter;


    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    ArrayList<CustList> filteredNames = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close, img_del, img_drop_down;
    TextView tv_hqName, tv_add_condition, tv_filterCount, noDoctor;
    Button btn_apply, btn_clear;
    String specialityCode = "", categoryCode = "", territoryCode = "", classCode = "";
    String specialityName = "", categoryName = "", territoryName = "", className = "";

    ListView lv_spec, lv_cate, lv_terr, lv_class;
    JSONArray jsonArray;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    ArrayList<String> listOfItems = new ArrayList<>();
    TextView tvSpec, tvCate, tvTerritory, tvClass;
    JSONObject jsonObject;
    ApiInterface apiInterface;
    ConstraintLayout constraintLayout;
    int count = 0;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private String STPNeed, STPBasedMTP, STPBasedDCR, TPNeed, TPMandatory, TPBasedDCR, TPDCRDeviation;
    private final DcrCallTabLayoutActivity.HQChangeListener hqChangeListener;

    public ListedDoctorFragment(DcrCallTabLayoutActivity.HQChangeListener hqChangeListener) {
        this.hqChangeListener = hqChangeListener;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "doctor");
        View view = inflater.inflate(R.layout.fragment_listed_doctor, container, false);
        rv_list = view.findViewById(R.id.rv_cust_list_selection);
        ed_search = view.findViewById(R.id.search_cust);
        iv_filter = view.findViewById(R.id.iv_filter);
        tv_hqName = view.findViewById(R.id.tv_hq_name);
        img_drop_down = view.findViewById(R.id.img_drop_down);
        filterList = view.findViewById(R.id.filter_list_view);
        constraintFilter = view.findViewById(R.id.constraint_filter_selection_list);
        tv_filterCount = view.findViewById(R.id.tv_filter_count);
        noDoctor = view.findViewById(R.id.no_doctor);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        getRequiredData();

        custListArrayList.clear();
        SetupAdapter();

        iv_filter.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                CustomizeFiltered();
            }
        });
        ed_search.addTextChangedListener(new TextWatcher() {
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

//        if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
//            tv_hqName.setOnClickListener(v -> {
//                try {
//                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
//                    ArrayList<String> list = new ArrayList<>();
//
//                    if(jsonArray.length()>0) {
//                        for (int i = 0; i<jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            if((WorkPlanFragment.mHQCode1 != null && WorkPlanFragment.mHQCode1.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F"))
//                                    || (WorkPlanFragment.mHQCode2 != null && WorkPlanFragment.mHQCode2.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))) {
//                                list.add(jsonObject.getString("name"));
//                            }
//                        }
//                    }
//
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
//                    View dialogView = inflater.inflate(R.layout.dialog_listview, null);
//                    alertDialog.setView(dialogView);
//                    TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
//                    ListView listView = dialogView.findViewById(R.id.listView);
//                    SearchView searchView = dialogView.findViewById(R.id.searchET);
//
//                    headerTxt.setText(getResources().getText(R.string.select_hq));
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, list);
//                    listView.setAdapter(adapter);
//                    AlertDialog dialog = alertDialog.create();
//
//                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                        @Override
//                        public boolean onQueryTextSubmit(String s) {
//                            adapter.getFilter().filter(s);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onQueryTextChange(String s) {
//                            adapter.getFilter().filter(s);
//                            return false;
//                        }
//                    });
//
//                    listView.setOnItemClickListener((adapterView, view1, position, l) -> {
//                        String selectedHq = listView.getItemAtPosition(position).toString();
//                        tv_hqName.setText(selectedHq);
//                        for (int i = 0; i<jsonArray.length(); i++) {
//                            try {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                if(jsonObject.getString("name").equalsIgnoreCase(selectedHq)) {
//                                    DcrCallTabLayoutActivity.TodayPlanSfCode = jsonObject.getString("id");
//                                    DcrCallTabLayoutActivity.TodayPlanSfName = jsonObject.getString("name");
//                                    SharedPref.saveHq(requireContext(), DcrCallTabLayoutActivity.TodayPlanSfName, DcrCallTabLayoutActivity.TodayPlanSfCode);
//                                    break;
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        hqChangeListener.onHQChange();
//                        dialog.dismiss();
//                    });
//
//                    alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
//
//                    dialog.show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                UtilityClass.hideKeyboard(requireActivity());
//            });
//        }
        HQSelector.setupHQSelector(
                this,
                requireContext(),
                tv_hqName,
                img_drop_down,
                masterDataDao,
                getLayoutInflater(),
                hqChangeListener::onHQChange
        );
        return view;
    }

    private void getRequiredData() {
        STPNeed = SharedPref.getStpNeed(requireContext());
        STPBasedMTP = SharedPref.getStpBasedMtp(requireContext());
        STPBasedDCR = SharedPref.getStpBasedDcr(requireContext());
        TPNeed = SharedPref.getTpNeed(requireContext());
        TPMandatory = SharedPref.getTpMandatoryNeed(requireContext());
        TPBasedDCR = SharedPref.getTpbasedDcr(requireContext());
        TPDCRDeviation = SharedPref.getTpdcrDeviation(requireContext());
    }

    public void CustomizeFiltered() {

        dialogFilter = new Dialog(requireContext());
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        dialogFilter.show();
        img_close = dialogFilter.findViewById(R.id.img_close);
        img_del = dialogFilter.findViewById(R.id.img_del);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        tv_add_condition = dialogFilter.findViewById(R.id.btn_add_condition);
        tvSpec = dialogFilter.findViewById(R.id.constraint_speciality);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);
        tvCate = dialogFilter.findViewById(R.id.constraint_category);
        tvClass = dialogFilter.findViewById(R.id.constraint_class);

        lv_spec = dialogFilter.findViewById(R.id.lv_spec);
        lv_cate = dialogFilter.findViewById(R.id.lv_category);
        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        lv_class = dialogFilter.findViewById(R.id.lv_class);
        tvSpec.setVisibility(View.VISIBLE);
        tvCate.setVisibility(View.VISIBLE);
        tv_add_condition.setVisibility(View.VISIBLE);
        img_del.setVisibility(View.GONE);

        if (!territoryCode.isEmpty()) {
            tvTerritory.setVisibility(View.VISIBLE);
            img_del.setVisibility(View.VISIBLE);
        } else {
            tvTerritory.setVisibility(View.GONE);
            img_del.setVisibility(View.GONE);
        }

        if (!classCode.isEmpty()) {
            if (territoryCode.isEmpty()) {
                tvTerritory.setVisibility(View.INVISIBLE);
            } else {
                tv_add_condition.setVisibility(View.GONE);
            }
            tvClass.setVisibility(View.VISIBLE);
        } else {
            tvClass.setVisibility(View.GONE);
        }

        constraintLayout = dialogFilter.findViewById(R.id.constraint_btns);
        img_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogFilter.dismiss();
            }
        });

        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                specialityCode = "";
                territoryCode = "";
                categoryCode = "";
                classCode = "";
                specialityName = "";
                territoryName = "";
                categoryName = "";
                className = "";
                tvSpec.setText("");
                tvTerritory.setText("");
                tvCate.setText("");
                tvClass.setText("");
                tvSpec.setHint(R.string.speciality);
                tvTerritory.setHint(R.string.territory);
                tvCate.setHint(R.string.category);
                tvClass.setHint(R.string.class_filter);
            }
        });

        tvSpec.setText(specialityName);
        tvTerritory.setText(territoryName);
        tvCate.setText(categoryName);
        tvClass.setText(className);

        tv_add_condition.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.GONE);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE) {
                    tvTerritory.setVisibility(View.VISIBLE);
                    img_del.setVisibility(View.VISIBLE);
                }
            }
        });

        img_del.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (!classCode.isEmpty()) {
                    classCode = "";
                    className = "";
                } else if (!territoryCode.isEmpty()) {
                    territoryCode = "";
                    territoryName = "";
                }
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvClass.getVisibility() == View.INVISIBLE) {
                    tvTerritory.setVisibility(View.GONE);
                    img_del.setVisibility(View.GONE);
                    tvTerritory.setHint(R.string.territory);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.INVISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    tvClass.setHint(R.string.class_filter);
                }
            }
        });

        tvSpec.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_class.setVisibility(View.GONE);
                lv_cate.setVisibility(View.GONE);
                lv_terr.setVisibility(View.GONE);
                if (lv_spec.getVisibility() == View.VISIBLE) {
                    lv_spec.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Speciality");

                    FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                        specialityCode = clickedItem.getCode();
                        specialityName = clickedItem.getName();
                        tvSpec.setText(clickedItem.getName());
                        lv_spec.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_spec.setAdapter(arrayAdapter);
                    lv_spec.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        tvCate.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_class.setVisibility(View.GONE);
                lv_spec.setVisibility(View.GONE);
                lv_terr.setVisibility(View.GONE);
                if (lv_cate.getVisibility() == View.VISIBLE) {
                    lv_cate.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Category");
                    FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                        categoryCode = clickedItem.getCode();
                        categoryName = clickedItem.getName();
                        tvCate.setText(clickedItem.getName());
                        lv_cate.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_cate.setAdapter(arrayAdapter);
                    lv_cate.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        tvTerritory.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_class.setVisibility(View.GONE);
                lv_cate.setVisibility(View.GONE);
                lv_spec.setVisibility(View.GONE);
                if (lv_terr.getVisibility() == View.VISIBLE) {
                    lv_terr.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Territory");
                    FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                        territoryCode = clickedItem.getCode();
                        territoryName = clickedItem.getName();
                        tvTerritory.setText(clickedItem.getName());
                        lv_terr.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_terr.setAdapter(arrayAdapter);
                    lv_terr.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        tvClass.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_spec.setVisibility(View.GONE);
                lv_cate.setVisibility(View.GONE);
                lv_terr.setVisibility(View.GONE);
                if (lv_class.getVisibility() == View.VISIBLE) {
                    lv_class.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Class");
                    FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                        classCode = clickedItem.getCode();
                        className = clickedItem.getName();
                        tvClass.setText(clickedItem.getName());
                        lv_class.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_class.setAdapter(arrayAdapter);
                    lv_class.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_apply.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Filtered();
            }
        });

    }

    private void getFilterList(String requiredList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (requiredList.equalsIgnoreCase("Speciality")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Category")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Territory")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Class")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
            }
            filterSelectionList.clear();
            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"), jsonObject.getString("Code")));
            }

        } catch (Exception ignored) {

        }
    }

    public void SetupAdapter() {
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        custListArrayList.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

            Log.d("hqSfcoe", DcrCallTabLayoutActivity.TodayPlanSfCode);

            if (jsonArray.length() == 0) {
                commonUtilsMethods.showToastMessage(requireContext(), requireContext().getString(R.string.no_data_found) + "  " + requireContext().getString(R.string.do_master_sync));
            }

            Log.v("DrCall", "-dr_full_length-" + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    if (SharedPref.getGeotagNeed(requireContext()).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                        if (!jsonObject.getString("Lat").isEmpty() && !jsonObject.getString("Long").isEmpty()) {
                            if (SharedPref.getGeotagApprovalNeed(requireContext()).equalsIgnoreCase("0")) {
//                                Log.v("DrCall", "111");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                        custListArrayList = SaveData(jsonObject, i, true);
                                    }
                                }
                            } else {
//                                Log.v("DrCall", "222");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    custListArrayList = SaveData(jsonObject, i, true);
                                }
                            }
                        }
                    } else {
//                        Log.v("DrCall", "333");

                        // This not need TbBased DCR
//                        if (SharedPref.getTpbasedDcr(requireContext()).equalsIgnoreCase("0")) {
//                            Log.v("DrCall", "444");
//                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
//                                custListArrayList = SaveData(jsonObject, i);
//                            }
//                        } else {
//                            Log.v("DrCall", "555");
//                            custListArrayList = SaveData(jsonObject, i);
//                        }

                        custListArrayList = SaveData(jsonObject, i, false);
                    }
                } catch (Exception e) {
                    Log.v("DrCall", "dr--error-1-" + e);
                }
               /* switch (RequiredFiltered) {
                    case "Speciality":
                        if (filteredStr.equalsIgnoreCase(jsonObject.getString("Specialty"))) {
                            AssignData(i);
                        }
                        break;
                    case "Category":
                        if (filteredStr.equalsIgnoreCase(jsonObject.getString("Category"))) {
                            AssignData(i);
                        }
                        break;
                    case "Territory":
                        if (filteredStr.equalsIgnoreCase(jsonObject.getString("Town_Name"))) {
                            AssignData(i);
                        }
                        break;
                    case "":
                        AssignData(i);
                        break;
                }*/
            }

            int count = custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).getclass(), custListArrayList.get(i).getClassCode(), custListArrayList.get(i).isClusterAvailable()));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).getclass(), custListArrayList.get(i).getClassCode(), custListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("DrCall", "-dr--error-2-" + e);
        }

        Log.v("call", "-dr--size--" + custListArrayList.size());

        FilltercustArraList.clear();
        FilltercustArraList.addAll(custListArrayList);

        if (FilltercustArraList.isEmpty()) {
            noDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getDrCap(requireContext()), getString(R.string.found)));
            noDoctor.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noDoctor.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, SharedPref.getCustSrtNd(requireContext()), "1");
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
            rv_list.setAdapter(adapterDCRCallSelection);
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i, boolean isFenced) {
        try {
            String brands = getBrands(jsonObject.getString("MappProds"));
            if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) && !stpOfflineDataDao.isNotApproved()))
                    && !deviation.equalsIgnoreCase("1")) {
                if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                    STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                    List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
                    Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
                    if (!drList.isEmpty()) {
                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.getString("Code")))) {
                            prepareData(jsonObject, i, brands, false);
                        }
                    }
                } else if (tpDataObj != null) {
//                    Type type = new TypeToken<ModelClass>() {
//                    }.getType();
//                    ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
//                    int fwSession = -1;
//                    if(!modelClass.getSessionList().isEmpty() && modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(0).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)){
//                        fwSession = 0;
//                    } else if((modelClass.getSessionList().size() > 1) && modelClass.getSessionList().get(1).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(1).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)) {
//                        fwSession = 1;
//                    }
//                    if(fwSession != -1) {
//                        for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(fwSession).getListedDr()) {
//                            drList.add(subClass.getCode());
//                        }
//                        Log.i("TP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
//                        if(!drList.isEmpty()) {
//                            if(SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.getString("Code")))) {
//                                prepareData(jsonObject, i, brands, false);
//                            }
//                        } else {
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
//                            } else {
//                                prepareData(jsonObject, i, brands, true);
                    }
//                        }
//                    } else {
//                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
//                            prepareData(jsonObject, i, brands, false);
//                        } else {
//                            prepareData(jsonObject, i, brands, true);
//                        }
//                    }
                } else {
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
                    } else {
                        prepareData(jsonObject, i, brands, true);
                    }
                }
            } else if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1"))))
                    && deviation.equalsIgnoreCase("1")) {
//                STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
//                List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
//                Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
//                if(!drList.isEmpty()) {
//                    if(SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.getString("Code")))) {
//                        prepareData(jsonObject, i, brands, false);
//                    }
//                } else
                if (isFenced) {
                    if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                        List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
                        Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
                        if (!drList.isEmpty()) {
                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.getString("Code")))) {
                                prepareData(jsonObject, i, brands, false);
                            }
                        }
                    } else if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
                    }
                } else {
                    if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                        List<String> drList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getDoctorCode()).split(","));
                        Log.i("STP DR LIST", "SaveData: " + Arrays.toString(drList.toArray()));
                        if (!drList.isEmpty()) {
                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code")) && (!drList.isEmpty() && drList.contains(jsonObject.getString("Code")))) {
                                prepareData(jsonObject, i, brands, false);
                            }
                        }
                    } else if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                        prepareData(jsonObject, i, brands, false);
                    } else {
                        prepareData(jsonObject, i, brands, true);
                    }
                }
            } else if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                prepareData(jsonObject, i, brands, false);
            } else {
                prepareData(jsonObject, i, brands, true);
            }
//            Log.v("DrCall", "--brands---" + brands);
        } catch (Exception e) {
            Log.v("DrCall", "--1111---" + e.toString());
            e.printStackTrace();
        }
        return custListArrayList;
    }

    private void prepareData(JSONObject jsonObject, int i, String brands, boolean isClusterAvailable) throws JSONException {
        if (jsonObject.has("Product_Code")) {
            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("HosAddr"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code"), brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"), jsonObject.getString("Doc_Class_ShortName"), jsonObject.getString("Doc_ClsCode"), isClusterAvailable));
        } else {
            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("HosAddr"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), "", brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"), jsonObject.getString("Doc_Class_ShortName"), jsonObject.getString("Doc_ClsCode"), isClusterAvailable));
        }
    }

    public String getBrands(String mappProds) {
        String subString = "";
        String[] StrArray = mappProds.split(",");
        StringBuilder ss1 = new StringBuilder();

        for (String value : StrArray) {
            int iEnd = value.indexOf("-");
            if (iEnd != -1) {
                ss1.append(value.substring(0, iEnd));
                ss1.append(",");
            }
        }

        subString = ss1.toString();
        return subString;
    }

  /*  private void AssignData(int i) {
        try {
            Log.v("CheckSelCall", "--dr-" + GeoTagApproval + "--" + DrGeoTag + "----" + TpBasedDcr);
            if (DrGeoTag.equalsIgnoreCase("1")) {
                if (!jsonObject.getString("Lat").isEmpty() && !jsonObject.getString("Long").isEmpty()) {
                    if (GeoTagApproval.equalsIgnoreCase("0")) {
                        Log.v("CheckSelCall", "111");
                        float[] distance = new float[2];
                        Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), lat, lng, distance);
                        if (distance[0] < limitKm * 1000.0) {
                            if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                if (jsonObject.has("Product_Code")) {
                                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                                } else {
                                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), ""));
                                }
                            }
                        }
                    } else {
                        Log.v("CheckSelCall", "222");
                        float[] distance = new float[2];
                        Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), lat, lng, distance);
                        if (distance[0] < limitKm * 1000.0) {
                            if (jsonObject.has("Product_Code")) {
                                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                            } else {
                                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), ""));
                            }
                        }
                    }
                }
            } else {
                Log.v("CheckSelCall", "333");
                if (TpBasedDcr.equalsIgnoreCase("0")) {
                    Log.v("CheckSelCall", "444");
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).equalsIgnoreCase(jsonObject.getString("Town_Code"))) {
                        if (jsonObject.has("Product_Code")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), ""));
                        }
                    }
                } else {
                    Log.v("CheckSelCall", "555");
                    if (jsonObject.has("Product_Code")) {
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                    } else {
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), ""));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("call", "dr--error--" + e);
        }
    }*/

    private void filter(String text) {
        filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filteredNames);
    }

    public void Filtered() {
        ArrayList<CustList> filterCusList = new ArrayList<>();
        if (!filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        } else {
            filterCusList.addAll(custListArrayList);
        }
        FilltercustArraList.clear();
        if (specialityCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("") && territoryCode.equalsIgnoreCase("") && classCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(filterCusList);
            tv_filterCount.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else {
            for (CustList mList : filterCusList) {
                if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && categoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && territoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && specialityCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else {
                    if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                            && specialityCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getClassCode().equalsIgnoreCase(classCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    }
                }
            }
            tv_filterCount.setText(String.valueOf(FilltercustArraList.size()));
        }

        if (FilltercustArraList.isEmpty()) {
            noDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getDrCap(requireContext()), getString(R.string.found)));
            noDoctor.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noDoctor.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection.filterList(FilltercustArraList);
        }
        dialogFilter.dismiss();
    }

}