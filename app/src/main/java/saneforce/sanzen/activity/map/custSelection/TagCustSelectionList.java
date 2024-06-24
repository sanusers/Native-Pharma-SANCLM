package saneforce.sanzen.activity.map.custSelection;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.sanzen.activity.map.MapsActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.MapDcrSelectionBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class TagCustSelectionList extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static MapDcrSelectionBinding binding;
    public static String SelectedCustPos;
    List<String> HqNameList = new ArrayList<>();
    List<String> HqCodeList = new ArrayList<>();
    CustListAdapter custListAdapter;
    CommonUtilsMethods commonUtilsMethods;
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


    // Create a map to store category codes and their corresponding names
    HashMap<String, String> categoryMap = new HashMap<>();
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(HomeDashBoard.selectedDate != null) {
            outState.putString("date", HomeDashBoard.selectedDate.toString());
        }
        outState.putBoolean("isSaved", true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState != null && savedInstanceState.getBoolean("isSaved")) {
            if(savedInstanceState.getString("date") != null) {
                HomeDashBoard.selectedDate = LocalDate.parse(savedInstanceState.getString("date"), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
            }
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                CommonAlertBox.permissionChangeAlert(this);
            }
        }

        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

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
                arrayAdapter.getFilter().filter(charSequence, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (count > 0) {
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

        binding.hqListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println("position--->" + position);
                String clickedValue = arrayAdapter.getItem(position);
                // Handle the clicked value here
                System.out.println("Clicked Value: " + clickedValue);
                hideKeyboard();
                binding.constraintHqList.setVisibility(View.GONE);
                binding.dummyView.setVisibility(View.GONE);
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew("Subordinate").getMasterSyncDataJsonArray();

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonHQList = jsonArray.getJSONObject(i);
                        String name = jsonHQList.optString("name", "");
                        if (name.equals(clickedValue)) {
                            SelectedHqCode = jsonHQList.optString("Code", "");
                            System.out.println("SelectedHqCode--->"+SelectedHqCode);
                            SelectedHqName = clickedValue;
                            System.out.println("SelectedHqName--->"+SelectedHqName);
                            MapsActivity.SelectedHqName = clickedValue;
                            System.out.println("MapsActivity.SelectedHqName--->"+MapsActivity.SelectedHqName);
                            MapsActivity.SelectedHqCode = SelectedHqCode;
                            System.out.println("MapsActivity.SelectedHqCode --->"+MapsActivity.SelectedHqCode );
                            break;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                binding.txtSelectedHq.setText(clickedValue);

                AddCustList(SelectedHqCode);
            }
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
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonHQList = jsonArray.getJSONObject(i);
                System.out.println("jsonHQList--->"+jsonHQList);
                String name = jsonHQList.optString("name", "");
                String code = jsonHQList.optString("id", "");
                if(!code.isEmpty() && code.equalsIgnoreCase(WorkPlanFragment.mHQCode1) || code.equalsIgnoreCase(WorkPlanFragment.mHQCode2)) {
                    HqNameList.add(name);
                    HqCodeList.add(code);
                }
            }

            arrayAdapter = new ArrayAdapter<>(TagCustSelectionList.this, R.layout.listview_items,HqNameList);
            binding.hqListView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
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
                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                jsonObject.put("Rsf", hqCode);
                Log.e("test","master sync obj in TP : " + jsonObject);
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
                                            }
                                        }

                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 2));
                                        }
                                    } else {
                                        masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
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
    private void AddCustList(String selectedHqCode)  {
        custListArrayList.clear();
        Log.v("map_selected_tab", "---" + SelectedTab);
        Log.v("selected_hq", "---" + selectedHqCode);
        switch (SelectedTab) {
            case "D":
                try {
                    binding.tagSelection.setText(SharedPref.getDrCap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.DOCTOR + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + selectedHqCode).getMasterSyncDataJsonArray();
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
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
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + selectedHqCode).getMasterSyncDataJsonArray();
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    JSONArray doctJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
                    Map<String, String> townMap = new HashMap<>();
                    for (int i = 0; i < doctJsonArray.length(); i++) {
                        JSONObject doctJsonObject = doctJsonArray.getJSONObject(i);
                        String chemistCategory = doctJsonObject.getString("Code");
                        String chemistCategoryName = doctJsonObject.getString("Chem_Cat_Name");
                        townMap.put(chemistCategory, chemistCategoryName);
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String chemistCategory = jsonObject.getString("Chm_cat");
                        String chemistTownName = townMap.get(chemistCategory);
                        if (chemistTownName==null) chemistTownName = "";
                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, chemistTownName, "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, chemistTownName, "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, chemistTownName, "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, chemistTownName, "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAG", "AddCustList: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

           /* case "C":
                try {
                    binding.tagSelection.setText(SharedPref.getChmCap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.CHEMIST + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + selectedHqCode).getMasterSyncDataJsonArray();
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
                break;*/
            case "S":
                try {
                    binding.tagSelection.setText(SharedPref.getStkCap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.STOCKIEST + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + selectedHqCode).getMasterSyncDataJsonArray();
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, "Category", "Specialty", jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                        }

                    }

                } catch (Exception ignored) {

                }
                break;
            case "U":
                try {
                    binding.tagSelection.setText(SharedPref.getUNLcap(this));
                    if (!masterDataDao.getMasterSyncDataOfHQ(Constants.UNLISTED_DOCTOR + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + selectedHqCode).getMasterSyncDataJsonArray();
                    }
                    if (jsonArray.length() == 0) {
                        commonUtilsMethods.showToastMessage(TagCustSelectionList.this, getString(R.string.no_data_found) + " " + getString(R.string.do_master_sync));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("cus_addr"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
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
                        System.out.println("custList1--->");
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getGeoTagStatus()));
                    } else if (custListArrayList.get(i).getMaxTag().equalsIgnoreCase("1") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("2") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("3") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("4") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("5") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("6") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("7") || custListArrayList.get(i).getMaxTag().equalsIgnoreCase("8")) {
                        System.out.println("custList2--->");
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), String.valueOf(Integer.parseInt(custListArrayList.get(i).getMaxTag())), String.valueOf(i), custListArrayList.get(i).getGeoTagStatus()));
                    }
                    custListArrayList.remove(j--);
                    count--;
                } else {
                    System.out.println("custList3--->");
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