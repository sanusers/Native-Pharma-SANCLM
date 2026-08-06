package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.deviation;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.tpDataObj;

import android.app.Dialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.HQChangeListener;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class StockiestFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> filteredNames = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close, img_drop_down;
    Button btn_apply, btn_clear;
    JSONArray jsonArray;
    TextView tv_hqName, tvTerritory, tv_filter_count, noStockist;
    CommonUtilsMethods commonUtilsMethods;

    String territoryCode = "", territoryName = "";

    ListView lv_terr;
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private HQChangeListener hqChangeListener;
    private String STPNeed, STPBasedMTP, STPBasedDCR, TPNeed, TPMandatory, TPBasedDCR, TPDCRDeviation;

    public StockiestFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HQChangeListener) {
            hqChangeListener = (HQChangeListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "stockist");
        View v = inflater.inflate(R.layout.fragment_stockiest, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_filter_count = v.findViewById(R.id.tv_filter_count);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        img_drop_down = v.findViewById(R.id.img_drop_down);
        noStockist = v.findViewById(R.id.no_stockist);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);

        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        getRequiredData();
        SetupAdapter();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

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

        HQSelector.setupHQSelector(
                this,
                requireContext(),
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

    public void SetupAdapter() {
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        custListArrayList.clear();
        if (SharedPref.getGeotagNeedStock(requireContext()).equalsIgnoreCase("1")) {
            try {
                JSONArray masterJsonArrayStkMas = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                JSONArray masterJsonArrayStkGeo = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_GEO + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                HashMap<String, JSONObject> docObj_stk = new HashMap<>();
                for (int i = 0; i < masterJsonArrayStkMas.length(); i++) {
                    JSONObject jsonObject = masterJsonArrayStkMas.getJSONObject(i);
                    String code = jsonObject.optString("Code");
                    if (!code.isEmpty()) {
                        docObj_stk.put(code, jsonObject);
                    } else {
                        Log.d("Merge", "Skipping DOCTOR_MAS object with empty 'Code': " + jsonObject.toString());
                    }
                }
                for (int i = 0; i < masterJsonArrayStkGeo.length(); i++) {
                    JSONObject jsonObject_geo = masterJsonArrayStkGeo.getJSONObject(i);
                    String code = jsonObject_geo.optString("Code");
                    if (code.isEmpty()) {
                        Log.w("Merge", "Skipping GEO object with empty 'Code': " + jsonObject_geo.toString());
                        continue;
                    }
                    if (docObj_stk.containsKey(code)) {
                        JSONObject existingObject = docObj_stk.get(code);
                        jsonObject_geo.keys().forEachRemaining(key -> {
                            System.out.println("Merging keys Tagcust Stk:" + key);
                            try {
                                assert existingObject != null;
                                existingObject.put(key, jsonObject_geo.get(key));
                            } catch (JSONException e) {
                                Log.e("MergeError", "Error merging key Stk: " + key);
                            }
                        });
                    }
                }
                List<JSONObject> sortedList = new ArrayList<>(docObj_stk.values());
                Collections.sort(sortedList, (o1, o2) -> {
                    String name1 = o1.optString("Name", "");
                    String name2 = o2.optString("Name", "");
                    return name1.compareToIgnoreCase(name2);
                });

                jsonArray = new JSONArray(docObj_stk.values());

                Log.v("STKCALL", "-stk_full_length-" + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (SharedPref.getGeotagNeedStock(requireContext()).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                        if (!jsonObject.optString("lat").isEmpty() && !jsonObject.optString("long").isEmpty()) {
                            if (SharedPref.getGeotagApprovalNeed(requireContext()).equalsIgnoreCase("0")) {
                                Log.v("STKCALL", "--11-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
//                                    if (jsonObject.optString("cust_status").equalsIgnoreCase("0")) {
                                    custListArrayList = SaveData(jsonObject, i, true);
//                                    }
                                }
                            } else {
                                Log.v("STKCALL", "--22-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    custListArrayList = SaveData(jsonObject, i, true);
                                }
                            }
                        }
                    } else {
//                        if (SharedPref.getTpbasedDcr(requireContext()).equalsIgnoreCase("0")) {
//                            Log.v("STKCALL", "--33-");
//                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
//                                custListArrayList = SaveData(jsonObject,i);
//                            }
//                        } else {
                        Log.v("STKCALL", "--44-");
                        custListArrayList = SaveData(jsonObject, i, false);
//                        }
                    }
                }

                int count = custListArrayList.size();
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                            custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), "", "", custListArrayList.get(i).isClusterAvailable()));
                            custListArrayList.remove(j--);
                            count--;
                        } else {
                            custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), "", "", custListArrayList.get(i).isClusterAvailable()));
                        }
                    }
                }

            } catch (Exception e) {
                Log.v("STKCALL", "-stk--error--" + e);
            }
           /* try {
                JSONArray masterJsonArrayStkMas = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                JSONArray masterJsonArrayStkGeo = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_GEO + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

                HashMap<String, List<JSONObject>> stockistMap = new HashMap<>();

                for (int i = 0; i < masterJsonArrayStkMas.length(); i++) {
                    JSONObject masObj = masterJsonArrayStkMas.getJSONObject(i);
                    String code = masObj.optString("Code");
                    if (code.isEmpty()) continue;

                    stockistMap.putIfAbsent(code, new ArrayList<>());
                    stockistMap.get(code).add(new JSONObject(masObj.toString())); // clone
                }

                for (int i = 0; i < masterJsonArrayStkGeo.length(); i++) {
                    JSONObject geoObj = masterJsonArrayStkGeo.getJSONObject(i);
                    String code = geoObj.optString("Code");
                    if (code.isEmpty()) continue;

                    if (stockistMap.containsKey(code)) {
                        for (JSONObject masObj : stockistMap.get(code)) {
                            Iterator<String> keys = geoObj.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                try {
                                    masObj.put(key, geoObj.get(key));
                                } catch (JSONException ignored) {}
                            }
                        }
                    }
                }

                jsonArray = new JSONArray();
                for (List<JSONObject> list : stockistMap.values()) {
                    for (JSONObject obj : list) {
                        jsonArray.put(obj);
                    }
                }

                Log.v("STKCALL", "Merged stockist count: " + jsonArray.length());

                custListArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try {
                        boolean withinDistance = true;

                        if (SharedPref.getGeotagNeedStock(requireContext()).equalsIgnoreCase("1")
                                && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {

                            if (!jsonObject.optString("lat").isEmpty() &&
                                    !jsonObject.optString("long").isEmpty()) {

                                float[] distance = new float[1];
                                Location.distanceBetween(
                                        Double.parseDouble(jsonObject.optString("lat")),
                                        Double.parseDouble(jsonObject.optString("long")),
                                        DcrCallTabLayoutActivity.lat,
                                        DcrCallTabLayoutActivity.lng,
                                        distance
                                );

                                withinDistance = distance[0] < (DcrCallTabLayoutActivity.limitKm * 1000.0);
                            }
                        }

                        if (withinDistance) {
                            custListArrayList = SaveData(jsonObject, i, true);                        }

                    } catch (Exception e) {
                        Log.e("STKCALL", "Distance filter error: " + e.getMessage());
                    }
                }

                Set<String> uniqueKeys = new HashSet<>();
                Iterator<CustList> iterator = custListArrayList.iterator();

                while (iterator.hasNext()) {
                    CustList c = iterator.next();
                    String key = c.getCode() + "_" + c.getLatitude() + "_" + c.getLongitude();

                    if (uniqueKeys.contains(key)) {
                        iterator.remove();
                    } else {
                        uniqueKeys.add(key);
                    }
                }

                Log.v("STKCALL", "Final stockist count: " + custListArrayList.size());

            } catch (Exception e) {
                Log.e("STKCALL", "Stockist error: " + e.getMessage());
                e.printStackTrace();
            }*/


        } else {
            try {
//            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

                Log.v("STKCALL", "-stk_full_length-" + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (SharedPref.getGeotagNeedStock(requireContext()).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                        if (!jsonObject.optString("lat").isEmpty() && !jsonObject.optString("long").isEmpty()) {
                            if (SharedPref.getGeotagApprovalNeed(requireContext()).equalsIgnoreCase("0")) {
                                Log.v("STKCALL", "--11-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
//                                    if (jsonObject.optString("cust_status").equalsIgnoreCase("0")) {
                                    custListArrayList = SaveData(jsonObject, i, true);
//                                    }
                                }
                            } else {
                                Log.v("STKCALL", "--22-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    custListArrayList = SaveData(jsonObject, i, true);
                                }
                            }
                        }
                    } else {
//                        if (SharedPref.getTpbasedDcr(requireContext()).equalsIgnoreCase("0")) {
//                            Log.v("STKCALL", "--33-");
//                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
//                                custListArrayList = SaveData(jsonObject,i);
//                            }
//                        } else {
                        Log.v("STKCALL", "--44-");
                        custListArrayList = SaveData(jsonObject, i, false);
//                        }
                    }
                }

                int count = custListArrayList.size();
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                            custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), "", "", custListArrayList.get(i).isClusterAvailable()));
                            custListArrayList.remove(j--);
                            count--;
                        } else {
                            custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), "", "", custListArrayList.get(i).isClusterAvailable()));
                        }
                    }
                }

            } catch (Exception e) {
                Log.v("STKCALL", "-stk--error--" + e);
            }

        }
        FilltercustArraList.clear();
        FilltercustArraList.addAll(custListArrayList);
        Log.v("STKCALL", "-stk--size--" + custListArrayList.size());

        if (FilltercustArraList.isEmpty()) {
            noStockist.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getStkCap(requireContext()), getString(R.string.found)));
            noStockist.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noStockist.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, "1", SharedPref.getGeotagNeedStock(requireContext()).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now()), "3");
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
            rv_list.setAdapter(adapterDCRCallSelection);

            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i, boolean isFenced) {
        try {
            List<String> todayPlannedClusters = Arrays.asList(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(SharedPref.getTodayDayPlanClusterName(requireContext()))).split(","));
            if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0")/* && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")*/)))
                    && !deviation.equalsIgnoreCase("1")) {
                if (tpDataObj != null) {
//                    if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
                    if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        Type type = new TypeToken<OneBuildModelClass>() {
                        }.getType();
                        OneBuildModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                        int fwSession = -1;
                        if (!modelClass.getSessionList().isEmpty() && modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("F") && (modelClass.getSessionList().get(0).getHeadquarters().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode) || SharedPref.getSfCode(requireContext()).equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode))) {
                            fwSession = 0;
                        } else if ((modelClass.getSessionList().size() > 1) && modelClass.getSessionList().get(1).getWorkType().getFWFlg().equalsIgnoreCase("F") && (modelClass.getSessionList().get(1).getHeadquarters().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode) || SharedPref.getSfCode(requireContext()).equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode))) {
                            fwSession = 1;
                        }
                        List<String> stkList = new ArrayList<>();
                        if (fwSession != -1) {
                            for (OneBuildModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(fwSession).getStockists()) {
                                stkList.add(subClass.getCode());
                            }
                            Log.i("TP DR LIST", "SaveData: " + Arrays.toString(stkList.toArray()));
                            if (!stkList.isEmpty()) {
                                if (stkList.contains(jsonObject.getString("Code"))) {
                                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", false));
                                }
                            } else {
                                if (isFenced) {
//                if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
//                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
                                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", ""));
//                }
                                } else {
                                    if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
//                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
                                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", false));
                                    } else {
                                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", true));
                                    }
                                }
                            }
                        } else {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
//                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
                                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", false));
                            } else {
                                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", true));
                            }
                        }
                    } else {
                        Type type = new TypeToken<ModelClass>() {
                        }.getType();
                        ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                        int fwSession = -1;
                        if (!modelClass.getSessionList().isEmpty() && modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("F") && (modelClass.getSessionList().get(0).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode) || SharedPref.getSfCode(requireContext()).equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode))) {
                            fwSession = 0;
                        } else if ((modelClass.getSessionList().size() > 1) && modelClass.getSessionList().get(1).getWorkType().getFWFlg().equalsIgnoreCase("F") && (modelClass.getSessionList().get(1).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode) || SharedPref.getSfCode(requireContext()).equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode))) {
                            fwSession = 1;
                        }
                        List<String> stkList = new ArrayList<>();
                        if (fwSession != -1) {
                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(fwSession).getStockiest()) {
                                stkList.add(subClass.getCode());
                            }
                            Log.i("TP DR LIST", "SaveData: " + Arrays.toString(stkList.toArray()));
                            if (!stkList.isEmpty()) {
                                if (stkList.contains(jsonObject.getString("Code"))) {
                                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", false));
                                }
                            } else {
                                if (isFenced) {
//                if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
//                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
                                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", ""));
//                }
                                } else {
                                    if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
//                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
                                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", false));
                                    } else {
                                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", true));
                                    }
                                }
                            }
                        } else {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
//                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
                                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", false));
                            } else {
                                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", true));
                            }
                        }
                    }
//                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", false));
//                    }
                }
            } else if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0")/* && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")*/)))
                    && deviation.equalsIgnoreCase("1")) {
//                if (isFenced) {
////                if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
////                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
//                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", ""));
////                }
//                } else {
                if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
//                        custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "","","",false));
                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", false));
                } else {
                    custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", "", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", "", "", "", true));
                }
//                }
            } else if (todayPlannedClusters.contains(jsonObject.optString("Town_Name"))) {
                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", false));
            } else {
                custListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "3", "Category", "", "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addrs"), "", "", jsonObject.optString("Stockiest_Email"), jsonObject.optString("Stockiest_Mobile"), jsonObject.optString("Stockiest_Phone"), jsonObject.optString("Stockiest_Cont_Desig"), "", "", "", true));
            }

        } catch (Exception e) {
            Log.v("STKCALL", "--1111---" + e.toString());
            e.printStackTrace();
        }
        return custListArrayList;
    }

    private void filter(String text) {
        filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        if (adapterDCRCallSelection != null) {
            adapterDCRCallSelection.filterList(filteredNames);
        }
    }

    public void CustomizeFiltered() {
        dialogFilter = new Dialog(requireContext());
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        dialogFilter.show();

        img_close = dialogFilter.findViewById(R.id.img_close);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);
        tvTerritory.setText(territoryName);
        tvTerritory.setVisibility(View.VISIBLE);

        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        img_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogFilter.dismiss();
            }
        });

        btn_apply.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Filtered();
            }
        });

        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                territoryCode = "";
                territoryName = "";
                tvTerritory.setText("");
                tvTerritory.setHint(R.string.territory);
            }
        });

        tvTerritory.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (lv_terr.getVisibility() == View.VISIBLE) {
                    lv_terr.setVisibility(View.GONE);

                } else {
                    filterSelectionList.clear();
                    try {
                        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                        Log.v("jsonArray", "--" + jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"), jsonObject.getString("Code")));
                        }

                        FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                            territoryCode = clickedItem.getCode();
                            territoryName = clickedItem.getName();
                            tvTerritory.setText(clickedItem.getName());
                            lv_terr.setVisibility(View.GONE);

                        });
                        lv_terr.setAdapter(arrayAdapter);
                        lv_terr.setVisibility(View.VISIBLE);


                    } catch (Exception ignored) {

                    }

                }
            }
        });
    }

    public void Filtered() {
        ArrayList<CustList> filterCusList = new ArrayList<>();
        if (!filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        } else {
            filterCusList.addAll(custListArrayList);
        }
        FilltercustArraList.clear();
        if (territoryName.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(filterCusList);

            tv_filter_count.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else {
            for (CustList mList : filterCusList) {
                if (mList.getTown_name().equalsIgnoreCase(territoryName)) {
                    FilltercustArraList.add(mList);
                }
            }
            tv_filter_count.setText(String.valueOf(FilltercustArraList.size()));
        }

        if (FilltercustArraList.isEmpty()) {
            noStockist.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getStkCap(requireContext()), getString(R.string.found)));
            noStockist.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noStockist.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            if (adapterDCRCallSelection != null) {
                adapterDCRCallSelection.filterList(FilltercustArraList);
            }
        }
        dialogFilter.dismiss();
    }
}