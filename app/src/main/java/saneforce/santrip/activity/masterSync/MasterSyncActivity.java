package saneforce.santrip.activity.masterSync;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.slideDownloaderAlertBox.SlideDownloaderAlertBox;
import saneforce.santrip.activity.tourPlan.model.ModelClass;
import saneforce.santrip.activity.tourPlan.model.ReceiveModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.MyDayPlanEntriesNeeded;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityMasterSyncBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.CallDataRestClass;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.santrip.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataDao;
import saneforce.santrip.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataTable;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkStatusTask;
import saneforce.santrip.utility.TimeUtils;

public class    MasterSyncActivity extends AppCompatActivity {

   public static ActivityMasterSyncBinding binding;
    ApiInterface apiInterface;
    MasterSyncAdapter masterSyncAdapter = new MasterSyncAdapter();
    public static Dialog dialog1;
    String rsf="";
    int doctorCount = 0, specialityCount = 0, qualificationCount = 0, categoryCount = 0,chemistCategoryCount=0, departmentCount = 0, classCount = 0, feedbackCount = 0;
    int unlistedDrCount = 0, chemistCount = 0, stockiestCount = 0, hospitalCount = 0, cipCount = 0, inputCount = 0, leaveCount = 0, leaveStatusCount = 0, tpSetupCount = 0, clusterCount = 0;
    int callSyncCount = 0, visitControlCount = 0, dateSyncCount = 0;
    int productCount = 0, proCatCount = 0, brandCount = 0, compProCount = 0, mapComPrdCount = 0;
    int workTypeCount = 0, holidayCount = 0, weeklyOfCount = 0;
    int proSlideCount = 0, proSpeSlideCount = 0, brandSlideCount = 0, therapticCount = 0;
    int subordinateCount = 0, subMgrCount = 0, jWorkCount = 0, Quixcount = 0;
    int setupCount = 0, customSetupCount = 0;
    // Api call status
    int doctorStatus = 0, specialityStatus = 0, qualificationStatus = 0, categoryStatus = 0, departmentStatus = 0, classStatus = 0, feedbackStatus = 0;
    int unlistedDrStatus = 0, chemistStatus = 0, stockiestStatus = 0, hospitalStatus = 0, cipStatus = 0, inputStatus = 0, leaveStatus = 0, leaveStatusStatus = 0, tpSetupStatus = 0, tourPLanStatus = 0, clusterStatus = 0;
    int callSyncStatus = 0, myDayPlanStatus = 0, visitControlStatus = 0, dateSyncStatus = 0, stockBalanceStatus = 0,calenderEventStaus=0;
    int productStatus = 0, proCatStatus = 0, brandStatus = 0, compProStatus = 0, mapCompPrdStatus = 0;
    int workTypeStatus = 0, holidayStatus = 0, weeklyOfStatus = 0;
    int proSlideStatus = 0, proSpeSlideStatus = 0, brandSlideStatus = 0, therapticStatus = 0;
    int subordinateStatus = 0, subMgrStatus = 0, jWorkStatus = 0, QuizStatus = 0;
    int setupStatus = 0, customSetupStatus = 0;
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
    ArrayList<MasterSyncItemModel> clusterModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> leaveModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> dcrModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> workTypeModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> tpModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> slideModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> subordinateModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> otherModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> setupModelArray = new ArrayList<>();
    public static ArrayList<String> HQCODE_SYN = new ArrayList<>();
    SharedPreferences sharedpreferences;
    LocalDate localDate;
    ArrayList<String> weeklyOffDays = new ArrayList<>();
    JSONArray holidayJSONArray = new JSONArray();
    ModelClass.SessionList.WorkType weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType();
    ModelClass.SessionList.WorkType holidayWorkTypeModel = new ModelClass.SessionList.WorkType();
    String holidayMode = "", weeklyOffCaption = "";
    boolean isDataAvailable;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB db;

   static   Context context;
    private MasterDataDao masterDataDao;
    private TourPlanOfflineDataDao tourPlanOfflineDataDao;
    private TourPlanOnlineDataDao tourPlanOnlineDataDao;


    public static ModelClass.SessionList prepareSessionListForAdapter(ArrayList<ModelClass.SessionList.SubClass> clusterArray, ArrayList<ModelClass.SessionList.SubClass> jcArray, ArrayList<ModelClass.SessionList.SubClass> drArray, ArrayList<ModelClass.SessionList.SubClass> chemistArray, ArrayList<ModelClass.SessionList.SubClass> stockArray, ArrayList<ModelClass.SessionList.SubClass> unListedDrArray, ArrayList<ModelClass.SessionList.SubClass> cipArray, ArrayList<ModelClass.SessionList.SubClass> hospArray, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass hq, String remarks) {
        return new ModelClass.SessionList("", true, remarks, workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AAAAA","OnCreate");
        binding = ActivityMasterSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        context=getApplicationContext();
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        sharedpreferences = getSharedPreferences("SLIDES", MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navigateFrom = getIntent().getExtras().getString(Constants.NAVIGATE_FROM);
        }
        Animation blinkAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.downloading);
        binding.imgDownloading.startAnimation(blinkAnimation);
        db = RoomDB.getDatabase(this);
        masterDataDao=db.masterDataDao();
        tourPlanOfflineDataDao = db.tourPlanOfflineDataDao();
        tourPlanOnlineDataDao = db.tourPlanOnlineDataDao();

        //Initializing all the data array
        uiInitialization();
        arrayForAdapter.clear();
        arrayForAdapter.addAll(doctorModelArray);
        populateAdapter(arrayForAdapter);

        if (navigateFrom.equalsIgnoreCase("Login")) {
            binding.backArrow.setVisibility(View.GONE);
            if (SharedPref.getSfType(this).equalsIgnoreCase("2")) { //MGR
                mgrInitialSync = true;
                if (UtilityClass.isNetworkAvailable(MasterSyncActivity.this)) {
                   /// sync(Constants.SUBORDINATE, "getsubordinate", subordinateModelArray, 0);
                    sync(Constants.DOCTOR, "gettodaydcr", dcrModelArray, 2);
                    // to get all the HQ list initially only for MGR
// to get all the HQ list initially only for MGR
                } else {
                    commonUtilsMethods.showToastMessage(MasterSyncActivity.this, getString(R.string.no_network));
                }



            } else {
                masterSyncAll(false);
            }
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            binding.backArrow.setVisibility(View.VISIBLE);
            if(SharedPref.getSlideDowloadingStatus(this)){
                binding.imgDownloading.setVisibility(View.GONE);
            }else {
                binding.imgDownloading.setVisibility(View.GONE);
            }
        }
//        else {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//        }

        //binding.backArrow.setOnClickListener(view -> startActivity(new Intent(MasterSyncActivity.this, HomeDashBoard.class)));

        binding.backArrow.setOnClickListener(view -> {
            if (navigateFrom.equalsIgnoreCase("Login")) {
                Intent intent = new Intent(MasterSyncActivity.this, HomeDashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                getOnBackPressedDispatcher().onBackPressed();
            }

        });

        binding.imgDownloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SlideDownloaderAlertBox.dialog.show();
            }
        });


        binding.hq.setOnClickListener(view -> {

            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
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

                headerTxt.setText(getResources().getText(R.string.select_hq));
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
                listView.setOnItemClickListener((adapterView, view1, position, l) -> {
                    String selectedHq = listView.getItemAtPosition(position).toString();
                    binding.hqName.setText(selectedHq);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString("name").equalsIgnoreCase(selectedHq)) {
                                rsf = jsonObject.getString("id");
                                SharedPref.saveHq(MasterSyncActivity.this, selectedHq, rsf);
                                 //myresource
                                HQCODE_SYN.add(jsonObject.getString("id"));
                                SharedPref.setsyn_hqcode(this, String.valueOf(HQCODE_SYN));
                                prepareArray(rsf); // replace the new rsf value
                                masterSyncAll(true);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    dialog.dismiss();
                });

                alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());

                dialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UtilityClass.hideKeyboard(MasterSyncActivity.this);

        });

        binding.listedDr.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.listedDr);
                binding.childSync.setText("Sync Listed Doctor");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(doctorModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.chemist.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.chemist);
                binding.childSync.setText("Sync Chemist");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(chemistModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.stockiest.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.stockiest);
                binding.childSync.setText("Sync Stockiest");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(stockiestModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.unlistedDoctor.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.unlistedDoctor);
                binding.childSync.setText("Sync Unlisted Doctor");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(unlistedDrModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.hospital.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.hospital);
                binding.childSync.setText("Sync Hospital");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(hospitalModelArray);
                arrayForAdapter.addAll(hospitalModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.cip.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.cip);
                binding.childSync.setText("Sync CIP");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(cipModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.input.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.input);
                binding.childSync.setText("Sync Input");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(inputModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.product.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.product);
                binding.childSync.setText("Sync Product");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(productModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.cluster.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.cluster);
                binding.childSync.setText("Sync Cluster");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(clusterModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.leave.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.leave);
                binding.childSync.setText("Sync Leave");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(leaveModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.dcr.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.dcr);
                binding.childSync.setText("Sync DCR");
                arrayForAdapter.clear();
                arrayForAdapter.addAll(dcrModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.workType.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.workType);
                binding.childSync.setText("Sync Work Type");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(workTypeModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.tourPlan.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.tourPlan);
                binding.childSync.setText("Sync Tour Plan");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(tpModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.slide.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.slide);
                binding.childSync.setText("Sync Slide");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(slideModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.subordinate.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.subordinate);
                binding.childSync.setText("Sync Subordinate");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(subordinateModelArray);
                populateAdapter(arrayForAdapter);
            }

        });
        binding.Other.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.Other);
                binding.childSync.setText("Sync Other");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(otherModelArray);
                populateAdapter(arrayForAdapter);
            }

        });


        binding.setup.setOnClickListener(view -> {
            if (!view.isSelected()) {
                listItemClicked(binding.setup);
                binding.childSync.setText("Sync Setup");

                arrayForAdapter.clear();
                arrayForAdapter.addAll(setupModelArray);
                populateAdapter(arrayForAdapter);
            }
        });

        binding.childSync.setOnClickListener(view -> {
            ArrayList<MasterSyncItemModel> arrayList = new ArrayList<>();

            NetworkStatusTask networkStatusTask = new NetworkStatusTask(MasterSyncActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void isNetworkAvailable(Boolean status) {
                    if (status) {
                        if (binding.listedDr.isSelected()) {
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
                        } else if (binding.cluster.isSelected()) {
                            arrayList.addAll(clusterModelArray);
                        } else if (binding.leave.isSelected()) {
                            arrayList.addAll(leaveModelArray);
                        } else if (binding.dcr.isSelected()) {
                            arrayList.addAll(dcrModelArray);
                        } else if (binding.workType.isSelected()) {
                            arrayList.addAll(workTypeModelArray);
                        } else if (binding.tourPlan.isSelected()) {
                            arrayList.addAll(tpModelArray);
                        } else if (binding.slide.isSelected()) {
                            arrayList.addAll(slideModelArray);
                        } else if (binding.subordinate.isSelected()) {
                            arrayList.addAll(subordinateModelArray);
                        } else if (binding.Other.isSelected()) {
                            arrayList.addAll(otherModelArray);
                        } else if (binding.setup.isSelected()) {
                            arrayList.addAll(setupModelArray);
                        }

                        for (int i = 0; i < arrayList.size(); i++) {
                            arrayForAdapter.get(i).setPBarVisibility(true);
                            masterSyncAdapter.notifyDataSetChanged();
                            sync(arrayList.get(i).getMasterOf(), arrayList.get(i).getRemoteTableName(), arrayList, i);
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(MasterSyncActivity.this, getString(R.string.no_network));
                    }
                }
            });
            networkStatusTask.execute();

        });

        binding.masterSyncAll.setOnClickListener(view -> masterSyncAll(false));

    }

    public void uiInitialization() {

        binding.hqName.setText(SharedPref.getHqName(MasterSyncActivity.this));
            rsf = SharedPref.getHqCode(MasterSyncActivity.this);

       // Rsf is HQ code

        binding.hq.setEnabled(SharedPref.getSfType(this).equalsIgnoreCase("2"));
        binding.lastSyncTime.setText(SharedPref.getLastSync(getApplicationContext()));

        binding.listedDr.setText(SharedPref.getDrCap(this));
        binding.chemist.setText(SharedPref.getChmCap(this));
        binding.stockiest.setText(SharedPref.getStkCap(this));
        binding.unlistedDoctor.setText(SharedPref.getUNLcap(this));
        binding.hospital.setText(SharedPref.getHospCaption(this));
        binding.cip.setText(SharedPref.getCipCaption(this));

        doctorCount = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + rsf).getMasterSyncDataJsonArray().length();
        specialityCount = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray().length();
        qualificationCount = masterDataDao.getMasterDataTableOrNew(Constants.QUALIFICATION).getMasterSyncDataJsonArray().length();
        categoryCount = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray().length();
        chemistCategoryCount = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray().length();
        departmentCount = masterDataDao.getMasterDataTableOrNew(Constants.DEPARTMENT).getMasterSyncDataJsonArray().length();
        classCount = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray().length();
        feedbackCount = masterDataDao.getMasterDataTableOrNew(Constants.FEEDBACK).getMasterSyncDataJsonArray().length();
        unlistedDrCount = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + rsf).getMasterSyncDataJsonArray().length();
        chemistCount = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + rsf).getMasterSyncDataJsonArray().length();
        stockiestCount = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + rsf).getMasterSyncDataJsonArray().length();
        hospitalCount = masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + rsf).getMasterSyncDataJsonArray().length();
        cipCount = masterDataDao.getMasterDataTableOrNew(Constants.CIP + rsf).getMasterSyncDataJsonArray().length();
        inputCount = masterDataDao.getMasterDataTableOrNew(Constants.INPUT).getMasterSyncDataJsonArray().length();
        leaveCount = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray().length();
        leaveStatusCount = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray().length();
        callSyncCount = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray().length();
        visitControlCount = masterDataDao.getMasterDataTableOrNew(Constants.VISIT_CONTROL).getMasterSyncDataJsonArray().length();
        dateSyncCount = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray().length();

        workTypeCount = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray().length();
        holidayCount = masterDataDao.getMasterDataTableOrNew(Constants.HOLIDAY).getMasterSyncDataJsonArray().length();
        weeklyOfCount = masterDataDao.getMasterDataTableOrNew(Constants.WEEKLY_OFF).getMasterSyncDataJsonArray().length();
        tpSetupCount = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray().length();
        productCount = masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT).getMasterSyncDataJsonArray().length();
        proCatCount = masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT_CATEGORY).getMasterSyncDataJsonArray().length();
        brandCount = masterDataDao.getMasterDataTableOrNew(Constants.BRAND).getMasterSyncDataJsonArray().length();
        compProCount = masterDataDao.getMasterDataTableOrNew(Constants.COMPETITOR_PROD).getMasterSyncDataJsonArray().length();
        mapComPrdCount = masterDataDao.getMasterDataTableOrNew(Constants.MAPPED_COMPETITOR_PROD).getMasterSyncDataJsonArray().length();
        clusterCount = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + rsf).getMasterSyncDataJsonArray().length();
        proSlideCount = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray().length();
        proSpeSlideCount = masterDataDao.getMasterDataTableOrNew(Constants.SPL_SLIDE).getMasterSyncDataJsonArray().length();
        brandSlideCount = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray().length();
        therapticCount = masterDataDao.getMasterDataTableOrNew(Constants.THERAPTIC_SLIDE).getMasterSyncDataJsonArray().length();
        subordinateCount = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray().length();
        subMgrCount = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE_MGR).getMasterSyncDataJsonArray().length();
        jWorkCount = masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + rsf).getMasterSyncDataJsonArray().length();
        Quixcount = masterDataDao.getMasterDataTableOrNew(Constants.QUIZ).getMasterSyncDataJsonArray().length();
        setupCount = masterDataDao.getMasterDataTableOrNew(Constants.SETUP).getMasterSyncDataJsonArray().length();
        customSetupCount = masterDataDao.getMasterDataTableOrNew(Constants.CUSTOM_SETUP).getMasterSyncDataJsonArray().length();

        doctorStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DOCTOR + rsf);
        specialityStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SPECIALITY);
        qualificationStatus = masterDataDao.getMasterSyncStatusByKey(Constants.QUALIFICATION);
        categoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY);
        departmentStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DEPARTMENT);
        classStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CLASS);
        feedbackStatus = masterDataDao.getMasterSyncStatusByKey(Constants.FEEDBACK);
        unlistedDrStatus = masterDataDao.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR + rsf);
        chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST + rsf);
        stockiestStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCKIEST + rsf);
        hospitalStatus = masterDataDao.getMasterSyncStatusByKey(Constants.HOSPITAL + rsf);
        cipStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CIP + rsf);
        inputStatus = masterDataDao.getMasterSyncStatusByKey(Constants.INPUT);
        leaveStatus = masterDataDao.getMasterSyncStatusByKey(Constants.LEAVE);
        leaveStatusStatus = masterDataDao.getMasterSyncStatusByKey(Constants.LEAVE_STATUS);

        callSyncStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CALL_SYNC);
        myDayPlanStatus = masterDataDao.getMasterSyncStatusByKey(Constants.MY_DAY_PLAN);
        visitControlStatus = masterDataDao.getMasterSyncStatusByKey(Constants.VISIT_CONTROL);
        dateSyncStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DATE_SYNC);
        stockBalanceStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCK_BALANCE_MASTER);
        stockBalanceStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCK_BALANCE_MASTER);
        calenderEventStaus=masterDataDao.getMasterSyncStatusByKey(Constants.CALENDER_EVENT_STATUS);



        workTypeStatus = masterDataDao.getMasterSyncStatusByKey(Constants.WORK_TYPE);
        holidayStatus = masterDataDao.getMasterSyncStatusByKey(Constants.HOLIDAY);
        weeklyOfStatus = masterDataDao.getMasterSyncStatusByKey(Constants.WEEKLY_OFF);
        tpSetupStatus = masterDataDao.getMasterSyncStatusByKey(Constants.TP_SETUP);
        tourPLanStatus = masterDataDao.getMasterSyncStatusByKey(Constants.TOUR_PLAN);
        productStatus = masterDataDao.getMasterSyncStatusByKey(Constants.PRODUCT);
        proCatStatus = masterDataDao.getMasterSyncStatusByKey(Constants.PRODUCT_CATEGORY);
        brandStatus = masterDataDao.getMasterSyncStatusByKey(Constants.BRAND);
        compProStatus = masterDataDao.getMasterSyncStatusByKey(Constants.COMPETITOR_PROD);
        mapCompPrdStatus = masterDataDao.getMasterSyncStatusByKey(Constants.MAPPED_COMPETITOR_PROD);
        clusterStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CLUSTER + rsf);
        proSlideStatus = masterDataDao.getMasterSyncStatusByKey(Constants.PROD_SLIDE);
        proSpeSlideStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SPL_SLIDE);
        brandSlideStatus = masterDataDao.getMasterSyncStatusByKey(Constants.BRAND_SLIDE);
        therapticStatus = masterDataDao.getMasterSyncStatusByKey(Constants.THERAPTIC_SLIDE);
        subordinateStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SUBORDINATE);
        subMgrStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SUBORDINATE_MGR);
        jWorkStatus = masterDataDao.getMasterSyncStatusByKey(Constants.JOINT_WORK + rsf);
        QuizStatus = masterDataDao.getMasterSyncStatusByKey(Constants.QUIZ);
        setupStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SETUP);
        customSetupStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CUSTOM_SETUP);

        binding.listedDr.setSelected(true);
        prepareArray(rsf);

    }

    public void prepareArray(String hqCode) {
        doctorModelArray.clear();
        MasterSyncItemModel doctorModel = new MasterSyncItemModel(SharedPref.getDrCap(this), doctorCount, Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode, doctorStatus, false);
        MasterSyncItemModel spl = new MasterSyncItemModel(Constants.SPECIALITY, specialityCount, Constants.DOCTOR, "getspeciality", Constants.SPECIALITY, specialityStatus, false);
        MasterSyncItemModel ql = new MasterSyncItemModel(Constants.QUALIFICATION, qualificationCount, Constants.DOCTOR, "getquali", Constants.QUALIFICATION, qualificationStatus, false);
        MasterSyncItemModel cat = new MasterSyncItemModel(Constants.CATEGORY, categoryCount, Constants.DOCTOR, "getcategorys", Constants.CATEGORY, categoryStatus, false);
        MasterSyncItemModel dep = new MasterSyncItemModel(Constants.DEPARTMENT, departmentCount, Constants.DOCTOR, "getdeparts", Constants.DEPARTMENT, departmentStatus, false);
        MasterSyncItemModel clas = new MasterSyncItemModel(Constants.CLASS, classCount, Constants.DOCTOR, "getclass", Constants.CLASS, classStatus, false);
        MasterSyncItemModel feedback = new MasterSyncItemModel(Constants.FEEDBACK, feedbackCount, Constants.DOCTOR, "getdrfeedback", Constants.FEEDBACK, feedbackStatus, false);

        doctorModelArray.add(doctorModel);
        doctorModelArray.add(spl);
        doctorModelArray.add(ql);
        doctorModelArray.add(cat);
        doctorModelArray.add(dep);
        doctorModelArray.add(clas);
        doctorModelArray.add(feedback);

        //Chemist
        chemistModelArray.clear();
        if (SharedPref.getChmNeed(this).equalsIgnoreCase("0")) {
            MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(this), chemistCount, Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode, chemistStatus, false);
            MasterSyncItemModel chemistCategory = new MasterSyncItemModel(Constants.CATEGORY, chemistCategoryCount, Constants.DOCTOR, "getchem_categorys", Constants.CATEGORY_CHEMIST, categoryStatus, false);
            chemistModelArray.add(cheModel);
            chemistModelArray.add(chemistCategory);
        } else binding.chemist.setVisibility(View.GONE);

        //Stockiest
        stockiestModelArray.clear();
        if (SharedPref.getStkNeed(this).equalsIgnoreCase("0")) {
            MasterSyncItemModel stockModel = new MasterSyncItemModel(SharedPref.getStkCap(this), stockiestCount, Constants.DOCTOR, "getstockist", Constants.STOCKIEST + hqCode, stockiestStatus, false);
            stockiestModelArray.add(stockModel);
        } else binding.stockiest.setVisibility(View.GONE);

        //Unlisted Dr
        unlistedDrModelArray.clear();
        if (SharedPref.getUnlNeed(this).equalsIgnoreCase("0")) {
            MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(this), unlistedDrCount, Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, unlistedDrStatus, false);
            unlistedDrModelArray.add(unListModel);
        } else binding.unlistedDoctor.setVisibility(View.GONE);

        //Hospital
        hospitalModelArray.clear();
        if (SharedPref.getHospNeed(this).equalsIgnoreCase("0")) {
            MasterSyncItemModel hospModel = new MasterSyncItemModel(SharedPref.getHospCaption(this), hospitalCount, Constants.DOCTOR, "gethospital", Constants.HOSPITAL + hqCode, hospitalStatus, false);
            hospitalModelArray.add(hospModel);
        } else binding.hospital.setVisibility(View.GONE);

        //CIP
        cipModelArray.clear();
        if (SharedPref.getCipNeed(this).equalsIgnoreCase("0")) {
            MasterSyncItemModel ciModel = new MasterSyncItemModel(SharedPref.getCipCaption(this), cipCount, Constants.DOCTOR, "getcip", Constants.CIP + hqCode, cipStatus, false);
            cipModelArray.add(ciModel);
        } else binding.cip.setVisibility(View.GONE);

        //Cluster
        clusterModelArray.clear();
        MasterSyncItemModel cluster = new MasterSyncItemModel(SharedPref.getClusterCap(this), clusterCount, Constants.DOCTOR, "getterritory", Constants.CLUSTER + hqCode, clusterStatus, false);
        clusterModelArray.add(cluster);

        //Input
        inputModelArray.clear();
        MasterSyncItemModel inpModel = new MasterSyncItemModel(Constants.INPUT, inputCount, Constants.PRODUCT, "getinputs", Constants.INPUT, inputStatus, false);
        inputModelArray.add(inpModel);

        //Product
        productModelArray.clear();
        MasterSyncItemModel proModel = new MasterSyncItemModel(Constants.PRODUCT, productCount, Constants.PRODUCT, "getproducts", Constants.PRODUCT, productStatus, false);
        MasterSyncItemModel proCatModel = new MasterSyncItemModel(Constants.PRODUCT_CATEGORY, proCatCount, Constants.PRODUCT, "", Constants.PRODUCT_CATEGORY, proCatStatus, false);
        MasterSyncItemModel brandModel = new MasterSyncItemModel(Constants.BRAND, brandCount, Constants.PRODUCT, "getbrands", Constants.BRAND, brandStatus, false);
        productModelArray.add(proModel);
        productModelArray.add(proCatModel);
        productModelArray.add(brandModel);
        if (SharedPref.getRcpaNd(this).equalsIgnoreCase("0") || SharedPref.getChmRcpaNeed(this).equalsIgnoreCase("0")) {
            MasterSyncItemModel compProductModel = new MasterSyncItemModel(Constants.COMPETITOR_PROD, compProCount, Constants.PRODUCT, "getcompdet", Constants.COMPETITOR_PROD, compProStatus, false);
            MasterSyncItemModel mapCompPrdModel = new MasterSyncItemModel(Constants.MAPPED_COMPETITOR_PROD, mapComPrdCount, "AdditionalDcr", "getmapcompdet", Constants.MAPPED_COMPETITOR_PROD, mapCompPrdStatus, false);
            productModelArray.add(compProductModel);
            productModelArray.add(mapCompPrdModel);
        }

        //Leave
        leaveModelArray.clear();
        MasterSyncItemModel leaveModel = new MasterSyncItemModel(Constants.LEAVE, leaveCount, "Leave", "getleavetype", Constants.LEAVE, leaveStatus, false);
        MasterSyncItemModel leaveStatusModel = new MasterSyncItemModel(Constants.LEAVE_STATUS, leaveStatusCount, "Leave", "getleavestatus", Constants.LEAVE_STATUS, leaveStatusStatus, false);
        leaveModelArray.add(leaveModel);
        leaveModelArray.add(leaveStatusModel);

        //DCR
        dcrModelArray.clear();
        MasterSyncItemModel callSyncModel = new MasterSyncItemModel(Constants.CALL_SYNC, callSyncCount, "Home", "gethome", Constants.CALL_SYNC, callSyncStatus, false);
        MasterSyncItemModel myDayPlanModel = new MasterSyncItemModel(Constants.MY_DAY_PLAN, -1, Constants.DOCTOR, "gettodaydcr", Constants.MY_DAY_PLAN, myDayPlanStatus, false);
        MasterSyncItemModel visitControlModel = new MasterSyncItemModel(Constants.VISIT_CONTROL, visitControlCount, "AdditionalDcr", "getvisit_contro", Constants.VISIT_CONTROL, visitControlStatus, false);
        MasterSyncItemModel dateSyncModel = new MasterSyncItemModel(Constants.DATE_SYNC, dateSyncCount, "Home", "getdcrdate", Constants.DATE_SYNC, dateSyncStatus, false);
        MasterSyncItemModel stockBalanceModel = new MasterSyncItemModel(Constants.STOCK_BALANCE, -1, "AdditionalDcr", "getstockbalance", Constants.STOCK_BALANCE_MASTER, stockBalanceStatus, false);
     //   MasterSyncItemModel EventCallSync = new MasterSyncItemModel("Status", -1, "AdditionalDcr", "gettodycalls", Constants.CALENDER_EVENT_STATUS, calenderEventStaus, false);


        dcrModelArray.add(callSyncModel);
        dcrModelArray.add(dateSyncModel);
        dcrModelArray.add(myDayPlanModel);
        dcrModelArray.add(visitControlModel);
        dcrModelArray.add(stockBalanceModel);


        //Work Type
        workTypeModelArray.clear();
        MasterSyncItemModel workType = new MasterSyncItemModel(Constants.WORK_TYPE, workTypeCount, Constants.DOCTOR, "getworktype", Constants.WORK_TYPE, workTypeStatus, false);
        MasterSyncItemModel holiday = new MasterSyncItemModel(Constants.HOLIDAY, holidayCount, Constants.DOCTOR, "getholiday", Constants.HOLIDAY, holidayStatus, false);
        MasterSyncItemModel weeklyOff = new MasterSyncItemModel(Constants.WEEKLY_OFF, weeklyOfCount, Constants.DOCTOR, "getweeklyoff", Constants.WEEKLY_OFF, weeklyOfStatus, false);
        workTypeModelArray.add(workType);
        workTypeModelArray.add(holiday);
        workTypeModelArray.add(weeklyOff);

        //Tour Plan
        tpModelArray.clear();
        if (SharedPref.getTpNeed(this).equalsIgnoreCase("0")) {
            MasterSyncItemModel tpSetup = new MasterSyncItemModel(Constants.TP_SETUP, tpSetupCount, Constants.SETUP, "gettpsetup", Constants.TP_SETUP, tpSetupStatus, false);
            MasterSyncItemModel tPlan = new MasterSyncItemModel(Constants.TOUR_PLAN, -1, Constants.TOUR_PLAN, "getall_tp", Constants.TOUR_PLAN, tourPLanStatus, false);
            tpModelArray.add(tpSetup);
            tpModelArray.add(tPlan);
        } else binding.tourPlan.setVisibility(View.GONE);

        //Slide
        slideModelArray.clear();
        MasterSyncItemModel proSlideModel = new MasterSyncItemModel(Constants.PROD_SLIDE, proSlideCount, "Slide", "getprodslides", Constants.PROD_SLIDE, proSlideStatus, false);
        MasterSyncItemModel splSlideModel = new MasterSyncItemModel(Constants.SPL_SLIDE, proSpeSlideCount, "Slide", "getslidespeciality", Constants.SPL_SLIDE, proSpeSlideStatus, false);
        MasterSyncItemModel brandSlideModel = new MasterSyncItemModel(Constants.BRAND_SLIDE, brandSlideCount, "Slide", "getslidebrand", Constants.BRAND_SLIDE, brandSlideStatus, false);
        MasterSyncItemModel therapticSlideModel = new MasterSyncItemModel(Constants.THERAPTIC_SLIDE, therapticCount, "Slide", "gettheraptic", Constants.THERAPTIC_SLIDE, therapticStatus, false);
        slideModelArray.add(proSlideModel);
        slideModelArray.add(splSlideModel);
        slideModelArray.add(brandSlideModel);
        slideModelArray.add(therapticSlideModel);

        //Subordinate
        subordinateModelArray.clear();
        MasterSyncItemModel subModel = new MasterSyncItemModel(Constants.SUBORDINATE, subordinateCount, Constants.SUBORDINATE, "getsubordinate", Constants.SUBORDINATE, subordinateStatus, false);
        MasterSyncItemModel subMgrModel = new MasterSyncItemModel(Constants.SUBORDINATE_MGR, subMgrCount, Constants.SUBORDINATE, "getsubordinatemgr", Constants.SUBORDINATE_MGR, subMgrStatus, false);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Joint Work", jWorkCount, Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode, jWorkStatus, false);
        subordinateModelArray.add(subModel);
        subordinateModelArray.add(subMgrModel);
        subordinateModelArray.add(jWorkModel);

        otherModelArray.clear();
        MasterSyncItemModel Quiz = new MasterSyncItemModel("Quiz", Quixcount, "AdditionalDcr", "getquiz", Constants.QUIZ, QuizStatus, false);
        otherModelArray.add(Quiz);
        //Setup
        setupModelArray.clear();
        MasterSyncItemModel setupModel = new MasterSyncItemModel(Constants.SETUP, setupCount, Constants.SETUP, "getsetups", Constants.SETUP, setupStatus, false);
        MasterSyncItemModel customSetupModel = new MasterSyncItemModel(Constants.CUSTOM_SETUP, customSetupCount, Constants.SETUP, "getcustomsetup", Constants.CUSTOM_SETUP, customSetupStatus, false);
        setupModelArray.add(setupModel);
        setupModelArray.add(customSetupModel);

        passDataToAdapter();

    }

    public void listItemClicked(TextView view) {
        binding.listedDr.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.unlistedDoctor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.chemist.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.stockiest.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.hospital.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.cip.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.input.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.product.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.cluster.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.leave.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.dcr.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.workType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.tourPlan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.slide.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.subordinate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.Other.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        binding.setup.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);

        binding.listedDr.setSelected(false);
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
        binding.Other.setSelected(false);
        binding.setup.setSelected(false);

        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.greater_than_white, 0);
        view.setSelected(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        masterSyncAdapter = new MasterSyncAdapter(masterSyncItemModels, getApplicationContext(), (masterSyncItemModel1, position) -> {
            if (UtilityClass.isNetworkAvailable(this)) {
                NetworkStatusTask networkStatusTask = new NetworkStatusTask(MasterSyncActivity.this, status -> {
                    if (status) {
                        if (binding.listedDr.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), doctorModelArray, position);
                        } else if (binding.chemist.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), chemistModelArray, position);
                        } else if (binding.stockiest.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), stockiestModelArray, position);
                        } else if (binding.unlistedDoctor.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), unlistedDrModelArray, position);
                        } else if (binding.hospital.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), hospitalModelArray, position);
                        } else if (binding.cip.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), cipModelArray, position);
                        } else if (binding.input.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), inputModelArray, position);
                        } else if (binding.product.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), productModelArray, position);
                        } else if (binding.cluster.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), clusterModelArray, position);
                        } else if (binding.leave.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), leaveModelArray, position);
                        } else if (binding.dcr.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), dcrModelArray, position);
                        } else if (binding.workType.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), workTypeModelArray, position);
                        } else if (binding.tourPlan.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), tpModelArray, position);
                        } else if (binding.slide.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), slideModelArray, position);
                        } else if (binding.subordinate.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), subordinateModelArray, position);
                        } else if (binding.Other.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), otherModelArray, position);
                        } else if (binding.setup.isSelected()) {
                            sync(masterSyncItemModel1.getMasterOf(), masterSyncItemModel1.getRemoteTableName(), setupModelArray, position);
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(MasterSyncActivity.this, getString(R.string.poor_connection));
                    }
                });
                networkStatusTask.execute();
            } else {
                commonUtilsMethods.showToastMessage(MasterSyncActivity.this, getString(R.string.no_network));
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(masterSyncAdapter);
        masterSyncAdapter.notifyDataSetChanged();
    }

    public void passDataToAdapter() {
        if (binding.listedDr.isSelected()) {
            populateAdapter(doctorModelArray);
        } else if (binding.chemist.isSelected()) {
            populateAdapter(chemistModelArray);
        } else if (binding.stockiest.isSelected()) {
            populateAdapter(stockiestModelArray);
        } else if (binding.unlistedDoctor.isSelected()) {
            populateAdapter(unlistedDrModelArray);
        } else if (binding.hospital.isSelected()) {
            populateAdapter(hospitalModelArray);
        } else if (binding.cip.isSelected()) {
            populateAdapter(cipModelArray);
        } else if (binding.input.isSelected()) {
            populateAdapter(inputModelArray);
        } else if (binding.product.isSelected()) {
            populateAdapter(productModelArray);
        } else if (binding.cluster.isSelected()) {
            populateAdapter(clusterModelArray);
        } else if (binding.leave.isSelected()) {
            populateAdapter(leaveModelArray);
        } else if (binding.dcr.isSelected()) {
            populateAdapter(dcrModelArray);
        } else if (binding.workType.isSelected()) {
            populateAdapter(workTypeModelArray);
        } else if (binding.tourPlan.isSelected()) {
            populateAdapter(tpModelArray);
        } else if (binding.slide.isSelected()) {
            populateAdapter(slideModelArray);
        } else if (binding.subordinate.isSelected()) {
            populateAdapter(subordinateModelArray);
        } else if (binding.Other.isSelected()) {
            populateAdapter(otherModelArray);
        } else if (binding.setup.isSelected()) {
            populateAdapter(setupModelArray);
        }
    }

    public void masterSyncAll(boolean hqChanged) {
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(MasterSyncActivity.this, new NetworkStatusTask.NetworkStatusInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void isNetworkAvailable(Boolean status) {
                if (status) {
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

                    if (!hqChanged) {
                        masterSyncAllModel.add(inputModelArray);
                        masterSyncAllModel.add(productModelArray);
                        masterSyncAllModel.add(leaveModelArray);
                        masterSyncAllModel.add(dcrModelArray);
                        masterSyncAllModel.add(workTypeModelArray);
                        masterSyncAllModel.add(tpModelArray);
                        masterSyncAllModel.add(slideModelArray);
                        masterSyncAllModel.add(otherModelArray);
                        masterSyncAllModel.add(setupModelArray);
                    }

                    for (int i = 0; i < masterSyncAllModel.size(); i++) {
                        ArrayList<MasterSyncItemModel> childArray = new ArrayList<>(masterSyncAllModel.get(i));
                        itemCount += childArray.size();
                        for (int j = 0; j < childArray.size(); j++) {
                            if (hqChanged) {
                                if (childArray.get(j).getLocalTableKeyName().contains(rsf)) {
                                    childArray.get(j).setPBarVisibility(true);
                                    masterSyncAdapter.notifyDataSetChanged();
                                    sync(childArray.get(j).getMasterOf(), childArray.get(j).getRemoteTableName(), childArray, j);
                                }
                            } else {
                                childArray.get(j).setPBarVisibility(true);
                                masterSyncAdapter.notifyDataSetChanged();
                                sync(childArray.get(j).getMasterOf(), childArray.get(j).getRemoteTableName(), childArray, j);
                            }
                        }
                    }
                    Log.e("test", "count : " + itemCount);
                } else {
                    commonUtilsMethods.showToastMessage(MasterSyncActivity.this, getString(R.string.no_network));
                }

            }
        });
        networkStatusTask.execute();
    }

    public void sync(String masterOf, String remoteTableName, ArrayList<MasterSyncItemModel> masterSyncItemModels, int position) {

        try {
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", remoteTableName);
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", rsf);
            jsonObject.put("sf_type", SharedPref.getSfType(this));
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            jsonObject.put("Designation", SharedPref.getDesig(this));
            jsonObject.put("state_code", SharedPref.getStateCode(this));
            jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
            switch (remoteTableName) {
                case "getholiday":
                case "getweeklyoff": {
                    jsonObject.put("year", Year.now().getValue());
                    break;
                }
                case "gettodaytpnew": {
                    jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                    break;
                }
                case "getall_tp": {
                    jsonObject.put("tp_month", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_8, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                    jsonObject.put("tp_year", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_10, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                    break;
                }
                case "gettodaydcr": {
                    MyDayPlanEntriesNeeded.updateMyDayPlanEntriesNeeded(this, false, new MyDayPlanEntriesNeeded.SyncTaskStatus() {
                        @Override
                        public void datesFound() {
                            try {
                                jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_1, SharedPref.getSelectedDateCal(MasterSyncActivity.this)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void noDatesFound() {
                            Log.e("Master Sync", "Get MyDayPlan Call and Date Sync failed!" );
                        }
                    });
                }
            }

            Map<String, String> mapString = new HashMap<>();
            Log.e("API Object", "master sync obj : " + jsonObject);
            Call<JsonElement> call = null;
            if (masterOf.equalsIgnoreCase(Constants.DOCTOR)) {
                mapString.put("axn", "table/dcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.SUBORDINATE)) {
                mapString.put("axn", "table/subordinates");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.PRODUCT)) {
                mapString.put("axn", "table/products");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("Leave")) {
                mapString.put("axn", "get/leave");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("Home")) {
                mapString.put("axn", "home");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("AdditionalDcr")) {
                mapString.put("axn", "table/additionaldcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("Slide")) {
                mapString.put("axn", "table/slides");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.SETUP)) {
                mapString.put("axn", "table/setups");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.TOUR_PLAN)) {
                mapString.put("axn", "get/tp");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());
            }

            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        masterSyncItemModels.get(position).setPBarVisibility(false);
                        ++apiSuccessCount;

                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        JSONObject jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                        if (!jsonObject2.has("success")) { // response as jsonObject with {"success" : "fail" } will be received only when there are unformed object passed or there are no data in back end.
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1); // only update sync status and no need to overwrite previously saved data when failed
                                            masterSyncItemModels.get(position).setSyncSuccess(1);
                                        }
                                    }

                                    if (success) {

                                        masterSyncItemModels.get(position).setCount(jsonArray.length());
                                        masterSyncItemModels.get(position).setSyncSuccess(0);

                                        String dateAndTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_16);
                                        binding.lastSyncTime.setText(dateAndTime);
                                        SharedPref.saveMasterLastSync(getApplicationContext(), dateAndTime);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(), 0));
                                        MasterDataTable MainData =new MasterDataTable();
                                        MainData.setMasterKey(masterSyncItemModels.get(position).getLocalTableKeyName());
                                        MainData.setMasterValues(jsonArray.toString());
                                        MainData.setSyncStatus(0);
                                        MasterDataTable mNChecked= masterDataDao.getMasterSyncDataByKey(masterSyncItemModels.get(position).getLocalTableKeyName());
                                        if(mNChecked!=null){
                                            masterDataDao.updateData(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString());
                                        }else {
                                            masterDataDao.insert(MainData);
                                        }

                                        if(masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.CALL_SYNC)){
                                            CallDataRestClass.resetcallValues(context);
                                        }


                                        if (masterOf.equalsIgnoreCase("AdditionalDcr") && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getstockbalance")) {
                                            if (jsonArray.length() > 0) {
                                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                JSONArray stockBalanceArray = jsonObject1.getJSONArray("Sample_Stock");
                                                JSONArray inputBalanceArray = jsonObject1.getJSONArray("Input_Stock");
                                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STOCK_BALANCE, stockBalanceArray.toString(), 0));
                                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.INPUT_BALANCE, inputBalanceArray.toString(), 0));

                                                MasterDataTable stockdata =new MasterDataTable();
                                                stockdata.setMasterKey(Constants.STOCK_BALANCE);
                                                stockdata.setMasterValues(stockBalanceArray.toString());
                                                stockdata.setSyncStatus(0);

                                                MasterDataTable mChecked= masterDataDao.getMasterSyncDataByKey(Constants.STOCK_BALANCE);
                                                if(mChecked!=null){
                                                    masterDataDao.updateData(Constants.STOCK_BALANCE, stockBalanceArray.toString());
                                                }else {
                                                    masterDataDao.insert(stockdata);
                                                }

                                                MasterDataTable inputdata =new MasterDataTable();
                                                inputdata.setMasterKey(Constants.INPUT_BALANCE);
                                                inputdata.setMasterValues(inputBalanceArray.toString());
                                                inputdata.setSyncStatus(0);
                                                MasterDataTable nChecked = masterDataDao.getMasterSyncDataByKey(Constants.STOCK_BALANCE);
                                                if(nChecked !=null){
                                                    masterDataDao.updateData(Constants.INPUT_BALANCE, inputBalanceArray.toString());
                                                }else {
                                                    masterDataDao.insert(inputdata);
                                                }

                                            }
                                        } else if (masterOf.equalsIgnoreCase(Constants.DOCTOR) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("gettodaydcr")) {
                                            if (mgrInitialSync) {
                                                setHq(jsonArray);
                                                return;
                                            }
                                        } else if (masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.PROD_SLIDE) && !navigateFrom.equalsIgnoreCase("Login")) {
                                            if (jsonArray.length() > 0)
                                                SlideDownloaderAlertBox.openCustomDialog(MasterSyncActivity.this, false);
                                        }
                                    }
                                } else {
                                    masterSyncItemModels.get(position).setSyncSuccess(1);
                                    masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            masterSyncItemModels.get(position).setSyncSuccess(1);
                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
                        }

                        // when all the masters are synced and intent from Login Activity
                        if (apiSuccessCount >= itemCount && navigateFrom.equalsIgnoreCase("Login")) {
                            if (masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray().length() > 0) {
                                // If product slide quantity is 0 then no need to display a dialog of Downloader
                                SharedPref.putAutomassync(getApplicationContext(), true);
                                SharedPref.setSetUpClickedTab(getApplicationContext(), "0");
                                binding.backArrow.setVisibility(View.VISIBLE);
                                SlideDownloaderAlertBox.openCustomDialog(MasterSyncActivity.this, true);
                            } else {
                                SharedPref.putAutomassync(getApplicationContext(), true);
                                SharedPref.setSetUpClickedTab(getApplicationContext(), "0");
                                Intent intent = new Intent(MasterSyncActivity.this, HomeDashBoard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                        masterSyncAdapter.notifyDataSetChanged();
                        Log.e("test", "success count : " + apiSuccessCount);
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        Log.e("test", "failed : " + t);
                        ++apiSuccessCount;
                        Log.e("test", "success count at error : " + apiSuccessCount);
                        masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
                        masterSyncItemModels.get(position).setPBarVisibility(false);
                        masterSyncItemModels.get(position).setSyncSuccess(1);
                        masterSyncAdapter.notifyDataSetChanged();
                        if (apiSuccessCount >= itemCount && navigateFrom.equalsIgnoreCase("Login")) {
                            binding.backArrow.setVisibility(View.VISIBLE);
                            SharedPref.putAutomassync(getApplicationContext(), true);
                            SharedPref.setSetUpClickedTab(getApplicationContext(), "0");
                            if (masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray().length() > 0) { // If product slide quantity is 0 then no need to display a dialog of Downloader
                                SlideDownloaderAlertBox.openCustomDialog(MasterSyncActivity.this, true);
                            } else {
                                SharedPref.setSetUpClickedTab(getApplicationContext(), "0");
                                SharedPref.putAutomassync(getApplicationContext(), true);
                                Intent intent = new Intent(MasterSyncActivity.this, HomeDashBoard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.v("masterCheck", "--error-" + e);
            e.printStackTrace();
        }

    }

    private void InitializeTpNeededData() {
        try {
            holidayJSONArray = masterDataDao.getMasterDataTableOrNew(Constants.HOLIDAY).getMasterSyncDataJsonArray(); //Holiday data
            JSONArray weeklyOff = masterDataDao.getMasterDataTableOrNew(Constants.WEEKLY_OFF).getMasterSyncDataJsonArray(); // Weekly Off data
            for (int i = 0; i < weeklyOff.length(); i++) {
                JSONObject jsonObject = weeklyOff.getJSONObject(i);
                holidayMode = jsonObject.getString("Holiday_Mode");
                weeklyOffCaption = jsonObject.getString("WTname");
            }
            String[] holidayModeArray = holidayMode.split(",");
            weeklyOffDays = new ArrayList<>();
            for (String str : holidayModeArray) {
                switch (str) {
                    case "0": {
                        weeklyOffDays.add("Sunday");
                        break;
                    }
                    case "1": {
                        weeklyOffDays.add("Monday");
                        break;
                    }
                    case "2": {
                        weeklyOffDays.add("Tuesday");
                        break;
                    }
                    case "3": {
                        weeklyOffDays.add("Wednesday");
                        break;
                    }
                    case "4": {
                        weeklyOffDays.add("Thursday");
                        break;
                    }
                    case "5": {
                        weeklyOffDays.add("Friday");
                        break;
                    }
                    case "6": {
                        weeklyOffDays.add("Saturday");
                        break;
                    }
                }
            }

            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray(); //List of Work Types
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (jsonObject.getString("Name").equalsIgnoreCase("Weekly Off"))
                    weeklyOffWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
                else if (jsonObject.getString("Name").equalsIgnoreCase("Holiday"))
                    holidayWorkTypeModel = new ModelClass.SessionList.WorkType(jsonObject.getString("FWFlg"), jsonObject.getString("Name"), jsonObject.getString("TerrSlFlg"), jsonObject.getString("Code"));
            }
        } catch (Exception ignored) {

        }
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        return date.format(formatter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        switch (dayOfWeek) {
            case 1: {
                dayOfWeek = 2;
                break;
            }
            case 2: {
                dayOfWeek = 3;
                break;
            }
            case 3: {
                dayOfWeek = 4;
                break;
            }
            case 4: {
                dayOfWeek = 5;
                break;
            }
            case 5: {
                dayOfWeek = 6;
                break;
            }
            case 6: {
                dayOfWeek = 7;
                break;
            }
            case 7: {
                dayOfWeek = 1;
                break;
            }
        }

        for (int i = 1; i <= 42; i++) {
            if (i < dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                if (i < daysInMonth + dayOfWeek) {
                    daysInMonthArray.add(String.valueOf((i + 1) - dayOfWeek));
                }
            }
        }

        //To eliminate the excess empty dates which comes with the LocalDate library
        if (daysInMonthArray.size() >= 22 && daysInMonthArray.size() <= 28) {
            for (int i = daysInMonthArray.size(); i < 28; i++) {
                daysInMonthArray.add("");
            }
        } else if (daysInMonthArray.size() >= 29 && daysInMonthArray.size() <= 35) {
            for (int i = daysInMonthArray.size(); i < 35; i++) {
                daysInMonthArray.add("");
            }
        } else if (daysInMonthArray.size() >= 36 && daysInMonthArray.size() <= 42) {
            for (int i = daysInMonthArray.size(); i < 42; i++) {
                daysInMonthArray.add("");
            }
        }

        return daysInMonthArray;
    }

    public String findTerrSlFlag(String code) {
        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray(); //List of Work Types
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                if (code.equals(jsonObject.getString("Code")))
                    return jsonObject.getString("TerrSlFlg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private ArrayList<ModelClass.SessionList.SubClass> addExtraData(String Name, String Code) {
        String[] arrName = Name.split(",");
        String[] arrCode = Code.split(",");
        ArrayList<String> dummyName = new ArrayList<>(Arrays.asList(arrName));
        ArrayList<String> dummyCode = new ArrayList<>(Arrays.asList(arrCode));
        ArrayList<ModelClass.SessionList.SubClass> Array = new ArrayList<>();


        for (int i = 0; i < dummyName.size(); i++) {
            Array.add(new ModelClass.SessionList.SubClass(dummyName.get(i), dummyCode.get(i)));
        }

        return Array;
    }

    private void SaveTourPlan(JSONObject jsonObject1) {
        try {
            localDate = LocalDate.now();
            if (jsonObject1.has("previous")) {
                JSONArray previousArray = new JSONArray(jsonObject1.getJSONArray("previous").toString());
                SaveLocalOnlineTable(localDate.minusMonths(1), previousArray);
            }

            if (jsonObject1.has("current")) {
                JSONArray currentArray = new JSONArray(jsonObject1.getJSONArray("current").toString());
                SaveLocalOnlineTable(localDate, currentArray);
            }

            if (jsonObject1.has("next")) {
                JSONArray nextArray = new JSONArray(jsonObject1.getJSONArray("next").toString());
                SaveLocalOnlineTable(localDate.plusMonths(1), nextArray);
            }

        } catch (Exception e) {
            Log.v("getTp", "----error---" + e);
        }
    }

    private void SaveLocalOnlineTable(LocalDate localDate, JSONArray listArray) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            ArrayList<String> days = new ArrayList<>(daysInMonthArray(localDate));

            String monthYear = monthYearFromDate(localDate);
            String monthNo = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_8, monthYear);
            String year = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_10, monthYear);
            String monthName = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate));
            ArrayList<ModelClass> modelClasses = new ArrayList<>();

            ArrayList<String> holidayDateArray = new ArrayList<>();
            for (int i = 0; i < holidayJSONArray.length(); i++) { //Getting Holiday dates from Holiday master data for the selected month
                if (holidayJSONArray.getJSONObject(i).getString("Holiday_month").equalsIgnoreCase(String.valueOf(localDate.getMonthValue())))
                    holidayDateArray.add(holidayJSONArray.getJSONObject(i).getString("Hday"));
            }

            JSONArray savedDataArray = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate))).getTpDataJSONArray();
            ArrayList<ModelClass> modelClassLocal = new ArrayList<>();
            if (savedDataArray.length() > 0) { //Use the saved data if Tour Plan table has data of a selected month
                Type typeLocal = new TypeToken<ArrayList<ModelClass>>() {
                }.getType();
                modelClassLocal = new Gson().fromJson(savedDataArray.toString(), typeLocal);
            }

            Type type = new TypeToken<ArrayList<ReceiveModel>>() {
            }.getType();
            ArrayList<ReceiveModel> arrayList = new Gson().fromJson(listArray.toString(), type);

            if (listArray.length() > 0) {
                String rejectionReason = listArray.getJSONObject(0).getString("Rejection_Reason");
                String status = listArray.getJSONObject(0).getString("Change_Status");
                tourPlanOnlineDataDao.saveTpData(new TourPlanOnlineDataTable(monthName, listArray.toString(), status, rejectionReason));
                for (String day : days) {
                    if (!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        isDataAvailable = false;

                        if (modelClassLocal.size() > 0) {
                            for (int j = 0; j < modelClassLocal.size(); j++) {
                                if (modelClassLocal.get(j).getDayNo().equalsIgnoreCase(day) && modelClassLocal.get(j).getSyncStatus().equalsIgnoreCase("0")) {

                                    for (int i = 0; i < arrayList.size(); i++) {
                                        ReceiveModel receiveModel = arrayList.get(i);
                                        if (modelClassLocal.get(j).getDayNo().equalsIgnoreCase(receiveModel.getDayno())) {
                                            SaveTpLocalFull(receiveModel, modelClasses, day, monthName, date, dayName, monthNo, year);
                                        }
                                    }
                                } else if (modelClassLocal.get(j).getDayNo().equalsIgnoreCase(day) && modelClassLocal.get(j).getSyncStatus().equalsIgnoreCase("1")) {
                                    isDataAvailable = true;
                                    ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, modelClassLocal.get(j).getSessionList());
                                    modelClasses.add(modelClass);
                                    saveTpLocal(modelClasses, day, monthName, "1");
                                }
                            }
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                ReceiveModel receiveModel = arrayList.get(i);
                                if (day.equalsIgnoreCase(receiveModel.getDayno())) {
                                    SaveTpLocalFull(receiveModel, modelClasses, day, monthName, date, dayName, monthNo, year);
                                }
                            }
                        }

                        if (!isDataAvailable) {
                            ModelClass.SessionList sessionList;
                            sessionList = prepareSessionListForAdapterEmpty();

                            if (weeklyOffDays.contains(dayName)) // add weekly off object when the day is declared as Weekly Off
                                sessionList.setWorkType(weeklyOffWorkTypeModel);

                            if (holidayDateArray.contains(day))
                                sessionList.setWorkType(holidayWorkTypeModel); // add holiday work type model object when current date is declared as holiday

                            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                            sessionLists.add(sessionList);
                            ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                            modelClasses.add(modelClass);
                            saveTpLocal(modelClasses, day, monthName, "0");
                        }
                    } else {
                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        ModelClass modelClass = new ModelClass(day, "", "", "", "", true, sessionLists);
                        modelClasses.add(modelClass);
                        saveTpLocal(modelClasses, day, monthName, "");
                    }
                }

                tourPlanOfflineDataDao.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, rejectionReason);

            } else {  //If tour plan table has no data
                for (String day : days) {
                    if (!day.isEmpty()) {
                        String date = day + " " + monthYear;
                        String dayName = formatter.format(new Date(date));
                        ModelClass.SessionList sessionList = new ModelClass.SessionList();
                        sessionList = prepareSessionListForAdapterEmpty();

                        if (weeklyOffDays.contains(dayName)) // add weekly off object when the day is declared as Weekly Off
                            sessionList.setWorkType(weeklyOffWorkTypeModel);

                        if (holidayDateArray.contains(day))
                            sessionList.setWorkType(holidayWorkTypeModel); // add holiday work type model object when current date is declared as holiday

                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        sessionLists.add(sessionList);
                        ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
                        modelClasses.add(modelClass);
                        saveTpLocal(modelClasses, day, monthName, "0");
                    } else {
                        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
                        ModelClass modelClass = new ModelClass(day, "", "", "", "", true, sessionLists);
                        modelClasses.add(modelClass);
                        saveTpLocal(modelClasses, day, monthName, "");
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void SaveTpLocalFull(ReceiveModel receiveModel, ArrayList<ModelClass> modelClasses, String day, String monthName, String date, String dayName, String monthNo, String year) {
        ModelClass.SessionList sessionList = new ModelClass.SessionList();
        ModelClass.SessionList sessionList2 = new ModelClass.SessionList();
        ModelClass.SessionList sessionList3 = new ModelClass.SessionList();

        isDataAvailable = true;
        boolean session2 = false;
        boolean session3 = false;

        String terrSlFlag = findTerrSlFlag(receiveModel.getWTCode());
        String remarks = receiveModel.getDayRemarks();
        String submittedTime = receiveModel.getSubmitted_time_dt();

        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stkArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(receiveModel.getFWFlg(), receiveModel.getWTName(), terrSlFlag, receiveModel.getWTCode());
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass(receiveModel.getHQNames(), receiveModel.getHQCodes());

        if (receiveModel.getFWFlg().equalsIgnoreCase("F")) {
            if (!receiveModel.getClusterName().isEmpty())
                clusterArray = addExtraData(receiveModel.getClusterName(), receiveModel.getClusterCode());
            if (!receiveModel.getJWNames().isEmpty())
                jcArray = addExtraData(receiveModel.getJWNames(), receiveModel.getJWCodes());
            if (!receiveModel.getDr_Name().isEmpty())
                drArray = addExtraData(receiveModel.getDr_Name(), receiveModel.getDr_Code());
            if (!receiveModel.getChem_Name().isEmpty())
                chemArray = addExtraData(receiveModel.getChem_Name(), receiveModel.getChem_Code());
            if (!receiveModel.getStockist_Name().isEmpty())
                stkArray = addExtraData(receiveModel.getStockist_Name(), receiveModel.getStockist_Code());
        }
        sessionList = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks);

        if (!receiveModel.getWTName2().isEmpty()) {
            session2 = true;
            String terrSlFlag2 = findTerrSlFlag(receiveModel.getWTCode2());
            String remarks2 = receiveModel.getDayRemarks2();
            workType = new ModelClass.SessionList.WorkType(receiveModel.getFWFlg2(), receiveModel.getWTName2(), terrSlFlag2, receiveModel.getWTCode2());
            hq = new ModelClass.SessionList.SubClass(receiveModel.getHQNames2(), receiveModel.getHQCodes2());
            clusterArray = new ArrayList<>();
            jcArray = new ArrayList<>();
            drArray = new ArrayList<>();
            chemArray = new ArrayList<>();
            stkArray = new ArrayList<>();
            unListedDrArray = new ArrayList<>();
            cipArray = new ArrayList<>();
            hospArray = new ArrayList<>();

            if (receiveModel.getFWFlg2().equalsIgnoreCase("F")) {
                if (!receiveModel.getClusterName().isEmpty())
                    clusterArray = addExtraData(receiveModel.getClusterName2(), receiveModel.getClusterCode2());
                if (!receiveModel.getJWNames2().isEmpty())
                    jcArray = addExtraData(receiveModel.getJWNames2(), receiveModel.getJWCodes2());
                if (!receiveModel.getDr_two_name().isEmpty())
                    drArray = addExtraData(receiveModel.getDr_two_name(), receiveModel.getDr_two_code());
                if (!receiveModel.getChem_Name().isEmpty())
                    chemArray = addExtraData(receiveModel.getChem_two_name(), receiveModel.getChem_two_code());
                if (!receiveModel.getStockist_two_name().isEmpty())
                    stkArray = addExtraData(receiveModel.getStockist_two_name(), receiveModel.getStockist_two_code());
            }
            sessionList2 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks2);

        }

        if (!receiveModel.getWTName3().isEmpty()) {
            session3 = true;
            String terrSlFlag3 = findTerrSlFlag(receiveModel.getWTCode3());
            String remarks3 = receiveModel.getDayRemarks2();
            workType = new ModelClass.SessionList.WorkType(receiveModel.getFWFlg3(), receiveModel.getWTName3(), terrSlFlag3, receiveModel.getWTCode3());
            hq = new ModelClass.SessionList.SubClass(receiveModel.getHQNames3(), receiveModel.getHQCodes3());
            clusterArray = new ArrayList<>();
            jcArray = new ArrayList<>();
            drArray = new ArrayList<>();
            chemArray = new ArrayList<>();
            stkArray = new ArrayList<>();
            unListedDrArray = new ArrayList<>();
            cipArray = new ArrayList<>();
            hospArray = new ArrayList<>();

            if (receiveModel.getFWFlg3().equalsIgnoreCase("F")) {
                if (!receiveModel.getClusterName3().isEmpty())
                    clusterArray = addExtraData(receiveModel.getClusterName3(), receiveModel.getClusterCode3());
                if (!receiveModel.getJWNames3().isEmpty())
                    jcArray = addExtraData(receiveModel.getJWNames3(), receiveModel.getJWCodes3());
                if (!receiveModel.getDr_three_name().isEmpty())
                    drArray = addExtraData(receiveModel.getDr_three_name(), receiveModel.getDr_three_code());
                if (!receiveModel.getChem_three_name().isEmpty())
                    chemArray = addExtraData(receiveModel.getChem_three_name(), receiveModel.getChem_three_code());
                if (!receiveModel.getStockist_three_name().isEmpty())
                    stkArray = addExtraData(receiveModel.getStockist_three_name(), receiveModel.getStockist_three_code());
            }
            sessionList3 = prepareSessionListForAdapter(clusterArray, jcArray, drArray, chemArray, stkArray, unListedDrArray, cipArray, hospArray, workType, hq, remarks3);
        }

        ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
        sessionLists.add(sessionList);
        if (session2) sessionLists.add(sessionList2);
        if (session3) sessionLists.add(sessionList3);
        ModelClass modelClass = new ModelClass(day, date, dayName, monthNo, year, true, sessionLists);
        modelClass.setSubmittedTime(submittedTime);
        modelClasses.add(modelClass);
        saveTpLocal(modelClasses, day, monthName, "0");
    }

    public void saveTpLocal(ArrayList<ModelClass> arrayList, String date, String month, String status) {
        for (ModelClass modelClass : arrayList) {
            if (modelClass.getDayNo().equals(date)) {
                modelClass.setSyncStatus(status);
                break;
            }
        }
        tourPlanOfflineDataDao.saveTpData(new TourPlanOfflineDataTable(month,  new Gson().toJson(arrayList)));
    }

    private ModelClass.SessionList prepareSessionListForAdapterEmpty() {
        ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType("", "", "", "");
        ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("", "");

        ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

        return new ModelClass.SessionList("", true, "", workType, hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
    }

    public void setHq(JSONArray jsonArray) {
        //we need to get the very first HQ id from subordinate(HQ) array to pass this in object of every api call to sync all other masters for a particular HQ
        //only for MGR .bcz MGR has multiple HQ
        mgrInitialSync = false;
        apiSuccessCount = 0;
        if (jsonArray.length() > 0) {
            try {
                String HqName="", Hqcode="";
                JSONObject firstObject = jsonArray.getJSONObject(0);
                if (firstObject.getString("FWFlg").equalsIgnoreCase("F")) {
                    Hqcode = firstObject.getString("SFMem");

                    HqName=  firstObject.getString("HQNm");
                }
                if (jsonArray.length() == 2) {
                    JSONObject secondObject = jsonArray.getJSONObject(1);
                    if (secondObject.getString("FWFlg").equalsIgnoreCase("F")) {
                        Hqcode = secondObject.getString("SFMem");
                        HqName=  secondObject.getString("HQNm");
                    }
                }
           //     SharedPref.saveHq(MasterSyncActivity.this, HqName, Hqcode);

                binding.hqName.setText(HqName);
                rsf = Hqcode;
               // SharedPref.saveHq(MasterSyncActivity.this, jsonArray.getJSONObject(0).getString("name"), rsf);
                prepareArray(rsf);// to replace the new rsf values
                masterSyncAll(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            masterSyncAll(false);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }



public void  MydayplanSync(){
  try {
      JSONObject jsonObject=new JSONObject();
      jsonObject.put("tableName","gettodaydcr");
      jsonObject.put("sfcode",SharedPref.getSfType(this));
      jsonObject.put("division_code",SharedPref.getDivisionCode(this));
      jsonObject.put("Rsf",SharedPref.getSfCode(this));
      jsonObject.put("sf_type",SharedPref.getSfType(this));
      jsonObject.put("ReqDt",TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
      jsonObject.put("Designation",SharedPref.getDesig(this));
      jsonObject.put("state_code",SharedPref.getStateCode(this));
      jsonObject.put("subdivision_code",SharedPref.getSubdivisionCode(this));
      Map<String, String> mapString = new HashMap<>();
      mapString.put("axn", "table/dcrmasterdata");
      apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
      Call<JsonElement>  call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), mapString, jsonObject.toString());

      call.enqueue(new Callback<JsonElement>() {
          @Override
          public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
              try {

                  if (response.code() == 200 || response.code() == 201) {
                      JsonElement jsonElement = response.body();
                      if (!jsonElement.isJsonNull()) {
                          if (jsonElement.isJsonArray()) {
                              JSONArray jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());

                              String HqName = "", HqCode = "";
                              if (jsonArray.length() > 0) {
                                  JSONObject firstObject = jsonArray.getJSONObject(0);
                                  if (firstObject.getString("FWFlg").equalsIgnoreCase("F")) {
                                      HqName = firstObject.getString("HQNm");
                                      HqCode = firstObject.getString("SFMem");
                                  }
                                  if (jsonArray.length() == 2) {
                                      JSONObject secondObject = jsonArray.getJSONObject(1);
                                      if (secondObject.getString("FWFlg").equalsIgnoreCase("F")) {
                                          HqName = secondObject.getString("HQNm");
                                          HqCode = secondObject.getString("SFMem");
                                      }
                                  }
                                  SharedPref.saveHq(MasterSyncActivity.this, HqName, HqCode);
                              }
                          }
                      }
                  }else {
                      SharedPref.saveHq(MasterSyncActivity.this, "", "");
                  }
              } catch (Exception ignore) {
              }


          }

          @Override
          public void onFailure(Call<JsonElement> call, Throwable t) {

          }
      });

  }catch (Exception ignore){
  }

    }

}