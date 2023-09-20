package saneforce.sanclm.activity.masterSync;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.HomeDashBoard;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.common.Constants;
import saneforce.sanclm.databinding.ActivityMasterSyncBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.DateTimeFormat;


public class MasterSyncActivity extends AppCompatActivity {

    ActivityMasterSyncBinding binding;
    ApiInterface apiInterface;
    MasterSyncViewModel masterSyncViewModel = new MasterSyncViewModel();
    MasterSyncAdapter masterSyncAdapter = new MasterSyncAdapter();
    SQLite sqLite;
    LoginResponse loginResponse;
    String sfCode = "",division_code = "",rsf ="",sf_type = "",designation = "",state_code ="",subdivision_code = "";
    int doctorCount = 0,specialityCount = 0,qualificationCount = 0,categoryCount = 0,departmentCount = 0,classCount = 0,feedbackCount = 0;
    int unlistedDrCount = 0,chemistCount = 0,stockiestCount = 0,hospitalCount = 0,cipCount = 0, inputCount = 0, leaveCount = 0,leaveStatusCount = 0,tpCount =0;
    int productCount = 0, proCatCount = 0,brandCount = 0, compProCount = 0;
    int proSlideCount = 0,proSpeSlideCount = 0,brandSlideCount = 0, therapticCount = 0;
    int subordinateCount = 0,subMgrCount = 0,jWorkCount= 0;
    int setupCount = 0, customSetupCount = 0;
    int apiSuccessCount = 0;
    int itemCount = 0;
    String origin = "";

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
    ArrayList<MasterSyncItemModel> leaveModelArray = new ArrayList<>();
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
            origin = getIntent().getExtras().getString("Origin");
        }
        Log.e("test","origin is  : " + origin);

        Cursor cursor = sqLite.getLoginData();
        loginResponse = new LoginResponse();

        String loginData = "";
        if (cursor.moveToNext()){
            loginData = cursor.getString(0);
        }
        cursor.close();
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        loginResponse = new Gson().fromJson(loginData, type);
        Log.e("test", "login data from sqlite : " + new Gson().toJson(loginResponse));
        binding.hqName.setText(loginResponse.getHQName());
        sfCode = loginResponse.getSF_Code();
        sf_type = loginResponse.getSf_type();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        rsf = loginResponse.getSF_Code();
        state_code = loginResponse.getState_Code();

        try {
            //Initializing all the data array
            uiInitialization();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        binding.listedDoctor.setSelected(true);
       // masterSyncAll();

        if (origin.equalsIgnoreCase("Login")){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            masterSyncAll();
        }else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        binding.lastSyncTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(MasterSyncActivity.this,HomeDashBoard.class));
            }
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
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
                }else if (binding.leave.isSelected()) {
                    arrayList.addAll(leaveModelArray);
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
                    sync(arrayList.get(i).getMasterFor(),arrayList.get(i).getRemoteTableName(),arrayList,i);
                    arrayForAdapter.get(i).setPB_visibility(true);
                    masterSyncAdapter.notifyDataSetChanged();
                }
            }
        });

        binding.masterSyncAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                masterSyncAll();
            }
        });

    }

    public void uiInitialization() throws JSONException {
        binding.lastSyncTime.setText(SharedPref.getLastSync(getApplicationContext()));

        binding.listedDrTxt.setText(loginResponse.getDrCap());
        binding.chemistTxt.setText(loginResponse.getChmCap());
        binding.stockiestTxt.setText(loginResponse.getStkCap());
        binding.unlistedDoctorTxt.setText(loginResponse.getNLCap());

        binding.listedDoctor.setSelected(true);
        doctorCount =  sqLite.getMasterSyncDataByKey(Constants.DOCTOR).length();
        specialityCount =  sqLite.getMasterSyncDataByKey(Constants.SPECIALITY).length();
        qualificationCount =  sqLite.getMasterSyncDataByKey(Constants.QUALIFICATION).length();
        categoryCount =  sqLite.getMasterSyncDataByKey(Constants.CATEGORY).length();
        departmentCount =  sqLite.getMasterSyncDataByKey(Constants.DEPARTMENT).length();
        classCount =  sqLite.getMasterSyncDataByKey(Constants.CLASS).length();
        feedbackCount =  sqLite.getMasterSyncDataByKey(Constants.FEEDBACK).length();

        unlistedDrCount = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR).length();
        chemistCount = sqLite.getMasterSyncDataByKey(Constants.CHEMIST).length();
        stockiestCount = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST).length();
        hospitalCount = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL).length();
        cipCount = sqLite.getMasterSyncDataByKey(Constants.CIP).length();
        inputCount = sqLite.getMasterSyncDataByKey(Constants.INPUT).length();
        leaveCount = sqLite.getMasterSyncDataByKey(Constants.LEAVE).length();
        leaveStatusCount = sqLite.getMasterSyncDataByKey(Constants.LEAVE_STATUS).length();

        tpCount = sqLite.getMasterSyncDataByKey(Constants.TP_PLAN).length();
        productCount = sqLite.getMasterSyncDataByKey(Constants.PRODUCT).length();
//        proCatCount = sqLite.getMasterSyncDataByKey(Constants.PR).length();
        brandCount = sqLite.getMasterSyncDataByKey(Constants.BRAND).length();
        compProCount = sqLite.getMasterSyncDataByKey(Constants.COMPETITOR_PROD).length();
        proSlideCount = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE).length();
        proSpeSlideCount = sqLite.getMasterSyncDataByKey(Constants.SPL_SLIDE).length();
        brandSlideCount = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE).length();
        therapticCount = sqLite.getMasterSyncDataByKey(Constants.THERAPTIC_SLIDE).length();
        subordinateCount = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE).length();
        subMgrCount = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE_MGR).length();
        jWorkCount = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK).length();
        setupCount = sqLite.getMasterSyncDataByKey(Constants.SETUP).length();
        customSetupCount = sqLite.getMasterSyncDataByKey(Constants.CUSTOM_SETUP).length();

        doctorModelArray.clear();
        MasterSyncItemModel doctor = new MasterSyncItemModel(loginResponse.getDrCap(),doctorCount,"Doctor","getdoctors",Constants.DOCTOR,false);
        MasterSyncItemModel spl = new MasterSyncItemModel("Speciality",specialityCount,"Doctor","getspeciality",Constants.SPECIALITY,false);
        MasterSyncItemModel ql = new MasterSyncItemModel("Qualification",qualificationCount,"Doctor","getquali",Constants.QUALIFICATION,false);
        MasterSyncItemModel cat = new MasterSyncItemModel("Category",categoryCount,"Doctor","getcategorys",Constants.CATEGORY,false);
        MasterSyncItemModel dep = new MasterSyncItemModel("Department",departmentCount,"Doctor","getdeparts",Constants.DEPARTMENT,false);
        MasterSyncItemModel clas = new MasterSyncItemModel("Class",classCount,"Doctor","getclass",Constants.CLASS,false);
        MasterSyncItemModel feedback = new MasterSyncItemModel("Feedback",feedbackCount,"Doctor","getdrfeedback",Constants.FEEDBACK,false);

        doctorModelArray.add(doctor);
        doctorModelArray.add(spl);
        doctorModelArray.add(ql);
        doctorModelArray.add(cat);
        doctorModelArray.add(dep);
        doctorModelArray.add(clas);
        doctorModelArray.add(feedback);
        arrayForAdapter.clear();
        arrayForAdapter.addAll(doctorModelArray);
        populateAdapter(arrayForAdapter);

        //Unlisted Dr
        unlistedDrModelArray.clear();
        MasterSyncItemModel unListModel = new MasterSyncItemModel("Unlisted Doctor",unlistedDrCount,"Doctor","getunlisteddr",Constants.UNLISTED_DOCTOR,false);
        unlistedDrModelArray.add(unListModel);

        //Chemist
        chemistModelArray.clear();
        MasterSyncItemModel cheModel = new MasterSyncItemModel("Chemist",chemistCount,"Doctor","getchemist",Constants.CHEMIST,false);
        chemistModelArray.add(cheModel);

        //Stockiest
        stockiestModelArray.clear();
        MasterSyncItemModel stockModel = new MasterSyncItemModel("Stockiest",stockiestCount,"Doctor","getstockist",Constants.STOCKIEST,false);
        stockiestModelArray.add(stockModel);

        //Hospital
        hospitalModelArray.clear();
        MasterSyncItemModel hospModel = new MasterSyncItemModel("Hospital",hospitalCount,"Doctor","gethospital",Constants.HOSPITAL,false);
        hospitalModelArray.add(hospModel);

        //CIP
        cipModelArray.clear();
        MasterSyncItemModel ciModel = new MasterSyncItemModel("CIP",cipCount,"Doctor","getcip",Constants.CIP,false);
        cipModelArray.add(ciModel);

        //Input
        inputModelArray.clear();
        MasterSyncItemModel inpModel = new MasterSyncItemModel("Input",inputCount,"Product","getinputs",Constants.INPUT,false);
        inputModelArray.add(inpModel);

        //Product
        productModelArray.clear();
        MasterSyncItemModel proModel = new MasterSyncItemModel("Product",productCount,"Product","getproducts",Constants.PRODUCT,false);
        MasterSyncItemModel proCatModel = new MasterSyncItemModel("Product Category",proCatCount,"Product","","",false);
        MasterSyncItemModel brandModel = new MasterSyncItemModel("Brand",brandCount,"Product","getbrands",Constants.BRAND,false);
        MasterSyncItemModel compProductModel = new MasterSyncItemModel("Competitor Product",compProCount,"Product","getcompdet",Constants.COMPETITOR_PROD,false);
        productModelArray.add(proModel);
        productModelArray.add(proCatModel);
        productModelArray.add(brandModel);
        productModelArray.add(compProductModel);

        //Leave
        leaveModelArray.clear();
        MasterSyncItemModel leaveModel = new MasterSyncItemModel("Leave Type",leaveCount,"Leave","getleavetype",Constants.LEAVE,false);
        MasterSyncItemModel leaveStatusModel = new MasterSyncItemModel("Leave Status",leaveStatusCount,"Leave","getleavestatus",Constants.LEAVE,false);
        leaveModelArray.add(leaveModel);
        leaveModelArray.add(leaveStatusModel);

        //Tour Plan
        tpModelArray.clear();
        MasterSyncItemModel masterSyncItemModel = new MasterSyncItemModel("Tour Plan",tpCount,"TP","gettpstatus",Constants.TP_PLAN,false);
        tpModelArray.add(masterSyncItemModel);

        //Slide
        slideModelArray.clear();
        MasterSyncItemModel proSlideModel = new MasterSyncItemModel("Product Slide",proSlideCount,"Slide","getprodslides",Constants.PROD_SLIDE,false);
        MasterSyncItemModel splSlideModel = new MasterSyncItemModel("Speciality Slide ",proSpeSlideCount,"Slide","getslidespeciality",Constants.SPL_SLIDE,false);
        MasterSyncItemModel brandSlideModel = new MasterSyncItemModel("Brand Slide",brandSlideCount,"Slide","getslidebrand",Constants.BRAND_SLIDE,false);
        MasterSyncItemModel therapticSlideModel = new MasterSyncItemModel("Theraptic Slide",therapticCount,"Slide","gettheraptic",Constants.THERAPTIC_SLIDE,false);
        slideModelArray.add(proSlideModel);
        slideModelArray.add(splSlideModel);
        slideModelArray.add(brandSlideModel);
        slideModelArray.add(therapticSlideModel);

        //Subordinate
        subordinateModelArray.clear();
        MasterSyncItemModel subModel = new MasterSyncItemModel("Subordinate",subordinateCount,"Subordinate","getsubordinate",Constants.SUBORDINATE,false);
        MasterSyncItemModel subMgrModel = new MasterSyncItemModel("Subordinate MGR",subMgrCount,"Subordinate","getsubordinatemgr",Constants.SUBORDINATE_MGR,false);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Joint Work",jWorkCount,"Subordinate","getjointwork",Constants.JOINT_WORK,false);

        subordinateModelArray.add(subModel);
        subordinateModelArray.add(subMgrModel);
        subordinateModelArray.add(jWorkModel);

        //Setup
        setupModelArray.clear();
        MasterSyncItemModel setupModel = new MasterSyncItemModel("Setup",setupCount,"Setup","getsetups",Constants.SETUP,false);
        MasterSyncItemModel customSetupModel = new MasterSyncItemModel("Custom Setup",customSetupCount,"Setup","getcustomsetup",Constants.CUSTOM_SETUP,false);
        setupModelArray.add(setupModel);
        setupModelArray.add(customSetupModel);

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
        binding.leaveArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
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
        binding.leave.setSelected(false);
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
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), doctorModelArray, position);
                } else if (binding.chemist.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), chemistModelArray, position);
                }else if (binding.stockiest.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), stockiestModelArray, position);
                }else if (binding.unlistedDoctor.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), unlistedDrModelArray, position);
                }else if (binding.hospital.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), hospitalModelArray, position);
                }else if (binding.cip.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), cipModelArray, position);
                }else if (binding.input.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), inputModelArray, position);
                }else if (binding.product.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), productModelArray, position);
                }else if (binding.leave.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), leaveModelArray, position);
                }else if (binding.tourPlan.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), tpModelArray, position);
                }else if (binding.slide.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), slideModelArray, position);
                }else if (binding.subordinate.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), subordinateModelArray, position);
                }else if (binding.setup.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), subordinateModelArray, position);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,3);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(masterSyncAdapter);
        masterSyncAdapter.notifyDataSetChanged();
    }

    public void masterSyncAll(){
        masterSyncAllModel.add(doctorModelArray);
        masterSyncAllModel.add(chemistModelArray);
        masterSyncAllModel.add(stockiestModelArray);
        masterSyncAllModel.add(unlistedDrModelArray);
        masterSyncAllModel.add(hospitalModelArray);
        masterSyncAllModel.add(cipModelArray);
        masterSyncAllModel.add(inputModelArray);
        masterSyncAllModel.add(productModelArray);
        masterSyncAllModel.add(leaveModelArray);
        masterSyncAllModel.add(tpModelArray);
        masterSyncAllModel.add(slideModelArray);
        masterSyncAllModel.add(subordinateModelArray);
        masterSyncAllModel.add(setupModelArray);
        Log.e("test","masterAll size : " + masterSyncAllModel.size());


        for (int i=0;i<masterSyncAllModel.size();i++){
            ArrayList<MasterSyncItemModel> childArray = new ArrayList<>(masterSyncAllModel.get(i));
            itemCount  += childArray.size();
            for (int j=0;j<childArray.size();j++){
                childArray.get(j).setPB_visibility(true);
                masterSyncAdapter.notifyDataSetChanged();
                sync(childArray.get(j).getMasterFor(),childArray.get(j).getRemoteTableName(),childArray,j);
            }
        }
        Log.e("test","count : " + itemCount);

    }

    public void sync(String masterFor, String remoteTableName,ArrayList<MasterSyncItemModel> masterSyncItemModels,int position)  {

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
            jsonObject.put("sf_type", sf_type);
            jsonObject.put("Designation", designation);
            jsonObject.put("state_code", state_code);
            jsonObject.put("subdivision_code", subdivision_code);

//            Log.e("test","master sync obj : " + jsonObject);
            Call<JsonArray> call = null;
            if (masterFor.equalsIgnoreCase("Doctor")){
                call = apiInterface.getDrMaster(jsonObject.toString());
            } else if (masterFor.equalsIgnoreCase("Subordinate")) {
                call = apiInterface.getSubordinateMaster(jsonObject.toString());
            }else if (masterFor.equalsIgnoreCase("Product")) {
                call = apiInterface.getProductMaster(jsonObject.toString());
            }else if (masterFor.equalsIgnoreCase("Leave")) {
                call = apiInterface.getLeaveMaster(jsonObject.toString());
            }else if (masterFor.equalsIgnoreCase("Slide")) {
                call = apiInterface.getSlideMaster(jsonObject.toString());
            }else if (masterFor.equalsIgnoreCase("Setup")) {
                call = apiInterface.getSetupMaster(jsonObject.toString());
            }else if (masterFor.equalsIgnoreCase("TP")) {
                call = apiInterface.getAdditionalMaster(jsonObject.toString());
            }

            if (call != null){
                call.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse (@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                        masterSyncItemModels.get(position).setPB_visibility(false);
                        apiSuccessCount ++;
                        if (response.isSuccessful()) {
                            Log.e("test","master for : " + masterFor + " -- " + "remote : " + remoteTableName);
                            Log.e("test","response " + remoteTableName +" : " + response.body().toString());
                            try {
                                JSONArray jsonArray = new JSONArray(response.body().toString());
                                masterSyncItemModels.get(position).setPB_visibility(false);
                                masterSyncItemModels.get(position).setCount(jsonArray.length());

                                masterSyncAdapter.notifyDataSetChanged();
                                String dateAndTime = DateTimeFormat.getCurrentDateTime(DateTimeFormat.FORMAT_1);
                                binding.lastSyncTime.setText(dateAndTime);
                                SharedPref.saveMasterLastSync(getApplicationContext(),dateAndTime );
                                sqLite.saveMasterSyncData(masterSyncItemModels.get(position).getLocalTableKeyName(),jsonArray.toString());

                                if (origin.equalsIgnoreCase("Login")){
                                    if (apiSuccessCount == itemCount){
                                        startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        masterSyncAdapter.notifyDataSetChanged();
                        Log.e("test","success count : " + apiSuccessCount);

                    }

                    @Override
                    public void onFailure (@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                        Log.e("test","failed : " + t);
                        apiSuccessCount++;
                        Log.e("test","success count at error : " + apiSuccessCount);
                        masterSyncItemModels.get(position).setPB_visibility(false);
                        masterSyncAdapter.notifyDataSetChanged();
                        if (apiSuccessCount == itemCount){
                            startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
                        }

                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}