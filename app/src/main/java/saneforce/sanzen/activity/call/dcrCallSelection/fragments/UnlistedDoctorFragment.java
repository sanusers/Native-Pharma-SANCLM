package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;


public class UnlistedDoctorFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close,img_del;
    JSONArray jsonArray;
    JSONObject jsonObjectDob, jsonObjectDow;
    CommonUtilsMethods commonUtilsMethods;
    TextView tvSpec, tvCate, tvTerritory, tvClass, noULDoctor;
    ConstraintLayout constraintLayout ;
    ListView lv_spec, lv_cate, lv_terr,lv_class;


    String specialityCode = "", categoryCode = "", territoryCode = "", classCode = "";
    String specialityName = "", categoryName = "", territoryName = "", className = "";
    TextView tv_hqName, tv_add_condition, tv_filterCount;
    Button btn_apply, btn_clear;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "unlisted");
        View v = inflater.inflate(R.layout.fragment_unlisted_doctor, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        tv_filterCount = v.findViewById(R.id.tv_filter_count);
        noULDoctor = v.findViewById(R.id.no_unlisted_doctor);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        SetupAdapter();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

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

    private void SetupAdapter() {
        custListArrayList.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();

            Log.v("UNDRCALL", "-UnDr_full_length-" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (SharedPref.getGeotagNeedUnlst(context).equalsIgnoreCase("1")) {
                    if (!jsonObject.getString("lat").isEmpty() && !jsonObject.getString("long").isEmpty()) {
                        if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
                            Log.v("UNDRCALL", "--11-");
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                            if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
//                                if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                    custListArrayList = SaveData(jsonObject, i);
//                                }
                            }
                        } else {
                            Log.v("UNDRCALL", "--22-");
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                            if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                custListArrayList = SaveData(jsonObject, i);
                            }
                        }
                    }
                } else {
//                    if (SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
//                        Log.v("UNDRCALL", "--33-");
//                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
//                            custListArrayList = SaveData(jsonObject, i);
//                        }
//                    } else {
                        Log.v("UNDRCALL", "--44-");
                        custListArrayList = SaveData(jsonObject, i);
//                    }
                }
            }

            int count = custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(),custListArrayList.get(i).getClassCode(),custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).isClusterAvailable()));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(),custListArrayList.get(i).getClassCode(),custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("UNDRCALL", "-UnDr--error--" + e);
        }
        FilltercustArraList.clear();
        FilltercustArraList.addAll(custListArrayList);
        Log.v("UNDRCALL", "-UnDr--size--" + custListArrayList.size());

        if(FilltercustArraList.isEmpty()){
            noULDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getUNLcap(requireContext()), getString(R.string.found)));
            noULDoctor.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        }else {
            noULDoctor.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, SharedPref.getUnlistSrtNd(requireContext()), "4");
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
            rv_list.setAdapter(adapterDCRCallSelection);
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i) {
        try {
            jsonObjectDob = new JSONObject(jsonObject.getString("DOB"));
            jsonObjectDow = new JSONObject(jsonObject.getString("DOW"));
            String dob = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_35, jsonObjectDob.getString("date"));
            String dow = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_35, jsonObjectDow.getString("date"));
            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "4", jsonObject.getString("CategoryName"), jsonObject.getString("Category"), jsonObject.getString("SpecialtyName"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addrs"), dob, dow, jsonObject.getString("Email"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("Qual"), "",jsonObject.getString("Doc_ClsCode"), jsonObject.getString("Specialty"),false));
            } else {
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "4", jsonObject.getString("CategoryName"), jsonObject.getString("Category"), jsonObject.getString("SpecialtyName"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addrs"), dob, dow, jsonObject.getString("Email"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("Qual"), "", jsonObject.getString("Doc_ClsCode"),jsonObject.getString("Specialty"),true));
            }
        } catch (Exception e) {
            Log.v("UNDRCALL", "--1111---" + e.toString());

        }
        return custListArrayList;
    }

    private void filter(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
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

        if(!territoryCode.isEmpty()) {
            tvTerritory.setVisibility(View.VISIBLE);
            img_del.setVisibility(View.VISIBLE);
        }else {
            tvTerritory.setVisibility(View.GONE);
            img_del.setVisibility(View.GONE);
        }

        if(!classCode.isEmpty()) {
            if(territoryCode.isEmpty()) {
                tvTerritory.setVisibility(View.INVISIBLE);
            }else {
                tv_add_condition.setVisibility(View.GONE);
            }
            tvClass.setVisibility(View.VISIBLE);
        }else {
            tvClass.setVisibility(View.GONE);
        }

        constraintLayout=dialogFilter.findViewById(R.id.constraint_btns);
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
            if(!classCode.isEmpty()) {
                classCode = "";
                className = "";
            }
            else if(!territoryCode.isEmpty()) {
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
            }else if(requiredList.equalsIgnoreCase("Class")){
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
            }
            filterSelectionList.clear();
            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject  jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"),jsonObject.getString("Code")));
            }

        } catch (Exception ignored) {

        }
    }

    public void Filtered() {
        FilltercustArraList.clear();
        if(specialityCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("") && territoryCode.equalsIgnoreCase("") && classCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(custListArrayList);
            tv_filterCount.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }else {
            for (CustList mList : custListArrayList) {
                if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)) {
                    FilltercustArraList.add(mList);
                }else if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && categoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && territoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && specialityCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else if(mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                }else {
                    if(mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    }else if(mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    }else if(mList.getTown_code().equalsIgnoreCase(territoryCode)
                            && specialityCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    }else if(mList.getClassCode().equalsIgnoreCase(classCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    }
                    tv_filterCount.setText(String.valueOf(FilltercustArraList.size()));
                }
            }
        }

        if(FilltercustArraList.isEmpty()){
            noULDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getUNLcap(requireContext()), getString(R.string.found)));
            noULDoctor.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        }else {
            noULDoctor.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            adapterDCRCallSelection.notifyDataSetChanged();
        }
        dialogFilter.dismiss();
    }

}