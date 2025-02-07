package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import static saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment.tpDataObj;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

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
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.adapters.AdapterPopupSpinnerSelection;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;


public class ChemistFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> cusListArrayList = new ArrayList<>();
    ArrayList<CustList> filteredNames = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close;
    Button btn_apply, btn_clear, save_btn;
    JSONArray jsonArray;
    TextView tv_hqName, tvTerritory, tv_filter_count, tvCate, noChemist, txt_select_terr;
    CommonUtilsMethods commonUtilsMethods;
    FloatingActionButton btn_add;

    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    String TerritoryCode = "", categoryCode = "", territoryName= "", categoryName = "";
    String SfType = "", SfCode = "", SfName = "", DivCode = "", terrname = "", terrcode = "", txt_terr = "";
    ListView lv_cate, lv_terr, lv_addchmterritory;
    ConstraintLayout constraintLayout ;

    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private String STPNeed, STPBasedMTP, STPBasedDCR, TPNeed, TPBasedDCR, TPDCRDeviation;
    private ProgressDialog progressDialog;
    ApiInterface apiInterface;

    ArrayList<MasterSyncItemModel> chemistModelArray = new ArrayList<>();
    int chemistStatus = 0,categoryStatus = 0;
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "chemist");
        View v = inflater.inflate(R.layout.fragment_chemist, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_filter_count = v.findViewById(R.id.tv_filter_count);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        noChemist = v.findViewById(R.id.no_chemist);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);

        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        getRequiredData();

        SetupAdapter();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);
        btn_add=v.findViewById(R.id.add_chm);
        if(SharedPref.getChemistAddition(context).equalsIgnoreCase("0")) {
            btn_add.setVisibility(View.VISIBLE);
        }
        else{
            btn_add.setVisibility(View.GONE);
        }
        btn_add.setOnClickListener(view -> {
            popupAddChemist();
        });
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

        return v;
    }

    private void getRequiredData() {
        STPNeed = SharedPref.getStpNeed(requireContext());
        STPBasedMTP = SharedPref.getStpBasedMtp(requireContext());
        STPBasedDCR = SharedPref.getStpBasedDcr(requireContext());
        TPNeed = SharedPref.getTpNeed(requireContext());
        TPBasedDCR = SharedPref.getTpbasedDcr(requireContext());
        TPDCRDeviation = SharedPref.getTpdcrDeviation(requireContext());
    }

    private void SetupAdapter() {
        cusListArrayList.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            Log.v("CheCall", "-che_full_length-" + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (SharedPref.getGeotagNeedChe(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
                    if (!jsonObject.getString("lat").isEmpty() && !jsonObject.getString("long").isEmpty()) {
                        if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                            if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
//                                if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                    cusListArrayList = SaveData(jsonObject, i);
//                                }
                            }
                        } else {
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                            if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                cusListArrayList = SaveData(jsonObject, i);
                            }
                        }
                    }
                } else {
                    // This not need TbBased DCR

//                 if (SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
//                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
//                            cusListArrayList = SaveData(jsonObject, i);
//                        }
//                    } else {
//                        cusListArrayList = SaveData(jsonObject, i);
//                    }
                    cusListArrayList = SaveData(jsonObject, i);
                }
            }

            int count = cusListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (cusListArrayList.get(i).getCode().equalsIgnoreCase(cusListArrayList.get(j).getCode())) {
                        cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(),"","", cusListArrayList.get(i).isClusterAvailable()));
                        cusListArrayList.remove(j--);
                        count--;
                    } else {
                        cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(),"","", cusListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }

        } catch (Exception e) {
            Log.v("CheCall", "-che--error--" + e);
        }

        Log.v("CheCall", "-che--size--" + cusListArrayList.size());

        FilltercustArraList.clear();
        FilltercustArraList.addAll(cusListArrayList);
        if(FilltercustArraList.isEmpty()){
            noChemist.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getChmCap(requireContext()), getString(R.string.found)));
            noChemist.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        }else {
            noChemist.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, SharedPref.getChmSrtNd(requireContext()), "2");
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
            rv_list.setAdapter(adapterDCRCallSelection);
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }
    }

    private String getChemistCategory(String chmCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                String code = jsonObject.getString("Code");
                if(code.equalsIgnoreCase(chmCode))
                    return name;
            }
        } catch (Exception e) {
            Log.e("Chemist Call", "getChemistCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i) {
        try {
            if(((TPNeed.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                    || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0")))) {
                List<String> chmList = new ArrayList<>();
                if(tpDataObj != null) {
                    Type type = new TypeToken<ModelClass>() {
                    }.getType();
                    ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                    for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(0).getChemist()) {
                        chmList.add(subClass.getCode());
                    }
                    Log.i("TP chm LIST", "SaveData: " + Arrays.toString(chmList.toArray()));
                    if(SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code")) && (!chmList.isEmpty() && chmList.contains(jsonObject.getString("Code")))) {
                        cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", getChemistCategory(jsonObject.getString("Chm_cat")), jsonObject.getString("Chm_cat"), "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addr"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "", "","",false));
                    }
                } else {
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                        cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", getChemistCategory(jsonObject.getString("Chm_cat")), jsonObject.getString("Chm_cat"), "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addr"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "", "","",false));
                    }
                }
            } else if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", getChemistCategory(jsonObject.getString("Chm_cat")), jsonObject.getString("Chm_cat"), "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addr"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "", "","",false));
            } else {
                cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", getChemistCategory(jsonObject.getString("Chm_cat")), jsonObject.getString("Chm_cat"), "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addr"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "","","", true));
            }
        } catch (Exception e) {
            Log.v("CheCall", "--1111---" + e.toString());
        }
        return cusListArrayList;
    }

    private void filter(String text) {
        filteredNames = new ArrayList<>();
        if(cusListArrayList != null) {
            for (CustList s : cusListArrayList) {
                if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                    filteredNames.add(s);
                }
            }
        }
        if(adapterDCRCallSelection != null) {
            adapterDCRCallSelection.filterList(filteredNames);
        }
    }

    public  void CustomizeFiltered(){
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
        constraintLayout=dialogFilter.findViewById(R.id.constraint_btns);

        img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

        btn_apply.setOnClickListener(view1 -> Filtered());

        btn_clear.setOnClickListener(view -> {
            TerritoryCode = "";
            categoryCode = "";
            territoryName = "";
            categoryName = "";
            tvTerritory.setText("");
            tvCate.setText("");
        });

        tvTerritory.setOnClickListener(view -> {
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
                        filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"),jsonObject.getString("Code")));
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
        });

        tvCate.setOnClickListener(view -> {
            lv_terr.setVisibility(View.GONE);
            if (lv_cate.getVisibility() == View.VISIBLE) {
                lv_cate.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
            } else {
                try {
                    filterSelectionList.clear();
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
                    Log.v("jsonArray", "--" + jsonArray.length());
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"), jsonObject.getString("Code")));
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
                } catch (Exception e){
                    Log.e("Chemist Call Selection", "CustomizeFiltered: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void Filtered() {
        ArrayList<CustList> filterCusList = new ArrayList<>();
        if(!filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        }else {
            filterCusList.addAll(cusListArrayList);
        }
        FilltercustArraList.clear();
        if (TerritoryCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(filterCusList);
            tv_filter_count.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else if(!TerritoryCode.isEmpty() && !categoryCode.isEmpty()){
            for (CustList mList : filterCusList) {
                if (mList.getTown_code().equalsIgnoreCase(TerritoryCode) && mList.getCategoryCode().equalsIgnoreCase(categoryCode)) {
                    FilltercustArraList.add(mList);
                }
            }
        } else if(!TerritoryCode.isEmpty()){
            for (CustList mList : filterCusList) {
                if (mList.getTown_code().equalsIgnoreCase(TerritoryCode)) {
                    FilltercustArraList.add(mList);
                }
            }
        } else if(!categoryCode.isEmpty()){
            for (CustList mList : filterCusList) {
                if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)) {
                    FilltercustArraList.add(mList);
                }
            }
        }
        tv_filter_count.setText(String.valueOf(FilltercustArraList.size()));

        if(FilltercustArraList.isEmpty()){
            noChemist.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getChmCap(requireContext()), getString(R.string.found)));
            noChemist.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        }else {
            noChemist.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection.filterList(FilltercustArraList);
        }
        dialogFilter.dismiss();
    }

    public void popupAddChemist() {
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_add_chm);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        txt_select_terr = dialog.findViewById(R.id.txt_select_terr);
        lv_addchmterritory= dialog.findViewById(R.id.lv_addchmterritory);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        save_btn = dialog.findViewById(R.id.btn_save);
        TextView chmtagname= dialog.findViewById(R.id.chmtagname);
        final EditText edt_dr = dialog.findViewById(R.id.edt_dr);
        final EditText edt_addr = dialog.findViewById(R.id.edt_addr);
        final EditText edt_ph = dialog.findViewById(R.id.edt_ph);
        Log.v("chemistcaption",SharedPref.getChmCap(requireContext()));
        if (SharedPref.getChmCap(requireContext()).isEmpty()|| SharedPref.getChmCap(requireContext())==null){
            chmtagname.setText(getResources().getString(R.string.add)+" " + "Chemist");
        }else {
            chmtagname.setText(getResources().getString(R.string.add)+" " + SharedPref.getChmCap(requireContext()));
        }

        save_btn.setOnClickListener(v -> {
            save_btn.setEnabled(false);
            if (!txt_select_terr.getText().toString().isEmpty() && !edt_dr.getText().toString().isEmpty()
                    && !edt_dr.getText().toString().contains("'"))
            {
                Log.v("qualification_txt", "arent_empty");
                save_btn.setEnabled(false);
                JSONObject json = new JSONObject();
                try
                {
                    SfType = SharedPref.getSfType(requireContext());
                    SfName =  SharedPref.getSfName(requireContext());
                    DivCode =  SharedPref.getDivisionCode(requireContext());
                    json.put("tableName", "savenewchm");
                    if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")){
                        SfCode = SharedPref.getHqCode(requireContext());}
                    else {
                        SfCode = SharedPref.getSfCode(requireContext());
                    }
//                    if (SfType.equalsIgnoreCase("2"))
//                        json.put("sfcode", SF_coding.get(spinnerpostion));
//                    else
//                        json.put("sfcode", SfCode);
                    json.put("sfcode", SfCode);
                    json.put("division_code", DivCode);
                    json.put("DrName", edt_dr.getText().toString());
                    json.put("DrAddr", edt_addr.getText().toString());
                    json.put("DrTerCd", terrcode);
                    Log.v("json_save_chemist", json.toString());
                    save_btn.setEnabled(false);
                    addChm(json.toString(), dialog);
                }
                catch (Exception e)
                {
                    save_btn.setEnabled(true);
                }
            }
            else
            {
                if (edt_dr.getText().toString().contains("'"))
                {
                    edt_dr.setError(getResources().getString(R.string.invalid_char));
                    save_btn.setEnabled(true);
                }
                else
                {
                    commonUtilsMethods.showToastMessage(getActivity(), getResources().getString(R.string.fill_all));
                    save_btn.setEnabled(true);
                }
            }
        });

        img_close.setOnClickListener(v -> {
            dialog.dismiss();
            commonFun();
        });
        txt_select_terr.setOnClickListener(v -> gettingTableValue(5));
    }
    public void commonFun()
    {
        try {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } catch (Exception e) {

        }
    }
    public void addChm(String val, final Dialog dialog) {
        try {
            if (progressDialog == null) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(getActivity());
                progressDialog = CommonUtilsMethods.createProgressDialog(getActivity());
                progressDialog.show();
            } else {
                progressDialog.show();
            }

            if (isNetworkConnected()) {
                String baseUrl = SharedPref.getBaseWebUrl(getActivity());
                String pathUrl = SharedPref.getPhpPathUrl(getActivity());
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                Log.e("test", "login url : " + baseUrl + replacedUrl);
                apiInterface = RetrofitClient.getRetrofit(getActivity(), baseUrl + replacedUrl);
                Log.d("save_obj", String.valueOf(val));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/masterdata");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, val);

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")){
                                    SfCode = SharedPref.getHqCode(requireContext());}
                                else {
                                    SfCode = SharedPref.getSfCode(requireContext());
                                }
                                SyncChemist(SfCode);
                                commonUtilsMethods.showToastMessage(getActivity(), getResources().getString(R.string.saved_successfully));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            save_btn.setEnabled(true);
                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(getActivity(), getResources().getString(R.string.poor_connection));
                        }
                    });
                }
            } else {
                save_btn.setEnabled(true);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(getActivity(), getResources().getString(R.string.poor_connection));
            }

        } catch (Exception e) {
            save_btn.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void SyncChemist(String hqCode) {
        chemistModelArray.clear();
        chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST + hqCode);
        categoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY);
        MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(getActivity()),  Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode, chemistStatus, false);
        MasterSyncItemModel chemistCategory = new MasterSyncItemModel(Constants.CATEGORY,  Constants.DOCTOR, "getchem_categorys", Constants.CATEGORY_CHEMIST, categoryStatus, false);
        chemistModelArray.add(cheModel);
        chemistModelArray.add(chemistCategory);
        arrayForAdapter.clear();
        arrayForAdapter.addAll(chemistModelArray);
        populateAdapter(arrayForAdapter);
    }

    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        try{
            for (int i = 0; i < masterSyncItemModels.size(); i++) {
                MasterSyncItemModel item = masterSyncItemModels.get(i);
                sync(item.getMasterOf(), item.getRemoteTableName(), chemistModelArray, i);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sync(String masterOf, String remoteTableName, ArrayList<MasterSyncItemModel> masterSyncItemModels, int position) {
        try {
            apiInterface = RetrofitClient.getRetrofit(getActivity(), SharedPref.getCallApiUrl(getActivity()));
            JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(getActivity());
            jsonObject.put("tableName", remoteTableName);
            jsonObject.put("sfcode", SharedPref.getSfCode(getActivity()));
            jsonObject.put("division_code", SharedPref.getDivisionCode(getActivity()));
            jsonObject.put("Rsf", SfCode);
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            apiInterface = RetrofitClient.getRetrofit(getActivity(), SharedPref.getCallApiUrl(getActivity()));
            Map<String, String> mapString = new HashMap<>();
            Log.e("API Object", "master sync obj : " + jsonObject);
            Call<JsonElement> call = null;
            if (masterOf.equalsIgnoreCase(Constants.DOCTOR)) {
                mapString.put("axn", "table/dcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getActivity()), mapString, jsonObject.toString());
            }
            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        // masterSyncItemModels.get(position).setPBarVisibility(false);
                        Log.e("response :   ",  remoteTableName + " : " + response.body().toString());
                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject2=new JSONObject();
                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                        if (!jsonObject2.has("success")) {
                                            // response as jsonObject with {"success" : "fail" } will be received only when there are unformed object passed or there are no data in back end.
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        }
                                        else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1); // only update sync status and no need to overwrite previously saved data when failed
                                            masterSyncItemModels.get(position).setSyncSuccess(1);
                                        }
                                    }

                                    if (success) {
                                        masterSyncItemModels.get(position).setCount(jsonArray.length());
                                        masterSyncItemModels.get(position).setSyncSuccess(2);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(), 2));
                                    }
                                    SetupAdapter();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            Log.v("masterCheck", "--error-" + e);
            e.printStackTrace();
        }
    }

    public void gettingTableValue(int x) {
        try {
            filterSelectionList.clear();
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(
                    Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(
                        jsonObject.getString("Name"),
                        jsonObject.getString("Code")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        popupSpinner(x);
    }

    public void popupSpinner(final int x) {
        String a = "chem";
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_spinner_selection);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ListView popup_list = dialog.findViewById(R.id.popup_list);
        ImageView close_img = dialog.findViewById(R.id.close_img);
        AdapterPopupSpinnerSelection popupAdapter = new AdapterPopupSpinnerSelection(getActivity(), filterSelectionList);
        popup_list.setAdapter(popupAdapter);
        popupAdapter.notifyDataSetChanged();

        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        popup_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.v("qualification_select", filterSelectionList.get(position).getName());
                txt_select_terr.setText(filterSelectionList.get(position).getName());
                terrname=filterSelectionList.get(position).getName();
                terrcode=filterSelectionList.get(position).getCode();
                txt_terr = filterSelectionList.get(position).getName() + "," + filterSelectionList.get(position).getCode();
                dialog.dismiss();
            }
        });
    }

}