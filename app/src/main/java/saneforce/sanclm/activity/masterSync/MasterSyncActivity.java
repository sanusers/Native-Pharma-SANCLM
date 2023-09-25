package saneforce.sanclm.activity.masterSync;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
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
    String sfCode = "", division_code = "", rsf = "", sf_type = "", designation = "", state_code = "", subdivision_code = "";
    int doctorCount = 0, specialityCount = 0, qualificationCount = 0, categoryCount = 0, departmentCount = 0, classCount = 0, feedbackCount = 0;
    int unlistedDrCount = 0, chemistCount = 0, stockiestCount = 0, hospitalCount = 0, cipCount = 0, inputCount = 0, leaveCount = 0, leaveStatusCount = 0, tpCount = 0;
    int productCount = 0, proCatCount = 0, brandCount = 0, compProCount = 0;
    int proSlideCount = 0, proSpeSlideCount = 0, brandSlideCount = 0, therapticCount = 0;
    int subordinateCount = 0, subMgrCount = 0, jWorkCount = 0;
    int setupCount = 0, customSetupCount = 0;
    int apiSuccessCount = 0, itemCount = 0;
    String navigateFrom = "";
    boolean mgrInitialSync = false;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMasterSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navigateFrom = getIntent().getExtras().getString("Origin");
        }

        Cursor cursor = sqLite.getLoginData();
        loginResponse = new LoginResponse();
        String loginData = "";
        if (cursor.moveToNext()) {
            loginData = cursor.getString(0);
        }
        cursor.close();
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        loginResponse = new Gson().fromJson(loginData, type);
//        Log.e("test", "login data from sqlite : " + new Gson().toJson(loginResponse));

        sfCode = loginResponse.getSF_Code();
        sf_type = loginResponse.getSf_type();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        state_code = loginResponse.getState_Code();
        binding.hqName.setText(SharedPref.getHqName(MasterSyncActivity.this));
        rsf = SharedPref.getHqCode(MasterSyncActivity.this);

        try {
            //Initializing all the data array
            uiInitialization();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        binding.listedDoctor.setSelected(true);
        if (navigateFrom.equalsIgnoreCase("Login")) {
            if (designation.equalsIgnoreCase("MGR")) {
                mgrInitialSync = true;
                sync("Subordinate", "getsubordinate", subordinateModelArray, 0);
            } else {
                masterSyncAll(false);
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
            }
        });

        binding.hq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                    ArrayList<String> list = new ArrayList<>();

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            list.add(jsonObject.getString("name"));
                        }
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MasterSyncActivity.this);
                    LayoutInflater inflater = MasterSyncActivity.this.getLayoutInflater();
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
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            String selectedHq = listView.getItemAtPosition(position).toString();
                            binding.hqName.setText(selectedHq);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("name").equalsIgnoreCase(selectedHq)) {
                                        rsf = jsonObject.getString("id");
                                        SharedPref.saveHq(MasterSyncActivity.this, selectedHq, rsf);
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
            public void onClick(View view) {
                listItemClicked(binding.listDrArrow, binding.listedDoctor);
                binding.childSync.setText("Sync Listed Doctor");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(doctorModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.chemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.chemistArrow, binding.chemist);
                binding.childSync.setText("Sync Chemist");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(chemistModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.stockiest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.stockArrow, binding.stockiest);
                binding.childSync.setText("Sync Stockiest");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(stockiestModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.unlistedDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.unListDrArrow, binding.unlistedDoctor);
                binding.childSync.setText("Sync Unlisted Doctor");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(unlistedDrModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.hospitalArrow, binding.hospital);
                binding.childSync.setText("Sync Hospital");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(hospitalModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.cip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.cipArrow, binding.cip);
                binding.childSync.setText("Sync CIP");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(cipModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.inputArrow, binding.input);
                binding.childSync.setText("Sync Input");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(inputModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.productArrow, binding.product);
                binding.childSync.setText("Sync Product");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(productModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.leaveArrow, binding.leave);
                binding.childSync.setText("Sync Leave");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(leaveModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.tourPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.tpArrow, binding.tourPlan);
                binding.childSync.setText("Sync Tour Plan");


                arrayForAdapter.clear();
                arrayForAdapter.addAll(tpModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.slideArrow, binding.slide);
                binding.childSync.setText("Sync Slide");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(slideModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.subordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.subordinateArrow, binding.subordinate);
                binding.childSync.setText("Sync Subordinate");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(subordinateModelArray);
                populateAdapter(arrayForAdapter);

            }
        });

        binding.setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClicked(binding.setupArrow, binding.setup);
                binding.childSync.setText("Sync Setup");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(setupModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.childSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MasterSyncItemModel> arrayList = new ArrayList<>();

                if (binding.listedDoctor.isSelected()) {
                    arrayList.addAll(doctorModelArray);
                } else if (binding.chemist.isSelected()) {
                    arrayList.addAll(chemistModelArray);
                } else if (binding.stockiest.isSelected()) {
                    arrayList.addAll(stockiestModelArray);
                } else if (binding.unlistedDoctor.isSelected()) {
                    arrayList.addAll(unlistedDrModelArray);
                } else if (binding.hospital.isSelected()) {
                    arrayList.addAll(hospitalModelArray);
                } else if (binding.cip.isSelected()) {
                    arrayList.addAll(cipModelArray);
                } else if (binding.input.isSelected()) {
                    arrayList.addAll(inputModelArray);
                } else if (binding.product.isSelected()) {
                    arrayList.addAll(productModelArray);
                } else if (binding.leave.isSelected()) {
                    arrayList.addAll(leaveModelArray);
                } else if (binding.tourPlan.isSelected()) {
                    arrayList.addAll(tpModelArray);
                } else if (binding.slide.isSelected()) {
                    arrayList.addAll(slideModelArray);
                } else if (binding.subordinate.isSelected()) {
                    arrayList.addAll(subordinateModelArray);
                } else if (binding.setup.isSelected()) {
                    arrayList.addAll(setupModelArray);
                }

                for (int i = 0; i < arrayList.size(); i++) {
                    arrayForAdapter.get(i).setPB_visibility(true);
                    masterSyncAdapter.notifyDataSetChanged();
                    sync(arrayList.get(i).getMasterFor(), arrayList.get(i).getRemoteTableName(), arrayList, i);
                }
            }
        });

        binding.masterSyncAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                masterSyncAll(false);
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
        doctorCount = sqLite.getMasterSyncDataByKey(Constants.DOCTOR).length();
        specialityCount = sqLite.getMasterSyncDataByKey(Constants.SPECIALITY).length();
        qualificationCount = sqLite.getMasterSyncDataByKey(Constants.QUALIFICATION).length();
        categoryCount = sqLite.getMasterSyncDataByKey(Constants.CATEGORY).length();
        departmentCount = sqLite.getMasterSyncDataByKey(Constants.DEPARTMENT).length();
        classCount = sqLite.getMasterSyncDataByKey(Constants.CLASS).length();
        feedbackCount = sqLite.getMasterSyncDataByKey(Constants.FEEDBACK).length();

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
        MasterSyncItemModel doctor = new MasterSyncItemModel(loginResponse.getDrCap(), doctorCount, "Doctor", "getdoctors", Constants.DOCTOR, false);
        MasterSyncItemModel spl = new MasterSyncItemModel("Speciality", specialityCount, "Doctor", "getspeciality", Constants.SPECIALITY, false);
        MasterSyncItemModel ql = new MasterSyncItemModel("Qualification", qualificationCount, "Doctor", "getquali", Constants.QUALIFICATION, false);
        MasterSyncItemModel cat = new MasterSyncItemModel("Category", categoryCount, "Doctor", "getcategorys", Constants.CATEGORY, false);
        MasterSyncItemModel dep = new MasterSyncItemModel("Department", departmentCount, "Doctor", "getdeparts", Constants.DEPARTMENT, false);
        MasterSyncItemModel clas = new MasterSyncItemModel("Class", classCount, "Doctor", "getclass", Constants.CLASS, false);
        MasterSyncItemModel feedback = new MasterSyncItemModel("Feedback", feedbackCount, "Doctor", "getdrfeedback", Constants.FEEDBACK, false);

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

        //Chemist
        chemistModelArray.clear();
        MasterSyncItemModel cheModel = new MasterSyncItemModel("Chemist", chemistCount, "Doctor", "getchemist", Constants.CHEMIST, false);
        chemistModelArray.add(cheModel);

        //Stockiest
        stockiestModelArray.clear();
        MasterSyncItemModel stockModel = new MasterSyncItemModel("Stockiest", stockiestCount, "Doctor", "getstockist", Constants.STOCKIEST, false);
        stockiestModelArray.add(stockModel);

        //Unlisted Dr
        unlistedDrModelArray.clear();
        MasterSyncItemModel unListModel = new MasterSyncItemModel("Unlisted Doctor", unlistedDrCount, "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR, false);
        unlistedDrModelArray.add(unListModel);

        //Hospital
        hospitalModelArray.clear();
        MasterSyncItemModel hospModel = new MasterSyncItemModel("Hospital", hospitalCount, "Doctor", "gethospital", Constants.HOSPITAL, false);
        hospitalModelArray.add(hospModel);

        //CIP
        cipModelArray.clear();
        MasterSyncItemModel ciModel = new MasterSyncItemModel("CIP", cipCount, "Doctor", "getcip", Constants.CIP, false);
        cipModelArray.add(ciModel);

        //Input
        inputModelArray.clear();
        MasterSyncItemModel inpModel = new MasterSyncItemModel("Input", inputCount, "Product", "getinputs", Constants.INPUT, false);
        inputModelArray.add(inpModel);

        //Product
        productModelArray.clear();
        MasterSyncItemModel proModel = new MasterSyncItemModel("Product", productCount, "Product", "getproducts", Constants.PRODUCT, false);
        MasterSyncItemModel proCatModel = new MasterSyncItemModel("Product Category", proCatCount, "Product", "", "", false);
        MasterSyncItemModel brandModel = new MasterSyncItemModel("Brand", brandCount, "Product", "getbrands", Constants.BRAND, false);
        MasterSyncItemModel compProductModel = new MasterSyncItemModel("Competitor Product", compProCount, "Product", "getcompdet", Constants.COMPETITOR_PROD, false);
        productModelArray.add(proModel);
        productModelArray.add(proCatModel);
        productModelArray.add(brandModel);
        productModelArray.add(compProductModel);

        //Leave
        leaveModelArray.clear();
        MasterSyncItemModel leaveModel = new MasterSyncItemModel("Leave Type", leaveCount, "Leave", "getleavetype", Constants.LEAVE, false);
        MasterSyncItemModel leaveStatusModel = new MasterSyncItemModel("Leave Status", leaveStatusCount, "Leave", "getleavestatus", Constants.LEAVE, false);
        leaveModelArray.add(leaveModel);
        leaveModelArray.add(leaveStatusModel);

        //Tour Plan
        tpModelArray.clear();
        MasterSyncItemModel masterSyncItemModel = new MasterSyncItemModel("Tour Plan", tpCount, "TP", "gettpstatus", Constants.TP_PLAN, false);
        tpModelArray.add(masterSyncItemModel);

        //Slide
        slideModelArray.clear();
        MasterSyncItemModel proSlideModel = new MasterSyncItemModel("Product Slide", proSlideCount, "Slide", "getprodslides", Constants.PROD_SLIDE, false);
        MasterSyncItemModel splSlideModel = new MasterSyncItemModel("Speciality Slide ", proSpeSlideCount, "Slide", "getslidespeciality", Constants.SPL_SLIDE, false);
        MasterSyncItemModel brandSlideModel = new MasterSyncItemModel("Brand Slide", brandSlideCount, "Slide", "getslidebrand", Constants.BRAND_SLIDE, false);
        MasterSyncItemModel therapticSlideModel = new MasterSyncItemModel("Theraptic Slide", therapticCount, "Slide", "gettheraptic", Constants.THERAPTIC_SLIDE, false);
        slideModelArray.add(proSlideModel);
        slideModelArray.add(splSlideModel);
        slideModelArray.add(brandSlideModel);
        slideModelArray.add(therapticSlideModel);

        //Subordinate
        subordinateModelArray.clear();
        MasterSyncItemModel subModel = new MasterSyncItemModel("Subordinate", subordinateCount, "Subordinate", "getsubordinate", Constants.SUBORDINATE, false);
        MasterSyncItemModel subMgrModel = new MasterSyncItemModel("Subordinate MGR", subMgrCount, "Subordinate", "getsubordinatemgr", Constants.SUBORDINATE_MGR, false);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Joint Work", jWorkCount, "Subordinate", "getjointwork", Constants.JOINT_WORK, false);

        subordinateModelArray.add(subModel);
        subordinateModelArray.add(subMgrModel);
        subordinateModelArray.add(jWorkModel);

        //Setup
        setupModelArray.clear();
        MasterSyncItemModel setupModel = new MasterSyncItemModel("Setup", setupCount, "Setup", "getsetups", Constants.SETUP, false);
        MasterSyncItemModel customSetupModel = new MasterSyncItemModel("Custom Setup", customSetupCount, "Setup", "getcustomsetup", Constants.CUSTOM_SETUP, false);
        setupModelArray.add(setupModel);
        setupModelArray.add(customSetupModel);

    }

    public void listItemClicked(ImageView imageView, LinearLayout view) {
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

    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {

        masterSyncAdapter = new MasterSyncAdapter(masterSyncItemModels, getApplicationContext(), new MasterSyncItemClick() {
            @Override
            public void itemClick(MasterSyncItemModel masterSyncItemModel1, int position) {
                if (binding.listedDoctor.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), doctorModelArray, position);
                } else if (binding.chemist.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), chemistModelArray, position);
                } else if (binding.stockiest.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), stockiestModelArray, position);
                } else if (binding.unlistedDoctor.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), unlistedDrModelArray, position);
                } else if (binding.hospital.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), hospitalModelArray, position);
                } else if (binding.cip.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), cipModelArray, position);
                } else if (binding.input.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), inputModelArray, position);
                } else if (binding.product.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), productModelArray, position);
                } else if (binding.leave.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), leaveModelArray, position);
                } else if (binding.tourPlan.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), tpModelArray, position);
                } else if (binding.slide.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), slideModelArray, position);
                } else if (binding.subordinate.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), subordinateModelArray, position);
                } else if (binding.setup.isSelected()) {
                    sync(masterSyncItemModel1.getMasterFor(), masterSyncItemModel1.getRemoteTableName(), subordinateModelArray, position);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(masterSyncAdapter);
        masterSyncAdapter.notifyDataSetChanged();
    }

    public void masterSyncAll(boolean hqChanged) {
        masterSyncAllModel.clear();
        itemCount = 0;
        apiSuccessCount = 0;

        if (hqChanged) {
            MasterSyncItemModel doctor = new MasterSyncItemModel(loginResponse.getDrCap(), doctorCount, "Doctor", "getdoctors", Constants.DOCTOR, false);
            ArrayList<MasterSyncItemModel> doctorModelArray = new ArrayList<>();
            doctorModelArray.add(doctor);
            masterSyncAllModel.add(doctorModelArray);
        } else {
            masterSyncAllModel.add(doctorModelArray);
        }

        masterSyncAllModel.add(chemistModelArray);
        masterSyncAllModel.add(stockiestModelArray);
        masterSyncAllModel.add(unlistedDrModelArray);
        masterSyncAllModel.add(hospitalModelArray);
        masterSyncAllModel.add(cipModelArray);
        masterSyncAllModel.add(inputModelArray);
        if (!hqChanged) {
            masterSyncAllModel.add(inputModelArray);
            masterSyncAllModel.add(productModelArray);
            masterSyncAllModel.add(leaveModelArray);
            masterSyncAllModel.add(tpModelArray);
            masterSyncAllModel.add(slideModelArray);
            masterSyncAllModel.add(subordinateModelArray);
            masterSyncAllModel.add(setupModelArray);
        }

        for (int i = 0; i < masterSyncAllModel.size(); i++) {
            ArrayList<MasterSyncItemModel> childArray = new ArrayList<>(masterSyncAllModel.get(i));
            itemCount += childArray.size();
            for (int j = 0; j < childArray.size(); j++) {
                childArray.get(j).setPB_visibility(true);
                masterSyncAdapter.notifyDataSetChanged();
                sync(childArray.get(j).getMasterFor(), childArray.get(j).getRemoteTableName(), childArray, j);
            }
        }
        Log.e("test", "count : " + itemCount);

    }

    public void sync(String masterFor, String remoteTableName, ArrayList<MasterSyncItemModel> masterSyncItemModels, int position) {

        if (UtilityClass.isNetworkAvailable(MasterSyncActivity.this)) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
                String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);

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
                if (masterFor.equalsIgnoreCase("Doctor")) {
                    call = apiInterface.getDrMaster(jsonObject.toString());
                } else if (masterFor.equalsIgnoreCase("Subordinate")) {
                    call = apiInterface.getSubordinateMaster(jsonObject.toString());
                } else if (masterFor.equalsIgnoreCase("Product")) {
                    call = apiInterface.getProductMaster(jsonObject.toString());
                } else if (masterFor.equalsIgnoreCase("Leave")) {
                    call = apiInterface.getLeaveMaster(jsonObject.toString());
                } else if (masterFor.equalsIgnoreCase("Slide")) {
                    call = apiInterface.getSlideMaster(jsonObject.toString());
                } else if (masterFor.equalsIgnoreCase("Setup")) {
                    call = apiInterface.getSetupMaster(jsonObject.toString());
                } else if (masterFor.equalsIgnoreCase("TP")) {
                    call = apiInterface.getAdditionalMaster(jsonObject.toString());
                }

                if (call != null) {
                    call.enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                            masterSyncItemModels.get(position).setPB_visibility(false);
                            apiSuccessCount++;
                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + masterFor + " -- " + remoteTableName + " : " + response.body().toString());

                                if (masterFor.equalsIgnoreCase("Setup")) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response.body().toString());
                                        JSONObject jsonSetup = jsonArray.getJSONObject(0);

                                        SharedPref.setGeoNeed(getApplicationContext(), jsonSetup.getString("GeoNeed"));
                                        SharedPref.setGeoCheck(getApplicationContext(), jsonSetup.getString("GeoChk"));
                                        SharedPref.setChemistNeed(getApplicationContext(), jsonSetup.getString("ChmNeed"));
                                        SharedPref.setStockistNeed(getApplicationContext(), jsonSetup.getString("StkNeed"));
                                        SharedPref.setUndrNeed(getApplicationContext(), jsonSetup.getString("UNLNeed"));
                                        SharedPref.setDrPrdNeed(getApplicationContext(), jsonSetup.getString("DPNeed"));
                                        SharedPref.setDrInpNeed(getApplicationContext(), jsonSetup.getString("DINeed"));
                                        SharedPref.setChePrdNeed(getApplicationContext(), jsonSetup.getString("CPNeed"));
                                        SharedPref.setCheInpNeed(getApplicationContext(), jsonSetup.getString("CINeed"));
                                        SharedPref.setStkPrdNeed(getApplicationContext(), jsonSetup.getString("SPNeed"));
                                        SharedPref.setStkInpNeed(getApplicationContext(), jsonSetup.getString("SINeed"));
                                        SharedPref.setUnDrPrdNeed(getApplicationContext(), jsonSetup.getString("NPNeed"));
                                        SharedPref.setUnDrInpNeed(getApplicationContext(), jsonSetup.getString("NINeed"));
                                        SharedPref.setCaptionDr(getApplicationContext(), jsonSetup.getString("DrCap"));
                                        SharedPref.setCaptionChemist(getApplicationContext(), jsonSetup.getString("ChmCap"));
                                        SharedPref.setCaptionStockist(getApplicationContext(), jsonSetup.getString("StkCap"));
                                        SharedPref.setCaptionUnDr(getApplicationContext(), jsonSetup.getString("NLCap"));
                                        SharedPref.setCaptionDrPrd(getApplicationContext(), jsonSetup.getString("Doc_Product_caption"));
                                        SharedPref.setCaptionChemistPrd(getApplicationContext(), jsonSetup.getString("Chm_Product_caption"));
                                        SharedPref.setCaptionStockistPrd(getApplicationContext(), jsonSetup.getString("Stk_Product_caption"));
                                        SharedPref.setCaptionUnDrPrd(getApplicationContext(), jsonSetup.getString("Ul_Product_caption"));
                                        SharedPref.setCaptionDrInp(getApplicationContext(), jsonSetup.getString("Doc_Input_caption"));
                                        SharedPref.setCaptionChemistInp(getApplicationContext(), jsonSetup.getString("Chm_Input_caption"));
                                        SharedPref.setCaptionStockistInp(getApplicationContext(), jsonSetup.getString("Stk_Input_caption"));
                                        SharedPref.setCaptionUnDrInp(getApplicationContext(), jsonSetup.getString("Ul_Input_caption"));
                                        SharedPref.setDrJwNeed(getApplicationContext(), jsonSetup.getString("Doc_jointwork_Need"));
                                        SharedPref.setChemistJwNeed(getApplicationContext(), jsonSetup.getString("Chm_jointwork_Need"));
                                        SharedPref.setStockistJwNeed(getApplicationContext(), jsonSetup.getString("Stk_jointwork_Need"));
                                        SharedPref.setUndJwNeed(getApplicationContext(), jsonSetup.getString("Ul_jointwork_Need"));
                                        SharedPref.setCaptionCheSamQty(getApplicationContext(), jsonSetup.getString("ChmSmpCap"));
                                        SharedPref.setChemistRcpaMandatory(getApplicationContext(), jsonSetup.getString("ChmRcpaMd"));
                                        SharedPref.setCaptionDrRxQty(getApplicationContext(), jsonSetup.getString("DrRxQCap"));
                                        SharedPref.setCaptionDrSamQty(getApplicationContext(), jsonSetup.getString("DrSmpQCap"));
                                        SharedPref.setCaptionCheRxQty(getApplicationContext(), jsonSetup.getString("ChmQCap"));
                                        SharedPref.setCaptionStkRxQty(getApplicationContext(), jsonSetup.getString("StkQCap"));
                                        SharedPref.setMgrDrRcpaMandatory(getApplicationContext(), jsonSetup.getString("RcpaMd_Mgr"));
                                        SharedPref.setCaptionCluster(getApplicationContext(), jsonSetup.getString("Cluster_Cap"));
                                        SharedPref.setMgrCheRcpaMandatory(getApplicationContext(), jsonSetup.getString("ChmRcpaMd_Mgr"));
                                        SharedPref.setDrEventCaptureMandatory(getApplicationContext(), jsonSetup.getString("DrEvent_Md"));
                                        SharedPref.setCheEventCaptureMandatory(getApplicationContext(), jsonSetup.getString("ChmEvent_Md"));
                                        SharedPref.setStkEventCaptureMandatory(getApplicationContext(), jsonSetup.getString("StkEvent_Md"));
                                        SharedPref.setUndrEventCaptureMandatory(getApplicationContext(), jsonSetup.getString("UlDrEvent_Md"));
                                        SharedPref.setCaptionUnDrRxQty(getApplicationContext(), jsonSetup.getString("NLRxQCap"));
                                        SharedPref.setCaptionUnDrSamQty(getApplicationContext(), jsonSetup.getString("NLSmpQCap"));
                                        SharedPref.setDrRxNeed(getApplicationContext(), jsonSetup.getString("DrRxNd"));
                                        SharedPref.setDrSamNeed(getApplicationContext(), jsonSetup.getString("DrSampNd"));
                                        SharedPref.setDrRxQtyMandatory(getApplicationContext(), jsonSetup.getString("DrRxQMd"));
                                        SharedPref.setDrSamQtyMandatory(getApplicationContext(), jsonSetup.getString("DrSmpQMd"));
                                        SharedPref.setCheckInOutNeed(getApplicationContext(), jsonSetup.getString("SrtNd"));
                                        SharedPref.setDrEventCapture(getApplicationContext(), jsonSetup.getString("deneed"));
                                        SharedPref.setChemistEventCapture(getApplicationContext(), jsonSetup.getString("ceneed"));
                                        SharedPref.setStockistEventCapture(getApplicationContext(), jsonSetup.getString("seneed"));
                                        SharedPref.setUndrEventCapture(getApplicationContext(), jsonSetup.getString("neneed"));
                                        SharedPref.setDrPrdMandatory(getApplicationContext(), jsonSetup.getString("DrPrdMd"));
                                        SharedPref.setDrInpMandatory(getApplicationContext(), jsonSetup.getString("DrInpMd"));
                                        SharedPref.setDrRcpaNeed(getApplicationContext(), jsonSetup.getString("RcpaNd"));
                                        SharedPref.setChemistRcpaNeed(getApplicationContext(), jsonSetup.getString("Chm_RCPA_Need"));
                                        SharedPref.setDrGeoTagNeed(getApplicationContext(), jsonSetup.getString("GEOTagNeed"));
                                        SharedPref.setChemistGeoTagNeed(getApplicationContext(), jsonSetup.getString("GEOTagNeedche"));
                                        SharedPref.setStockistGeoTagNeed(getApplicationContext(), jsonSetup.getString("GEOTagNeedstock"));
                                        SharedPref.setCipGeoTagNeed(getApplicationContext(), jsonSetup.getString("GeoTagNeedcip"));
                                        SharedPref.setUndrGeoTagNeed(getApplicationContext(), jsonSetup.getString("GEOTagNeedunlst"));
                                        SharedPref.setGeofencingCircleRadius(getApplicationContext(), jsonSetup.getString("DisRad"));
                                        SharedPref.setGeotagImage(getApplicationContext(), jsonSetup.getString("geoTagImg"));
                                        SharedPref.setDeviceRegId(getApplicationContext(), jsonSetup.getString("DeviceRegId"));
                                        SharedPref.setProfileMclDetailing(getApplicationContext(), jsonSetup.getString("MCLDet"));
                                        SharedPref.setPresentationNActivityNeed(getApplicationContext(), jsonSetup.getString("NActivityNeed"));
                                        SharedPref.setPresentationSpecFilter(getApplicationContext(), jsonSetup.getString("SpecFilter"));
                                        SharedPref.setChemistRxNeed(getApplicationContext(), jsonSetup.getString("ChmRxNd"));
                                        SharedPref.setAddDr(getApplicationContext(), jsonSetup.getString("addDr"));
                                        SharedPref.setShowDeleteOption(getApplicationContext(), jsonSetup.getString("showDelete"));
                                        SharedPref.setDetailingChemSkip(getApplicationContext(), jsonSetup.getString("Detailing_chem"));
                                        SharedPref.setDetailingStockistSkip(getApplicationContext(), jsonSetup.getString("Detailing_stk"));
                                        SharedPref.setDetailingUndrSkip(getApplicationContext(), jsonSetup.getString("Detailing_undr"));
                                        SharedPref.setAddChemist(getApplicationContext(), jsonSetup.getString("addChm"));
                                        SharedPref.setStockistPobNeed(getApplicationContext(), jsonSetup.getString("Stk_Pob_Need"));
                                        SharedPref.setUndrPobNeed(getApplicationContext(), jsonSetup.getString("Ul_Pob_Need"));
                                        SharedPref.setTpNeed(getApplicationContext(), jsonSetup.getString("tp_need"));
                                        SharedPref.setSurveyNeed(getApplicationContext(), jsonSetup.getString("SurveyNd"));
                                        SharedPref.setPastLeavePost(getApplicationContext(), jsonSetup.getString("past_leave_post"));
                                        SharedPref.setRcpaMandatory(getApplicationContext(), jsonSetup.getString("RcpaMd"));
                                        SharedPref.setQuizNeed(getApplicationContext(), jsonSetup.getString("quiz_need"));
                                        SharedPref.setQuizNeedMandatory(getApplicationContext(), jsonSetup.getString("quiz_need_mandt"));
                                        SharedPref.setMissedDateMandatory(getApplicationContext(), jsonSetup.getString("MissedDateMand"));
                                        SharedPref.setTpDcrDeviation(getApplicationContext(), jsonSetup.getString("TPDCR_Deviation"));
                                        SharedPref.setTpBasedDcr(getApplicationContext(), jsonSetup.getString("TPbasedDCR"));
                                        SharedPref.setTpMandatoryNeed(getApplicationContext(), jsonSetup.getString("TP_Mandatory_Need"));
                                        SharedPref.setTpStartDate(getApplicationContext(), jsonSetup.getString("Tp_Start_Date"));
                                        SharedPref.setTpEndDate(getApplicationContext(), jsonSetup.getString("Tp_End_Date"));
                                        SharedPref.setChemistSamQtyNeed(getApplicationContext(), jsonSetup.getString("chmsamQty_need"));
                                        SharedPref.setDrJwMandatory(getApplicationContext(), jsonSetup.getString("Doc_jointwork_Mandatory_Need"));
                                        SharedPref.setChemistJwMandatory(getApplicationContext(), jsonSetup.getString("Chm_jointwork_Mandatory_Need"));
                                        SharedPref.setStockistJwMandatory(getApplicationContext(), jsonSetup.getString("stk_jointwork_Mandatory_Need"));
                                        SharedPref.setUndrJwMandatory(getApplicationContext(), jsonSetup.getString("Ul_jointwork_Mandatory_Need"));
                                        SharedPref.setDetailingCipSkip(getApplicationContext(), jsonSetup.getString("CIP_ENeed"));
                                        SharedPref.setDrFeedbackMandatory(getApplicationContext(), jsonSetup.getString("DrFeedMd"));
                                        SharedPref.setDrFeedbackNeed(getApplicationContext(), jsonSetup.getString("DFNeed"));
                                        SharedPref.setChemistFeedbackNeed(getApplicationContext(), jsonSetup.getString("CFNeed"));
                                        SharedPref.setStockistFeedbackNeed(getApplicationContext(), jsonSetup.getString("SFNeed"));
                                        SharedPref.setUndrFeedbackNeed(getApplicationContext(), jsonSetup.getString("NFNeed"));
                                        SharedPref.setCipFeedbackNeed(getApplicationContext(), jsonSetup.getString("CIP_FNeed"));
                                        SharedPref.setDrPobNeed(getApplicationContext(), jsonSetup.getString("Doc_Pob_Need"));
                                        SharedPref.setDrPobMandatory(getApplicationContext(), jsonSetup.getString("Doc_Pob_Mandatory_Need"));
                                        SharedPref.setChemistPobNeed(getApplicationContext(), jsonSetup.getString("Chm_Pob_Need"));
                                        SharedPref.setChemistPobMandatory(getApplicationContext(), jsonSetup.getString("Chm_Pob_Mandatory_Need"));
                                        SharedPref.setStockistPobMandatory(getApplicationContext(), jsonSetup.getString("Stk_Pob_Mandatory_Need"));
                                        SharedPref.setUndrPobMandatory(getApplicationContext(), jsonSetup.getString("Ul_Pob_Mandatory_Need"));
                                        SharedPref.setCipPobNeed(getApplicationContext(), jsonSetup.getString("CIPPOBNd"));
                                        SharedPref.setCipPobMandatory(getApplicationContext(), jsonSetup.getString("CIPPOBMd"));
                                        SharedPref.setDrRemarksNeed(getApplicationContext(), jsonSetup.getString("TempNd"));
                                        SharedPref.setSalesReportNeed(getApplicationContext(), jsonSetup.getString("Target_report_Nd"));
                                        SharedPref.setVisitControl(getApplicationContext(), jsonSetup.getString("vstnd"));
                                        SharedPref.setMultiCluster(getApplicationContext(), jsonSetup.getString("multi_cluster"));
                                        SharedPref.setSampleValidation(getApplicationContext(), jsonSetup.getString("sample_validation"));
                                        SharedPref.setInputValidation(getApplicationContext(), jsonSetup.getString("input_validation"));
                                        SharedPref.setLeaveEntitlementNeed(getApplicationContext(), jsonSetup.getString("Leave_entitlement_need"));
                                        SharedPref.setReminderCallNeed(getApplicationContext(), jsonSetup.getString("RmdrNeed"));
                                        SharedPref.setReminderCallPrdMandatory(getApplicationContext(), jsonSetup.getString("Remainder_prd_Md"));

                                    } catch (Exception e) {

                                    }
                                }


                                try {
                                    JSONArray jsonArray = new JSONArray(response.body().toString());
                                    masterSyncItemModels.get(position).setPB_visibility(false);
                                    masterSyncItemModels.get(position).setCount(jsonArray.length());

                                    masterSyncAdapter.notifyDataSetChanged();
                                    String dateAndTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_16);
                                    binding.lastSyncTime.setText(dateAndTime);
                                    SharedPref.saveMasterLastSync(getApplicationContext(), dateAndTime);
                                    sqLite.saveMasterSyncData(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString());

                                    if (masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getdoctors")) {
                                        sqLite.saveDrMaster(rsf, jsonArray.toString());
                                    }

                                    if (masterFor.equalsIgnoreCase("Subordinate") && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getsubordinate")) {
                                        if (mgrInitialSync) {
                                            setHq(jsonArray);
                                        }
                                    }

                                    if (apiSuccessCount >= itemCount) {
                                        if (navigateFrom.equalsIgnoreCase("Login")) {
                                           startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
                                        }
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            masterSyncAdapter.notifyDataSetChanged();
                            // Log.e("test","success count : " + apiSuccessCount);
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                            //  Log.e("test","failed : " + t);
                            apiSuccessCount++;
                            //  Log.e("test","success count at error : " + apiSuccessCount);
                            masterSyncItemModels.get(position).setPB_visibility(false);
                            masterSyncAdapter.notifyDataSetChanged();
                            if (apiSuccessCount >= itemCount) {
                                startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class));
                            }

                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }


    public void setHq(JSONArray jsonArray) {
        mgrInitialSync = false;
        apiSuccessCount = 0;
        if (jsonArray.length() > 0) {
            try {
                binding.hqName.setText(jsonArray.getJSONObject(0).getString("name"));
                rsf = jsonArray.getJSONObject(0).getString("id");
                SharedPref.saveHq(MasterSyncActivity.this, jsonArray.getJSONObject(0).getString("name"), rsf);
                masterSyncAll(false);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

}