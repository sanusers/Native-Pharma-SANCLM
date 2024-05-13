package saneforce.santrip.activity.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.santrip.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.santrip.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;

public class ListedDoctorFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static ListView filterList;
    public static ConstraintLayout constraintFilter;


    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close, img_del;
    TextView tv_hqName, tv_add_condition, tv_filterCount;
    Button btn_apply, btn_clear;
    String specialityCode, categoryCode, territoryCode, ClassCode;


    ListView lv_spec, lv_cate, lv_terr,lv_class;
    JSONArray jsonArray;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    ArrayList<String> listOfItems = new ArrayList<>();
    TextView tvSpec, tvCate, tvTerritory, tvClass;
    JSONObject jsonObject;
    ApiInterface apiInterface;
    ConstraintLayout constraintLayout ;
    int count = 0;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;


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
        filterList = v.findViewById(R.id.filter_list_view);
        constraintFilter = v.findViewById(R.id.constraint_filter_selection_list);
        tv_filterCount = v.findViewById(R.id.tv_filter_count);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

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

        return v;
    }
    public  void CustomizeFiltered(){


        specialityCode ="";
        territoryCode ="";
        ClassCode =""; categoryCode ="";

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
        img_del.setVisibility(View.VISIBLE);
        constraintLayout=dialogFilter.findViewById(R.id.constraint_btns);
        img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

        btn_clear.setOnClickListener(view15 -> {
            tvSpec.setHint(R.string.speciality);
            tvTerritory.setHint(R.string.territory);
            tvCate.setHint(R.string.category);
            tvClass.setHint(R.string.class_filter);

        });

        tv_add_condition.setOnClickListener(view13 -> {
            if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                tvClass.setVisibility(View.VISIBLE);
            } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE) {
                tvTerritory.setVisibility(View.VISIBLE);
            }
        });

        img_del.setOnClickListener(view14 -> {
            if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvClass.getVisibility() == View.INVISIBLE) {
                tvTerritory.setVisibility(View.GONE);
                tvTerritory.setText(R.string.territory);
            } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                tvClass.setVisibility(View.INVISIBLE);
                tvClass.setText(R.string.class_filter);
            }
        });




        tvSpec.setOnClickListener(view -> {
            if (lv_spec.getVisibility() == View.VISIBLE) {
                lv_spec.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Speciality");

                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    specialityCode = clickedItem.getCode();
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


        });



        tvCate.setOnClickListener(view -> {
            if (lv_cate.getVisibility() == View.VISIBLE) {
                lv_cate.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Category");
                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    categoryCode = clickedItem.getCode();
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
            if (lv_terr.getVisibility() == View.VISIBLE) {
                lv_terr.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Territory");
                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    territoryCode = clickedItem.getCode();
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
            if (lv_class.getVisibility() == View.VISIBLE) {
                lv_class.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilterList("Class");
                FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                    ClassCode = clickedItem.getCode();
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
            Filltered();
        });

        btn_clear.setOnClickListener(view2 -> {
            Filltered();
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
            }else if(requiredList.equalsIgnoreCase("Class")){
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
            }
            filterSelectionList.clear();
            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"),jsonObject.getString("Code")));
            }

        } catch (Exception ignored) {

        }
    }



    private void SetupAdapter() {
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

            Log.d("hqSfcoe",DcrCallTabLayoutActivity.TodayPlanSfCode);

            if (jsonArray.length() == 0) {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_data_found)  + "  " +  context.getString(R.string.do_master_sync) );
            }

            Log.v("DrCall", "-dr_full_length-" + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    if (SharedPref.getGeotagNeed(context).equalsIgnoreCase("1")) {
                        if (!jsonObject.getString("Lat").isEmpty() && !jsonObject.getString("Long").isEmpty()) {
                            if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
                                Log.v("DrCall", "111");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                        custListArrayList = SaveData(jsonObject, i);
                                    }
                                }
                            } else {
                                Log.v("DrCall", "222");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    custListArrayList = SaveData(jsonObject, i);
                                }
                            }
                        }
                    } else {
                        Log.v("DrCall", "333");
                        if (SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
                            Log.v("DrCall", "444");
                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                                custListArrayList = SaveData(jsonObject, i);
                            }
                        } else {
                            Log.v("DrCall", "555");
                            custListArrayList = SaveData(jsonObject, i);
                        }
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
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(),custListArrayList.get(i).getclass() ,custListArrayList.get(i).getClassCode() ,custListArrayList.get(i).isClusterAvailable()));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).getclass(),custListArrayList.get(i).getClassCode() ,custListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("DrCall", "-dr--error-2-" + e);
        }

        Log.v("call", "-dr--size--" + custListArrayList.size());

        FilltercustArraList.clear();
        FilltercustArraList.addAll(custListArrayList);

        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, SharedPref.getCustSrtNd(requireContext()),"1");
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));

    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i) {

        try {
            String brands = getBrands(jsonObject.getString("MappProds"));
            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                if (jsonObject.has("Product_Code")) {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code"), brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"),jsonObject.getString("Doc_Class_ShortName") ,jsonObject.getString("Doc_ClsCode"),false));
                } else {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), "", brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst") ,jsonObject.getString("Doc_Class_ShortName"),jsonObject.getString("Doc_ClsCode"),false));
                }
            } else {
                if (jsonObject.has("Product_Code")) {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code"), brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"),jsonObject.getString("Doc_Class_ShortName"),jsonObject.getString("Doc_ClsCode"), true));
                } else {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), "", brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"),jsonObject.getString("Doc_Class_ShortName"),jsonObject.getString("Doc_ClsCode"), true));
                }
            }
            Log.v("DrCall", "--brands---" + brands);
        } catch (Exception e) {
            Log.v("DrCall", "--1111---" + e.toString());

        }
        return custListArrayList;
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
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filteredNames);
    }




    public  void  Filltered(){
        FilltercustArraList.clear();
        if(specialityCode.equalsIgnoreCase("")&& categoryCode.equalsIgnoreCase("")&& territoryCode.equalsIgnoreCase("")&& ClassCode.equalsIgnoreCase("")){
            FilltercustArraList.addAll(custListArrayList);
            tv_filterCount.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }else {
            for(CustList mList:custListArrayList){
                if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)){
                    FilltercustArraList.add(mList);
                }
                else if(mList.getCategoryCode().equalsIgnoreCase(categoryCode)){
                    FilltercustArraList.add(mList);
                }
                else if(mList.getTown_code().equalsIgnoreCase(territoryCode)){
                    FilltercustArraList.add(mList);
                }
                else if(mList.getClassCode().equalsIgnoreCase(ClassCode)){
                    FilltercustArraList.add(mList);
                }
            }
            tv_filterCount.setText(String.valueOf(FilltercustArraList.size()));
        }


        adapterDCRCallSelection.notifyDataSetChanged();
        dialogFilter.dismiss();
    }

}