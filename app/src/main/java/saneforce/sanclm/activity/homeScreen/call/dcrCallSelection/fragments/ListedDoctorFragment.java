package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.ChemistNeed;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.CipNeed;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.Designation;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DivCode;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DrGeoTag;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.GeoTagApproval;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.HospNeed;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfCode;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfType;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.StateCode;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.StockistNeed;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SubDivisionCode;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfName;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TpBasedDcr;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.UndrNeed;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.laty;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.limitKm;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.lngy;

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
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.FilterDataList;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.AdapterFilterSelection;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class ListedDoctorFragment extends Fragment {
    public static ListView filterList;
    public static ConstraintLayout constraintFilter;
    public static AdapterFilterSelection adapterFilterSelection;
    public static ArrayList<FilterDataList> ArrayFilteredList = new ArrayList<>();
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
    ArrayAdapter arrayAdapter;
    CommonUtilsMethods commonUtilsMethods;
    ArrayList<String> filterSelectionList = new ArrayList<>();
    ArrayList<FilterDataList> filteredDataList = new ArrayList<>();
    ArrayList<String> listOfItems = new ArrayList<>();
    TextView tvSpec, tvCate, tvTerriorty, tvClass;
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
        commonUtilsMethods = new CommonUtilsMethods(getActivity());

        custListArrayList.clear();
        SetupAdapter("", "");

        dialogFilter = new Dialog(getActivity());
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        img_close = dialogFilter.findViewById(R.id.img_close);
        img_del = dialogFilter.findViewById(R.id.img_del);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        //   rv_filter = dialogFilter.findViewById(R.id.rv_conditions);
        tv_add_condition = dialogFilter.findViewById(R.id.btn_add_condition);
        tvSpec = dialogFilter.findViewById(R.id.constraint_speciality);
        tvTerriorty = dialogFilter.findViewById(R.id.constraint_territory);
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

        tvTerriorty.setOnClickListener(view -> SetupListviewAdapter(lv_terr, "Territory"));

        lv_terr.setOnItemClickListener((adapterView, view, i, l) -> {
            territoryTxt = lv_terr.getItemAtPosition(i).toString();
            tvTerriorty.setText(lv_terr.getItemAtPosition(i).toString());
            lv_terr.setVisibility(View.GONE);
            tv_add_condition.setVisibility(View.VISIBLE);
        });

      /*  ArrayFilteredList.clear();
        ArrayFilteredList.add(new FilterDataList("Speciality", 0));
        adapterFilterSelection = new AdapterFilterSelection(getContext(), ArrayFilteredList, custListArrayList);*/
        //rv_filter.setLayoutManager(new CustomGridLayoutManager(getContext()));
      /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_filter.setLayoutManager(linearLayoutManager);*/

     /*   LinearLayoutPagerManager linearLayoutPagerManager = new LinearLayoutPagerManager(getContext(), RecyclerView.VERTICAL, false, 2);
        rv_filter.setLayoutManager(linearLayoutPagerManager);*/

        // rv_filter.setLayoutManager(new LinearLayoutPagerManager(getContext(), LinearLayoutManager.VERTICAL, false, 2));
        /*rv_filter.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        rv_filter.setAdapter(adapterFilterSelection);*/

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

        iv_filter.setOnClickListener(view -> {
            tvSpec.setText(specialityTxt);
            tvCate.setText(categoryTxt);
            tvTerriorty.setText(territoryTxt);
            if (count == 3) {
                tvTerriorty.setVisibility(View.VISIBLE);
            } else if (count == 4) {
                tvTerriorty.setVisibility(View.VISIBLE);
                tvClass.setVisibility(View.VISIBLE);
            }

            dialogFilter.show();

            tv_add_condition.setOnClickListener(view13 -> {
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerriorty.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.VISIBLE);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE) {
                    tvTerriorty.setVisibility(View.VISIBLE);
                }
            });

            img_del.setOnClickListener(view14 -> {
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvClass.getVisibility() == View.GONE) {
                    tvTerriorty.setVisibility(View.GONE);
                    tvTerriorty.setText(R.string.territory);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerriorty.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.GONE);
                    tvClass.setText(R.string.class_filter);
                }
               /* int count = ArrayFilteredList.size();
                ArrayFilteredList.remove(count - 1);
                adapterFilterSelection.notifyDataSetChanged();*/
            });


            btn_clear.setOnClickListener(view15 -> {
                tvSpec.setText(R.string.speciality);
                tvTerriorty.setText(R.string.territory);
                tvCate.setText(R.string.category);
                tvClass.setText(R.string.class_filter);
           /*     ArrayFilteredList.clear();
                ArrayFilteredList.add(new FilterDataList("Speciality", 0));
                adapterFilterSelection = new AdapterFilterSelection(getContext(), ArrayFilteredList, custListArrayList);
                //   rv_filter.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                rv_filter.setAdapter(adapterFilterSelection);*/
            });

            img_close.setOnClickListener(view12 -> {
                dialogFilter.dismiss();
                //commonUtilsMethods.FullScreencall();
            });

            btn_apply.setOnClickListener(view1 -> {
                dialogFilter.dismiss();
                // commonUtilsMethods.FullScreencall();
                custListArrayList.clear();
                filteredDataList.clear();
                listOfItems.clear();
                count = 0;
                if (!tvSpec.getText().toString().equalsIgnoreCase("Speciality") && !tvSpec.getText().toString().isEmpty()) {
                   /* filteredDataList.add(new FilterDataList(tvSpec.getText().toString(), "Speciality"));
                    listOfItems.add("Speciality");*/
                    SetupAdapter(tvSpec.getText().toString(), "Speciality");
                    count++;
                } else {
                    specialityTxt = "Speciality";
                }

                if (!tvCate.getText().toString().equalsIgnoreCase("Category") && !tvCate.getText().toString().isEmpty()) {
                    SetupAdapter(tvCate.getText().toString(), "Category");
                    count++;
                } else {
                    categoryTxt = "Category";
                }

                if (!tvTerriorty.getText().toString().equalsIgnoreCase("Territory") && !tvTerriorty.getText().toString().isEmpty()) {
                    SetupAdapter(tvTerriorty.getText().toString(), "Territory");
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
                        Log.v("ttrtr", ArrayFilteredList.get(i).getName());
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

            arrayAdapter = new ArrayAdapter(getContext(), R.layout.listview_items, filterSelectionList);
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
        } catch (Exception e) {

        }
    }

    public void prepareMasterToSync(String hqCode) {
        masterSyncArray.clear();
        MasterSyncItemModel doctorModel = new MasterSyncItemModel("Doctor", "getdoctors", Constants.DOCTOR + hqCode);
        masterSyncArray.add(doctorModel);

        if (ChemistNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel cheModel = new MasterSyncItemModel("Doctor", "getchemist", Constants.CHEMIST + hqCode);
            masterSyncArray.add(cheModel);
        }

        if (StockistNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel stockModel = new MasterSyncItemModel("Doctor", "getstockist", Constants.STOCKIEST + hqCode);
            masterSyncArray.add(stockModel);
        }

        if (UndrNeed.equalsIgnoreCase("0")) {
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

        // MasterSyncItemModel cluster = new MasterSyncItemModel("Doctor", "getterritory", Constants.CLUSTER + hqCode);
        // masterSyncArray.add(cluster);

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

// Log.e("test","master sync obj in TP : " + jsonObject);
                Call<JsonElement> call = null;
                if (masterSyncItemModel.getMasterFor().equalsIgnoreCase("Doctor")) {
                    call = apiInterface.getDrMaster(jsonObject.toString());
                } else if (masterSyncItemModel.getMasterFor().equalsIgnoreCase("Subordinate")) {
                    call = apiInterface.getSubordinateMaster(jsonObject.toString());
                }

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                            boolean success = false;
                            if (response.isSuccessful()) {
// Log.e("test","response : " + masterSyncItemModel.getRemoteTableName() +" : " + response.body().toString());
                                try {
                                    JsonElement jsonElement = response.body();
                                    JSONArray jsonArray = new JSONArray();
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
            Toast.makeText(context, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    private void SetupAdapter(String filteredStr, String RequiredFiltered) {
        try {

            //jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode);
            if (!sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode)) {
                prepareMasterToSync(DcrCallTabLayoutActivity.TodayPlanSfCode);
            } else {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode);
            }
            if (jsonArray.length() == 0) {
                Toast.makeText(getContext(), Constants.NO_DATA_AVAILABLE + "  Kindly Do MasterSync", Toast.LENGTH_SHORT).show();
            }

          /*  boolean isDataAvailable = false;
            isDataAvailable = sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode);

            if (isDataAvailable) {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode);
            } else {
                MasterSyncActivity masterSyncActivity = new MasterSyncActivity();
                masterSyncActivity.masterSyncAll(true);
            }*/

            Log.v("call", "-dr_full_length-" + jsonArray.length());
         /*   if (jsonArray.length() == 0) {
                if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                    // Toast.makeText(getActivity(), "No Data Available!", Toast.LENGTH_SHORT).show();
                    // MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Doctor", "getdoctors", SfCode, SharedPref.getDivisionCode(TagCustSelectionList.this), selectedHqCode, SfType, SharedPref.getDesignationName(TagCustSelectionList.this), SharedPref.getStateCode(TagCustSelectionList.this), SharedPref.getSubdivCode(TagCustSelectionList.this));
                } else {
                    Toast.makeText(getActivity(), Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }*/

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);

               /* if (filteredDataList.size() > 0) {
                    for (int j = 0; j < filteredDataList.size(); j++) {
                        if (filteredDataList.size() == 1) {
                            if (filteredDataList.get(j).getIscase().equalsIgnoreCase("Speciality")) {
                                if (filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Specialty"))) {
                                    AssignData(i);
                                }
                            } else if (filteredDataList.get(j).getIscase().equalsIgnoreCase("Category")) {
                                if (filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Category"))) {
                                    AssignData(i);
                                }
                            } else if (filteredDataList.get(j).getIscase().equalsIgnoreCase("Territory")) {
                                if (filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Town_Name"))) {
                                    AssignData(i);
                                }
                            }
                        } else if (filteredDataList.size() == 2) {
                            if (filteredDataList.get(j).getIscase().contains("Speciality") && filteredDataList.get(j).getIscase().contains("Category")) {
                                if (filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Specialty")) && filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Category"))) {
                                    AssignData(i);
                                }
                            }
                        }
                        if (filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Specialty")) && filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Category")) && filteredDataList.get(j).getName().equalsIgnoreCase(jsonObject.getString("Town_Name"))) {
                            AssignData(i);
                        }
                    }
                } else {
                    AssignData(i);
                }*/

                switch (RequiredFiltered) {
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
                }
            }

            int count = custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode()));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode()));
                    }
                }
            }
        } catch (Exception e) {
            Log.v("call", "-dr--error--" + e);
        }

        Log.v("call", "-dr--size--" + custListArrayList.size());
        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), custListArrayList);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        Collections.sort(custListArrayList, Comparator.comparing(CustList::getTown_name));
    }

    private void AssignData(int i) {
        try {
            Log.v("chkgeotag", "--dr-" + GeoTagApproval + "--" + DrGeoTag + "----" + TpBasedDcr);
            if (DrGeoTag.equalsIgnoreCase("1")) {
                if (!jsonObject.getString("Lat").isEmpty() && !jsonObject.getString("Long").isEmpty()) {
                    if (GeoTagApproval.equalsIgnoreCase("0")) {
                        Log.v("chkgeotag", "111");
                        float[] distance = new float[2];
                        Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), laty, lngy, distance);
                        if (distance[0] < limitKm * 1000.0) {
                            if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                            }
                        }
                    } else {
                        Log.v("chkgeotag", "222");
                        float[] distance = new float[2];
                        Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), laty, lngy, distance);
                        if (distance[0] < limitKm * 1000.0) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                        }
                    }
                }
            } else {
                Log.v("chkgeotag", "333");
                if (TpBasedDcr.equalsIgnoreCase("0")) {
                    Log.v("chkgeotag", "444");
                    if (SharedPref.getTodayDayPlanClusterCode(requireContext()).equalsIgnoreCase(jsonObject.getString("Town_Code"))) {
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                    }
                } else {
                    Log.v("chkgeotag", "555");
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("CategoryCode"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("DOB"), jsonObject.getString("DOW"), jsonObject.getString("DrEmail"), jsonObject.getString("Mobile"), jsonObject.getString("Phone"), jsonObject.getString("DrDesig"), jsonObject.getString("Product_Code")));
                }
            }
        } catch (Exception e) {
            Log.v("call", "dr--error--" + e);
        }
    }

    private void filter(String text) {
        ArrayList<CustList> filterdNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filterdNames);
    }
}