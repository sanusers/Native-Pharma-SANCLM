package saneforce.sanzen.activity.presentation.customerSelection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.activityModule.DynamicActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanzen.activity.presentation.customerSelection.adapter.CustomerListSelectionAdapter;
import saneforce.sanzen.activity.presentation.customerSelection.model.CustomerDataModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityCustomerSelectionBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class CustomerSelectionActivity extends AppCompatActivity {
    private ActivityCustomerSelectionBinding binding;
    private final ArrayList<CustomerDataModel> customerDataList = new ArrayList<>();
    private final ArrayList<CustomerDataModel> filteredCustomerDataList = new ArrayList<>();
    private final ArrayList<CustomerDataModel> filteredNames = new ArrayList<>();
    private CustomerListSelectionAdapter customerListSelectionAdapter;
    private Dialog dialogFilter;
    private String specialityCode = "", categoryCode = "", territoryCode = "", classCode = "";
    private String specialityName = "", categoryName = "", territoryName = "", className = "";
    private CommonUtilsMethods commonUtilsMethods;
    private final ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    private JSONObject jsonObject;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private PresentationDataDao presentationDataDao;
    private String selectedHQ = "", customerType = "", selectedCustomerCaption = "Customer", isFrom = "", presentationName = "";
    private Set<String> selectedCustomerCodes = new HashSet<>();
    public static final String PRESENTATION_NAME = "PRESENTATION_NAME", IS_FROM = "IS_FROM", CUSTOMER_TYPE = "CUSTOMER_TYPE", HEAD_QUARTER_CODE = "HEAD_QUARTER_CODE";
    private PresentationDataTable presentationDataTable = new PresentationDataTable();
    private List<String> SynqList = new ArrayList<>();
    private ApiInterface apiInterface;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            binding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            roomDB = RoomDB.getDatabase(this);
            masterDataDao = roomDB.masterDataDao();
            presentationDataDao = roomDB.presentationDataDao();
            commonUtilsMethods = new CommonUtilsMethods(this);
            customerDataList.clear();
            selectedHQ = SharedPref.getHqCode(this);
            String hqName = getHQName(SharedPref.getHqCode(this));
            binding.txthqName.setText(hqName);
            selectedCustomerCodes = new HashSet<>();

            if(!SharedPref.getSfType(this).equalsIgnoreCase("1")) {
                binding.imgArrow.setVisibility(View.VISIBLE);
            }else {
                binding.imgArrow.setVisibility(View.GONE);
            }

            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                customerType = bundle.getString(CUSTOMER_TYPE);
                if(bundle.containsKey(IS_FROM)) {
                    isFrom = bundle.getString(IS_FROM);
                }
                if(bundle.containsKey(PRESENTATION_NAME)) {
                    presentationName = bundle.getString(PRESENTATION_NAME);
                }
                if(bundle.containsKey(HEAD_QUARTER_CODE)) {
                    selectedHQ = bundle.getString(HEAD_QUARTER_CODE);
                }

                if(customerType != null) {
                    switch (customerType){
                        case Constants.DOCTOR:
                            selectedCustomerCaption = SharedPref.getDrCap(this);
                            break;
                        case Constants.CHEMIST:
                            selectedCustomerCaption = SharedPref.getChmCap(this);
                            break;
                        case Constants.STOCKIEST:
                            selectedCustomerCaption = SharedPref.getStkCap(this);
                            break;
                        case Constants.UNLISTED_DOCTOR:
                            selectedCustomerCaption = SharedPref.getUNLcap(this);
                            break;
                   /*     case Constants.DOCTOR_MAS:
                            selectedCustomerCaption = SharedPref.getDrCap(this);
                            break;
                        case Constants.CHEMIST_MAS:
                            selectedCustomerCaption = SharedPref.getChmCap(this);
                            break;
                        case Constants.STOCKIEST_MAS:
                            selectedCustomerCaption = SharedPref.getStkCap(this);
                            break;
                        case Constants.UNLISTED_DOCTOR_MAS:
                            selectedCustomerCaption = SharedPref.getUNLcap(this);
                            break;*/
                        case Constants.CIP:
                            selectedCustomerCaption = SharedPref.getCipCaption(this);
                            break;
                        case Constants.HOSPITAL:
                            selectedCustomerCaption = SharedPref.getHospCaption(this);
                            break;
                    }
                }
            }

            binding.title.setText(String.format("Select %s", selectedCustomerCaption));

        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.backArrow.setOnClickListener(v -> finish());

        if(isFrom.equalsIgnoreCase("edit") && !presentationName.isEmpty()) {
            presentationDataTable = presentationDataDao.getPresentationData(presentationName);
            String[] selectedCustomers = presentationDataTable.getCustomerCodes().split(", ");
            selectedCustomerCodes.addAll(Arrays.asList(selectedCustomers));
            binding.btnNext.setText(getString(R.string.save));
            binding.txthqName.setText(getHQName(selectedHQ));
        }else {
            binding.btnNext.setText(getString(R.string.next));
        }

        binding.btnNext.setOnClickListener(v -> {
            if(selectedCustomerCodes.isEmpty()) {
                commonUtilsMethods.showToastMessage(this, "Please select any " + selectedCustomerCaption);
            }else {
                if(binding.btnNext.getText().toString().equals(getString(R.string.next))) {
                    Intent intent = new Intent(this, CreatePresentationActivity.class);
                    intent.putExtra("customerType", customerType);
                    intent.putExtra("headquarterCode", selectedHQ);
                    intent.putExtra("customerCodes", (Arrays.toString(selectedCustomerCodes.toArray()).replaceAll("\\[", "").replaceAll("\\]", "")));
                    startActivity(intent);
                    finish();
                }else if(binding.btnNext.getText().toString().equals(getString(R.string.save))) {
                    presentationDataDao.changeSelectedCustomers(presentationName, (Arrays.toString(selectedCustomerCodes.toArray()).replaceAll("\\[", "").replaceAll("\\]", "")));
                    commonUtilsMethods.showToastMessage(this, selectedCustomerCaption + " updated successfully");
                    finish();
                }
            }
        });

        binding.searchCustomer.addTextChangedListener(new TextWatcher() {
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

        binding.ivFilter.setOnClickListener(view -> {
            CustomizeFiltered();
        });

        binding.constraintHq.setOnClickListener(view -> {
            if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                if(!selectedCustomerCodes.isEmpty()) {
                    changeHQAlert();
                } else {
                    showHQSelection();
                }
            }
        });

        setupAdapter();
    }

    private void setupAdapter() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(customerType + selectedHQ).getMasterSyncDataJsonArray();
            if(jsonArray.length() == 0) {
                commonUtilsMethods.showToastMessage(this, this.getString(R.string.no_data_found) + "  " + this.getString(R.string.do_master_sync));
            }

            Set<String> customerCodes = new HashSet<>();
            customerDataList.clear();
            String code = "";
            for (int i = 0; i<jsonArray.length(); i++) {
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    code = jsonObject.getString("Code");
                    if(!customerCodes.contains(code)) {
                        customerCodes.add(code);
                        CustomerDataModel customerDataModel = createCustomerDataModel(customerType, jsonObject);
                        if(presentationDataTable != null
                                && presentationDataTable.getCustomerCodes() != null
                                && !presentationDataTable.getCustomerCodes().isEmpty()
                                && presentationDataTable.getCustomerCodes().contains(code)) {
                            customerDataModel.setSelected(true);
                        }
                        customerDataList.add(customerDataModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        filteredCustomerDataList.clear();
        filteredCustomerDataList.addAll(customerDataList);

        if(filteredCustomerDataList.isEmpty()) {
            binding.noCustomer.setText(String.format("%s %s %s", getString(R.string.no), selectedCustomerCaption, getString(R.string.found)));
            binding.noCustomer.setVisibility(View.VISIBLE);
            binding.rvCustomerListSelection.setVisibility(View.GONE);
        }else {
            binding.noCustomer.setVisibility(View.GONE);
            binding.rvCustomerListSelection.setVisibility(View.VISIBLE);
            Collections.sort(filteredCustomerDataList, Comparator.comparing(CustomerDataModel::getName));
            customerListSelectionAdapter = new CustomerListSelectionAdapter(this, filteredCustomerDataList, customerType, customerSelectionListener);
            binding.rvCustomerListSelection.setItemAnimator(new DefaultItemAnimator());
            binding.rvCustomerListSelection.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
            binding.rvCustomerListSelection.setAdapter(customerListSelectionAdapter);
        }
    }

    private final CustomerListSelectionAdapter.CustomerSelectionListener customerSelectionListener = customerDataModel -> {
        if(selectedCustomerCodes.contains(customerDataModel.getCode())) {
            selectedCustomerCodes.remove(customerDataModel.getCode());
        }else {
            selectedCustomerCodes.add(customerDataModel.getCode());
        }
    };

    private CustomerDataModel createCustomerDataModel(String customerType, JSONObject jsonObject) throws Exception {
        String name = jsonObject.getString("Name");
        String code = jsonObject.getString("Code");
        String townName = jsonObject.getString("Town_Name");
        String townCode = jsonObject.getString("Town_Code");

        switch (customerType){
            case Constants.DOCTOR:
//            case Constants.DOCTOR_MAS:
                return new CustomerDataModel(
                        name,
                        code,
                        townName,
                        townCode,
                        jsonObject.getString("Category"),
                        jsonObject.getString("CategoryCode"),
                        jsonObject.getString("Specialty"),
                        jsonObject.getString("SpecialtyCode"),
                        jsonObject.getString("Doc_Class_ShortName"),
                        jsonObject.getString("Doc_ClsCode")
                );
            case Constants.CHEMIST:
//            case Constants.CHEMIST_MAS:
                return new CustomerDataModel(
                        name,
                        code,
                        townName,
                        townCode,
                        getChemistCategory(jsonObject.getString("Chm_cat")),
                        jsonObject.getString("Chm_cat"),
                        "", "", "", ""
                );
            case Constants.STOCKIEST:
//            case Constants.STOCKIEST_MAS:
                return new CustomerDataModel(
                        name,
                        code,
                        townName,
                        townCode,
                        "", "", "", "", "", ""
                );
            case Constants.UNLISTED_DOCTOR:
//            case Constants.UNLISTED_DOCTOR_MAS:
                return new CustomerDataModel(
                        name,
                        code,
                        townName,
                        townCode,
                        jsonObject.getString("CategoryName"),
                        jsonObject.getString("Category"),
                        jsonObject.getString("SpecialtyName"),
                        jsonObject.getString("Specialty"),
                        getUnListedClassName(jsonObject.getString("Doc_ClsCode")),
                        jsonObject.getString("Doc_ClsCode")
                );
            case Constants.CIP:
            case Constants.HOSPITAL:
            default:
                return new CustomerDataModel(name, code, townName, townCode, "", "", "", "", "", "");
        }
    }

    private String getUnListedClassName(String unlDocClsCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                String code = jsonObject.getString("Code");
                if(code.equalsIgnoreCase(unlDocClsCode))
                    return name;
            }
        } catch (Exception e) {
            Log.e("Customer Selection", "getUnListedClassName: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private String getChemistCategory(String catCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                String code = jsonObject.getString("Code");
                if(code.equalsIgnoreCase(catCode))
                    return name;
            }
        } catch (Exception e) {
            Log.e("Customer Selection", "getChemistCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private void changeHQAlert() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        alertText.setText("Changing " + getString(R.string.headquarter) + " will clear all selected " + selectedCustomerCaption);
        btn_yes.setText(getString(R.string.ok));
        btn_no.setText(getString(R.string.cancel));
        btn_yes.setOnClickListener(view -> {
            dialog.dismiss();
            showHQSelection();
        });

        btn_no.setOnClickListener(view -> dialog.dismiss());
    }

    private String getHQName(String hqCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            ArrayList<String> list = new ArrayList<>();
            if(jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.optString("id").equalsIgnoreCase(hqCode)) {
                        return jsonObject.optString("name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showHQSelection() {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            ArrayList<String> list = new ArrayList<>();
            if(jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(jsonObject.getString("name"));
                }
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_listview, null);
            alertDialog.setView(dialogView);
            TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
            ListView listView = dialogView.findViewById(R.id.listView);
            SearchView searchView = dialogView.findViewById(R.id.searchET);
            headerTxt.setText(getResources().getText(R.string.select_hq));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
            AlertDialog dialog = alertDialog.create();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    adapter.getFilter().filter(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapter.getFilter().filter(s);
                    return false;
                }
            });
            listView.setOnItemClickListener((adapterView, view1, position, l) -> {
                String selectedHq = listView.getItemAtPosition(position).toString();
                binding.txthqName.setText(selectedHq);
                for (int i = 0; i<jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
                            selectedHQ = jsonObject.optString("id");
                            UtilityClass.hideKeyboard(this);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                UtilityClass.hideKeyboard(this);
                getHQData(selectedHQ);
                dialog.dismiss();
            });

            alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getHQData(String hqCode) {
        try {
            Log.d("DynamicActivity", "showHQ: " + hqCode);
            boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR + hqCode),
                    chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST + hqCode),
                    stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST + hqCode),
                    ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR + hqCode),
                        hosAvailability = masterDataDao.isDataAvailable(Constants.HOSPITAL + hqCode),
                        cipAvailability = masterDataDao.isDataAvailable(Constants.CIP + hqCode),
            /*boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR_MAS + hqCode),
                    chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST_MAS + hqCode),
                    stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST_MAS + hqCode),
                    ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR_MAS + hqCode),*/
                    clusterAvailability = masterDataDao.isDataAvailable(Constants.CLUSTER + hqCode);
            Log.e("DynamicActivity", "showHQ: " + docAvailability + " " + chemAvailability + " " + stkAvailability + " " + ulDocAvailability + " " + clusterAvailability);
//                if(docAvailability && chemAvailability && stkAvailability && ulDocAvailability && hosAvailability && cipAvailability && clusterAvailability){
            if (docAvailability && chemAvailability && stkAvailability && ulDocAvailability && clusterAvailability) {
                Log.d("DynamicActivity", "getHQData: Data Available");
                selectedCustomerCodes.clear();
                setupAdapter();
                commonFun();
            } else if (UtilityClass.isNetworkAvailable(CustomerSelectionActivity.this)) {
                getData(hqCode);
            } else {
                commonUtilsMethods.showToastMessage(CustomerSelectionActivity.this, getString(R.string.no_network));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData(String hqCode) {
        SynqList = SharedPref.getsyn_hqcode(CustomerSelectionActivity.this);
        SynqList.add(hqCode);
        SharedPref.setSyncHQ(CustomerSelectionActivity.this, SynqList);
        binding.flSyncHqProgress.setVisibility(View.VISIBLE);
        List<MasterSyncItemModel> list = new ArrayList<>();
        list.add(new MasterSyncItemModel("Doctor", "Doctor", "getdoctors", Constants.DOCTOR + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", "Doctor", "getchemist", Constants.CHEMIST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", "Doctor", "getstockist", Constants.STOCKIEST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Doctor", Constants.DOCTOR_MAS, "getdoctors_master", Constants.DOCTOR_MAS + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Chemist", Constants.DOCTOR_MAS, "getchemist_master", Constants.CHEMIST_MAS + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Stockiest", Constants.DOCTOR_MAS, "getstockist_master", Constants.STOCKIEST_MAS + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Unlisted Doctor", Constants.DOCTOR_MAS, "getunlisteddr_master", Constants.UNLISTED_DOCTOR_MAS + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Cluster", Constants.DOCTOR_MAS, "getterritory", Constants.CLUSTER + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Joint Work", Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode, 0, false));

        for (int i = 0; i < list.size(); i++) {
            syncMaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hqCode);
        }
    }

    public void syncMaster(String masterFor, String remoteTableName, String LocalTableKeyName, String hqCode) {
        if (UtilityClass.isNetworkAvailable(CustomerSelectionActivity.this)) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(CustomerSelectionActivity.this);
                String pathUrl = SharedPref.getPhpPathUrl(CustomerSelectionActivity.this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                apiInterface = RetrofitClient.getRetrofit(CustomerSelectionActivity.this, baseUrl + replacedUrl);

                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(CustomerSelectionActivity.this);
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", SharedPref.getSfCode(CustomerSelectionActivity.this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(CustomerSelectionActivity.this));
                jsonObject.put("Rsf", hqCode);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(CustomerSelectionActivity.this), mapString, jsonObject.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();

                            if (response.isSuccessful()) {
                                Log.e("test CSA", "response : " + masterFor + " -- " + remoteTableName + " : " + Objects.requireNonNull(response.body()));
                                try {
                                    JsonElement jsonElement = response.body();
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                            if (!jsonObject2.has("success")) {
                                                jsonArray.put(jsonObject2);
                                                success = true;
                                            } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                                masterDataDao.saveMasterSyncStatus(LocalTableKeyName, 1);
                                            }
                                        }
                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(LocalTableKeyName, jsonArray.toString(), 2));
                                        }
                                    }
                                    selectedCustomerCodes.clear();
                                    setupAdapter();
                                    commonFun();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            binding.flSyncHqProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            binding.flSyncHqProgress.setVisibility(View.GONE);
                            t.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                binding.flSyncHqProgress.setVisibility(View.GONE);
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(CustomerSelectionActivity.this, getString(R.string.no_network));
        }
    }

    public void CustomizeFiltered() {
        dialogFilter = new Dialog(this);
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        if(dialogFilter.getWindow() != null) {
            dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialogFilter.setCancelable(false);

        ImageView img_close;
        Button btn_apply, btn_clear;
        ListView lv_spec, lv_cate, lv_terr, lv_class;
        TextView tvSpec, tvCate, tvTerritory, tvClass;
        ConstraintLayout constraintLayout;

        img_close = dialogFilter.findViewById(R.id.img_close);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        tvSpec = dialogFilter.findViewById(R.id.constraint_speciality);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);
        tvCate = dialogFilter.findViewById(R.id.constraint_category);
        tvClass = dialogFilter.findViewById(R.id.constraint_class);

        lv_spec = dialogFilter.findViewById(R.id.lv_spec);
        lv_cate = dialogFilter.findViewById(R.id.lv_category);
        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        lv_class = dialogFilter.findViewById(R.id.lv_class);

        if(customerType.equals(Constants.DOCTOR) || customerType.equals(Constants.UNLISTED_DOCTOR)) {
//        if(customerType.equals(Constants.DOCTOR_MAS) || customerType.equals(Constants.UNLISTED_DOCTOR_MAS)) {
            tvSpec.setVisibility(View.VISIBLE);
            tvClass.setVisibility(View.VISIBLE);
        }else {
            tvSpec.setVisibility(View.GONE);
            tvClass.setVisibility(View.GONE);
        }
        if(customerType.equals(Constants.DOCTOR) || customerType.equals(Constants.CHEMIST) || customerType.equals(Constants.UNLISTED_DOCTOR)) {
//        if(customerType.equals(Constants.DOCTOR_MAS) || customerType.equals(Constants.CHEMIST_MAS) || customerType.equals(Constants.UNLISTED_DOCTOR_MAS)) {
            tvCate.setVisibility(View.VISIBLE);
        }else {
            tvCate.setVisibility(View.GONE);
        }
        tvTerritory.setVisibility(View.VISIBLE);

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

        tvSpec.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if(lv_spec.getVisibility() == View.VISIBLE) {
                lv_spec.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
            }else {
                getFilterList("Speciality");

                FillteredAdapter arrayAdapter = new FillteredAdapter(this, filterSelectionList, clickedItem -> {
                    specialityCode = clickedItem.getCode();
                    specialityName = clickedItem.getName();
                    tvSpec.setText(clickedItem.getName());
                    lv_spec.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_spec.setAdapter(arrayAdapter);
                lv_spec.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvCate.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_spec.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if(lv_cate.getVisibility() == View.VISIBLE) {
                lv_cate.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
            }else {
                getFilterList("Category");
                FillteredAdapter arrayAdapter = new FillteredAdapter(this, filterSelectionList, clickedItem -> {
                    categoryCode = clickedItem.getCode();
                    categoryName = clickedItem.getName();
                    tvCate.setText(clickedItem.getName());
                    lv_cate.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_cate.setAdapter(arrayAdapter);
                lv_cate.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvTerritory.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_spec.setVisibility(View.GONE);
            if(lv_terr.getVisibility() == View.VISIBLE) {
                lv_terr.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
            }else {
                getFilterList("Territory");
                FillteredAdapter arrayAdapter = new FillteredAdapter(this, filterSelectionList, clickedItem -> {
                    territoryCode = clickedItem.getCode();
                    territoryName = clickedItem.getName();
                    tvTerritory.setText(clickedItem.getName());
                    lv_terr.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_terr.setAdapter(arrayAdapter);
                lv_terr.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvClass.setOnClickListener(view -> {
            lv_spec.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if(lv_class.getVisibility() == View.VISIBLE) {
                lv_class.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
            }else {
                getFilterList("Class");
                FillteredAdapter arrayAdapter = new FillteredAdapter(this, filterSelectionList, clickedItem -> {
                    classCode = clickedItem.getCode();
                    className = clickedItem.getName();
                    tvClass.setText(clickedItem.getName());
                    lv_class.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_class.setAdapter(arrayAdapter);
                lv_class.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        btn_apply.setOnClickListener(view1 -> {
            Filtered();
        });

        dialogFilter.show();
    }

    private void getFilterList(String requiredList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if(requiredList.equalsIgnoreCase("Territory")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + selectedHQ).getMasterSyncDataJsonArray();
            }else if(customerType.equals(Constants.DOCTOR) || customerType.equals(Constants.UNLISTED_DOCTOR)) {
//            }else if(customerType.equals(Constants.DOCTOR_MAS) || customerType.equals(Constants.UNLISTED_DOCTOR_MAS)) {
                if(requiredList.equalsIgnoreCase("Speciality")) {
                    jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
                }else if(requiredList.equalsIgnoreCase("Category")) {
                    jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
                }else if(requiredList.equalsIgnoreCase("Class")) {
                    jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
                }
            }else if(customerType.equals(Constants.CHEMIST)) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
            }
//            }else if(customerType.equals(Constants.CHEMIST_MAS)) {
//                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
//            }
            filterSelectionList.clear();
            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i<jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"), jsonObject.getString("Code")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filter(String text) {
        filteredNames.clear();
        for (CustomerDataModel s : filteredCustomerDataList) {
            if(s.getName().toLowerCase().contains(text.toLowerCase()) || s.getClusterName().toLowerCase().contains(text.toLowerCase()) || s.getCategoryName().toLowerCase().contains(text.toLowerCase()) || s.getSpecialityName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }

        if(filteredNames.isEmpty()) {
            binding.noCustomer.setText(String.format("%s %s %s", getString(R.string.no), selectedCustomerCaption, getString(R.string.found)));
            binding.noCustomer.setVisibility(View.VISIBLE);
            binding.rvCustomerListSelection.setVisibility(View.GONE);
        }else {
            binding.noCustomer.setVisibility(View.GONE);
            binding.rvCustomerListSelection.setVisibility(View.VISIBLE);
            customerListSelectionAdapter.filterList(filteredNames);
        }
    }

    public void Filtered() {
        ArrayList<CustomerDataModel> filterCusList = new ArrayList<>();
        if(!filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        }else {
            filterCusList.addAll(customerDataList);
        }
        filteredCustomerDataList.clear();
        if(specialityCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("") && territoryCode.equalsIgnoreCase("") && classCode.equalsIgnoreCase("")) {
            filteredCustomerDataList.addAll(customerDataList);
            binding.tvFilterCount.setText("0");
            Collections.sort(filteredCustomerDataList, Comparator.comparing(CustomerDataModel::getName));
        }else {
            for (CustomerDataModel mList : filterCusList) {
                if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                        && mList.getClusterCode().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                        && mList.getClusterCode().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && classCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                        && mList.getClusterCode().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && categoryCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getClusterCode().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                        && mList.getClusterCode().equalsIgnoreCase(territoryCode)
                        && categoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && territoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getClusterCode().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && specialityCode.isEmpty()
                        && classCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getClusterCode().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else if(mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && territoryCode.isEmpty()) {
                    filteredCustomerDataList.add(mList);
                }else {
                    if(mList.getSpecialityCode().equalsIgnoreCase(specialityCode)
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        filteredCustomerDataList.add(mList);
                    }else if(mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        filteredCustomerDataList.add(mList);
                    }else if(mList.getClusterCode().equalsIgnoreCase(territoryCode)
                            && specialityCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        filteredCustomerDataList.add(mList);
                    }else if(mList.getClassCode().equalsIgnoreCase(classCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()) {
                        filteredCustomerDataList.add(mList);
                    }
                }
            }
            binding.tvFilterCount.setText(String.valueOf(filteredCustomerDataList.size()));
        }

        if(filteredCustomerDataList.isEmpty()) {
            binding.noCustomer.setText(String.format("%s %s %s", getString(R.string.no), selectedCustomerCaption, getString(R.string.found)));
            binding.noCustomer.setVisibility(View.VISIBLE);
            binding.rvCustomerListSelection.setVisibility(View.GONE);
        }else {
            binding.noCustomer.setVisibility(View.GONE);
            binding.rvCustomerListSelection.setVisibility(View.VISIBLE);
            customerListSelectionAdapter.filterList(filteredCustomerDataList);
        }
        dialogFilter.dismiss();
    }

    public void commonFun() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}