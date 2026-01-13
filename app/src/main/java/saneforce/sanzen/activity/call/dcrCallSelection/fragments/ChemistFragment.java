package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.deviation;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.tpDataObj;
import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.workDayCode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import java.util.List;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.ChemistAddition;
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
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;

public class ChemistFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> cusListArrayList = new ArrayList<>();
    ArrayList<CustList> filteredNames = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close, img_drop_down;
    Button btn_apply, btn_clear;
    JSONArray jsonArray;
    TextView tv_hqName, tvTerritory, tv_filter_count, tvCate, noChemist;
    CommonUtilsMethods commonUtilsMethods;

    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    String TerritoryCode = "", categoryCode = "", territoryName = "", categoryName = "";
    ListView lv_cate, lv_terr;
    ConstraintLayout constraintLayout;
    Button btn_addchm;

    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    private String STPNeed, STPBasedMTP, STPBasedDCR, TPNeed, TPMandatory, TPBasedDCR, TPDCRDeviation;
    private HQChangeListener hqChangeListener;

    public ChemistFragment() {
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
        Log.v("fragment", "---" + "chemist");
        View v = inflater.inflate(R.layout.fragment_chemist, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_filter_count = v.findViewById(R.id.tv_filter_count);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        img_drop_down = v.findViewById(R.id.img_drop_down);
        noChemist = v.findViewById(R.id.no_chemist);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        btn_addchm = v.findViewById(R.id.add_chm);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        getRequiredData();

        SetupAdapter();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);
        if ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && SharedPref.getEntryFormNeed(requireContext()).equalsIgnoreCase("0"))
                || (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && SharedPref.getEntryFormMgr(requireContext()).equalsIgnoreCase("0"))) {
            btn_addchm.setVisibility(View.VISIBLE);
        } else {
            btn_addchm.setVisibility(View.GONE);
        }
        btn_addchm.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent intent = new Intent(getContext(), ChemistAddition.class);
                activityResultLauncher.launch(intent);
            }
        });
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
        cusListArrayList.clear();
        if (SharedPref.getGeotagNeedChe(requireContext()).equalsIgnoreCase("1")) {
            try {
                JSONArray masterJsonArrayChe = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                JSONArray masterJsonArrayGeo = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_GEO + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                HashMap<String, JSONObject> docObj_che = new HashMap<>();
                for (int i = 0; i < masterJsonArrayChe.length(); i++) {
                    JSONObject jsonObject = masterJsonArrayChe.getJSONObject(i);
                    String code = jsonObject.optString("Code");
                    if (!code.isEmpty()) {
                        docObj_che.put(code, jsonObject);
                    } else {
                        Log.d("Merge", "Skipping DOCTOR_MAS object with empty 'Code': " + jsonObject.toString());
                    }
                }
                for (int i = 0; i < masterJsonArrayGeo.length(); i++) {
                    JSONObject jsonObject_geo = masterJsonArrayGeo.getJSONObject(i);
                    String code = jsonObject_geo.optString("Code");
                    if (code.isEmpty()) {
                        Log.w("Merge", "Skipping GEO object with empty 'Code': " + jsonObject_geo.toString());
                        continue;
                    }
                    if (docObj_che.containsKey(code)) {
                        JSONObject existingObject = docObj_che.get(code);
                        jsonObject_geo.keys().forEachRemaining(key -> {
//                            System.out.println("Merging keys Tagcust Chm:" + key);
                            try {
                                assert existingObject != null;
                                existingObject.put(key, jsonObject_geo.get(key));
                            } catch (JSONException e) {
                                Log.e("MergeError", "Error merging key Chm: " + key);
                            }
                        });
                    }
                }

                jsonArray = new JSONArray(docObj_che.values());
                Log.v("CheCall", "-che_full_length-" + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    try {
                        if (SharedPref.getGeotagNeedChe(requireContext()).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                            if (!jsonObject.optString("lat").isEmpty() && !jsonObject.optString("long").isEmpty()) {
                                if (SharedPref.getGeotagApprovalNeed(requireContext()).equalsIgnoreCase("0")) {
                                    float[] distance = new float[2];
                                    Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                    if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
//                                if (jsonObject.optString("cust_status").equalsIgnoreCase("0")) {
                                        cusListArrayList = SaveData(jsonObject, i, true);
//                                }
                                    }
                                } else {
                                    float[] distance = new float[2];
                                    Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                    if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                        cusListArrayList = SaveData(jsonObject, i, true);
                                    }
                                }
                            }
                        } else {
                            // This not need TbBased DCR

//                 if (SharedPref.getTpbasedDcr(requireContext()).equalsIgnoreCase("0")) {
//                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
//                            cusListArrayList = SaveData(jsonObject, i);
//                        }
//                    } else {
//                        cusListArrayList = SaveData(jsonObject, i);
//                    }
                            cusListArrayList = SaveData(jsonObject, i, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("DrCall", "dr--error-1-" + e);
                    }
                }

                int count = cusListArrayList.size();
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (cusListArrayList.get(i).getCode().equalsIgnoreCase(cusListArrayList.get(j).getCode())) {
                            cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(), "", "", cusListArrayList.get(i).isClusterAvailable()));
                            cusListArrayList.remove(j--);
                            count--;
                        } else {
                            cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(), "", "", cusListArrayList.get(i).isClusterAvailable()));
                        }
                    }
                }

            } catch (Exception e) {
                Log.v("CheCall", "-che--error--" + e);
                e.printStackTrace();
            }
        } else {
            try {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

                Log.v("CheCall", "-che_full_length-" + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (SharedPref.getGeotagNeedChe(requireContext()).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                        if (!jsonObject.optString("lat").isEmpty() && !jsonObject.optString("long").isEmpty()) {
                            if (SharedPref.getGeotagApprovalNeed(requireContext()).equalsIgnoreCase("0")) {
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
//                                if (jsonObject.optString("cust_status").equalsIgnoreCase("0")) {
                                    cusListArrayList = SaveData(jsonObject, i, true);
//                                }
                                }
                            } else {
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.optString("lat")), Double.parseDouble(jsonObject.optString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    cusListArrayList = SaveData(jsonObject, i, true);
                                }
                            }
                        }
                    } else {
                        // This not need TbBased DCR

//                 if (SharedPref.getTpbasedDcr(requireContext()).equalsIgnoreCase("0")) {
//                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.optString("Town_Code"))) {
//                            cusListArrayList = SaveData(jsonObject, i);
//                        }
//                    } else {
//                        cusListArrayList = SaveData(jsonObject, i);
//                    }
                        cusListArrayList = SaveData(jsonObject, i, false);
                    }
                }

                int count = cusListArrayList.size();
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (cusListArrayList.get(i).getCode().equalsIgnoreCase(cusListArrayList.get(j).getCode())) {
                            cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(), "", "", cusListArrayList.get(i).isClusterAvailable()));
                            cusListArrayList.remove(j--);
                            count--;
                        } else {
                            cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(), "", "", cusListArrayList.get(i).isClusterAvailable()));
                        }
                    }
                }

            } catch (Exception e) {
                Log.v("CheCall", "-che--error--" + e);
                e.printStackTrace();
            }
        }

        Log.v("CheCall", "-che--size--" + cusListArrayList.size());
        FilltercustArraList.clear();
        FilltercustArraList.addAll(cusListArrayList);
        if (FilltercustArraList.isEmpty()) {
            noChemist.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getChmCap(requireContext()), getString(R.string.found)));
            noChemist.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noChemist.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, SharedPref.getChmSrtNd(requireContext()), SharedPref.getGeotagNeedChe(requireContext()).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now()), "2");
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
            rv_list.setAdapter(adapterDCRCallSelection);
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }
    }

    private String getChemistCategory(String chmCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.optString("Name");
                String code = jsonObject.optString("Code");
                if (code.equalsIgnoreCase(chmCode))
                    return name;
            }
        } catch (Exception e) {
            Log.e("Chemist Call", "getChemistCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i, boolean isFenced) {
        try {
            List<String> todayPlannedClusters = Arrays.asList(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(SharedPref.getTodayDayPlanClusterCode(requireContext()))).split(","));
            if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) && !stpOfflineDataDao.isNotApproved()))
                    && !deviation.equalsIgnoreCase("1")) {
                if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                    STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                    List<String> chmList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getChemistCode()).split(","));
                    Log.i("STP DR LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
                    if (!chmList.isEmpty()) {
                        if (todayPlannedClusters.contains(jsonObject.optString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.optString("Code")))) {
                            cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                        }
                    } else {
                        if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                            cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                        } else {
                            cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));
                        }
                    }
                } else if (tpDataObj != null) {
                    if(SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")){
                        Type type = new TypeToken<OneBuildModelClass>() {
                        }.getType();
                        OneBuildModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                        int fwSession = -1;
                        if(!modelClass.getSessionList().isEmpty() && modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(0).getHeadquarters().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)){
                            fwSession = 0;
                        } else if((modelClass.getSessionList().size() > 1) && modelClass.getSessionList().get(1).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(1).getHeadquarters().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)) {
                            fwSession = 1;
                        }
                        List<String> chmList = new ArrayList<>();
                        if(fwSession != -1) {
                            for (OneBuildModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(fwSession).getChemists()) {
                                chmList.add(subClass.getCode());
                            }
                            Log.i("TP DR LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
                            if(!chmList.isEmpty()) {
                                if(todayPlannedClusters.contains(jsonObject.getString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.getString("Code")))) {
                                    cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                                }
                            } else {
                                if (todayPlannedClusters.contains(jsonObject.getString("Town_Code"))) {
                                    cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
//                                } else {
//                                    prepareData(jsonObject, i, brands, true);
                                }
                            }
                        } else {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                            } else {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));
                            }
                        }
                    }else{
                        Type type = new TypeToken<ModelClass>() {
                        }.getType();
                        ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                        int fwSession = -1;
                        if(!modelClass.getSessionList().isEmpty() && modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(0).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)){
                            fwSession = 0;
                        } else if((modelClass.getSessionList().size() > 1) && modelClass.getSessionList().get(1).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(1).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)) {
                            fwSession = 1;
                        }
                        List<String> chmList = new ArrayList<>();
                        if(fwSession != -1) {
                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(fwSession).getChemist()) {
                                chmList.add(subClass.getCode());
                            }
                            Log.i("TP DR LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
                            if(!chmList.isEmpty()) {
                                if(todayPlannedClusters.contains(jsonObject.getString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.getString("Code")))) {
                                    cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                                }
                            } else {
                                if (todayPlannedClusters.contains(jsonObject.getString("Town_Code"))) {
                                    cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
//                                } else {
//                                    prepareData(jsonObject, i, brands, true);
                                }
                            }
                        } else {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                            } else {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));
                            }
                        }
                    }

//                    Type type = new TypeToken<ModelClass>() {
//                    }.getType();
//                    ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
//                    int fwSession = -1;
//                    if(!modelClass.getSessionList().isEmpty() &&  modelClass.getSessionList().get(0).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(0).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)){
//                        fwSession = 0;
//                    } else if((modelClass.getSessionList().size() > 1) && modelClass.getSessionList().get(1).getWorkType().getFWFlg().equalsIgnoreCase("F") && modelClass.getSessionList().get(1).getHQ().getCode().equalsIgnoreCase(DcrCallTabLayoutActivity.TodayPlanSfCode)) {
//                        fwSession = 1;
//                    }
//                    if(fwSession != -1) {
//                        for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(fwSession).getChemist()) {
//                            chmList.add(subClass.getCode());
//                        }
//                    }
//                    Log.i("TP chm LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
//                }
//                if(!chmList.isEmpty()) {
//                    if(todayPlannedClusters.contains(jsonObject.optString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.optString("Code")))) {
//                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
//                    }
//                } else {
                    if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
//                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "","",false));
                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));

//                    } else {
//                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "","","", true));
                    }
                } else {
                    if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                    } else {
                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));
                    }
                }
            } else if ((((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1"))))
                    && deviation.equalsIgnoreCase("1")) {
//                STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
//                List<String> chmList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getChemistCode()).split(","));
//                Log.i("STP DR LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
//                if(!chmList.isEmpty()) {
//                    if(todayPlannedClusters.contains(jsonObject.optString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.optString("Code")))) {
//                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
//                    }
//                }
                if (isFenced) {
                    if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                        List<String> chmList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getChemistCode()).split(","));
                        Log.i("STP DR LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
                        if (!chmList.isEmpty()) {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.optString("Code")))) {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                            }
                        } else {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                            } else {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));
                            }
                        }
                    } else if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                    }
                } else {
                    if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved())) {
                        STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(workDayCode);
                        List<String> chmList = Arrays.asList(CommonUtilsMethods.removeLastComma(stpOfflineDataTable.getChemistCode()).split(","));
                        Log.i("STP DR LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
                        if (!chmList.isEmpty()) {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.optString("Code")))) {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                            }
                        } else {
                            if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                            } else {
                                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));
                            }
                        }
                    } else if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));
                    } else {
                        cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));

                    }
                }
            } else if (todayPlannedClusters.contains(jsonObject.optString("Town_Code"))) {
//                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "","",false));
                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", false));

            } else {
//                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("MaxGeoMap"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("Addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "","","", true));
                cusListArrayList.add(new CustList(jsonObject.optString("Name"), jsonObject.optString("Code"), "2", getChemistCategory(jsonObject.optString("Chm_cat")), jsonObject.optString("Chm_cat"), "Specialty", jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), jsonObject.optString("GEOTagCnt"), jsonObject.optString("Geototal"), String.valueOf(i), jsonObject.optString("lat"), jsonObject.optString("long"), jsonObject.optString("addr"), "", "", jsonObject.optString("Chemists_Email"), jsonObject.optString("Chemists_Mobile"), jsonObject.optString("Chemists_Phone"), "", "", "", "", true));

            }
        } catch (Exception e) {
            Log.v("CheCall", "--1111---" + e.toString());
            e.printStackTrace();
        }
        return cusListArrayList;
    }

    private void filter(String text) {
        filteredNames = new ArrayList<>();
        if (cusListArrayList != null) {
            for (CustList s : cusListArrayList) {
                if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                    filteredNames.add(s);
                }
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
        tvCate = dialogFilter.findViewById(R.id.constraint_category);
//        ViewGroup.LayoutParams layoutParams = tvCate.getLayoutParams();
//        if(layoutParams instanceof ViewGroup.MarginLayoutParams) {
//            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
//            marginLayoutParams.leftMargin = 400;
//            tvCate.setLayoutParams(layoutParams);
//        }
        tvTerritory.setText(territoryName);
        tvCate.setText(categoryName);

        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        lv_cate = dialogFilter.findViewById(R.id.lv_category);
        tvTerritory.setVisibility(View.VISIBLE);
        tvCate.setVisibility(View.VISIBLE);
        constraintLayout = dialogFilter.findViewById(R.id.constraint_btns);

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
                TerritoryCode = "";
                categoryCode = "";
                territoryName = "";
                categoryName = "";
                tvTerritory.setText("");
                tvCate.setText("");
            }
        });

        tvTerritory.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_cate.setVisibility(View.GONE);
                if (lv_terr.getVisibility() == View.VISIBLE) {
                    lv_terr.setVisibility(View.GONE);

                } else {
                    filterSelectionList.clear();
                    try {
                        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
                        Log.v("jsonArray", "--" + jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            filterSelectionList.add(new DCRFillteredModelClass(jsonObject.optString("Name"), jsonObject.optString("Code")));
                        }

                        FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                            TerritoryCode = clickedItem.getCode();
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

        tvCate.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_terr.setVisibility(View.GONE);
                if (lv_cate.getVisibility() == View.VISIBLE) {
                    lv_cate.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    try {
                        filterSelectionList.clear();
                        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
                        Log.v("jsonArray", "--" + jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            filterSelectionList.add(new DCRFillteredModelClass(jsonObject.optString("Name"), jsonObject.optString("Code")));
                        }
                        FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                            categoryCode = clickedItem.getCode();
                            categoryName = clickedItem.getName();
                            tvCate.setText(clickedItem.getName());
                            lv_cate.setVisibility(View.GONE);
                            constraintLayout.setVisibility(View.VISIBLE);
                        });
                        lv_cate.setAdapter(arrayAdapter);
                        lv_cate.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        Log.e("Chemist Call Selection", "CustomizeFiltered: " + e.getMessage());
                        e.printStackTrace();
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
            filterCusList.addAll(cusListArrayList);
        }
        FilltercustArraList.clear();
        if (TerritoryCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(filterCusList);
            tv_filter_count.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else if (!TerritoryCode.isEmpty() && !categoryCode.isEmpty()) {
            for (CustList mList : filterCusList) {
                if (mList.getTown_code().equalsIgnoreCase(TerritoryCode) && mList.getCategoryCode().equalsIgnoreCase(categoryCode)) {
                    FilltercustArraList.add(mList);
                }
            }
        } else if (!TerritoryCode.isEmpty()) {
            for (CustList mList : filterCusList) {
                if (mList.getTown_code().equalsIgnoreCase(TerritoryCode)) {
                    FilltercustArraList.add(mList);
                }
            }
        } else if (!categoryCode.isEmpty()) {
            for (CustList mList : filterCusList) {
                if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)) {
                    FilltercustArraList.add(mList);
                }
            }
        }
        tv_filter_count.setText(String.valueOf(FilltercustArraList.size()));

        if (FilltercustArraList.isEmpty()) {
            noChemist.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getChmCap(requireContext()), getString(R.string.found)));
            noChemist.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            noChemist.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            if (adapterDCRCallSelection != null) {
                adapterDCRCallSelection.filterList(FilltercustArraList);
            }
        }
        dialogFilter.dismiss();
    }

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) { // Check if data is saved successfully
                    SetupAdapter(); // Reload the current fragment
                }
            }
    );

}