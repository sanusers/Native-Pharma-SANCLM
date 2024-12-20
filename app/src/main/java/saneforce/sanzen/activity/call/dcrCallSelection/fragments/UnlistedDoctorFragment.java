package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;


public class UnlistedDoctorFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> filteredNames = new ArrayList<>();
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
    FloatingActionButton btn_add;

    String specialityCode = "", categoryCode = "", territoryCode = "", classCode = "";
    String specialityName = "", categoryName = "", territoryName = "", className = "";
    String SfType = "", SfCode = "", SfName = "", DivCode = "", terrname = "", terrcode = "";
    String txt_qua = "", txt_cat = "", txt_class = "", txt_spec = "", txt_terr = "", hospitaltxt="";
    TextView tv_hqName, tv_add_condition, tv_filterCount;
    Button btn_apply, btn_clear, save_btn;
    TextView txt_select_qua, txt_select_category, txt_select_class, txt_select_spec, txt_select_terr, tv_hospital, txt_select_hospital;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private ProgressDialog progressDialog;
    ApiInterface apiInterface;
    ArrayList<MasterSyncItemModel> UnlistedModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();
    int UnlistedStatus = 0;

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
        btn_add=v.findViewById(R.id.add_unlst);
        if (SharedPref.getUnlistAddition(context).equalsIgnoreCase("0")) {
            btn_add.setVisibility(View.VISIBLE);
        } else {
            btn_add.setVisibility(View.GONE);
        }
        iv_filter.setOnClickListener(view -> {
            CustomizeFiltered();
        });
        btn_add.setOnClickListener(view -> {
            popupAddUnlisted();
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
                if (SharedPref.getGeotagNeedUnlst(context).equalsIgnoreCase("1") && HomeDashBoard.selectedDate.isEqual(LocalDate.now())) {
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
            JSONArray qualifications = masterDataDao.getMasterDataTableOrNew(Constants.QUALIFICATION).getMasterSyncDataJsonArray();
            HashMap<String, String> qualificationMap = new HashMap<>();
            for (int index = 0; index < qualifications.length(); index++) {
                JSONObject jsonObject1 = qualifications.getJSONObject(index);
                qualificationMap.put(jsonObject1.getString("Code"), jsonObject1.getString("Name"));
            }

            jsonObjectDob = new JSONObject(jsonObject.getString("DOB"));
            jsonObjectDow = new JSONObject(jsonObject.getString("DOW"));
            String dob = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_35, jsonObjectDob.getString("date"));
            String dow = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_35, jsonObjectDow.getString("date"));
            String qualification = qualificationMap.get(jsonObject.getString("Qual"));
            if(qualification == null) qualification = "";
            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "4", jsonObject.getString("CategoryName"), jsonObject.getString("Category"), jsonObject.getString("SpecialtyName"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addrs"), dob, dow, jsonObject.getString("Email"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), qualification, "",jsonObject.getString("Doc_ClsCode"), jsonObject.getString("Specialty"),false));
            } else {
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "4", jsonObject.getString("CategoryName"), jsonObject.getString("Category"), jsonObject.getString("SpecialtyName"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("Addrs"), dob, dow, jsonObject.getString("Email"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), qualification, "", jsonObject.getString("Doc_ClsCode"),jsonObject.getString("Specialty"),true));
            }
        } catch (Exception e) {
            Log.v("UNDRCALL", "--1111---" + e.toString());

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
        ArrayList<CustList> filterCusList = new ArrayList<>();
        if(!filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        }else {
            filterCusList.addAll(custListArrayList);
        }
        FilltercustArraList.clear();
        if(specialityCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("") && territoryCode.equalsIgnoreCase("") && classCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(filterCusList);
            tv_filterCount.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }else {
            for (CustList mList : filterCusList) {
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
            adapterDCRCallSelection.filterList(FilltercustArraList);
        }
        dialogFilter.dismiss();
    }

    public void popupAddUnlisted() {
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_add_dcr);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        txt_select_qua = dialog.findViewById(R.id.txt_select_qua);
        txt_select_category = dialog.findViewById(R.id.txt_select_category);
        txt_select_class = dialog.findViewById(R.id.txt_select_class);
        txt_select_spec = dialog.findViewById(R.id.txt_select_spec);
        txt_select_terr = dialog.findViewById(R.id.txt_select_terr);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        save_btn = dialog.findViewById(R.id.btn_save);
        TextView drtagname = dialog.findViewById(R.id.drtagname);
        final EditText edt_dr = dialog.findViewById(R.id.edt_dr);
        final EditText edt_addr = dialog.findViewById(R.id.edt_addr);
        final EditText edt_mob = dialog.findViewById(R.id.edt_mob);
        final EditText edt_code = dialog.findViewById(R.id.edt_code);
        final EditText edt_phone = dialog.findViewById(R.id.edt_phone);
        RelativeLayout hos_dropdown = dialog.findViewById(R.id.lnhosdropdown);
        txt_select_hospital = dialog.findViewById(R.id.txt_select_hospitals);
        if (SharedPref.getUNLcap(requireContext()).isEmpty() || SharedPref.getChmCap(requireContext()) == null) {
            drtagname.setText(getResources().getString(R.string.add) + " " + "Unlisted Doctor");
        } else {
            drtagname.setText(getResources().getString(R.string.add) + " " + SharedPref.getUNLcap(requireContext()));
        }
        if (SharedPref.getHospNeed(context).equalsIgnoreCase("0"))
            hos_dropdown.setVisibility(View.VISIBLE);
        else
            hos_dropdown.setVisibility(View.GONE);

        save_btn.setOnClickListener(v -> {
            save_btn.setEnabled(false);
            if (!txt_select_qua.getText().toString().isEmpty() && !txt_select_category.getText().toString().isEmpty()
                    && !txt_select_class.getText().toString().isEmpty() && !txt_select_spec.getText().toString().isEmpty()
                    && !txt_select_terr.getText().toString().isEmpty() && !edt_dr.getText().toString().isEmpty()
                    && !edt_dr.getText().toString().contains("'")) {
                Log.v("qualification_txt", "arent_empty");
                save_btn.setEnabled(false);
                JSONObject json = new JSONObject();
                try {
                    SfType = SharedPref.getSfType(requireContext());
                    SfName = SharedPref.getSfName(requireContext());
                    DivCode = SharedPref.getDivisionCode(requireContext());
                    json.put("tableName", "savenewdr");
//                    if (SF_Type.equalsIgnoreCase("2"))
//                        json.put("sfcode", SF_coding.get(spinnerpostion));
//                    else
//                        json.put("sfcode", SF_Code);
                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                        SfCode = SharedPref.getHqCode(requireContext());
                    }
                    else {
                        SfCode = SharedPref.getSfCode(requireContext());
                    }
                    json.put("sfcode", SfCode);
                    json.put("division_code", DivCode);
                    json.put("DrName", edt_dr.getText().toString());
                    json.put("DrQCd", txt_qua);
                    json.put("DrQNm", txt_select_qua.getText().toString());
                    json.put("DrClsCd", txt_class);
                    json.put("DrClsNm", txt_select_class.getText().toString());
                    json.put("DrCatCd", txt_cat);
                    json.put("DrCatNm", txt_select_category.getText().toString());
                    json.put("DrSpcCd", txt_spec);
                    json.put("DrSpcNm", txt_select_spec.getText().toString());
                    json.put("DrAddr", edt_addr.getText().toString());
                    json.put("DrTerCd", txt_terr);
                    json.put("DrTerNm", txt_select_terr.getText().toString());
                    json.put("DrPincd", edt_code.getText().toString());
                    json.put("DrPhone", edt_phone.getText().toString());
                    json.put("DrMob", edt_mob.getText().toString());
                    if (SharedPref.getHospNeed(context).equalsIgnoreCase("0")) {
                        json.put("DrHosNm", txt_select_hospital.getText().toString());
                        json.put("DrHosCd", hospitaltxt);
                    }
                    Log.v("printing_add_dr", json.toString());
                    save_btn.setEnabled(false);
                    addDoctor(json.toString(), dialog);
                }
                catch (Exception e) {
                    save_btn.setEnabled(true);
                }
            }
            else {
                if (edt_dr.getText().toString().contains("'")) {
                    edt_dr.setError("Invalid Character");
                    save_btn.setEnabled(true);
                } else {
                    commonUtilsMethods.showToastMessage(getActivity(), getResources().getString(R.string.fill_all));
                    save_btn.setEnabled(true);
                }
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                commonFun();
            }
        });

        txt_select_qua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingTableValue(1);
            }
        });

        txt_select_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingTableValue(2);
            }
        });

        txt_select_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingTableValue(3);
            }
        });

        txt_select_spec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingTableValue(4);
            }
        });

        txt_select_terr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingTableValue(5);
            }
        });

        txt_select_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingTableValue(6);
            }
        });

    }

    public void commonFun() {
        try {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } catch (Exception e) {

        }
    }
    public void gettingTableValue(int x) {
        try {
            filterSelectionList.clear();
            JSONArray jsonArray = new JSONArray();
            if (x == 1){
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.QUALIFICATION).getMasterSyncDataJsonArray();
            }
            else if (x == 2){
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
            }
            else if (x == 3){
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
            }
            else if (x == 4){
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            }
            else if (x == 6) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL +  DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            }
            else{
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            }
            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"), jsonObject.getString("Code")));
            }
        }
        catch (Exception ignored)
        {
        }
        popupSpinner(x);
    }
    public void popupSpinner(final int x) {
        String a = "undr";
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_spinner_selection);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ListView popup_list = dialog.findViewById(R.id.popup_list);
        ImageView close_btn = dialog.findViewById(R.id.close_img);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        AdapterPopupSpinnerSelection popupAdapter = new AdapterPopupSpinnerSelection(getActivity(), filterSelectionList, a);
        popup_list.setAdapter(popupAdapter);
        popupAdapter.notifyDataSetChanged();

        popup_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("qualification_select", filterSelectionList.get(position).getName());
                if (x == 1) {
                    txt_select_qua.setText(filterSelectionList.get(position).getName());
                    txt_qua =filterSelectionList.get(position).getCode();
                } else if (x == 2) {
                    txt_select_category.setText(filterSelectionList.get(position).getName());
                    txt_cat = filterSelectionList.get(position).getCode();
                } else if (x == 3) {
                    txt_select_class.setText(filterSelectionList.get(position).getName());
                    txt_class = filterSelectionList.get(position).getCode();
                } else if (x == 4) {
                    txt_select_spec.setText(filterSelectionList.get(position).getName());
                    txt_spec = filterSelectionList.get(position).getCode();
                } else if (x == 6) {
                    txt_select_hospital.setText(filterSelectionList.get(position).getName());
                    hospitaltxt = filterSelectionList.get(position).getCode();
                } else {
                    txt_select_terr.setText(filterSelectionList.get(position).getName());
                    txt_terr = filterSelectionList.get(position).getCode();
                }
                dialog.dismiss();
            }
        });
    }
    public void addDoctor(String val, final Dialog dialog) {
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
                                SyncUnlisted(SfCode);
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
            }
            else {
                save_btn.setEnabled(true);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(getActivity(), getResources().getString(R.string.poor_connection));
            }

        }
        catch (Exception e) {
            save_btn.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public void SyncUnlisted(String hqCode) {
        UnlistedModelArray.clear();
        UnlistedStatus = masterDataDao.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR + hqCode);
        MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(getActivity()),  Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, UnlistedStatus, false);
        UnlistedModelArray.add(unListModel);
        arrayForAdapter.clear();
        arrayForAdapter.addAll(UnlistedModelArray);
        populateAdapter(arrayForAdapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        try{
            for (int i = 0; i < masterSyncItemModels.size(); i++) {
                MasterSyncItemModel item = masterSyncItemModels.get(i);
                sync(item.getMasterOf(), item.getRemoteTableName(), UnlistedModelArray, i);
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
                    @SuppressLint("NotifyDataSetChanged")
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

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    }
                });
            }
        } catch (Exception e) {
            Log.v("masterCheck", "--error-" + e);
            e.printStackTrace();
        }

    }

}