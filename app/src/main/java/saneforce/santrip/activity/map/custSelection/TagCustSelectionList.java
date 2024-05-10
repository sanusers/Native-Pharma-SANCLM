package saneforce.santrip.activity.map.custSelection;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.map.MapsActivity;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.MapDcrSelectionBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;

public class TagCustSelectionList extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static MapDcrSelectionBinding binding;
    public static String SelectedCustPos;
    List<String> HqNameList = new ArrayList<>();
    List<String> HqCodeList = new ArrayList<>();
    CustListAdapter custListAdapter;
    CommonUtilsMethods commonUtilsMethods;
//    SQLite sqLite;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> custListArrayNew = new ArrayList<>();
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    ApiInterface apiInterface;
    ArrayAdapter<String> arrayAdapter;

    JSONArray jsonArray;
    JSONObject jsonObject;
    String SelectedTab, SelectedHqCode, SelectedHqName;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;


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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStart() {
        Log.v("onstart", "-000-");
        super.onStart();
        try {
            if (MapsActivity.isTagged) {
                CustList mm = custListArrayList.get(Integer.parseInt(SelectedCustPos));
                int yy = Integer.parseInt(mm.getTag()) + 1;
                mm.setTag(String.valueOf(yy));
                if (MapsActivity.GeoTagApprovalNeed.equalsIgnoreCase("0")) {
                    mm.setGeoTagStatus("1");
                }
                mm.setLatitude(String.valueOf(MapsActivity.TaggedLat));
                mm.setLongitude(String.valueOf(MapsActivity.TaggedLng));
                mm.setAddress(String.valueOf(MapsActivity.TaggedAdd));
                custListAdapter.notifyDataSetChanged();
                MapsActivity.isTagged = false;
            }
        } catch (Exception e) {
            Log.v("onstart", e.toString());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

//        sqLite = new SQLite(getApplicationContext());
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        SelectedTab = MapsActivity.SelectedTab;



        if (SharedPref.getSfType(this).equalsIgnoreCase("1")) {
            AddCustList(SharedPref.getSfCode(this));
            SelectedHqCode = SharedPref.getSfCode(this);
            SelectedHqName = SharedPref.getSfName(this);
            binding.txtSelectedHq.setText(SharedPref.getSfName(this));
            binding.txtSelectedHq.setEnabled(false);
        } else {
            binding.txtSelectedHq.setEnabled(true);
            if (SharedPref.getTpbasedDcr(this).equalsIgnoreCase("0")) {
                binding.txtSelectedHq.setEnabled(false);
            }
            SetHqAdapter();
        }

        binding.dummyView.setOnClickListener(view -> {
        });

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
                TagCustSelectionList.this.arrayAdapter.getFilter().filter(editable);
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
            Intent intent = new Intent(TagCustSelectionList.this, MapsActivity.class);
            intent.putExtra("from", "not_tagging");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.hqListView.setOnItemClickListener((adapterView, view, i, l) -> {
            hideKeyboard();
            binding.constraintHqList.setVisibility(View.GONE);
            binding.dummyView.setVisibility(View.GONE);
            SelectedHqCode = HqCodeList.get(i);
            SelectedHqName = HqNameList.get(i);
            binding.txtSelectedHq.setText(SelectedHqName);
            MapsActivity.SelectedHqName = HqNameList.get(i);
            MapsActivity.SelectedHqCode = SelectedHqCode;
            AddCustList(SelectedHqCode);
        });
    }


    private void SetHqAdapter() {
        HqNameList.clear();
        HqCodeList.clear();
        SelectedHqCode = MapsActivity.SelectedHqCode;
        SelectedHqName = MapsActivity.SelectedHqName;
        Log.v("dddd", "-1sds-" + SelectedHqCode + "---" + SelectedHqName);
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew("Subordinate").getMasterSyncDataJsonArray();
//            JSONArray jsonArray = sqLite.getMasterSyncDataByKey("Subordinate");
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

            arrayAdapter = new ArrayAdapter<>(TagCustSelectionList.this, R.layout.listview_items, HqNameList);
            binding.hqListView.setAdapter(arrayAdapter);
            binding.txtSelectedHq.setText(SelectedHqName);
            if (SelectedHqCode.isEmpty()) {
                SelectedHqCode = HqCodeList.get(0);
                SelectedHqName = HqNameList.get(0);
            }
            AddCustList(SelectedHqCode);

        } catch (Exception ignored) {

        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchCust.getWindowToken(), 0);
    }

    public void prepareMasterToSync(String hqCode) {
        masterSyncArray.clear();
        MasterSyncItemModel doctorModel = new MasterSyncItemModel("Doctor", "getdoctors", Constants.DOCTOR + hqCode);
        MasterSyncItemModel cheModel = new MasterSyncItemModel("Doctor", "getchemist", Constants.CHEMIST + hqCode);
        MasterSyncItemModel stockModel = new MasterSyncItemModel("Doctor", "getstockist", Constants.STOCKIEST + hqCode);
        MasterSyncItemModel unListModel = new MasterSyncItemModel("Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode);
        MasterSyncItemModel hospModel = new MasterSyncItemModel("Doctor", "gethospital", Constants.HOSPITAL + hqCode);
        MasterSyncItemModel ciModel = new MasterSyncItemModel("Doctor", "getcip", Constants.CIP + hqCode);

        masterSyncArray.add(doctorModel);
        masterSyncArray.add(cheModel);
        masterSyncArray.add(stockModel);
        masterSyncArray.add(unListModel);
        masterSyncArray.add(hospModel);
        masterSyncArray.add(ciModel);
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
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", SharedPref.getSfType(this));
                jsonObject.put("Designation", SharedPref.getDesig(this));
                jsonObject.put("state_code", SharedPref.getStateCode(this));
                jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));

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
                                                masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
//                                                sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0));
//                                            sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0);
                                        }
                                    } else {
                                        masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
//                                        sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
//                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_network));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddCustList(String selectedHqCode) {
        custListArrayList.clear();
        Log.v("map_selected_tab", "---" + SelectedTab);
        Log.v("selected_hq", "---" + selectedHqCode);

        switch (SelectedTab) {
            case "D":
                try {
                    binding.tagSelection.setText(SharedPref.getDrCap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.DOCTOR + selectedHqCode)) {
//                    if (!sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + selectedHqCode).getMasterSyncDataJsonArray();
//                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                        }

                    }
                } catch (Exception e) {
                    Log.v("dr_tag", "---error--" + e);
                }

                break;
            case "C":
                try {
                    binding.tagSelection.setText(SharedPref.getChmCap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.CHEMIST + selectedHqCode)) {
//                    if (!sqLite.getMasterSyncDataOfHQ(Constants.CHEMIST + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + selectedHqCode).getMasterSyncDataJsonArray();
//                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                        }
                    }
                } catch (Exception ignored) {

                }
                break;
            case "S":
                try {
                    binding.tagSelection.setText(SharedPref.getStkCap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.STOCKIEST + selectedHqCode)) {
//                    if (!sqLite.getMasterSyncDataOfHQ(Constants.STOCKIEST + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + selectedHqCode).getMasterSyncDataJsonArray();
//                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                        }
                    }
                } catch (Exception ignored) {

                }
                break;
            case "U":
                try {
                    binding.tagSelection.setText(SharedPref.getUNLcap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.UNLISTED_DOCTOR + selectedHqCode)) {
//                    if (!sqLite.getMasterSyncDataOfHQ(Constants.UNLISTED_DOCTOR + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + selectedHqCode).getMasterSyncDataJsonArray();
//                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                        }
                    }
                } catch (Exception ignored) {

                }
                break;
        }


        int count = custListArrayList.size();
        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {
                if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                    if (custListArrayList.get(i).getMaxTag().equalsIgnoreCase("0")) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getGeoTagStatus()));
                    } else if (custListArrayList.get(i).getMaxTag().equalsIgnoreCase("1") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("2") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("3") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("4") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("5") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("6") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("7") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("8")) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), String.valueOf(Integer.parseInt(custListArrayList.get(i).getMaxTag()) + 1), String.valueOf(i), custListArrayList.get(i).getGeoTagStatus()));
                    }
                    custListArrayList.remove(j--);
                    count--;
                } else {
                    custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getGeoTagStatus()));
                }
            }
        }

        custListAdapter = new CustListAdapter(TagCustSelectionList.this, TagCustSelectionList.this, custListArrayList, custListArrayNew);
        binding.rvCustList.setItemAnimator(new DefaultItemAnimator());
        binding.rvCustList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        binding.rvCustList.setAdapter(custListAdapter);
        custListAdapter.notifyDataSetChanged();
    }

    private void filter(String text) {
        ArrayList<CustList> filtered_names = new ArrayList<>();
        for (CustList s : custListArrayList) {
            String values = s.getTag() + "/" + s.getMaxTag();
            String Status = "";
            String Views = "";

            if (s.getGeoTagStatus().equalsIgnoreCase("1")) {
                Status = "pending";
            }

            if (Integer.parseInt(s.getMaxTag()) <= Integer.parseInt(s.getTag())) {
                Views = " view";
            }

            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getTag().toLowerCase().contains(text.toLowerCase()) || s.getMaxTag().toLowerCase().contains(text.toLowerCase()) || values.toLowerCase().contains(text.toLowerCase()) || Status.toLowerCase().contains(text.toLowerCase()) || Views.toLowerCase().contains(text.toLowerCase())) {
                filtered_names.add(s);
            }
        }
        custListAdapter.filterList(filtered_names);
    }
}