package saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.ChemistNeed;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.CipNeed;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.Designation;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DivCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DrGeoTag;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DrNeed;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.GeoTagApproval;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.HospNeed;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfType;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.StateCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.StockistNeed;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SubDivisionCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfName;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TpBasedDcr;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.UnDrNeed;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.lat;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.limitKm;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.lng;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class ListedDoctorFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static ListView filterList;
    public static ConstraintLayout constraintFilter;
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close, img_del;
    TextView tv_hqName, tv_add_condition, tv_filterCount;
    Button btn_apply, btn_clear;
    String specialityTxt, categoryTxt, territoryTxt;
    ListView lv_spec, lv_cate, lv_terr;
    SQLite sqLite;
    JSONArray jsonArray;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<String> filterSelectionList = new ArrayList<>();
    ArrayList<String> listOfItems = new ArrayList<>();
    TextView tvSpec, tvCate, tvTerritory, tvClass;
    JSONObject jsonObject;
    ApiInterface apiInterface;
    int count = 0;

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
        tv_hqName.setText(TodayPlanSfName);
        sqLite = new SQLite(getContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        custListArrayList.clear();
        SetupAdapter("", "");

        dialogFilter = new Dialog(requireContext());
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        img_close = dialogFilter.findViewById(R.id.img_close);
        img_del = dialogFilter.findViewById(R.id.img_del);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        //   rv_filter = dialogFilter.findViewById(R.id.rv_conditions);
        tv_add_condition = dialogFilter.findViewById(R.id.btn_add_condition);
        tvSpec = dialogFilter.findViewById(R.id.constraint_speciality);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);
        tvCate = dialogFilter.findViewById(R.id.constraint_category);
        tvClass = dialogFilter.findViewById(R.id.constraint_class);

        lv_spec = dialogFilter.findViewById(R.id.lv_spec);
        lv_cate = dialogFilter.findViewById(R.id.lv_category);
        lv_terr = dialogFilter.findViewById(R.id.lv_territory);

        tvSpec.setOnClickListener(view -> SetupListviewAdapter(lv_spec, "Speciality"));

        lv_spec.setOnItemClickListener((adapterView, view, i, l) -> {
            specialityTxt = lv_spec.getItemAtPosition(i).toString();
            tvSpec.setText(lv_spec.getItemAtPosition(i).toString());
            lv_spec.setVisibility(View.GONE);
            tv_add_condition.setVisibility(View.VISIBLE);
        });

        tvCate.setOnClickListener(view -> SetupListviewAdapter(lv_cate, "Category"));

        lv_cate.setOnItemClickListener((adapterView, view, i, l) -> {
            categoryTxt = lv_cate.getItemAtPosition(i).toString();
            tvCate.setText(lv_cate.getItemAtPosition(i).toString());
            lv_cate.setVisibility(View.GONE);
            tv_add_condition.setVisibility(View.VISIBLE);
        });

        tvTerritory.setOnClickListener(view -> SetupListviewAdapter(lv_terr, "Territory"));

        lv_terr.setOnItemClickListener((adapterView, view, i, l) -> {
            territoryTxt = lv_terr.getItemAtPosition(i).toString();
            tvTerritory.setText(lv_terr.getItemAtPosition(i).toString());
            lv_terr.setVisibility(View.GONE);
            tv_add_condition.setVisibility(View.VISIBLE);
        });

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);
        //  imm.hideSoftInputFromInputMethod(ed_search.getWindowToken(), 0);

        iv_filter.setOnClickListener(view -> {
            tvSpec.setText(specialityTxt);
            tvCate.setText(categoryTxt);
            tvTerritory.setText(territoryTxt);
            if (count == 3) {
                tvTerritory.setVisibility(View.VISIBLE);
            } else if (count == 4) {
                tvTerritory.setVisibility(View.VISIBLE);
                tvClass.setVisibility(View.VISIBLE);
            }

            dialogFilter.show();

            tv_add_condition.setOnClickListener(view13 -> {
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.VISIBLE);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE) {
                    tvTerritory.setVisibility(View.VISIBLE);
                }
            });

            img_del.setOnClickListener(view14 -> {
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvClass.getVisibility() == View.GONE) {
                    tvTerritory.setVisibility(View.GONE);
                    tvTerritory.setText(R.string.territory);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.GONE);
                    tvClass.setText(R.string.class_filter);
                }
            });


            btn_clear.setOnClickListener(view15 -> {
                tvSpec.setText(R.string.speciality);
                tvTerritory.setText(R.string.territory);
                tvCate.setText(R.string.category);
                tvClass.setText(R.string.class_filter);
           /*     ArrayFilteredList.clear();
                ArrayFilteredList.add(new FilterDataList("Speciality", 0));
                adapterFilterSelection = new AdapterFilterSelection(getContext(), ArrayFilteredList, custListArrayList);
                //   rv_filter.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                rv_filter.setAdapter(adapterFilterSelection);*/
            });

            img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

            btn_apply.setOnClickListener(view1 -> {
                dialogFilter.dismiss();
                custListArrayList.clear();
                listOfItems.clear();
                count = 0;
                if (!tvSpec.getText().toString().equalsIgnoreCase("Speciality") && !tvSpec.getText().toString().isEmpty()) {
                   /* filteredDataList.add(new FilterDataList(tvSpec.getText().toString(), "Speciality"));
                    listOfItems.add("Speciality");*/
                    // SetupAdapter(tvSpec.getText().toString(), "Speciality");
                    specialityTxt = tvSpec.getText().toString();
                    count++;
                } else {
                    specialityTxt = "Speciality";
                }

                if (!tvCate.getText().toString().equalsIgnoreCase("Category") && !tvCate.getText().toString().isEmpty()) {
                    //  SetupAdapter(tvCate.getText().toString(), "Category");
                    categoryTxt = tvCate.getText().toString();
                    count++;
                } else {
                    categoryTxt = "Category";
                }

                if (!tvTerritory.getText().toString().equalsIgnoreCase("Territory") && !tvTerritory.getText().toString().isEmpty()) {
                    // SetupAdapter(tvTerritory.getText().toString(), "Territory");
                    territoryTxt = tvTerritory.getText().toString();
                    count++;
                } else {
                    territoryTxt = "Territory";
                }

                if (count == 0) {
                    SetupAdapter("", "");
                }

                tv_filterCount.setText(String.valueOf(count));

             /*   int count = 0;
                for (int i = 0; i < ArrayFilteredList.size(); i++) {
                    if (ArrayFilteredList.get(i).getName().equalsIgnoreCase("Speciality") || ArrayFilteredList.get(i).getName().equalsIgnoreCase("Category") || ArrayFilteredList.get(i).getName().equalsIgnoreCase("Qualification") || ArrayFilteredList.get(i).getName().equalsIgnoreCase("Class")) {

                    } else {
                        Log.v("sss", ArrayFilteredList.get(i).getName());
                        filter(ArrayFilteredList.get(i).getName());
                        count++;
                    }

                }
                tv_filterCount.setText(String.valueOf(count));*/
            });

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


    private void SetupListviewAdapter(ListView lv, String str_filter) {
        if (lv.getVisibility() == View.VISIBLE) {
            lv.setVisibility(View.GONE);
            tv_add_condition.setVisibility(View.VISIBLE);
        } else {
            filterSelectionList.clear();
            switch (str_filter) {
                case "Speciality":
                    getFilterList("Speciality");
                    // filterSelectionList.add(custListFully.get(i).getSpecialist());
                    break;
                case "Category":
                    getFilterList("Category");
                    //filterSelectionList.add(custListFully.get(i).getCategory());
                    break;
                case "Territory":
                    getFilterList("Territory");
                    //filterSelectionList.add(custListFully.get(i).getTown_name());
                    break;
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.listview_items, filterSelectionList);
            lv.setAdapter(arrayAdapter);
            lv.setVisibility(View.VISIBLE);
            tv_add_condition.setVisibility(View.INVISIBLE);
        }
    }

    private void getFilterList(String requiredList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (requiredList.equalsIgnoreCase("Speciality")) {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.SPECIALITY);
            } else if (requiredList.equalsIgnoreCase("Category")) {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.CATEGORY);
            } else if (requiredList.equalsIgnoreCase("Territory")) {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + TodayPlanSfCode);
            }

            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(jsonObject.getString("Name"));
            }
        } catch (Exception ignored) {

        }
    }

    public void prepareMasterToSync(String hqCode) {
        masterSyncArray.clear();

        if (DrNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel doctorModel = new MasterSyncItemModel("Doctor", "getdoctors", Constants.DOCTOR + hqCode);
            masterSyncArray.add(doctorModel);
        }

        if (ChemistNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel cheModel = new MasterSyncItemModel("Doctor", "getchemist", Constants.CHEMIST + hqCode);
            masterSyncArray.add(cheModel);
        }

        if (StockistNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel stockModel = new MasterSyncItemModel("Doctor", "getstockist", Constants.STOCKIEST + hqCode);
            masterSyncArray.add(stockModel);
        }

        if (UnDrNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel unListModel = new MasterSyncItemModel("Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode);
            masterSyncArray.add(unListModel);
        }

        if (HospNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel hospModel = new MasterSyncItemModel("Doctor", "gethospital", Constants.HOSPITAL + hqCode);
            masterSyncArray.add(hospModel);
        }
        if (CipNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel ciModel = new MasterSyncItemModel("Doctor", "getcip", Constants.CIP + hqCode);
            masterSyncArray.add(ciModel);
        }
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Subordinate", "getjointwork", Constants.JOINT_WORK + hqCode);
        masterSyncArray.add(jWorkModel);

        for (int i = 0; i < masterSyncArray.size(); i++) {
            sync(masterSyncArray.get(i), hqCode);
        }
    }

    public void sync(MasterSyncItemModel masterSyncItemModel, String hqCode) {

        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", SfCode);
                jsonObject.put("division_code", DivCode);
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", SfType);
                jsonObject.put("Designation", Designation);
                jsonObject.put("state_code", StateCode);
                jsonObject.put("subdivision_code", SubDivisionCode);

                Call<JsonElement> call = null;
                Map<String, String> mapString = new HashMap<>();
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Doctor")) {
                    mapString.put("axn", "table/dcrmasterdata");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()),mapString,jsonObject.toString());
                } else if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Subordinate")) {
                    mapString.put("axn", "table/subordinates");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()),mapString,jsonObject.toString());
                }

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            if (response.isSuccessful()) {
                                try {
                                    JsonElement jsonElement = response.body();
                                    JSONArray jsonArray = new JSONArray();
                                    assert jsonElement != null;
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                            if (!jsonObject1.has("success")) { // json object with "success" : "fail" will be received only when api call is failed ,"success will not be received when api call is success
                                                jsonArray.put(jsonObject1);
                                                success = true;
                                            } else if (jsonObject1.has("success") && !jsonObject1.getBoolean("success")) {
                                                sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        if (success) {
                                            sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0);
                                        }
                                    } else {
                                        sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.ShowToast(context, context.getString(R.string.no_network), 100);
        }
    }

    private void SetupAdapter(String filteredStr, String RequiredFiltered) {
        try {
            if (!sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode)) {
                prepareMasterToSync(DcrCallTabLayoutActivity.TodayPlanSfCode);
            } else {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode);
            }
            if (jsonArray.length() == 0) {
                commonUtilsMethods.ShowToast(context, context.getString(R.string.no_data_found)  + "  " +  context.getString(R.string.do_master_sync) , 100);
            }

            Log.v("DrCall", "-dr_full_length-" + jsonArray.length());
            Log.v("DrCall", "--dr-" + GeoTagApproval + "--" + DrGeoTag + "----" + TpBasedDcr);

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    if (DrGeoTag.equalsIgnoreCase("1")) {
                        if (!jsonObject.getString("Lat").isEmpty() && !jsonObject.getString("Long").isEmpty()) {
                            if (GeoTagApproval.equalsIgnoreCase("0")) {
                                Log.v("DrCall", "111");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), lat, lng, distance);
                                if (distance[0] < limitKm * 1000.0) {
                                    if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                        custListArrayList = SaveData(jsonObject, i);
                                    }
                                }
                            } else {
                                Log.v("DrCall", "222");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), lat, lng, distance);
                                if (distance[0] < limitKm * 1000.0) {
                                    custListArrayList = SaveData(jsonObject, i);
                                }
                            }
                        }
                    } else {
                        Log.v("DrCall", "333");
                        if (TpBasedDcr.equalsIgnoreCase("0")) {
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
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).isClusterAvailable()));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getSpecialistCode(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(), custListArrayList.get(i).getMappedBrands(), custListArrayList.get(i).getMappedSlides(), custListArrayList.get(i).getTotalVisitCount(), custListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("DrCall", "-dr--error-2-" + e);
        }

        Log.v("call", "-dr--size--" + custListArrayList.size());
        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), custListArrayList);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        Collections.sort(custListArrayList, Comparator.comparing(CustList::getTown_name));
        Collections.sort(custListArrayList, Comparator.comparing(CustList::isClusterAvailable));
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i) {

        try {
            String brands = getBrands(jsonObject.getString("MappProds"));
            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                if (jsonObject.has("Product_Code")) {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code"), brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"), false));
                } else {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), "", brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"), false));
                }
            } else {
                if (jsonObject.has("Product_Code")) {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code"), brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"), true));
                } else {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), "", brands, jsonObject.getString("MProd"), jsonObject.getString("Tlvst"), true));
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
}