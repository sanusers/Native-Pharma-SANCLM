package saneforce.sanclm.activity.map.custSelection;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.MapDcrSelectionBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.response.SetupResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class TagCustSelectionList extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static MapDcrSelectionBinding binding;
    public static String SelectedCustPos;
    List<String> HqNameList = new ArrayList<>();
    List<String> HqCodeList = new ArrayList<>();
    CustListAdapter custListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    SQLite sqLite;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> custListArrayNew = new ArrayList<>();
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    SQLiteHandler sqLiteHandler;
    ApiInterface apiInterface;
    ArrayAdapter arrayAdapter;
    LoginResponse loginResponse;
    SetupResponse setUpResponse;
    JSONArray jsonArray;
    JSONObject jsonObject;
    String SelectedTab, SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, SelectedHqCode, SelectedHqName, DrCaption, ChemistCaption, CipCaption, StockistCaption, UndrCaption, TpBasedDcr;


    @Override
    public void onBackPressed() {
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStart() {
        Log.v("onstart", "-000-");
        super.onStart();
        try {
            // if (SharedPref.getTaggedSuccessfully(TagCustSelectionList.this).equalsIgnoreCase("true")) {
            if (MapsActivity.isTagged) {
                //  CustList mm = custListArrayList.get(Integer.parseInt(SharedPref.getCustomerPosition(TagCustSelectionList.this)));
                CustList mm = custListArrayList.get(Integer.parseInt(SelectedCustPos));
                int yy = Integer.parseInt(mm.getTag()) + 1;
                mm.setTag(String.valueOf(yy));
                if (MapsActivity.GeoTagApprovalNeed.equalsIgnoreCase("0")) {
                    mm.setGeoTagStatus("1");
                }
                Log.v("latttlng", "--taggedonstart--" + MapsActivity.TaggedLaty + "---" + MapsActivity.TaggedLngy);
                mm.setLatitude(String.valueOf(MapsActivity.TaggedLaty));
                mm.setLongitude(String.valueOf(MapsActivity.TaggedLngy));
                mm.setAddress(String.valueOf(MapsActivity.TaggedAddr));
                custListAdapter.notifyDataSetChanged();
                MapsActivity.isTagged = false;
                // SharedPref.setTaggedSuccessfully(TagCustSelectionList.this, "false");
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

        commonUtilsMethods = new CommonUtilsMethods(this);

        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

        sqLiteHandler = new SQLiteHandler(this);
        sqLiteHandler.open();
        sqLite = new SQLite(getApplicationContext());
       // commonUtilsMethods.FullScreencall();

        getRequiredData();

        Log.v("data", "--" + SfType + "--" + SfCode);

        if (SfType.equalsIgnoreCase("1")) {
            AddCustList(SfCode);
            SelectedHqCode = SfCode;
            SelectedHqName = SfName;
            binding.txtSelectedHq.setText(SfName);
            binding.txtSelectedHq.setEnabled(false);
        } else {
            binding.txtSelectedHq.setEnabled(true);
            if (TpBasedDcr.equalsIgnoreCase("0")) {
                binding.txtSelectedHq.setEnabled(false);
            }
            SetHqAdapter();
        }

        binding.dummyView.setOnClickListener(view -> {
            binding.constraintHqList.setVisibility(View.GONE);
            binding.dummyView.setVisibility(View.GONE);
            hideKeyboard();
        });

        binding.txtSelectedHq.setOnClickListener(view -> {
            hideKeyboard();
            if (binding.constraintHqList.getVisibility() == View.VISIBLE) {
                binding.constraintHqList.setVisibility(View.GONE);
                binding.dummyView.setVisibility(View.VISIBLE);
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
            binding.constraintHqList.setVisibility(View.GONE);
            SelectedHqCode = HqCodeList.get(i);
            SelectedHqName = HqNameList.get(i);
            binding.txtSelectedHq.setText(SelectedHqName);
            MapsActivity.SelectedHqName = HqNameList.get(i);
            MapsActivity.SelectedHqCode = SelectedHqCode;
            /*SharedPref.setSelectedHqCode(TagCustSelectionList.this, SelectedHqCode);
            SharedPref.setSelectedHqName(TagCustSelectionList.this, HqNameList.get(i));*/
            AddCustList(SelectedHqCode);
        });
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

            JSONArray jsonArray = new JSONArray();
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);

                setUpResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setUpResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
                DrCaption = setUpResponse.getCaptionDr();
                ChemistCaption = setUpResponse.getCaptionChemist();
                StockistCaption = setUpResponse.getCaptionStockist();
                UndrCaption = setUpResponse.getCaptionUndr();
                if (setupData.has("cip_need")) {
                    CipCaption = setUpResponse.getCaptionCip();
                }
                TpBasedDcr = setUpResponse.getTpBasedDcr();
            }
        } catch (Exception e) {

        }
    }

    private void SetHqAdapter() {
        HqNameList.clear();
        HqCodeList.clear();
        SelectedHqCode = MapsActivity.SelectedHqCode;
        SelectedHqName = MapsActivity.SelectedHqName;
      /*  SelectedHqCode = SharedPref.getSelectedHqCode(TagCustSelectionList.this);
        SelectedHqName = SharedPref.getSelectedHqName(TagCustSelectionList.this);*/
        Log.v("dddd", "-1sds-" + SelectedHqCode + "---" + SelectedHqName);
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

            arrayAdapter = new ArrayAdapter(TagCustSelectionList.this, R.layout.listview_items, HqNameList);
            binding.hqListView.setAdapter(arrayAdapter);
            binding.txtSelectedHq.setText(SelectedHqName);
            if (SelectedHqCode.isEmpty()) {
                SelectedHqCode = HqCodeList.get(0);
                SelectedHqName = HqNameList.get(0);
            }
            AddCustList(SelectedHqCode);

        } catch (Exception e) {

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
        //  MasterSyncItemModel cluster = new MasterSyncItemModel("Doctor", "getterritory", Constants.CLUSTER + hqCode);
        //  MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Subordinate", "getjointwork", Constants.JOINT_WORK + hqCode);

        masterSyncArray.add(doctorModel);
        masterSyncArray.add(cheModel);
        masterSyncArray.add(stockModel);
        masterSyncArray.add(unListModel);
        masterSyncArray.add(hospModel);
        masterSyncArray.add(ciModel);
        // masterSyncArray.add(cluster);
        // masterSyncArray.add(jWorkModel);
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
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Doctor")) {
                    call = apiInterface.getDrMaster(jsonObject.toString());
                } else if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Subordinate")) {
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

    @SuppressLint("NotifyDataSetChanged")
    private void AddCustList(String selectedHqCode) {
        custListArrayList.clear();
        Log.v("map_selected_tab", "---" + SelectedTab);
        Log.v("selected_hq", "---" + selectedHqCode);

        switch (SelectedTab) {
            case "D":
                try {
                    binding.tagSelection.setText(DrCaption);
                    if (!sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE + "  Kindly Do MasterSync", Toast.LENGTH_SHORT).show();
                    }
                 /*   JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + selectedHqCode);
                    Log.v("jsonArray", "--" + jsonArray.length() + "---" + jsonArray);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Doctor", "getdoctors", SfCode, DivCode, selectedHqCode, SfType, Designation, StateCode, SubDivisionCode);
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }*/
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        if (jsonObject.has("cust_status")) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("cust_status")));
                        } else {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                            custListArrayNew.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), SelectedTab, jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Lat"), jsonObject.getString("Long"), jsonObject.getString("Addrs"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), "0"));
                        }
                      /*  if (jsonObject.getString("Code").equalsIgnoreCase("24482") && jsonObject.getString("uRwID").equalsIgnoreCase("2")) {
                            jsonObject.put("cust_status", "1");
                        }

                        if (jsonObject.getString("Code").equalsIgnoreCase("24526") && jsonObject.getString("uRwID").equalsIgnoreCase("4")) {
                            jsonObject.put("cust_status", "1");
                        }

                        if (jsonObject.getString("Code").equalsIgnoreCase("24517") && jsonObject.getString("uRwID").equalsIgnoreCase("2")) {
                            jsonObject.put("cust_status", "1");
                        }

                        if (jsonObject.getString("Code").equalsIgnoreCase("24574")) {
                            jsonObject.put("cust_status", "1");
                        }

                        if (jsonObject.getString("Code").equalsIgnoreCase("24543")) {
                            jsonObject.put("cust_status", "1");
                        }*/

                    }
                } catch (Exception e) {
                    Log.v("dr_tag", "---error--" + e);
                }

                break;
            case "C":
                try {
                    binding.tagSelection.setText(ChemistCaption);
                    if (!sqLite.getMasterSyncDataOfHQ(Constants.CHEMIST + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE + "  Kindly Do MasterSync", Toast.LENGTH_SHORT).show();
                    }
                /*    JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + selectedHqCode);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Chemist", "getchemist", SfCode, DivCode, selectedHqCode, SfType, Designation, StateCode, SubDivisionCode);
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }*/
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
                } catch (Exception e) {

                }
                break;
            case "S":
                try {
                    binding.tagSelection.setText(StockistCaption);
                    if (!sqLite.getMasterSyncDataOfHQ(Constants.STOCKIEST + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE + "  Kindly Do MasterSync", Toast.LENGTH_SHORT).show();
                    }
                /*    JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + selectedHqCode);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Stockiest", "getstockist", SfCode, DivCode, selectedHqCode, SfType, Designation, StateCode, SubDivisionCode);
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }*/
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
                } catch (Exception e) {

                }
                break;
            case "U":
                try {
                    binding.tagSelection.setText(UndrCaption);
                    if (!sqLite.getMasterSyncDataOfHQ(Constants.UNLISTED_DOCTOR + selectedHqCode)) {
                        prepareMasterToSync(selectedHqCode);
                    } else {
                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + selectedHqCode);
                    }
                    if (jsonArray.length() == 0) {
                        Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE + "  Kindly Do MasterSync", Toast.LENGTH_SHORT).show();
                    }
                 /*   JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + selectedHqCode);
                    if (jsonArray.length() == 0) {
                        if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                            Toast.makeText(TagCustSelectionList.this, "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                            MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Unlisted_Doctor", "getunlisteddr", SfCode, DivCode, selectedHqCode, SfType, Designation, StateCode, SubDivisionCode);
                        } else {
                            Toast.makeText(TagCustSelectionList.this, Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                        }
                    }*/
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
                } catch (Exception e) {

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

         /*   if (s.getGeoTagStatus().equalsIgnoreCase("0")) {
                Status = "approved";
            } else*/

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