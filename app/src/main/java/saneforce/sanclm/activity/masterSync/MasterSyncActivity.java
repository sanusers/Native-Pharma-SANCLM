package saneforce.sanclm.activity.masterSync;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Year;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.ActivityMasterSyncBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.TimeUtils;


public class MasterSyncActivity extends AppCompatActivity {

    ActivityMasterSyncBinding binding;
    ApiInterface apiInterface;
    MasterSyncViewModel masterSyncViewModel = new MasterSyncViewModel();
    MasterSyncAdapter masterSyncAdapter = new MasterSyncAdapter();
    SQLite sqLite;
    LoginResponse loginResponse;
    String sfCode = "",division_code = "",sfType = "",rsf ="",designation = "",state_code ="",subdivision_code = "";

    int doctorCount = 0,specialityCount = 0,qualificationCount = 0,categoryCount = 0,departmentCount = 0,classCount = 0,feedbackCount = 0;
    int unlistedDrCount = 0,chemistCount = 0,stockiestCount = 0,hospitalCount = 0,cipCount = 0, inputCount = 0, leaveCount = 0,leaveStatusCount = 0,tpCount =0,clusterCount = 0;
    int dcrCount = 0,visitControlCount= 0,missedDateCount = 0,stockBalanceCount = 0;
    int productCount = 0, proCatCount = 0,brandCount = 0, compProCount = 0;
    int workTypeCount = 0,holidayCount = 0,weeklyOfCount = 0;
    int proSlideCount = 0,proSpeSlideCount = 0,brandSlideCount = 0, therapticCount = 0;
    int subordinateCount = 0,subMgrCount = 0,jWorkCount= 0;
    int setupCount = 0, customSetupCount = 0;

    // Api call status
    int doctorStatus = 0,specialityStatus = 0,qualificationStatus = 0,categoryStatus = 0,departmentStatus = 0,classStatus = 0,feedbackStatus = 0;
    int unlistedDrStatus = 0,chemistStatus = 0,stockiestStatus = 0,hospitalStatus = 0,cipStatus = 0, inputStatus = 0, leaveStatus = 0,leaveStatusStatus = 0,tpStatus =0,clusterStatus = 0;
    int dcrStatus = 0,visitControlStatus= 0,missedDateStatus = 0,stockBalanceStatus = 0;
    int productStatus = 0, proCatStatus = 0,brandStatus = 0, compProStatus = 0;
    int workTypeStatus = 0,holidayStatus = 0,weeklyOfStatus = 0;
    int proSlideStatus = 0,proSpeSlideStatus = 0,brandSlideStatus = 0, therapticStatus = 0;
    int subordinateStatus = 0,subMgrStatus = 0,jWorkStatus= 0;
    int setupStatus = 0, customSetupStatus = 0;

    int apiSuccessCount = 0,itemCount = 0;
    String navigateFrom = "";
    boolean mgrInitialSync = false;
    CommonUtilsMethods commonUtilsMethods;

    ArrayList<ArrayList<MasterSyncItemModel>> masterSyncAllModel = new ArrayList<>();
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();
    ArrayList<MasterSyncItemModel> doctorModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> stockiestModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> chemistModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> unlistedDrModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> hospitalModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> cipModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> inputModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> productModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> clusterModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> leaveModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> dcrModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> workTypeModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> tpModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> slideModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> subordinateModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> setupModelArray = new ArrayList<>();


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMasterSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            navigateFrom = getIntent().getExtras().getString("Origin");
        }

        //Initializing all the data array
        uiInitialization();
        arrayForAdapter.clear();
        arrayForAdapter.addAll(doctorModelArray);
        populateAdapter(arrayForAdapter);

        if (navigateFrom.equalsIgnoreCase("Login")){
            if (sfType.equalsIgnoreCase("2")){ //MGR
                mgrInitialSync = true;
                sync(Constants.SUBORDINATE, "getsubordinate", subordinateModelArray, 0); // to get all the HQ list initially only for MGR
            } else {
                masterSyncAll(false);
            }
        }


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
            }
        });

        binding.hq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                try{
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                    ArrayList<String> list = new ArrayList<>();

                    if (jsonArray.length() > 0){
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            list.add(jsonObject.getString("name"));
                        }
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MasterSyncActivity.this);
                    LayoutInflater inflater =MasterSyncActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_listview, null);
                    alertDialog.setView(dialogView);
                    TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
                    ListView listView = dialogView.findViewById(R.id.listView);
                    SearchView searchView = dialogView.findViewById(R.id.searchET);

                    headerTxt.setText("Select HQ");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MasterSyncActivity.this, android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);
                    AlertDialog dialog = alertDialog.create();

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit (String s) {
                            adapter.getFilter().filter(s);
                            return false;
                        }
                        @Override
                        public boolean onQueryTextChange (String s) {
                            adapter.getFilter().filter(s);
                            return false;
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick (AdapterView<?> adapterView, View view, int position, long l) {
                            String selectedHq = listView.getItemAtPosition(position).toString();
                            binding.hqName.setText(selectedHq);
                            for (int i=0;i<jsonArray.length();i++){
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("name").equalsIgnoreCase(selectedHq)){
                                        rsf = jsonObject.getString("id");
                                        SharedPref.saveHq(MasterSyncActivity.this, selectedHq, rsf);
                                        prepareArray(rsf); // replace the new rsf value
                                        masterSyncAll(true);
                                        break;
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                UtilityClass.hideKeyboard(MasterSyncActivity.this);

            }
        });

        binding.listedDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.listDrArrow,binding.listedDoctor);
                binding.childSync.setText("Sync Listed Doctor");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(doctorModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.chemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.chemistArrow,binding.chemist);
                binding.childSync.setText("Sync Chemist");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(chemistModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.stockiest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.stockArrow,binding.stockiest);
                binding.childSync.setText("Sync Stockiest");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(stockiestModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.unlistedDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.unListDrArrow,binding.unlistedDoctor);
                binding.childSync.setText("Sync Unlisted Doctor");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(unlistedDrModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.hospitalArrow,binding.hospital);
                binding.childSync.setText("Sync Hospital");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(hospitalModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.cip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.cipArrow,binding.cip);
                binding.childSync.setText("Sync CIP");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(cipModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.inputArrow,binding.input);
                binding.childSync.setText("Sync Input");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(inputModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.productArrow,binding.product);
                binding.childSync.setText("Sync Product");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(productModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.cluster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.clusterArrow,binding.cluster);
                binding.childSync.setText("Sync Cluster");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(clusterModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.leaveArrow,binding.leave);
                binding.childSync.setText("Sync Leave");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(leaveModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.dcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.dcrArrow,binding.dcr);
                binding.childSync.setText("Sync DCR");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(dcrModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.workType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.workTypeArrow,binding.workType);
                binding.childSync.setText("Sync Work Type");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(workTypeModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.tourPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.tpArrow,binding.tourPlan);
                binding.childSync.setText("Sync Tour Plan");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(tpModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.slideArrow,binding.slide);
                binding.childSync.setText("Sync Slide");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(slideModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.subordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.subordinateArrow,binding.subordinate);
                binding.childSync.setText("Sync Subordinate");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(subordinateModelArray);
                populateAdapter(arrayForAdapter);

            }
        });

        binding.setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.setupArrow,binding.setup);
                binding.childSync.setText("Sync Setup");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(setupModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.childSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                ArrayList<MasterSyncItemModel> arrayList = new ArrayList<>();

                if (binding.listedDoctor.isSelected()){
                    arrayList.addAll(doctorModelArray);
                } else if (binding.chemist.isSelected()) {
                    arrayList.addAll(chemistModelArray);
                }else if (binding.stockiest.isSelected()) {
                    arrayList.addAll(stockiestModelArray);
                }else if (binding.unlistedDoctor.isSelected()) {
                    arrayList.addAll(unlistedDrModelArray);
                }else if (binding.hospital.isSelected()) {
                    arrayList.addAll(hospitalModelArray);
                }else if (binding.cip.isSelected()) {
                    arrayList.addAll(cipModelArray);
                }else if (binding.input.isSelected()) {
                    arrayList.addAll(inputModelArray);
                }else if (binding.product.isSelected()) {
                    arrayList.addAll(productModelArray);
                }else if (binding.cluster.isSelected()) {
                    arrayList.addAll(clusterModelArray);
                }else if (binding.leave.isSelected()) {
                    arrayList.addAll(leaveModelArray);
                } else if (binding.dcr.isSelected()) {
                    arrayList.addAll(dcrModelArray);
                }else if (binding.workType.isSelected()) {
                    arrayList.addAll(workTypeModelArray);
                }else if (binding.tourPlan.isSelected()) {
                    arrayList.addAll(tpModelArray);
                }else if (binding.slide.isSelected()) {
                    arrayList.addAll(slideModelArray);
                }else if (binding.subordinate.isSelected()) {
                    arrayList.addAll(subordinateModelArray);
                }else if (binding.setup.isSelected()) {
                    arrayList.addAll(setupModelArray);
                }

                for (int i=0;i<arrayList.size();i++){
                    arrayForAdapter.get(i).setPBarVisibility(true);
                    Log.e("test","notifyDataSetChanged 1");
                    masterSyncAdapter.notifyDataSetChanged();
                    sync(arrayList.get(i).getMasterOf(), arrayList.get(i).getRemoteTableName(), arrayList, i);
                }
            }
        });

        binding.masterSyncAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                masterSyncAll(false);
            }
        });

    }

    public void uiInitialization() {

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        sfType = loginResponse.getSf_type();
        sfCode = loginResponse.getSF_Code();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        state_code = loginResponse.getState_Code();
        binding.hqName.setText(SharedPref.getHqName(MasterSyncActivity.this));
        rsf = SharedPref.getHqCode(MasterSyncActivity.this); // Rsf is HQ code

        binding.hq.setEnabled(sfType.equalsIgnoreCase("2"));
        binding.lastSyncTime.setText(SharedPref.getLastSync(getApplicationContext()));

        binding.listedDrTxt.setText(loginResponse.getDrCap());
        binding.chemistTxt.setText(loginResponse.getChmCap());
        binding.stockiestTxt.setText(loginResponse.getStkCap());
        binding.unlistedDoctorTxt.setText(loginResponse.getNLCap());
        binding.hospitalTxt.setText(loginResponse.getHosp_caption());
        binding.cipTxt.setText(loginResponse.getCIP_Caption());

        doctorCount = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + rsf).length();
        specialityCount = sqLite.getMasterSyncDataByKey(Constants.SPECIALITY).length();
        qualificationCount = sqLite.getMasterSyncDataByKey(Constants.QUALIFICATION).length();
        categoryCount = sqLite.getMasterSyncDataByKey(Constants.CATEGORY).length();
        departmentCount = sqLite.getMasterSyncDataByKey(Constants.DEPARTMENT).length();
        classCount = sqLite.getMasterSyncDataByKey(Constants.CLASS).length();
        feedbackCount = sqLite.getMasterSyncDataByKey(Constants.FEEDBACK).length();
        unlistedDrCount = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + rsf).length();
        chemistCount = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + rsf).length();
        stockiestCount = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + rsf).length();
        hospitalCount = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + rsf) .length();
        cipCount = sqLite.getMasterSyncDataByKey(Constants.CIP + rsf).length();
        inputCount = sqLite.getMasterSyncDataByKey(Constants.INPUT).length();
        leaveCount = sqLite.getMasterSyncDataByKey(Constants.LEAVE).length();
        leaveStatusCount = sqLite.getMasterSyncDataByKey(Constants.LEAVE_STATUS).length();
        dcrCount = sqLite.getMasterSyncDataByKey(Constants.DCR).length();
        visitControlCount = sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL).length();
        missedDateCount = sqLite.getMasterSyncDataByKey(Constants.MISSED_DATE).length();

        workTypeCount = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE).length();
        holidayCount = sqLite.getMasterSyncDataByKey(Constants.HOLIDAY).length();
        weeklyOfCount = sqLite.getMasterSyncDataByKey(Constants.WEEKLY_OFF).length();
        tpCount = sqLite.getMasterSyncDataByKey(Constants.TP_PLAN).length();
        productCount = sqLite.getMasterSyncDataByKey(Constants.PRODUCT).length();
        proCatCount = sqLite.getMasterSyncDataByKey(Constants.PRODUCT_CATEGORY).length();
        brandCount = sqLite.getMasterSyncDataByKey(Constants.BRAND).length();
        compProCount = sqLite.getMasterSyncDataByKey(Constants.COMPETITOR_PROD).length();
        clusterCount = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + rsf).length();
        proSlideCount = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE).length();
        proSpeSlideCount = sqLite.getMasterSyncDataByKey(Constants.SPL_SLIDE).length();
        brandSlideCount = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE).length();
        therapticCount = sqLite.getMasterSyncDataByKey(Constants.THERAPTIC_SLIDE).length();
        subordinateCount = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE).length();
        subMgrCount = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE_MGR).length();
        jWorkCount = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + rsf).length();
        setupCount = sqLite.getMasterSyncDataByKey(Constants.SETUP).length();
        customSetupCount = sqLite.getMasterSyncDataByKey(Constants.CUSTOM_SETUP).length();

        doctorStatus = sqLite.getMasterSyncStatusByKey(Constants.DOCTOR + rsf);
        specialityStatus = sqLite.getMasterSyncStatusByKey(Constants.SPECIALITY);
        qualificationStatus = sqLite.getMasterSyncStatusByKey(Constants.QUALIFICATION);
        categoryStatus = sqLite.getMasterSyncStatusByKey(Constants.CATEGORY);
        departmentStatus = sqLite.getMasterSyncStatusByKey(Constants.DEPARTMENT);
        classStatus = sqLite.getMasterSyncStatusByKey(Constants.CLASS);
        feedbackStatus = sqLite.getMasterSyncStatusByKey(Constants.FEEDBACK);
        unlistedDrStatus = sqLite.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR + rsf);
        chemistStatus = sqLite.getMasterSyncStatusByKey(Constants.CHEMIST + rsf);
        stockiestStatus = sqLite.getMasterSyncStatusByKey(Constants.STOCKIEST + rsf);
        hospitalStatus = sqLite.getMasterSyncStatusByKey(Constants.HOSPITAL + rsf) ; //sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + rsf).length();
        cipStatus = sqLite.getMasterSyncStatusByKey(Constants.CIP + rsf);
        inputStatus = sqLite.getMasterSyncStatusByKey(Constants.INPUT);
        leaveStatus = sqLite.getMasterSyncStatusByKey(Constants.LEAVE);
        leaveStatusStatus = sqLite.getMasterSyncStatusByKey(Constants.LEAVE_STATUS);
        dcrStatus = sqLite.getMasterSyncStatusByKey(Constants.DCR);
        visitControlStatus = sqLite.getMasterSyncStatusByKey(Constants.VISIT_CONTROL);
        missedDateStatus = sqLite.getMasterSyncStatusByKey(Constants.MISSED_DATE);
        stockBalanceStatus = sqLite.getMasterSyncStatusByKey(Constants.STOCK_BALANCE_MASTER);

        workTypeStatus = sqLite.getMasterSyncStatusByKey(Constants.WORK_TYPE);
        holidayStatus = sqLite.getMasterSyncStatusByKey(Constants.HOLIDAY);
        weeklyOfStatus = sqLite.getMasterSyncStatusByKey(Constants.WEEKLY_OFF);
        tpStatus = sqLite.getMasterSyncStatusByKey(Constants.TP_PLAN);
        productStatus = sqLite.getMasterSyncStatusByKey(Constants.PRODUCT);
        proCatStatus = sqLite.getMasterSyncStatusByKey(Constants.PRODUCT_CATEGORY);
        brandStatus = sqLite.getMasterSyncStatusByKey(Constants.BRAND);
        compProStatus = sqLite.getMasterSyncStatusByKey(Constants.COMPETITOR_PROD);
        clusterStatus = sqLite.getMasterSyncStatusByKey(Constants.CLUSTER + rsf);
        proSlideStatus = sqLite.getMasterSyncStatusByKey(Constants.PROD_SLIDE);
        proSpeSlideStatus = sqLite.getMasterSyncStatusByKey(Constants.SPL_SLIDE);
        brandSlideStatus = sqLite.getMasterSyncStatusByKey(Constants.BRAND_SLIDE);
        therapticStatus = sqLite.getMasterSyncStatusByKey(Constants.THERAPTIC_SLIDE);
        subordinateStatus = sqLite.getMasterSyncStatusByKey(Constants.SUBORDINATE);
        subMgrStatus = sqLite.getMasterSyncStatusByKey(Constants.SUBORDINATE_MGR);
        jWorkStatus = sqLite.getMasterSyncStatusByKey(Constants.JOINT_WORK + rsf);
        setupStatus = sqLite.getMasterSyncStatusByKey(Constants.SETUP);
        customSetupStatus = sqLite.getMasterSyncStatusByKey(Constants.CUSTOM_SETUP);

        binding.listedDoctor.setSelected(true);
        prepareArray(rsf);

    }

    public void prepareArray(String hqCode){
        doctorModelArray.clear();
        MasterSyncItemModel doctorModel = new MasterSyncItemModel(loginResponse.getDrCap(), doctorCount, Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode,doctorStatus, false);
        MasterSyncItemModel spl = new MasterSyncItemModel(Constants.SPECIALITY,specialityCount,Constants.DOCTOR,"getspeciality",Constants.SPECIALITY,specialityStatus,false);
        MasterSyncItemModel ql = new MasterSyncItemModel(Constants.QUALIFICATION,qualificationCount,Constants.DOCTOR,"getquali",Constants.QUALIFICATION,qualificationStatus,false);
        MasterSyncItemModel cat = new MasterSyncItemModel(Constants.CATEGORY,categoryCount,Constants.DOCTOR,"getcategorys",Constants.CATEGORY,categoryStatus,false);
        MasterSyncItemModel dep = new MasterSyncItemModel(Constants.DEPARTMENT,departmentCount,Constants.DOCTOR,"getdeparts",Constants.DEPARTMENT,departmentStatus,false);
        MasterSyncItemModel clas = new MasterSyncItemModel(Constants.CLASS,classCount,Constants.DOCTOR,"getclass",Constants.CLASS,classStatus,false);
        MasterSyncItemModel feedback = new MasterSyncItemModel(Constants.FEEDBACK,feedbackCount,Constants.DOCTOR,"getdrfeedback",Constants.FEEDBACK,feedbackStatus,false);

        doctorModelArray.add(doctorModel);
        doctorModelArray.add(spl);
        doctorModelArray.add(ql);
        doctorModelArray.add(cat);
        doctorModelArray.add(dep);
        doctorModelArray.add(clas);
        doctorModelArray.add(feedback);

        //Chemist
        chemistModelArray.clear();
        MasterSyncItemModel cheModel = new MasterSyncItemModel(loginResponse.getChmCap(),chemistCount,Constants.DOCTOR,"getchemist",Constants.CHEMIST + hqCode,chemistStatus,false);
        chemistModelArray.add(cheModel);

        //Stockiest
        stockiestModelArray.clear();
        MasterSyncItemModel stockModel = new MasterSyncItemModel(loginResponse.getStkCap(),stockiestCount,Constants.DOCTOR,"getstockist",Constants.STOCKIEST + hqCode,stockiestStatus,false);
        stockiestModelArray.add(stockModel);

        //Unlisted Dr
        unlistedDrModelArray.clear();
        MasterSyncItemModel unListModel = new MasterSyncItemModel(loginResponse.getNLCap(),unlistedDrCount,Constants.DOCTOR,"getunlisteddr",Constants.UNLISTED_DOCTOR + hqCode,unlistedDrStatus,false);
        unlistedDrModelArray.add(unListModel);

        //Hospital
        hospitalModelArray.clear();
        MasterSyncItemModel hospModel = new MasterSyncItemModel(loginResponse.getHosp_caption(),hospitalCount,Constants.DOCTOR,"gethospital",Constants.HOSPITAL + hqCode,hospitalStatus,false);
        hospitalModelArray.add(hospModel);

        //CIP
        cipModelArray.clear();
        MasterSyncItemModel ciModel = new MasterSyncItemModel(loginResponse.getCIP_Caption(),cipCount,Constants.DOCTOR,"getcip",Constants.CIP + hqCode,cipStatus,false);
        cipModelArray.add(ciModel);

        //Cluster
        clusterModelArray.clear();
        MasterSyncItemModel cluster = new MasterSyncItemModel(loginResponse.getCluster_cap(), clusterCount, Constants.DOCTOR, "getterritory", Constants.CLUSTER + hqCode, clusterStatus, false);
        clusterModelArray.add(cluster);

        //Input
        inputModelArray.clear();
        MasterSyncItemModel inpModel = new MasterSyncItemModel(Constants.INPUT,inputCount,Constants.PRODUCT,"getinputs",Constants.INPUT,inputStatus,false);
        inputModelArray.add(inpModel);

        //Product
        productModelArray.clear();
        MasterSyncItemModel proModel = new MasterSyncItemModel(Constants.PRODUCT,productCount,Constants.PRODUCT,"getproducts",Constants.PRODUCT,productStatus,false);
        MasterSyncItemModel proCatModel = new MasterSyncItemModel(Constants.PRODUCT_CATEGORY,proCatCount,Constants.PRODUCT,"",Constants.PRODUCT_CATEGORY,proCatStatus,false);
        MasterSyncItemModel brandModel = new MasterSyncItemModel(Constants.BRAND,brandCount,Constants.PRODUCT,"getbrands",Constants.BRAND,brandStatus,false);
        MasterSyncItemModel compProductModel = new MasterSyncItemModel(Constants.COMPETITOR_PROD,compProCount,Constants.PRODUCT,"getcompdet",Constants.COMPETITOR_PROD,compProStatus,false);
        productModelArray.add(proModel);
        productModelArray.add(proCatModel);
        productModelArray.add(brandModel);
        productModelArray.add(compProductModel);

        //Leave
        leaveModelArray.clear();
        MasterSyncItemModel leaveModel = new MasterSyncItemModel(Constants.LEAVE,leaveCount,"Leave","getleavetype",Constants.LEAVE,leaveStatus,false);
        MasterSyncItemModel leaveStatusModel = new MasterSyncItemModel(Constants.LEAVE_STATUS,leaveStatusCount,"Leave","getleavestatus",Constants.LEAVE_STATUS,leaveStatusStatus,false);
        leaveModelArray.add(leaveModel);
        leaveModelArray.add(leaveStatusModel);

        //DCR
        dcrModelArray.clear();
        MasterSyncItemModel dcrModel = new MasterSyncItemModel(Constants.DCR,dcrCount,"Home","gethome",Constants.DCR,dcrStatus,false);
        MasterSyncItemModel visitControlModel = new MasterSyncItemModel(Constants.VISIT_CONTROL,visitControlCount,"AdditionalDcr","getvisit_contro",Constants.VISIT_CONTROL,visitControlStatus,false);
        MasterSyncItemModel missedDateModel = new MasterSyncItemModel(Constants.MISSED_DATE,missedDateCount,"MissedDate","getmissdates",Constants.MISSED_DATE,missedDateStatus,false);
        MasterSyncItemModel stockBalanceModel = new MasterSyncItemModel(Constants.STOCK_BALANCE,0,"AdditionalDcr","getstockbalance",Constants.STOCK_BALANCE_MASTER,stockBalanceStatus,false);
        dcrModelArray.add(dcrModel);
        dcrModelArray.add(visitControlModel);
        dcrModelArray.add(missedDateModel);
        dcrModelArray.add(stockBalanceModel);

        //Work Type
        workTypeModelArray.clear();
        MasterSyncItemModel workType = new MasterSyncItemModel(Constants.WORK_TYPE,workTypeCount,Constants.DOCTOR,"getworktype",Constants.WORK_TYPE,workTypeStatus,false);
        MasterSyncItemModel holiday = new MasterSyncItemModel(Constants.HOLIDAY,holidayCount,Constants.DOCTOR,"getholiday",Constants.HOLIDAY,holidayStatus,false);
        MasterSyncItemModel weeklyOff = new MasterSyncItemModel(Constants.WEEKLY_OFF,weeklyOfCount,Constants.DOCTOR,"getweeklyoff",Constants.WEEKLY_OFF,weeklyOfStatus,false);
        workTypeModelArray.add(workType);
        workTypeModelArray.add(holiday);
        workTypeModelArray.add(weeklyOff);

        //Tour Plan
        tpModelArray.clear();
        MasterSyncItemModel tp = new MasterSyncItemModel("Tour Plan Setup",tpCount,Constants.SETUP,"gettpsetup",Constants.TP_PLAN,tpStatus,false);
        tpModelArray.add(tp);

        //Slide
        slideModelArray.clear();
        MasterSyncItemModel proSlideModel = new MasterSyncItemModel(Constants.PROD_SLIDE,proSlideCount,"Slide","getprodslides",Constants.PROD_SLIDE,proSlideStatus,false);
        MasterSyncItemModel splSlideModel = new MasterSyncItemModel(Constants.SPL_SLIDE,proSpeSlideCount,"Slide","getslidespeciality",Constants.SPL_SLIDE,proSpeSlideStatus,false);
        MasterSyncItemModel brandSlideModel = new MasterSyncItemModel(Constants.BRAND_SLIDE,brandSlideCount,"Slide","getslidebrand",Constants.BRAND_SLIDE,brandSlideStatus,false);
        MasterSyncItemModel therapticSlideModel = new MasterSyncItemModel(Constants.THERAPTIC_SLIDE,therapticCount,"Slide","gettheraptic",Constants.THERAPTIC_SLIDE,therapticStatus,false);
        slideModelArray.add(proSlideModel);
        slideModelArray.add(splSlideModel);
        slideModelArray.add(brandSlideModel);
        slideModelArray.add(therapticSlideModel);

        //Subordinate
        subordinateModelArray.clear();
        MasterSyncItemModel subModel = new MasterSyncItemModel(Constants.SUBORDINATE,subordinateCount,Constants.SUBORDINATE,"getsubordinate",Constants.SUBORDINATE,subordinateStatus,false);
        MasterSyncItemModel subMgrModel = new MasterSyncItemModel(Constants.SUBORDINATE_MGR,subMgrCount,Constants.SUBORDINATE,"getsubordinatemgr",Constants.SUBORDINATE_MGR,subMgrStatus,false);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Joint Work",jWorkCount,Constants.SUBORDINATE,"getjointwork",Constants.JOINT_WORK + hqCode,jWorkStatus,false);
        subordinateModelArray.add(subModel);
        subordinateModelArray.add(subMgrModel);
        subordinateModelArray.add(jWorkModel);

        //Setup
        setupModelArray.clear();
        MasterSyncItemModel setupModel = new MasterSyncItemModel(Constants.SETUP,setupCount,Constants.SETUP,"getsetups",Constants.SETUP,setupStatus,false);
        MasterSyncItemModel customSetupModel = new MasterSyncItemModel(Constants.CUSTOM_SETUP,customSetupCount,Constants.SETUP,"getcustomsetup",Constants.CUSTOM_SETUP,customSetupStatus,false);
        setupModelArray.add(setupModel);
        setupModelArray.add(customSetupModel);

        passDataToAdapter();

    }

    public void listItemClicked(ImageView imageView, LinearLayout view){
        binding.listDrArrow.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arrow_down, null));
        binding.chemistArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.stockArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.unListDrArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.hospitalArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.cipArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.inputArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.productArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.clusterArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.leaveArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.dcrArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.workTypeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.tpArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.slideArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.subordinateArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        binding.setupArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));

        binding.listedDoctor.setSelected(false);
        binding.unlistedDoctor.setSelected(false);
        binding.chemist.setSelected(false);
        binding.stockiest.setSelected(false);
        binding.hospital.setSelected(false);
        binding.cip.setSelected(false);
        binding.input.setSelected(false);
        binding.product.setSelected(false);
        binding.cluster.setSelected(false);
        binding.leave.setSelected(false);
        binding.dcr.setSelected(false);
        binding.workType.setSelected(false);
        binding.tourPlan.setSelected(false);
        binding.slide.setSelected(false);
        binding.subordinate.setSelected(false);
        binding.setup.setSelected(false);

        imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_black, null));
        view.setSelected(true);
    }

    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels){

        masterSyncAdapter = new MasterSyncAdapter(masterSyncItemModels, getApplicationContext(), new MasterSyncItemClick() {
            @Override
            public void itemClick (MasterSyncItemModel masterSyncItemModel1,int position) {
                if (binding.listedDoctor.isSelected()){
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), doctorModelArray, position);
                } else if (binding.chemist.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), chemistModelArray, position);
                }else if (binding.stockiest.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), stockiestModelArray, position);
                }else if (binding.unlistedDoctor.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), unlistedDrModelArray, position);
                }else if (binding.hospital.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), hospitalModelArray, position);
                }else if (binding.cip.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), cipModelArray, position);
                }else if (binding.input.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), inputModelArray, position);
                }else if (binding.product.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), productModelArray, position);
                } else if (binding.cluster.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), clusterModelArray, position);
                }else if (binding.leave.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), leaveModelArray, position);
                }else if (binding.dcr.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), dcrModelArray, position);
                }else if (binding.workType.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), workTypeModelArray, position);
                }else if (binding.tourPlan.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), tpModelArray, position);
                }else if (binding.slide.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), slideModelArray, position);
                }else if (binding.subordinate.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), subordinateModelArray, position);
                }else if (binding.setup.isSelected()) {
                    sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), setupModelArray, position);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,3);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(masterSyncAdapter);
        masterSyncAdapter.notifyDataSetChanged();
    }

    public void passDataToAdapter(){
        if (binding.listedDoctor.isSelected()){
            populateAdapter(doctorModelArray);
        } else if (binding.chemist.isSelected()) {
            populateAdapter(chemistModelArray);
        }else if (binding.stockiest.isSelected()) {
            populateAdapter(stockiestModelArray);
        }else if (binding.unlistedDoctor.isSelected()) {
            populateAdapter(unlistedDrModelArray);
        }else if (binding.hospital.isSelected()) {
            populateAdapter(hospitalModelArray);
        }else if (binding.cip.isSelected()) {
            populateAdapter(cipModelArray);
        }else if (binding.input.isSelected()) {
            populateAdapter(inputModelArray);
        }else if (binding.product.isSelected()) {
            populateAdapter(productModelArray);
        } else if (binding.cluster.isSelected()) {
            populateAdapter(clusterModelArray);
        }else if (binding.leave.isSelected()) {
            populateAdapter(leaveModelArray);
        }else if (binding.dcr.isSelected()) {
            populateAdapter(dcrModelArray);
        }else if (binding.workType.isSelected()) {
            populateAdapter(workTypeModelArray);
        }else if (binding.tourPlan.isSelected()) {
            populateAdapter(tpModelArray);
        }else if (binding.slide.isSelected()) {
            populateAdapter(slideModelArray);
        }else if (binding.subordinate.isSelected()) {
            populateAdapter(subordinateModelArray);
        }else if (binding.setup.isSelected()) {
            populateAdapter(setupModelArray);
        }
    }

    public void masterSyncAll(boolean hqChanged){
        masterSyncAllModel.clear();
        itemCount = 0;
        apiSuccessCount = 0;

        //Whenever hq changed true , we need to sync only dr,chemist,stockiest,unListDr,hosp,cip,cluster and joint work
        masterSyncAllModel.add(doctorModelArray);
        masterSyncAllModel.add(chemistModelArray);
        masterSyncAllModel.add(stockiestModelArray);
        masterSyncAllModel.add(unlistedDrModelArray);
        masterSyncAllModel.add(hospitalModelArray);
        masterSyncAllModel.add(cipModelArray);
        masterSyncAllModel.add(clusterModelArray);
        masterSyncAllModel.add(subordinateModelArray);

        if (!hqChanged){
            masterSyncAllModel.add(inputModelArray);
            masterSyncAllModel.add(productModelArray);
            masterSyncAllModel.add(leaveModelArray);
            masterSyncAllModel.add(dcrModelArray);
            masterSyncAllModel.add(workTypeModelArray);
            masterSyncAllModel.add(tpModelArray);
            masterSyncAllModel.add(slideModelArray);
            masterSyncAllModel.add(setupModelArray);
        }

        for (int i=0; i<masterSyncAllModel.size(); i++) {
            ArrayList<MasterSyncItemModel> childArray = new ArrayList<>(masterSyncAllModel.get(i));
            itemCount += childArray.size();
            for (int j=0; j<childArray.size(); j++) {
                if (hqChanged) {
                    if (childArray.get(j).getLocalTableKeyName().contains(rsf)){
                        childArray.get(j).setPBarVisibility(true);
                        masterSyncAdapter.notifyDataSetChanged();
                        sync(childArray.get(j).getMasterOf(), childArray.get(j).getRemoteTableName(), childArray, j);
                    }
                }else{
                    childArray.get(j).setPBarVisibility(true);
                    masterSyncAdapter.notifyDataSetChanged();
                    sync(childArray.get(j).getMasterOf(), childArray.get(j).getRemoteTableName(), childArray, j);
                }
            }
        }
//        Log.e("test","count : " + itemCount);
    }

    public void sync(String masterOf, String remoteTableName,ArrayList<MasterSyncItemModel> masterSyncItemModels,int position) {

        if (UtilityClass.isNetworkAvailable(MasterSyncActivity.this)){
            try {
                String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
                String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
                String replacedUrl = pathUrl.replaceAll("\\?.*","/");
                apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl+replacedUrl);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", sfCode);
                jsonObject.put("division_code", division_code);
                jsonObject.put("Rsf", rsf);
                jsonObject.put("sf_type", sfType);
                jsonObject.put("Designation", designation);
                jsonObject.put("state_code", state_code);
                jsonObject.put("subdivision_code", subdivision_code);
                if (remoteTableName.equalsIgnoreCase("getholiday") || remoteTableName.equalsIgnoreCase("getweeklyoff")){
                    jsonObject.put("year",Year.now().getValue());
                }

            Log.e("test","master sync obj : " + jsonObject);
                Call<JsonElement> call = null;
                if (masterOf.equalsIgnoreCase(Constants.DOCTOR)){
                    call = apiInterface.getDrMaster(jsonObject.toString());
                } else if (masterOf.equalsIgnoreCase(Constants.SUBORDINATE)) {
                    call = apiInterface.getSubordinateMaster(jsonObject.toString());
                }else if (masterOf.equalsIgnoreCase(Constants.PRODUCT)) {
                    call = apiInterface.getProductMaster(jsonObject.toString());
                }else if (masterOf.equalsIgnoreCase("Leave")) {
                    call = apiInterface.getLeaveMaster(jsonObject.toString());
                }else if (masterOf.equalsIgnoreCase("Home")) {
                    call = apiInterface.getDCRMaster(jsonObject.toString());
                }else if (masterOf.equalsIgnoreCase("MissedDate")) {
                    call = apiInterface.getMissedDCRMaster(jsonObject.toString());
                }else if (masterOf.equalsIgnoreCase("AdditionalDcr")) {
                    call = apiInterface.getAdditionalMaster(jsonObject.toString());
                }else if (masterOf.equalsIgnoreCase("Slide")) {
                    call = apiInterface.getSlideMaster(jsonObject.toString());
                }else if (masterOf.equalsIgnoreCase(Constants.SETUP)) {
                    call = apiInterface.getSetupMaster(jsonObject.toString());
                }
                if (call != null){
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse (@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            masterSyncItemModels.get(position).setPBarVisibility(false);
                            apiSuccessCount ++;
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();
                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                                try {
                                    JsonElement jsonElement = response.body();
                                    if (!jsonElement.isJsonNull()){
                                        if (jsonElement.isJsonArray()){
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                            if (!jsonObject2.has("success")){ // "success" will not be received when api call is success,response as jsonObject with {"success" : "fail" } will be received only when api call is failed for any reasons
                                                jsonArray.put(jsonObject2);
                                                success = true;
                                            } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                                sqLite.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(),1); // only update sync status and no need to overwrite previous data when failed
                                                masterSyncItemModels.get(position).setSyncSuccess(1);
                                            }
                                        }

                                        if (success) {
                                            masterSyncItemModels.get(position).setCount(jsonArray.length());
                                            masterSyncItemModels.get(position).setSyncSuccess(0);

                                            String dateAndTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_16);
                                            binding.lastSyncTime.setText(dateAndTime);
                                            SharedPref.saveMasterLastSync(getApplicationContext(),dateAndTime );
                                            sqLite.saveMasterSyncData(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(),0);

                                            if(masterOf.equalsIgnoreCase("AdditionalDcr") && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getstockbalance")){
                                                if (jsonArray.length() > 0){
                                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                    JSONArray stockBalanceArray = jsonObject1.getJSONArray("Sample_Stock");
                                                    JSONArray inputBalanceArray = jsonObject1.getJSONArray("Input_Stock");
                                                    sqLite.saveMasterSyncData(Constants.STOCK_BALANCE, stockBalanceArray.toString(),0);
                                                    sqLite.saveMasterSyncData(Constants.INPUT_BALANCE, inputBalanceArray.toString(),0);
                                                }

                                            }else if(masterOf.equalsIgnoreCase(Constants.SUBORDINATE) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getsubordinate")){
                                                if(mgrInitialSync)
                                                    setHq(jsonArray);

                                            }else if(masterSyncItemModels.get(position).getName().equalsIgnoreCase(Constants.SETUP)){
                                                if (jsonArray.length() > 0){
                                                    JSONObject jsonSetup = jsonArray.getJSONObject(0);
                                                    if (jsonSetup.has("geoTagImg"))
                                                        SharedPref.setGeotagImage(getApplicationContext(), jsonSetup.getString("geoTagImg"));
                                                    if (jsonSetup.has("GeoTagApprovalNeed"))
                                                        SharedPref.setGeotagApprovalNeed(getApplicationContext(), jsonSetup.getString("GeoTagApprovalNeed"));
                                                }
                                            }
                                        }

                                        if (apiSuccessCount >= itemCount && navigateFrom.equalsIgnoreCase("Login")){
                                            startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
                                        }

                                    } else {
                                        masterSyncItemModels.get(position).setSyncSuccess(1);
                                        sqLite.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(),1);
                                    }

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                masterSyncItemModels.get(position).setSyncSuccess(1);
                                sqLite.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(),1);
                            }

                            masterSyncAdapter.notifyDataSetChanged();
                            // Log.e("test","success count : " + apiSuccessCount);
                        }

                        @Override
                        public void onFailure (@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test","failed : " + t);
                            apiSuccessCount++;
                            //  Log.e("test","success count at error : " + apiSuccessCount);
                            sqLite.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(),  1);
                            masterSyncItemModels.get(position).setPBarVisibility(false);
                            masterSyncItemModels.get(position).setSyncSuccess(1);
                            masterSyncAdapter.notifyDataSetChanged();
                            if (apiSuccessCount >= itemCount && navigateFrom.equalsIgnoreCase("Login")){
                                startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    public void setHq(JSONArray jsonArray){
        mgrInitialSync = false;
        apiSuccessCount = 0;
        if (jsonArray.length() > 0){
            try {
                binding.hqName.setText(jsonArray.getJSONObject(0).getString("name"));
                rsf = jsonArray.getJSONObject(0).getString("id");
                SharedPref.saveHq(MasterSyncActivity.this,jsonArray.getJSONObject(0).getString("name"),rsf);
                prepareArray(rsf);// to replace the new rsf values
                masterSyncAll(false);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void callList(SQLite sqLite, ApiInterface apiInterface, Context context, String masterFor, String tablename, String sfCode, String DivCode, String RSF, String SfType, String Designation, String StateCode, String SubDivCode) {
        try {
            sqLite = new SQLite(context);
            sqLite.getWritableDatabase();
            String baseUrl = SharedPref.getBaseWebUrl(context);
            String pathUrl = SharedPref.getPhpPathUrl(context);
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
            apiInterface = RetrofitClient.getRetrofit(context, baseUrl + replacedUrl);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", tablename);
            jsonObject.put("sfcode", sfCode);
            jsonObject.put("division_code", DivCode);
            jsonObject.put("Rsf", RSF);
            jsonObject.put("sf_type", SfType);
            jsonObject.put("Designation", Designation);
            jsonObject.put("state_code", StateCode);
            jsonObject.put("subdivision_code", SubDivCode);
            Log.v("table_json", jsonObject.toString());

            Call<JsonElement> call = null;
            call = apiInterface.getDrMaster(jsonObject.toString());

            SQLite finalSqLite = sqLite;
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.v("table_json", response.body().toString());
                    if (response.isSuccessful()) {
                        try {
                            JsonElement jsonElement = response.body();
                            JSONArray jsonArray = new JSONArray();
                            if (jsonElement.isJsonArray()) {
                                JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                jsonArray = new JSONArray(jsonArray1.toString());
                            } else if (jsonElement.isJsonObject()) {
                                JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                jsonArray.put(jsonObject2);
                            }

                            finalSqLite.saveMasterSyncData(masterFor + "_" + sfCode, jsonArray.toString(),0);

                        } catch (Exception e) {
                            Log.v("table_json", "error---" + e);
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                }
            });

        } catch (Exception e) {

        }
    }

}