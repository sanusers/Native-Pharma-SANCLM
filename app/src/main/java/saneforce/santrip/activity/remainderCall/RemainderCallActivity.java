package saneforce.santrip.activity.remainderCall;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.map.MapsActivity;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityRemainderCallBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class RemainderCallActivity extends AppCompatActivity {
    ActivityRemainderCallBinding binding;
    List<String> HqNameList = new ArrayList<>();
    List<String> HqCodeList = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;
    SQLite sqLite;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> custListArrayNew = new ArrayList<>();
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    ApiInterface apiInterface;
    ArrayAdapter<String> arrayAdapter;
    RemainderCallAdapter remainderCallAdapter;
    LoginResponse loginResponse;
    JSONArray jsonArray;
    JSONObject jsonObject;

    String SelectedTab, SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, SelectedHqCode, SelectedHqName, DrCaption, ChemistCaption, CipCaption, StockistCaption, UndrCaption, TpBasedDcr;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemainderCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());
        getRequiredData();

        if (SfType.equalsIgnoreCase("1")) {
            AddCusList(SfCode);
            SelectedHqCode = SfCode;
            SelectedHqName = SfName;
            binding.txtSelectedHq.setText(SfName);
            binding.txtSelectedHq.setEnabled(false);
        } else {
            SelectedHqCode = SharedPref.getHqCode(getApplicationContext());
            SelectedHqName = SharedPref.getHqName(getApplicationContext());
            binding.txtSelectedHq.setEnabled(true);
            if (TpBasedDcr.equalsIgnoreCase("0")) {
                binding.txtSelectedHq.setEnabled(false);
            }
            SetHqAdapter();
        }

        binding.txtSelectedHq.setOnClickListener(view -> {
            hideKeyboard();
            if (binding.constraintHqList.getVisibility() == View.VISIBLE) {
                binding.constraintHqList.setVisibility(View.GONE);
                binding.dummyView.setVisibility(View.GONE);
            } else {
                binding.constraintHqList.setVisibility(View.VISIBLE);
                binding.dummyView.setVisibility(View.VISIBLE);
            }
        });

        binding.dummyView.setOnClickListener(view -> {
        });

        binding.imgClose.setOnClickListener(view -> {
            hideKeyboard();
            binding.constraintHqList.setVisibility(View.GONE);
            binding.dummyView.setVisibility(View.GONE);
        });

        binding.clrSearchCustTxt.setOnClickListener(view -> {
            binding.searchCust.setText("");
            hideKeyboard();
        });

        binding.searchHq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                RemainderCallActivity.this.arrayAdapter.getFilter().filter(editable);
            }
        });

        binding.searchCust.addTextChangedListener(new TextWatcher() {
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

        binding.ivBack.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.hqListView.setOnItemClickListener((adapterView, view, i, l) -> {
            hideKeyboard();
            binding.constraintHqList.setVisibility(View.GONE);
            binding.dummyView.setVisibility(View.GONE);
            SelectedHqCode = HqCodeList.get(i);
            SelectedHqName = HqNameList.get(i);
            binding.txtSelectedHq.setText(SelectedHqName);
            AddCusList(SelectedHqCode);
        });
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchCust.getWindowToken(), 0);
    }

    private void SetHqAdapter() {
        HqNameList.clear();
        HqCodeList.clear();
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Subordinate");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonHQList = jsonArray.getJSONObject(i);
                if (SelectedHqCode.equalsIgnoreCase(jsonHQList.getString("id"))) {
                    if (HqCodeList.size() != 0) {
                        HqNameList.add(jsonHQList.getString("name"));
                        HqCodeList.add(jsonHQList.getString("id"));
                    } else {
                        HqNameList.add(jsonHQList.getString("name"));
                        HqCodeList.add(jsonHQList.getString("id"));
                    }
                } else {
                    HqNameList.add(jsonHQList.getString("name"));
                    HqCodeList.add(jsonHQList.getString("id"));
                }
            }

            arrayAdapter = new ArrayAdapter<>(RemainderCallActivity.this, R.layout.listview_items, HqNameList);
            binding.hqListView.setAdapter(arrayAdapter);
            binding.txtSelectedHq.setText(SelectedHqName);
            if (SelectedHqCode.isEmpty()) {
                SelectedHqCode = HqCodeList.get(0);
                SelectedHqName = HqNameList.get(0);
            }
            AddCusList(SelectedHqCode);
        } catch (Exception ignored) {

        }
    }

    public void prepareMasterToSync(String hqCode) {
        masterSyncArray.clear();
        MasterSyncItemModel doctorModel = new MasterSyncItemModel("Doctor", "getdoctors", Constants.DOCTOR + hqCode);
        masterSyncArray.add(doctorModel);
        for (int i = 0; i < masterSyncArray.size(); i++) {
            sync(masterSyncArray.get(i), hqCode);
        }
    }

    public void sync(MasterSyncItemModel masterSyncItemModel, String hqCode) {

        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
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
                Map<String, String> mapString = new HashMap<>();
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Doctor")) {
                    mapString.put("axn", "table/dcrmasterdata");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
                } else if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Subordinate")) {
                    mapString.put("axn", "table/subordinates");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
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
                                    if (!Objects.requireNonNull(jsonElement).isJsonNull()) {
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
            commonUtilsMethods.ShowToast(getApplicationContext(), getString(R.string.no_network), 100);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddCusList(String selectedHqCode) {
        custListArrayList.clear();
        Log.v("selected_hq", "---" + selectedHqCode);
        try {
            if (!sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + selectedHqCode)) {
                prepareMasterToSync(selectedHqCode);
            } else {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + selectedHqCode);
            }
            if (jsonArray.length() == 0) {
                commonUtilsMethods.ShowToast(context, getString(R.string.no_data_found) + " " + context.getString(R.string.do_master_sync), 100);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has("cust_status")) {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                } else {
                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                }
            }

        } catch (Exception e) {
            Log.v("dr_tag", "---error--" + e);
        }


        remainderCallAdapter = new RemainderCallAdapter(RemainderCallActivity.this, RemainderCallActivity.this, custListArrayList);
        binding.rvCustList.setItemAnimator(new DefaultItemAnimator());
        binding.rvCustList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        binding.rvCustList.setAdapter(remainderCallAdapter);
        remainderCallAdapter.notifyDataSetChanged();
        Collections.sort(custListArrayList, Comparator.comparing(CustList::getTown_name));
    }

    private void getRequiredData() {
        try {
            SelectedTab = MapsActivity.SelectedTab;
            loginResponse = sqLite.getLoginData();
            SfType = loginResponse.getSf_type();
            SfCode = loginResponse.getSF_Code();
            SfName = loginResponse.getSF_Name();
            DivCode = loginResponse.getDivision_Code();
            SubDivisionCode = loginResponse.getSubdivision_code();
            Designation = loginResponse.getDesig();
            StateCode = loginResponse.getState_Code();
            DrCaption = loginResponse.getDrCap();
            ChemistCaption = loginResponse.getChmCap();
            StockistCaption = loginResponse.getStkCap();
            UndrCaption = loginResponse.getNLCap();
            CipCaption = loginResponse.getCIP_Caption();
            TpBasedDcr = loginResponse.getTPbasedDCR();

        } catch (Exception ignored) {

        }
    }

    private void filter(String text) {
        ArrayList<CustList> filtered_names = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getTag().toLowerCase().contains(text.toLowerCase()) || s.getMaxTag().toLowerCase().contains(text.toLowerCase())) {
                filtered_names.add(s);
            }
        }
        remainderCallAdapter.filterList(filtered_names);
    }
}
